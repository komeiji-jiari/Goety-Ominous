---
name: mc-1201-client-interp-sync-order
description: "客户端同步包在实体 tick 之前落地，所以 tick() 里读不到\"上一 tick 的同步值\"；给被覆写的 getXRot 补插值必须自存 prev/cur 一对；setupRotations 第3参是 getBob 第4参是 body yaw"
metadata: 
  node_type: memory
  type: reference
  originSessionId: 7243ed09-ddbf-45ce-8d7c-6ee99bf5eaf9
  modified: 2026-10-10T16:20:29.342Z
---

1.20.1 客户端做 partial-tick 插值时踩到的三条顺序/参数事实（2026-10-10 给蜈蚣仆从链条"丝滑化"时逐条在反编译源码里验过）：

- **同步包在主线程 task 队列里、早于实体 `tick()` 落地**。所以 `tick()` 里读 `entityData.get(...)` 拿到的**已经是本 tick 的新值**，"在 tick 末尾快照当上一帧"是无效的（prev == cur）。给一个**被覆写的** `getXRot()`（读同步字段，非 `xRot` 字段）补插值，只能自存一对：`tick()` 开头 `prevBodyXRot = bodyXRot; bodyXRot = getXRot();`，渲染用 `Mth.lerp(partial, prevBodyXRot, bodyXRot)`。
- **`yHeadRotO` 反而可以直接用**（`Mth.rotLerp(partial, entity.yHeadRotO, entity.yHeadRot)`）：`LivingEntity.baseTick()` 末尾才 `yHeadRotO = yHeadRot`（`LivingEntity.java:435`），而 `ClientboundRotateHeadPacket` → `handleRotateMob` → `lerpHeadTo(f, 3)` 只写 `lyHeadRot/lerpHeadSteps`，真正推进 `yHeadRot` 的是 `aiStep():2519-2522` 的 `lerpHeadSteps` 块 —— 在 `baseTick` **之后**。所以渲染时两端不同值，插值成立。（位置同理：`lerpTo` 只塞 `lerpX/Y/Z`，`aiStep` 才 `setPos`。）
- **`LivingEntityRenderer.render` 传给 `setupRotations` 的第 3 个参数是 `getBob()`（≈0，不是 pitch！），第 4 个是插值后的 body yaw**。所以在 `setupRotations(entity, stack, pitchIn, yawIn, partial)` 里：`pitchIn` 拿来当俯仰是错的；`yawIn` 换成 `yHeadRot` 也是错的（render 在 `setupAnim` 之后还会补一个 `headYaw - bodyYaw` 的 `mulPose`，而 `setupAnim` 的第 4 参收到的正是那个 body yaw）。要插值 head yaw 就显式写 `Mth.rotLerp(partial, entity.yHeadRotO, entity.yHeadRot)`，"同一个量、只补插值"，零语义变化。

相关：[[forge-bare-entity-update-interval-smoothness]]（发包频率那条通道）、[[forge-bare-entity-update-interval-smoothness]] 里的 `lerpTo` steps=3。
