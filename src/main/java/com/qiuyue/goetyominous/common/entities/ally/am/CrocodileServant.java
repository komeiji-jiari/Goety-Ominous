package com.qiuyue.goetyominous.common.entities.ally.am;

import com.Polarice3.Goety.common.entities.ally.AnimalSummon;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.github.alexthe666.alexsmobs.entity.EntityCrocodile;
import com.github.alexthe666.alexsmobs.entity.ISemiAquatic;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIFindWater;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAILeaveWater;
import com.github.alexthe666.alexsmobs.entity.ai.AquaticMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.GroundPathNavigatorWide;
import com.github.alexthe666.alexsmobs.entity.ai.SemiAquaticPathNavigator;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.qiuyue.goetyominous.common.init.am.AmEntityRegistry;
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
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.BreathAirGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.ForgeEventFactory;

/**
 * 鳄鱼仆从，移植 AlexMobs 原版 EntityCrocodile 的水陆两栖/扑咬/死亡翻滚行为。
 *
 * 与 TusklinServant 同一套模式：继承 Goety 的 AnimalSummon（可繁殖）。
 * 动画常量直接复用 EntityCrocodile.ANIMATION_* 实例——RenderCrocodileServant /
 * ModelCrocodileServant 内部用 `getAnimation() == EntityCrocodile.ANIMATION_*`
 * 做对象恒等比较，若这里自己 create 新实例，恒等比较永远不成立，动画将无法播放。
 *
 * 由于 CrocodileServant 并非 EntityCrocodile，原版里对 EntityCrocodile 硬转型的
 * CrocodileAIMelee / CrocodileAIRandomSwimming 不能直接用，改写为内部静态类；
 * AnimalAIFindWater / AnimalAILeaveWater / AquaticMoveController /
 * SemiAquaticPathNavigator / GroundPathNavigatorWide 只依赖 ISemiAquatic / PathfinderMob，
 * 可直接复用。
 */
public class CrocodileServant extends AnimalSummon implements IAnimatedEntity, ISemiAquatic {

    public static final Animation ANIMATION_LUNGE = EntityCrocodile.ANIMATION_LUNGE;
    public static final Animation ANIMATION_DEATHROLL = EntityCrocodile.ANIMATION_DEATHROLL;
    private static final EntityDataAccessor<Byte> CLIMBING = SynchedEntityData.defineId(CrocodileServant.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(CrocodileServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DESERT = SynchedEntityData.defineId(CrocodileServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> STUN_TICKS = SynchedEntityData.defineId(CrocodileServant.class, EntityDataSerializers.INT);
    /** 以下进度字段供 ModelCrocodileServant 的 setupAnim 读取 */
    public float groundProgress = 0;
    public float prevGroundProgress = 0;
    public float swimProgress = 0;
    public float prevSwimProgress = 0;
    public float baskingProgress = 0;
    public float prevBaskingProgress = 0;
    public float grabProgress = 0;
    public float prevGrabProgress = 0;
    public int baskingType = 0;
    public boolean forcedSit = false;
    private int baskingTimer = 0;
    private int swimTimer = -1000;
    private int passengerTimer = 0;
    private boolean isLandNavigator;
    private boolean hasSpedUp = false;
    private int animationTick;
    private Animation currentAnimation;

    public CrocodileServant(EntityType<? extends Owned> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0.0F);
        switchNavigator(false);
        this.baskingType = random.nextInt(1);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.CrocodileServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.CrocodileServantFollowRange.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.CrocodileServantDamage.get())
                .add(Attributes.ARMOR, AttributesConfig.CrocodileServantArmor.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.CrocodileServantKnockbackResistance.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.CrocodileServantMovementSpeed.get());
    }

    protected SoundEvent getAmbientSound() {
        return isBaby() ? AMSoundRegistry.CROCODILE_BABY.get() : AMSoundRegistry.CROCODILE_IDLE.get();
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
        compound.putBoolean("ForcedToSit", this.forcedSit);
        compound.putInt("BaskingStyle", this.baskingType);
        compound.putInt("BaskingTimer", this.baskingTimer);
        compound.putInt("SwimTimer", this.swimTimer);
        compound.putInt("StunTimer", this.getStunTicks());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setOrderedToSit(compound.getBoolean("CrocodileSitting"));
        this.setDesert(compound.getBoolean("Desert"));
        this.forcedSit = compound.getBoolean("ForcedToSit");
        this.baskingType = compound.getInt("BaskingStyle");
        this.baskingTimer = compound.getInt("BaskingTimer");
        this.swimTimer = compound.getInt("SwimTimer");
        this.setStunTicks(compound.getInt("StunTimer"));
    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveControl = new MoveControl(this);
            this.navigation = new GroundPathNavigatorWide(this, level());
            this.isLandNavigator = true;
        } else {
            this.moveControl = new AquaticMoveController(this, 1F);
            this.navigation = new SemiAquaticPathNavigator(this, level());
            this.isLandNavigator = false;
        }
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SITTING, false);
        this.entityData.define(DESERT, false);
        this.entityData.define(CLIMBING, (byte) 0);
        this.entityData.define(STUN_TICKS, 0);
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

        // 追击目标时小幅加速，原版为 0.25 -> 0.28，这里基准取配置值
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

