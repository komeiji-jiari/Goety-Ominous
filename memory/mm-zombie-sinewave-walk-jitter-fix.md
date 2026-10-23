---
name: mm-zombie-sinewave-walk-jitter-fix
description: "MM 正弦走路动画三个 bug 的定案修法:抖动=相位 f×tickCount 突跳→实体上的相位积分;战斗中\"立正\"=平滑振幅的 target 带了 keyframe 门控被压到 0→去掉门控而非退回二值;走路/站立没衔接=二值硬切→互补平滑权重;模型是每种实体一个单例,任何跨帧状态都必须放实体上"
metadata: 
  node_type: memory
  type: project
  modified: 2026-10-15T23:05:56.309Z
  originSessionId: 2de83047-6979-4f19-b5d1-4021d6d8c3a8
---

2026-10-15 三种突变僵尸仆从(MutantJungleZombieServant / MutantHuskServant / MutantFrozenZombieServant)的正弦走路动画连修两轮,最终定案如下(未构建 jar)。

**bug1 走路抽搐:** MM 原版相位 = `getTick(tickCount) × f`,`f = min(deltaMovement.lengthSqr()*50, 8)` 每 tick 变 → 相位是乘积不是积分,f 一变相位瞬跳,跳幅 = `tickCount × 0.2182 × Δf`,**活越久抽越狠**(实测 t=600 时跳 ~157 rad)。移植版逐字节照抄,属继承性 bug。

**bug2 战斗中"立正"**(2026-10-15 用户报):第一轮按疣猪兽方子引入了平滑振幅 `tickAmountMultiplierChange(amount, shouldPlay, deltaTicks * 0.12F)`,而所有 `shouldPlayX` 都 AND 了 `entity.notCurrentlyPlayingKeyframeAnimation()` → 攻击键帧期间振幅被压到 0(**振幅 0 = 只保持 resetPose = 立正**),键帧结束后 `else` 分支立刻跑但振幅要 ~8 tick 才爬回 1。攻击连续(咆哮 120t/锁喉 80t/连拳 25t×5)时振幅长期趴在 0 → 一直立正。上游是真·二值 `shouldPlayX ? 1.0f : 0.0f`,没有这个窗口。

**最终修法(`*ServantModel.setupAnim` + `AbstractMutantServant`):**

- 相位:**弃用 `getDeltaMovement`/`f`/`MiscUtils.isMovingOnLand`**,移动判定换 `entity.onGround() && !entity.isInWaterOrBubble() && limbSwingAmount > 0.005F`;**积分 `entity.walkPhase += 步频因子 * deltaTicks`,`walkTick = entity.walkPhase * π/180 / 20`** 传给正弦的 tick 参数,正弦的 speed 参数固定传 `1.0F`(抖动就是这么消掉的:f 折进相位了)。
- 步频因子现状:**`Math.min((float) Math.sqrt(xDiff * xDiff + zDiff * zDiff) * 8.64F, 6.0F)` × deltaTicks**(位置差 `xDiff = getX()-xo`,2026-10-16 问过用户后定的;`walkPhase` 在实体上,`walkTick = walkPhase * π/180 / 20`)。中间过程:2026-10-15 是 `getDeltaMovement().horizontalDistance() * 7.0F`,当天改成位置差版又当场回退,**10-16 用户再报"动作太慢/脚步太快"后重新落地**;推导见 [[mm-sine-walk-phase-footstep-lock]](那个 `.scratch/_mmz_walkphase_full_backup.patch` 是**整文件快照**,别整套贴)。
- 振幅:**最终定案 = 互补平滑权重,存实体**(jungle `walkAnimationAmount`/`idleAnimationAmount`,husk/frozen 再加 `jumpingAnimationAmount`,`idle` 初值 1.0F),`tickAmountMultiplierChange(amount, target, deltaTicks * 0.12F)`(≈8 tick = 0.42s 过渡,和疣猪兽同值),else 分支传 `entity.xxxAnimationAmount`。
  - **target 只由状态决定,绝不能带 `notCurrentlyPlayingKeyframeAnimation()` 门控**:`walk = movingOnLand`、`idle = !movingOnLand`(husk/frozen 先让 `jumping = entity.jumping` 占位,walk 再 `&& !jumping`)。这是整个 bug 的关键 —— 第一轮平滑之所以炸成"战斗中立正",就是因为 target 里 AND 了这个门控:weights 在 keyframe 分支期间被拉到 0(分支虽然不画正弦,但权重每帧都在更新),键帧结束后要 ~8 tick 才爬回,连环攻击时长期趴在 0。**平滑 ≠ 立正,带 keyframe 门控的平滑才是。**
  - 目标互补 + 步长相同 → 权重和恒为 1(walk+idle,或 jump+walk+idle),不会有 rest pose 塌陷窗口;中途从 keyframe 分支回到 else 时权重仍是满值,也不补一段爬升。
  - 二值 `shouldPlayX ? 1.0F : 0.0F`(= 上游 MM 原样)是硬切:**2026-10-15 用户报"突变僵尸们的走路和站立没有动画衔接"就是它**,别再退回二值。
