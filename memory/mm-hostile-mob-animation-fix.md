---
name: mm-hostile-mob-animation-fix
description: 让 Mutant More 本体敌对突变怪用我们的动画修复——无 mixin，靠渲染器后注册覆盖 + 借用仆从模型当零件架
metadata: 
  node_type: memory
  type: project
  originSessionId: acfb4920-9f33-4cd2-85ec-13764f598e15
  modified: 2026-10-22T12:44:29.425Z
---

配置键 `mutantMoreMobAnimationFix`（`MmConfig` 的 "Mutant More Mobs" 段，默认 true，需重启）。覆盖 MM 本体 6 种怪：突变疣猪兽、突变尸壳、突变丛林僵尸、突变霜冻僵尸、突变凋灵骷髅、突变潜影贝。未覆盖的四种不需要：突变烈焰人/罗德灵的仆从模型动画代码与 MM 本体逐行相同（只差仆从专属分支）；突变幻翼本体是纯 AdvancedAnimator，且无仆从可迁移。

**Why 无 mixin 就够：** `mods.toml` 已声明 `mutantmore` `mandatory=false ordering="AFTER"`，我们的 `EntityRenderersEvent.RegisterRenderers` 监听晚于 MM；`EntityRenderers.register` 内部是 `PROVIDERS.put`，后注册者赢。

**How to apply:**
- 渲染器泛型必须声明 MM 的基类型（`MobRenderer<MutantHoglin, MutantHoglinModel<MutantHoglin>>`）——泛型不变，不能写我们的子类。
- 固定模型 `extends MutantXModel`，但构造函数里用**同一份 baked `ModelPart root`** 造一个 `<我们的>ServantModel` 当"零件架"（`this.parts`），现有 `Mutant*SineWaveAnimations` 类型签名要求仆从模型，这样零重构直接调。零件名两边已核对一致。
- 每实体状态不可放单例模型上 → `MutantMobAnimationStates`（`WeakHashMap<LivingEntity, MutantMobAnimationState>`）。
- 已确认 `MutantHoglin.charging` 经 `handleEntityEvent` 12/13 同步到客户端，冲锋动画可用。
- 潜影贝是个例外：固定模型必须 `extends MutantShulkerModel<MutantShulker>`（`MutantShulkerNormanFaceLayer` 的泛型约束要求），且**必须保留 `spinningAnimation`/`enterSpinAnimation` 两段飞行动画**（仆从已删飞行，没有这两段）。本体也有 `isInBox()`，门控可直接用。陷阱/炮塔没走路动画，不用管。

相关：[[mm-mutant-shulker-anim-gate]]、[[mm-zombie-sinewave-walk-jitter-fix]]、[[mm-walkmodifiers-framerate-phase]]、[[mm-wide-mob-waypoint-offset-yaw-sway]]
