---
name: trial-loot-tables
description: 试炼宝库/刷怪笼战利品表的链路、热更边界（内容可 /reload、接线在 worldgen/代码里）、已加的 18 条 goety 条目；含 enchant_randomly 指定列表可绕过 isAllowedOnBooks
metadata: 
  node_type: memory
  type: project
  originSessionId: ab079425-7aa4-4142-936b-fcacacd852f1
---

**链路（2026-10-11 全量核实）**
- 宝库（模板里 7 个实例：6 普通 + 1 不祥）：结构 NBT `blocks[*].nbt.config.loot_table` → `goetyominous:chests/trial_chambers/reward` / `reward_ominous`（**分派表**：pool0 = rare w8 / common w2；pool1 = 1~3 抽 common；pool2 = 1 抽 unique）→ 6 张子表。`VaultConfig.DEFAULT_LOOT/DEFAULT_OMINOUS_LOOT` 指向同样两张；`VaultBlockEntity.java:210` **每次开箱现取表** ⇒ 改表内容 `/reload` 生效。
- 刷怪笼：不祥形态在模板 NBT 里显式引用 `goetyominous:spawners/ominous/trial_chamber/{key,consumables}`；普通形态走**代码默认值**（`TrialLootTables` → `spawners/trial_chamber/{key,consumables}` + `items_to_drop_when_ominous`）。
- 与原版 1.21.1 jar 逐条目对比：宝库 8 张 + 刷怪笼 5 张**全部 1:1**（唯一差异 = `items_to_drop_when_ominous` 多一条 `minecraft:tipped_arrow`，属有意偏离）。⚠️ 对比时注意 **1.21 的 loot_table 引用条目字段叫 `value`，1.20.1 叫 `name`**，不然会误判"原版有 0 条"。

**边界（重要）**：改表**内容** = 纯数据、`/reload` 即生效、不需要新世界；改**接线**（宝库用哪张表）要动结构 NBT（worldgen，需新区块）；改普通刷怪笼的接线要改 `TrialLootTables` 代码（需重编译）。

