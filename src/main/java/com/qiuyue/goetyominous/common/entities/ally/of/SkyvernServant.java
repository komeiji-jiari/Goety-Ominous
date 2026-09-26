package com.qiuyue.goetyominous.common.entities.ally.of;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.qiuyue.goetyominous.common.entities.ally.of.goals.SkyvernServantChargeGoal;
import com.qiuyue.goetyominous.common.entities.ally.of.goals.SkyvernServantFlightGoal;
import com.qiuyue.goetyominous.client.sound.SkyvernServantLoopSoundHandler;
import com.qiuyue.goetyominous.common.init.of.OfEntityRegistry;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
import com.unusualmodding.opposing_force.entity.Skyvern.SkyvernVariant;
import com.unusualmodding.opposing_force.entity.ai.control.SkyvernLookControl;
import com.unusualmodding.opposing_force.entity.ai.navigation.SmoothFlyingPathNavigation;
import com.unusualmodding.opposing_force.entity.utils.AttackState;
import com.unusualmodding.opposing_force.entity.utils.OPPoses;
import com.unusualmodding.opposing_force.registry.OPSoundEvents;
import com.unusualmodding.opposing_force.utils.SmoothAnimationState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class SkyvernServant extends Summoned implements FlyingAnimal, AttackState {

    private static final EntityDataAccessor<Float> TARGET_PITCH;
    private static final EntityDataAccessor<Integer> ATTACK_STATE;
    private static final EntityDataAccessor<Integer> SEGMENTS;
    private static final EntityDataAccessor<Integer> VARIANT;

    public final SmoothAnimationState flyAnimationState = new SmoothAnimationState();
    public final SmoothAnimationState attackAnimationState = new SmoothAnimationState();
    public final SmoothAnimationState roarAnimationState = new SmoothAnimationState();
    public final SmoothAnimationState rollAnimationState = new SmoothAnimationState();

    private int roarTicks;
    private int rollTicks;
    private int roarCooldown;
    private int rollCooldown;
    private int segmentCheckCooldown;

    private int lSteps;
    private double lx;
    private double ly;
    private double lz;
    private double lyr;
    private double lxr;
    private double lxd;
    private double lyd;
    private double lzd;

    private float prevPitch;
    private float pitch;

    @Nullable
    private SkyvernSegmentServant[] parts;

    public SkyvernServant(EntityType<? extends Owned> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new SkyvernServantMoveControl(this);
        this.lookControl = new SkyvernLookControl(this);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.FENCE, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.WALKABLE, -1.0F);
        this.xpReward = 20;
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.SkyvernServantHealth.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.SkyvernServantMovementSpeed.get())
                .add(Attributes.FLYING_SPEED, AttributesConfig.SkyvernServantFlyingSpeed.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.SkyvernServantAttackDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.SkyvernServantKnockbackResistance.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.SkyvernServantFollowRange.get());
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new SkyvernServantChargeGoal(this));
        this.goalSelector.addGoal(2, new SkyvernServantFlightGoal(this));
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        SmoothFlyingPathNavigation navigation = new SmoothFlyingPathNavigation(this, level, 1.0F) {
            @Override
            public boolean isStableDestination(@NotNull BlockPos pos) {
                return !this.level.getBlockState(pos.below()).isAir();
            }
        };
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(false);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return MobsConfig.SkyvernServantLimit.get();
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean isFlying() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    public boolean isFood(ItemStack stack) {
        if (!stack.getItem().isEdible()) {
            return false;
        }
        FoodProperties foodProperties = stack.getFoodProperties(this);
        return foodProperties != null && foodProperties.isMeat();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (this.getTrueOwner() != null && player == this.getTrueOwner()
                && this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
            if (!this.level().isClientSide) {
                if (!player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                this.heal(5.0F);
                this.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
                this.gameEvent(GameEvent.EAT, this);
                if (this.level() instanceof ServerLevel serverLevel) {
                    for (int i = 0; i < 8; ++i) {
                        double d0 = this.random.nextGaussian() * 0.02D;
                        double d1 = this.random.nextGaussian() * 0.02D + 0.1D;
                        double d2 = this.random.nextGaussian() * 0.02D;
                        serverLevel.sendParticles(ParticleTypes.HEART,
                                this.getRandomX(1.0F),
                                this.getY() + this.getBbHeight() + 0.3F + this.random.nextDouble() * 0.5F,
                                this.getRandomZ(1.0F), 0, d0, d1, d2, 0.5D);
                    }
                }
            }
            player.swing(hand);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TARGET_PITCH, 0.0F);
        this.entityData.define(ATTACK_STATE, 0);
        this.entityData.define(SEGMENTS, 0);
        this.entityData.define(VARIANT, SkyvernVariant.CLOUDY.id());
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("Segments", this.getSegments());
        compoundTag.putInt("Variant", this.getVariant().id());
        compoundTag.putInt("AttackState", this.getAttackState());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setSegments(compoundTag.getInt("Segments"));
        this.setVariant(SkyvernVariant.byId(compoundTag.getInt("Variant")));
        this.setAttackState(compoundTag.getInt("AttackState"));
        this.applySegmentHealth();
    }

    @Override
    public int getAttackState() {
        return this.entityData.get(ATTACK_STATE);
    }

    @Override
    public void setAttackState(int attackState) {
        this.entityData.set(ATTACK_STATE, attackState);
    }

    public void holdMoveControl() {
        if (this.moveControl instanceof SkyvernServantMoveControl control) {
            control.hold();
        }
    }

    public void setTargetPitch(float pitch) {
        this.entityData.set(TARGET_PITCH, pitch);
    }

    public float getTargetPitch() {
        return this.entityData.get(TARGET_PITCH);
    }

    public void setSegments(int segments) {
        this.entityData.set(SEGMENTS, segments);
    }

    public int getSegments() {
        return this.entityData.get(SEGMENTS);
    }

    public void setVariant(SkyvernVariant variant) {
        this.entityData.set(VARIANT, variant.id());
    }

    public SkyvernVariant getVariant() {
        return SkyvernVariant.byId(this.entityData.get(VARIANT));
    }

    @Override
    public float getXRot() {
        return this.pitch;
    }

    @Override
    public float getViewXRot(float partialTicks) {
        return this.prevPitch + (this.pitch - this.prevPitch) * partialTicks;
    }

    @Override
    protected float getStandingEyeHeight(@NotNull Pose pose, @NotNull EntityDimensions dimensions) {
        return 0.5F * dimensions.height;
    }

    @Override
    public float getSoundVolume() {
        return super.getSoundVolume() * 3.0F;
    }

    @Override
    public void travel(@NotNull Vec3 travelVec) {
        if (this.isEffectiveAi() || this.isVehicle()) {
            this.moveRelative(this.getSpeed(), travelVec);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.calculateEntityAnimation(false);
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
        } else {
            super.travel(travelVec);
        }
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, @NotNull BlockState state, @NotNull BlockPos pos) {
    }

    @Override
    protected void playStepSound(@NotNull BlockPos pos, @NotNull BlockState state) {
    }

    @Override
    public boolean isInvulnerableTo(@NotNull DamageSource damageSource) {
        return super.isInvulnerableTo(damageSource)
                || damageSource.is(DamageTypes.IN_WALL)
                || damageSource.is(DamageTypes.CACTUS)
                || damageSource.is(DamageTypes.DROWN)
                || damageSource.is(DamageTypes.FALL);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distanceSqr) {
        return Math.sqrt(distanceSqr) < 1024.0D;
    }

    @Override
    public @NotNull AABB getBoundingBoxForCulling() {
        return super.getBoundingBoxForCulling().inflate(6.0D);
    }

    @Override
    public void push(@NotNull Entity entity) {
        if (this.level().isClientSide || this.isStaying() || entity == this.getTrueOwner()) {
            return;
        }
        if (this.isPassengerOfSameVehicle(entity) || entity instanceof SkyvernSegmentServant
                || entity.noPhysics || this.noPhysics) {
            return;
        }
        double dx = entity.getX() - this.getX();
        double dz = entity.getZ() - this.getZ();
        double max = Mth.absMax(dx, dz);
        if (max < 0.01D) {
            return;
        }
        max = Math.sqrt(max);
        dx /= max;
        dz /= max;
        double scale = 1.0D / max;
        if (scale > 1.0D) {
            scale = 1.0D;
        }
        dx *= scale;
        dz *= scale;
        dx *= 0.05D;
        dz *= 0.05D;
        if (!entity.isPassenger() && entity.isPushable()) {
            entity.push(dx, 0.0D, dz);
        }
    }

    @Override
    public void lerpTo(double x, double y, double z, float yRot, float xRot, int steps, boolean teleport) {
        this.lx = x;
        this.ly = y;
        this.lz = z;
        this.lyr = yRot;
        this.lxr = xRot;
        this.lSteps = steps;
        this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
    }

    @Override
    public void lerpMotion(double x, double y, double z) {
        this.lxd = x;
        this.lyd = y;
        this.lzd = z;
        this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isGhost()) {
            this.noPhysics = true;
        }
        this.prevPitch = this.pitch;
        this.yBodyRot = this.getYRot();
        this.yHeadRot = this.getYRot();
        if (!this.level().isClientSide) {
            this.setTargetPitch(-(float) (Mth.atan2(this.getDeltaMovement().y, this.getDeltaMovement().horizontalDistance()) * 57.2957763671875D));
        }
        this.pitch = Mth.approachDegrees(this.pitch, this.getTargetPitch(), 4.0F);
        if (!this.level().isClientSide) {
            if (this.roarCooldown > 0) {
                --this.roarCooldown;
            }
            if (this.rollCooldown > 0) {
                --this.rollCooldown;
            }
        }
        if (this.roarTicks > 0) {
            --this.roarTicks;
        }
        if (this.rollTicks > 0) {
            --this.rollTicks;
        }
        if (this.roarTicks == 0 && this.getPose() == Pose.ROARING) {
            this.setPose(Pose.STANDING);
        }
        if (this.rollTicks == 0 && this.getPose() == OPPoses.ROLLING.get()) {
            this.setPose(Pose.STANDING);
        }
        if (this.level().isClientSide) {
            this.setupAnimationStates();
            if (this.isAlive()) {
                SkyvernServantLoopSoundHandler.playFor(this);
            } else {
                SkyvernServantLoopSoundHandler.clearFor(this);
            }
            if (this.lSteps > 0) {
                double x = this.getX() + (this.lx - this.getX()) / (double) this.lSteps;
                double y = this.getY() + (this.ly - this.getY()) / (double) this.lSteps;
                double z = this.getZ() + (this.lz - this.getZ()) / (double) this.lSteps;
                this.setYRot(Mth.wrapDegrees((float) this.lyr));
                this.setXRot(this.getXRot() + (float) (this.lxr - (double) this.getXRot()) / (float) this.lSteps);
                --this.lSteps;
                this.setPos(x, y, z);
            } else {
                this.reapplyPosition();
            }
        } else {
            if (--this.segmentCheckCooldown <= 0) {
                this.segmentCheckCooldown = 10;
                this.ensureSegments();
            }
        }
        if (this.random.nextInt(800) == 0) {
            this.roar();
        } else if (this.random.nextInt(700) == 0) {
            this.roll();
        }
    }

    @Override
    public void moveTo(double x, double y, double z, float yRot, float xRot) {
        double dx = x - this.getX();
        double dy = y - this.getY();
        double dz = z - this.getZ();
        super.moveTo(x, y, z, yRot, xRot);
        if (this.parts != null && (dx != 0.0D || dy != 0.0D || dz != 0.0D)) {
            for (SkyvernSegmentServant part : this.parts) {
                if (part != null && !part.isRemoved()) {
                    part.moveTo(part.getX() + dx, part.getY() + dy, part.getZ() + dz, part.getYRot(), part.getXRot());
                }
            }
        }
    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        if (this.level().isClientSide) {
            SkyvernServantLoopSoundHandler.clearFor(this);
        }
        if (reason == RemovalReason.KILLED || reason == RemovalReason.DISCARDED || reason == RemovalReason.CHANGED_DIMENSION) {
            this.discardSegments();
        }
        super.remove(reason);
    }

    @Override
    public void die(@NotNull DamageSource damageSource) {
        if (this.level().isClientSide) {
            SkyvernServantLoopSoundHandler.clearFor(this);
        }
        this.discardSegments();
        super.die(damageSource);
    }

    private void setupAnimationStates() {
        this.flyAnimationState.animateWhen(this.getPose() == Pose.STANDING, this.tickCount);
        this.attackAnimationState.animateWhen(this.getPose() == OPPoses.ATTACKING.get(), this.tickCount);
        this.roarAnimationState.animateWhen(this.getPose() == Pose.ROARING, this.tickCount);
        this.rollAnimationState.animateWhen(this.getPose() == OPPoses.ROLLING.get(), this.tickCount);
    }

    public void roar() {
        if (this.roarCooldown == 0 && this.getPose() == Pose.STANDING) {
            this.roarTicks = 40;
            this.setPose(Pose.ROARING);
            this.playSound(OPSoundEvents.SKYVERN_ROAR.get(), 4.0F, 0.9F + this.random.nextFloat() * 0.2F);
            this.roarCooldown = 200 + this.random.nextInt(200);
        }
    }

    public void roll() {
        if (this.rollCooldown == 0 && this.getPose() == Pose.STANDING) {
            this.rollTicks = 40;
            this.setPose(OPPoses.ROLLING.get());
            this.rollCooldown = 300 + this.random.nextInt(300);
        }
    }

    private void applySegmentHealth() {
        double health = AttributesConfig.SkyvernServantHealth.get()
                + AttributesConfig.SkyvernServantHealthPerSegment.get() * (double) this.getSegments();
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(health);
        if (this.getHealth() > this.getMaxHealth()) {
            this.setHealth(this.getMaxHealth());
        }
    }

    private static SkyvernVariant getSkyvernVariant(ServerLevelAccessor level) {
        if (level.getLevel().isRaining() && !level.getLevel().isThundering()) {
            return SkyvernVariant.AZURE;
        }
        if (level.getLevel().isThundering()) {
            return SkyvernVariant.THUNDER;
        }
        return SkyvernVariant.CLOUDY;
    }

    private void ensureSegments() {
        int count = this.getSegments();
        if (count <= 0) {
            return;
        }
        if (this.parts != null && this.parts.length == count) {
            Entity front = this;
            boolean intact = true;
            for (SkyvernSegmentServant part : this.parts) {
                if (part == null || part.isRemoved() || part.distanceToSqr(front) > 144.0D) {
                    intact = false;
                    break;
                }
                front = part;
            }
            if (intact) {
                return;
            }
        }
        this.discardSegments();
        this.buildSegments(count);
    }

    private void buildSegments(int count) {
        SkyvernSegmentServant[] created = new SkyvernSegmentServant[count];
        Entity front = this;
        for (int i = 0; i < count; ++i) {
            SkyvernSegmentServant segment = new SkyvernSegmentServant(OfEntityRegistry.SKYVERN_SEGMENT_SERVANT.get(), this.level());
            segment.setHeadUUID(this.getUUID());
            segment.setFrontEntityUUID(front.getUUID());
            segment.setIndex(i);
            segment.setYRot(front.getYRot());
            segment.setXRot(front.getXRot());
            segment.setPos(segment.getIdealPosition(front));
            if (i >= 2 && i < count - 3 && (i - 2) % 4 == 0) {
                segment.setHasArms(true);
                if (this.random.nextBoolean()) {
                    segment.setHasOffsetArms(true);
                }
            }
            created[i] = segment;
            front = segment;
        }
        for (int i = 0; i < count; ++i) {
            SkyvernSegmentServant segment = created[i];
            if (i + 1 < count) {
                segment.setBackEntityUUID(created[i + 1].getUUID());
            }
            segment.setLinkIds(this, i == 0 ? this : created[i - 1], i + 1 < count ? created[i + 1] : null);
            this.level().addFreshEntity(segment);
        }
        this.parts = created;
    }

    private void discardSegments() {
        if (this.parts == null) {
            return;
        }
        for (SkyvernSegmentServant part : this.parts) {
            if (part != null) {
                part.discard();
            }
        }
        this.parts = null;
    }

    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor level, @NotNull DifficultyInstance difficulty, @NotNull MobSpawnType spawnType, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        this.setSegments(20 + level.getRandom().nextInt(4));
        this.applySegmentHealth();
        this.setVariant(getSkyvernVariant(level));
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, spawnType, spawnData, dataTag);
        this.setHealth(this.getMaxHealth());
        return result;
    }

    public boolean isGhost() {
        return this.isUpgraded() && MobsConfig.SkyvernServantGhost.get();
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return this.getTarget() != null ? OPSoundEvents.SKYVERN_IDLE_HOSTILE.get() : OPSoundEvents.SKYVERN_IDLE.get();
    }

    @Override
    protected @NotNull SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return OPSoundEvents.SKYVERN_HURT.get();
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return OPSoundEvents.SKYVERN_DEATH.get();
    }

    static {
        TARGET_PITCH = SynchedEntityData.defineId(SkyvernServant.class, EntityDataSerializers.FLOAT);
        ATTACK_STATE = SynchedEntityData.defineId(SkyvernServant.class, EntityDataSerializers.INT);
        SEGMENTS = SynchedEntityData.defineId(SkyvernServant.class, EntityDataSerializers.INT);
        VARIANT = SynchedEntityData.defineId(SkyvernServant.class, EntityDataSerializers.INT);
    }

    public static class SkyvernServantMoveControl extends MoveControl {
        public SkyvernServantMoveControl(Mob mob) {
            super(mob);
        }

        public void hold() {
            this.operation = Operation.WAIT;
        }

        @Override
        public void tick() {
            if (this.operation == Operation.MOVE_TO) {
                Vec3 delta = new Vec3(this.wantedX - this.mob.getX(), this.wantedY - this.mob.getY(), this.wantedZ - this.mob.getZ());
                double distance = delta.length();
                double width = this.mob.getBoundingBox().getSize();
                Vec3 scaled = delta.scale(this.speedModifier * 0.1D / distance);
                this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(scaled).scale(0.9D));
                if (distance < width) {
                    this.operation = Operation.WAIT;
                } else {
                    float yaw = -((float) Mth.atan2(scaled.x, scaled.z)) * 57.295776F;
                    this.mob.setYRot(Mth.approachDegrees(this.mob.getYRot(), yaw, 8.0F));
                }
            }
        }
    }
}
