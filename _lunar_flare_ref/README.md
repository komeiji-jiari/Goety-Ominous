# 月耀 / 月华 · 参考实现（来自 Goety: Astrology）

这是 `Goety-Astrology 0.3.2fix`（分支 `main`，2026-11-15）里 **月耀聚晶** 的整套视觉实现，
抽出来放在 `_lunar_flare_ref/`，**不参与本仓库编译**（Gradle 只编译 `src/main/java`，
这里在仓库根目录下，是纯参考目录）。

要给 Goety-Ominous 抄的是两件事：**一道从天上落下来的青色光柱**，以及**它落地时的那一下**。

> ⚠ 这里的 Java 是星象学的原样，**带完整注释**（那边要求写注释）。
> 移植进 Ominous 时请按本仓库习惯**全部去掉注释**，只留逻辑。

---

## 1. 目录对照

| 这里 | 原仓库位置 |
| --- | --- |
| `java/.../AstralMoteOptions.java` | 粒子的「颜色 + 尺寸」选项，自带网络序列化 |
| `java/.../AstralSparkOptions.java` | 火花粒子的同款选项 |
| `java/.../client/AstralMoteParticle.java` | **光点粒子本体**（加色、无深度写、自绘 quad） |
| `java/.../client/AstralSparkParticle.java` | 火花粒子本体（有重力、会烧完） |
| `java/.../client/LunarBurstRenderer.java` | 月华绘制（世界阶段 + 三段批次） |
| `java/.../astrology/LunarFlareEntity.java` | 月耀矢：下落的光柱本身 |
| `java/.../astrology/LunarFlareSpell.java` | 聚晶施法：一次撒 5 道 |
| `java/.../astrology/LunarBurstEntity.java` | 月华：纯视觉实体（爆炸的载体） |
| `resources/.../shaders/core/astral_lunar_burst.*` | 爆炸着色器（三模式：地波 / 火球 / 黑） |
| `resources/.../shaders/include/astro_noise.glsl` | 上面那个着色器 import 的噪声库 |
| `resources/.../shaders/include/astro_glow.glsl` | 加色光晕的「外缘必须归零」规范 |
| `resources/.../textures/particle/astral_mote_glow.png` | 光点贴图（**RGB 纯白 + alpha 载衰减**） |
| `resources/.../textures/particle/astral_core.png` | 火花贴图（带放射状尖刺） |

---

## 2. 需要注册的东西

```java
// 粒子类型
PARTICLE_TYPES.register("astral_mote",  AstralMoteOptions::type);
PARTICLE_TYPES.register("astral_spark", AstralSparkOptions::type);

// 实体类型（第二个是纯视觉的爆炸载体；updateInterval 要给 1，下落每 tick 都在动）
ENTITY_TYPES.register("lunar_flare",       ... .sized(0.5F, 0.5F).updateInterval(1).noSummon());
ENTITY_TYPES.register("lunar_flare_burst", ... .sized(0.1F, 0.1F).updateInterval(20).noSummon());

// 渲染器（月耀矢自己不画东西，只放粒子 ⇒ 用一个空的 EntityRenderer）
event.registerEntityRenderer(LUNAR_FLARE.get(),       HiddenEntityRenderer::new);
event.registerEntityRenderer(LUNAR_FLARE_BURST.get(), LunarBurstRenderer::new);

// 着色器（MOD 自己的 shader 注册钩子）
register(event, "astral_lunar_burst", DefaultVertexFormat.POSITION_COLOR_TEX,
         shader -> lunarBurst = shader);
```

---

## 3. 几条踩过的坑（直接抄结论，别重走）

### 3.1 🔴 `DustParticleOptionsBase` 会把 scale 钳到 0.01~4.0

基类构造函数里就是 `Mth.clamp(scale, 0.01f, 4.0f)`，**final 字段，绕不过去**。
想要一个 0.7 格宽的光晕（需要 8.5）会被**静默**压成 4.0 —— 数值写在源码里好看，实际不是那个尺寸。

所以 `AstralMoteOptions` 自己存 `size`，基类 scale 固定给 1.0，并**覆写 `writeToNetwork`**
把真尺寸发过去（否则过一趟网络又变小）。`AstralMoteParticle` 只读 `options.size()`。

### 3.2 光点贴图 = RGB 纯白 + alpha 载衰减

`astral_mote_glow.png` 的 RGB 全白，衰减**全在 alpha 通道**（半宽 0.23 处半亮，0.5 处 6%）。
好处是可以任意放大不会出现方角；坏处是叠加亮度只能靠颜色值。

### 3.3 加色混合里「亮」= 颜色值，不是 alpha

