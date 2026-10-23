---
name: goety-sauropod-ridden-neck-lag
description: 蜥脚类仆从(擎天龙/瞑煌龙)骑乘时扭头看玩家:脖子锚在慢转的 yBodyRot 而 yRot 秒跟镜头;骑乘分支须吸附身体+头,getTargetNeckYRot 用夹 ±40 的 buffer 拖尾项(不能归零,用户要扭脖子)
metadata: 
  node_type: memory
  type: project
  originSessionId: 37effd27-4657-48c3-a4ef-142ed2de678a
  modified: 2026-10-06T18:49:23.108Z
---

瞑煌龙(`LuxtructosaurusServant`)/擎天龙(`AtlatitanServant`)骑乘时脖子+头会转回来盯着骑手,根因在多部件脖子骨架的锚点:

- `tickMultipart()` 把 neckPart1-3/headPart 沿 `yBodyRot + neckYRot/4·n` 摆位,视觉朝向由 `AtlatitanServantPartEntity.calculateAnimationAngle()` 按 `parentRot = -(lerp(yBodyRot)) - 90` 反推 ⇒ **头/脖朝向 = yBodyRot + 脖子偏角**。
- 脖子偏角目标(`getTargetNeckYRot()`,与 AC `SauropodBaseEntity:501-502` 同式)= `getYHeadRot() - yBodyRot + (getYawFromBuffer(10,1.0F) - yBodyRot)`,按 `neckRotateSpeed` 10°/tick 逼近。
- 而 `yBodyRot = Mth.approachDegrees(yBodyRotO, getYRot(), turningFast ? 10 : 2)` —— **2°/tick**;骑乘时 `yRot` 被 `lerpTo` 秒吸附到骑手镜头 yaw ⇒ 镜头一甩,身体要 ~90 tick 才跟上,期间脖子+头指着转向前的方向 = 正对背后的骑手。第二个项 `getYawFromBuffer(10) - yBodyRot` 还会在急转后把脖子额外往反方向拽。

**How to apply:** 判"是否被玩家骑"别用 `getControllingPassenger()`(只有玩家按了 zza/xxa 才非 null),`LuxtructosaurusServant.isRiddenByPlayer()` = `getFirstPassenger() instanceof Player` 才是输入无关的。骑乘分支里三件事:①`yBodyRot = getYRot()`(与 [[mob-tickheadturn-bodyrot-snap]] 里 `BodyRotationControl.clientTick()` 移动分支同款硬赋,本仓其它可骑仆从 Forsaken/Gammaroach/Grottoceratops/Tremorsaurus 也都这样);②`setYHeadRot(getYRot())` 压掉一切 look goal(`LookAtPlayerGoal` 其实已用 `EntitySelector.notRiding(mob)` 排除骑手,不是元凶);③`getTargetNeckYRot()` 换成 `Mth.clamp(getYawFromBuffer(10,1.0F) - yBodyRot, -40.0F, 40.0F)`。

**③ 是 2026-10-06 用户实测后改的:先写成 `return 0.0F`,用户回"我希望转向有扭脖子"—— 全掐掉连转向时该有的脖子扭动也没了。** 只留 buffer 那一项、夹到 ±40° 就对了:`getYawFromBuffer(10)` = 10 tick(±128 环形缓冲)前的身体 yaw,所以 `buffer - yBodyRot` = "这 10 tick 转了多少度"、符号是负的 ⇒ 身体先到位、脖子朝转弯反方向甩 = 拖尾,正是原来那股扭动;夹 ±40 保证脖子最多偏 40°、永远够不到坐在正后方 180° 的骑手。`neckRotateSpeed` 10°/tick(+turningFast 30),`tickMultipart()` 里 `headYStep = neckYRot/4`,所以 40° 目标约 4 tick 到位、随后随缓冲窗口滑出自然回正。想让脖子"领转"而不是"拖尾"就把 `buffer - yBodyRot` 反过来写成 `yBodyRot - buffer`;嫌扭得不够就调大 40。

## 2026-10-06 转向时脖子突然抽一下 → yaw 环绕(yaw wrap)两处

用户:"我持续按动a或者d 脖子有时候会突然抽一下再恢复"。根因是骑乘分支 `yBodyRot = this.getYRot()` 硬贴,而 `Entity.setYRot()` 做的是 `% 360`(不是 wrapDegrees)—— 转过一圈或(我改的 A/D 转身里)每过 ±180° 时 yBodyRot 的**原始值**会跳 ±360。两个消费者都按"原始差值"算,于是:

