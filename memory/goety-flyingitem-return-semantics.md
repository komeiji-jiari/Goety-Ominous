---
name: goety-flyingitem-return-semantics
description: Goety FlyingItem 死亡返还物品的行为细节:owner 为空会永远上飘丢失、setItem 强制数量 1、64 格外瞬移
metadata: 
  node_type: memory
  type: reference
  originSessionId: 620f35fe-071e-4c00-8478-a36f47716524
  modified: 2026-09-27T00:07:44.828Z
---

"死亡返还物品"(仆从死了把蛋/核弹飞回主人)用 Goety 的 `com.Polarice3.Goety.common.entities.projectiles.FlyingItem` + `ModEntityType.FLYING_ITEM`(仓库先例 `CloudGolemServant.die`、`HullbreakerServant`;AC 的 `FlyingItem` 是另一个类)。`new FlyingItem(type, level, x, y, z)` → `setOwner(owner)` → `setItem(stack)` → `addFreshEntity`。

行为细节:

- 前 80 tick 只上升(`deltaMovement = 0,0.05,0`),100 tick 后才回家;主人距离 ≥64 格会逐段 `teleportTowards`。
- 主人是 Player → `player.getInventory().add(item)`,背包满则掉在脚下;主人是普通 LivingEntity → 原地生成 `ItemEntity`;主人跨维度 → 直接塞进玩家背包。
- **`setItem` 会 `copyWithCount(1)`** —— 数量永远变 1(NBT 保留);想返多件得自己另想办法。
- **`getOwner() == null` 时不会交付**,一直上飘到消失(65 秒后?总之会丢) —— 无主仆从必须 fallback 回 `spawnAtLocation`。
- `setParticle(...)` 可选,默认 `PORTAL` 粒子;`setSecondsCool(n)` 是给"返还后重新上冷却"用的(`SEHelper.addSpecificCooldown`)。

相关:[[goety-persistent-item-cooldown]]、[[lm-servant-boss-mechanic-stripping]]
