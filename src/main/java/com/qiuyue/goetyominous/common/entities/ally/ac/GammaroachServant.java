package com.qiuyue.goetyominous.common.entities.ally.ac;

import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.init.ModMobType;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ai.GroundPathNavigatorNoSpin;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class GammaroachServant extends Summoned implements IAnimatedEntity, PlayerRideable {

    private Animation currentAnimation;
    private int animationTick;
    public static final Animation ANIMATION_SPRAY = Animation.create(40);
    public static final Animation ANIMATION_RAM = Animation.create(25);

    private static final EntityDataAccessor<Boolean> FED = SynchedEntityData.defineId(GammaroachServant.class, EntityDataSerializers.BOOLEAN);

    public GammaroachServant(EntityType<? extends Summoned> entityType, Level level) {
        super(entityType, level);
        this.setMaxUpStep(1.1F);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new DriveRiddenGoal());
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeGoal());
        this.goalSelector.addGoal(3, new Summoned.WanderGoal<>(this, 1.0D, 45, 0.001F));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 15.0F));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.GammaroachServantMovementSpeed.get())
                .add(Attributes.MAX_HEALTH, AttributesConfig.GammaroachServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.GammaroachServantFollowRange.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.GammaroachServantDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.GammaroachServantKnockbackResistance.get())
                .add(Attributes.ARMOR, AttributesConfig.GammaroachServantArmor.get());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FED, false);
    }

    public boolean isFed() {
        return this.entityData.get(FED);
    }

    public void setFed(boolean fed) {
        this.entityData.set(FED, fed);
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData,
                                        @Nullable CompoundTag tag) {
        if (this.getTrueOwner() instanceof Player player) {
            if (countServants(player) >= MobsConfig.GammaroachServantLimit.get()) {
                this.discard();
                return null;
            }
        }
        return super.finalizeSpawn(levelAccessor, difficulty, spawnType, spawnGroupData, tag);
    }

    private int countServants(Player player) {
        int count = 0;
        if (player.level() instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof GammaroachServant servant && servant != this) {
                    if (servant.getTrueOwner() == player) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    protected PathNavigation createNavigation(Level level) {
        return new GroundPathNavigatorNoSpin(this, level);
    }

    @Override
    public boolean isControlledByLocalInstance() {
        return this.isEffectiveAi();
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        Entity entity = this.getFirstPassenger();
        if (entity instanceof Player) {
            return (Player) entity;
        }
        if (entity instanceof IServant servant && (this.getTrueOwner() == null || this.getTrueOwner() == servant.getTrueOwner())) {
            return (LivingEntity) entity;
        }
        return null;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        if (this.isBaby() || !this.getPassengers().isEmpty()) {
            return false;
        }
        if (passenger instanceof Player) {
            return true;
        }
        return passenger instanceof IServant
                && (this.getTrueOwner() == null || this.getTrueOwner() == ((IServant) passenger).getTrueOwner());
    }

    @Override
    public double getPassengersRidingOffset() {
        return 0.5D;
    }

    @Override
    protected void updateControlFlags() {
        super.updateControlFlags();
        boolean steering = this.getControllingPassenger() instanceof Player player && (player.zza != 0.0F || player.xxa != 0.0F);
        boolean busy = this.getControllingPassenger() instanceof IServant || steering;
        boolean notInBoat = !(this.getVehicle() instanceof Boat);
        this.goalSelector.setControlFlag(Goal.Flag.MOVE, !busy);
        this.goalSelector.setControlFlag(Goal.Flag.JUMP, !busy && notInBoat);
        this.goalSelector.setControlFlag(Goal.Flag.LOOK, !busy);
        this.goalSelector.setControlFlag(Goal.Flag.TARGET, !busy);
    }

    protected void doPlayerRide(Player player) {
        if (!this.level().isClientSide) {
            player.setYRot(this.getYRot());
            player.setXRot(this.getXRot());
            player.startRiding(this);
        }
    }

    @Override
    public void positionRider(Entity passenger, Entity.MoveFunction moveFunction) {
        if (this.isPassengerOfSameVehicle(passenger) && passenger instanceof LivingEntity living) {
            if (!this.touchingUnloadedChunk()) {
                living.setYBodyRot(this.yBodyRot);
                living.setYHeadRot(this.getYRot());
                living.fallDistance = 0.0F;
                Vec3 seatOffset = new Vec3(0.0D, 0.0D, 0.2D).yRot((float) Math.toRadians(-this.yBodyRot));
                moveFunction.accept(passenger, this.getX() + seatOffset.x, this.getY() + seatOffset.y + this.getPassengersRidingOffset(), this.getZ() + seatOffset.z);
                return;
            }
        }
        super.positionRider(passenger, moveFunction);
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        return new Vec3(this.getX(), this.getBoundingBox().minY, this.getZ());
    }

    @Override
    protected Vec3 getRiddenInput(Player player, Vec3 vec3) {
        float f = player.zza < 0.0F ? 0.5F : 1.0F;
        return new Vec3(player.xxa * 0.35F, 0.0D, player.zza * 0.8F * f);
    }

    @Override
    protected void tickRidden(Player player, Vec3 vec3) {
        super.tickRidden(player, vec3);
        if (player.zza != 0.0F || player.xxa != 0.0F) {
            this.setRot(player.getYRot(), player.getXRot() * 0.25F);
            this.yBodyRot = this.yHeadRot = this.getYRot();
            this.yRotO = this.yHeadRot;
            this.getNavigation().stop();
            this.setTarget(null);
        }
    }

    @Override
    protected float getRiddenSpeed(Player rider) {
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);
    }

    public boolean canBeAffected(MobEffectInstance effectInstance) {
        return super.canBeAffected(effectInstance) && effectInstance.getEffect() != ACEffectRegistry.IRRADIATED.get();
    }

    public void tick() {
        super.tick();
        if (this.getAnimation() == ANIMATION_SPRAY) {
            if (this.getAnimationTick() == 10) {
                AreaEffectCloud areaeffectcloud = new AreaEffectCloud(this.level(), this.getX(), this.getY() + 0.2F, this.getZ());
                areaeffectcloud.setParticle(ACParticleRegistry.GAMMAROACH.get());
                areaeffectcloud.setFixedColor(0X77D60E);
                areaeffectcloud.addEffect(new MobEffectInstance(ACEffectRegistry.IRRADIATED.get(), 2000, 2));
                areaeffectcloud.setRadius(2.3F);
                areaeffectcloud.setDuration(200);
                areaeffectcloud.setWaitTime(10);
                areaeffectcloud.setRadiusPerTick(-areaeffectcloud.getRadius() / (float) areaeffectcloud.getDuration());
                this.level().addFreshEntity(areaeffectcloud);
            } else if (this.getAnimationTick() >= 10 && this.getAnimationTick() <= 30) {
                Vec3 randomOffset = new Vec3(random.nextFloat() - 0.5F, random.nextFloat() - 0.5F, random.nextFloat() - 0.5F).normalize().scale(1).add(this.getEyePosition());
                this.level().addParticle(ACParticleRegistry.GAMMAROACH.get(), this.getRandomX(2), this.getEyeY(), this.getRandomZ(2), randomOffset.x, randomOffset.y + 0.23D, randomOffset.z);

            }
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public MobType getMobType() {
        return ModMobType.NATURAL;
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
        return new Animation[]{ANIMATION_SPRAY, ANIMATION_RAM};
    }

    public void triggerSpraying() {
        if (this.getAnimation() != ANIMATION_SPRAY) {
            this.playSound(ACSoundRegistry.GAMMAROACH_SPRAY.get());
            this.syncAnimation(ANIMATION_SPRAY);
        }
    }

    public void travel(Vec3 vec3d) {
        if (this.getAnimation() == ANIMATION_RAM || this.getAnimation() == ANIMATION_SPRAY) {
            vec3d = Vec3.ZERO;
        }
        super.travel(vec3d);
    }

    public void calculateEntityAnimation(boolean flying) {
        float f1 = (float) Mth.length(this.getX() - this.xo, flying ? this.getY() - this.yo : 0, this.getZ() - this.zo);
        float f2 = Math.min(f1 * 8.0F, 1.0F);
        this.walkAnimation.update(f2, 0.4F);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide) {
            ItemStack itemStack = player.getItemInHand(hand);
            InteractionResult prev = super.mobInteract(player, hand);
            if (prev == InteractionResult.SUCCESS) {
                return prev;
            }
            if (itemStack.is(ACItemRegistry.SPELUNKIE.get()) && (this.getTarget() == player || !isFed())) {
                if (!player.getAbilities().instabuild) {
                    itemStack.shrink(1);
                }
                this.setFed(true);
                this.setLastHurtByMob(null);
                this.setTarget(null);
                this.level().broadcastEntityEvent(this, (byte) 49);
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.HEART,
                            this.getX(), this.getY() + this.getBbHeight() / 2, this.getZ(),
                            5, 0.5, 0.5, 0.5, 0.0);
                }
                return InteractionResult.SUCCESS;
            }
            if (this.getTrueOwner() != null && player == this.getTrueOwner() && !this.isBaby() && !player.isCrouching()
                    && !itemStack.is(ACItemRegistry.SPELUNKIE.get()) && !(itemStack.getItem() instanceof IWand)) {
                Entity passenger = this.getFirstPassenger();
                if (passenger != null && passenger != player) {
                    passenger.stopRiding();
                    return InteractionResult.SUCCESS;
                }
                if (this.getPassengers().isEmpty()) {
                    this.doPlayerRide(player);
                    return InteractionResult.SUCCESS;
                }
            }
            return prev;
        }
        return super.mobInteract(player, hand);
    }

    public void handleEntityEvent(byte b) {
        if (b == 49) {
            ItemStack itemstack = new ItemStack(ACItemRegistry.SPELUNKIE.get());
            for (int i = 0; i < 8; ++i) {
                Vec3 headPos = (new Vec3(0D, 0.1D, 0.5D)).xRot(-this.getXRot() * ((float) Math.PI / 180F)).yRot(-this.yBodyRot * ((float) Math.PI / 180F));
                this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, itemstack), this.getX() + headPos.x, this.getY(0.5) + headPos.y, this.getZ() + headPos.z, (random.nextFloat() - 0.5F) * 0.1F, random.nextFloat() * 0.15F, (random.nextFloat() - 0.5F) * 0.1F);
            }
        } else {
            super.handleEntityEvent(b);
        }
    }

    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.GAMMAROACH_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.GAMMAROACH_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.GAMMAROACH_DEATH.get();
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
        if (!this.isBaby()) {
            this.playSound(ACSoundRegistry.GAMMAROACH_STEP.get(), 1.0F, 1.0F);
        }
    }

    private class DriveRiddenGoal extends Goal {

        private DriveRiddenGoal() {
        }

        @Override
        public boolean canUse() {
            return !GammaroachServant.this.level().isClientSide && GammaroachServant.this.getControllingPassenger() instanceof Summoned;
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse();
        }

        @Override
        public void start() {
            GammaroachServant.this.getNavigation().stop();
            GammaroachServant.this.setTarget(null);
        }

        @Override
        public void stop() {
            GammaroachServant.this.getNavigation().stop();
        }

        @Override
        public void tick() {
            GammaroachServant roach = GammaroachServant.this;
            if (!(roach.getControllingPassenger() instanceof Summoned driver)) {
                return;
            }
            if (driver.isStaying()) {
                roach.getNavigation().stop();
                return;
            }
            LivingEntity target = driver.getTarget();
            if (target != null && target.isAlive()) {
                double range = (double) (2.0F + target.getBbWidth());
                if (roach.distanceToSqr(target) > range * range) {
                    if (roach.getNavigation().isDone()) {
                        roach.getNavigation().moveTo(target, 1.0D);
                    }
                } else {
                    roach.getNavigation().stop();
                    roach.lookAt(target, 30.0F, 30.0F);
                }
                return;
            }
            if (driver.isCommanded() && driver.getCommandPos() != null) {
                BlockPos commandPos = driver.getCommandPos();
                if (roach.distanceToSqr(commandPos.getX() + 0.5D, commandPos.getY(), commandPos.getZ() + 0.5D) > 4.0D) {
                    if (roach.getNavigation().isDone()) {
                        roach.getNavigation().moveTo(commandPos.getX() + 0.5D, commandPos.getY(), commandPos.getZ() + 0.5D, 1.0D);
                    }
                } else {
                    roach.getNavigation().stop();
                }
                return;
            }
            if (driver.isWandering()) {
                if (roach.getNavigation().isDone() && roach.getRandom().nextInt(60) == 0) {
                    Vec3 pos = LandRandomPos.getPos(roach, 6, 4);
                    if (pos != null) {
                        roach.getNavigation().moveTo(pos.x, pos.y, pos.z, 0.7D);
                    }
                }
                return;
            }
            if (driver.isGuardingArea()) {
                roach.getNavigation().stop();
                return;
            }
            LivingEntity owner = driver.getTrueOwner();
            if (owner != null && owner.isAlive()) {
                if (roach.distanceToSqr(owner) > 25.0D) {
                    if (roach.getNavigation().isDone()) {
                        roach.getNavigation().moveTo(owner, 1.0D);
                    }
                } else {
                    roach.getNavigation().stop();
                }
                return;
            }
            roach.getNavigation().stop();
        }
    }

    private class MeleeGoal extends Goal {

        public MeleeGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = GammaroachServant.this.getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public void tick() {
            LivingEntity target = GammaroachServant.this.getTarget();
            if (target != null && target.isAlive()) {
                GammaroachServant.this.getNavigation().moveTo(target, 1.0D);
                GammaroachServant.this.lookAt(target, 180, 30);
                if (GammaroachServant.this.distanceTo(target) < 1.5F + target.getBbWidth()) {
                    if (GammaroachServant.this.getAnimation() == NO_ANIMATION) {
                        if (target.hasEffect(ACEffectRegistry.IRRADIATED.get())) {
                            GammaroachServant.this.syncAnimation(GammaroachServant.ANIMATION_RAM);
                        } else {
                            GammaroachServant.this.triggerSpraying();
                        }
                    } else if (GammaroachServant.this.getAnimation() == GammaroachServant.ANIMATION_RAM && GammaroachServant.this.getAnimationTick() > 8 && GammaroachServant.this.getAnimationTick() < 15) {
                        GammaroachServant.this.playSound(ACSoundRegistry.GAMMAROACH_ATTACK.get());
                        target.hurt(damageSources().mobAttack(GammaroachServant.this), (float) GammaroachServant.this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                    }
                }
            }
        }
    }
}
