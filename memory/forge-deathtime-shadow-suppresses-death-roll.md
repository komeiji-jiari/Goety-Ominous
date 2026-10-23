---
name: forge-deathtime-shadow-suppresses-death-roll
description: "LM 实体自带的 public int deathTime 会遮蔽 LivingEntity.deathTime,用于关掉原版渲染器 90° 倒地旋转;移植时漏掉这个字段,自绘死亡动画就会被叠上原版侧翻"
metadata: 
  node_type: memory
  type: reference
  originSessionId: 83bbe004-3fb8-4621-8087-e537b77dee48
  modified: 2026-09-26T18:47:32.461Z
---

原版 `LivingEntityRenderer.setupRotations`（Forge 源码 188 行）读的是 **`LivingEntity.deathTime`** 这个字段，`> 0` 就把模型绕 Z 轴翻 `sqrt((deathTime+tick-1)/20*1.6) * getFlipDegrees(=90°)`——这就是"僵尸直挺挺侧翻倒地"的来源。

LM（传奇怪物）的 boss 实体普遍在类里另声明一个同名字段：

```java
public int deathTime;
```

Java 字段遮蔽：实体**自己**的 `m_6153_()` 里 `++this.deathTime` 加的是新字段，而渲染器通过 `LivingEntity` 静态类型取字段，读到的永远是那个**没人动过、恒为 0** 的原版字段 → **原版侧翻被整个关掉**，死亡演出完全交给它自己的关键帧动画（如 Obliterator 的 `death5`，102 tick）。

**移植判定表（已核对 2026-09-26）：**

| LM 原版 | 有该字段 | 仓库仆从 |
|---|---|---|
| `TheObliteratorEntity:351` | ✅ | 曾经漏（已补） |
| `PossessedPaladinEntity:290` | ✅ | 漏字段，但 `tickDeath()` 每 tick `deathTime = 0` 硬压回去 + 自用 `deathTicks` 计时，等效 |
| `AnnihilationPursuerEntity:192` | ✅ | ✅ 照抄 |
| `Shulker_MimicEntity` | ✅ | 漏字段，但 `tickDeath()` 只加自用 `mimicDeathTime`，原版字段恒 0，等效 |
| `FlameDrifterEntity` | ✅ | ✅ 照抄 |
| `Cloud_GolemEntity` / `Overgrown_colossusEntity` / `HoveringHurricaneEntity` / `AbstractFlameborn` | ❌ | ❌ 一致（原版也侧翻） |

**判定捷径**：仆从同时满足 ①原版 LM 有该字段 ②仆从 `tickDeath()` 里 `++this.deathTime` 且没声明自己的字段 —— 就会多出一层原版侧翻。两个条件缺一个都不会出问题（不声明字段又不自增 = 恒 0；声明了字段 = 影子生效）。

**Why:** 症状是"死亡动画像假死演出、和原版不一样"，很容易误判成动画绑错或 AnimationState 没启动，实际是动画数据一模一样、被叠了一层原版旋转。
**How to apply:** 移植任何带自绘死亡动画的 LM/AC 实体，先比对 LM 原版有没有 `public int deathTime;`；有就照抄一行，别用 `= 0` 覆盖法（每 tick 赋值只是碰巧等效）。相关 [[lm-servant-tier-from-class-superclass]]、[[mob-tickheadturn-bodyrot-snap]]。
