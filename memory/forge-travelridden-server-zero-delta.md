---
name: forge-travelridden-server-zero-delta
description: "服务端玩家骑乘实体的 getDeltaMovement() 恒为 0,以速度做门槛的逻辑(脚步声等)骑乘时全部失效,需 isVehicle/controllingPassenger 兜底"
metadata: 
  node_type: memory
  type: reference
  originSessionId: 99bb38c5-e429-48b3-8bf3-0b019ccaa88d
  modified: 2026-10-10T14:26:53.036Z
---

1.20.1 `LivingEntity.travelRidden`(从 `LivingEntity.tick()` 的 travel 段调用,`getControllingPassenger() instanceof Player` 时才走):

```java
if (this.isControlledByLocalInstance()) { this.setSpeed(this.getRiddenSpeed(p)); this.travel(vec3); }
else { this.calculateEntityAnimation(false); this.setDeltaMovement(Vec3.ZERO); }
```

而 `Entity.isControlledByLocalInstance()` = `getControllingPassenger() instanceof Player player ? player.isLocalPlayer() : isEffectiveAi()`
→ **服务端 player.isLocalPlayer() 恒 false → 走 else 支,deltaMovement 每 tick 被清零**。

后果:任何在服务端用 `getDeltaMovement().length()` 当门槛的逻辑,只要实体被玩家骑着就恒为 0 而失效。
AC `SauropodBaseEntity.tickWalking()` 的落足音效就是靠 `speed > 0.003f || this.isVehicle()` 兜底的;
本仓库 [[goety-ac-servant-mount-jump-authority]] 记的同一条 else 支清零跳冲量是同一根因的另一个面。

移植时别丢这个 `||` 分支(擎天龙 AtlatitanServant 抄成 `|| this.getControllingPassenger() != null`,
瞑煌龙 LuxtructosaurusServant 漏了 → 骑乘时完全不播"践踏"落足音效)。

注:`tickRidden` 在 isControlledByLocalInstance 判定**之前**调用,所以服务端仍能靠它写同步 WALKING 等标志位。
注 2:`aiStep()` 是在 `super.tick()` 内部跑的(`LivingEntity.tick()` 第 2297 行),所以子类 tick 体里读到的 deltaMovement 已经是被清零后的值——写在 `!level.isClientSide` 块里的 `getDeltaMovement()` 门槛是彻底的死代码。

**2026-10-10 实例(AM 獠牙兽撞人)**:AM `EntityTusklin` 的骑乘撞人(`m_20184_().m_165925_() > 0.1`)整块在 `!isClientSide` 里,而且骑乘时近战那条也进不去(`TameableAIRide.tick()` 每 tick `setTarget(null)`)⇒ **原版骑乘时对敌人零伤害,这就是死代码,不是移植漏抄**。`TusklinServant` 已按下面这条改活:
- 不能用 `getX()-xo`/`calculateEntityAnimation` 那套原地测位移——位置同步走位置包,`Entity.absMoveTo(double,double,double)` 会把 `xo/yo/zo` 一起设成新坐标,`getX()-xo` 同样是 0。
- 可行解:自己存上一 tick 的位置(`private Vec3 lastRidePos`),本 tick 与 `position()` 求横向位移平方,阈值沿用原值 0.1(≈6.3 格/秒,0.3 移速骑乘 ≈13 格/秒);不骑乘时把缓存置 null,否则骑乘第一 tick 会拿旧坐标误判成一次巨位移。
- 位置包(`ServerboundMoveVehiclePacket`)骑乘时每 tick 都发,所以这个位移量是连续的;伤害值/击退/友军过滤原样保留,重复命中靠 `LivingEntity.hurt` 自带的 10 tick 无敌帧兜。
