package com.qiuyue.goetyominous.common.entities.ally.mobs;
import com.Polarice3.Goety.client.particles.SmashParticleOption;
import com.Polarice3.Goety.common.entities.ai.path.GroundPathNavigatorFat;
import com.Polarice3.Goety.common.entities.ai.path.ModWaterPathNavigation;
import com.Polarice3.Goety.common.entities.ally.Leapleaf;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.util.CameraShake;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.Nullable;
import java.util.EnumSet;
import java.util.List;
public class Leapkelp extends Leapleaf {
    public static final int MAX_MOISTNESS = 40000;
    public static final int SEEK_WATER_MOISTNESS = 4000;
    private static final int DRY_OUT_TICKS = 3 * 60 * 20;
    private static final int MAX_REST_TICKS = 20;
    private static final float SWIM_SPEED_MULTIPLIER = 4.0F;
    private static final float SWIM_THRUST = 0.014F;
    private static final int SWIM_MAX_TURN_Y = 90;
    private static final int SWIM_MAX_TURN_X = 85;
    private static final float SWIM_PITCH_STEP = 5.0F;
    private static final float SWIM_CLIMB_SPEED = 0.3F;
    private static final float SWIM_MIN_DISTANCE_SQR = 2.5E-7F;
    private static final float SWIM_POSE_STEP = 0.15F;
    private static final int SWIM_UP_RETARGET_COOLDOWN = 10;
    private static final int MOISTNESS_DRAIN_DAY = 10;
    private static final int MOISTNESS_DRAIN_NIGHT = 5;
    private static final int LEAP_COOLDOWN_TICKS = 10;
    private static final int LEAP_STUCK_TICKS = 80;
    private static final int CHARGE_STALE_TICKS = 24;
    private static final int SMASH_STATE_RIGHT = 8;
    private static final int SMASH_STATE_LEFT = 9;
    private static final int SMASH_HIT_TICK = 16;
    private static final int SMASH_END_TICK = 36;
    private static final float SMASH_HIT_VOLUME = 0.5F;
    private static final double SMASH_LAUNCH_POWER = 2.0D;
    private static final double SMASH_LAUNCH_LIFT = 0.5D;
    public static String RIGHT_SMASH = "right_smash";
    public static String LEFT_SMASH = "left_smash";
    private static final EntityDataAccessor<Integer> MOISTNESS = SynchedEntityData.defineId(Leapkelp.class, EntityDataSerializers.INT);
    public AnimationState rightSmashAnimationState = new AnimationState();
    public AnimationState leftSmashAnimationState = new AnimationState();
    protected final ModWaterPathNavigation waterNavigation;
    protected final GroundPathNavigation groundNavigation;
    private boolean searchingForLand;
    private int smashAnimation;
    private int surfaceLeapCooldown;
    private float swimPoseAmount;
    private float swimPoseAmountO;
    public Leapkelp(EntityType<? extends Owned> type, Level level) {
        super(type, level);
        this.moveControl = new LeapkelpMoveControl(this);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.UNPASSABLE_RAIL, 0.0F);
        this.waterNavigation = new ModWaterPathNavigation(this, level);
        this.groundNavigation = new GroundPathNavigatorFat(this, level);
    }
    public static AttributeSupplier.Builder setCustomAttributes() {
        return Leapleaf.setCustomAttributes().add(Attributes.MOVEMENT_SPEED, 0.3D);
    }
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.getAvailableGoals().removeIf(wrapped -> {
            Goal goal = wrapped.getGoal();
            Class<?> type = goal.getClass();
            return goal instanceof FloatGoal
                    || type.getEnclosingClass() == Leapleaf.class && type.getSimpleName().equals("AttackGoal");
        });
        this.goalSelector.addGoal(0, new LeapkelpWaterSmashGoal(this));
        this.goalSelector.addGoal(0, new LeapkelpSmashGoal(this));
        this.goalSelector.addGoal(2, new LeapkelpSurfaceLeapGoal(this));
        this.goalSelector.addGoal(3, new LeapkelpGoToBeachGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new LeapkelpGoToWaterGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new LeapkelpSwimAttackGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LeapkelpSwimUpGoal(this, 1.0D, this.level().getSeaLevel()));
        this.goalSelector.addGoal(7, new Summoned.WaterWanderGoal<Leapkelp>(this, 1.0D) {
            @Override
            public boolean canUse() {
                return super.canUse() && !Leapkelp.this.isNovelty;
            }
            @Override
            protected Vec3 getPosition() {
                if (Leapkelp.this.isInWaterOrBubble()) {
                    Vec3 view = Leapkelp.this.getViewVector(0.0F);
                    Vec3 waterPos = AirAndWaterRandomPos.getPos(Leapkelp.this, 10, 7, -2, view.x, view.z, Math.PI / 2.0D);
                    if (waterPos != null) {
                        return waterPos;
                    }
                }
                return super.getPosition();
            }
        });
    }
    @Override
    public void followGoal() {
        this.goalSelector.addGoal(5, new LeapkelpFollowOwnerGoal(this, 1.0D, 10.0F, 2.0F));
    }
    @Override
    public int getAnimationState(String animation) {
        if (RIGHT_SMASH.equals(animation)) {
            return SMASH_STATE_RIGHT;
        }
        if (LEFT_SMASH.equals(animation)) {
            return SMASH_STATE_LEFT;
        }
        return super.getAnimationState(animation);
    }
    @Override
    public List<AnimationState> getAllAnimations() {
        List<AnimationState> animationStates = super.getAllAnimations();
        animationStates.add(this.rightSmashAnimationState);
        animationStates.add(this.leftSmashAnimationState);
        return animationStates;
    }
    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        super.onSyncedDataUpdated(accessor);
        if (!this.level().isClientSide) {
            return;
        }
        int animation = this.getCurrentAnimation();
        if (animation != SMASH_STATE_RIGHT && animation != SMASH_STATE_LEFT) {
            this.smashAnimation = 0;
            return;
        }
        if (this.smashAnimation == animation) {
            return;
        }
        this.smashAnimation = animation;
        AnimationState smash = animation == SMASH_STATE_RIGHT ? this.rightSmashAnimationState : this.leftSmashAnimationState;
        smash.startIfStopped(this.tickCount);
        this.stopMostAnimation(smash);
    }
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(MOISTNESS, MAX_MOISTNESS);
    }
    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Moisture", this.getMoistness());
    }
    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Moisture")) {
            this.setMoistness(compound.getInt("Moisture"));
        }
    }
    public int getMoistness() {
        return this.entityData.get(MOISTNESS);
    }
    public void setMoistness(int moistness) {
        this.entityData.set(MOISTNESS, moistness);
    }
    protected boolean needsWater() {
        if (this.getMoistness() < SEEK_WATER_MOISTNESS) {
            return true;
        }
        return this.getTrueOwner() != null && this.isFollowing() && this.getTrueOwner().isInWater();
    }
    @Override
    public void tick() {
        super.tick();
        this.updateSwimPoseAmount();
        if (this.level().isClientSide) {
            return;
        }
        if (this.restTick > MAX_REST_TICKS) {
            this.restTick = MAX_REST_TICKS;
        }
        if (this.isLeaping() && this.leapTick > LEAP_STUCK_TICKS) {
            this.setLeaping(false);
            this.coolTick = LEAP_COOLDOWN_TICKS;
        }
        if (this.isCharging() && this.chargeTick >= CHARGE_STALE_TICKS
                && this.goalSelector.getRunningGoals().noneMatch(running -> running.getFlags().contains(Goal.Flag.JUMP))) {
            this.setCharging(false);
            this.chargeTick = 0;
        }
        if (this.isInWater() && !this.isLeaping() && !this.onGround()) {
            this.coolTick = 0;
        }
        if (this.surfaceLeapCooldown > 0) {
            --this.surfaceLeapCooldown;
        }
        if (this.isInWaterRainOrBubble() || !com.qiuyue.goetyominous.config.MobsConfig.LeapkelpMoistness.get()) {
            if (this.getMoistness() < MAX_MOISTNESS) {
                this.setMoistness(this.getMoistness() + 2);
            }
        } else {
            int dry = this.level().isDay() ? MOISTNESS_DRAIN_DAY : MOISTNESS_DRAIN_NIGHT;
            this.setMoistness(this.getMoistness() - dry);
        }
    }
    protected boolean wantsToSwim() {
        if (this.searchingForLand) {
            return true;
        }
        if (this.wantsToLeaveWater()) {
            return false;
        }
        if (this.isInWater()) {
            return true;
        }
        if (this.getTarget() != null && this.getTarget().isInWater()) {
            return true;
        }
        return this.getTrueOwner() != null && this.isFollowing() && this.getTrueOwner().isInWater();
    }
    @Override
    public void updateSwimming() {
        if (!this.level().isClientSide) {
            if (this.isEffectiveAi() && this.isInWater()) {
                this.navigation = this.waterNavigation;
                this.setSwimming(this.wantsToSwim());
            } else {
                this.navigation = this.groundNavigation;
                this.setSwimming(false);
            }
        }
    }
    protected void updateSwimPoseAmount() {
        this.swimPoseAmountO = this.swimPoseAmount;
        float target = this.isSwimmingPose() ? 1.0F : 0.0F;
        this.swimPoseAmount = Mth.approach(this.swimPoseAmount, target, SWIM_POSE_STEP);
    }
    protected boolean isSwimmingPose() {
        return this.isInWater()
                && this.isEyeInFluidType(ForgeMod.WATER_TYPE.get())
                && this.level().noCollision(this, this.getBoundingBox())
                && !this.isLeaping()
                && !this.isCharging();
    }
    public int getSurfaceLeapCooldown() {
        return this.surfaceLeapCooldown;
    }
    public void setSurfaceLeapCooldown(int ticks) {
        this.surfaceLeapCooldown = ticks;
    }
    public float getSwimPoseAmount(float partialTicks) {
        return Mth.lerp(partialTicks, this.swimPoseAmountO, this.swimPoseAmount);
    }
    @Override
    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isInWater() && this.wantsToSwim() && !this.isLeaping()) {
            this.moveRelative(SWIM_THRUST, travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
        } else {
            super.travel(travelVector);
        }
    }
    @Override
    public boolean isVisuallySwimming() {
        return this.isSwimming();
    }
    @Override
    public boolean isPushedByFluid() {
        return !this.isSwimming();
    }
    @Override
    public float getStepHeight() {
        return Math.max(1.25F, super.getStepHeight());
    }
    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }
    @Override
    public int getMaxAirSupply() {
        return DRY_OUT_TICKS - 20;
    }
    protected void handleAirSupply(int airSupply) {
        if (this.isAlive() && !this.isInWaterRainOrBubble()) {
            this.setAirSupply(airSupply - 1);
            if (this.getAirSupply() == -20) {
                this.setAirSupply(0);
                this.hurt(this.damageSources().dryOut(), 2.0F);
            }
        } else {
            this.setAirSupply(this.getMaxAirSupply());
        }
    }
    @Override
    public void aiStep() {
        int airSupply = this.getAirSupply();
        super.aiStep();
        this.handleAirSupply(airSupply);
    }
    @Override
    protected int increaseAirSupply(int airSupply) {
        return airSupply;
    }
    @Override
    public boolean checkSpawnObstruction(LevelReader levelReader) {
        return levelReader.isUnobstructed(this);
    }
    public void setSearchingForLand(boolean searchingForLand) {
        this.searchingForLand = searchingForLand;
    }
    protected boolean closeToNextPos() {
        Path path = this.getNavigation().getPath();
        if (path != null) {
            BlockPos target = path.getTarget();
            if (target != null) {
                double distance = this.distanceToSqr(target.getX(), target.getY(), target.getZ());
                return distance < 4.0D;
            }
        }
        return false;
    }
    protected boolean wantsToLeaveWater() {
        LivingEntity target = this.getTarget();
        if (target != null && target.isAlive()) {
            return !target.isInWater() && !this.needsWater();
        }
        if (this.needsWater()) {
            return false;
        }
        LivingEntity owner = this.getTrueOwner();
        return owner != null && this.isFollowing() && !owner.isInWater();
    }
    public static class LeapkelpMoveControl extends MoveControl {
        private final Leapkelp leapkelp;
        private float swimPitch;
        public LeapkelpMoveControl(Leapkelp leapkelp) {
            super(leapkelp);
            this.leapkelp = leapkelp;
        }
        @Override
        public void tick() {
            if (this.leapkelp.isLeaping()) {
                this.leapkelp.setSpeed(0.0F);
                this.leapkelp.setXxa(0.0F);
                this.leapkelp.setZza(0.0F);
                this.leapkelp.setYya(0.0F);
                return;
            }
            LivingEntity target = this.leapkelp.getTarget();
            LivingEntity owner = this.leapkelp.getTrueOwner();
            if (this.leapkelp.isInWater()) {
                if (target != null && target.getY() > this.leapkelp.getY() || this.leapkelp.searchingForLand || owner != null && owner.getY() > this.leapkelp.getY() && this.leapkelp.isFollowing()) {
                    this.leapkelp.setDeltaMovement(this.leapkelp.getDeltaMovement().add(0.0D, 0.002D, 0.0D));
                }
                if (this.operation == MoveControl.Operation.MOVE_TO && !this.leapkelp.getNavigation().isDone()) {
                    double dx = this.wantedX - this.leapkelp.getX();
                    double dy = this.wantedY - this.leapkelp.getY();
                    double dz = this.wantedZ - this.leapkelp.getZ();
                    if (dx * dx + dy * dy + dz * dz < SWIM_MIN_DISTANCE_SQR) {
                        this.leapkelp.setZza(0.0F);
                        this.leapkelp.setYya(0.0F);
                        return;
                    }
                    float yRot = (float) (Mth.atan2(dz, dx) * (180F / (float) Math.PI)) - 90.0F;
                    this.leapkelp.setYRot(this.rotlerp(this.leapkelp.getYRot(), yRot, SWIM_MAX_TURN_Y));
                    this.leapkelp.yBodyRot = this.leapkelp.getYRot();
                    this.leapkelp.yHeadRot = this.leapkelp.getYRot();
                    float speed = (float) (this.speedModifier * SWIM_SPEED_MULTIPLIER * this.leapkelp.getAttributeValue(Attributes.MOVEMENT_SPEED));
                    this.leapkelp.setSpeed(speed);
                    Vec3 movement = this.leapkelp.getDeltaMovement();
                    if (movement.y < (double) SWIM_CLIMB_SPEED
                            && dy > (double) this.leapkelp.getStepHeight()
                            && dx * dx + dz * dz < (double) Math.max(1.0F, this.leapkelp.getBbWidth())
                            && this.leapkelp.level().getFluidState(BlockPos.containing(this.wantedX, this.wantedY, this.wantedZ)).isEmpty()) {
                        this.leapkelp.setDeltaMovement(movement.x, SWIM_CLIMB_SPEED, movement.z);
                    }
                    double horizontal = Math.sqrt(dx * dx + dz * dz);
                    if (Math.abs(dy) > 1.0E-5D || horizontal > 1.0E-5D) {
                        float pitch = Mth.clamp(Mth.wrapDegrees(-(float) (Mth.atan2(dy, horizontal) * (180F / (float) Math.PI))), -SWIM_MAX_TURN_X, SWIM_MAX_TURN_X);
                        this.swimPitch += Mth.clamp(Mth.wrapDegrees(pitch - this.swimPitch), -SWIM_PITCH_STEP, SWIM_PITCH_STEP);
                    }
                    float pitchRad = this.swimPitch * ((float) Math.PI / 180.0F);
                    this.leapkelp.setZza(Mth.cos(pitchRad) * speed);
                    this.leapkelp.setYya(-Mth.sin(pitchRad) * speed);
                } else {
                    this.leapkelp.setSpeed(0.0F);
                    this.leapkelp.setXxa(0.0F);
                    this.leapkelp.setZza(0.0F);
                    this.leapkelp.setYya(0.0F);
                }
            } else {
                if (!this.leapkelp.onGround()) {
                    this.leapkelp.setDeltaMovement(this.leapkelp.getDeltaMovement().add(0.0D, -0.008D, 0.0D));
                }
                super.tick();
            }
        }
        @Override
        protected float rotlerp(float source, float target, float maxChange) {
            double dx = this.wantedX - this.leapkelp.getX();
            double dz = this.wantedZ - this.leapkelp.getZ();
            if (dx * dx + dz * dz < 0.5D) {
                return source;
            }
            return super.rotlerp(source, target, maxChange);
        }
    }
    public static class LeapkelpGoToBeachGoal extends MoveToBlockGoal {
        private static final int BEACH_TIMEOUT_TICKS = 120;
        private static final int BEACH_RETRY_TICKS = 20;
        private final Leapkelp leapkelp;
        private int timeoutTick;
        public LeapkelpGoToBeachGoal(Leapkelp leapkelp, double speedModifier) {
            super(leapkelp, speedModifier, 8, 2);
            this.leapkelp = leapkelp;
        }
        @Override
        public boolean canUse() {
            if (!this.leapkelp.isInWater() || this.leapkelp.isCommanded() || this.leapkelp.isStaying() || !this.leapkelp.wantsToLeaveWater()) {
                return false;
            }
            return super.canUse();
        }
        @Override
        public boolean canContinueToUse() {
            return this.timeoutTick > 0 && this.leapkelp.isInWater() && this.leapkelp.wantsToLeaveWater() && super.canContinueToUse();
        }
        @Override
        public void start() {
            this.timeoutTick = BEACH_TIMEOUT_TICKS;
            super.start();
        }
        @Override
        public void tick() {
            --this.timeoutTick;
            super.tick();
        }
        @Override
        protected int nextStartTick(PathfinderMob mob) {
            return BEACH_RETRY_TICKS + mob.getRandom().nextInt(BEACH_RETRY_TICKS);
        }
        @Override
        protected boolean isValidTarget(LevelReader levelReader, BlockPos pos) {
            BlockPos above = pos.above();
            return levelReader.isEmptyBlock(above) && levelReader.isEmptyBlock(above.above())
                    && levelReader.getBlockState(pos).entityCanStandOn(levelReader, pos, this.leapkelp);
        }
    }
    public static class LeapkelpSmashGoal extends Goal {
        private static final float ATTACK_RANGE = 2.5F;
        private static final float SMASH_INFLATE = 1.0F;
        private static final int SMASH_ANIM_TICK = 1;
        private static final int SMASH_HIT_TICK = 13;
        private final Leapkelp leapkelp;
        public LeapkelpSmashGoal(Leapkelp leapkelp) {
            this.leapkelp = leapkelp;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
        }
        @Override
        public boolean canUse() {
            if (this.leapkelp.isInWater() && !this.leapkelp.onGround()) {
                return false;
            }
            LivingEntity target = this.leapkelp.getTarget();
            if (target == null || !target.isAlive() || this.leapkelp.isCharging() || this.leapkelp.isLeaping() || this.leapkelp.isMeleeAttacking()) {
                return false;
            }
            return target.distanceTo(this.leapkelp) <= ATTACK_RANGE && this.leapkelp.hasLineOfSight(target);
        }
        @Override
        public boolean canContinueToUse() {
            return this.leapkelp.isMeleeAttacking();
        }
        @Override
        public void start() {
            this.leapkelp.setMeleeAttacking(true);
            this.leapkelp.setAggressive(true);
            this.leapkelp.level().broadcastEntityEvent(this.leapkelp, (byte) 6);
        }
        @Override
        public void stop() {
            this.leapkelp.setMeleeAttacking(false);
            this.leapkelp.setAggressive(false);
            this.leapkelp.level().broadcastEntityEvent(this.leapkelp, (byte) 7);
        }
        @Override
        public void tick() {
            LivingEntity target = this.leapkelp.getTarget();
            if (target != null) {
                MobUtil.instaLook(this.leapkelp, target);
            }
            this.leapkelp.getNavigation().stop();
            if (this.leapkelp.attackTick == SMASH_ANIM_TICK) {
                this.leapkelp.setAnimationState(SMASH);
                this.leapkelp.playSound(ModSounds.LEAPLEAF_SMASH.get(), this.leapkelp.getSoundVolume(), this.leapkelp.getVoicePitch());
            }
            if (this.leapkelp.attackTick != SMASH_HIT_TICK) {
                return;
            }
            Vec3 look = this.leapkelp.getHorizontalLookAngle();
            double x = this.leapkelp.getX() + look.x * 2.0D;
            double z = this.leapkelp.getZ() + look.z * 2.0D;
            Vec3 front = MobUtil.getFrontPos(this.leapkelp, 1.0D);
            AABB aabb = new AABB(front, front).inflate(SMASH_INFLATE);
            for (LivingEntity living : this.leapkelp.level().getEntitiesOfClass(LivingEntity.class, aabb)) {
                if (living == this.leapkelp || living.isAlliedTo(this.leapkelp) || this.leapkelp.isAlliedTo(living)) {
                    continue;
                }
                this.leapkelp.doHurtTarget(living);
            }
            if (!(this.leapkelp.level() instanceof ServerLevel serverLevel)) {
                return;
            }
            BlockPos pos = BlockPos.containing(x, this.leapkelp.getY() - 1.0D, z);
            BlockState state = serverLevel.getBlockState(pos);
            int color = state.getMapColor(serverLevel, pos).col;
            ColorUtil colorUtil = color == 0 ? ColorUtil.WHITE : new ColorUtil(color);
            serverLevel.sendParticles(new SmashParticleOption(colorUtil, 5.0F, 10), x, BlockFinder.moveDownToGround(this.leapkelp), z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }
    public static class LeapkelpWaterSmashGoal extends Goal {
        private static final float ATTACK_RANGE = 3.75F;
        private static final float HIT_INFLATE = 3.0F;
        private static final float HIT_PARTICLE_SIZE = 3.0F;
        private final Leapkelp leapkelp;
        private boolean right = true;
        private int startTick;
        public LeapkelpWaterSmashGoal(Leapkelp leapkelp) {
            this.leapkelp = leapkelp;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
        }
        @Override
        public boolean canUse() {
            if (this.leapkelp.isCommanded() || this.leapkelp.isStaying()) {
                return false;
            }
            if (!this.leapkelp.isInWater() || this.leapkelp.onGround()) {
                return false;
            }
            if (this.leapkelp.isCharging() || this.leapkelp.isLeaping() || this.leapkelp.isMeleeAttacking()) {
                return false;
            }
            LivingEntity target = this.leapkelp.getTarget();
            if (target == null || !target.isAlive() || !target.isInWater()) {
                return false;
            }
            return this.leapkelp.distanceTo(target) <= ATTACK_RANGE && this.leapkelp.hasLineOfSight(target);
        }
        @Override
        public boolean canContinueToUse() {
            return this.leapkelp.isMeleeAttacking() && this.elapsedTicks() <= SMASH_END_TICK;
        }
        private int elapsedTicks() {
            return this.leapkelp.tickCount - this.startTick;
        }
        @Override
        public void start() {
            this.startTick = this.leapkelp.tickCount;
            this.right = this.leapkelp.getRandom().nextBoolean();
            this.leapkelp.setMeleeAttacking(true);
            this.leapkelp.setAggressive(true);
            this.leapkelp.level().broadcastEntityEvent(this.leapkelp, (byte) 6);
            this.leapkelp.setAnimationState(this.right ? RIGHT_SMASH : LEFT_SMASH);
            this.leapkelp.getNavigation().stop();
        }
        @Override
        public void stop() {
            this.leapkelp.setMeleeAttacking(false);
            this.leapkelp.setAggressive(false);
            this.leapkelp.level().broadcastEntityEvent(this.leapkelp, (byte) 7);
        }
        @Override
        public void tick() {
            LivingEntity target = this.leapkelp.getTarget();
            if (target != null) {
                this.leapkelp.getLookControl().setLookAt(target, 30.0F, 30.0F);
            }
            this.leapkelp.getNavigation().stop();
            if (this.elapsedTicks() == SMASH_HIT_TICK) {
                this.hit();
            }
            this.leapkelp.attackTick = 0;
        }
        private void hit() {
            AABB aabb = this.leapkelp.getBoundingBox().inflate(HIT_INFLATE);
            for (LivingEntity living : this.leapkelp.level().getEntitiesOfClass(LivingEntity.class, aabb)) {
                if (living == this.leapkelp || living.isAlliedTo(this.leapkelp) || this.leapkelp.isAlliedTo(living)) {
                    continue;
                }
                this.leapkelp.doHurtTarget(living);
                this.launch(living);
            }
            this.leapkelp.playSound(ModSounds.HAMMER_IMPACT.get(), SMASH_HIT_VOLUME, 1.0F + this.leapkelp.getRandom().nextFloat() * 0.1F);
            CameraShake.cameraShake(this.leapkelp.level(), this.leapkelp.position(), 10.0F, 0.15F, 0, 20);
            if (!(this.leapkelp.level() instanceof ServerLevel serverLevel)) {
                return;
            }
            Vec3 look = this.leapkelp.getHorizontalLookAngle();
            double x = this.leapkelp.getX() + look.x * 2.0D;
            double z = this.leapkelp.getZ() + look.z * 2.0D;
            serverLevel.sendParticles(new SmashParticleOption(ColorUtil.WHITE, HIT_PARTICLE_SIZE, 8), x, this.leapkelp.getY(), z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            serverLevel.sendParticles(ParticleTypes.SPLASH, x, this.leapkelp.getY() + 0.3D, z, 20, 1.5D, 0.3D, 1.5D, 0.15D);
        }
        private void launch(LivingEntity living) {
            double dx = living.getX() - this.leapkelp.getX();
            double dz = living.getZ() - this.leapkelp.getZ();
            double distance = Math.max(dx * dx + dz * dz, 0.001D);
            living.push(dx / distance * SMASH_LAUNCH_POWER, SMASH_LAUNCH_LIFT, dz / distance * SMASH_LAUNCH_POWER);
        }
        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }
    public static class LeapkelpSurfaceLeapGoal extends Goal {
        private static final int CHARGE_TICKS = 24;
        private static final int COOLDOWN_TICKS = 40;
        private static final float LAUNCH_RANGE = 6.5F;
        private static final float APPROACH_SPEED = 1.25F;
        private static final double LEAP_HORIZONTAL_SPEED = 0.6D;
        private static final double LEAP_LIFT = 0.75D;
        private static final int MIN_AIR_TICKS = 10;
        private static final float LAND_INFLATE = 3.0F;
        private static final float LAND_REST_CHANCE = 0.25F;
        private static final float LAND_PARTICLE_SIZE = 5.0F;
        private static final float LAND_SHAKE = 10.0F;
        private static final float LAND_SHAKE_Y = 0.15F;
        private static final int LAND_SHAKE_TICKS = 20;
        private final Leapkelp leapkelp;
        private LivingEntity target;
        private Vec3 leapDirection = Vec3.ZERO;
        public LeapkelpSurfaceLeapGoal(Leapkelp leapkelp) {
            this.leapkelp = leapkelp;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
        }
        @Override
        public boolean canUse() {
            this.target = this.leapkelp.getTarget();
            return this.leapkelp.getSurfaceLeapCooldown() <= 0
                    && this.target != null
                    && !this.leapkelp.isLeaping()
                    && !this.leapkelp.isCharging()
                    && this.leapkelp.isInWater()
                    && !this.leapkelp.isSwimmingPose()
                    && !this.leapkelp.onGround()
                    && this.leapkelp.canAttack(this.target);
        }
        @Override
        public boolean canContinueToUse() {
            this.target = this.leapkelp.getTarget();
            return this.target != null && (this.leapkelp.isCharging() || this.leapkelp.isLeaping());
        }
        @Override
        public void start() {
            if (this.target != null) {
                MobUtil.instaLook(this.leapkelp, this.target);
            }
            this.leapkelp.setMeleeAttacking(false);
            this.leapkelp.setCharging(!this.leapkelp.isCharging());
        }
        @Override
        public void stop() {
            this.leapkelp.setLeaping(false);
            this.leapkelp.leapTick = 0;
            this.leapDirection = Vec3.ZERO;
            this.leapkelp.setSurfaceLeapCooldown(COOLDOWN_TICKS);
        }
        @Override
        public void tick() {
            this.target = this.leapkelp.getTarget();
            this.leapkelp.getNavigation().stop();
            if (this.target != null) {
                this.leapkelp.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
                if (this.leapkelp.chargeTick == 1) {
                    this.leapkelp.setAnimationState(CHARGE);
                    this.leapkelp.playSound(ModSounds.LEAPLEAF_CHARGE.get(), this.leapkelp.getSoundVolume(), this.leapkelp.getVoicePitch());
                }
                if (this.leapkelp.chargeTick < CHARGE_TICKS) {
                    this.leapkelp.getNavigation().stop();
                } else if (this.leapkelp.distanceTo(this.target) <= LAUNCH_RANGE) {
                    this.leapkelp.setCharging(false);
                    this.leapkelp.chargeTick = 0;
                    this.leapkelp.setAnimationState(LEAP);
                    this.leapkelp.playSound(ModSounds.LEAPLEAF_LEAP.get(), this.leapkelp.getSoundVolume(), this.leapkelp.getVoicePitch());
                    this.leapkelp.setLeaping(true);
                    double dx = this.target.getX() - this.leapkelp.getX();
                    double dy = this.target.getY() - this.leapkelp.getY();
                    double dz = this.target.getZ() - this.leapkelp.getZ();
                    double horizontal = Math.sqrt(dx * dx + dz * dz);
                    this.leapDirection = horizontal > 1.0E-4D ? new Vec3(dx / horizontal, dy, dz / horizontal) : this.leapkelp.getLookAngle();
                } else {
                    this.leapkelp.getNavigation().moveTo(this.target, APPROACH_SPEED);
                }
                if (this.leapkelp.leapTick == 1) {
                    this.leapkelp.setDeltaMovement(this.leapDirection.x * LEAP_HORIZONTAL_SPEED,
                            LEAP_LIFT + Mth.clamp(this.leapDirection.y * 0.05D, 0.0D, 0.5D),
                            this.leapDirection.z * LEAP_HORIZONTAL_SPEED);
                }
            }
            if (this.leapkelp.isLeaping() && this.leapkelp.leapTick > MIN_AIR_TICKS
                    && (this.leapkelp.onGround() || this.leapkelp.isInWater())) {
                this.land();
            }
        }
        private void land() {
            Vec3 front = MobUtil.getFrontPos(this.leapkelp, 1.0D);
            AABB aabb = new AABB(front, front).inflate(LAND_INFLATE);
            boolean rest = this.leapkelp.getRandom().nextFloat() <= LAND_REST_CHANCE;
            for (LivingEntity living : this.leapkelp.level().getEntitiesOfClass(LivingEntity.class, aabb)) {
                if (living == this.leapkelp || living.isAlliedTo(this.leapkelp) || this.leapkelp.isAlliedTo(living)
                        || !this.leapkelp.doHurtTarget(living) || !rest && living.isAlive()) {
                    continue;
                }
                this.leapkelp.restTick = REST_TIME;
            }
            this.leapkelp.setLeaping(false);
            this.leapkelp.playSound(ModSounds.LEAPLEAF_SMASH.get(), this.leapkelp.getSoundVolume(), this.leapkelp.getVoicePitch());
            CameraShake.cameraShake(this.leapkelp.level(), this.leapkelp.position(), LAND_SHAKE, LAND_SHAKE_Y, 0, LAND_SHAKE_TICKS);
            if (!(this.leapkelp.level() instanceof ServerLevel serverLevel)) {
                return;
            }
            Vec3 look = this.leapkelp.getHorizontalLookAngle();
            double x = this.leapkelp.getX() + look.x * 2.0D;
            double z = this.leapkelp.getZ() + look.z * 2.0D;
            serverLevel.sendParticles(new SmashParticleOption(ColorUtil.WHITE, LAND_PARTICLE_SIZE, 10), x, this.leapkelp.getY(), z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            serverLevel.sendParticles(ParticleTypes.SPLASH, x, this.leapkelp.getY() + 0.3D, z, 20, 1.5D, 0.3D, 1.5D, 0.15D);
        }
        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }
    public static class LeapkelpGoToWaterGoal extends Goal {
        private final Leapkelp leapkelp;
        private final double speedModifier;
        private final Level level;
        private double wantedX;
        private double wantedY;
        private double wantedZ;
        public LeapkelpGoToWaterGoal(Leapkelp leapkelp, double speedModifier) {
            this.leapkelp = leapkelp;
            this.speedModifier = speedModifier;
            this.level = leapkelp.level();
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }
        @Override
        public boolean canUse() {
            if (this.leapkelp.isCommanded() || this.leapkelp.isInWater() || !this.leapkelp.needsWater()) {
                return false;
            }
            Vec3 waterPos = this.getWaterPos();
            if (waterPos == null) {
                return false;
            }
            this.wantedX = waterPos.x;
            this.wantedY = waterPos.y;
            this.wantedZ = waterPos.z;
            return true;
        }
        @Override
        public boolean canContinueToUse() {
            return !this.leapkelp.getNavigation().isDone();
        }
        @Override
        public void start() {
            this.leapkelp.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
        }
        @Nullable
        private Vec3 getWaterPos() {
            RandomSource random = this.leapkelp.getRandom();
            BlockPos origin = this.leapkelp.blockPosition();
            for (int i = 0; i < 10; ++i) {
                BlockPos pos = origin.offset(random.nextInt(20) - 10, 2 - random.nextInt(8), random.nextInt(20) - 10);
                if (this.level.getBlockState(pos).is(Blocks.WATER)) {
                    return Vec3.atBottomCenterOf(pos);
                }
            }
            return null;
        }
    }
    public static class LeapkelpSwimAttackGoal extends Goal {
        private static final double CHASE_DISTANCE_SQR = 4.0D;
        private static final int PATH_RECALC_TICKS = 10;
        private final Leapkelp leapkelp;
        private final double speedModifier;
        private LivingEntity target;
        private int pathTick;
        public LeapkelpSwimAttackGoal(Leapkelp leapkelp, double speedModifier) {
            this.leapkelp = leapkelp;
            this.speedModifier = speedModifier;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }
        @Override
        public boolean canUse() {
            if (this.leapkelp.isCommanded() || this.leapkelp.isStaying()) {
                return false;
            }
            if (!this.canSwimToTarget()) {
                return false;
            }
            return this.leapkelp.distanceToSqr(this.target) > CHASE_DISTANCE_SQR;
        }
        @Override
        public boolean canContinueToUse() {
            return this.canSwimToTarget();
        }
        private boolean canSwimToTarget() {
            if (!this.leapkelp.isInWater() || this.leapkelp.onGround() || !this.leapkelp.wantsToSwim()) {
                return false;
            }
            if (this.leapkelp.isCharging() || this.leapkelp.isLeaping() || this.leapkelp.isMeleeAttacking()) {
                return false;
            }
            LivingEntity living = this.leapkelp.getTarget();
            if (living == null || !living.isAlive() || !living.isInWater()) {
                return false;
            }
            this.target = living;
            return true;
        }
        @Override
        public void start() {
            this.pathTick = 0;
        }
        @Override
        public void stop() {
            this.target = null;
            this.leapkelp.getNavigation().stop();
        }
        @Override
        public void tick() {
            this.leapkelp.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
            if (this.leapkelp.distanceToSqr(this.target) <= CHASE_DISTANCE_SQR) {
                this.leapkelp.getNavigation().stop();
                return;
            }
            this.pathTick = Math.max(this.pathTick - 1, 0);
            if (this.pathTick <= 0) {
                this.pathTick = PATH_RECALC_TICKS;
                this.leapkelp.getNavigation().moveTo(this.target, this.speedModifier);
            }
        }
    }
    public static class LeapkelpSwimUpGoal extends Goal {
        private final Leapkelp leapkelp;
        private final double speedModifier;
        private final int seaLevel;
        private boolean stuck;
        private int retargetTick;
        public LeapkelpSwimUpGoal(Leapkelp leapkelp, double speedModifier, int seaLevel) {
            this.leapkelp = leapkelp;
            this.speedModifier = speedModifier;
            this.seaLevel = seaLevel;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }
        @Override
        public boolean canUse() {
            if (this.leapkelp.isCommanded() || this.leapkelp.isStaying() || this.leapkelp.isGuardingArea()) {
                return false;
            }
            if (this.leapkelp.getTrueOwner() != null && this.leapkelp.isFollowing()) {
                return false;
            }
            return (this.leapkelp.level().isRaining() || this.leapkelp.isInWater()) && this.leapkelp.getY() < (double) (this.seaLevel - 2);
        }
        @Override
        public boolean canContinueToUse() {
            return this.canUse() && !this.stuck;
        }
        @Override
        public void tick() {
            this.retargetTick = Math.max(this.retargetTick - 1, 0);
            if (this.retargetTick <= 0 && this.leapkelp.getY() < (double) (this.seaLevel - 1) && this.leapkelp.getNavigation().isDone()) {
                Vec3 pos = DefaultRandomPos.getPosTowards((PathfinderMob) this.leapkelp, 4, 8, new Vec3(this.leapkelp.getX(), this.seaLevel - 1, this.leapkelp.getZ()), 1.5707963705062866D);
                if (pos == null) {
                    this.stuck = true;
                    return;
                }
                this.retargetTick = SWIM_UP_RETARGET_COOLDOWN;
                this.leapkelp.getNavigation().moveTo(pos.x, pos.y, pos.z, this.speedModifier);
            }
        }
        @Override
        public void start() {
            this.leapkelp.setSearchingForLand(true);
            this.stuck = false;
            this.retargetTick = 0;
        }
        @Override
        public void stop() {
            this.leapkelp.setSearchingForLand(false);
        }
    }
    public static class LeapkelpFollowOwnerGoal extends Goal {
        private final Leapkelp leapkelp;
        private final Level level;
        private final double followSpeed;
        private final float startDistance;
        private final float stopDistance;
        private LivingEntity owner;
        private Path path;
        private double pathedTargetX;
        private double pathedTargetY;
        private double pathedTargetZ;
        private int ticksUntilNextPathRecalculation;
        public LeapkelpFollowOwnerGoal(Leapkelp leapkelp, double speed, float startDistance, float stopDistance) {
            this.leapkelp = leapkelp;
            this.level = leapkelp.level();
            this.followSpeed = speed;
            this.startDistance = startDistance;
            this.stopDistance = stopDistance;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }
        @Override
        public boolean canUse() {
            LivingEntity livingentity = this.leapkelp.getTrueOwner();
            if (livingentity == null) {
                return false;
            } else if (livingentity.isSpectator()) {
                return false;
            } else if (this.leapkelp.distanceToSqr(livingentity) < (double) Mth.square(this.startDistance)) {
                return false;
            } else if (this.leapkelp.distanceTo(livingentity) >= 1024.0F) {
                return false;
            } else if (!this.leapkelp.isFollowing() || this.leapkelp.isCommanded()) {
                return false;
            } else if (this.leapkelp.getTarget() != null) {
                return false;
            } else {
                this.owner = livingentity;
                if (!livingentity.isAlive()) {
                    return false;
                }
                this.path = this.leapkelp.getNavigation().createPath(livingentity, 0);
                return true;
            }
        }
        @Override
        public boolean canContinueToUse() {
            if (this.owner == null || !this.owner.isAlive()) {
                return false;
            } else if (!this.leapkelp.isFollowing() || this.leapkelp.isCommanded()) {
                return false;
            } else if (this.leapkelp.getTarget() != null) {
                return false;
            } else if (this.leapkelp.getNavigation().isDone()) {
                return false;
            } else {
                return !(this.leapkelp.distanceToSqr(this.owner) <= (double) Mth.square(this.stopDistance));
            }
        }
        @Override
        public void start() {
            this.leapkelp.getNavigation().moveTo(this.path, this.followSpeed);
            this.ticksUntilNextPathRecalculation = 0;
        }
        @Override
        public void stop() {
            this.owner = null;
            this.leapkelp.getNavigation().stop();
        }
        @Override
        public void tick() {
            this.leapkelp.getLookControl().setLookAt(this.owner, 30.0F, 30.0F);
            double distance = this.leapkelp.distanceToSqr(this.owner.getX(), this.owner.getY(), this.owner.getZ());
            this.ticksUntilNextPathRecalculation = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);
            if (this.ticksUntilNextPathRecalculation <= 0 && (this.pathedTargetX == 0.0D && this.pathedTargetY == 0.0D && this.pathedTargetZ == 0.0D || this.owner.distanceToSqr(this.pathedTargetX, this.pathedTargetY, this.pathedTargetZ) >= 1.0D || this.leapkelp.getRandom().nextFloat() < 0.05F)) {
                this.pathedTargetX = this.owner.getX();
                this.pathedTargetY = this.owner.getY();
                this.pathedTargetZ = this.owner.getZ();
                this.ticksUntilNextPathRecalculation = 4 + this.leapkelp.getRandom().nextInt(7);
                double range = this.owner instanceof Mob ? 32.0D : 16.0D;
                boolean teleport = distance > Mth.square(range);
                if (this.owner instanceof Mob) {
                    teleport |= !this.leapkelp.hasLineOfSight(this.owner) && distance >= Mth.square(8.0D);
                } else {
                    teleport &= MobsConfig.ServantTeleport.get();
                }
                if (teleport) {
                    this.tryToTeleportNearEntity();
                }
                if (distance > 1024.0D) {
                    this.ticksUntilNextPathRecalculation += 10;
                } else if (distance > 256.0D) {
                    this.ticksUntilNextPathRecalculation += 5;
                }
                if (!this.leapkelp.getNavigation().moveTo(this.owner, this.followSpeed)) {
                    this.ticksUntilNextPathRecalculation += 15;
                }
            }
        }
        private void tryToTeleportNearEntity() {
            BlockPos blockpos = this.owner.blockPosition();
            for (int i = 0; i < 10; ++i) {
                int j = this.getRandomNumber(-3, 3);
                int k = this.getRandomNumber(-1, 1);
                int l = this.getRandomNumber(-3, 3);
                if (this.tryToTeleportToLocation(blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l)) {
                    return;
                }
            }
        }
        private boolean tryToTeleportToLocation(int x, int y, int z) {
            if (Math.abs(x - this.owner.getX()) < 2.0D && Math.abs(z - this.owner.getZ()) < 2.0D) {
                return false;
            } else if (!this.isTeleportFriendlyBlock(new BlockPos(x, y, z))) {
                return false;
            } else {
                this.leapkelp.moveTo(x + 0.5D, y, z + 0.5D, this.leapkelp.getYRot(), this.leapkelp.getXRot());
                this.leapkelp.getNavigation().stop();
                return true;
            }
        }
        private boolean isTeleportFriendlyBlock(BlockPos pos) {
            BlockPathTypes pathType = WalkNodeEvaluator.getBlockPathTypeStatic(this.level, pos.mutable());
            if (pathType != BlockPathTypes.WALKABLE) {
                return false;
            } else if (this.level.getBlockState(pos.below()).is(BlockTags.LEAVES)) {
                return false;
            } else {
                BlockPos offset = pos.subtract(this.leapkelp.blockPosition());
                return this.level.noCollision(this.leapkelp, this.leapkelp.getBoundingBox().move(offset));
            }
        }
        private int getRandomNumber(int min, int max) {
            return this.leapkelp.getRandom().nextInt(max - min + 1) + min;
        }
    }
}
