package com.qiuyue.goetyominous.common.entities.ally.ac;

import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.common.entities.ally.AnimalSummon;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ai.GroundPathNavigatorNoSpin;
import com.github.alexmodguy.alexscaves.server.entity.util.KeybindUsingMount;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.message.MountedEntityKeyMessage;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.qiuyue.goetyominous.client.sound.CandicornServantChargeSoundHandler;
import com.qiuyue.goetyominous.common.init.ac.AcParticles;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class CandicornServant extends AnimalSummon implements IAnimatedEntity, PlayerRideableJumping, KeybindUsingMount {

    public static final Animation ANIMATION_BUCK = Animation.create(25);
    public static final Animation ANIMATION_TAIL_FLICK_1 = Animation.create(12);
    public static final Animation ANIMATION_TAIL_FLICK_2 = Animation.create(12);
    public static final Animation ANIMATION_NIBBLE_IDLE = Animation.create(35);
    public static final Animation ANIMATION_STAB = Animation.create(25);
    private static final EntityDataAccessor<Boolean> RUNNING = SynchedEntityData.defineId(CandicornServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> LEAPING = SynchedEntityData.defineId(CandicornServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> CHARGING = SynchedEntityData.defineId(CandicornServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> CHARGE_YAW = SynchedEntityData.defineId(CandicornServant.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> METER_AMOUNT = SynchedEntityData.defineId(CandicornServant.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> LEAP_PITCH = SynchedEntityData.defineId(CandicornServant.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(CandicornServant.class, EntityDataSerializers.INT);
    private float prevLeapProgress;
    private float leapProgress;
    private float prevRunProgress;
    private float runProgress;
    private float prevChargeProgress;
    private float vehicleProgress;
    private float prevVehicleProgress;
    private float chargeProgress;
    private float prevSitProgress;
    private float sitProgress;
    private float prevManeAngle;
    private float maneAngle;
    private Animation currentAnimation = IAnimatedEntity.NO_ANIMATION;
    private int animationTick;
    private float tailYaw;
    private float prevTailYaw;
    private float leapPitch;
    private float prevLeapPitch;
    private boolean hasRunningAttributes = false;
    private double baseMoveSpeed;
    private int chargeParticleCooldown = 0;
    private boolean leapImpulse;
    private int playerDrivenChargeTicks = 0;
    private int controllerForwardsTicks;
    protected int gallopSoundCounter;

    public CandicornServant(EntityType<? extends CandicornServant> type, Level level) {
        super(type, level);
        tailYaw = this.getYRot();
        prevTailYaw = this.getYRot();
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.CandicornServantMovementSpeed.get())
                .add(Attributes.MAX_HEALTH, AttributesConfig.CandicornServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.CandicornServantFollowRange.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.CandicornServantDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.CandicornServantKnockbackResistance.get());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(RUNNING, false);
        this.entityData.define(LEAPING, false);
        this.entityData.define(CHARGING, false);
        this.entityData.define(CHARGE_YAW, 0.0F);
        this.entityData.define(METER_AMOUNT, 0.0F);
        this.entityData.define(LEAP_PITCH, 0.0F);
        this.entityData.define(VARIANT, 0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(2, new CandicornServantMeleeGoal());
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
        if (this.isCharging() || this.isLeaping() || this.isRunning()) {
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
        if (this.isBaby()) {
            return false;
        }
        if (passenger instanceof Player) {
            return true;
        }
        return passenger instanceof IServant
                && (this.getTrueOwner() == null || this.getTrueOwner() == ((IServant) passenger).getTrueOwner());
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
        return new Animation[]{ANIMATION_BUCK, ANIMATION_TAIL_FLICK_1, ANIMATION_TAIL_FLICK_2, ANIMATION_NIBBLE_IDLE, ANIMATION_STAB};
    }

    public boolean isRunning() {
        return this.entityData.get(RUNNING);
    }

    public void setRunning(boolean bool) {
        this.entityData.set(RUNNING, bool);
    }

    public boolean isLeaping() {
        return this.entityData.get(LEAPING);
    }

    public void setLeaping(boolean bool) {
        this.entityData.set(LEAPING, bool);
    }

    public boolean isCharging() {
        return this.entityData.get(CHARGING);
    }

    public void setCharging(boolean bool) {
        this.entityData.set(CHARGING, bool);
    }

    public int getVariant() {
        return this.entityData.get(VARIANT);
    }

    public void setVariant(int variant) {
        this.entityData.set(VARIANT, variant);
    }

    public float getChargeYaw() {
        return this.entityData.get(CHARGE_YAW);
    }

    public void setChargeYaw(float chargeYaw) {
        this.entityData.set(CHARGE_YAW, chargeYaw);
    }

    public float getMeterAmount() {
        return this.entityData.get(METER_AMOUNT);
    }

    public void setMeterAmount(float meterAmount) {
        this.entityData.set(METER_AMOUNT, meterAmount);
    }

    public boolean isSittingDown() {
        return this.isStaying() && !this.isVehicle() && !this.isPassenger();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Variant", this.getVariant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Variant")) {
            this.setVariant(compound.getInt("Variant"));
        }
    }

    @Override
    public void tick() {
        super.tick();
        prevLeapProgress = leapProgress;
        prevRunProgress = runProgress;
        prevChargeProgress = chargeProgress;
        prevSitProgress = sitProgress;
        prevTailYaw = tailYaw;
        prevVehicleProgress = vehicleProgress;
        prevManeAngle = maneAngle;
        prevLeapPitch = leapPitch;
        if (!this.level().isClientSide) {
            leapPitch = Mth.clamp((float) this.getDeltaMovement().y, -0.5F, 1.5F) * -(float) (180F / (float) Math.PI);
            this.entityData.set(LEAP_PITCH, leapPitch);
        } else {
            leapPitch = this.entityData.get(LEAP_PITCH);
        }
        if (this.isLeaping() && leapProgress < 5.0F) {
            leapProgress++;
        }
        if (!this.isLeaping() && leapProgress > 0.0F) {
            leapProgress--;
        }
        if (this.isRunning() && runProgress < 5.0F) {
            runProgress++;
        }
        if (!this.isRunning() && runProgress > 0.0F) {
            runProgress--;
        }
        if (this.isCharging() && chargeProgress < 5.0F) {
            chargeProgress++;
        }
        if (!this.isCharging() && chargeProgress > 0.0F) {
            chargeProgress = Math.max(0.0F, chargeProgress - 0.25F);
        }
        if (this.isSittingDown() && sitProgress < 5.0F) {
            sitProgress++;
        }
        if (!this.isSittingDown() && sitProgress > 0.0F) {
            sitProgress--;
        }
        if (this.getControllingPassenger() != null && vehicleProgress < 5.0F) {
            vehicleProgress++;
        }
        if (this.getControllingPassenger() == null && vehicleProgress > 0.0F) {
            vehicleProgress--;
        }
        if (this.onGround() && this.isLeaping() && !leapImpulse) {
            this.setLeaping(false);
        }
        if (leapImpulse) {
            leapImpulse = false;
        }
        if (this.isCharging() && !this.level().isClientSide) {
            Vec3 chargeFocalPoint = this.position().add(new Vec3(0.0F, 0.0F, 1.0F).yRot((float) Math.toRadians(-this.getChargeYaw())));
            for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(3.5D))) {
                float dist = (float) entity.distanceToSqr(chargeFocalPoint);
                if (!this.isAlliedTo(entity) && !entity.isAlliedTo(this) && !entity.isPassengerOfSameVehicle(this) && !(entity instanceof CandicornServant) && entity != this && dist < 7.0F) {
                    float dmgExtra = 7.0F - dist;
                    entity.hurt(this.damageSources().mobAttack(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() + dmgExtra);
                    entity.knockback(2.0F, chargeFocalPoint.x - entity.getX(), chargeFocalPoint.z - entity.getZ());
                }
            }
            if (!this.isVehicle()) {
                this.setYRot(Mth.approachDegrees(this.yRotO, this.getChargeYaw(), 25));
                this.yBodyRot = Mth.approachDegrees(this.yBodyRot, this.getChargeYaw(), 25);
                this.yBodyRotO = this.yBodyRot;
                this.yHeadRot = this.getYRot();
                this.yHeadRotO = this.yHeadRot;
            }
        }
        if (chargeParticleCooldown <= 0 && this.chargeProgress > 2.5F) {
            chargeParticleCooldown = 3;
            this.level().addAlwaysVisibleParticle((SimpleParticleType) AcParticles.CANDICORN_CHARGE.get(), true, this.getX(), this.getY() + 0.5F, this.getZ(), this.getId(), 0.0F, this.getChargeYaw());
        }
        if (chargeParticleCooldown > 0) {
            chargeParticleCooldown--;
        }
        if (!this.level().isClientSide) {
            if (this.getDeltaMovement().horizontalDistance() < 0.02 && random.nextInt(200) == 0 && controllerForwardsTicks <= 0 && this.getAnimation() == NO_ANIMATION && !this.isNoAi() && !this.isVehicle() && !this.isSittingDown() && !this.isLeaping()) {
                Animation idle;
                float rand = random.nextFloat();
                if (rand < 0.15F) {
                    idle = ANIMATION_BUCK;
                } else if (rand < 0.6F) {
                    idle = random.nextBoolean() ? ANIMATION_TAIL_FLICK_1 : ANIMATION_TAIL_FLICK_2;
                } else {
                    idle = ANIMATION_NIBBLE_IDLE;
                }
                this.syncAnimation(idle);
            }
            if (this.isRunning() && !hasRunningAttributes) {
                hasRunningAttributes = true;
                if (baseMoveSpeed == 0.0D) {
                    baseMoveSpeed = this.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue();
                }
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(baseMoveSpeed * 2.0D);
            }
            if (!this.isRunning() && hasRunningAttributes) {
                hasRunningAttributes = false;
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(baseMoveSpeed);
            }
        } else {
            if (this.isCharging() && this.isAlive()) {
                CandicornServantChargeSoundHandler.startChargeFor(this);
            }
            Player player = AlexsCaves.PROXY.getClientSidePlayer();
            if (player != null && player.isPassengerOfSameVehicle(this)) {
                if (AlexsCaves.PROXY.isKeyDown(2) && this.getMeterAmount() >= 1.0F && this.isRunning()) {
                    AlexsCaves.sendMSGToServer(new MountedEntityKeyMessage(this.getId(), player.getId(), 2));
                }
            }
        }
        Entity controllingPassenger = this.getControllingPassenger();
        if (controllingPassenger != null) {
            float f = Mth.degreesDifference(controllingPassenger.getYHeadRot(), this.yBodyRot);
            if (Math.abs(f) > 2.0F) {
                if (f < 0.0F) {
                    maneAngle = Mth.approach(maneAngle, -1.0F, 0.3F);
                } else {
                    maneAngle = Mth.approach(maneAngle, 1.0F, 0.3F);
                }
            }
            if (!this.level().isClientSide) {
                if (this.touchingWall()) {
                    this.setRunning(false);
                    this.setCharging(false);
                    playerDrivenChargeTicks = 0;
                    controllerForwardsTicks = 0;
                }
                if (playerDrivenChargeTicks > 0) {
                    this.setCharging(true);
                    this.setRunning(true);
                    this.setChargeYaw(controllingPassenger.getYHeadRot());
                    playerDrivenChargeTicks--;
                } else if (this.isCharging()) {
                    this.setCharging(false);
                }
                if (controllerForwardsTicks > 20) {
                    this.setRunning(true);
                } else {
                    this.setRunning(false);
                    this.setCharging(false);
                    playerDrivenChargeTicks = 0;
                }
                if (this.getMeterAmount() < 1.0F && !this.isCharging() && this.isRunning()) {
                    this.setMeterAmount(Math.min(this.getMeterAmount() + 0.005F, 1.0F));
                }
            }
        } else {
            if (controllerForwardsTicks > 0) {
                controllerForwardsTicks = 0;
                this.setRunning(false);
            }
            if (playerDrivenChargeTicks > 0) {
                playerDrivenChargeTicks = 0;
                this.setCharging(false);
            }
            this.setMeterAmount(0.0F);
        }
        tailYaw = Mth.approachDegrees(this.tailYaw, this.yBodyRot, 10);

        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public boolean touchingWall() {
        if (this.noPhysics) {
            return false;
        } else {
            float f = this.getBbWidth() + 0.1F;
            AABB aabb = AABB.ofSize(this.getEyePosition(), (double) f, 1.0E-6D, (double) f);
            return BlockPos.betweenClosedStream(aabb).anyMatch((blockPos) -> {
                BlockState blockstate = this.level().getBlockState(blockPos);
                return !blockstate.isAir() && blockstate.isSuffocating(this.level(), blockPos) && Shapes.joinIsNotEmpty(blockstate.getCollisionShape(this.level(), blockPos).move(blockPos.getX(), blockPos.getY(), blockPos.getZ()), Shapes.create(aabb), BooleanOp.AND);
            });
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> dataAccessor) {
        if (CHARGING.equals(dataAccessor) && this.level().isClientSide) {
            if (this.isCharging()) {
                this.playSound(ACSoundRegistry.CANDICORN_CHARGE_START.get(), 1.0F, 1.0F);
            }
        }
        super.onSyncedDataUpdated(dataAccessor);
    }

    @Override
    public void onKeyPacket(Entity keyPresser, int type) {
        if (keyPresser.isPassengerOfSameVehicle(this)) {
            if (type == 2) {
                if (this.getMeterAmount() >= 1.0F) {
                    this.setChargeYaw(keyPresser.getYHeadRot());
                    this.playerDrivenChargeTicks = 100;
                    this.setMeterAmount(0.0F);
                }
            }
        }
    }

    @Override
    public void onPlayerJump(int i) {
        this.performServantLeap(i);
    }

    @Override
    public boolean canJump() {
        return !this.isLeaping();
    }

    @Override
    public void handleStartJump(int i) {
        this.performServantLeap(i);
    }

    @Override
    public void handleStopJump() {
    }

    private void performServantLeap(int i) {
        this.setLeaping(true);
        if (this.onGround()) {
            this.leapImpulse = true;
            float f = 0.2F + i * 0.01F;
            Vec3 jumpForwards = new Vec3(0.0F, f, this.zza).yRot((float) Math.toRadians(-this.yBodyRot));
            this.setDeltaMovement(this.getDeltaMovement().add(jumpForwards));
        }
    }

    @Override
    protected Vec3 getRiddenInput(Player player, Vec3 deltaIn) {
        float f = player.zza < 0.0F ? 0.5F : 1.0F;
        return new Vec3(player.xxa * 0.35F, 0.0D, player.zza * 0.8F * f);
    }

    @Override
    protected void tickRidden(Player player, Vec3 vec3) {
        super.tickRidden(player, vec3);
        if (player.zza != 0.0F || player.xxa != 0.0F) {
            this.setRot(player.getYRot(), player.getXRot() * 0.25F);
            this.setYHeadRot(player.getYHeadRot());
            this.setTarget(null);
        }
        if (vec3.z <= 0.0D) {
            this.gallopSoundCounter = 0;
        }
        if (player.zza > 0) {
            controllerForwardsTicks++;
        } else {
            controllerForwardsTicks = 0;
        }
    }

    @Override
    protected float getRiddenSpeed(Player rider) {
        float f = 0.0F;
        if (this.isCharging()) {
            f = 0.25F;
        } else if (controllerForwardsTicks < 20) {
            f = (20 - controllerForwardsTicks) * -0.005F;
        }
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) + f;
    }

    @Override
    public float maxUpStep() {
        return this.isCharging() ? 2.2F : (this.isRunning() || this.hasRiderController()) ? 1.1F : 0.6F;
    }

    public float getLeapProgress(float partialTicks) {
        return (prevLeapProgress + (leapProgress - prevLeapProgress) * partialTicks) * 0.2F;
    }

    public float getRunProgress(float partialTicks) {
        return (prevRunProgress + (runProgress - prevRunProgress) * partialTicks) * 0.2F;
    }

    public float getChargeProgress(float partialTicks) {
        return (prevChargeProgress + (chargeProgress - prevChargeProgress) * partialTicks) * 0.2F;
    }

    public float getSitProgress(float partialTicks) {
        return (prevSitProgress + (sitProgress - prevSitProgress) * partialTicks) * 0.2F;
    }

    public float getVehicleProgress(float partialTicks) {
        return (prevVehicleProgress + (vehicleProgress - prevVehicleProgress) * partialTicks) * 0.2F;
    }

    public float getManeAngle(float partialTicks) {
        return (prevManeAngle + (maneAngle - prevManeAngle) * partialTicks);
    }

    public float getTailYaw(float partialTick) {
        return (prevTailYaw + (tailYaw - prevTailYaw) * partialTick);
    }

    public float getLeapPitch(float partialTicks) {
        return (prevLeapPitch + (leapPitch - prevLeapPitch) * partialTicks);
    }

    @Override
    public void positionRider(Entity passenger, Entity.MoveFunction moveFunction) {
        if (this.isPassengerOfSameVehicle(passenger) && passenger instanceof LivingEntity living && !this.touchingUnloadedChunk()) {
            float animationUp = 0.0F;
            float animationBack = 0.0F;
            if (this.getAnimation() == ANIMATION_BUCK) {
                float f = ACMath.cullAnimationTick(this.getAnimationTick(), 2.5F, this.getAnimation(), 1.0F, 0, 25);
                animationUp = 0.25F * f;
                animationBack = f;
            }
            if (this.getAnimation() == ANIMATION_STAB) {
                float f = ACMath.cullAnimationTick(this.getAnimationTick(), 2.0F, this.getAnimation(), 1.0F, 0, 11);
                animationUp = 0.15F * f;
                animationBack = 1.5F * f;
            }
            Vec3 seatOffset = new Vec3(0.0F, -0.4F + animationUp, -0.2F - animationBack).yRot((float) Math.toRadians(-this.yBodyRot));
            passenger.setYBodyRot(this.yBodyRot);
            passenger.fallDistance = 0.0F;
            moveFunction.accept(passenger, this.getX() + seatOffset.x, this.getY() + seatOffset.y + this.getPassengersRidingOffset(), this.getZ() + seatOffset.z);
        } else {
            super.positionRider(passenger, moveFunction);
        }
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        return new Vec3(this.getX(), this.getBoundingBox().minY, this.getZ());
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(ACBlockRegistry.CANDY_CANE.get().asItem()) || itemStack.is(ACItemRegistry.CARAMEL_APPLE.get());
    }

    public boolean isBreedingItem(ItemStack itemStack) {
        return itemStack.is(ACBlockRegistry.CANDY_CANE.get().asItem());
    }

    private void doPlayerRide(Player player) {
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
                boolean breedingFood = this.isBreedingItem(itemstack);
                if (this.isFood(itemstack)) {
                    boolean isBaby = this.isBaby();
                    boolean hurt = this.getHealth() < this.getMaxHealth();
                    boolean canLove = breedingFood && !isBaby && this.getAge() == 0 && this.canFallInLove();
                    boolean grow = breedingFood && isBaby;
                    if (canLove || grow || hurt) {
                        if (canLove) {
                            this.usePlayerItem(player, hand, itemstack);
                            this.setInLove(player);
                        } else if (grow) {
                            this.usePlayerItem(player, hand, itemstack);
                            this.ageUp(AnimalSummon.getSpeedUpSecondsWhenFeeding(-this.getAge()), true);
                        }
                        if (hurt) {
                            this.heal(5.0F);
                            if (!canLove && !grow && !player.getAbilities().instabuild) {
                                itemstack.shrink(1);
                            }
                        }
                        this.gameEvent(GameEvent.EAT, this);
                        this.spawnFeedParticles();
                        this.swing(hand);
                        return InteractionResult.SUCCESS;
                    }
                    return InteractionResult.PASS;
                }
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

    private void spawnFeedParticles() {
        if (this.level() instanceof ServerLevel serverLevel) {
            double yTop = this.getY() + this.getBbHeight();
            for (int i = 0; i < 7; ++i) {
                double d0 = this.getRandom().nextGaussian() * 0.02D;
                double d1 = this.getRandom().nextGaussian() * 0.02D + 0.02D;
                double d2 = this.getRandom().nextGaussian() * 0.02D;
                serverLevel.sendParticles(ParticleTypes.HEART,
                        this.getX() + this.getRandom().nextGaussian() * 0.35D,
                        yTop + this.getRandom().nextGaussian() * 0.15D + 0.05D,
                        this.getZ() + this.getRandom().nextGaussian() * 0.35D,
                        1, d0, d1, d2, 0.0D);
            }
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn,
                                        MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn,
                                        @Nullable CompoundTag dataTag) {
        if (this.getTrueOwner() instanceof Player player) {
            if (countServants(player) >= MobsConfig.CandicornServantLimit.get()) {
                this.discard();
                return null;
            }
        }
        SpawnGroupData spawnGroupData = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        if (dataTag != null && dataTag.contains("Variant")) {
            this.setVariant(dataTag.getInt("Variant"));
        } else {
            this.setVariant(this.getRandom().nextInt(5));
        }
        return spawnGroupData;
    }

    private int countServants(Player player) {
        int count = 0;
        if (player.level() instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof CandicornServant servant && servant != this) {
                    if (servant.getTrueOwner() == player) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    @Override
    public boolean canMate(AnimalSummon partner) {
        if (!super.canMate(partner)) {
            return false;
        }
        if (this.getTrueOwner() != partner.getTrueOwner()) {
            return false;
        }
        if (this.getTrueOwner() instanceof Player player) {
            return countServants(player) + 1 < MobsConfig.CandicornServantLimit.get();
        }
        return true;
    }

    @Override
    @Nullable
    public AnimalSummon getBreedOffspring(ServerLevel level, AnimalSummon partner) {
        AnimalSummon offspring = super.getBreedOffspring(level, partner);
        if (offspring instanceof CandicornServant baby && partner instanceof CandicornServant mate) {
            baby.setVariant(this.getRandom().nextBoolean() ? this.getVariant() : mate.getVariant());
        }
        return offspring;
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return this.getType().getDimensions().scale(this.isBaby() ? 0.5F : 1.0F);
    }

    @Override
    protected void ageBoundaryReached() {
        super.ageBoundaryReached();
        this.refreshDimensions();
    }

    @Override
    public void setTrueOwner(@Nullable LivingEntity livingEntity) {
        super.setTrueOwner(livingEntity);
        if (!this.level().isClientSide && livingEntity instanceof Player player) {
            if (countServants(player) >= MobsConfig.CandicornServantLimit.get()) {
                this.discard();
            }
        }
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effectInstance) {
        return super.canBeAffected(effectInstance) && effectInstance.getEffect() != MobEffects.HUNGER;
    }

    @Override
    public void remove(Entity.RemovalReason removalReason) {
        if (this.level().isClientSide) {
            CandicornServantChargeSoundHandler.clearChargeFor(this);
        }
        super.remove(removalReason);
    }

    public int getParticleColor() {
        switch (this.getVariant()) {
            case 1:
                return 0XFFADD2;
            case 2:
                return 0XDFF3FF;
            case 3:
                return 0XA7FFD0;
            case 4:
                return 0XFFBAF4;
            default:
                return 0XFFEF57;
        }
    }

    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.CANDICORN_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.CANDICORN_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.CANDICORN_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos blockPos, BlockState blockState) {
        if (!blockState.liquid()) {
            BlockState blockstate = this.level().getBlockState(blockPos.above());
            SoundType soundtype = blockState.getSoundType(level(), blockPos, this);
            if (blockstate.is(Blocks.SNOW)) {
                soundtype = blockstate.getSoundType(level(), blockPos, this);
            }
            if (this.isVehicle()) {
                ++this.gallopSoundCounter;
                if (this.gallopSoundCounter > 5 && this.gallopSoundCounter % 6 == 0) {
                    this.playSound(ACSoundRegistry.CANDICORN_GALLOP.get(), soundtype.getVolume() * 0.2F, soundtype.getPitch());
                } else if (this.gallopSoundCounter <= 10) {
                    this.playSound(ACSoundRegistry.CANDICORN_STEP.get(), soundtype.getVolume() * 0.15F, soundtype.getPitch());
                }
            } else {
                this.playSound(ACSoundRegistry.CANDICORN_STEP.get(), soundtype.getVolume() * 0.15F, soundtype.getPitch());
            }
        }
    }

    private class CandicornServantMeleeGoal extends Goal {

        private int chargeCooldown = 0;
        private int chargeTimeout = 0;
        private Vec3 startChargeTargetVec = Vec3.ZERO;
        private Vec3 startChargeFromVec = Vec3.ZERO;

        public CandicornServantMeleeGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = CandicornServant.this.getTarget();
            return target != null && target.isAlive() && !CandicornServant.this.hasRiderController();
        }

        @Override
        public void stop() {
            CandicornServant.this.setRunning(false);
            CandicornServant.this.setCharging(false);
            chargeCooldown = 0;
            startChargeTargetVec = Vec3.ZERO;
            startChargeFromVec = Vec3.ZERO;
        }

        @Override
        public void tick() {
            CandicornServant candicorn = CandicornServant.this;
            if (chargeCooldown > 0) {
                chargeCooldown--;
            }
            if (chargeTimeout > 100) {
                chargeTimeout = 0;
                candicorn.setCharging(false);
            }
            LivingEntity target = candicorn.getTarget();
            if (target != null && target.isAlive()) {
                double distance = candicorn.distanceTo(target);
                double attackDistance = candicorn.getBbWidth() + target.getBbWidth() + 0.5F;
                candicorn.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
                if (candicorn.getAnimation() == ANIMATION_STAB) {
                    candicorn.getNavigation().stop();
                    if (candicorn.getAnimationTick() > 8 && candicorn.getAnimationTick() <= 12) {
                        if (target.hurt(target.damageSources().mobAttack(candicorn), (float) candicorn.getAttribute(Attributes.ATTACK_DAMAGE).getValue())) {
                            target.knockback(0.6F, candicorn.getX() - target.getX(), candicorn.getZ() - target.getZ());
                        }
                    }
                } else {
                    if (candicorn.isCharging()) {
                        chargeTimeout++;
                        candicorn.getNavigation().stop();
                        Vec3 sub = startChargeTargetVec.subtract(startChargeFromVec);
                        Vec3 delta = sub.normalize().scale(0.85);
                        candicorn.setDeltaMovement(candicorn.getDeltaMovement().scale(0.9).add(delta));
                        candicorn.setChargeYaw(Mth.wrapDegrees((float) (Mth.atan2(sub.z, sub.x) * (double) (180F / (float) Math.PI)) - 90.0F));
                        if ((distance < attackDistance || candicorn.distanceToSqr(startChargeTargetVec) < attackDistance * attackDistance) && candicorn.getChargeProgress(1.0F) == 1.0F && candicorn.hasLineOfSight(target)) {
                            chargeCooldown = 100;
                            candicorn.setCharging(false);
                        }
                        candicorn.setRunning(true);
                    } else if (!candicorn.isStaying() && distance > attackDistance && distance < 15.0D && chargeCooldown <= 0 && !candicorn.isCharging() && candicorn.hasLineOfSight(target)) {
                        candicorn.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
                        candicorn.setCharging(true);
                        startChargeTargetVec = target.position();
                        startChargeFromVec = candicorn.position();
                    } else if (distance < attackDistance && candicorn.getAnimation() == IAnimatedEntity.NO_ANIMATION && candicorn.hasLineOfSight(target) && !candicorn.isCharging()) {
                        candicorn.syncAnimation(ANIMATION_STAB);
                    } else if (!candicorn.isStaying() && distance > attackDistance) {
                        candicorn.getNavigation().moveTo(target, 1.0D);
                        candicorn.setRunning(true);
                    } else {
                        candicorn.setRunning(false);
                    }
                }
            }
        }
    }
}
