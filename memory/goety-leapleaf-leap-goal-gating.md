---
name: goety-leapleaf-leap-goal-gating
description: "Goety Leapleaf 三个 AI goal 的水陆门控:AttackGoal(SMASH) 无 onGround 检查,LeapGoal 有 → 浅水会跳;LeapGoal 是包私有静态内部类,只能 mixin targets 拦 canUse"
metadata: 
  node_type: memory
  type: reference
  originSessionId: fbf5c252-d6e9-419a-bc0b-63ff4b170a9a
  modified: 2026-09-10T15:09:21.108Z
---

Goety `Leapleaf`(`libs/goety-2.5.56.0.jar`)的 goal 门控,反编译 `Leapleaf.class` 实测:

- `Leapleaf$AttackGoal`(非静态内部类,priority 0,**不设 Flag**)`canUse` = `target != null && target.isAlive() && !isCharging() && !isLeaping() && hasLineOfSight(target) && distanceTo(target) <= 2.5F` —— **没有 `onGround()` 检查**,所以 SMASH 本身在水里可用。`tick` 里 `attackTick==1` 放 `SMASH` 动画 + 音效,`==13` 结算伤害/粒子。
- `Leapleaf$LeapGoal`(静态内部类,priority 2,Flag.MOVE+JUMP)`canUse` 里有 `leapleaf.m_20096_()`(`onGround`)—— **浅水(站在水底)时 onGround 为真且 isInWater 为真,会照常蓄力起跳**,`leapTick==1` 给 `deltaMovement = dir*0.6, 0.75+…`,这就是"水里跳起来"的来源。深水 onGround 为假,不会触发。
- `Leapleaf$StrafeGoal`(静态,priority 1)`canUse` 要求 `coolTick > 0`,而 `coolTick` 只在 `LeapGoal` 落地时重置为 2s、在 `Leapleaf.tick` 里单向递减 → **没跳过的生物 2 秒后 coolTick 恒为 0,StrafeGoal 永久失效**(疑似上游笔误,写成 `<= 0` 才合理)。

priority 8 的匿名 `Leapleaf$1` **不是** MeleeAttackGoal(2026-09 更正),是 `new Summoned.WanderGoal<Leapleaf>(this, 1.0D){ canUse = super && !isNovelty }` —— 随机游荡。判据:`Leapleaf$1.class` 仅 1.2KB,常量池里是 `Summoned$WanderGoal`/`m_8036_`/`isNovelty`,构造符 `(Leapleaf,Leapleaf,D)V`,EnclosingMethod=`registerGoals`。**所以上游整条继承链里根本没有任何"朝目标移动"的 goal**(`Summoned.registerGoals` 只加 `ServantHurtByTargetGoal`/`FollowOwnerGoal`/`SummonTargetGoal`,`Owned.registerGoals` 只加两个 OwnerHurt* target goal)。接近敌人唯一的机制是 `LeapGoal.tick()` 里 `chargeTick>=1.21s && 距离>6.5F` 时的 `getNavigation().moveTo(target, 1.25D)`(注意该 tick 开头先 `navigation.stop()`,距离<=6.5F 直接起跳),而它被 `canUse` 的 `onGround()` 卡死 → **水里(悬泳)Leapleaf/Leapkelp 绝不会主动靠近敌人**,只有 `AttackGoal`@0(不设 flag、无 onGround 检查、`distanceTo<=2.5F && hasLineOfSight`)能打贴脸的。

`LeapGoal`/`StrafeGoal` 都是**包私有**静态嵌套类,外部包无法 `instanceof` 也无法继承;`AttackGoal` 还是非静态内部类。要改行为只能 mixin:

```java
@Mixin(targets = "com.Polarice3.Goety.common.entities.ally.Leapleaf$LeapGoal")
public abstract class X {
    @Shadow public Leapleaf leapleaf;
    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true, require = 1)
    ...
}
```

`canUse` 是覆写 MC `Goal.m_8036_`,**不要写 `remap = false`** —— 让 refmap 生成 `Leapleaf$LeapGoal;m_8036_()Z`(可用 `build/tmp/compileJava/goetyominous.mixins.refmap.json` 验证)。

上面这个 mixin 2026-09 做过一版(`LeapleafLeapGoalMixin`,拦 water 里不跳),**用户后来要求撤销,已删除并移出 mixins.json**;下面这套写法只作技术参考留档,别以为仓库里还有它。

**不要**用"覆写 `setCharging` 在 water 里 no-op"这种取巧法:那样 `LeapGoal.canUse` 仍为真、`canContinueToUse`(`isCharging()||isLeaping()`)立刻为假 → goal 每 tick start/stop 抖动,而 `start()` 里的 `setMeleeAttacking(false)` 会把正在进行的 SMASH 反复打断。

相关:[[goety-mold-hardcoded-mixin]]、[[goety-leapleaf-model-geometry]]、[[goety-servant-goal-priority-flags]]
