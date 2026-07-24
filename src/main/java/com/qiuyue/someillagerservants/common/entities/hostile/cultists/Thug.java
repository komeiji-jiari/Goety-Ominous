package com.qiuyue.someillagerservants.common.entities.hostile.cultists;

import com.qiuyue.someillagerservants.config.AttributesConfig;
import com.qiuyue.someillagerservants.common.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class Thug extends AbstractSISCultist {
    private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(Thug.class, EntityDataSerializers.BYTE);
    private int attackTick;

    public Thug(EntityType<? extends AbstractSISCultist> type, Level level) {
        super(type, level);
        this.setMaxUpStep(1.0F);
        this.xpReward = 15;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0F, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.ThugHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.ThugDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte) 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("AttackTick", this.attackTick);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.attackTick = compound.getInt("AttackTick");
    }

    private boolean getThugFlag(int mask) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        return (i & mask) != 0;
    }

    private void setThugFlag(int mask, boolean value) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        if (value) {
            i = i | mask;
        } else {
            i = i & ~mask;
        }
        this.entityData.set(DATA_FLAGS_ID, (byte) (i & 255));
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.THUG_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSounds.THUG_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.THUG_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(ModSounds.THUG_STEP.get(), 0.15F, 1.0F);
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return ModSounds.THUG_CELEBRATE.get();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.isAlive()) {
            if (this.isImmobile()) {
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0D);
            } else {
                double d0 = this.getTarget() != null ? 0.3D : 0.23D;
                double d1 = this.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue();
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(Mth.lerp(0.1D, d1, d0));
            }
            if (this.level().isClientSide) {
                if (this.isRaging() && this.tickCount % 10 == 0) {
                    this.addParticlesAroundSelf(ParticleTypes.ANGRY_VILLAGER);
                }
            }
            if (this.getHealth() < this.getMaxHealth() / 1.5F) {
                if (this.isAggressive() || this.getTarget() != null) {
                    this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20, 1));
                    this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, 1));
                    if (this.tickCount % 20 == 0) {
                        this.hurt(this.damageSources().generic(), 1.0F);
                    }
                    this.setIsRaging(true);
                } else {
                    if (this.tickCount % 20 == 0) {
                        this.heal(2.0F);
                    }
                    this.setIsRaging(false);
                }
            }
            if (this.attackTick > 0) {
                --this.attackTick;
            }
            if (this.getDeltaMovement().horizontalDistanceSqr() > 2.5000003E-7F && this.random.nextInt(5) == 0) {
                BlockPos pos = BlockPos.containing(this.getX(), this.getY() - 0.2D, this.getZ());
                BlockState blockstate = this.level().getBlockState(pos);
                if (!blockstate.isAir()) {
                    this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockstate),
                            this.getX() + (this.random.nextDouble() - 0.5D) * this.getBbWidth(),
                            this.getY() + 0.1D,
                            this.getZ() + (this.random.nextDouble() - 0.5D) * this.getBbWidth(),
                            4.0D * (this.random.nextDouble() - 0.5D), 0.5D, (this.random.nextDouble() - 0.5D) * 4.0D);
                }
            }
            if (this.isAggressive()) {
                if (this.horizontalCollision && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level(), this)) {
                    boolean flag = false;
                    AABB aabb = this.getBoundingBox().inflate(0.2D);
                    for (BlockPos blockpos : BlockPos.betweenClosed(
                            Mth.floor(aabb.minX), Mth.floor(aabb.minY), Mth.floor(aabb.minZ),
                            Mth.floor(aabb.maxX), Mth.floor(aabb.maxY), Mth.floor(aabb.maxZ))) {
                        BlockState blockstate = this.level().getBlockState(blockpos);
                        Block block = blockstate.getBlock();
                        if ((block instanceof LeavesBlock
                                || blockstate.is(net.minecraft.tags.BlockTags.FENCES)
                                || blockstate.is(net.minecraft.tags.BlockTags.WOODEN_DOORS))
                                && !blockstate.hasBlockEntity()) {
                            flag = this.level().destroyBlock(blockpos, true, this) || flag;
                        }
                    }
                    if (!flag && this.onGround()) {
                        this.jumpFromGround();
                    }
                }
            }
        }
    }

    public boolean isRaging() {
        return this.getThugFlag(1);
    }

    public void setIsRaging(boolean raging) {
        this.setThugFlag(1, raging);
    }

    @OnlyIn(Dist.CLIENT)
    protected void addParticlesAroundSelf(ParticleOptions particleData) {
        for (int i = 0; i < 5; ++i) {
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;
            this.level().addParticle(particleData, this.getRandomX(1.0D), this.getRandomY() + 1.0D, this.getRandomZ(1.0D), d0, d1, d2);
        }
    }

    private float getAttackDamage() {
        return (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 4) {
            this.attackTick = 10;
            this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
        } else {
            super.handleEntityEvent(id);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public int getAttackTick() {
        return this.attackTick;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        this.attackTick = 10;
        this.level().broadcastEntityEvent(this, (byte) 4);
        float f = this.getAttackDamage();
        float f1 = (int) f > 0 ? f / 2.0F + this.random.nextInt((int) f) : f;
        boolean flag = target.hurt(this.damageSources().mobAttack(this), f1);
        if (flag) {
            target.setDeltaMovement(target.getDeltaMovement().add(0.0D, 0.4F, 0.0D));
            this.doEnchantDamageEffects(this, target);
        }
        this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
        return flag;
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new ThugNavigator(this, level);
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 1.62F;
    }

    static class ThugNavigator extends GroundPathNavigation {
        public ThugNavigator(Mob mob, Level level) {
            super(mob, level);
        }

        @Override
        protected PathFinder createPathFinder(int maxVisitedNodes) {
            this.nodeEvaluator = new ThugNodeEvaluator();
            return new PathFinder(this.nodeEvaluator, maxVisitedNodes);
        }
    }

    static class ThugNodeEvaluator extends WalkNodeEvaluator {
        @Override
        public BlockPathTypes getBlockPathType(net.minecraft.world.level.BlockGetter level, int x, int y, int z) {
            BlockPathTypes type = super.getBlockPathType(level, x, y, z);
            if (type == BlockPathTypes.LEAVES || type == BlockPathTypes.DOOR_WOOD_CLOSED || type == BlockPathTypes.FENCE) {
                return BlockPathTypes.OPEN;
            }
            return type;
        }
    }
}