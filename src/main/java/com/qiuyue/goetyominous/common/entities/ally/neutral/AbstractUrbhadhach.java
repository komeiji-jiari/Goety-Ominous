package com.qiuyue.goetyominous.common.entities.ally.neutral;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.utils.EntityFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.qiuyue.goetyominous.common.entities.ai.CenteredGroundNavigation;
import com.qiuyue.goetyominous.common.entities.ai.SmoothBodyRotationControl;
import com.qiuyue.goetyominous.common.init.ModSounds;
import com.qiuyue.goetyominous.config.AttributesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BooleanSupplier;

public abstract class AbstractUrbhadhach extends Summoned {
    public static final int ANIM_NONE = 0;
    public static final int ANIM_BITE = 1;
    public static final int ANIM_CLAW = 2;
    public static final int ANIM_SIT = 3;
    public static final int ANIM_SLEEP = 4;
    public static final int ANIM_WAKE_UP = 5;
    public static final int ANIM_ROAR = 6;
    public static final int ANIM_DASH_START = 7;
    public static final int ANIM_DASH_LOOP = 8;
    public static final int ANIM_DASH_END = 9;
    public static final int ANIM_DASH_STUN = 10;
    public static final int ANIM_STUN_END = 11;
    public static final int ANIM_EAT = 12;
    public static final int BITE_DURATION = 15;
    public static final int CLAW_DURATION = 45;
    private static final int BELLY_EXPOSED_START = 9;
    private static final int BELLY_EXPOSED_END = 35;
    public static final int SIT_DURATION = 20;
    public static final int SLEEP_DURATION = 50;
    public static final int WAKE_UP_DURATION = 49;
    public static final int ROAR_DURATION = 53;
    private static final int ROAR_RECOVER_TICK = 10;
    private static final int ROAR_IMPACT_TICK = 30;
    public static final int DASH_START_DURATION = 18;
    public static final int DASH_LOOP_DURATION = 15;
    public static final int DASH_END_DURATION = 31;
    public static final int DASH_STUN_DURATION = 23;
    public static final int STUN_END_DURATION = 30;
    public static final int EAT_DURATION = 110;
    private static final EntityDataAccessor<Integer> ANIMATION_STATE = SynchedEntityData.defineId(AbstractUrbhadhach.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ANIMATION_SEQUENCE = SynchedEntityData.defineId(AbstractUrbhadhach.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_STANDING_ID = SynchedEntityData.defineId(AbstractUrbhadhach.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_THRALLING_ID = SynchedEntityData.defineId(AbstractUrbhadhach.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<UUID>> THRALL_UUID = SynchedEntityData.defineId(AbstractUrbhadhach.class, EntityDataSerializers.OPTIONAL_UUID);
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState biteAnimationState = new AnimationState();
    public final AnimationState clawAnimationState = new AnimationState();
    public final AnimationState sitAnimationState = new AnimationState();
    public final AnimationState sleepAnimationState = new AnimationState();
    public final AnimationState wakeUpAnimationState = new AnimationState();
    public final AnimationState roarAnimationState = new AnimationState();
    public final AnimationState dashStartAnimationState = new AnimationState();
    public final AnimationState dashLoopAnimationState = new AnimationState();
    public final AnimationState dashEndAnimationState = new AnimationState();
    public final AnimationState dashStunAnimationState = new AnimationState();
    public final AnimationState stunEndAnimationState = new AnimationState();
    public final AnimationState eatAnimationState = new AnimationState();
    private int clientAnimationSequence;
    private int actionTicks;
    private int actionDuration;
    private boolean eatQueued;
    private boolean stunEndQueued;
    private boolean restingInShade;
    private int warningSoundTicks;
    private int roarCooldown;
    private int thrallCooldown;
    private int healTime;
    private float animationYaw;
    private int animationEndTick = -1;
    private long animationEndTime = -1L;

    protected AbstractUrbhadhach(EntityType<? extends AbstractUrbhadhach> type, Level level) {
        super(type, level);
        this.setMaxUpStep(1.0F);
        this.moveControl = new UrbhadhachMoveControl(this);
        this.setPathfindingMalus(net.minecraft.world.level.pathfinder.BlockPathTypes.WATER, 0.0F);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.UrbhadhachHealth.get())
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.UrbhadhachDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.25D);
    }

    @Override
    public void setConfigurableAttributes() {
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(AttributesConfig.UrbhadhachHealth.get());
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(AttributesConfig.UrbhadhachDamage.get());
    }

    @Override
    public void setUpgraded(boolean upgraded) {
        super.setUpgraded(upgraded);
        AttributeInstance knockback = this.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        if (knockback != null) {
            knockback.setBaseValue(upgraded ? 1.0D : 0.25D);
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new DashAttackGoal(this));
        this.goalSelector.addGoal(2, new AttackGoal());
    }

    @Override
    public int xpReward() {
        return 0;
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new CenteredGroundNavigation(this, level);
    }

    @Override
    protected BodyRotationControl createBodyControl() {
        return new SmoothBodyRotationControl(this);
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effectInstance) {
        if (effectInstance.getEffect() == MobEffects.MOVEMENT_SLOWDOWN
                || effectInstance.getEffect() == GoetyEffects.FREEZING.get()) {
            return false;
        }
        return super.canBeAffected(effectInstance);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.URBHADHACH_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSounds.URBHADHACH_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.URBHADHACH_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        this.playSound(ModSounds.URBHADHACH_STEP.get(), 0.15F, 1.0F);
    }

    protected void playWarningSound() {
        if (this.warningSoundTicks <= 0) {
            this.playSound(ModSounds.URBHADHACH_ROAR.get(), 1.0F, 1.0F);
            this.warningSoundTicks = 40;
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIMATION_STATE, ANIM_NONE);
        this.entityData.define(ANIMATION_SEQUENCE, 0);
        this.entityData.define(DATA_THRALLING_ID, false);
        this.entityData.define(DATA_STANDING_ID, false);
        this.entityData.define(THRALL_UUID, Optional.empty());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.getThrallUUID() != null) {
            compound.putUUID("Thrall", this.getThrallUUID());
        }
        compound.putInt("ThrallCooldown", this.thrallCooldown);
        compound.putInt("RoarCooldown", this.roarCooldown);
        compound.putInt("HealTime", this.healTime);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.hasUUID("Thrall")) {
            this.setThrallUuid(compound.getUUID("Thrall"));
        }
        this.thrallCooldown = compound.getInt("ThrallCooldown");
        this.roarCooldown = compound.getInt("RoarCooldown");
        this.healTime = compound.getInt("HealTime");
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (this.level().isClientSide && ANIMATION_SEQUENCE.equals(accessor)) {
            int sequence = this.entityData.get(ANIMATION_SEQUENCE);
            if (sequence != this.clientAnimationSequence) {
                this.clientAnimationSequence = sequence;
                this.startClientAnimation();
            }
        }
        if (this.level().isClientSide && ANIMATION_STATE.equals(accessor) && this.getAnimationState() == ANIM_NONE) {
            this.actionTicks = 0;
            this.actionDuration = 0;
            this.animationEndTick = this.tickCount;
            this.animationEndTime = this.findClientAnimationTime();
        }
        super.onSyncedDataUpdated(accessor);
    }

