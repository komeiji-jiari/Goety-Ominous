package com.qiuyue.goetyominous.common.entities.ally.mobs;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.ZPiglinServant;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.common.entities.ai.CenteredGroundNavigation;
import com.qiuyue.goetyominous.common.entities.ai.SmoothBodyRotationControl;
import com.qiuyue.goetyominous.common.entities.ai.ZGuzzlerAttackGoal;
import com.qiuyue.goetyominous.common.init.ModSounds;
import com.qiuyue.goetyominous.config.AttributesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ZGuzzler extends ZPiglinServant {
    public static final int ANIM_IDLE = 0;
    public static final int ANIM_SLAM = 1;
    public static final int ANIM_SPIN = 2;
    public static final int ANIM_JUMP_START = 3;
    public static final int ANIM_JUMP_LAND = 4;
    public static final int ANIM_SIT = 5;
    public static final int ANIM_REST = 6;
    public static final int ANIM_STAND_UP = 7;
    public static final int ANIM_DEATH = 8;
    public static final double LEAP_HEADROOM = 8.5D;
    public static final double LEAP_VERTICAL = 0.80D;
    public static final int MAX_UPGRADE_LEVEL = 3;
    private static final double MAX_UPGRADE_HEALTH = 180.0D;

    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(
            ZGuzzler.class, EntityDataSerializers.INT);
    private static final int SLEEP_DELAY_TICKS = 300;
    private static final int SLEEP_RETRY_TICKS = 100;
    private static final float SLEEP_CHANCE = 0.25F;
    private static final int SIT_ANIM_TICKS = 20;
    private static final int COCOA_NO_SLEEP_TICKS = 24000;
    private static final int STAND_UP_TICKS = 35;
    private static final int NEARBY_CHECK_INTERVAL = 10;
    private static final double NEARBY_RANGE = 16.0D;
    private static final int DEATH_TICKS = 44;

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState slamAnimationState = new AnimationState();
    public final AnimationState spinAnimationState = new AnimationState();
    public final AnimationState jumpStartAnimationState = new AnimationState();
    public final AnimationState jumpLandAnimationState = new AnimationState();
    public final AnimationState sitAnimationState = new AnimationState();
    public final AnimationState restAnimationState = new AnimationState();
    public final AnimationState standUpAnimationState = new AnimationState();
    public final AnimationState deathAnimationState = new AnimationState();

    private int slamCooldown;
    private int spinCooldown;
    private int jumpCooldown;
    private int stayTicks;
    private int sitRestTicks;
    private int standUpStartTick;
    private int nearbyCheckTicks;
    private int noSleepTicks;
    private int sleepGraceTicks;
    private int restSoundTicks;
    private boolean leaping;
    private int leapTicks;
    private int upgradeLevel;
    private int upgradePoints;

    public ZGuzzler(EntityType<? extends Summoned> type, Level level) {
        super(type, level);
        this.xpReward = 0;
        this.moveControl = new MobUtil.SlowRotMoveControl(this);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new CenteredGroundNavigation(this, level);
    }

    @Override
    protected BodyRotationControl createBodyControl() {
        return new SmoothBodyRotationControl(this);
    }

    @Override
    public float getStepHeight() {
        return 1.5F;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, Math.min(AttributesConfig.GuzzlerServantHealth.get(), MAX_UPGRADE_HEALTH))
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.GuzzlerServantAttackDamage.get())
                .add(Attributes.ARMOR, 2.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), this.getConfiguredMaxHealth());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.GuzzlerServantAttackDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), 2.0D);
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MOVEMENT_SPEED), 0.23D);
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.KNOCKBACK_RESISTANCE), 1.0D);
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.FOLLOW_RANGE), 32.0D);
    }

    @Override
    public double getFollowSpeed() {
        return 1.0D;
    }

    public int getUpgradePoints() {
        return this.upgradePoints;
    }

    public int getUpgradeLevel() {
        return this.upgradeLevel;
    }

    public void setUpgrade(int level, int points) {
        this.upgradeLevel = Mth.clamp(level, 0, MAX_UPGRADE_LEVEL);
        this.upgradePoints = Math.max(0, points);
        this.normalizeUpgrade();
        this.refreshMaxHealth();
    }

    private void normalizeUpgrade() {
        while (this.upgradeLevel < MAX_UPGRADE_LEVEL) {
            int required = Guzzler.getRequiredPoints(this.upgradeLevel);
            if (this.upgradePoints < required) {
                break;
            }
            this.upgradePoints -= required;
            ++this.upgradeLevel;
        }
        if (this.upgradeLevel >= MAX_UPGRADE_LEVEL) {
            this.upgradePoints = 0;
        }
    }

    private double getConfiguredMaxHealth() {
        double health = AttributesConfig.GuzzlerServantHealth.get();
        return Math.min(health + this.getUpgradeLevel() * health / 3.0D, MAX_UPGRADE_HEALTH);
    }

    private void refreshMaxHealth() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), this.getConfiguredMaxHealth());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIM_STATE, ANIM_IDLE);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("SlamCooldown", this.slamCooldown);
        tag.putInt("SpinCooldown", this.spinCooldown);
        tag.putInt("JumpCooldown", this.jumpCooldown);
        tag.putInt("NoSleepTicks", this.noSleepTicks);
        tag.putBoolean("Sleeping", this.isSitting());
        tag.putInt("UpgradeLevel", this.upgradeLevel);
        tag.putInt("UpgradePoints", this.upgradePoints);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.slamCooldown = tag.getInt("SlamCooldown");
        this.spinCooldown = tag.getInt("SpinCooldown");
        this.jumpCooldown = tag.getInt("JumpCooldown");
        this.noSleepTicks = tag.getInt("NoSleepTicks");
        this.upgradeLevel = Mth.clamp(tag.getInt("UpgradeLevel"), 0, MAX_UPGRADE_LEVEL);
        this.upgradePoints = Math.max(0, tag.getInt("UpgradePoints"));
        this.normalizeUpgrade();
        if (tag.getBoolean("Sleeping")) {
            this.setAnimState(ANIM_REST);
            this.sitRestTicks = 0;
            this.stayTicks = 0;
            this.sleepGraceTicks = 20;
        } else {
            this.setAnimState(ANIM_IDLE);
        }
        this.setConfigurableAttributes();
        if (tag.contains("Health")) {
            this.setHealth(Math.min(tag.getFloat("Health"), this.getMaxHealth()));
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new ZGuzzlerAttackGoal(this));
        this.goalSelector.addGoal(8, new Summoned.WanderGoal<>(this, 0.6D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 8.0F) {
            @Override
            public boolean canUse() {
                return !ZGuzzler.this.isSitting() && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return !ZGuzzler.this.isSitting() && super.canContinueToUse();
            }
        });
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Mob.class, 8.0F) {
            @Override
            public boolean canUse() {
                return !ZGuzzler.this.isSitting() && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return !ZGuzzler.this.isSitting() && super.canContinueToUse();
            }
        });
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this) {
            @Override
            public boolean canUse() {
                return !ZGuzzler.this.isSitting() && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return !ZGuzzler.this.isSitting() && super.canContinueToUse();
            }
        });
    }

    @Override
    public void followGoal() {
        this.goalSelector.addGoal(5, new NoLookFollowGoal(this, this.getFollowSpeed(), 10.0F, 2.0F));
    }

    private static class NoLookFollowGoal extends Summoned.FollowOwnerGoal<ZGuzzler> {
        NoLookFollowGoal(ZGuzzler mob, double speed, float startDistance, float stopDistance) {
            super(mob, speed, startDistance, stopDistance);
        }

        @Override
        public void tick() {
            if (this.owner == null) {
                return;
            }
            if (this.summonedEntity.getControlledVehicle() != null) {
                this.navigation.moveTo(this.owner, this.followSpeed + 0.25D);
                if (this.summonedEntity.getControlledVehicle() instanceof Mob mob) {
                    mob.getNavigation().moveTo(this.owner, this.followSpeed + 0.25D);
                }
            } else if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = 10;
                if (!this.summonedEntity.isLeashed() && !this.summonedEntity.isPassenger()) {
                    double range = this.owner instanceof Mob ? 32.0D : 16.0D;
                    boolean teleport = this.summonedEntity.distanceToSqr(this.owner) >= Mth.square(range);
                    if (this.owner instanceof Mob) {
                        teleport |= !this.summonedEntity.hasLineOfSight(this.owner)
                                && this.summonedEntity.distanceToSqr(this.owner) >= Mth.square(8.0D);
                    } else {
                        teleport &= this.canTeleport();
                    }
                    if (teleport) {
                        this.tryToTeleportNearEntity();
                    } else {
                        this.navigation.moveTo(this.owner, this.followSpeed);
                    }
                }
            }
        }
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    protected boolean isSunSensitive() {
        return false;
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    public void setBaby(boolean baby) {
    }

    @Override
    public boolean canWearArmor() {
        return false;
    }

    @Override
    public boolean canHaveWeapon() {
        return false;
    }

    @Override
    public void populateDefaultWeapons(RandomSource random, DifficultyInstance difficulty) {
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 3.3F;
    }

    @Override
    public float getVoicePitch() {
        return 0.55F;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.GUZZLER_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.GUZZLER_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.GUZZLER_DEATH.get();
    }

    @Override
    protected float getSoundVolume() {
        return 0.5F;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(ModSounds.GUZZLER_STEP.get(), 0.2F, this.getVoicePitch());
    }

    public void playSlamImpactSound() {
        this.playSound(com.Polarice3.Goety.init.ModSounds.LEAPLEAF_SMASH.get(), this.getSoundVolume() * 1.5F, this.getVoicePitch());
    }

    public void playJumpLaunchSound() {
        this.playSound(ModSounds.GUZZLER_AGGRO.get(), 0.8F, this.getVoicePitch());
    }

    public void playJumpLandSound() {
        this.playSlamImpactSound();
    }

    public void playSpinStartSound() {
        this.playSound(ModSounds.GUZZLER_AGGRO.get(), 0.8F, this.getVoicePitch());
    }

    public void playSpinSound() {
        this.playSound(ModSounds.GUZZLER_SPIN_LOOP.get(), 0.6F, this.getVoicePitch());
    }

    public void playSpinHitSound() {
        this.playSound(ModSounds.GUZZLER_ARM.get(), 0.45F, this.getVoicePitch());
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (this.getTrueOwner() != null && player.getUUID().equals(this.getTrueOwner().getUUID())
                && stack.is(Items.COCOA_BEANS)) {
            if (!this.level().isClientSide) {
                this.noSleepTicks = COCOA_NO_SLEEP_TICKS;
                this.stayTicks = 0;
                if (this.isSitting()) {
                    this.beginStandUp();
                }
                this.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            if (this.slamCooldown > 0) {
                --this.slamCooldown;
            }
            if (this.spinCooldown > 0) {
                --this.spinCooldown;
            }
            if (this.jumpCooldown > 0) {
                --this.jumpCooldown;
            }
            if (this.leaping) {
                ++this.leapTicks;
            }
            if (this.noSleepTicks > 0) {
                --this.noSleepTicks;
                if (this.isSitting()) {
                    this.beginStandUp();
                }
            }
            this.tickSitting();
            if (this.isSitting()) {
                if (--this.restSoundTicks <= 0) {
                    this.restSoundTicks = 120 + this.random.nextInt(120);
                    float roll = this.random.nextFloat();
                    if (roll < 0.2F) {
                        this.playSound(ModSounds.GUZZLER_SNORT.get(), 0.25F, this.getVoicePitch());
                    } else if (roll < 0.4F) {
                        this.playSound(ModSounds.GUZZLER_CHEST_BEAT.get(), 0.2F, this.getVoicePitch());
                    } else {
                        this.playSound(ModSounds.GUZZLER_BREATHE.get(), 0.25F, this.getVoicePitch());
                    }
                }
            } else if (this.restSoundTicks != 0) {
                this.restSoundTicks = 0;
            }
        }
    }

    private void tickSitting() {
        if (this.isDeadOrDying()) {
            return;
        }
        if (this.getAnimState() == ANIM_STAND_UP) {
            this.getNavigation().stop();
            if (this.tickCount - this.standUpStartTick >= STAND_UP_TICKS) {
                this.setAnimState(ANIM_IDLE);
            }
            return;
        }
        if (this.getAnimState() == ANIM_SIT && --this.sitRestTicks <= 0) {
            this.setAnimState(ANIM_REST);
        }
        if (this.isActing() || this.leaping || this.getTarget() != null || !this.isStaying()) {
            this.stayTicks = 0;
            if (this.isSitting()) {
                this.beginStandUp();
            }
            return;
        }
        if (this.sleepGraceTicks > 0) {
            --this.sleepGraceTicks;
        } else if (this.getNavigation().isInProgress() || this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-4D) {
            this.stayTicks = 0;
            if (this.isSitting()) {
                this.beginStandUp();
            }
            return;
        }
        if (this.isSitting()) {
            if (this.getHealth() < this.getMaxHealth()) {
                this.beginStandUp();
                return;
            }
            if (this.nearbyCheckTicks > 0) {
                --this.nearbyCheckTicks;
            }
            if (this.nearbyCheckTicks <= 0) {
                this.nearbyCheckTicks = NEARBY_CHECK_INTERVAL;
                if (this.hasNearbyEnemy()) {
                    this.beginStandUp();
                }
            }
            return;
        }
        if (this.noSleepTicks > 0) {
            this.stayTicks = 0;
            return;
        }
        if (this.getHealth() < this.getMaxHealth()) {
            this.stayTicks = 0;
            return;
        }
        if (++this.stayTicks >= SLEEP_DELAY_TICKS
                && (this.stayTicks - SLEEP_DELAY_TICKS) % SLEEP_RETRY_TICKS == 0
                && this.random.nextFloat() < SLEEP_CHANCE) {
            this.stayTicks = 0;
            this.setAnimState(ANIM_SIT);
            this.sitRestTicks = SIT_ANIM_TICKS;
        }
    }

    private void beginStandUp() {
        this.setAnimState(ANIM_STAND_UP);
        this.standUpStartTick = this.tickCount;
        this.stayTicks = 0;
        this.sitRestTicks = 0;
        this.nearbyCheckTicks = 0;
    }

    private boolean hasNearbyEnemy() {
        AABB area = this.getBoundingBox().inflate(NEARBY_RANGE);
        return !this.level().getEntitiesOfClass(LivingEntity.class, area,
                entity -> entity != this && entity.isAlive() && !entity.isSpectator()
                        && entity instanceof Enemy && MobUtil.canAttack(this, entity)).isEmpty();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean result = super.hurt(source, amount);
        if (result && !this.level().isClientSide && this.isSitting()) {
            this.beginStandUp();
        }
        return result;
    }

    @Override
    public void tryKill(Player player) {
        if (this.killChance <= 0) {
            this.warnKill(player);
        } else {
            super.tryKill(player);
        }
    }

    @Override
    public void knockback(double strength, double x, double z) {
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected float getDamageAfterArmorAbsorb(DamageSource source, float amount) {
        float reduced = super.getDamageAfterArmorAbsorb(source, amount);
        int level = this.getUpgradeLevel();
        if (level <= 0 || source.is(DamageTypes.GENERIC_KILL)) {
            return reduced;
        }
        return Math.max(0.0F, reduced - level);
    }

    @Override
    public void die(DamageSource source) {
        this.setAnimState(ANIM_DEATH);
        this.stopLeap();
        this.getNavigation().stop();
        this.setDeltaMovement(0.0D, Math.min(this.getDeltaMovement().y, 0.0D), 0.0D);
        this.hasImpulse = true;
        super.die(source);
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        this.getNavigation().stop();
        if (!this.level().isClientSide && this.deathTime >= DEATH_TICKS && !this.isRemoved()) {
            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.POOF,
                        this.getX(), this.getY() + this.getBbHeight() * 0.5D, this.getZ(),
                        100, this.getBbWidth() * 0.5D, this.getBbHeight() * 0.45D, this.getBbWidth() * 0.5D, 0.02D);
            }
            this.remove(Entity.RemovalReason.KILLED);
        }
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return !this.leaping && super.causeFallDamage(fallDistance, multiplier, source);
    }

    public void startLeap(Vec3 direction, double speed) {
        Vec3 flat = new Vec3(direction.x, 0.0D, direction.z);
        if (flat.lengthSqr() < 1.0E-6D) {
            Vec3 look = this.getLookAngle();
            flat = new Vec3(look.x, 0.0D, look.z);
        }
        flat = flat.lengthSqr() < 1.0E-6D ? new Vec3(0.0D, 0.0D, 1.0D) : flat.normalize();
        this.leaping = true;
        this.leapTicks = 0;
        this.setDeltaMovement(flat.x * speed, LEAP_VERTICAL, flat.z * speed);
        this.hasImpulse = true;
    }

    public void stopLeap() {
        this.leaping = false;
        this.leapTicks = 0;
    }

    public boolean isLeaping() {
        return this.leaping;
    }

    public int getLeapTicks() {
        return this.leapTicks;
    }

    public boolean hasLeapHeadroom() {
        AABB box = this.getBoundingBox();
        AABB check = new AABB(box.minX, box.minY, box.minZ, box.maxX, box.minY + LEAP_HEADROOM, box.maxZ);
        return this.level().noCollision(this, check);
    }

    public void setAnimState(int state) {
        this.entityData.set(ANIM_STATE, state);
    }

    public int getAnimState() {
        return this.entityData.get(ANIM_STATE);
    }

    public boolean isSitting() {
        int state = this.getAnimState();
        return state == ANIM_SIT || state == ANIM_REST;
    }

    public boolean isGettingUp() {
        return this.getAnimState() == ANIM_STAND_UP;
    }

    public boolean isActing() {
        int state = this.getAnimState();
        return state == ANIM_SLAM || state == ANIM_SPIN || state == ANIM_JUMP_START || state == ANIM_JUMP_LAND;
    }

    public int getSlamCooldown() {
        return this.slamCooldown;
    }

    public void setSlamCooldown(int ticks) {
        this.slamCooldown = ticks;
    }

    public int getSpinCooldown() {
        return this.spinCooldown;
    }

    public void setSpinCooldown(int ticks) {
        this.spinCooldown = ticks;
    }

    public int getJumpCooldown() {
        return this.jumpCooldown;
    }

    public void setJumpCooldown(int ticks) {
        this.jumpCooldown = ticks;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (ANIM_STATE.equals(accessor) && this.level().isClientSide) {
            this.stopActionAnimations();
            switch (this.getAnimState()) {
                case ANIM_SLAM -> this.slamAnimationState.start(this.tickCount);
                case ANIM_SPIN -> this.spinAnimationState.start(this.tickCount);
                case ANIM_JUMP_START -> this.jumpStartAnimationState.start(this.tickCount);
                case ANIM_JUMP_LAND -> this.jumpLandAnimationState.start(this.tickCount);
                case ANIM_SIT -> this.sitAnimationState.start(this.tickCount);
                case ANIM_REST -> this.restAnimationState.start(this.tickCount);
                case ANIM_STAND_UP -> this.standUpAnimationState.start(this.tickCount);
                case ANIM_DEATH -> this.deathAnimationState.start(this.tickCount);
                default -> {
                    this.idleAnimationState.start(this.tickCount);
                    this.walkAnimationState.start(this.tickCount);
                }
            }
        }
        super.onSyncedDataUpdated(accessor);
    }

    private void stopActionAnimations() {
        this.slamAnimationState.stop();
        this.spinAnimationState.stop();
        this.jumpStartAnimationState.stop();
        this.jumpLandAnimationState.stop();
        this.sitAnimationState.stop();
        this.restAnimationState.stop();
        this.standUpAnimationState.stop();
        this.deathAnimationState.stop();
    }
}
