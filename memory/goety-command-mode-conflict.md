---
name: goety-command-mode-conflict
description: Goety command focus/horn 的命令移动由 IServant.servantTick→commandMode 每 tick 直接在 goal 体系外 navigation.moveTo(commandPos);自定义 Summoned 的战斗/传送 goal 不 gate isCommanded 会与其互抢导航导致动作中途打断
metadata: 
  node_type: memory
  type: project
  originSessionId: 6eae68de-0c72-4d6b-82d7-7c3eccffa2d8
  modified: 2026-09-10T14:37:30.839Z
---

Goety 的 CommandFocus / CommandHorn 对仆从下命令 = `setCommandPos(BlockPos[,true])` 或 `setCommandPosEntityOrder(LivingEntity)` → `commandPos != null` → `isCommanded()` 为 true。

命令期间移动不走 goal 体系:`IServant.servantTick()` 的默认 `commandMode()`(每服务端 tick 在 `Summoned.tick→super→Owned.tick` 之后执行)无条件 `getNavigation().moveTo(commandPos+0.5, 1.25)`(或 moveTo commandPosEntity),直到 到达(bbox 交叠)/导航 stuck/commandTick 10s 归零才清 commandPos。它每 tick 最后跑 → **覆盖任何 goal 当 tick 设的导航**。

Goety 自带目标停让:Summoned.FollowOwnerGoal.canUse 含 `isCommanded()→false`;Summoned.WanderGoal 命令时也停。所以命令期间移动唯一来源=commandMode,自带的战斗/移动 goal 必须自己让位(参考 SlimeServant 同构)。

自定义 Summoned(如 LicowitchServant)若自带用 navigation/moveControl 的 attack goal + 随机传送 goal 却不 gate `isCommanded()`,命令期间每 tick 与 commandMode 互抢:追逐/strafe/SPELL 动画自设 deltaMovement/战斗传送 `setTeleportingToPos` 把怪从命令路线上拉走 →「索敌时用 command focus,战斗 AI 和命令移动 AI 中途互相打断」。

修复样板(LicowitchServant 2026-09):attack goal 的 canUse(含 canContinueToUse 走 canUse)加 `&& !isCommanded()`,RandomlyTeleportGoal 同;LicowitchUseCrucibleGoal 本就停。编译过。

**Why:** commandMode 在 goal 优先级之外,goal 没法靠优先级赢它;只能让有 MOVE/LOOK 副作用的自定义 goal 在命令期停掉。

**How to apply:** 把 AC 敌对怪改成 Goety Summoned 时,凡移植了会自己走位/传送的战斗 goal,记得补 isCommanded 守卫(和 isStaying 一样是必查位)。[[goety-servant-follow-standby]] [[goety-summon-idle-freeze]]

**水生仆从的两个额外必查点(2026-09,Leapkelp 审计):** ①`IServant.isFollowing()` = `!isWandering && !isStaying && !isGuardingArea && canFollow`,所以"只挡 isFollowing"的水生 goal(如照抄海豚式 SwimUpGoal)在**待命/驻守/游荡时恒为真**,会把仆从从岗位上拽走 —— 必须额外挡 `isCommanded()||isStaying()||isGuardingArea()`。②自定义 goal 若直接 `getNavigation().moveTo(...)` 却忘了 `setFlags(EnumSet.of(Flag.MOVE))`,就会不带 Flag 与所有 MOVE goal 并发抢导航。上游对齐样板:`Summoned$GoToWaterGoal` 有 Flag.MOVE 无 staying 门控(它是"主人在水里"触发的生存反射,故意压过 stay);`Summoned$WaterWanderGoal.canUse` 三个门控齐全(它是空闲游荡,必须让位)。判断标准:**生存反射可以压 stay,空闲行为不行**。