    private void startClientAnimation() {
        this.actionTicks = 0;
        this.actionDuration = durationOf(this.getAnimationState());
        this.animationEndTick = -1;
        this.animationEndTime = -1L;
        List.of(this.idleAnimationState, this.biteAnimationState, this.clawAnimationState,
                this.sitAnimationState, this.sleepAnimationState, this.wakeUpAnimationState,
                this.roarAnimationState, this.dashStartAnimationState,
                this.dashLoopAnimationState, this.dashEndAnimationState,
                this.dashStunAnimationState, this.stunEndAnimationState,
                this.eatAnimationState).forEach(AnimationState::stop);
        switch (this.getAnimationState()) {
            case ANIM_BITE -> this.biteAnimationState.start(this.tickCount);
            case ANIM_CLAW -> this.clawAnimationState.start(this.tickCount);
            case ANIM_SIT -> this.sitAnimationState.start(this.tickCount);
            case ANIM_SLEEP -> this.sleepAnimationState.start(this.tickCount);
            case ANIM_WAKE_UP -> this.wakeUpAnimationState.start(this.tickCount);
            case ANIM_ROAR -> this.roarAnimationState.start(this.tickCount);
            case ANIM_DASH_START -> this.dashStartAnimationState.start(this.tickCount);
            case ANIM_DASH_LOOP -> this.dashLoopAnimationState.start(this.tickCount);
            case ANIM_DASH_END -> this.dashEndAnimationState.start(this.tickCount);
            case ANIM_DASH_STUN -> this.dashStunAnimationState.start(this.tickCount);
            case ANIM_STUN_END -> this.stunEndAnimationState.start(this.tickCount);
            case ANIM_EAT -> this.eatAnimationState.start(this.tickCount);
        }
    }

    private static int durationOf(int animation) {
        return switch (animation) {
            case ANIM_BITE -> BITE_DURATION;
            case ANIM_CLAW -> CLAW_DURATION;
            case ANIM_SIT -> SIT_DURATION;
            case ANIM_SLEEP -> SLEEP_DURATION;
            case ANIM_WAKE_UP -> WAKE_UP_DURATION;
            case ANIM_ROAR -> ROAR_DURATION;
            case ANIM_DASH_START -> DASH_START_DURATION;
            case ANIM_DASH_LOOP -> DASH_LOOP_DURATION;
            case ANIM_DASH_END -> DASH_END_DURATION;
            case ANIM_DASH_STUN -> DASH_STUN_DURATION;
            case ANIM_STUN_END -> STUN_END_DURATION;
            case ANIM_EAT -> EAT_DURATION;
            default -> 0;
        };
    }

    public void startAnimation(int animation, int duration) {
        this.actionTicks = 0;
        this.actionDuration = duration;
        if (!this.level().isClientSide) {
            LivingEntity target = this.getTarget();
            if (target != null) {
                this.animationYaw = this.yawTo(target);
                this.setYRot(this.animationYaw);
                this.yBodyRot = this.animationYaw;
                this.yHeadRot = this.animationYaw;
            } else {
                this.animationYaw = this.getYRot();
            }
            this.entityData.set(ANIMATION_STATE, animation);
            this.entityData.set(ANIMATION_SEQUENCE, this.entityData.get(ANIMATION_SEQUENCE) + 1);
        }
    }

    private float yawTo(LivingEntity target) {
        return -((float) Mth.atan2(target.getX() - this.getX(), target.getZ() - this.getZ())) * (180F / (float) Math.PI);
    }

    public void finishAnimation() {
        this.actionTicks = 0;
        this.actionDuration = 0;
        if (!this.level().isClientSide) {
            this.entityData.set(ANIMATION_STATE, ANIM_NONE);
        }
    }

    public int getAnimationState() {
        return this.entityData.get(ANIMATION_STATE);
    }

