---
name: vanilla-swim-movecontrol-xxa-reference
description: "自制水中 MoveControl 必须逐行对照原版 SmoothSwimmingMoveControl(海豚/海龟):最大坑=实体 xRot 是 LookControl 每 tick 复位的寄存器,拿它当俯仰积分器→永远攒不起角度→不能下潜→绕节点打转;另含 xxa 必须清、降速因子只在出水分支、Goety StrafeGoal 优先级1抢 MOVE(门 coolTick>0)、maxTurnX/Y 取 85/90"
metadata: 
  node_type: memory
  type: reference
  originSessionId: 0672aaf0-77b6-46d7-81bf-27f28135f10d
  modified: 2026-10-06T21:41:36.610Z
---

潮涌之叶"老旋转"(2026-10-03)的根因。**原版早就有一个可照抄的泳控**:`net/minecraft/world/entity/ai/control/SmoothSwimmingMoveControl`(海豚/海龟用,`new SmoothSwimmingMoveControl(mob, maxTurnX, maxTurnY, inWaterSpeedModifier, outsideWaterSpeedModifier, applyGravity)`,海豚/海龟都是 `(85, 10, 0.02F, 0.1F, true)`)。自己写泳控时先拿它逐行对,**别凭感觉发明**。

## 三处偏离(都是真 bug)

1. **`xxa` 必须显式清零,否则侧着身子一直游。**
   `xxa` 在整个原版里**只有 `MoveControl` STRAFE 分支**(`MoveControl.java:83`,`strafe(f,r)` 时写 `setXxa(strafeRight)`)和少数 hardcode 写;**没有任何地方会把它清回去**。`MOVE_TO` 分支也只 `setZza`,不动 xxa。所以一旦陆上 strafe 过(`xxa=±0.5`),进水里若你的水中分支不清它,`LivingEntity.aiStep()` 末尾的 `travel(new Vec3(xxa, yya, zza))` → `moveRelative(SWIM_THRUST, …)` 就每 tick 侧推一次 → **模型朝前、身体横着滑,看着就是原地打转**。
   原版泳控的 else 分支是**四条全清**:`setSpeed(0) + setXxa(0) + setYya(0) + setZza(0)`。自己写的时候连 `isLeaping()` 这类提前 return 也要带上 `setXxa(0.0F)`。

2. **转速降速因子只属于「出水」分支。**
   `getTurningSpeedFactor(err) = 1 - clamp((err-10)/50, 0, 1)`(≥60° 归零)在原版里是 `outsideWaterSpeedModifier` 那条**岸上**分支才乘的;**水里是 `setSpeed(f1)` 全速**。把它挪进水里 → 朝向误差 ≥90° 时推力归零,而 `rotlerp` 还在 20°/tick 转 → "只转不走";waypoint 又近又在动时(追活体、目标点抖)会锁死。**原版从不用朝向误差把速度打到 0。**

3. **到点(vanilla `d3 < 2.5000003E-7F`)只 `setZza(0)`,别顺手清别的。** 俯仰更新也带 `|dy| > 1e-5 || |horizontal| > 1e-5` 的门。水里的 `zza/yya` 用 `speed` 而不是 `speed * inWaterSpeedModifier`(那个 modifier 只写进 `setSpeed` 字段,而水中 `travel` 根本不读 speed 字段,只吃 `moveRelative(0.02F,…)`)—— 这也说明**游泳速度实际全由 zza/yya 决定**。

## Goety 老 goal 会抢 MOVE(优先级比新加的 goal 高)

`Leapleaf`(Goety)在 `registerGoals` 里给陆地跳虫装了 `StrafeGoal`(**优先级 1**,`EnumSet.of(MOVE, JUMP)`)。子类只按类名删了 `FloatGoal`/`Leapleaf$AttackGoal` 时,**`StrafeGoal` 会活下来**,把子类后加的 3/4/5/6/7 号 MOVE goal 全压住。它 `canUse` 的门是 **`coolTick > 0`**(`Leapleaf.java:132` 初始 40 tick,跳劈落地后重置 40);在 `coolTick > 0` 的窗口里它 `nav.stop()` + `MoveControl.strafe(0.5, ±0.5)`,水里就是整合 AI 静默 + `LookControl` 盯目标 + `BodyRotationControl` 把身体拧过去。
**封法**:`StrafeGoal` 是包私有内部类,删/包都做不到;但 `coolTick` 是 public 字段 —— 在水里离底时 `this.coolTick = 0` 即可(`LeapGoal` 另需 `onGround()`,深水里本来跑不了;`!onGround()` 这个门还能保住"卡跳后 10 tick 冷却"不被抹掉)。陆地行为零改动。

