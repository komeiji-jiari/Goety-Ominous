---
name: lm-servant-damage-config
description: LM 仆从的伤害配置——LM 的 *DamageMutliplier 键与 boss 共用(会互相干涉),10 个 LM 仆从已全部改用仓库自有键 + 全局 LivingHurtEvent 按施暴实体缩放
metadata:
  node_type: memory
  type: reference
  originSessionId: 6a57ba39-21f0-4b79-99c1-e80b3d1212e3
  modified: 2026-09-29T21:58:51.066Z
---

## 问题

LM 的每个 boss 招式伤害是**字面量**，唯一的全局旋钮是 `ModConfig.MOB_CONFIG.<Boss>DamageMutliplier`（`net.miauczel.legendary_monsters.config.ModConfig`，**不是**仓库的 `MobsConfig`）。移植时逐行照抄，所以这些键被带进了仓库，导致：调 LM 的 boss 会连带改我们的仆从，反之亦然。

**两个坑**（都是 LM 自己的，不是移植引入；2026-09-26 用 `.class` 常量池 grep 核实，手法见 [[goetyominous-deadcode-analysis-scripts]]）：

1. 同一个键 LM 的 boss 也在读。
2. **投射物类读错键**：`AnnihilationBombEntity` 读 `StratlingDamageMultiplier`、`SmallAnnihilationBombEntity` 读 `FlameDrifterDamageMutliplier`。仓库的 `projectile/AnnihilationBomb` / `SmallAnnihilationBomb` 忠实照抄，而这两个投射物**被 4 个仆从共用**（Pursuer / FlameDrifter / Obliterator / AnnihilationExplosion）→ 给 FlameDrifter 调伤害会同时改掉另外三家的炸弹。改造后这个副作用自然消失（改成按发射者缩放）。

## 别的 mod 怎么做的（远古遗魂 = Cataclysm Ancient Remnant）

goety_cataclysm 的 `AncientRemnantServant`（`extends IABossSummon`）：

```
伤害 = getAttributeValue(ATTACK_DAMAGE) * <硬编码倍率> + min(同左, 目标最大生命 * hpdamage)
```

- 基数 = **攻击力属性**（addon 自己的 `GCAttributesConfig.AncientRemnantDamage`）；倍率**仍然硬编码**（`1.35f` / `0.9f` / 冲锋 `2.0f`）。
- 只有「目标最大生命百分比」进配置：`CMCommonConfig.AncientRemnant.{HpDamage=0.05, StompHpDamage=0.03, ChargeHpDamage=0.1}` —— 用的是 **Cataclysm 原版**的配置类。

**结论：它没消灭硬编码**，只是把"绝对伤害值"换成"属性 × 比例"。照办会被别的 mod 的属性修饰符放大，且与 [[ac-servant-parity-keep-original]] 的"别把数值换成属性值"冲突。

## 仓库的做法（2026-09-26 全部完成，`compileJava` 过）

**全局事件按施暴实体缩放**，而不是在每个伤害点乘：

- `IAnimatedMonsterServant.damageMultiplier()`（默认 `1.0D`）—— 10 个 LM 仆从的公共祖先，唯一的挂钩点。
- `common/events/LmServantDamageEvents.java`：`LivingHurtEvent` → `event.getSource().getEntity() instanceof IAnimatedMonsterServant servant` → `setAmount(amount * servant.damageMultiplier())`；`== 1.0` 直接 return。
- `MobsConfig` 新增 10 条 `<x>ServantDamageMultiplier`（Double，默认 1.0，紧挨各自的 `<x>ServantLimit`；Flameborn/WanderingEye 家族没有 limit，排在一起）+ 一条 `theObliteratorCloneBurstHpDamage`（Integer，默认 7，对齐 LM 的 `TheObliteratorCloneBurstHPDamage`）。共用一个 `LM_DAMAGE_MULTIPLIER_COMMENT` 字符串数组当 `.comment(...)` 实参。
- 各仆从覆写 `damageMultiplier()` 返回自己的键。**投射物照样不用改**：`getEntity()`(causing entity) 对 `mobAttack(this)`、`ModDamageTypes.causeAnnihilationDamage(owner, owner)`、`causeGravityDamage(owner, owner)`、分身 `getCaster()` 全都成立，所以一个钩子覆盖近战+投射物+分身。