**2026-10-11 已加（仅普通宝库 3 张表，共 18 条 goety 物品）**
- `reward_common` +6（权重和 25→38）：速修腻子 w2 3~6 / 速生之种 w2 6~8 / 觉醒绿宝石 w2 6~8 / 诅咒金属锭 **w3** 6~8 / 指路石 w2 4~6 / 神秘布料 w2 4~6
- `reward_rare` +9（23→39）：浓缩绿宝石 w2 1~2 / **focus 附魔书 w1**（potency,radius,burning,absorb,range,velocity 六选一、随机等级）/ 下界疣蛋 w2 4~6 / 诡异富疣卵 w2 4~6 / 神秘核心·饥饿核心·烈风核心 w2 2~3 / 飞行药膏 w1 / 活力核心 w2 2~3
- `reward_unique` +3（12→15）：狂暴之斧 w1 / 诡异之镐 w1 / 禁书碎片 w1 5~6
- **不祥宝库 +15 条（同日第二批，权重和：common 15→34 / rare 29→34 / unique 10→14）**：`reward_ominous_common` +7（烈风核心 w3 5~6 / 玉块 w3 1~2 / **诅咒金属块 w4** 1~2 / 禁书碎片 w3 2~3 / 活力·饥饿·神秘核心 w2 5~6）；`reward_ominous_rare` +4（不祥碎片 w1 1~2 / 禁书片段 w2 1 / 坚韧药膏 w1 / 衰老面霜 w1）；`reward_ominous_unique` +4（黑暗金属锭 w1 1~2 / 风之·荒野·死灵魔杖 w1 各 1）。
- **普通刷怪笼 supplies +5 条（同日第三批，`spawners/trial_chamber/consumables.json`，权重和 10→18）**：火棘果 `goety:firethorn_berries` w2 2~3 / 熟青蛙脚 `goety:cooked_feet_of_frog` w3 1~2 / **女巫精酿 w1 ×2**（效果分别 `goety:venomous_hands` 剧毒之手、`goety:repulsive` 排斥）/ 啪啪菌团 `goety:snap_fungus` w1 1~2。（`snap_fungus` 游戏内名是"**啪啪**菌团"，用户写"砰砰"。）
- **不祥刷怪笼 supplies +5 条（同日第四批，`spawners/ominous/trial_chamber/consumables.json`，权重和 10→18）**：女巫精酿 ×2（食尸者 `effect.goety.corpse_eater` **3600** / 风旋 `effect.goety.swirling` **1800**）/ **浸药苹果 ×2**（`minecraft:apple`，效果再生 `Id:10`+`forge:id:"minecraft:regeneration"` **900**、瞬间治疗 `Id:6`+`forge:id:"minecraft:instant_health"` **1**，各 w2 1~2）/ 爆破菌团 `goety:blast_fungus` w2 1~3（游戏内名是"**爆破**菌团"，用户写"爆裂"）。至此四批（普通宝库/不祥宝库/普通刷怪笼/不祥刷怪笼）全部完成。
- 🔥 **血案二（2026-10-11 用户报"斧头竟然有保护附魔"）：`minecraft:enchant_randomly` 带显式 `enchantments` 列表时，完全不按物品适用性过滤**（反编译实证：列表**非空 → 直接用**；只有**空列表**才走 `canEnchant(stack)` 过滤）。移植 1.21 表时把 tag 展开成硬列表（1.20.1 的函数只吃列表、不吃 tag）就**丢了过滤** ⇒ `chests/trial_chambers/corridor.json` 的 iron_axe 与 `intersection_barrel.json` 的 diamond_axe 会 roll 到 `minecraft:protection`（39 条列表里含盔甲附魔）。**对照：模组自己的 `goetyominous:enchant_with_levels` 是带过滤的**（`EnchantPoolHelper.getAvailableEnchantmentResults` → `canApplyAtEnchantingTable(stack)`；链路实测：`Enchantment.canApplyAtEnchantingTable` → `ItemStack` → `IForgeItem` 默认 = `enchantment.category.canEnchant(item)`；`Protection` 类别 = `ARMOR`，`ARMOR.canEnchant` = `item instanceof ArmorItem`）⇒ 宝库那两张表的斧头（走这个函数）本来就没事。**修法**：把那两处列表换成"该物品适用子集"，从 **1.21 jar 的 `data/minecraft/enchantment/*.json` 的 `supported_items`（递归解析标签）∩ 原列表**推导 ⇒ 斧头 = **9 条**：`sharpness/smite/bane_of_arthropods/efficiency/silk_touch/unbreaking/fortune/vanishing_curse/mending`（与 1.21 效果等价）。⚠️ 通用教训：**给战利品表加"随机附魔"时，凡是显式列表都得自己保证条目适用**（1.20.1 的 `enchant_randomly` 不会替你过滤）；只有书（`minecraft:book`）例外，什么附魔都合法。
- 🔥 **血案一（2026-10-11 当天发现+修复）：`set_nbt` 的 SNBT 里键名含 `:` 必须加引号**。我写了 `{...,forge:id:"minecraft:regeneration",...}` ⇒ **不祥 consumables 整表反序列化失败变空表 ⇒ 清完怪后静默不吐任何物品**（症状：**只有没被动过的 key 表能出东西**，因为不祥弹射权重 key3:consumables7 ⇒ 30% 出钥匙、70% 空）。根因实证：brigadier `StringReader.isAllowedInUnquotedString` 只允许 `[0-9A-Za-z._+-]`，**冒号不在内** ⇒ 键名被截成 `forge` 后撞上 `id:` ⇒ 整段 tag 解析失败。**修法：写成 `"forge:id"`**（JSON 文件里是 `\"forge:id\"`）。⚠️ 教训：**战利品表里任何一处解析失败 = 整张表变空、只在日志留一行**（`/reload` 后无感）；用 `set_nbt` 前务必先过 SNBT 语法校验。
- ⚠️ **浸药苹果（brew 食物）的机制**：物品就是 **`minecraft:apple` + NBT**（Goety 酿造锅手持苹果点已完成的锅 = `BrewUtils.setCustomEffects`）。**原版吃食物不认 `CustomPotionEffects`**（`LivingEntity.addEatEffect` 只读食物自带效果；全 jar 仅 `Arrow`/`PotionUtils` 引用该键）—— 生效靠 Goety 的 Forge 事件 **`PotionEvents.finishItemEvents`（`LivingEntityUseItemEvent.Finish`）**，且**仅当物品属于 `goety:brewable_food` 标签**（= `minecraft:apple`/`golden_apple`/`enchanted_golden_apple`）✓ 所以加苹果不需要动标签。事件内：`PotionUtils.getMobEffects`（读 Potion tag + CustomPotionEffects）逐条应用（瞬时效果走 `applyInstantenousEffect`）+ `BrewUtils.getBrewEffects` 走 `drinkBlockEffect`。
- **`MobEffectInstance.save()`（Forge 1.20.1 实测）**：`Id`(int **数字** id) + **`forge:id`(字符串，load 时优先覆盖 Id)** + `Amplifier`(byte) + `Duration`(int) ⇒ 战利品表里**两个都写**最稳。数字 id 实证：`instant_health=6`、`regeneration=10`（`MobEffects.register(6/10, ...)` 显式给出）。⚠️ `LivingEntity` 的吃东西路径**只认食物自带效果**，别指望 CustomPotionEffects 自己生效。
- ⚠️ **女巫精酿（`goety:brew`）的效果不能走 `set_potion`**（它不是 PotionItem；`ModPotions` 里只有 climbing/spasms 那几个）：效果存自定义 NBT —— `BrewUtils.getCustomEffects` 读 **`CustomBrewEffects`**，每项 = `BrewEffectInstance.save()` ⇒ **`BrewId`(字符串) + `Amplifier`(byte) + `Duration`(int)**。战利品表写法 = `{"function":"minecraft:set_nbt","tag":"{CustomBrewEffects:[{BrewId:\"…\",Amplifier:0b,Duration:3600}]}"}`。**⚠️ `BrewId` 是翻译键形式**：`BrewEffect(MobEffect)` 构造取 `mobEffect.getDescriptionId()`，而 `Util.makeDescriptionId("effect",id)` = `effect.<ns>.<path>` ⇒ 必须写 **`effect.goety.venomous_hands`**／**`effect.goety.repulsive`**，写成 `goety:xxx` 会**静默无效果**。Goety 酿造站的标准时长：venomous_hands **3600**、repulsive **1800**（`BrewEffects` 注册处）。
- ⚠️ **校验物品 id 的三条路**（缺一条就会误报"未注册"）：`ModItems.java` / `ModBlocks.java`（方块也有物品形态）/ **Goety jar 的 `data/` 里作为 item 被引用**（`tags/items/*.json`、`recipes`、自身方块战利品表）—— `firethorn_berries` 就是这样证实的（ModItems 里没有，但 `minecraft:tags/items/fox_food` + 自己方块的掉落表都指向它）。
- ⚠️ **校验 id 时要同时查 `ModItems.java` 和 `ModBlocks.java`**：`goety:jade_block`、`goety:cursed_metal_block` 是**方块注册**（方块也有物品形态，证据：`forge:tags/items/storage_blocks/jade` 物品标签 + `cursed_ingot_from_block` 配方把它当材料），只查 ModItems 会误报"未注册"。另注意这几个 id 的游戏内名与用户口述有出入：`ominous_shard`=**不祥碎片**（用户写"不详"）、`wind_staff`=**风之魔杖**（用户写"烈风魔杖"）。
- 落盘方式：按文件原格式（键序 `type, functions, name, weight`；`add/count/function` 字母序）生成条目插到 entries 末尾；注意 **`reward_common`/`reward_rare` 是 CRLF、`reward_unique` 是 LF**，必须按各文件自己的换行符写（用 `newline=''` 读写）。校验器注意：rare 表本来就有两条 `minecraft:book`，"重名"不一定是错。

**两个可复用事实**
- `minecraft:enchant_randomly` 带 `enchantments` 列表时**直接用、不再过滤 `isAllowedOnBooks`**（反编译实证）⇒ 能产"本不该上书"的附魔书（Goety 的 focus 附魔全族 `isAllowedOnBooks=false`）。等级 = 该附魔 min~max **均匀随机** ✓。物品写 `minecraft:book`，函数会自动转成附魔书。
- 该批附魔真名：`goety:potency / radius / burning / absorb / **range** / velocity`（**没有 `ranged`** —— 用户口述用了 "ranged"，真名是 range）。

相关：[[trial-chamber-structure-nbt-refs]] [[vault-port-progress]] [[trial-chamber-port-plan]]