**副作用(2026-10-03 晚踩到)**:这一封把 `coolTick` 在水里变成了**恒 0 的死值**,任何"想借上游 coolTick 当自己技能冷却"的水中 goal 都会变成无冷却(每 tick 可再触发)。水中新加的招必须自带冷却字段;而且**不能只在自己 goal 的 `tick()` 里递减** —— goal 不在跑时 `tick()` 不被调用,冷却会永远卡在初值上,要在实体 `tick()` 里递减。见 [[goety-leapkelp-land-water-leap]] 第 15 条。

## 4. 别删 `yBodyRot/yHeadRot` 那两行(2026-10-03 复核)

看到自研泳控里 `yBodyRot = getYRot(); yHeadRot = getYRot();` 容易当成"多余、和 LookControl 抢"而删掉 —— **原版也这么写**。`SmoothSwimmingMoveControl.java`(`.scratch/forge_src2/...`)水里分支同样是 `setYRot(rotlerp(...))` 紧跟这两行。逐行对完的结论:我们 `LeapkelpMoveControl` 的水分支与原版同构,只有三处有意偏差(4× 速度乘子、`SWIM_THRUST` 单独走 `travel`、水里绝不落回 `super.tick()`)。**朝向抖动的账不能算在这两行上**,去查 goal 层(见 [[goety-attackgoal-no-flags-yaw-fight]])。
(第 6 条那个俯仰钳位也算一处偏差,2026-10-03 晚已按原版改掉。)

## 5. 宽怪 waypoint 瞄点偏移 → 取 Goety `noSpinControl` 的死区

`Path.getEntityPosAtNode` 是 `node + (int)(bbWidth + 1) * 0.5`(**整数截断**),Forge 补丁把 `followThePath` 的到点判定改成精确的 `(bbWidth + 1) / 2`。两者只在 bbWidth ∈ (1,2) 时错开:我们 **bbWidth = 1.9 → 瞄点 node+1.0,判定点 node+1.45**,差 0.45 格。瞄点偏在 +x/+z 定向上,所以往 -x/-z 走时瞄点可能落到怪身后 → 朝向目标翻 180°,`rotlerp` 每 tick 往回拧 = 转。Goety 给它自己的胖石傀儡用的是 **`MobUtil.noSpinControl`**:复写 `rotlerp`(`m_24991_`),**离 wanted 点 0.5²格(≈0.707)以内直接返回原角度**,否则按 `0.25×` 转速。我们只在 `LeapkelpMoveControl.rotlerp` 取了**死区那一半**(`dx²+dz² < 0.5D → return source`),**没取 0.25 倍**:我们的水里推力是跟 yRot 耦合的([[goety-leapkelp-land-water-leap]] 第 15 条/`travel` 走 `moveRelative`),转速砍到 5°/tick 会变成大弧线。死区靠 `rotlerp` 虚分发对陆上 `super.tick()`(原版 MOVE_TO 也调 `rotlerp`)一并生效。

## 6. 真正的根因(2026-10-03 晚实测定位):`xRot` 是 `LookControl` 独占的「每 tick 复位」寄存器

**任何把实体 `xRot` 当自己的积分器/状态位、用来驱动自研 MoveControl 的写法都是坏的。**

`LookControl.tick()`(`Mob.serverAiStep` 里排在 **moveControl 之后**)第一件事就是:

```java
if (this.resetXRotOnTick()) { this.mob.setXRot(0.0F); }   // 默认 return true
if (this.lookAtCooldown > 0) { ...; this.mob.setXRot(rotateTowards(getXRot(), getXRotD(), xMaxRotAngle)); }
```

所以每 tick 收尾时 xRot 只可能是 **0**,或有 lookAt 目标时被拉过去的角度。我们水里推力 `setZza(cos(xRot)*speed)` / `setYya(-sin(xRot)*speed)` 是拿 xRot 当俯仰积分器的(`rotlerp(getXRot(), pitch, 5.0F)`),**每 tick 都被清零,增量永远攒不起来**(实测 xRot 卡在 −5…−12.7 = 正被拉向 owner 的角度,而 waypoint 要的是 +81 低头)。

后果链(这就是「边游边绕圈」):

