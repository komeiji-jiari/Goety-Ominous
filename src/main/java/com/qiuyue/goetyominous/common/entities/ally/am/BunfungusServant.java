package com.qiuyue.goetyominous.common.entities.ally.am;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.init.ModMobType;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAILeapRandomly;
import com.github.alexthe666.alexsmobs.entity.ai.GroundPathNavigatorWide;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class BunfungusServant extends Summoned implements IAnimatedEntity {

    public static final Animation ANIMATION_SLAM = EntityBunfungus.ANIMATION_SLAM;
    public static final Animation ANIMATION_BELLY = EntityBunfungus.ANIMATION_BELLY;
    public static final Animation ANIMATION_EAT = EntityBunfungus.ANIMATION_EAT;

    private static final EntityDataAccessor<Boolean> JUMP_ACTIVE = SynchedEntityData.defineId(BunfungusServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SLEEPING = SynchedEntityData.defineId(BunfungusServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> BEGGING = SynchedEntityData.defineId(BunfungusServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> CARROTED = SynchedEntityData.defineId(BunfungusServant.class, EntityDataSerializers.BOOLEAN);

    public float jumpProgress;
    public float prevJumpProgress;
    public float reboundProgress;
    public float prevReboundProgress;
    public float sleepProgress;
    public float prevSleepProgress;
    public float interestedProgress;
    public float prevInterestedProgress;
    private int animationTick;
    private Animation currentAnimation;

    public BunfungusServant(EntityType<? extends Owned> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.BunfungusServantHealth.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.BunfungusServantDamage.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.BunfungusServantFollowRange.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.BunfungusServantMovementSpeed.get());
    }

    public MobType getMobType() {
        return ModMobType.NATURAL;
    }

    @Override
    public void playAmbientSound() {
        if (!this.isSleeping()) {
            super.playAmbientSound();
        }
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.BUNFUNGUS_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.BUNFUNGUS_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.BUNFUNGUS_HURT.get();
    }

    @Override
    public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
        return false;
    }

    @Override
    protected PathNavigation createNavigation(Level worldIn) {
        return new GroundPathNavigatorWide(this, worldIn);
    }

    private boolean canUseComplexAI() {
        return !this.isSleeping();
    }

    @Override
    protected float getWaterSlowDown() {
        return 0.98F;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(JUMP_ACTIVE, false);
        this.entityData.define(SLEEPING, false);
        this.entityData.define(BEGGING, false);
        this.entityData.define(CARROTED, false);
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        return false;
    }

    @Override
    protected void checkFallDamage(double pY, boolean pOnGround, BlockState pState, BlockPos pPos) {
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new BunfungusServantAIMelee(this));
        this.goalSelector.addGoal(2, new BunfungusServantAIBeg(this, 1.0D));
        this.goalSelector.addGoal(4, new BunfungusServantAILeap(this, 60, 7));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1.0D, 60) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && !BunfungusServant.this.isStaying()
                        && !BunfungusServant.this.isCommanded()
                        && !BunfungusServant.this.isSleeping();
            }
        });
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 10.0F) {
            @Override
            public boolean canUse() {
                return super.canUse() && canUseComplexAI();
            }
        });
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this) {
            @Override
            public boolean canUse() {
                return super.canUse() && canUseComplexAI();
            }
        });
    }

    @Override
    public void tick() {
        super.tick();
        this.prevJumpProgress = this.jumpProgress;
        this.prevReboundProgress = this.reboundProgress;
        this.prevSleepProgress = this.sleepProgress;
        this.prevInterestedProgress = this.interestedProgress;

        if (!this.level().isClientSide) {
            this.entityData.set(JUMP_ACTIVE, !this.onGround());
        }
        if (this.entityData.get(JUMP_ACTIVE) && !this.isInWaterOrBubble()) {
            if (this.jumpProgress < 5.0F) {
                this.jumpProgress += 0.5F;
                if (this.reboundProgress > 0) {
                    this.reboundProgress -= 1.0F;
                }
            }
            if (this.jumpProgress >= 5.0F && this.reboundProgress < 5.0F) {
                this.reboundProgress += 0.5F;
            }
        } else {
            if (this.reboundProgress > 0) {
                this.reboundProgress = Math.max(0.0F, this.reboundProgress - 1.0F);
            }
            if (this.jumpProgress > 0) {
                this.jumpProgress = Math.max(0.0F, this.jumpProgress - 1.0F);
            }
        }

        if (this.isSleepingPose()) {
            if (this.sleepProgress < 5.0F) {
                this.sleepProgress += 1.0F;
            }
        } else {
            if (this.sleepProgress > 0) {
                this.sleepProgress -= 1.0F;
            }
        }

        if (this.isBegging()) {
            if (this.interestedProgress < 5.0F) {
                this.interestedProgress += 1.0F;
            }
        } else {
            if (this.interestedProgress > 0) {
                this.interestedProgress -= 1.0F;
            }
        }

        if (!this.level().isClientSide) {
            LivingEntity target = this.getTarget();
            if (target != null && target.isAlive()) {
                if (this.isSleeping()) {
                    this.setSleeping(false);
                }
                double dist = this.distanceTo(target);
                boolean hit = false;
                if (this.getAnimationTick() == 5) {
                    if (dist < 3.5D && this.getAnimation() == ANIMATION_BELLY) {
                        for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(2.0D))) {
                            if ((entity == target || entity instanceof Monster) && !entity.getType().is(AMTagRegistry.BUNFUNGUS_IGNORE_AOE_ATTACKS)) {
                                hit = true;
                                this.launch(entity);
                                entity.hurt(this.damageSources().mobAttack(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
                            }
                        }
                    }
                    if (dist < 2.5D && this.getAnimation() == ANIMATION_SLAM) {
                        for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(2.0D))) {
                            if ((entity == target || entity instanceof Monster) && !entity.getType().is(AMTagRegistry.BUNFUNGUS_IGNORE_AOE_ATTACKS)) {
                                hit = true;
                                entity.knockback(0.2D, entity.getX() - this.getX(), entity.getZ() - this.getZ());
                                entity.hurt(this.damageSources().mobAttack(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
                            }
                        }
                    }
                }
                if (hit) {
                    this.playSound(AMSoundRegistry.BUNFUNGUS_ATTACK.get(), this.getSoundVolume(), this.getVoicePitch());
                }
            }
            if (this.tickCount % 40 == 0) {
                this.heal(1.0F);
            }
        }

        if (this.getAnimation() == NO_ANIMATION && this.isCarrot(this.getItemInHand(InteractionHand.MAIN_HAND))) {
            this.setAnimation(ANIMATION_EAT);
        }
        if (this.getAnimation() == ANIMATION_EAT) {
            if (this.getAnimationTick() % 4 == 0) {
                this.gameEvent(GameEvent.EAT);
                this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
            }
            if (this.getAnimationTick() >= 18) {
                ItemStack stack = this.getItemInHand(InteractionHand.MAIN_HAND);
                if (!stack.isEmpty() && !this.level().isClientSide) {
                    stack.shrink(1);
                    this.setCarroted(true);
                    this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1000));
                    this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 1000, 1));
                    this.heal(8.0F);
                    if (this.level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.HEART,
                                this.getX(), this.getY() + this.getBbHeight() + 0.5D, this.getZ(),
                                6, 0.4D, 0.2D, 0.4D, 0.0D);
                    }
                }
            } else {
                for (int i = 0; i < 3; i++) {
                    double d0 = this.random.nextGaussian() * 0.02D;
                    double d1 = this.random.nextGaussian() * 0.02D;
                    double d2 = this.random.nextGaussian() * 0.02D;
                    this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.getItemInHand(InteractionHand.MAIN_HAND)),
                            this.getX() + this.random.nextFloat() * this.getBbWidth() - this.getBbWidth() * 0.5D,
                            this.getY() + this.getBbHeight() * 0.5F + this.random.nextFloat() * this.getBbHeight() * 0.5F,
                            this.getZ() + this.random.nextFloat() * this.getBbWidth() - this.getBbWidth() * 0.5D,
                            d1, d2, d0);
                }
            }
        }

        if (this.level().isClientSide) {
            if (this.isSleeping()) {
                if (this.random.nextFloat() < 0.3F) {
                    double d0 = this.random.nextGaussian() * 0.02D;
                    float radius = this.getBbWidth() * (0.7F + this.random.nextFloat() * 0.1F);
                    float angle = 0.017453292F * this.yBodyRot;
                    double x = this.getX() + radius * Mth.sin(Mth.PI + angle) + this.random.nextFloat() * 0.5F - 0.25F;
                    double z = this.getZ() + radius * Mth.cos(angle) + this.random.nextFloat() * 0.5F - 0.25F;
                    ParticleOptions particle = this.random.nextFloat() < 0.3F ? AMParticleRegistry.BUNFUNGUS_TRANSFORMATION.get() : AMParticleRegistry.FUNGUS_BUBBLE.get();
                    this.level().addParticle(particle, x, this.getY() + this.random.nextFloat() * 0.1F, z, 0.0D, d0, 0.0D);
                }
            }
        } else {
            if (this.isStaying() && this.getTarget() == null && !this.isBegging() && !this.isInWaterOrBubble()) {
                this.setSleeping(true);
            } else if (this.isSleeping()) {
                this.setSleeping(false);
            }
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    private void launch(LivingEntity entity) {
        if (entity.onGround()) {
            double dx = entity.getX() - this.getX();
            double dz = entity.getZ() - this.getZ();
            double dist = Math.max(dx * dx + dz * dz, 0.001D);
            float strength = 6.0F + this.random.nextFloat() * 2.0F;
            entity.push(dx / dist * strength, 0.6F + this.random.nextFloat() * 0.7F, dz / dist * strength);
        }
    }

    public boolean isSleeping() {
        return this.entityData.get(SLEEPING);
    }

    public void setSleeping(boolean sleeping) {
        this.entityData.set(SLEEPING, sleeping);
    }

    public boolean isSleepingPose() {
        return this.isSleeping() || (this.getAnimation() == ANIMATION_SLAM && this.getAnimationTick() < 10);
    }

    public boolean isCarroted() {
        return this.entityData.get(CARROTED);
    }

    public void setCarroted(boolean carroted) {
        this.entityData.set(CARROTED, carroted);
    }

    public boolean isBegging() {
        return this.entityData.get(BEGGING) && this.getAnimation() != ANIMATION_EAT;
    }

    public void setBegging(boolean begging) {
        this.entityData.set(BEGGING, begging);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        InteractionResult type = super.mobInteract(player, hand);
        InteractionResult result = itemstack.interactLivingEntity(player, this, hand);
        if (result != InteractionResult.SUCCESS && type != InteractionResult.SUCCESS && this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && this.isCarrot(itemstack)) {
            ItemStack copy = itemstack.copy();
            copy.setCount(1);
            this.setItemInHand(InteractionHand.MAIN_HAND, copy);
            if (!player.isCreative()) {
                itemstack.shrink(1);
            }
        }
        return type;
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isSleeping()) {
            super.travel(Vec3.ZERO);
        } else {
            super.travel(travelVector);
        }
    }

    @Override
    public int getAnimationTick() {
        return this.animationTick;
    }

    @Override
    public void setAnimationTick(int tick) {
        this.animationTick = tick;
    }

    @Override
    public Animation getAnimation() {
        return this.currentAnimation;
    }

    @Override
    public void setAnimation(Animation animation) {
        this.currentAnimation = animation;
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_EAT, ANIMATION_BELLY, ANIMATION_SLAM};
    }

    public boolean isCarrot(ItemStack stack) {
        return stack.is(AMTagRegistry.BUNFUNGUS_FOODSTUFFS);
    }

    public boolean defendsMungusAgainst(LivingEntity entity) {
        if (entity instanceof Player) {
            return this.isCarroted();
        }
        return true;
    }

    public void onJump() {
    }

    static class BunfungusServantAILeap extends AnimalAILeapRandomly {
        private final BunfungusServant bunfungus;

        public BunfungusServantAILeap(BunfungusServant bunfungus, int chance, int maxLeapDistance) {
            super(bunfungus, chance, maxLeapDistance);
            this.bunfungus = bunfungus;
        }

        @Override
        public boolean canUse() {
            return super.canUse()
                    && !this.bunfungus.isStaying()
                    && !this.bunfungus.isCommanded()
                    && !this.bunfungus.isSleeping();
        }
    }

    static class BunfungusServantAIMelee extends Goal {
        private final BunfungusServant chungus;
        private LivingEntity target;
        private boolean hasJumped;
        private int jumpCooldown;

        public BunfungusServantAIMelee(BunfungusServant bunfungus) {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
            this.chungus = bunfungus;
        }

        @Override
        public boolean canUse() {
            if (this.chungus.getTarget() != null && this.chungus.getTarget().isAlive()) {
                this.hasJumped = false;
                return true;
            }
            return false;
        }

        @Override
        public void tick() {
            if (this.jumpCooldown > 0) {
                this.jumpCooldown--;
            }
            double dist = this.chungus.distanceTo(this.chungus.getTarget()) - this.chungus.getTarget().getBbWidth();
            if (dist < 2.0D) {
                if (this.hasJumped) {
                    if (!this.chungus.onGround()) {
                        this.chungus.getTarget().hurt(this.chungus.damageSources().mobAttack(this.chungus), (float) this.chungus.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
                    }
                    this.hasJumped = false;
                } else {
                    if (this.chungus.getRandom().nextBoolean()) {
                        this.chungus.setAnimation(BunfungusServant.ANIMATION_SLAM);
                    } else {
                        this.chungus.setAnimation(BunfungusServant.ANIMATION_BELLY);
                    }
                }
            } else if (dist >= 2.0D && dist < 5.0D && !this.chungus.isStaying() && this.chungus.hasLineOfSight(this.chungus.getTarget()) && this.jumpCooldown <= 0 && !this.chungus.isInWaterOrBubble()) {
                this.chungus.getNavigation().stop();
                if (this.chungus.onGround()) {
                    Vec3 vec3 = this.chungus.getDeltaMovement();
                    Vec3 vec = new Vec3(this.chungus.getTarget().getX() - this.chungus.getX(), 0.0D, this.chungus.getTarget().getZ() - this.chungus.getZ());
                    if (vec.lengthSqr() > 1.0E-7D) {
                        vec = vec.normalize().scale(0.9D).add(vec3.scale(0.8D));
                    }
                    this.chungus.onJump();
                    this.chungus.setDeltaMovement(vec.x, 0.6D, vec.z);
                    this.chungus.setYRot(-(float) Mth.atan2(vec.x, vec.z) * 57.295776F);
                    this.chungus.yBodyRot = this.chungus.getYRot();
                    this.hasJumped = true;
                    this.jumpCooldown = 10;
                }
            } else if (!this.chungus.isStaying()) {
                this.chungus.getNavigation().moveTo(this.chungus.getTarget(), 1.0D);
            }
        }
    }

    static class BunfungusServantAIBeg extends Goal {
        protected final BunfungusServant jerboa;
        private final double speed;
        protected Player closestPlayer;
        private int delayTemptCounter;
        private boolean isRunning;

        public BunfungusServantAIBeg(BunfungusServant bunfungus, double speed) {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
            this.jerboa = bunfungus;
            this.speed = speed;
        }

        @Override
        public boolean canUse() {
            if (this.delayTemptCounter > 0) {
                this.delayTemptCounter--;
                return false;
            }
            if (this.jerboa.getTrueOwner() instanceof Player ownerPlayer
                    && !this.jerboa.isStaying()
                    && ownerPlayer.level() == this.jerboa.level()
                    && this.jerboa.distanceToSqr(ownerPlayer) < 32.0D * 32.0D
                    && (this.isFood(ownerPlayer.getMainHandItem()) || this.isFood(ownerPlayer.getOffhandItem()))) {
                this.closestPlayer = ownerPlayer;
                return true;
            }
            this.closestPlayer = null;
            return false;
        }

        private boolean isFood(ItemStack stack) {
            return stack.getItem() == Items.CARROT;
        }

        @Override
        public boolean canContinueToUse() {
            return this.jerboa.getMainHandItem().isEmpty() && this.canUse();
        }

        @Override
        public void start() {
            this.isRunning = true;
        }

        @Override
        public void stop() {
            this.closestPlayer = null;
            this.jerboa.getNavigation().stop();
            this.delayTemptCounter = 100;
            this.jerboa.setBegging(false);
            this.isRunning = false;
        }

        @Override
        public void tick() {
            this.jerboa.setSleeping(false);
            this.jerboa.getLookControl().setLookAt(this.closestPlayer, (float) (this.jerboa.getMaxHeadYRot() + 20), (float) this.jerboa.getMaxHeadXRot());
            if (this.jerboa.distanceToSqr(this.closestPlayer) < 12.0D) {
                this.jerboa.getNavigation().stop();
                this.jerboa.setBegging(true);
            } else {
                this.jerboa.getNavigation().moveTo(this.closestPlayer, this.speed);
            }
        }

        public boolean isRunning() {
            return this.isRunning;
        }
    }
}
