---
name: forge-nodeevaluator-per-cell-hooks
description: "自定义 NodeEvaluator 里做\"按格\"判定（亮度/温度/自定义地形）必须覆写哪个方法——evaluateBlockPathType 的 pos 是 mob 自身位置"
metadata: 
  node_type: memory
  type: project
  originSessionId: 7bf2c1f4-aad2-421d-9456-63b08b41921f
  modified: 2026-10-07T23:07:12.552Z
---

给寻路加"按格"判据（亮度、自定义可通行性）时，`evaluateBlockPathType(BlockGetter, BlockPos, BlockPathTypes)` **不能用**：`WalkNodeEvaluator.getBlockPathTypes(...)` 把 **mob 自身位置**当作 `pos` 传进来（`WalkNodeEvaluator.java` 里 `blockpos = p_..._.blockPosition()` 那行；`FlyNodeEvaluator` 的 5 参版同样把自己的 `blockPosition()` 传下去）。在里面按 `pos` 查亮度 = 用仆从自己那格的亮度否决所有邻居，它站在亮处时寻路全灭，站在暗处则永远不绕。

正确的按格钩子（1.20.1 Forge，两个都是"每格调用 + 结果进 cache"的入口）：

- **陆地**（`WalkNodeEvaluator` 子类）：`protected BlockPathTypes getCachedBlockType(Mob, int x, int y, int z)` —— 缓存填充点，包在 `super` 外面加判据即可。
- **飞行/挖掘**（`FlyNodeEvaluator` 子类）：`public BlockPathTypes getBlockPathType(BlockGetter, int x, int y, int z, Mob)` —— 它自己的 `getCachedBlockPathType` 是 **private**，所以只能覆写这个 5 参公开版；缓存 lambda 调的就是它。
- 4 参 `getBlockPathType(BlockGetter,int,int,int)` 是给 `MoveControl.isWalkable` 这类外部调用用的，覆写它不影响寻路本身。

配套两条本来就有用的路径器性质：`getStart()` 不查格子类型、"前一节点 `costMalus < 0` 则放行" ⇒ 把某类格子标 BLOCKED 后，**走得出去、走不进来**（起点在自己脚下不会被自己的判据锁死）。

样板：`CorrodentServant.CorrodentLandNavigator`/`LightAvoidingNodeEvaluator`（怕光期亮格禁行）、`Thug.ThugNavigator`（树叶/门/栅栏改成 OPEN）。导航器侧还要 `createPathFinder` 里 `this.nodeEvaluator = new XxxNodeEvaluator(); return new PathFinder(this.nodeEvaluator, maxVisitedNodes);`，别忘 `setCanPassDoors(true)`（`GroundPathNavigation` 默认会设，自定义后就丢了）。

见 [[corrodent-servant-fidelity-audit]]、[[goety-ac-no-mixin]]（不用 mixin 也能做到，这是正规通道）。