    private long findClientAnimationTime() {
        AnimationState[] states = {this.biteAnimationState, this.clawAnimationState, this.sitAnimationState,
                this.sleepAnimationState, this.wakeUpAnimationState, this.roarAnimationState,
                this.dashStartAnimationState, this.dashLoopAnimationState, this.dashEndAnimationState,
                this.dashStunAnimationState, this.stunEndAnimationState, this.eatAnimationState};
        for (AnimationState state : states) {
            if (state.isStarted()) {
                return state.getAccumulatedTime();
            }
        }
        return -1L;
    }

    public int getAnimationEndTick() {
        return this.animationEndTick;
    }

    public long getAnimationEndTime() {
        return this.animationEndTime;
    }

    public boolean isActionLocked() {
        int animation = this.getAnimationState();
        return animation != ANIM_NONE && animation != ANIM_SIT && animation != ANIM_SLEEP;
    }

    public boolean isPlayingSpecialAnimation() {
        return this.getAnimationState() != ANIM_NONE;
    }

    protected boolean autoFinishesAnimation(int animation) {
        return animation == ANIM_BITE || animation == ANIM_CLAW || animation == ANIM_ROAR
                || animation == ANIM_WAKE_UP || animation == ANIM_DASH_START
                || animation == ANIM_DASH_END || animation == ANIM_DASH_STUN
                || animation == ANIM_STUN_END || animation == ANIM_EAT;
    }

    @Nullable
    public AgeableMob getThrall() {
        try {
            UUID uuid = this.getThrallUUID();
            if (uuid != null) {
                Entity entity = this.level() instanceof ServerLevel serverLevel
                        ? serverLevel.getEntity(uuid)
                        : EntityFinder.getLivingEntityByUuiD(uuid);
                if (entity instanceof AgeableMob ageableMob && ageableMob.isBaby()) {
                    return ageableMob;
                }
            }
            return null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Nullable
    public UUID getThrallUUID() {
        return this.entityData.get(THRALL_UUID).orElse(null);
    }

    public void setThrallUuid(UUID uuid) {
        this.entityData.set(THRALL_UUID, Optional.ofNullable(uuid));
    }

    public void setThrall(AgeableMob ageableMob) {
        this.setThrallUuid(ageableMob.getUUID());
    }

    @Override
    public void aiStep() {
        if (this.isSunBurnTick()) {
            this.setSecondsOnFire(8);
        }
        super.aiStep();

        if (!this.level().isClientSide && this.isMotionLocked()) {
            this.setYRot(this.animationYaw);
            this.setXRot(0.0F);
            this.yBodyRot = this.animationYaw;
            this.yHeadRot = this.animationYaw;
        }

        this.tickThrall();

        if (this.healTime > 0) {
            --this.healTime;
            if (this.tickCount % 20 == 0) {
                this.heal(1.0F);
            }
        }

        if (!this.level().isClientSide) {
            if (this.roarCooldown > 0) {
                --this.roarCooldown;
            }

            if (!this.isVehicle() && this.roarCooldown <= 0) {
                if (this.getTarget() != null && !this.getTarget().isBaby() && !this.isStanding()
                        && !this.isThralling() && this.isAlive() && this.getAnimationState() == ANIM_NONE) {
                    this.getLookControl().setLookAt(this.getTarget().position());
                    if (this.hasEffect(MobEffects.INVISIBILITY)) {
                        this.removeEffect(MobEffects.INVISIBILITY);
                    }
                    this.roarCooldown = 300;
                    this.startAnimation(ANIM_ROAR, ROAR_DURATION);
                    this.level().broadcastEntityEvent(this, (byte) 104);
                }
            }

            if (this.getAnimationState() == ANIM_ROAR) {
                if (this.actionTicks == ROAR_RECOVER_TICK) {
                    this.roarRecover();
                }
                if (this.actionTicks == ROAR_IMPACT_TICK) {
                    this.roarImpact();
                    this.roarEffect();
                }
            }
        }

        if (this.getHealth() <= this.getMaxHealth() / 2) {
            if (this.isHostile()) {
                for (AgeableMob ageableMob : this.level().getEntitiesOfClass(AgeableMob.class, this.getBoundingBox().inflate(32))) {
                    if (ageableMob.isBaby() && this.getTarget() != ageableMob && !MobUtil.areAllies(this, ageableMob)) {
                        this.setTarget(ageableMob);
                    }
                }
            }
            if (this.getTarget() != null && this.getTarget().isBaby()) {
                this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, 1, false, false));
            }
        }
    }

