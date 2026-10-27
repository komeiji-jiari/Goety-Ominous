---
name: config-goety-style-naming
description: "配置分区命名对齐 Goety —— 法术表用显示名 \"X Spell\"，全局侍从规则单独放 [Servants]"
metadata: 
  node_type: memory
  type: feedback
  originSessionId: 2b4149b5-5fc5-4caf-a945-7a6f1ce3fffe
  modified: 2026-10-25T21:32:39.961Z
---

配置统一格式定案后，用户进一步要求两处对齐 Goety 原版：

- `[Spells]` 下的子表名用游戏内显示名并带 " Spell" 后缀（如 `[Spells."Sand Boulder Spell"]`），不再用内部类名（`SandBall`）。名字取对应聚晶英文显示名去掉 " Focus" 再加 " Spell"；内部名与显示名不同的要按显示名走（SandBall→Sand Boulder、Dreden→Rigid、RedstoneCube→Redstone、SoulVulture→Vulture、CrimsonMosquito→Crimson、DropBear→Plunging、VoidShot→Void Crystal、DeepOne→Abyssal Dweller、AtomicBomb→Nuclear Bomb、TremorzillaBreath→Atomic Breath、Umber→Stalker、Inferno→Fiery、SandSpell→Sandy）。
- 全局侍从规则从 `[Mechanics]` 拆出，单独建 `[Servants]` 段（对应 Goety 的 `[Servants]` 在 goety-mobs.toml）。目前 MobsConfig 的 `[Servants]` 有 HeresiarchHysteria/Channeller/Piglin 进化/Hurricane Core/Leapkelp 共 11 键；LmConfig 的 `[Servants]` 有 soulFractureOnServantHit。屏幕震动、动画修复等表现类开关仍留在 `[Mechanics]`。

**Why:** Goety 的表名是给人看的显示名；全局侍从规则集中一处，查找比埋在世界机制里直观。

**How to apply:** 新增法术配置时按聚晶显示名建表；新增跨生物侍从规则放 `[Servants]`，别塞 `[Mechanics]`。改表名会让旧 TOML 值回默认（无迁移），沿用 [[config-file-split-rule]] 与 [[forge-config-toml-order-historical]] 的既有决定。
