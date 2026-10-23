---
name: mm-servant-death-red-overlay
description: 三僵尸假死复活后持续通红——原版 getOverlayCoords 看 deathTime，改为客户端 tick 里复活时清零
metadata:
  node_type: memory
  type: project
  originSessionId: c6e12552-53c3-420f-9575-5bd05d7000b8
  modified: 2026-10-19T21:27:48.892Z
---

原版 1.20.1 `LivingEntityRenderer.getOverlayCoords` 是 `hurtTime > 0 || deathTime > 0`；`deathTime` 客户端只在 `isDeadOrDying()` 时自增，服务端先复活、血量同步先到，客户端停止自增但停在阈值以下不归零，于是假死复活后一直红到下一次假死。MM 自带 `LivingEntityRendererMixin` 只判 `AbstractMutant`，Goety 侧吃不到。

**Why:** 移植假死三僵尸（尸壳/丛林僵尸/冻僵）时只搬了实体逻辑，没搬这条渲染豁免。

**How to apply:** 不再用 mixin。在 `AbstractMutantServant.tick()` 客户端分支里，`!isDeadOrDying()` 时把 `deathTime` 置 0 即可（`prepareRevived()` 只在服务端跑，管不到客户端）。原 `mm/MixinLivingEntityRenderer` 已删、mixins.json 条目已移除，`require = 1` 那套注意事项作废。注意区别：倒地动画期间血量仍为 0，仍会短暂泛红；要完全像 MM 一样全程不红才需要 mixin。相关：[[mm-zombie-servant-down-yaw-freeze]]、[[mm-jungle-zombie-servant-port-decisions]]。
