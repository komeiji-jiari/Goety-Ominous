---
name: lm-projectile-ally-check-parity-quirk
description: "圣骑 8 个弹射物的 caster.isAlliedTo(...) 是对的,别当 bug 报——caster 是覆写过 isAlliedTo 的圣骑,而 Goety Owned 的判定本身就很全"
metadata: 
  node_type: memory
  type: project
  originSessionId: 39831f36-71ab-4a74-a9c9-fad5a3614b75
  modified: 2026-09-26T11:47:08.497Z
---

**2026-09-26 曾误报为 bug，同日查实是错的，记在这里防止再犯：** 圣骑的 8 个弹射物（`SoulStrike` / `ThrownPhantomDagger` / `SoulTrident` / `SoulPillarExplosionEntity` / `SoulPillar` / `SoulBlade` / `FallingSoulBlade` / `SoulShield`）里挡盟友写的是 `caster.isAlliedTo(impactEntity)`，看着像 [[ac-ao-ally-check-direction]] 那个"纯队伍判定放跑主人"的形态 —— **实际不是**，因为这两点：

1. **caster 永远是圣骑本身**（`spawnXxx` 传的都是 `this`，三叉戟那条传的是 `this.getOwner()`），而 `PossessedPaladinServant` 自己 `@Override isAlliedTo(Entity)`（约 1461 行）：先 `super`，再判 `pEntity == owner`、`other.getOwner() == owner`、`owner.isAlliedTo(pEntity)`。虚分派到的是这个覆写，不是 `Entity` 的原版实现。
2. **Goety 的 `Owned.isAlliedTo` 本身就极全**（`Summoned extends Owned extends PathfinderMob`）：真主人、主人的盟友、`IOwned` 的 ownerStack、`OwnableEntity.getOwner() == trueOwner`、`isAllyWith` 全在里面。所以哪怕只走 `super` 也不会误伤主人。

结论：**这一组不用改，也没有方向问题。** 另外 `EntityThrown` 走的是 `MobUtil.areAllies(owner, living)`，机制不同但等价。判"某处盟友判定有没有漏主人"时，先确认**接收者的实际运行时类型**有没有覆写 `isAlliedTo`（Goety 侧凡是 `Owned` 子孙都覆写了），别只看静态类型 `LivingEntity`。

相关：[[ac-ao-ally-check-direction]]（那条是真的坑，但主体是 AC 实体不是 Goety 仆从）、[[lm-projectile-model-borrow-impossible]]
