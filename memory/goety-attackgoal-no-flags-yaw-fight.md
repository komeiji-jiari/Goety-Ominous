---
name: goety-attackgoal-no-flags-yaw-fight
description: "无 flag 的 Goety AttackGoal(照抄上游 Leapleaf$AttackGoal)每 tick instaLook 硬写 yRot/yBodyRot/yHeadRot,会与并发的 MOVE goal 的 MoveControl.rotlerp 抢朝向 → 每 tick 抖动=肉眼『转』;陆生怪几乎撞不上,移植成两栖怪后 GoToBeach/GoToWater/Wander 常驻并发才暴露;修法=照自家水中同款补 MOVE+LOOK+JUMP"
metadata: 
  node_type: memory
  type: reference
  originSessionId: 0672aaf0-77b6-46d7-81bf-27f28135f10d
  modified: 2026-10-03T18:23:05.308Z
---

潮涌之叶「有时候还是会转」(2026-10-03 晚)的根因,和 [[vanilla-swim-movecontrol-xxa-reference]] 记的 xxa 那个是**两回事**。

## 机制

`MobUtil.instaLook(mob, entity)` 一次写三个:**`setYRot` + `yBodyRot` + `yHeadRot` 全硬贴目标方向**(还带 `lookAt(target,100,100)`)。上游 `Leapleaf$AttackGoal.m_8037_()` 就是每 tick 这么写。而 `MoveControl.tick()` 在其后(goal→nav→**moveControl**→look→jump→`LivingEntity.tick()` 尾部 `BodyRotationControl` 把 `yBodyRot = yRot`)把 yRot 用 `rotlerp` 拧向 **waypoint**,陆上 90°/tick、我们水里 20°/tick。

于是只要**同时**有另一个持 MOVE 的 goal 在 `navigation.moveTo()`:

- goal 段:yRot = 目标方向(instaLook 硬贴)
- moveControl 段:yRot 被 rotlerp 拉回 waypoint 方向(≤90°/tick)
- 每 tick 重复 → 朝向在「目标」和「waypoint」之间来回跳 → **看起来就是在原地转/抽搐**

## 为什么上游不炸、我们炸

上游 `AttackGoal` 的构造器**没有 `m_7021_`(setFlags)** —— 这不是漏写,是上游 `Leapleaf` 是**陆生**的:陆地砸地时能并发抢 MOVE 的只有 Wandergoal 之类,而 `FollowOwnerGoal` 要 `target == null` 早被排除,所以撞上的概率极低。

**我们的 `Leapkelp` 是两栖的**,而且陆地砸地 `canUse` 特意允许「在水里 + 踩底」([[goety-leapkelp-land-water-leap]] 第 15 条的水面站立态)。这个状态下:

- `LeapkelpSmashGoal`(0 号,**无 flag**)能跑;
- `LeapkelpGoToBeachGoal`(3)/`GoToWaterGoal`(3)/`SwimUpGoal`(6)不查 `isMeleeAttacking()`,也不要求 `target == null`,于是**同时**在跑 —— 尤其水边打岸上目标时 `wantsToLeaveWater()` 恒真,Beach goal 每次都抢 MOVE。

→ 怪一边砸地一边被往岸上拽 + 朝向每 tick 抖 = 用户看到的「转」。

## 修法

`LeapkelpSmashGoal` 补 `setFlags(EnumSet.of(MOVE, LOOK, JUMP))`,与自家 `LeapkelpWaterSmashGoal` 完全一致。0 号优先级压得住所有并发 goal(`canBeReplacedBy` = `interruptable && candidate.priority < running.priority`),`nav.stop()` 本来每 tick 就调,补 flag 后才能真正独占。**失败模式是"没抢到"(就退回今天的行为),不会把砸地搞坏**,所以安全。

## 教训

移植 Goety 的 goal 时,**"上游没写 setFlags"要当成结论来复验**:上游怪的活动域(陆/水/空)决定了那个位置会不会有并发 MOVE goal。怪的生态一改(陆生 → 两栖),原来无害的"无 flag + 每 tick 硬写朝向"就变成每 tick 抢朝向。凡是 tick 里调 `instaLook`/`lookAt` 硬贴朝向的 goal,都要问一句「谁在和它抢 yRot」。

相关:[[goety-servant-goal-priority-flags]]、[[forge-movecontrol-runs-after-goals]]、[[vanilla-swim-movecontrol-xxa-reference]]、[[goety-leapkelp-land-water-leap]]
