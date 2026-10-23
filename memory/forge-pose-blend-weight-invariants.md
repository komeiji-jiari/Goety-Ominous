---
name: forge-pose-blend-weight-invariants
description: 用 KeyframeAnimations.animate 的 scale 参数做姿势交叉淡入淡出的四条硬约束(权重和恒为1/互补初始化/单一步长/状态存活判定)
metadata: 
  node_type: memory
  type: reference
  originSessionId: 5cfa8bcb-6412-47e3-a5df-88906c215210
  modified: 2026-10-15T22:32:27.888Z
---

`net.minecraft.client.animation.KeyframeAnimations.animate(model, def, accumulatedTime, scale, cache)` 是 public static,第 4 个参数 `scale` 会乘在通道增量上(`Interpolations.LINEAR` 内 `lerp(...).mul(scale)`),而 `ModelPart.offsetPos/offsetRotation/offsetScale` 全是 `this.x += vec.x()` 的加法 → 这个 scale 就是真正的**线性混合权重**,可用于自己做交叉淡入淡出(原版 `HierarchicalModel.animate` 永远传 1.0F,只能硬切)。仓库里的实现见 `client/render/model/animation/PoseBlend.java`(自带私有 Vector3f 缓存,因为 `HierarchicalModel.ANIMATION_VECTOR_CACHE` 是 private static)。

四条必须同时满足的约束,少一条就会看到姿势塌陷或弹跳:

1. **任一渲染帧上所有权重之和 = 1**。`ModelPart::resetPose` 之后每个姿势都是增量,权重和 < 1 就会向静止姿势塌陷。
2. **过渡用互补初始化**:落地那一刻 `landingWeight = 1.0F - jumpWeight`(并把 `landingWeightOld` 一起设成同值,否则该帧两端权重不等、和不为 1),之后两者朝 (0, 1) 各自爬升。
3. **所有方向用同一个步长**(`1.0F / POSE_BLEND_TICKS`)。上升与下降速率不同时,跳跃+落地权重会短暂 > 1(姿势过冲)或 < 1(塌陷)。
4. **状态存活判定要用 `max(new, newOld)`**(gait 侧用 `max(gaitWeight, gaitWeightOld) > 0`),因为渲染帧的权重是 `Mth.lerp(partial, old, new)`,只在 new 上判定会让权重归零的那一帧少一个姿势贡献。
5. `setupAnim` 拿不到 partialTicks,但 `LivingEntityRenderer.getBob` = `tickCount + partialTicks` → `ageInTicks - Mth.floor(ageInTicks)` 精确还原;实体侧 getter 统一收 `ageInTicks`。

实体侧配套:客户端非玩家操控的 Mob **不跑 `travel`**(`isControlledByLocalInstance` = `isEffectiveAi` = `!level.isClientSide`),所以 `getDeltaMovement()` 在这边**不是本 tick 位移**;改用 `getY() > this.yo`(yo 在 baseTick 里更新,位置来自包插值,符号可靠),在头 3 个空中 tick 里锁存。
**修正(2026-10-15 查证):** 这里原来写的"会被清零/滞留"过头了 —— 非玩家 Mob 的速度包**一直在发**(`EntityType.trackDeltas()` 只排除 PLAYER/WITHER 等少数几个,`ServerEntity.sendChanges` 每 updateInterval 广播一次),所以 `getDeltaMovement()` 有值、只是**每 ~3 tick 阶跃一次且乘过方块摩擦(普通地面 ≈0.546×位移)、还在 `aiStep` 里每 tick ×0.98 衰减**;它当"∝速度的幅度"能用,当"走了多远/每 tick 增量"不能用(位置差才是同一量)。详见 [[mc-client-deltamovement-motion-packets]]。

相关:[[forge-pose-transition-from-pose]]、[[forge-goalselector-in-aistep-ordering]]
