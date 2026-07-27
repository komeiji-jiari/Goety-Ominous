package com.qiuyue.goetyominus.common.entities.ally.mobs;

import com.Polarice3.Goety.common.entities.ai.BackawayCrossbowGoal;
import com.qiuyue.goetyominus.common.entities.ally.neutral.AbstractPiglinServant;
import com.qiuyue.goetyominus.config.AttributesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.piglin.PiglinArmPose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;

public class PiglinHunterServant extends AbstractPiglinServant implements CrossbowAttackMob {

    private static final EntityDataAccessor<Boolean> DATA_IS_CHARGING_CROSSBOW = SynchedEntityData.defineId(PiglinHunterServant.class, EntityDataSerializers.BOOLEAN);

    public PiglinHunterServant(EntityType<? extends AbstractPiglinServant> type, Level level) {
        super(type, level);
        this.xpReward = 0;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.PiglinHunterServantHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.PiglinHunterServantArmor.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.PiglinHunterServantFollowRange.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.PiglinHunterServantMovementSpeed.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.PiglinHunterServantDamage.get());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_IS_CHARGING_CROSSBOW, false);
    }

    public boolean canHunt() { return false; }

    @Override
    protected boolean isAcceptedWeapon(Item item) { return item instanceof CrossbowItem; }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(2, new BackawayCrossbowGoal<>(this, 1.0D, 15.0F));
        this.goalSelector.addGoal(8, new com.Polarice3.Goety.common.entities.ally.Summoned.WanderGoal<>(this, 0.6D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 15.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 15.0F));
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.CROSSBOW));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        this.populateDefaultEquipmentSlots(level.getRandom(), difficulty);
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        this.performCrossbowAttack(this, 1.6F);
    }

    @Override
    protected void finishConversion(ServerLevel serverLevel) {
        this.getInventory().removeAllItems().forEach(this::spawnAtLocation);
        ZPiglinHunterServant zombified = new ZPiglinHunterServant(
                com.qiuyue.goetyominus.common.init.ModEntityTypes.ZPIGLIN_HUNTER_SERVANT.get(), serverLevel);
        zombified.copyTrueOwner(this);
        zombified.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
        zombified.setItemSlot(EquipmentSlot.MAINHAND, this.getMainHandItem().copy());
        zombified.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
        zombified.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(this.blockPosition()),
                MobSpawnType.CONVERSION, null, null);
        ForgeEventFactory.onLivingConvert(this, zombified);
        this.discard();
        serverLevel.addFreshEntity(zombified);
    }

    @Override
    public PiglinArmPose getArmPose() {
        if (this.isChargingCrossbow()) return PiglinArmPose.CROSSBOW_CHARGE;
        if (this.isAggressive() && this.getMainHandItem().getItem() instanceof CrossbowItem)
            return PiglinArmPose.CROSSBOW_HOLD;
        return PiglinArmPose.DEFAULT;
    }

    public boolean isChargingCrossbow() { return this.entityData.get(DATA_IS_CHARGING_CROSSBOW); }
    public void setChargingCrossbow(boolean charging) { this.entityData.set(DATA_IS_CHARGING_CROSSBOW, charging); }

    public void onCrossbowAttackPerformed() { this.noActionTime = 0; }

    public void shootCrossbowProjectile(LivingEntity target, ItemStack crossbow, Projectile projectile, float angle) {
        this.shootCrossbowProjectile(this, target, projectile, angle, 1.6F);
    }

    @Override
    public boolean canFireProjectileWeapon(ProjectileWeaponItem weapon) {
        return weapon instanceof CrossbowItem;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
    }

    protected SoundEvent getAmbientSound() { return SoundEvents.PIGLIN_BRUTE_AMBIENT; }

    protected SoundEvent getHurtSound(DamageSource s) { return SoundEvents.PIGLIN_BRUTE_HURT; }

    protected SoundEvent getDeathSound() { return SoundEvents.PIGLIN_BRUTE_DEATH; }

    protected void playStepSound(BlockPos p, BlockState s) { this.playSound(SoundEvents.PIGLIN_BRUTE_STEP, 0.15F, 1.0F); }

    protected void playConvertedSound() { this.playSound(SoundEvents.PIGLIN_BRUTE_CONVERTED_TO_ZOMBIFIED, 1.0F, this.getVoicePitch()); }
}