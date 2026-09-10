package com.qiuyue.goetyominous.common.entities.ally.mobs;
import com.Polarice3.Goety.common.entities.ai.path.GroundPathNavigatorFat;
import com.Polarice3.Goety.common.entities.ai.path.ModWaterPathNavigation;
import com.Polarice3.Goety.common.entities.ally.Leapleaf;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.config.MobsConfig;
import com.qiuyue.goetyominous.config.AttributesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.BlockTags;
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
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import java.util.EnumSet;
public class Leapkelp extends Leapleaf {
    public static final int MAX_MOISTNESS = 40000;
    public static final int SEEK_WATER_MOISTNESS = 4000;
    private static final int DRY_OUT_TICKS = 3 * 60 * 20;
    private static final int MAX_REST_TICKS = 20;
    private static final float SWIM_SPEED_MULTIPLIER = 1.6F;
    private static final int MOISTNESS_DRAIN_DAY = 10;
    private static final int MOISTNESS_DRAIN_NIGHT = 5;
    private static final EntityDataAccessor<Integer> MOISTNESS = SynchedEntityData.defineId(Leapkelp.class, EntityDataSerializers.INT);
    protected final ModWaterPathNavigation waterNavigation;
    protected final GroundPathNavigation groundNavigation;
    private boolean searchingForLand;
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
        if (this.level().isClientSide) {
            return;
        }
        if (this.restTick > MAX_REST_TICKS) {
            this.restTick = MAX_REST_TICKS;
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
    @Override
    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isInWater() && this.wantsToSwim()) {
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
    public static class LeapkelpMoveControl extends MoveControl {
        private final Leapkelp leapkelp;
        public LeapkelpMoveControl(Leapkelp leapkelp) {
            super(leapkelp);
            this.leapkelp = leapkelp;
        }
        @Override
        public void tick() {
            LivingEntity target = this.leapkelp.getTarget();
            LivingEntity owner = this.leapkelp.getTrueOwner();
            if (this.leapkelp.wantsToSwim() && this.leapkelp.isInWater()) {
                if (target != null && target.getY() > this.leapkelp.getY() || this.leapkelp.searchingForLand || owner != null && owner.getY() > this.leapkelp.getY() && this.leapkelp.isFollowing()) {
                    this.leapkelp.setDeltaMovement(this.leapkelp.getDeltaMovement().add(0.0D, 0.002D, 0.0D));
                }
                if (this.operation != MoveControl.Operation.MOVE_TO || this.leapkelp.getNavigation().isDone()) {
                    this.leapkelp.setSpeed(0.0F);
                    return;
                }
                double dx = this.wantedX - this.leapkelp.getX();
                double dy = this.wantedY - this.leapkelp.getY();
                double dz = this.wantedZ - this.leapkelp.getZ();
                double distance = Mth.sqrt((float) (dx * dx + dy * dy + dz * dz));
                dy /= distance;
                float yRot = (float) (Mth.atan2(dz, dx) * (180F / (float) Math.PI)) - 90.0F;
                this.leapkelp.setYRot(this.rotlerp(this.leapkelp.getYRot(), yRot, 90.0F));
                this.leapkelp.yBodyRot = this.leapkelp.getYRot();
                float speed = (float) (this.speedModifier * SWIM_SPEED_MULTIPLIER * this.leapkelp.getAttributeValue(Attributes.MOVEMENT_SPEED));
                float smoothed = Mth.lerp(0.125F, this.leapkelp.getSpeed(), speed);
                this.leapkelp.setSpeed(smoothed);
                this.leapkelp.setDeltaMovement(this.leapkelp.getDeltaMovement().add((double) smoothed * dx * 0.005D, (double) smoothed * dy * 0.1D, (double) smoothed * dz * 0.005D));
            } else {
                if (!this.leapkelp.onGround()) {
                    this.leapkelp.setDeltaMovement(this.leapkelp.getDeltaMovement().add(0.0D, -0.008D, 0.0D));
                }
                super.tick();
            }
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
            if (this.leapkelp.getY() < (double) (this.seaLevel - 1) && (this.leapkelp.getNavigation().isDone() || this.leapkelp.closeToNextPos())) {
                Vec3 pos = DefaultRandomPos.getPosTowards((PathfinderMob) this.leapkelp, 4, 8, new Vec3(this.leapkelp.getX(), this.seaLevel - 1, this.leapkelp.getZ()), 1.5707963705062866D);
                if (pos == null) {
                    this.stuck = true;
                    return;
                }
                this.leapkelp.getNavigation().moveTo(pos.x, pos.y, pos.z, this.speedModifier);
            }
        }
        @Override
        public void start() {
            this.leapkelp.setSearchingForLand(true);
            this.stuck = false;
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
