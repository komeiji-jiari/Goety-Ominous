---
name: mutant-hoglin-anim-fix
description: 突变疣猪兽仆从动画 bug 根因与修复(正弦无过渡硬切 0/1 + charging 不同步 + 蓄力 26→24)
metadata: 
  node_type: memory
  type: project
  originSessionId: f008ca66-33d8-49c7-9c29-f63e6f9d251a
  modified: 2026-10-15T22:33:29.267Z
---

突变疣猪兽仆从(MutantHoglinServant)动画播放 bug 已定位并修复。移植版与原版 Mutant More 逐字节一致,属继承性 bug。

根因(原版就有):
1. `setupAnim` 里三个正弦动画(idle/walk/charge)的 amountMultiplier 是硬编码 `1.0F/0.0F`,无过渡 → 启停时姿势跳变。`SineWaveAnimationUtils.tickAmountMultiplierChange(float,boolean,float)` 本来就是平滑方法但模型没用。
2. `charging` 是普通字段,只靠 broadcastEntityEvent(byte 12/13)同步,客户端中途加载实体时看不到冲锋状态。
3. PREPARE_CHARGE 关键帧 1.75s=35tick,模型按 1.5× 播放,但 `prepareChargeAnimationTick=26` → 动画 23.3tick 就放完,之后定格 ~3tick。
4. 移植版特有:`travel()` 覆写在 `charging || prepareChargeAnimationTick>0` 时把骑乘的疣猪以 1.25× 速度向前滑行,整个蓄力动画期间都在原地滑动。

修复:
- `MutantHoglinServantModel`: 加 3 个平滑字段,经 `tickAmountMultiplierChange` 向目标 lerp;else 分支传平滑值。
  - 第二轮(移动仍不丝滑):`tickAmountMultiplierChange` 的 delta 原为 0.1/帧,高刷新率下 ~0.07s 切完≈硬切。改用 `deltaTicks = clamp(ageInTicks - lastAgeInTicks, 0, 2)` × 0.12/tick(帧率无关,恒 0.42s);`f`(相位速度)和 `speed` 用指数平滑(`+= (target-cur)*deltaTicks*0.5`)防腿部相位跳变。
  - 关键帧动画不再与正弦波互斥:正弦波恒播,关键帧先快照 pre(正弦姿态)再叠加,`keyframeAnimationAmount` 以 0.2/tick 淡入淡出;`heldPose` 缓存关键帧末姿态用于结束后渐出。`getAllParts()` 在 1.20.1 返回 Stream,需 `.collect(Collectors.toList())`。
  - 第三轮(跑步高频率抽动):walk 正弦相位频率 = `f*12.5°/tick`(f=lenSqr*50),移速 0.28 → f≈3.9 → 身体/头等 divisor-500 部件 ~5Hz 混叠抽动。指数平滑压不住 client 端 `getDeltaMovement()` 的 2-tick 周期抖动(0.5/tick 只衰减一半)。最终修复:**弃用 getDeltaMovement 当相位驱动**(注意:它并不是 0,而是速度包每 ~3 tick 下发一次的**乘过摩擦的存量值**,见 [[mc-client-deltamovement-motion-packets]];不能用的是"拿它当每 tick 位移/相位增量",因为阶梯+衰减把速率抖成锯齿),移动判定改用 vanilla `limbSwingAmount`(client 平滑累积,v² 代理,阈值 moving>0.005),正弦相位固定节拍(walk=1.6、charge=2.6,永不混叠)。**2026-10-15 后话:** 同族三个突变僵尸当时试过把步频因子换成"位置差 × 8.64 锁脚步"(见 [[mm-sine-walk-phase-footstep-lock]]),**当天即被用户令回退**,现为 `getDeltaMovement().horizontalDistance() * 7.0F`;疣猪兽这份固定节拍因 walk/trot/charge 三态共用相位、且带 1.2 倍率,始终没动。删除 smoothedWalkSpeed/smoothedChargeSpeed 字段与 MiscUtils/Vec3 import。
  - 第四轮(正常走路却播疾驰):limbSwingAmount 是 v² 代理,正常移速 0.28 → ~0.078,而疾驰阈值初设 0.05(v≈0.22)过低 → 正常走路也触发 charge 疾驰。阈值改 `highSpeed = limbSwingAmount > 0.12`(v≈0.35),正常走路走 walk 动画,仅激怒加速/冲锋攻击时才疾驰。
  - 第五轮(走路缓、跑步猛):走路节拍 1.6→1.2、腿摆幅 35/25→28/20、身体起伏 2.5/1.5→2.0/1.2、头颈胸 2.5/2.5/1.0→2.0/2.0/0.8;疾驰节拍 2.6→3.2、腿摆幅 50/30/40→62/38/50、前腿抬腿 7.5→9、身体前倾 adjust -15→-22 + 俯仰 -2.5→-4 + 弹跳 2.5/3.0→3.5/4.5、头颈更低 -5→-6.5、尾 135→145。charge 节拍 3.2 时腿部 40°/tick(2Hz)、身体 40°/tick,60fps 下 13.3°/帧仍平滑。
  - 第六轮(跟随玩家不平缓):`ApproachTargetGoal` 跟随速度倍率 `following_movement_speed_multiplier` 默认 **1.3**(javap 从 mutantmore jar 反汇编确认),跟随玩家速度 0.28×1.3≈0.364 → limbSwingAmount≈0.132>0.12 疾驰阈值 → **跟随走却在播冲锋攻击的暴力前倾动画**。修复:新增 `mutantHoglinTrotAnimation`(平滑快步走:腿 42/28,身体前倾 -28,弹跳 2.0,头自然 -12,节拍 2.0),冲锋动画只留给真正的 `entity.charging`;阈值用迟滞字段 `trotting`(进入 >0.12,退出 <0.06)防寻路速度波动在 trot↔walk 间闪烁。三个状态:walk(慢走,节拍1.2)/ trot(高速移动,节拍2.0)/ charge(冲锋攻击,节拍3.2)。
- `MutantHoglinServant`: 给 `charging` 加 SynchedEntityData key(CHARGING),`setCharging()` 统一写入 + `onSyncedDataUpdated` 回填字段;`prepareChargeAnimationLength` 26→24;`travel()` 拆两分支——`charging` 时 1.25× 前冲,`prepareChargeAnimationTick>0` 时 `super.travel(Vec3.ZERO)` 原地不动。
- `MutantHoglinChargeAttackGoal`: 计数器 26→24,`mob.charging=X` 改 `mob.setCharging(X)`。

相关: [[forge-compile-setup]] [[ac-compat-porting-notes]]
