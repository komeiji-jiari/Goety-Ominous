---
name: goety-servant-goal-priority-flags
description: Summoned 仆从的 idle 型目标被跟随/注视目标 flag 压制永不触发;须把 priority 压到低于 FollowOwnerGoal(5)/LookAtPlayerGoal(3) 才能打断
metadata: 
  node_type: memory
  type: project
  originSessionId: 6986f809-7b47-4aaa-8abf-b8c937a1fe7f
  modified: 2026-09-21T08:46:00.303Z
---

1.20.1 GoalSelector 用 WrappedGoal + LinkedHashSet(插入序):候选目标能启动当且仅当它需要的每个 flag(MOVE/LOOK)当前持有者 `canBeReplacedBy(candidate)` = 持有者可中断且 **候选 priority < 持有者 priority**。同 priority 互不可替换。

抢占的代价是 `WrappedGoal.stop()` —— 停掉的是**整个持有者目标**(不是只让出那个 flag)。所以一个只声明 Flag.LOOK 的高优先级目标,会连带把 `FollowOwnerGoal(5)`(MOVE+LOOK)整个停掉,而它随后又挡住 follow 重启(5.canBeReplacedBy(4)=false)→ 仆从从此不跟随。想改"朝向类"行为别用 goal,直接写实体 tick()(见 [[vanilla-lookcontrol-head-forward]])。

**Why:** LicowitchServant 从 AC 敌对版移植 `LicowitchUseCrucibleGoal` 时抄了逻辑却把 priority 放在 6。Goety 基类 Summoned 已在 goalSelector 注册 `FollowOwnerGoal`=**5**(MOVE+LOOK),再加 LookAtPlayer=3(LOOK)/RandomlyTeleport=2(MOVE)/Wander=6(MOVE),于是主人附近时 LookAtPlayer 锁 LOOK、主人稍远时 FollowOwnerGoal 锁 MOVE+LOOK —— 坩埚目标(6)永远 6<X 不成立,变成死代码。敌对版 Licowitch 把坩埚目标放 **2**(仅低于攻击 1)才正常。

**How to apply:** 移植 AC/MM 这类"空闲时去用某方块"的敌对目标到 Goety Summoned 仆从时:
- 把该目标 priority 放 **2**(与攻击 1 之下、可打断 follow 5/lookat/闲逛),才可能打断基类跟随;
- 战斗让位靠 Attack=1 优先级天然覆盖 + 目标 canUse 里 gate `target 存活`/`动画 NO_ANIMATION`;
- 仆从特有的 stay/command 门控仍保留(需求是"待命时禁用",参考 [[goety-command-mode-conflict]]、[[goety-summon-idle-freeze]]);
- 其它顺延:RandomlyTeleportGoal 3、LookAtPlayerGoal 4、WanderGoal 6、RandomLookAround 7(对齐敌对版次序)。

样例: `LicowitchServant.registerGoals()` `addGoal(2, new LicowitchUseCrucibleGoal())`(已修)。

样例2: `RelicheirusServant.registerGoals()` 砍树目标放 **4**(`RelicheirusServantPushTreesGoal`,25 格)。原版 AC 里它和 Tempt 是 4/5,但 Goety 的 FollowOwnerGoal 占 5 且先注册 → 同 5 平级抢不到,主人一走出 10 格砍树就停;提到 4 即可打断跟随(4<5),同级 `ServantTemptGoal(4,先注册)` 仍压它一头(与原版 Tempt 优先一致)。**前提**:该目标必须自带终止条件(喂汤计时 1200t 到期、`madeTreeEntity` 掉落即停),否则会像上面那样永久压住跟随。
