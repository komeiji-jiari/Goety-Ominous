---
name: goety-water-navigation-goals
description: Goety 两栖仆从的水生 goal 规则(WanderGoal 水下自灭、WaterWanderGoal 陆上才跑、该用 AirAndWaterRandomPos)+ vanilla GoalSelector flag 替换规则
metadata: 
  node_type: memory
  type: reference
  originSessionId: a0939e80-4053-4812-b20e-cbd7de54cbb8
  modified: 2026-09-10T10:30:43.477Z
---

## vanilla GoalSelector 的 flag 替换规则(1.20.1 mojmap,已反编译确认)

`WrappedGoal.canBeReplacedBy(other) = this.isInterruptable() && other.getPriority() < this.getPriority()`。
`GoalSelector.NO_GOAL = new WrappedGoal(Integer.MAX_VALUE, ...)` 且**只覆写了 `isRunning()`**,`isInterruptable()` 取 `Goal` 默认的 `true` → 未上锁的 flag 恒可占用。

**结论:共享同一个 `Goal.Flag` 的两个 goal 绝不可能同时运行,优先级数字小的必胜,与插入顺序无关**(`availableGoals` 是 `LinkedHashSet`,插入序,不是 TreeSet;靠 `lockedFlags` 这个 `Map<Flag, WrappedGoal>` 仲裁)。所以往 Goety 的 goal 表里插更优(数字更小)优先级的同 flag goal 去"覆盖"基类 goal 是安全且有效的写法。

## Goety 的 wander goal(源码依据 `Summoned.java`)

- `Summoned.WanderGoal.canUse()` 里 `!(getNavigation() instanceof WaterBoundPathNavigation)` 是**水下禁用的唯一来源**。装了 `ModWaterPathNavigation` 的仆从水里永不游荡。
- `Summoned.WaterWanderGoal` **`canUse()` 完全没有水域要求** —— 它在陆地上能跑,在水里反而跑不了:它的 `getPosition()` 走 `RandomStrollGoal` → `LandRandomPos`,要求目标下方是实心方块,开阔水域里恒返回 null → `canUse()` false。
- `WaterWanderGoal<T extends PathfinderMob>` 是**非静态内部类**;实际构造器是 `(T entity, double speed)` → 内部 interval=120、`checkNoActionTime=false`(所以不受"仆从空闲冻结"影响)。写法:`new Summoned.WaterWanderGoal<MyMob>(this, 1.0D)`,**不能**写成 `new Summoned.WaterWanderGoal<>((Summoned)this, this)`(类型参数就是实体自身类型,强转会编译失败)。
- `Summoned.FollowOwnerWaterGoal` 构造器里缓存了 `this.navigation = summonedEntity.getNavigation()`,切换导航后它 `stop()` 的是旧实例(它的 `moveTo` 走 `summonedEntity.getNavigation()`,所以不算致命)。
- `Summoned.registerGoals()` 会调 `this.followGoal()`;覆写 `followGoal()` 即可整体替换跟随目标。`registerGoals()` 由 `Mob` 构造器链触发,子类构造器里改 `moveControl`/`navigation` 是安全的。

## 1.20.1 没有 `WaterRandomPos`

mojmap 里只有 `AirAndWaterRandomPos` / `DefaultRandomPos` / `HoverRandomPos` / `LandRandomPos` / `RandomPos`。
水/空随机位置用 `AirAndWaterRandomPos.getPos(PathfinderMob, int xz, int y, int yRange, double x, double z, double maxAngle)`,其中 **(x,z) 是方向向量不是坐标**(Goety 的 `HoverWanderGoal` 传的是 view vector)。

**How to apply:** 给仆从加两栖 wander —— 在基类 wander 优先级**之上**(数字更小)注册一个 `Summoned.WaterWanderGoal` 匿名子类,`canUse()` 里 `super.canUse() && !isNovelty`,`getPosition()` 里 `isInWaterOrBubble()` 时返 `AirAndWaterRandomPos.getPos(...)`,否则 `super.getPosition()`。一个 goal 管两个域,避开优先级互压。

样板:`Leapkelp`(本仓库 `common/entities/ally/mobs/Leapkelp.java`,非联动仆从)、`AbstractSunkenNecromancer`。

相关:[[goety-servant-goal-priority-flags]] [[goety-summon-idle-freeze]] [[goety-command-mode-conflict]] [[forge-goety-flyer-isStableDestination]] [[forge-compile-setup]]
