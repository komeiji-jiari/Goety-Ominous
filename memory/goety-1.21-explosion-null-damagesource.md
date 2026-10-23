---
name: goety-1-21-explosion-null-damagesource
description: "1.21 爆炸传 null damageSource 的兜底是 explosion(直接源, 间接源) 而非裸 explosion —— 生物造成的爆炸会变成 player_explosion"
metadata: 
  node_type: memory
  type: reference
  originSessionId: 97e87718-bd88-49e3-8284-e2cd365e56cc
  modified: 2026-10-07T18:29:48.318Z
---

移植 1.21 的「实体引发的爆炸」时，`Level/ServerLevel.explode(source, null, calculator, ...)` 里那个 `null` **不等于没有攻击者**。

1.21.1 `Explosion` 构造器：`this.damageSource = damageSource == null ? level.damageSources().explosion(this) : damageSource;` —— 走的是 `explosion(@Nullable Explosion)` 重载，取 `getDirectSourceEntity()` 与 `getIndirectSourceEntity()`；而 `getIndirectSourceEntityInternal(LivingEntity)` **把 LivingEntity 自己原样返回**。所以生物引发的爆炸实际是 `explosion(mob, mob)`，两个参数都非空 → 伤害类型落 `minecraft:player_explosion`（不是 `minecraft:explosion`），死亡消息带攻击者。

仓库的 `WindChargeExplosion` 要求显式传 `DamageSource`（没有 null 兜底），所以照抄时必须写
`level.damageSources().explosion(entity, entity)`
——写成 `explosion(entity, null)` 会静默降级成裸 `explosion`，死亡消息丢攻击者。

参照物：`BreezeWindCharge` 走的是 `wind_charge` 自定义伤害类型（那是风弹本体，1.21 确实用 `damageSources().windCharge()`），两者不是一回事，别照抄错那条。

见 [[forge-1.20.1-mobeffect-no-death-hook]]、[[ac-tephra-explosion-quirks]]。
