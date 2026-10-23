---
name: mm-servant-textures-own-copies
description: MM 仆从贴图自家化：渲染器原指 mutantmore:textures/entities/*.png，用户给新贴图时落到 goetyominous:textures/entity/<x>_servant.png 并把所有共用该贴图的渲染器一起改（含雪球/冰锥/沙球等子实体），否则仆从与它的投射物会新旧混搭
metadata: 
  node_type: memory
  type: project
  originSessionId: 51f63f6c-bee0-455c-b643-9f301c3041ff
  modified: 2026-10-16T09:48:18.617Z
---

**2026-10-16：突变霜冻僵尸 / 突变尸壳两个仆从换成自家贴图**，是 [[emu-servant-port]]（鸸鹋不再直引 alexsmobs 贴图）的同类操作，MM 仆从也在逐步走这条路。

**2026-10-16 突变丛林僵尸也自家化**：`goetyominous:textures/entity/mutant_jungle_zombie_servant.png`。这张是 MM 原来的**普通 128x128 单人皮**（不是 husk/霜冻那种 2:1 宽图、没有右半边附属件），且消费者只有 `MutantJungleZombieServantRenderer` 一个——手臂道具已删、vine 是方块贴图，**没有连带渲染器**。判断「有没有连带」的手法：直接对 MM 解包目录 `grep -rl "<贴图名>" com assets`，命中的类/文件就是全部消费者。

**换的时候别只改主体渲染器。** 这两个仆从的贴图被**同一套装备的多个渲染器共用**（都指 `mutantmore:textures/entities/...`）：
- 霜冻僵尸 → `MutantFrozenZombieServantRenderer` + **`GiantSnowballServantRenderer` / `IcicleSpikeServantRenderer` / `ThrownIcicleServantRenderer`**（雪球/冰锥尖/投掷冰锥都用它的贴图做 UV）。
- 尸壳 → `MutantHuskServantRenderer` + **`SandBoulderServantRenderer`**。
只改主体的话，仆从换了新皮、它扔出去的东西还是旧皮，一眼看出。落点一律 `goetyominous:textures/entity/<name>_servant.png`，代码用 `GoetyOminous.MOD_ID`（别硬写 `"goetyominous"`）。

**尸壳的贴图命名带沙型后缀**：`mutant_husk_servant.png`（普通沙）/ `mutant_husk_servant_red_sand.png`，渲染器里是 `"textures/entity/mutant_husk_servant" + entity.getSandType() + ".png"`。`getSandType()` 的取值**只有 `""` 和 `"_red_sand"` 两种**（`MiscUtils.sandStringForPos`，从 MM jar 常量池里只捞出 `_red_sand` 一个后缀字面量；MM 上游也只发这两张贴图）——加沙型必须同时补贴图，否则就是紫黑块。

**贴图是 2:1 左右的宽图，别按 64x64 想**：`mutant_frozen_zombie.png` 是 288x128，两张 husk 是 256x128。**只有左半边（x < 128）是实体本体**，右半边是模型另外用的附属件（沙块/仙人掌类植物/沙粒粒子贴图）。用户改图时通常只动左半边——2026-10-16 那三张就是：两张 husk 只改了 x<128 且**轮廓 alpha 完全没动**（纯换色），霜冻那张是整张重画（86% 像素不同，仅 10 个 alpha 像素变），都保留了透明通道。

**验证手法**（`.scratch/_png.mjs` 的 `readPng` 返回 `{w,h,rgba}`，纯 Node 无依赖）：跑像素 diff 既能确认「用户这张对应 MM 的哪一张」（比 md5 强——文件名的 md5 是内容哈希，恰好能当参照），也能查 alpha 覆盖率有没有被重新保存时压平（透明区变不透明 = 实体带白底）。比肉眼看缩略图可靠，缩略图里的白是透明底色。

相关：[[mm-mutant-husk-servant-port-notes]]、[[variant-servant-renderer-reuse]]、[[mm-shulker-trap-texture-blue-outline]]
