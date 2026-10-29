---
name: leapkelp-water-smash-resmooth
description: 潮涌之叶水中重砸动画 2026-10-28 按用户令重排(有意偏离珊瑚傀儡烘培):11 点 CATMULLROM 时间线取代 8 点 LINEAR+两段静止窗口、body X ×2.5 加重量、砸击期间按 ageInTicks 补 30% 划水;含时间线公式/生成器路径/applySwimMotion 两处仍未修的上游偏差
metadata: 
  node_type: memory
  type: project
  originSessionId: fd31cb6e-a22c-4df9-883f-a51127943f9f
  modified: 2026-10-28T18:17:52.206Z
---

用户 2026-10-28 反馈「潮涌之叶水中攻击动作不流畅、有停顿」,对比原版腾跃之叶后选定方案 A:**保留珊瑚傀儡左右侧砸的身份,但把片子重排成连续的**。这是**有意偏离上游烘培**,不是移植漏项([[leapkelp-swim-anim-coralssus-port]] 的 8 点 LINEAR 结论自此作废)。

**Why:** 珊瑚傀儡 `ModelAnimator` 的 `setStaticKeyframe(5)` / `setStaticKeyframe(10)` 让 36 tick 里有 15 tick 全身完全静止(10–15、16–26);加上本体攻击锁 MOVE → `LeapkelpMoveControl` else 分支清零输入 → `limbSwingAmount` 归零 → 程序化划水也冻住,停顿被放大两重。原版 Goety `SMASH` 是 1.3333s 全 CATMULLROM 无静止窗口。

**How to apply:** 再改这段动画时守住三条:①命中帧必须仍是 0.8s(tick 16),`Leapkelp.SMASH_HIT_TICK` 与关键帧 S 姿态绑死;②时长 1.8s/36 tick 与 `SMASH_END_TICK` 绑死;③所有通道 CATMULLROM(与原版同插值),不要退回 LINEAR。

## 新时间线(生成器 `.scratch/_resmooth_golem.mjs`,gitignore 不提交)

- 采样点:`0, 0.25, 0.5, 0.6, 0.7, 0.8, 0.9, 1.1, 1.35, 1.55, 1.8` 秒(11 点),`withLength(1.8F)`,11 个通道各 11 帧。
- 从旧版读 W=0.5s 帧、S=0.8s 帧(命中姿态),其余按公式生成:
  - 0 / 0.25 = 旧值;0.5 = W
  - **预备** 0.6 = `W' = W + 0.08·(W−S)`;整段静止的通道(如 `right_wrist` 的 −25)改用 `W' = S·1.12` 给一点屈伸
  - **挥砸** 0.7 = `W' + 0.25·(S−W')`(= u² 缓入);0.8 = S 精确命中
  - **跟随** 0.9 = `O = S + 0.12·(S−W)`(过冲)
  - **回收** 1.1 / 1.35 / 1.55 / 1.8 = `S·0.45 / 0.22 / 0.09 / 0`
- 逐 tick 采样自检:挥砸那一下最陡 ~55°/tick(`right_arm` xRot,命中前),与原版 tick13 的 61°/1.6tick 同量级;0.9s 后各通道幅度单调收敛。
- **A+2 加重量**:`body` ROTATION 的 X 全轨 ×2.5(−7.5→−18.75 预备,峰值 −21.25;命中 +5→+12.5,过冲 +16.25)。叠加泳姿 base +17.5° 后命中时躯干前压 ~34°。Y/Z 分量不动。
- 生成后务必 `grep -c "Interpolations.LINEAR"` = 0;`posVec` 的 Y 取反沿用 [[leapkelp-swim-anim-coralssus-port]] 第 40 条。

## A+1 砸击期间保留划水

- `Leapkelp`:新字段 `attackStrokeAmount/O`,在 `tick()` 里 `updateSwimPoseAmount()` 之后 `updateAttackStrokeAmount()`(`Mth.approach(..., isMeleeAttacking()?1:0, SWIM_POSE_STEP=0.15)`,两端都跑),getter `getAttackStrokeAmount(partialTicks)` 用 `Mth.lerp`。**不能直接读 `limbSwingAmount`**——攻击锁 MOVE 时它就是 0。
- `LeapkelpModel.setupAnim`:swimAmount>0 分支里再调一次 `applySwimMotion(ageInTicks * 0.5F, attackStroke * swimAmount * SWIM_STROKE_GAIN * 0.3F)`。用 `ageInTicks` 当相位(不是 `limbSwing`)——相位是时间连续的,幅度由 0.15/tick 缓入缓出,所以起停不会跳。`Targets` 都是 `offsetRotation/offsetPos`(加法),与关键帧动画自然叠加,已核实 `.scratch/forge_src2/.../AnimationChannel.java`。

## 两处仍未修的上游偏差(用户没让改,别自作主张)

`LeapkelpModel.applySwimMotion` 与珊瑚傀儡 `CoralGolemServantModel` 对照:
1. **手臂轴错了**:上游 `flap(...)` 加 **rotateAngleZ**,我们的 `leftArm/rightArm` 写的是 **xRot**(腿的 `walk` 才是 xRot)。现有两条手臂 stroke 应改 `zRot`。
2. **丢了 `weight` 项**:`calculateRotation = cos·degree·amount + weight·amount`,上游手臂 weight=1.5(root/腿=0),`stroke()` 没加。
两处都会改泳姿观感,要动先问。

## 已知未修的小 bug

`onSyncedDataUpdated` 以 **ANIM_STATE 值** 为键(161–178 行):连续两次同侧重砸(ANIM_STATE 没经过 IDLE)不会重启动画,第二次攻击**完全没有挥砸动作**。`canUse()` 要求 `!isMeleeAttacking()` 且目标仍在 3.75 内,所以只在目标被墙挡住/推不走时偶发。真要修得走实体事件(如 `handleEntityEvent`),不能靠 synced 值。
