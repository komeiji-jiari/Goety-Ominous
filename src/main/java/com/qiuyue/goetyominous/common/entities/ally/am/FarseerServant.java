package com.qiuyue.goetyominous.common.entities.ally.am;

import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.config.MobsConfig;
import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.alexsmobs.message.MessageSendVisualFlagFromServer;
import com.github.alexthe666.alexsmobs.misc.AMDamageTypes;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;

import java.util.EnumSet;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FarseerServant extends Summoned implements IAnimatedEntity {

    public static final Animation ANIMATION_EMERGE = Animation.create(50);
    private static final int HANDS = 4;
    private static final EntityDataAccessor<Boolean> ANGRY = SynchedEntityData.defineId(FarseerServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HAS_EMERGED = SynchedEntityData.defineId(FarseerServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> MELEEING = SynchedEntityData.defineId(FarseerServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> LASER_ENTITY_ID = SynchedEntityData.defineId(FarseerServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> LASER_ATTACK_LVL = SynchedEntityData.defineId(FarseerServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> LASER_DISTANCE = SynchedEntityData.defineId(FarseerServant.class, EntityDataSerializers.FLOAT);
    public static final int LASER_ATTACK_DURATION = 10;
    public final double[][] positions = new double[64][4];
    public final float[] claspProgress = new float[HANDS];
    public final float[] prevClaspProgress = new float[HANDS];
    public final float[] strikeProgress = new float[HANDS];
    public final float[] prevStrikeProgress = new float[HANDS];
    public final boolean[] isStriking = new boolean[HANDS];
    public int posPointer = -1;
    public float angryProgress;
    public float prevAngryProgress;
    public Vec3 angryShakeVec = Vec3.ZERO;
    public float prevLaserLvl;
    private float faceCameraProgress;
    private float prevFaceCameraProgress;
    private LivingEntity laserTargetEntity;
    private int claspingHand = -1;
    private int animationTick;
    private Animation currentAnimation;
    private int meleeCooldown = 0;

    public FarseerServant(EntityType<? extends Owned> type, Level level) {
        super(type, level);
        this.moveControl = new MoveController();
        this.xpReward = 20;
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.FarseerServantHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.FarseerServantArmor.get())
                .add(Attributes.FLYING_SPEED, AttributesConfig.FarseerServantFlyingSpeed.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.FarseerServantDamage.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.FarseerServantMovementSpeed.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.FarseerServantFollowRange.get());
    }

    public boolean isNoGravity() {
        return true;
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    @Override
    public boolean canBeCommanded() {
        return false;
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return dimensions.height * 0.7F;
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, level());
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(true);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AttackGoal());
        this.goalSelector.addGoal(3, new RandomFlyGoal(this));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 15.0F, 1.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    @Override
    public void followGoal() {
        this.goalSelector.addGoal(2, new FlyToOwnerGoal(this, 1.0D, 10.0F, 2.0F));
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Emerged", this.hasEmerged());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setHasEmerged(compound.getBoolean("Emerged"));
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.FARSEER_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.FARSEER_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.FARSEER_HURT.get();
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HAS_EMERGED, false);
        this.entityData.define(MELEEING, false);
        this.entityData.define(ANGRY, false);
        this.entityData.define(LASER_ENTITY_ID, -1);
        this.entityData.define(LASER_ATTACK_LVL, 0);
        this.entityData.define(LASER_DISTANCE, 0F);
    }

    public boolean isAngry() {
        return this.entityData.get(ANGRY);
    }

    public void setAngry(boolean angry) {
        this.entityData.set(ANGRY, Boolean.valueOf(angry));
    }

    public boolean hasLaser() {
        return this.entityData.get(LASER_ENTITY_ID) != -1 && this.getAnimation() != FarseerServant.ANIMATION_EMERGE;
    }

    public int getLaserAttackLvl() {
        return this.entityData.get(LASER_ATTACK_LVL);
    }

    public float getLaserDistance() {
        return this.entityData.get(LASER_DISTANCE);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_32834_) {
        super.onSyncedDataUpdated(p_32834_);
        if (LASER_ENTITY_ID.equals(p_32834_)) {
            this.laserTargetEntity = null;
        }
    }

    @Nullable
    public LivingEntity getLaserTarget() {
        if (!this.hasLaser()) {
            return null;
        } else if (this.level().isClientSide) {
            if (this.laserTargetEntity != null) {
                return this.laserTargetEntity;
            } else {
                Entity fromID = this.level().getEntity(this.entityData.get(LASER_ENTITY_ID));
                if (fromID instanceof LivingEntity) {
                    this.laserTargetEntity = (LivingEntity) fromID;
                    return this.laserTargetEntity;
                } else {
                    return null;
                }
            }
        } else {
            return this.getTarget();
        }
    }

    public boolean hasEmerged() {
        return this.entityData.get(HAS_EMERGED);
    }

    public void setHasEmerged(boolean emerged) {
        this.entityData.set(HAS_EMERGED, Boolean.valueOf(emerged));
    }

    public void tick() {
        super.tick();
        prevFaceCameraProgress = faceCameraProgress;
        prevLaserLvl = this.getLaserAttackLvl();
        if (this.getAnimation() == ANIMATION_EMERGE) {
            this.setHasEmerged(true);
            faceCameraProgress = 1F;
        } else if (faceCameraProgress > 0.0F) {
            faceCameraProgress = Math.max(0, faceCameraProgress - 0.2F);
        }
        prevAngryProgress = angryProgress;
        for (int i = 0; i < HANDS; i++) {
            prevClaspProgress[i] = claspProgress[i];
            prevStrikeProgress[i] = strikeProgress[i];
        }
        if (this.posPointer < 0) {
            for (int i = 0; i < this.positions.length; ++i) {
                this.positions[i][0] = this.getX();
                this.positions[i][1] = this.getY();
                this.positions[i][2] = this.getZ();
                this.positions[i][3] = this.yBodyRot;
            }
        }
        if (++this.posPointer == this.positions.length) {
            this.posPointer = 0;
        }
        this.positions[this.posPointer][0] = this.getX();
        this.positions[this.posPointer][1] = this.getY();
        this.positions[this.posPointer][2] = this.getZ();
        this.positions[this.posPointer][3] = this.yBodyRot;
        if (this.isAngry() && angryProgress < 5F) {
            angryProgress++;
        }
        if (!this.isAngry() && angryProgress > 0F) {
            angryProgress--;
        }
        if (this.isAlive()) {
            if (random.nextInt(isAngry() ? 12 : 40) == 0 && claspingHand == -1) {
                int i = Mth.clamp(random.nextInt(HANDS), 0, 3);
                if (claspProgress[i] == 0) {
                    claspingHand = i;
                }
            }
            if (claspingHand >= 0) {
                if (claspProgress[claspingHand] < 5F) {
                    claspProgress[claspingHand]++;
                } else {
                    claspingHand = -1;
                }
            } else {
                for (int i = 0; i < HANDS; i++) {
                    if (claspProgress[i] > 0) {
                        claspProgress[i]--;
                    }
                }
            }
            if (!this.hasEmerged()) {
                this.setInvisible(true);
                this.setAnimation(ANIMATION_EMERGE);
            } else {
                this.setInvisible(this.hasEffect(MobEffects.INVISIBILITY));
            }
            if (this.getAnimation() == ANIMATION_EMERGE) {
                if(this.level().isClientSide){
                    this.level().addParticle(AMParticleRegistry.STATIC_SPARK.get(), this.getRandomX(0.75F), this.getRandomY(), this.getRandomZ(0.75F), (this.getRandom().nextFloat() - 0.5F) * 0.2F, this.getRandom().nextFloat() * 0.2F, (this.getRandom().nextFloat() - 0.5F) * 0.2F);
                }
                if(this.getAnimationTick() == 1){
                    this.playSound(AMSoundRegistry.FARSEER_EMERGE.get(), this.getSoundVolume(), this.getVoicePitch());
                }
            }
            LivingEntity target = this.getTarget();
            if (target != null) {
                if (this.entityData.get(MELEEING)) {
                    if (meleeCooldown == 0) {
                        meleeCooldown = 5;
                        int i = random.nextInt(HANDS);
                        this.isStriking[i] = true;
                        this.level().broadcastEntityEvent(this, (byte) (40 + i));
                    }
                }
            }
            if (meleeCooldown > 0) {
                meleeCooldown--;
            }
            for (int i = 0; i < HANDS; i++) {
                if (!this.isStriking[i] || !this.entityData.get(MELEEING)) {
                    if (strikeProgress[i] > 0F) {
                        strikeProgress[i]--;
                    }
                } else if (this.isStriking[i]) {
                    if (strikeProgress[i] < 5F) {
                        strikeProgress[i]++;
                    }
                    if (strikeProgress[i] == 5F) {
                        isStriking[i] = false;
                        this.level().broadcastEntityEvent(this, (byte) (44 + i));
                        if (target != null && distanceTo(target) <= 4F) {
                            target.hurt(this.damageSources().mobAttack(this), 5 + random.nextInt(5));
                        }
                    }
                }
            }

            if (this.hasLaser()) {
                LivingEntity livingentity = this.getLaserTarget();
                if (livingentity != null) {
                    Vec3 hit = this.calculateLaserHit(livingentity.getEyePosition());
                    this.entityData.set(LASER_DISTANCE, (float) hit.distanceTo(this.getEyePosition()));
                    this.getLookControl().setLookAt(livingentity, 90.0F, 90.0F);
                    this.getLookControl().tick();
                    double d0 = hit.x - this.getX();
                    double d1 = hit.y - this.getEyeY();
                    double d2 = hit.z - this.getZ();
                    double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                    d0 = d0 / d3;
                    d1 = d1 / d3;
                    d2 = d2 / d3;
                    float progress = this.getLaserAttackLvl() / (float) LASER_ATTACK_DURATION;
                    double d4 = this.random.nextDouble();
                    while (d4 < d3 * progress) {
                        d4 += 0.5F + 2F * this.random.nextDouble();
                        double width = d4 / (d3 * progress);
                        double d5 = (random.nextDouble() - 0.5F) * width;
                        double d6 = (random.nextDouble() - 0.5F) * width;
                        this.level().addParticle(AMParticleRegistry.STATIC_SPARK.get(), this.getX() + d0 * d4 + d5, this.getEyeY() + d1 * d4, this.getZ() + d2 * d4 + d6, (this.getRandom().nextFloat() - 0.5F) * 0.2F, this.getRandom().nextFloat() * 0.2F, (this.getRandom().nextFloat() - 0.5F) * 0.2F);
                    }
                }
            }
        }
        if (this.isAngry()) {
            angryShakeVec = new Vec3(random.nextFloat() - 0.5F, random.nextFloat() - 0.5F, random.nextFloat() - 0.5F);
        } else {
            angryShakeVec = Vec3.ZERO;
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id >= 40 && id <= 43) {
            int i = id - 40;
            isStriking[i] = true;
        } else if (id >= 44 && id <= 48) {
            int i = id - 44;
            isStriking[i] = false;
        } else {
            super.handleEntityEvent(id);
        }
    }

    public double getLatencyVar(int pointer, int index, float partialTick) {
        if (this.isDeadOrDying()) {
            partialTick = 1.0F;
        }
        int i = this.posPointer - pointer & 63;
        int j = this.posPointer - pointer - 1 & 63;
        double d0 = this.positions[j][index];
        double d1 = Mth.wrapDegrees(this.positions[i][index] - d0);
        return d0 + d1 * partialTick;
    }

    public Vec3 getLatencyOffsetVec(int offset, float partialTick) {
        double d0 = Mth.lerp(partialTick, this.xOld, this.getX());
        double d1 = Mth.lerp(partialTick, this.yOld, this.getY());
        double d2 = Mth.lerp(partialTick, this.zOld, this.getZ());
        float renderYaw = (float) this.getLatencyVar(offset, 3, partialTick);
        return new Vec3(this.getLatencyVar(offset, 0, partialTick) - d0, this.getLatencyVar(offset, 1, partialTick) - d1, this.getLatencyVar(offset, 2, partialTick) - d2).yRot(renderYaw * Mth.DEG_TO_RAD);
    }

    public Vec3 calculateAfterimagePos(float partialTick, boolean flip, float speed) {
        float f = (partialTick + this.tickCount) * speed;
        float f1 = 0.1F;
        Vec3 v = new Vec3((float) Math.sin(f) * f1, (float) Math.cos(f - Math.PI / 2) * f1, -(float) Math.cos(f) * f1);
        if (flip) {
            return new Vec3(v.z, -v.y, v.x);
        }
        return v;
    }

    @Override
    public int getAnimationTick() {
        return animationTick;
    }

    @Override
    public void setAnimationTick(int i) {
        animationTick = i;
    }

    @Override
    public Animation getAnimation() {
        return currentAnimation;
    }

    @Override
    public void setAnimation(Animation animation) {
        this.currentAnimation = animation;
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_EMERGE};
    }

    public int getPortalFrame() {
        if (this.getAnimation() == ANIMATION_EMERGE) {
            if (this.getAnimationTick() < 10) {
                return 0;
            } else if (this.getAnimationTick() < 20) {
                return 1;
            } else if (this.getAnimationTick() < 30) {
                return 2;
            } else if (this.getAnimationTick() > 40) {
                int i = 50 - this.getAnimationTick();
                return i < 6 ? i < 3 ? 0 : 1 : 2;
            } else {
                return 3;
            }
        }
        return 0;
    }

    public float getPortalOpacity(float partialTicks) {
        if (this.getAnimation() == ANIMATION_EMERGE) {
            float tick = this.getAnimationTick() - 1 + partialTicks;
            if (tick < 5F) {
                return tick / 5F;
            }
            return 1.0F;
        }
        return 0.0F;
    }

    public float getFarseerOpacity(float partialTicks) {
        if (this.getAnimation() == ANIMATION_EMERGE) {
            float tick = this.getAnimationTick() - 1 + partialTicks;
            float prog = tick / (float) ANIMATION_EMERGE.getDuration();
            return prog > 0.5F ? (prog - 0.5F) / 0.5F : 0F;
        }
        return 1.0F;
    }

    public float getFacingCameraAmount(float partialTicks) {
        return prevFaceCameraProgress + (faceCameraProgress - prevFaceCameraProgress) * partialTicks;
    }

    public boolean isEffectiveAi() {
        return super.isEffectiveAi() && this.getAnimation() != ANIMATION_EMERGE && this.hasEmerged();
    }

    private Vec3 calculateLaserHit(Vec3 target) {
        Vec3 eyes = new Vec3(this.getX(), this.getEyeY(), this.getZ());
        HitResult hitResult = this.level().clip(new ClipContext(eyes, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        return hitResult.getLocation();
    }

    public boolean isTargetBlocked(Vec3 target) {
        Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
        return this.level().clip(new ClipContext(Vector3d, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() != HitResult.Type.MISS;
    }

    public void travel(Vec3 vec3) {
        if (this.isEffectiveAi() || this.isControlledByLocalInstance()) {
            if (this.isInWater()) {
                this.moveRelative(0.02F, vec3);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.8F));
            } else if (this.isInLava()) {
                this.moveRelative(0.02F, vec3);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.5D));
            } else {
                this.moveRelative(this.getSpeed(), vec3);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.91F));
            }
        }

        this.calculateEntityAnimation(false);
    }

    public boolean isInvulnerableTo(DamageSource dmg) {
        return super.isInvulnerableTo(dmg) || this.getAnimation() == ANIMATION_EMERGE;
    }

    private static class RandomFlyGoal extends Goal {
        private final FarseerServant parentEntity;
        private BlockPos target = null;
        private final float speed = 0.6F;
        public RandomFlyGoal(FarseerServant mosquito) {
            this.parentEntity = mosquito;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            if (this.parentEntity.getNavigation().isDone() && this.parentEntity.getTarget() == null
                    && !this.parentEntity.isStaying() && !this.parentEntity.isCommanded()
                    && this.parentEntity.getRandom().nextInt(4) == 0) {
                target = getBlockInViewFarseer();
                if (target != null) {
                    this.parentEntity.getMoveControl().setWantedPosition(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D, speed);
                    return true;
                }
            }
            return false;
        }

        public boolean canContinueToUse() {
            return target != null && parentEntity.getTarget() == null && !parentEntity.isStaying() && !parentEntity.isCommanded();
        }

        public void stop() {
            target = null;
        }

        public void tick() {
            if (target != null) {
                this.parentEntity.getMoveControl().setWantedPosition(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D, speed);
                if (parentEntity.distanceToSqr(Vec3.atCenterOf(target)) < 4D || this.parentEntity.horizontalCollision) {
                    target = null;
                }
            }
        }

        private BlockPos getFarseerGround(BlockPos in) {
            BlockPos position = new BlockPos(in.getX(), (int) parentEntity.getY(), in.getZ());
            while (position.getY() < 256 && !parentEntity.level().getFluidState(position).isEmpty()) {
                position = position.above();
            }
            while (position.getY() > 1 && parentEntity.level().isEmptyBlock(position)) {
                position = position.below();
            }
            return position;
        }

        public BlockPos getBlockInViewFarseer() {
            float neg = parentEntity.getRandom().nextBoolean() ? 1 : -1;
            float renderYawOffset = parentEntity.getYRot();
            float angle = (Maths.STARTING_ANGLE * renderYawOffset) + 3.15F * (parentEntity.getRandom().nextFloat() * neg);
            float radius;
            BlockPos center;
            if (parentEntity.isGuardingArea() && parentEntity.getBoundPos() != null) {
                center = parentEntity.getBoundPos();
                radius = 2 + parentEntity.getRandom().nextInt(Math.max(2, (int) (IServant.GUARDING_RANGE * 0.5F)));
            } else if (parentEntity.isFollowing() && parentEntity.getTrueOwner() != null) {
                center = parentEntity.getTrueOwner().blockPosition();
                radius = 3 + parentEntity.getRandom().nextInt(5);
            } else {
                center = parentEntity.blockPosition();
                radius = 5 + parentEntity.getRandom().nextInt(10);
            }
            double extraX = radius * Mth.sin(Mth.PI + angle);
            double extraZ = radius * Mth.cos(angle);
            BlockPos radialPos = new BlockPos((int) (center.getX() + extraX), (int) parentEntity.getY(), (int) (center.getZ() + extraZ));
            BlockPos ground = getFarseerGround(radialPos).above(2 + parentEntity.random.nextInt(2));

            if (!parentEntity.isTargetBlocked(Vec3.atCenterOf(ground.above()))) {
                return ground;
            }
            return null;
        }

    }


    private boolean canUseLaser() {
        return !this.hasEffect(MobEffects.BLINDNESS);
    }

    private class AttackGoal extends Goal {

        private boolean attackDecision = true;
        private int timeSinceLastSuccessfulAttack = 0;
        private int laserCooldown = 0;
        private int laserUseTime = 0;
        private int lasersShot = 0;

        public AttackGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return FarseerServant.this.getTarget() != null && FarseerServant.this.getTarget().isAlive();
        }

        public void stop() {
            this.lasersShot = 0;
            this.laserCooldown = 0;
            this.laserUseTime = 0;
            attackDecision = FarseerServant.this.getRandom().nextBoolean();
            FarseerServant.this.entityData.set(LASER_ENTITY_ID, -1);
            timeSinceLastSuccessfulAttack = 0;
            FarseerServant.this.setAngry(false);
        }

        public void tick() {
            super.tick();
            LivingEntity target = FarseerServant.this.getTarget();
            if (laserCooldown > 0) {
                laserCooldown--;
            }
            timeSinceLastSuccessfulAttack++;
            if (timeSinceLastSuccessfulAttack > 100) {
                timeSinceLastSuccessfulAttack = 0;
                attackDecision = !attackDecision;
            }
            if (target != null) {
                double dist = FarseerServant.this.distanceTo(target);
                boolean canLaserHit = willLaserHit(target);
                if (this.laserCooldown == 0 && attackDecision && canLaserHit && dist > 2F) {
                    FarseerServant.this.setAngry(true);
                    FarseerServant.this.entityData.set(LASER_ENTITY_ID, target.getId());
                    if(laserUseTime == 0){
                        FarseerServant.this.playSound(AMSoundRegistry.FARSEER_BEAM.get(), FarseerServant.this.getSoundVolume(), FarseerServant.this.getVoicePitch());
                    }
                    laserUseTime++;
                    if (laserUseTime > LASER_ATTACK_DURATION) {
                        laserUseTime = 0;
                        if (canLaserHit) {
                            float healthTenth = target.getMaxHealth() * 0.1F;
                            if(target.hurt(AMDamageTypes.causeFarseerDamage(FarseerServant.this), random.nextInt(2) + Math.max(6, healthTenth)) && !target.isAlive()){
                                AlexsMobs.sendMSGToAll(new MessageSendVisualFlagFromServer(target.getId(), 87));
                            }
                            timeSinceLastSuccessfulAttack = 0;
                        }
                        if (lasersShot++ > 5) {
                            lasersShot = 0;
                            laserCooldown = 80 + random.nextInt(40);
                            FarseerServant.this.entityData.set(LASER_ENTITY_ID, -1);
                            attackDecision = FarseerServant.this.getRandom().nextBoolean();
                        }
                    }
                    FarseerServant.this.entityData.set(LASER_ATTACK_LVL, laserUseTime);
                    FarseerServant.this.lookAt(target, 180F, 180F);
                    if (FarseerServant.this.isStaying() || (dist < 17F && canLaserHit)) {
                        FarseerServant.this.getNavigation().stop();
                    } else {
                        FarseerServant.this.getNavigation().moveTo(target, 1F);
                    }
                    FarseerServant.this.entityData.set(MELEEING, false);
                } else {
                    if (!canLaserHit && dist > 10) {
                        FarseerServant.this.setAngry(false);
                    }
                    if (FarseerServant.this.hasLaser()) {
                        FarseerServant.this.entityData.set(LASER_ENTITY_ID, -1);
                    }
                    FarseerServant.this.entityData.set(MELEEING, dist < 4F);
                    if (dist < 4F) {
                        timeSinceLastSuccessfulAttack = 0;
                    } else if (!FarseerServant.this.isStaying()) {
                        FarseerServant.this.getNavigation().moveTo(target, 1F);
                        FarseerServant.this.moveControl.setWantedPosition(target.getX(), target.getEyeY(), target.getZ(), 1F);
                    }
                }
            }
        }

        private boolean willLaserHit(LivingEntity target) {
            Vec3 vec = FarseerServant.this.calculateLaserHit(target.getEyePosition());
            return vec.distanceTo(target.getEyePosition()) < 1F && FarseerServant.this.canUseLaser();
        }
    }

    class MoveController extends MoveControl {
        private final Mob parentEntity;


        public MoveController() {
            super(FarseerServant.this);
            this.parentEntity = FarseerServant.this;
        }

        public void tick() {
            boolean preciseMode = FarseerServant.this.isCommanded() || FarseerServant.this.isStaying();
            float angle = (Maths.STARTING_ANGLE * (parentEntity.yBodyRot + 90));
            float radius = preciseMode ? 0.0F : (float) Math.sin(parentEntity.tickCount * 0.2F) * 2;
            double extraX = radius * Mth.sin(Mth.PI + angle);
            double extraY = radius * -Math.cos(angle - Math.PI / 2);
            double extraZ = radius * Mth.cos(angle);
            Vec3 strafPlus = new Vec3(extraX, extraY, extraZ);
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                Vec3 vector3d = new Vec3(this.wantedX - parentEntity.getX(), this.wantedY - parentEntity.getY(), this.wantedZ - parentEntity.getZ());
                double d0 = vector3d.length();
                double width = parentEntity.getBoundingBox().getSize();
                Vec3 shimmy = Vec3.ZERO;
                LivingEntity attackTarget = parentEntity.getTarget();
                if (attackTarget != null) {
                    if (parentEntity.horizontalCollision) {
                        shimmy = new Vec3(0, 0.005, 0);
                    }
                }

                if (d0 < width) {
                    this.operation = MoveControl.Operation.WAIT;
                    parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().scale(0.5D));
                } else {
                    Vec3 vector3d1 = vector3d.scale(this.speedModifier * 0.05D / d0);
                    parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().add(vector3d1.add(strafPlus.scale(0.003D * Math.min(d0, 100)).add(shimmy))));
                    parentEntity.setYRot(-((float) Mth.atan2(vector3d1.x, vector3d1.z)) * Mth.RAD_TO_DEG);
                    if (FarseerServant.this.hasLaser()) {
                        parentEntity.yBodyRot = parentEntity.getYRot();
                    }
                }

            } else if (this.operation == MoveControl.Operation.WAIT) {
                parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().add(strafPlus.scale(0.003D)));
            }
        }
    }

    public static class FlyToOwnerGoal extends Goal {
        public final FarseerServant summonedEntity;
        public LivingEntity owner;
        public final double followSpeed;
        public int timeToRecalcPath;
        public final float stopDistance;
        public final float startDistance;

        public FlyToOwnerGoal(FarseerServant summonedEntity, double speed, float startDistance, float stopDistance) {
            this.summonedEntity = summonedEntity;
            this.followSpeed = speed;
            this.startDistance = startDistance;
            this.stopDistance = stopDistance;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity livingentity = this.summonedEntity.getTrueOwner();
            if (livingentity == null) {
                return false;
            } else if (livingentity.isSpectator()) {
                return false;
            } else if (this.summonedEntity.distanceToSqr(livingentity) < (double) (Mth.square(this.startDistance))) {
                return false;
            } else if (!this.summonedEntity.isFollowing() || this.summonedEntity.isCommanded()) {
                return false;
            } else if (this.summonedEntity.getTarget() != null) {
                return false;
            } else {
                this.owner = livingentity;
                return true;
            }
        }

        public boolean canContinueToUse() {
            if (this.summonedEntity.getTarget() != null) {
                return false;
            } else if (this.summonedEntity.isPassenger()) {
                return false;
            } else if (this.owner == null || !this.owner.isAlive()) {
                return false;
            } else if (!this.summonedEntity.isFollowing() || this.summonedEntity.isCommanded()) {
                return false;
            } else {
                return !(this.summonedEntity.distanceToSqr(this.owner) <= (double) (Mth.square(this.stopDistance)));
            }
        }

        public void start() {
            this.timeToRecalcPath = 0;
        }

        public void stop() {
            this.owner = null;
            this.summonedEntity.getMoveControl().setWantedPosition(this.summonedEntity.getX(), this.summonedEntity.getY(), this.summonedEntity.getZ(), 0.0D);
        }

        public void tick() {
            if (this.owner != null) {
                this.summonedEntity.getLookControl().setLookAt(this.owner, 10.0F, (float) this.summonedEntity.getMaxHeadXRot());
                if (--this.timeToRecalcPath <= 0) {
                    this.timeToRecalcPath = 10;
                    double range = this.owner instanceof Mob ? 32.0D : 16.0D;
                    boolean flag = this.summonedEntity.distanceToSqr(this.owner) >= Mth.square(range);
                    if (this.owner instanceof Mob) {
                        flag |= !this.summonedEntity.hasLineOfSight(this.owner) && this.summonedEntity.distanceToSqr(this.owner) >= Mth.square(8.0D);
                    } else {
                        flag &= this.canTeleport();
                    }
                    if (flag) {
                        this.tryToTeleportNearEntity();
                    } else {
                        this.summonedEntity.getMoveControl().setWantedPosition(this.owner.getX(), this.owner.getY() + this.summonedEntity.getBbHeight() * 0.5F, this.owner.getZ(), this.followSpeed);
                    }
                }
            }
        }

        protected boolean canTeleport() {
            return MobsConfig.ServantTeleport.get();
        }

        protected void tryToTeleportNearEntity() {
            BlockPos blockpos = this.owner.blockPosition();

            for (int i = 0; i < 10; ++i) {
                int j = this.getRandomNumber(-3, 3);
                int k = this.getRandomNumber(-1, 1);
                int l = this.getRandomNumber(-3, 3);
                boolean flag = this.tryToTeleportToLocation(blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l);
                if (flag) {
                    return;
                }
            }

        }

        protected boolean tryToTeleportToLocation(int x, int y, int z) {
            if (Math.abs((double) x - this.owner.getX()) < 2.0D && Math.abs((double) z - this.owner.getZ()) < 2.0D) {
                return false;
            } else if (!this.isTeleportFriendlyBlock(new BlockPos(x, y, z))) {
                return false;
            } else {
                this.summonedEntity.moveTo((double) x + 0.5D, (double) y, (double) z + 0.5D, this.summonedEntity.getYRot(), this.summonedEntity.getXRot());
                this.summonedEntity.getMoveControl().setWantedPosition(this.summonedEntity.getX(), this.summonedEntity.getY(), this.summonedEntity.getZ(), 0.0D);
                return true;
            }
        }

        protected boolean isTeleportFriendlyBlock(BlockPos pos) {
            BlockPathTypes pathnodetype = WalkNodeEvaluator.getBlockPathTypeStatic(this.summonedEntity.level(), pos.mutable());
            if (pathnodetype != BlockPathTypes.WALKABLE) {
                return false;
            } else {
                BlockState blockstate = this.summonedEntity.level().getBlockState(pos.below());
                if (blockstate.getBlock() instanceof LeavesBlock) {
                    return false;
                } else {
                    BlockPos blockpos = pos.subtract(this.summonedEntity.blockPosition());
                    return this.summonedEntity.level().noCollision(this.summonedEntity, this.summonedEntity.getBoundingBox().move(blockpos));
                }
            }
        }

        protected int getRandomNumber(int min, int max) {
            return this.summonedEntity.getRandom().nextInt(max - min + 1) + min;
        }
    }
}
