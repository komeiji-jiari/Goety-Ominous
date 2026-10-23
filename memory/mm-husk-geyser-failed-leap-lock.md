---
name: mm-husk-geyser-failed-leap-lock
description: 突变尸壳地涌起跳扑空会永久锁 AI——原版 MM 遗留 bug，仆从侧已加出口（有意偏离上游）
metadata: 
  node_type: memory
  type: project
  originSessionId: c6e12552-53c3-420f-9575-5bd05d7000b8
  modified: 2026-10-19T11:47:32.483Z
---

原版 MM 的 `MutantHuskGeyserAttackGoal` 有一处死锁：地涌把目标抬起后起跳（`jumped = true`），但砸击要求 `jumping` 期间与目标三维距离 ≤ 3；三维距离常因目标被抬在正上方而永不满足。落地 20 tick 后 `jumping` 被实体自动清掉，`slammed` 仍为 false，而 goal 的 `readyToStop` 只有“砸中落地”或“着火”两条出路（`!jumped && !slammed` 兜底进不去）。goal 又 `isInterruptable()` 返回 false 且占 MOVE+JUMP，其他攻击 goal 全部抢不到，表现为原地不动、不攻击。

**Why:** 上游原版同代码同 bug；不是移植错误。只有目标死亡/尸壳着火才解得开。

**How to apply:** 已在 `MutantHuskGeyserAttackGoal.tick()` 加出口：`jumped && !slammed && !mob.jumping && onGround` → `readyToStop = true`（约扑空落地后 20 tick 退出进冷却）。**这是有意偏离原版**，以后做还原度审计时别当漏搬删掉。丛林/冻僵的跳跃 goal 以动画 tick 结束，没有此问题。相关：[[mm-mutant-husk-servant-port-notes]]、[[mm-jungle-zombie-servant-port-decisions]]、[[frowzy-servant-deliberate-divergences]]。
