---
name: mount-key-steering-xxa-sign
description: 键控转向坐骑：xxa>0 是左键；MC yaw 增大=右转，所以左键转左必须 yaw - xxa；大象曾写成 + 导致左右反
metadata: 
  node_type: memory
  type: reference
  modified: 2026-10-27T18:55:32.964Z
  originSessionId: 07781a68-7733-4ddc-a3ff-88df715f3e7e
---

2026-10-27 实机反馈「灾厄大象左右转向反了」：`client/am/ElephantRiderInputHandler.java` 每 tick 用 `player.setYRot(yaw ± xxa * IllagerElephantServant.RIDER_TURN_SPEED)` 把 A/D 变成转向，写成 `+` 就是按 A 往右转。

- **键位**：vanilla `KeyboardInput` 里左键给 `leftImpulse = +1`，即 `xxa > 0` 代表按左；右键为负。
- **朝向**：MC yaw 0 指南（+Z）、+90 指西（−X），**yaw 增大 = 向右转**；vanilla `Boat.controlBoat()` 左键就是 `--deltaRotation`。
- **结论**：左键（xxa>0）要转左 → `yaw - xxa * speed`。本仓库既有写法可直接抄：`AtlatitanServant` / `LuxtructosaurusServant` 的 `tickRidden` 都是 `Mth.wrapDegrees(player.getYRot() - player.xxa * RIDDEN_TURN_SPEED)`，只有大象写反。

相关：[[goety-servant-riding-mechanism]]、[[ac-impulse-movecontrol-speed]]、[[forge-movecontrol-runs-after-goals]]
