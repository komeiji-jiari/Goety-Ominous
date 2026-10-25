---
name: modernui-text-pipeline-findings
description: ModernUI 文字管线逆向结论（三 shader 路径/图集语义相反/反射点/反编译命令）——rlyeh tooltip 共存失败已撤，等朋友方案
metadata: 
  node_type: memory
  type: project
  originSessionId: 6e85125a-4f69-47e4-9f73-3712133c05f4
---

2026-09-30 为 [[key-of-rlyeh-tooltip-port]] 的 rlyeh tooltip 做 ModernUI 共存（T2），
**实测失败，代码已全部撤除**（用户决定等朋友想到方法再回来修）。
下面是逆向出来的硬结论，回来时直接接着用，不用重新挖。

## 为什么难：ModernUI 的图集语义和我们**相反**

| | 图集 alpha 的含义 |
|---|---|
| `rlyeh_text_glyph.fsh` | `cov = texture(Sampler0, uv).a` = **覆盖率**（字内=1，边缘 0→1 渐变） |
| ModernUI SDF 图集 | `dist = texColor.a - 127./255.` = **有符号距离**（0.5=边缘，整个字格都在渐变） |

见 `assets/modernui/shaders/core/rendertype_modern_text_sdf_fill.fsh`。
所以**不能**把我们的 shader 塞进 ModernUI 的 SDF 路径。要么全程待覆盖率图集（MODE_NORMAL），
要么另写一个吃距离场的 shader 变体（原作者 fsh 里被注释掉的 `rim 0.018..0.125` 阈值就是给真 SDF 写的）。

## 三条 shader 路径（`TextLayout.drawText` 按字形来源选 RenderType）

| | RenderType | 实际 shader 字段 | 何时走 |
|---|---|---|---|
| ① | `makeNormalType` | `TextRenderType.sShaderNormal` | ModernUI 自己的字体（CJK、KEEP_OTHER/IGNORE_ALL 下的 ASCII），覆盖率图集 |
| ② | `makeSDFFillType` | `TextRenderType.sCurrentShaderSDFFill` | `MODE_SDF_FILL`，SDF 图集 |
| ③ | `makeVanillaType` | **`GameRenderer.getRendertypeTextShader()`**（`f_173086_` shard） | `BitmapFont` 字形 —— `defaultFontBehavior=KEEP_ASCII`（1.19+ 默认）时的 **ASCII** |

**坑**：`defaultFontBehavior` 默认 KEEP_ASCII，所以名字里的 ASCII 走 ③、CJK 走 ①。
只换一个字段 = 一部分字形没效果。

**另一个坑**：`ModernTextRenderer.drawText8xOutline` 里 `preferredMode` **硬编码为 1**：
```java
layout.drawText(matrix, source, x, y, r, g, b, a, false, 1, false, 1.0F, 0, packedLight);
```
所以描边那遍**永远走 ②（SDF 图集）**，和我们走 ① 的材质层图集不同 → 字形 bearing 不同 → **错位重影**。
这是实测到的症状。

## ModernUI 的关键结构（可直接用）

- `Font` 被 `@Overwrite`：`m_272191_`(drawInBatch) → `ModernTextRenderer.drawText`；
  `m_168645_`(drawInBatch8xOutline) → `ModernTextRenderer.drawText8xOutline`
- ModernUI 内部用的是**和原版一样的模式**：
  ```java
  private static volatile ShaderInstance sShaderNormal;
  static final ShaderStateShard RENDERTYPE_MODERN_TEXT_NORMAL =
      new ShaderStateShard(TextRenderType::getShaderNormal);
  ```
- 反射点（都能拿到，`TextRenderType` 是 private static）：
  - `icyllis.modernui.mc.text.TextRenderType` → `sShaderNormal`（private）
  - `icyllis.modernui.mc.text.ModernTextRenderer` → `sAllowSDFTextIn2D`（public）
  - 更省事的路线：`TextLayoutEngine.getInstance().lookupFormattedLayout(seq)` 给
    `TextLayout`，它有 public `getGlyphs()/getPositions()/getAdvances()/getFont(i)`；
    `GlyphManager.getInstance().lookupGlyph(Font, fontSize, glyphCode)` 给 `GLBakedGlyph`
    （public 的 `x/y/width/height/u1/v1/u2/v2`）→ 可以完全自己发四边形，绕开两家引擎
- `MuiModApi` **没有**自定义文字渲染的扩展点

## 撤除时动了什么（便于复原/对比）

- 删 `compat/mod/ModernUiTextHook.java`
- `ClientTextTooltipRlyehMixin` 还原成无条件 `drawInBatch8xOutline` + `glyphPass` 只换
  `GameRenderer.rendertypeTextShader`
- `build.gradle` 里 `compileOnly files('libs/ModernUI-Forge-...-universal.jar')` **保留了**（未撤）

## 反编译 ModernUI 的命令（本次用的）

```bash
mkdir -p build/tmp/mui/c build/tmp/mui/s
unzip -o -q "libs/ModernUI-Forge-1.20.1-3.12.0.1-universal.jar" 'icyllis/modernui/mc/*' -d build/tmp/mui/c
java -jar ~/.gradle/caches/forge_gradle/maven_downloader/net/minecraftforge/forgeflower/2.0.629.0/forgeflower-2.0.629.0.jar \
     -dgs=1 build/tmp/mui/c build/tmp/mui/s
```
（ForgeFlower 是 ForgeGradle 自带的，不用另外下。`libs/ModernUI-...jar` 自带 refmap 可看 mixin 目标。）
