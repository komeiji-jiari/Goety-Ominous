---
name: goety-projectile-base-choice
description: 弹射物换 Goety 基类时选 WaterHurtingProjectile 而非 SpellHurtingProjectile——后者的 canHitEntity 自带盟友过滤会改命中检测
metadata: 
  node_type: memory
  type: reference
  originSessionId: 4f42b101-34d1-4346-a9f4-013beee54de7
  modified: 2026-09-23T08:55:55.370Z
---
Goety 的弹射物基类链是 `AbstractHurtingProjectile` ← `WaterHurtingProjectile`（空灵根，只有 `isAffectedByWater()=false`、`getGravity()=0`、手写 `travel/hitDetection/trailParticle`）← `SpellHurtingProjectile`（加 `DATA_EXTRA_DAMAGE` + `boltSpeed`，并覆写 `canHitEntity` 做盟友过滤）。
**移植外部 mod 的弹射物时选 `WaterHurtingProjectile`**：`SpellHurtingProjectile.canHitEntity` 的盟友过滤会让友军"被射线穿过而不吸收命中"，射线因此打到后面的墙 → `onHitBlock` → 提前引爆/自毁，属于**命中检测**层面的行为改变，不是无害的增益。选父类则 `super.canHitEntity(entity)` 恰好等于原版 `AbstractHurtingProjectile.canHitEntity`（= `Projectile.canHitEntity && !noPhysics`）。
`WaterHurtingProjectile.tick()` 会自己调 `baseTick()`，所以整段覆写 `tick()` 时应写 `this.baseTick()`（**不是** `super.tick()`，那会双倍位移 + 双倍命中）。
`AbstractHurtingProjectile` 的 `readAdditionalSaveData`/`addAdditionalSaveData` 是 **public**，子类覆写不能用 `protected`（`Entity` 声明的是 protected，容易想当然）。
相关：[[ac-servant-parity-keep-original]] [[goety-ac-dinosaur-spirit-port]]