---
name: forge-creative-tab-insert-position
description: "BuildCreativeModeTabContentsEvent 插到指定位置:getEntries() 是 MutableHashedLinkedMap,锚点用 new ItemStack(原版物品) 即可命中;putAfter 顺序调用会反序"
metadata: 
  node_type: memory
  type: reference
  originSessionId: 5850c515-ab9a-4d80-b4d6-9483a063d382
  modified: 2026-10-09T22:34:24.558Z
---

Forge 1.20.1 往**原版页签**指定位置塞物品，用 `BuildCreativeModeTabContentsEvent`（**mod 总线**，`@Mod.EventBusSubscriber(bus = Bus.MOD, value = Dist.CLIENT)`，事件只在客户端触发）。判栏靠 `event.getTabKey().equals(CreativeModeTabs.BUILDING_BLOCKS)` —— `CreativeModeTabs.BUILDING_BLOCKS` 在 1.20.1 是 **public** 的 `ResourceKey<CreativeModeTab>`，能直接用。

`event.getEntries()` 返回 `MutableHashedLinkedMap<ItemStack, TabVisibility>`（不是 Collection），有 `putBefore(anchor, key, value)` / `putAfter(...)` / `putFirst(...)` / `remove(...)`。

三个关键点：

1. **键策略是 `ItemStackLinkedSet.TYPE_AND_TAG`**（ForgeHooks 建这个 map 时传的），比的是物品+NBT，**不是身份**。所以锚点写 `new ItemStack(Items.BRICKS)` 就能命中页签里那个真实的砖块栈，不用去遍历 entries 找现成的 ItemStack。
2. **`putBefore` 顺序调用保持顺序**：反复 `putBefore(BRICKS, x)`，每个 x 都紧贴 BRICKS 插进去，结果是 `A, B, C, BRICKS`。⇒ 想按顺序插一组，锚点固定用那个「后者」。
3. **`putAfter` 顺序调用会反序**：反复 `putAfter(A, x)` 每次都插在 A 正后面 ⇒ `A, C, B`。⇒ 要么倒着遍历数组，要么用「移动锚点」：插完把锚点换成刚插进去的那个栈。本工程 `TrialCreativeTabs.after()` 用的后者。

锚点找不到时不会抛异常，`putBefore/putAfter` 会**静默退化成追加到末尾** —— 顺序错了不报错，只能肉眼看页签。

**How to apply:** 移植原版页签排布时，先从目标版本的 `CreativeModeTabs`（反编译 `client-<ver>-official.jar`，`.scratch/cfr.jar` 可解）抄出**目标栏里那段完整顺序**，再拿两个版本**共有**的邻居当锚点。本工程实例：1.21.1 试炼密室 53 方块全在建筑方块栏，凝灰岩族紧邻 `REINFORCED_DEEPSLATE → BRICKS`，铜族按档插在 `_COPPER`→`CUT_COPPER` 之间和 `CUT_COPPER_SLAB`→下一档之间。参见 [[goetyominous-trial-blocks-port]]、[[mc-weathering-block-per-tier-props]]。

**查 1.20.1 原版顺序别去抠 srg jar**（只有 `f_xxxxx_`）。带可读名字的映射包在 `C:\Users\TangJinbo\.gradle\caches\forge_gradle\minecraft_user_repo\net\minecraftforge\forge\1.20.1-47.3.22_mapped_official_1.20.1\forge-1.20.1-47.3.22_mapped_official_1.20.1-recomp.jar`，`unzip` 出 `net/minecraft/world/item/CreativeModeTabs.class` 再 `.scratch/cfrrun.sh` 即可。

1.20.1 实测锚点（2026-10-09 反编译核对，行号是 CFR 输出行）：
- `BUILDING_BLOCKS`：`REINFORCED_DEEPSLATE → BRICKS`（342）；铜族 445–476，**`WAXED_OXIDIZED_CUT_COPPER_SLAB` 是本栏最后一个**（后接即追加）。**`Items.TUFF` 不在本栏，在 `NATURAL_BLOCKS`** —— 照抄 1.21 的 `before(BRICKS, Items.TUFF, ...)` 会把原版凝灰岩同时塞进两栏（putBefore 只动本栏，不会从自然方块栏摘掉）。
- `INGREDIENTS`：`PIGLIN_BANNER_PATTERN` 是最后一个旗帜图案（1456），`SNORT_POTTERY_SHERD` 是最后一片陶片（1476），`SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE` 是最后一个锻造模板（1493）—— 新图案/陶片/模板各接在各自那组的**尾巴**上。
- `TOOLS_AND_UTILITIES`：`MUSIC_DISC_RELIC` 是最后一张唱片（1255），新唱片接它后面。
- `event.getEntries()` 的 `Item.BY_BLOCK` 可用于把「某注册表里的全部方块」批量映射成物品。