- **键帧 ↔ 走/站 也做了衔接(2026-10-15,用户"这两个也改"):** 抄疣猪兽那套 `pre` 快照 + `heldPose` + `keyframeAnimationAmount` 的**姿态快照 lerp**(不是 `PoseBlend` 的 scale 混合):
  1. 正弦**移出 else 分支恒播**(jungle 连 resting 也移出,休息再也不是链里的一支);
  2. `keyframeActive = !entity.notCurrentlyPlayingKeyframeAnimation()`(jungle 再 `|| entity.stopRestingAnimationTick > 0`,以对齐它自己的链);
  3. **所有正弦 amount 都 AND 上 `!keyframeActive`** —— 键帧动画是全姿态动画,不压掉正弦就成了"边走边叠加键帧";
  4. `entity.keyframeAnimationAmount` 以 `deltaTicks * 0.2F`(≈5 tick)淡入淡出;链照旧只放键帧;
  5. 姿态 = `pre + k * (heldPose - pre)`,由模型在链之后 lerp 回去,`heldPose`(`float[][6]`)在 `keyframeActive` 期间逐帧冻结当前姿态,键帧结束后靠它渐出。
  - **为什么这次 amount 带 keyframe 门控却不立正**(和"bug2"的区别,别再混淆):立正的成因是"门控把 amount 拉到 0 却**没有** k 的补位";这里 amount 归零的同一段时间里 k 正把姿态 lerp 到`heldPose`,而 `k`(0.2/tick)比 amount(0.12/tick)回归更快,所以正弦权重即便还没爬满,可见姿态也一直落在 pre↔held 的连线上,没有静止姿势窗口。
  - 三个实体各加 `keyframeAnimationAmount` + `heldPose`(jungle 另加 `restingAnimationAmount`),模型各加 `java.util.List/Collectors` import。键帧清单必须与链**逐条对齐**(jungle 的 `notCurrentlyPlayingKeyframeAnimation()` 少一个 `stopRestingAnimationTick`,已补)。
  - 已知取舍:键帧起始的前 ~8 tick 里正弦仍留一点残余(amount 0.12/tick 衰减比 k 0.2/tick 慢),这是过渡本身,不是残留 bug。
- **跨帧状态一律放实体,不放模型**:模型是渲染器构造里 `bakeLayer` 出来的**每种实体一个单例**,两只同类仆从同屏时后渲染那只 `deltaTicks = clamp(ageInTicks - lastAgeInTicks...)` 恒为 0(同类型实体的 ageInTicks 相同才导致共享,不同 tickCount 时后一只恒 0)。落地:`AbstractMutantServant.walkPhase / walkPhaseAge`;疣猪兽 `charge/walk/trot/idle/keyframeAnimationAmount + trotting + heldPose`;潜影贝 `walkAmount/wasWalking/walkScale/walkPhaseSeconds`。`pre`/`basePose`/`walkPose` 这类单次调用内的临时缓冲可以留模型。

**范围:** 只有疣猪兽 + 这 3 个模型走正弦路径。`MutantBlaze/Rodling` 的 `walkSpeed` 是幅度不是相位;`StormNecromancer/SunkenNecromancerModel` 的 `f = lengthSqr` 只在 `getFallFlyingTicks() > 4` 生效(死代码)。`Mutant{X}KeyframeAnimations.MUTANT_X(entity)` 每帧重建 AnimationDefinition(GC 负担,低优先级,未动)。

另:MM 的 `MutantMob.introAnimationTick` 全靠 `onMutated()` 触发,而本仓库 `onMutated()` **0 个调用点**(grep 过) → 三个僵尸的 intro 分支、凋零骷髅 `shouldBeStationary() = getAnimation("intro").isPlaying()` / `RemainStationaryGoal` 都是不可达死码;**留着没删**(要接召唤流程再启用,属设计决定)。见 [[mm-hurt-inline-event-die-order]]、[[mm-jungle-zombie-servant-port-decisions]]。
