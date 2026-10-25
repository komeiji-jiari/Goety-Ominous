---
name: goety2-servant-system
description: Goety 2.5.58.2 仆从基座 Summoned/Owned/IServant 的继承、stance、命令、goal 装配与优先级——写新仆从照着这个来
metadata: 
  node_type: memory
  type: reference
  originSessionId: aa83982d-2283-4121-a0ca-337b4239c3a9
---

源码见 [[goety2-src-decompile]]（`E:\java\.disasm\goety_src\com\Polarice3\Goety\`）。版本 **Goety 2.5.58.2 / MC 1.20.1**。

## 1. 继承链

```
PathfinderMob
 └ Owned          common\entities\neutral\Owned.java   implements IOwned, OwnableEntity, ICustomAttributes
    └ Summoned    common\entities\ally\Summoned.java   implements IServant
       └ AnimalSummon   （可繁殖/成长；BearServant, BlackWolf, TwilightGoat, HoglinServant, Hellhound…）
    └ Minion      common\entities\neutral\Minion.java  ← 飞行 vex 系，**不是 IServant**，没有 stance/命令系统
```

- `IOwned` → `IServant extends IOwned, IChunkLoader`，**默认方法极多**，接口本身只强制 `isWandering/setWandering/isStaying/setStaying/canUpdateMove/isCommanded` 等少量。
- 构造函数统一是 `public Foo(EntityType<? extends Owned> type, Level worldIn) { super(type, worldIn); }`。

## 2. 最关键的：`registerGoals()` 已经把该装的都装了

```java
// Summoned
protected void registerGoals() {
   super.registerGoals();      // -> Owned.registerGoals() -> ownerHurtGoals()
   this.targetRetaliateGoal();
   this.patrolGoal();
   this.followGoal();
   this.targetSelectGoal();
}
public void patrolGoal()         { goalSelector.addGoal(2, new ServantPatrolGoal<>(this, this.getCommandSpeed())); }
public void followGoal()         { goalSelector.addGoal(5, new Summoned.FollowOwnerGoal(this, this.getFollowSpeed(), 10.0F, 2.0F)); }
public void targetRetaliateGoal(){ targetSelector.addGoal(1, new ServantHurtByTargetGoal(this)); }
public void targetSelectGoal()   { targetSelector.addGoal(1, new SummonTargetGoal(this)); }

