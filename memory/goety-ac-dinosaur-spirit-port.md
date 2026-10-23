---
name: goety-ac-dinosaur-spirit-port
description: "移植 AC 实体时的正确姿势——继承 AC 的实体类 + 直接注册 AC 的渲染器,而不是重写成独立 Entity"
metadata: 
  node_type: memory
  type: project
  originSessionId: f9629217-d45e-4fb1-9e1f-138a63788b4a
  modified: 2026-09-13T15:00:23.120Z
---

往本仓库移植 Alex's Caves 的实体时,**优先 `extends` AC 的原实体类**,而不是照着反编译源码重写一个独立 `Entity`。AC 的模型动画方法（如 `SubterranodonModel.animateSpirit(DinosaurSpiritEntity, float)`）参数写死成 AC 的实体类型,且方法体里读模型自己的 **private** `AdvancedModelBox` 字段——意味着**外部无法照抄这些方法体**,只能继承 AC 实体来让 `animateSpirit(this, partialTicks)` 编译通过。

继承之后,AC 的渲染器可以直接注册给我们的实体类型（`registerEntityRenderer(OUR_TYPE.get(), DinosaurSpiritRenderer::new)`）,泛型 `EntityRenderer<DinosaurSpiritEntity>` 对子类天然成立——**不需要新写渲染器/模型/贴图/`setupAnim` 调用**。

仓库既有先例：`MeltedCaramelServantEntity extends MeltedCaramelEntity`（`AcEntityRegistry.java:159` 注册时用 `.setCustomClientFactory(...)`）→ `ModModelLayers.java:738` 直接 `MeltedCaramelRenderer::new`。

配套约定：
- `AcEntityRegistry.register(modEventBus)` 由 `AcCompatManager.java:43` 在 AC 加载分支内调用,所以引用 AC 类的实体类型注册天然是条件加载的,不会 NoClassDefFoundError。
- 用 `setCustomClientFactory` 注册时**不要**再覆写 `getAddEntityPacket()`（`MeltedCaramelServantEntity` 两者只取其一）；反之无 custom factory 时才需要 `NetworkHooks.getEntitySpawningPacket(this)`（样板 `PureDarkVoid`）。

副作用：AC 的 `ExtinctionSpearItem.killGrottoGhostsFor` 用 `getEntitiesOfClass(DinosaurSpiritEntity.class, ...)` 搜索,我们继承它就会被 AC 长矛一起吸走。反向安全——我们自己的 `getEntitiesOfClass(自己的子类.class, ...)` 永远匹配不到 AC 的原生幽灵。

相关：[[forge-bypass-intermediate-tick-baseTick]]
