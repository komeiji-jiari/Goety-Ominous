---
name: forge-entity-sized-vs-getdimensions
description: "非生物实体服务端受击盒只来自 EntityType .sized;getDimensions 覆写在服务端不生效直到 refreshDimensions,大 AoE 投射物必须把 .sized 注册成实际体型"
metadata: 
  node_type: memory
  type: project
  originSessionId: 6eae68de-0c72-4d6b-82d7-7c3eccffa2d8
  modified: 2026-09-08T07:56:05.010Z
---

Entity 的 `getBoundingBox()` 由存储字段 `Entity.dimensions` 生成(setPos/move 里 `dimensions.makeBoundingBox()`),**不是** `getDimensions(Pose)` 覆写。覆写值只有等 `refreshDimensions()` 被调用后才写入字段;refreshDimensions 靠 entityData 值变更触发,服务端构造器 `defineSynchedData()` 里的初始 define 不触发,只有客户端收 spawn 包灌数据时才触发。

后果:地面大法阵型 AoE 投射物(LicowitchServantHex)即使覆写了 `getDimensions` 返回 `scalable(4,0.25)`,只要 EntityType 注册 `.sized(0.1F,0.1F)`,**服务端 hurt 盒始终 0.1 宽** → `getEntitiesOfClass(getBoundingBox())` 几乎碰不到站立的怪 → 「法阵没伤害」;客户端却照常渲染大贴图,视觉一切正常,极难排查。

修复:注册 `.sized(4.0F,0.25F)`,直接镜像 AC `SugarStaffHexEntity` 的注册体型(与 getDimensions 覆写一致)。[[ac-compat-porting-notes]] [[forge-particle-unbound-spriteset]]

**Why:** 服务端判伤盒只看注册体型;getDimensions 覆写只对客户端/refresh 后生效。

**How to apply:** 凡带 getDimensions 覆写或靠体型判伤的实体,AcEntityRegistry 的 `.sized` 必须等于实际体型,别用小尺寸「反正会被覆写」。实体真正要按 data 位缩放时,保留 getDimensions + onSyncedDataUpdated→refreshDimensions。
