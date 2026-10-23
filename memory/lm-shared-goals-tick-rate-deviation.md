---
name: lm-shared-goals-tick-rate-deviation
description: repo 共享 IStateGoal 漏了 requiresUpdateEveryTick→true(与 LM 不符);IAttackGoal 只差一个射程运算符
metadata: 
  node_type: memory
  type: project
  originSessionId: 4192c732-5f86-4d2b-987c-dd136bc9e155
  modified: 2026-09-29T21:53:30.354Z
---

`common\entities\ally\lm\goals\IStateGoal.java` **没有**覆写 `requiresUpdateEveryTick()`，
于是按原版 `GoalSelector` 规则只在偶数 tick 被 tick。LM 的
`net.miauczel.legendary_monsters.entity.ai.goal.IStateGoal` 返回 `true`（每 tick 都 tick）。

同一目录的 `IAttackGoal.java` **不是**这个问题：LM 的
`entity.ai.goal.IAttackGoal.requiresUpdateEveryTick()` 本身也返回 `false`，
repo 不覆写 → 隐式 false，一致。它真正的偏差是 `IAttackGoal:49` 的射程判定用
`distanceTo(target) <= attackrange`，LM 用严格 `<`。（`IAttackGoalMin` 同 LM 也是 false。）

**Why:** 这是移植早期就带进来的偏差，不是骑士那批引入的。2026-09-29 移植 Beheaded /
Resurrected Knight 时逐行对照 LM 反编译才发现；当时决定**不动它** —— 修了会同时改变
已有的 LM 仆从（Paladin / Obliterator / Pursuer / Colossus / ShulkerMimic /
Flameborn 系）的攻击节奏，属于没测过的行为变更，超出那次移植的范围。

**How to apply:** 新写的 goal 别复用这两个共享类，照 LM 逐字写自己的。
注意 LM 自己也**不是**全部 `true` —— 12 个骑士 goal 实测是「state 系 true / attack 系 false」
（`BHSynergyStateGoal`·`GhostUppercutGoal`·`DoubleStabGoal`·`BHSynergyGoal`·
`RSAlertedStateGoal`·`RSAttackGoal`·`RSStabGoal`·`RSStateGoal`·`RSSynergyGoal`·
`RSSynergyStateGoal`·`RSDoubleStabGoal` 逐个反编译确认）。repo 的
`goals\knight\KnightStateGoal`→true / `KnightAttackGoal`→false 已经逐条对上，
别"统一"成一律 true。真要修共享类，先单独起一次任务 + 全量回归。
相关：[[goety-servant-goal-priority-flags]]