1. **`getYawFromBuffer()`**(trail 项的来源)返回原始 buffer 值,`buffer - yBodyRot` 在跨界那 10 tick 里变成 ±320 左右 → 夹到 ∓40,**符号翻掉**:脖子目标从 +40 猛甩到 -40,等缓冲窗口把跨界前的值挤出去再甩回 +40。整段约 20 tick = 1 秒,就是用户看到的"抽一下再恢复"。**这是主因。** 修:`d1 = Mth.wrapDegrees(yawBuffer[i] - d0)` + `return this.yBodyRot + Mth.wrapDegrees(d0 + d1 * partialTick - this.yBodyRot)`(返回值改成"相对当前 yBodyRot 的带符号转角",三个调用点本来都减 `yBodyRot`,语义不变)。
2. **`*PartEntity.calculateAnimationAngle()` 的 `parentRot = -(yBodyRotO + (yBodyRot - yBodyRotO) * partialTicks) - 90`** —— 跨界的那个渲染帧插值会绕远路(±360 的 lerp),脖子/尾巴姿态错一帧。修:差值外面套 `Mth.wrapDegrees`。

Hullbreaker/Tremorzilla 的同类 PartEntity 和 getYawFromBuffer **故意没动**:它们的 yBodyRot 走 `Mth.approachDegrees()`(wrap-aware,值连续),不骑乘就永远不会跨这个界。

**通用教训**:这对龙骑乘时 yBodyRot 是 `= getYRot()` 硬贴的,值不连续;任何"过去的 yBodyRot 减现在的 yBodyRot"、"yBodyRotO lerp 到 yBodyRot"的写法都必须过 `Mth.wrapDegrees`。想彻底躲开也可以把硬贴换成 `Mth.approachDegrees(this.yBodyRotO, this.getYRot(), 360.0F)`(一步到位但值连续),**但别做** —— yBodyRot 会累加到 ±540 之类,而非骑乘分支的 `getYHeadRot() - yBodyRot` 是原始减法,会炸成几百度的脖子偏角,下马后要好几秒才收敛。

**动画分支要留在短路之前**(whip/roar/stomp/SPEW_FLAMES/跳劈),瞑煌龙的跳劈那段按 `jumpTarget` 用 `JUMP_TURN_SPEED` 转身体,加 `&& !jumping` 门控。

## 2026-10-06 骑乘 A/D = 转身 + 走(用户要求)

用户先要"左右移动不是移动 而是摆头",接着补"不止扭头 还要转身走"。最终方案(两文件逐条对称):

1. `getRiddenInput()`:`new Vec3(0.0D, 0.0D, forward * 0.8F * f)`,其中 `float forward = player.zza != 0.0F ? player.zza : player.xxa;` —— **A/D 不再横移,改成"转到哪就往哪走"**;只按 A 也会前进(所以走路不再必须按 W)。想退回"A/D 只转身、前进仍要 W":把 `forward` 那行换成 `player.zza`。
2. `tickRidden()` 里 A/D 直接转**骑手**的 yRot:`player.setYRot(Mth.wrapDegrees(player.getYRot() - player.xxa * RIDDEN_TURN_SPEED))`,`RIDDEN_TURN_SPEED = 4.0F`(°/tick,80°/s)。**这是刻意选的实现**:龙朝向本来就=`player.getYRot()`,转骑手 yRot 等于转龙 + 镜头跟着走,鼠标转向照旧并且与 A/D 叠加;**不需要任何自持积分器**,所以不存在客户端/服务端累加漂移(客户端每 tick 的 yRot 由 move packet 报给服务端,服务端慢 1 tick 但输入一停就收敛 —— 和原来用鼠标转向时的误差同性质)。**符号:xxa>0 是按 A 按左,左 = yaw 减小**,所以是减号。千万别改成"只转龙的 yRot"——`setRot(player.getYRot())` 每 tick 会把龙拉回镜头朝向,转不动。
3. `WALKING` 回到 `player.zza != 0 || player.xxa != 0`(A/D 现在也走)。脚步声门控仍是 `speed > 0.003F || this.entityData.get(WALKING)`(Atlatitan 的 `getControllingPassenger()` 只要有人坐着就非 null,用它会站着也踩响)。
4. **撤掉上一版的 `- player.xxa * 55.0F` 摆头项**,`getTargetNeckYRot()` 骑乘分支恢复成只留 `Mth.clamp(buffer - yBodyRot, ±40)`。原因:身体一开始转,trail 项自己就出扭脖子了 —— 而且**符号相反**(左转时 buffer 是老的、yaw 更大 → trail = +40 → 头朝高 yaw 一侧 = 拖在后面),硬加 `-xxa*55` 会和 trail 对消,净剩 -15 反而看不见。脖颈角速度 10°/tick,所以 trail 那 40° 4 tick 到位、松手 4 tick 回正。想让头**领转**而不是拖尾就把该项取反。

