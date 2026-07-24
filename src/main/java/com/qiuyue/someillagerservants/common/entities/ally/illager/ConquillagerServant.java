package com.qiuyue.someillagerservants.common.entities.ally.illager;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ally.illager.SpellcasterIllagerServant;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.EnumSet;


/**
 * 征服者仆从实体类
 */
public class ConquillagerServant extends SpellcasterIllagerServant implements CrossbowAttackMob {
    private static final EntityDataAccessor<Boolean> IS_CHARGING_CROSSBOW = SynchedEntityData
            .defineId(ConquillagerServant.class, EntityDataSerializers.BOOLEAN);

    public ConquillagerServant(EntityType<? extends ConquillagerServant> p_i48556_1_, Level p_i48556_2_) {
        // 实体构造函数，初始化征服者仆从实例
        super(p_i48556_1_, p_i48556_2_);
        this.xpReward = 20;// 设置此实体被玩家击杀时掉落的经验值为 20（与灾厄村民相同）
    }

    protected void registerGoals() {
        // AI 目标注册方法，决定实体会执行哪些行为
        super.registerGoals();// 先调用父类方法注册施法者仆从的基础 AI
        this.goalSelector.addGoal(3, new IllagerCrossbowGoal<>(this, 1.0D, 16.0F));
        // 添加弩攻击目标，优先级为 3：
        // - this: 实体实例本身
        // - 1.0D: 移动速度倍数
        // - 16.0F: 攻击半径（16 格内会射击）
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        // 自定义属性构造方法，在主类中通过 EntityAttributeCreationEvent 调用以注册实体属性
        return Mob.createMobAttributes()// 创建生物属性构建器
                .add(Attributes.FOLLOW_RANGE, 32.0D)// 追踪范围 32 格
                .add(Attributes.MAX_HEALTH, AttributesConfig.ConquillagerHealth.get())// 最大生命值从 Goety 配置文件读取
                .add(Attributes.ARMOR, AttributesConfig.ConquillagerArmor.get())// 护甲值从配置文件读取
                .add(Attributes.MOVEMENT_SPEED, 0.35D)// 移动速度 0.35
                .add(Attributes.ATTACK_DAMAGE, 5.0D);// 基础攻击伤害 5.0
    }

