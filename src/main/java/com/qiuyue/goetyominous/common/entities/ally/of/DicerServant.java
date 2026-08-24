package com.qiuyue.goetyominous.common.entities.ally.of;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.qiuyue.goetyominous.common.entities.ally.of.goals.DicerServantAttackGoal;
import com.qiuyue.goetyominous.common.entities.ally.of.goals.DicerServantLaserGoal;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
import com.unusualmodding.opposing_force.entity.ai.navigation.SmoothGroundPathNavigation;
import com.unusualmodding.opposing_force.entity.utils.AttackState;
import com.unusualmodding.opposing_force.entity.utils.EliteVariant;
import com.unusualmodding.opposing_force.entity.utils.OPPoses;
import com.unusualmodding.opposing_force.registry.OPSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * Opposing Force 的 Dicer（圆锯机器人）移植成 Goety 仆从。
 * 战斗系统照搬原版：攻击状态 1=斩击 2=尾旋 3=十字斩冲刺，外加激光。
 * 与原版 Dicer 不同的是取消了怪物自带的敌对 AI，改用仆从的 Summoned 目标 AI。
 */
public class DicerServant extends Summoned implements AttackState, EliteVariant {
    private static final EntityDataAccessor<Integer> ATTACK_STATE;
    private static final EntityDataAccessor<Boolean> RUNNING;
    private static final EntityDataAccessor<Boolean> LASERING;
    private static final EntityDataAccessor<Boolean> ARCH_DICER;
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState slash1AnimationState = new AnimationState();
    public final AnimationState slash2AnimationState = new AnimationState();
    public final AnimationState crossSlashAnimationState = new AnimationState();
    public final AnimationState tailSpinAnimationState = new AnimationState();
    public final AnimationState laserAnimationState = new AnimationState();
    public int laserCooldown;
    public int slashCooldown;
    public int crossSlashCooldown;
    public int tailSpinCooldown;
    private int slashTicks;
    private int crossSlashTicks;
    private int tailSpinTicks;
    private int laserTicks;

