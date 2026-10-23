---
name: bk-rs-head-attach-feasibility
description: 复生骑士脑袋挂无头骑士的可行性推导：骨架同源、三条坑、待定触发条件
metadata: 
  node_type: memory
  type: project
  originSessionId: 4aa2e396-8af2-4ffa-84ec-9155da6f4d4f
  modified: 2026-10-21T15:37:59.968Z
---

2026-09-30 评估「把 ResurrectedKnight 的头按到 BeheadedKnight 身上」。结论可行、不用 mixin，用户当时选择**只评估不写代码**，尚未实现。

**取头方式**：`BeheadedKnightModel` 里没有任何 head 骨骼，只能挂渲染层。RS 的 `ResurrectedKnightModel.createBodyLayer()` 是 public static，`ModelPart.getChild()` 也是 public，所以 `bakeLayer(新 location).getChild("rotator").getChild("body").getChild("head")` 就是头盔+面罩整棵子树。

**骨架同源**（模型空间，16 单位 = 1 格）：
- RS `head` 原点 (−1, −22.23, −7.85)
- BK `upper_body` 原点 (0.14, −21.26, −7.12)
- 差 (−1.14, −0.97, −0.73)，基本重合 → 挂 `upper_body` 底下最省事。

**三个坑**：
1. 别复用 `RESURRECTED_KNIGHT_SERVANT_LAYER` —— `bakeLayer` 按 location 缓存，两个渲染器会拿到同一个 ModelPart 实例，RS 的 setupAnim 每帧写 head.yRot/xRot 会串到 BK 头上。必须新注册 layer。
2. 会埋进肩甲：RS 头盔占 model y −34.2~−15.2，RS 肋骨顶 −23.3（露出 11 单位）；BK 肩块 `cube_r4` 顶在 −42.4，比头盔顶还高 8 单位。Y 要上抬约 6~8，得对着游戏调。
3. BK 是驼背：`core_body` +20° + `upper_body` +12.5° = 前倾 32.5°；RS 的 head 父级 `body` 零旋转。头会跟着前倾，是否回正看效果。

**其它**：贴图要单独绑 `legendary_monsters:textures/entity/resurrected_knight/helmet.png`（+golden/diamond）；红化只有 ghost_body/javelin/ghost_arm 三张红贴图，**没有红头盔**，要跟着 `isEnhanced()` 变红得再生成。`BeheadedKnightServantModel.translateModel` 一路做到 `fist7`（给抓取层用的），不能复用，要另加 `translateToUpperBody`。

相关：[[lm-knight-skin-variants]]、[[goety-model-reuse-private-fields]]
