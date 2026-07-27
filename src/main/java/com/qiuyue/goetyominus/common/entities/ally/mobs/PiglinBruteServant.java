package com.qiuyue.goetyominus.common.entities.ally.mobs;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.neutral.ZPiglinBruteServant;
import javax.annotation.Nullable;

import com.qiuyue.goetyominus.common.init.ModEntityTypes;
import com.qiuyue.goetyominus.common.entities.ally.neutral.AbstractPiglinServant;
import com.qiuyue.goetyominus.config.AttributesConfig;
import com.qiuyue.goetyominus.config.MobsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.entity.monster.piglin.PiglinArmPose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.ForgeEventFactory;

public class PiglinBruteServant extends AbstractPiglinServant {

    public PiglinBruteServant(EntityType<? extends Owned> p_35048_, Level p_35049_) {
        super(p_35048_, p_35049_);
        this.xpReward = 0;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(2, new com.Polarice3.Goety.common.entities.ai.ModMeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(8, new com.Polarice3.Goety.common.entities.ally.Summoned.WanderGoal<>(this, 0.6D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 15.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 15.0F));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.PiglinBruteServantHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.PiglinBruteServantArmor.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.PiglinBruteServantFollowRange.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.PiglinBruteServantMovementSpeed.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.PiglinBruteServantDamage.get());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("MeleeDamageDealt", this.meleeDamageDealt);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.meleeDamageDealt = tag.getInt("MeleeDamageDealt");
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_35058_, DifficultyInstance p_35059_, MobSpawnType p_35060_, @Nullable SpawnGroupData p_35061_, @Nullable CompoundTag p_35062_) {
        this.populateDefaultEquipmentSlots(p_35058_.getRandom(), p_35059_);
        return super.finalizeSpawn(p_35058_, p_35059_, p_35060_, p_35061_, p_35062_);
    }

    @Override
    protected void onMeleeDamageDealt() {
        if (this.level().isClientSide) return;
        if (this.meleeDamageDealt >= MobsConfig.PiglinBruteServantEvolutionDamage.get()){
            StrongPiglinBruteServant stronger = new StrongPiglinBruteServant(
                    ModEntityTypes.STRONG_PIGLIN_BRUTE_SERVANT.get(), this.level());
            stronger.copyTrueOwner(this);
            stronger.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
            stronger.setBaby(this.isBaby());
            stronger.setHealth(stronger.getMaxHealth());

            for (EquipmentSlot slot : EquipmentSlot.values()) {
                ItemStack stack = this.getItemBySlot(slot);
                if (!stack.isEmpty()) stronger.setItemSlot(slot, stack.copy());
            }
            for (int i = 0; i < this.getInventory().getContainerSize(); i++) {
                ItemStack stack = this.getInventory().getItem(i);
                if (!stack.isEmpty()) stronger.getInventory().setItem(i, stack.copy());
            }
            if (this.isImmuneToZombification()) stronger.setImmuneToZombification(true);
            stronger.setGuaranteedDrop(EquipmentSlot.MAINHAND);

            this.discard();
            ((ServerLevel) this.level()).addFreshEntity(stronger);
            ((ServerLevel) this.level()).sendParticles(ParticleTypes.FLASH,
                    this.getX(), this.getY() + 1.0, this.getZ(), 1, 0, 0, 0, 0);
        }
    }

    @Override
    protected void finishConversion(ServerLevel serverLevel) {
        this.getInventory().removeAllItems().forEach(this::spawnAtLocation);
        ZPiglinBruteServant zombified = new ZPiglinBruteServant(
                com.Polarice3.Goety.common.entities.ModEntityType.ZPIGLIN_BRUTE_SERVANT.get(), serverLevel);
        zombified.copyTrueOwner(this);
        zombified.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
        zombified.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
        zombified.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(this.blockPosition()),
                MobSpawnType.CONVERSION, null, null);
        ForgeEventFactory.onLivingConvert(this, zombified);
        this.discard();
        serverLevel.addFreshEntity(zombified);
    }

    @Override
    protected boolean isAcceptedWeapon(Item item) {
        return item instanceof SwordItem || item instanceof AxeItem
                || item instanceof com.qiuyue.goetyominus.common.items.BoneCudgelItem;
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if (target != null && this.getTarget() == null) {
            this.playAngrySound();
        }
        super.setTarget(target);
    }

    protected void populateDefaultEquipmentSlots(RandomSource p_219209_, DifficultyInstance p_219210_) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_AXE));
    }

    protected void customServerAiStep() {
        super.customServerAiStep();
    }

    public boolean canHunt() {
        return false;
    }

    public PiglinArmPose getArmPose() {
        return this.isAggressive() && this.isHoldingMeleeWeapon() ? PiglinArmPose.ATTACKING_WITH_MELEE_WEAPON : PiglinArmPose.DEFAULT;
    }

    public boolean hurt(DamageSource p_35055_, float p_35056_) {
        return super.hurt(p_35055_, p_35056_);
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.PIGLIN_BRUTE_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource p_35072_) {
        return SoundEvents.PIGLIN_BRUTE_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.PIGLIN_BRUTE_DEATH;
    }

    protected void playStepSound(BlockPos p_35066_, BlockState p_35067_) {
        this.playSound(SoundEvents.PIGLIN_BRUTE_STEP, 0.15F, 1.0F);
    }

    protected void playAngrySound() {
        this.playSound(SoundEvents.PIGLIN_BRUTE_ANGRY, 1.0F, this.getVoicePitch());
    }

    protected void playConvertedSound() {
        this.playSound(SoundEvents.PIGLIN_BRUTE_CONVERTED_TO_ZOMBIFIED, 1.0F, this.getVoicePitch());
    }

}