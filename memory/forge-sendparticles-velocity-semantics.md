---
name: forge-sendparticles-velocity-semantics
description: "ServerLevel.sendParticles 的 count=0 才给确定性初速度(dist×speed),count>0 是高斯随机,做粒子柱/定向喷射必须用 count=0"
metadata: 
  node_type: memory
  type: reference
  originSessionId: 8dd7fafb-bf3f-4088-96d3-cd657f068de1
  modified: 2026-09-14T02:43:00.700Z
---

`ServerLevel.sendParticles(type, x, y, z, count, xDist, yDist, zDist, speed)` 到客户端 `ClientPacketListener.handleParticleEvent` 后分两支:

- `count == 0`:**一个**粒子,位置就在 (x,y,z),初速度 = `(xDist*speed, yDist*speed, zDist*speed)` —— 确定性、可控。
- `count > 0`:count 个粒子,位置加 `nextGaussian()*dist`,初速度加 `nextGaussian()*speed` —— 每轴**随机正负**,想要"往上飘"会变成上下乱窜。

所以服务端做**粒子柱/定向喷射**(黑烟柱、喷流等)要循环若干次单发 `count=0`,dist 传单位向量、speed 传速率:
`sendParticles(BLACK_VENT_SMOKE.get(), x, y, z, 0, 0.0D, 1.0D, 0.0D, 0.05D)` = 竖直上升速度 0.05/格·tick。

配套:AC 的 `ACParticleRegistry.BLACK_VENT_SMOKE`(VentSmokeParticle,颜色≈0.1 纯黑,big_smoke 贴图,friction 0.99,lifetime 40~60)是现成的黑烟;因为 friction 0.99 几乎不衰减,速度×寿命就是总上升高度,黑烟柱靠这条算高度。粒子源文件见 `.scratch/ac_particles_decomp/.../VentSmokeParticle.java`。

另:`count=0` 时 `overrideLimiter=false`,客户端 32 格外不生成;玩家在场(仆从特效)无影响。
