---
name: trial-spawner-port-plan
description: 试炼刷怪笼移植**已完成**（2026-10-08）：落地清单、1.20.1 无法消除的差异清单、以及核对时踩过的坑
metadata: 
  node_type: memory
  type: project
  originSessionId: 0a67d0e5-b114-477d-a06a-cd45bdc57deb
---

第四批（试炼刷怪笼 + 不祥 + OminousItemSpawner）**已全部落地并编译通过**（2026-10-08），只差进游戏实测。

**已交付**
- Java：`TrialSpawnerState` / `TrialSpawner` / `TrialSpawnerBlock` / `TrialSpawnerBlockEntity` / `TrialSpawnerRenderer`（在 `common.blocks.trial` 与 `client.render.block`）、`OminousItemSpawner` + `OminousItemSpawnerRenderer`、`FlyStraightTowardsParticle`、`EquipmentTable` + `SpawnDataWithEquipment`（装备表机制）、独立 `WindCharge`（`common.entities.projectile`）
- mixin：`TrialSpawnerLevelRendererMixin`（3011/3012/3013/3014/3019/3021）、`SpawnEggItemMixin`（让刷怪蛋能点自家刷怪笼 —— 1.21 走 `Spawner` 接口，1.20.1 只能 mixin）
- 资源：blockstate/6 模型/12 贴图/`cube_bottom_top_inner_faces.json`(parent 要显式写 `minecraft:`)/3 个粒子 json+贴图/39 个 ogg/sounds.json 16 条/中英字幕
- 数据：5 张刷怪笼表 + 6 张装备表（[[trial-chamber-structure-nbt-refs]] 有引用清单）
- 注册：TrialBlocks/ModBlockEntities/ModEntityTypes/ModSounds(16+1)/ModParticleTypes(3)/TrialSoundTypes/ClientEvents/TrialCreativeTabs(刷怪蛋页签)/ModModelLayers

**⚠️ 创造页签血案（2026-10-10，已修）**

`0e6ae964`（"spawn_eggs 改名 compat，已进原版页签的物品从自建页签剔除"）**只做了半套**：它把 `TrialCreativeTabs` 里**风弹 / 重锤 / 旋风棒+重核 / 两把试炼钥匙 / 不祥之瓶的 amplifier 循环 / 试炼刷怪笼**这六处**原版页签插入删掉**，但这些物品**没有**进 `ModCreativeTab.isVanillaTabbed`（该集合只有陶片、旗帜图案、锻造模板、唱片、TrialBlocks 除 VAULT/TRIAL_SPAWNER）⇒ 原版页签没了、自建页签又没剔 ⇒ 只剩主页签。症状："试炼内容全堆在模组页签里、不祥之瓶只剩一个"（瓶子原本在 FOOD_AND_DRINKS 里 amplifier 0~4 共 5 个 + 主页签 1 个）。

修法（用户拍板方案 A＝恢复原状、两处都有）：`TrialCreativeTabs` 补回六处 + `OminousBottleItem` import。锚点：风弹 `after(WRITABLE_BOOK)`；重锤 `after(TRIDENT)`（COMBAT 整块）；旋风棒+重核 `after(BLAZE_ROD)`；钥匙接在 `after(SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, ...)` 串尾；不祥之瓶在 FOOD_AND_DRINKS 用 `putAfter` 串 0~4；刷怪笼 `after(SPAWNER)` 进 SPAWN_EGGS。原文取 `git show 0e6ae964^:src/.../TrialCreativeTabs.java`。

**两套语义别混**：`isVanillaTabbed` 里的（陶片/旗帜图案/模板/唱片/TrialBlocks）＝"只在原版页签"；上面六样＝"两处都有"。再"顺手去重"就会复现血案。

**1.20.1 无法消除的差异（写代码时别当 bug 修）**
- `SpawnData` 1.21 有第三个组件 `Optional<EquipmentTable>`（配套 `Mob.equip(EquipmentTable)` / `EquipmentUser` 接口 / `Equipable` 数据组件定槽位）→ 1.20.1 全都没有 → 本模组用 `SpawnDataWithEquipment` 重建编解码（NBT 布局与 1.21 一致）＋自写 `equipMob/resolveEquipmentSlot`（`ArmorItem`/盾/Forge `IForgeItem.getEquipmentSlot`，参数集用 `minecraft:entity` 并补 DAMAGE_SOURCE）
- `MobSpawnType.TRIAL_SPAWNER`（1.21 新增）→ 用 `SPAWNER`
- `Mob.dropPreservedEquipment()`（1.21）→ 1.20.1 没有，省略（irons 也留空）
- `ProjectileDeflection`/`Projectile.deflect`（1.21）→ 1.20.1 完全没有这套机制 → 风弹"前 5 刻不可被偏转"移植不了也不需要
- `Registries.LOOT_TABLE`（1.21 才有 loot 表注册表）→ 1.20.1 全链用 `ResourceLocation`
- `Block.appendHoverText`（1.21）/`Spawner` 接口 → 物品 tooltip、接口化都省略

