---
name: mutant-blaze-servant-port-progress
description: "MutantBlaze→Goety仆从移植进度(ShieldPart/Fireball/Rod已交付,Rodling待办与已定裁剪)"
metadata: 
  node_type: memory
  type: project
  originSessionId: 0c0c97cd-742b-4ad1-bddb-e13ef7f0de1a
---

# Mutant Blaze 仆从移植进度（2026-09 会话存档）

mutantmore 的 MutantBlaze → Goety 仆从移植。项目：E:\java（goetyominous 1.20.1 Forge）。

## 已完成（代码已交付用户，用户自行粘贴）

1. **MutantBlazeServantShieldPart**（PartEntity，泛型 MutantBlazeServant）——完整代码已给，不注册实体、无独立渲染器；需主体提供 `shieldsInactive()`/`isShieldBroken(int)`/`setShieldBroken(int,boolean)`；尺寸 1.21875×1.875；破裂粒子 `ParticleTypeInit.BROKEN_MUTANT_BLAZE_SHIELD`
2. **MutantBlazeServantFireball**（extends MC Fireball，注册 `mutant_blaze_servant_fireball` MISC/1.0×1.0/无fireImmune）——`explosionBlockInteraction()` 固定返回 KEEP（永不破坏方块，`explosionFire` 保留可点燃）；`canHit/canHarm` 仆从化（排除 MutantBlazeServant + `MobUtil.areAllies`）；眩晕同类分支保留（需主体 `stunnedTicks/stunnedLength` + 事件12）；渲染 = `ThrownItemRenderer<>(ctx, 3.0F, true)`（无模型）；音效内联 EntityLoopingSoundInstance（MUTANT_WITHER_SKELETON_FIRE_SLASH_LOOP）
3. **MutantBlazeServantRodProjectile**（extends Goety SpellThrowableProjectile，注册 `mutant_blaze_servant_rod_projectile` MISC/0.5×0.5/fireImmune）——tick 删手动命中块（super 已含）；Rodling 变身结构保留（RODLING data/调用点/mobInteract），`becomeRodling()` 方法体 TODO（等 RodlingServant）；渲染 `MutantBlazeServantRodProjectileRenderer`（复用 mutantmore `RodlingModel` + 其已注册 LAYER_LOCATION，bake 即可）+ GlowLayer（energySwirl 需 3 参 (RL,0,0)——1.20.1 无单参）；纹理 rodling.png/rodling_hostile.png；原版细节：非 rodling scale 1.5、tickCount×500 自转、getBlockLightLevel 15

## 待办：MutantBlazeServant（本体）—— 已调查要点（2026-09 续）

**本体 MutantBlazeServant**：dump blaze.txt 3337 行在 /tmp/mm_full。extends AbstractMutant implements IHeatSource/IHasCustomExplosion。字段/方法清单已存档于 blaze.txt dump 头部。用户需求：
1. **Heat Zone 只烧敌对**（玩家/仆从/驯服/动物豁免）——原版 heat 由外部体系驱动：`HandleEntityHeatEvent`(LivingTickEvent) → `HeatUtils.performHeating` → `heatNearbyMobs`（AABB 内 LivingEntity 逐个 + `EntityHeatProvider.ENTITY_HEAT` capability 蓄热，**无 canHeat 过滤、对所有生物生效**）。**结论：HeatUtils/capability 体系不适合仆从（会烧友军），仆从版建议自写简化灼烧循环**（aiStep 内 AABB + 敌对判定(areAllies+非动物+非驯服) + 点燃/火伤；MutantBlaze 的 IHeatSource 方法：heatRange()←Config.heat_range、heatRangeY()←heat_range_y、heatSpeed()←heat_speed、heats_fire_immune_mobs 配置）
2. **Rod Shot Attack 改为生成 RodlingServant**（RodShotAttackGoal 发射带 isRodling 的 rod → 落地 becomeRodling 召唤 RodlingServant——补 Rod 的 becomeRodling TODO：生成 RodlingServant，owner 传 rod 主人的主人）
3. **死亡散射 32 根 rod**（collectable 状态、glows 默认 false 不高亮、despawn 6000——collectable rod 的 tick despawn 6000 已实现于 rod）；"额外 rodling 不产生"（不设 isRodling）
4. 死亡动画：原版大爆炸+火球散射（用户自己处理火球部分）
5. 生物破坏/火焰默认关（walkGriefing/hurtGriefing 默认 false）