**`xxa/zza` 双端都有**:`ServerboundPlayerInputPacket` 带 xxa/zza,服务端直接写进 `player.xxa/zza`(猪/船也靠这个)。取 player 的方式两文件不同:Atlatitan 用 `getControllingPassenger() instanceof Player player`(骑乘时恒非 null),瞑煌龙用 `isRiddenByPlayer()` 判 + `getFirstPassenger()`(它的 `getControllingPassenger()` 是输入门控的,无输入返回 null)。

**顺带查实(别再查)**:AC 的 `LuxtructosaurusEntity`/`AtlatitanEntity` **根本没覆写** `m_274312_`(getRiddenInput)/`m_274498_`(tickRidden)/`m_245547_`(getRiddenSpeed)(SRG 名查 `~/.gradle/.../srg_to_official_1.20.1.tsrg`,反编译源在 `.scratch/decomp/ac/`),`SauropodBaseEntity:271` 的脚步声门控是 `m_20160_()`(isVehicle)。所以本仓那套 `xxa*0.35F` 是自创的 house pattern(Candicorn/Forsaken/Gammaroach/Grottoceratops/Thrasher/Tremorsaurus/Tremorzilla 全一样),**没有 AC 原版可对齐,这里怎么改都不算破坏 parity**。

AC 原版 `LookForwardsGoal`(`canUse()==true`、`tick(){ mob.setYHeadRot(mob.getYRot()); }`,优先级 10)在移植里被丢了 —— 这就是缺口的直接来源,但**不要**靠补这个 goal 修:它每 tick 硬贴 yHeadRot,而骑乘时真正卡住的是 yBodyRot。相关:[[sauropod-client-ybodyrot-local]] [[sauropod-part-oldpos-teleport-neck]]

## 2026-10-06 全坐骑审计结论(别再重查)

**判据:只有「头/脖挂在 yBodyRot 上的多部件骨架」才会中招。** 全仓唯一的脖/头 PartEntity 链就是这两只龙(`...PartEntity extends PartEntity` 只出现在 Atlatitan/Luxtructosaurus/Hullbreaker/Corrodent,后两只不是坐骑)。

骑乘分支硬赋 `yBodyRot = yHeadRot = getYRot()`(原版马 `AbstractHorse.tickRidden` 的写法)的一律安全:`Thrasher`、`Warg`/`Cerberus`(继承)、`Tusklin`、`Skelewag`、`IllagerElephant`、`Tremorsaurus`、`Forsaken`、`Gammaroach`、`Grottoceratops`;在 `travel()` 里硬赋的:`UrbhadhachServant:152`、`MutantHoglinServant:261`。`GumWormSegment` 的骑乘体是 segment(无 yBodyRot),头段在 `GumWormServant.tick()` 硬锁 yRot。

两处**故意**让头跟司机视线的(逐行对过 AC 反编译,`m_5616_(player.m_6080_())` = `setYHeadRot(player.getYHeadRot())`),属表现原版、不要"修正":`CandicornServant:567`、`TremorzillaServant:1562`(后者身体 `approachDegrees(..., 4.0F)` 也是 AC 原版 `m_14148_(f_20884_, m_146908_(), 4.0f)`)。副作用是甩镜头时头相对身体扭过去,想改要先问用户。

乘客位是抓取/投掷或驮别的怪、玩家骑不上:`WarpedMosco`、`ZombieCrocodile`、`TheObliterator`、`AnnihilationPursuer`、`BeheadedKnight`/`ResurrectedKnight`(`canAddPassenger` 只放行同主人 `IServant`)、`PossessedPaladin`、`ShulkerMimic`、`CloudGolem`、`Rambler`(是别的怪的坐骑)。

顺带确认:`tickRidden(Player,Vec3)` 是**原版** `LivingEntity` 的方法(`LivingEntity.java:2171`),由 `travelRidden()` 调用、双端都跑;`Player.serverAiStep()` 里 `yHeadRot = getYRot()`(Player.java:481),所以服务端 `player.getYHeadRot() == 骑手镜头 yaw`。
