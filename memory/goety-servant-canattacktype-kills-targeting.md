---
name: goety-servant-canattacktype-kills-targeting
description: "仆从覆写 canAttackType 返回 false 会废掉全部索敌（含还手），排查\"仆从不打怪\"先看这里"
metadata: 
  node_type: memory
  type: project
  originSessionId: 6bc59e20-9141-4318-9ae1-34104b26313d
  modified: 2026-09-24T12:24:37.347Z
---

Goety 的仆从索敌全部走 `TargetingConditions.forCombat()`：`Summoned.registerGoals()` 注册的 `ServantHurtByTargetGoal`(1) 和 `SummonTargetGoal`(1)，加上自己加的 `HurtByTargetGoal`（用 `HURT_BY_TARGETING = TargetingConditions.forCombat()...`）。而 `TargetingConditions.test()` 里有 `!p_26886_.canAttackType(...)` 这一项。

**Why:** 所以 `canAttackType()` 返回 false 的仆从**既不会主动索敌，被打了也不会还手**——`Mob.setTarget` 本身不查这个，`doHurtTarget` 也不查，所以不是"打不疼"而是"根本不进战斗状态"，从现象上很容易误判成 AI goal 写错。

**How to apply:** 排查"仆从站着不动/不还手"先 grep `canAttackType`。移植时注意别把 `m_7341_`（canRide）当成 `m_21399_`（canAttackType）——`IAnimatedMonsterServant` 已经提供了 `canRide → false`，原版那条覆写是冗余的，不需要搬。SRG 反查用 `build/createMcpToSrg/output.tsrg`。

相关：[[goety-servant-follow-standby]]、[[goety-summon-idle-freeze]]、[[lm-miniboss-servant-spawn-teleport]]