// Owned
public void ownerHurtGoals() {
   targetSelector.addGoal(1, new Owned.OwnerHurtByTargetGoal(this));
   targetSelector.addGoal(2, new Owned.OwnerHurtTargetGoal(this));
}
```

**基座自动带的 goal：**

| selector | 优先级 | goal |
|---|---|---|
| goalSelector | 2 | `ServantPatrolGoal<>(this, getCommandSpeed())` |
| goalSelector | 5 | `Summoned.FollowOwnerGoal(this, getFollowSpeed(), 10.0F, 2.0F)` |
| targetSelector | 1 | `Owned.OwnerHurtByTargetGoal` / `ServantHurtByTargetGoal` / `SummonTargetGoal` |
| targetSelector | 2 | `Owned.OwnerHurtTargetGoal`（+ `isHostile()` 时 `NearestAttackableTargetGoal<Player>`） |

**子类只加自己的东西**：`FloatGoal`(0)、攻击 goal(1–4)、`Summoned.WanderGoal<T>`(8)、`LookAtPlayerGoal`(9–10)、`RandomLookAroundGoal`(10)。

**绝不要重复添加 follow/patrol goal** —— 同优先级下先注册的赢，后加的永远不跑。要改优先级/速度/类型就**覆写** `followGoal()` / `patrolGoal()`（`RaiderServant` 用 prio 6，`AbstractSpiderServant`/`Prisoner` 同理）。

⚠️ `AbstractSpiderServant.registerGoals()` **故意不调 `super.registerGoals()`**，手写 Owned 两个 owner goal + patrol/follow/targetSelect，但**漏了 `targetRetaliateGoal()`**，改用原版 `HurtByTargetGoal`。抄这个模式必须自己补回 `targetRetaliateGoal()`，否则仆从会打友军。

## 3. stance（站姿）系统

同步标志位：`SUMMONED_FLAGS` bit1=wandering、bit2=staying；`UPGRADE_FLAGS` bit4=upgraded。

`IServant` 的四个 stance 谓词 / 切换 / 能力门：
- `isWandering()/setWandering()`、`isStaying()/setStaying()`、`isGuardingArea()/setGuarding()`、`isFollowing()/setFollowing()`
- `canWander()/canStay()/canGuardArea()/canFollow()`（默认全 `true`）—— 覆写成 `false` 可禁用某个 stance；`canPatrol()` 默认 = `canGuardArea()`
- `copyStance(IServant)` 由魔杖 UI 调用

⚠️ `Summoned.isStaying()` 不只是那个 flag：
`getFlag(2) && !isCommanded() && getControllingPassenger() == null`
—— 有待执行命令点或被骑乘时 `isStaying()` 返回 false，会重新放开 `ServantWanderGoal`。

## 4. 命令 / 站位 / 巡逻

- 命令：`commandPos` / `commandPosEntity`（public 字段）、`setCommandPos(BlockPos[,boolean])`、`getCommandTick()`。`isCommanded()` 就是 `commandPos != null`。
- 命令模式由 `IServant.commandMode()` 每 tick 驱动（从 `Summoned.tick()` → `servantTick()` 自动调用，**不要自己重复调**）。
- 巡逻：`patrolList`(List<GlobalPos>) / `patrolIndex` / `getCurrentWaypoint()` / `advancePatrol()` / `clearPatrol()`。`advancePatrol()` 会顺带 `setBoundPos(...)`。
- 守卫：`boundPos` / `boundDim` / `getBoundPos()` / `vec3BoundPos()` / `isWithinGuard(BlockPos)`。`IServant.GUARDING_RANGE` 是接口静态常量，来自 `MobsConfig.ServantGuardingRange`（默认 16）。
- ⚠️ `Summoned.setTarget(LivingEntity)` 在 `isGuardingArea() && !isPrioritizing()` 时会**拒掉守卫范围外的目标**；要绕过用 `overrideSetTarget(...)`。`setPriorityTarget(...)` 内部就调 `overrideSetTarget` 并置 `priorityTime=100`。
- 优先目标：`priorityTarget`（**私有字段**，`@Nullable`）/ `priorityTime` / `priorityPos` / `isPrioritizing()`，走 getter/setter。
- ⚠️ 守卫归位是**白送**的：`IServant.servantTick()` 在 `distanceToSqr(vec3BoundPos()) > GUARDING_RANGE²` 且未命令/未巡逻时会把仆从拉回 `boundPos`。所以全模组只有 `Prisoner` 额外用了 `Summoned.ReturnToGuardPos`。

## 5. 生命周期 / 上限 / 生存

- `Owned.tick()` → `ownedTick()`；`Summoned.tick()` → `servantTick()`：这些里已经做了寿命衰减、stance、命令、回守卫点、chunkload、太阳燃烧等，**别重复实现**。
- `checkDespawn()`：`Summoned` 覆写为 `if (isHostile()) super.checkDespawn()` —— **非 hostile 的仆从永不自然消失**。同理 `removeWhenFarAway()` / `shouldDespawnInPeaceful()` 都返回 `isHostile()`。
- 寿命：`setLimitedLife(ticks)` / `setHasLifespan`；`IOwned.ownedTick()` 服务端递减，归 1 时调 `lifeSpanDamage()`（默认饿 1 血并重置 20 tick）。**要自定义消失表现就覆写 `lifeSpanDamage()`**。
- 召唤上限（**不在 goal 系统里**）：
  - `IOwned` 默认：`summonPredicate() = e -> mob.getClass().isAssignableFrom(e.getClass())`、`getSummonLimit(owner) = 64` → 被 `RitualRequirements` 和 `SummonSpell` 消费。
  - `SummonSpell` 自己还有独立的 `summonLimit()`(64) / `summonPredicate()`，**法术侧以法术的为准**。见 [[goety2-spell-system]]。
- `Owned.remove(reason)`：仆从死亡/丢弃时，把它自己名下（`getTrueOwner() == this`）的子召唤物**转继承**给自己的主人，没有主人则清空。所以"召唤物有自己的召唤物"不会断链。
- 升级：`isUpgraded()/setUpgraded()/upgrade()/downgrade()`；`IServant.spawnUpgraded()` 会按主人身上的 curio 法袍自动升级（`Summoned.finalizeSpawn()` 已调用）。
- `Owned.finalizeSpawn()` 负责 natural / persistence：`natural = true` 仅在 `spawnType != MOB_SUMMONED && getTrueOwner() == null` 时成立。
- ⚠️ 覆写 `finalizeSpawn(...)` 一定要先调 `super.finalizeSpawn(...)`。
- `checkHostility()` 是 `IOwned` 默认方法，`Owned` 构造器里就调；owner 是 `Enemy` 或本身 hostile 的 `IOwned` 时本仆从也 hostile。

## 6. goal 速查（本版本实际存在的）

顶层 `common\entities\ai\`：`MinionFollowGoal`、`ServantHurtByTargetGoal`、`SummonTargetGoal`、`TargetHostileOwnedGoal`、`GoToTargetGoal`、`AvoidTargetGoal`(+嵌套 `AvoidRadiusGoal`)、`ModMeleeAttackGoal`、`ModAvoidGoal`、`FollowMobClassGoal`、`HuntDownPlayerGoal`、`NeutralZombieAttackGoal`、`ShieldedMeleeGoal`、`SurroundGoal`、`CreatureBowAttackGoal`、`CreatureCrossbowAttackGoal`、`BackawayCrossbowGoal`、`FloatAroundGoal`、`FloatSwimGoal`、`LookAtTargetGoal`、`StrollAroundLeaderGoal` …

`common\entities\ai\servant\`：`ServantFollowOwnerGoal`、`ServantFollowOwnerWaterGoal`、`ServantGoToWaterGoal`、`ServantWaterWanderGoal`、`ServantPatrolGoal`、`ServantWanderGoal`、`ServantNaturalAttackGoal`、`ServantReturnToGuardPos`。

要点：
- `ServantFollowOwnerGoal<T extends Mob & IServant>(T, double speed, float startDist, float stopDist)`，flags = MOVE+LOOK，`canUse()` 门 = `isFollowing() && !isCommanded() && getTarget()==null && dist²>=startDist²`。传送默认**关**（`MobsConfig.ServantTeleport` 默认 false）；owner 是 Mob 时 >8 格无视线则必传（不受配置门控）。
- `ServantWanderGoal<T>(T, double speedModifier[, float probability[, int interval]])`，flags = MOVE，`canUse()` 额外要求 `(!isStaying() && !isCommanded()) || getTrueOwner()==null`，且导航**不能**是 `WaterBoundPathNavigation`。
- `ServantPatrolGoal<T>(T, double speed)`，**只有 MOVE**，没有 `canContinueToUse()`（每 tick 重算）。由 `Summoned.patrolGoal()` 注册，别手动加。
- `ServantNaturalAttackGoal<T, M extends PathfinderMob & IServant>` —— ⚠️ `canUse()` 要求 `isNatural() && getTrueOwner()==null`，**只对野生未召唤的仆从生效**；玩家召唤的仆从永远不会跑它。真正的索敌是基座已注册的 `SummonTargetGoal`。
- `SummonTargetGoal(Mob[, ...])`，静态工厂 `SummonTargetGoal.predicate(LivingEntity attacker)` = `MobUtil.isOwnedTargetable`。⚠️ hostile 仆从的 `canUse()` 被反转成"只打非 `Enemy`"。
- `MinionFollowGoal(Minion, double speed, float minDist, float maxDist, boolean teleportToLeaves)` —— 专给 `Minion`（vex 系），构造器要求导航是 `GroundPathNavigation` 或 `FlyingPathNavigation`，否则抛 `IllegalArgumentException`。
- `ModMeleeAttackGoal(PathfinderMob, double speed, boolean followingTargetEvenIfNotSeen)` —— 原版 `MeleeAttackGoal` 的翻版，多了 `IRavager` 载具够到距离判定；钩子 `checkAndPerformAttack` / `getAttackInterval` / `getAttackReachSqr` 是 `protected`。`BlackBeast` 用 `(this, 1.2D, false)` @ prio 2。
- ⚠️ `GoToTargetGoal` / `ModAvoidGoal` / `ServantWaterWanderGoal` / `ReturnToGuardPos` 在本版本**几乎或完全没有被注册**（死代码）。`GoToTargetGoal` 一次占满 MOVE+JUMP+LOOK+TARGET 四个 flag，且在 `canUse()` 里调 `navigation.moveTo` —— 千万别抄。
- ⚠️ `Summoned.WaterWanderGoal` 是**非 static 内部类**，外部只能写成 `summonedInstance.new WaterWanderGoal<>(...)`，基本没法用。

## 7. 骑乘 / 控制 flag

`Summoned.updateControlFlags()`（`Mob.serverAiStep()` 每 tick 调）：

```java
boolean flag  = !(this.getControllingPassenger() instanceof Mob);
boolean flag1 = !(this.getVehicle() instanceof Boat);
goalSelector.setControlFlag(Flag.MOVE,   flag);
goalSelector.setControlFlag(Flag.JUMP,   flag && flag1);
goalSelector.setControlFlag(Flag.LOOK,   flag);
goalSelector.setControlFlag(Flag.TARGET, flag);
```

⚠️ 只要 `getControllingPassenger()` 返回一个 `Mob`，**整个 goalSelector 里所有 MOVE/LOOK/TARGET goal 全部无法启动**。可骑乘仆从若返回 Mob 乘客必须自己处理（`BearServant.getControllingPassenger()` 在 `MobsConfig.ServantRideAutonomous` + `isAutonomous()==false` 时返回 null，就是为了保命）。

## 8. 最小可用仆从模板

```java
public class MyServant extends Summoned {          // 要繁殖/成长就 extends AnimalSummon

    public MyServant(EntityType<? extends Owned> type, Level worldIn) { super(type, worldIn); }

    @Override
    protected void registerGoals() {
        super.registerGoals();                                   // ★ 基座全套，别重复加
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(4, new ModMeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.addGoal(8, new Summoned.WanderGoal<MyServant>(this, 1.0D, 10.0F));
        this.goalSelector.addGoal(9,  new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
    }

    // 需要时的其余钩子：
    // public static AttributeSupplier.Builder setCustomAttributes()
    // @Override public void setConfigurableAttributes()   // Owned.readAdditionalSaveData 会调
    // @Override public MobType getMobType()
    // @Override public void setUpgraded(boolean)
    // @Override public void lifeSpanDamage()  // 寿命耗尽表现
    // @Override public double getFollowSpeed() / getCommandSpeed()   // IServant 默认 1.0D / 1.25D
}
```

`Summoned` 上可直接用的 public 状态：`commandPosEntity`、`commandPos`、`priorityPos`、`boundPos`、`boundDim`、`patrolList`、`patrolIndex`、`priorityTime`、`commandTick`、`killChance`、`noHealTime`、`ticketTime`。

相关：[[goety2-spell-system]]、[[goety2-src-decompile]]