MutantBlazeServant 主体还需移植：multipart 盾牌（SHIELDS_BROKEN 列表/4 ShieldPart/subEntities/positionShield/movePart/updateParts/shieldsInactive）、飞行（FLYING+FlyStrafeMovementGoal+ImprovedMoveControl）、shootMutantBlazeFireball、眩晕（stunnedTicks/stunnedLength/事件12/shockwave）、crouchingDimensions 1.9×2.4、死亡 tickDeath/dropAllTickDeathLoot、属性（血250/甲10/韧0/击退1/速0.25/索敌64——MutantBlazeCommonConfig 直用）、注册 MISC/1.9×4.5/fireImmune（category 仆从惯例 MISC 待定，尺寸必须 1.9×4.5）

建议交付批次：骨架(字段/data/构造/盾部件/注册/属性) → 盾牌系统+眩晕 → 飞行+移动 → 远程攻击(fireball+rod shot 召唤) → heat 灼烧(自写) → hurt/死亡(爆炸+32 rod) → NBT/交互 → 渲染器(MutantBlazeRenderer+GlowLayer+MutantBlazeModel bake 复用+盾牌视觉)

**本体 MutantBlazeServant**：multipart 盾牌系统（SHIELDS_BROKEN×?/shieldRegenerateTicks）、飞行（FLYING+FlyStrafe+ImprovedMoveControl）、双远程（shootMutantBlazeFireball + rod shot）、震波 shockwave、IHeatSource、crouchingDimensions 1.9×2.4；注册 MISC?（原版 MONSTER/1.9×4.5/fireImmune——仆从按惯例改 MISC 还是保 MONSTER 待定——属性默认：血250/甲10/韧0/击退1/速0.25/索敌64）

**RodlingServant 切 Summoned 改造中（2026-09 用户拍板切 Summoned）**：
- 用户曾恢复为 892 行直移版（TamableAnimal + 原版内部类引用）。AI 病根：isTame=false 恒 → registerHostileGoals 敌对 AI。
- **用户最终选择：全量切 Goety Summoned 继承**——改造地图（基于直移版行号，新会话读文件确认）：
  - 类声明 `extends TamableAnimal implements ContainerListener, HasCustomInventoryScreen` → `extends Summoned`（删两接口）
  - 构造：`EntityType<? extends Rodling>` → `EntityType<? extends Owned>`；`setTame(false)` 删
  - registerGoals/isTame 双分支/reassessTameGoals → 恒仆从 AI；registerHostileGoals 删；registerTamedGoals 里的 TamableAnimal.FollowOwnerGoal（FollowType 门控）删（Summoned super.registerGoals 自带跟随）；`new com.alexander.mutantmore.entities.Rodling.ShootAttackGoal(this)`/`RemainStationaryGoal`（原版内部类引用）→ 用此前交付过的 RodlingServant 内联 ShootAttackGoal/RemainStationaryGoal（批次②版本，注意其中 hasLineOfSight 替代 canSee、fireAt 引用 RodlingServantFireball）
  - createConfiguredAttributes：hostile_* 配置 → AttributesConfig.RodlingServant*（6 项已交付：15/0/0/0/20/0.125）；setTame 覆写(属性 hostile/tamed 切换)删
  - canTarget（AttackType.NONE/isTame 分支）→ isOwnedTargetable；shouldBeStationary（FollowType.SIT）→ 死亡条件
  - 声音三处 isTame ? A : B → 恒驯服档（IDLE/0.5/120）
  - openCustomInventoryScreen/GuiUtils 删；inventory/Container 全套(createInventory/containerChanged/updateContainerEquipment/InvWrapper/getCapability/hasInventoryChanged)删 → 换字段(Shields/HasHelmet/Coals/AmountToHeal/Flags——批次①定义过)
  - SOUL/COLOR_ID/染料/CONVERTS 逻辑删；NBT FollowType/AttackType 键删
  - hurt 盾挡里 TamableAnimal cast 分支 → 简化仆从记录
  - hurt 后段(460-892 未读：mobInteract/travel/tick/AI step/炭恢复/healAmount/regenerateShield 等在 460-892——新会话需读 460-892 后完整改)——原版 m_6075_ 炭恢复/盾恢复翻译稿在会话前段已交付（aiStep 版——可复用）

