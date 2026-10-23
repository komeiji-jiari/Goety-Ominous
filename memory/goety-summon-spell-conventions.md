---
name: goety-summon-spell-conventions
description: Goety SummonSpell 的必覆写面 + 各派系召唤法术的 CastingSound/完成音效对照表 + 四个易漏项
metadata: 
  node_type: memory
  type: project
  originSessionId: f679e98a-fbbe-4801-81b0-f32a882d650a
  modified: 2026-09-25T08:47:12.900Z
---
召唤聚晶模板 = `com.Polarice3.Goety.common.magic.SummonSpell`（反编译在 `build/cfrgoety9/com/Polarice3/Goety/common/magic/SummonSpell.java`）。抽象面只有 4 个：`defaultSoulCost` / `defaultCastDuration` / `defaultSpellCooldown`（来自 `Spell`）+ `SummonDownDuration`（来自 `ISummonSpell`）。
**易漏项：**
- `acceptedEnchantments()` 不写 → `Spell` 基类返回**空 list** → `MagicFocus.canApplyAtEnchantingTable` 直接拒绝附魔台（不是"没效果"，是附不上）。
- `summonLimit()` 不写 → 走基类默认 64。仆从侧还要覆写 `IOwned.getSummonLimit(owner)` 对齐 —— 那个只被 `RitualRequirements` 用（仪式召唤），法术侧不看它。**"无上限"就给 `Integer.MAX_VALUE`**：两处消费方（`SummonSpell.conditionsMet`、`RitualRequirements`）都只做 `count >= limit` 判定，没有别的算术，所以 int 上限天然等于无限。
- `summonPredicate()` 被**三处**复用：`conditionsMet`（数已有仆从判上限）、`commonResult`（潜行时把仆从传到身边）、`SpellResult`。必须包含目标类，否则上限统计和潜行召回一起失效。
- `rightStaff` 基类默认就是「魔杖的 `getSpellTypes()` 含本法术派系」，派系一致时**不用覆写**；覆写只是为了多认几种杖（VoltSpell 认 STORM 杖就是这么来的）。`getSpellTypes()` 只影响加成和 `SpellAttribute`，**不禁施法**。
- `uponSummon(...)` → `summonParticles` 才有派系色召唤粒子；只在 `addFreshEntity` 返回 true 时调。NETHER / VOID / WILD / GEOMANCY 有专属色，**ABYSS 和 WIND 落白色 NORMAL_SUMMON**。
- **落位**：必须 `BlockFinder.SummonRadius(caster.blockPosition(), 实体, worldIn)` → `moveTo(pos, yRot, 0F)` → `MobUtil.moveDownToGround(实体)`，顺序照抄 `WatchlingSpell`。`SummonRadius` 会拒掉碰撞/含液体的候选点（`moveTo` 前调用，它按实体当前 boundingBox 偏移判定，不需要先给实体定位），`moveDownToGround` 再向下射线贴合地面。**飞行仆从也照做**：Goety 的 `WatchlingSpell` / `SnarelingSpell` / `BlastlingSpell` 全是飞行/悬浮单位且都调了它，`MobUtil.moveUpFromGround` 上游零调用点。`caster.blockPosition().offset(±2, +1, ±2)` 这种写法是 `VoltSpell` 带来的例外，地形不平时会把仆从卡进方块里。
**派系音效对照（上游召唤法术实测）：**
| SpellType | CastingSound | 召唤完成 |
|---|---|---|
| ABYSS | `ABYSS_PREPARE_SPELL` | `DROWNED_NECROMANCER_SUMMON`（GuardianSpell） |
| VOID | `VOID_PREPARE_SPELL` | `SUMMON_SPELL`（Watchling/Snareling） |
| FROST | `FROST_PREPARE_SPELL` | `SUMMON_SPELL`（IceGolem） |
| WILD | `WILD_PREPARE_SPELL`（上游 SwarmSpell 自己返回 null） | — |
| NETHER | 邪恶套 `APOSTLE_PREPARE_SUMMON`，否则 `PREPARE_SUMMON`（BlazeSpell） | `SUMMON_SPELL_FIERY` |
| NECROMANCY | `PREPARE_SUMMON`（ZombieSpell） | `SoundUtil.playNecromancerSummon(caster)` |
| STORM / WIND / 其余 | `PREPARE_SUMMON`（SpriteSpell） | `SUMMON_SPELL` |
派系专属 prepare 音效**只有 FROST / WILD / ABYSS / VOID 四个**，其余派系别再找，一律 `PREPARE_SUMMON`。`CastingSound()` 无参和 `CastingSound(LivingEntity)` 带参并存，后者默认转发到前者，只有要查 `CuriosFinder`（邪恶套）时才需要带参版。
`getSpellType()` 一处改三件事：魔杖加成判定、**施法粒子形态**（`Spell.useParticle` 按派系分支：FROST 雪花 / GEOMANCY 方块尘 / STORM 电 / WILD 方粒+雾 / WIND·ABYSS·VOID·NETHER·NECROMANCY 权杖聚气）、focus tooltip 的派系文字。完整枚举 11 个：`NONE NECROMANCY NETHER ILL FROST GEOMANCY WIND STORM ABYSS WILD VOID`。
**OF 仆从聚晶的数量规则（用户朋友定的）：** 错误魔杖恒定 **1 只**；正确魔杖 `worldIn.getRandom().nextIntBetweenInclusive(2, 5)` = **2–5 只随机**，且每只都置精英。对应惧鱼（ABYSS）、震颤蜗牛（GEOMANCY）、伏特（ABYSS，`rightStaff` 额外认 STORM 杖）三个。
**例外（用户明确要求）**：天空飞龙（WIND）**恒定单召，连 `rightStaff` 分支都不写**（每只带 20–23 个 segment 实体）。`rightStaff()` 上游只在各法术自己的 `SpellResult` 里被调用，没有外部调用点，不写就彻底没了。
**贪食怪聚晶已由用户要求整体删除**（`GuzzlerSpell` + `OfItems.GUZZLER_FOCUS` + 模型 JSON + lang `guzzler_focus[.info]` + `SpellConfig` 的 Guzzler 四组配置全清掉）；贪食怪现在只能靠仪式拿。所以 OF 现存**召唤**聚晶是**惧鱼 / 天空飞龙 / 震颤蜗牛 / 伏特 / 切削者**五个（切削者另有 `dicer_laser_focus` 攻击聚晶；火焰史莱姆两种都没有）。
注意上限检查在施法**前**（`conditionsMet` 数已有仆从），所以一次召 5 只可以超出 `summonLimit` —— Goety 自己的 `WatchlingSpell` 就是这个行为，别当 bug 修。
**"哪个长袍治疗它" ≠ 法术派系，别拿前者推后者。** 治疗分组在 `ServantUtil.HealType`：`isWildHeal` = `getMobType()==NATURAL||ARTHROPOD||wild_heal tag`，而 `isGeoHeal` **只认 `goety:geo_heal` 实体 tag**（没有 GEO mobtype）。OF 仆从 `getMobType()` 普遍返回 `ModMobType.NATURAL` → 一律落进 WILD 治疗桶，但派系完全可能是别的（震颤蜗牛就是 mobtype NATURAL + `SpellType.GEOMANCY`）。想给它 Geo Robe 治疗要另外加 tag。
**派系该看同类召唤物的先例**：滚动/带甲撞人的走 `RollerSpell`（岩滚兽）= GEOMANCY。
**派系 SpellAttribute 是死路径：** `ModAttributes.getPotency(caster, spell)` 按**法术自己的** `getSpellTypes()`（默认 `[getSpellType()]`，不是魔杖的）挑 `NETHER_POTENCY` / `WIND_POTENCY` 之类，然后读**施法者身上的属性值**。但 Goety 本体只有 `ModAttributes` 里注册了这些属性，**全上游没有任何地方给玩家授予**（本仓库也只注册了自定义 FEL 的），所以对任意派系法术这条路径恒返回 0；魔杖/聚晶都不会经它给加成。它们是留给附属 mod 的扩展点。别再重复"拿对应派系魔杖吃 potency"这个错说法。
**Why:** 这些全是查 Goety jar 才有的约定，仓库里没有第二处记录，凭印象挑音效必错。
**How to apply:** 新写召唤聚晶时按这张表选音效、对照四个易漏项过一遍。
相关：、[[goety-summonspawn-owner-order]]、[[goety-move-speed-two-channels]]