    private boolean isMotionLocked() {
        int animation = this.getAnimationState();
        return this.isActionLocked()
                && animation != ANIM_DASH_START
                && animation != ANIM_DASH_LOOP
                && !this.isVehicle();
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isMotionLocked()) {
            super.travel(Vec3.ZERO);
            return;
        }
        super.travel(travelVector);
    }

    private void tickThrall() {
        if (this.thrallCooldown > 0) {
            --this.thrallCooldown;
        }

        if (this.getLastHurtByMob() == null
                && this.lastHurtByPlayer == null
                && !this.isUnderWater()
                && this.thrallCooldown <= 0
                && (!this.level().isDay() || this.level().isRainingAt(this.blockPosition()))) {
            this.setThralling(true);
        }

        if (this.isThralling()) {
            AgeableMob thrall = this.getThrall();
            if (thrall == null && this.tickCount % 20 == 0) {
                AgeableMob nearest = null;
                double nearestDistance = Double.MAX_VALUE;
                for (AgeableMob ageableMob : this.level().getEntitiesOfClass(AgeableMob.class,
                        this.getBoundingBox().inflate(64.0D, 8.0D, 64.0D))) {
                    if (!ageableMob.isBaby() || ageableMob.isDeadOrDying()
                            || ageableMob.hasEffect(MobEffects.NIGHT_VISION)
                            || MobUtil.areAllies(this, ageableMob)
                            || !this.hasLineOfSight(ageableMob)) {
                        continue;
                    }
                    double distance = this.distanceToSqr(ageableMob);
                    if (distance < nearestDistance) {
                        nearestDistance = distance;
                        nearest = ageableMob;
                    }
                }
                if (nearest != null) {
                    this.setThrall(nearest);
                    thrall = nearest;
                }
            }
            if (thrall != null) {
                if (thrall.distanceToSqr(this) > 16 && !thrall.isUnderWater()) {
                    if (thrall.isSleeping()) {
                        thrall.stopSleeping();
                    } else {
                        float speed = 0.5F;
                        if (thrall.getAttribute(Attributes.FOLLOW_RANGE) != null) {
                            if (thrall.getAttributeValue(Attributes.MOVEMENT_SPEED) < 0.5F) {
                                speed = 1.0F;
                            }
                            if (thrall.getNavigation().isDone()) {
                                Path path = this.pathToUrbhadhach(thrall);
                                if (path != null) {
                                    thrall.getNavigation().moveTo(path, speed);
                                }
                            }
                        } else {
                            this.thrallCooldown = 600;
                            this.setThralling(false);
                        }
                    }
                    if (this.tickCount % 20 == 0) {
                        thrall.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 40));
                        this.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 40));
                    }
                    if (this.tickCount % 100 == 0) {
                        this.playSound(SoundEvents.BELL_BLOCK, 2.0F, 0.25F);
                        thrall.playSound(SoundEvents.BELL_RESONATE, 1.0F, 0.5F);
                    }
                } else {
                    this.thrallCooldown = 600;
                    this.setThralling(false);
                }
                if (thrall.isDeadOrDying()) {
                    this.setThrallUuid(null);
                    this.thrallCooldown = 600;
                    this.setThralling(false);
                }
            }
        }

        if (this.hurtTime > 0) {
            this.thrallCooldown = 600;
            this.setThralling(false);
        }

        if (this.isHostile()) {
            boolean hasEnemyNearby = false;
            for (LivingEntity livingEntity : this.level().getEntitiesOfClass(LivingEntity.class,
                    this.getBoundingBox().inflate(8),
                    MobUtil.NO_CREATIVE_OR_SPECTATOR)) {
                if (livingEntity instanceof net.minecraft.world.entity.Mob mob && mob.getTarget() == this) {
                    hasEnemyNearby = true;
                    this.setTarget(livingEntity);
                }
            }
            if (this.getTarget() == null) {
                Player nearestPlayer = null;
                double nearestPlayerDistance = Double.MAX_VALUE;
                for (Player player : this.level().getEntitiesOfClass(Player.class,
                        this.getBoundingBox().inflate(DashAttackGoal.MAX_RANGE),
                        MobUtil.NO_CREATIVE_OR_SPECTATOR)) {
                    if (!this.canAttack(player) || MobUtil.areAllies(this, player) || !this.hasLineOfSight(player)) {
                        continue;
                    }
                    double distance = this.distanceToSqr(player);
                    if (distance < nearestPlayerDistance) {
                        nearestPlayerDistance = distance;
                        nearestPlayer = player;
                    }
                }
                if (nearestPlayer != null) {
                    this.setTarget(nearestPlayer);
                }
            }
            if (hasEnemyNearby) {
                this.thrallCooldown = 600;
                this.setThralling(false);
            }
        }
    }

    public net.minecraft.world.level.pathfinder.Path pathToUrbhadhach(AgeableMob ageableMob) {
        if (this.getY() < this.level().getMinBuildHeight()
                || this.getY() > this.level().getMaxBuildHeight()
                || ageableMob.getY() < ageableMob.level().getMinBuildHeight()
                || ageableMob.getY() > ageableMob.level().getMaxBuildHeight()) {
            return null;
        }
        return ageableMob.getNavigation().createPath(this, 0);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.warningSoundTicks > 0) {
            --this.warningSoundTicks;
        }
        if (this.actionDuration > 0) {
            ++this.actionTicks;
            if (!this.level().isClientSide && this.actionTicks >= this.actionDuration
                    && this.autoFinishesAnimation(this.getAnimationState())) {
                this.finishAnimation();
            }
        }
        if (!this.level().isClientSide && this.stunEndQueued && this.getAnimationState() == ANIM_NONE) {
            this.stunEndQueued = false;
            this.startAnimation(ANIM_STUN_END, STUN_END_DURATION);
        }
        if (!this.level().isClientSide && this.eatQueued && this.getAnimationState() == ANIM_NONE) {
            this.eatQueued = false;
            this.startAnimation(ANIM_EAT, EAT_DURATION);
        }
    }

    @Override
    public boolean isSunBurnTick() {
        return !this.level().isClientSide
                && this.level().isDay()
                && this.level().canSeeSky(this.blockPosition())
                && !this.level().isRainingAt(this.blockPosition());
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        return -level.getPathfindingCostFromLightLevels(pos);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean flag = target.hurt(this.damageSources().mobAttack(this), (int) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
        if (flag) {
            this.level().broadcastEntityEvent(this, (byte) 105);
            this.playSound(ModSounds.URBHADHACH_ATTACK.get(), 1.0F, 1.0F);
            if (target instanceof LivingEntity livingEntity) {
                livingEntity.addEffect(new MobEffectInstance(GoetyEffects.FREEZING.get(), 100, 1), this);
                if (this.random.nextFloat() < 0.25F) {
                    if (livingEntity instanceof Player player) {
                        if (player.isUsingItem() && player.getUseItem().is(Items.SHIELD)) {
                            player.getCooldowns().addCooldown(Items.SHIELD, 100);
                            player.stopUsingItem();
                            player.level().broadcastEntityEvent(player, (byte) 30);
                        }
                    }
                    livingEntity.knockback(1.0F,
                            livingEntity.getX() - this.getX(),
                            livingEntity.getZ() - this.getZ());
                    livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
                }
                if (livingEntity.isBaby()) {
                    livingEntity.hurt(this.damageSources().mobAttack(this), livingEntity.getMaxHealth());
                }
                if (livingEntity.isDeadOrDying()) {
                    this.onKillBaby(livingEntity);
                    this.onKillEntity(livingEntity);
                }
            }
            this.doEnchantDamageEffects(this, target);
        }
        return flag;
    }

    protected void onKillEntity(LivingEntity killedEntity) {
    }

    private void onKillBaby(LivingEntity killedEntity) {
        if (killedEntity.isBaby() && killedEntity.getMobType() != MobType.UNDEAD) {
            this.playSound(SoundEvents.GENERIC_EAT, 2.0F, 0.25F);
            this.playSound(SoundEvents.PLAYER_BURP, 2.0F, 0.25F);
            this.heal(killedEntity.getMaxHealth() * 2);
            this.healTime = 600;
            this.eatQueued = true;
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        float damage = amount;

        if (ModDamageSource.freezeAttacks(source)
                || source.is(net.minecraft.world.damagesource.DamageTypes.FREEZE)) {
            return false;
        }

        if (source.is(net.minecraft.tags.DamageTypeTags.IS_FIRE)) {
            damage = amount * 2;
        }

        if (this.isBellyExposed()) {
            damage = amount * 1.5F;
        }

        return super.hurt(source, damage);
    }

    @Override
    public void knockback(double strength, double x, double z) {
        int animation = this.getAnimationState();
        if (animation == ANIM_CLAW) {
            return;
        }
        super.knockback(strength, x, z);
    }

    private void roarRecover() {
        if (this.level().isClientSide || !this.isAlive() || this.isSilent()) {
            return;
        }
        if (this.isOnFire()) {
            this.clearFire();
        }
        this.heal(2.0F);
    }

    private void roarImpact() {
        if (this.level().isClientSide || !this.isAlive() || this.isSilent()) {
            return;
        }
        for (LivingEntity livingEntity : this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(4.0D),
                MobUtil.NO_CREATIVE_OR_SPECTATOR.and(e -> e != this && !MobUtil.areAllies(this, e)))) {
            if (livingEntity.hurt(ModDamageSource.directFreeze(this), 6.0F)) {
                this.knockBack(livingEntity);
                if (livingEntity.canFreeze() && livingEntity.getMaxHealth() < this.getMaxHealth()) {
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 300));
                }
            }
        }
    }

    private void roarEffect() {
        if (this.level().isClientSide || !this.isAlive() || this.isSilent()) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel) this.level();
        Vec3 center = this.getBoundingBox().getCenter();

        int particleRadius = 4;
        for (int ix = -particleRadius; ix <= particleRadius; ++ix) {
            for (int j = -particleRadius; j <= particleRadius; ++j) {
                for (int k = -particleRadius; k <= particleRadius; ++k) {
                    double d13 = (double) ix + (this.random.nextDouble() - this.random.nextDouble()) * 0.5D;
                    double d15 = (double) j + (this.random.nextDouble() - this.random.nextDouble()) * 0.5D;
                    double d17 = (double) k + (this.random.nextDouble() - this.random.nextDouble()) * 0.5D;
                    double d19 = Math.sqrt(d13 * d13 + d15 * d15 + d17 * d17) / 0.5D + this.random.nextGaussian() * 0.05D;

                    serverLevel.sendParticles(ModParticleTypes.FROST_NOVA.get(),
                            center.x, center.y, center.z,
                            0,
                            d13 / d19, d15 / d19, d17 / d19,
                            0.5F);

                    if (ix != -particleRadius && ix != particleRadius && j != -particleRadius && j != particleRadius) {
                        k += particleRadius * 2 - 1;
                    }
                }
            }
        }

        serverLevel.sendParticles(
                new com.Polarice3.Goety.client.particles.ShockwaveParticleOption(0.0F, (float)particleRadius * 2.0F, 1),
                center.x, center.y + 0.5D, center.z,
                0, 0.0D, 0.0D, 0.0D, 0.0D);
    }

    private void knockBack(Entity entity) {
        double d0 = entity.getX() - this.getX();
        double d1 = entity.getZ() - this.getZ();
        double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
        entity.push(d0 / d2 * 2.0D, 0.1D, d1 / d2 * 2.0D);
    }

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || this.getAnimationState() == ANIM_ROAR;
    }

    public boolean isThralling() {
        return this.entityData.get(DATA_THRALLING_ID);
    }

    public void setThralling(boolean thralling) {
        this.entityData.set(DATA_THRALLING_ID, thralling);
    }

    public boolean isBellyExposed() {
        return this.getAnimationState() == ANIM_CLAW
                && this.actionTicks >= BELLY_EXPOSED_START
                && this.actionTicks <= BELLY_EXPOSED_END;
    }

    public boolean isStanding() {
        return this.entityData.get(DATA_STANDING_ID);
    }

    public void setStanding(boolean standing) {
        this.entityData.set(DATA_STANDING_ID, standing);
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 104) {
            this.playSound(ModSounds.URBHADHACH_STRONG_ROAR.get(), 5.0F, 1.0F);
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    protected float getWaterSlowDown() {
        return 0.98F;
    }

    public void triggerWakeUp() {
        int animation = this.getAnimationState();
        if (animation == ANIM_SIT || animation == ANIM_SLEEP) {
            this.startAnimation(ANIM_WAKE_UP, WAKE_UP_DURATION);
        }
    }

    private void setRestingInShade(boolean restingInShade) {
        this.restingInShade = restingInShade;
    }

    protected boolean isRestingInShade() {
        return this.restingInShade;
    }

    public static class RestGoal extends Goal {
        private static final int SIT_DELAY = 40;
        private final AbstractUrbhadhach mob;
        private final BooleanSupplier canRest;
        private final BooleanSupplier canContinueResting;
        private final boolean shadeRest;
        private int restTicks;
        private int sitTicks;

        public RestGoal(AbstractUrbhadhach mob, BooleanSupplier canRest, BooleanSupplier canContinueResting, boolean shadeRest) {
            this.mob = mob;
            this.canRest = canRest;
            this.canContinueResting = canContinueResting;
            this.shadeRest = shadeRest;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return this.mob.getTarget() == null && this.canRest.getAsBoolean();
        }

        @Override
        public boolean canContinueToUse() {
            return this.mob.getTarget() == null && this.canContinueResting.getAsBoolean();
        }

        @Override
        public void start() {
            this.restTicks = 0;
            this.sitTicks = 0;
            this.mob.setRestingInShade(this.shadeRest);
        }

        @Override
        public void tick() {
            int animation = this.mob.getAnimationState();
            if (animation == ANIM_WAKE_UP) {
                this.restTicks = 0;
            } else {
                ++this.restTicks;
            }
            if (animation == ANIM_SIT) {
                ++this.sitTicks;
            } else {
                this.sitTicks = 0;
            }
            this.mob.getNavigation().stop();
            if (animation == ANIM_NONE && this.restTicks > SIT_DELAY) {
                this.mob.startAnimation(ANIM_SIT, SIT_DURATION);
            } else if (animation == ANIM_SIT && this.sitTicks >= SIT_DURATION) {
                this.mob.startAnimation(ANIM_SLEEP, SLEEP_DURATION);
            }
        }
    }

    class AttackGoal extends MeleeAttackGoal {
        private static final int BITE_HIT_TICK = 9;
        private static final int CLAW_FIRST_HIT_TICK = 17;
        private static final int CLAW_SECOND_HIT_TICK = 28;
        private static final double CLAW_SWEEP_RADIUS = 3.5D;
        private int attackSpeed;
        private int actionType = ANIM_NONE;
        private int actionElapsed;
        private int actionDuration;

        public AttackGoal() {
            super(AbstractUrbhadhach.this, 1.25D, true);
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
            if (this.actionType != ANIM_NONE || AbstractUrbhadhach.this.isPlayingSpecialAnimation()) {
                return;
            }
            double d0 = this.getAttackReachSqr(enemy);
            if (distToEnemySqr <= d0 && this.attackTime()) {
                this.resetAttack();
                AbstractUrbhadhach.this.setStanding(false);
                this.beginAction(AbstractUrbhadhach.this.getRandom().nextFloat() < 0.35F ? ANIM_CLAW : ANIM_BITE);
            } else if (distToEnemySqr <= d0 * 2.0D) {
                if (this.attackTime()) {
                    AbstractUrbhadhach.this.setStanding(false);
                    this.resetAttack();
                }

                if (this.ticksUntilNextAttack() <= 10) {
                    AbstractUrbhadhach.this.setStanding(true);
                    AbstractUrbhadhach.this.playWarningSound();
                }
            } else {
                this.resetAttack();
                AbstractUrbhadhach.this.setStanding(false);
            }
        }

        @Override
        protected double getAttackReachSqr(LivingEntity enemy) {
            return 9.0D + enemy.getBbWidth();
        }

        private void beginAction(int type) {
            this.actionType = type;
            this.actionElapsed = 0;
            this.actionDuration = type == ANIM_CLAW ? CLAW_DURATION : BITE_DURATION;
            AbstractUrbhadhach.this.startAnimation(type, this.actionDuration);
            this.mob.getNavigation().stop();
        }

        @Override
        public void start() {
            super.start();
            this.attackSpeed = 0;
            this.actionType = ANIM_NONE;
            this.actionElapsed = 0;
        }

        @Override
        public void stop() {
            AbstractUrbhadhach.this.setStanding(false);
            this.endAction();
            super.stop();
        }

        @Override
        public void tick() {
            this.attackSpeed = Math.max(this.attackSpeed - 1, 0);
            if (this.actionType != ANIM_NONE) {
                this.tickAction();
                return;
            }
            super.tick();
        }

        private void tickAction() {
            LivingEntity enemy = this.mob.getTarget();
            this.mob.getNavigation().stop();
            if (enemy != null) {
                this.mob.getLookControl().setLookAt(enemy, 30.0F, 30.0F);
            }
            ++this.actionElapsed;
            if (this.actionType == ANIM_BITE && this.actionElapsed == BITE_HIT_TICK) {
                if (enemy != null) {
                    this.mob.doHurtTarget(enemy);
                }
            } else if (this.actionType == ANIM_CLAW
                    && (this.actionElapsed == CLAW_FIRST_HIT_TICK || this.actionElapsed == CLAW_SECOND_HIT_TICK)) {
                this.sweepAttack();
            }
            if (this.actionElapsed >= this.actionDuration) {
                this.endAction();
            }
        }

        private void sweepAttack() {
            for (LivingEntity livingEntity : this.mob.level().getEntitiesOfClass(LivingEntity.class,
                    this.mob.getBoundingBox().inflate(CLAW_SWEEP_RADIUS),
                    living -> living != this.mob
                            && this.mob.distanceToSqr(living) <= Mth.square(CLAW_SWEEP_RADIUS)
                            && this.mob.canAttack(living)
                            && !MobUtil.areAllies(this.mob, living)
                            && this.mob.hasLineOfSight(living))) {
                this.mob.doHurtTarget(livingEntity);
            }
        }

        private void endAction() {
            this.actionType = ANIM_NONE;
            this.actionElapsed = 0;
            int animation = AbstractUrbhadhach.this.getAnimationState();
            if (animation == ANIM_BITE || animation == ANIM_CLAW) {
                AbstractUrbhadhach.this.finishAnimation();
            }
        }

        protected void resetAttack() {
            this.attackSpeed = this.mob.getRandom().nextFloat() <= 0.25 ? 10 : 20;
        }

        protected boolean attackTime() {
            return this.attackSpeed <= 0;
        }

        @Override
        protected int getAttackInterval() {
            return this.attackSpeed;
        }

        protected int ticksUntilNextAttack() {
            return this.attackSpeed;
        }
    }

    static class DashAttackGoal extends Goal {
        private static final double MIN_RANGE = 10.0D;
        private static final double MAX_RANGE = 24.0D;
        private static final int WINDUP_TICKS = DASH_START_DURATION;
        private static final int CHARGE_TICKS = 30;
        private static final double CHARGE_SPEED = 1.0D;
        private static final float CHARGE_TURN_SPEED = 2.0F;
        private static final double CONTACT_INFLATE = 0.2D;
        private static final int RESULT_MISS = 0;
        private static final int RESULT_HIT = 1;
        private static final int RESULT_BLOCKED = 2;
        private static final int PHASE_WINDUP = 0;
        private static final int PHASE_CHARGE = 1;
        private final AbstractUrbhadhach mob;
        @Nullable
        private LivingEntity target;
        private Vec3 direction = Vec3.ZERO;
        private float heading;
        private int phase = -1;
        private int timer;

        DashAttackGoal(AbstractUrbhadhach mob) {
            this.mob = mob;
            this.setFlags(EnumSet.allOf(Flag.class));
        }

        @Override
        public boolean canUse() {
            if (this.mob.isVehicle() || this.mob.isPlayingSpecialAnimation()) {
                return false;
            }
            LivingEntity target = this.mob.getTarget();
            if (target == null || !target.isAlive() || !this.mob.onGround() || this.mob.isUnderWater()
                    || !this.mob.hasLineOfSight(target)) {
                return false;
            }
            double distance = this.mob.distanceToSqr(target);
            if (distance < Mth.square(MIN_RANGE) || distance > Mth.square(MAX_RANGE)) {
                return false;
            }
            return this.mob.getRandom().nextFloat() < 0.25F;
        }

        @Override
        public boolean canContinueToUse() {
            return this.phase >= 0 && this.target != null;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void start() {
            this.target = this.mob.getTarget();
            this.phase = PHASE_WINDUP;
            this.timer = 0;
            this.mob.getNavigation().stop();
            this.mob.setZza(0.0F);
            this.mob.setXxa(0.0F);
            this.mob.setStanding(false);
            this.mob.startAnimation(ANIM_DASH_START, DASH_START_DURATION);
        }

        @Override
        public void tick() {
            if (this.target == null || !this.target.isAlive()) {
                this.phase = -1;
                return;
            }
            ++this.timer;
            this.mob.getLookControl().setLookAt(this.target,
                    this.phase == PHASE_CHARGE ? CHARGE_TURN_SPEED : 30.0F, 30.0F);
            switch (this.phase) {
                case PHASE_WINDUP -> {
                    this.mob.getNavigation().stop();
                    this.updateDirection();
                    if (this.timer >= WINDUP_TICKS) {
                        this.beginCharge();
                    }
                }
                case PHASE_CHARGE -> {
                    if (this.mob.horizontalCollision || this.mob.isInWater() || this.mob.isInLava()) {
                        this.mob.startAnimation(ANIM_DASH_STUN, DASH_STUN_DURATION);
                        this.mob.stunEndQueued = true;
                        this.phase = -1;
                    } else {
                        int contact = this.checkDashContact();
                        if (contact == RESULT_BLOCKED) {
                            this.mob.startAnimation(ANIM_DASH_STUN, DASH_STUN_DURATION);
                            this.mob.stunEndQueued = true;
                            this.phase = -1;
                        } else if (contact == RESULT_HIT) {
                            this.mob.setDeltaMovement(0.0D, this.mob.getDeltaMovement().y, 0.0D);
                            this.mob.startAnimation(ANIM_DASH_END, DASH_END_DURATION);
                            this.phase = -1;
                        } else if (this.timer >= CHARGE_TICKS) {
                            this.mob.startAnimation(ANIM_DASH_END, DASH_END_DURATION);
                            this.phase = -1;
                        } else {
                            this.moveTowardsTarget(CHARGE_SPEED, this.mob.getDeltaMovement().y);
                        }
                    }
                }
            }
        }

        private void beginCharge() {
            this.phase = PHASE_CHARGE;
            this.timer = 0;
            this.updateDirection();
            this.heading = this.mob.getYRot();
            this.mob.startAnimation(ANIM_DASH_LOOP, DASH_LOOP_DURATION);
        }

        private int checkDashContact() {
            DamageSource source = this.mob.damageSources().mobAttack(this.mob);
            float damage = (float) this.mob.getAttributeValue(Attributes.ATTACK_DAMAGE);
            for (LivingEntity entity : this.mob.level().getEntitiesOfClass(LivingEntity.class,
                    this.mob.getBoundingBox().inflate(CONTACT_INFLATE),
                    e -> e != this.mob && e.isAlive() && !MobUtil.areAllies(this.mob, e))) {
                boolean vanillaBlockFeedback = !entity.isInvulnerableTo(source) && entity.invulnerableTime <= 10;
                boolean blocked = entity.isDamageSourceBlocked(source);
                boolean hurt = entity == this.target
                        ? this.mob.doHurtTarget(entity)
                        : entity.hurt(source, damage);
                if (blocked || (!hurt && entity.isBlocking())) {
                    this.mob.setDeltaMovement(0.0D, this.mob.getDeltaMovement().y, 0.0D);
                    if (!vanillaBlockFeedback) {
                        entity.level().broadcastEntityEvent(entity, (byte) 29);
                    }
                    return RESULT_BLOCKED;
                }
                if (hurt) {
                    return RESULT_HIT;
                }
            }
            return RESULT_MISS;
        }

        private void moveTowardsTarget(double speed, double y) {
            if (this.target != null) {
                float wanted = this.mob.yawTo(this.target);
                float delta = Mth.wrapDegrees(wanted - this.heading);
                this.heading = Mth.wrapDegrees(this.heading
                        + Mth.clamp(delta, -CHARGE_TURN_SPEED, CHARGE_TURN_SPEED));
                double dx = this.target.getX() - this.mob.getX();
                double dz = this.target.getZ() - this.mob.getZ();
                double distance = Math.sqrt(dx * dx + dz * dz);
                double stopDistance = (this.mob.getBbWidth() + this.target.getBbWidth()) * 0.5D;
                speed = Math.min(Math.max(distance - stopDistance, 0.0D), speed);
            }
            float radians = this.heading * ((float) Math.PI / 180.0F);
            this.direction = new Vec3(-Mth.sin(radians), 0.0D, Mth.cos(radians));
            this.mob.setYRot(this.heading);
            this.mob.yBodyRot = this.heading;
            this.mob.yHeadRot = this.heading;
            this.mob.setDeltaMovement(this.direction.x * speed, y, this.direction.z * speed);
            this.mob.hasImpulse = true;
        }

        private void updateDirection() {
            if (this.target == null) {
                return;
            }
            Vec3 delta = new Vec3(this.target.getX() - this.mob.getX(), 0.0D, this.target.getZ() - this.mob.getZ());
            if (delta.lengthSqr() < 0.25D) {
                return;
            }
            this.direction = delta.normalize();
            this.mob.setYRot(-((float) Mth.atan2(this.target.getX() - this.mob.getX(),
                    this.target.getZ() - this.mob.getZ())) * (180F / (float) Math.PI));
            this.mob.yBodyRot = this.mob.getYRot();
            this.mob.yHeadRot = this.mob.getYRot();
        }

        @Override
        public void stop() {
            this.phase = -1;
            this.target = null;
            this.mob.setStanding(false);
            int animation = this.mob.getAnimationState();
            if (animation == ANIM_DASH_START || animation == ANIM_DASH_LOOP) {
                this.mob.finishAnimation();
            }
        }
    }

    static class UrbhadhachMoveControl extends MoveControl {
        private final AbstractUrbhadhach urbhadhach;

        public UrbhadhachMoveControl(AbstractUrbhadhach urbhadhach) {
            super(urbhadhach);
            this.urbhadhach = urbhadhach;
        }

        @Override
        public void tick() {
            LivingEntity target = this.urbhadhach.getTarget();
            if (this.urbhadhach.isInWater()) {
                if (target != null && target.getY() > this.urbhadhach.getY()) {
                    this.urbhadhach.setDeltaMovement(this.urbhadhach.getDeltaMovement().add(0.0D, 0.002D, 0.0D));
                }

                if (this.operation != Operation.MOVE_TO || this.urbhadhach.getNavigation().isDone()) {
                    this.urbhadhach.setSpeed(0.0F);
                    return;
                }

                double d0 = this.wantedX - this.urbhadhach.getX();
                double d1 = this.wantedY - this.urbhadhach.getY();
                double d2 = this.wantedZ - this.urbhadhach.getZ();
                double d3 = Mth.sqrt((float) (d0 * d0 + d1 * d1 + d2 * d2));
                d1 = d1 / d3;
                float f = (float) (Mth.atan2(d2, d0) * (double) (180F / (float) Math.PI)) - 90.0F;
                this.urbhadhach.setYRot(this.rotlerp(this.urbhadhach.getYRot(), f, 90.0F));
                this.urbhadhach.yBodyRot = this.urbhadhach.getYRot();
                float speed = (float) (this.speedModifier * this.urbhadhach.getAttributeValue(Attributes.MOVEMENT_SPEED));
                float f2 = Mth.lerp(0.125F, this.urbhadhach.getSpeed(), speed);
                this.urbhadhach.setSpeed(f2);
                this.urbhadhach.setDeltaMovement(this.urbhadhach.getDeltaMovement().add(
                        f2 * d0 * 0.005D, f2 * d1 * 0.1D, f2 * d2 * 0.005D));
            } else {
                if (!this.urbhadhach.onGround()) {
                    this.urbhadhach.setDeltaMovement(this.urbhadhach.getDeltaMovement().add(0.0D, -0.008D, 0.0D));
                }
                super.tick();
            }
        }
    }
}
