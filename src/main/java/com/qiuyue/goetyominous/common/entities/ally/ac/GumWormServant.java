package com.qiuyue.goetyominous.common.entities.ally.ac;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.SummonTargetGoal;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.projectiles.FlyingItem;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ai.GroundPathNavigatorNoSpin;
import com.github.alexmodguy.alexscaves.server.entity.util.KaijuMob;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.qiuyue.goetyominous.common.entities.ai.ac.GumWormServantAttackGoal;
import com.qiuyue.goetyominous.common.entities.ai.ac.GumWormServantLeapRandomlyGoal;
import com.qiuyue.goetyominous.common.entities.ai.ac.GumWormServantRidingGoal;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
import com.qiuyue.goetyominous.utils.ModMobType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class GumWormServant extends Summoned implements KaijuMob {

    private static final EntityDataAccessor<Boolean> Z_ROT_DIRECTION = SynchedEntityData.defineId(GumWormServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> LEAPING = SynchedEntityData.defineId(GumWormServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> BITING = SynchedEntityData.defineId(GumWormServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> TARGET_DIG_PITCH = SynchedEntityData.defineId(GumWormServant.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> RIDING_SEGMENT_ID = SynchedEntityData.defineId(GumWormServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<UUID>> RIDING_SEGMENT_UUID = SynchedEntityData.defineId(GumWormServant.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> RIDER_LEAP_TIME = SynchedEntityData.defineId(GumWormServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> RIDER_LEAP_TIME_MAX = SynchedEntityData.defineId(GumWormServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> VALID_RIDER = SynchedEntityData.defineId(GumWormServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> LIMP = SynchedEntityData.defineId(GumWormServant.class, EntityDataSerializers.BOOLEAN);

    public static final int SEGMENT_COUNT_MIN = 15;
    public static final int SEGMENT_COUNT_RANGE = 5;
    public static final int RIDING_SEGMENT_INDEX = 3;
    private static final int ORPHAN_RECHECK_INTERVAL = 20;

    private int lSteps;
    private double lx;
    private double ly;
    private double lz;
    private double lyr;
    private double lxr;
    private double lxd;
    private double lyd;
    private double lzd;
    private float prevZRot;
    private float zRot;
    private float prevMouthOpenProgress;
    private float mouthOpenProgress;
    private float prevDigPitch;
    private float digPitch;
    private Vec3 surfacePosition;
    private Vec3 prevSurfacePosition;
    public int timeBetweenAttacks;
    public int leapAttackCooldown;
    private int ridingModeTicks;
    private int recentlyLeaptTicks;
    private int forceMouthOpenTicks;
    private int attackNoiseCooldown;
    private int stopDiggingNoiseCooldown;
    private int orphanRecheckTime;
    private Player ridingPlayer;
    private boolean keepLoadedStance;

    public GumWormServant(EntityType<? extends Summoned> entityType, Level level) {
        super(entityType, level);
        this.setMaxUpStep(1.0F);
        this.moveControl = new GumWormMoveController();
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.GumWormServantMovementSpeed.get())
                .add(Attributes.MAX_HEALTH, AttributesConfig.GumWormServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.GumWormServantFollowRange.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.GumWormServantDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.GumWormServantKnockbackResistance.get())
                .add(Attributes.ARMOR, AttributesConfig.GumWormServantArmor.get());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(Z_ROT_DIRECTION, false);
        this.entityData.define(LEAPING, false);
        this.entityData.define(BITING, false);
        this.entityData.define(TARGET_DIG_PITCH, 0.0F);
        this.entityData.define(RIDING_SEGMENT_ID, -1);
        this.entityData.define(RIDING_SEGMENT_UUID, Optional.empty());
        this.entityData.define(RIDER_LEAP_TIME_MAX, 1);
        this.entityData.define(RIDER_LEAP_TIME, 0);
        this.entityData.define(VALID_RIDER, false);
        this.entityData.define(LIMP, false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new GumWormServantRidingGoal(this));
        this.goalSelector.addGoal(1, new GumWormServantAttackGoal(this));
        this.goalSelector.addGoal(6, new GumWormServantLeapRandomlyGoal(this));
    }

    @Override
    public void targetSelectGoal() {
        super.targetSelectGoal();
        this.targetSelector.addGoal(1, new SummonTarget3DGoal());
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new GroundPathNavigatorNoSpin(this, level);
    }

    @Override
    public MobType getMobType() {
        return ModMobType.FEL;
    }

    @Override
    public int getSummonLimit(LivingEntity player) {
        return MobsConfig.GumWormServantLimit.get();
    }

    @Override
    public Predicate<Entity> summonPredicate() {
        return entity -> entity instanceof GumWormServant;
    }

    @Override
    public boolean isStaying() {
        return !this.isRidingMode() && super.isStaying();
    }

    @Override
    public boolean isCommanded() {
        return !this.isRidingMode() && super.isCommanded();
    }

    @Override
    public void setFollowing() {
        if (this.keepLoadedStance) {
            return;
        }
        this.setBoundPos(null);
        this.setWandering(false);
        this.setStaying(false);
    }

    @Override
    public void tryKill(Player player) {
        if (this.killChance <= 0) {
            this.warnKill(player);
        } else {
            super.tryKill(player);
        }
    }

    @Override
    public boolean canUpdateMove() {
        return !this.isRidingMode() && super.canUpdateMove();
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 0.4F * dimensions.height;
    }

    @Override
    public float getStepHeight() {
        return this.isRidingMode() ? 5.0F : this.maxUpStep();
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() || this.isVehicle()) {
            this.moveRelative(this.getSpeed(), travelVector);
            Vec3 delta = this.getDeltaMovement();
            if (!this.isNoGravity() && !this.onGround() && !this.isLeaping()) {
                if (this.isRidingMode()) {
                    if (!this.horizontalCollision && !this.level().getBlockState(this.blockPosition().below()).isSolid()) {
                        delta = delta.scale(0.9D).add(0.0D, -0.8D, 0.0D);
                    }
                } else {
                    delta = delta.add(0.0D, -0.5D, 0.0D);
                }
            }
            this.move(MoverType.SELF, delta);
            this.calculateEntityAnimation(false);
            this.setDeltaMovement(delta.scale(0.8D));
        } else {
            super.travel(travelVector);
        }
    }

    @Override
    public float getXRot() {
        return this.digPitch;
    }

    @Override
    public float getViewXRot(float partialTick) {
        return this.prevDigPitch + (this.digPitch - this.prevDigPitch) * partialTick;
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return this.getBoundingBox().inflate(6.0D);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return Math.sqrt(distance) < 1024.0D;
    }

    @Override
    public void tick() {
        super.tick();
        this.keepLoadedStance = false;
        this.prevSurfacePosition = this.surfacePosition;
        this.prevMouthOpenProgress = this.mouthOpenProgress;
        if (!this.level().isClientSide) {
            this.entityData.set(LIMP, this.isStaying() && this.getTarget() == null && !this.isRidingMode());
        }
        boolean limp = this.isLimp();
        if (this.isMoving() || this.surfacePosition == null) {
            this.surfacePosition = this.calculateLightAbovePosition();
        }
        if (this.isMouthOpen() && this.mouthOpenProgress < 10.0F) {
            this.mouthOpenProgress += 1.0F;
        }
        if (!this.isMouthOpen() && this.mouthOpenProgress > 0.0F) {
            this.mouthOpenProgress -= 1.0F;
        }
        this.prevZRot = this.zRot;
        this.prevDigPitch = this.digPitch;
        this.yBodyRot = this.getYRot();
        this.yHeadRot = this.getYRot();
        LivingEntity target = this.getTarget();
        if (limp) {
            this.getNavigation().stop();
            this.setSpeed(0.0F);
            this.setLeaping(false);
            this.setBiting(false);
            this.forceMouthOpenTicks = 0;
            this.setDeltaMovement(this.getDeltaMovement().scale(0.5D).multiply(1.0D, 0.0D, 1.0D));
            this.setTargetDigPitch(0.0F);
        } else if (!(this.level().isClientSide || this.isLeaping() && target != null && target.isAlive() || this.isRidingMode())) {
            this.setTargetDigPitch(-((float) Mth.atan2(this.getDeltaMovement().y, this.getDeltaMovement().horizontalDistance()) * 57.2957763671875F));
        }
        this.digPitch = Mth.approachDegrees(this.digPitch, this.getTargetDigPitch(), limp ? 20.0F : 5.0F);
        if (this.isMoving() && !limp) {
            this.zRot += this.getZRotDirection() ? -10.0F : 10.0F;
            if (this.random.nextInt(300) == 0 && !this.level().isClientSide) {
                this.entityData.set(Z_ROT_DIRECTION, this.random.nextBoolean());
            }
        } else {
            this.zRot = Mth.approachDegrees(this.zRot, limp ? 180.0F : 0.0F, limp ? 20.0F : 2.0F);
        }
        if (this.level().isClientSide) {
            if (this.lSteps > 0) {
                double d5 = this.getX() + (this.lx - this.getX()) / (double) this.lSteps;
                double d6 = this.getY() + (this.ly - this.getY()) / (double) this.lSteps;
                double d7 = this.getZ() + (this.lz - this.getZ()) / (double) this.lSteps;
                this.setYRot(Mth.wrapDegrees((float) this.lyr));
                this.setXRot(this.getXRot() + (float) (this.lxr - (double) this.getXRot()) / (float) this.lSteps);
                --this.lSteps;
                this.setPos(d5, d6, d7);
            } else {
                this.reapplyPosition();
            }
        } else {
            Entity ridingSegment = this.getRidingSegment();
            this.entityData.set(RIDING_SEGMENT_ID, ridingSegment == null ? -1 : ridingSegment.getId());
            if (ridingSegment == null && --this.orphanRecheckTime <= 0) {
                this.orphanRecheckTime = ORPHAN_RECHECK_INTERVAL;
                this.rebuildSegmentsIfOrphaned();
            }
        }
        if (this.timeBetweenAttacks > 0) {
            --this.timeBetweenAttacks;
        }
        if (this.leapAttackCooldown > 0) {
            --this.leapAttackCooldown;
        }
        if (this.ridingModeTicks > 0) {
            --this.ridingModeTicks;
        }
        if (this.recentlyLeaptTicks > 0 && !this.isLeaping()) {
            --this.recentlyLeaptTicks;
        }
        if (this.forceMouthOpenTicks > 0) {
            --this.forceMouthOpenTicks;
        }
        if (this.attackNoiseCooldown > 0) {
            --this.attackNoiseCooldown;
        }
        if (this.stopDiggingNoiseCooldown > 0) {
            --this.stopDiggingNoiseCooldown;
        }
    }

    private void rebuildSegmentsIfOrphaned() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        List<GumWormSegmentServantEntity> orphans = serverLevel.getEntitiesOfClass(GumWormSegmentServantEntity.class,
                this.getBoundingBox().inflate(128.0D), segment -> this.getUUID().equals(segment.getHeadUUID()));
        GumWormSegmentServantEntity riding = null;
        for (GumWormSegmentServantEntity segment : orphans) {
            if (segment.getIndex() == RIDING_SEGMENT_INDEX) {
                riding = segment;
                break;
            }
        }
        if (riding != null) {
            this.setRidingSegmentUUID(riding.getUUID());
            this.setRidingSegmentId(riding.getId());
        } else {
            GumWormSegmentServantEntity.createWormSegmentsFor(this, SEGMENT_COUNT_MIN + this.random.nextInt(SEGMENT_COUNT_RANGE));
        }
    }

    public void onMounted() {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        mutableBlockPos.set(this.getBlockX(), this.getBlockY() - 1, this.getBlockZ());
        while (!this.level().getBlockState(mutableBlockPos).is(Blocks.BEDROCK) && !this.level().getBlockState(mutableBlockPos).isAir()
                && mutableBlockPos.getY() < this.level().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, mutableBlockPos.getX(), mutableBlockPos.getZ())) {
            mutableBlockPos.move(0, 1, 0);
        }
        this.setPos(this.getX(), mutableBlockPos.getY(), this.getZ());
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        if (this.level().isClientSide) {
            return;
        }
        ItemStack stack = new ItemStack(ACItemRegistry.SWEET_TOOTH.get());
        LivingEntity owner = this.getTrueOwner();
        if (owner == null) {
            this.spawnAtLocation(stack);
            return;
        }
        FlyingItem flyingItem = new FlyingItem(ModEntityType.FLYING_ITEM.get(), this.level(), this.getX(), this.getY(), this.getZ());
        flyingItem.setOwner(owner);
        flyingItem.setItem(stack);
        flyingItem.setParticle(ACParticleRegistry.SUGAR_FLAKE.get());
        this.level().addFreshEntity(flyingItem);
    }

    @Override
    public void remove(RemovalReason removalReason) {
        if (!this.level().isClientSide && (removalReason == RemovalReason.KILLED || removalReason == RemovalReason.DISCARDED)) {
            for (GumWormSegmentServantEntity segment : this.getSegments()) {
                segment.discard();
            }
        }
        super.remove(removalReason);
    }

    public List<GumWormSegmentServantEntity> getSegments() {
        List<GumWormSegmentServantEntity> segments = new java.util.ArrayList<>();
        Entity riding = this.getRidingSegment();
        if (!(riding instanceof GumWormSegmentServantEntity rideSegment)) {
            return segments;
        }
        Entity front = rideSegment.getFrontEntity();
        while (front instanceof GumWormSegmentServantEntity frontSegment) {
            segments.add(frontSegment);
            front = frontSegment.getFrontEntity();
        }
        segments.add(rideSegment);
        Entity back = rideSegment.getBackEntity();
        while (back instanceof GumWormSegmentServantEntity backSegment) {
            segments.add(backSegment);
            back = backSegment.getBackEntity();
        }
        return segments;
    }

    public boolean attackAllAroundMouth(float damageAmount, float knockbackAmount) {
        boolean attackedMainTarget = false;
        AABB hurtBox = this.getBoundingBox().inflate(this.isLeaping() ? 3.0D : 1.0D);
        LivingEntity target = this.getTarget();
        DamageSource damageSource = this.damageSources().mobAttack(this);
        for (LivingEntity living : this.level().getEntitiesOfClass(LivingEntity.class, hurtBox, EntitySelector.NO_CREATIVE_OR_SPECTATOR)) {
            if (living.is(this) || living.isAlliedTo(this) || this.isRidingPlayer(living) || living.getType() == this.getType()) {
                continue;
            }
            if (living.hurt(damageSource, damageAmount)) {
                living.knockback(knockbackAmount, this.getX() - living.getX(), this.getZ() - living.getZ());
            }
            if (target == null || !living.is(target)) {
                continue;
            }
            attackedMainTarget = true;
        }
        return attackedMainTarget;
    }

    public boolean isRidingPlayer(Entity player) {
        return this.ridingPlayer != null && player.is(this.ridingPlayer);
    }

    public boolean isRidingMode() {
        return this.ridingModeTicks > 0;
    }

    public Player getRidingPlayer() {
        return this.ridingPlayer;
    }

    @Override
    public void lerpTo(double x, double y, double z, float yr, float xr, int steps, boolean b) {
        this.lx = x;
        this.ly = y;
        this.lz = z;
        this.lyr = yr;
        this.lxr = xr;
        this.lSteps = steps;
        this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
    }

    @Override
    public void lerpMotion(double lerpX, double lerpY, double lerpZ) {
        this.lxd = lerpX;
        this.lyd = lerpY;
        this.lzd = lerpZ;
        this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
    }

    public boolean getZRotDirection() {
        return this.entityData.get(Z_ROT_DIRECTION);
    }

    public boolean isLeaping() {
        return this.entityData.get(LEAPING);
    }

    public void setLeaping(boolean leaping) {
        this.entityData.set(LEAPING, leaping);
    }

    public boolean isBiting() {
        return this.entityData.get(BITING);
    }

    public void setBiting(boolean biting) {
        this.entityData.set(BITING, biting);
    }

    public boolean isMoving() {
        return this.getDeltaMovement().length() > 0.1D;
    }

    public boolean isMouthOpen() {
        return this.isLeaping() || this.isBiting();
    }

    public void setTargetDigPitch(float pitch) {
        this.entityData.set(TARGET_DIG_PITCH, pitch);
    }

    public float getTargetDigPitch() {
        return this.entityData.get(TARGET_DIG_PITCH);
    }

    public boolean isValidRider() {
        return this.entityData.get(VALID_RIDER);
    }

    public float getMouthOpenProgress(float partialTicks) {
        return (this.prevMouthOpenProgress + (this.mouthOpenProgress - this.prevMouthOpenProgress) * partialTicks) * 0.1F;
    }

    public float getBodyZRot(float partialTicks) {
        return this.prevZRot + (this.zRot - this.prevZRot) * partialTicks;
    }

    public boolean isLimp() {
        return this.entityData.get(LIMP);
    }

    public Entity getRidingSegment() {
        if (!this.level().isClientSide) {
            UUID id = this.getRidingSegmentUUID();
            return id == null ? null : ((ServerLevel) this.level()).getEntity(id);
        }
        int id = this.entityData.get(RIDING_SEGMENT_ID);
        return id == -1 ? null : this.level().getEntity(id);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> dataAccessor) {
        if (LEAPING.equals(dataAccessor)) {
            this.attemptPlayStopDiggingNoise();
            this.recentlyLeaptTicks = 15;
        }
        super.onSyncedDataUpdated(dataAccessor);
    }

    public void setRidingSegmentId(int id) {
        this.entityData.set(RIDING_SEGMENT_ID, id);
    }

    @Nullable
    public UUID getRidingSegmentUUID() {
        return this.entityData.get(RIDING_SEGMENT_UUID).orElse(null);
    }

    public void setRidingSegmentUUID(@Nullable UUID uniqueId) {
        this.entityData.set(RIDING_SEGMENT_UUID, Optional.ofNullable(uniqueId));
    }

    public void setRidingLeapTime(int time) {
        this.entityData.set(RIDER_LEAP_TIME, time);
    }

    public void setMaxRidingLeapTime(int time) {
        this.entityData.set(RIDER_LEAP_TIME_MAX, time);
    }

    public int getRidingLeapTime() {
        return this.entityData.get(RIDER_LEAP_TIME);
    }

    public int getMaxRidingLeapTime() {
        return this.entityData.get(RIDER_LEAP_TIME_MAX);
    }

    private Vec3 calculateLightAbovePosition() {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        mutableBlockPos.set(this.getBlockX(), this.getBlockY() - 1, this.getBlockZ());
        while (mutableBlockPos.getY() < this.level().getMaxBuildHeight()
                && this.level().getBlockState(mutableBlockPos).isSuffocating(this.level(), mutableBlockPos)) {
            mutableBlockPos.move(0, 1, 0);
        }
        return new Vec3(this.getX(), mutableBlockPos.getY(), this.getZ());
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        if (reason == MobSpawnType.NATURAL) {
            this.doInitialPosing(level);
        }
        GumWormSegmentServantEntity.createWormSegmentsFor(this, SEGMENT_COUNT_MIN + this.random.nextInt(SEGMENT_COUNT_RANGE));
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }

    private void doInitialPosing(LevelAccessor world) {
        BlockPos down = this.blockPosition().below();
        for (int downCount = 0; !world.getBlockState(down).isAir() && downCount < 10 && down.getY() > world.getMinBuildHeight(); ++downCount) {
            down = down.below();
        }
        this.setPos((float) down.getX() + 0.5F, down.getY() + 1, (float) down.getZ() + 0.5F);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        if (this.getRidingSegmentUUID() != null) {
            compoundTag.putUUID("RidingSegmentUUID", this.getRidingSegmentUUID());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.keepLoadedStance = true;
        if (compoundTag.hasUUID("RidingSegmentUUID")) {
            this.setRidingSegmentUUID(compoundTag.getUUID("RidingSegmentUUID"));
        }
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void push(Entity entity) {
        if (this.isPassengerOfSameVehicle(entity) || entity instanceof GumWormSegmentServantEntity || entity.noPhysics || this.noPhysics) {
            return;
        }
        double d0 = entity.getX() - this.getX();
        double d1 = entity.getZ() - this.getZ();
        double d2 = Mth.absMax(d0, d1);
        if (d2 < 0.01F) {
            return;
        }
        d2 = Math.sqrt(d2);
        d0 /= d2;
        d1 /= d2;
        double d3 = 1.0 / d2;
        if (d3 > 1.0) {
            d3 = 1.0;
        }
        d0 *= d3;
        d1 *= d3;
        d0 *= 0.05F;
        d1 *= 0.05F;
        if (!entity.isVehicle() && (entity.isPushable() || entity instanceof KaijuMob)) {
            entity.push(d0, 0.0D, d1);
        }
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return super.isInvulnerableTo(damageSource) || damageSource.is(DamageTypes.IN_WALL) || damageSource.is(DamageTypes.CACTUS)
                || damageSource.is(DamageTypes.DROWN) || damageSource.is(DamageTypes.FALL)
                || damageSource.getEntity() != null && this.isRidingPlayer(damageSource.getEntity());
    }

    @Override
    public Vec3 getLightProbePosition(float f) {
        if (this.surfacePosition != null && this.prevSurfacePosition != null) {
            Vec3 difference = this.surfacePosition.subtract(this.prevSurfacePosition);
            return this.prevSurfacePosition.add(difference.scale(f)).add(0.0D, this.getEyeHeight(), 0.0D);
        }
        return super.getLightProbePosition(f);
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effectInstance) {
        return super.canBeAffected(effectInstance) && effectInstance.getEffect() != MobEffects.HUNGER;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.GUM_WORM_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.GUM_WORM_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.GUM_WORM_DEATH.get();
    }

    @Override
    protected float getSoundVolume() {
        return super.getSoundVolume() * 3.0F;
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND && player.getItemInHand(hand).isEmpty() && !player.isShiftKeyDown()
                && player.getUUID().equals(this.getOwnerId()) && !this.level().isClientSide) {
            Entity ridingSegment = this.getRidingSegment();
            if (ridingSegment != null && !ridingSegment.isVehicle()) {
                this.onMounted();
                this.setCommandPos(null);
                player.startRiding(ridingSegment, true);
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(player, hand);
    }

    public void tickController(Player passenger) {
        this.ridingPlayer = passenger;
        this.entityData.set(VALID_RIDER, this.isRidingPlayer(passenger));
        if (!this.level().isClientSide) {
            this.ridingModeTicks = 10;
        }
    }

    public void onPlayerJump(int i) {
        int leapFor = (int) Math.ceil(i * 0.2F) + 10;
        this.setRidingLeapTime(leapFor);
        this.setMaxRidingLeapTime(leapFor);
    }

    public boolean recentlyLeapt() {
        return this.recentlyLeaptTicks > 0;
    }

    public void onRidingPlayerAttack() {
        this.forceMouthOpenTicks = 40;
        this.attemptPlayAttackNoise();
    }

    public boolean isMouthForcedOpen() {
        return this.forceMouthOpenTicks > 0;
    }

    public void attemptPlayAttackNoise() {
        if (this.attackNoiseCooldown == 0) {
            this.playSound(ACSoundRegistry.GUM_WORM_ATTACK.get(), this.getSoundVolume(), this.getVoicePitch());
            this.attackNoiseCooldown = 70;
        }
    }

    public void attemptPlayStopDiggingNoise() {
        if (this.stopDiggingNoiseCooldown == 0) {
            this.playSound(ACSoundRegistry.GUM_WORM_DIG_STOP.get(), this.getSoundVolume(), this.getVoicePitch());
            this.stopDiggingNoiseCooldown = 10;
        }
    }

    private class GumWormMoveController extends MoveControl {

        public GumWormMoveController() {
            super(GumWormServant.this);
        }

        @Override
        public void tick() {
            if (this.operation != MoveControl.Operation.MOVE_TO) {
                return;
            }
            Vec3 offset = new Vec3(this.wantedX - this.mob.getX(), this.wantedY - this.mob.getY(), this.wantedZ - this.mob.getZ());
            double distance = offset.length();
            double width = this.mob.getBoundingBox().getSize();
            if (distance < 1.0E-4D) {
                this.operation = MoveControl.Operation.WAIT;
                return;
            }
            double impulse = this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED);
            Vec3 push = offset.scale(impulse / distance);
            this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(push).scale(0.9F));
            if (distance < width * 0.15F) {
                this.operation = MoveControl.Operation.WAIT;
            } else if (distance >= width && !GumWormServant.this.isLeaping()) {
                this.mob.setYRot(Mth.approachDegrees(this.mob.getYRot(), -((float) Mth.atan2(push.x, push.z)) * 57.295776F, 25.0F));
            }
        }
    }

    private class SummonTarget3DGoal extends SummonTargetGoal {
        public SummonTarget3DGoal() {
            super(GumWormServant.this, false, false);
        }

        @Override
        protected AABB getTargetSearchArea(double distance) {
            return this.mob.getBoundingBox().inflate(distance, distance, distance);
        }
    }

}
