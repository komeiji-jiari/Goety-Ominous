---
name: lm-nextint-overflow-random-gate
description: "LM 里 `nextInt() * 100 < 50` 不是恒假也不是可控随机,而是「参数失效的 ~50% 抛硬币」;有符号比较,负数那一半恒 < 50"
metadata: 
  node_type: memory
  type: project
  originSessionId: 7d71d7b7-2482-4ea1-8b3d-6ad931cf75c0
  modified: 2026-09-26T11:13:48.099Z
---

**2026-09-26 更正**：此前这条记的是「溢出后 `< 50` 命中 ~1.2e-8，实际恒 false」——**那个结论是错的**，因为它把比较当成了无符号。Java 的 `int <` 是**有符号**比较，`nextInt()` 的负半区（占全部取值的一半）乘 100 之后仍然是负数，**负数恒 `< 50`**。

正确结论：`nextInt() * 100 < N`（N 为 50 这类小正数）的概率 ≈ **0.5**（精确值 `0.5 + 13/2^30`，2000 万次采样得 0.50003735）。推导：`x*100 mod 2^32` 均匀落在 4 的倍数上，负值全部命中（2^29 个），非负且 < 50 的只有 0,4,…,48 共 13 个。

所以正确的读法是：**这不是随机门，是一个「概率参数完全失效的 ~50% 抛硬币」**。凡是 `nextInt() * 100 < 某个百分比常量` 的地方，那个常量（10 / 15 / 25 / 35 / 50 都一样）根本不生效，实际全是约一半概率。

判据不变：CFR 不是锅，作者源码就这么写；`canUse()` 里 LM 写的是正确的 `nextFloat() * 100.0f < 32.0f`，只有 `stop()` / `start()` 里挑动画状态、挑朝向时才会出现 `nextInt() * 100` 这种写法。移植时**照抄即可**（parity 优先，见 [[ac-servant-parity-keep-original]]）；要修就是换成 `nextFloat() * 100.0F < N`。

**被这条更正推翻的旧结论**：此前记的「湮灭追猎者 `TeleportChaseGoal.stop()` 里 `nextInt()*100 < 50 && stab_finisher_cooldown <= 0 ? 19 : attackendstate` 恒走 else → state 19 永远进不去 → 整条抓取终结链是死代码」——**该推论不成立**，那个门大约一半概率为真，state 19 是可达的。要断言那条链是死代码必须另找证据（例如 `stab_finisher_cooldown` 是否恒 > 0），不能再用溢出当理由。

圣骑侧同样的形态（parity，非新 bug）：`PossessedPaladinAlertedGoal.canBeAlerted()` 的 `(double)(nextInt() * 100) < this.allertedStateChance`，传入的 10/15/25/35/50 全部塌缩成约 50%。

**它不是死代码**（2026-09-26 查实）：`canBeAlerted()` 在 5 个 goal 的 `stop()` 里被真调用 —— `PossessedPaladinStateGoal` / `AttackGoal` / `JumpSmashComboGoal` / `SideRollSpinGoal` / `FinisherAttackGoal`，形态是 `setAttackState(canBeAlerted() ? 9 : attackendstate)`。9 号状态 = `PossessedPaladinAlertedGoal`，以目标为心半径 8 的横向绕圈闪身。注册点里 `canAllertedState=true` 的有 17 个（传 false 的 8 个被 `&&` 短路丢弃，改不改都无影响）。

**2026-09-26 用户决定：不改，保持 parity。** 评估结论备查：改成 `nextInt(100) < chance` 后按注册点加权 ≈50%→29%（50.0×3 不变、35.0×4、25.0×7、15.0×1、10.0×2），观感是招式之间少掉约四成横向绕圈、更站桩；另外 `nextInt()` 与 `nextInt(100)` 都只推进一格 LCG，改完下游随机序列逐位相同，可干净 A/B。不改的理由：LM 原版传的是同一组分级数值（bug 在 LM 自己），且 chance 硬编码无配置项。**别再提议只改圣骑** —— 要改就得全仓 15+ 处一起，否则内部不一致。

相关：[[lm-mechanic-presence-version-check]]、[[goetyominous-deadcode-cleanup-preference]]
