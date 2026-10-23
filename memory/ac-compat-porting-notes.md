---
name: ac-compat-porting-notes
description: "Alex's Caves 联动移植要点——蛋块属性/blockstate 部分匹配、ACMath.cullAnimationTick 重载、vallumraptor 蛋模型命名、出水 BUBBLED 需 resists_bubbled tag"
metadata: 
  node_type: memory
  type: reference
  originSessionId: 52408598-4c39-4fdd-86ad-d5dd43e89cb7
  modified: 2026-10-02T23:20:12.938Z
---

移植 Alex's Caves 恐龙仆从（Grottoceratops/Tremorsaurus/Vallumraptor）时踩过的非显而易见点（bundled AC = curse.maven alexs-caves-924854:5848216）：

- **DinosaurEggBlock 属性**:只有 `HATCH`(0-2) + `NEEDS_PLAYER`(boolean)。blockstate 只写 `hatch=0/1/2` 即可——1.17+ 的 BlockModelDefinition 是"部分匹配"：未列出的属性(如 needs_player)匹配任意值,所以 3 个变体就覆盖全部状态,不会崩。
- **ACMath.cullAnimationTick 重载**:bundled 版只有两个 `(int tick, float amplitude, Animation, float partialTick, int startOffset[, int endAt])` 形式(int 第二个参)。int 字面量(如 `3`)会拓宽成 float,所以 `cullAnimationTick(tick, 3, anim, partialTick, 0)` 也能编过。
- **vallumraptor 蛋模型**:block model 用 parent `alexscaves:block/small_dinosaur_egg_1_template` + 单个 `egg` 纹理(`alexscaves:block/egg/vallumraptor_egg` / `_cracked` / `_very_cracked`);item 用 `alexscaves:item/vallumraptor_egg`。不像 tremorsaurus 用 north/east/south 多向模板。
- AC 原版 `vallumraptor_egg` blockstate 有 `eggs=1..4 × hatch=0..2`,但那是 AC 自己的蛋块另加 EGGS 属性;仆从蛋块 extends DinosaurEggBlock 只有 hatch。
- **AC 蛋方块结构**:每蛋一个 `*_egg_template.json`(纯几何,parent `block/block` + cutout)+ 3 个状态模型(`_egg`/`_egg_cracked`/`_egg_very_cracked`,只把 `side/top/bottom` 绑到 `alexscaves:block/egg/*`)。尺寸各异:atlatitan 整方块 0-16、grotto 4-12 高 10、relicheirus 1-15 高 16、tremorsaurus 3-13 高 16 且六面贴图。vallumraptor/subterranodon 例外,复用 `small_dinosaur_egg_1..4_template`。
- **仓库那 3 套本地蛋副本(grotto/tremor/vallum)是原样搬运**:模板几何逐字一致(仅 tab→空格)、贴图与 AC 原图 **byte 相同**;只改命名 `_egg`→`_servant_egg` + 命名空间。所以 atlatitan/relicheirus 直接引用 `alexscaves:block/xxx_egg` 与本地副本**等价**,不是结构不同。
- AC 的 `grottoceratops_egg_template.json` 结尾是个多余 `},`(畸形 JSON 且无换行),仓库副本去掉了该逗号。
- Vallumraptor 实体 `.sized(0.8F, 1.5F)`,spawn egg 颜色 base `0x22389A` / spots `0xEEE5AB`;属性 28 血/3 伤/0.2 速/32 跟随/0 击退抗性/0 甲。
- 中文名:阔鼻迅猛龙(AC 官方 zh_cn)。
- **Vallumraptor Hiding(隐身/藏匿)机制**:原版实体靠 `HIDING_FOR`(synched int)+ `hideProgress`(0→20)+ `getHideProgress()`= `进度*0.05F`。低血(<45% max)且驯服时 `setHideFor(80+random(40))`、清空攻击者目标记忆、`FleeGoal` 逃跑;隐藏期间回血 `tickCount % 15`(否则 %100)。渲染:renderer 覆写 `scale()` 调 `model.setAlpha(1 - 0.9*getHideProgress(partialTick))`,并覆写 `getRenderType()` 在 `getHideProgress(1.0F)>0` 时返回半透明 render type。模型已有 `setAlpha` 钩子并在 renderToBuffer 里 `alphaIn * this.alpha`。
- **1.20.1 映射坑:`RenderType.translucent(ResourceLocation)` 不存在**——javac 只会解析到无参 `translucent()`。AC 字节码里的 `m_110473_` 对应 Mojang 名 `entityTranslucent(ResourceLocation)`(内部调 `entityTranslucent(loc,true)`)。半透明实体要用 `RenderType.entityTranslucent(texture)`。
- **DeepOneMageServant 出水 BUBBLED 泡膜不变量**:出水时 `addEffect(BUBBLED, 200)`、回水 `removeEffect`(该块客户端服务端都执行)。实体类型**必须在** `alexscaves:resists_bubbled` tag(`data/alexscaves/tags/entity_types/resists_bubbled.json`,与 AC 原版 `deep_one_mage` 并列)——AC 的 `EntityMixin.ac_isInWater` 对"持 BUBBLED 且非该 tag 的水族实体"强制 `isInWater()=true`,不在 tag 里会每 tick 自激振荡(BUBBLED 永不存活、泡泡不显示)。注意仆从直接继承 Goety `Summoned`、不继承 AC `DeepOneMageEntity`,这是全类唯一一份 BUBBLED 逻辑,勿当冗余删除。

