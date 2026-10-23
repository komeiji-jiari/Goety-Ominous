---
name: goety-ac-servant-mount-jump-authority
description: "AC 仆从坐骑若覆写 isControlledByLocalInstance=isEffectiveAi 走服务端权威,空格跳跃必须把冲量放 handleStartJump;onPlayerJump 只客户端生效且会被 travelRidden 末支清零"
metadata: 
  node_type: memory
  type: project
  originSessionId: 4e90db19-3b97-46af-ac4e-62cd6619e1e0
  modified: 2026-09-06T09:57:31.046Z
---

本 repo 把 Alex's Caves 坐骑改造成 Goety 仆从时,一律覆写 `isControlledByLocalInstance() { return this.isEffectiveAi(); }`(ForsakenServant.java:217、CandicornServant.java:180 等),让坐骑**服务端权威**移动(客户端不模拟、也不发 ServerboundMoveVehiclePacket;服务端靠 ServerboundPlayerInputPacket 的 xxa/zza 驱动)。

这导致原版/AC 的"骑乘跳跃"移植有个大坑:MC 的骑乘起跳冲量只在**客户端**发生(`LocalPlayer.aiStep` 松空格→`onPlayerJump(i)`,LocalPlayer.java:765),服务端只收到空壳 `handleStartJump(i)`。客户端权威的实体(AC 原版 Candicorn、原版马**不覆写** isControlledByLocalInstance)→ 客户端躯干本地模拟,onPlayerJump 冲量真实产生位移并经 MoveVehicle 上报,能跳。服务端权威的仆从 → 客户端躯干在 `LivingEntity.travelRidden` 末支(非本地控制)`setDeltaMovement(Vec3.ZERO)` 每 tick 清零(冲量被吞、无位移),服务端躯干虽模拟却永远拿不到冲量(handleStartJump 空)→ **任何一侧都不跳**。

**修复模板(CandicornServant)**:把冲量应用挪进服务端 `handleStartJump(int i)`(原版 START_RIDING_JUMP 包已带充能值 i,服务端 `canJump() && i>0` 才回调),与 onPlayerJump 共用同一私有方法。凡实现 PlayerRideableJumping 的仆从坐骑都能直接复用它,无需自建包。

若坐骑**只实现 PlayerRideable**(不实现 PlayerRideableJumping,如 ForsakenServant),原版根本不会发 START_RIDING_JUMP → 必须自建客户端充能感测+自定义跳包服务端施力(参考 [[goety-ac-servant-keydown-channels]] 的 ForsakenRiderJumpPacket)。

客户端 canJump 取决于同步的 isLeaping(落地才清),故空中不会重复触发。快速点按 i=0 会被服务端 `i>0` 门忽略(需按住≥1 tick,同原版马手感)。
