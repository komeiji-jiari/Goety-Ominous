package com.qiuyue.goetyominous.common.entities.ally.ac;

import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.github.alexmodguy.alexscaves.server.entity.ai.GroundPathNavigatorNoSpin;
import com.github.alexmodguy.alexscaves.server.entity.util.ShakesScreen;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.animation.LegSolverQuadruped;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
import com.qiuyue.goetyominous.common.network.ForsakenRiderJumpPacket;
import com.qiuyue.goetyominous.common.network.ModNetwork;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.PlayerRideable;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class ForsakenServant extends Summoned implements IAnimatedEntity, ShakesScreen, PlayerRideable {
    private static final EntityDataAccessor<Boolean> RUNNING = SynchedEntityData.defineId(ForsakenServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> LEAPING = SynchedEntityData.defineId(ForsakenServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SONIC_CHARGE = SynchedEntityData.defineId(ForsakenServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> SONAR_ID = SynchedEntityData.defineId(ForsakenServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> HELD_MOB_ID = SynchedEntityData.defineId(ForsakenServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DARKNESS_TIME = SynchedEntityData.defineId(ForsakenServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> RIDER_CHARGE_HOLD = SynchedEntityData.defineId(ForsakenServant.class, EntityDataSerializers.BOOLEAN);
    public static final Animation ANIMATION_SUMMON = Animation.create(50);
    public static final Animation ANIMATION_PREPARE_JUMP = Animation.create(15);
    public static final Animation ANIMATION_BITE = Animation.create(15);
    public static final Animation ANIMATION_LEFT_SLASH = Animation.create(33);
    public static final Animation ANIMATION_RIGHT_SLASH = Animation.create(33);
    public static final Animation ANIMATION_GROUND_SMASH = Animation.create(30);
    public static final Animation ANIMATION_SONIC_ATTACK = Animation.create(35);
    public static final Animation ANIMATION_SONIC_BLAST = Animation.create(45);
    public static final Animation ANIMATION_LEFT_PICKUP = Animation.create(48);
    public static final Animation ANIMATION_RIGHT_PICKUP = Animation.create(48);
    private static final int LIGHT_THRESHOLD = 4;
    private Animation currentAnimation = IAnimatedEntity.NO_ANIMATION;
    private int animationTick;
    public LegSolverQuadruped legSolver = new LegSolverQuadruped(-0.4F, 1.4F, 1F, 0.75F, 1F);
    private float runProgress;
    private float prevRunProgress;
    private float leapProgress;
    private float prevLeapProgress;
    private float leapPitch;
    private float prevLeapPitch;
    private float prevScreenShakeAmount;
    private float screenShakeAmount;
    private int timeLeaping = 0;
    private float raiseLeftArmProgress;
    private float prevRaiseLeftArmProgress;
    private float raiseRightArmProgress;
    private float prevRaiseRightArmProgress;
    private float darknessProgress;
    private float prevDarknessProgress;
    private boolean hasRunningAttributes = false;
    private int destroyBlocksTick = 10;

    private float cachedWalkSpeed = -1.0F;

    private static final int RIDER_JUMP_MAX_CHARGE = 15;

    private static final float RIDER_JUMP_MIN_RANGE = 5.0F;
    private static final float RIDER_JUMP_MAX_RANGE = 24.0F;
    private static final float RIDER_JUMP_FORWARD_IMPULSE = 0.155F;
    private static final float RIDER_JUMP_UP_BASE = 0.2F;
    private static final float RIDER_JUMP_UP_VX_SCALE = 0.3F;
    private static final float RIDER_JUMP_IDLE_FORWARD_FACTOR = 0.3F;

    private static final float POSE_SHIFT_BLOCKS = 10.0F / 16.0F;
    private static final float RIDER_CARRY_DAMP = 0.25F;

    private boolean riderCharging;

    private float riderJumpPendingPower = -1.0F;

    private boolean riderPrevJumpHeld;
    private boolean riderChargingClient;
    private int riderChargeTicksClient;

    public ForsakenServant(EntityType<? extends ForsakenServant> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.ForsakenServantMovementSpeed.get())
                .add(Attributes.MAX_HEALTH, AttributesConfig.ForsakenServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.ForsakenServantFollowRange.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.ForsakenServantDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.ForsakenServantKnockbackResistance.get());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(RUNNING, false);
        this.entityData.define(LEAPING, false);
        this.entityData.define(SONIC_CHARGE, false);
        this.entityData.define(DARKNESS_TIME, 0);
        this.entityData.define(RIDER_CHARGE_HOLD, false);
        this.entityData.define(SONAR_ID, -1);
        this.entityData.define(HELD_MOB_ID, -1);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ForsakenServantAttackGoal());
        this.goalSelector.addGoal(2, new ForsakenServantRandomlyJumpGoal());

        this.goalSelector.addGoal(5, new Summoned.WanderGoal<>(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new GroundPathNavigatorNoSpin(this, level);
    }

    public boolean hasRiderController() {
        return this.getControllingPassenger() instanceof Player;
    }

    private boolean isRiderBusy() {
        if (!(this.getControllingPassenger() instanceof Player player)) {
            return false;
        }
        if (this.riderCharging || this.riderChargingClient || this.isLeaping()) {
            return true;
        }
        return player.zza != 0.0F || player.xxa != 0.0F;
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        Entity entity = this.getFirstPassenger();
        if (entity instanceof Player player) {
            return player;
        }
        return null;
    }

    @Override
    public boolean isControlledByLocalInstance() {
        return this.isEffectiveAi();
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return passenger instanceof Player;
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        return new Vec3(this.getX(), this.getBoundingBox().minY, this.getZ());
    }

    @Override
    protected void updateControlFlags() {
        super.updateControlFlags();
        boolean busy = this.isRiderBusy();
        boolean notInBoat = !(this.getVehicle() instanceof Boat);
        this.goalSelector.setControlFlag(Goal.Flag.MOVE, !busy);
        this.goalSelector.setControlFlag(Goal.Flag.JUMP, !busy && notInBoat);
        this.goalSelector.setControlFlag(Goal.Flag.LOOK, !busy);
    }

    @Override
    protected Vec3 getRiddenInput(Player player, Vec3 vec3) {

        if (this.riderCharging) {
            return Vec3.ZERO;
        }
        float f = player.zza < 0.0F ? 0.5F : 1.0F;
        return new Vec3(player.xxa * 0.4F, 0.0D, player.zza * 0.9F * f);
    }

    @Override
    protected void tickRidden(Player player, Vec3 vec3) {
        super.tickRidden(player, vec3);

        if (player.zza != 0.0F || player.xxa != 0.0F || this.riderCharging) {
            this.setRot(player.getYRot(), player.getXRot() * 0.5F);
            this.yBodyRot = this.yHeadRot = this.yRotO = this.getYRot();
            this.getNavigation().stop();
            this.setTarget(null);
        }
        if (this.level().isClientSide) {

            this.tickRiderChargeSensing(player);
        } else if (this.riderJumpPendingPower >= 0.0F) {

            float power = this.riderJumpPendingPower;
            this.riderJumpPendingPower = -1.0F;
            if (this.onGround() && !this.isLeaping() && !this.isInWater()) {
                this.performRiderLeap(player, power);
            } else if (this.getAnimation() == ANIMATION_PREPARE_JUMP) {
                this.syncAnimation(NO_ANIMATION);
            }
        }
    }

    private void tickRiderChargeSensing(Player player) {
        if (!(player instanceof LocalPlayer local)) {
            return;
        }
        boolean held = local.input.jumping;
        boolean grounded = this.onGround() && !this.isLeaping() && !this.isInWater();
        if (held) {
            if (!this.riderPrevJumpHeld) {

                if (grounded && this.getAnimation() == NO_ANIMATION) {
                    this.riderChargingClient = true;
                    this.riderChargeTicksClient = 0;
                    this.sendRiderChargePacket(true, 0.0F);
                } else {
                    this.riderChargingClient = false;
                    this.riderChargeTicksClient = 0;
                }
            } else if (this.riderChargingClient && this.riderChargeTicksClient < RIDER_JUMP_MAX_CHARGE) {
                this.riderChargeTicksClient++;
            }
        } else if (this.riderChargingClient) {
            float power = Mth.clamp((float) this.riderChargeTicksClient / (float) RIDER_JUMP_MAX_CHARGE, 0.0F, 1.0F);
            this.riderChargingClient = false;
            this.riderChargeTicksClient = 0;
            this.sendRiderChargePacket(false, power);
        }
        this.riderPrevJumpHeld = held;
    }

    private void sendRiderChargePacket(boolean startCharge, float power) {
        ModNetwork.CHANNEL.sendToServer(new ForsakenRiderJumpPacket(this.getId(), startCharge, power));
    }

    public void serverStartRiderCharge() {
        if (!this.hasRiderController() || this.level().isClientSide || !this.onGround() || this.isLeaping() || this.isInWater()) {
            return;
        }
        this.riderCharging = true;
        this.riderJumpPendingPower = -1.0F;
        this.setRiderChargeHold(true);
        this.getNavigation().stop();
        this.setTarget(null);
        this.syncAnimation(ANIMATION_PREPARE_JUMP);
    }

    public void serverReleaseRiderCharge(float power) {
        boolean wasCharging = this.riderCharging;
        this.riderCharging = false;
        this.setRiderChargeHold(false);
        if (this.getAnimation() == ANIMATION_PREPARE_JUMP) {
            this.syncAnimation(NO_ANIMATION);
        }
        if (wasCharging && this.onGround() && !this.isLeaping() && !this.isInWater()) {
            this.riderJumpPendingPower = Mth.clamp(power, 0.0F, 1.0F);
        } else {
            this.riderJumpPendingPower = -1.0F;
        }
    }

    private void performRiderLeap(Player player, float power) {
        power = Mth.clamp(power, 0.0F, 1.0F);
        float range = RIDER_JUMP_MIN_RANGE + (RIDER_JUMP_MAX_RANGE - RIDER_JUMP_MIN_RANGE) * power;
        boolean forward = player.zza > 0.0F;
        float forwardFactor = forward ? 1.0F : RIDER_JUMP_IDLE_FORWARD_FACTOR;
        float forwardSpeed = RIDER_JUMP_FORWARD_IMPULSE * range * forwardFactor;
        float verticalSpeed = RIDER_JUMP_UP_BASE + RIDER_JUMP_UP_VX_SCALE * (RIDER_JUMP_FORWARD_IMPULSE * range);
        float yaw = this.getYRot() * ((float) Math.PI / 180F);
        Vec3 delta = this.getDeltaMovement();
        this.setDeltaMovement(
                delta.x - (double) (Mth.sin(yaw) * forwardSpeed),
                Math.max(verticalSpeed, delta.y),
                delta.z + (double) (Mth.cos(yaw) * forwardSpeed));
        this.setLeaping(true);
        this.setAnimation(NO_ANIMATION);
        this.hasImpulse = true;
        this.playSound(ACSoundRegistry.FORSAKEN_LEAP.get(), this.getSoundVolume(), this.getVoicePitch());
    }

    public float getRiderChargeMeter() {
        return this.riderChargingClient ? Mth.clamp((float) this.riderChargeTicksClient / (float) RIDER_JUMP_MAX_CHARGE, 0.0F, 1.0F) : 0.0F;
    }

    private float riderSeatPoseDelta() {
        float crouchAmount = 0.0F;
        if (this.getAnimation() == ANIMATION_PREPARE_JUMP && this.onGround() && !this.isLeaping()) {
            int tick = this.getAnimationTick();
            if (tick <= 5) {
                crouchAmount = tick / 5.0F;
            } else if (tick <= 10) {
                crouchAmount = 1.0F;
            } else {
                crouchAmount = Math.max(0.0F, (15 - tick) / 5.0F);
            }
        }
        float carryAmount = this.getLeapProgress(1.0F) * RIDER_CARRY_DAMP;
        return POSE_SHIFT_BLOCKS * (carryAmount - crouchAmount);
    }

    @Override
    protected float getRiddenSpeed(Player player) {

        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 1.15F;
    }

    protected void doPlayerRide(Player player) {
        if (!this.level().isClientSide) {
            player.setYRot(this.getYRot());
            player.setXRot(this.getXRot());
            player.startRiding(this);
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide) {
            ItemStack itemstack = player.getItemInHand(hand);
            if (this.getTrueOwner() != null && player == this.getTrueOwner()) {
                if (!player.isCrouching() && !this.isBaby()) {
                    Entity entity = this.getFirstPassenger();
                    if (entity != null && entity != player) {
                        entity.stopRiding();
                        return InteractionResult.SUCCESS;
                    }
                    if (!(itemstack.getItem() instanceof IWand)) {
                        this.doPlayerRide(player);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return super.mobInteract(player, hand);
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
    public void positionRider(Entity passenger, Entity.MoveFunction moveFunction) {
        if (this.hasPassenger(passenger) && passenger instanceof LivingEntity living) {
            if (!this.touchingUnloadedChunk()) {
                passenger.setYBodyRot(this.yBodyRot);
                passenger.fallDistance = 0.0F;
                this.clampRotation(living, 105.0F);
                Vec3 seatOffset = new Vec3(0.0F, 0.1F, 0.6F).yRot((float) Math.toRadians(-this.yBodyRot));
                float leapFade = 1.0F - this.getLeapProgress(1.0F);
                float heightBackLeft = this.legSolver.legs[0].getHeight(1.0F);
                float heightBackRight = this.legSolver.legs[1].getHeight(1.0F);
                float maxLegSolverHeight = (1.0F - ACMath.smin(1.0F - heightBackLeft, 1.0F - heightBackRight, 0.1F)) * 0.8F * leapFade;
                moveFunction.accept(passenger, this.getX() + seatOffset.x, this.getY() + seatOffset.y + this.getPassengersRidingOffset() - maxLegSolverHeight + this.riderSeatPoseDelta(), this.getZ() + seatOffset.z);
                return;
            }
        }
        super.positionRider(passenger, moveFunction);
    }

    private void clampRotation(LivingEntity livingEntity, float clampRange) {
        livingEntity.setYBodyRot(this.getYRot());
        float f = Mth.wrapDegrees(livingEntity.getYRot() - this.getYRot());
        float f1 = Mth.clamp(f, -clampRange, clampRange);
        livingEntity.yRotO += f1 - f;
        livingEntity.yBodyRotO += f1 - f;
        livingEntity.setYRot(livingEntity.getYRot() + f1 - f);
        livingEntity.setYHeadRot(livingEntity.getYRot());
    }

    @Override
    public void tick() {
        super.tick();
        this.prevRunProgress = runProgress;
        this.prevLeapProgress = leapProgress;
        this.prevRaiseLeftArmProgress = raiseLeftArmProgress;
        this.prevRaiseRightArmProgress = raiseRightArmProgress;
        this.prevDarknessProgress = darknessProgress;
        this.prevLeapPitch = leapPitch;
        this.prevScreenShakeAmount = screenShakeAmount;
        this.yBodyRot = Mth.approachDegrees(this.yBodyRotO, yBodyRot, getHeadRotSpeed());
        if (this.isRunning() && runProgress < 5.0F) {
            runProgress++;
        }
        if (!this.isRunning() && runProgress > 0.0F) {
            runProgress--;
        }
        if (isLeaping() && leapProgress < 5F) {
            leapProgress++;
        }
        if (!isLeaping() && leapProgress > 0F) {
            leapProgress--;
        }
        if (getDarknessTime() > 0 && darknessProgress < 5F) {
            darknessProgress++;
        }
        if (getDarknessTime() <= 0 && darknessProgress > 0F) {
            darknessProgress--;
        }
        if (this.isLeaping()) {
            if (this.onGround() && leapProgress >= 5.0F) {
                this.setLeaping(false);
            }
            timeLeaping++;
            Vec3 vec3 = this.getDeltaMovement();
            float f2 = (float) (-(Mth.atan2(vec3.y, vec3.horizontalDistance()) * (double) (180F / (float) Math.PI)));
            this.leapPitch = Mth.approachDegrees(leapPitch, f2, 5);
        } else {
            timeLeaping = 0;
            this.leapPitch = Mth.approachDegrees(leapPitch, 0, 5);

            if (this.getAnimation() == ANIMATION_PREPARE_JUMP && this.onGround() && !this.hasRiderController() && !this.isStaying() && !level().isClientSide && this.getAnimationTick() >= 8 && this.getAnimationTick() <= 10) {
                this.setLeaping(true);
                this.playSound(ACSoundRegistry.FORSAKEN_LEAP.get(), this.getSoundVolume(), this.getVoicePitch());
            }
        }

        if (!level().isClientSide && this.riderCharging) {
            if (!this.hasRiderController() || !this.isAlive()) {
                this.riderCharging = false;
                this.riderJumpPendingPower = -1.0F;
                this.setRiderChargeHold(false);
                if (this.getAnimation() == ANIMATION_PREPARE_JUMP) {
                    this.syncAnimation(NO_ANIMATION);
                }
            } else if (this.getAnimation() != ANIMATION_PREPARE_JUMP && !this.isLeaping()) {
                this.syncAnimation(ANIMATION_PREPARE_JUMP);
            }
        }
        if (level().isClientSide && !this.hasRiderController() && (this.riderChargingClient || this.riderPrevJumpHeld || this.riderChargeTicksClient > 0)) {
            this.riderChargingClient = false;
            this.riderPrevJumpHeld = false;
            this.riderChargeTicksClient = 0;
        }
        if (isRunning() && !hasRunningAttributes) {
            hasRunningAttributes = true;
            if (cachedWalkSpeed < 0) {
                cachedWalkSpeed = (float) this.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue();
            }
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(cachedWalkSpeed * 1.8F);
        }
        if (!isRunning() && hasRunningAttributes) {
            hasRunningAttributes = false;
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(cachedWalkSpeed);
        }
        boolean raisingLeftArm = this.isRaisingArm(true);
        boolean raisingRightArm = this.isRaisingArm(false);
        if (raisingLeftArm && raiseLeftArmProgress < 10.0F) {
            raiseLeftArmProgress++;
        }
        if (!raisingLeftArm && raiseLeftArmProgress > 0.0F) {
            raiseLeftArmProgress--;
        }
        if (raisingRightArm && raiseRightArmProgress < 10.0F) {
            raiseRightArmProgress++;
        }
        if (!raisingRightArm && raiseRightArmProgress > 0.0F) {
            raiseRightArmProgress--;
        }
        if (screenShakeAmount > 0) {
            screenShakeAmount = Math.max(0, screenShakeAmount - 0.34F);
        }
        this.legSolver.update(this, this.yBodyRot, this.getScale());
        if (level().isClientSide) {
            if (darknessProgress > 0) {
                for (int i = 0; i < 1; i++) {
                    if (random.nextBoolean()) {
                        level().addParticle(ACParticleRegistry.UNDERZEALOT_MAGIC.get(), this.getRandomX(1.0D), this.getRandomY(), this.getRandomZ(1.0D), this.getX(), this.getEyeY(), this.getZ());
                    } else {
                        level().addParticle(ParticleTypes.SMOKE, this.getRandomX(1.0D), this.getRandomY(), this.getRandomZ(1.0D), 0, 0, 0);
                    }
                }
            }
            if (this.getAnimation() == ANIMATION_SONIC_ATTACK) {
                if (this.getAnimationTick() > 10 && this.getAnimationTick() < 30) {
                    if (this.getAnimationTick() % 4 == 0) {
                        this.spawnForsakenSonar(false);
                    }
                    this.screenShakeAmount = 1F;
                }
            }
            if (this.getAnimation() == ANIMATION_SONIC_BLAST) {
                if (this.getAnimationTick() > 10 && this.getAnimationTick() < 30) {
                    if (this.getAnimationTick() % 4 == 0) {
                        this.spawnForsakenSonar(true);
                    }
                    this.screenShakeAmount = 1F;
                }
            }
            if (this.getAnimation() == ANIMATION_GROUND_SMASH) {
                if (this.getAnimationTick() >= 10 && this.getAnimationTick() <= 15) {
                    this.screenShakeAmount = 1F;
                }
                if (this.getAnimationTick() == 12) {
                    Vec3 smashPos = this.position().add(new Vec3(0, 0, 3.5F).yRot((float) -Math.toRadians(this.yBodyRot)));
                    float radius = 1.4F;
                    float particleCount = 20 + random.nextInt(12);
                    for (int i1 = 0; i1 < particleCount; i1++) {
                        double motionX = (getRandom().nextFloat() - 0.5F) * 0.7D;
                        double motionY = getRandom().nextFloat() * 0.7D + 1.8F;
                        double motionZ = (getRandom().nextFloat() - 0.5F) * 0.7D;
                        float angle = (0.01745329251F * (this.yBodyRot + (i1 / particleCount) * 360F));
                        double extraX = radius * Mth.sin((float) (Math.PI + angle));
                        double extraY = 1.2F;
                        double extraZ = radius * Mth.cos(angle);
                        BlockPos ground = BlockPos.containing(ACMath.getGroundBelowPosition(level(), new Vec3(Mth.floor(smashPos.x + extraX), Mth.floor(smashPos.y + extraY) + 2, Mth.floor(smashPos.z + extraZ))));
                        BlockState groundState = this.level().getBlockState(ground);
                        if (groundState.isSolid()) {
                            level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, groundState), true, smashPos.x + extraX, ground.getY() + extraY, smashPos.z + extraZ, motionX, motionY, motionZ);
                        }
                    }
                }
            }
        } else {

            if (this.isRiderBusy()) {
                this.setTarget(null);
                this.setRunning(false);
            }
            LivingEntity target = this.getTarget();
            if (target != null && target.isAlive() && target.distanceTo(this) < 10 && this.hasLineOfSight(target) && (this.getAnimation() == ANIMATION_RIGHT_PICKUP || this.getAnimation() == ANIMATION_LEFT_PICKUP)) {
                if (getHeldMobId() == -1) {
                    this.playSound(ACSoundRegistry.FORSAKEN_GRAB.get(), this.getSoundVolume(), this.getVoicePitch());
                }
                this.setHeldMobId(target.getId());
            } else if (getHeldMobId() != -1) {
                this.setHeldMobId(-1);
            }
            if (this.getHealth() < this.getMaxHealth() * 0.5F) {
                int lightLevel = getLightLevel();
                if (lightLevel <= LIGHT_THRESHOLD) {
                    this.setDarknessTime(30);
                } else if (getDarknessTime() > 0) {
                    this.setDarknessTime(this.getDarknessTime() - 1);
                }
                if (getDarknessTime() > 0 && this.tickCount % 30 == 0) {
                    this.heal(1);
                }
            } else {
                this.setDarknessTime(0);
            }
        }
        Entity grabbedEntity = this.getHeldMob();
        if (grabbedEntity != null && grabbedEntity.isAlive() && grabbedEntity.distanceTo(this) < 10) {
            grabbedEntity.fallDistance = 0;
            if ((this.getAnimation() == ANIMATION_RIGHT_PICKUP || this.getAnimation() == ANIMATION_LEFT_PICKUP) && this.getAnimationTick() >= 10 && this.getAnimationTick() <= 38) {
                Vec3 grabPos = getPickupPos();
                Vec3 minus = new Vec3(grabPos.x - grabbedEntity.getX(), grabPos.y - grabbedEntity.getY(), grabPos.z - grabbedEntity.getZ()).scale(0.33F);
                grabbedEntity.setDeltaMovement(minus);
            }
        }

        if (!this.level().isClientSide && this.currentAnimation == ANIMATION_SUMMON && this.animationTick <= 3) {
            AnimationHandler.INSTANCE.sendAnimationMessage(this, ANIMATION_SUMMON);
        }
        if (this.getRiderChargeHold() && this.currentAnimation == ANIMATION_PREPARE_JUMP && !this.isLeaping()) {
            AnimationHandler.INSTANCE.updateAnimations(this);
            if (this.currentAnimation == ANIMATION_PREPARE_JUMP && this.getAnimationTick() >= 8) {
                this.setAnimationTick(8);
            }
        } else {
            AnimationHandler.INSTANCE.updateAnimations(this);
        }
    }

    private void spawnForsakenSonar(boolean aoe) {
        Vec3 from = this.getEyePosition();
        if (!aoe) {
            Vec3 dir;
            Entity sonarTarget = this.getSonarTarget();
            if (sonarTarget != null && sonarTarget.isAlive()) {
                dir = sonarTarget.getEyePosition().subtract(from).normalize();
            } else {
                dir = new Vec3(0, 0, 1).yRot((float) -Math.toRadians(this.getYHeadRot())).xRot((float) -Math.toRadians(this.getXRot())).normalize();
            }

            float pitch = (float) Math.toDegrees(Math.atan2(dir.y, dir.horizontalDistance()));
            float yaw = (float) -Math.toDegrees(Math.atan2(dir.x, dir.z));
            level().addAlwaysVisibleParticle(ACParticleRegistry.FORSAKEN_SONAR.get(), true, from.x, from.y, from.z, this.getId(), pitch, yaw);
        } else {

            level().addAlwaysVisibleParticle(ACParticleRegistry.FORSAKEN_SONAR_LARGE.get(), true, from.x, from.y, from.z, this.getId(), 90.0F, 0.0F);
        }
    }

    private int getLightLevel() {
        BlockPos blockPos = this.blockPosition().above();
        return Math.max(this.level().getBrightness(LightLayer.BLOCK, blockPos), this.level().getMaxLocalRawBrightness(blockPos));
    }

    private Vec3 getPickupPos() {
        Vec3 handRotated = getHandPos(animationTick).yRot(-this.yBodyRot * ((float) Math.PI / 180F));
        return this.position().add(handRotated);
    }

    public int getDarknessTime() {
        return this.entityData.get(DARKNESS_TIME);
    }

    public void setDarknessTime(int time) {
        this.entityData.set(DARKNESS_TIME, time);
    }

    private Vec3 getHandPos(int animationTick) {
        float sideOffset = this.getAnimation() == ANIMATION_LEFT_PICKUP ? 1 : -1;
        Vec3 hand;
        if (animationTick <= 10) {
            hand = new Vec3(0F, 0, 4F);
        } else if (animationTick <= 15) {
            hand = new Vec3(0F, 1F, 3.7F);
        } else if (animationTick <= 25) {
            hand = new Vec3(sideOffset * 2.75F, 4.65F, 1.9F);
        } else {
            hand = new Vec3(sideOffset * 1.2F, 3.15F, 2.4F);
        }
        return hand;
    }

    public Entity getHeldMob() {
        int id = getHeldMobId();
        return id == -1 ? null : level().getEntity(id);
    }

    private boolean isRaisingArm(boolean left) {
        if (currentAnimation != NO_ANIMATION && currentAnimation != null && animationTick > currentAnimation.getDuration() - 5) {
            return false;
        }
        if (left && (this.currentAnimation == ANIMATION_LEFT_PICKUP || this.currentAnimation == ANIMATION_LEFT_SLASH)) {
            return true;
        }
        if (!left && (this.currentAnimation == ANIMATION_RIGHT_PICKUP || this.currentAnimation == ANIMATION_RIGHT_SLASH)) {
            return true;
        }
        return this.currentAnimation == ANIMATION_SUMMON || this.currentAnimation == ANIMATION_GROUND_SMASH;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public float getRunProgress(float partialTick) {
        return (prevRunProgress + (runProgress - prevRunProgress) * partialTick) * 0.2F;
    }

    public boolean isLeaping() {
        return this.entityData.get(LEAPING);
    }

    public void setLeaping(boolean bool) {
        this.entityData.set(LEAPING, bool);
    }

    public boolean getRiderChargeHold() {
        return this.entityData.get(RIDER_CHARGE_HOLD);
    }

    public void setRiderChargeHold(boolean bool) {
        this.entityData.set(RIDER_CHARGE_HOLD, bool);
    }

    public float getLeapProgress(float partialTick) {
        return (prevLeapProgress + (leapProgress - prevLeapProgress) * partialTick) * 0.2F;
    }

    public void setSonarId(int i) {
        this.entityData.set(SONAR_ID, i);
    }

    public Entity getSonarTarget() {
        int id = this.entityData.get(SONAR_ID);
        return id == -1 ? null : level().getEntity(id);
    }

    public void setHeldMobId(int i) {
        this.entityData.set(HELD_MOB_ID, i);
    }

    public int getHeldMobId() {
        return this.entityData.get(HELD_MOB_ID);
    }

    public float getLeapPitch(float partialTick) {
        return prevLeapPitch + (leapPitch - prevLeapPitch) * partialTick;
    }

    public boolean isRunning() {
        return this.entityData.get(RUNNING);
    }

    public void setRunning(boolean bool) {
        this.entityData.set(RUNNING, bool);
    }

    public boolean hasSonicCharge() {
        return this.entityData.get(SONIC_CHARGE);
    }

    public void setSonicCharge(boolean bool) {
        this.entityData.set(SONIC_CHARGE, bool);
    }

    public float getRaisedLeftArmAmount(float partialTicks) {
        return (prevRaiseLeftArmProgress + (raiseLeftArmProgress - prevRaiseLeftArmProgress) * partialTicks) * 0.1F;
    }

    public float getRaisedRightArmAmount(float partialTicks) {
        return (prevRaiseRightArmProgress + (raiseRightArmProgress - prevRaiseRightArmProgress) * partialTicks) * 0.1F;
    }

    @Override
    public float maxUpStep() {
        return hasRunningAttributes || hasRiderController() ? 1.1F : 0.6F;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn,
                                        MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn,
                                        @Nullable CompoundTag dataTag) {
        if (this.getTrueOwner() instanceof Player player) {
            if (countServants(player) >= MobsConfig.ForsakenServantLimit.get()) {
                this.discard();
                return null;
            }
        }
        SpawnGroupData spawnGroupData = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        if (!this.level().isClientSide) {
            this.setAnimation(ANIMATION_SUMMON);
        }
        return spawnGroupData;
    }

    private int countServants(Player player) {
        int count = 0;
        if (player.level() instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof ForsakenServant servant && servant != this) {
                    if (servant.getTrueOwner() == player) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    @Override
    public void setTrueOwner(@Nullable LivingEntity livingEntity) {
        super.setTrueOwner(livingEntity);
        if (!this.level().isClientSide && livingEntity instanceof Player player) {
            if (countServants(player) >= MobsConfig.ForsakenServantLimit.get()) {
                this.discard();
            }
        }
    }

    @Override
    public int getAnimationTick() {
        return animationTick;
    }

    @Override
    public void setAnimationTick(int tick) {
        animationTick = tick;
    }

    @Override
    public Animation getAnimation() {
        return currentAnimation;
    }

    @Override
    public void setAnimation(Animation animation) {
        if (this.getAnimation() != animation) {
            this.animationTick = 0;
            this.currentAnimation = animation;
        }
    }

    public void syncAnimation(Animation animation) {
        if (this.level().isClientSide) {
            this.setAnimation(animation);
        } else {
            AnimationHandler.INSTANCE.sendAnimationMessage(this, animation);
        }
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_SUMMON, ANIMATION_PREPARE_JUMP, ANIMATION_BITE, ANIMATION_LEFT_SLASH, ANIMATION_RIGHT_SLASH, ANIMATION_GROUND_SMASH, ANIMATION_SONIC_ATTACK, ANIMATION_SONIC_BLAST, ANIMATION_LEFT_PICKUP, ANIMATION_RIGHT_PICKUP};
    }

    public float getDarknessAmount(float partialTicks) {
        float animationValue = 0.0F;
        if (this.currentAnimation == ANIMATION_SUMMON) {
            animationValue = 1.0F - (this.getAnimationTick() + partialTicks) / (float) ANIMATION_SUMMON.getDuration();
        }
        return Math.max((prevDarknessProgress + (darknessProgress - prevDarknessProgress) * partialTicks) * 0.2F, animationValue);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (this.isInWall()) {
            if (this.destroyBlocksTick > 0) {
                --this.destroyBlocksTick;
                if (this.destroyBlocksTick == 0 && ForgeEventFactory.getMobGriefingEvent(this.level(), this)) {
                    int j1 = Mth.floor(this.getY());
                    int i2 = Mth.floor(this.getX());
                    int j2 = Mth.floor(this.getZ());
                    boolean flag = false;
                    for (int j = -1; j <= 1; ++j) {
                        for (int k2 = -1; k2 <= 1; ++k2) {
                            for (int k = 0; k <= 3; ++k) {
                                int l2 = i2 + j;
                                int l = j1 + k;
                                int i1 = j2 + k2;
                                BlockPos blockpos = new BlockPos(l2, l, i1);
                                BlockState blockstate = this.level().getBlockState(blockpos);
                                if (blockstate.canEntityDestroy(this.level(), blockpos, this) && !blockstate.is(ACTagRegistry.UNMOVEABLE) && ForgeEventFactory.onEntityDestroyBlock(this, blockpos, blockstate)) {
                                    flag = this.level().destroyBlock(blockpos, true, this) || flag;
                                }
                            }
                        }
                    }
                    if (flag) {
                        this.level().levelEvent((Player) null, 1022, this.blockPosition(), 0);
                    }
                    this.destroyBlocksTick = 20;
                }
            }
        }
    }

    @Override
    public float getScreenShakeAmount(float partialTicks) {
        return prevScreenShakeAmount + (screenShakeAmount - prevScreenShakeAmount) * partialTicks;
    }

    @Override
    public boolean canFeelShake(Entity player) {
        return true;
    }

    public boolean hurt(DamageSource damageSource, float f) {
        if (damageSource.is(DamageTypes.SONIC_BOOM)) {
            this.setSonicCharge(true);
            return false;
        } else {
            if (damageSource.getEntity() instanceof AbstractGolem) {
                f *= 0.5F;
            }
            return super.hurt(damageSource, f);
        }
    }

    public float getSonicDamageAgainst(LivingEntity target) {
        return target.getType().is(ACTagRegistry.WEAK_TO_FORSAKEN_SONIC_ATTACK) ? 45.0F : 4.0F;
    }

    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.FORSAKEN_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.FORSAKEN_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.FORSAKEN_DEATH.get();
    }

    @Override
    public float getSoundVolume() {
        return 2.5F;
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
        if (!this.isBaby()) {
            this.playSound(ACSoundRegistry.FORSAKEN_STEP.get(), 1F, 1F);
        }
    }

    protected float getWaterSlowDown() {
        return 0.98F;
    }

    private class ForsakenServantAttackGoal extends Goal {
        private BlockPos jumpTarget = null;
        private boolean jumpEnqueued = false;
        private boolean sonicEnqueued = false;
        private int navigationCheckCooldown = 0;
        private int attemptSonicDamageIn = 0;

        public ForsakenServantAttackGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = ForsakenServant.this.getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public void start() {
            this.navigationCheckCooldown = 0;
            this.jumpEnqueued = false;
            this.sonicEnqueued = ForsakenServant.this.getRandom().nextBoolean();
            this.attemptSonicDamageIn = 0;
        }

        @Override
        public void stop() {
            this.jumpEnqueued = false;
            this.sonicEnqueued = false;
            ForsakenServant.this.setRunning(false);
        }

        @Override
        public void tick() {
            LivingEntity target = ForsakenServant.this.getTarget();
            if (target != null && target.isAlive()) {
                boolean inPursuit;
                double distance = ForsakenServant.this.distanceTo(target);
                double attackDistance = ForsakenServant.this.getBbWidth() + target.getBbWidth();

                boolean standingBy = ForsakenServant.this.isStaying();
                boolean bl = inPursuit = !this.isMovementFrozen() && !standingBy;
                if (this.attemptSonicDamageIn > 0) {
                    --this.attemptSonicDamageIn;
                    if (this.attemptSonicDamageIn == 0 && ForsakenServant.this.hasLineOfSight(target)) {
                        target.hurt(target.damageSources().sonicBoom(ForsakenServant.this), ForsakenServant.this.getSonicDamageAgainst(target));
                        this.knockBackAngle(target, 1.0, 0.0F);
                    }
                }
                if (this.sonicEnqueued && ForsakenServant.this.hasLineOfSight(target) && distance < 200.0) {
                    ForsakenServant.this.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
                    ForsakenServant.this.setSonarId(target.getId());
                    ForsakenServant.this.getNavigation().stop();
                    if (distance > 10.0 || ForsakenServant.this.getRandom().nextFloat() < 0.4) {
                        this.tryAnimation(ANIMATION_SONIC_ATTACK);
                        ForsakenServant.this.playSound(ACSoundRegistry.FORSAKEN_SCREECH.get(), ForsakenServant.this.getSoundVolume(), ForsakenServant.this.getVoicePitch());
                    } else {
                        this.tryAnimation(ANIMATION_SONIC_BLAST);
                        ForsakenServant.this.playSound(ACSoundRegistry.FORSAKEN_AOE.get(), ForsakenServant.this.getSoundVolume(), ForsakenServant.this.getVoicePitch());
                    }
                    if (ForsakenServant.this.getAnimation() == ANIMATION_SONIC_ATTACK) {
                        inPursuit = false;
                        if (ForsakenServant.this.getAnimationTick() >= 10 && ForsakenServant.this.getAnimationTick() <= 30 && this.attemptSonicDamageIn <= 0) {
                            this.attemptSonicDamageIn = (int) Math.ceil(distance * 0.2F);
                        }
                        if (ForsakenServant.this.getAnimationTick() > 30) {
                            this.sonicEnqueued = false;
                        }
                    }
                    if (ForsakenServant.this.getAnimation() == ANIMATION_SONIC_BLAST) {
                        inPursuit = false;
                        if (ForsakenServant.this.getAnimationTick() >= 10 && ForsakenServant.this.getAnimationTick() <= 30 && ForsakenServant.this.getAnimationTick() % 5 == 0) {
                            List<LivingEntity> list = ForsakenServant.this.level().getEntitiesOfClass(LivingEntity.class, ForsakenServant.this.getBoundingBox().inflate(16.0, 8.0, 16.0));
                            for (LivingEntity living : list) {
                                if (living == ForsakenServant.this || ForsakenServant.this.isAlliedTo(living) || living.isAlliedTo(ForsakenServant.this) || !(living.distanceTo(ForsakenServant.this) <= 14.0F) || living.getType().is(ACTagRegistry.FORSAKEN_IGNORES)) {
                                    continue;
                                }
                                living.hurt(living.damageSources().sonicBoom(ForsakenServant.this), (float) Math.ceil(ForsakenServant.this.getSonicDamageAgainst(target) * 0.65F));
                            }
                        }
                        if (ForsakenServant.this.getAnimationTick() > 40) {
                            this.sonicEnqueued = false;
                        }
                    }
                } else if (this.jumpEnqueued) {
                    if (ForsakenServant.this.hasRiderController()) {
                        this.jumpEnqueued = false;
                        this.jumpTarget = null;
                    } else if (standingBy) {

                        this.jumpEnqueued = false;
                        this.jumpTarget = null;
                    } else if (this.jumpTarget == null) {
                        this.jumpTarget = this.findJumpTarget(target, distance > 20.0);
                    } else {
                        inPursuit = false;
                        if (ForsakenServant.this.isLeaping()) {
                            Vec3 vec3 = ForsakenServant.this.getDeltaMovement();
                            Vec3 vec31 = new Vec3((float) this.jumpTarget.getX() + 0.5F - ForsakenServant.this.getX(), 0, (float) this.jumpTarget.getZ() + 0.5F - ForsakenServant.this.getZ());
                            if (vec31.lengthSqr() > 1.0E-7) {
                                vec31 = vec31.scale(0.155F).add(vec3.scale(0.2));
                            }
                            ForsakenServant.this.setDeltaMovement(vec31.x, 0.2F + vec31.length() * 0.3F, vec31.z);
                            this.jumpEnqueued = false;
                            this.jumpTarget = null;
                        } else if (ForsakenServant.this.onGround()) {
                            ForsakenServant.this.lookAt(EntityAnchorArgument.Anchor.EYES, Vec3.atCenterOf(this.jumpTarget));
                            this.tryAnimation(ANIMATION_PREPARE_JUMP);
                        }
                    }
                }
                if (standingBy) {

                    ForsakenServant.this.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
                    ForsakenServant.this.getNavigation().stop();
                } else if (inPursuit) {
                    ForsakenServant.this.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
                    ForsakenServant.this.getNavigation().moveTo(target, 1.0);
                }
                if ((standingBy || inPursuit) && distance < attackDistance + 1.0 && ForsakenServant.this.getAnimation() == NO_ANIMATION) {
                    float attackType = ForsakenServant.this.getRandom().nextFloat();
                    if (attackType < 0.25F && target.getBbWidth() < 2.0F) {
                        this.tryAnimation(ForsakenServant.this.getRandom().nextBoolean() ? ANIMATION_LEFT_PICKUP : ANIMATION_RIGHT_PICKUP);
                    } else if (attackType < 0.5F) {
                        this.tryAnimation(ForsakenServant.this.getRandom().nextBoolean() ? ANIMATION_LEFT_SLASH : ANIMATION_RIGHT_SLASH);
                    } else if (attackType < 0.75F) {
                        this.tryAnimation(ANIMATION_GROUND_SMASH);
                    } else {
                        this.tryAnimation(ANIMATION_BITE);
                        ForsakenServant.this.playSound(ACSoundRegistry.FORSAKEN_BITE.get());
                    }
                }
                if ((distance > 20.0 && ForsakenServant.this.getRandom().nextFloat() < 0.01 || ForsakenServant.this.hasSonicCharge()) && !this.sonicEnqueued) {
                    this.sonicEnqueued = true;
                    ForsakenServant.this.setSonicCharge(false);
                }
                if (distance > 30.0 && !standingBy && !ForsakenServant.this.hasRiderController() && ForsakenServant.this.getRandom().nextFloat() < 0.05 && !this.jumpEnqueued) {
                    this.startCleanJump();
                }
                if (distance < 64.0 && distance > attackDistance && inPursuit) {
                    ForsakenServant.this.setRunning(true);
                } else {
                    ForsakenServant.this.setRunning(false);
                }
                if ((ForsakenServant.this.getAnimation() == ANIMATION_RIGHT_PICKUP || ForsakenServant.this.getAnimation() == ANIMATION_LEFT_PICKUP) && ForsakenServant.this.getHeldMobId() == target.getId() && ForsakenServant.this.getAnimationTick() >= 30) {
                    this.checkAndDealDamage(target, 1.2F, 5.0F);
                }
                if (ForsakenServant.this.getAnimation() == ANIMATION_RIGHT_SLASH && ForsakenServant.this.getAnimationTick() >= 15 && ForsakenServant.this.getAnimationTick() <= 18) {
                    float knockbackStrength = 0.5F;
                    if (this.checkAndDealDamage(target, 0.8F, 2.0F)) {
                        knockbackStrength = 3.0F;
                        ForsakenServant.this.playSound(ACSoundRegistry.FORSAKEN_GRAB.get());
                    }
                    this.knockBackAngle(target, knockbackStrength, -90.0F);
                }
                if (ForsakenServant.this.getAnimation() == ANIMATION_LEFT_SLASH && ForsakenServant.this.getAnimationTick() >= 15 && ForsakenServant.this.getAnimationTick() <= 18) {
                    float knockbackStrength = 0.5F;
                    if (this.checkAndDealDamage(target, 0.8F, 2.0F)) {
                        knockbackStrength = 3.0F;
                        ForsakenServant.this.playSound(ACSoundRegistry.FORSAKEN_GRAB.get());
                    }
                    this.knockBackAngle(target, knockbackStrength, 90.0F);
                }
                if (ForsakenServant.this.getAnimation() == ANIMATION_GROUND_SMASH && ForsakenServant.this.getAnimationTick() >= 10 && ForsakenServant.this.getAnimationTick() <= 15) {
                    Vec3 smashPos = ForsakenServant.this.position().add(new Vec3(0, 0, 3.5F).yRot((float) -Math.toRadians(ForsakenServant.this.yBodyRot)));
                    List<LivingEntity> list = ForsakenServant.this.level().getEntitiesOfClass(LivingEntity.class, new AABB(smashPos.x - 4.0, smashPos.y - 2.0, smashPos.z - 4.0, smashPos.x + 4.0, smashPos.y + 3.0, smashPos.z + 4.0));
                    boolean flag = false;
                    for (LivingEntity living : list) {
                        if (living == ForsakenServant.this || ForsakenServant.this.isAlliedTo(living) || living.isAlliedTo(ForsakenServant.this) || !(living.distanceToSqr(smashPos) <= 16.0) || living.getType().is(ACTagRegistry.FORSAKEN_IGNORES) || !this.checkAndDealDamage(living, 0.8F, 3.0F) || !living.onGround()) {
                            continue;
                        }
                        living.setDeltaMovement(living.getDeltaMovement().add(0.0, 0.5, 0.0));
                        flag = true;
                    }
                    if (flag) {
                        ForsakenServant.this.playSound(ACSoundRegistry.FORSAKEN_GRAB.get());
                    }
                }
                if (ForsakenServant.this.getAnimation() == ANIMATION_BITE && ForsakenServant.this.getAnimationTick() >= 5 && ForsakenServant.this.getAnimationTick() <= 8) {
                    float knockbackStrength = 0.0F;
                    if (this.checkAndDealDamage(target, 1.0, 1.0F)) {
                        knockbackStrength = 0.5F;
                    }
                    this.knockBackAngle(target, knockbackStrength, 0.0F);
                }
                if (this.navigationCheckCooldown-- < 0 && !standingBy && ForsakenServant.this.onGround()) {
                    this.navigationCheckCooldown = 20 + ForsakenServant.this.getRandom().nextInt(40);
                    if (!this.canReach(target) && !ForsakenServant.this.hasRiderController()) {
                        this.startCleanJump();
                    }
                }
            }
        }

        private boolean canReach(LivingEntity target) {
            Path path = ForsakenServant.this.getNavigation().createPath(target, 0);
            if (path == null) {
                return false;
            }
            Node node = path.getEndNode();
            if (node == null) {
                return false;
            }
            int i = node.x - target.getBlockX();
            int j = node.y - target.getBlockY();
            int k = node.z - target.getBlockZ();
            return (double) (i * i + j * j + k * k) <= 3.0;
        }

        private boolean isMovementFrozen() {
            return ForsakenServant.this.getAnimation() == ANIMATION_LEFT_PICKUP || ForsakenServant.this.getAnimation() == ANIMATION_RIGHT_PICKUP;
        }

        private void startCleanJump() {
            this.jumpTarget = null;
            this.jumpEnqueued = true;
        }

        private boolean checkAndDealDamage(LivingEntity target, double multiplier, float extraRange) {
            if (ForsakenServant.this.hasLineOfSight(target) && ForsakenServant.this.distanceTo(target) < ForsakenServant.this.getBbWidth() + target.getBbWidth() + extraRange) {
                boolean b = target.hurt(target.damageSources().mobAttack(ForsakenServant.this), (float) (multiplier * ForsakenServant.this.getAttribute(Attributes.ATTACK_DAMAGE).getValue()));

                if (!ForsakenServant.this.isStaying() && !ForsakenServant.this.hasRiderController() && ForsakenServant.this.getRandom().nextInt(2) == 0) {
                    this.startCleanJump();
                }
                if (!this.sonicEnqueued && ForsakenServant.this.getRandom().nextInt(5) == 0) {
                    this.sonicEnqueued = true;
                }
                return b;
            }
            return false;
        }

        private void knockBackAngle(LivingEntity target, double strength, float angle) {
            float yRot = ForsakenServant.this.yBodyRot + angle;
            target.knockback(strength, Mth.sin((float) (yRot * ((float) Math.PI / 180))), -Mth.cos((float) (yRot * ((float) Math.PI / 180))));
        }

        private boolean tryAnimation(Animation animation) {
            if (ForsakenServant.this.getAnimation() == NO_ANIMATION) {
                ForsakenServant.this.syncAnimation(animation);
                return true;
            }
            return false;
        }

        private BlockPos findJumpTarget(LivingEntity target, boolean far) {
            int lengthOfRadius = far ? ForsakenServant.this.getRandom().nextInt(2) + 4 : ForsakenServant.this.getRandom().nextInt(10) + 15;
            Vec3 offset = target.position().add(new Vec3(0, 0, lengthOfRadius).yRot((float) (Math.PI * 2 * ForsakenServant.this.getRandom().nextFloat())));
            Vec3 vec3 = null;
            if (far) {
                BlockPos farPos = LandRandomPos.movePosUpOutOfSolid(ForsakenServant.this, BlockPos.containing(offset));
                if (farPos != null) {
                    vec3 = Vec3.atCenterOf(farPos);
                }
            } else {
                vec3 = LandRandomPos.getPosTowards(ForsakenServant.this, 20, 10, offset);
            }
            if (vec3 != null) {
                BlockPos blockpos = BlockPos.containing(vec3);
                AABB aabb = ForsakenServant.this.getBoundingBox().move(vec3.add(0.5, 1.0, 0.5).subtract(ForsakenServant.this.position()));
                if (ForsakenServant.this.level().getBlockState(blockpos.below()).isSolidRender(ForsakenServant.this.level(), blockpos.below()) && ForsakenServant.this.getPathfindingMalus(WalkNodeEvaluator.getBlockPathTypeStatic(ForsakenServant.this.level(), blockpos.mutable())) == 0.0F && ForsakenServant.this.level().isUnobstructed(ForsakenServant.this, Shapes.create(aabb))) {
                    return blockpos;
                }
            }
            return null;
        }
    }

    private class ForsakenServantRandomlyJumpGoal extends Goal {
        private BlockPos jumpTarget = null;
        private boolean hasPerformedJump = false;

        public ForsakenServantRandomlyJumpGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = ForsakenServant.this.getTarget();
            if (ForsakenServant.this.onGround() && !ForsakenServant.this.hasRiderController() && (target == null || !target.isAlive()) && !ForsakenServant.this.isStaying() && !ForsakenServant.this.isCommanded() && ForsakenServant.this.getRandom().nextInt(140) == 0 && ForsakenServant.this.getAnimation() == NO_ANIMATION) {
                BlockPos findTarget = this.findJumpTarget();
                if (findTarget != null) {
                    this.jumpTarget = findTarget;
                    return true;
                }
            }
            return false;
        }

        @Override
        public void start() {
            this.hasPerformedJump = false;
            ForsakenServant.this.getNavigation().stop();
            if (ForsakenServant.this.getAnimation() == NO_ANIMATION) {
                ForsakenServant.this.syncAnimation(ANIMATION_PREPARE_JUMP);
            }
            ForsakenServant.this.lookAt(EntityAnchorArgument.Anchor.EYES, Vec3.atCenterOf(this.jumpTarget));
        }

        @Override
        public boolean canContinueToUse() {
            return (ForsakenServant.this.getAnimation() == ANIMATION_PREPARE_JUMP || ForsakenServant.this.isLeaping()) && this.jumpTarget != null;
        }

        @Override
        public void tick() {
            if (ForsakenServant.this.isLeaping() && !this.hasPerformedJump) {
                this.hasPerformedJump = true;
                Vec3 vec3 = ForsakenServant.this.getDeltaMovement();
                Vec3 vec31 = new Vec3((float) this.jumpTarget.getX() + 0.5F - ForsakenServant.this.getX(), 0, (float) this.jumpTarget.getZ() + 0.5F - ForsakenServant.this.getZ());
                if (vec31.length() > 100.0) {
                    vec31 = vec3.normalize().scale(100.0);
                }
                if (vec31.lengthSqr() > 1.0E-7) {
                    vec31 = vec31.scale(0.155F).add(vec3.scale(0.2));
                }
                ForsakenServant.this.setDeltaMovement(vec31.x, 0.2F + vec31.length() * 0.3F, vec31.z);
            }
        }

        private BlockPos findJumpTarget() {
            Vec3 vec3 = DefaultRandomPos.getPos(ForsakenServant.this, 25, 10);
            if (vec3 != null) {
                BlockPos blockpos = BlockPos.containing(vec3);
                AABB aabb = ForsakenServant.this.getBoundingBox().move(vec3.add(0.5, 1.0, 0.5).subtract(ForsakenServant.this.position()));
                if (ForsakenServant.this.level().getBlockState(blockpos.below()).isSolidRender(ForsakenServant.this.level(), blockpos.below()) && ForsakenServant.this.getPathfindingMalus(WalkNodeEvaluator.getBlockPathTypeStatic(ForsakenServant.this.level(), blockpos.mutable())) == 0.0F && ForsakenServant.this.level().isUnobstructed(ForsakenServant.this, Shapes.create(aabb))) {
                    return blockpos;
                }
            }
            return null;
        }
    }
}
