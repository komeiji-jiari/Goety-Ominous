package com.qiuyue.goetyominous.common.entities.ally.sar;

import com.Polarice3.Goety.common.entities.ally.undead.skeleton.AbstractSkeletonServant;
import com.google.common.collect.Maps;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.teamabnormals.savage_and_ravage.common.entity.ai.goal.ImprovedCrossbowGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;
import java.util.Map;

public class SkeletonVillagerServant extends AbstractSkeletonServant implements CrossbowAttackMob {
    private static final EntityDataAccessor<Boolean> DATA_CHARGING_STATE = SynchedEntityData.defineId(SkeletonVillagerServant.class, EntityDataSerializers.BOOLEAN);
    private final ImprovedCrossbowGoal<SkeletonVillagerServant> aiCrossBow = new ImprovedCrossbowGoal<>(this, 1.0D, 8.0F, 5.0D);
    private final MeleeAttackGoal aiMelee = new MeleeAttackGoal(this, 1.2D, false) {
        @Override
        public void stop() {
            super.stop();
            SkeletonVillagerServant.this.setAggressive(false);
        }

        @Override
        public void start() {
            super.start();
            SkeletonVillagerServant.this.setAggressive(true);
        }
    };

    public SkeletonVillagerServant(EntityType<? extends SkeletonVillagerServant> type, Level worldIn) {
        super(type, worldIn);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.SkeletonVillagerServantMovementSpeed.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.SkeletonVillagerServantFollowRange.get())
                .add(Attributes.MAX_HEALTH, AttributesConfig.SkeletonVillagerServantHealth.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.SkeletonVillagerServantDamage.get());
    }

    @Override
    public void reassessWeaponGoal() {
        if (!this.level().isClientSide) {
            this.goalSelector.removeGoal(this.meleeGoal);
            this.goalSelector.removeGoal(this.aiCrossBow);
            ItemStack itemstack = this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, item -> item instanceof CrossbowItem));
            if (itemstack.getItem() instanceof CrossbowItem) {
                this.goalSelector.addGoal(4, this.aiCrossBow);
            } else {
                super.reassessWeaponGoal();
            }
        }
    }

    @Override
    protected SoundEvent getStepSound() {
        return SoundEvents.SKELETON_STEP;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.SKELETON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SKELETON_DEATH;
    }

    @Override
    public ItemStack getPickedResult(HitResult target) {
        return new ItemStack(com.qiuyue.goetyominous.common.items.sar.SarItems.SKELETON_VILLAGER_SERVANT_SPAWN_EGG.get());
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        spawnDataIn = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.populateDefaultEquipmentSlots(worldIn.getRandom(), difficultyIn);
        this.populateDefaultEquipmentEnchantments(worldIn.getRandom(), difficultyIn);
        return spawnDataIn;
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        ItemStack itemstack = new ItemStack(Items.CROSSBOW);
        if (random.nextInt(300) == 0) {
            Map<Enchantment, Integer> map = Maps.newHashMap();
            map.put(Enchantments.PIERCING, 1);
            EnchantmentHelper.setEnchantments(map, itemstack);
        }
        this.setItemSlot(EquipmentSlot.MAINHAND, itemstack);
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    @Override
    public EntityType<?> getVariant(@Nullable Player player, Level level, BlockPos blockPos) {
        return com.qiuyue.goetyominous.common.init.sar.SarEntityRegistry.SKELETON_VILLAGER_SERVANT.get();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_CHARGING_STATE, false);
    }

    @Override
    public void setChargingCrossbow(boolean isCharging) {
        this.entityData.set(DATA_CHARGING_STATE, isCharging);
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        this.performCrossbowAttack(this, 1.6F);
    }

    public boolean isCharging() {
        return this.entityData.get(DATA_CHARGING_STATE);
    }

    @Override
    public void onCrossbowAttackPerformed() {
        this.noActionTime = 0;
    }

    @Override
    public void shootCrossbowProjectile(LivingEntity p_230284_1_, ItemStack p_230284_2_, Projectile p_230284_3_, float p_230284_4_) {
        this.shootCrossbowProjectile(this, p_230284_1_, p_230284_3_, p_230284_4_, 1.6F);
    }
}
