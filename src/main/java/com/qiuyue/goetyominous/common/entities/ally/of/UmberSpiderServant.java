package com.qiuyue.goetyominous.common.entities.ally.of;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.common.entities.ally.of.goals.UmberSpiderServantAttackGoal;
import com.qiuyue.goetyominous.common.entities.ally.of.goals.UmberSpiderServantFearLightGoal;
import com.qiuyue.goetyominous.common.entities.ally.of.goals.UmberSpiderServantLeapAtTargetGoal;
import com.qiuyue.goetyominous.common.entities.ally.of.goals.UmberSpiderServantRandomLookAroundGoal;
import com.qiuyue.goetyominous.common.entities.ally.of.goals.UmberSpiderServantRandomStrollGoal;
import com.unusualmodding.opposing_force.entity.utils.AttackState;
import com.unusualmodding.opposing_force.entity.utils.EliteVariant;
import com.unusualmodding.opposing_force.registry.OPMobEffects;
import com.unusualmodding.opposing_force.registry.OPSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class UmberSpiderServant extends Summoned implements AttackState, EliteVariant {
    private static final EntityDataAccessor<Integer> ATTACK_STATE;
    private static final EntityDataAccessor<Boolean> ATTACKING;
    public static final EntityDataAccessor<Integer> LIGHT_THRESHOLD;
    private static final EntityDataAccessor<Boolean> TENEBROUS;
    private static final EntityDataAccessor<Byte> DATA_FLAGS_ID;

    public int fleeLightFor;
    public Vec3 fleeFromPosition;

    public final AnimationState idleAnimationState = new AnimationState();

    public UmberSpiderServant(EntityType<? extends Owned> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.FOLLOW_RANGE, 16.0D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false,
                (target) -> target instanceof Enemy && !MobUtil.areAllies(this, target)));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new UmberSpiderServantFearLightGoal(this));
        this.goalSelector.addGoal(2, new UmberSpiderServantLeapAtTargetGoal(this));
        this.goalSelector.addGoal(3, new UmberSpiderServantAttackGoal(this));
        this.goalSelector.addGoal(7, new UmberSpiderServantRandomStrollGoal(this));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(9, new UmberSpiderServantRandomLookAroundGoal(this));
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        return new WallClimberNavigation(this, level);
    }

    @Override
    public double getPassengersRidingOffset() {
        return this.getBbHeight() * 0.5F;
    }

    public boolean isClimbing() {
        return (this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
    }

    public void setClimbing(boolean climbing) {
        byte flag = this.entityData.get(DATA_FLAGS_ID);
        if (climbing) {
            flag = (byte) (flag | 1);
        } else {
            flag = (byte) (flag & -2);
        }
        this.entityData.set(DATA_FLAGS_ID, flag);
    }

    @Override
    public boolean onClimbable() {
        return this.isClimbing();
    }

    @Override
    public MobType getMobType() {
        return MobType.ARTHROPOD;
    }

    @Override
    public void makeStuckInBlock(@NotNull BlockState state, @NotNull Vec3 motion) {
        if (!state.is(Blocks.COBWEB)) {
            super.makeStuckInBlock(state, motion);
        }
    }

    @Override
    protected float getStandingEyeHeight(@NotNull Pose pose, EntityDimensions dimensions) {
        return dimensions.height * 0.65F;
    }

    @Override
    public int getAttackState() {
        return this.entityData.get(ATTACK_STATE);
    }

    @Override
    public void setAttackState(int attackState) {
        this.entityData.set(ATTACK_STATE, attackState);
    }

    public boolean isAttacking() {
        return this.entityData.get(ATTACKING);
    }

    public void setAttacking(boolean attacking) {
        this.entityData.set(ATTACKING, attacking);
    }

    public int getLightThreshold() {
        return this.entityData.get(LIGHT_THRESHOLD);
    }

    public void setLightThreshold(int lightThreshold) {
        this.entityData.set(LIGHT_THRESHOLD, lightThreshold);
    }

    @Override
    public boolean isElite() {
        return this.entityData.get(TENEBROUS);
    }

    @Override
    public void setElite(boolean elite) {
        this.entityData.set(TENEBROUS, elite);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACK_STATE, 0);
        this.entityData.define(ATTACKING, false);
        this.entityData.define(LIGHT_THRESHOLD, 10);
        this.entityData.define(TENEBROUS, false);
        this.entityData.define(DATA_FLAGS_ID, (byte) 0);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("AttackState", this.getAttackState());
        compoundTag.putBoolean("Attacking", this.isAttacking());
        compoundTag.putInt("LightThreshold", this.getLightThreshold());
        compoundTag.putBoolean("Tenebrous", this.isElite());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setAttackState(compoundTag.getInt("AttackState"));
        this.setAttacking(compoundTag.getBoolean("Attacking"));
        this.setLightThreshold(compoundTag.getInt("LightThreshold"));
        this.setElite(compoundTag.getBoolean("Tenebrous"));
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            this.setClimbing(this.horizontalCollision);
        }
        if (this.level().isClientSide) {
            this.setupAnimationStates();
        }
    }

    private void setupAnimationStates() {
        this.idleAnimationState.animateWhen(this.isAlive(), this.tickCount);
    }

    @Override
    public boolean canBeAffected(@NotNull MobEffectInstance effect) {
        if (effect.getEffect() == MobEffects.POISON || effect.getEffect() == OPMobEffects.GLOOM_TOXIN.get()) {
            MobEffectEvent.Applicable event = new MobEffectEvent.Applicable(this, effect);
            MinecraftForge.EVENT_BUS.post(event);
            return event.getResult() == Event.Result.ALLOW;
        }
        return super.canBeAffected(effect);
    }

    @Override
    protected boolean isSunSensitive() {
        return true;
    }

    @Override
    public void aiStep() {
        if (this.isAlive()) {
            boolean flag = this.isSunSensitive() && this.isSunBurnTick();
            if (flag) {
                this.setSecondsOnFire(8);
            }
        }
        if (!this.isElite()) {
            BlockPos pos = this.blockPosition();
            BlockPos offset = pos.offset(this.getRandom().nextInt(20) - 10,
                    this.getRandom().nextInt(6) - 3,
                    this.getRandom().nextInt(20) - 10);
            if (this.level().getBrightness(LightLayer.BLOCK, this.blockPosition()) > this.getLightThreshold()
                    || this.isOnFire()) {
                this.fleeFromPosition = Vec3.atBottomCenterOf(offset);
            }
        }
        if (this.fleeLightFor > 0) {
            --this.fleeLightFor;
        }
        super.aiStep();
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity target) {
        if (super.doHurtTarget(target)) {
            if (target instanceof LivingEntity living) {
                int duration = 0;
                if (this.level().getDifficulty() == Difficulty.NORMAL) {
                    duration = 5;
                } else if (this.level().getDifficulty() == Difficulty.HARD) {
                    duration = 10;
                }
                if (duration > 0) {
                    living.addEffect(new MobEffectInstance(OPMobEffects.GLOOM_TOXIN.get(), duration * 20,
                            this.isElite() ? 1 : 0), this);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return OPSoundEvents.UMBER_SPIDER_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return OPSoundEvents.UMBER_SPIDER_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return OPSoundEvents.UMBER_SPIDER_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockState) {
        this.playSound(SoundEvents.SPIDER_STEP, 0.1F, 0.8F);
    }

    @Override
    public int getAmbientSoundInterval() {
        return 180;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData spawnData,
                                        @Nullable CompoundTag compoundTag) {
        spawnData = super.finalizeSpawn(level, difficulty, spawnType, spawnData, compoundTag);

        RandomSource random = level.getRandom();
        if (random.nextInt(this.getEliteSpawnChance()) == 0) {
            this.setElite(true);
            this.setEliteStats(this);
        }

        return spawnData;
    }

    static {
        ATTACK_STATE = SynchedEntityData.defineId(UmberSpiderServant.class, EntityDataSerializers.INT);
        ATTACKING = SynchedEntityData.defineId(UmberSpiderServant.class, EntityDataSerializers.BOOLEAN);
        LIGHT_THRESHOLD = SynchedEntityData.defineId(UmberSpiderServant.class, EntityDataSerializers.INT);
        TENEBROUS = SynchedEntityData.defineId(UmberSpiderServant.class, EntityDataSerializers.BOOLEAN);
        DATA_FLAGS_ID = SynchedEntityData.defineId(UmberSpiderServant.class, EntityDataSerializers.BYTE);
    }
}
