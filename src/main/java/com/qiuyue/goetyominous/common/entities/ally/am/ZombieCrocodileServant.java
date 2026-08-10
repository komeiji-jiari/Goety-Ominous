package com.qiuyue.goetyominous.common.entities.ally.am;

import com.Polarice3.Goety.common.entities.ally.AnimalSummon;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.github.alexthe666.alexsmobs.entity.EntityCrocodile;
import com.github.alexthe666.alexsmobs.entity.ISemiAquatic;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIFindWater;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAILeaveWater;
import com.github.alexthe666.alexsmobs.entity.ai.AquaticMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.SemiAquaticPathNavigator;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
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
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.BreathAirGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.ForgeEventFactory;

public class ZombieCrocodileServant extends Summoned implements IAnimatedEntity, ISemiAquatic {

    public static final Animation ANIMATION_LUNGE = EntityCrocodile.ANIMATION_LUNGE;
    public static final Animation ANIMATION_DEATHROLL = EntityCrocodile.ANIMATION_DEATHROLL;
    private static final EntityDataAccessor<Byte> CLIMBING = SynchedEntityData.defineId(ZombieCrocodileServant.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(ZombieCrocodileServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DESERT = SynchedEntityData.defineId(ZombieCrocodileServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> STUN_TICKS = SynchedEntityData.defineId(ZombieCrocodileServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> BASKING_TYPE = SynchedEntityData.defineId(ZombieCrocodileServant.class, EntityDataSerializers.INT);
    public float groundProgress = 0;
    public float prevGroundProgress = 0;
    public float swimProgress = 0;
    public float prevSwimProgress = 0;
    public float baskingProgress = 0;
    public float prevBaskingProgress = 0;
    public float grabProgress = 0;
    public float prevGrabProgress = 0;
    private int baskingTimer = 0;
    private int swimTimer = -1000;
    private int passengerTimer = 0;
    private boolean isLandNavigator;
    private boolean hasSpedUp = false;
    private int animationTick;
    private Animation currentAnimation;

    public ZombieCrocodileServant(EntityType<? extends Owned> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0.0F);
        switchNavigator(false);
        this.setBaskingType(random.nextInt(2));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.ZombieCrocodileServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.ZombieCrocodileServantFollowRange.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.ZombieCrocodileServantDamage.get())
                .add(Attributes.ARMOR, AttributesConfig.ZombieCrocodileServantArmor.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.ZombieCrocodileServantKnockbackResistance.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.ZombieCrocodileServantMovementSpeed.get());
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    public boolean isSunSensitive() {
        return true;
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.CROCODILE_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.CROCODILE_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.CROCODILE_HURT.get();
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("CrocodileSitting", this.isSitting());
        compound.putBoolean("Desert", this.isDesert());
        compound.putInt("BaskingStyle", this.getBaskingType());
        compound.putInt("BaskingTimer", this.baskingTimer);
        compound.putInt("SwimTimer", this.swimTimer);
        compound.putInt("StunTimer", this.getStunTicks());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setOrderedToSit(compound.getBoolean("CrocodileSitting"));
        this.setDesert(compound.getBoolean("Desert"));
        this.setBaskingType(compound.getInt("BaskingStyle"));
        this.baskingTimer = compound.getInt("BaskingTimer");
        this.swimTimer = compound.getInt("SwimTimer");
        this.setStunTicks(compound.getInt("StunTimer"));
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new SemiAquaticPathNavigator(this, level);
    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveControl = new MoveControl(this);
            this.isLandNavigator = true;
        } else {
            this.moveControl = new AquaticMoveController(this, 1F);
            this.isLandNavigator = false;
        }
    }

    private boolean isFollowingOwner() {
        return this.getTrueOwner() != null && this.isFollowing();
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SITTING, false);
        this.entityData.define(DESERT, false);
        this.entityData.define(CLIMBING, (byte) 0);
        this.entityData.define(STUN_TICKS, 0);
        this.entityData.define(BASKING_TYPE, 0);
    }

    public boolean isBesideClimbableBlock() {
        return (this.entityData.get(CLIMBING) & 1) != 0;
    }

    public void setBesideClimbableBlock(boolean climbing) {
        byte b0 = this.entityData.get(CLIMBING);
        if (climbing) {
            b0 = (byte) (b0 | 1);
        } else {
            b0 = (byte) (b0 & -2);
        }
        this.entityData.set(CLIMBING, b0);
    }

    public void tick() {
        super.tick();
        this.prevGroundProgress = groundProgress;
        this.prevSwimProgress = swimProgress;
        this.prevBaskingProgress = baskingProgress;
        this.prevGrabProgress = grabProgress;

        final boolean ground = !this.isInWater();
        final boolean groundAnimate = !this.isInWater();
        final boolean basking = groundAnimate && this.isSitting();
        final boolean grabbing = !this.getPassengers().isEmpty();

        if (!ground && this.isLandNavigator) {
            switchNavigator(false);
        }
        if (ground && !this.isLandNavigator) {
            switchNavigator(true);
        }

        if (groundAnimate) {
            if (this.groundProgress < 10F)
                this.groundProgress++;

            if (this.swimProgress > 0F)
                this.swimProgress--;
        } else {
            if (this.groundProgress > 0F)
                this.groundProgress--;

            if (this.swimProgress < 10F)
                this.swimProgress++;
        }

        if (basking) {
            if (this.baskingProgress < 10F)
                this.baskingProgress++;
        } else {
            if (this.baskingProgress > 0F)
                this.baskingProgress--;
        }

        if (grabbing) {
            if (this.grabProgress < 10F)
                this.grabProgress++;
        } else {
            if (this.grabProgress > 0F)
                this.grabProgress--;
        }

        if (this.getTarget() == null) {
            if (hasSpedUp) {
                hasSpedUp = false;
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.getBaseSpeed());
            }
        } else {
            if (!hasSpedUp) {
                hasSpedUp = true;
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.getBaseSpeed() + 0.03D);
            }
        }

