---
name: sauropod-client-ybodyrot-local
description: "蜥脚类仆从(瞑煌龙/擎天龙)的 yBodyRot 客户端本地算:客户端无 turningFast 也无 jumpTarget,要让身体转向必须走 setYRot 而非直接改 yBodyRot"
metadata: 
  node_type: memory
  type: project
  originSessionId: d1c93426-cb94-4686-8895-427df6b5a15a
  modified: 2026-09-14T01:42:23.708Z
---

`LuxtructosaurusServant.tick()` 里那段
`yBodyRot = Mth.approachDegrees(yBodyRotO, getYRot(), turningFast ? 10 : 2)` 是**双端各算各的**,而:

- `turningFast` 只在 `LuxtructosaurusServantMeleeGoal.tick()`(goal,服务端专属)里赋值 ⇒ 客户端恒为 false ⇒ 客户端身体永远 2°/tick,服务端 kick/whip 期间算 10°/tick 白算。
- `jumpTarget` 是裸 public 字段(无 entityData)⇒ 客户端恒 null。
- 客户端拿到的只有 `getYRot()`(经 `lerpTo` 每步硬赋 `setYRot(lyr)` 同步)、`getAnimation()/getAnimationTick()`(Citadel 同步)和 `turningFast` 之外的双端共享状态。

**How to apply:** 想让身体朝向某方向(例:2026-09-14 瞑煌龙"面朝目标跳"),服务端用 `setYRot(Mth.approachDegrees(getYRot(), yaw, 速率))` 转 yRot,再把 `yBodyRot` 的 approach 速率按动画调快 —— 两边都只读 yRot,自动同步。直接 `yBodyRot = x` 或只改 `turningFast` 客户端不跟随。身体 yaw 同时喂给 `MobRenderer` 的 entityYaw 与 `tickMultipart()` 的部件摆位,所以两者一致地转。

另外:跳劈的跃迁冲量在 `tickLuxtructosaurus()` 服务端分支按 `jumpTarget` 施加(anim tick 15~25),而"跳"的触发条件是 `canReach(target) == false` —— 此时 navigation 的 moveTo 恰好失败 ⇒ MoveControl 不会把 yRot 转向目标,所以跳跃前身体停在原朝向,看着是横着跳的。

相关: [[sauropod-part-oldpos-teleport-neck]] [[forge-goalselector-in-aistep-ordering]] [[citadel-advancedpathnavigate-moveto-always-true]]
