---
name: ac-tephra-explosion-quirks
description: AC TephraExplosion(流星/瞑煌龙落地爆炸)三个反直觉点:放火不看 mobGriefing、KEEP 仍放火、伤害由半径公式算出而非攻击力属性
metadata: 
  node_type: memory
  type: reference
  originSessionId: d1c93426-cb94-4686-8895-427df6b5a15a
  modified: 2026-09-14T01:50:25.360Z
---

`com.github.alexmodguy.alexscaves.server.entity.util.TephraExplosion`(AC 抄的原版 Explosion 改版,仆从的 TephraEntity 与 2026-09-14 加的瞑煌龙落地爆炸都用它):

- **放火不看书 mobGriefing**:`finalizeExplosion` 结尾那段"空气格 + 下方实心 → 1/3 概率 `BaseFireBlock.getState`"写在 `if (flag /* interactsWithBlocks */)` **之外** ⇒ 即使 `BlockInteraction.KEEP`(仆从 Tephra 被 `EntityMobGriefingEvent` DENY 成 KEEP)也照样点火。要真正禁火只能不用这个类。
- **伤害与攻击力属性无关**:`explode()` 里伤害 = `(d10² + d10)/2 * 9 * f2 + 1`(`f2 = radius * 2`,d10 = (1-距离/f2) * 视线遮挡率)⇒ 半径 2 ≈ 37、半径 3 ≈ 55 点,而 `LuxtructosaurusServantDamage` 默认才 12。想按属性调就得换掉这个类。
- **不伤自己但要护友军**:`explode()` 用 `level.getEntities(this.source, aabb)` 取实体(source 被排除),且实体循环里只特判 `instanceof LuxtructosaurusEntity`(AC boss)⇒ 仆从自身安全,但主人/同主人仆从会被炸。本仓库靠 `LuxtructosaurusTephraHandler.onLivingAttack` 兜:direct 是 `LuxtructosaurusServant` 且 source `is(DamageTypeTags.IS_EXPLOSION)` 时,友军/自己走 cancel + `PENDING_VELOCITY_RESTORE`(爆炸的击退是 `setDeltaMovement` 直接加的,取消伤害挡不住)。
- `explode()` 与 `finalizeExplosion()` 全在服务端跑(粒子走 `sendParticles`、音效 `ACSoundRegistry.TEPHRA_HIT` 音量 4)。

相关: [[ac-ao-ally-check-direction]] [[ac-goety-port-probability-gates]]
