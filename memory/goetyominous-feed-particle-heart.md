---
name: goetyominous-feed-particle-heart
description: "喂食/投喂治疗一律用 ParticleTypes.HEART;ModParticleTypes.HEAL_EFFECT 是绿色医疗十字(goety:heal 贴图)不是爱心,provider 同类不代表贴图相同"
metadata: 
  node_type: memory
  type: reference
  originSessionId: 387ef423-e4f2-4814-b644-3a15178ef504
  modified: 2026-09-24T09:53:55.315Z
---

**规则(用户 2026-09-24 明确)**:仆从喂食反馈粒子必须是爱心 = `net.minecraft.core.particles.ParticleTypes.HEART`。

**别踩的坑**:`ModParticleTypes.HEAL_EFFECT`(`com.Polarice3.Goety.client.particles`)乍看是"治疗粒子",而且 Goety 在 `ClientSideInit` 里确实用 `HeartParticle.Provider::new` 注册它 —— 但 provider 类相同 **不代表贴图相同**:

- `ModParticleTypes.HEAL_EFFECT` 注册 id 是 `goety:heal`,spriteSet 读 `assets/goety/particles/heal.json` → 贴图 `goety:heal.png`
- `ParticleTypes.HEART` 用 `assets/minecraft/particles/heart.png`

`goety:heal.png` 画的是一张**绿色医疗十字(绿十字/加号)**,不是爱心。所以两者外观完全不同,只是共用 `HeartParticle` 这个粒子行为类 + 各自的 spriteSet。查证方法:`unzip -p <goety jar> assets/goety/particles/heal.json` 拿贴图路径,再从 `assets/goety/textures/particle/` 取图看。

**判定口径**:凡是 `mobInteract` 里 owner 手持物品 → `shrink/usePlayerItem` + `heal()` + `GameEvent.EAT` 的喂食分支,粒子都该是 `ParticleTypes.HEART`。

**2026-09-24 已改**:
- 12 个文件 `HEAL_EFFECT` → `ParticleTypes.HEART`:`Agony`、`Returned`、`AbstractSunkenNecromancer`、`AbstractStormNecromancer`、`TremblerServant`、`TerrorServant`、`DicerServant`、`AxolotlServant`、`Warg`(spawnHealParticles,只被喂食路径调用)、`EmuServant`、`DropBearServant`、`HullbreakerServant`
- 2 个 `HAPPY_VILLAGER` 喂食分支也换成爱心:`RelicheirusServant`(原始汤,同方法下面的 TRILOCARIS_TAIL 分支本来就是 HEART)、`OvergrownColossusServant`(苔藓块,顺带按 [[forge-heal-particle-above-bbox]] 把 `getRandomY()` 提到 `getY()+getBbHeight()+0.3F`,它 3.0×5.0)
- **保留** `HAPPY_VILLAGER` 的非喂食互动:illager 系列换武器(`ConquillagerServant`/`InquillagerServant`/`MagispellerServant`)、`BoggedServant` 骨粉去剪毛、`PiglinMerchant` 金块消气、`AbstractPiglinServant` 存粮进背包;`WarpedMoscoServant` 的 60 颗 `SOUL_FIRE_FLAME` 是邪恶之血变身特效,也不是喂食

相关:[[forge-heal-particle-above-bbox]]、[[forge-livingentity-eat-double-consume]]
