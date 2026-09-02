package com.qiuyue.goetyominous.common.entities.ally.ac;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.ai.SummonTargetGoal;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.projectiles.FlyingItem;
import com.Polarice3.Goety.utils.MobUtil;
import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.entity.ai.AnimalRandomlySwimGoal;
import com.github.alexmodguy.alexscaves.server.entity.ai.VerticalSwimmingMoveControl;
import com.github.alexmodguy.alexscaves.server.entity.item.SubmarineEntity;
import com.github.alexmodguy.alexscaves.server.entity.util.KaijuMob;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.entity.PartEntity;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;

public class HullbreakerServant extends Summoned implements IAnimatedEntity, KaijuMob {

    public static final Animation ANIMATION_PUZZLE = Animation.create(60);
    public static final Animation ANIMATION_BITE = Animation.create(20);
    public static final Animation ANIMATION_BASH = Animation.create(25);
    public static final Animation ANIMATION_DIE = Animation.create(50);

    private static final EntityDataAccessor<Integer> INTEREST_LEVEL = SynchedEntityData.defineId(HullbreakerServant.class, EntityDataSerializers.INT);

    public final HullbreakerServantPartEntity headPart;
    public final HullbreakerServantPartEntity tail1Part;
    public final HullbreakerServantPartEntity tail2Part;
    public final HullbreakerServantPartEntity tail3Part;
    public final HullbreakerServantPartEntity tail4Part;
    private final HullbreakerServantPartEntity[] allParts;
    private Animation currentAnimation;
    private int animationTick;
    private float landProgress;
    private float prevLandProgress;
    private float fishPitch = 0;
    private float prevFishPitch = 0;
    private float pulseAmount;
    private float prevPulseAmount;
    private float[] yawBuffer = new float[128];
    private int yawPointer = -1;
    private int blockBreakCooldown = 0;

    public HullbreakerServant(EntityType<? extends Summoned> entityType, Level level) {
        super(entityType, level);
        headPart = new HullbreakerServantPartEntity(this, this, 3, 2);
        tail1Part = new HullbreakerServantPartEntity(this, this, 2, 2);
        tail2Part = new HullbreakerServantPartEntity(this, tail1Part, 2, 1.5F);
        tail3Part = new HullbreakerServantPartEntity(this, tail2Part, 2.5F, 1.5F);
        tail4Part = new HullbreakerServantPartEntity(this, tail3Part, 1.5F, 1F);
        allParts = new HullbreakerServantPartEntity[]{headPart, tail1Part, tail2Part, tail3Part, tail4Part};
        this.moveControl = new VerticalSwimmingMoveControl(this, 0.7F, 30);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.3D).add(Attributes.MAX_HEALTH, 400.0D).add(Attributes.ATTACK_DAMAGE, 16.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(INTEREST_LEVEL, 0);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData,
                                        @Nullable CompoundTag tag) {
        if (spawnType == MobSpawnType.MOB_SUMMONED && this.getTrueOwner() instanceof Player player) {
            if (countServants(player) >= MobsConfig.HullbreakerServantLimit.get()) {
                return null;
            }
        }
        return super.finalizeSpawn(levelAccessor, difficulty, spawnType, spawnGroupData, tag);
    }

    private int countServants(Player player) {
        int count = 0;
        if (player.level() instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof HullbreakerServant servant && servant != this) {
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
            if (countServants(player) >= MobsConfig.HullbreakerServantLimit.get()) {
                this.discard();
            }
        }
    }

    protected void registerGoals() {
        super.registerGoals();
        
        
        List<WrappedGoal> inherited = new ArrayList<>(this.targetSelector.getAvailableGoals());
        for (WrappedGoal wrapped : inherited) {
            Goal goal = wrapped.getGoal();
            if (goal instanceof SummonTargetGoal || goal instanceof Owned.OwnerHurtTargetGoal || goal instanceof Owned.OwnerHurtByTargetGoal) {
                this.targetSelector.removeGoal(goal);
            }
        }
        
        this.targetSelector.addGoal(2, new GlowingTargetGoal(this));
        this.targetSelector.addGoal(3, new ProximityTargetGoal(this));
        this.goalSelector.addGoal(1, new MeleeGoal());
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(6, new AnimalRandomlySwimGoal(this, 10, 35, 15, 1.0D));
    }

