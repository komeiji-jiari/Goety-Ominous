---
name: thrasher-servant-player-riding
description: "长尾鲨/巨型长尾鲨的玩家骑乘——乘客位被\"叼猎物\"占用,靠 instanceof Player 分流;只有主人能骑;水中速度走 SWIM_SPEED"
metadata: 
  node_type: memory
  type: project
  originSessionId: 32403d76-827f-4982-aecf-dbf281e3ede4
  modified: 2026-10-04T16:51:26.196Z
---

2026-10-04 给两种长尾鲨仆从加了玩家骑乘(玩家 WASD 操控、**只有主人能骑**、坐在背上而非嘴里)。`GreatThrasherServant extends ThrasherServant`,只覆写属性/体型/骑乘偏移,骑乘逻辑两边共用。

**核心难点:乘客位是共用的。** 原版(UA)设计里 `getFirstPassenger()` 是「嘴里叼住的猎物」:`positionRider` 把人摆在头前方 `getMountDistance()`(小 1.2/巨 2.1)格处,`addPassenger` 把碰撞箱撑成 `CAUGHT_SIZE`,`ThrashGoal` 每 5 tick 给乘客扣一次攻击伤害并把人甩得 75° 乱转。所以骑乘分流全部靠 **`passenger instanceof Player`**:
- 「乘客是 Player ⇒ 玩家主动骑上来的」这条等价要**两处合起来**才成立:`isValidTarget` 排除 Player(索敌那条路,见 [[thrasher-servant-two-stage-targeting]])**加上**下面那条 `setTarget` 拦主人(报复那条路会绕过 `isValidTarget`)。
- `positionRider` 玩家分支走 **`super.positionRider(passenger, function)`(双参!)** → 居中 + `getPassengersRidingOffset()`,0.5/0.875 = 背上;`getDimensions` 对玩家乘客不返回 `CAUGHT_SIZE`(否则碰撞箱被撑宽)。**写成单参 `super.positionRider(passenger)` 会无限递归崩服**——踩过,见 [[forge-positionrider-two-overloads]]。
- **`setTarget` 拦主人**(2026-10-04 补):Goety 的 `targetRetaliateGoal()` 挂的是 `ServantHurtByTargetGoal(this, new Class[0])`,它只排除"同为 IOwned/同盟"的攻击者——**主人是 Player 不是 IOwned,漏网**。所以主人误伤自家长尾鲨 → `HurtByTargetGoal` 把主人设成 target → `GrabGoal` 把主人当猎物叼嘴里(而 `startRiding` force=true 又绕过 `canAddPassenger`)。修法:`ThrasherServant.setTarget` 里 `if (target == getTrueOwner()) target = null;`——这是 Goety 自己 `Owned` 内层报复 goal 的写法(`attacker != getTrueOwner()`),对齐它。**没动 `targetRetaliateGoal`**:传 `Player.class` 会把"别的玩家打我仆从要还手"一起废掉。
- `ThrashGoal` 两个 canUse 见到 Player 直接 false;`shouldRiderSit()` 返回 `getFirstPassenger() instanceof Player`(见 [[forge-ridden-mob-water-speed]] 第二条,不这样玩家会站着渲染)。
- `canAddPassenger`:Player 只放行 `getTrueOwner()`,其余交 `super`(原版 `Entity` 默认只允许 1 个乘客,所以猎物上限没变)。

**上马点**:`mobInteract` 里喂鱼那段的同级,条件 `!player.isShiftKeyDown() && !(itemstack.getItem() instanceof IWand) && !level().isClientSide`。三个坑(2026-10-04 全部踩过一轮):
- **门必须看 shift 键,不能看 `isCrouching()`**。姿势在水里/游泳时与 shift 分叉,于是分支照常执行、而原版 `canRide` 静默返回 false → 表现就是"右键完全没反应"。见 [[forge-ridden-mob-water-speed]] 第五条。
- **骑乘必须空手**:法杖右键仆从是 Goety 的指令,`DarkWand.interactLivingEntity` 会先吃掉对自己仆从的右键(切换 待命/游荡/守卫,带 CommandFocus 则是令仆从移动到该位置)——门里那句 `!(item instanceof IWand)` 就是为了不抢这个。症状:右键仆从"只会移到位置/没骑上去"。
- **已在背上的右键要幂等空操作**。原本写成"先 `ejectPassengers()` 再上马(顺便当右键下马用)",结果连点两下 = 上去→被踹下来,而且 `removePassenger` 会给乘客 `boardingCooldown = 60` → **3 秒内再点一律失败**。**下马交给 Shift** 就行(`Player.rideTick()` → `wantsToStopRiding()`),不需要右键兜。

补充:UA 自己的 `UAEvents.onPlayerMount` 只在 `entityBeingMounted instanceof Thrasher` 时**取消下马**(叼住的猎物跑不掉),我们的 `ThrasherServant` 是 `Summoned` 不是 UA 的 `Thrasher`,所以不适用、Shift 正常下马。

**操控**:`getRiddenInput` 抄 `SkelewagServant`——`(xxa*0.5, isInWater() ? player.getLookAngle().y : 0, zza)` 拿视角俯仰当升降;`tickRidden` 里 `setRot(player.getYRot(), player.getXRot()*0.5)` 并**顺手 `setMoving(zza/xxa != 0)`**,否则骑乘时 `ThrasherMoveController` 走 else 分支把 `isMoving` 压成 false、尾巴动画僵住。`updateControlFlags` 用 `isRiderBusy()`(玩家按了方向键)关掉 MOVE/JUMP/LOOK,松手 AI 接管。

**水中速度**:`getRiddenSpeed` 只管陆地(`MOVEMENT_SPEED * 0.2`);水里靠 `updateRiddenSwimSpeed()` 每 tick 增删 Forge `SWIM_SPEED` 上的 `RIDDEN_SWIM_SPEED`(`MULTIPLY_TOTAL +0.75`)瞬态修饰符。用瞬态是因为它不进 NBT,存档重载会自动清掉,不会留下一条永久超速的鲨鱼。**嫌快/嫌慢就调这个 0.75。**

另外把「离水在岸上乱弹」那段(`!isInWater() && !isStunned() && onGround()`)加了 `getControllingPassenger() == null`:否则骑到岸上会被随机冲量和随机 yRot 甩飞。
