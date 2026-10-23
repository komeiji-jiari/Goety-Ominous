---
name: forge-meleeattackgoal-reach
description: 1.20.1 MeleeAttackGoal 射程内联不可覆写;Mob 上没有 getAttackReachSqr
metadata: 
  node_type: memory
  type: reference
  originSessionId: 8fbb760e-9c73-47e2-95bd-60b52fe5f7d2
  modified: 2026-08-19T13:54:09.762Z
---

Forge 1.20.1 的 `net.minecraft.world.entity.ai.goal.MeleeAttackGoal` 把攻击射程内联在 tick/checkAndPerformAttack 里(经官方映射 `rename/output.jar` javap 验证:只有 ctor/canUse/canContinueToUse/start/stop/requiresUpdateEveryTick/tick/checkAndPerformAttack),没有可覆写的射程方法;`Mob` 上也没有 `getAttackReachSqr`(那是新版 API,覆写会报"方法不会覆盖超类型方法")。要自定义攻击射程/时机/动画绑定,直接 `extends Goal` 自写近战 goal(参考原版 alexscaves `GrottoceratopsMeleeGoal` 的移植,setFlags MOVE+LOOK,canUse=target 存活)。关联 [[forge-maxupstep-mapping]]。
