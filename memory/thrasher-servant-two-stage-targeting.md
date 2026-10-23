---
name: thrasher-servant-two-stage-targeting
description: "长尾鲨仆从的两段式索敌设计——8 格内直接咬,8 格外只能靠声呐;以及为什么声呐目标必须在水中"
metadata: 
  node_type: memory
  type: project
  originSessionId: 32403d76-827f-4982-aecf-dbf281e3ede4
  modified: 2026-10-04T16:12:16.092Z
---

2026-10-04 按用户要求把长尾鲨仆从改成两段式索敌:近(≤8 格)**直接咬**,远(8~32 格)**只能靠声呐发现**——远处发现敌人→播声纳动画→锁定→冲过去咬。

实现要点:
- `targetSelectGoal()` 覆写成装 `ThrasherServantCloseTargetGoal`(自定义 `NearestAttackableTargetGoal`,构造完 `super` 后把 protected 的 `targetConditions` 重设成 `TargetingConditions.forCombat().range(8.0D)`)替代 Goety 的 `SummonTargetGoal`。Goety 那个是 FOLLOW_RANGE(32) 全距 20%/tick 静默锁敌,留着会抢在声呐前面。
- `SummonTargetGoal` 被替掉不亏:`targetRetaliateGoal()` 是独立调用保留;**主要是有个 `isHostile()` 分支会废掉索敌,但 `Summoned extends Owned extends PathfinderMob` 并不 implements Enemy,玩家召唤时 isHostile() 恒 false**,那条分支永远不触发。
- `isValidTarget`(怪/鱼,无水域要求)= 近距离咬 + 0.5 格贴身扫描;`isSonarTarget` = `isValidTarget && isInWater`,只给声呐链(探测点搜索 + 光束锁定)用。
- `ThrasherServantFindDetectionPointGoal` 的搜索谓词要求距目标 > BITE_RANGE,否则近距离的敌人会去触发声呐而不是直接咬。

**Why:** 声呐链(5%/tick 找探测点 + 55 tick 冷却)原本抢不过 SummonTargetGoal,所以声纳动画几乎见不到。

**How to apply:** 声呐目标要 `isInWater()` 不是洁癖——长尾鲨用 `WaterBoundPathNavigation`(`SwimNodeEvaluator` 只认水节点),陆上目标根本走不过去,锁了只会在岸边干叫。近距离那 8 格没有这个要求。相关:[[goety-water-navigation-goals]]、[[goety-attackgoal-no-flags-yaw-fight]]。
