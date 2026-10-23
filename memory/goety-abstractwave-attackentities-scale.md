---
name: goety-abstractwave-attackentities-scale
description: Goety AbstractWave.attackEntities 收到的 scale = slamAmount*2 + 1 + waveScale，不是 getWaveScale()
metadata: 
  node_type: memory
  type: reference
  originSessionId: 375bdf5c-f8e2-45b8-b1cd-1de3911fea44
  modified: 2026-10-08T10:36:36.787Z
---

Goety `AbstractWave.tick()` 服务端每 tick 调:

```java
this.attackEntities(this.getSlamAmount(1.0f) * 2.0f + 1.0f + this.getWaveScale());
```

`getSlamAmount() = slamProgress * 0.1F`，而 `slamProgress` 在 `setSlamming(true)` 之后每 tick +1 直到 10。所以子类 `attackEntities(float scale)` 收到的:

- 飞行中（slamAmount=0）: `waveScale + 1`
- 命中瞬间（slamAmount=1）: `waveScale + 3`

**推论**:子类里写 `damage = scale + 1.0F` 就等于「飞行中 waveScale+2、命中 waveScale+4」。waveScale 默认 1.0 → 实际 3.0~5.0。别把形参 `scale` 读成 `getWaveScale()` —— 那个多出来的 +1/+3 就是从这里来的，只看子类代码会算错。

`scaleBasedDamage` 是**本仓库自己加的**字段（不是 Goety 的），用来在「随 waveScale 缩放」和「固定 5.0F」两套之间切；DeepOneServantWave 已统一到法师波那套（恒 `scale + 1.0F + getExtraDamage()`）。相关:[[corrodent-servant-fidelity-audit]]。

确认方法（本次实测）:
`bash .scratch/cfrrun.sh <deobf jar> --jarfilter 'AbstractWave'`
Goety 的 deobf jar 在 `~/.gradle/caches/forge_gradle/deobf_dependencies/curse/maven/goety-586095/<id>_mapped_official_1.20.1/`，取 id 最大的那个。**没有 sources jar**，只能反编译。
