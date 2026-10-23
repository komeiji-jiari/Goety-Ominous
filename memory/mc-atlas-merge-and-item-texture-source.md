---
name: mc-atlas-merge-and-item-texture-source
description: assets/<ns>/atlases/*.json 跨包增量合并不覆盖原版;mob_effect 贴图在独立 atlas 想当物品贴图必须补 single source;创造页签图标只能是物品且 GUI 渲染会照样跑模型谓词
metadata: 
  node_type: memory
  type: reference
  originSessionId: 1b64da48-01ac-40c6-b482-0a16204710a0
  modified: 2026-10-09T22:49:29.514Z
---

**atlas 配置是跨包增量合并的。** 1.20.1 `SpriteResourceLoader.load(ResourceManager, atlasLocation)` 用 `resourceManager.getResourceStack(atlases/<name>.json)` 取到**所有资源包**的同名文件，逐个 parse 后把 `sources` **串起来**。所以 mod 只写自己那几条即可，**不会**覆盖原版。本仓库 `assets/minecraft/atlases/armor_trims.json` 只列 bolt/flow 两条、原版 16 个 trim 照常，就是这个机制的活证据。反编译入口是 `net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader`（该包下**没有** `SpriteSourceList`/`AtlasConfig`，别找错类）。

**物品模型的 `layer0` 在 `minecraft:blocks` atlas 里查**，而 `mob_effect/*` 属于独立 atlas `minecraft:mob_effects`（`assets/minecraft/atlases/mob_effects.json`，source = `mob_effect` 目录、prefix 空）。⇒ 想让**物品**用某个 buff 图标当贴图，光写 `"layer0": "minecraft:mob_effect/bad_omen"` 会变缺失贴图，**必须同时在 `assets/minecraft/atlases/blocks.json` 加一条 `{"type":"single","resource":"minecraft:mob_effect/bad_omen"}`**，两处 id 一致。原版 blocks.json 的 source 只有 `block/`、`item/`、`entity/conduit/` 加几个 single，不含 mob_effect。

**创造页签图标只能是 ItemStack**（`CreativeModeTab.Builder.icon(Supplier<ItemStack>)`，没有贴图 API），所以必须落到某个物品上；见 [[forge-creative-tab-insert-position]]。**别想拿现成的 `goetyominous:ominous_icon` 当页签图标**：它靠 `goetyominous:ominous_icon_index` 物品谓词轮转，而 **GUI 物品渲染传的 `level` 非 null** —— `GuiGraphics.renderItem` 走 `this.renderItem(this.minecraft.player, this.minecraft.level, ...)`（连 `renderFakeItem` 也传 level），谓词在背包/页签里照样生效，图标会随 gameTime 轮转，拿不到固定那张。

主页签图标最终定为 `goetyominous:fel_core`（邪能核心，`ModItems.FEL_CORE`）。2026-10-09 曾为"用改过的原版不祥之兆图标当页签"临时加过隐藏物品 `ominous_tab_icon` + `assets/minecraft/atlases/blocks.json`，用户当天就要求撤掉改回物品图标，两处都已删除——**上面那套 atlas 手法本身是对的**（`bad_omen.png` 是 18x18 调色板 PNG，深蓝脸+红眼），只是本项目没用上。相关：[[goetyominous-trial-blocks-port]]
