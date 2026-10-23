---
name: vanilla-lookcontrol-head-forward
description: "原版 LookControl 无 look 请求时已按 10°/tick 把 yHeadRot 拉回 yBodyRot,所以\"缺 LookForwardsGoal\"不是脖子呆滞的根因;真凶是 FollowOwnerGoal 每 tick setLookAt(owner);修法要在实体 tick() 写 yHeadRot,不能用 LOOK goal"
metadata: 
  node_type: memory
  type: reference
  originSessionId: 6e05f794-1069-466c-b853-be81b3148e3e
  modified: 2026-09-14T02:17:09.367Z
---

`LookControl.tick()` 两个分支：`lookAtCooldown > 0` 时用**调用方给的** yMaxRotSpeed 转过去（`setLookAt(Entity…)` 传的是 `getHeadRotSpeed()`）；`lookAtCooldown <= 0` 时硬编码 `rotateTowards(yHeadRot, yBodyRot, 10.0F)` —— 即**没有 look 请求时头部本来就会以 10°/tick 自动回正到身体朝向**。

**Why:** 排查瞑煌龙"脖子不跟身体转"时我先怀疑"没移植 AC 的 LookForwardsGoal"，实测不成立——AC 的 LookForwardsGoal(priority 6) 会被它自己的 LookAtPlayerGoal(3/5)、RandomLookAroundGoal(4) 抢占，只在"周围没人"时生效，效果和原版 else 分支几乎等价（差别只是瞬间 pin 到 `getYRot()` vs 10°/tick 靠向 `getYBodyRot()`）。真正让长脖子歪着的是 **`Summoned.FollowOwnerGoal.tick()` 每 tick `lookControl.setLookAt(owner, 10.0F, maxHeadXRot)`**：请求被不断续期，else 分支永不触发，跟随状态下 yHeadRot 永久盯着主人，而 `getTargetNeckYRot() = yHeadRot - yBodyRot + buffered` 把这几十度全变成颈子偏航。AC 的瞑煌龙没有 follow goal，所以它走路时头永远朝前。

**How to apply:**
- 需要"走路时头朝前"时**不要**新增 LOOK flag 的 goal：`GoalSelector.tick()` 里高优先级 goal 抢占 flag 时执行的是 `WrappedGoal.stop()`，会把整个持有者目标停掉——放 priority 4 的 LOOK goal 会直接停掉 `FollowOwnerGoal(5)`，仆从从此不跟随（见 [[goety-servant-goal-priority-flags]]）。
- 正确位置是实体自己的 `tick()` 里、`super.tick()` **之后**（goal/lookControl 都在 super.tick() 内跑完），且只写服务端（`!level().isClientSide`），让 `ClientboundRotateHeadPacket` 同步给客户端；客户端也写会和同步包打架抖动。
- 门控用 `!isRiddenByPlayer()`（否则压掉 `tickRidden` 里 `setYHeadRot(player.getYHeadRot())`，骑乘时头不能跟着视角转）+ `!turningFast`（攻击瞄准）+ `areLegsMoving()`（只在走）。
