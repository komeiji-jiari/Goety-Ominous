---
name: goety-summoned-dohurttarget-duplicate
description: Goety Summoned.doHurtTarget 已含亡灵徒手点燃+武器耐久，移植 OF 僵尸类时别重复搬 Monster.doHurtTarget
metadata: 
  node_type: memory
  type: reference
  originSessionId: 9dced9ba-62ae-4dba-8491-210f006590d7
  modified: 2026-10-18T12:09:56.771Z
---

Goety 2.5.56.0 里 `Summoned.doHurtTarget`（先走 `Owned.doHurtTarget`）已经实现：
- 有主人时伤害走 `ModDamageSource.summonAttack(this, owner)`，并处理附魔/击退/破盾；
- `getMobType() == UNDEAD`、徒手、自身着火时按 `difficulty * 0.3` 点燃目标；
- 主手可损坏物品扣耐久（`ItemHelper.hurtAndBreak`）。

OF 的 `Frowzy` 这类 `extends Monster` 的原版实现会自己重写 `doHurtTarget` 做点燃——直接搬进 Goety 仆从就会和基类各掷一次骰，点燃概率翻倍。同理，把 `isSunSensitive()` 设为 true 后，日光灼烧由 `IServant.servantTick → burnServant` 负责，不要再写 `aiStep`（否则绕过 `MobsConfig.UndeadServantSunlightBurn/Helmet` 并双扣头盔耐久）。

**Why**: 这是"原版 Monster 没有、Goety 基类有"的差异，逐行对着 OF 反编译看不出来。
**How to apply**: 移植 OF 亡灵系仆从时先查 `Summoned` 已覆写的方法，只保留基类没有的 OF 覆写；见 [[of-servant-port-checklist]]、[[ac-servant-parity-keep-original]]。
