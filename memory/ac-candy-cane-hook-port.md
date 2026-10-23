---
name: ac-candy-cane-hook-port
description: AC 甘蔗钩骑乘链的移植结论（2026-10-03 做过又按用户要求整套回退，仓库里现在没有）——instanceof 硬编码必须重指向、OPTIONAL_VEC_3 可跨 mod 复用、空手路线与钩子不冲突
metadata: 
  node_type: memory
  type: reference
  originSessionId: e6112b54-7831-4204-8858-4e13afce37db
  modified: 2026-10-03T01:28:32.166Z
---

**状态：已回退。** 2026-10-03 完成过整套移植并通过编译，随后用户要求"整套甘蔗钩回退"，新增的 5 个文件已删、`GumWormServant` 里的钩子契约（`LEFT_HOOK_ID`/`RIGHT_HOOK_ID`/`setHookId`/`getHook`/`hasARidingHook`/`getHookPosition`）也已删。**现在仓库里没有甘蔗钩，沙虫只能空手右键上虫。** 下面这些结论仍然成立，用户哪天再想加可以直接用。

当初的产物（现已不存在）与结论：

- 产物会是：`common/entities/projectile/CandyCaneHookProjectile.java`、`common/items/ac/CandyCaneHookItem.java`、`client/render/ac/RenderCandyCaneHook.java`、`client/render/model/ac/ModelCandyCaneHook.java`、`models/item/candy_cane_hook.json`（`parent: alexscaves:item/candy_cane_hook`，overrides/display 全继承）；接线点在 `AcEntityRegistry`、`AcItems`、`ModModelLayers`、`GoetyOminous.onClientSetup`（`cast` 属性，包 `AlexCavesCompat.isAlexCavesLoaded()`）。
- **必须自己重写，不能复用 AC 的物品**：`CandyCaneHookEntity` 里 `instanceof GumWormEntity` / `GumWormSegmentEntity` 是硬编码的，指向 AC 自己的实体；`CandyCaneHookItem.use` 里也一样。整条链要重指向 `GumWormServant` / `GumWormSegmentServantEntity`。后果：AC 那根钩钩不动我们的仆从，我们这根也拽不动 AC 野生的沙虫。
- **`ACEntityDataRegistry.OPTIONAL_VEC_3` 可以跨 mod 直接复用**（`public static RegistryObject<EntityDataSerializer<Optional<Vec3>>>`）。`SynchedEntityData.defineId(..., OPTIONAL_VEC_3.get())` 写在实体类的 `static final` 字段里是安全的：实体类只在**首次实例化**时初始化，那时 AC 的 DeferredRegister 早已填好。已确认 `libs/alexscaves-2.0.2.jar` 与编译用的 gradle 缓存 jar 都有它。
- **空手右键与钩子不冲突**：`mobInteract` 手里不是空手时返 `super.mobInteract` → `Mob.mobInteract` 返 PASS（已确认 Goety 的 `Summoned`/`Owned` 都不覆写 `m_6076_`）→ 客户端 `Minecraft.startUseItem` 在 `gameMode.interact` 没 consume 时会继续落到 `gameMode.useItem` → `Item.use` 抛钩。
- **当时的两处有意偏离**（若重做要想清楚）：① AC 的三个钩子附魔（FAR_FLUNG / SHARP_CANE / STRAIGHT_HOOK）没移植，连带 `DAMAGE`/`RESISTS_GRAVITY` 两个同步数据去掉，钩子不造成伤害、`getGravity()` 简化成 `isReeling() ? 0 : 0.08F`；② AC 要求**双手**各一根钩才交控制权，我们当时是**一根就给**（因为 `GumWormServant.isRidingPlayer` 只看 `ridingPlayer`，与空手路线共用判据）。
- 表现层全部直接吃 AC 原版资源：弹射物贴图 `alexscaves:textures/entity/candy_cane_hook.png`、物品模型 parent、`ACSoundRegistry.CANDY_CANE_HOOK_LAUNCH/_REEL`。只有模型几何是手抄的（AC 的 `AdvancedModelBox` 字段 private）。

相关：[[ac-goety-port-probability-gates]]、[[goety-compile-jar-source-of-truth]]、[[goetyominous-deadcode-cleanup-preference]]
