---
name: client-dist-audit-fragile-spots
description: 2026-10-18 客户端/服务端 dist 审计结论，以及已修复和仍须保持的脆弱点
metadata: 
  node_type: memory
  type: project
  originSessionId: 57183679-dbd4-4cdf-aacd-5500421e0205
  modified: 2026-10-17T23:54:21.156Z
---

2026-10-18 全量 dist 审计（jdeps 2106 class + 二进制扫描 + javap 类级 @OnlyIn）结论：当前 master 的 dedicated server 启动与玩家加入路径没有客户端类泄漏；旧版报告的 `LocalPlayer` 崩溃来自已修的三处（CroneRobeEvents 公共 Forge 总线上的 PlayerTickEvent 处理器、ForsakenServant 直接 instanceof LocalPlayer、renderer mixin 放在公共 mixin 列表）。

2026-10-18 按审计结果做过一轮结构修复（`compileJava` 通过）：
- 6 个 AC HUD 事件类从 `common/events/ac` 移到 `client/events/ac`，并加类级 `@OnlyIn(Dist.CLIENT)`；仍只在 `AcClientEvents` 的 `FMLClientSetupEvent` 里注册。
- `FungusPackContainer` 从 `client/inventory/container` 移到 `common/inventory/container`（服务端确实会加载/实例化它：`ModContainerTypes` + `OpenFungusPackPacket`）。
- `MutantBlazeServantSoundInstance` 从 common 移到 `client/sound/mm`。
- `ModernUiTextCompat` 从 `compat/mod` 移到 `client/compat/mod`，并加类级 `@OnlyIn(Dist.CLIENT)`。

仍脆弱、改代码时必须注意：
- `MutantBlazeServantSoundInstance` extends 客户端 `AbstractTickableSoundInstance`，类加载即炸；common 的 `MutantBlazeServant` 仍直接 `new` 它，唯一防线是 `onAddedToWorld` 里的 `isClientSide`。别把 `new` 移出守卫，也别在服务端可达路径里 `instanceof`/取 class literal。
- `LmParticles`（common 注册表）依赖 `Circle$RingData`、`PhantomDaggerTrail$OrbData`、`Circle$EnumRingBehavior`，以及 LM 的 `AnnihilationBombTrail$OrbData`、`LightningParticle$OrbData`。外层 `Circle`/`PhantomDaggerTrail` 是类级 `@OnlyIn(CLIENT)`，但嵌套类**没有**注解且是 static，所以服务端能构造它们（`TheObliteratorCloneArmed:456` 等 `serverLevel.sendParticles`，启动时 `LmParticles` 注册也会加载）。它们必须保持：static 嵌套、无类级 `@OnlyIn`、不访问外层静态成员。LM 的 OrbData getter 只有方法级 `@OnlyIn`（服务端被剥掉），服务端只调构造器/`writeToNetwork`/`CODEC`，没问题。

**Why:** Forge `RuntimeDistCleaner` 对类级 `@OnlyIn` 是加载即抛异常，方法/字段级只剥离；JVM 懒解析使得未执行方法体里的客户端引用不会加载类。
**How to apply:** 新增客户端 helper 优先放 `client/` 包；common→client 的引用必须落在 `isClientSide` 分支或 `initializeClient` 等纯客户端钩子里；客户端事件用 `value = Dist.CLIENT`。相关：[[lm-mechanic-presence-version-check]]、[[forge-entityjoinlevelevent-on-readd]]。
