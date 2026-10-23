---
name: mc-client-deltamovement-motion-packets
description: "客户端非玩家 Mob 的 getDeltaMovement() 不是 0(速度包每 updateInterval 一次),但它是 travel 末尾乘过摩擦的存量值 ≈0.546×本 tick 位移 → 可以拿来当速率/幅度,绝不能当"这一 tick 走了多远""
metadata:
  node_type: memory
  type: reference
---

**2026-10-15 查证(`.scratch/forge_src2`):客户端非本地玩家的 Mob,`getDeltaMovement()` 既不是 0,也不等于位移。**

链路:
- `Entity.isEffectiveAi() = !level().isClientSide`(`Entity.java:2959`),`isControlledByLocalInstance()`(`2950-2957`)对非骑乘 Mob 直接返回它 → 客户端**确实不跑** `LivingEntity.travel()`(守卫在 `LivingEntity.java:2018`)。
- 但服务端一直下发速度:`ChunkMap.java:1153` 建 `TrackedEntity(entity, range, updateInterval, entitytype.trackDeltas())`,而 `EntityType.trackDeltas()` = `defaultVelocitySupplier()`(`EntityType.java:627-632`)只排除 PLAYER/LLAMA_SPIT/WITHER/BAT/ITEM_FRAME/GLOW_ITEM_FRAME/LEASH_KNOT/PAINTING/END_CRYSTAL/EVOKER_FANGS → **几乎所有怪都是 true**。
- `ServerEntity.sendChanges()`(约 171-178):`if ((trackDelta || entity.hasImpulse || isFallFlying) && tickCount > 0)`,只要 `deltaMovement` 与上次下发的 `ap` 不同(`distanceToSqr > 1e-7`)就 `broadcast(new ClientboundSetEntityMotionPacket(...))` → **每 updateInterval(怪默认 3 tick)一次**;`hurtMarked` 时另外无条件补发一次。
- 客户端 `handleSetEntityMotion` → `Entity.lerpMotion()`(`Entity.java:2096` 附近)→ `setDeltaMovement`。此后由 `LivingEntity.aiStep()`(`2505-2516`)维护:`lerpSteps > 0` 时不动它,否则每 tick `scale(0.98)`,分量 < 0.003 直接归零。

**关键:发出去的是 travel 末尾那个值,不是位移。** 地面分支是 `handleRelativeFrictionAndCalculateMovement()`(返回 `move()` 之后的 deltaMovement)**先**移动,然后 `travel` 再 `setDeltaMovement(vec35.x * f3, …, vec35.z * f3)`,`f3 = 脚下方块摩擦 × 0.91`(`2107-2149`,`2236-2237`;`Entity.move` 里那次 `× getBlockSpeedFactor()` 正常方块是 1.0 空操作)。于是客户端读到的 ≈ **0.546×本 tick 位移**(普通方块 0.6×0.91;冰面 0.89;空中 0.91),还带 3 tick 阶梯。

**用法判据:**
- ✅ 当**速率/幅度**用(正弦相位倍率、扑击俯仰 `getDeltaMovement().y*30`、指数平滑的输入)——要的是 ∝速度,摩擦系数是常数倍数,无所谓(真要跟"格"对齐就得自己补系数)。
- ❌ 当**"走了多远"**用 —— 要跟脚步声/位移对齐必须改用位置差 `sqrt((getX()-xo)²+(getZ()-zo)²)`,它跟 `Entity.move` 里喂脚步的 `moveDist` 同源,恒等于位移。

**修正旧结论:** [[forge-pose-blend-weight-invariants]] 第 21 行"`getDeltaMovement()` 会被清零/滞留、不能用来判断"与 [[mutant-hoglin-anim-fix]] 的"彻底弃用 getDeltaMovement"都过头了 —— 那些场合的真坑是**阶梯抖动**(3 tick 一阶跃 + 0.98 衰减),压不住它的是"每 tick 指数平滑",解法是**积分**(相位累加)或**二值判定**(`limbSwingAmount` 过阈);而不是"这个值永远是 0"。

相关:[[mm-sine-walk-phase-footstep-lock]]、[[mm-walkmodifiers-framerate-phase]]、[[forge-pose-blend-weight-invariants]]
