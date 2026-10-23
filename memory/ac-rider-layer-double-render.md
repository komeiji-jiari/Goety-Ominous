---
name: ac-rider-layer-double-render
description: "AC 骑乘层 block/release 渲染锁会因乘客先于坐骑渲染而失效导致骑乘者双重渲染,须用 currentRenderingPassenger 门控"
metadata: 
  node_type: memory
  type: project
  originSessionId: bfca9e67-198d-4d64-9198-6c97fd751a73
  modified: 2026-09-11T12:20:40.892Z
---

AC 原版骑乘层(`AlexsCaves.PROXY.blockRenderingEntity/releaseRenderingEntity` + AC `ClientEvents.preRenderLiving` 消费 `blockedEntityRenders`)在本移植里必然失效:世界渲染通道按实体 ID 插入顺序遍历,本地玩家 ID 最小,乘客总是先于坐骑渲染,坐骑渲染层里那次 `blockRenderingEntity` 加上的锁当帧就被乘客自己的通道渲染消费掉,于是乘客被画两次 —— 座位上(通道)一次、模型骑乘点(层)一次,表现为重影。

**Why:** 锁是"一次性消费"型(命中即 `remove`),强依赖"坐骑先渲染"的帧内顺序;顺序反过来就每帧双重渲染。

**How to apply:** 给 AC 可骑乘仆从写 RiderLayer 时,不要用 `AlexsCaves.PROXY`,改用本仓库自建的 `*RenderEvents`:`preRenderLiving` 里判 `entity.getVehicle() instanceof XXXServant` → 非 `currentRenderingPassenger` 一律 cancel;层里 `setCurrentRenderingPassenger` + `try/finally clearCurrentRenderingPassenger` 放行手动渲染那一次。样板:`TremorzillaRenderEvents`、`AtlatitanRenderEvents`(两者均已注册在 `GoetyOminous.onClientSetup`)。cancel 前要补发 `RenderLivingEvent.Post`,否则其他 handler 的 Pre/Post 配平会被打破。

相关:[[ac-compat-porting-notes]]、[[goety-ac-servant-mount-jump-authority]]
