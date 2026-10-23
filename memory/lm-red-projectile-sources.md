---
name: lm-red-projectile-sources
description: "LM 内容里的\"红\"分三种来源——圣骑士三叉戟恒红、灵魂剑二阶段红（都是原版），复生骑士标枪才本该是青白色"
metadata: 
  node_type: memory
  type: project
  originSessionId: 4192c732-5f86-4d2b-987c-dd136bc9e155
  modified: 2026-09-29T23:18:35.535Z
---

用户看到"红色的 XX"时先查下面这张表，**两个是原版、一个才是 bug**：

| 看到的东西 | 红从哪来 | 判定 |
|---|---|---|
| 堕落圣骑士的**三叉戟**（`SoulTrident`） | LM `SoulTridentRenderer` **硬编码** `posessed_paladin/layer/possessed_paladin_trident_red_layer.png`，**不看阶段** | 原版 |
| 堕落圣骑士的**灵魂剑**（`SoulBlade` / `FallingSoulBlade`） | `isRed = getPhase() >= 2`；LM `PosessedPaladinEntity:2536/2538` 一字不差同款 | 原版 |
| 复生骑士的**标枪**（`SoulJavelin`） | 应为 `resurrected_knight/javelin.png`（青白），命中只放 `GHOSTLY_SOUL` / `SOUL_EXPLOSION`，**没有柱子** | 红 = bug |

判据是**贴图**：LM 给圣骑士那一套（三叉戟 / 灵魂剑 / 幻影匕首 / 灵魂盾）每个都有 `_red` 变体，但**只有灵魂剑/匕首/盾走阶段门控**，三叉戟的渲染器没有分支。

**LM 的弹射物归属容易搞混**：`SoulTridentEntity` 是圣骑士的，复生骑士 boss 的 `shootSoulJavelin` 用的是另一个类 `SoulJavelinEntity`。历史上我们的 RS 借了圣骑士的三叉戟（连带红贴图、红灵魂柱、镜头震动），因为 `SoulJavelinEntity` 没移植过来。补的时候注意 `SoulJavelinEntity` **不治疗主人、不挂 SoulFracture**（那是三叉戟才有的），`getPickupItem()` 在 1.20.1 是抽象方法、必须实现，`onHit` 里那个 `BeheadedKnightEntity` 例外是死代码（`BlockHitResult` 与 `EntityHitResult` 互斥）。

查这类问题的入口：`.scratch/_lmver/` 是解包好的 LM，`grep -rl "<类名>" .scratch/_lmver/net` 找引用者，`node .scratch/_dec_lm_cls.mjs <fqcn>` 反编译，`ls .scratch/_lmver/assets/legendary_monsters/textures/...` 直接看贴图文件名。

相关：[[lm-servant-boss-mechanic-stripping]]、[[ac-servant-parity-keep-original]]、[[lm-mechanic-presence-version-check]]
