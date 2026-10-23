---
name: pokeball-tesseract-pack
description: "精灵球模组的\"秘仪方块\"材质包:改了什么、源在哪、M 变体没做区分"
metadata: 
  node_type: memory
  type: project
  originSessionId: 013128df-21c2-4fff-92e1-a2fda5b72ea8
  modified: 2026-10-02T17:37:53.734Z
---

2026-10-02 给 `[精灵球] pokeballhv-1.5` 做了"变成诡厄巫法 Esoteric Tesseract"的材质包,已装到实例 `1.20.1-Forge 霜冻/resourcepacks/[精灵球] 秘仪方块 Esoteric Tesseract.zip`。

- **物品**:4 个物品(poke_ball / _has_entity / _m / _m_has_entity)的模型整体换成秘仪方块的 3D 立方体。空球→`tesseract_empty`(暗核),装到生物的球→`tesseract_inner`(紫色等离子动画核)。贴图是**复制**进包的(不是引用 `goety:`),所以包自包含、能单独分享。
- **投掷物**:实体几何是 Java 写死的 10×10×10 空心立方体(18 个 5px 盒子:6 块面板 + 12 根棱),资源包改不了形状;重画了 `textures/entity/texture.png`(64×64)成暗紫符文方块 + 发光核心。**原贴图是作者的占位/调试图**(一堆彩色方块),换掉不亏。手法见 [[mc-hardcoded-entitymodel-retexture]]。
- **没做的**:普通球和"保护精灵球"(M)现在共用同一套贴图,游戏中分不出来 —— 用户没要求区分,当时也没擅自加;要区分就得给 M 生成一份换色的 `tesseract*.png`(工具现成,色相一转就行)。GUI 贴图 `textures/gui/sceen/gui.png` 没碰。

**源在 `.scratch/pack_src/pokeball_tesseract/`** —— 是 scratch,可能被清掉;生成脚本 `_genpbatlas.mjs` + `_buildpack.mjs` 才是真正的源,清之前先捞出来。