    public void setConfigurableAttributes() {
        // 动态调整已存在实体属性的方法，用于运行时配置更新
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
                AttributesConfig.ConquillagerHealth.get());// 设置最大生命值
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.ConquillagerArmor.get());
        // 设置护甲值
    }

    public void tick() {
        // 实体刻更新方法，每游戏刻（1/20 秒）调用一次
        super.tick();// 先调用父类的 tick 方法执行基础更新

        // 对周围 8 格内的非亡灵生物施加瘟疫
        for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(8.0D), EntitySelector.NO_CREATIVE_OR_SPECTATOR)) {
            // 检查条件：生物存活、不是盟友、不是亡灵生物
            // 这里原本的逻辑不适用于仆从版本，故改为使用MobUtil.areAllies判断
            if (entity.isAlive() && !MobUtil.areAllies(this, entity)
                    && entity.getMobType() != MobType.UNDEAD
                    && !(entity instanceof Player)
                    && !(entity instanceof Owned)
                    && !(entity instanceof com.Polarice3.Goety.common.entities.ally.Summoned)) {
                // 每 100 tick（5 秒）且有 1/20 概率（5%）施加效果
                if (this.tickCount % 100 == 0 && this.getRandom().nextInt(20) == 0) {
                    entity.addEffect(new MobEffectInstance(GoetyEffects.ILLAGUE.get(), 2000, 0, false, false));
                    // 瘟疫持续 2000 tick（100 秒），等级 0，不显示粒子，不显示图标
                }
            }
        }

        // 瘟疫粒子效果，仅在客户端生成瘟疫粒子
        if (this.level().isClientSide) {// 只在客户端执行，避免服务端同步问题
            if (this.tickCount % 20 == 0) {// 每 20 tick（1 秒）生成一次粒子
                for (int i = 0; i < 8; ++i) {
                    // 在实体周围随机位置生成瘟疫粒子
                    this.level().addParticle(ModParticleTypes.PLAGUE_EFFECT.get(), this.getRandomX(0.5D),
                            this.getRandomY(), this.getRandomZ(0.5D), 0.0D, 0.5D, 0.0D);
                    // 粒子向上飘动（Y 轴速度 0.5）
                }
            }
        }
    }

    public void pickUpItem(ItemEntity pItemEntity) {
        // 拾取物品的方法，重写了此方法以实现免疫拾取TieredItem
        // TieredItem 是分级物品（如不同材质的装备），征服者仆从不拾取这些物品
        if (!(pItemEntity.getItem().getItem() instanceof TieredItem)) {
            super.pickUpItem(pItemEntity);// 如果不是分级物品，则调用父类方法正常拾取
        }
        // 如果是分级物品，则直接忽略（不拾取）
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        Item item = itemstack.getItem();
        ItemStack itemstack2 = this.getMainHandItem();
        if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
            if (item instanceof CrossbowItem) {
                this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
                this.dropEquipment(EquipmentSlot.MAINHAND, itemstack2.copyAndClear());
                this.setItemSlot(EquipmentSlot.MAINHAND, itemstack.copyWithCount(1));
                this.setGuaranteedDrop(EquipmentSlot.MAINHAND);
                for (int i = 0; i < 7; ++i) {
                    double d0 = this.random.nextGaussian() * 0.02D;
                    double d1 = this.random.nextGaussian() * 0.02D;
                    double d2 = this.random.nextGaussian() * 0.02D;
                    this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
                }
                if (!pPlayer.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(pPlayer, pHand);
    }

    @Override // @Override注解
    protected SoundEvent getCastingSoundEvent() {
        // 获取施法音效的方法
        return null;// 征服者仆从使用弩攻击而非施法，所以返回 null（没有施法音效）
    }

    protected void defineSynchedData() {
        // 定义同步数据的方法，用于在客户端和服务端之间同步实体数据
        super.defineSynchedData();// 先调用父类方法同步父类数据
        this.entityData.define(IS_CHARGING_CROSSBOW, false);
        // 定义 IS_CHARGING_CROSSBOW 用于同步弩的装填状态，初始值为 false（未装填）
    }

    public boolean canFireProjectileWeapon(ProjectileWeaponItem p_230280_1_) {
        // 判断是否可以使用射弹武器的方法
        return p_230280_1_ == Items.CROSSBOW;// 只允许使用弩（CROSSBOW）作为射弹武器
    }

    public boolean isChargingCrossbow() {
        // 判断是否正在装填弩的方法
        return this.entityData.get(IS_CHARGING_CROSSBOW);// 从同步数据管理器获取装填状态
    }

    public void setChargingCrossbow(boolean pIsCharging) {
        // 设置弩装填状态的方法
        this.entityData.set(IS_CHARGING_CROSSBOW, pIsCharging);
        // 将装填状态设置到同步数据管理器，这样客户端也能看到变化
    }

    public void onCrossbowAttackPerformed() {
        // 弩攻击执行后的回调方法
        this.noActionTime = 0;// 重置无行动时间计数器，防止 AI 停止活动
    }

    public IllagerServantArmPose getArmPose() {//原本为刌民ArmPose，改为刌民仆从ArmPose
        // 获取手臂姿势的方法，用于渲染器判断显示哪种手臂动作
        if (this.isChargingCrossbow()) {// 如果正在装填弩
            return IllagerServantArmPose.CROSSBOW_CHARGE;// 返回装填姿势（双手持弩拉弦）
        } else if (this.isHolding(item -> item.getItem() instanceof CrossbowItem)) {
            // 如果手持弩但未装填
            return IllagerServantArmPose.CROSSBOW_HOLD;// 返回持弩姿势（单手持弩）
        } else {
            // 既没有装填也没有持弩
            return this.isAggressive() ? IllagerServantArmPose.ATTACKING : IllagerServantArmPose.NEUTRAL;
            // 如果处于攻击状态则返回攻击姿势，否则返回中立姿势
        }
    }

    public float getWalkTargetValue(BlockPos pPos, LevelReader pLevel) {
        // 获取行走目标价值的方法，影响路径寻找时的偏好
        BlockState blockstate = pLevel.getBlockState(pPos.below());// 获取脚下方块的状态
        // 如果不是草地或沙子，则返回较低的价值（不喜欢走这些路）
        return !blockstate.is(Blocks.GRASS_BLOCK) && !blockstate.is(Blocks.SAND)
                ? 0.5F - pLevel.getPathfindingCostFromLightLevels(pPos)// 光照也会影响寻路成本
                : 10.0F;// 如果是草地或沙子，返回高价值（优先选择这些路径）
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_33282_, DifficultyInstance p_33283_,
            MobSpawnType p_33284_, @Nullable SpawnGroupData p_33285_, @Nullable CompoundTag p_33286_) {
        // 实体生成完成时的处理方法，用于初始化刚生成的实体
        RandomSource randomsource = p_33282_.getRandom();// 获取随机数生成器
        this.populateDefaultEquipmentSlots(randomsource, p_33283_);// 装备默认装备（弩）
        this.populateDefaultEquipmentEnchantments(randomsource, p_33283_);// 为装备附魔
        return super.finalizeSpawn(p_33282_, p_33283_, p_33284_, p_33285_, p_33286_);
        // 返回父类的处理结果
    }

    protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance pDifficulty) {
        // 装备默认装备槽位的方法，决定实体生成时会携带什么装备
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.CROSSBOW));
        // 在主手装备一把弩
    }

    protected void enchantSpawnedWeapon(RandomSource randomsource, float p_241844_1_) {
        // 为生成的武器附魔的方法，根据难度有一定概率附魔
        super.enchantSpawnedWeapon(randomsource, p_241844_1_);// 先调用父类方法进行基础附魔
        ItemStack itemstack = this.getMainHandItem();// 获取主手物品
        if (itemstack.getItem() == Items.CROSSBOW) {// 如果主手物品是弩
            Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemstack);// 获取当前附魔列表
            map.putIfAbsent(Enchantments.PIERCING, 4);// 如果没有穿透附魔，则添加穿透 IV
            EnchantmentHelper.setEnchantments(map, itemstack);// 应用附魔到物品上
            this.setItemSlot(EquipmentSlot.MAINHAND, itemstack);// 重新设置带附魔的弩到主手
        }
    }

    protected SoundEvent getAmbientSound() {
        // 获取环境音效的方法，实体空闲时会随机播放
        return ModSounds.CONQUILLAGER_AMBIENT.get();// 返回征服者的环境音效（从 Goety 模组的声音注册表获取）
    }

    protected SoundEvent getDeathSound() {
        // 获取死亡音效的方法
        return ModSounds.CONQUILLAGER_DEATH.get();// 返回征服者的死亡音效
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        // 获取受伤音效的方法
        return ModSounds.CONQUILLAGER_HURT.get();// 返回征服者的受伤音效
    }

    public void performRangedAttack(LivingEntity pTarget, float pDistanceFactor) {
        // 执行远程攻击的方法，由 RangedAttackMob 接口要求
        this.performCrossbowAttack(this, 1.6F);// 调用弩攻击方法，1.6F 是弹药速度
    }

    public void shootCrossbowProjectile(LivingEntity shooter, ItemStack itemStack, Projectile projectileEntity,
            float p_230284_4_) {
        // 发射弩箭抛射物的方法（不带目标版本）
        this.shootCrossbowProjectile(this, shooter, projectileEntity, p_230284_4_, 1.6F);
        // 调用带速度的重载方法，1.6F 是默认弹药速度
    }

    public void performCrossbowAttack(LivingEntity shooter, float velocity) {
        // 执行弩攻击的核心方法
        InteractionHand hand = ProjectileUtil.getWeaponHoldingHand(shooter, item -> item instanceof CrossbowItem);
        // 获取持有弩的手（主手或副手）
        ItemStack itemstack = shooter.getItemInHand(hand);// 获取弩物品
        if (shooter.isHolding(itemStack -> itemStack.getItem() instanceof CrossbowItem)) {
            // 如果确实持有弩
            CrossbowItem.performShooting(shooter.level(), shooter, hand, itemstack, velocity,
                    (float) (14 - shooter.level().getDifficulty().getId() * 4));
            // 执行弩的射击逻辑：
            // - velocity: 弹药速度
            // - 偏差计算：14 - 难度*4（和平 14，简单 10，普通 6，困难 2），难度越高越精准
        }

        this.onCrossbowAttackPerformed();// 攻击完成后调用回调方法
    }

    public void shootCrossbowProjectile(LivingEntity shooter, LivingEntity target, Projectile projectileEntity,
            float p_234279_4_, float velocity) {
        // 向目标发射弩箭抛射物的方法（带目标版本）
        double d0 = target.getX() - shooter.getX();// X 轴相对位置
        double d1 = target.getY(0.5F) - shooter.getY(0.5F);// Y 轴相对位置（瞄准眼部高度）
        double d2 = target.getZ() - shooter.getZ();// Z 轴相对位置
        Vector3f vector3f = this.getProjectileShotVector(shooter, new Vec3(d0, d1, d2), p_234279_4_);
        // 计算抛射物发射向量（考虑重力等因素）
        projectileEntity.shoot(vector3f.x(), vector3f.y(), vector3f.z(), velocity,
                (float) (14 - shooter.level().getDifficulty().getId() * 4));
        // 发射抛射物，包含速度和偏差
        shooter.playSound(SoundEvents.CROSSBOW_SHOOT, 1.0F, 1.0F / (shooter.getRandom().nextFloat() * 0.4F + 0.8F));
        // 播放弩射击的声音，音调随机（0.8~1.2 之间）
    }

    public ItemStack getProjectile(ItemStack pShootable) {
        // 获取弩使用的弹药（抛射物）的方法
        int difficulty = this.level().getCurrentDifficultyAt(this.blockPosition()).getDifficulty().getId();
        // 获取当前位置的区域难度
        return MobUtil.createFirework(difficulty * 2, DyeColor.values());
        // 创建烟花火箭作为弹药：
        // - 飞行高度：难度*2
        // - 颜色：所有染料颜色（彩虹色）
    }

    public SoundEvent getCelebrateSound() {
        // 获取庆祝音效的方法（raid 胜利时播放）
        return ModSounds.CONQUILLAGER_CELEBRATE.get();// 返回征服者的庆祝音效
    }

    public static class IllagerCrossbowGoal<T extends PathfinderMob & RangedAttackMob & CrossbowAttackMob>
            extends Goal {
        // 灾厄村民弩攻击 AI 目标内部类
        // 泛型限制：必须是
        // PathfinderMob（可寻路生物）、RangedAttackMob（远程攻击生物）、CrossbowAttackMob（弩攻击生物）

        private final T mob;// 持有实体引用
        private CrossbowState crossbowState = CrossbowState.UNCHARGED;// 弩的状态机，初始为未装填
        private final double speedModifier;// 移动速度修正值
        private final float attackRadiusSqr;// 攻击半径的平方（用于距离判断，避免开方运算）
        private int seeTime;// 看到目标的持续时间计数器
        private int attackDelay;// 攻击延迟计数器

        public IllagerCrossbowGoal(T p_i50322_1_, double p_i50322_2_, float p_i50322_4_) {
            // 构造函数
            // @param p_i50322_1_ 实体实例
            // @param p_i50322_2_ 移动速度倍数
            // @param p_i50322_4_ 攻击半径
            this.mob = p_i50322_1_;
            this.speedModifier = p_i50322_2_;
            this.attackRadiusSqr = p_i50322_4_ * p_i50322_4_;// 存储平方值
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));// 设置 AI 标志：控制移动和看向目标
        }

        public boolean canUse() {
            // 判断此 AI 目标是否可以开始执行
            return this.isValidTarget() && this.isHoldingCrossbow();
            // 需要同时满足：有有效目标 + 手持弩
        }

        private boolean isHoldingCrossbow() {
            // 判断是否手持弩的辅助方法
            return this.mob.isHolding(is -> is.getItem() instanceof CrossbowItem);
            // 检查主手或副手是否持有弩物品
        }

        public boolean canContinueToUse() {
            // 判断是否可以继续执行此 AI 目标
            return this.isValidTarget() && (this.canUse() || !this.mob.getNavigation().isDone())
                    && this.isHoldingCrossbow();
            // 需要满足：有有效目标 + （可以开始新任务或寻路未完成）+ 手持弩
        }

        private boolean isValidTarget() {
            // 判断目标是否有效的辅助方法
            return this.mob.getTarget() != null && this.mob.getTarget().isAlive();
            // 目标必须存在且存活
        }

        public void start() {
            // AI 目标开始执行时的初始化方法
            super.start();// 调用父类方法
            this.mob.setAggressive(true);// 设置攻击状态为 true，触发攻击动画和音效
        }

        public void stop() {
            // AI 目标停止执行时的清理方法
            super.stop();// 调用父类方法
            this.mob.setAggressive(false);// 清除攻击状态
            this.mob.setTarget((LivingEntity) null);// 清除目标
            this.seeTime = 0;// 重置看见时间计数器
            if (this.mob.isUsingItem()) {// 如果正在使用物品（装填弩）
                this.mob.stopUsingItem();// 停止使用物品
                this.mob.setChargingCrossbow(false);// 清除装填状态
                CrossbowItem.setCharged(this.mob.getUseItem(), false);// 将弩标记为未装填
            }
        }

        public void tick() {
            // 每刻调用此方法更新 AI 状态，这是弩攻击 AI 的核心逻辑
            LivingEntity livingentity = this.mob.getTarget();// 获取当前目标
            if (livingentity != null) {
                // 检查是否能看见目标
                boolean canSeeEnemy = this.mob.getSensing().hasLineOfSight(livingentity);

                if (canSeeEnemy) {
                    ++this.seeTime;// 如果能看见目标，累计看见时间
                } else {
                    this.seeTime = 0;// 否则重置看见时间
                }

                // 计算与目标的距离
                double distanceSq = this.mob.distanceToSqr(livingentity);// 距离的平方
                double distance = Mth.sqrt((float) distanceSq);// 实际距离

                // 如果距离过近（小于 5 格）
                if (distance <= 5.0F) {
                    if (this.isWalkable()) {// 如果脚下可以行走
                        // 向后撤退，装填时后退速度慢（-0.5），未装填时快速后退（-3.0）
                        this.mob.getMoveControl().strafe(mob.isUsingItem() ? -0.5F : -3.0F, 0);
                    }
                }

                ItemStack activeStack = this.mob.getUseItem();// 获取正在使用的物品（装填中的弩）
                // 判断是否需要向目标移动
                boolean shouldMoveTowardsEnemy = ((distanceSq > (double) this.attackRadiusSqr) || this.seeTime < 5)
                        && this.attackDelay == 0;
                // 条件：超出攻击范围 OR 看见目标时间不足 5 tick，且不在攻击延迟中

                if (shouldMoveTowardsEnemy) {
                    // 需要移动时
                    double speedChange = this.isCrossbowUncharged() ? this.speedModifier : this.speedModifier * 0.5D;
                    // 弩未装填时用正常速度，已装填时用一半速度（保持警惕）
                    this.mob.getNavigation().moveTo(livingentity, speedChange);
                } else {
                    // 不需要移动时
                    this.mob.getNavigation().stop();// 停止寻路
                }

                // 让实体始终看着目标
                this.mob.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
                // 水平最大旋转 30 度/刻，垂直最大旋转 30 度/刻

                // 根据弩的状态执行不同逻辑
                if (this.crossbowState == CrossbowState.UNCHARGED && !CrossbowItem.isCharged(activeStack)) {
                    // 状态 1：未装填且弩未充能
                    if (canSeeEnemy) {// 如果能看见敌人
                        this.mob.startUsingItem(
                                ProjectileUtil.getWeaponHoldingHand(this.mob, item -> item instanceof CrossbowItem));
                        // 开始使用弩（装填动作）
                        this.crossbowState = CrossbowState.CHARGING;// 切换到装填状态
                        this.mob.setChargingCrossbow(true);// 设置装填标志
                    }
                } else if (this.crossbowState == CrossbowState.CHARGING) {
                    // 状态 2：正在装填
                    if (!this.mob.isUsingItem()) {// 如果停止了使用物品（被打断）
                        this.crossbowState = CrossbowState.UNCHARGED;// 回到未装填状态
                    }

                    int i = this.mob.getTicksUsingItem();// 获取使用物品的持续时间
                    // 如果装填时间达到要求或弩已充能
                    if (i >= CrossbowItem.getChargeDuration(activeStack) || CrossbowItem.isCharged(activeStack)) {
                        this.mob.releaseUsingItem();// 释放使用物品（完成装填）
                        this.crossbowState = CrossbowState.CHARGED;// 切换到已装填状态
                        this.attackDelay = 20 + this.mob.getRandom().nextInt(20);
                        // 设置攻击延迟 20~40 tick 的随机值（模拟反应时间）
                        if (this.mob.getOffhandItem().getItem() instanceof FireworkRocketItem) {
                            // 如果副手有烟花火箭（作为弹药）
                            this.mob.startUsingItem(InteractionHand.OFF_HAND);// 开始使用副手物品
                        }
                        this.mob.setChargingCrossbow(false);// 清除装填标志
                    }
                } else if (this.crossbowState == CrossbowState.CHARGED) {
                    // 状态 3：已装填（等待射击）
                    --this.attackDelay;// 减少攻击延迟
                    if (this.attackDelay == 0) {// 延迟结束时
                        this.crossbowState = CrossbowState.READY_TO_ATTACK;// 切换到准备攻击状态
                    }
                } else if (this.crossbowState == CrossbowState.READY_TO_ATTACK && canSeeEnemy) {
                    // 状态 4：准备攻击且能看见敌人
                    this.mob.performRangedAttack(livingentity, 1.0F);// 执行远程攻击
                    CrossbowItem.setCharged(this.mob.getItemInHand(
                            ProjectileUtil.getWeaponHoldingHand(this.mob, item -> item instanceof CrossbowItem)),
                            false);
                    // 将弩设置为未充能状态（消耗弹药）
                    this.crossbowState = CrossbowState.UNCHARGED;// 回到未装填状态，开始下一轮循环
                }

            }
        }

        private boolean isWalkable() {
            // 判断当前位置是否可以行走的辅助方法
            PathNavigation pathnavigator = this.mob.getNavigation();// 获取路径导航器
            NodeEvaluator nodeprocessor = pathnavigator.getNodeEvaluator();// 获取节点评估器
            // 检查前方 1 格的方块类型是否为可行走（WALKABLE）
            return nodeprocessor.getBlockPathType(this.mob.level(), Mth.floor(this.mob.getX() + 1.0D),
                    Mth.floor(this.mob.getY()), Mth.floor(this.mob.getZ() + 1.0D)) == BlockPathTypes.WALKABLE;
        }

        private boolean isCrossbowUncharged() {
            // 判断弩是否未装填的辅助方法
            return this.crossbowState == CrossbowState.UNCHARGED;// 检查状态机是否为 UNCHARGED
        }

        enum CrossbowState {
            // 弩的状态机枚举
            UNCHARGED, // 未装填：弩没有弹药，准备开始装填
            CHARGING, // 装填中：正在拉弦装填弹药
            CHARGED, // 已装填：装填完成，等待射击时机
            READY_TO_ATTACK;// 准备攻击：延迟结束，可以立即射击
        }
    }
}