- 推力仰角恒 ≈0 → **怪根本没能力下潜**(水里 `travel` 无重力,浮力那条 0.002 只朝上) → owner 上岸时 `wantsToLeaveWater()`→`wantsToSwim()`=false,在水里却用 `GroundPathNavigatorFat`,给的点在脚下 3.5 格;而 `followThePath` 的到点判据是 `|mobY − nodeY| < 1.0`,**永远不满足** → 路径索引卡在 0,绕着节点的水平投影转圈、每圈 yaw 翻 180°。
- 同一段日志的反证:t=90–95 用 `ModWaterPathNavigation`(节点同层)时游得干干净净。

**修法**:MoveControl 自持 `swimPitch` 字段做积分(`swimPitch += clamp(wrapDegrees(target − swimPitch), ±5.0F)`),推力吃它,**不写 xRot**(xRot 归 LookControl,原版语义就是"视线俯仰")。别指望改 LookControl:即使覆写 `resetXRotOnTick` 返回 false(蜜蜂/狐狸/青蛙都这么干,青蛙是 `getTongueTarget().isEmpty()`),**有 lookAt 目标时它照样**把 xRot 拉向目标 —— 而我们的跟随 goal 每 tick 都在 setLookAt(owner)。

## 7. 两个曾误判为根因的偏差(仍照原版改,但都不解释打转)

原版 `SmoothSwimmingMoveControl` 构造统一 `(85, 10, 0.02F, 0.1F, true)`(`Dolphin.java:77`、`Frog.java:86`、`Tadpole.java:49`)。我们的 `SWIM_MAX_TURN_X=20`、`SWIM_MAX_TURN_Y=20` 已改回 **85 / 90**;值本身该改,但**都不是打转的原因**(俯仰那个因第 6 条压根没生效,偏航那个只是把圈画小)。

- `maxTurnX` 钳的是**推力仰角上限**(仰角 = xRot,`moveRelative` 只按 yRot 水平旋转,y 分量直通)。
- `maxTurnY` 决定转弯半径 `r = v/ω`:水里 `v ≈ 0.14 格/tick`(`SWIM_THRUST` 0.014 × `travel` 的 `scale(0.9)` 阻尼)、`ω=20°/tick` → `r ≈ 0.4 格`,而怪 **bbWidth 1.9** → 读作原地磨圈。**判据:转弯半径要 ≥ 体型**。
- 陆地走原版 `super.tick()`(90°/tick → r≈0.09)所以从来看不出转;Goety 给水生仆从的 `MoveHelperController` 也是 90。
- **别往"慢转 = 大弧线"方向修**:r 太大(5°/tick → r≈2.8 格)会变成**绕节点公转、永远够不着**。
- 第 6 条那个抢写问题**陆地上不存在**(陆行俯仰只影响外观、不驱动位移)—— 这就是"只有水里打转"的原因。

## 8. 自研水分支替换 `super.tick()` 会丢掉原版的「台阶太高就跳」(2026-10-03 晚「水里被岸边顶住」)

`MoveControl.tick()` 的 **MOVE_TO 分支末段**有一整块「前方够不着就跳」,水里自研分支若整段替换 `super.tick()`,这段就没了 → **怪上了岸,顶在岸壁前一步一顶、出不来**(水里没有 jump 来源,`yya` 推力一出水面就失效:推力只在 `isInWater()` 时给,离开水面立刻被重力拽回 → 在水线反复下沉)。

原版条件(逐字,`d0/d1/d2` = wanted − mob 的 x/z/y):

```java
BlockPos blockpos = this.mob.blockPosition();
BlockState blockstate = this.mob.level().getBlockState(blockpos);
VoxelShape voxelshape = blockstate.getCollisionShape(this.mob.level(), blockpos);
if (d2 > (double)this.mob.getStepHeight() && d0 * d0 + d1 * d1 < (double)Math.max(1.0F, this.mob.getBbWidth())
    || !voxelshape.isEmpty() && this.mob.getY() < voxelshape.max(Direction.Axis.Y) + (double)blockpos.getY()
       && !blockstate.is(BlockTags.DOORS) && !blockstate.is(BlockTags.FENCES)) {
   this.mob.getJumpControl().jump();
   this.operation = MoveControl.Operation.JUMPING;
}
```

