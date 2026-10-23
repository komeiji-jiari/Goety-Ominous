---
name: trial-effect-port-1-21-1
description: 四个试炼效果移植的定案：命名空间 goetyominous:*、蓄风复用仓库风弹爆炸设施、粒子走 ModEffectParticles 单点收集
metadata: 
  node_type: memory
  type: project
  originSessionId: 97e87718-bd88-49e3-8284-e2cd365e56cc
  modified: 2026-10-07T18:29:52.347Z
---

2026-10-07 定案并落地：把 1.21.1 的**渗浆 oozing / 盘丝 weaving / 蓄风 wind_charged / 寄生 infested** 四个试炼效果移进 `goetyominous:oozing|weaving|wind_charged|infested`（不套用原版 `minecraft:` 命名空间，与已有 `trial_omen` 一致）。

用户拍板：死亡触发**对齐原版**（mixin 而非 `LivingDeathEvent`）、爆炸伤害类型**对齐原版**、蛛网加速**做**、每效果独立粒子**做**、寄生**一起做**。

关键复用（别重造）：
- **蓄风零新基础设施**：`AbstractWindCharge.EXPLOSION_DAMAGE_CALCULATOR` + `WindChargeExplosion` + `ModParticleTypes.GUST_EMITTER_SMALL/LARGE` + `ModSounds.BREEZE_WIND_CHARGE_BURST` 都是之前移植 Breeze 风弹时铺好的，照 `BreezeWindCharge.explode` 抄，只把半径换成 `3.0F + rnd.nextFloat()*2.0F`、中心 `y + bbHeight/2`。
- **粒子集中在 `common/effects/ModEffectParticles.collect(LivingEntity)`**：`ModEffectParticleMixin`（原 `TrialOmenParticleMixin`，因同一调用点不能再加第二个 `@Redirect` 而泛化）只负责取列表 + 发。新增效果加粒子往这一个方法里加一行。
- 密度对齐 1.21：可见时 1/4、隐身 1/15（靠 `if (!isInvisible && nextBoolean()) return;` 叠在原版 `tickEffects` 已有的门之上）。

**未做**：获得途径（配方/酿造/仪式/手册一律没碰，按既有约定只注册，靠 `/effect` 或别的模组取用）；`levelEvent(3018)` 铺网音（1.20.1 无此事件，见 [[forge-1.20.1-mobeffect-no-death-hook]]）。

状态：编译通过 + refmap 已确认三个 mixin 全部解析成功，**未进游戏实测**。

见 [[forge-1.20.1-mobeffect-no-death-hook]]、[[goety-1.21-explosion-null-damagesource]]、[[no-unrequested-recipes-patchouli]]。
