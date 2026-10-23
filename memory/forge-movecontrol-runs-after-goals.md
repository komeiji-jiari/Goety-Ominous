---
name: forge-movecontrol-runs-after-goals
description: "Mob.serverAiStep 里 moveControl.tick() 排在 goalSelector.tick() 之后,每 tick 抢走 yRot/deltaMovement;且 PathNavigation.stop() 不复位 MoveControl.operation,陈旧 MOVE_TO 会让身体朝旧目标、速度朝新方向=侧着飞"
metadata: 
  node_type: memory
  type: reference
  originSessionId: 0d58ac35-0bc6-43dd-a0dc-a74668734c95
  modified: 2026-09-23T22:37:35.336Z
---

`Mob.serverAiStep()`(`build/cfrvan2/net/minecraft/world/entity/Mob.java`) 的控制器顺序是写死的:

```
704  goalSelector.tick()      ← goal 里的 setYRot / setDeltaMovement
715  navigation.tick()
722  moveControl.tick()       ← MoveControl 再写一遍,最后写下的才生效
724  lookControl.tick()
```

所以**自定义 MoveControl 每 tick 都对 goal 写下的朝向/速度有最终发言权**。任何"goal 直接设 `setYRot` + `setDeltaMovement` 做定向冲刺"的写法,只要 MoveControl 还处于 `MOVE_TO` 状态,就会被它按自己的转向速率拖走 yaw(速度公式仍按 goal 算的那个 yaw,于是身体和实际移动方向分家)。除此之外 `goalSelector.tick()`(能 start 新 goal 的那个)只在**偶数 tick** 跑,但 `moveControl.tick()` 每 tick 都跑。

关键坑:`PathNavigation.stop()` 只有一行 `this.f_26496_ = null;`(`ai/navigation/PathNavigation.java:323-325`),**不会把 `MoveControl.operation` 复位**。`MoveControl.operation` 默认 `WAIT`,只有 `setWantedPosition` 会置 `MOVE_TO`;vanilla `MoveControl.tick()` 的 MOVE_TO 分支里有句自动 `operation = WAIT`,但**整段覆写 tick() 的自定义 MoveControl 通常没有这句**。于是 `moveTo` 留下的陈旧目标点会一直挂着直到 `distance < width`。修法:给自定义 MoveControl 加 `public void hold() { this.operation = Operation.WAIT; }`,在要独占控制的时刻调一次;后续 goal 再 `navigation.moveTo` 会自动重新置回 `MOVE_TO`。

副作用:hold 之后 MoveControl 的 `.scale(0.9)` 阻尼也不再施加,冲刺速度会从 `0.9×speed` 恢复到 goal 写的 `speed`。
相关:[[forge-goalselector-in-aistep-ordering]]、[[goety-command-mode-conflict]]
