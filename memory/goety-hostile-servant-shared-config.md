---
name: goety-hostile-servant-shared-config
description: "Goety 惯例是敌对版与仆从版共用同一组属性配置键,只有该族没有敌对对应体时才加 Servant 后缀"
metadata: 
  node_type: memory
  type: reference
  originSessionId: e896b36c-ab47-446a-b096-7db27fb7c037
  modified: 2026-10-04T18:24:08.729Z
---

Goety 里同族的敌对生物和仆从**共用一套属性配置**，不是分开两套。证据来自反编译源码（`.disasm/gsrc`）：

- `hostile/cultists/Warlock` 与 `ally/illager/cultist/WarlockServant` 是两个独立类、各自写 `setCustomAttributes()`，但**都读** `AttributesConfig.WarlockHealth/Armor`；Heretic、Maverick、Reprobate、Piker、Ripper、Crusher 同理。
- `Wraith` / `WraithServant` / `BorderWraithServant` / `MuckWraithServant` 全继承 `AbstractWraith`，基类只读一组 `WraithHealth/Armor/Damage`。
- `NecromancerServant extends AbstractNecromancer`，与敌对 `Necromancer` 同源，连二阶段强化倍率 `Health × max(i*1.25, 1)` 和自愈量都取同一个键。
- `Summoned` / `Owned` 基类**不做任何属性缩放**（grep 不到 `MAX_HEALTH`/`setBaseAttributes`），所以仆从版数值与敌对版逐项相同。

**键名规律**：带 `Servant` 后缀的键（`GhastServantHealth`、`BearServantHealth`、`SkeletonServant*`）只在**该族没有 Goety 自有敌对对应体**时出现——要么是给原版怪做的仆从版（Pillager/Vindicator/Witch/Evoker），要么是仆从独有怪（Bear、Black Wolf、Gnasher）。有敌对对应体就不带后缀。

**配置文件分段按阵营**（`Cultists` / `Illagers` / `General` / `Summoned Mobs` / `Mini-Bosses` / `Bosses`），但**键按怪族命名**，与分段无关——所以 `General` 段里的 `NecromancerHealth` 同时喂给仆从版。

**How to apply:** 给"野生版 + 仆从版"共用一套属性的怪加配置时，别拆两个键；沿用 Goety 的一键共用写法。只有仆从独占的机制（召唤上限、掉落开关）才单独出键，且按 [[config-file-split-rule]] 走 MobsConfig。本仓库 `AbstractHurricane`（同时服务 boss `Hurricane` 和 `HurricaneServant`）与 `Breeze`（服务 `BreezeEntity` 和 `BreezeServant`）都遵循这个规律。

2026-10-04：曾把 `BreezeEntity` 拆出独立的 `breezeHealth/Armor/RangeDamage` 后又按此惯例回退，并把段名从 `Breeze Servant` 改成 `Breeze`（未发布，改键名无成本）。
