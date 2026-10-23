---
name: goety-monstrosity-foliage-smash
description: 红石巨兽「撞碎树木」的上游实现细节 —— tag、默认值、扫描方式；以及 AC 仆从 bbox 破坏判定会跳过树叶的坑
metadata: 
  node_type: memory
  type: reference
  originSessionId: f1b49668-5bd4-428e-bf2e-119a64424f6f
  modified: 2026-09-30T13:07:55.858Z
---

用户说「红石巨兽同款撞碎树木」时，指的就是 `com.Polarice3.Goety.common.entities.ally.golem.RedstoneMonstrosity` 里的这段（反编译在 `.scratch/_rm_dec.txt:758-770`）：

- 每 tick、服务端、`isAlive() && !isActivating()`，`getBoundingBox().inflate(0.2)` 扫 `BlockPos.betweenClosed`；
- 命中 `block instanceof LeavesBlock || state.is(ModTags.Blocks.MONSTROSITY_BREAKS)` 就 `level.destroyBlock(pos, true, this)`；
- 门控 = `MobsConfig.RedstoneMonstrosityLeafBreak`（**Goety 默认 true**）+ `ForgeEventFactory.getMobGriefingEvent`。

`ModTags.Blocks.MONSTROSITY_BREAKS` = `#minecraft:leaves` + `#minecraft:logs` + `#minecraft:wool`，定义在 goety jar 内的 `data/goety/tags/blocks/monstrosity_breaks.json`。Goety 是硬依赖，**不需要在本仓库新建同名 tag 覆盖**。

## 坑：AC 仆从的 bbox 破坏判定会跳过树叶

`TremorzillaServant.breakBlocksInBoundingBox` 的 continue 条件含
`!state.is(Blocks.COBWEB) && state.getCollisionShape(level, pos).isEmpty()` ——
树叶的碰撞箱是空的，所以**树叶永远进不了破坏列表**（树干能进）。想让巨人清树冠必须另走通道，别指望这个方法。

2026-09-30 已给撼地斯拉 + 暝煌龙加上同款：共享助手 `common/entities/ally/ac/FoliageSmash.java`（静态方法，照同包 `PartProjectileHits` 的先例），挂载点在两者 `aiStep()` 的服务端块里，每 tick 跑（不跟撼地斯拉原有的 10 tick `blockBreakCounter` 节流）。

- 两只各用一个**新键**、都默认 **true**：`tremorzillaServantBreakTrees` / `luxtructosaurusServantBreakTrees`。命名刻意统一成 `*BreakTrees`，因为范围相同（只管 leaves/logs/wool）。
- 撼地斯拉**既有**的 `tremorzillaServantBreakBlocks`（默认 **false**）保持原样、含义收窄为「地形/近战/光束/尾扫破坏」——它门控 `breakBlocksAround`(:1167) 和 `breakBlocksInBoundingBox`(:1192) 两处，**不是**树木那条通道。别把两者混起来：用户 2026-09-30 明确选了「只开撞碎树木」，不想让巨人开箱就一路挖地。
- 撼地斯拉的树木通道额外带 `!isBaby()`；暝煌龙没有 `isBaby()`，不加。
- 用户明确要求**骑乘时不暂停**，所以没加 `isVehicle()` / `isRiddenByPlayer()` 门控（与仓库「玩家操控时挂起 mob 行为」的惯例相反，是这次的有意例外）。

相关：[[config-file-split-rule]]、[[ac-servant-parity-keep-original]]
