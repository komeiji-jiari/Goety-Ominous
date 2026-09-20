package com.qiuyue.goetyominous.common.entities.ally.of;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.common.entities.ally.of.goals.VoltServantLeapGoal;
import com.qiuyue.goetyominous.common.entities.ally.of.goals.VoltServantShootGoal;
import com.qiuyue.goetyominous.common.entities.ally.of.goals.VoltServantShootInWaterGoal;
import com.unusualmodding.opposing_force.entity.ai.navigation.SmoothGroundPathNavigation;
import com.unusualmodding.opposing_force.entity.utils.AttackState;
import com.unusualmodding.opposing_force.entity.utils.EliteVariant;
import com.unusualmodding.opposing_force.entity.utils.OPPoses;
import com.unusualmodding.opposing_force.registry.OPDamageTypes;
import com.unusualmodding.opposing_force.registry.OPSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class VoltServant extends Summoned implements AttackState, EliteVariant {
    private static final EntityDataAccessor<Integer> ATTACK_STATE;
    private static final EntityDataAccessor<Boolean> CHARGED;
    private static final EntityDataAccessor<Boolean> ELITE;
    private static final EntityDataAccessor<Boolean> SWIMMING;

    private static final EntityDimensions FISH_IN_WATER_DIMENSIONS = EntityDimensions.scalable(1.1F, 0.5F);

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState shootAnimationState = new AnimationState();
    public final AnimationState shootWaterAnimationState = new AnimationState();
    public final AnimationState twitch1AnimationState = new AnimationState();
    public final AnimationState twitch2AnimationState = new AnimationState();
    public final AnimationState jumpAnimationState = new AnimationState();
    public final AnimationState fallingAnimationState = new AnimationState();
    public final AnimationState landingAnimationState = new AnimationState();
    public final AnimationState swimIdleAnimationState = new AnimationState();
    public final AnimationState leapAnimationState = new AnimationState();

    public int leapCooldown;
    public boolean isLandNavigator;
    private boolean wasOnGround;
    private Pose lastPose;
    private int jumpTicks;
    private int fallingTicks;
    private int landingTicks;
    private int shootingTicks;
    private final byte TWITCH1 = 68;
    private final byte TWITCH2 = 69;

    public VoltServant(EntityType<? extends Owned> entityType, Level level) {
        super(entityType, level);
        this.leapCooldown = 40 + this.getRandom().nextInt(20);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0.0F);
        this.switchNavigator(true);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 0.5D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, false, false,
                (target) -> target instanceof Enemy && !MobUtil.areAllies(this, target)));
        this.goalSelector.addGoal(1, new VoltServantLeapGoal(this));
        this.goalSelector.addGoal(2, new VoltServantShootGoal(this));
        this.goalSelector.addGoal(2, new VoltServantShootInWaterGoal(this));
        this.goalSelector.addGoal(3, new RandomSwimmingGoal(this, 1.0D, 10));
        this.goalSelector.addGoal(3, new VoltRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        return new SmoothGroundPathNavigation(this, level);
    }

    protected void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveControl = new MoveControl(this);
            this.navigation = new SmoothGroundPathNavigation(this, this.level());
            this.lookControl = new LookControl(this);
            this.isLandNavigator = true;
        } else {
            this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.2F, 0.1F, false);
            this.navigation = new AmphibiousPathNavigation(this, this.level());
            this.lookControl = new SmoothSwimmingLookControl(this, 10);
            this.isLandNavigator = false;
        }
    }

    @Override
    public void travel(Vec3 vec3) {
        if (this.isNoAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), vec3);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
            if (this.onGround()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, 0.4D * this.getSpeed(), 0.0D));
            }
        } else {
            super.travel(vec3);
        }
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader levelReader) {
        if (levelReader.getFluidState(pos).is(FluidTags.WATER)) {
            return 10.0F;
        }
        return 0.0F;
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return this.isVoltSwimming()
                ? FISH_IN_WATER_DIMENSIONS.scale(this.getScale())
                : super.getDimensions(pose);
    }

    @Override
    public int getAttackState() {
        return this.entityData.get(ATTACK_STATE);
    }

    @Override
    public void setAttackState(int attackState) {
        this.entityData.set(ATTACK_STATE, attackState);
    }

    @Override
    public boolean isElite() {
        return this.entityData.get(ELITE);
    }

    @Override
    public void setElite(boolean elite) {
        this.entityData.set(ELITE, elite);
    }

    public boolean isCharged() {
        return this.entityData.get(CHARGED);
    }

    public void setCharged(boolean charged) {
        this.entityData.set(CHARGED, charged);
    }

    public boolean isVoltSwimming() {
        return this.entityData.get(SWIMMING);
    }

    public void setVoltSwimming(boolean swimming) {
        this.entityData.set(SWIMMING, swimming);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACK_STATE, 0);
        this.entityData.define(CHARGED, false);
        this.entityData.define(ELITE, false);
        this.entityData.define(SWIMMING, false);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("AttackState", this.getAttackState());
        compoundTag.putBoolean("Charged", this.isCharged());
        compoundTag.putBoolean("Elite", this.isElite());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setAttackState(compoundTag.getInt("AttackState"));
        this.setCharged(compoundTag.getBoolean("Charged"));
        this.setElite(compoundTag.getBoolean("Elite"));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.leapCooldown > 0) {
            --this.leapCooldown;
        }
        if (this.level().isClientSide) {
            this.setupAnimationStates();
        }
        this.updatePoseAnimations();

        if (this.onGround() && !this.wasOnGround) {
            this.playSound(OPSoundEvents.VOLT_SQUISH.get(), 0.2F,
                    (1.0F + (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.2F) / 0.8F);
        }
        this.setVoltSwimming(this.isInWater());
        this.wasOnGround = this.onGround();

        boolean onLand = !this.isInWater();
        if (onLand && !this.isLandNavigator) {
            this.switchNavigator(true);
        }
        if (!onLand && this.isLandNavigator) {
            this.switchNavigator(false);
        }

        if (this.shootingTicks > 0) {
            --this.shootingTicks;
        }
        if (this.jumpTicks > 0) {
            --this.jumpTicks;
        }
        if (this.fallingTicks > 0) {
            --this.fallingTicks;
        }
        if (this.landingTicks > 0) {
            --this.landingTicks;
        }

        if (this.shootingTicks == 0 && this.getPose() == OPPoses.SHOOTING.get()) {
            this.setPose(Pose.STANDING);
        }
        if (this.jumpTicks == 0 && this.getPose() == Pose.LONG_JUMPING) {
            this.setPose(Pose.FALL_FLYING);
        }
        if (this.getPose() == Pose.FALL_FLYING) {
            if (this.fallingTicks == 0) {
                this.setPose(Pose.STANDING);
            }
            if (this.onGround()) {
                this.setPose(OPPoses.LANDING.get());
            }
        }
        if (this.landingTicks == 0 && this.getPose() == OPPoses.LANDING.get()) {
            this.setPose(Pose.STANDING);
        }

        if (this.isCharged() && this.tickCount % 100 == 0 && this.getHealth() < this.getMaxHealth()) {
            this.heal(2.0F);
        }

        if (this.getPose() == Pose.STANDING) {
            if (this.getRandom().nextInt(504) == 0 && !this.twitch2AnimationState.isStarted()) {
                this.level().broadcastEntityEvent(this, this.TWITCH1);
            }
            if (this.getRandom().nextInt(505) == 0 && !this.twitch1AnimationState.isStarted()) {
                this.level().broadcastEntityEvent(this, this.TWITCH2);
            }
        }
    }

    private void setupAnimationStates() {
        if (this.shootingTicks == 0 && (this.shootAnimationState.isStarted() || this.shootWaterAnimationState.isStarted())) {
            this.shootAnimationState.stop();
            this.shootWaterAnimationState.stop();
        }
        if (this.jumpTicks == 0 && this.jumpAnimationState.isStarted()) {
            this.jumpAnimationState.stop();
        }
        if (this.fallingTicks == 0 && this.fallingAnimationState.isStarted()) {
            this.fallingAnimationState.stop();
        }
        if (this.landingTicks == 0 && this.landingAnimationState.isStarted()) {
            this.landingAnimationState.stop();
        }
        this.idleAnimationState.animateWhen(!this.isInWater() && this.getPose() == Pose.STANDING, this.tickCount);
        this.swimIdleAnimationState.animateWhen(this.isInWater() && this.getPose() == Pose.STANDING, this.tickCount);
    }

    @Override
    public void refreshDimensions() {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        super.refreshDimensions();
        this.setPos(d0, d1, d2);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (SWIMMING.equals(key)) {
            this.refreshDimensions();
        }
        super.onSyncedDataUpdated(key);
    }

    private void updatePoseAnimations() {
        Pose pose = this.getPose();
        if (pose != this.lastPose) {
            if (pose == Pose.FALL_FLYING) {
                this.jumpAnimationState.stop();
                this.fallingTicks = 100;
                this.fallingAnimationState.start(this.tickCount);
            } else if (pose == Pose.LONG_JUMPING) {
                this.fallingAnimationState.stop();
                this.jumpTicks = 10;
                this.jumpAnimationState.start(this.tickCount);
            } else if (pose == OPPoses.LANDING.get()) {
                this.landingTicks = 10;
                this.landingAnimationState.start(this.tickCount);
            } else if (pose == OPPoses.SHOOTING.get()) {
                this.shootingTicks = 20;
                if (this.isInWater()) {
                    this.shootWaterAnimationState.start(this.tickCount);
                } else {
                    this.shootAnimationState.start(this.tickCount);
                }
            } else if (pose == Pose.STANDING) {
                this.shootWaterAnimationState.stop();
                this.shootAnimationState.stop();
                this.jumpAnimationState.stop();
                this.fallingAnimationState.stop();
                this.landingAnimationState.stop();
            }
            this.lastPose = pose;
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == this.TWITCH1) {
            this.twitch1AnimationState.start(this.tickCount);
        } else if (id == this.TWITCH2) {
            this.twitch2AnimationState.start(this.tickCount);
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return OPSoundEvents.VOLT_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return OPSoundEvents.VOLT_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return OPSoundEvents.VOLT_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockState) {
        this.playSound(OPSoundEvents.VOLT_SQUISH.get(), 0.1F, 1.0F);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        if (super.isInvulnerableTo(source)) {
            return true;
        }
        if (source.is(OPDamageTypes.ELECTRIC)) {
            return true;
        }
        return source.is(OPDamageTypes.ELECTRIFIED);
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public void thunderHit(ServerLevel level, LightningBolt lightning) {
        this.setCharged(true);
        this.heal(this.getMaxHealth());
    }

    @Override
    public int getExperienceReward() {
        return 400;
    }

    public void setEliteStats(Mob mob) {
        if (mob.getAttribute(Attributes.MAX_HEALTH) != null) {
            mob.getAttribute(Attributes.MAX_HEALTH).setBaseValue(mob.getAttributeBaseValue(Attributes.MAX_HEALTH) * 1.5D);
        }
        mob.setHealth(mob.getMaxHealth());
    }

    private static class VoltRandomStrollGoal extends RandomStrollGoal {
        private final VoltServant entity;

        public VoltRandomStrollGoal(VoltServant volt, double speed) {
            super(volt, speed);
            this.entity = volt;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && this.entity.isLandNavigator && !this.entity.isInWater();
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && this.entity.isLandNavigator && !this.entity.isInWater();
        }
    }

    static {
        ATTACK_STATE = SynchedEntityData.defineId(VoltServant.class, EntityDataSerializers.INT);
        CHARGED = SynchedEntityData.defineId(VoltServant.class, EntityDataSerializers.BOOLEAN);
        ELITE = SynchedEntityData.defineId(VoltServant.class, EntityDataSerializers.BOOLEAN);
        SWIMMING = SynchedEntityData.defineId(VoltServant.class, EntityDataSerializers.BOOLEAN);
    }
}