- 注意 `d0*d0 + d1*d1 <` 右边是 **bbWidth 本身(没平方)** —— 原版就这么写,照抄别"顺手修正"。
- **`operation = JUMPING` 不能照搬**:`MoveControl.setWantedPosition()` 有守卫 `if (this.operation != Operation.JUMPING) { this.operation = MOVE_TO; }`,而自研分支没有 JUMPING 的出口(原版靠 `onGround()` 退出,水里等不到)→ 一旦写进去,导航再也无法把它恢复成 MOVE_TO,**自研分支会永久走 else、推力恒 0**。只调 `getJumpControl().jump()` 就够。
- **水面跳跃的力度取决于 `canFloat()`**:`JumpControl.jump()` → `setJumping(true)` → 同 tick `LivingEntity.aiStep` 的 "jump" 段 → `jumpInFluid(WATER)` → `Mob.jumpInLiquidInternal` = `getNavigation().canFloat() ? 原版(+0.04×swimSpeed) : setDeltaMovement(add(0, 0.3, 0))`。`canFloat` 来自 `NodeEvaluator.canFloat`(默认 false),只有显式 `setCanFloat(true)` 才变 —— Goety 的 `ModWaterPathNavigation`(自建 `AmphibiousNodeEvaluator`,没调 setCanFloat)和 `GroundPathNavigatorFat`(纯 `WalkNodeEvaluator`)都是 **false** → **+0.3 真跳**(够上 1 格岸)。反过来,若哪天给它们补了 `setCanFloat(true)`,这个跳会退化成 0.04 的浮力,出不了岸。
- 时序没问题:`serverAiStep`(goal→nav→moveControl→lookControl→**jumpControl**)在 `LivingEntity.aiStep` 的 "jump" 段与 `travel` **之前**,同 tick 生效。

## 9. 第 8 条那个 `jump()` 是「水里高速上升、飞出水面」的根因(2026-10-06 修)

用户报「潮涌之叶在水里有时候会高速上升,甚至飞出水面」。机制:

- `getJumpControl().jump()` 在水中最终走 `LivingEntity.aiStep` 的 jump 段 → `jumpInFluid(WATER_TYPE)` → `Mob.jumpInLiquidInternal`:nav 的 `canFloat()` 为 false 时是 **`deltaMovement.y += 0.3`**(单 tick 加常量),不是原版那个 0.04。
- 而自研 swim `travel` **无重力、只 `scale(0.9)`** → 每 tick +0.3 累积,终端速度 `0.3/(1-0.9)` = **3.0 格/tick(60 格/s)**。原版水里看不出来是因为原版水分支有 `multiply(f4, 0.8, f4)` + `getFluidFallingAdjustedMovement` 重力兜底。
- 触发面比想的宽:照抄的原版条件有两半,第二半 **`!shape.isEmpty() && mobY < shape.max(Y)+pos.getY()`** 只要怪的脚稍微陷进水底方块就成立 → **贴着海底游 = 每 tick 起跳**;第一半 `dy > stepHeight && 水平² < bbWidth` 在「水柱正上方有目标/路径点」时同样成立。

**修法(Leapkelp.java `LeapkelpMoveControl` 水分支)**:丢掉 shape 那半(水里"脚下是实心块"不是台阶,只有害),第一半加两个门 + 把冲量换成有界赋值:

```java
Vec3 movement = this.leapkelp.getDeltaMovement();
if (movement.y < (double) SWIM_CLIMB_SPEED                                   // 0.3F,防叠加
        && dy > (double) this.leapkelp.getStepHeight()
        && dx * dx + dz * dz < (double) Math.max(1.0F, this.leapkelp.getBbWidth())
        && this.leapkelp.level().getFluidState(BlockPos.containing(this.wantedX, this.wantedY, this.wantedZ)).isEmpty()) {
    this.leapkelp.setDeltaMovement(movement.x, SWIM_CLIMB_SPEED, movement.z);
}
```

- **「目标点不是水」才是"要出水"的真判据**:深水里追水面上的主人 / `SwimUpGoal` 的路径点都是水节点 → 不再触发;岸边 `MoveToBlockGoal`(GoToBeach)的 `wantedY = nextPos.getY()+1` 是陆地 → 照常触发。比 `horizontalCollision` 稳(俯仰陡时水平推力小,可能压根没贴到岸壁)。
- **`set` 而不是 `add`**(照抄原版水里那个 `setDeltaMovement(x, 0.3, z)` 的爬岸 hop):`movement.y < 0.3` 门保证不叠加,封顶 0.3 格/tick = 6 格/s。仍比正常泳速(0.14 格/tick)高一个量级,够顶上岸;但不再是指数级的 3.0。
- 配套:1 格坎本来就不归它管(`getStepHeight()` 覆写成 1.25,`Entity.move` 自己跨),这段只管出水/2 格以上坎。

相关:[[goety-leapkelp-land-water-leap]]、[[goety-servant-goal-priority-flags]]、[[forge-movecontrol-runs-after-goals]]、[[leapkelp-swim-anim-coralssus-port]]、[[goety-attackgoal-no-flags-yaw-fight]]、[[forge-maxupstep-mapping]]
