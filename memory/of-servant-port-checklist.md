---
name: of-servant-port-checklist
description: Opposing Force(OF) 仆从移植的三个易漏落点：finalizeSpawn 的 elite 掷骰、EliteVariant 配套、刷怪蛋取色规则
metadata: 
  node_type: memory
  type: project
  originSessionId: 81866738-8c8d-476f-bbe9-60ba3d33e657
  modified: 2026-10-07T14:03:43.839Z
---

仓库里 OF(Opposing Force, `libs/Opposing-Force-1.20.1-2.0.3.jar`) 仆从的还原核对/移植有三个固定落点，漏了不会报错、只在游戏里表现为"某个变体永远见不到"：

1. **`finalizeSpawn` 里的 elite 掷骰最容易漏**
   ```java
   spawnData = super.finalizeSpawn(...);
   if (level.getRandom().nextInt(this.getEliteSpawnChance()) == 0) {  // 默认 1/50
       this.setElite(true);
       this.setEliteStats(this);   // MAX_HEALTH ×1.5、ATTACK_DAMAGE ×1.25
   }
   ```
   `EliteVariant` 是 OF 的接口（`isElite/setElite` + 这两个 default 方法）。不写这段 → `isElite()` 恒 false → 所有精英分支变死代码：精英贴图不显示、`getBlockLightLevel→15` 不触发、精英专属数值/行为永不出现。Dicer/Terror/Trembler/Volt 都有；阴影蜘蛛曾漏（2026-10-07 补上）。

2. **精英相关的其余配套**：渲染器 `getBlockLightLevel` 精英返回 15、眼睛层分普通/精英两套 RenderType、`dropFromLootTable` 精英双倍掉落（只在有 loot table 时有意义，本仓库 OF 仆从都没 loot table，可不移植）。

3. **刷怪蛋颜色：原版是从实体贴图取色的**。OF 的 `bgColor` 基本等于模型贴图的主色调（如阴影蜘蛛 `0x241D2C` 就是贴图第二多颜色）。仓库里 8/9 个 OF 仆从蛋与原版逐位相同，手填容易跑偏 —— 核对时直接把 OF `OPItems.registerSpawnEggItem` 的十进制转十六进制比对最稳。

核对手段：`.scratch/decompile_of_umber.gradle` / `decompile_of_reg.gradle` / `decompile_of_attack.gradle`（CFR via gradlew `-I`，见 [[forge-compile-setup]]）。另见 [[goety-summonspawn-owner-order]]、[[ac-servant-parity-keep-original]]。

**Why**: 这三处都是"静默缺失"——编译过、能跑、看不出问题，只有对着反编译逐行比才发现。
**How to apply**: 新移植 OF 仆从时先照这张单子过一遍；核对已有仆从还原度时也先查这三处。