`blendFunc(SRC_ALPHA, ONE)` 下，一颗粒子的贡献是 `贴图 * 顶点色 * alpha`。
所以要做「大而暗」的光晕就把 RGB 压暗（`0.02, 0.22, 0.22`），**不要指望 alpha**。

同理，光柱的**核心** R 通道故意只留 `0.05`：加色会把 RGB 一起加，偏白的粒子叠十层就纯白，
留 0.05 的话叠十层还是青的。这就是「一道光」和「一根白条」的区别。

### 3.4 一道光束怎么画：沿位移铺珠

月耀矢每 tick 走 1.9 格。只在头部落一颗 → 一串间隔 1.9 的点。
做法是拿「上一 tick 位置 → 本 tick 位置」这段位移**均匀切 14 份**，每份一颗；
每颗再配一颗 8.5 倍大小、1/10 亮度的光鞘（每 3 颗给一次）。间距 0.14 < 核心亮区 0.18 ⇒ 连绵成绳。

### 3.5 🔴 服务端不要再发粒子

每个客户端自己就会为它看得见的每一道矢跑一遍 `clientTrail()`。
服务端再 `sendParticles` 一遍 = 每颗画两次 ⇒ 加色叠成白条。

### 3.6 爆炸要像爆炸，得同时有三样

1. **球**：camera-facing quad，轮廓被 `fbm` 位移（`edgeR = ballR * (1 + 0.34*(fbm-0.5))`）⇒ 边缘沸腾；
   再加一层极坐标下的 `fbm` 拉成细丝；白热核心缩小得比球快得多。
   撞击点在方块**表面**，所以球心要抬高 `0.30 * radius`，否则半个球埋在地里。
2. **地面波**：双 crest + 一条「晚 0.14 出发、跑得更快」的冲击前沿 + 径向条纹
   （条纹用**方向向量**采 noise，避开 `atan` 回绕处的接缝）。
3. **碎屑**：`AstralSparkParticle` 天生有重力/摩擦/短寿命，是抛射物。没有碎屑的爆炸只是个漂亮的圈。

### 3.7 🔴 「一瞬间是黑的」必须换混合模式

加色混合只能让画面更亮，**做不出黑色**。黑闪是单独一批：
`blendFunc(SRC_ALPHA, ONE_MINUS_SRC_ALPHA)` + 输出 `vec4(0,0,0,alpha)`，用 alpha 从 1 淡到 0。

而且必须**在所有加色之后画**（所以渲染器要对实体跑两遍：先所有光，再所有墨），
否则后一颗球的加色会把前一颗的黑提亮。

### 3.8 🔴 一个 uniform 一批次

`uMode` 是在**批次绘制时**读取的，同一批里改它 = 全部顶点都用最后一个值。
所以地波 / 火球 / 黑 是三批，不是一批换 uniform。

### 3.9 🔴 加色光晕的外缘必须在 quad 边界前归零

`astroGlowFalloff` 就是干这个的；手写的话至少要有 `* (1.0 - smoothstep(0.86, 1.0, d))`，
否则高斯尾巴还亮着就被 quad 硬切，出现**看得见的方框**。

### 3.10 纯加色的世界物件要延后画

`RenderLevelStageEvent.Stage.AFTER_WEATHER` 画，装了光影包时再走一层 `LateSkyPass` 延迟提交 ——
光影包会重算远平面上的像素，实体阶段画的加色物体直接消失。

---

## 4. 关键参数（调观感就改这些）

```java
// 光柱
STREAM_BEADS = 14;        // 每 tick 铺几颗（间距 = 1.9 / 这个数）
CORE_WOBBLE  = 0.075;     // 核心的抖动半径
SHEATH_EVERY = 3;         // 每几颗珠配一次光鞘
SHED_*                    // 光柱旁边撒的碎光（"下坠粒子"）
LUNAR_BEAM   = (0.05, 0.95, 0.93) size 4.2    // 核心
LUNAR_SHEATH = (0.02, 0.22, 0.22) size 8.5    // 光鞘
BASE_SIZE    = 0.085F;    // 实际 quad = 0.085 * size

// 施法
STREAMS              = 5;
DROP_HEIGHT_MIN/MAX  = 13 / 33;   // 不同落高 ⇒ 错开落地时间
TILT_DEGREES         = 2.0;       // 偏离铅垂 0~2°，方向随机 360°

// 爆炸
LunarBurstEntity.LIFE   = 13;     // tick
BALL_SCALE              = 1.45;   // quad 半宽 = radius * 这个
BALL_LIFT               = 0.30;   // 球心抬高
```
