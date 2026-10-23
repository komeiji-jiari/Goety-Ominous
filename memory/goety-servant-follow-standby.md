---
name: goety-servant-follow-standby
description: "Goety 仆从 isFollowing/待命判定与 FollowOwnerGoal 瞬移机制,重写跟随目标需覆写 Summoned.followGoal()"
metadata: 
  node_type: memory
  type: reference
  originSessionId: ac3e8b11-2074-43cd-b8e0-15931f96296b
  modified: 2026-09-01T14:55:34.291Z
---
Goety `Summoned` 仆从模式机制(反编译 javap 确认,1.20.1):
- `isFollowing()` = `!isWandering() && !isStaying() && !isGuardingArea() && canFollow()`(`canFollow()` 默认 true)。模式由 CommandHorn/WaystoneItem 等切换。
- 仆从模式语义(突变潜影贝):**"待命" = `getTarget() == null && isStaying()`**,即 CommandHorn 的 Stay/驻守模式且无目标。跟随(Following)/游荡(Wandering)/守护(Guarding)/警戒(有目标)都算正常活动状态,应出壳;只有待命(isStaying)缩壳。注意"待命"≠`!isFollowing()`(那会把游荡/守护也误判为待命)。
- `Summoned.registerGoals()`(`m_8099_`)通过虚方法 `followGoal()` 注册 `Summoned$FollowOwnerGoal`(优先级 5,flags MOVE+LOOK,参数 speed=1.0/start=10F/stop=2F)。**要替换跟随目标,直接覆写 `followGoal()` 即可**——它在 registerGoals 里 invokevirtual 分发。
- 原版 `Summoned$FollowOwnerGoal` 在主人 >16(玩家)/32(Mob)格或寻路失败时 `tryToTeleportNearEntity()` 瞬移补位;canUse 还要求 `!isCommanded() && getTarget()==null && distance>=10`。
- SRG 映射坑:`m_21523_`=`isLeashed()`,`m_20159_`=`isPassenger()`,`m_275832_`=`getControlledVehicle()`,`Mob.isFlying()` 在 1.20.1 不存在(那是实体自定义方法)。
- **索敌目标**:`Summoned.targetSelectGoal()` 只注册一个 `SummonTargetGoal(Mob)` = `NearestAttackableTargetGoal(Mob, LivingEntity, 5, mustSee=true, mustReach=false, predicate)`。**mustSee=true = 要视线** → 埋地/隐身仆从索敌失明。要穿墙穿地索敌(钻地伏击),子类化 `SummonTargetGoal`(public,非 final),构造传 `(mob, false, false)`(mustSee=false=ignoreLineOfSight),再覆写 `protected AABB getTargetSearchArea(double)` 为 `mob.getBoundingBox().inflate(d, d, d)`(立方体,AC `MobTarget3DGoal` 同款);在覆写的 `targetSelectGoal()` 里 `super` 后追加 `targetSelector.addGoal(1, ...)`。`IServant.GUARDING_RANGE` 是接口上的 `int`(clinit 从 `MobsConfig.ServantGuardingRange` 读),`isGuardingArea()/getBoundPos()` 也在 IServant。
- 相关:[[mutant-hoglin-anim-fix]] [[ac-compat-porting-notes]]