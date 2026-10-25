---
name: port-data-fidelity-rule
description: 最高优先级·移植数据铁律：一切注册/数值数据必须与 1.21 原版或 Goety-3 逐字段一致，禁止凭记忆捏造；1.20.1 缺 API 时用等价手段复现同一数值
metadata: 
  node_type: memory
  type: feedback
  originSessionId: 2692f880-0ad0-4ed1-a2d2-664239adcf3c
---

**规则（2026-10-03 用户明确要求，属 Windy 移植长期约束）**：
1. **移植内容的注册数据必须与原版/上游一致**：实体尺寸（sized）、eyeHeight、MobCategory、clientTrackingRange、updateInterval、属性数值、音效/粒子参数、配置默认值……**逐字段照抄**，写之前先反编译/读源码核对，**绝不凭记忆或"大概"捏造**。
2. **1.20.1 缺对应 API 时**：用等价手段实现**同一个数值/行为**（例：`EntityType.Builder.eyeHeight(0.0F)` 是 1.20.5+ 才有 → 1.20.1 改为在实体里覆写 `getEyeHeight(Pose, EntityDimensions)` 返回 0.0F），**不许降级成近似值或干脆不设**。
3. 每组新移植的注册数据，交付前自查一遍"和原版/Goety-3 对账"（可写进 [[windy-update-port-plan]] 的差异表）。

**Why**：用户不想每次都提醒；捏造数值会造成静默的行为差异（本次实例：SERVANT_WIND_CHARGE 被写成 `sized(0.5F, 0.5F)`，而 1.21 原版与 Goety-3 都是 `0.3125 × 0.3125` + `eyeHeight 0.0F`；0.5 的碰撞箱大了 60%，直击判定明显不同）。

**How to apply**：写任何 EntityType/物品/音效/粒子/属性注册或常量前，先查 `E:\java\Goety-3-main`（上游）与 `libs/1.21.1.jar`+CFR（原版，工具在 `E:\java\tools\cfr-0.152.jar`，映射在 `tools/mappings-1.21.1.txt`）；对不上就先核对再写。参考 [[windy-port-1to1-principle]]。
