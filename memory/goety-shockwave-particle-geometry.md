---
name: goety-shockwave-particle-geometry
description: "ShockwaveParticleOption 尺寸换算(quadSize 是半径,渲染直径=1.5×size)与构造函数重载选择的坑"
metadata: 
  node_type: memory
  type: reference
  originSessionId: b76b305f-3cde-4e3f-8fb0-334f6dfa6ce7
  modified: 2026-09-14T00:40:29.791Z
---

Goety `ShockwaveParticleOption`(在 `client.particles` 包但无 `@OnlyIn`,common 代码可直接 new,本仓库 UrbhadhachServant/Leapkelp 已有先例):

**尺寸换算**(读 `.disasm/gsrc/.../GroundCircleParticle.java` 的 `renderRotatedParticle` 得出):四角向量是 `(±1,±1,0)` 再乘 `f3 = getQuadSize(pt)`,所以 **quadSize 是半径不是直径**。正向模式 `getQuadSize = size × clamp(age/lifetime×0.75, 0, 2)`,寿命终点系数恰为 0.75 → **渲染直径 = 1.5 × size**。
- 默认(`new ShockwaveParticleOption()`)size=10 → 直径 15 格;Goety 的 WarStompSpell 用的就是默认。
- 想对齐"半径 R 的伤害范围"→ `size ≈ 1.33R`;想要直径 D → `size ≈ 0.67D`。

**speed 字段**:进 tick 做 `age += speed`,与 super.tick() 的 age++ 叠加 → speed=1 时视觉时长和寿命都减半(life 30 实际只演 15 tick);speed=0 即 life 那么多 tick。

**重载选择的坑**:`new ShockwaveParticleOption(0.0F, 8.0F, 1)` 这种"float,float,int"实参,**不会**匹配 `(r,g,b)` 三参构造,而是按更具体原则命中 `(float originSize, float size, int speed)`(int→float 是拓宽转换,比 (float,float,float) 更具体)。UrbhadhachServant:427 就是这个写法,本意即 (originSize=0, size=8→12格直径, speed=1),不是笔误。要指定颜色必须显式给三通道 float。

相关:[[goety-ac-servant-keydown-channels]]
