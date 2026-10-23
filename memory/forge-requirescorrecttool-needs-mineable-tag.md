---
name: forge-requirescorrecttool-needs-mineable-tag
description: 方块声明 requiresCorrectToolForDrops 却不在 mineable/* tag 里 → 永久无掉落且工具无加速
metadata: 
  node_type: memory
  type: reference
  originSessionId: b29fbf94-157e-4dd3-bc76-bd093f70660d
  modified: 2026-10-08T17:56:08.797Z
---

`BlockBehaviour.Properties.requiresCorrectToolForDrops()` 只是声明"要正确工具才掉落";**"什么工具才算正确"完全由 `minecraft:mineable/*` tag 决定**。两者缺一,症状是三条一起出现:

- 挖得动但**永远没有掉落物**
- 镐子**不加速**(手感"挖掘速度没改")
- 方块属性/loot table/贴图全都正常,查半天查不出

1.20.1 判定链:`DiggerItem` 构造时持有 `TagKey<Block>`(镐子 = `MINEABLE_WITH_PICKAXE`),`isCorrectToolForDrops(state)` 最终落在 `state.is(this.blocks)`;`getDestroySpeed()` = `isCorrectToolForDrops ? speed : 1.0F`;`ServerPlayerGameMode.destroyBlock` 的 `canHarvestBlock` = `!requiresCorrectToolForDrops || player.hasCorrectToolForDrops(state)`,false 时**只破坏方块、不执行 `playerDestroy`**。1.21 把这套搬进了 `Tool` 组件,语义不变。

**排查顺序**:方块属性对了却挖不出东西 → 先看 `data/minecraft/tags/blocks/mineable/` 里有没有它,而不是去翻 loot table(试炼方块那次 53 份 loot table 全都在,就是 tag 漏了)。

**移植时的 tag 归属**(1.21 原版为准,`data/minecraft/tags/block/` 单数目录;1.20.1 是 `blocks/` 复数):
- 凝灰岩系 → 只进 `mineable/pickaxe`,**不进** `needs_stone_tool`(任意镐子)
- 铜系(凿制/格栅/灯泡/门/活板门)→ `mineable/pickaxe` **且** `needs_stone_tool`(要石镐以上)
- `vault` / `trial_spawner` → 两个都不进(原版也没 `requiresCorrectToolForDrops`)

自己新建 `needs_stone_tool.json` 时 `"replace"` 必须 `false`,否则会把原版那份整表清掉。

校验脚本套路:从方块注册类正则抠 `block("name",` 出全集,和 tag 条目求差集,两个方向都查(注册了没进 tag / tag 里写了没注册)。相关:[[of-servant-port-checklist]]、[[mc-weathering-block-per-tier-props]]
