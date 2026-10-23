---
name: goety-friendly-fire-suppresses-tipped-arrow-effects
description: "Goety Summoned 同主人友伤免疫让 hurt 返回 false,原版 AbstractArrow 因此跳过 doPostHurtEffects,治疗/增益药箭对同主人仆从零效果;须自定义弹射物直接 addEffect"
metadata: 
  node_type: memory
  type: project
  originSessionId: e31aa220-bc19-477d-a6fa-4dc6cbb28b2b
  modified: 2026-10-21T14:27:31.673Z
---

Goety `Summoned.hurt` 在 `MobsConfig.ServantsMasterImmune` 开启时,对同主人(且双方 `getTrueOwner()` 非 null、同一实例)的非敌对 Summoned 伤害源直接 `return false`。原版 `AbstractArrow.onHitEntity` 只在 `entity.hurt(...)` 返回 true 时才调用 `doPostHurtEffects(...)`,所以**药箭命中同主人仆从时药水效果完全不会施加**——不是 NBT/药水数据丢了,是伤害门被免疫短路。玩家主人不受此限(不是 Summoned),无主仆从(双方 owner 均 null)也不免疫,箭正常造成伤害并生效。

修复样板(2026-10,PP 炼金术士治疗箭):自定义 `AlchemistArrow extends Arrow`,在 `setEffectsFromItem` 里把 `PotionUtils.getPotion` / `getCustomEffects` 缓存到自己的字段(原版 `Arrow` 的 `potion/effects` 是 private),覆写 `onHitEntity`:目标为同主人 `AbstractPiglinServant` 时按 `duration/8` 时长直接 `addEffect`,命中音效 + `discard()`,否则走 `super`。`performRangedAttackAt` 遇到 `Items.TIPPED_ARROW` 时构造该箭头。炼金术士僵尸变体继承同一路径,无需重复改。投掷药水 `ThrownPotion` 不走 hurt,不受影响。

**Why:** 友方辅助弹射物如果依赖"命中造成伤害"来触发效果,在 Goety 仆从体系里必然失效。

**How to apply:** 做治疗/增益类弹射物时,别依赖 `hurt` 返回值;覆写 `onHitEntity` 对友方目标直接应用效果。排查"药箭没效果"先看目标是否同主人 Summoned,而不是查药水数据。[[goety-summonspawn-owner-order]] [[goety-projectile-base-choice]]
