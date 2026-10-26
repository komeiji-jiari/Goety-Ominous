package com.qiuyue.goetyominous.common.entities.ally.am;

import com.Polarice3.Goety.common.entities.ally.AnimalSummon;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.init.ModMobType;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityTiger;
import com.github.alexthe666.alexsmobs.entity.ai.AdvancedPathNavigateNoTeleport;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.server.entity.collision.ICustomCollisions;
import com.qiuyue.goetyominous.common.init.am.AmEntityRegistry;

import javax.annotation.Nullable;
import java.util.EnumSet;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import com.qiuyue.goetyominous.config.am.AmConfig;

public class TigerServant extends AnimalSummon implements IAnimatedEntity, ICustomCollisions {

    public static final Animation ANIMATION_PAW_R = EntityTiger.ANIMATION_PAW_R;
    public static final Animation ANIMATION_PAW_L = EntityTiger.ANIMATION_PAW_L;
    public static final Animation ANIMATION_TAIL_FLICK = EntityTiger.ANIMATION_TAIL_FLICK;
    public static final Animation ANIMATION_LEAP = EntityTiger.ANIMATION_LEAP;

    private static final EntityDataAccessor<Boolean> WHITE = SynchedEntityData.defineId(TigerServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> RUNNING = SynchedEntityData.defineId(TigerServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> STEALTH_MODE = SynchedEntityData.defineId(TigerServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HOLDING = SynchedEntityData.defineId(TigerServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> LAST_SCARED_MOB_ID = SynchedEntityData.defineId(TigerServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> INTERESTED = SynchedEntityData.defineId(TigerServant.class, EntityDataSerializers.BOOLEAN);

    public float prevInterestedAngle;
    public float interestedAngle;
    public float prevSitProgress;
    public float sitProgress;
    public float prevHoldProgress;
    public float holdProgress;
    public float prevStealthProgress;
    public float stealthProgress;
    private int animationTick;
    private Animation currentAnimation;
    private boolean hasSpedUp = false;
    private int holdTime = 0;
    private int prevScaredMobId = -1;
    private int happyCool = 0;

    public TigerServant(EntityType<? extends Owned> type, Level level) {
        super(type, level);
        this.setMaxUpStep(0.6F);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0.0F);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AmConfig.TigerServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.ATTACK_DAMAGE, AmConfig.TigerServantDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(WHITE, false);
        this.entityData.define(RUNNING, false);
        this.entityData.define(STEALTH_MODE, false);
        this.entityData.define(HOLDING, false);
        this.entityData.define(LAST_SCARED_MOB_ID, -1);
        this.entityData.define(INTERESTED, false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(1, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(2, new AIMelee());
        this.goalSelector.addGoal(5, new Summoned.WanderGoal<>(this, 1.0D, 60, 0.001F));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 15.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(9, new BegGoal(this, 8.0F));
    }

    @Override
    public MobType getMobType() {
        return ModMobType.NATURAL;
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return AmConfig.TigerServantLimit.get();
    }

    protected SoundEvent getAmbientSound() {
        return this.isStealth() ? super.getAmbientSound() : (this.getTarget() != null ? AMSoundRegistry.TIGER_ANGRY.get() : AMSoundRegistry.TIGER_IDLE.get());
    }

    public int getAmbientSoundInterval() {
        return this.getTarget() != null ? 40 : 80;
    }

    protected SoundEvent getHurtSound(DamageSource pSource) {
        return AMSoundRegistry.TIGER_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.TIGER_HURT.get();
    }

    protected float getWaterSlowDown() {
        return 0.99F;
    }

    @Override
    protected PathNavigation createNavigation(Level worldIn) {
        return new Navigator(this, worldIn);
    }

    public boolean shouldMove() {
        return !this.isHolding();
    }

    @Override
    public double getVisibilityPercent(@Nullable Entity lookingEntity) {
        if (this.isStealth()) {
            return 0.2D;
        }
        return super.getVisibilityPercent(lookingEntity);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(AMTagRegistry.TIGER_BREEDABLES);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (this.getTrueOwner() != null && player == this.getTrueOwner()) {
            FoodProperties foodProperties = this.getMeatFood(itemstack);
            if (foodProperties != null && this.getHealth() < this.getMaxHealth()) {
                if (!this.level().isClientSide) {
                    this.heal(foodProperties.getNutrition());
                    if (!player.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    this.gameEvent(GameEvent.EAT, this);
                    this.sendHeartParticles(7);
                }
                player.swing(hand);
                return InteractionResult.SUCCESS;
            }
            if (!this.isFood(itemstack) && player.getMainHandItem().isEmpty() && this.happyCool <= 0) {
                this.happyCool = 40;
                if (!this.level().isClientSide) {
                    this.heal(1.0F);
                    this.playSound(AMSoundRegistry.TIGER_IDLE.get(), 1.0F, 0.5F);
                    this.sendHeartParticles(5);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(player, hand);
    }

    @Nullable
    private FoodProperties getMeatFood(ItemStack stack) {
        if (!stack.isEdible()) {
            return null;
        }
        FoodProperties foodProperties = stack.getFoodProperties(this);
        return foodProperties != null && foodProperties.isMeat() ? foodProperties : null;
    }

    private void sendHeartParticles(int count) {
        if (this.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < count; ++i) {
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D + 0.1D;
                double d2 = this.random.nextGaussian() * 0.02D;
                serverLevel.sendParticles(ParticleTypes.HEART, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5D);
            }
        }
    }

    @Override
    public void awardKillScore(Entity entity, int score, DamageSource source) {
        this.heal(5.0F);
        super.awardKillScore(entity, score, source);
    }

    @Override
    public void travel(Vec3 vec3d) {
        if (!this.shouldMove()) {
            if (this.getNavigation().getPath() != null) {
                this.getNavigation().stop();
            }
            vec3d = Vec3.ZERO;
        }
        super.travel(vec3d);
    }

    public boolean isSitting() {
        return this.isStaying();
    }

    public boolean isWhite() {
        return this.entityData.get(WHITE);
    }

    public void setWhite(boolean white) {
        this.entityData.set(WHITE, white);
    }

    public boolean isRunning() {
        return this.entityData.get(RUNNING);
    }

    public void setRunning(boolean running) {
        this.entityData.set(RUNNING, running);
    }

    public boolean isStealth() {
        return this.entityData.get(STEALTH_MODE);
    }

    public void setStealth(boolean stealth) {
        this.entityData.set(STEALTH_MODE, stealth);
    }

    public boolean isHolding() {
        return this.entityData.get(HOLDING);
    }

    public void setHolding(boolean holding) {
        this.entityData.set(HOLDING, holding);
    }

    public boolean isInterested() {
        return this.entityData.get(INTERESTED);
    }

    public void setInterested(boolean interested) {
        this.entityData.set(INTERESTED, interested);
    }

    public float getHeadRollAngle(float partialTick) {
        return Mth.lerp(partialTick, this.prevInterestedAngle, this.interestedAngle) * 0.15F * (float) Math.PI;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("White", this.isWhite());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setWhite(compound.getBoolean("White"));
    }

    @Override
    public boolean isColliding(BlockPos pos, BlockState blockstate) {
        return blockstate.getBlock() != Blocks.BAMBOO && !blockstate.is(BlockTags.LEAVES) && super.isColliding(pos, blockstate);
    }

    @Override
    public Vec3 collide(Vec3 vec3) {
        return ICustomCollisions.getAllowedMovementForEntity(this, vec3);
    }

    @Override
    public boolean canPassThrough(BlockPos mutablePos, BlockState blockstate, VoxelShape voxelshape) {
        return blockstate.getBlock() == Blocks.BAMBOO || blockstate.is(BlockTags.LEAVES);
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        return false;
    }

    @Override
    protected void checkFallDamage(double pY, boolean pOnGround, BlockState pState, BlockPos pPos) {
    }

    @Override
    public void push(Entity entityIn) {
        if (!this.isHolding() || entityIn != this.getTarget()) {
            super.push(entityIn);
        }
    }

    @Override
    protected void doPush(Entity entityIn) {
        if (!this.isHolding() || entityIn != this.getTarget()) {
            super.doPush(entityIn);
        }
    }

    @Override
    public AnimalSummon getBreedOffspring(ServerLevel level, AnimalSummon otherParent) {
        TigerServant baby = AmEntityRegistry.TIGER_SERVANT.get().create(level);
        if (baby != null) {
            boolean whiteOther = otherParent instanceof TigerServant && ((TigerServant) otherParent).isWhite();
            double whiteChance = 0.1D;
            if (this.isWhite() && whiteOther) {
                whiteChance = 0.8D;
            }
            if (this.isWhite() != whiteOther) {
                whiteChance = 0.4D;
            }
            baby.setWhite(this.random.nextDouble() < whiteChance);
            if (this.isUpgraded() || otherParent.isUpgraded()) {
                baby.setUpgraded(true);
            }
            baby.setPersistenceRequired();
        }
        return baby;
    }

    public float getAgeScale() {
        return this.isBaby() ? 0.5F : 1.0F;
    }

    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        return this.getType().getDimensions().scale(this.getAgeScale());
    }

    @Override
    protected void ageBoundaryReached() {
        super.ageBoundaryReached();
        this.refreshDimensions();
    }

    @Override
    public void tick() {
        super.tick();
        this.prevInterestedAngle = this.interestedAngle;
        if (!this.isDeadOrDying()) {
            this.interestedAngle += ((this.isInterested() ? 1.0F : 0.0F) - this.interestedAngle) * 0.4F;
        }
        this.prevSitProgress = this.sitProgress;
        this.prevHoldProgress = this.holdProgress;
        this.prevStealthProgress = this.stealthProgress;
        boolean sitting = this.isSitting();
        boolean holding = this.isHolding();
        boolean stealth = this.isStealth();
        if (sitting) {
            if (this.sitProgress < 5.0F) {
                this.sitProgress += 1.0F;
            }
        } else if (this.sitProgress > 0.0F) {
            this.sitProgress -= 1.0F;
        }
        if (holding) {
            if (this.holdProgress < 5.0F) {
                this.holdProgress += 1.0F;
            }
        } else if (this.holdProgress > 0.0F) {
            this.holdProgress -= 1.0F;
        }
        if (stealth) {
            if (this.stealthProgress < 10.0F) {
                this.stealthProgress += 0.25F;
            }
        } else if (this.stealthProgress > 0.0F) {
            this.stealthProgress -= 1.0F;
        }
        if (!this.level().isClientSide) {
            if (this.happyCool > 0) {
                --this.happyCool;
            }
            if (this.isRunning() && !this.hasSpedUp) {
                this.hasSpedUp = true;
                this.setMaxUpStep(1.0F);
                this.setSprinting(true);
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25 + 0.15D);
            }
            if (!this.isRunning() && this.hasSpedUp) {
                this.hasSpedUp = false;
                this.setMaxUpStep(0.6F);
                this.setSprinting(false);
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25);
            }
            if (this.getDeltaMovement().lengthSqr() < 0.03D && this.getAnimation() == NO_ANIMATION && !this.isHolding() && this.getTarget() == null && this.random.nextInt(100) == 0) {
                this.setAnimation(ANIMATION_TAIL_FLICK);
            }
        }
        if (this.isHolding()) {
            this.setSprinting(false);
            this.setRunning(false);
            LivingEntity target = this.getTarget();
            if (!this.level().isClientSide && target != null && target.isAlive()) {
                this.setXRot(0.0F);
                float radius = 1.0F + target.getBbWidth() * 0.5F;
                float angle = (float) Math.PI / 180.0F * this.yBodyRot;
                double extraX = radius * Mth.sin((float) Math.PI + angle);
                double extraZ = radius * Mth.cos(angle);
                Vec3 minus = new Vec3(this.getX() + extraX - target.getX(), this.getY() - 0.5D - target.getY(), this.getZ() + extraZ - target.getZ());
                target.setDeltaMovement(minus);
                target.hasImpulse = true;
                if (this.holdTime % 20 == 0) {
                    target.hurt(this.damageSources().mobAttack(this), (float) (5 + this.getRandom().nextInt(2)));
                }
                if (target.distanceTo(this) > 8.0F) {
                    this.setHolding(false);
                    this.holdTime = 150;
                }
            }
            ++this.holdTime;
            if (this.holdTime > 100) {
                this.holdTime = 0;
                this.setHolding(false);
            }
        } else {
            this.holdTime = 0;
        }
        if (this.prevScaredMobId != this.entityData.get(LAST_SCARED_MOB_ID) && this.level().isClientSide) {
            Entity entity = this.level().getEntity(this.entityData.get(LAST_SCARED_MOB_ID));
            if (entity != null) {
                double d0 = this.random.nextGaussian() * 0.1D;
                double d1 = this.random.nextGaussian() * 0.1D;
                double d2 = this.random.nextGaussian() * 0.1D;
                this.level().addParticle(AMParticleRegistry.SHOCKED.get(), entity.getX(), entity.getEyeY() + (double) (entity.getBbHeight() * 0.15F) + (double) (this.random.nextFloat() * entity.getBbHeight() * 0.15F), entity.getZ(), d0, d1, d2);
            }
        }
        this.prevScaredMobId = this.entityData.get(LAST_SCARED_MOB_ID);
        AnimationHandler.INSTANCE.updateAnimations(this);
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
        return new Animation[]{ANIMATION_PAW_R, ANIMATION_PAW_L, ANIMATION_LEAP, ANIMATION_TAIL_FLICK};
    }

    class BegGoal extends Goal {
        private final TigerServant tiger;
        private final float lookDistance;
        private int lookTime;

        public BegGoal(TigerServant tiger, float lookDistance) {
            this.tiger = tiger;
            this.lookDistance = lookDistance;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return this.tiger.getTarget() == null && !this.tiger.isHolding() && this.tiger.getTrueOwner() != null && this.playerHoldingInteresting(this.tiger.getTrueOwner());
        }

        @Override
        public boolean canContinueToUse() {
            if (this.tiger.getTarget() != null || this.tiger.isHolding()) {
                return false;
            }
            LivingEntity owner = this.tiger.getTrueOwner();
            if (owner == null || !owner.isAlive()) {
                return false;
            }
            if (this.tiger.distanceToSqr(owner) > (double) (this.lookDistance * this.lookDistance)) {
                return false;
            }
            return this.lookTime > 0 && this.playerHoldingInteresting(owner);
        }

        @Override
        public void start() {
            this.tiger.setInterested(true);
            this.lookTime = 40 + this.tiger.getRandom().nextInt(40);
        }

        @Override
        public void stop() {
            this.tiger.setInterested(false);
        }

        @Override
        public void tick() {
            LivingEntity owner = this.tiger.getTrueOwner();
            if (owner != null) {
                this.tiger.getLookControl().setLookAt(owner.getX(), owner.getEyeY(), owner.getZ(), 10.0F, (float) this.tiger.getMaxHeadXRot());
            }
            --this.lookTime;
        }

        private boolean playerHoldingInteresting(LivingEntity entity) {
            for (InteractionHand interactionHand : InteractionHand.values()) {
                ItemStack itemstack = entity.getItemInHand(interactionHand);
                if (this.tiger.isFood(itemstack) || this.tiger.getMeatFood(itemstack) != null) {
                    return true;
                }
            }
            return false;
        }
    }

    class AIMelee extends Goal {
        private int jumpAttemptCooldown = 0;

        public AIMelee() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return !TigerServant.this.isSitting() && TigerServant.this.getTarget() != null && TigerServant.this.getTarget().isAlive();
        }

        @Override
        public boolean canContinueToUse() {
            return !TigerServant.this.isSitting() && TigerServant.this.getTarget() != null && TigerServant.this.getTarget().isAlive();
        }

        @Override
        public void tick() {
            if (this.jumpAttemptCooldown > 0) {
                --this.jumpAttemptCooldown;
            }
            LivingEntity target = TigerServant.this.getTarget();
            if (target != null && target.isAlive()) {
                double dist = TigerServant.this.distanceTo(target);
                if (dist < 10.0D && TigerServant.this.getLastHurtByMob() != null && TigerServant.this.getLastHurtByMob().isAlive()) {
                    TigerServant.this.setStealth(false);
                } else if (dist > 20.0D) {
                    TigerServant.this.setRunning(false);
                    TigerServant.this.setStealth(true);
                }
                if (dist <= 20.0D) {
                    TigerServant.this.setStealth(false);
                    TigerServant.this.setRunning(true);
                    if (TigerServant.this.entityData.get(LAST_SCARED_MOB_ID) != target.getId()) {
                        TigerServant.this.entityData.set(LAST_SCARED_MOB_ID, target.getId());
                        target.addEffect(new MobEffectInstance(AMEffectRegistry.FEAR.get(), 100, 0, true, false));
                    }
                }
                if (dist < 12.0D && TigerServant.this.getAnimation() == NO_ANIMATION && TigerServant.this.onGround() && this.jumpAttemptCooldown == 0 && !TigerServant.this.isHolding()) {
                    TigerServant.this.setAnimation(ANIMATION_LEAP);
                    this.jumpAttemptCooldown = 70;
                }
                if ((this.jumpAttemptCooldown > 0 || TigerServant.this.isInWaterOrBubble()) && !TigerServant.this.isHolding() && TigerServant.this.getAnimation() == NO_ANIMATION && dist < (double) (4.0F + target.getBbWidth())) {
                    TigerServant.this.setAnimation(TigerServant.this.getRandom().nextBoolean() ? ANIMATION_PAW_L : ANIMATION_PAW_R);
                }
                if (dist < (double) (4.0F + target.getBbWidth()) && (TigerServant.this.getAnimation() == ANIMATION_PAW_L || TigerServant.this.getAnimation() == ANIMATION_PAW_R) && TigerServant.this.getAnimationTick() == 8) {
                    target.hurt(TigerServant.this.damageSources().mobAttack(TigerServant.this), (float) TigerServant.this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                }
                if (TigerServant.this.getAnimation() == ANIMATION_LEAP) {
                    TigerServant.this.getNavigation().stop();
                    Vec3 vec = target.position().subtract(TigerServant.this.position());
                    TigerServant.this.setYRot(-((float) Mth.atan2(vec.x, vec.z)) * 57.295776F);
                    TigerServant.this.yBodyRot = TigerServant.this.getYRot();
                    if (TigerServant.this.getAnimationTick() >= 5 && TigerServant.this.getAnimationTick() < 11 && TigerServant.this.onGround()) {
                        Vec3 vector3d1 = new Vec3(target.getX() - TigerServant.this.getX(), 0.0D, target.getZ() - TigerServant.this.getZ());
                        if (vector3d1.lengthSqr() > 1.0E-7D) {
                            vector3d1 = vector3d1.normalize().scale(Math.min(dist, 15.0D) * (double) 0.2F);
                        }
                        TigerServant.this.setDeltaMovement(vector3d1.x, vector3d1.y + (double) 0.3F + (double) 0.1F * Mth.clamp(target.getEyeY() - TigerServant.this.getY(), 0.0D, 2.0D), vector3d1.z);
                    }
                    if (dist < (double) (target.getBbWidth() + 3.0F) && TigerServant.this.getAnimationTick() >= 15) {
                        target.hurt(TigerServant.this.damageSources().mobAttack(TigerServant.this), 2.0F);
                        TigerServant.this.setRunning(false);
                        TigerServant.this.setStealth(false);
                        TigerServant.this.setHolding(true);
                    }
                } else {
                    TigerServant.this.getNavigation().moveTo(target, TigerServant.this.isStealth() ? 0.75D : 1.0D);
                }
            }
        }

        @Override
        public void stop() {
            TigerServant.this.setStealth(false);
            TigerServant.this.setRunning(false);
            TigerServant.this.setHolding(false);
        }
    }

    static class TigerNodeEvaluator extends WalkNodeEvaluator {
        protected BlockPathTypes evaluateBlockPathType(BlockGetter level, BlockPos pos, BlockPathTypes typeIn) {
            return typeIn == BlockPathTypes.LEAVES || level.getBlockState(pos).getBlock() == Blocks.BAMBOO ? BlockPathTypes.OPEN : super.evaluateBlockPathType(level, pos, typeIn);
        }
    }

    static class Navigator extends AdvancedPathNavigateNoTeleport {

        public Navigator(Mob mob, Level world) {
            super(mob, world, true);
        }

        protected PathFinder createPathFinder(int i) {
            this.nodeEvaluator = new TigerNodeEvaluator();
            return new PathFinder(this.nodeEvaluator, i);
        }
    }
}
