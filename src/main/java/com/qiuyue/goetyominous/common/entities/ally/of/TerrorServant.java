package com.qiuyue.goetyominous.common.entities.ally.of;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.client.sound.TerrorServantSawSoundHandler;
import com.qiuyue.goetyominous.common.entities.ally.of.goals.TerrorServantAttackGoal;
import com.qiuyue.goetyominous.common.entities.ally.of.goals.TerrorServantFollowGoal;
import com.qiuyue.goetyominous.common.entities.ally.of.goals.TerrorServantStrollGoal;
import com.qiuyue.goetyominous.common.entities.ally.of.goals.TerrorServantSwimGoal;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
import com.unusualmodding.opposing_force.entity.ai.navigation.SmoothGroundPathNavigation;
import com.unusualmodding.opposing_force.entity.utils.EliteVariant;
import com.unusualmodding.opposing_force.entity.utils.OPPoses;
import com.unusualmodding.opposing_force.registry.OPSoundEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class TerrorServant extends Summoned implements EliteVariant {
    private static final EntityDataAccessor<Boolean> SAWING;
    private static final EntityDataAccessor<Boolean> HAS_LEGS;
    private static final EntityDataAccessor<Boolean> RUNNING;
    private static final EntityDataAccessor<Integer> FLOP_TIME;
    private static final EntityDataAccessor<Boolean> ANTEDILUVIAN;
    private static final EntityDimensions FISH_OUT_OF_WATER_DIMENSIONS = EntityDimensions.scalable(1.3F, 1.7F);

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState flopAnimationState = new AnimationState();
    public final AnimationState cooldownAnimationState = new AnimationState();
    public final AnimationState swimIdleAnimationState = new AnimationState();
    public final AnimationState growLegsAnimationState = new AnimationState();
    public final AnimationState startSawingAnimationState = new AnimationState();
    public final AnimationState sawingAnimationState = new AnimationState();
    public final AnimationState retractLegsAnimationState = new AnimationState();
    public final AnimationState spinSawAnimationState = new AnimationState();

    public boolean isLandNavigator;
    private SmoothGroundPathNavigation landNavigation;
    private int growLegsTicks;
    private int retractLegsTicks;
    private int startSawingTicks;
    private int stopSawingTicks;
    private int spinSawTicks;
    private int spinSawCooldown;

    public TerrorServant(EntityType<? extends Owned> entityType, Level level) {
        super(entityType, level);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0.0F);
        this.switchNavigator(true);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.TerrorServantHealth.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.TerrorServantMovementSpeed.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.TerrorServantAttackDamage.get());
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, false, false,
                (target) -> target instanceof Enemy && !MobUtil.areAllies(this, target)));
        this.goalSelector.addGoal(1, new TerrorServantAttackGoal(this));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(8, new TerrorServantSwimGoal(this, 1.0D));
        this.goalSelector.addGoal(9, new TerrorServantStrollGoal(this, 1.0D, 110, 0.001F));
    }

    @Override
    public void followGoal() {
        this.goalSelector.addGoal(5, new TerrorServantFollowGoal(this));
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        this.landNavigation = new SmoothGroundPathNavigation(this, level);
        return this.landNavigation;
    }

    public void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveControl = new MoveControl(this);
            if (this.landNavigation == null) {
                this.landNavigation = new SmoothGroundPathNavigation(this, this.level());
            }
            this.navigation = this.landNavigation;
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
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SAWING, false);
        this.entityData.define(HAS_LEGS, false);
        this.entityData.define(RUNNING, false);
        this.entityData.define(FLOP_TIME, 20 + this.random.nextInt(20));
        this.entityData.define(ANTEDILUVIAN, false);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putBoolean("Sawing", this.isSawing());
        compoundTag.putBoolean("HasLegs", this.hasLegs());
        compoundTag.putInt("FlopTime", this.getFlopTime());
        compoundTag.putBoolean("Antediluvian", this.isElite());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setSawing(compoundTag.getBoolean("Sawing"));
        this.setHasLegs(compoundTag.getBoolean("HasLegs"));
        this.setFlopTime(compoundTag.getInt("FlopTime"));
        this.setElite(compoundTag.getBoolean("Antediluvian"));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.setupAnimationStates();
            if (this.isAlive() && this.getPose() == OPPoses.SAWING.get()) {
                TerrorServantSawSoundHandler.playFor(this);
            } else {
                TerrorServantSawSoundHandler.clearFor(this);
            }
        } else if (this.random.nextInt(600) == 0) {
            this.spinSaw();
        }

        boolean canLandNavigate = !this.isInWater() && this.hasLegs();
        if (!canLandNavigate && this.isLandNavigator) {
            this.switchNavigator(false);
        }
        if (canLandNavigate && !this.isLandNavigator) {
            this.switchNavigator(true);
        }

        if (!this.hasLegs() && !this.isInWater() && this.onGround()
                && this.getPose() != OPPoses.GROWING_LEGS.get() && this.getFlopTime() == 0) {
            this.setPose(OPPoses.GROWING_LEGS.get());
            this.growLegsTicks = 40;
        }
        if (this.growLegsTicks > 0) {
            --this.growLegsTicks;
            if (this.growLegsTicks == 0 && this.getPose() == OPPoses.GROWING_LEGS.get()) {
                this.setPose(Pose.STANDING);
                this.setHasLegs(true);
                this.setFlopTime(10 + this.random.nextInt(20));
            }
        }

        if (this.hasLegs() && this.isInWater() && this.getPose() != OPPoses.RETRACTING_LEGS.get()) {
            this.setPose(OPPoses.RETRACTING_LEGS.get());
            this.retractLegsTicks = 10;
        }
        if (this.retractLegsTicks > 0) {
            --this.retractLegsTicks;
            if (this.retractLegsTicks == 0 && this.getPose() == OPPoses.RETRACTING_LEGS.get()) {
                this.setPose(Pose.STANDING);
                this.setHasLegs(false);
                this.setFlopTime(10 + this.random.nextInt(20));
            }
        }

        if (this.getFlopTime() > 0 && this.onGround() && !this.isInWater()) {
            this.setFlopTime(this.getFlopTime() - 1);
        }

        if (this.startSawingTicks > 0) {
            --this.startSawingTicks;
        }
        if (this.stopSawingTicks > 0) {
            --this.stopSawingTicks;
        }
        if (this.spinSawTicks > 0) {
            --this.spinSawTicks;
        }
        if (this.startSawingTicks == 0 && this.getPose() == OPPoses.START_SAWING.get()) {
            this.setPose(OPPoses.SAWING.get());
        }
        if (this.stopSawingTicks == 0 && this.getPose() == OPPoses.RECOVERING.get()) {
            this.setPose(Pose.STANDING);
        }
        if (this.spinSawTicks == 0 && this.getPose() == OPPoses.SPIN_SAW.get()) {
            this.setPose(Pose.STANDING);
        }
        if (this.spinSawCooldown > 0) {
            --this.spinSawCooldown;
        }
    }

    private void setupAnimationStates() {
        if (this.growLegsTicks == 0 && this.growLegsAnimationState.isStarted()) {
            this.growLegsAnimationState.stop();
        }
        if (this.retractLegsTicks == 0 && this.retractLegsAnimationState.isStarted()) {
            this.retractLegsAnimationState.stop();
        }
        if (this.startSawingTicks == 0 && this.startSawingAnimationState.isStarted()) {
            this.startSawingAnimationState.stop();
        }
        if (this.stopSawingTicks == 0 && this.cooldownAnimationState.isStarted()) {
            this.cooldownAnimationState.stop();
        }
        if (this.spinSawTicks == 0 && this.spinSawAnimationState.isStarted()) {
            this.spinSawAnimationState.stop();
        }
        this.idleAnimationState.animateWhen(!this.isInWaterOrBubble() && this.hasLegs(), this.tickCount);
        this.flopAnimationState.animateWhen(!this.isInWaterOrBubble() && !this.hasLegs()
                && this.getPose() != OPPoses.GROWING_LEGS.get(), this.tickCount);
        this.swimIdleAnimationState.animateWhen(this.isInWaterOrBubble()
                && this.getPose() != OPPoses.RETRACTING_LEGS.get(), this.tickCount);
        this.growLegsAnimationState.animateWhen(this.growLegsAnimationState.isStarted(), this.tickCount);
        this.retractLegsAnimationState.animateWhen(this.retractLegsAnimationState.isStarted(), this.tickCount);
        this.startSawingAnimationState.animateWhen(this.startSawingAnimationState.isStarted(), this.tickCount);
        this.cooldownAnimationState.animateWhen(this.cooldownAnimationState.isStarted(), this.tickCount);
        this.spinSawAnimationState.animateWhen(this.spinSawAnimationState.isStarted(), this.tickCount);
        this.sawingAnimationState.animateWhen(this.sawingAnimationState.isStarted(), this.tickCount);
    }

    public void spinSaw() {
        if (this.spinSawCooldown == 0 && this.getPose() == Pose.STANDING && this.getTarget() == null
                && (this.hasLegs() || this.isInWater())) {
            this.setPose(OPPoses.SPIN_SAW.get());
            this.spinSawTicks = 40;
            this.spinSawCooldown = 200 + this.random.nextInt(200);
        }
    }

    @Override
    public void calculateEntityAnimation(boolean flying) {
        float f1 = (float) Mth.length(this.getX() - this.xo, this.getY() - this.yo, this.getZ() - this.zo);
        float f2 = Math.min(f1 * 10.0F, 1.0F);
        this.walkAnimation.update(f2, 0.4F);
    }

    @Override
    public void travel(@NotNull Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
            if (this.horizontalCollision) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, 0.4D * (double) this.getSpeed(), 0.0D));
            }
        } else {
            super.travel(travelVector);
        }
    }

    @Override
    public InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        if (!this.level().isClientSide) {
            ItemStack itemstack = player.getItemInHand(hand);
            if (this.getTrueOwner() != null && player == this.getTrueOwner()
                    && itemstack.is(ItemTags.FISHES) && this.getHealth() < this.getMaxHealth()) {
                FoodProperties foodProperties = itemstack.getFoodProperties(this);
                if (foodProperties != null) {
                    this.heal((float) foodProperties.getNutrition());
                    if (!player.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    this.gameEvent(GameEvent.EAT, this);
                    if (this.level() instanceof ServerLevel serverLevel) {
                        for (int i = 0; i < 7; ++i) {
                            double d0 = this.getRandom().nextGaussian() * 0.02D;
                            double d1 = this.getRandom().nextGaussian() * 0.02D;
                            double d2 = this.getRandom().nextGaussian() * 0.02D;
                            serverLevel.sendParticles(ParticleTypes.HEART,
                                    this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D),
                                    0, d0, d1, d2, 0.5D);
                        }
                    }
                    player.swing(hand);
                    return InteractionResult.CONSUME;
                }
            }
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        if (this.level().isClientSide) {
            TerrorServantSawSoundHandler.clearFor(this);
        }
        super.remove(reason);
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> entityDataAccessor) {
        if (HAS_LEGS.equals(entityDataAccessor)) {
            this.refreshDimensions();
        }
        if (DATA_POSE.equals(entityDataAccessor)) {
            if (this.getPose() == OPPoses.GROWING_LEGS.get()) {
                this.flopAnimationState.stop();
                this.growLegsAnimationState.start(this.tickCount);
            } else if (this.getPose() == OPPoses.RETRACTING_LEGS.get()) {
                this.swimIdleAnimationState.stop();
                this.retractLegsAnimationState.start(this.tickCount);
            } else if (this.getPose() == OPPoses.START_SAWING.get()) {
                this.startSawingTicks = 20;
                this.startSawingAnimationState.start(this.tickCount);
            } else if (this.getPose() == OPPoses.SAWING.get()) {
                this.startSawingAnimationState.stop();
                this.sawingAnimationState.start(this.tickCount);
            } else if (this.getPose() == OPPoses.RECOVERING.get()) {
                this.sawingAnimationState.stop();
                this.stopSawingTicks = 50;
                this.cooldownAnimationState.start(this.tickCount);
            } else if (this.getPose() == OPPoses.SPIN_SAW.get()) {
                this.spinSawTicks = 40;
                this.spinSawAnimationState.start(this.tickCount);
            } else if (this.getPose() == Pose.STANDING) {
                this.growLegsAnimationState.stop();
                this.retractLegsAnimationState.stop();
                this.sawingAnimationState.stop();
                this.startSawingAnimationState.stop();
                this.cooldownAnimationState.stop();
                this.spinSawAnimationState.stop();
            }
        }
        super.onSyncedDataUpdated(entityDataAccessor);
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pose) {
        return this.hasLegs() ? FISH_OUT_OF_WATER_DIMENSIONS.scale(this.getScale()) : super.getDimensions(pose);
    }

    @Override
    public void refreshDimensions() {
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        super.refreshDimensions();
        this.setPos(x, y, z);
    }

    @Override
    public boolean isImmobile() {
        return super.isImmobile() || this.getPose() == OPPoses.GROWING_LEGS.get();
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public float maxUpStep() {
        return this.isInWater() ? 1.25F : 0.6F;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData spawnData,
                                        @Nullable CompoundTag compoundTag) {
        spawnData = super.finalizeSpawn(level, difficulty, spawnType, spawnData, compoundTag);

        this.setHasLegs(true);

        RandomSource random = level.getRandom();
        if (random.nextInt(this.getEliteSpawnChance()) == 0) {
            this.setElite(true);
            this.setEliteStats(this);
        }

        return spawnData;
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return MobsConfig.TerrorServantLimit.get();
    }

    @Override
    public MobType getMobType() {
        return MobType.WATER;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 160;
    }

    public boolean isSawing() {
        return this.entityData.get(SAWING);
    }

    public void setSawing(boolean sawing) {
        this.entityData.set(SAWING, sawing);
    }

    public boolean hasLegs() {
        return this.entityData.get(HAS_LEGS);
    }

    public void setHasLegs(boolean hasLegs) {
        this.entityData.set(HAS_LEGS, hasLegs);
    }

    public boolean isRunning() {
        return this.entityData.get(RUNNING);
    }

    public void setRunning(boolean running) {
        this.entityData.set(RUNNING, running);
    }

    public int getFlopTime() {
        return this.entityData.get(FLOP_TIME);
    }

    public void setFlopTime(int flopTime) {
        this.entityData.set(FLOP_TIME, flopTime);
    }

    @Override
    public boolean isElite() {
        return this.entityData.get(ANTEDILUVIAN);
    }

    @Override
    public void setElite(boolean elite) {
        this.entityData.set(ANTEDILUVIAN, elite);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return OPSoundEvents.TERROR_IDLE.get();
    }

    @Override
    protected @NotNull SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return OPSoundEvents.TERROR_HURT.get();
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return OPSoundEvents.TERROR_DEATH.get();
    }

    static {
        SAWING = SynchedEntityData.defineId(TerrorServant.class, EntityDataSerializers.BOOLEAN);
        HAS_LEGS = SynchedEntityData.defineId(TerrorServant.class, EntityDataSerializers.BOOLEAN);
        RUNNING = SynchedEntityData.defineId(TerrorServant.class, EntityDataSerializers.BOOLEAN);
        FLOP_TIME = SynchedEntityData.defineId(TerrorServant.class, EntityDataSerializers.INT);
        ANTEDILUVIAN = SynchedEntityData.defineId(TerrorServant.class, EntityDataSerializers.BOOLEAN);
    }
}
