---
name: trial-omen-captain-drop-findings
description: 1.21.1 掉瓶=掠夺者专属战利品表;1.20.1 die 内联坏兆块与精确 mixin 注入点;1.20.1 官方映射 jar 路径与 CFR 用法
metadata: 
  node_type: memory
  type: reference
  originSessionId: 65018864-ac2d-4aec-9ff3-3802858f9ec1
---

**1.21.1 原版实证（libs/1.21.1.jar 反编译+数据）**：
- `Raider.die`（混淆 coj，源行 121:138）**已无坏兆逻辑**，只剩袭击账目（removeLeader / addHeroOfTheVillage / removeFromRaid）。
- 掉瓶在 `data/minecraft/loot_table/entities/pillager.json`：条件 `entity_properties this → type_specific:{type:minecraft:raider, is_captain:true}`，条目 ominous_bottle + `set_ominous_bottle_amplifier`（uniform 0–4）。
- ⚠️ **只有掠夺者**的战利品表挂了这池（vindicator/evoker/illusioner 都没有）；且**无 killed_by_player 条件**（任何死法都掉）。`is_captain` = 头顶不祥旗帜 && `isPatrolLeader()`。
- 1.21.1 obf 扫描心得：`Items.OMINOUS_BOTTLE` 声明类型是基类 `Item`，所以按 `Lcvh;` 描述符找不掉落点（别用类型扫描找字段引用）。
- ⚠️ `libs/1.21.1.jar` 的 lang **只有 en_us.json**（zh_cn 不在），中文译名要另找来源。
- irons_spellbooks-1.20.1 没做队长掉瓶（只有自己的试炼宝箱池），别去那里找参考。

**1.20.1 原版实证（官方映射 jar + CFR）**：
- `Raider.die` 里内联坏兆块：巡逻队长+头戴不祥旗帜+玩家/驯服狼主人击杀+不在袭击中 → 升级式挂 BAD_OMEN(120000t, clamp 0–4)。
- 精确注入签名（`javap -c` 实证，两处调用 owner 都是 Player）：
  - `Player.removeEffectNoUpdate(Lnet/minecraft/world/effect/MobEffect;)Lnet/minecraft/world/effect/MobEffectInstance;`（1.20.1 返回**实例**不是 boolean！）
  - `Player.addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z`
- 1.20.1 **没有** `RaiderPredicate`/`EntitySubPredicates`（type_specific 谓词是 1.20.2+ 的），也没有 loot function `set_ominous_bottle_amplifier` → 数据路线要自造谓词/函数或退化成 NBT+equipment 匹配。
- 1.20.1 `Entity.spawnAtLocation(ItemStack)` 与 Forge 的 `Entity.getRandom()` 都是 public，可直接在 mixin 里调。

**工具路径**：1.20.1 官方命名类在 `C:\Users\MECHREVO\.gradle\caches\forge_gradle\minecraft_user_repo\net\minecraftforge\forge\1.20.1-47.3.22_mapped_official_1.20.1\forge-1.20.1-47.3.22_mapped_official_1.20.1.jar`（含 raid/Raider 等全部类；`--jarfilter` 反编译单个类）；它的 `_mapped_official_` 里没有 data/*.json（没有原版战利品表）。1.21.1 混淆名：Raider=coj、Raid=coi、Items=cut、OminousBottleItem=cvh、EntityType=bsx、EquipmentSlot=bsy、ServerLevel=aqu、DamageSource=brk、ItemStack=cuq。

相关：[[mc-1211-asset-extraction]] [[windy-port-1to1-principle]] [[port-data-fidelity-rule]]
