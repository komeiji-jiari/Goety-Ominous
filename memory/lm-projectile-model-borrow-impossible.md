---
name: lm-projectile-model-borrow-impossible
description: "LM 的灵魂剑/盾模型是 HierarchicalModel<T extends LM实体>,无法借用只能抄几何;圣骑 8 个弹射物已全部去 LM 化"
metadata: 
  node_type: memory
  type: project
  originSessionId: 39831f36-71ab-4a74-a9c9-fad5a3614b75
  modified: 2026-09-26T12:05:01.087Z
---

**2026-09-26 完成**：圣骑（`PossessedPaladinServant`）召唤的 8 个弹射物**全部**换成了自有类，仓库里已无任何 `net.miauczel...SoulPillarEntity/SoulBladeEntity/FallingSoulBladeEntity/SoulShieldEntity/EntityThrownEntity` 引用（可 grep 验证）。新增在 `common/entities/ally/lm/projectile/`：`SoulPillar`（纯粒子，EmptyRenderer）、`SoulBlade` + `FallingSoulBlade`（共用 `SoulBladeBase`，只差 discard 阈值 70/65）、`SoulShield`，以及共同基类 `LmAnimatedProjectile`（对应 LM 的 `IAnimatedEntity`+`IHurtingEntity`：ANIMATIONSTATE / AnimationTicks / `controlledAnim` / `disappearTicks()` / `getDamage()`）。

**关键坑：LM 的模型借不了。** 用户当时选的是「借粒子、借模型」，但：
- `SoulBladeUndergroundModel<T extends SoulBladeEntity>`、`SoulBladeFallingModel<T extends FallingSoulBladeEntity>`、`SoulShieldModel<T extends SoulShieldEntity>` 都是**泛型绑死 LM 自己实体类**的 `HierarchicalModel`，`setupAnim(T entity, ...)` 里直接读 LM 实体的成员（`emergeAnimationState`、`getDestinationX/Y/Z()`）。
- 也不能让自有类**继承** LM 实体来复用：LM 的 9 参构造器硬编码 `ModEntities.X.get()`，且要设的 `warmupDelayTicks` 等字段是 `private`；更要命的是 `damage()` 是 private 且写死 `caster.isAlliedTo(...)`，继承等于修不了盟友判定。
- **可借的是非泛型模型**：`SoulTridentModel` 是普通 `Model`（只 `renderToBuffer`，无实体参数），所以 `SoulTridentServantRenderer` 直接 import LM 的模型没问题 —— 别把这条当成通用结论推广。

**实际做法 = 抄几何**：`createBodyLayer()` 的 CubeListBuilder 逐行照抄（零视觉漂移），`setupAnim` 本body 只有 1~3 行。

**2026-09-26 修正（这条当初下得太满）："类借不了" ≠ "几何借不了"。** 上游那 13 个 LM 模型的 `createBodyLayer()` 全是 `public static LayerDefinition createBodyLayer()` —— 静态方法不参与泛型绑定，**可以一行委托**：`return SoulBladeUndergroundModel.createBodyLayer();`。仓库自己早有先例（`of/DicerServantModel` 等 8 个、`mm/MutantShulkerServantModel`）。所以真正借不了的只有 `setupAnim`（读实体成员 + `T extends 上游实体`）和渲染器的泛型实参。判定手法：读 class 文件方法访问标志（`.scratch/_methflags.mjs`），别靠猜。同一结论适用于 mm/ias/sar 的模型（上游对应类同样是 public static），只有 SAR `RunePrisonModel` 不是。CFR 的 SRG→mojmap 对照：`m_171576_`=getRoot, `m_171599_`=addOrReplaceChild, `m_171514_`=texOffs, `m_171480_`=mirror(), `m_171555_(bool)`=mirror(bool)（**`mirror()` 会被 addBox 当场捕获，不能省**；尾随的 `mirror(false)` 只影响后续盒子可删）, `m_171488_`=addBox, `m_171419_`=offset, `m_171423_`=offsetAndRotation, `m_171565_`=LayerDefinition.create, `m_171331_`=getAllParts, `m_233569_`=resetPose, `m_233385_`=animate。动画定义 `SoulBladeAnimations.emerge/.fall` 是 `public static final AnimationDefinition`，**可以直接借**。

两个 blade 模型的 `createBodyLayer()` 逐字节相同，只差 `bb_main` 的 y（坠落 115 / 地下 75）。

相关：[[lm-servant-boss-mechanic-stripping]]、[[goetyominous-deadcode-cleanup-preference]]、[[ac-ao-ally-check-direction]]
