---
name: ac-ao-ally-check-direction
description: "AC 的 hurtEntitiesAround 用 living.isAlliedTo(this) 方向反了,主人会吃自己坐骑的 AoE;移植须翻成 this.isAlliedTo(living)"
metadata: 
  node_type: memory
  type: project
  originSessionId: fe0c95b5-be83-44a1-b7c7-e4146e8cafd5
  modified: 2026-09-11T10:48:34.697Z
---

AC 的 `SauropodBaseEntity.hurtEntitiesAround`(Atlatitan 的踢/甩尾、以及其它 AC 巨兽的 AoE 伤害)过滤友军用 **`living.isAlliedTo(this)`** —— 拿**被击者**去判。这个方向对"主人"失效。

**Why:** 1.20.1 里 `Player` 和 `LivingEntity` 都**不覆写** `isAlliedTo`(javap 验过),只有 `Entity` 覆写,且是纯记分板队伍判定:`getTeam().isAlliedTo(entity.getTeam())`,没队伍直接返回 false。主人和仆从通常不在同一 team → 主人被自己的擎天龙尾巴扫/踢会掉血,骑着时乘客位就在 12 格甩尾半径内,一样中招。

同主人的**其他仆从**反而安全(反向调用会走到 Goety `Owned.isAlliedTo` → `MobUtil.ownerStack`),主人的原版宠物也安全(`OwnableEntity.getOwner() == trueOwner`)。所以只测"仆从之间互不误伤"是测不出这个洞的 —— 漏的恰好只有主人本人。

**How to apply:** 移植任何 AC 的 `hurtEntitiesAround`(或类似遍历 `getEntitiesOfClass` 做伤害的循环)时,把 `living.isAlliedTo(this)` 翻成 **`this.isAlliedTo(living)`**。仓库里已有两处修正可作样板:`TremorzillaServant:998` 就是 `this.isAlliedTo(living)`(逐字一致);`TremorsaurusServant.isAlly()` 是双向版 `this.isAlliedTo(e) || e.isAlliedTo(this) || MobUtil.areAllies(this, e)`。想更保险直接用 `MobUtil.areAllies(living, this)`(仓库里 20 处的主流写法,双向 + SEHelper)。

注意 AC 同一行还带 `living.getType() == this.getType()`(同种不互伤),这是 AC 原设计,保留它会导致**打不到敌对玩家的同种仆从** —— 要开 PvP 得单独去掉。

关联:[[ac-goety-port-probability-gates]](同类"逐行对照反编译源码"的移植坑)、[[ac-compat-porting-notes]]、[[goety-servant-goal-priority-flags]]
