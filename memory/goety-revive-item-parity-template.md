---
name: goety-revive-item-parity-template
description: Goety 转化物品(复活物)的三条模板约定：结尾 return super、冷却门只有全局事件管得住、宿主判定三段式
metadata: 
  node_type: memory
  type: reference
  originSessionId: efc58a99-0e1e-4fe0-98b4-63bb2d46c575
  modified: 2026-10-16T17:44:57.112Z
---

对齐 Goety 原生转化物品（`ReviveServantItem`/`SoulJar`/`HowlingSoul`）时固定的三条：

1. **结尾统一 `return super.interactLivingEntity(stack, player, target, hand)`**（= `Item` 的 PASS），成功也照样 PASS，绝不返回 SUCCESS/CONSUME/FAIL。所有服务端工作写成嵌套 `if`（`addFreshEntity` 返回 true 才算成功），失败自然落到同一句 super。
2. **冷却门**：原版 `ItemCooldowns` 只拦 `useItem`/`useItemOn`（方块/物品），`ServerboundInteractPacket → player.interactOn` 路径**没有**冷却检查。真正生效的只有 Goety 的全局事件 `ItemEvents.InteractEntityEvents`（只认 `ReviveServantItem` 子类）。所以 `extends Item` 的自定义转化物（ColdHeartItem、WarpedSteroidsItem）必须自己写 `SEHelper.isOnCooldown` 门；`extends ReviveServantItem` 的（灭结催化剂/飓风核心/僵尸复活物/两个魂罐）不要写，是冗余。返还侧冷却走 `ItemConfig.ReviveSecondsCool`（默认 90s），且只对两个死灵法师用 `addSpecificCooldown`（Goety `AbstractNecromancer.soulJar()` 原样），转化侧是硬编码 5 秒。
3. **宿主判定三段式**：`仆从 || 野生怪 || target.getType().is(<convert tag>)`（如 Goety `WitherSkeletonServant || WitherSkeleton || WITHER_CONVERT`）。没有野生对应体的（Mossy、我们的 Sunken）就只留 `仆从 || tag`。tag 是自己 data 下的 `convert/xxx.json`，`replace:false` + `required:false`，默认值把仆从和野生体都列上（Goety 的 `necromancer/regular_convert` 也这么写）。

音效/粒子也照模板：`spawnAnim()` → 8×(`SCULK_SOUL`+`POOF`) `addParticlesAroundSelf` → `GENERIC_EXPLODE 1.0/0.5` → 生物自己的 voice `2.0/0.5` → `target.discard()` → `swing` → 冷却 → `shrink(1)`（Goety 不查 instabuild）。

**但"对齐"只动约定，不动已写好的写法**：用户 2026-10-16 明确退了两次——灭结催化剂的返回值/重复音效/`spawnAnim` 和飓风核心的返回值/`GENERIC_EXPLODE` 都要求**恢复原样**（原话"这是刻意的"/"这个不改 撤回"）。所以对这类手写转化物：**只同步冷却（`ItemConfig.ReviveSecondsCool`）、宿主判定和 tag**；返回值(SUCCESS/CONSUME/FAIL)、音效层数、粒子种类一律保持仓库现有写法，别按 Goety 模板重构。

**Why:** 这几条是"表现像本家、实现跟 Goety"落地时的实际差异点，我按字面移植时踩过（重复音效、缺 spawnAnim、多余冷却门、FAIL/SUCCESS 收尾）；而按模板重写用户手写物品的写法被驳回，那些差异是有意的。
**How to apply:** 新写转化物品可照模板；改已有物品只碰冷却/宿主/tag，其余原样。相关：[[mm-zombie-servant-death-drop-hook]]、[[goety-multilife-servant-dismiss-conflict]]、[[ac-servant-parity-keep-original]]。
