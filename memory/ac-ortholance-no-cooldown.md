---
name: ac-ortholance-no-cooldown
description: "AC 螺纹长枪没有冷却，只有\"按住满 10 刻\"这一条；AC 的 DeepOneKnightEntity 根本不用它"
metadata: 
  node_type: memory
  type: reference
  originSessionId: 8056a397-37f6-44dd-89a5-71c6e4a64947
  modified: 2026-10-08T08:58:17.943Z
---

AC 2.0.2 的 `OrtholanceItem` **没有任何冷却**：整个类的常量池里连 `cooldown` 字符串都不存在，没有 `ItemCooldowns` 调用。玩家侧唯一的门槛在 `releaseUsing`：

```java
int i = Mth.clamp(getUseDuration(stack) - useTime, 0, 60);  // = 按住刻数，上限 60
if (i > 0) { ... if (i >= 10 && !level.isClientSide) { /* 放波 */ } }
```

- 必须按住 ≥10 刻才放得出 → 两轮之间的最短间隔就是 10 刻，场上没有额外锁定。
- 按住越久波越多：`maxWaves = i / 5`（i=60 时每侧 12 组）。`TSUNAMI` 附魔覆盖成 5、单发巨浪；`SECOND_WAVE` 追加延迟二段；`FLINGING` 只影响速度。**没有任何附魔影响冷却/蓄力时间**，所以"无附魔"这个限定对冷却无意义。
- 自己后坐：`setDeltaMovement(vec3.add(0, onGround ? 0.2 : 0, 0))` —— 只是个小前跳，不是伤害冲刺。

另：AC 的 `DeepOneKnightEntity` 压根不引用螺纹长枪（只用三叉戟），全树只有 model/renderer/registry/enchant 提到它。所以本仓库那个"骑士·螺纹长枪变体"是自研的，唯一的对齐参照就是玩家手持。因此 [[goetyominous-split-into-modules]] 之外，遇到"骑士技能要跟长枪一样"这类需求时，先确认参照是玩家侧还是 AC 怪物侧——AC 怪物侧没有这个东西。

相关：`DeepOneKnightServantOrtholanceWaveCooldown`（AttributesConfig，默认 60 刻）管的是"扇形波 + 冲刺"一整套，2026-10-08 用户明确选择**保持 60 不改**。
