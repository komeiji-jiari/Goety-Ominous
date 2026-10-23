---
name: mc-weathering-block-per-tier-props
description: "移植氧化方块族(铜/灯泡)必须逐档核对 mapColor 与 lightLevel,原版按档递减不是统一值"
metadata: 
  node_type: memory
  type: reference
  originSessionId: 5850c515-ab9a-4d80-b4d6-9483a063d382
  modified: 2026-10-05T13:52:42.274Z
---

原版氧化方块族（1.21.1 的 `chiseled_copper` / `copper_grate` / `copper_bulb` / `copper_door` / `copper_trapdoor`，各 4 档 × 打蜡/未打蜡）的 **mapColor 和 lightLevel 逐档不同**：

| 档 | mapColor | 灯泡亮度 |
|---|---|---|
| 未氧化 | `COLOR_ORANGE`（= `COPPER_BLOCK.defaultMapColor()`） | 15 |
| 暴露 EXPOSED | `TERRACOTTA_LIGHT_GRAY` | 12 |
| 风化 WEATHERED | `WARPED_STEM` | 8 |
| 氧化 OXIDIZED | `WARPED_NYLIUM` | 4 |

来源规律（`Blocks.java:1231-1234`）：`EXPOSED_COPPER` / `WEATHERED_COPPER` / `OXIDIZED_COPPER` 三只铜块自己就带上述 mapColor，所以 `exposed_*` 写成 `ofFullCopy(EXPOSED_COPPER)` 就自动继承；灯泡另有**显式** `lightLevel(Blocks.litBlockEmission(N))` 覆盖。打蜡变体 `ofFullCopy(未打蜡那个)` 继承同档值。注意 `copper_grate` 基态是显式 `mapColor(COLOR_ORANGE)`，不是拷铜块。

**Why:** 移植时最自然的写法是把 5 个辅助方法各写成单一硬编码 `mapColor(MapColor.COLOR_ORANGE)` + `lightLevel(state -> lit ? 15 : 0)`。这能编译、能跑、方块能放能挖，**只有地图颜色和氧化后的灯泡亮度是错的** —— 游戏里几乎看不出来，除非氧化过的灯泡和新灯泡并排开灯。

**How to apply:** 从 1.21.1 反编译的 `Blocks.java` 逐条 grep `Blocks.register("<方块名>"`，把 `ofFullCopy(...)` 链追到源方块再叠加显式覆盖项；辅助方法按 `WeatheringCopper.WeatherState` 分档返回，别硬编码。参照 [[goetyominous-trial-blocks-port]]。
