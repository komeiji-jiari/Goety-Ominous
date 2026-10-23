---
name: mc-animatewalk-requires-looping
description: 自写 AnimationDefinition 喂 animateWalk 必须 .looping()，否则走到动画时长后腿部冻结在末帧，实体仍正常移动
metadata: 
  node_type: memory
  type: reference
  originSessionId: 25383922-fa9c-45d0-8441-152dc270fbd3
  modified: 2026-10-19T14:51:22.267Z
---

**症状**：仆从/怪物正常走位、服务端移动无异常，但动画在走了约 1.5 秒后**永久冻结在最后一帧**（看起来"卡住、没有动作"），停下再走也不恢复。

**机制**：`HierarchicalModel.animateWalk(def, limbSwing, limbSwingAmount, timeScale, maxSpeed)` 把 `limbSwing * 50 * timeScale` 当作毫秒时间传入；而 `limbSwing`（`walkAnimation.position()`）随累计行走距离**单调递增、不会归零**。`KeyframeAnimations.getElapsedSeconds` 只在 `definition.looping()` 为 true 时对长度取模，否则原样返回——超过 `withLength` 后关键帧查找夹在末帧 → 姿态永久冻结。

**判据**：Blockbench/手写的循环动画（WALK/IDLE 等）`AnimationDefinition.Builder.withLength(...)` 后必须 `.looping()`。Goety 反编译里 `.m_232282_()` 就是 `looping()`。一次性动画（TELEPORT_IN/OUT、ATTACK）不要加。

**实例**：`EndersentServantAnimations.WALK` 手写移植时漏了 `.looping()`，导致紫颂遣使跟随玩家时走一会就"卡住无动作"。同项目 `OvergrownColossusServantAnimations.WALK` 就带 `.looping()`，可对照。

与 [[mc-moveto-every-tick-yaw-whipsaw]]、`mob-turn-cap-min-radius-orbit` 是不同层面的问题：那两个影响朝向，这个只影响动画播放。
