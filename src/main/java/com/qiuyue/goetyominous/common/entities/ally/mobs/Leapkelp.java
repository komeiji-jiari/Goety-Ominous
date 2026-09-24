package com.qiuyue.goetyominous.common.entities.ally.mobs;
import com.Polarice3.Goety.client.particles.SmashParticleOption;
import com.Polarice3.Goety.common.entities.ai.path.GroundPathNavigatorFat;
import com.Polarice3.Goety.common.entities.ai.path.ModWaterPathNavigation;
import com.Polarice3.Goety.common.entities.ally.Leapleaf;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.config.AttributesConfig;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
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
import org.jetbrains.annotations.Nullable;
import java.util.EnumSet;
public class Leapkelp extends Leapleaf {
    public static final int MAX_MOISTNESS = 40000;
    public static final int SEEK_WATER_MOISTNESS = 4000;
    private static final int DRY_OUT_TICKS = 3 * 60 * 20;
    private static final int MAX_REST_TICKS = 20;
    private static final float SWIM_SPEED_MULTIPLIER = 2.0F;
    private static final int SWIM_MAX_TURN_Y = 20;
    private static final int SWIM_MAX_TURN_X = 20;
    private static final float SWIM_FULL_SPEED_TURN = 10.0F;
    private static final float SWIM_STOP_TURN = 60.0F;
    private static final float SWIM_MIN_DISTANCE_SQR = 2.5E-7F;
    private static final float SWIM_POSE_STEP = 0.15F;
    private static final int SWIM_UP_RETARGET_COOLDOWN = 10;
    private static final int MOISTNESS_DRAIN_DAY = 10;
    private static final int MOISTNESS_DRAIN_NIGHT = 5;
    private static final int LEAP_COOLDOWN_TICKS = 10;
    private static final int LEAP_TIMEOUT_TICKS = 60;
    private static final int LEAP_RETRY_BASE_TICKS = 20;
    private static final int LEAP_RETRY_MAX_TICKS = 200;
    private static final int LEAP_MIN_DISTANCE = 2;
    private static final int LEAP_MAX_DISTANCE = 5;
    private static final int LEAP_MAX_RISE = 3;
    private static final int LEAP_MAX_DROP = 8;
    private static final double LEAP_MIN_SPEED = 0.1D;
    private static final double LEAP_MAX_SPEED = 1.2D;
    private static final double LEAP_HORIZONTAL_DRAG = 0.91D;
    private static final double LEAP_VERTICAL_DRAG = 0.98D;
    private static final double LEAP_GRAVITY = 0.08D;
    private static final int LEAP_MAX_AIR_TICKS = 60;
    private static final int LEAP_ANIM_TICKS = 30;
    private static final int LEAP_ANIM_SPEED_MIN = 50;
    private static final int LEAP_ANIM_SPEED_MAX = 300;
    private static final int LEAP_TAIL_TICKS = 3;
    private static final double LEAP_LIFT_BASE = 0.45D;
    private static final double LEAP_LIFT_PER_BLOCK = 0.2D;
    private static final double LEAP_MAX_LIFT = 1.0D;
    private static final int LEAP_STUCK_TICKS = 80;
    private static final int CHARGE_STALE_TICKS = 24;
    private static final int LEAP_WATER_SETTLE_TICKS = 6;
    private static final double LEAP_WATER_SETTLE_SQR = 0.02D;
    private static final double LEAP_WATER_ENTRY_DRAG = 0.4D;
    private static final double LEAP_WATER_ENTRY_SINK = 0.2D;
    private static final EntityDataAccessor<Integer> MOISTNESS = SynchedEntityData.defineId(Leapkelp.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> LEAP_ANIM_SPEED = SynchedEntityData.defineId(Leapkelp.class, EntityDataSerializers.INT);
    protected final ModWaterPathNavigation waterNavigation;
    protected final GroundPathNavigation groundNavigation;
    private boolean searchingForLand;
    private int leapRetryTick;
    private int leapRetryDelay = LEAP_RETRY_BASE_TICKS;
    private boolean leapAirborne;
    private boolean leapWaterLanded;
    private int leapWaterSettleTick;
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
        this.goalSelector.getAvailableGoals().removeIf(wrapped -> wrapped.getGoal() instanceof FloatGoal);
        this.goalSelector.addGoal(2, new LeapkelpLeapAttackGoal(this));
        this.goalSelector.addGoal(2, new LeapkelpLeapIntoWaterGoal(this));
        this.goalSelector.addGoal(3, new LeapkelpLeapAshoreGoal(this));
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
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(MOISTNESS, MAX_MOISTNESS);
        this.entityData.define(LEAP_ANIM_SPEED, 100);
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
        if (!this.level().isClientSide && this.isLeaping()) {
            this.tickLeapWaterLanding();
        }
        super.tick();
        this.updateSwimPoseAmount();
        if (this.level().isClientSide) {
            return;
        }
        if (this.restTick > MAX_REST_TICKS) {
            this.restTick = MAX_REST_TICKS;
        }
        if (this.leapRetryTick > 0) {
            --this.leapRetryTick;
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
        if (this.isInWaterRainOrBubble() || !AttributesConfig.LeapkelpMoistness.get()) {
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
        if (this.getTarget() != null && this.getTarget().isInWater()) {
            return true;
        }
        return this.getTrueOwner() != null && this.isFollowing() && (this.getTrueOwner().isInWater() || this.isInWater());
    }
    @Override
    public void updateSwimming() {
        if (!this.level().isClientSide) {
            if (this.isEffectiveAi() && this.isInWater() && this.wantsToSwim()) {
                this.navigation = this.waterNavigation;
                this.setSwimming(true);
            } else {
                this.navigation = this.groundNavigation;
                this.setSwimming(false);
            }
        }
    }
    protected void updateSwimPoseAmount() {
        this.swimPoseAmountO = this.swimPoseAmount;
        float target = this.isInWater() && !this.isLeaping() && !this.isCharging() ? 1.0F : 0.0F;
        this.swimPoseAmount = Mth.approach(this.swimPoseAmount, target, SWIM_POSE_STEP);
    }
    public float getSwimPoseAmount(float partialTicks) {
        return Mth.lerp(partialTicks, this.swimPoseAmountO, this.swimPoseAmount);
    }
    @Override
    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isInWater() && this.wantsToSwim() && !this.isLeaping()) {
            this.moveRelative(0.01F, travelVector);
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
    protected boolean canLeap() {
        return !this.isLeaping() && !this.isCharging() && this.coolTick <= 0 && this.leapRetryTick <= 0
                && !this.isCommanded() && !this.isStaying();
    }
    @Override
    public void setLeaping(boolean leaping) {
        if (leaping) {
            this.leapAirborne = false;
            this.leapWaterLanded = false;
            this.leapWaterSettleTick = 0;
        } else {
            this.setLeapAnimSpeed(1.0F);
        }
        super.setLeaping(leaping);
    }
    public float getLeapAnimSpeed() {
        return this.entityData.get(LEAP_ANIM_SPEED) / 100.0F;
    }
    protected void setLeapAnimSpeed(float speed) {
        this.entityData.set(LEAP_ANIM_SPEED, Mth.clamp(Math.round(speed * 100.0F), LEAP_ANIM_SPEED_MIN, LEAP_ANIM_SPEED_MAX));
    }
    protected double leapLaunchDrag() {
        if (!this.onGround()) {
            return LEAP_HORIZONTAL_DRAG;
        }
        BlockPos pos = this.getBlockPosBelowThatAffectsMyMovement();
        return this.level().getBlockState(pos).getFriction(this.level(), pos, this) * LEAP_HORIZONTAL_DRAG;
    }
    protected int leapAirTicks(double lift, double dy) {
        double velocity = lift;
        double height = lift;
        int apexTick = 1;
        double apexHeight = height;
        for (int tick = 1; tick <= LEAP_MAX_AIR_TICKS; ++tick) {
            velocity = (velocity - LEAP_GRAVITY) * LEAP_VERTICAL_DRAG;
            height += velocity;
            if (height > apexHeight) {
                apexHeight = height;
                apexTick = tick;
            } else if (height <= dy) {
                return tick;
            }
        }
        return apexTick;
    }
    protected void alignLeapAnimSpeed(double lift, double dy, int tailTicks) {
        this.setLeapAnimSpeed((float) LEAP_ANIM_TICKS / (float) Math.max(this.leapAirTicks(lift, dy) + tailTicks, 1));
    }
    protected double leapSpeedTo(double horizontal, double dy, double lift, int tailTicks) {
        int airTicks = this.leapAirTicks(lift, dy);
        this.setLeapAnimSpeed((float) LEAP_ANIM_TICKS / (float) Math.max(airTicks + tailTicks, 1));
        double factor = 0.0D;
        double decay = 1.0D;
        for (int tick = 0; tick <= airTicks; ++tick) {
            factor += decay;
            decay *= tick == 0 ? this.leapLaunchDrag() : LEAP_HORIZONTAL_DRAG;
        }
        return Mth.clamp(horizontal / Math.max(factor, 1.0E-4D), LEAP_MIN_SPEED, LEAP_MAX_SPEED);
    }
    protected void tickLeapWaterLanding() {
        if (this.leapWaterLanded) {
            if (this.leapWaterSettleTick > 0) {
                --this.leapWaterSettleTick;
            }
            return;
        }
        if (!this.isInWater()) {
            this.leapAirborne = true;
            return;
        }
        if (this.leapAirborne) {
            this.leapAirborne = false;
            this.leapWaterLanded = true;
            this.leapWaterSettleTick = LEAP_WATER_SETTLE_TICKS;
            this.setDeltaMovement(this.getDeltaMovement().multiply(LEAP_WATER_ENTRY_DRAG, LEAP_WATER_ENTRY_SINK, LEAP_WATER_ENTRY_DRAG));
            this.playSound(this.getSwimSplashSound(), 0.8F, this.getVoicePitch());
            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SPLASH, this.getX(), this.getY() + 0.3D, this.getZ(), 40,
                        this.getBbWidth() * 1.5D, 0.3D, this.getBbWidth() * 1.5D, 0.15D);
                serverLevel.sendParticles(ParticleTypes.BUBBLE, this.getX(), this.getY() + 0.3D, this.getZ(), 20,
                        this.getBbWidth(), 0.3D, this.getBbWidth(), 0.05D);
            }
        } else if (this.leapTick > 2 && this.getDeltaMovement().horizontalDistanceSqr() < LEAP_WATER_SETTLE_SQR) {
            this.leapWaterLanded = true;
            this.leapWaterSettleTick = LEAP_WATER_SETTLE_TICKS;
        }
    }
    protected boolean isLeapWaterLandingDone() {
        return this.leapWaterLanded && this.leapWaterSettleTick <= 0;
    }
    protected boolean wantsToLeaveWater() {
        if (this.searchingForLand) {
            return true;
        }
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
    @Nullable
    protected Vec3 getShoreSeekTarget() {
        LivingEntity target = this.getTarget();
        if (target != null && target.isAlive() && !target.isInWater()) {
            return target.position();
        }
        LivingEntity owner = this.getTrueOwner();
        if (owner != null && this.isFollowing() && !owner.isInWater()) {
            return owner.position();
        }
        return null;
    }
    @Nullable
    protected Vec3 getWaterSeekTarget() {
        LivingEntity owner = this.getTrueOwner();
        if (owner != null && this.isFollowing() && owner.isInWater()) {
            return owner.position();
        }
        return null;
    }
    protected void recordLeapResult(boolean success) {
        if (success) {
            this.leapRetryTick = 0;
            this.leapRetryDelay = LEAP_RETRY_BASE_TICKS;
        } else {
            this.leapRetryTick = this.leapRetryDelay;
            this.leapRetryDelay = Math.min(this.leapRetryDelay * 2, LEAP_RETRY_MAX_TICKS);
        }
    }
    @Nullable
    protected Vec3 findLeapTarget(@Nullable Vec3 towards, boolean water) {
        double baseAngle;
        if (towards != null) {
            baseAngle = Math.atan2(towards.z - this.getZ(), towards.x - this.getX());
        } else {
            Vec3 look = this.getLookAngle();
            baseAngle = Math.atan2(look.z, look.x);
        }
        RandomSource random = this.getRandom();
        for (int distance = LEAP_MIN_DISTANCE; distance <= LEAP_MAX_DISTANCE; ++distance) {
            for (int i = 0; i < 8; ++i) {
                double angle = i == 0 ? baseAngle : baseAngle + (random.nextDouble() - 0.5D) * Math.PI;
                int x = Mth.floor(this.getX() + Math.cos(angle) * (double) distance);
                int z = Mth.floor(this.getZ() + Math.sin(angle) * (double) distance);
                Vec3 spot = this.findColumnSpot(x, z, water);
                if (spot != null) {
                    return spot;
                }
            }
        }
        return null;
    }
    @Nullable
    private Vec3 findColumnSpot(int x, int z, boolean water) {
        int feet = Mth.floor(this.getY());
        if (water) {
            for (int y = feet + 1; y >= feet - LEAP_MAX_DROP; --y) {
                if (this.isWaterLeapSpot(x, y, z)) {
                    return new Vec3((double) x + 0.5D, (double) y, (double) z + 0.5D);
                }
            }
        } else {
            for (int y = feet; y <= feet + LEAP_MAX_RISE; ++y) {
                if (this.isGroundLeapSpot(x, y, z)) {
                    return new Vec3((double) x + 0.5D, (double) y, (double) z + 0.5D);
                }
            }
        }
        return null;
    }
    private boolean isWaterLeapSpot(int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        if (!this.level().getFluidState(pos).is(FluidTags.WATER)) {
            return false;
        }
        if (this.level().getBlockState(pos).blocksMotion() || this.level().getBlockState(pos.above()).blocksMotion()) {
            return false;
        }
        return this.level().noCollision(this, this.leapBox(pos));
    }
    private boolean isGroundLeapSpot(int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        if (!this.level().getFluidState(pos).isEmpty() || this.level().getBlockState(pos).blocksMotion()) {
            return false;
        }
        if (!this.level().getBlockState(pos.below()).blocksMotion()) {
            return false;
        }
        return this.level().noCollision(this, this.leapBox(pos));
    }
    private AABB leapBox(BlockPos pos) {
        return this.getBoundingBox().move((double) pos.getX() + 0.5D - this.getX(), (double) pos.getY() - this.getY(), (double) pos.getZ() + 0.5D - this.getZ());
    }
    public static class LeapkelpMoveControl extends MoveControl {
        private final Leapkelp leapkelp;
        public LeapkelpMoveControl(Leapkelp leapkelp) {
            super(leapkelp);
            this.leapkelp = leapkelp;
        }
        @Override
        public void tick() {
            if (this.leapkelp.isLeaping()) {
                this.leapkelp.setSpeed(0.0F);
                this.leapkelp.setZza(0.0F);
                this.leapkelp.setYya(0.0F);
                return;
            }
            LivingEntity target = this.leapkelp.getTarget();
            LivingEntity owner = this.leapkelp.getTrueOwner();
            if (this.leapkelp.wantsToSwim() && this.leapkelp.isInWater()) {
                if (target != null && target.getY() > this.leapkelp.getY() || this.leapkelp.searchingForLand || owner != null && owner.getY() > this.leapkelp.getY() && this.leapkelp.isFollowing()) {
                    this.leapkelp.setDeltaMovement(this.leapkelp.getDeltaMovement().add(0.0D, 0.002D, 0.0D));
                }
                if (this.operation != MoveControl.Operation.MOVE_TO || this.leapkelp.getNavigation().isDone()) {
                    this.leapkelp.setSpeed(0.0F);
                    this.leapkelp.setZza(0.0F);
                    this.leapkelp.setYya(0.0F);
                    return;
                }
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
                double horizontal = Math.sqrt(dx * dx + dz * dz);
                float pitch = Mth.clamp(Mth.wrapDegrees(-(float) (Mth.atan2(dy, horizontal) * (180F / (float) Math.PI))), -SWIM_MAX_TURN_X, SWIM_MAX_TURN_X);
                this.leapkelp.setXRot(this.rotlerp(this.leapkelp.getXRot(), pitch, 5.0F));
                float speed = (float) (this.speedModifier * SWIM_SPEED_MULTIPLIER * this.leapkelp.getAttributeValue(Attributes.MOVEMENT_SPEED));
                float turnFactor = Mth.clamp(1.0F - (Math.abs(Mth.wrapDegrees(this.leapkelp.getYRot() - yRot)) - SWIM_FULL_SPEED_TURN) / (SWIM_STOP_TURN - SWIM_FULL_SPEED_TURN), 0.0F, 1.0F);
                float smoothed = Mth.lerp(0.125F, this.leapkelp.getSpeed(), speed * turnFactor);
                this.leapkelp.setSpeed(smoothed);
                float pitchRad = this.leapkelp.getXRot() * ((float) Math.PI / 180.0F);
                this.leapkelp.setZza(Mth.cos(pitchRad) * smoothed);
                this.leapkelp.setYya(-Mth.sin(pitchRad) * smoothed);
            } else {
                if (!this.leapkelp.onGround()) {
                    this.leapkelp.setDeltaMovement(this.leapkelp.getDeltaMovement().add(0.0D, -0.008D, 0.0D));
                }
                super.tick();
            }
        }
    }
    public abstract static class LeapkelpLeapGoal extends Goal {
        protected final Leapkelp leapkelp;
        private Vec3 leapTarget = Vec3.ZERO;
        private Vec3 leapDirection = Vec3.ZERO;
        private double leapSpeed;
        private double leapLift;
        private boolean lifted;
        protected LeapkelpLeapGoal(Leapkelp leapkelp) {
            this.leapkelp = leapkelp;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }
        protected abstract boolean wantsToLeap();
        @Nullable
        protected abstract Vec3 findLeapTarget();
        protected int leapTailTicks() {
            return LEAP_TAIL_TICKS;
        }
        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
        @Override
        public boolean canUse() {
            if (!this.leapkelp.canLeap() || !this.wantsToLeap()) {
                return false;
            }
            Vec3 target = this.findLeapTarget();
            if (target == null) {
                return false;
            }
            this.leapTarget = target;
            return true;
        }
        @Override
        public boolean canContinueToUse() {
            return this.leapkelp.isLeaping();
        }
        @Override
        public void start() {
            double dx = this.leapTarget.x - this.leapkelp.getX();
            double dz = this.leapTarget.z - this.leapkelp.getZ();
            double dy = this.leapTarget.y - this.leapkelp.getY();
            double horizontal = Math.sqrt(dx * dx + dz * dz);
            Vec3 look = this.leapkelp.getLookAngle();
            this.leapDirection = horizontal > 1.0E-4D ? new Vec3(dx / horizontal, dy, dz / horizontal) : new Vec3(look.x, dy, look.z);
            this.leapLift = Mth.clamp(LEAP_LIFT_BASE + Math.max(dy, 0.0D) * LEAP_LIFT_PER_BLOCK, LEAP_LIFT_BASE, LEAP_MAX_LIFT);
            this.leapSpeed = this.leapkelp.leapSpeedTo(horizontal, dy, this.leapLift, this.leapTailTicks());
            this.lifted = false;
            float yRot = (float) (Mth.atan2(dz, dx) * (180.0D / Math.PI)) - 90.0F;
            this.leapkelp.setYRot(yRot);
            this.leapkelp.yBodyRot = yRot;
            this.leapkelp.yHeadRot = yRot;
            this.leapkelp.setXRot(0.0F);
            this.leapkelp.getNavigation().stop();
            this.leapkelp.setSpeed(0.0F);
            this.leapkelp.setZza(0.0F);
            this.leapkelp.setYya(0.0F);
            this.leapkelp.setLeaping(true);
            this.leapkelp.setAnimationState(LEAP);
            this.leapkelp.playSound(ModSounds.LEAPLEAF_LEAP.get(), this.leapkelp.getSoundVolume(), this.leapkelp.getVoicePitch());
            this.leapkelp.coolTick = LEAP_COOLDOWN_TICKS;
        }
        @Override
        public void tick() {
            this.leapkelp.getNavigation().stop();
            if (!this.lifted && this.leapkelp.leapTick > 0) {
                this.lifted = true;
                this.leapkelp.setDeltaMovement(this.leapDirection.x * this.leapSpeed, this.leapLift, this.leapDirection.z * this.leapSpeed);
            }
            if (this.leapkelp.leapTick > LEAP_TIMEOUT_TICKS) {
                this.leapkelp.setLeaping(false);
            } else if (this.leapkelp.leapTick > 2 && (this.leapkelp.onGround() || this.leapkelp.isLeapWaterLandingDone())) {
                this.leapkelp.setLeaping(false);
            }
        }
        @Override
        public void stop() {
            this.leapkelp.setLeaping(false);
            this.leapkelp.leapTick = 0;
            this.leapkelp.coolTick = LEAP_COOLDOWN_TICKS;
        }
    }
    public static class LeapkelpLeapAshoreGoal extends LeapkelpLeapGoal {
        public LeapkelpLeapAshoreGoal(Leapkelp leapkelp) {
            super(leapkelp);
        }
        @Override
        protected boolean wantsToLeap() {
            return this.leapkelp.isInWater() && this.leapkelp.wantsToLeaveWater();
        }
        @Override
        @Nullable
        protected Vec3 findLeapTarget() {
            return this.leapkelp.findLeapTarget(this.leapkelp.getShoreSeekTarget(), false);
        }
        @Override
        public void stop() {
            super.stop();
            this.leapkelp.recordLeapResult(!this.leapkelp.isInWater());
        }
    }
    public static class LeapkelpLeapIntoWaterGoal extends LeapkelpLeapGoal {
        public LeapkelpLeapIntoWaterGoal(Leapkelp leapkelp) {
            super(leapkelp);
        }
        @Override
        protected boolean wantsToLeap() {
            return this.leapkelp.onGround() && !this.leapkelp.isInWater() && this.leapkelp.needsWater();
        }
        @Override
        protected int leapTailTicks() {
            return LEAP_WATER_SETTLE_TICKS + LEAP_TAIL_TICKS;
        }
        @Override
        @Nullable
        protected Vec3 findLeapTarget() {
            return this.leapkelp.findLeapTarget(this.leapkelp.getWaterSeekTarget(), true);
        }
        @Override
        public void stop() {
            super.stop();
            this.leapkelp.recordLeapResult(this.leapkelp.isInWater());
        }
    }
    public static class LeapkelpLeapAttackGoal extends Goal {
        private static final float ATTACK_DISTANCE = 6.5F;
        private static final int CHARGE_TICKS = 24;
        private static final int ATTACK_COOLDOWN_TICKS = 40;
        private static final int LEAP_TIMEOUT_TICKS = 40;
        private static final double LEAP_SPEED = 0.6D;
        private static final double LEAP_BASE_LIFT = 0.75D;
        private static final double LEAP_LIFT_PER_BLOCK = 0.05D;
        private static final double LEAP_MAX_LIFT = 0.5D;
        private static final float SMASH_INFLATE = 3.0F;
        private static final float SMASH_REST_CHANCE = 0.25F;
        private final Leapkelp leapkelp;
        @Nullable
        private LivingEntity target;
        private Vec3 leapDirection = Vec3.ZERO;
        private double leapLift = LEAP_BASE_LIFT;
        private boolean released;
        public LeapkelpLeapAttackGoal(Leapkelp leapkelp) {
            this.leapkelp = leapkelp;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
        }
        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
        @Override
        public boolean canUse() {
            if (!this.leapkelp.canLeap() || !this.leapkelp.isInWater() || this.leapkelp.onGround()) {
                return false;
            }
            LivingEntity living = this.leapkelp.getTarget();
            if (living == null || !living.isAlive() || !living.isInWater()) {
                return false;
            }
            if (this.leapkelp.distanceTo(living) > ATTACK_DISTANCE || !this.leapkelp.hasLineOfSight(living)) {
                return false;
            }
            this.target = living;
            return true;
        }
        @Override
        public boolean canContinueToUse() {
            return this.leapkelp.isLeaping() || this.target != null && this.leapkelp.isCharging();
        }
        @Override
        public void start() {
            this.target = this.leapkelp.getTarget();
            this.leapkelp.setMeleeAttacking(false);
            this.leapkelp.chargeTick = 0;
            this.leapkelp.setCharging(true);
            this.released = false;
            this.leapDirection = Vec3.ZERO;
        }
        @Override
        public void stop() {
            this.leapkelp.setCharging(false);
            this.leapkelp.chargeTick = 0;
            this.leapkelp.setLeaping(false);
            this.leapDirection = Vec3.ZERO;
            this.target = null;
        }
        @Override
        public void tick() {
            this.leapkelp.getNavigation().stop();
            if (this.leapkelp.isLeaping()) {
                this.tickLeap();
                return;
            }
            this.target = this.leapkelp.getTarget();
            if (this.target == null || !this.target.isAlive()) {
                return;
            }
            this.leapkelp.lookAt(EntityAnchorArgument.Anchor.EYES, this.target.position());
            if (this.leapkelp.chargeTick == 1) {
                this.leapkelp.setAnimationState(CHARGE);
                this.leapkelp.playSound(ModSounds.LEAPLEAF_CHARGE.get(), this.leapkelp.getSoundVolume(), this.leapkelp.getVoicePitch());
            }
            if (this.leapkelp.chargeTick < CHARGE_TICKS) {
                return;
            }
            if (this.leapkelp.distanceTo(this.target) <= ATTACK_DISTANCE) {
                this.beginLeap();
            } else {
                this.leapkelp.getNavigation().moveTo(this.target, 1.25D);
            }
        }
        private void beginLeap() {
            double dx = this.target.getX() - this.leapkelp.getX();
            double dy = this.target.getY() - this.leapkelp.getY();
            double dz = this.target.getZ() - this.leapkelp.getZ();
            double horizontal = Math.sqrt(dx * dx + dz * dz);
            this.leapDirection = horizontal > 1.0E-4D ? new Vec3(dx / horizontal, dy, dz / horizontal) : this.leapkelp.getLookAngle();
            this.leapLift = LEAP_BASE_LIFT + Mth.clamp(dy * LEAP_LIFT_PER_BLOCK, 0.0D, LEAP_MAX_LIFT);
            this.leapkelp.alignLeapAnimSpeed(this.leapLift, dy, LEAP_WATER_SETTLE_TICKS + LEAP_TAIL_TICKS);
            this.released = false;
            MobUtil.instaLook(this.leapkelp, this.target);
            this.leapkelp.setCharging(false);
            this.leapkelp.chargeTick = 0;
            this.leapkelp.setAnimationState(LEAP);
            this.leapkelp.playSound(ModSounds.LEAPLEAF_LEAP.get(), this.leapkelp.getSoundVolume(), this.leapkelp.getVoicePitch());
            this.leapkelp.setLeaping(true);
        }
        private void tickLeap() {
            if (!this.released && this.leapkelp.leapTick > 0) {
                this.released = true;
                this.leapkelp.setDeltaMovement(this.leapDirection.x * LEAP_SPEED, this.leapLift, this.leapDirection.z * LEAP_SPEED);
            }
            if (this.leapkelp.leapTick > LEAP_TIMEOUT_TICKS
                    || this.leapkelp.leapTick > 2 && (this.leapkelp.onGround() || this.leapkelp.isLeapWaterLandingDone())) {
                this.smash();
                this.leapkelp.setLeaping(false);
                this.leapkelp.coolTick = ATTACK_COOLDOWN_TICKS;
            }
        }
        private void smash() {
            Vec3 front = MobUtil.getFrontPos(this.leapkelp, 1.0D);
            AABB aabb = new AABB(front, front).inflate(SMASH_INFLATE);
            boolean rest = this.leapkelp.getRandom().nextFloat() <= SMASH_REST_CHANCE;
            for (LivingEntity living : this.leapkelp.level().getEntitiesOfClass(LivingEntity.class, aabb)) {
                if (living == this.leapkelp || living.isAlliedTo(this.leapkelp) || this.leapkelp.isAlliedTo(living)) {
                    continue;
                }
                if (this.leapkelp.doHurtTarget(living) && (rest || !living.isAlive())) {
                    this.leapkelp.restTick = REST_TIME;
                }
            }
            if (!(this.leapkelp.level() instanceof ServerLevel serverLevel)) {
                return;
            }
            BlockPos pos = BlockPos.containing(front.x, this.leapkelp.getY() - 1.0D, front.z);
            BlockState state = serverLevel.getBlockState(pos);
            int color = state.getMapColor(serverLevel, pos).col;
            ColorUtil colorUtil = color == 0 ? ColorUtil.WHITE : new ColorUtil(color);
            double smashY = this.leapkelp.isInWater() ? this.leapkelp.getY() : BlockFinder.moveDownToGround(this.leapkelp);
            serverLevel.sendParticles(new SmashParticleOption(colorUtil, 5.0F, 10), front.x, smashY, front.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
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
            if (this.leapkelp.isCharging() || this.leapkelp.isLeaping()) {
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
