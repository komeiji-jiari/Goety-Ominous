package com.qiuyue.goetyominous.common.entities.hostile.cultists;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.qiuyue.goetyominous.config.AttributesConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class Martyr extends AbstractGOCultist {
    private static final EntityDataAccessor<Integer> DATA_CONVERT_TIME = SynchedEntityData.defineId(Martyr.class, EntityDataSerializers.INT);
    private static final int CONVERT_DURATION = 200;

    public Martyr(EntityType<? extends AbstractGOCultist> type, Level level) {
        super(type, level);
        this.xpReward = 10;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.MartyrHealth.get())
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_CONVERT_TIME, -1);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("ConvertTime", this.getConvertTime());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setConvertTime(compound.getInt("ConvertTime"));
    }

    @Override
    public CultistArmPose getArmPose() {
        if (this.isConverting()) {
            return CultistArmPose.SPELLCASTING;
        }
        return super.getArmPose();
    }

    public int getConvertTime() {
        return this.entityData.get(DATA_CONVERT_TIME);
    }

    public void setConvertTime(int time) {
        this.entityData.set(DATA_CONVERT_TIME, time);
    }

    public boolean isConverting() {
        return this.getConvertTime() > 0;
    }

    @Override
    public void aiStep() {
        super.aiStep();

        int convertTime = this.getConvertTime();

        if (convertTime > 0) {
            this.getNavigation().stop();
            this.setTarget(null);

            if (--convertTime <= 0) {
                this.finishConversion();
                return;
            }
            this.setConvertTime(convertTime);

            if (this.level() instanceof ServerLevel serverLevel) {
                Vec3 handPos = this.position().add(0, this.getEyeHeight() * 0.6D, 0);
                Vec3 lookDir = this.getLookAngle();
                serverLevel.sendParticles(ModParticleTypes.TOTEM_EFFECT.get(),
                        handPos.x + lookDir.yRot(0.3F).x * 0.4D,
                        handPos.y, handPos.z + lookDir.yRot(0.3F).z * 0.4D,
                        1, 0.0D, 0.0D, 0.0D, 0.0D);
                serverLevel.sendParticles(ModParticleTypes.TOTEM_EFFECT.get(),
                        handPos.x + lookDir.yRot(-0.3F).x * 0.4D,
                        handPos.y, handPos.z + lookDir.yRot(-0.3F).z * 0.4D,
                        1, 0.0D, 0.0D, 0.0D, 0.0D);

                int elapsed = CONVERT_DURATION - convertTime;
                if (elapsed % 5 == 0 && elapsed < 50) {
                    serverLevel.sendParticles(
                            new com.Polarice3.Goety.client.particles.TeleportInShockwaveParticleOption(8.0F, 2.0F),
                            this.getX(), this.getY() + 0.25D, this.getZ(),
                            0, 0.0D, 0.0D, 0.0D, 0.5D);
                }
            }

            if (convertTime % 20 == 0) {
                this.playSound(SoundEvents.ILLUSIONER_PREPARE_MIRROR, 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
            }
        } else if (convertTime == -1 && this.getTarget() != null && this.hasLineOfSight(this.getTarget())) {
            this.setConvertTime(CONVERT_DURATION);
            this.playSound(SoundEvents.EVOKER_PREPARE_WOLOLO, 1.0F, 1.0F);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isConverting() && source.getEntity() instanceof LivingEntity) {
            int newTime = Math.max(20, this.getConvertTime() - 20);
            this.setConvertTime(newTime);
        }
        return super.hurt(source, amount);
    }

    @Override
    public void die(DamageSource source) {
        if (!this.level().isClientSide) {
            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ModParticleTypes.TOTEM_EFFECT.get(),
                        this.getX(), this.getY() + 1.0D, this.getZ(),
                        15, 0.5D, 0.5D, 0.5D, 0.1D);
                serverLevel.sendParticles(ParticleTypes.FLASH,
                        this.getX(), this.getY() + 1.0D, this.getZ(),
                        1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }
        super.die(source);
    }

    private void finishConversion() {
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.FLASH,
                    this.getX(), this.getY() + 0.5D, this.getZ(),
                    1, 0.0D, 0.0D, 0.0D, 0.0D);

            for (int i = 0; i < 5; i++) {
                serverLevel.sendParticles(
                        new com.Polarice3.Goety.client.particles.TeleportInShockwaveParticleOption(8.0F, 2.0F),
                        this.getX(), this.getY() + 0.25D, this.getZ(),
                        0, 0.0D, 0.0D, 0.0D, 0.5F + i * 0.1F);
            }

            serverLevel.sendParticles(ModParticleTypes.TOTEM_EFFECT.get(),
                    this.getX(), this.getY() + 1.0D, this.getZ(),
                    30, 1.0D, 1.0D, 1.0D, 0.2D);

            this.playSound(SoundEvents.GENERIC_EXPLODE, 1.5F, 0.5F);
            this.playSound(com.Polarice3.Goety.init.ModSounds.BOSS_SUMMON.get(), 4.0F, 1.0F);

            // 替换为 TurnedMartyr（后续实现）
            this.discard();
        }
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 1.62F;
    }
}
