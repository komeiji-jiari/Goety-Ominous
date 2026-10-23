---
name: goety-amphibious-floatgoal-bounce
description: 两栖仆从贴着水面一直弹 = 继承了 FloatGoal；Goety 上游对 Wavewhisperer 也是摘掉它
metadata: 
  node_type: memory
  type: project
  originSessionId: cbb0bb01-e4b1-49d0-aaf8-d63c99c52faa
  modified: 2026-10-03T16:53:55.845Z
---

把 Goety 的两栖生物（Drowned 式 `waterNavigation`/`groundNavigation` 切换 + `wantsToSwim()` + `travel()` 水中分支）改成子类时，如果父类 `registerGoals()` 里有 `new FloatGoal(this)`，子类会在水里**贴着水面一直弹**而不是游泳：`FloatGoal.canUse()` 只判"在水里且超过跳跃阈值"，`tick()` 每 tick 调 `getJumpControl().jump()`，`LivingEntity.aiStep` 就每 tick 给一次向上冲量。

Goety 上游自己也踩过：`Whisperer` 里是匿名子类
`Whisperer$1 extends FloatGoal { canUse() { return super.canUse() && !(Whisperer.this instanceof Wavewhisperer); } }`
—— 即对两栖的那只直接禁用。

**Why:** 原版两栖 mob（Drowned）根本不加 `FloatGoal`，所以从 Leapleaf 这种陆生近战 mob 继承来的 FloatGoal 是纯粹的移植事故，不是设计。

**How to apply:** 覆写 `registerGoals()`，`super.registerGoals()` 之后
`this.goalSelector.getAvailableGoals().removeIf(w -> w.getGoal() instanceof FloatGoal);`
（`GoalSelector.getAvailableGoals()` 返回的是活的 `Set<WrappedGoal>`，`removeIf` 直接生效；`registerGoals()` 只在 Mob 构造器里跑一次，此时没有 goal 在运行，不必走 `removeGoal()` 的 stop 流程）。

注意区分：`LeapGoal` 之类的跳跃 goal 都要求 `isOnGround()`，游泳时不会触发，不是弹跳来源。

**第二个来源（2026-10-03 在潮涌之叶上实测踩到，比 FloatGoal 更隐蔽）：自研 `MoveControl` 的 `else` 分支落到原版 `MoveControl.tick()`。** 原版 MOVE_TO 分支里有 `d1 > maxUpStep() && 水平距离够近 → getJumpControl().jump()`，**完全没有 `onGround` 判据**；水中 `jump()` 就等于上浮，而且随后的 `Operation.JUMPING` 状态会把 `zza` 清零 → **原地悬着蹦、永远不前进**（观感就是"卡在水面一直跳"）。触发条件只是"有 MOVE_TO 目标且目标 y 在自己上方"——跟随站在岸上的主人、走向 `pos.above()` 的上岸目标都算。

所以**自研 MoveControl 的分支条件不能写成 `wantsToSwim() && isInWater()`**：只要 `wantsToSwim()` 意外为 false（见下）就漏进原版。潮涌之叶的修法两条：①分支条件改成 `isInWater()`，水里绝不落到原版；②`wantsToSwim()` 里补 `if (isInWater()) return true;` —— 原代码的 `|| this.isInWater()` 被嵌在 `owner != null && this.isFollowing() && (...)` 后面，于是**野生/待命/主人在岸上的个体在水里全判 false**，连带"用陆行导航 + 无浮力 + 落进原版 MoveControl"三重后果。教训：`wantsToSwim()` 这类"模式开关"只该决定用哪套 travel/导航，**不该决定水中是否按游泳处理**，后者一律用 `isInWater()`。

**推论（实测踩过）:** 正因为 FloatGoal 每 tick 弹，`getFluidHeight(WATER) > getFluidJumpThreshold()` 这条线会被反复穿越。拿它当"持续在水中"的判据（比如驱动动画/粒子）必然一断一连——间断处动画速度每 tick ×0.6 归零，看起来就是"一抽一抽"，停在阈值平衡点的个体则永远不触发。持续状态一律用 `isInWater()` + 几 tick 闩锁。
同类已记录：[[goety-water-navigation-goals]]、[[goety-servant-follow-standby]]
