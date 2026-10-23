---
name: goety-target-search-area-vertical-4-blocks
description: NearestAttackableTargetGoal 的搜索盒纵向只有 ±4 格，悬停在高空的飞行仆从因此永远索不到地面怪
metadata: 
  node_type: memory
  type: reference
  originSessionId: fe08c6f8-3da9-48f5-8514-b3e24973e0d6
  modified: 2026-10-09T18:11:30.809Z
---

原版 `NearestAttackableTargetGoal.getTargetSearchArea(double)` = `this.mob.getBoundingBox().inflate(distance, 4.0, distance)` —— **横向 ±followDistance，纵向只有 ±4 格**。`findTarget()` 就用这个盒做 `getEntitiesOfClass`。

后果：悬停在主人头顶 6~10 格的飞行仆从，与地面怪的垂直差经常 >4 → 目标压根不在搜索盒里 → `findTarget()` 恒返回 null。横向再近（9 格、18 格）都没用。表现为「战斗时鸟宁可绕玩家飞」，且**按个体粘滞**（`heightOffset = id%5` 决定谁恰好压进 ±4 带）。主人主动打怪却全体扑上去，是因为 `OwnerHurtTargetGoal` 直接塞目标、完全绕过搜索盒。

判据（一次就能定死）：同 tick 用均匀盒 `bbox.inflate(32)` 找得到怪、用真实盒 `bbox.inflate(fr, 4, fr)` 找不到 ⟹ 就是纵向问题。日志里直接对比鸟的 `pos.y`：低飞到贴近地面时立刻锁定，爬回悬停高度就丢。另一个标志是索敌 goal 的 `canUse()` 每 tick 都在被调用（计数器猛涨）却**从不成功**。

别误判成：goal 优先级、`FOLLOW_RANGE` 太小、目标残留。真实 leash 是 `TargetingConditions.range(followDistance)`（3D 距离，与搜索盒互不相干）；`TargetGoal.canContinueToUse()` 末尾还会 `mob.setTarget($0)`，所以一旦锁定，目标每 tick 被重设，不存在「goal 挂着旧目标不补」。**保留**只查 3D 距离，不受搜索盒影响 —— 所以症状永远是「锁不上」，不是「掉了捡不回来」。

修法：自定义 `SummonTargetGoal` 子类覆写
`protected AABB getTargetSearchArea(double distance) { return this.mob.getBoundingBox().inflate(distance, distance, distance); }`
再覆写 `Summoned.targetSelectGoal()`（Goety 就在那里注册原版那个）换成自己的实例。撑高盒子安全：径向仍由 `TargetingConditions.range` 把关。

相关：[[soul-vulture-servant-port]]、[[goety-servant-follow-standby]]、[[goety-servant-goal-priority-flags]]
