---
name: mm-wide-mob-waypoint-offset-yaw-sway
description: 宽生物的寻路路点被 Path.getEntityPosAtNode 偏移 (int)(bbWidth+1)*0.5（疣猪兽 2.9 → 1.5 格），跟随/斜走时 yRot 反复跳，身体整体左右晃；MM 侧改为中心路点 + 中心判定后消失
metadata: 
  node_type: memory
  type: reference
  originSessionId: acfb4920-9f33-4cd2-85ec-13764f598e15
  modified: 2026-10-22T12:22:52.667Z
---

**症状**：突变疣猪兽仆从走路时整个身体（连后腿和尾巴）左右转——是实体 yRot，不是模型胸/腰的正弦动画。

**机制（1.20.1）**：
- `Path.getEntityPosAtNode` 的路点 = `node + (int)(bbWidth + 1) * 0.5`。宽 <1 时是 0.5（格中心）；`bbWidth` 1.0~1.9 → 1.0；**2.9 → 1.5**（格中心再往前一格，且 x/z 都加）。
- `GroundPathNavigation.followThePath` 的 Forge 判定用 `node + (bbWidth+1)/2`（2.9 → 1.95）+ `bbWidth/2`（1.45）半径。
- 跟随目标每 10 tick 重算路径，斜向路径 / 楼梯状路径下，MoveControl 的 aimed 方向会在相邻节点间跳 45~90°；`rotlerp(..., 90)` 单 tick 就能甩过去。
- 我们给 MM 仆从的 `MutantBodyRotationControl` 是 `yBodyRot += diff*0.3`（不是原版移动时硬贴），于是这些 yRot 急跳被低通成平滑的整身摆动 → "一晃一晃"。

**修复（2026-10-22，`AbstractMutantServant` 内 `MutantNavigation`）**：路点当成格中心。
- `MutantNavigation.createPathFinder` 返回 `MutantPathFinder`，把 `Path` 包成 `MutantPath`；`getEntityPosAtNode` 返回 `node + 0.5`（与 `ServantPathNavigation` 给 LM 的做法一致）。
- 覆写 `followThePath`，推进判定改成 `|mob - (node+0.5)| <= maxDistanceToWaypoint`（`shouldTargetNextNodeInDirection` 照抄原版）。
- 数值（`.scratch/_navsim4.mjs` 模拟 2.9 宽、0.2 格/tick、每 10 tick 重算）：斜向跟随身体 yBodyRot 摆幅 38.7°→8.9°、楼梯路径 76°→27°；直线无变化。疣猪兽偏移最大所以最明显；所有 MM 仆从共用此导航，宽 ≥1 的都受益。

**判据**：Vanilla 的偏移本身对 <1 宽无影响；排查"宽生物走路身体左右摆"先查 `getEntityPosAtNode`，别先怀疑正弦动画（腿是 `everything` 的子节点，body 的 ROTATION_Z 管不到）。

相关：[[forge-followthepath-bbwidth-ulp-freeze]]、[[lm-modpathnavigation-shortcut-stub]]、[[mm-sine-walk-phase-footstep-lock]]、[[mutant-hoglin-anim-fix]]