**⚠️ 有意偏离 1.21 的地方（以后跟原版对照时别当 bug 改回去）**
1. `TrialSpawnerData.getOrCreateDisplayEntity` 前置 `spawner.canSpawnInLevel(level)`（照 GoetyAwaken）→ 和平下笼内显示实体隐藏
2. `TrialSpawnerData.resetKeepingSpawnData()`：**禁用刷怪路径（和平/关 doMobSpawning）只清计数、不清 `nextSpawnData`**；冷却结束仍走原版 `reset()`（保留按 `spawn_potentials` 重掷）；`reset()` 本身是原版语义没动
3. `TrialSpawner.tickServer` 顶部的禁刷怪守卫：`canSpawnInLevel` 为假时 → 清计数 + 强制 INACTIVE + **`return` 跳过状态机**（修掉原版"和平不灭 + 切回疯狂闪烁"）
4. `TrialSpawner.checkSpawnRulesForTrialSpawner`：`MobCategory.MONSTER` 时只判 `difficulty != PEACEFUL` —— 这是**补** 1.21 的光照旁路（`MobSpawnType.ignoresLightRequirements(TRIAL_SPAWNER)`），不是偏离；不改则一只怪都刷不出来（试炼刷怪笼激活态自身发光 8 级）
5. `SpawnDataWithEquipment` 的装备槽位判定用 `ArmorItem`/盾牌/Forge `IForgeItem.getEquipmentSlot` 替代 1.21 的 `Equipable` 数据组件
6. 战利品表 `items_to_drop_when_ominous` 的两条"箭 + `set_potion`"写的是 `minecraft:tipped_arrow`（1.20.1 的 `setEffectsFromItem` 只认这个物品，1.21 靠数据组件不看物品类型）

**核对时踩过的坑（SRG/混淆陷阱）**
- `dsf` 里 `$$15.fR()` 是 `Mob.setPersistenceRequired()`（不是 spawnAnim）；`dsh` 里 `$$2.fQ()` 是 `dropPreservedEquipment()`。我一度把两处都写成 `spawnAnim` → 刷怪笼的怪会自然消失，是真 bug
- `Direction.a` = **DOWN**（不祥物品生成器朝下打）；`AbstractArrow.Pickup` 在 1.21 被重排成 `DISALLOWED=a, ALLOWED=b, CREATIVE_ONLY=c`
- 1.21 的 `com.mojang.math.Axis` 被混淆进默认包 `a.class`：a=XN b=XP c=YN **d=YP** e=ZN f=ZP
- `DataResult.ifSuccess/ifError` 在 1.20.1 不存在 → 用 `resultOrPartial`；`InclusiveRange.contains(T)` 1.20.1 叫 `isValueInRange(T)`；`SimpleWeightedRandomList.Builder.add(E)` 不存在 → `add(E,int)`
- 1.20.1 的 `LevelRenderer.levelEvent` switch 只到 **3009** → 3011-3021 全部空置可用
- **`Vec3` 静态工厂**：`a=atLowerCornerOf`、`b=atCenterOf`、**`c=atBottomCenterOf`（`c` 不是"角"，喷物位置曾因此偏半格）**；实例方法 `add->b`、`offsetRandom->a`、`relative->a`、`vectorTo->a`
- **`Mth`**：`square=h`、`floor=d`、`wrapDegrees=g`、`lerp=i(双精度d)`、**`rotLerp=j(双精度e)`** —— 原版两个渲染器故意用了不同插值（`SpawnerRenderer` 用 `lerp`、`OminousItemSpawnerRenderer` 用 `rotLerp`），抄错就自转倒退一格
- `EntityType.getBaseClass()` 在 1.20.1 是**假 API**（写死返回 `Entity.class`）→ 判类型用 `getCategory()`
- `Mob.setPersistenceRequired()`（fR）不是 spawnAnim（这个曾导致刷怪笼的怪会自然消失）；`Mob.dropPreservedEquipment()`（fQ）1.20.1 没有 → 省略
- 1.21.1 客户端 jar（`libs/1.21.1.jar`）被裁剪过：**没有 sounds.json、sounds/ 下 0 文件**，但有 lang/models/blockstates/particles/textures 和 `data/minecraft/loot_table/**`（战利品表可直接读）

**全量校对结论（2026-10-10，34 份模板 + 9 组别名）**
- **普通形态的 `loot_tables_to_eject` / `items_to_drop_when_ominous` 在模板里是省略的**，靠 codec 默认值 ⇒ 默认值必须是本模组的表：`TrialLootTables.java` 实证用 `GoetyOminous.MOD_ID` 建 `spawners/trial_chamber/{key,consumables,items_to_drop_when_ominous}`（5 张表全在盘）；不祥形态的 eject 表才在 NBT 里显式写 `goetyominous:spawners/ominous/...`。若以后看到"普通刷怪笼不喷奖励"，先查这里。
- **与 1.21.1 jar 对照法**：`libs/1.21.1.jar` 的 `data/minecraft/structure/trial_chambers/spawner/**`（19 份，含 5 个 connectors 与 breeze）可逐字段 diff；已知**8 类替换**归一化后应 19/19 全等：`minecraft:→goetyominous:` 的 `trial_spawner`、`waxed_chiseled_copper`、`chiseled_tuff`、`bogged`、`breeze`、前缀 `spawners/`、`equipment/`、`trial_chambers/`（connectors 的 `pool` 字段）。
- 实测：16 个 mod 侧实体（goety 12 + goetyominous 4，含 `breeze`=`ModEntityTypes:369`）**全部 MONSTER**；9 对 ranged/slow_ranged 姊妹一致（方块/ominous/去节奏 normal 全同，快 20/3.0/0.5、慢 160/4.0/2.0）；baby_zombie 的 `simultaneous_mobs=2.0` 是**原版值**（勿当 bug 改）。

相关：[[trial-chamber-structure-nbt-refs]] [[vault-port-progress]] [[mc-1211-asset-extraction]] [[git-add-new-files]]
