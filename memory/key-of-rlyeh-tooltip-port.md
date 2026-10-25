---
name: key-of-rlyeh-tooltip-port
description: key_of_rlyeh 拉莱耶 tooltip 效果移植：资源已落地(2026-09-30)，Java 由用户自填；含三项设计决议与朋友包的两个坑
metadata: 
  node_type: memory
  type: project
  originSessionId: 6e85125a-4f69-47e4-9f73-3712133c05f4
---

把朋友给的 `E:\java\rlayeh_presentation\`（原 mod 名 `goety_astrology`，诡厄巫法：星象学）
移植进本 mod，给 `goetyominous:key_of_rlyeh` 做深海 tooltip：四层着色器
（astral_tooltip 面板 / astral_frame 边框 / astral_text_glyph 字形材质 / astral_text_flux 字形辉光）。

**前提（用户 2026-09-30 明确）**：朋友只是做示范，**之后不会在自己的 mod 里放这个** →
因此**不要写任何跨 mod 兼容层**，`GoetyOminousCompat`、`registerExternal`/`externalOf`
那一套外部物品映射机制全部砍掉。

## 三项设计决议（用户拍板）
1. **方式 A**：`KeyOfRlyehItem` / `KeyOfRlyehStaffItem` 直接 `implements AstrologyStyled`
   + 覆写 `getName`，不走 `ItemTooltipEvent` 替换首行。
2. **保留 4 个枚举值**，但 `AstrologyStyle` 改用**显式 `shaderTheme` 字段**而非 `ordinal()`
   传 `uTheme` —— 因为 4 个 fsh 的拉莱耶分支都是 `uTheme > 2.5` 硬编码阈值，
   裁枚举会让序号偏移、主题静默串到天体分支。
3. **GameRenderer 字段用 AT 不用 @Accessor mixin**：`rendertypeTextShader` 的 1.20.1 SRG 是
   `f_172598_`（从 forge_gradle 的 `srg_to_official_1.20.1.tsrg` 核实；原版只有 public getter
   `m_172749_`，没有 public setter，所以必须放开字段本身）。项目已有 `accesstransformer.cfg`
   且里面已有 field AT 先例。

## 当前进度（2026-09-30）
**已完成**：`src/main/resources/assets/goetyominous/shaders/core/` 下 11 个着色器
+ `font/astrology_abyss.json` 已复制；4 个 json 已改成本 mod 命名空间。
**着色器已按用户要求去品牌化改名 `astral_*` → `abyss_*`**（注册名同此）。

**未完成（用户说自己填）**：
- 6 个 Java 类（`common/astral/` 三个 + `client/astral/` 两个 + `common/mixin/` 一个）
- `goetyominous.mixins.json:66` 的 `"client"` 列表加 `"ClientTextTooltipOutlineMixin"`
- `accesstransformer.cfg` 加 `public net.minecraft.client.renderer.GameRenderer f_172598_`

**没做这两步时资源是死的**，面板不画、名字不上字体，而且**不报错**。

## 朋友包里的两个坑
1. `astral_text_flux.json` 缺一个逗号（JSON 非法）→ flux shader 静默加载失败、
   气泡辉光层永不渲染。移植时已修。相关静默陷阱见 [[goetyominous-soft-dep-reflection]]。
2. README 声称依赖 `LateSkyPass.deferring()`，实际导出代码里**一处都没有**，是虚报。
   真依赖只有 `GoetyAstrologyMod.MODID` 和 `ModShaders.xxx()` 五个 getter。

## 另外两点
- `key_of_rlyeh` 受 Alex's Caves 门控（`GoetyOminous.java` onClientSetup + `AcCompatManager`），
  没装 AC 时物品不存在，方式 A 天然无副作用。
- 名字**必须**用 `AstrologyFont.abyss(Component)` 重载（保留 TranslatableComponent），
  不能用朋友的 `abyss(String)`：`Item#getName` 服务端也会调用，
  `getString()` 在服务端按 en_us 解析，会把中文名漏成英文。
