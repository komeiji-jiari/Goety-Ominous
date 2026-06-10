package com.qiuyue.someillagerservants.common.entities.ally.illager;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.common.entities.ai.ModMeleeAttackGoal;
import com.Polarice3.Goety.common.entities.ally.illager.SpellcasterIllagerServant;
import com.Polarice3.Goety.common.magic.spells.SoulHealSpell;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.RandomUtil;
import com.google.common.collect.Maps;
import com.qiuyue.someillagerservants.SomeIllagerServants;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import com.Polarice3.Goety.api.items.magic.IWand;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.particles.ParticleTypes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * 巡查官仆从实体类
 * 原本继承自猎杀实体类，但是猎杀实体类的功能已经基本被刌民仆从类囊括了，故改为继承自猎杀实体类的父类——SpellcasterIllager的仆从版本类——SpellcasterIllagerServant
 * 继承自 SpellcasterIllagerServant(施法灾厄村民仆从)
 */
public class InquillagerServant extends SpellcasterIllagerServant {
    private static final EntityDataAccessor<Integer> DATA_TYPE_ID = SynchedEntityData.defineId(InquillagerServant.class,
            EntityDataSerializers.INT);
    public static final Map<Integer, ResourceLocation> TEXTURE_BY_TYPE = Util.make(Maps.newHashMap(), (map) -> {
        map.put(0, new ResourceLocation(SomeIllagerServants.MOD_ID, "textures/entity/illager/inquillager.png"));
        map.put(1, new ResourceLocation(SomeIllagerServants.MOD_ID, "textures/entity/illager/inquillager_2.png"));
        map.put(2, new ResourceLocation(SomeIllagerServants.MOD_ID, "textures/entity/illager/inquillager_3.png"));
    });
    public int coolDown;// 公共变量coolDown
    public int healTimes;// 公共变量healTimes

    public InquillagerServant(EntityType<? extends InquillagerServant> p_i48556_1_, Level p_i48556_2_) {
        super(p_i48556_1_, p_i48556_2_);
        this.coolDown = 0;
        this.healTimes = 0;// 实体构造函数，初始化公共变量
    }

    public ResourceLocation getResourceLocation() {// 供渲染器使用的方法，返回当前实体应使用的纹理资源路径
        // 根据 outfitType（服装类型）从 TEXTURE_BY_TYPE 映射表中获取对应的纹理
        // 如果找不到则默认使用第 0 号纹理
        return TEXTURE_BY_TYPE.getOrDefault(this.getOutfitType(), TEXTURE_BY_TYPE.get(0));
    }

    public boolean isMainWeapon(ItemStack itemStack) {
        return itemStack.getItem() instanceof SwordItem || itemStack.is(ItemTags.SWORDS);
    }
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        Item item = itemstack.getItem();
        ItemStack itemstack2 = this.getMainHandItem();
        if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
            if (!(pPlayer.getOffhandItem().getItem() instanceof IWand)) {
                if (this.isMainWeapon(itemstack)) {
                    this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
                    this.setItemSlot(EquipmentSlot.MAINHAND, itemstack.copy());
                    this.dropEquipment(EquipmentSlot.MAINHAND, itemstack2);
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
        }
        return super.mobInteract(pPlayer, pHand);
    }


    protected void registerGoals() {// 实体 AI 目标注册方法，决定了实体会执行哪些行为
        super.registerGoals();// 调用父类（SpellcasterIllagerServant）的 registerGoals 方法以继承施法者刌民的所有基础行为
        this.goalSelector.addGoal(1, new CastingSpellGoal());// 添加施法目标，优先级为 1（高优先级），让实体能够看着目标准备施法
        this.goalSelector.addGoal(2, new HealingSelfSpellGoal());// 添加治疗自己目标，优先级为 2，当生命值低于一半时自动治疗
        this.goalSelector.addGoal(2, new ThrowPotionGoal(this));// 添加投掷药水目标，优先级为 2，会向敌人投掷伤害/治疗药水
        this.goalSelector.addGoal(2, new AttackGoal(this));// 添加近战攻击目标，优先级为 2，使用铁剑进行近战攻击
    }

