---
name: goety-summonspawn-owner-order
description: "Goety 非玩家召唤者召仆从必须 先 setTrueOwner 再 finalizeSpawn(MOB_SUMMONED),否则仆从被标 wandering 且无召唤粒子/升级"
metadata: 
  node_type: memory
  type: project
  originSessionId: 6eae68de-0c72-4d6b-82d7-7c3eccffa2d8
  modified: 2026-09-08T07:56:09.783Z
---

Goety 召唤者在服务端召 Owned/Summoned 仆从时,顺序必须是「`setTrueOwner(召唤者)` → (lifespan/persistence) → `finalizeSpawn(MOB_SUMMONED)` → `addFreshEntity`」,参考 `AbstractCairnNecromancer`。

若反过来先 `finalizeSpawn` 再 `setTrueOwner`:`Summoned.finalizeSpawn` 内部读 `getTrueOwner()==null` → `setWandering(true)`、跳过 summonParticles、跳过 spawnUpgraded,主人后设也不会重置这些——糖果变成「野生」样、无召唤粒子,召唤表现不「正常」。

模板见 LicowitchServant.spawnCandyMob(2026-09 修正)。[[ac-compat-porting-notes]] [[goety-servant-follow-standby]]

**Why:** finalizeSpawn 用 getTrueOwner() 决定 wandering/粒子/升级,owner 是 finalize 时机敏感状态。

**How to apply:** 仿 Goety Necromancer:owner 先设,再 finalize,最后 addFreshEntity;EntityFinder 能按 UUID 解析非玩家 mob owner(遍历所有 ServerLevel.getEntity),mob 作 owner 的链路服务端没问题。