**RodlingServant 已定决策**（用户 2026-09 拍板）：
- extends Goety Summoned（参考 MutantShulkerServantTrap extends Summoned 先例；原版是 TamableAnimal——驯服/繁殖删除）
- **删除**：RodlingChecker（Item/Menu/Screen/GuiUtils 全删）、FollowType/AttackType/SOUL 转化/染料装饰/COLOR_ID、网络包（OpenRodlingCheckerScreenPacket/Update/Swap 全删）
- **装备与煤炭改 entityData 字段**（不用 inventory/Container/capability）
- mobInteract 三件套（主人 `getTrueOwner()==player` 才可用）：Rodling Shields（盾数、两盾各挡一次、regenerateShieldTime 恢复）、Rodling Helmet（非 BYPASSES_ARMOR 伤害减半 + 免疫雨/水伤害）、煤炭/木炭（上限64、右键可多给）
- 炭恢复：**照原版逻辑**（AMOUNT_TO_HEAL 溢出存留机制）
- 属性注册直用 `RodlingCommonConfig.tamed_*` 配置（联动直用风格）：tamed_max_health(15)/tamed_armour(0)/tamed_armour_toughness(0)/tamed_knockback_resistance(0)/tamed_follow_range/tamed_movement_speed(0.125)
- 注册：`rodling_servant` MISC/0.25×0.65/fireImmune

**RodlingServant 第一批骨架已交付**（extends Summoned）：entityData = SHIELDS(int)/HAS_HELMET(bool)/COALS(int)/AMOUNT_TO_HEAL(float)/FLAGS(byte)；flag 位：1=烟雾 2=griefing 4=名字（原版 32/64/128 → 简化为 1/2/4，默认烟雾+griefing 开）；getFlag/setFlag/装备 getter 齐。**第二批及以后待交付**：AI(registerGoals+ShootAttackGoal 内联类)、盾恢复/炭恢复 travel 逻辑、hurt/水免疫、NBT/mobInteract、RodlingFireball 实体、渲染器。

## RodlingServant 现盘状态（2026-09 复核）——516 行 Summoned 版（注意：分析期间用户把文件从 892 行直移版换回此版；892 直移版编译不过+敌对AI+火球断链，勿再恢复）

- extends Summoned；entityData = SHIELDS(int,默认0)/HAS_HELMET(bool)/COALS(int)/AMOUNT_TO_HEAL(float)/FLAGS(byte 32=smoke 64=name 128=griefing——既定说要简化为1/2/4 未落地)
- ctor EntityType<? extends Owned> ✓ 注册/属性(AttributesConfig.RodlingServant*,Health默认10)/rod 生成调用全通
- registerGoals = super + RemainStationaryGoal(0,canUse=isDeadOrDying 恒不触发)+ShootAttackGoal(1)+ApproachTargetGoal(2)+LookAtTarget(3)+stroll(4)+LookAtPlayer(5)+RandomLook(6)；targetSelector HurtBy(0)+Nearest isOwnedTargetable(1)
- ⚠️ 待改：自加 Nearest targetSelector 与 super 自动 SummonTargetGoal/ServantHurtByTargetGoal 同 prio1 会被饿死 → 挪到 2（对齐 MutantShulkerServant.java:236）；自加 HurtBy(0) 与 auto ServantHurtBy(1) 重复可删
- ShootAttackGoal 攻击链与原版逐条对齐：冷却20/蓄力22/动作点7/事件11/粒子 length-5/发 RodlingServantFireball（tamed_* 值、griefing=自己的 griefing flag）/RODLING_SHOOT；渲染器+ThrownItemRenderer(0.5)已注册(ModModelLayers 545-551)
- mobInteract(getTrueOwner==player)：RODLING_SHIELDS→setShields(2)、RODLING_HELMET→setHasHelmet、煤炭/木炭上限64
- 盾恢复150t(regenerateShieldTime=tamed_shield_regenerate_speed)+炭回血(healAmount*percent/100, AMOUNT_TO_HEAL 溢出存留)在 baseTick；isSensitiveToWater 恒 false（原版=!hasHelmet）
- 全部引用的 mutantmore 符号已 javap 核对存在（ItemInit.RODLING_SHIELDS/HELMET、friendly_fire_on、tamed_* 等）；静态可编译，未实跑 build

