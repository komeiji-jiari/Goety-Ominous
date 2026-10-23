---
name: mm-advanced-animation-client-sync
description: "MM AdvancedAnimation 是服务端权威:start/stop/lock 全 serverSide 门控,客户端只能被动读包;sync 只在状态翻转那一下发、且客户端按实体 ID 找实体、找不到就静默丢 → 新开始追踪的玩家永远学不到当前状态(走路却立正)"
metadata: 
  node_type: memory
  type: project
  originSessionId: 2de83047-6979-4f19-b5d1-4021d6d8c3a8
  modified: 2026-10-15T21:16:34.353Z
---

**MM 的 `AdvancedAnimationTracker`(凋零骷髅那套)是服务端权威状态机** —— `AbstractAdvancedAnimation.start/stop/startLooping/lock/unlock/setTransitionTicks/setLength` 第一行全是 `if (serverSide() && ...)`,客户端调这些**全是 no-op**;客户端只能被动执行 `SyncAdvancedAnimationToClient.handle` 里的 `advancedAnimation.read(buf)`(整份状态:animationTime/length/transitionTicks/amount/amountO/looping/temporaryLock/lock/holdOnLastFrame)。`syncToClient()` 只在状态**翻转**时被调一次(没有心跳、没有脏检查),而且 handle 里是 `Minecraft.getInstance().player.level().getEntity(targetEntityID)` —— **按包到达时刻查实体,查不到就静默 return**。

**推论(2026-10-15 定案"永远不立正"的病根):** 任何"客户端晚于服务端开始追踪该实体"的场合(玩家走进已经在你基地里走动的仆从、重登、维度切换、服务端发的包早于 spawn 包)客户端都是全新实体 + 全 amount=0 → **立正**,而且服务端以为它在走路,`startLooping` 早退(已 isPlaying)什么都不发 → 只有等下一次 walk/idle 翻转才自愈。表现就是用户报的"**有时候立正移动**",追着玩家跑一路立正、停下才好。UP 上游 MM 同病,不是移植漏抄。

**判据/修法(`MutantWitherSkeletonServant` 2026-10-15):**
- `@Override startSeenByPlayer(ServerPlayer)` 只置 `resyncAnimations = true`,真正的重发放到**下一次 `baseTick`**(服务端分支)里 `tracker.animations.forEach((n, a) -> a.syncToClient())` —— 延一 tick 是为了让 spawn 包在同一条 TCP 通道上**严格先于**动画包,不依赖 `addPairing` 与 `startSeenByPlayer` 谁先谁后。全量发(不只 walk/idle)才能连"追踪上来时正在播的死亡/攻击动画"一起补回。
- 另加一条保险丝:同一处 `if (!isDeadOrDying() && isRestPose()) { unlockAllAnimations(); 走/站 对应 `walk`/`idle`.startLooping(0); }`。`isRestPose()` = 遍历 tracker,**排除 `offset_legs`**(它 `startLooping(0)` 恒在播,但只写腿部 Y 偏移,不构成姿势)后没有任何 `isPlaying()` 的动画。攻击/格挡/落地/晕眩期间必有动画在播→不触发;只在"锁死没人解锁"这种真坏状态下触发,不会误伤。
- 服务端本身的锁不变量当时审计过是**闭合**的:5 个攻击 goal 全是 `stopAndLockIdleAnimations` + `start(..., unlockIdleAnimations)` 成对;唯一会**销毁回调**的是 `stop()`(`onFinish = null`),而 `stopAndLockAllAnimations` 只有 `hurt()` 的格挡分支和 `die()` 调,两者之后都紧跟一个会 `unlockAllAnimations` 的动画(block 1.63s / death)。顺带查实:格挡期间 `hurt()` 走 `block.isPlaying()` → `flag=false` 直接免伤,所以"格挡窗口里被打死 → death 被 lock → 死亡动画丢失"这条路**不可达**。

**为什么三个突变僵尸不用改**(同一天查的):它们不走 AdvancedAnimation,而是一组 `xAnimationTick` 计数 + `AnimationState`,模型 `else` 分支永远给正弦 walk/idle/jumping 里一个**满权重(二值 1.0)**,而 `notCurrentlyPlayingKeyframeAnimation()` 与 else-if 链的键帧清单一一对应 → 不在键帧就一定在正弦;走/站/跳的判定又是**客户端本地**算的(`limbSwingAmount`/`onGround`),不需要服务端同步 → 晚追踪的玩家也摆得对。见 [[mm-zombie-sinewave-walk-jitter-fix]]。

**通用规律(移植别的高级动画实体时要记住):** AdvancedAnimator 是**加法**叠加,`lerpAmount() == 0` 的动画什么都不写;所以"永远不立正" ⟺ "任意时刻至少有一个姿势类动画权重 > 0",交叉淡入淡出的两端 transitionTicks 要相等才能让和恒为 1(MM 自己的 walk↔idle 都是 4,block 淡出 4 也对得上)。`lerpAmount()` 里是 `Minecraft.getInstance()` → **服务端不能拿它判断**,服务端只能用 `isPlaying()`(looping 或 animationTime > 0)。
