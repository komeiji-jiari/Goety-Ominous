---
name: forge-mob-rider-disables-vehicle-ai
description: "Mob 骑 Mob 会让载具掉 AI:Mob.getControllingPassenger 把 Mob 乘客当操控者,updateControlFlags 关掉载具 MOVE/JUMP/LOOK(Goety Summoned 还有 TARGET)goal flag"
metadata: 
  node_type: memory
  type: project
  originSessionId: a8b15557-da59-48de-92df-dd57ae25753b
  modified: 2026-09-13T10:22:56.958Z
---

Forge 1.20.1 原版 `Mob.getControllingPassenger()` 在第一个乘客是 `Mob` 时直接返回它；`Mob.tick()` 每 5 tick 调 `updateControlFlags()`，据此把载具的 `Goal.Flag.MOVE/JUMP/LOOK` 置 false（Goety `Summoned.updateControlFlags` 额外加 `TARGET`）。`GoalSelector.tick()` 里 `disabledFlags` 会 **stop 正在跑的目标并拒绝启动新目标**，所以载具表现为不动/不转向/不索敌（"丢 AI"），乘客下车后 ≤5 tick 自动恢复。

**Why:** 用户报的"绯红蚊子仆从吸血导致乘客丢 AI"就是这个——蚊子用 `startRiding(受害者, true)` 挂上去吸血，受害者作为载具恰好被当成"被 Mob 操控"。

**How to apply:** 任何"仆从骑到别的生物身上"的机制都会触发，别只在骑乘代码里找问题。本仓库最终采用**无 mixin** 修法（用户明确要求别动 mixin）：在 `CrimsonMosquitoServant.rideTick()` 里，`this.tick()` 之后若载具是 Mob 且 `getFirstPassenger() == this`，直接 `goalSelector.setControlFlag(MOVE/LOOK/TARGET, true)` + `JUMP = !(mount.getVehicle() instanceof Boat)`（照抄原版 `updateControlFlags` 的船判定），每 tick 重开一次。

能这么修的依据 = tick 顺序：`ServerLevel.tickNonPassenger` 先 `vehicle.tick()`（goal 在其中跑完），`Mob.tick()` 才在末尾 `updateControlFlags()` 关 flag，紧接着同 tick 内 `tickPassenger` 递归跑乘客的 `rideTick()` 把 flag 开回来 → `GoalSelector` 的 `disabledFlags` 永远看不到关闭状态，不会 stop/restart 目标。前提是乘客 tick 紧跟载具 tick（原版如此）；若换成"别的实体 tick 时手动开 flag"才脆弱。别对第一乘客不是自己的情况乱开（会破坏 GammaroachServant 那类"被仆从驾驶时故意关 AI"的坐骑）。相关：[[goety-servant-riding-mechanism]]、[[goety-servant-goal-priority-flags]]
