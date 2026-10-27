---
name: goety-goodwill-ally-check-api
description: 亲善之书名单只有 SEHelper.isAlly 认；isAlliedTo 完全不认，MobUtil.areAllies 只在一方是 Player 时认
metadata: 
  node_type: memory
  type: reference
  originSessionId: 2b4149b5-5fc5-4caf-a945-7a6f1ce3fffe
  modified: 2026-10-25T22:39:13.061Z
---

Goety 亲善之书（`item.goety.grimoire_of_goodwill`）的名单存在 `SEHelper` 的 capability 里：单个实体 `getAllyEntities(Player)`、实体类型 `getAllyEntityTypes(Player)`，统一判断用 `SEHelper.isAlly(Player, LivingEntity)`（两类都查）。另有 `isAlly(LivingEntity owner, ...)` 重载，但只对 Player 生效，不会追 IOwned 的 true owner。

**Why:** 原版 `Entity.isAlliedTo` 只看队伍；`MobUtil.areAllies(a, b)` 只有在 a 或 b 是 **Player** 时才走 `SEHelper.isAlly`，两边都是生物（陷阱、仆从、弹射物）时完全不看亲善名单。所以写友伤过滤时误以为 `areAllies` 已经覆盖，实际没有。

**How to apply:** 玩家法术/弹射物/陷阱做友伤排除，用 `owner instanceof Player p && SEHelper.isAlly(p, target)`，再配合 `IOwned.getTrueOwner() == owner` 挡自家仆从。范例：`WaterBoltProjectile` 的 `canHitEntity`/`damageMobs`/`onRicochetHit`，`MutantShulkerServantTrap.isHostileTarget`。弹射物命中路径还有一处坑见 [[mm-shulker-bullet-trajectory]]。