- **AC 导航器 vs Goety FollowOwnerGoal(撼地斯拉仆从坑)**:AC 的 `AdvancedPathNavigateNoTeleport` 继承 **Citadel** 的 `com.github.alexthe666.citadel...raycoms.AdvancedPathNavigate`,既非原版 `GroundPathNavigation` 也非 `FlyingPathNavigation`。而 Goety `Summoned.followGoal()` 构造 `FollowOwnerGoal` 时硬校验导航类型,否则抛 `IllegalArgumentException("Unsupported mob type for FollowOwnerGoal")`。关键:该调用发生在 `Mob.<init>` 的 `registerGoals()` 阶段(先 `navigation=createNavigation()` 后 `registerGoals()`),所以用 AC 导航器的实体**根本无法完成构造**,跟随/索敌 AI 全部注册不上。修复:仆从类覆写 `followGoal()` 换自定义 Goal(仿 `DeepOneMageServant.MageFollowOwnerGoal`):不做导航校验、`tick` 动态取 `getNavigation()`(避免 `switchNavigator` 换导航后的陈旧引用)、传送仍走 `com.Polarice3.Goety.config.MobsConfig.ServantTeleport.get()`。陆地恐龙用 `GroundPathNavigatorNoSpin`(extends GroundPathNavigation)则无此问题。
- **骑乘抖动必须实现 `IAdvancedPathingMob`(撼地斯拉仆从)**:**原版在骑乘时不会停导航**——`Mob.isEffectiveAi()` = `!isClientSide && !isNoAi`,骑乘仍为 true;`LivingEntity.aiStep()` 的 `else if (isEffectiveAi())` 分支无条件调 `customServerAiStep()`(tick `navigation.tick()`/`goalSelector`/`moveControl`),唯一跳过条件是 `isImmobile()` = `isDeadOrDying()`。原版 AC 之所以平滑,靠 `DinosaurEntity implements IAdvancedPathingMob`,`stopTickingPathing()` = `isInSittingPose() || isVehicle() || isDancing()`,让 Citadel `AdvancedPathNavigate.tick()`(约 285 行)`stopTickingPathing()==true` 时提前 return(不再 `setWantedPosition` 驱动 MoveControl,也不触发 stuckHandler 传送)。仆从漏掉该接口 → 骑乘时残留 wander/跟随路径持续驱动 MoveControl 与玩家操控冲突 → 第三人称视角抖动;stuckHandler 判定"卡住"还会跳/瞬移 → 卡死。修复:TremorzillaServant implements `com.github.alexthe666.citadel.server.entity.pathfinding.raycoms.IAdvancedPathingMob`,覆写 `stopTickingPathing(){ return this.isVehicle() || this.isStaying(); }`(servant 用 Goety `isStaying()` 当 AC 的 sit,无跳舞)。Tremorsaurus/Grottoceratops 仆从用 vanilla 导航器不识别该接口,机制不同未动。

- **`ICustomCollisions` 里没有 `collide`,别加 `@Override`**:Citadel 的 `ICustomCollisions` 只有 4 个成员(`getAllowedMovementForEntity` / `canPassThrough` / `collideBoundingBox2` / `collideWithShapes2`),`Entity.collide(Vec3)` 在原版是 **private**,AC 用**访问转换器**(`alexscaves` 的 `META-INF/accesstransformer.cfg`:`public net.minecraft.world.entity.Entity m_20272_(...)`)改成 public,子类才真正被派发到。仓库惯例因此是 **`public Vec3 collide(Vec3)` 不写 `@Override`**(见 `CorrodentServant`)。这不是 mixin,不违反"AC 兼容不许用 mixin"。同理 `clampRotation(LivingEntity)` / `getRiderPosition(Entity)` 在 1.20.1 `Entity` 上**根本不存在**,只能是自己调的工具方法,写 `@Override` 必编译失败。
- **`ShakesScreen` 不用自研 handler**:AC 自己的客户端事件扫 `Mob instanceof ShakesScreen`,仆从实现该接口(覆写 `getScreenShakeAmount(float)` + `getShakeDistance()`)即生效;别混用 Goety 的 `CameraShake`。
- AC 的多节虫类实体(Gum Worm):节是**真实体**不是 `PartEntity`,靠 `setPos` 朝 `getIdealPosition(front)` **弹簧追赶**(步长 `1.0F + max(距-2,0)`),所以头瞬移后节几乎当帧追上——`Summoned` 默认的 `FollowOwnerGoal` 传送可以直接用,不需要换非瞬移跟随 goal。

相关:[[forge-compile-setup]]、[[forge-meleeattackgoal-reach]]
