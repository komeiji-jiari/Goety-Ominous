package com.qiuyue.goetyominous.common.entities.ally.of;

import com.Polarice3.Goety.common.entities.ai.servant.ServantFollowOwnerGoal;
import com.Polarice3.Goety.common.entities.ai.servant.ServantFollowOwnerWaterGoal;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.common.entities.ally.of.goals.VoltServantLeapGoal;
import com.qiuyue.goetyominous.common.entities.ally.of.goals.VoltServantShootGoal;
import com.qiuyue.goetyominous.common.entities.ally.of.goals.VoltServantShootInWaterGoal;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
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
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class VoltServant extends Summoned implements AttackState, EliteVariant, PowerableMob {
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
                // 对齐 OF 原版 Volt：16 点血（8 颗心）。合并远端后改走配置项，默认值就是 16.0。
                .add(Attributes.MAX_HEALTH, AttributesConfig.VoltServantHealth.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.VoltServantMovementSpeed.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.VoltServantFollowRange.get())
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 0.5D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        // mustSee 必须是 true！
        // Goety 的 FollowOwnerGoal.canUse() 里有一条硬性条件：getTarget() != null 就直接 return false。
        // 而 TargetGoal.canContinueToUse() 里是 return !mustSee || hasLineOfSight(target)，
        // mustSee=false 时恒为 true —— 仆从会隔着墙/山/地洞锁定一个根本看不见的敌人并且永不放手，
        // 跟随 goal 从此再也启动不了（优先级调到多少都没用，因为它压根没跑过）。
        // Goety 自己的 SummonTargetGoal 用的就是 (mob, LivingEntity.class, 5, true, false, predicate)。
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false,
                (target) -> target instanceof Enemy && !MobUtil.areAllies(this, target)));
        this.goalSelector.addGoal(1, new VoltServantLeapGoal(this));
        this.goalSelector.addGoal(2, new VoltServantShootGoal(this));
        this.goalSelector.addGoal(2, new VoltServantShootInWaterGoal(this));
        // 游荡/环视排在 7 之后：Goety 的 FollowOwnerGoal 默认优先级是 5，
        // 而 Goal.canBeReplacedBy 允许「优先级数字更小」的 goal 抢占正在跑的 goal，
        // 所以只要数字小于 5，伏特瑶就会追到一半跑去闲逛、回不到主人身边。
        // 合并远端(2026-09-24)：游荡改用 Goety 原生的 Summoned.WanderGoal，游泳 goal 加上
        // 「驻守/被指令时不动」的守卫；优先级仍保留本项目的 7/8/9。
        this.goalSelector.addGoal(7, new Summoned.WanderGoal<>(this, 1.0D, 110, 0.001F));
        this.goalSelector.addGoal(7, new RandomSwimmingGoal(this, 1.0D, 10) {
            @Override
            public boolean canUse() {
                return super.canUse() && !VoltServant.this.isStaying() && !VoltServant.this.isCommanded();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && !VoltServant.this.isStaying() && !VoltServant.this.isCommanded();
            }
        });
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
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
        this.rebuildFollowGoal();
    }

    /**
     * 故意把 Goety 的默认跟随 goal 注册变成空操作，改由 rebuildFollowGoal() 手动挂。
     *
     * 原因：Summoned$FollowOwnerGoal 在**构造的那一刻**就把 mob.getNavigation() 存进了自己的
     * final 字段（字节码：invokevirtual Mob.getNavigation() → putfield），此后永不更新。
     * 它假定「一个生物的导航器一辈子不变」，普通陆生仆从确实如此 —— 但伏特瑶是水陆双栖的，
     * switchNavigator() 会把整个导航器对象换掉，这个假定就不成立了。
     * 后果：跟随 goal 每 10 tick 把算好的路径塞给**已经被丢弃的旧导航器**，而 Mob.serverAiStep()
     * 每 tick 驱动的是 this.navigation（新导航器）→ 新导航器手里永远没有路径，实体一步都迈不出去。
     * 偏偏 FollowOwnerGoal.tick() 第一句就是 setLookAt(owner)，LOOK 标志位照常工作，
     * 所以表现是「一直扭头盯着主人但不动」，看着像想跟随却跟不上。
     */
    @Override
    public void followGoal() {
    }

    /**
     * 按当前真正在用的导航器重新挂载跟随 goal：陆/空用 FollowOwnerGoal，水里用 FollowOwnerWaterGoal。
     * 优先级 4 比 Goety 默认的 5 更高，保证跟随优先于游荡。
     */
    private void rebuildFollowGoal() {
        for (WrappedGoal wrapped : new ArrayList<>(this.goalSelector.getAvailableGoals())) {
            Goal goal = wrapped.getGoal();
            if (goal instanceof ServantFollowOwnerGoal<?> || goal instanceof ServantFollowOwnerWaterGoal<?>) {
                this.goalSelector.removeGoal(goal);
            }
        }
        if (this.navigation instanceof GroundPathNavigation || this.navigation instanceof FlyingPathNavigation) {
            this.goalSelector.addGoal(4, new Summoned.FollowOwnerGoal<>(this, this.getFollowSpeed(), 10.0F, 2.0F));
        } else {
            this.goalSelector.addGoal(4, new Summoned.FollowOwnerWaterGoal(this, this.getFollowSpeed(), 10.0F, 2.0F));
        }
    }

    @Override
    public void travel(Vec3 vec3) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), vec3);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
            if (this.horizontalCollision) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, 0.4D * this.getSpeed(), 0.0D));
            }
        } else {
            super.travel(vec3);
        }
    }

    /**
     * 水下呼吸。OF 原版 Volt 覆写了 canBreatheUnderwater() 返回 true，
     * 移植时漏了，导致伏特瑶一进水就按普通陆生生物扣氧气、被淹死。
     */
    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    /** 不被水流推着走，自己在水里游（OF 原版 isPushedByFluid() 返回 false）。 */
    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader levelReader) {
        if (levelReader.getFluidState(pos).is(FluidTags.WATER)) {
            return 10.0F;
        }
        return 0.0F;
    }

    @Override
    public MobType getMobType() {
        return MobType.WATER;
    }

    @Override
    public boolean isPowered() {
        return this.isCharged();
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
        return super.isInvulnerableTo(source)
                || source.is(DamageTypeTags.IS_FALL)
                || source.is(OPDamageTypes.ELECTRIC)
                || source.is(OPDamageTypes.ELECTRIFIED);
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    /**
     * 落地不对方块做任何处理（OF 原版 Volt 的 checkFallDamage 就是空实现）。
     * 少了它，伏特瑶跳来跳去会把主人的农田踩成泥土、把雪踩实。
     */
    @Override
    protected void checkFallDamage(double y, boolean onGround, @NotNull BlockState state, @NotNull BlockPos pos) {
        // 故意留空，对齐原版
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
    public int getSummonLimit(LivingEntity owner) {
        return MobsConfig.VoltServantLimit.get();
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

    @Override
    public int getAmbientSoundInterval() {
        return 400;
    }

    @Override
    public void thunderHit(ServerLevel level, LightningBolt lightning) {
        this.setCharged(true);
        this.heal(this.getMaxHealth());
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
