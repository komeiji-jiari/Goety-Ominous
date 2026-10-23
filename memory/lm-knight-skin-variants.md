---
name: lm-knight-skin-variants
description: BK/RS 喂食交互现状：金锭/钻石/铁锭切 LM 原版皮肤（可来回切）；红染料「强化红化」已于 2026-10-21 按要求整条删除
metadata: 
  node_type: memory
  type: project
  originSessionId: 40c1a477-9dd3-4963-b06a-f114d779bc47
  modified: 2026-10-21T15:37:57.052Z
---

`BeheadedKnightServant`(BK) 和 `ResurrectedKnightServant`(RS) 的喂食交互。

## 2026-10-21：删除「强化红化」

用户要求删除红色染料变红 + 红色皮肤。已整条移除：

- 两个实体的 `mobInteract` 里 `Items.RED_DYE` 开 / `ModItems.ECTOPLASM` 关两个分支，以及 `ENHANCED` EntityData、`isEnhanced()`/`setEnhanced()`、`enhanced` NBT 键。
- 红化分支产生的红魂粒子、红色冲击弧、红 jabalin 等分支全部改回普通版。
- `ResurrectedKnightServantBodyLayer` / `BeheadedKnightServantGhostArmLayer` 只保留 LM 原版贴图。
- 三张自生成红贴图已删：`beheaded_knight_ghost_arm_red.png`、`resurrected_knight_ghost_body_red.png`、`resurrected_knight_javelin_red.png`（以及生成脚本 `.scratch/_redden.mjs`）。
- 投射物 `SoulJavelin` / `ThrownKnight` 的 `enhanced` 透传参数与状态位一并删除；`SoulPillar` 的 `isRed` 保留（圣骑二阶段仍在用），骑士调用点传 `false`。

**别再主动重提这个红化形态。**

## 金/钻皮肤 + 铁锭还原（2026-09-30 加，仍在）

金锭 → `GOLDEN_VARIANT=2`，钻石 → `DIAMOND_VARIANT=3`，铁锭 → `DEFAULT_VARIANT=1`（还原）。走各实体私有 `tryRestyle(Player, InteractionHand, ItemStack)`；`mobInteract` 只剩一行 owner + tryRestyle 判断。

- 音效/粒子：金 = `ARMOR_EQUIP_GOLD` + `WAX_ON`；钻 = `ARMOR_EQUIP_DIAMOND` + `ELECTRIC_SPARK`；铁 = `ARMOR_EQUIP_IRON` + `WAX_OFF`。生存模式扣 1，头顶粒子 ×12。
- **可来回切**：`getTextureVariant() == variant` 直接返回 `false`，落到 `super.mobInteract`，不消耗物品。
- LM 原版本就有 `_golden`/`_diamond` 贴图与 `TEXTURE_VARIANT` 管线（同步 + NBT + 渲染器 switch），但从未写过 setter——我们的 `tryRestyle` 才让它可达。
- BK 的 `UpdateWithAttack` 按变体给冲击弧 tint：2 → 黄 (234,238,87)，3 → 青 (161,251,232)，默认灰 (203,212,214)。RS 没有这个 switch。
- RS 的身体是 `ghost_body.png` 叠加层，只有头盔有金/钻贴图。

## duo 皮肤同步已按用户要求删除

`ResurrectedKnightServant.detectDuoFight()` 里原本每 tick 把 RS 皮肤同步成 BK 的，会覆盖刚喂的结果，已整段删除。代价（用户已知悉）：BK 变金时 RS 不再自动跟随，两只须各喂一次。`duoFight`/`shouldAttack`/`syncedEntity` 与 `synchronisedDuoKnight()` 保留，仍被 `getIsDuoFight()`/`canSynergyAttack()` 使用。

相关：[[lm-red-projectile-sources]]、[[lm-servant-boss-mechanic-stripping]]、[[bk-rs-head-attach-feasibility]]
