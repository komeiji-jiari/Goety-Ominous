---
name: goety-tesseract-eject-forces-follow
description: 奥术方匣取出仆从时无条件 setFollowing();要保留原姿态就覆写 setFollowing + 读档后一次性放过
metadata: 
  node_type: memory
  type: project
  originSessionId: e6112b54-7831-4204-8858-4e13afce37db
  modified: 2026-10-03T00:46:30.901Z
---

Goety 的 `EsotericTesseract.ejectSingleServant` / `ejectAllServants` 取出仆从时，先 `servant.readAdditionalSaveData(servantTag)`（这一步**已经**把 `wandering`/`staying`/`boundPos`/`boundDim`/`commandPos` 都还原了，见 `IServant.saveServantData/readServantData`），紧接着又无条件调 `servant.setFollowing()`——姿态被这一句冲掉，等于取出即变跟随。

保留原姿态的做法（沙虫 `GumWormServant` 已用）：`readAdditionalSaveData` 里置一个 `keepLoadedStance = true`，覆写 `setFollowing()` 时若该标志为真就直接 return（否则照抄 IServant 默认的三行：`setBoundPos(null)` / `setWandering(false)` / `setStaying(false)`），再在 `tick()` 里（`super.tick()` 之后）清零。

- 标志只能是**一次性**的：正常读档也会走 `readAdditionalSaveData`，长期挂着会把 CallFocus / CommandHorn / TroopFocus 那些合法的 `setFollowing()` 一起吃掉。取出流程是 `readAdditionalSaveData → addFreshEntity → setFollowing()` 同步连着跑，实体首个 tick 在这之后，所以"读档置位、首个 tick 清位"刚好只覆盖取出那一次。
- 那几个正常调用方都在世界实体上直接动手，路径里没有 `readAdditionalSaveData`，不会误伤。

**Why:** 用户明确要求"口香糖蠕虫装进方匣再拿出来不要变成跟随，保持之前的待命/警戒/游荡"。

**How to apply:** 任何要在方匣里保姿态的仆从都照这个套路；只要 `extends Summoned` 就能覆写 `setFollowing()`（全 Goety 只有 `IServant:217` 定义它，`Summoned`/`Owned` 都没覆写，所以不用调 super）。

相关：[[goety-tesseract-capacity-tags]]、[[goety-servant-follow-standby]]
