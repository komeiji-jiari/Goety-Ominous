---
name: windy-port-1to1-principle
description: "最高优先级·移植原则：Goety-3(Windy Update) → 1.20.1 必须 1:1 复刻 1.21 的签名与方法，禁止\"简化/近似/砍功能\"式妥协"
metadata: 
  node_type: memory
  type: feedback
  originSessionId: 2692f880-0ad0-4ed1-a2d2-664239adcf3c
---

**规则（2026-10-03 用户明确要求）**：Windy Update 移植（Breeze/Hurricane/Whirling Cage/Wind Shrine）**必须 1:1**：
- 尽可能**完全复刻 1.21 原版的类签名、方法签名、常量值、数值参数**，即使 1.20.1 里没有对应 API，也要用自研件**逐条等价实现**，不许因为"1.20.1 不方便"就省略功能或改数值。
- 缺的原版件（LongJumpUtil / AbstractWindCharge / SimpleExplosionDamageCalculator / ProjectileDeflection / Pose 新枚举 …）**按原版逐行为准**，用反编译器读源码级实现后再写。
- 参数/常量一律照抄（例：JUMP_SCALE=0.25、风弹半径 3.0、伤害 1.0、hitbox 下沉 0.15、法线外移 0.25、`getBlockY() > getMinBuildHeight()+30` 自爆 等）。
- 只在**无法避免的 API 差异**处做映射（如 `damageSources().windCharge()` → `goety:wind_blast`、1.21 粒子 → Goety 2.x 风粒子、`Pose` 新枚举 → 自建 synced 状态），并明确记录差异清单，不许静默丢弃。

**Why**：玩家的目的是"功能不缺失"；用户不接受为了省事而砍内容，宁可多写适配层。

**How to apply**：写任何 Windy 系新类前，先反编译 1.21 原件（CFR 在 `E:\java\tools\cfr-0.152.jar`，用法 `java -jar tools/cfr-0.152.jar libs/1.21.1.jar <混淆类名>`，输出到 stdout 或 `E:\java\decompiled-1.21.1\`），逐方法核对；差异点写进 [[windy-update-port-plan]] 的差异清单。

相关：[[windy-update-port-plan]]、[[cuotiben-lm-port]]（那边是"别人有公开 API 别抄"，这边相反：原版没有 API 就要自研等价件）