**清掉的 LM 键读取点**：Pursuer ×5 / Paladin ×4 / ShulkerMimic ×4（含 `:1157` 那个 LM 自己优先级写歪的 `damage - nerf*key + …`，照原样只去掉 key）/ CloudGolem ×1 / FlamebornGuard ×1 / FlamebornWarrior ×1 / FlameDrifter ×1 / HoveringHurricane ×1（读的是 `StratlingDamageMultiplier`）/ WanderingEye ×1 / Obliterator ×8 / `BigShulkerBullet` ×1 / `AnnihilationBomb` ×1 / `SmallAnnihilationBomb` ×1 / `TheObliteratorCloneArmed` ×1。**默认值下伤害与 LM 逐位相同**。

改完顺手删掉了 8 个文件里变成未使用的 `import net.miauczel.legendary_monsters.config.ModConfig;`（CloudGolem 与 Paladin 仍用着 `*DamageCap`/`*InvulnerabilityTime`，保留）。

## 非"倍率"类 LM 键也已全部迁走（2026-09-26，`compileJava` 过）

同一类干涉（和 LM boss 共用），一并迁到 `MobsConfig`，各带自己的默认值语义：

| LM 键 | 仓库键 | 类型 | 默认 |
|---|---|---|---|
| `MiniBossDamageCap` | ~~`AttributesConfig.MiniBossServantDamageCap`~~ **2026-09-29 已删** | Double | ~~21.0~~ |
| `CloudGolemDamageCap` | `AttributesConfig.CloudGolemServantDamageCap` | Double | 18.0 |
| `CloudGolemLosesConsciousness` | `MobsConfig.CloudGolemServantLosesConsciousness` | Boolean | true |
| `CloudGolemCanCharge2Times` | `MobsConfig.CloudGolemServantCanCharge2Times` | Boolean | true |
| `CloudGolemInvulnerabilityTime` | `MobsConfig.CloudGolemServantInvulnerabilityTime` | Boolean | true |
| `CloudGolemProjectileImmunityTimer` | `MobsConfig.CloudGolemServantProjectileImmunityTimer` | Boolean | true |
| `TheObliteratorFirstPhaseProjectileImmunity` | `MobsConfig.TheObliteratorServantFirstPhaseProjectileImmunity` | Boolean | true |
| `Overgrownprojectile` | `MobsConfig.OvergrownColossusServantProjectileImmunity` | Boolean | true |
| `allowFallingCloudParticles` | `MobsConfig.CloudGolemServantFallingCloudParticles` | Boolean | true |
| `PossessedPaladinInvulnerabilityTime` | `MobsConfig.PossessedPaladinServantInvulnerabilityTime` | Boolean | true |

### 放哪个文件有硬规矩（2026-09-26 查上游定的）

用户让我"参考 goety cataclysm 怎么做"后核实的：

