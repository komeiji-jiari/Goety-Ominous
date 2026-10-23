---
name: config-file-split-rule
description: 配置该进 AttributesConfig 还是 MobsConfig —— 照 Goety / goety_cataclysm 的硬规矩：数字进 Attributes，布尔开关进 Mobs
metadata: 
  node_type: memory
  type: reference
  originSessionId: 6a57ba39-21f0-4b79-99c1-e80b3d1212e3
  modified: 2026-09-26T16:43:13.685Z
---

`goetyominous.config` 下的 `AttributesConfig` / `MobsConfig` / `SpellConfig` 是照 Goety 自己的 `com.Polarice3.Goety.config.{AttributesConfig,MobsConfig,SpellConfig}` 建的（名字直接抄的），所以**分工也该照上游**。2026-09-26 用户「参考一下 goety cataclysm 是怎么做的」后核实的规矩：

## 规矩

| | `AttributesConfig` | `MobsConfig` |
|---|---|---|
| 内容 | **只有数字**：Health / Armor / ArmorToughness / Damage / MovementSpeed / FollowRange / KnockbackResistance / AttackKnockback / **DamageCap** | **所有布尔开关** + 召唤上限 + 生成权重 |
| Goety | 布尔键数量 = **0**。`VizierDamageCap` / `ApostleDamageCap` / `EnderKeeperDamageCap` / `RedstoneMonstrosityDamageCap` 都在这 | `ServantsMasterImmune` / `HellfireFireImmune` / `BossInvulnerabilityTime` |
| goety_cataclysm | `GCAttributesConfig` 只有 Health/Armor/Damage，同样零布尔 | `GCMobsConfig` = `NetheriteMonstrosityGriefing` / `AncientRemnantGriefing` / `SymbioctoBreath` / `NetheriteMonstrositySpawnLava` / `DeeplingMoistness` + spawn 权重 |

判据一句话：**`ConfigValue<Boolean>` 就别放 `AttributesConfig`。**

## 仓库现状

2026-09-26 已把最后 4 条漂移清干净，**`AttributesConfig` 布尔键 = 0**。挪过的：`PossessedPaladinInvulnerabilityTime`、`MineGuardianServantExplosionGriefing`、`HullbreakerServantBlockBreakGriefing`、`LeapkelpMoistness`（全进 `MobsConfig`，`LeapkelpMoistness` 直接对应 goety_cataclysm 的 `DeeplingMoistness`）。LM 那批见 [[lm-servant-damage-config]]。

## 挪键的两个坑

1. **toml 路径变了**：`attributes.toml` → `mobs.toml`，老用户改过的值会丢、退回默认。默认值不变时对行为无影响，但**搬之前要看有没有读者**——`PossessedPaladinInvulnerabilityTime` 当时就是"声明+定义齐全但零读者"的死配置，所以搬它零风险。
2. **`Leapkelp.java` 同时要两个 `MobsConfig`**：它 `import com.Polarice3.Goety.config.MobsConfig`（用 `ServantTeleport`），我们的同名类只能写成全限定 `com.qiuyue.goetyominous.config.MobsConfig.XXX`。仓库已有 8 处这么写（`GrottoceratopsServant` / `SkelewagServant` / `HeresiarchServant` …），照抄即可。
