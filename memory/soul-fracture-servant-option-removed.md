---
name: soul-fracture-servant-option-removed
description: 灵魂碎裂（SOUL_FRACTURE）的仆人开关与全部叠加代码已删除，仆从永不施加该 debuff
metadata: 
  node_type: memory
  type: project
  originSessionId: df7594ad-41f6-4424-9529-697887305b47
  modified: 2026-10-25T23:44:21.865Z
---

2026-10-25 用户要求删除开启 soul fracture 的配置，选择「整段删掉」。已删 `LmConfig.SoulFractureOnServantHit`，以及圣骑 3 处命中、`SoulPillar` / `SoulPillarExplosionEntity` / `SoulShield`（2 处）/ `SoulTrident` / `ThrownPhantomDagger` 的叠加块，`IAnimatedBossServant.applyStackingEffect()` 随之删除。

**Why:** 这是 LM Boss 的叠层 debuff，用户对跑到仆从身上的 boss 机制一贯要求「不是关掉而是删掉」（同 [[lm-servant-boss-mechanic-stripping]]）。

**How to apply:** 以后补弹射物 / 移植招式时不要给仆从重新接 `ModEffects.SOUL_FRACTURE`，也不要加回开关；原版 Boss 自己的逻辑不在此列。可调开关的同类取舍见 [[servant-speed-follow-config-removed]]。