        if (!this.level().isClientSide) {
            this.setBesideClimbableBlock(this.horizontalCollision);
        }
        if (baskingTimer < 0) {
            baskingTimer++;
        }
        if (passengerTimer > 0 && this.getPassengers().isEmpty()) {
            passengerTimer = 0;
        }
        if (!this.level().isClientSide) {
            if (isInWater()) {
                swimTimer++;
            } else {
                swimTimer--;
            }

            if (!this.isInWater() && this.onGround() && this.getTrueOwner() == null) {
                if (!this.isSitting() && baskingTimer == 0 && this.getTarget() == null && this.getNavigation().isDone()) {
                    this.setOrderedToSit(true);
                    this.baskingTimer = 1000 + random.nextInt(750);
                }
                if (this.isSitting() && (baskingTimer <= 0 || this.getTarget() != null || swimTimer < -1000)) {
                    this.setOrderedToSit(false);
                    this.baskingTimer = -2000 - random.nextInt(750);
                }
                if (this.isSitting() && baskingTimer > 0) {
                    baskingTimer--;
                }
            }
            if (this.getStunTicks() == 0 && this.isAlive() && this.getTarget() != null && this.getAnimation() == ANIMATION_LUNGE && (level().getDifficulty() != Difficulty.PEACEFUL || !(this.getTarget() instanceof Player)) && this.getAnimationTick() > 5 && this.getAnimationTick() < 9) {
                final float f1 = this.getYRot() * Mth.DEG_TO_RAD;
                this.setDeltaMovement(this.getDeltaMovement().add(-Mth.sin(f1) * 0.02F, 0.0D, Mth.cos(f1) * 0.02F));
                if (this.distanceTo(this.getTarget()) < 3.5F && this.hasLineOfSight(this.getTarget())) {
                    boolean flag = this.getTarget().isBlocking();
                    if (!flag) {
                        if (this.getTarget().getBbWidth() < this.getBbWidth() && this.getPassengers().isEmpty() && !this.getTarget().isShiftKeyDown()) {
                            this.getTarget().startRiding(this, true);
                        }
                    }
                    if (flag) {
                        if (this.getTarget() instanceof final Player player) {
                            this.damageShieldFor(player, (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
                        }
                        if (this.getStunTicks() == 0) {
                            this.setStunTicks(25 + random.nextInt(20));
                        }
                    } else {
                        this.getTarget().hurt(this.damageSources().mobAttack(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
                    }
                    this.playSound(AMSoundRegistry.CROCODILE_BITE.get(), this.getSoundVolume(), this.getVoicePitch());
                }
            }
            if (this.isAlive() && this.getTarget() != null && this.isInWater() && (level().getDifficulty() != Difficulty.PEACEFUL || !(this.getTarget() instanceof Player))) {
                if (this.getTarget().getVehicle() != null && this.getTarget().getVehicle() == this) {
                    if (this.getAnimation() == NO_ANIMATION) {
                        this.setAnimation(ANIMATION_DEATHROLL);
                    }
                    if (this.getAnimation() == ANIMATION_DEATHROLL && this.getAnimationTick() % 10 == 0 && this.distanceTo(this.getTarget()) < 5D) {
                        this.getTarget().hurt(this.damageSources().mobAttack(this), 5);
                    }
                }
            }
        }
        if (this.getAnimation() == ANIMATION_DEATHROLL) {
            this.getNavigation().stop();
        }
        if (this.getStunTicks() > 0) {
            this.setStunTicks(this.getStunTicks() - 1);
            if (this.level().isClientSide) {
                final float angle = (Maths.STARTING_ANGLE * this.yBodyRot);
                final double headX = 1.5F * getScale() * Mth.sin(Mth.PI + angle);
                final double headZ = 1.5F * getScale() * Mth.cos(angle);
                for (int i = 0; i < 5; i++) {
                    final float innerAngle = (Maths.STARTING_ANGLE * (this.yBodyRot + tickCount * 5) * (i + 1));
                    final double extraX = 0.5F * Mth.sin((float) (Math.PI + innerAngle));
                    final double extraZ = 0.5F * Mth.cos(innerAngle);
                    level().addParticle(ParticleTypes.CRIT, true, this.getX() + headX + extraX, this.getEyeY() + 0.5F, this.getZ() + headZ + extraZ, 0, 0, 0);
                }
            }
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    private double getBaseSpeed() {
        return AttributesConfig.ZombieCrocodileServantMovementSpeed.get();
    }

    protected void damageShieldFor(Player holder, float damage) {
        if (holder.getUseItem().canPerformAction(ToolActions.SHIELD_BLOCK)) {
            if (!this.level().isClientSide) {
                holder.awardStat(Stats.ITEM_USED.get(holder.getUseItem().getItem()));
            }
            if (damage >= 3.0F) {
                int i = 1 + Mth.floor(damage);
                InteractionHand hand = holder.getUsedItemHand();
                holder.getUseItem().hurtAndBreak(i, holder, (p_213833_1_) -> {
                    p_213833_1_.broadcastBreakEvent(hand);
                    ForgeEventFactory.onPlayerDestroyItem(holder, holder.getUseItem(), hand);
                });
                if (holder.getUseItem().isEmpty()) {
                    if (hand == InteractionHand.MAIN_HAND) {
                        holder.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    } else {
                        holder.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                    }
                    holder.playSound(SoundEvents.SHIELD_BREAK, 0.8F, 0.8F + this.level().random.nextFloat() * 0.4F);
                }
            }
        }
    }

    protected boolean isImmobile() {
        return super.isImmobile() || this.getStunTicks() > 0;
    }

    @Override
    public boolean canRiderInteract() {
        return true;
    }

    public boolean shouldRiderSit() {
        return false;
    }

    public void positionRider(Entity passenger, Entity.MoveFunction moveFunc) {
        if (!this.getPassengers().isEmpty()) {
            this.yBodyRot = Mth.wrapDegrees(this.getYRot() - 180F);
        }
        if (this.hasPassenger(passenger)) {
            final float radius = 2F;
            final float angle = (Maths.STARTING_ANGLE * this.yBodyRot);
            final double extraX = radius * Mth.sin(Mth.PI + angle);
            final double extraZ = radius * Mth.cos(angle);
            passenger.setPos(this.getX() + extraX, this.getY() + 0.1F, this.getZ() + extraZ);
            passengerTimer++;
            if (this.isAlive() && passengerTimer > 0 && passengerTimer % 40 == 0) {
                passenger.hurt(this.damageSources().mobAttack(this), 2);
            }
        }
    }

    @Nullable
    public LivingEntity getControllingPassenger() {
        return null;
    }

    public boolean onClimbable() {
        return isInWater() && this.isBesideClimbableBlock();
    }

    public boolean isPushedByFluid() {
        return false;
    }

    public boolean doHurtTarget(Entity entityIn) {
        if (this.getAnimation() == NO_ANIMATION && this.getPassengers().isEmpty() && this.getStunTicks() == 0) {
            this.setAnimation(ANIMATION_LUNGE);
        }
        return true;
    }

    public void travel(Vec3 travelVector) {
        if (isSitting()) {
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

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source.is(DamageTypes.DROWN) || source.is(DamageTypes.IN_WALL) || super.isInvulnerableTo(source);
    }

    public boolean canBreatheUnderwater() {
        return true;
    }

    public boolean shouldLeaveWater() {
        if (!this.getPassengers().isEmpty()) {
            return false;
        }
        if (this.isFollowingOwner() && !this.getTrueOwner().isInWater()) {
            return true;
        }
        if (this.getTarget() != null && !this.getTarget().isInWater()) {
            return true;
        }
        return swimTimer > 600;
    }

    @Override
    public boolean shouldStopMoving() {
        return this.getAnimation() == ANIMATION_DEATHROLL || this.isSitting();
    }

    @Override
    public int getWaterSearchRange() {
        return this.getPassengers().isEmpty() ? 15 : 45;
    }

    public boolean isSitting() {
        return this.entityData.get(SITTING);
    }

    public void setOrderedToSit(boolean sit) {
        this.entityData.set(SITTING, Boolean.valueOf(sit));
    }

    public boolean isDesert() {
        return this.entityData.get(DESERT);
    }

    public void setDesert(boolean desert) {
        this.entityData.set(DESERT, Boolean.valueOf(desert));
    }

    public boolean isBiomeDesert(LevelAccessor world, BlockPos pos) {
        return world.getBiome(pos).is(AMTagRegistry.SPAWNS_DESERT_CROCODILES);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        this.setDesert(this.isBiomeDesert(worldIn, this.blockPosition()));
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public int getStunTicks() {
        return this.entityData.get(STUN_TICKS);
    }

    private void setStunTicks(int stun) {
        this.entityData.set(STUN_TICKS, stun);
    }

    public int getBaskingType() {
        return this.entityData.get(BASKING_TYPE);
    }

    public void setBaskingType(int baskingType) {
        this.entityData.set(BASKING_TYPE, baskingType);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new BreathAirGoal(this));
        this.goalSelector.addGoal(2, new AnimalAIFindWater(this));
        this.goalSelector.addGoal(2, new AnimalAILeaveWater(this));
        this.goalSelector.addGoal(4, new ZombieCrocodileServantAIMelee(this, 1, true));
        this.goalSelector.addGoal(5, new ZombieCrocodileServantAIRandomSwimming(this, 1.0D, 7));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
    }

    @Override
    public void followGoal() {
        this.goalSelector.addGoal(4, new Summoned.FollowOwnerWaterGoal(this, 1.0D, 10.0F, 2.0F));
    }

    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            Entity entity = source.getEntity();
            this.setOrderedToSit(false);
            if (entity != null && this.getTrueOwner() != null && !(entity instanceof Player) && !(entity instanceof AbstractArrow)) {
                amount = (amount + 1.0F) / 3.0F;
            }
            return super.hurt(source, amount);
        }
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
            return InteractionResult.SUCCESS;
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
        if (!this.getPassengers().isEmpty()) {
            return true;
        }
        if (this.isFollowingOwner() && !this.getTrueOwner().isInWater()) {
            return false;
        }
        return this.getTarget() == null && !this.isSitting() && this.baskingTimer <= 0 && !shouldLeaveWater() && swimTimer <= -1000;
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
        return new Animation[]{ANIMATION_LUNGE, ANIMATION_DEATHROLL};
    }

    public boolean isCrowned() {
        String s = net.minecraft.ChatFormatting.stripFormatting(this.getName().getString());
        return s != null && (s.toLowerCase().contains("crown") || s.toLowerCase().contains("king") || s.toLowerCase().contains("rool"));
    }

    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        return this.getType().getDimensions();
    }

    static class ZombieCrocodileServantAIMelee extends MeleeAttackGoal {
        private final ZombieCrocodileServant crocodile;

        public ZombieCrocodileServantAIMelee(ZombieCrocodileServant crocodile, double speedIn, boolean useLongMemory) {
            super(crocodile, speedIn, useLongMemory);
            this.crocodile = crocodile;
        }

        public boolean canUse() {
            return super.canUse() && crocodile.getPassengers().isEmpty();
        }

        public boolean canContinueToUse() {
            return super.canContinueToUse() && crocodile.getPassengers().isEmpty();
        }

        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
            double d0 = this.getAttackReachSqr(enemy);
            if (distToEnemySqr <= d0) {
                this.resetAttackCooldown();
                this.mob.swing(InteractionHand.MAIN_HAND);
                this.mob.doHurtTarget(enemy);
            }
        }
    }

    static class ZombieCrocodileServantAIRandomSwimming extends RandomStrollGoal {
        public ZombieCrocodileServantAIRandomSwimming(PathfinderMob creature, double speed, int chance) {
            super(creature, speed, chance, false);
        }

        public boolean canUse() {
            if (this.mob.isVehicle() || ((ZombieCrocodileServant) mob).isSitting() || mob.getTarget() != null || !this.mob.isInWater() && this.mob instanceof ISemiAquatic && !((ISemiAquatic) this.mob).shouldEnterWater()) {
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
