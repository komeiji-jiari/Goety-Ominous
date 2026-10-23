---
name: goety-summon-idle-freeze
description: "非敌对 Goety Summoned 站桩不动根因——plain RandomStrollGoal 被 noActionTime 永久禁用,须用 Summoned.WanderGoal"
metadata: 
  node_type: memory
  type: project
  originSessionId: 84fcbd10-1e6d-41a5-a7c1-2a653db36395
  modified: 2026-09-23T19:04:51.095Z
---

非敌对 Goety 仆从(Summoned 系)若用原版 `new RandomStrollGoal(this, 1.0D, 45)` 会在空闲约 5 秒后彻底站桩不动。

**Why:** 每个 AI tick `Mob.serverAiStep()` 都 `noActionTime++`;唯一空闲复位点在 vanilla `checkDespawn()`(玩家在 noDespawn 距离内时置 0)。而 `Summoned.checkDespawn()` 对非敌对仆从短路(`if(isHostile()) super.checkDespawn()`),不复位 noActionTime。原版 `RandomStrollGoal` 默认 `checkNoActionTime=true`,`noActionTime>=100` 时 `canUse()` 永久 false → 再也不游荡(战斗/受伤可短暂解冻)。

**How to apply:** 给仆从加游荡一律用 Goety 内建 `Summoned.WanderGoal<>(this, speed, interval, prob)`——它向 RandomStrollGoal 传 `checkNoActionTime=false`,还会把落点限定在主人附近、尊重 staying/commanded。4 参构造保原 AC 节奏示例:`new Summoned.WanderGoal<>(this, 1.0D, 45, 0.001F)`。别名写法:4 参是 `(entity, speed, int interval, float prob)`,2 参 `(entity, speed)`=interval120+prob0.001。已修 CaniacServant/GummyBearServant/GumbeeperServant/OvergrownColossusServant;其余仍用 plain RandomStrollGoal 的仆从(Brainiac/Nucleeper/Gammaroach/Corrodent/DeepOneMage/Bunfungus 及部分 illager/mobs)有同样潜伏 bug。参考 [[goety-servant-follow-standby]]、[[ac-compat-porting-notes]];诊断依据:Forge gradle 缓存的 joined decompile/output.jar 是带 SRG 名的原版反编译源码。

**prio 摆放**:`OvergrownColossusServant`(2026-09-08)登记了 RandomStrollGoal(prio5) 与 super 的 FollowOwnerGoal(5) 撞号,但 idle 时 follow 不激活,故真实杀伤是 noActionTime 冻结。改 `addGoal(6, new Summoned.WanderGoal<>(this, 1.0D))`——该仆从已有大量 IAttackGoal/IStateGoal(prio0/1)+IMoveGoal(2) 索敌近战,2 参 WanderGoal 保原 120 tick 节奏;prio 取 >5(follow) 且 <7(LookAtPlayer)。巨像无 stay-no-move 特化 IStateGoal(那类 `setDeltaMovement(0,...)` 都挂在 attackState=1/11/12 战斗态,idle 态是 0 不触发)。

**变体:AM 的 `AnimalAIWanderRanged`(比 plain RandomStrollGoal 更毒,2026-09-23)**。AM 版 `canUse()` 自己写了 `if (mob.getNoActionTime() >= 100) return false;`,**完全无视构造参数 `checkNoActionTime`**——`super(creature, speedIn, chance)` 走 3 参构造即 `checkNoActionTime=true`,想传真也绕不过去。所以凡是直接 `new AnimalAIWanderRanged(this, interval, speed, xz, y)` 的仆从,同样生成 5 秒后永久站桩(且冻结必然发生在幼年期窗口内,容易被误判成"幼年没交互导致的")。修法同上,把它换 `Summoned.WanderGoal<>(this, speed, interval, 0.001F)`,保留原 interval/speed/priority:已修 TusklinServant(120)/EmuServant(110)/FroststalkerServant(90,prio6)/RockyRollerServant(90,prio6)/GusterServant(60,prio2)/MurmurServant(55,prio2),仓库内 `AnimalAIWanderRanged` 已清零。注意 ServantWanderGoal 的 `getPosition()` 与 AM 那份形状一致(in-water→LandRandomPos、prob 门控),**但落点锚在主人周围**且水域会往岸上找路,故语义不漏。AM 那份的 `isSitting()` 匿名重写可一并删——ServantWanderGoal.canUse 内建 `!isStaying()&&!isCommanded()`。

**变体:仆从压根没注册任何 stroll/wander 目标**(如 TeletorServant,2026-09-08)。`Summoned.registerGoals()` 只挂 FollowOwnerGoal(prio 5)+索敌;空闲游荡要靠每个仆从自己加 WanderGoal。Teletor 只挂了 MeleeGoal(3)+RandomLookAroundGoal(9),而 RandomLookAround 只转头不含 MOVE → 切到**游荡模式**(isWandering=true→isFollowing()=false,FollowOwnerGoal 被禁,又没目标)就彻底悬停。原 AC Teletor 有 `RandomStrollGoal(this,1.0,45)`,仆从化时丢掉了。修法:`addGoal(7, new Summoned.WanderGoal<>(this, 1.0D, 45, 0.001F))`——prio 须低于 melee(3)/follow(5)之上(此处 5<7<9)战斗/跟随优先;WanderGoal.canUse 内建 !staying&&!commanded 门控,待命悬浮守卫不受影响。
