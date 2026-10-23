---
name: mc-moveto-every-tick-yaw-whipsaw
description: 近战距离还每 tick 调 navigation.moveTo(target) 会让怪原地甩头/扭身子——MoveControl 90°/tick 硬甩；AC 的结构是 dist<f+1 只播动画不寻路
metadata: 
  node_type: memory
  type: reference
  originSessionId: 8056a397-37f6-44dd-89a5-71c6e4a64947
  modified: 2026-10-08T09:08:12.998Z
---

**症状**：怪在战斗里原地甩头、扭身子，像"找不到该朝哪边"，人却不怎么动。

**机制**（1.20.1 原版，已核对 forge sources jar）：
- `PathNavigation.moveTo(Entity, double)` = `createPath(entity, 1)` → 目标点是**实体当前所在的格**。每次调用都重算一条以目标脚下为终点的路径。
- `PathNavigation.tick()` 每 tick 把 `path.getNextEntityPos()` 推给 `MoveControl.setWantedPosition(...)`。
- `MoveControl` MOVE_TO 分支：`f9 = atan2(dz, dx)*57.3 - 90`，然后 `setYRot(rotlerp(yRot, f9, 90.0F))` —— **单 tick 最多甩 90°**。（注意：1.20.1 的 vanilla MoveControl **不写** `yBodyRot`，AC 的 `VerticalSwimmingMoveControl` 才额外加 `mob.yBodyRot = mob.getYRot()` 硬贴。）
- 所以"已经贴脸了还每 tick 重新寻路到目标脚下"→ 目标一格一格挪动时 wanted position 的方向来回翻 → yaw 每 tick 90° 地反打 → 身体/头（`clampedYaw = clamp(wrapDegrees(yHeadRot - yBodyRot), ±40)`）来回扫。人不动，因为本来就已经在近战距离。

**AC 的写法**（`DeepOneKnightEntity.startAttackBehavior`，也是 `DeepOneAttackGoal` 那一族）是**互斥**的，别摊平成一个 else：

```java
if (distance < (double)(f + 1.0f)) {
    if (getAnimation() == NO_ANIMATION) { setAnimation(SCRATCH/BITE); playSound(); }
} else {
    this.navigation.moveTo(target, 1.2);
}
```

近战距离内**根本不喂导航**：旧路径自然走完 → `isDone()` → 不再 `setWantedPosition` → 没有硬甩；朝向交回 `LookControl.setLookAt(..., 20F, ...)`（20°/tick）平滑跟。

**移植坑**：把 AC 的"近战只播动画"和仓库自研技能（扇形波/冲刺）缝在一起时，很容易写成 `if (放技能) {...} else if (放波) {...} else { moveTo; }` 再接一段 `if (dist < f+1) 播动画` —— 两个块就都跑了，近战距离变成每 tick 重新寻路。正确做法与 AC 对齐：`inMeleeRange` 先算好，所有 `moveTo` 调用点都加 `!inMeleeRange` 门。

实例：`DeepOneKnightServant.startAttackBehavior`（2026-10-08 修，用户报"战斗的时候不要乱转"→选"原地甩头/扭身子"）。同族另两只同样是这个门型：`DeepOneEntity` 用的就是 `dist < (bbWidth+targetBbWidth)+1.0 ? 播动画 : moveTo(target, 1.3)`；`DeepOneMageEntity` 是 `distance > 20+f` 才 moveTo。
