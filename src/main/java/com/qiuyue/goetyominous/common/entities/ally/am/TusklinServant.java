package com.qiuyue.goetyominous.common.entities.ally.am;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.HoglinServant;
import com.Polarice3.Goety.common.entities.ally.AnimalSummon;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.EntityTusklin;
import com.github.alexthe666.alexsmobs.entity.ai.AdvancedPathNavigateNoTeleport;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIMeleeNearby;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.entity.ai.TameableAIRide;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import com.qiuyue.goetyominous.common.init.am.AmEntityRegistry;
import com.qiuyue.goetyominous.config.AttributesConfig;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class TusklinServant extends AnimalSummon implements IAnimatedEntity {

    // 复用 AlexMobs 原版 EntityTusklin 的 Animation 实例。RenderTusklin/ModelTusklin 内部用
    // `getAnimation() == EntityTusklin.ANIMATION_*` 做对象恒等比较，若这里自己 create 新实例，
    // 恒等比较永远不成立，冲刺/顶撞/拱地动画将无法播放。
    public static final Animation ANIMATION_RUT = EntityTusklin.ANIMATION_RUT;
    public static final Animation ANIMATION_GORE_L = EntityTusklin.ANIMATION_GORE_L;
    public static final Animation ANIMATION_GORE_R = EntityTusklin.ANIMATION_GORE_R;
    public static final Animation ANIMATION_FLING = EntityTusklin.ANIMATION_FLING;
    public static final Animation ANIMATION_BUCK = EntityTusklin.ANIMATION_BUCK;
    private int animationTick;
    private Animation currentAnimation;
    private int conversionTime = 0;

    public TusklinServant(EntityType<? extends Owned> type, Level level) {
        super(type, level);
        this.setMaxUpStep(1.1F);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.TusklinServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.TusklinServantFollowRange.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.TusklinServantDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.TusklinServantKnockbackResistance.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.TusklinServantMovementSpeed.get());
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.TUSKLIN_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.TUSKLIN_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.TUSKLIN_HURT.get();
    }

    @Override
    protected PathNavigation createNavigation(Level worldIn) {
        return new AdvancedPathNavigateNoTeleport(this, worldIn, true);
    }

    public boolean isInNether() {
        return this.level().dimension() == Level.NETHER && !this.isNoAi();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(1, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(2, new AnimalAIMeleeNearby(this, 15, 1.25D));
        this.goalSelector.addGoal(4, new TameableAIRide(this, 2D, false) {
            @Override
            public boolean shouldMoveForward() {
                return true;
            }

            @Override
            public boolean shouldMoveBackwards() {
                return false;
            }
        });
        this.goalSelector.addGoal(5, new AnimalAIWanderRanged(this, 120, 0.6F, 14, 7));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 15.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(0, new HurtByTargetGoal(this));
    }

    @Override
    protected Vec3 getRiddenInput(Player player, Vec3 deltaIn) {
        return new Vec3(0, 0, 1);
    }
    @Override
    protected void tickRidden(Player player, Vec3 vec3) {
        super.tickRidden(player, vec3);
        this.setRot(player.getYRot(), player.getXRot() * 0.25F);
        this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
        this.setMaxUpStep(1);
        this.getNavigation().stop();
        this.setTarget(null);
        this.setSprinting(true);
    }


    @Override
    protected float getRiddenSpeed(Player rider) {
        return (float)(this.getAttributeValue(Attributes.MOVEMENT_SPEED));
    }
    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        for (Entity passenger : this.getPassengers()) {
            if (passenger instanceof Player) {
                Player player = (Player) passenger;
                return player;
            }
        }
        return null;
    }

    @Override
    public boolean doHurtTarget(Entity entityIn) {
        if (this.getAnimation() == NO_ANIMATION) {
            final int anim = this.random.nextInt(3);
            switch (anim) {
                case 0 -> this.setAnimation(ANIMATION_FLING);
                case 1 -> this.setAnimation(ANIMATION_GORE_L);
                case 2 -> this.setAnimation(ANIMATION_GORE_R);
            }
        }
        return true;
    }

    @Override
    public void positionRider(Entity passenger, Entity.MoveFunction moveFunc) {
        if (this.hasPassenger(passenger)) {
            float radius = 0.4F;
            if (this.getAnimation() == ANIMATION_GORE_L || this.getAnimation() == ANIMATION_GORE_R) {
                if (this.getAnimationTick() <= 4) {
                    radius -= this.getAnimationTick() * 0.1F;
                } else {
                    radius -= -0.4F + Math.min(this.getAnimationTick() - 4, 4) * 0.1F;
                }
            }
            if (this.getAnimation() == ANIMATION_BUCK) {
                if (this.getAnimationTick() < 5) {
                    radius -= this.getAnimationTick() * 0.1F;
                } else if (this.getAnimationTick() < 10) {
                    radius -= 0.4F - (this.getAnimationTick() - 5) * 0.1F;
                }
            }
            final float angle = (Maths.STARTING_ANGLE * this.yBodyRot);
            final double extraX = radius * Mth.sin(Mth.PI + angle);
            final double extraZ = radius * Mth.cos(angle);
            passenger.setPos(this.getX() + extraX, this.getY() + this.getPassengersRidingOffset() + passenger.getMyRidingOffset(), this.getZ() + extraZ);
        }
    }

    @Override
    public double getPassengersRidingOffset() {
        final float f = this.walkAnimation.position();
        final float f1 = this.walkAnimation.speed();
        float f2 = 0;
        if (this.getAnimation() == ANIMATION_FLING) {
            if (this.getAnimationTick() <= 3F) {
                f2 = this.getAnimationTick() * -0.1F;
            } else {
                f2 = -0.3F + Mth.clamp(this.getAnimationTick() - 3, 0, 3) * 0.1F;
            }
        }
        if (this.getAnimation() == ANIMATION_BUCK) {
            if (this.getAnimationTick() < 5) {
                f2 = (this.getAnimationTick() * 0.2F) * 0.8F;
            } else if (this.getAnimationTick() < 10) {
                f2 = (0.8F - (this.getAnimationTick() - 5) * 0.2F) * 0.8F;
            }
        }
        return (double) this.getBbHeight() - 0.3D + (float) (Math.abs(Math.sin(f * 0.7F) * (double) f1 * 0.0625F * 1.6F)) + f2;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        if (item == AMItemRegistry.PIGSHOES.get() && this.getTrueOwner() == player && this.getShoeStack().isEmpty() && !this.isBaby()) {
            this.setShoeStack(itemstack.copy());
            if (!player.isCreative()) {
                itemstack.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }
        if (isMushroom(itemstack) && this.getTrueOwner() == player && this.getHealth() < this.getMaxHealth()) {
            if (!this.level().isClientSide) {
                this.heal(6);
                if (!player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                this.playSound(SoundEvents.ITEM_PICKUP, 1.0F, 1.0F);
                Vec3 heartDir = new Vec3(player.getX() - this.getX(), 0.0D, player.getZ() - this.getZ()).normalize();
                double heartX = this.getX() + heartDir.x * (this.getBbWidth() / 2.0D + 0.5D);
                double heartY = this.getY() + this.getBbHeight() * 0.8D;
                double heartZ = this.getZ() + heartDir.z * (this.getBbWidth() / 2.0D + 0.5D);
                ((ServerLevel) this.level()).sendParticles(ParticleTypes.HEART, heartX, heartY, heartZ, 5, 0.3D, 0.25D, 0.3D, 0.0D);
            }
            return InteractionResult.SUCCESS;
        }
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
        if (type != InteractionResult.SUCCESS && !isFood(itemstack) && !isMushroom(itemstack)) {
            // 只有主人可以骑乘
            if (!player.isShiftKeyDown() && !this.isBaby() && this.getAnimation() != ANIMATION_BUCK && this.getTrueOwner() == player) {
                player.startRiding(this);
                return InteractionResult.SUCCESS;
            }
        }
        return type;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(AMTagRegistry.TUSKLIN_BREEDABLES);
    }

    @Override
    public AnimalSummon getBreedOffspring(ServerLevel pLevel, AnimalSummon pOtherParent) {
        TusklinServant baby = AmEntityRegistry.TUSKLIN_SERVANT.get().create(pLevel);
        if (baby != null) {
            baby.setPersistenceRequired();
        }
        return baby;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag p_31808_) {
        super.addAdditionalSaveData(p_31808_);
        if (!this.getShoeStack().isEmpty()) {
            p_31808_.put("ShoeItem", this.getShoeStack().save(new CompoundTag()));
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag p_31795_) {
        super.readAdditionalSaveData(p_31795_);
        CompoundTag compoundtag = p_31795_.getCompound("ShoeItem");
        if (compoundtag != null && !compoundtag.isEmpty()) {
            ItemStack itemstack = ItemStack.of(compoundtag);
            if (itemstack.isEmpty()) {
                AlexsMobs.LOGGER.warn("Unable to load item from: {}", compoundtag);
            }
            this.setShoeStack(itemstack);
        }
    }

    public boolean isMushroom(ItemStack stack) {
        return stack.is(AMTagRegistry.TUSKLIN_FOODSTUFFS);
    }

    public boolean isSaddled() {
        return !this.getShoeStack().isEmpty();
    }

    public float getAgeScale() {
        return this.isBaby() ? 0.5F : 1.0F;
    }

    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        return this.getType().getDimensions().scale(this.getAgeScale());
    }

    /**
     * Goety 的 AnimalSummon 只在船里处理年龄边界变化，不会刷新尺寸。
     * 这里补充刷新，保证繁殖出生的幼崽（以及长大）时碰撞箱与 0.5 倍渲染一致。
     */
    @Override
    protected void ageBoundaryReached() {
        super.ageBoundaryReached();
        this.refreshDimensions();
    }

    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        if (!this.getShoeStack().isEmpty()) {
            if (!this.level().isClientSide) {
                this.spawnAtLocation(this.getShoeStack().copy());
            }
        }
        this.setShoeStack(ItemStack.EMPTY);
    }

    public ItemStack getShoeStack() {
        return this.getItemBySlot(EquipmentSlot.FEET);
    }

    public void setShoeStack(ItemStack shoe) {
        this.setItemSlot(EquipmentSlot.FEET, shoe);
    }

    @Override
    public void tick() {
        super.tick();
        if(isInNether()) {
            conversionTime++;
            if (conversionTime > 300 && !this.level().isClientSide) {
                this.dropEquipment();
                HoglinServant hoglin = this.convertTo(ModEntityType.HOGLIN_SERVANT.get(), false);
                if(hoglin != null){
                    hoglin.setTrueOwner(this.getTrueOwner());
                    hoglin.setAge(this.getAge());
                    hoglin.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
                }
            }
        }
        if (!this.level().isClientSide) {
            if (this.isAlive() && this.isVehicle() && this.getDeltaMovement().horizontalDistanceSqr() > 0.1D) {
                for (Entity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(1.0D))) {
                    if (!(entity instanceof TusklinServant) && !entity.isPassengerOfSameVehicle(this) && !this.isAlliedTo(entity)) {
                        entity.hurt(this.damageSources().mobAttack(this), 4F + random.nextFloat() * 3.0F);
                        if (entity.onGround()) {
                            double d0 = entity.getX() - this.getX();
                            double d1 = entity.getZ() - this.getZ();
                            double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
                            float f = 0.5F;
                            entity.push(d0 / d2 * f, f, d1 / d2 * f);
                        }
                    }
                }
                this.setMaxUpStep(2F);
            }else{
                this.setMaxUpStep(1.1F);
            }
            if (this.getTarget() != null && this.hasLineOfSight(this.getTarget()) && distanceTo(this.getTarget()) < this.getTarget().getBbWidth() + this.getBbWidth() + 1.8F) {
                if (this.getAnimation() == ANIMATION_FLING && this.getAnimationTick() == 6) {
                    this.getTarget().hurt(this.damageSources().mobAttack(this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                    knockbackTarget(this.getTarget(), 0.9F, 0F);
                }
                if ((this.getAnimation() == ANIMATION_GORE_L) && this.getAnimationTick() == 6) {
                    this.getTarget().hurt(this.damageSources().mobAttack(this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                    knockbackTarget(this.getTarget(), 0.5F, -90F);
                }
                if ((this.getAnimation() == ANIMATION_GORE_R) && this.getAnimationTick() == 6) {
                    this.getTarget().hurt(this.damageSources().mobAttack(this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                    knockbackTarget(this.getTarget(), 0.5F, 90F);
                }
            }
        }
        if (this.getAnimation() == ANIMATION_RUT && this.getAnimationTick() == 23) {
            if (level().getBlockState(this.blockPosition().below()).is(Blocks.GRASS_BLOCK) && getRandom().nextInt(3) == 0) {
                if (this.isBaby()) {
                    if (level().getBlockState(this.blockPosition()).canBeReplaced() && random.nextInt(3) == 0) {
                        level().setBlockAndUpdate(this.blockPosition(), Blocks.BROWN_MUSHROOM.defaultBlockState());
                        this.gameEvent(GameEvent.BLOCK_DESTROY);
                        this.playSound(SoundEvents.CROP_PLANTED, this.getSoundVolume(), this.getVoicePitch());
                    }
                }
                this.level().levelEvent(2001, blockPosition().below(), Block.getId(Blocks.GRASS_BLOCK.defaultBlockState()));
                this.level().setBlock(blockPosition().below(), Blocks.DIRT.defaultBlockState(), 2);
                this.heal(5);
            }
        }
        if (!this.level().isClientSide && this.getAnimation() == NO_ANIMATION && getRandom().nextInt(isBaby() ? 140 : 70) == 0 && (this.getLastHurtByMob() == null || this.distanceTo(this.getLastHurtByMob()) > 30)) {
            if (level().getBlockState(this.blockPosition().below()).is(Blocks.GRASS_BLOCK) && getRandom().nextInt(3) == 0) {
                this.setAnimation(ANIMATION_RUT);
            }
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    private void knockbackTarget(LivingEntity entity, float strength, float angle) {
        float rot = getYRot() + angle;
        if(entity != null){
            entity.knockback(strength, Mth.sin(rot * Mth.DEG_TO_RAD), -Mth.cos(rot * Mth.DEG_TO_RAD));
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
        currentAnimation = animation;
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_RUT, ANIMATION_GORE_L, ANIMATION_GORE_R, ANIMATION_FLING, ANIMATION_BUCK};
    }

}