---
name: ac-synthetic-renderpost-head-zeroing
description: "取消 Pre 后手动补发合成 RenderLivingEvent.Post 会触发 AC 的 resetMagnetHeadRotation 清零骑乘者头部;第三人称镜头抖动的真凶"
metadata: 
  node_type: memory
  type: project
  originSessionId: 18a34da4-519e-4f89-9f63-5b0f6f0ae0d3
  modified: 2026-09-30T15:51:49.845Z
---

AC 的 `LivingEntityMixin` 给每个 `LivingEntity` 挂 `prevHeadYaw/prevHeadYaw0/prevHeadPitch/prevHeadPitch0`,由 `HeadRotationEntityAccessor`(`com.github.alexmodguy.alexscaves.server.entity.util`) 的 `setMagnetHeadRotation()`(保存) / `resetMagnetHeadRotation()`(还原 yHeadRot/yHeadRotO/xRot/xRotO) 成对使用。

AC 的 `ClientEvents.preRenderLiving` 第一件事就是无条件 `setMagnetHeadRotation()`;`postRenderLiving` 第一件事是 `resetMagnetHeadRotation()`。

移植 AC 那套"blockedEntityRenders + 取消 Pre + 手动补发合成 `RenderLivingEvent.Post`"时,若漏掉 pre 里的保存行,合成 Post 就会让 AC 拿**从未保存过的全 0 字段**去还原 → 每帧把骑乘者 `yHeadRot/yHeadRotO/xRot/xRotO` 清零。相机走 `LivingEntity.getViewYRot` = `Mth.lerp(pt, yHeadRotO, yHeadRot)`(**不是 rotLerp**),于是第三人称 yaw 在 0 ↔ yRot 之间来回跳、pitch 被拉回 0。

**Why:** 只有第三人称会现形 —— 这些 handler 都有 `isFirstPersonPlayer` 门控,第一人称既不取消也不发合成 Post,清零链断开。另外我们的 handler 一定排在 AC 前面:`AlexsCaves.clientSetup` 用 `event.enqueueWork` 延迟注册 `ClientEvents`,而我们 `GoetyOminous.onClientSetup` 是立即注册,Forge 同优先级保持注册顺序 —— 所以 AC 的保存来不及挡。

**How to apply:** 补回这行即可(顺序无关,先存后还原净效果为零):

```java
LivingEntity entity = event.getEntity();
if (entity instanceof HeadRotationEntityAccessor magnetic) {
    magnetic.setMagnetHeadRotation();
}
```

已修 `LuxtructosaurusRenderEvents` / `AtlatitanRenderEvents` / `TremorzillaRenderEvents`。**同样漏行的还有 `PossessedPaladinGrabRenderEvents`、`BeheadedKnightGrabRenderEvents`(LM 抓取,未修:加 AC 接口会给无 AC 依赖的类引入硬引用)**。相关:[[ac-goety-port-probability-gates]]、[[ac-compat-porting-notes]]、[[ac-servant-parity-keep-original]]
