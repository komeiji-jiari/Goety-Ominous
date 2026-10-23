---
name: double-block-early-removal-eats-drop
description: 双格方块在 playerWillDestroy 里无条件拆另一半会吞掉落——破上半永远不掉，和工具无关
metadata: 
  node_type: memory
  type: reference
  originSessionId: e440cfb1-40a1-4c83-b20f-201e147f2f8c
  modified: 2026-10-11T18:38:52.192Z
---

自建双格方块（`HALF` + `DoubleBlockHalf`，`setPlacedBy` 放上半）的 `playerWillDestroy` 里那句 `level.setBlock(otherPos, AIR, 35)` **只能关在 `if (!level.isClientSide && player.isCreative())` 里**，抄原版 `DoorBlock.java:85-91` / `DoublePlantBlock.java:72-86`。

**Why:** 生存下也提前拆另一半的话，那一刻的 shape 级联拆掉的不是"另一半"而是**正在被挖的那一半自己**：

- `Level.markAndNotifyBlock` 里 `int i = p_46607_ & -34`（`-34 == ~33`）会**清掉** `UPDATE_SUPPRESS_DROPS(32)`，所以级联是**带掉落**的；`Level` 用的是 `CollectingNeighborUpdater`，`addAndRun` 在 `count == 0`（不在级联里）时 `if (!flag) runUpdates()` **当场同步跑完**，不是延迟到 tick 末。
- 级联 → `Block.updateOrDestroy` → `Level.destroyBlock(pos, drop = (flags & 32) == 0, ...)` 跑战利品表。战利品表若带 `block_state_property half=lower`（原版门/高花的写法，也是对的写法），破**上半**时拿到的是 `half=upper` → **0 掉落**；破下半时拿到 `half=lower` → 恰好 1 个（所以只有上半坏）。
- 更致命：级联已经把 `pos` 变空气，`onDestroyedByPlayer` 结尾的 `setBlock(pos, fluid.createLegacyBlock(), 3)` 命中 `LevelChunk.setBlockState` 的 `blockstate == newState → return null` → 返回 **false** → `ServerPlayerGameMode.destroyBlock` 的 `boolean flag = removeBlock(...)` 为假 → **`playerDestroy` 根本不调用**（挖方块掉落的唯一正路被跳过）。
- 原版路子的正确性依赖"级联拆的是另一半"：生存不提前拆 → 破下半时级联拆上半（`half=upper` 空手而归，`playerDestroy` 发下半的掉落），破上半时级联拆下半（`half=lower` 命中，`playerDestroy` 因 `half=upper` 不发）→ 两个方向都恰好 1 个。

**How to apply:** 判据 = 双格 + 战利品表带 `half=lower` + `playerWillDestroy` 无条件拆另一半 → 破上半不掉（`goetyominous:wolf_totem` 就是这个，2026-10-11 修）。排查时**先看挖的是哪一格**，别去查工具：工具只走 `state.is(MINEABLE_WITH_PICKAXE)` + `TierSortingRegistry.isCorrectTierForDrops`，方块不在任何 `needs_*_tool` 里就恒真，连木镐都掉（`EeriePickaxeItem`/`DarkPickaxeItem` 都是裸 `PickaxeItem`，没覆写掉落）。两个残留副作用：级联掉落用 `ItemStack.EMPTY` 当 tool（吃不到时运/精准）、级联不看 `requiresCorrectToolForDrops`（破上半空手也掉 1 个）。

相关：[[forge-maxupstep-mapping]]、[[goety-tesseract-capacity-tags]]
