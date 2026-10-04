package com.qiuyue.goetyominous.common.entities.ally.mobs;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.utils.EffectsUtil;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.common.entities.ai.BreezeIdleSlideGoal;
import com.qiuyue.goetyominous.common.entities.ai.BreezeLike;
import com.qiuyue.goetyominous.common.entities.hostile.BreezeEntity;
import com.qiuyue.goetyominous.common.entities.projectile.AbstractWindCharge;
import com.qiuyue.goetyominous.common.entities.projectile.ServantWindCharge;
import com.qiuyue.goetyominous.common.init.ModSounds;
import com.qiuyue.goetyominous.common.init.ModTags;
import com.qiuyue.goetyominous.common.items.ModItems;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.utils.LongJumpUtil;
import com.qiuyue.goetyominous.utils.ProjectileDeflection;
import com.qiuyue.goetyominous.utils.ProjectileDeflector;
import com.qiuyue.goetyominous.utils.ServantCombatUtil;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class BreezeServant extends Summoned implements ProjectileDeflector, BreezeLike {
    private static final int SLIDE_PARTICLES_AMOUNT = 20;
    private static final int IDLE_PARTICLES_AMOUNT = 1;
    private static final int JUMP_TRAIL_PARTICLES_AMOUNT = 3;
    private static final int JUMP_TRAIL_DURATION_TICKS = 5;
    private static final float FALL_DISTANCE_SOUND_TRIGGER_THRESHOLD = 3.0F;
    private static final int WHIRL_SOUND_FREQUENCY_MIN = 1;
    private static final int WHIRL_SOUND_FREQUENCY_MAX = 80;
    private static final float KNOCKBACK_PER_BUFF_LEVEL = 0.5F;

    private static final byte POSE_STANDING = 0;
    private static final byte POSE_SLIDING = 1;
    private static final byte POSE_INHALING = 2;
    private static final byte POSE_SHOOTING = 3;
    private static final EntityDataAccessor<Byte> DATA_BREEZE_POSE =
            SynchedEntityData.defineId(BreezeServant.class, EntityDataSerializers.BYTE);

    private static final ProjectileDeflection PROJECTILE_DEFLECTION = (projectile, entity, random) -> {
        entity.level().playSound(null, entity, ModSounds.BREEZE_DEFLECT.get(), entity.getSoundSource(), 1.0F, 1.0F);
        ProjectileDeflection.REVERSE.deflect(projectile, entity, random);
    };

    public final AnimationState idle = new AnimationState();
    public final AnimationState slide = new AnimationState();
    public final AnimationState slideBack = new AnimationState();
    public final AnimationState longJump = new AnimationState();
    public final AnimationState shoot = new AnimationState();
    public final AnimationState inhale = new AnimationState();
    private int jumpTrailStartedTick = 0;
    private int soundTick = 0;
    @Nullable private BlockState goetyominous$inBlockState;

    public BreezeServant(EntityType<? extends Summoned> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(BlockPathTypes.TRAPDOOR, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, -1.0F);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_BREEZE_POSE, POSE_STANDING);
    }

    public byte getBreezePose() {
        return this.entityData.get(DATA_BREEZE_POSE);
    }

    public void setBreezePose(byte pose) {
        this.entityData.set(DATA_BREEZE_POSE, pose);
    }

    public boolean isBreezeStanding() {
        return this.getBreezePose() == POSE_STANDING && this.getPose() == Pose.STANDING;
    }

    public boolean isBreezeSliding() {
        return this.getBreezePose() == POSE_SLIDING;
    }

    public boolean isBreezeInhaling() {
        return this.getBreezePose() == POSE_INHALING;
    }

    public boolean isBreezeShooting() {
        return this.getBreezePose() == POSE_SHOOTING;
    }

    public void setBreezeStanding() {
        this.setBreezePose(POSE_STANDING);
        this.setPose(Pose.STANDING);
    }

    @Override
    public void setBreezeSliding() {
        this.setBreezePose(POSE_SLIDING);
    }

    @Override
    public boolean canIdleSlide() {
        return !this.isStaying() && !this.isCommanded() && !this.isFollowing();
    }

    public BlockState getInBlockState() {
        if (this.goetyominous$inBlockState == null) {
            this.goetyominous$inBlockState = this.level().getBlockState(this.blockPosition());
        }
        return this.goetyominous$inBlockState;
    }

    @Override
    public void baseTick() {
        this.goetyominous$inBlockState = null;
        super.baseTick();
    }

    private void playLocalSound(SoundEvent sound, float volume, float pitch) {
        this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), sound, this.getSoundSource(), volume, pitch, false);
    }

    @Override
    public void followGoal() {
        this.goalSelector.addGoal(5, new Summoned.FollowOwnerGoal<>(this, 0.6D, 10.0F, 2.0F));
    }

    @Override
    public double getCommandSpeed() {
        return 0.6D;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(4, new BreezeAttackGoal(this));
        this.goalSelector.addGoal(6, new BreezeIdleSlideGoal<>(this));
        this.goalSelector.addGoal(7, new Summoned.WanderGoal<>(this, 0.6D, 0.0F));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.63D)
                .add(Attributes.MAX_HEALTH, AttributesConfig.BreezeHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.BreezeArmor.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.BreezeRangeDamage.get())
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.BreezeHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.BreezeArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.BreezeRangeDamage.get());
    }

    @Override
    public int xpReward() {
        return 10;
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 1.3452F;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (this.level().isClientSide() && (DATA_BREEZE_POSE.equals(accessor) || DATA_POSE.equals(accessor))) {
            this.resetAnimations();
            byte pose = this.getBreezePose();
            if (pose == POSE_SHOOTING) {
                this.shoot.startIfStopped(this.tickCount);
            } else if (pose == POSE_INHALING) {
                this.longJump.startIfStopped(this.tickCount);
            } else if (pose == POSE_SLIDING) {
                this.slide.startIfStopped(this.tickCount);
            }
        }
        super.onSyncedDataUpdated(accessor);
    }

    private void resetAnimations() {
        this.shoot.stop();
        this.idle.stop();
        this.inhale.stop();
        this.longJump.stop();
    }

    @Override
    public void tick() {
        if (this.getPose() == Pose.LONG_JUMPING) {
            this.emitJumpTrailParticles();
        } else if (this.isBreezeSliding()) {
            this.emitGroundParticles(SLIDE_PARTICLES_AMOUNT);
        } else if (this.isBreezeShooting() || this.isBreezeInhaling() || this.isBreezeStanding()) {
            this.resetJumpTrail().emitGroundParticles(IDLE_PARTICLES_AMOUNT + this.getRandom().nextInt(1));
        }

        if (!this.isBreezeSliding() && this.slide.isStarted()) {
            this.slideBack.start(this.tickCount);
            this.slide.stop();
        }

        if (!this.level().isClientSide && this.isAlive()) {
            boolean walking = !this.getNavigation().isDone() && this.onGround();
            if (walking && this.isBreezeStanding()) {
                this.playSound(ModSounds.BREEZE_SLIDE.get());
                this.setBreezePose(POSE_SLIDING);
            } else if (!walking && this.isBreezeSliding() && this.getNavigation().isDone()) {
                this.setBreezeStanding();
            }
        }

        this.soundTick = this.soundTick == 0
                ? this.random.nextIntBetweenInclusive(WHIRL_SOUND_FREQUENCY_MIN, WHIRL_SOUND_FREQUENCY_MAX)
                : this.soundTick - 1;
        if (this.soundTick == 0) {
            this.playWhirlSound();
        }

        super.tick();
    }

    public BreezeServant resetJumpTrail() {
        this.jumpTrailStartedTick = 0;
        return this;
    }

    public void emitJumpTrailParticles() {
        if (++this.jumpTrailStartedTick <= JUMP_TRAIL_DURATION_TICKS) {
            BlockState blockstate = !this.getInBlockState().isAir() ? this.getInBlockState() : this.getBlockStateOn();
            Vec3 motion = this.getDeltaMovement();
            Vec3 pos = this.position().add(motion).add(0.0D, 0.1F, 0.0D);
            for (int i = 0; i < JUMP_TRAIL_PARTICLES_AMOUNT; i++) {
                this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockstate), pos.x, pos.y, pos.z, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    public void emitGroundParticles(int amount) {
        if (!this.isPassenger()) {
            Vec3 center = this.getBoundingBox().getCenter();
            Vec3 pos = new Vec3(center.x, this.position().y, center.z);
            BlockState blockstate = !this.getInBlockState().isAir() ? this.getInBlockState() : this.getBlockStateOn();
            if (blockstate.getRenderShape() != RenderShape.INVISIBLE) {
                for (int i = 0; i < amount; i++) {
                    this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockstate), pos.x, pos.y, pos.z, 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }

    @Override
    public void playAmbientSound() {
        if (this.getTarget() == null || !this.onGround()) {
            this.playLocalSound(this.getAmbientSound(), 1.0F, 1.0F);
        }
    }

    public void playWhirlSound() {
        float pitch = 0.7F + 0.4F * this.random.nextFloat();
        float volume = 0.8F + 0.2F * this.random.nextFloat();
        this.playLocalSound(ModSounds.BREEZE_WHIRL.get(), volume, pitch);
    }

    @Override
    public ProjectileDeflection deflection(Projectile projectile) {
        if (!(projectile instanceof AbstractWindCharge)) {
            return this.getType().is(ModTags.DEFLECTS_PROJECTILES)
                    ? PROJECTILE_DEFLECTION : ProjectileDeflection.NONE;
        }
        return ProjectileDeflection.NONE;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.BREEZE_DEATH.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.BREEZE_HURT.get();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.onGround() ? ModSounds.BREEZE_IDLE_GROUND.get() : ModSounds.BREEZE_IDLE_AIR.get();
    }

    public boolean withinInnerCircleRange(Vec3 pos) {
        Vec3 center = this.blockPosition().getCenter();
        double dx = center.x - pos.x;
        double dy = center.y - pos.y;
        double dz = center.z - pos.z;
        return (dx * dx + dz * dz) < (4.0D * 4.0D) && Math.abs(dy) < 10.0D;
    }

    @Override
    public int getMaxHeadYRot() {
        return 30;
    }

    @Override
    public int getHeadRotSpeed() {
        return 25;
    }

    public double getSnoutYPosition() {
        return this.getEyeY() - 0.4D;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source.getEntity() instanceof BreezeEntity
                || source.getEntity() instanceof BreezeServant
                || super.isInvulnerableTo(source);
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        if (fallDistance > FALL_DISTANCE_SOUND_TRIGGER_THRESHOLD) {
            this.playSound(ModSounds.BREEZE_LAND.get(), 1.0F, 1.0F);
        }
        return super.causeFallDamage(fallDistance, multiplier, source);
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.EVENTS;
    }

    public int getBuffLevel() {
        return this.hasEffect(GoetyEffects.BUFF.get()) ? EffectsUtil.getAmplifier(this, GoetyEffects.BUFF.get()) + 1 : 0;
    }

    public float getWindChargeDamage() {
        return ServantCombatUtil.getSpecialAttackDamage(this, AttributesConfig.BreezeRangeDamage.get().floatValue());
    }

    public float getWindChargeKnockback() {
        return 1.0F + KNOCKBACK_PER_BUFF_LEVEL * this.getBuffLevel();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide) {
            ItemStack itemstack = player.getItemInHand(hand);
            if (this.getTrueOwner() != null && player == this.getTrueOwner()) {
                boolean rod = itemstack.is(ModTags.BREEZE_RODS);
                if ((rod || itemstack.is(ModItems.WIND_CHARGE.get())) && this.getHealth() < this.getMaxHealth()) {
                    if (!player.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    this.playSound(ModSounds.BREEZE_IDLE_GROUND.get(), 1.0F, 1.25F);
                    this.heal(rod ? 4.0F : 1.0F);
                    if (this.level() instanceof ServerLevel serverLevel) {
                        for (int i = 0; i < 7; ++i) {
                            double d0 = this.random.nextGaussian() * 0.02D;
                            double d1 = this.random.nextGaussian() * 0.02D;
                            double d2 = this.random.nextGaussian() * 0.02D;
                            serverLevel.sendParticles(ModParticleTypes.HEAL_EFFECT.get(), this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                        }
                    }
                    player.swing(hand);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.mobInteract(player, hand);
    }

    public static Vec3 randomPointBehindTarget(LivingEntity target, RandomSource random) {
        float yaw = target.yHeadRot + 180.0F + (float) random.nextGaussian() * 90.0F / 2.0F;
        float distance = Mth.lerp(random.nextFloat(), 4.0F, 8.0F);
        Vec3 offset = Vec3.directionFromRotation(0.0F, yaw).scale(distance);
        return target.position().add(offset);
    }

    public boolean hasLineOfSight(Vec3 pos) {
        Vec3 from = new Vec3(this.getX(), this.getY(), this.getZ());
        return pos.distanceTo(from) <= 50.0D
                && this.level().clip(new ClipContext(from, pos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() == HitResult.Type.MISS;
    }

    static class BreezeAttackGoal extends Goal {
        private static final int ATTACK_RANGE_MAX_SQRT = 256;
        private static final int ATTACK_RANGE_MIN_SQRT = 4;
        private static final float PROJECTILE_MOVEMENT_SCALE = 0.7F;
        private static final float PROJECTILE_DIVERGENCY = 5.0F;
        private static final float PROJECTILE_DIVERGENCY_DIFFICULTY_MODIFIER = 4.0F;
        private static final int STUCK_SHOOT_WINDOW = 60;
        private static final int SHOOT_INITIAL_DELAY_TICKS = 15;
        private static final int SHOOT_RECOVER_DELAY_TICKS = 4;
        private static final int SHOOT_COOLDOWN_TICKS = 10;
        private static final int SHOOT_WINDOW_AFTER_JUMP = 100;
        private static final int SHOOT_WINDOW_AFTER_SLIDE = 60;
        private static final int JUMP_COOLDOWN_TICKS = 10;
        private static final int JUMP_COOLDOWN_WHEN_HURT_TICKS = 2;
        private static final int INHALING_DURATION_TICKS = 10;
        private static final int REQUIRED_AIR_BLOCKS_ABOVE = 4;
        private static final float FOLLOW_RANGE_MULTIPLIER_FOR_VELOCITY = 0.058333334F;
        private static final float SLIDE_SPEED = 0.6F;
        private static final int SLIDE_TIMEOUT_TICKS = 60;
        private static final int STUCK_TICKS_BEFORE_SHOOTING = 20;
        private static final List<Integer> ALLOWED_ANGLES = List.of(40, 55, 60, 75, 80);

        private enum Phase {
            REPOSITION,
            INHALING,
            JUMPING,
            SLIDING,
            SHOOTING
        }

        private final BreezeServant breeze;
        private Phase phase = Phase.REPOSITION;
        private int timer;
        private int shootWindow;
        private int shootCooldown;
        private int jumpCooldown;
        private int stuckTicks;
        private boolean fired;
        @Nullable
        private BlockPos jumpTarget;
        @Nullable
        private Vec3 slideTarget;

        BreezeAttackGoal(BreezeServant breeze) {
            this.breeze = breeze;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.breeze.getTarget();
            return target != null && target.isAlive() && this.breeze.canAttack(target);
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = this.breeze.getTarget();
            return target != null && target.isAlive() && this.breeze.canAttack(target);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void start() {
            this.phase = Phase.REPOSITION;
            this.timer = 0;
            this.shootWindow = 0;
            this.stuckTicks = 0;
        }

        @Override
        public void stop() {
            this.breeze.getNavigation().stop();
            this.breeze.setDiscardFriction(false);
            if (!this.breeze.isBreezeStanding()) {
                this.breeze.setBreezeStanding();
            }
            this.jumpTarget = null;
            this.slideTarget = null;
        }

        @Override
        public void tick() {
            LivingEntity target = this.breeze.getTarget();
            if (target == null) {
                return;
            }
            if (this.shootCooldown > 0) {
                --this.shootCooldown;
            }
            if (this.jumpCooldown > 0) {
                --this.jumpCooldown;
            }
            if (this.shootWindow > 0) {
                --this.shootWindow;
            }
            switch (this.phase) {
                case REPOSITION -> this.tickReposition(target);
                case INHALING -> this.tickInhaling();
                case JUMPING -> this.tickJumping();
                case SLIDING -> this.tickSliding(target);
                case SHOOTING -> this.tickShooting(target);
            }
        }

        private boolean isTargetWithinShootRange(LivingEntity target) {
            double distanceSqr = this.breeze.position().distanceToSqr(target.position());
            return distanceSqr > ATTACK_RANGE_MIN_SQRT && distanceSqr < ATTACK_RANGE_MAX_SQRT;
        }

        private void tickReposition(LivingEntity target) {
            double followRange = this.breeze.getAttributeValue(Attributes.FOLLOW_RANGE);
            if (this.breeze.distanceToSqr(target) > followRange * followRange) {
                this.breeze.setTarget(null);
                return;
            }
            boolean stuck = this.breeze.isPassenger()
                    || this.breeze.isInWater()
                    || this.breeze.hasEffect(MobEffects.LEVITATION);
            if (stuck) {
                this.shootWindow = STUCK_SHOOT_WINDOW;
            }
            this.breeze.getLookControl().setLookAt(target, 10.0F, 10.0F);
            if (this.shootWindow > 0 && this.shootCooldown <= 0 && this.isTargetWithinShootRange(target) && this.breeze.isBreezeStanding()) {
                this.beginShooting();
                return;
            }
            boolean grounded = this.breeze.onGround() || this.breeze.isInWater();
            if (grounded && this.jumpCooldown <= 0 && this.tryStartJump(target)) {
                return;
            }
            if (this.breeze.onGround() && !this.breeze.isInWater() && this.breeze.isBreezeStanding()) {
                this.startSlide(target);
                return;
            }
            if (++this.stuckTicks > STUCK_TICKS_BEFORE_SHOOTING && this.shootCooldown <= 0 && this.isTargetWithinShootRange(target)) {
                this.beginShooting();
            }
        }

        private boolean tryStartJump(LivingEntity target) {
            if (target.distanceTo(this.breeze) - 4.0F <= 0.0F) {
                return false;
            }
            if (!this.canJumpFromCurrentPosition()) {
                return false;
            }
            BlockPos landing = this.snapToSurface(randomPointBehindTarget(target, this.breeze.getRandom()));
            if (landing == null) {
                return false;
            }
            BlockState below = this.breeze.level().getBlockState(landing.below());
            if (this.breeze.getType().isBlockDangerous(below)) {
                return false;
            }
            if (!this.breeze.hasLineOfSight(landing.getCenter()) && !this.breeze.hasLineOfSight(landing.above(REQUIRED_AIR_BLOCKS_ABOVE).getCenter())) {
                return false;
            }
            this.jumpTarget = landing;
            this.phase = Phase.INHALING;
            this.timer = 0;
            this.stuckTicks = 0;
            this.breeze.getNavigation().stop();
            this.breeze.setBreezePose(POSE_INHALING);
            this.breeze.level().playSound(null, this.breeze, ModSounds.BREEZE_CHARGE.get(), this.breeze.getSoundSource(), 1.0F, 1.0F);
            this.breeze.lookAt(EntityAnchorArgument.Anchor.EYES, landing.getCenter());
            return true;
        }

        private boolean canJumpFromCurrentPosition() {
            BlockPos pos = this.breeze.blockPosition();
            if (this.breeze.level().getBlockState(pos).is(Blocks.HONEY_BLOCK)) {
                return false;
            }
            for (int i = 1; i <= REQUIRED_AIR_BLOCKS_ABOVE; i++) {
                BlockPos above = pos.relative(Direction.UP, i);
                if (!this.breeze.level().getBlockState(above).isAir() && !this.breeze.level().getFluidState(above).is(FluidTags.WATER)) {
                    return false;
                }
            }
            return true;
        }

        @Nullable
        private BlockPos snapToSurface(Vec3 pos) {
            ClipContext down = new ClipContext(pos, pos.relative(Direction.DOWN, 10.0D), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this.breeze);
            HitResult hit = this.breeze.level().clip(down);
            if (hit.getType() == HitResult.Type.BLOCK) {
                return BlockPos.containing(hit.getLocation()).above();
            }
            ClipContext up = new ClipContext(pos, pos.relative(Direction.UP, 10.0D), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this.breeze);
            HitResult hitUp = this.breeze.level().clip(up);
            return hitUp.getType() == HitResult.Type.BLOCK ? BlockPos.containing(hitUp.getLocation()).above() : null;
        }

        private void tickInhaling() {
            if (this.jumpTarget != null) {
                this.breeze.lookAt(EntityAnchorArgument.Anchor.EYES, this.jumpTarget.getCenter());
            }
            if (++this.timer < INHALING_DURATION_TICKS) {
                return;
            }
            Vec3 velocity = this.jumpTarget == null ? null : this.calculateOptimalJumpVector(Vec3.atBottomCenterOf(this.jumpTarget)).orElse(null);
            if (velocity == null) {
                this.breeze.setBreezeStanding();
                this.jumpCooldown = JUMP_COOLDOWN_TICKS;
                this.phase = Phase.REPOSITION;
                return;
            }
            this.breeze.playSound(ModSounds.BREEZE_JUMP.get(), 1.0F, 1.0F);
            this.breeze.setPose(Pose.LONG_JUMPING);
            this.breeze.setYRot(this.breeze.yBodyRot);
            this.breeze.setDiscardFriction(true);
            this.breeze.setDeltaMovement(velocity);
            this.phase = Phase.JUMPING;
            this.timer = 0;
        }

        private Optional<Vec3> calculateOptimalJumpVector(Vec3 target) {
            float maxVelocity = FOLLOW_RANGE_MULTIPLIER_FOR_VELOCITY * (float) this.breeze.getAttributeValue(Attributes.FOLLOW_RANGE);
            for (int angle : net.minecraft.Util.toShuffledList(ALLOWED_ANGLES.stream(), this.breeze.getRandom())) {
                Optional<Vec3> vector = LongJumpUtil.calculateJumpVectorForAngle(this.breeze, target, maxVelocity, angle, false);
                if (vector.isPresent()) {
                    if (this.breeze.hasEffect(MobEffects.JUMP)) {
                        Vec3 velocity = vector.get();
                        return Optional.of(velocity.add(0.0D, velocity.normalize().y * this.breeze.getJumpBoostPower(), 0.0D));
                    }
                    return vector;
                }
            }
            return Optional.empty();
        }

        private void tickJumping() {
            ++this.timer;
            boolean landed = this.breeze.onGround() || (this.breeze.isInWater() && this.timer > 5);
            if (landed || this.timer > 100) {
                this.breeze.playSound(ModSounds.BREEZE_LAND.get(), 1.0F, 1.0F);
                this.breeze.setBreezeStanding();
                this.breeze.setDiscardFriction(false);
                this.jumpCooldown = this.breeze.getLastHurtByMob() != null ? JUMP_COOLDOWN_WHEN_HURT_TICKS : JUMP_COOLDOWN_TICKS;
                this.shootWindow = SHOOT_WINDOW_AFTER_JUMP;
                this.jumpTarget = null;
                this.phase = Phase.REPOSITION;
            }
        }

        private void startSlide(LivingEntity target) {
            Vec3 destination = null;
            if (this.breeze.withinInnerCircleRange(target.position())) {
                Vec3 away = DefaultRandomPos.getPosAway(this.breeze, 5, 5, target.position());
                if (away != null && this.breeze.hasLineOfSight(away) && target.distanceToSqr(away.x, away.y, away.z) > target.distanceToSqr(this.breeze)) {
                    destination = away;
                }
            }
            if (destination == null) {
                destination = this.breeze.getRandom().nextBoolean() ? randomPointBehindTarget(target, this.breeze.getRandom()) : this.randomPointInMiddleCircle(target);
            }
            this.slideTarget = destination;
            this.phase = Phase.SLIDING;
            this.timer = 0;
            this.stuckTicks = 0;
            this.breeze.playSound(ModSounds.BREEZE_SLIDE.get());
            this.breeze.setBreezePose(POSE_SLIDING);
            BlockPos pos = BlockPos.containing(destination);
            this.breeze.getNavigation().moveTo(pos.getX(), pos.getY(), pos.getZ(), SLIDE_SPEED);
        }

        private Vec3 randomPointInMiddleCircle(LivingEntity target) {
            Vec3 toTarget = target.position().subtract(this.breeze.position());
            double distance = toTarget.length() - Mth.lerp(this.breeze.getRandom().nextDouble(), 8.0D, 4.0D);
            Vec3 offset = toTarget.normalize().multiply(distance, distance, distance);
            return this.breeze.position().add(offset);
        }

        private void tickSliding(LivingEntity target) {
            ++this.timer;
            boolean arrived = this.slideTarget != null && this.breeze.position().closerThan(this.slideTarget, 1.5D);
            boolean stuck = this.breeze.getNavigation().isDone() && this.timer > 2;
            if (arrived || stuck || this.timer > SLIDE_TIMEOUT_TICKS) {
                this.breeze.getNavigation().stop();
                this.breeze.setBreezeStanding();
                this.shootWindow = SHOOT_WINDOW_AFTER_SLIDE;
                this.slideTarget = null;
                this.phase = Phase.REPOSITION;
            }
        }

        private void beginShooting() {
            this.phase = Phase.SHOOTING;
            this.timer = 0;
            this.fired = false;
            this.stuckTicks = 0;
            this.breeze.getNavigation().stop();
            this.breeze.setBreezePose(POSE_SHOOTING);
            this.breeze.playSound(ModSounds.BREEZE_INHALE.get(), 1.0F, 1.0F);
        }

        private void tickShooting(LivingEntity target) {
            ++this.timer;
            this.breeze.lookAt(EntityAnchorArgument.Anchor.EYES, target.position());
            if (this.timer >= SHOOT_INITIAL_DELAY_TICKS && !this.fired) {
                this.fired = true;
                if (this.isFacingTarget(target) && this.breeze.level() instanceof ServerLevel serverLevel) {
                    double dx = target.getX() - this.breeze.getX();
                    double dy = target.getY(target.isPassenger() ? 0.8D : 0.3D) - this.breeze.getY(0.5D);
                    double dz = target.getZ() - this.breeze.getZ();
                    ServantWindCharge charge = new ServantWindCharge(serverLevel, this.breeze, this.breeze.getX(), this.breeze.getSnoutYPosition(), this.breeze.getZ(),
                            this.breeze.getWindChargeDamage(), this.breeze.getWindChargeKnockback());
                    this.breeze.playSound(ModSounds.BREEZE_SHOOT.get(), 1.5F, 1.0F);
                    charge.shoot(dx, dy, dz, PROJECTILE_MOVEMENT_SCALE,
                            PROJECTILE_DIVERGENCY - serverLevel.getDifficulty().getId() * PROJECTILE_DIVERGENCY_DIFFICULTY_MODIFIER);
                    serverLevel.addFreshEntity(charge);
                }
            }
            if (this.timer >= SHOOT_INITIAL_DELAY_TICKS + SHOOT_RECOVER_DELAY_TICKS) {
                if (this.breeze.isBreezeShooting()) {
                    this.breeze.setBreezeStanding();
                }
                this.shootCooldown = SHOOT_COOLDOWN_TICKS;
                this.phase = Phase.REPOSITION;
            }
        }

        private boolean isFacingTarget(LivingEntity target) {
            Vec3 view = this.breeze.getViewVector(1.0F);
            Vec3 toTarget = target.position().subtract(this.breeze.position()).normalize();
            return view.dot(toTarget) > 0.5D;
        }
    }
}
