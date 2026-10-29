---
name: goety-areallies-servant-vs-pet
description: "MobUtil.areAllies(仆从, 原版驯服宠物) 恒 false（双向 isAlliedTo），保护宠物要按双方主人比对"
metadata: 
  node_type: memory
  type: reference
  originSessionId: 3e230f8d-fae9-4e0a-b5b4-b949e8273a08
  modified: 2026-10-29T12:50:58.872Z
---

Goety 2.5.59.0(9153454) 与 2.5.60.0(9349375) 反编译实测：`MobUtil.areAllies(<Goety 仆从>, <原版驯服宠物>)` **两个版本都返回 false**，不是升级回归。

原因链：
- `areAllies` 要求双向 `isAlliedTo` 都为 true。
- 仆从→宠物 true：`Owned.isAlliedTo` 有 `entityIn instanceof OwnableEntity && getOwner() == trueOwner`（`.scratch/goety312_owned/.../Owned.java:232`）。
- 宠物→仆从 false：`TamableAnimal.isAlliedTo` 委托给主人，`Entity.isAlliedTo` 只认计分板队伍，玩家无队伍。
- 2.5.60.0 新增的尾部 `OwnableEntity.getOwner() == 对方` / `IOwned.getMasterOwner() == 对方` 比的是"对方本人"，没有"双方主人相同"这一条。

**How to apply:** 需要保护"主人 + 原版宠物 + 友方仆从"时，别直接 `MobUtil.areAllies(仆从, 实体)`；走 `ServantAllyUtil.areAllied`（已补共享主人 + `OwnableEntity` 兜底）或改用 `MobUtil.areAllies(主人, 实体)`（主人非空时宠物侧走 `OwnableEntity` 分支成立）。反向的 `areAllies(主人, 实体)` 是好的。

顺带事实：`MiscUtils.canHarmBasedOnTeamAndTag` 兜底是 `ownerPredicate.test(owner)`，只查 tag/队伍，**不含任何 owner/ally 概念**；MM 传送过来的敌对实体技能若只靠它，就会打主人和宠物（冰刺 `spellOwned=false` 路径就是）。相关：[[mm-servant-aoe-ally-guards]]、[[ac-ao-ally-check-direction]]。
