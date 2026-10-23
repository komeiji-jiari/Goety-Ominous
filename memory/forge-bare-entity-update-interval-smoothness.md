---
name: forge-bare-entity-update-interval-smoothness
description: "裸 Entity 的视觉流畅度全靠位置包频率;ServerEntity 的门是 tickCount%updateInterval==0 || hasImpulse || entityData.isDirty(),删掉每 tick 置脏的同步字段会让包降到每 3 tick"
metadata: 
  node_type: memory
  type: reference
  originSessionId: 0d58ac35-0bc6-43dd-a0dc-a74668734c95
  modified: 2026-10-10T16:20:36.902Z
---

`ServerEntity.sendChanges()` 的外层门是 `tickCount % updateInterval == 0 || entity.hasImpulse || entity.getEntityData().isDirty()`（`build/cfrvan2/net/minecraft/server/level/ServerEntity.java:112` 一行）。而客户端 `ClientPacketListener` 处理 `ClientboundMoveEntityPacket`/`ClientboundTeleportEntityPacket` 时调的 `lerpTo(..., steps, ...)` **steps 硬编码 3**（同文件 570/597/601 行）。所以：

- 位置包**每 tick** 到 → 客户端每 tick 都有新目标、3 tick 插值追 → 连续平滑
- 位置包**每 3 tick** 到 → 目标刚好在插值走完那一刻更新 → 速度锯齿 = "卡卡的"

`Entity.setPos` / `setDeltaMovement` **不置** `hasImpulse`（只有 `Entity.push`、`LivingEntity.knockback/jumpFromGround/startSleeping` 置），所以裸 `Entity` 若没有"每 tick 都会变的同步字段"，就只剩 `tickCount % updateInterval` 这一条路，默认 `updateInterval = 3`。

结论：给裸 `Entity` 做"多部件跟随"时，**每 tick 写一个会变的 synched 字段（如 OF `SkyvernSegment` 的 `TARGET_POS`）不是冗余，它是把位置包提到每 tick 的机制**。要复刻同样效果又不想多发包，直接把 EntityType 设成 `.setUpdateInterval(1)`（静止时不会发包，因为 `$$15` 要求位置真的变了）。**“Mob 有本地预测所以不受影响”是错的**：链条式多部件（如蜈蚣 `ServantCentipedeBody`，`Summoned` 但 `isControlledByLocalInstance()` 为 false、每 tick 强行 `setDeltaMovement(Vec3.ZERO)`）既不 `travel` 也没人给它发 velocity，位置包同样是它唯一的运动来源。而且这类部件往往每 tick 改 `BODY_XROT` 之类的同步字段 → `isDirty()` 让它**永远每 tick 发包**，`setUpdateInterval` 根本管不住它。于是同一视觉链条里若头是默认 `updateInterval 3`、节段是每 tick，两边的稳态滞后不同（0~3 tick vs 恒 3 tick 的位移）→ 肉眼看到的就是“头在链条里前后喘气、随速度被拉长”。解法是统一到 `.setUpdateInterval(1)`（2026-10-10 蜈蚣仆从），代价是整体恒定滞后 3 tick 位移。相关:[[forge-entity-sized-vs-getdimensions]]、[[goety-multipart-part-projectile-crash]]、[[mc-1201-client-interp-sync-order]]
