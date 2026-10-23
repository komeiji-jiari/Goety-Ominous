---
name: ac-servant-walk-shake-camerashake
description: "巨型仆从的走路震荡统一用 Goety CameraShake(radius 20/magnitude 0.03/duration 0/fade 20,对齐红石巨兽),AC 的 screenShakeAmount 只留给踩踏/咆哮等大动作"
metadata: 
  node_type: memory
  type: feedback
  originSessionId: b268f2b3-7e88-4165-80e8-eecf09ea0925
  modified: 2026-09-14T02:48:03.464Z
---

AC 系巨型仆从(撼地斯拉、瞑煌龙)的**走路**屏幕震荡,不要用 AC `ShakesScreen`/`screenShakeAmount`,改发 Goety 的 `CameraShake.cameraShake(level, position(), 20.0F, 0.03F, 0, 20)`——与 Goety 红石巨兽 `RedstoneMonstrosity.playStepSound` 的参数逐字对齐。

**Why:** 用户 2026-09-14 明确要求"走路震荡对齐红石巨兽,因为这样抖动幅度小"。AC 那条通道是每 tick 随机偏移,且幅度会被客户端上限截到 2.0,单步就是满幅。Goety CameraShake 的 magnitude 直接乘 25 当角度用(0.03 → 最大约 0.75°),再按距离平方衰减、20 tick 二次曲线收尾,踩上去只是轻微一晃。两者数值不可直接换算,别试图把 2.0F "调小一点"糊弄过去。

**How to apply:**
- 只换走路那一处。`screenShakeAmount = 4.0F/8.0F`(踩踏、咆哮、落地)等大动作保留原样,AC 通道仍在用。
- 落足事件里原本靠 `screenShakeAmount` 当节流阀的,要另给一个 tick 冷却(如 TremorzillaServant 的 `stompSoundCooldown = 4`),否则音效会连着两 tick 各播一次;且保留 `screenShakeAmount <= 0.3F` 那道"大动作期间不播走路音"的判断。
- `CameraShake.cameraShake` 内部已判 `!world.isClientSide`,双端调用安全;它是真实体,20 tick 后 discard,红石巨兽也是每步发一个。
- Goety 侧有 `MainConfig.CameraShake` 客户端开关,玩家可整体关掉。

关联:[[ac-goety-port-probability-gates]](走路漏行那条即源于此)、[[goety-shockwave-particle-geometry]]
