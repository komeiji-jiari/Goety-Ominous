package com.qiuyue.goetyominous.common.entities.ally.am;

import com.Polarice3.Goety.api.entities.IAutoRideable;
import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.config.MobsConfig;
import com.github.alexthe666.alexsmobs.entity.ISemiAquatic;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIFindWater;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAILeaveWater;
import com.github.alexthe666.alexsmobs.entity.ai.AquaticMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.SemiAquaticPathNavigator;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.qiuyue.goetyominous.config.AttributesConfig;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreathAirGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidType;

public class SkelewagServant extends Summoned implements IAnimatedEntity, ISemiAquatic, PlayerRideable, IAutoRideable {

    public static final Animation ANIMATION_STAB = Animation.create(10);
    public static final Animation ANIMATION_SLASH = Animation.create(25);
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(SkelewagServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> AUTONOMOUS = SynchedEntityData.defineId(SkelewagServant.class, EntityDataSerializers.BOOLEAN);
    private int animationTick;
    private Animation currentAnimation;
    public float prevOnLandProgress;
    public float onLandProgress;
    public SkelewagServant(EntityType<? extends Owned> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0.0F);
        this.moveControl = new AquaticMoveController(this, 1F);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.SkelewagServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.SkelewagServantFollowRange.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.SkelewagServantDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.SkelewagServantKnockbackResistance.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.SkelewagServantMovementSpeed.get());
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.SKELEWAG_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.SKELEWAG_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.SKELEWAG_HURT.get();
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new SemiAquaticPathNavigator(this, level);
    }

    private boolean isFollowingOwner() {
        return this.getTrueOwner() != null && this.isFollowing();
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(VARIANT, Integer.valueOf(0));
        this.entityData.define(AUTONOMOUS, Boolean.valueOf(false));
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Variant", this.getVariant());
        compound.putBoolean("AutoMode", this.isAutonomous());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setVariant(compound.getInt("Variant"));
        if (compound.contains("AutoMode")) {
            this.setAutonomous(compound.getBoolean("AutoMode"));
        }
    }

    // Goety's servant commands (DarkWand + OrderFocus) route "stay" through IServant.isStaying()
    // (a vanilla Mob flag), never through Mob.setOrderedToSit. Delegate the fish's sit-freeze state
    // to it so the stay command actually freezes the fish, instead of a custom flag nothing sets.
    // (Vanilla 1.20.1 Mob has no isSitting(), so this is a plain fish-only method, not an override.)
    public boolean isSitting() {
        return this.isStaying();
    }

    public void tick() {
        super.tick();
        this.prevOnLandProgress = onLandProgress;
        boolean onLand = !this.isInWaterOrBubble() && this.onGround();
        if (onLand && onLandProgress < 5F) {
            onLandProgress++;
        }
        if (!onLand && onLandProgress > 0F) {
            onLandProgress--;
        }

        float targetXRot = 0;
        if (this.getDeltaMovement().length() > 0.09) {
            targetXRot = -((float) (Mth.atan2(this.getDeltaMovement().y, this.getDeltaMovement().horizontalDistance()) * (double) Mth.RAD_TO_DEG));
        }
        if (targetXRot < this.getXRot() - 5) {
            targetXRot = this.getXRot() - 5;
        }
        if (targetXRot > this.getXRot() + 5) {
            targetXRot = this.getXRot() + 5;
        }
        this.setXRot(targetXRot);

        if (!this.level().isClientSide && this.getTarget() != null && this.distanceTo(this.getTarget()) < 2.0F + this.getTarget().getBbWidth()) {
            this.lookAt(this.getTarget(), 350, 200);
            if (this.getAnimation() == ANIMATION_STAB && this.getAnimationTick() == 7 && this.hasLineOfSight(this.getTarget())) {
                float f1 = this.getYRot() * Mth.DEG_TO_RAD;
                this.setDeltaMovement(this.getDeltaMovement().add(-Mth.sin(f1) * 0.02F, 0.0D, Mth.cos(f1) * 0.02F));
                this.getTarget().knockback(1F, this.getTarget().getX() - this.getX(), this.getTarget().getZ() - this.getZ());
                this.getTarget().hurt(this.getServantAttack(), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
            }
            if (this.getAnimation() == ANIMATION_SLASH && this.getAnimationTick() % 5 == 0 && this.getAnimationTick() > 0 && this.getAnimationTick() < 25 && this.hasLineOfSight(this.getTarget())) {
                for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getTarget().getBoundingBox().inflate(2.0D))) {
                    if (!entity.isPassengerOfSameVehicle(this) && entity != this && !entity.isAlliedTo(this)) {
                        entity.hurt(this.getServantAttack(), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.5F);
                    }
                }
            }
        }
        if (onLandProgress >= 5.0F && this.isVehicle()) {
            this.ejectPassengers();
        }
        if (!this.level().isClientSide && !this.isInWaterOrBubble()) {
            if (this.onGround() && random.nextFloat() < 0.2F) {
                this.setDeltaMovement(this.getDeltaMovement().add((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F, 0.5D, (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F));
                this.setYRot(this.random.nextFloat() * 360.0F);
                this.playSound(AMSoundRegistry.SKELEWAG_HURT.get(), this.getSoundVolume(), this.getVoicePitch());
            }
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public int getVariant() {
        return this.entityData.get(VARIANT);
    }

    public void setVariant(int variant) {
        this.entityData.set(VARIANT, Integer.valueOf(variant));
    }

    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        return level().getFluidState(pos).is(FluidTags.WATER) ? 10.0F + level.getLightLevelDependentMagicValue(pos) - 0.5F : super.getWalkTargetValue(pos, level());
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new BreathAirGoal(this));
        this.goalSelector.addGoal(2, new AnimalAIFindWater(this));
        this.goalSelector.addGoal(2, new AnimalAILeaveWater(this));
        this.goalSelector.addGoal(4, new SkelewagServantAIMelee(this));
        this.goalSelector.addGoal(5, new SkelewagServantAIRandomSwimming(this, 1.0D, 7));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
    }

    @Override
    public void followGoal() {
        this.goalSelector.addGoal(4, new Summoned.FollowOwnerWaterGoal(this, 1.0D, 10.0F, 2.0F));
    }

    public void travel(Vec3 travelVector) {
        if (this.isSitting()) {
            super.travel(Vec3.ZERO);
        } else if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
            if (this.getTarget() == null && !this.isFollowingOwner()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.005D, 0.0D));
            }
        } else {
            super.travel(travelVector);
        }
    }

    // Riding logic mirrors Goety's Gnasher: a Player (or non-autonomous rider) controls the fish,
    // while a Mob rider is only allowed when ServantRideAutonomous is off (the default).
    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        if (this.isNoAi()) {
            return null;
        }
        Entity first = this.getFirstPassenger();
        if (first instanceof Mob mob) {
            if (MobsConfig.ServantRideAutonomous.get()) {
                return null;
            }
            return mob;
        }
        if (first instanceof LivingEntity le) {
            if (this.isAutonomous()) {
                return null;
            }
            return le;
        }
        return null;
    }

    @Override
    public boolean isControlledByLocalInstance() {
        return this.isEffectiveAi();
    }

    @Override
    protected Vec3 getRiddenInput(Player player, Vec3 vec3) {
        float sideways = player.xxa * 0.5F;
        float forward = player.zza;
        if (forward <= 0.0F) {
            forward *= 0.25F;
        }
        Vec3 look = player.getLookAngle();
        double y = look.y;
        if (!this.isInWater()) {
            y = 0.0D;
        }
        return new Vec3(sideways, y, forward);
    }

    @Override
    protected void tickRidden(Player player, Vec3 vec3) {
        super.tickRidden(player, vec3);
        // Same math as the vanilla LivingEntity#getRiddenRotation (absent from 1.20.1): half pitch,
        // full yaw. This lets the player steer the fish's pitch by looking up/down.
        Vec2 rot = new Vec2(player.getXRot() * 0.5F, player.getYRot());
        this.setRot(rot.y, rot.x);
        this.yHeadRot = this.yBodyRot = this.yRotO = this.getYRot();
    }

    @Override
    protected float getRiddenSpeed(Player player) {
        float speed = (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.2F;
        return this.isInWater() ? speed : speed * 0.2F;
    }

    @Override
    public void setAutonomous(boolean autonomous) {
        this.entityData.set(AUTONOMOUS, autonomous);
    }

    @Override
    public boolean isAutonomous() {
        return this.entityData.get(AUTONOMOUS);
    }

    public void positionRider(Entity passenger, Entity.MoveFunction moveFunc) {
        if (this.hasPassenger(passenger)) {
            passenger.setYBodyRot(this.yBodyRot);
            Vec3 vec = new Vec3(0, this.getBbHeight() * 0.4F, 0).xRot(-this.getXRot() * Mth.DEG_TO_RAD).yRot(-this.getYRot() * Mth.DEG_TO_RAD);
            passenger.setPos(this.getX() + vec.x, this.getY() + vec.y + passenger.getMyRidingOffset(), this.getZ() + vec.z);
        }
    }

    @Override
    public boolean canBeRiddenUnderFluidType(FluidType type, Entity rider) {
        return true;
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        this.setVariant(this.getRandom().nextFloat() < 0.3F ? 1 : 0);
        // Spawn eggs create permanent servants. The summon-limited lifespan set in the
        // Stray/Wither variants' constructors is meant for summoned (focus) entities only,
        // and would otherwise expire a freshly-spawned egg entity within a minute or two.
        if (reason == MobSpawnType.SPAWN_EGG) {
            this.setHasLifespan(false);
            this.setLifespan(0);
        }
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public boolean canBreatheUnderwater() {
        return true;
    }

    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source.is(DamageTypes.DROWN) || super.isInvulnerableTo(source);
    }

    public boolean doHurtTarget(Entity entityIn) {
        if (this.getAnimation() == NO_ANIMATION) {
            this.setAnimation(this.random.nextBoolean() ? ANIMATION_STAB : ANIMATION_SLASH);
        }
        return true;
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        final ItemStack itemstack = player.getItemInHand(hand);
        final Item item = itemstack.getItem();
        if (item == Items.NAME_TAG) {
            return super.mobInteract(player, hand);
        }
        if (item.isEdible() && item.getFoodProperties() != null && item.getFoodProperties().isMeat() && this.getTrueOwner() == player && this.getHealth() < this.getMaxHealth()) {
            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
            this.heal(10);
            this.gameEvent(GameEvent.EAT);
            this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
            if (!this.level().isClientSide) {
                ((ServerLevel) this.level()).sendParticles(ParticleTypes.HEART,
                        this.getX(), this.getY() + this.getBbHeight() + 0.4D, this.getZ(),
                        6, 0.5D, 0.2D, 0.5D, 0.0D);
            }
            return InteractionResult.SUCCESS;
        }
        // Right-click to ride (owner only, not while sneaking). If someone else is already riding,
        // kick them off first. Wands are left to super.mobInteract so they can still command.
        if (!this.level().isClientSide && this.getTrueOwner() == player && !player.isCrouching()) {
            if (this.getFirstPassenger() != null && this.getFirstPassenger() != player) {
                this.getFirstPassenger().stopRiding();
                return InteractionResult.SUCCESS;
            }
            if (!(itemstack.getItem() instanceof IWand)) {
                player.setYRot(this.getYRot());
                player.setXRot(this.getXRot());
                player.startRiding(this);
                return InteractionResult.SUCCESS;
            }
        }
        InteractionResult type = super.mobInteract(player, hand);
        itemstack.interactLivingEntity(player, this, hand);
        return type;
    }

    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Override
    public boolean shouldEnterWater() {
        if (this.isFollowingOwner()) {
            return this.getTrueOwner().isInWater();
        }
        return this.getTarget() == null && !this.isSitting();
    }

    @Override
    public boolean shouldLeaveWater() {
        if (this.isFollowingOwner()) {
            return false;
        }
        return this.getTarget() != null && !this.getTarget().isInWater();
    }

    @Override
    public boolean shouldStopMoving() {
        return this.isSitting();
    }

    @Override
    public int getWaterSearchRange() {
        return 15;
    }

    @Override
    public Animation getAnimation() {
        return currentAnimation;
    }

    @Override
    public void setAnimation(Animation animation) {
        currentAnimation = animation;
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
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_SLASH, ANIMATION_STAB};
    }

    private class SkelewagServantAIMelee extends Goal {
        private final SkelewagServant fish;
        private boolean isCharging = false;

        public SkelewagServantAIMelee(SkelewagServant skelewag) {
            this.fish = skelewag;
        }

        @Override
        public boolean canUse() {
            return this.fish.getTarget() != null && !this.fish.isSitting();
        }

        public boolean canContinueToUse() {
            return this.fish.getTarget() != null && !this.fish.isSitting();
        }

        public void tick() {
            LivingEntity target = this.fish.getTarget();
            if (target != null) {
                double dist = this.fish.distanceTo(target);
                if (dist > 5) {
                    isCharging = true;
                }
                this.fish.getNavigation().moveTo(target, isCharging ? 1.3F : 0.8F);
                if (dist < 2.0F + 3.0F + target.getBbWidth() / 2) {
                    this.fish.setAnimation(isCharging ? ANIMATION_STAB : random.nextBoolean() ? ANIMATION_SLASH : ANIMATION_STAB);
                    isCharging = false;
                }
            }
        }

        public void stop() {
            isCharging = false;
        }
    }

    static class SkelewagServantAIRandomSwimming extends RandomStrollGoal {
        public SkelewagServantAIRandomSwimming(PathfinderMob creature, double speed, int chance) {
            super(creature, speed, chance, false);
        }

        public boolean canUse() {
            if (this.mob.isVehicle() || ((SkelewagServant) mob).isSitting() || mob.getTarget() != null || !this.mob.isInWater() && this.mob instanceof ISemiAquatic && !((ISemiAquatic) this.mob).shouldEnterWater()) {
                return false;
            } else {
                if (!this.forceTrigger) {
                    if (this.mob.getRandom().nextInt(this.interval) != 0) {
                        return false;
                    }
                }
                Vec3 vector3d = this.getPosition();
                if (vector3d == null) {
                    return false;
                } else {
                    this.wantedX = vector3d.x;
                    this.wantedY = vector3d.y;
                    this.wantedZ = vector3d.z;
                    this.forceTrigger = false;
                    return true;
                }
            }
        }

        @Nullable
        protected Vec3 getPosition() {
            if (this.mob.hasRestriction() && this.mob.distanceToSqr(Vec3.atCenterOf(this.mob.getRestrictCenter())) > this.mob.getRestrictRadius() * this.mob.getRestrictRadius()) {
                return DefaultRandomPos.getPosTowards(this.mob, 7, 3, Vec3.atBottomCenterOf(this.mob.getRestrictCenter()), 1);
            }
            if (this.mob.getRandom().nextFloat() < 0.3F) {
                Vec3 vector3d = findSurfaceTarget(this.mob, 15, 7);
                if (vector3d != null) {
                    return vector3d;
                }
            }
            Vec3 vector3d = DefaultRandomPos.getPos(this.mob, 7, 3);
            for (int i = 0; vector3d != null && !this.mob.level().getBlockState(AMBlockPos.fromVec3(vector3d)).isPathfindable(this.mob.level(), AMBlockPos.fromVec3(vector3d), PathComputationType.WATER) && i++ < 15; vector3d = DefaultRandomPos.getPos(this.mob, 10, 7)) {
            }
            return vector3d;
        }

        private boolean canJumpTo(BlockPos pos, int dx, int dz, int scale) {
            BlockPos blockpos = pos.offset(dx * scale, 0, dz * scale);
            return this.mob.level().getFluidState(blockpos).is(FluidTags.WATER) && !this.mob.level().getBlockState(blockpos).blocksMotion();
        }

        private boolean isAirAbove(BlockPos pos, int dx, int dz, int scale) {
            return this.mob.level().getBlockState(pos.offset(dx * scale, 1, dz * scale)).isAir() && this.mob.level().getBlockState(pos.offset(dx * scale, 2, dz * scale)).isAir();
        }

        private Vec3 findSurfaceTarget(PathfinderMob creature, int i, int i1) {
            BlockPos upPos = creature.blockPosition();
            while (creature.level().getFluidState(upPos).is(FluidTags.WATER)) {
                upPos = upPos.above();
            }
            if (isAirAbove(upPos.below(), 0, 0, 0) && canJumpTo(upPos.below(), 0, 0, 0)) {
                return new Vec3(upPos.getX() + 0.5F, upPos.getY() - 1F, upPos.getZ() + 0.5F);
            }
            return null;
        }
    }
}
