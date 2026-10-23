---
name: oculus-extends-vanilla-vertexformat-singletons
description: "自定义 RenderType 复用原版 VertexFormat 单例会被 Oculus 按身份换成带 at_tangent 的扩展格式,Embeddium 模型快路径转换时炸 TANGENT"
metadata: 
  node_type: memory
  type: project
  originSessionId: 9a4ad94d-844b-4b31-9b76-8dba6723ddcd
  modified: 2026-10-04T19:55:17.447Z
---

自制 RenderType **不要**直接复用原版 `DefaultVertexFormat.*` 单例(尤其 `POSITION_COLOR_TEX_LIGHTMAP`),要 new 一个元素表相同的私有 `VertexFormat`。

**Why:** Oculus 1.8 (`net.irisshaders.iris.mixin.vertices.MixinBufferBuilder#iris$extendFormat`) 在 BufferBuilder `begin` 时按**引用相等**判格式并整份替换:

- `DefaultVertexFormat.BLOCK` → `IrisVertexFormats.TERRAIN`
- `DefaultVertexFormat.NEW_ENTITY` → `IrisVertexFormats.ENTITY`
- `DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP` → `IrisVertexFormats.GLYPH`
- 其它 → 原样返回(所以自定义实例天然免疫)

前提是 `WorldRenderingSettings.INSTANCE.shouldUseExtendedVertexFormat()`(光影包用了 mc_Entity/at_tangent/mc_midTexCoord,如 Complementary)+ `iris$shouldNotExtend == false`。GLYPH 在 Position/Color/UV0/UV2 之后多挂了 `Normal + iris_Entity + mc_midTexCoord + at_tangent`,而 Embeddium 的模型快路径 (`EntityRenderer.renderCuboids`) 固定按 `ModelVertex.FORMAT` 推顶点 → 格式不等走 `BufferBuilder.copySlow` → `VertexSerializerFactory` 发现源缺 TANGENT → `RuntimeException: Source format is missing element TANGENT as required by destination format`(崩在渲染实体)。

`NEW_ENTITY` 不会炸是 Iris 额外注册了 `ModelToEntityVertexSerializer`(会自己算切线);GLYPH 只给字体用,没这个序列化器 → 只有自定义 wind 层中招。2026-10-04 修的就是 `WindChargeRenderTypes.breezeWind`(Breeze/BreezeServant/WindCharge 三处共用,崩在 `BreezeRenderer$WindLayer`)。

**How to apply:** 1.20.1 没有 `VertexFormat.builder()`,要 `new VertexFormat(ImmutableMap.<String, VertexFormatElement>builder().put("Position", DefaultVertexFormat.ELEMENT_POSITION)...build())`;元素**名字**必须和原版一致(ShaderInstance 靠名字查 attrib location),顺序一致(决定 offset)。只换格式实例、别的都不动。排查同类崩溃的姿势:用 javap 看 `MixinBufferBuilder.iris$extendFormat` 的 `if_acmpne` 表 + `IrisVertexFormats` 各格式的元素表。

相关:[[forge-compile-setup]] [[vanilla-client-jar-lookup]] [[goetyominous-test-instance-and-logs]]
