---
name: mob-turn-cap-min-radius-orbit
description: MoveControl.rotlerp 限幅 = 最小转弯半径 v/ω；半径超过 PathNavigation 的 bbWidth/2 到点容差 → 路径永不推进 → 怪永久绕目标跑
metadata: 
  node_type: memory
  type: reference
  originSessionId: 179ea371-33c0-4953-bb69-2993835aa165
  modified: 2026-10-10T18:51:04.518Z
---

**症状**：AI 驱动的怪（无玩家骑乘）绕着目标画圈跑，撞击/近战判定死活触发不了，看着像"追不上"。骑乘时不会（骑乘的 yRot 来自玩家鼠标，不走 MoveControl）。

**机制**（1.20.1 原版，已核对 forge sources jar）：
- `MoveControl.tick()` MOVE_TO 分支：`setYRot(rotlerp(yRot, atan2(dz,dx)*57.3-90, 90.0F))`（`MoveControl.java:96-97`）——原版单 tick 最多 90°。自己覆写 `rotlerp` 把 `maxDelta` 压小 = 给怪加了**最小转弯半径** `r = v / ω`。
- AI 怪的前进量：`Mob.setSpeed(v)` 是 `super.setSpeed(v); setZza(v)`（`Mob.java:530-533`）→ zza 被写成 v，`moveRelative` 再乘一次 `getSpeed()`=v → **每 tick 加速度 = v²**（AI 怪速度进入两次，与骑乘输入的 |input| 不同）。地面摩擦 0.6 → 终止速度 ≈ **v² × 2.2 格/tick**。
- `PathNavigation.followThePath` 的"到点"判定：`maxDistanceToWaypoint = bbWidth > 0.75 ? bbWidth/2 : ...`（`PathNavigation.java:230`），判定 `d0 <= tol && d2 <= tol && d1 < 1.0`（`:235`）。所以**到点容差 = bbWidth/2**。

**判据**：`v² × 2.2 / (ω 弧度)` > `bbWidth/2` → 怪物理上无法把路径推进到下一节点 → `moveTo(target)` 每轮重算都指向同一个"到不了"的点 → 永久绕圈。

**实例**：灾厄战象（`IllagerElephantServant`，bbWidth 3.7 → 容差 1.85 格）。我把 `rotlerp` 限到 10°/tick（0.1745 rad）：常态速度 0.35 → 半径 1.5 格（刚好能收敛，看不出问题）；**冲刺速度 0.65 → 半径 5.3 格**，远超 1.85 容差，也远超撞击判定 `bbWidth/2+target.bbWidth/2 ≈ 2.15` 格 → 直线冲是能撞上（不需要转弯），撞完目标被击飞到侧后方、需要掉头时就开始绕圈，绕到死。2026-10-10 删掉该限幅（回原版 90°/tick → 冲刺半径 0.6 格）解决。

**经验**：限幅值不能凭手感定，先算 `v²×2.2/ω ≤ bbWidth/2`。大体积怪 bbWidth/2 反而更宽松，别以为大怪更容易绕；真正致命的是高速段（冲刺/加速）那一档。要保留限幅就用 45°（0.785 rad → 冲刺半径 1.2 格），10°~30° 在冲刺速度下都还不够（30° 只有 1.8 格，卡在容差边缘）。

和大体积怪的**视觉**猛拧是两回事：那属于 yBodyRot 1:1 跟 yRot 台阶，用 `[[mc-moveto-every-tick-yaw-whipsaw]]` 那类阻尼控制（`SmoothBodyRotationControl`）修，**别用压 yRot 限幅的办法**——那会改动真实转向能力。渲染用的是 yBodyRot，只阻尼它就能把视觉抹平。