**已读原版要点（字节码 dump rodling.txt 2381 行在 /tmp/mm_full，新会话需重新 unzip）**：
- Rodling 射 **RodlingFireball**（独立实体，注册 0.3125×0.3125 MISC 无 fireImmune——待移植，字段 damage/fireLength/griefing/ignoresInvulTime 与 MutantBlazeFireball 类似；构造 (Level, LivingEntity, dx,dy,dz)）
- ShootAttackGoal（内部类）：canUse=shouldBeStationary；isInterruptable=目标有效+距离≤tamed_shoot_max_distance(10)+冷却(nextUseTime)+animationsUseable；start=shootAnimationTick=length+广播事件11+音效 RODLING_CHARGE_SHOT；stop 里有蓄力粒子（AdvancedParticleOption RODLING_CHARGE_SHOT，位置 getX/getY(0.45)/getZ）与发射（new RodlingFireball(level, mob, dx,dy,dz) 设 damage=tamed_fireball_damage/fireLength=tamed_fireball_fire_length/griefing=tamed_friendly_fire?/ignoresInvulTime=tamed_ignores_invulnerability_time）——发射段后半(方向计算 230-480)未读完，下轮先读再交付
- registerTamedGoals：清空→RemainStationaryGoal(内部类 flags MOVE/LOOK/TARGET/JUMP, isInterruptable=shouldBeStationary)→跟随(匿名 FollowOwnerGoal→仆从版走 Summoned 默认)→ShootAttackGoal→mutantmore ApproachTargetGoal(tamed_follow_target_wanted_distance, tamed_following_movement_speed_multiplier)→LookAtTargetGoal→WaterAvoidingRandomStrollGoal→LookAtPlayerGoal×2
- RemainStationaryGoal(内部类)：flags MOVE/LOOK/TARGET/JUMP；isInterruptable=shouldBeStationary
- hurt（原版完整已读）：有盾&&getShields()>0&&非特定 DamageTypes 排除(ON_FIRE 类两个 resource key f_286979_/f_268724_)&&!isInvulnerableTo&&direct 非空 → 记录仇恨(lastHurtByMob/lastHurtByPlayer——Tamable 逻辑仆从版简化) + invulnerableTime=20 + breakShield() + return false；有盔甲&&非 DamageTypeTags.BYPASSES_ARMOR(f_268490_) → super.hurt(amount/2)；否则 super
- m_6075_(travel 主逻辑)：盾恢复计时(regenerateShieldTick→regenerateShieldTime=tamed_shield_regenerate_speed=150)+炭恢复(RODLING_HEAL 音效+heal 粒子+AMOUNT_TO_HEAL/tamed_heal_time 驱动)在 1925-2255 区间——恢复节奏细节未完全翻译，下轮补读
- config 字段名全集：hostile_*/tamed_*(armour/armour_toughness/attacks_players/exp_reward/fireball_damage/fireball_fire_length/fireball_griefing/follow_range/following_movement_speed_multiplier/following_target_distance/ignores_invulnerability_time/knockback_resistance/max_health/movement_speed/shield_regenerate_speed/shoot_max_distance + tamed 独有: fed_heal_percent/follow_owner_*/follow_target_wanted_distance/friendly_fire/heal_time)
- 渲染待办：RodlingRenderer/RodlingColourLayer/RodlingGlowLayer（RodlingModel 已复用于 rod 渲染，LAYER_LOCATION mutantmore 已注册）

## 关键工具/方法速查

- mutantmore 反编译源在 /tmp/mm_full（会话内有效，新会话需重新 unzip libs/mutantmore-1.20.1-early_access-2.0.0.jar）
- Rodling 全量字节码 dump：`javap -p -c .../Rodling.class`（2381 行）——新会话重点读 m_6075_(travel 主逻辑:盾恢复+炭恢复+水免疫)、m_142535_、registerTamedGoals(仆从目标参考)、createConfiguredAttributes
- 原版注册属性核对方法：EntityTypeInit 的 lambda（sized/MobCategory/fireImmune）+ MutantBlazeCommonConfig/RodlingCommonConfig 默认值
- 音效/粒子/伤害类型直用 mutantmore：SoundEventInit/ParticleTypeInit/MMDamageTypes（fireResBypassingFireball/mutantBlazeRodProjectile）
