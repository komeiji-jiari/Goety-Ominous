---
name: forge-hot-floor-fireimmune
description: "hot_floor 属于 #minecraft:is_fire；Entity.isInvulnerableTo 对 fireImmune 实体直接短路 → 岩浆火焰伤害本来就打不动 fireImmune 仆从"
metadata: 
  node_type: memory
  type: reference
  originSessionId: f9629217-d45e-4fb1-9e1f-138a63788b4a
  modified: 2026-09-13T15:41:34.392Z
---

1.20.1 `data/minecraft/tags/damage_type/is_fire.json` 含 `in_fire/on_fire/lava/hot_floor/unattributed_fireball/fireball`；`Entity.isInvulnerableTo` 是
`isRemoved() || invulnerable && !BYPASSES_INVULNERABILITY && !isCreativePlayer() || source.is(IS_FIRE) && fireImmune() || source.is(IS_FALL) && type.is(FALL_DAMAGE_IMMUNE)`。

所以 `EntityType.Builder.fireImmune()` 的实体挨 `damageSources().hotFloor()` 时 **hurt 直接返回 false**，`setSecondsOnFire` 也会在 `baseTick` 被 fireImmune 分支以 -4/tick 抹掉。查岩浆/火焰类伤害豁免时先看这条，别急着写事件去 cancel。

Alex's Caves 原始岩浆（`PrimalMagmaBlock`）真正坑 fireImmune 仆从的不是伤害而是 `getCollisionShape` 的 `SINK_SHAPE`（非 `LuxtructosaurusEntity` 一律陷进方块 2px），陷进去后 `entityInside` 每 tick 跑 `multiply(0.9,0.1,0.9)` 拖拽 + 未激活时 `add(0,0.1,0)` 弹跳。修碰撞形状等于把这两个副作用一起消掉——站到顶面后 `entityInside` 根本不触发。

**不用 mixin 也能改 AC 原始岩浆**（`Forge` 侧纯方块状态）：`PrimalMagmaBlock.tick` 只在 `updateShape` 被邻居变动的 `scheduleTick(pos,this,2)` 唤到，而 `setBlockAndUpdate(pos, ACTIVE=true)` 只通知**邻居**去 scheduleTick，**pos 自己不会被重新 tick** → 亲手激活的方块会稳定保持激活，不会被 AC 的 `isBossActive` 判定改回去。反过来把仆从 AABB 覆盖的方块钉在 `ACTIVE=false`，`getCollisionShape` 就走 `super`（整块）而非 `SINK_SHAPE`，仆从站上顶面后 `entityInside` 根本不触发，拖拽/弹跳/伤害一并消失。公开静态 `PrimalMagmaBlock.isBossActive(level)` 可直接拿来当"真首领在打就收手"的开关。`FissurePrimalMagmaBlock` 是直接 `extends Block`、无条件陷落，没有对应状态位，纯 API 豁免不了。

相关：[[goety-ac-dinosaur-spirit-port]]、[[forge-entity-sized-vs-getdimensions]]、[[goety-ac-no-mixin]]