    @Override
    public void followGoal() {
        this.goalSelector.addGoal(5, new Summoned.FollowOwnerWaterGoal(this, 1.0D, 10.0F, 2.0F));
    }

    protected PathNavigation createNavigation(Level level) {
        return new WaterBoundPathNavigation(this, level);
    }

    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isInWaterOrBubble()) {
            this.moveRelative(this.getSpeed(), travelVector);
            Vec3 delta = this.getDeltaMovement();
            this.move(MoverType.SELF, delta);
            this.setDeltaMovement(delta.scale(0.9D));
        } else {
            super.travel(travelVector);
        }
    }

    protected void playSwimSound(float f) {
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public MobType getMobType() {
        return MobType.WATER;
    }

    @Override
    public void tryKill(Player player) {
        if (this.killChance <= 0) {
            this.warnKill(player);
        } else {
            super.tryKill(player);
        }
    }

    protected void tickDeath() {
        this.deathTime++;
        this.setAnimation(ANIMATION_DIE);
        this.setXRot(0.0F);
        this.setYHeadRot(this.getYRot());
        if (this.getAnimation() == ANIMATION_DIE && this.getAnimationTick() > 45 && !this.level().isClientSide() && !this.isRemoved()) {
            
            if (this.getTrueOwner() != null && MobsConfig.HullbreakerServantReturnEmbryo.get()) {
                FlyingItem flyingItem = new FlyingItem(ModEntityType.FLYING_ITEM.get(), this.level(), this.getX(), this.getY(), this.getZ());
                flyingItem.setOwner(this.getTrueOwner());
                flyingItem.setItem(new ItemStack(ACItemRegistry.IMMORTAL_EMBRYO.get()));
                this.level().addFreshEntity(flyingItem);
            }
            this.level().broadcastEntityEvent(this, (byte) 60);
            this.remove(Entity.RemovalReason.KILLED);
        }
        
        if (this.level() instanceof ServerLevel serverLevel) {
            double d0 = this.random.nextGaussian() * 0.02;
            double d1 = this.random.nextGaussian() * 0.02;
            double d2 = this.random.nextGaussian() * 0.02;
            serverLevel.sendParticles((SimpleParticleType) ModParticleTypes.WRAITH.get(), this.getRandomX(1.0D), this.getRandomY(), this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5D);
            serverLevel.sendParticles((SimpleParticleType) ModParticleTypes.WRAITH_BURST.get(), this.getRandomX(1.0D), this.getY(), this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5D);
        }
    }

    @Override
    protected void dropFromLootTable(DamageSource damageSource, boolean b) {
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 0.45F * dimensions.height;
    }

    protected SoundEvent getSwimSound() {
        return SoundEvents.FISH_SWIM;
    }

    public void remove(Entity.RemovalReason removalReason) {
        super.remove(removalReason);
        if (allParts != null) {
            for (PartEntity<?> part : allParts) {
                part.remove(Entity.RemovalReason.KILLED);
            }
        }
    }

    public void tick() {
        tickMultipart();
        super.tick();
        this.yBodyRot = Mth.approachDegrees(this.yBodyRotO, yBodyRot, getHeadRotSpeed());
        prevLandProgress = landProgress;
        prevFishPitch = fishPitch;
        prevPulseAmount = pulseAmount;
        float targetFishPitch = Mth.clamp((float) this.getDeltaMovement().y * 2F, -1.4F, 1.4F) * -(float) (180F / (float) Math.PI);
        if (!isAlive()) {
            targetFishPitch = 0.0F;
        }
        fishPitch = Mth.approachDegrees(fishPitch, targetFishPitch, 2.5F);
        boolean grounded = this.onGround() && !isInWaterOrBubble();
        if (grounded && landProgress < 5F) {
            landProgress++;
        }
        if (!grounded && landProgress > 0F) {
            landProgress--;
        }
        float pulseBy = getInterestLevel() * 0.45F;
        pulseAmount += pulseBy;
        if (!level().isClientSide) {
            double waterHeight = getFluidTypeHeight(ForgeMod.WATER_TYPE.get());
            if (waterHeight > 0 && waterHeight < this.getBbHeight() - 1.0F) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, -0.05, 0));
            }
        }
        if (this.getAnimation() == HullbreakerServant.ANIMATION_BASH && this.getAnimationTick() > 10 && this.getAnimationTick() <= 20) {
            breakBlock();
        }
        if (blockBreakCooldown > 0) {
            blockBreakCooldown--;
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public float getFishPitch(float partialTick) {
        return (prevFishPitch + (fishPitch - prevFishPitch) * partialTick);
    }

    public float getLandProgress(float partialTicks) {
        return (prevLandProgress + (landProgress - prevLandProgress) * partialTicks) * 0.2F;
    }

    public float getPulseAmount(float partialTicks) {
        return (prevPulseAmount + (pulseAmount - prevPulseAmount) * partialTicks) * 0.2F;
    }

    public int getInterestLevel() {
        return this.entityData.get(INTEREST_LEVEL);
    }

    public void setInterestLevel(int level) {
        this.entityData.set(INTEREST_LEVEL, level);
    }

    public int getHeadRotSpeed() {
        return 5;
    }

    public void breakBlock() {
        if (blockBreakCooldown-- > 0) {
            return;
        }
        boolean flag = false;
        AABB damageBox = this.headPart.getBoundingBox().inflate(1.2F).move(this.calculateViewVector(this.getXRot(), this.getYRot()));
        boolean noGriefing = !AttributesConfig.HullbreakerServantBlockBreakGriefing.get()
                || !level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
        
        if (!level().isClientSide && this.getTarget() != null) {
            for (int a = (int) Math.round(damageBox.minX); a <= (int) Math.round(damageBox.maxX); a++) {
                for (int b = (int) Math.round(damageBox.minY) - 1; (b <= (int) Math.round(damageBox.maxY) + 1) && (b <= 127); b++) {
                    for (int c = (int) Math.round(damageBox.minZ); c <= (int) Math.round(damageBox.maxZ); c++) {
                        final BlockPos pos = new BlockPos(a, b, c);
                        final BlockState state = level().getBlockState(pos);
                        boolean softPlant = this.isSoftAquaticPlant(state);
                        if (!state.isAir() && !state.getShape(level(), pos).isEmpty() && !state.is(ACTagRegistry.UNMOVEABLE)
                                && (softPlant || (!noGriefing && state.getBlock().getExplosionResistance() <= 15))) {
                            final Block block = state.getBlock();
                            if (block != Blocks.AIR) {
                                this.setDeltaMovement(this.getDeltaMovement().multiply(0.6F, 1, 0.6F));
                                flag = true;
                                level().destroyBlock(pos, true);
                                if (state.is(BlockTags.ICE)) {
                                    level().setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
                                }
                            }
                        }
                    }
                }
            }
        }
        if (flag) {
            blockBreakCooldown = 3;
        }
    }

    
    private boolean isSoftAquaticPlant(BlockState state) {
        return state.is(BlockTags.CORALS)
                || state.is(BlockTags.CORAL_BLOCKS)
                || state.is(BlockTags.WALL_CORALS)
                || state.is(Blocks.KELP)
                || state.is(Blocks.KELP_PLANT);
    }

    private void tickMultipart() {
        if (yawPointer == -1) {
            for (int i = 0; i < yawBuffer.length; i++) {
                yawBuffer[i] = this.yBodyRot;
            }
        }
        if (++this.yawPointer == this.yawBuffer.length) {
            this.yawPointer = 0;
        }
        this.yawBuffer[this.yawPointer] = this.yBodyRot;

        Vec3[] avector3d = new Vec3[this.allParts.length];
        for (int j = 0; j < this.allParts.length; ++j) {
            avector3d[j] = new Vec3(this.allParts[j].getX(), this.allParts[j].getY(), this.allParts[j].getZ());
        }
        Vec3 center = this.position().add(0, this.getBbHeight() * 0.5F, 0);
        this.headPart.setPosCenteredY(this.rotateOffsetVec(new Vec3(0, 0, 3.5F), fishPitch + this.getXRot(), this.getYHeadRot()).add(center));
        this.tail1Part.setPosCenteredY(this.rotateOffsetVec(new Vec3(swimDegree(1F, 4), 0, -3.5F), fishPitch, this.getYawFromBuffer(2, 1.0F)).add(center));
        this.tail2Part.setPosCenteredY(this.rotateOffsetVec(new Vec3(swimDegree(1F, 3), 0, -2), fishPitch, this.getYawFromBuffer(4, 1.0F)).add(this.tail1Part.centeredPosition()));
        this.tail3Part.setPosCenteredY(this.rotateOffsetVec(new Vec3(swimDegree(2F, 2), 0, -2.65F), fishPitch, this.getYawFromBuffer(6, 1.0F)).add(this.tail2Part.centeredPosition()));
        this.tail4Part.setPosCenteredY(this.rotateOffsetVec(new Vec3(swimDegree(1.5F, 1), 0, -3), fishPitch, this.getYawFromBuffer(8, 1.0F)).add(this.tail3Part.centeredPosition()));
        for (int l = 0; l < this.allParts.length; ++l) {
            this.allParts[l].xo = avector3d[l].x;
            this.allParts[l].yo = avector3d[l].y;
            this.allParts[l].zo = avector3d[l].z;
            this.allParts[l].xOld = avector3d[l].x;
            this.allParts[l].yOld = avector3d[l].y;
            this.allParts[l].zOld = avector3d[l].z;
        }
    }

    private double swimDegree(float width, float sinOffset) {
        double move = Math.cos(this.walkAnimation.position() * 0.33F + sinOffset) * this.walkAnimation.speed() * width * 0.8F;
        double idle = Math.sin((tickCount + AlexsCaves.PROXY.getPartialTicks()) * 0.05F + sinOffset) * width * 0.5F;
        return (move + idle * (1 - this.walkAnimation.speed())) * (1 - getLandProgress(AlexsCaves.PROXY.getPartialTicks()));
    }

    private Vec3 rotateOffsetVec(Vec3 offset, float xRot, float yRot) {
        return offset.xRot(-xRot * ((float) Math.PI / 180F)).yRot(-yRot * ((float) Math.PI / 180F));
    }

    public float getYawFromBuffer(int pointer, float partialTick) {
        int i = this.yawPointer - pointer & 127;
        int j = this.yawPointer - pointer - 1 & 127;
        float d0 = this.yawBuffer[j];
        float d1 = this.yawBuffer[i] - d0;
        return d0 + d1 * partialTick;
    }

    public void calculateEntityAnimation(boolean flying) {
        float f1 = (float) Mth.length(this.getX() - this.xo, this.getY() - this.yo, this.getZ() - this.zo);
        float f2 = Math.min(f1 * 3.0F, 1.0F);
        this.walkAnimation.update(f2, 0.4F);
    }

    @Override
    public boolean isMultipartEntity() {
        return true;
    }

    @Override
    public PartEntity<?>[] getParts() {
        return allParts;
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
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypeTags.IS_PROJECTILE)) {
            amount *= 0.65F;
        }
        return super.hurt(source, amount);
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_PUZZLE, ANIMATION_BITE, ANIMATION_BASH, ANIMATION_DIE};
    }

    protected SoundEvent getAmbientSound() {
        return isInWaterOrBubble() ? ACSoundRegistry.HULLBREAKER_IDLE.get() : ACSoundRegistry.HULLBREAKER_LAND_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return isInWaterOrBubble() ? ACSoundRegistry.HULLBREAKER_HURT.get() : ACSoundRegistry.HULLBREAKER_LAND_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return isInWaterOrBubble() ? ACSoundRegistry.HULLBREAKER_DEATH.get() : ACSoundRegistry.HULLBREAKER_LAND_DEATH.get();
    }

    protected float getSoundVolume() {
        return super.getSoundVolume() + 2.0F;
    }

    
    private class GlowingTargetGoal extends NearestAttackableTargetGoal<LivingEntity> {

        private GlowingTargetGoal(Mob mob) {
            super(mob, LivingEntity.class, 5, false, false, target ->
                    target.hasEffect(MobEffects.GLOWING)
                            && !MobUtil.areAllies(HullbreakerServant.this, target)
                            && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target));
        }


        @Override
        protected double getFollowDistance() {
            return AttributesConfig.HullbreakerServantGlowTargetRange.get();
        }
    }

    private class ProximityTargetGoal extends NearestAttackableTargetGoal<LivingEntity> {

        private ProximityTargetGoal(Mob mob) {
            super(mob, LivingEntity.class, 5, false, false, target ->
                    MobUtil.isOwnedTargetable(HullbreakerServant.this, target));
        }

        @Override
        protected double getFollowDistance() {
            return AttributesConfig.HullbreakerServantProximityTargetRange.get();
        }

        @Override
        protected AABB getTargetSearchArea(double distance) {
            return this.mob.getBoundingBox().inflate(distance, distance, distance);
        }
    }

    private class MeleeGoal extends Goal {

        private MeleeGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = HullbreakerServant.this.getTarget();
            return target != null && target.isAlive() && !HullbreakerServant.this.isStaying();
        }

        public void start() {
            HullbreakerServant.this.setInterestLevel(6);
        }

        public void stop() {
            HullbreakerServant.this.setInterestLevel(0);
        }

        public void tick() {
            LivingEntity target = HullbreakerServant.this.getTarget();
            if (target == null) {
                return;
            }
            double dist = HullbreakerServant.this.distanceTo(target);
            float f = HullbreakerServant.this.getBbWidth() + target.getBbWidth();
            HullbreakerServant.this.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
            if (dist < (double) f + 7.0F && HullbreakerServant.this.getAnimation() == IAnimatedEntity.NO_ANIMATION && this.isTargetInFront(target)) {
                this.tryAnimation(HullbreakerServant.this.getRandom().nextBoolean() && HullbreakerServant.this.hasLineOfSight(target) ? HullbreakerServant.ANIMATION_BITE : HullbreakerServant.ANIMATION_BASH);
            }
            if (dist > (double) (f + 2.0F)) {
                double chaseSpeed = target.hasEffect(MobEffects.GLOWING)
                        ? AttributesConfig.HullbreakerServantGlowChaseSpeed.get()
                        : 1.6D;
                HullbreakerServant.this.getNavigation().moveTo(target, chaseSpeed);
            }
            if (HullbreakerServant.this.getAnimation() == HullbreakerServant.ANIMATION_BITE && HullbreakerServant.this.getAnimationTick() > 10 && HullbreakerServant.this.getAnimationTick() <= 14) {
                this.checkAndDealDamage(target, 1.0F);
            }
            if (HullbreakerServant.this.getAnimation() == HullbreakerServant.ANIMATION_BASH && HullbreakerServant.this.getAnimationTick() > 10 && HullbreakerServant.this.getAnimationTick() <= 12) {
                this.checkAndDealDamage(target, 1.5F);
            }
            SubmarineEntity.alertSubmarineMountOf(target);
        }

        private boolean isTargetInFront(LivingEntity target) {
            Vec3 look = HullbreakerServant.this.getLookAngle();
            Vec3 toTarget = target.getEyePosition()
                    .subtract(HullbreakerServant.this.getEyePosition())
                    .normalize();
            return look.dot(toTarget) > 0.5F;
        }

        private void checkAndDealDamage(LivingEntity target, float multiplier) {
            if (this.isTargetInFront(target) && HullbreakerServant.this.hasLineOfSight(target) && (double) HullbreakerServant.this.distanceTo(target) < (double) (HullbreakerServant.this.getBbWidth() + target.getBbWidth()) + 5.0F) {
                float f = (float) HullbreakerServant.this.getAttributeValue(Attributes.ATTACK_DAMAGE) * multiplier;
                target.hurt(target.damageSources().mobAttack(HullbreakerServant.this), f);
                target.knockback(0.8 + 0.5F * multiplier, HullbreakerServant.this.getX() - target.getX(), HullbreakerServant.this.getZ() - target.getZ());
                Entity entity = target.getVehicle();
                if (entity != null) {
                    entity.setDeltaMovement(target.getDeltaMovement());
                    entity.hurt(target.damageSources().mobAttack(HullbreakerServant.this), f * 0.5F);
                }
            }
        }

        private boolean tryAnimation(Animation animation) {
            if (HullbreakerServant.this.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                HullbreakerServant.this.setAnimation(animation);
                if (HullbreakerServant.this.isInWaterOrBubble()) {
                    HullbreakerServant.this.playSound(ACSoundRegistry.HULLBREAKER_ATTACK.get());
                }
                return true;
            } else {
                return false;
            }
        }
    }
}
