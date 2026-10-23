---
name: forge-ridden-mob-water-speed
description: 原版骑乘的七个硬事实——水中 getRiddenSpeed 失效要改用 SWIM_SPEED、shouldRiderSit 决定骑手姿势、travelRidden 的调用顺序、服务端只在 isPassenger 时收 WASD、上马门 canRide 看 shift 键且全部静默失败、removePassenger 会挂 60tick boardingCooldown、下马走 rideTick/wantsToStopRiding
metadata: 
  node_type: memory
  type: reference
  originSessionId: 32403d76-827f-4982-aecf-dbf281e3ede4
  modified: 2026-10-10T14:38:59.716Z
---

1.20.1 给「玩家骑怪」写覆写时的七个原版事实,查过源码的(路径见下):

**一、水里 `getRiddenSpeed()` 是废的。** `LivingEntity.travel` 水中分支:
`f5 = 0.02F; f6 = EnchantmentHelper.getDepthStrider(this); if (f6 > 0) f5 += (getSpeed() - f5) * f6/3; f5 *= SWIM_SPEED;`
怪没有靴子附魔 → `f6` 恒 0 → `f5` 恒 `0.02 * SWIM_SPEED`,**`getSpeed()` 被完全忽略**。而 `travelRidden` 写的正是 `setSpeed(getRiddenSpeed(player))`。所以水中想调骑乘速度只能动 Forge 的 `ForgeMod.SWIM_SPEED` 属性。注意 `moveRelative(f5, vec)` 内部会 `normalize()`,**缩放输入向量没用**。
`SkelewagServant` 能用 `getRiddenSpeed` 是因为它连 `travel` 一起覆写、直接 `moveRelative(this.getSpeed(), …)` 绕开原版水分支——代价是丢了原版 2064 行的「贴墙时 y=0.3 蹬台阶」,会顶在岸边出不了水(与 [[vanilla-swim-movecontrol-xxa-reference]] 同一个坑)。

**二、`shouldRiderSit()` 决定骑手用不用跨坐姿势。** `LivingEntityRenderer:56`:
`boolean shouldSit = e.isPassenger() && e.getVehicle() != null && e.getVehicle().shouldRiderSit(); this.model.riding = shouldSit;`
返回 false → 骑手**站着**渲染。UA 长尾鲨返回 false 是因为嘴里叼的猎物在挣扎;玩家骑背必须 true。它没有参数,想按乘客区分只能用 `getFirstPassenger()`。

**三、`travelRidden` 的顺序**(`LivingEntity` 2600 附近,被 `aiStep` 尾部调用):
`getRiddenInput` → `tickRidden` → 若 `isControlledByLocalInstance()` 则 `setSpeed(getRiddenSpeed(player))` 再 `travel(input)`,否则 `setDeltaMovement(ZERO)`。
关键:这段在 `Mob.serverAiStep()`(内含 `moveControl.tick()`)之后,所以自研 MoveControl 里那句 `setSpeed(0F)` **不会**踩掉骑乘速度。

**四、服务端确实收得到骑乘玩家的 WASD。** `ServerboundPlayerInputPacket` 带 `xxa`/`zza`,`ServerPlayer.setPlayerInput(xxa, zza, jumping, shift)` 赋值——但门是 `if (this.isPassenger())`,**只有当乘客时才同步**。所以 `isControlledByLocalInstance() → isEffectiveAi()` 这条本模组常用写法是成立的:服务端 `isEffectiveAi()` 为真,用真实输入模拟。副作用:`ThrashGoal` 里那句 `entity.setShiftKeyDown(false)` 会**禁掉骑乘玩家的下马键**(shift),玩家乘客不能走那条路。