            // 未认主的鳄鱼才会在岸边自动趴窝晒太阳；仆从不会自动坐下
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
            // 扑咬（Lunge）：向前冲一段并把体型较小的目标叼到背上
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
            // 死亡翻滚（Death Roll）：水下对背上叼着的目标持续伤害
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
        if (this.isInLove() && this.getTarget() != null) {
            this.setTarget(null);
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
        return AttributesConfig.CrocodileServantMovementSpeed.get();
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
        // 伤害由 tick() 里的扑咬/翻滚判定打出（与 TusklinServant 一致），
        // 不再调用 super.doHurtTarget，避免与 tick 的伤害叠加。
        return true;
    }

    public void travel(Vec3 travelVector) {
        if (isSitting()) {
            super.travel(Vec3.ZERO);
        } else if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
            if (this.getTarget() == null) {
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

    public int getStunTicks() {
        return this.entityData.get(STUN_TICKS);
    }

    private void setStunTicks(int stun) {
        this.entityData.set(STUN_TICKS, stun);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(1, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(2, new BreathAirGoal(this));
        this.goalSelector.addGoal(2, new AnimalAIFindWater(this));
        this.goalSelector.addGoal(2, new AnimalAILeaveWater(this));
        this.goalSelector.addGoal(4, new CrocodileServantAIMelee(this, 1, true));
        this.goalSelector.addGoal(5, new CrocodileServantAIRandomSwimming(this, 1.0D, 7));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.targetSelector.addGoal(0, new HurtByTargetGoal(this));
    }

    public void setTarget(@Nullable LivingEntity entitylivingbaseIn) {
        if (!this.isBaby()) {
            super.setTarget(entitylivingbaseIn);
        }
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

    @Nullable
    @Override
    public AnimalSummon getBreedOffspring(ServerLevel pLevel, AnimalSummon pOtherParent) {
        CrocodileServant baby = AmEntityRegistry.CROCODILE_SERVANT.get().create(pLevel);
        if (baby != null) {
            baby.setPersistenceRequired();
        }
        return baby;
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        final ItemStack itemstack = player.getItemInHand(hand);
        final Item item = itemstack.getItem();
        if (item == Items.NAME_TAG) {
            return super.mobInteract(player, hand);
        }
        // 喂肉治疗（仅主人）
        if (item.isEdible() && item.getFoodProperties() != null && item.getFoodProperties().isMeat() && this.getTrueOwner() == player && this.getHealth() < this.getMaxHealth()) {
            this.usePlayerItem(player, hand, itemstack);
            this.heal(10);
            this.gameEvent(GameEvent.EAT);
            this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
            return InteractionResult.SUCCESS;
        }
        // 繁殖/催熟（仅主人）
        if (isFood(itemstack) && this.getTrueOwner() == player) {
            if (this.isBaby()) {
                this.usePlayerItem(player, hand, itemstack);
                this.ageUp(this.getSpeedUpSecondsWhenFeeding(-this.getAge()), true);
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            } else if (this.getAge() >= 0 && this.canFallInLove()) {
                this.usePlayerItem(player, hand, itemstack);
                this.setInLove(player);
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
            return InteractionResult.SUCCESS;
        }
        InteractionResult type = super.mobInteract(player, hand);
        InteractionResult interactionresult = itemstack.interactLivingEntity(player, this, hand);
        // 坐下/站起（仅主人）
        if (interactionresult != InteractionResult.SUCCESS && type != InteractionResult.SUCCESS && this.getTrueOwner() == player && !isFood(itemstack)) {
            if (this.isSitting()) {
                this.forcedSit = false;
                this.setOrderedToSit(false);
            } else {
                this.forcedSit = true;
                this.setOrderedToSit(true);
            }
            return InteractionResult.SUCCESS;
        }
        return type;
    }

    public boolean isFood(ItemStack stack) {
        return stack.is(AMTagRegistry.CROCODILE_BREEDABLES);
    }

    @Override
    public boolean shouldEnterWater() {
        if (!this.getPassengers().isEmpty()) {
            return true;
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

    /**
     * 幼崽碰撞箱跟随模型 young 分支（整体 0.15 缩放、头 1.5 倍），避免箱体远大于渲染。
     */
    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        return this.getType().getDimensions().scale(this.isBaby() ? 0.15F : 1.0F);
    }

    @Override
    protected void ageBoundaryReached() {
        super.ageBoundaryReached();
        this.refreshDimensions();
    }

    /**
     * 原版 CrocodileAIMelee 构造器强类型 EntityCrocodile 且目标判定用到
     * getPassengers()，直接复用会对 CrocodileServant 抛 ClassCastException，这里改写。
     */
    static class CrocodileServantAIMelee extends MeleeAttackGoal {
        private final CrocodileServant crocodile;

        public CrocodileServantAIMelee(CrocodileServant crocodile, double speedIn, boolean useLongMemory) {
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

    /**
     * 原版 CrocodileAIRandomSwimming 的 canUse 里对 mob 强转 EntityCrocodile 调用
     * isSitting()，直接复用会抛 ClassCastException，这里把转型改成 CrocodileServant。
     */
    static class CrocodileServantAIRandomSwimming extends RandomStrollGoal {
        public CrocodileServantAIRandomSwimming(PathfinderMob creature, double speed, int chance) {
            super(creature, speed, chance, false);
        }

        public boolean canUse() {
            if (this.mob.isVehicle() || ((CrocodileServant) mob).isSitting() || mob.getTarget() != null || !this.mob.isInWater() && this.mob instanceof ISemiAquatic && !((ISemiAquatic) this.mob).shouldEnterWater()) {
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
