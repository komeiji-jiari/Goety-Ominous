---
name: wind-charge-knockback-delivery
description: "风弹(1.21 Windy Update)砸地自跳比原版低一格的根因:自定义爆炸别用 hurtMarked 运动包同步玩家击退,要同 tick 发小包让客户端 add"
metadata: 
  node_type: memory
  type: project
  originSessionId: 8c90739f-f3c3-4411-ae6a-0cc9b97df4de
  modified: 2026-10-04T22:13:43.898Z
---

2026-10-04 定位并修复。用户实测「拿 mod 风弹砸地,跳起高度比 1.21.1 原版低一格」。

**根因不在冲击量,在投递方式。** 逐行核对 1.21.1 反编译后确认数值全部一致(半径 1.2F、`ddn(...,Optional.of(1.22f),...)`、落点 `命中点+法线×0.25`、`(1-ratio)*seenPercent*getKnockbackMultiplier`)。差异只有一条回路:

- **原版风弹爆炸不造成伤害**:`SimpleExplosionDamageCalculator(true, false, ...)` 第二个参数是 `damagesEntities`,它的 `shouldDamageEntity()` 返回该值 → false → mod 的 `WindChargeExplosion` 与原版一样跳过 `entity.hurt()` → **`markHurt()` 永不触发 → `hurtMarked` 永不为 true**。所以原版里 `ClientboundSetEntityMotionPacket`(下一 tick 的 replace)根本不会发,抛弹者客户端跳起来**只靠 `ClientPacketListener.handleExplosion` 那一次 `setDeltaMovement(getDeltaMovement().add(knockback))`**(同 tick 的 ADD)。
- 移植时 `WindChargeExplosion.explode()` 的玩家分支多写了 `hasImpulse = true; hurtMarked = true; setOnGround(false);`(1.21.1 的 `dco.c()` 里玩家分支只有 `hitPlayers.put(player, impulse)`,三行都不是原版)。这三行强行让服务端下一 tick 发运动包,客户端 `Entity.lerpMotion` 是 **replace**,落在 ADD 之后一 tick,把上升中的客户端速度换成服务端那份 → **正好吃掉一 tick 飞行 ≈ 一格高度**。

**修法**(`utils/WindChargeExplosion.java` + `common/network/WindChargeImpulsePacket.java` + `client/WindChargeImpulseHandler.java`):删掉那三行,改成本类自己发一个只带 impulse 的客户端包(`PacketDistributor.PLAYER`),handler 照抄 `handleExplosion` 做 `player.setDeltaMovement(player.getDeltaMovement().add(...))`(带 `player.getId() != playerId` 的新旧包守卫),**同 tick、ADD、不触发粒子音效**。改完实测「修好了」。

**Why:** 这个 mod 用自造 `WindChargeExplosion` 替代原版 `Explosion`,而原版的 `ClientboundExplodePacket`(带 hitPlayers 击退)由 `finalizeExplosion` 发出,自造类没有发 —— 于是移植者用 `hurtMarked` 补击退,补上了但语义不同(晚一 tick 且是 replace)。

**How to apply:** 凡是自定义爆炸/击退要作用于玩家,别用 `hurtMarked`/`hasImpulse` 的运动包同步(replace + 晚一 tick,会吃掉高度/速度);发一个同 tick 的客户端小包做 ADD。另外因为不发 `ClientboundExplodePacket`,**旁观玩家也要各自收到那个包**,否则他们的击退会随这三行的删除一起消失(`hitPlayers` 里每个玩家都要 send)。相关:[[goety-projectile-base-choice]]、[[projectile-no-despawn-three-conditions]]、[[ac-servant-parity-keep-original]]
