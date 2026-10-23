---
name: forge-followthepath-bbwidth-ulp-freeze
description: Forge MC-94054 补丁让 followThePath 的判定点与 MoveControl 的转向点错开，bbWidth=0.95 的怪永久站桩
metadata: 
  node_type: memory
  type: project
  originSessionId: 07f3c0be-d34c-459b-80a3-e34f62002e71
  modified: 2026-09-23T21:10:30.260Z
---

Forge 1.20.1 给 `PathNavigation.followThePath()` 打的 MC-94054 补丁，把判定点从原版的 `node + 0.5` 改成 `node + (bbWidth + 1) / 2D`，而 `MoveControl` 实际被驱向的 `Path.getEntityPosAtNode` 仍是 `node + (int)(bbWidth + 1.0F) * 0.5`。bbWidth ∈ [0.75, 2) 时两者差值恰好等于容差 `maxDistanceToWaypoint = bbWidth / 2`，判定 `d0 <= tol` 全看浮点舍入。

`0.95F` 是唯一判假的宽度（`0.95F/2` = 0.47499999499320984，而 `(0.95F+1)/2D − 0.5` = 0.47500002384185791），差一个 ULP → `advance()` 永不触发 → `getNextEntityPos()` 永远返回自己脚下 → `MoveControl` 停在到达分支 → speed 恒 0 → 永久站桩。幼年 bbWidth 减半到 0.475，差值 0.2375 < 容差 0.5125，所以幼年正常行走。

**Why:** 这是寒霜潜伏者仆从多年"成年后一动不动"的根因，不是 AI goal 或 noActionTime 问题。全仓库 `sized(0.95F, ...)` 只有它和 MutantShulkerServantTrap（不走路），故只有它中招。

**How to apply:** 覆写 `createNavigation` 返回的 `GroundPathNavigation`，重写 `followThePath()` —— 先 `super`，若索引没动且 `path == getPath() && !isDone()`，就改用 `path.getNextEntityPos(this.mob)`（真正的转向点）配 `maxDistanceToWaypoint` 再判一次并 `path.advance()`。以后新增宽度落在 [0.75, 2) 的步行实体，先按上式验算。相关：[[forge-flying-stroll-deadzone]]、[[goety-summon-idle-freeze]]
