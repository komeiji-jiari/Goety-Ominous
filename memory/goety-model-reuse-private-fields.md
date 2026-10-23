---
name: goety-model-reuse-private-fields
description: "复用 Goety 模型类做自家子类时,root/head 等字段是 private 且 setupAnim 无钩子,须整体覆写并自行 resolve ModelPart"
metadata: 
  node_type: memory
  type: reference
  originSessionId: cbb0bb01-e4b1-49d0-aaf8-d63c99c52faa
  modified: 2026-09-10T11:30:35.837Z
---
Goety 的 `HierarchicalModel` 子类（如 `LeapleafModel`）把 `root`/`head` 声明成 `private final`，且 `animateHeadLookTarget` 之类是 `private`。所以 `extends LeapleafModel<T>` 时：
- `root()` 是 public（来自 `HierarchicalModel`），可用
- `head` 拿不到 → 在子类构造函数里自己走一遍：`root.getChild("body").getChild("main").getChild("chest").getChild("head")`
- `setupAnim` 不调 `super`，整段重写（照抄父类的，再加自己的分支），避免父类先播一遍再叠一层
模板层**不用重新注册**：Goety 的 `com.Polarice3.Goety.client.render.ModModelLayer.LEAPLEAF` 是 `public static final ModelLayerLocation`，直接 `context.bakeLayer(ModModelLayer.LEAPLEAF)` 复用同一个烘焙骨架（两边每帧都 `resetPose()`，共用不会串味）。
`RenderType.eyes(RL)` = 原版 `EyesLayer` 用的发光层渲染类型，配 `EyesLayer`（它已实现 `render`，只需覆写 `renderType()`；Goety 的 GlowLayer 覆写 `render` 是为了在两张贴图间切换）。
样板见本仓库 `LeapkelpModel` / `LeapkelpRenderer`