    public static AttributeSupplier.Builder setCustomAttributes() {// 自定义属性构造方法，在主类中通过 EntityAttributeCreationEvent
                                                                   // 调用以注册实体属性
        return Mob.createMobAttributes()// 创建生物属性构建器
                .add(Attributes.FOLLOW_RANGE, 32.0D)// 设置追踪范围为 32 格
                .add(Attributes.MAX_HEALTH, AttributesConfig.InquillagerHealth.get())// 最大生命值从 Goety 配置文件中读取
                .add(Attributes.ARMOR, AttributesConfig.InquillagerArmor.get())// 护甲值从配置文件中读取
                .add(Attributes.MOVEMENT_SPEED, 0.35D)// 移动速度设为 0.35（比玩家稍快）
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.InquillagerDamage.get());// 攻击伤害从配置文件中读取
    }

    public void setConfigurableAttributes() {// 另一个只涉及可配置属性的构造方法，用于动态调整已存在实体的属性
        // 使用 MobUtil 工具类设置基础属性值
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.InquillagerHealth.get());// 设置最大生命值
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.InquillagerArmor.get());// 设置护甲值
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE),
                AttributesConfig.InquillagerDamage.get());// 设置攻击伤害
    }

    @Override
    protected void defineSynchedData() {// 定义同步数据的方法，用于在客户端和服务端之间同步实体数据
        super.defineSynchedData();// 先调用父类方法同步父类数据
        this.entityData.define(DATA_TYPE_ID, 0);// 定义 DATA_TYPE_ID 用于同步 outfitType（服装类型），初始值为 0
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {// 从 NBT 标签读取额外保存数据的方法，用于加载存档中的实体数据
        super.readAdditionalSaveData(pCompound);// 先读取父类数据
        this.coolDown = pCompound.getInt("Cooldown");// 从 NBT 中读取冷却时间
        this.healTimes = pCompound.getInt("HealTimes");// 从 NBT 中读取治疗次数
        if (pCompound.contains("Outfit")) {// 如果 NBT 中包含服装类型数据
            this.setOutfitType(pCompound.getInt("Outfit"));// 设置服装类型
        }
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {// 添加额外数据到 NBT 标签的方法，用于保存实体数据到存档
        super.addAdditionalSaveData(pCompound);// 先保存父类数据
        pCompound.putInt("Cooldown", this.coolDown);// 将冷却时间保存到 NBT
        pCompound.putInt("HealTimes", this.healTimes);// 将治疗次数保存到 NBT
        pCompound.putInt("Outfit", this.getOutfitType());// 将服装类型保存到 NBT
    }

    public int getOutfitType() {// 获取服装类型的方法
        return this.entityData.get(DATA_TYPE_ID);// 从同步数据管理器中获取 DATA_TYPE_ID 对应的值
    }

    public void setOutfitType(int pType) {// 设置服装类型的方法
        // 检查传入的类型是否有效（必须在 0 到最大类型数之间）
        if (pType < 0 || pType >= this.OutfitTypeNumber() + 1) {
            pType = this.random.nextInt(this.OutfitTypeNumber());// 如果无效则随机生成一个有效类型
        }

        this.entityData.set(DATA_TYPE_ID, pType);// 将服装类型设置到同步数据管理器
    }

    public int OutfitTypeNumber() {// 获取服装类型总数的方法
        return TEXTURE_BY_TYPE.size();// 返回 TEXTURE_BY_TYPE 映射表的大小，即纹理变种数量
    }

    @Override
    public void tick() {// 实体刻更新方法，每游戏刻（1/20 秒）调用一次
        super.tick();// 先调用父类的 tick 方法执行基础更新
        if (this.coolDown > 0) {
            --this.coolDown;// 如果冷却时间大于 0，则每刻减少 1
        }
    }

    protected void customServerAiStep() {// 服务端专属 AI 步骤方法，仅在服务端调用
        super.customServerAiStep();// 调用父类方法，目前为空实现，可用于添加服务端专属 AI 逻辑
    }

    public void setCoolDown(int coolDown) {// 设置冷却时间的方法
        this.coolDown = coolDown;// 直接将传入值赋给 coolDown 字段
    }

    public int getCoolDown() {// 获取冷却时间的方法
        return coolDown;// 返回当前冷却时间值
    }

    public void increaseHealTimes() {// 增加治疗次数的方法
        ++this.healTimes;// 将 healTimes 字段加 1
    }

    public void setHealTimes(int healTimes) {// 设置治疗次数的方法
        this.healTimes = healTimes;// 直接将传入值赋给 healTimes 字段
    }

    public int getHealTimes() {// 获取治疗次数的方法
        return healTimes;// 返回当前治疗次数值
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {// 获取施法音效的方法
        return SoundEvents.EVOKER_CAST_SPELL;// 返回唤魔者的施法音效
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
            MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        // 实体生成完成时的处理方法，用于初始化刚生成的实体，先调用一次父类finalizeSpawn结果
        SpawnGroupData ilivingentitydata = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        // 将此实体的路径导航设置为可以开门
        ((GroundPathNavigation) this.getNavigation()).setCanOpenDoors(true);
        RandomSource randomSource = pLevel.getRandom();
        this.populateDefaultEquipmentSlots(randomSource, pDifficulty);// 装备默认装备（铁剑）
        this.populateDefaultEquipmentEnchantments(randomSource, pDifficulty);// 为装备附魔
        this.setOutfitType(RandomUtil.nextInt(pLevel.getRandom(), this.OutfitTypeNumber()));// 随机设置服装类型
        return ilivingentitydata;// 返回生成数据
    }

    public boolean hurt(@Nonnull DamageSource source, float amount) {// 实体受伤时的处理方法
        // 检查伤害来源是否属于女巫抗性标签（WITCH_RESISTANT_TO），即原版女巫拥有抗性的伤害类型。实际上也可以用forge的魔法伤害tag
        if (source.is(DamageTypeTags.WITCH_RESISTANT_TO)) {
            return false;// 如果是，则完全免疫该伤害（类似女巫的抗伤特性）
        } else {
            return super.hurt(source, amount);// 否则调用父类的受伤方法处理正常伤害
        }
    }

    protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance pDifficulty) {
        // 装备默认装备槽位的方法，决定实体生成时会携带什么装备
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));// 在主手装备一把铁剑
    }

    protected void enchantSpawnedWeapon(RandomSource randomSource, float p_241844_1_) {
        // 为生成的武器附魔的方法，根据难度有一定概率附魔
        super.enchantSpawnedWeapon(randomSource, p_241844_1_);// 先调用父类方法进行基础附魔
        ItemStack itemstack = this.getMainHandItem();// 获取主手物品
        if (itemstack.getItem() == Items.IRON_SWORD) {// 如果主手物品是铁剑
            Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemstack);// 获取当前附魔列表
            map.putIfAbsent(Enchantments.FIRE_ASPECT, 2);// 如果没有火焰附加附魔，则添加火焰附加 II
            EnchantmentHelper.setEnchantments(map, itemstack);// 应用附魔到物品上
            this.setItemSlot(EquipmentSlot.MAINHAND, itemstack);// 重新设置带附魔的物品到主手
        }
    }

    @Override
    public IllagerServantArmPose getArmPose() {// 获取手臂姿势的方法，用于渲染器判断显示哪种手臂动作
        if (this.isCastingSpell()) {// 如果正在施法
            return IllagerServantArmPose.SPELLCASTING;// 返回施法姿势（双手抬起）
        } else if (this.isAggressive()) {// 如果不是施法但处于攻击状态
            return IllagerServantArmPose.ATTACKING;// 返回攻击姿势（举起武器）
        } else {// 既没有施法也没有攻击
            return this.isCelebrating() ? IllagerServantArmPose.CELEBRATING : IllagerServantArmPose.CROSSED;
            // 如果在庆祝（ raid 胜利等）则返回庆祝姿势，否则返回交叉手臂的待机姿势
        }
    }

    protected SoundEvent getAmbientSound() {// 获取环境音效的方法，实体空闲时会随机播放
        return ModSounds.INQUILLAGER_AMBIENT.get();// 返回巡查官的环境音效（从 Goety 模组的声音注册表中获取）
    }

    protected SoundEvent getDeathSound() {// 获取死亡音效的方法
        return ModSounds.INQUILLAGER_DEATH.get();// 返回巡查官的死亡音效
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {// 获取受伤音效的方法
        return ModSounds.INQUILLAGER_HURT.get();// 返回巡查官的受伤音效
    }

    public SoundEvent getCelebrateSound() {// 获取庆祝音效的方法（raid 胜利时播放）
        return ModSounds.INQUILLAGER_CELEBRATE.get();// 返回巡查官的庆祝音效
    }

    class CastingSpellGoal extends SpellcasterCastingSpellGoal {// 施法目标内部类，继承自施法者施法目标基类
        // 这是一个准备施法的目标，会让实体看着目标并播放施法前摇动画
        private CastingSpellGoal() {
            // 私有构造函数，只能通过外部类实例化
        }

        public void tick() {// 每刻调用此方法更新 AI 状态
            if (InquillagerServant.this.getTarget() != null) {// 如果有目标
                // 让实体的头部看向目标，最大旋转角度由 getMaxHeadYRot 和 getMaxHeadXRot 决定
                InquillagerServant.this.getLookControl().setLookAt(InquillagerServant.this.getTarget(),
                        (float) InquillagerServant.this.getMaxHeadYRot(),
                        (float) InquillagerServant.this.getMaxHeadXRot());
            }
        }
    }

    class HealingSelfSpellGoal extends SpellcasterUseSpellGoal {// 自我治疗法术目标内部类，继承自施法者使用法术目标基类
        // 这个目标会在生命值低于一半时自动治疗自己
        private HealingSelfSpellGoal() {
            // 私有构造函数
        }

        public boolean canUse() {// 判断此目标是否可以开始执行
            if (!super.canUse()) {// 先检查父类条件是否满足（如是否在冷却中等）
                return false;// 不满足则返回 false
            } else {
                // 检查当前生命值是否小于最大生命值的一半，并且冷却时间已经结束
                return InquillagerServant.this.getHealth() < InquillagerServant.this.getMaxHealth() / 2
                        && InquillagerServant.this.getCoolDown() <= 0;
            }
        }

        protected int getCastWarmupTime() {// 获取施法预热时间（tick 数）
            return 40;// 返回 40 tick（2 秒），这是施法前的准备时间
        }

        protected int getCastingTime() {// 获取施法持续时间（tick 数）
            return 40;// 返回 40 tick（2 秒），这是实际施法动作的时间
        }

        protected int getCastingInterval() {// 获取施法间隔时间（tick 数）
            return 20;// 返回 20 tick（1 秒），这是两次施法之间的间隔
        }

        protected void performSpellCasting() {// 实际执行法术效果的方法
            if (InquillagerServant.this.level() instanceof ServerLevel) {// 只在服务端执行，避免客户端同步问题
                new SoulHealSpell().mobSpellResult(InquillagerServant.this, ItemStack.EMPTY);// 使用灵魂治疗法术治疗自己
                if (InquillagerServant.this.getHealTimes() > 3) {// 如果治疗次数超过 3 次
                    InquillagerServant.this.setHealTimes(0);// 重置治疗次数为 0
                    InquillagerServant.this.setCoolDown(1000);// 设置长冷却时间 1000 tick（50 秒）
                } else {
                    InquillagerServant.this.increaseHealTimes();// 否则治疗次数加 1
                    InquillagerServant.this.setCoolDown(200);// 设置短冷却时间 200 tick（10 秒）
                }
            }
        }

        protected SoundEvent getSpellPrepareSound() {// 获取法术准备音效
            return SoundEvents.EVOKER_PREPARE_SUMMON;// 使用唤魔者的召唤准备音效
        }

        protected IllagerServantSpell getSpell() {// 获取要使用的法术类型
            return IllagerServantSpell.SUMMON_VEX;// 返回召唤恼鬼法术（虽然实际使用的是治疗法术，这里只是标识）
        }
    }

    static class ThrowPotionGoal extends Goal {// 投掷药水目标内部类，继承自 Goal 基类
        public int bombTimer;// 炸弹计时器，用于倒计时投掷动作
        public InquillagerServant inquillagerservant;// 持有外部类引用，用于访问实体数据

        public ThrowPotionGoal(InquillagerServant inquillagerservant) {
            // 构造函数，接收外部类实例
            this.inquillagerservant = inquillagerservant;
        }

        @Override
        public boolean canUse() {// 判断此 AI 目标是否可以开始执行
            if (this.inquillagerservant.getTarget() != null) {// 必须有目标
                LivingEntity livingEntity = this.inquillagerservant.getTarget();
                // 检查目标距离：必须大于 4 格且小于等于 10 格，并且视线无遮挡
                return this.inquillagerservant.distanceTo(livingEntity) > 4.0
                        && this.inquillagerservant.distanceTo(livingEntity) <= 10
                        && this.inquillagerservant.getSensing().hasLineOfSight(livingEntity);
            } else {
                return false;// 没有目标则不能执行
            }
        }

        @Override
        public boolean canContinueToUse() {// 判断是否可以继续执行此目标
            // 目标必须存在且未死亡
            return this.inquillagerservant.getTarget() != null && !this.inquillagerservant.getTarget().isDeadOrDying();
        }

        @Override
        public void stop() {// 停止执行此目标时的清理方法
            this.bombTimer = 0;// 重置计时器
        }

        @Override
        public void tick() {// 每刻调用此方法更新 AI 状态
            super.tick();// 调用父类 tick
            LivingEntity livingEntity = this.inquillagerservant.getTarget();
            if (livingEntity != null) {
                ++this.bombTimer;// 计时器每刻增加 1
                if (this.bombTimer >= 60) {// 当计时器达到 60 tick（3 秒）时执行投掷
                    // 计算目标的运动向量和位置
                    Vec3 vector3d = livingEntity.getDeltaMovement();
                    double d0 = livingEntity.getX() + vector3d.x - this.inquillagerservant.getX();// X 轴相对位置
                    double d1 = livingEntity.getEyeY() - (double) 1.1F - this.inquillagerservant.getY();// Y
                                                                                                        // 轴相对位置（瞄准眼部下方）
                    double d2 = livingEntity.getZ() + vector3d.z - this.inquillagerservant.getZ();// Z 轴相对位置
                    float f = Mth.sqrt((float) (d0 * d0 + d2 * d2));// 计算水平距离
                    Potion potion;// 声明药水变量
                    // 判断目标是否反转治疗和伤害效果（如亡灵生物）
                    if (livingEntity.isInvertedHealAndHarm()) {
                        potion = Potions.HEALING;// 对亡灵生物使用治疗药水（实际上会伤害它们）
                    } else {
                        potion = Potions.HARMING;// 对普通生物使用伤害药水
                    }
                    ThrownPotion potionentity = new ThrownPotion(this.inquillagerservant.level(),
                            this.inquillagerservant);// 创建喷溅药水实体
                    potionentity.setItem(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), potion));// 设置药水类型
                    potionentity.setXRot(potionentity.getXRot() - -20.0F);// 调整投掷角度（向上 20 度）
                    potionentity.shoot(d0, d1 + (double) (f * 0.2F), d2, 0.75F, 8.0F);// 发射药水，速度 0.75，偏差 8.0
                    if (!this.inquillagerservant.isSilent()) {// 如果实体不是静音状态
                        // 播放女巫投掷药水的声音
                        this.inquillagerservant.level().playSound((Player) null, this.inquillagerservant.getX(),
                                this.inquillagerservant.getY(), this.inquillagerservant.getZ(), SoundEvents.WITCH_THROW,
                                this.inquillagerservant.getSoundSource(), 1.0F,
                                0.8F + this.inquillagerservant.random.nextFloat() * 0.4F);
                    }
                    this.inquillagerservant.level().addFreshEntity(potionentity);// 将药水实体添加到世界中
                    this.bombTimer = 0;// 重置计时器，准备下一次投掷
                }
            }
        }
    }

    static class AttackGoal extends ModMeleeAttackGoal {// 近战攻击目标内部类，继承自 Goety 的模组近战攻击目标
        // 这是一个简化的近战 AI，使用 ModMeleeAttackGoal 而不是原版的 MeleeAttackGoal
        public AttackGoal(InquillagerServant p_i50577_2_) {
            // 构造函数，参数：实体实例、移动速度 1.0、不跟随目标（false）
            super(p_i50577_2_, 1.0D, false);
        }
    }
}