    public DicerServant(EntityType<? extends Owned> entityType, Level level) {
        super(entityType, level);
        // 激光冷却初始值和 OF 原版一致：100~199 随机。索敌后先打近战，
        // 冷却归零才会偶尔放激光，不会一开局就放激光。
        this.laserCooldown = 100 + this.getRandom().nextInt(100);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.DicerServantHealth.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.DicerServantMovementSpeed.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.DicerServantAttackDamage.get())
                .add(Attributes.ATTACK_KNOCKBACK, AttributesConfig.DicerServantAttackKnockback.get());
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new DicerServantLaserGoal(this));
        this.goalSelector.addGoal(2, new DicerServantAttackGoal(this));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
    }

    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        return new SmoothGroundPathNavigation(this, level);
    }

    public float getStepHeight() {
        return this.getAttackState() == 3 ? 1.1F : 0.6F;
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACK_STATE, 0);
        this.entityData.define(RUNNING, false);
        this.entityData.define(LASERING, false);
        this.entityData.define(ARCH_DICER, false);
    }

    public void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("AttackState", this.getAttackState());
        compoundTag.putBoolean("Running", this.isRunning());
        compoundTag.putBoolean("Lasering", this.isLasering());
        compoundTag.putBoolean("ArchDicer", this.isElite());
    }

    public void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setAttackState(compoundTag.getInt("AttackState"));
        this.setRunning(compoundTag.getBoolean("Running"));
        this.setLasering(compoundTag.getBoolean("Lasering"));
        this.setElite(compoundTag.getBoolean("ArchDicer"));
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return MobsConfig.DicerServantLimit.get();
    }

    /**
     * 右键交互：玩家手持铁锭右键圆锯仆从，消耗一块铁锭并回复生命。
     * 只有它的主人能修；满血时不能修。
     */
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(Items.IRON_INGOT) && this.getHealth() < this.getMaxHealth()
                && this.getOwnerId() != null && this.getOwnerId().equals(player.getUUID())) {
            if (!this.level().isClientSide) {
                stack.shrink(1);
                this.heal(8.0F);
                this.playSound(SoundEvents.IRON_GOLEM_REPAIR, 1.0F, 1.0F);
                this.level().broadcastEntityEvent(this, (byte) 20);
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    public int getAttackState() {
        return (Integer)this.entityData.get(ATTACK_STATE);
    }

    public void setAttackState(int attackState) {
        this.entityData.set(ATTACK_STATE, attackState);
    }

    public boolean isRunning() {
        return (Boolean)this.entityData.get(RUNNING);
    }

    public void setRunning(boolean running) {
        this.entityData.set(RUNNING, running);
    }

    public boolean isLasering() {
        return (Boolean)this.entityData.get(LASERING);
    }

    public void setLasering(boolean lasering) {
        this.entityData.set(LASERING, lasering);
    }

    public boolean isElite() {
        return (Boolean)this.entityData.get(ARCH_DICER);
    }

    public void setElite(boolean elite) {
        this.entityData.set(ARCH_DICER, elite);
    }

    public void tick() {
        super.tick();
        if (this.getPose() == Pose.STANDING) {
            if (this.laserCooldown > 0) {
                --this.laserCooldown;
            }

            if (this.slashCooldown > 0) {
                --this.slashCooldown;
            }

            if (this.tailSpinCooldown > 0) {
                --this.tailSpinCooldown;
            }

            if (this.crossSlashCooldown > 0) {
                --this.crossSlashCooldown;
            }
        }

        if (this.slashTicks > 0) {
            --this.slashTicks;
        }

        if (this.crossSlashTicks > 0) {
            --this.crossSlashTicks;
        }

        if (this.tailSpinTicks > 0) {
            --this.tailSpinTicks;
        }

        if (this.laserTicks > 0) {
            --this.laserTicks;
        }

        if (this.slashTicks == 0 && this.getPose() == OPPoses.SLASHING.get()) {
            this.setPose(Pose.STANDING);
        }

        if (this.crossSlashTicks == 0 && this.getPose() == OPPoses.CROSS_SLASHING.get()) {
            this.setPose(Pose.STANDING);
        }

        if (this.tailSpinTicks == 0 && this.getPose() == OPPoses.TAIL_SPINNING.get()) {
            this.setPose(Pose.STANDING);
        }

        if (this.laserTicks == 0 && this.getPose() == OPPoses.LASERING.get()) {
            this.setPose(Pose.STANDING);
        }

        if (this.level().isClientSide) {
            this.setupAnimationStates();
        }
    }

    private void setupAnimationStates() {
        if (this.slashTicks == 0 && (this.slash1AnimationState.isStarted() || this.slash2AnimationState.isStarted())) {
            this.slash1AnimationState.stop();
            this.slash2AnimationState.stop();
        }

        if (this.crossSlashTicks == 0 && this.crossSlashAnimationState.isStarted()) {
            this.crossSlashAnimationState.stop();
        }

        if (this.tailSpinTicks == 0 && this.tailSpinAnimationState.isStarted()) {
            this.tailSpinAnimationState.stop();
        }

        if (this.laserTicks == 0 && this.laserAnimationState.isStarted()) {
            this.laserAnimationState.stop();
        }

        this.idleAnimationState.animateWhen(this.getPose() != OPPoses.LASERING.get()
                && this.getPose() != OPPoses.CROSS_SLASHING.get()
                && this.getPose() != OPPoses.TAIL_SPINNING.get(), this.tickCount);
    }

    public void calculateEntityAnimation(boolean flying) {
        float f1 = (float) Mth.length(this.getX() - this.xo, this.getY() - this.yo, this.getZ() - this.zo);
        float f2 = Math.min(f1 * 8.0F, 1.0F);
        this.walkAnimation.update(f2, 0.4F);
    }

    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> entityDataAccessor) {
        if (DATA_POSE.equals(entityDataAccessor)) {
            if (this.getPose() == OPPoses.SLASHING.get()) {
                this.slashTicks = 20;
                if (this.getRandom().nextBoolean()) {
                    this.slash2AnimationState.start(this.tickCount);
                } else {
                    this.slash1AnimationState.start(this.tickCount);
                }
            } else if (this.getPose() == OPPoses.CROSS_SLASHING.get()) {
                this.crossSlashTicks = 50;
                this.crossSlashAnimationState.start(this.tickCount);
            } else if (this.getPose() == OPPoses.TAIL_SPINNING.get()) {
                this.tailSpinTicks = 20;
                this.tailSpinAnimationState.start(this.tickCount);
            } else if (this.getPose() == OPPoses.LASERING.get()) {
                this.laserTicks = 100;
                this.laserAnimationState.start(this.tickCount);
            } else if (this.getPose() == Pose.STANDING) {
                this.slash1AnimationState.stop();
                this.slash2AnimationState.stop();
                this.crossSlashAnimationState.stop();
                this.laserAnimationState.stop();
            }
        }

        super.onSyncedDataUpdated(entityDataAccessor);
    }

    public boolean doHurtTarget(@NotNull Entity target) {
        if (super.doHurtTarget(target)) {
            if (this.isElite()) {
                target.setSecondsOnFire(5);
            }

            this.playSound((SoundEvent)OPSoundEvents.DICER_ATTACK.get(), 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
            return true;
        }
        return false;
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        return (SoundEvent) OPSoundEvents.DICER_IDLE.get();
    }

    protected @NotNull SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return (SoundEvent)OPSoundEvents.DICER_HURT.get();
    }

    protected @NotNull SoundEvent getDeathSound() {
        return (SoundEvent)OPSoundEvents.DICER_DEATH.get();
    }

    protected void playStepSound(@NotNull BlockPos pos, @NotNull BlockState state) {
        this.playSound(SoundEvents.METAL_STEP, 0.1F, 1.3F);
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
        ATTACK_STATE = SynchedEntityData.defineId(DicerServant.class, EntityDataSerializers.INT);
        RUNNING = SynchedEntityData.defineId(DicerServant.class, EntityDataSerializers.BOOLEAN);
        LASERING = SynchedEntityData.defineId(DicerServant.class, EntityDataSerializers.BOOLEAN);
        ARCH_DICER = SynchedEntityData.defineId(DicerServant.class, EntityDataSerializers.BOOLEAN);
    }
}
