---
name: goety-servant-missing-attack-damage
description: 自建攻击型仆从必须注册 ATTACK_DAMAGE，否则 Aether 武器减伤查询 source 属性会崩服
metadata: 
  node_type: memory
  type: project
  originSessionId: e336502b-123a-4b4b-b3e5-ce357f0ab660
  modified: 2026-10-23T15:36:56.122Z
---

`Mob.createMobAttributes()` **不含** `ATTACK_DAMAGE`（只有 `Monster.createMonsterAttributes()` 会加默认 2.0）。我们的 MM 仆从都用 `Mob.createMobAttributes()` 建属性表，而伤害全走配置值直接 `hurt(...)`，所以从来没注册过攻击力。

Aether 的 `AbilityHooks$WeaponHooks.reduceWeaponEffectiveness` 会对 DamageSource 源头的 LivingEntity 调用 `getAttributeValue(ATTACK_DAMAGE)`；属性表里没有该键时 `AttributeSupplier` 直接抛 `IllegalArgumentException: Can't find attribute minecraft:generic.attack_damage`，服务端 tick 崩（2026-10-23 突变尸壳 roar 打动物时实测）。

**Why:** 凡是自己发出 LivingEntity 伤害源的实体都可能中招，不止尸壳；Goety 自带的 `Summoned` 不补属性。

**How to apply:** 自建攻击型仆从/实体在 `createConfiguredAttributes()` 里补 `.add(Attributes.ATTACK_DAMAGE, <主攻击配置>.get())`。已补 MutantHusk / MutantFrozenZombie / MutantBlaze / MutantShulker / MutantShulkerTrap（2026-10-23）。该属性不驱动我们自己的伤害计算，只是给外部模组读；纯投弹型（如 Rodling 火球）源是 Projectile，不走这个分支。

相关：[[mm-mutant-husk-servant-port-notes]]