**五、上马的门是三重的,而且全部静默失败。** `Entity.startRiding(vehicle, force)` 的顺序:`vehicle.couldAcceptPassenger()`(已 deprecated,默认 true)→ 环检测 → `ForgeEventFactory.canMountEntity(...)`(post `EntityMountEvent`,**别的 mod 能整个否决上马**)→ 最后才 `force || (this.canRide(vehicle) && vehicle.canAddPassenger(this))`。任一条不满足就 `return false`,无日志无提示。其中 `Entity.canRide` = **`!this.isShiftKeyDown() && this.boardingCooldown <= 0`** —— 看的是 **shift 键标志**。拿 `isCrouching()`(=`getPose()==CROUCHING`)当门会分叉:游泳时按着 shift 姿势不是 CROUCHING,于是自己的分支跑了、原版却拒绝,症状就是"右键毫无反应"。**写骑乘交互时门直接判 `isShiftKeyDown()`。**

**六、`removePassenger` 会给乘客挂 60 tick `boardingCooldown`。** `Entity.removePassenger` 里那句 `pPassenger.boardingCooldown = 60`。所以**任何"先踹下来再重新上马"的写法都会把自己锁 3 秒**——典型是让右键兼任上/下马:玩家连点两下就是 上去→被踹→之后 3 秒内怎么点都失败。

**七、下马是服务端按 shift 标志做的,不用自己实现。** `Player.rideTick()`:`if (!level().isClientSide && this.wantsToStopRiding() && this.isPassenger()) this.stopRiding();`,而 `wantsToStopRiding()` 对 Player 就是 `isShiftKeyDown()`。反过来说,任何强制 `setShiftKeyDown(false)` 的代码(UA 的 `ThrashGoal` 里就有一句)会**禁掉玩家下马**。另外下马同样要过 `EntityMountEvent`(`removeVehicle()` 里 `canMountEntity(this, entity, false)`),所以别的 mod 连下马都能否决。

**八、水里骑乘浮不起来 / 上不了岸(2026-10-10,AM 獠牙兽):** 上浮靠的是 `jumping` 标志——`LivingEntity.aiStep` 2554 行 `if (this.jumping && this.isAffectedByFluids())` 才走 `jumpInFluid(WATER)`(=`jumpInLiquid`,y += `0.04 * SWIM_SPEED`)。而 `jumping` 只有 `FloatGoal` 经 `JumpControl` 置位,`JumpControl.tick()` 在 `serverAiStep()` 里,**只跑服务端**;骑乘时服务端那份还会被 `travelRidden` 的 else 支 `setDeltaMovement(ZERO)` 抹掉,客户端那份(真正算骑乘位移的地方)则从来没人置位 ⇒ 骑乘怪在水里只能下沉,浮不上来。修法:`tickRidden` 里按 `FloatGoal` 同一个门自己置位——`this.setJumping(this.isInWater() && this.getFluidHeight(FluidTags.WATER) > this.getFluidJumpThreshold())`(tickRidden 两侧都跑,客户端这次置位下一 tick 就被 aiStep 的 jump 段读到)。

「上不了岸」有两半:
- `Entity.collide` 的抬台阶前提是 `flag3 = onGround() || (竖直碰撞 && y < 0)`(`_Entity_src.java:895`),飘在水面(不着地、y 不下坠)时**永远抬不上去**。
- 骑乘常写的 `tickRidden → setSprinting(true)` 会让 `getFluidFallingAdjustedMovement` 整段跳过(`if (!isNoGravity() && !isSprinting())`),水里一点重力/下沉都没有,于是怪悬在离底 0.1~0.3 格处 → 不着地 → 抬不了台阶。水里应 `setSprinting(false)`,让它落到湖底拿到 `onGround`;原版水里贴墙还有一条 `if (horizontalCollision && isFree(y+0.6)) setDeltaMovement(x, 0.3, z)`(`LivingEntity.travel` 水分支)能把怪顶上岸,前提是它得在 `travel()` 那只分支里(骑乘客户端走 `travel`,服务端被 `travelRidden` 短路,见 [[forge-travelridden-server-zero-delta]])。

源码位置:`.scratch/mcsrc`(服务端 vanilla)、`.scratch/forge_src2`(含 client 与 ServerPlayer)。
