---
name: frowzy-servant-deliberate-divergences
description: Frowzy 仆从有两处有意不照搬 OF：近战距离不喂导航、删掉额外的 NearestAttackableTargetGoal
metadata: 
  node_type: memory
  type: project
  originSessionId: 9dced9ba-62ae-4dba-8491-210f006590d7
  modified: 2026-10-18T12:12:26.742Z
---

`FrowzyServant` 有两处**有意偏离 OF 原版**（2026-10-18 用户拍板"改"），以后核对还原度时别改回去：

1. `FrowzyServantAttackGoal.tick()` 只在 `distance > getAttackReachSqr(target)` 时才 `navigation.moveTo(target, 1.0D)`。OF 原版是每 tick 无条件喂导航，会触发 [[mc-moveto-every-tick-yaw-whipsaw]] 的贴身甩头。
2. `registerGoals()` 不再追加 priority 2 的 `NearestAttackableTargetGoal`（Rambler/Dicer/Terror 等其他 OF 仆从仍有）。索敌完全交给 `Summoned` 基类 priority 1 的 `SummonTargetGoal`，从而遵守 Goety 的规则（`ServantsAttackCreepers`、巫术/死灵袍中立等）。

**Why**: 这两点是功能性修正，不是漏移植；盲目"补齐 OF 写法"会重新引入抖动或绕过 Goety 配置。
**How to apply**: 移植其它 OF 仆从时可以考虑同样改法，但先问用户；Frowzy 已经改过，不要回退。见 [[of-servant-port-checklist]]、[[ac-servant-parity-keep-original]]。
