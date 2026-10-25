---
name: mc-1201-vanilla-data-source
description: 1.20.1 权威原版数据（战利品表/标签）在 PCL Forge 客户端 jar 里，不在 mapped_official_ jar；附 1.20.1 数据格式实证清单
metadata: 
  node_type: memory
  type: reference
  originSessionId: 7533bf00-ff71-4c78-8f0b-44374dd78647
---

**路径**：`E:/.minecraft/versions/1.20.1-Forge_47.3.12/1.20.1-Forge_47.3.12.jar`（23MB，PCL 启动器装的 Forge 实例）
- 含完整原版 data pack：`data/minecraft/loot_tables/`（1091 张）+ `data/minecraft/tags/`（413 个），直接 `unzip -p <jar> data/minecraft/loot_tables/...` 取任意 1.20.1 原版表做 1:1 对照。
- ⚠️ 该客户端 jar **没有** `tags/enchantment/`（附魔 tag 在服务端侧）→ 想判断某附魔 tag 在 1.20.1 是否存在，只能搜原版 loot_tables 的引用（如 `on_random_loot` 引用数=0，故 1.20.1 无此 tag）。
- 修正旧结论：`forge-..._mapped_official_1.20.1.jar`（gradle 缓存）确实无 data；**有 data 的是客户端 jar**。

**1.20.1 战利品表 JSON 格式实证**（做移植适配时照着写）：
- `enchant_with_levels`：字段是 `levels` + `treasure`(布尔)；**没有** 1.21 的 `options`（写 `"treasure": false` 等价非宝藏池）。
- `enchant_randomly`：字段是 `enchantments`（1.21 改名叫 `options`），值是 id 列表。
- `set_potion`：`"id": "minecraft:strong_regeneration"`（与 1.21 同）。
- 支持 `random_sequence`（原版 simple_dungeon 等表尾都有）与 `"type": "minecraft:loot_table"` 引用条目（原版 fishing.json 有用例）；`bonus_rolls`/uniform rolls 写法与 1.21 相同。
- ⚠️ **`loot_table` 引用条目的字段是 `"name"`**（1.21 才改叫 `"value"`；`LootTableReference` 常量池只有 name）。
- **1.20.1 API 等价件实证（CFR/javap 落地时踩到的）**：
  - `Codec.validate(...)` **1.20.1 的 DFU 6.0.8 没有**（1.21 才有）→ 用 `.flatXmap(cfg -> cfg.validate(), cfg -> cfg.validate())`；不加会连带 `instance.group(...)` 出"Kind1.group 无法应用"的误导性级联错。
  - `ClipContext` 构造器第 5 参是 **Entity**（1.21 是 CollisionContext）→ 传 `null` 等价 `CollisionContext.empty()`（构造器内部转换）。
  - loot function 的 Gson 体系：`extends LootItemConditionalFunction` + 内部 `Serializer extends LootItemConditionalFunction.Serializer<T>`（`serialize(JsonObject,T,JsonSerializationContext)` / `deserialize(JsonObject,JsonDeserializationContext,LootItemCondition[] )`）；注册用 `DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, MODID)` + `new LootItemFunctionType(serializer)`。
  - `IntProvider` 在 1.20.1 叫 **`NumberProvider`**（`getInt(LootContext)`）。
  - 取附魔 tag 要用 `BuiltInRegistries.ENCHANTMENT`（声明类型 `Registry<Enchantment>`）——`ForgeRegistries.ENCHANTMENTS` 是 `IForgeRegistry`，**不是 Registry 子接口**，没有 getOrCreateTag；附魔 tag 目录 `data/<ns>/tags/enchantment/` 可加载（IllagerInvasion/mutantmore/twilightforest 1.20.1 有先例；vanilla 1.20.1 无附魔 tag，要枚举）。
  - javac 中文报错是 GBK：`grep -a "\.java:" log | iconv -f GBK -t UTF-8` 才可读。
- **没有** `set_ominous_bottle_amplifier` 函数。
- `BlockStateProperties` 里**没有** `OMINOUS`/`VAULT_STATE`（1.21 才有）。
- `ItemEntityRenderer` **没有** `getSeedForItemStack`/`renderMultipleFromCount`（1.21 才抽出成 public static）。
- `UUIDUtil` 只有 `CODEC`/`STRING_CODEC`/`AUTHLIB_CODEC`，**没有** `CODEC_SET`/`CODEC_LINKED_SET`（1.21 有）。

相关：[[trial-omen-captain-drop-findings]] [[mc-1211-asset-extraction]] [[port-data-fidelity-rule]] [[cuotiben-tooling]]