- **Goety 自己的 `AttributesConfig` 里布尔键数量 = 0**，纯数字（Health/Armor/Damage/FollowRange/**DamageCap**）。`VizierDamageCap` / `ApostleDamageCap` / `EnderKeeperDamageCap` / `RedstoneMonstrosityDamageCap` 全在这里。goety_cataclysm 的 `GCAttributesConfig` 只有 Health/Armor/Damage，一条布尔都没有。
- **开关一律 `MobsConfig`**：Goety 的 `ServantsMasterImmune` / `HellfireFireImmune` / `BossInvulnerabilityTime`；goety_cataclysm 的 `GCMobsConfig` = `NetheriteMonstrosityGriefing` / `AncientRemnantGriefing` / `SymbioctoBreath` / `NetheriteMonstrositySpawnLava` + spawn 权重。仓库的 `<X>ServantBreakBlocks` / `LuxtructosaurusServantFire` 正是照这个来的。

⇒ **受伤上限归 `AttributesConfig`**（和仓库已有的 `PossessedPaladinServantDamageCap` / `TheObliteratorServantDamageCap` / `TremorzillaServantDamageCap` 对齐），**开关归 `MobsConfig`**。所以 `PossessedPaladinInvulnerabilityTime` 从 `AttributesConfig` **搬到了** `MobsConfig`（改名 `<X>ServantInvulnerabilityTime`），并把 `MobsConfig` 里两条 Cap 挪进了 `AttributesConfig`（`MiniBossServantDamageCap` 单独一组 `"Legendary Monsters Mini-Boss Servants (Optional - LM)"`，在 LM 各组之前）。

**仓库里原有的 3 条同类漂移也一并挪了**（2026-09-26 用户「挪」，`compileJava` 过）：`MineGuardianServantExplosionGriefing`（AC）、`HullbreakerServantBlockBreakGriefing`（AM）、`LeapkelpMoistness`（Goety 本体）从 `AttributesConfig` 搬进 `MobsConfig`。**`AttributesConfig` 现在布尔键 = 0**，与上游完全一致。具体拆法与坑见 [[config-file-split-rule]]。
- ~~`MiniBossDamageCap` 是 `IAnimatedMiniBossServant.damageCap()` 的默认实现~~ —— **2026-09-29 用户「我想删掉小boss限伤」后整套已删**，见下节。圣骑 / 湮灭者 / 云傀儡各自覆写 `damageCap()`、走 `AttributesConfig.<X>ServantDamageCap`，**这三条没动**。
- `allowFallingCloudParticles` 归 CloudGolem 名下是核过的：`projectile/CloudEntity` 只被 `CloudGolemServant:1746/1932` 创建。
- **至此全仓库再无 `net.miauczel.legendary_monsters.config.ModConfig` 的引用**（grep 为空），LM 配置彻底解耦。`ForgeConfigSpec.ConfigValue<Boolean>` vs LM 的 `BooleanValue` 不影响，`get()` 都返回 `Boolean`。

### 默认值是从哪个 jar 抠出来的

`MobsConfig` 的默认值来自 **`8902349`**（`build.gradle:242` 声明的 `curse.maven:legendary-monsters-944035:8902349`，deobf 后在 `~/.gradle/caches/forge_gradle/deobf_dependencies/curse/maven/legendary-monsters-944035/8902349_mapped_official_1.20.1/`）。它的 `ModConfig$MobConfig.class` 是 **32661** 字节。

**别拿别的 jar 查 LM 配置**：`libs/legendarymonsters-2.1.20.jar`（32315 字节）、`2.1.18`、gradle 缓存里的 `8657289`、以及 `.scratch/lm_cfg` 那份 2.2.3 反编译**都缺** `CloudGolemProjectileImmunityTimer` 和 `TheObliteratorFirstPhaseProjectileImmunity`（前两个键是 8902349 才有的）。`mods.toml` 里 `version` 一律写死 `"1.20.1"`，**不能用来判版本**，只能靠 `.class` 字节数 / 常量池有没有那个键。判据见 [[lm-mechanic-presence-version-check]]。

## 小 boss 限伤已整条删除（2026-09-29，`compileJava` 过）

用户「我想删掉小boss限伤」。删的是 `IAnimatedMiniBossServant` 那一条分支（`MiniBossServantDamageCap`
连同 `damageCap()` / `damageReduction()` / `REDUCED_DAMAGE_TICKS` / `reducedDamageTicks` /
`tick()` 覆写 / `hurt()` 覆写，以及 `AttributesConfig` 里那个键和它单独的
`"Legendary Monsters Mini-Boss Servants (Optional - LM)"` 组）。

**为什么能连 `hurt()` 一起删**：`damageReduction()` 在 mini-boss 分支返回字面量 `1` 且 5 个
子类**没有一个覆写**，所以 `pAmount *= damageReduction()` 是恒等变换，`reducedDamageTicks`
只是喂这个空转的刻度尺；剩下的 FALL 免疫 `IAnimatedMobServant.hurt` 已经有了（还多一条 IN_WALL）。
全链 `IAnimatedMiniBossServant → IAnimatedMobServant → IAnimatedMonsterServant(isCarryImmune) → Summoned` 完好。

**受影响 5 个**（原本共用 21.0）：`AnnihilationPursuerServant` / `OvergrownColossusServant` /
`ShulkerMimicServant` / `BeheadedKnightServant` / `ResurrectedKnightServant`。现在全额吃伤害。

**没动的**：`IAnimatedBossServant` 分支——它有一套**完全不同**的减伤系统
（`damageTimeFactor` / `damageAdaptationTicks` / `reducedDamage()`，不是 `damageReduction()`），
圣骑 21 / 湮灭者 20 / 云傀儡 18 各自保留。

这是**有意偏离 LM**（LM 的 `IAnimatedMiniBoss.hurt` 有限伤）。和
[[ac-servant-parity-keep-original]] 那次删 AC 蜥脚类限伤是同一诉求：限伤是玩家看得见的数字，
不许按"移植对齐"的名义自作主张留着。

边界依据见 [[ac-servant-parity-keep-original]]。
