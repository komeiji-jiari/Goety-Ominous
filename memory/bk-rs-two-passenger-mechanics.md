---
name: bk-rs-two-passenger-mechanics
description: "无头骑士/复生骑士有两套独立的乘客机制,用户口中的\"骑乘\"专指前者;2026-09-30 前者已删"
metadata: 
  node_type: memory
  type: project
  originSessionId: 14d3f3b9-0b38-4179-93de-762d32787889
  modified: 2026-09-30T01:38:20.899Z
---

BK(BeheadedKnightServant) 和 RS(ResurrectedKnightServant) 之间**有两套互不相干的乘客机制**,都靠 `passenger` 实现,极易混淆:

1. **骑乘/坐骑层** —— 用户说「骑乘机制」时指的就是这套。RS 空闲(attackState==0)且离 BK < 2.5 格时自动 `startRiding(mount, true)` 爬上 BK 的背,用 `boundMountUUID` / BK 侧 `boundPartnerUUID` 双向绑定(NBT 键 `bound_mount` / `bound_partner`),并由 `BK.canBeRidden` 把骑手限定成 RS(玩家骑不了)。**2026-09-30 已整套删除**(RS 侧 46 行、BK 侧 36 行),`canBeRidden` 删掉后走 `IServant.canBeRidden` 默认 false,谁都不能骑 BK。
2. **抓取投掷** —— `SyncedKnightGrab()` 抓住自己的 duo 搭档(synchronisedDuoKnight,也就是那只 RS)塞成乘客,`grab` 标记为真,状态机走到 attackState 12,在 attackTicks==57 时用 `throwAnGravityEntity()` → `ThrownKnight` 把 RS 当投掷物甩向目标。配套 `BHGrabAndThrowGoal`(抓失败是状态 13)、`BeheadedKnightServantGrabLayer` 渲染乘客。**这套没删,是用户明确要留的。**

第 1 套删掉后,第 2 套照旧:`canAddPassenger`(放行同主人的 IServant)、`shouldRiderSit()=false`、`getControllingPassenger()=null`、`updateControlFlags` 的乘客分支、`shouldAttack && !isPassenger()` 都是为第 2 套服务的,**别当成骑乘残留删掉**。

`detectDuoFight()` 两侧各有一个 `boundMount()/boundPartner()` 快路径 + 一个范围扫描兜底;快路径随第 1 套删掉了,duo 识别改走范围扫描,功能不受影响。
