---
name: forge-particle-unbound-spriteset
description: "Forge 1.20.1 粒子 NPE 根因=粒子 JSON 资源未被加载(spriteSet 未绑定),常见于部署 jar 缺 assets;含日志排查法与 render 守卫为何必需"
metadata: 
  node_type: memory
  type: project
  originSessionId: b0c3f523-c851-4095-8b4b-aced1267ad4f
  modified: 2026-09-14T03:38:55.872Z
---

goetyominous 糖果独角兽冲锋/遗弃者吐息粒子的崩溃,根因链:**部署到游戏实例里的 jar 缺少 `assets/goetyominous/particles/*.json`(连同部分模型也缺)→ ParticleEngine.reload 从未处理这些定义 → MutableSpriteSet 永不 bind → 取 sprite 时 NPE**。不是代码/纹理本身问题。

**Why:** 1.20.1 `ParticleEngine.lambda$reload$8`(bindSpriteSets)对每个已注册且有 JSON 的粒子**必然** rebind 成非空列表——纹理缺了会用图集 missing sprite 顶替并打 `Missing particle sprites: {}` 警告。所以 `MutableSpriteSet.sprites` 列表为 null(`Cannot invoke List.size() ... f_107406_ is null`)只可能意味着该粒子 JSON 压根没被 reload 处理(资源不可见)。渲染期 NPE(`Cannot invoke TextureAtlasSprite.m_118409_() ... f_108321_ is null` = TextureSheetParticle.getU0 对 null sprite 取 UV)是 createParticle 兜底也拿不到 sprite 后的次生崩溃。

**Why(2) 构造器 try/catch 不够,render 守卫才致命必需:** `ParticleEngine.tick()` 末尾把 `particlesToAdd` 抽干进渲染队列时**不检查 `isAlive()`/`removed`**(1.20.1 ParticleEngine.java:358-365),唯一清理点在它之前跑过的 `tickParticleList`。所以构造器里 `catch (RuntimeException) { remove(); }` 只是把粒子标死,**sprite 仍是 null**,它照样以 Age 0 被渲染一帧 → 崩在 `getU0()`。`luxtructosaurus_servant_ash` 就是漏了 render 守卫(三个同类粒子都有)才在 2026-09-14 崩的。

**How to apply(排查法):**
- 从 crash 包的 latest/debug.log 搜 `Missing particle sprites`(只列"被引用但图集里没有"的纹理)与 `Redundant texture list`(DEBUG,JSON 不在 spriteSets)。**若自己粒子的 JSON 两者都不产生、而别的 mod 粒子(如 legendary_monsters)有日志 → 自己的 JSON 没被读到 → 查部署 jar**。
- 交叉验证:日志里 `FileNotFoundException: goetyominous:models/item/xxx.json`,而本地 `build/libs` jar(`unzip -l`)里有该文件 → 游戏里跑的是缺 assets 的旧/半成品 jar。正确产物 = `gradlew build` 出的 `build/libs/goetyominous-3.0.0-1.20.1.jar`(含 `assets/goetyominous/particles/*.json`)。
- 防御代码(CandicornServantChargeParticle/ForsakenServantSpitParticle/LuxtructosaurusServantSpitParticle):pickSprite 失败 → `getTextureAtlas(LOCATION_PARTICLES).apply(alexscaves 纹理)` 兜底(注意 setSprite 要判空),render 首行 `if (this.sprite == null) { remove(); return; }` 保证任何情况不崩。**新增 TextureSheetParticle 子类时必须带上这个 render 覆写。** 相关 [[ac-compat-porting-notes]] [[intellij-red-font-troubleshooting]]。
