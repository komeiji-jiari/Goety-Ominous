---
name: trial-chamber-structure-nbt-refs
description: "移植试炼密室结构 NBT 时必须改的战利品表/音效等引用命名空间清单（minecraft: → goetyominous:），否则引用会指向 1.20.1 不存在的原版条目"
metadata: 
  node_type: memory
  type: project
  originSessionId: 0a67d0e5-b114-477d-a06a-cd45bdc57deb
---

**移植 1.21 试炼密室结构（jigsaw 模板 NBT）时，凡是引用原版数据的地方都要把命名空间改成 `goetyominous:`** —— 因为我们把表放在自己的数据包里，`minecraft:` 下没有这些条目，引用会静默失效（掷到空表）。

必须在结构 NBT 里改的引用（TrialSpawnerBlockEntity 的 `spawn_data` / `normal_config` / `ominous_config` 里）：

| 结构 NBT 字段 | 1.21 原版值 | 本模组值 |
|---|---|---|
| `spawn_data.equipment.loot_table` | `minecraft:equipment/trial_chamber_melee` | `goetyominous:equipment/trial_chamber_melee` |
| 同上 | `minecraft:equipment/trial_chamber_ranged` | `goetyominous:equipment/trial_chamber_ranged` |
| `normal_config.loot_tables_to_eject` / `ominous_config…` | `minecraft:spawners/trial_chamber/{key,consumables}` | `goetyominous:spawners/trial_chamber/{key,consumables}` |
| `normal_config.items_to_drop_when_ominous` | `minecraft:spawners/trial_chamber/items_to_drop_when_ominous` | `goetyominous:spawners/trial_chamber/items_to_drop_when_ominous` |
| `ominous_config.loot_tables_to_eject` | `minecraft:spawners/ominous/trial_chamber/{key,consumables}` | `goetyominous:spawners/ominous/trial_chamber/{key,consumables}` |

相关的本模组数据路径（1.20.1 目录名是 **`loot_tables`** 复数）：
- `data/goetyominous/loot_tables/spawners/trial_chamber/{key,consumables,items_to_drop_when_ominous}.json`（3 张）
- `data/goetyominous/loot_tables/spawners/ominous/trial_chamber/{key,consumables}.json`（2 张）
- `data/goetyominous/loot_tables/equipment/trial_chamber.json` + `equipment/trial_chamber/{chainmail,iron,diamond}.json` + `equipment/trial_chamber_{melee,ranged}.json`（6 张）

**Why:** 表内容都是从 1.21.1 原版逐字段 diff 过来的（只改了命名空间和 3 处格式），但**引用它们的结构 NBT 不会自动跟着改**；漏改的话表现是"怪物没装备/不喷奖励"，不报错，极难查。

**How to apply:** 移植/转换密室结构 NBT 时，按上表把 `minecraft:` 前缀替换掉；另外装备表用的是 `minecraft:entity` 参数集（1.20.1 没有 EQUIPMENT），掷表时已由 `TrialSpawner.equipMob` 提供 ORIGIN/THIS_ENTITY/DAMAGE_SOURCE。

相关：[[trial-spawner-port-plan]] [[mc-1201-vanilla-data-source]]
