---
name: mm-mutant-shulker-dyeing
description: 突变潜影贝染色链路的现状：本体/陷阱可染色，大导弹和小导弹（小导弹为自家实体）都按用户要求保持不染色；白黄本体贴图是外推生成的近似
metadata:
  node_type: memory
  type: project
  originSessionId: 161ce5ce-f2bc-46fd-b2e3-9a3ac4d89d6f
  modified: 2026-10-20T21:01:28.833Z
---

突变潜影贝（mm 联动）染色功能现状：

**可染色**：`MutantShulkerServant` / `MutantShulkerServantTrap` 都有 `COLOR_ID`（byte，`16` = 无色）、`"Color"` NBT、`mobInteract` 里染料/水桶、渲染器按 `DyeColor.getName()` 取 `mutant_shulker_<color>.png`。MM 自己的 `MutantShulkerTrap` 用完全相同的约定（`getId()` / `byId` / `<= 15` / `16`），所以两边 NBT 互通。陷阱继承主人色（两个 scatter goals 里 `trap.setColor(mob.getColor())`）；陷阱捡起保留色（掉落物挂 `EntityTag{Color:<byte>}`）。

**贴图**：本体 7 档紫色 shading，每色一张手绘 7 档梯度（不是公式生成：对 14 色的仿射/HSV/亮度拟合 leave-one-out 误差都大到不可用）。`mutant_shulker_white.png` / `_yellow.png` 原先缺失 → 用两条独立证据推出：白色用灰/浅灰的「染料亮度→梯度顶亮度」强线性关系外推；黄色用橙↔柠檬在 HSV 里按黄色 H 插值。**这两张是近似品，不是原作者手笔**，若日后要改可整张替换。

**导弹不染色（用户 2026-10-20 明确要求）**：大导弹 `MutantShulkerServantBullet` 保持 MM 原样的贴图直出，不要加 tint；陷阱小导弹也**用自家实体** `MutantShulkerServantTrapBullet`（`common.entities.ally.mm`，行为复刻 MM 的 `CustomShulkerBullet`，但替换为 Goety 的盟友判定，并对 `forge:bosses` 不施加浮空），渲染器写死 1,1,1、同样不染色。用户再次强调「不要用 MM 的 `CustomShulkerBullet`，自己写一个」——小导弹一直是自家实体，只是中途被换成 MM 类，已改回。陷阱小导弹浮空读 MM 自己的 `trap_shulker_bullet_levitation_length`（默认 100 = 5 秒），`MutantShulkerServantTrap` 变身点直接取值，和上游一致、可配置。

**未做**：子弹拖尾粒子 `ParticleTypeInit.MUTANT_SHULKER_BULLET` 无法传色；同样不需要。

相关：[[goety-servant-follow-standby]]、[[variant-servant-renderer-reuse]]、[[mc-resourcepack-texture-revert]]
