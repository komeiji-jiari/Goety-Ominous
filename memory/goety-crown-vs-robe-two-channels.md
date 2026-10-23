---
name: goety-crown-vs-robe-two-channels
description: "Goety 装备联动是两条独立通道——*_heal tag 接王冠(寿命),spawnUpgraded 长袍链接 isUpgraded(升级位);加 tag 拿不到升级位"
metadata: 
  node_type: memory
  type: reference
  originSessionId: f57e6f4b-1531-44e9-8a0d-14699b05a693
  modified: 2026-09-25T10:35:13.284Z
---

Goety 仆从的"装备联动"是**两条互不相通的通道**,排查"联动没生效"时必须先分清问的是哪一条:

**通道 A — `*_heal` entity_type tag → 王冠 → 无限寿命**
`IServant.healServant()`(`Summoned.servantTick` 每 tick 调,`cfrgoety9/.../IServant.java:622`)按 `ServantUtil.isXxxHeal(this)` 查 tag,命中就把 `setHasLifespan(false)`。判定是 tag **或** MobType(不对称),例:WIND/STORM/GEO 只认 tag,FROST 是 `MobType.FROST` 或 `frost_heal`。
王冠侧配对:`hasWindCrown` = 任意 `spellType == SpellType.WIND` 的 `MagicCrownItem`。注意 GEO 配的是**紫水晶项链** `hasAmethystNecklace` 不是王冠。

**通道 B — `Summoned.spawnUpgraded()` → 长袍 → `isUpgraded()` 升级位**
只在 `Summoned.finalizeSpawn`(`Summoned.java:310-320`)里玩家主人非 null 时调一次;if-else 链首个命中胜出,写 `UPGRADE_FLAGS` bit 4。
NECRO(`hasUndeadCape` 或主人是 `AbstractNecromancer`)→ WILD → FROST → ABYSS → NETHER → WIND(`hasWindRobes`)→ VOID → GEO。**STORM 没有长袍分支**。
`hasWindRobes` = `hasCurio(le, ModItems.WIND_ROBE)`,而 `findCurio`(`CuriosFinder.java:97-103`)要求 `instanceof Player && CuriosLoaded.CURIOS.isLoaded()`,且物品必须在 **Curios 槽**——塞护甲胸甲槽或背包一律 false。
`UPGRADE_FLAGS` 已同步且以 NBT `"Upgraded"` 存档,客户端读得到。

**踩过的坑:** 把新仆从加进 `wind_heal` 只拿到通道 A(王冠/寿命),**不会**让 `isUpgraded()` 变 true,所以靠 `isUpgraded()` 驱动的表现(半透明/穿墙)依然不触发。同理,在 `finalizeSpawn` 里手写 `if (hasWindRobes(owner)) setUpgraded(true)` 与通道 B 的 WIND 分支逐字等价,属于纯冗余。

**怎么查"到底带没带":** 别问用户,直接解存档 `saves/<世界>/playerdata/<uuid>.dat`,gunzip 后裸扫 `TAG_String` 名为 `id` 的字节模式 `08 00 02 69 64 <len:2> <bytes>` 就能列出玩家全部物品 id(含 `EnderItems`)。实测有一次报"没半透明",扫出来玩家身上只有 `goety:wind_crown`,压根没有 `goety:wind_robe`。

相关:[[goety-summon-spell-conventions]] [[goety-summonspawn-owner-order]]
