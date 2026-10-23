---
name: goety-multipart-part-projectile-crash
description: 仆从的 PartEntity 肢体被第三方投射物命中会 ClassCastException 崩端;自家肢体覆写 canBeHitByProjectile=false(减伤改几何折算),别人的肢体只能在发射端用 ProjectileImpactEvent 拦(LM 小湮灭炸弹)
metadata: 
  node_type: memory
  type: project
  originSessionId: 7e6dd53a-b46b-4f79-9ced-0bc752342248
  modified: 2026-09-26T20:20:44.932Z
---

非 LivingEntity 的 `PartEntity` 只要 `isPickable()==true` 就会进投射物的射线候选。第三方 mod 常在 `onHitEntity` 里直接 `(LivingEntity) hitResult.getEntity()`,命中肢体即 ClassCastException 崩端(2026-09-23 legendary_monsters 2.2.3 的 small_dimensional_bomb 命中 LuxtructosaurusServantPartEntity;AC 原版 SauropodPartEntity 同样中招,是对方少 instanceof)。

**Why:** `Projectile.canHitEntity` = `canBeHitByProjectile() && ...`,而 `Entity.canBeHitByProjectile()` 默认是 `isAlive() && isPickable()`;Forge 的 `PartEntity` 不覆写它,所以肢体默认可被投射物选中。这是唯一由我们掌控的否决点(射线由对方的 mod 发起,`hurt()` 根本轮不到)。

**How to apply:** 在 PartEntity 子类里覆写 `canBeHitByProjectile()` 返回 `false`。已加固:ac 下的 Atlatitan/Hullbreaker/Luxtructosaurus/TremorzillaServantPartEntity + 继承 AC `ACMultipartEntity` 的 CorrodentServantTailEntity。**不要**动 `MutantBlazeServantShieldPart`——它的盾牌就是要吃箭(pierce 等级>0 的箭破盾后继续打本体),而且它的伤害是服务端权威的。近战射线不走这个判定,砍肢体照常。

肢体减伤没丢,改用几何折算补回:`ac/PartProjectileHits.projectileMultiplier(parent, source, body, part)`。投射物射线在 move 之前就判命中(AbstractArrow.tick 的 `m_6351_(vec32, vec33)`、ThrowableProjectile/AbstractHurtingProjectile 同理),所以 hit 时刻投射物还在**移动前**的位置,当tick走的路径就是 `getBoundingBox().expandTowards(getDeltaMovement())`——用它和 `parent.getParts()` 碰撞箱求交,交到就是"打肢体"。命中肢体时**替换**本体的减伤(AC 里肢体走 `damageSources().generic()` 客户端回传,本体那条 IS_PROJECTILE 分支根本不再执行,不是叠乘)。已接:Atlatitan 0.75→0.33、Luxtructosaurus 0.55→0.33、Hullbreaker 0.65→0.35。Tremorzilla 不走几何折算——AC 里它本体和肢体的倍率**同值**(都是 0.35),直接照 AC 在 `hurt` 里写死 `amount *= 0.35F` 即可(移植版原先漏了本体这条,已补)。Corrodent 尾巴在 AC 里本来就没有倍率(`ACMultipartEntity.hurt` 全伤回传),无需补。

只改得动我们自己的肢体。别人(AC 原版、goety_cataclysm 的 NetheriteMonstrosityServant)的 PartEntity 覆写不了,只能在**我们这边**拦发射端:2026-09-26 加了 `common/events/LmAnnihilationBombGuardEvents`——`ProjectileImpactEvent` 里认出 LM 的 `SmallAnnihilationBombEntity`(只有小的那条没做 instanceof,大的 `AnnihilationBombEntity` 已有 `target instanceof LivingEntity` 守卫),命中目标不是 LivingEntity 就 `setCanceled(true)`。Forge 的钩子在 `ThrowableProjectile.tick` 的 `this.m_6532_(hitresult)` **之前**,取消即整个 impact 不发生(不爆炸、不 discard,炸弹穿过去接着飞)。双端都取消以保持一致,不要只在服务端拦。

相关:[[ac-servant-parity-keep-original]]、[[goety-ac-no-mixin]]、[[forge-entity-sized-vs-getdimensions]]
