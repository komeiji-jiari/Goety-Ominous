---
name: mm-zombie-servant-down-yaw-freeze
description: 三个突变僵尸仆从倒地不再转向的修法 —— 在 tick() 尾部把 yRot/yHeadRot/yBodyRot 及三个 O 字段钉回 downYaw;downYaw 服务端在 die()、客户端在 entity event 7 各捕获一次;放 aiStep/baseTick 会被 LivingEntity.tick 的 headTurn 段盖掉
metadata: 
  node_type: memory
  type: project
  originSessionId: 0d6ebe98-227e-4b82-9991-057c0f3871ec
  modified: 2026-10-15T23:04:02.642Z
---

2026-10-15 用户报「倒下的突变僵尸们还在转方向」,定案修法(三个文件逐字相同,未构建 jar、未实测)。

**落点(MutantJungleZombieServant / MutantHuskServant / MutantFrozenZombieServant 各一份):**
1. 新字段 `public float downYaw;`(挨着 `downAnimationTick`);
2. 捕获:服务端 `die()` 里、客户端 `handleEntityEvent` 的 `p_21375_ == 7` 分支里,各加一句 `this.downYaw = this.getYRot();`(**必须在置位 `downAnimationTick` 的同一个地方**,两边各自的"进入倒地"时刻);
3. 新增 `tick()` 覆写:`super.tick()` 之后,若 `downAnimationTick > 0`,把 **六个**字段全部钉回 `downYaw` —— `setYRot` / `setYHeadRot` / `yRotO` / `yHeadRotO` / `yBodyRot` / `yBodyRotO`(O 字段一并钉,否则渲染 `rotLerp(O, cur)` 还会补一段旧朝向的插值)。

**为什么必须放 `tick()` 尾部:** `LivingEntity.tick()` 里 `aiStep()` 之后还有 headTurn 段(`Mob.tickHeadTurn` → `bodyRotationControl.clientTick()`)+ rangeChecks。放 `aiStep()`/`baseTick()` 都会被这后面的一段再转回去。三个僵尸自己原本都不覆写 `tick()`,父类 `AbstractMutantServant.tick()` 只多一句客户端 `updateAnimations()`,钉在后面无副作用。

**为什么两边各自捕获而不是同步一个值:** 倒地在服务端只由 `die()` 置位、客户端只能靠 entity event 7(`Entity.broadcastEntityEvent` 只发包、不回调本地);而 `yBodyRot` 本来就不参与同步(客户端自己算),所以两边各冻"自己当时那一刻"的朝向 —— 差异就是常态的插值延迟,不会在倒地瞬间产生跳变。

**排除过的怀疑(flag 机制没问题):** `RemainStationaryGoal`(`canUse` = `shouldBeStationary()`,优先级 0,`MOVE|LOOK|TARGET|JUMP`)确实挡得住 MM 的 `LookAtTargetGoal`(LOOK)与 `ApproachTargetGoal`(MOVE —— 注意它 `setFlags` 把 `MeleeAttackGoal` 的 `MOVE|LOOK` **覆盖成只剩 MOVE`,`Goal.setFlags` 是 clear+addAll)。`GoalSelector` 的规则:候选 goal 的每个 flag 的当前持有者必须 `isInterruptable() && 候选优先级数字更小` 才让位,所以 0 号能独占。转动来自 goal **之外**:`navigation.tick()`/`MoveControl`(不受 flag 管)+ `BodyRotationControl.clientTick()` 移动分支 `yBodyRot = yRot` 硬贴、非移动分支 `rotateHeadTowardsFront()` 把身体往头上拉。

**范围:** 只改了这三个僵尸。`MutantWitherSkeletonServant` 也有 down 状态(另有 `getFlipDegrees` 等),用户没点名,未动。相关:[[mm-zombie-servant-equip-interaction]]、[[mob-tickheadturn-bodyrot-snap]]、[[goety-attackgoal-no-flags-yaw-fight]]、[[mm-mutant-husk-servant-port-notes]]
