package com.qiuyue.goetyominous.common.entities.hostile;

import com.Polarice3.Goety.common.entities.util.CameraShake;
import com.Polarice3.Goety.common.network.ModServerBossInfo;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.utils.MiscCapHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.common.entities.ai.CenteredGroundNavigation;
import com.qiuyue.goetyominous.common.entities.ai.GuzzlerBossAttackGoal;
import com.qiuyue.goetyominous.common.entities.ai.SmoothBodyRotationControl;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import com.qiuyue.goetyominous.common.init.ModSounds;
import com.qiuyue.goetyominous.config.AttributesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import java.util.Optional;

public class GuzzlerBoss extends Monster {
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
            GuzzlerBoss.class, EntityDataSerializers.INT);
    private static final int STAND_UP_TICKS = 35;
    private static final int NEARBY_CHECK_INTERVAL = 10;
    private static final double NEARBY_RANGE = 8.0D;
    private static final int SLEEP_DELAY_TICKS = 300;
    private static final int SLEEP_RETRY_TICKS = 100;
    private static final float SLEEP_CHANCE = 0.25F;
    private static final int SIT_ANIM_TICKS = 20;
    private static final int DEATH_TICKS = 44;
    private static final int DEATH_SHAKE_TICK = 29;
    private static final int REPELLENT_PACIFY_TICKS = 200;
    private static final double REPELLENT_DISTANCE = 8.0D;
    private static final ResourceLocation MYND_WARPED_FUNGUS_COLONY = new ResourceLocation("mynethersdelight", "warped_fungus_colony");

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

    private final ModServerBossInfo bossInfo = new ModServerBossInfo(this, BossEvent.BossBarColor.RED, false, false);

    private int slamCooldown;
    private int spinCooldown;
    private int jumpCooldown;
    private int standUpStartTick;
    private int nearbyCheckTicks;
    private int calmTicks;
    private int sitRestTicks;
    private int restSoundTicks = 120;
    protected int timeInOverworld;
    private boolean playerNearby;
    private boolean nearbyKnown;
    private boolean leaping;
    private int leapTicks;
    private BlockPos nearestRepellent;
    private int repellentPacifyTicks;

    public GuzzlerBoss(EntityType<? extends GuzzlerBoss> type, Level level) {
        super(type, level);
        this.xpReward = 50;
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
        double health = AttributesConfig.GuzzlerServantHealth.get();
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, Math.min(health + MAX_UPGRADE_LEVEL * health / 3.0D, MAX_UPGRADE_HEALTH))
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.GuzzlerServantAttackDamage.get())
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIM_STATE, ANIM_IDLE);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new GuzzlerBossAttackGoal(this));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.6D) {
            @Override
            public boolean canUse() {
                return !GuzzlerBoss.this.isSitting() && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return !GuzzlerBoss.this.isSitting() && super.canContinueToUse();
            }
        });
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F) {
            @Override
            public boolean canUse() {
                return !GuzzlerBoss.this.isSitting() && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return !GuzzlerBoss.this.isSitting() && super.canContinueToUse();
            }
        });
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this) {
            @Override
            public boolean canUse() {
                return !GuzzlerBoss.this.isSitting() && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return !GuzzlerBoss.this.isSitting() && super.canContinueToUse();
            }
        });
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<Player>(this, Player.class, true) {
            @Override
            public boolean canUse() {
                return !GuzzlerBoss.this.isSitting() && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return !GuzzlerBoss.this.isSitting() && super.canContinueToUse();
            }
        });
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason,
                                        @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
        this.setPersistenceRequired();
        this.setAnimState(ANIM_REST);
        this.setHealth(this.getMaxHealth());
        return data;
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
            this.tickSleep();
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
            if (this.tickCount % 5 == 0) {
                this.bossInfo.update();
            }
            this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
        }
        MiscCapHelper.updateMobTarget(this);
    }

    @Override
    public void aiStep() {
        if (this.level() instanceof ServerLevel serverLevel && !this.isDeadOrDying()) {
            Optional<BlockPos> repellent = (this.tickCount + this.getId()) % 20 == 0
                    ? this.findNearestRepellent(serverLevel)
                    : Optional.ofNullable(this.nearestRepellent);
            if (repellent.isPresent()) {
                if (this.nearestRepellent == null) {
                    this.getNavigation().stop();
                    if (this.isSitting()) {
                        this.beginStandUp();
                    }
                }
                this.nearestRepellent = repellent.get();
                this.repellentPacifyTicks = REPELLENT_PACIFY_TICKS;
            } else {
                this.nearestRepellent = null;
            }
            if (this.repellentPacifyTicks > 0) {
                --this.repellentPacifyTicks;
                this.setTarget(null);
            }
            if (this.nearestRepellent != null) {
                Vec3 center = this.nearestRepellent.getCenter();
                if (this.position().closerThan(center, REPELLENT_DISTANCE) && this.getNavigation().isDone()) {
                    this.fleeRepellent(center);
                }
            }
        }
        super.aiStep();
    }

    private void fleeRepellent(Vec3 center) {
        for (int i = 0; i < 10; ++i) {
            Vec3 away = LandRandomPos.getPosAway(this, 16, 7, center);
            if (away == null) {
                continue;
            }
            Path path = this.getNavigation().createPath(away.x, away.y, away.z, 0);
            if (path != null && path.canReach()) {
                this.getNavigation().moveTo(path, 1.0D);
                break;
            }
        }
    }

    private Optional<BlockPos> findNearestRepellent(ServerLevel serverLevel) {
        return BlockPos.findClosestMatch(this.blockPosition(), 8, 4,
                pos -> this.isRepellent(serverLevel.getBlockState(pos)));
    }

    private boolean isRepellent(BlockState state) {
        return state.is(Blocks.WARPED_FUNGUS)
                || state.is(Blocks.POTTED_WARPED_FUNGUS)
                || state.getBlock().builtInRegistryHolder().key().location().equals(MYND_WARPED_FUNGUS_COLONY);
    }

    @Override
    public void setTarget(LivingEntity target) {
        if (target != null && this.repellentPacifyTicks > 0) {
            return;
        }
        super.setTarget(target);
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader levelReader) {
        if (this.nearestRepellent != null && this.nearestRepellent.closerThan(pos, REPELLENT_DISTANCE)) {
            return -1.0F;
        }
        return super.getWalkTargetValue(pos, levelReader);
    }

    private void tickSleep() {
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
        if (!this.isSitting()) {
            this.tickFallingAsleep();
            return;
        }
        this.getNavigation().stop();
        if (this.nearbyCheckTicks > 0) {
            --this.nearbyCheckTicks;
        }
        if (this.nearbyCheckTicks <= 0) {
            this.nearbyCheckTicks = NEARBY_CHECK_INTERVAL;
            boolean nearby = this.hasNearbyPlayer();
            boolean approached = this.nearbyKnown && nearby && !this.playerNearby;
            this.playerNearby = nearby;
            this.nearbyKnown = true;
            if (approached) {
                this.beginStandUp();
            }
        }
    }

    private void tickFallingAsleep() {
        if (this.isDeadOrDying() || this.isActing() || this.leaping || this.getTarget() != null) {
            this.calmTicks = 0;
            this.nearbyCheckTicks = 0;
            return;
        }
        if (this.nearbyCheckTicks > 0) {
            --this.nearbyCheckTicks;
        } else {
            this.nearbyCheckTicks = NEARBY_CHECK_INTERVAL;
            this.playerNearby = this.hasNearbyPlayer();
            this.nearbyKnown = true;
        }
        if (this.playerNearby) {
            this.calmTicks = 0;
            return;
        }
        if (++this.calmTicks >= SLEEP_DELAY_TICKS
                && (this.calmTicks - SLEEP_DELAY_TICKS) % SLEEP_RETRY_TICKS == 0
                && this.random.nextFloat() < SLEEP_CHANCE) {
            this.calmTicks = 0;
            this.restSoundTicks = 120;
            this.getNavigation().stop();
            this.setDeltaMovement(0.0D, this.getDeltaMovement().y, 0.0D);
            this.hasImpulse = true;
            this.setAnimState(ANIM_SIT);
            this.sitRestTicks = SIT_ANIM_TICKS;
        }
    }

    private void beginStandUp() {
        this.setAnimState(ANIM_STAND_UP);
        this.standUpStartTick = this.tickCount;
        this.nearbyCheckTicks = 0;
        this.calmTicks = 0;
        this.sitRestTicks = 0;
    }

    private boolean hasNearbyPlayer() {
        AABB area = this.getBoundingBox().inflate(NEARBY_RANGE, 8.0D, NEARBY_RANGE);
        return !this.level().getEntitiesOfClass(Player.class, area,
                player -> EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(player) && this.hasLineOfSight(player)).isEmpty();
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
    protected float getDamageAfterArmorAbsorb(DamageSource source, float amount) {
        float reduced = super.getDamageAfterArmorAbsorb(source, amount);
        if (source.is(DamageTypes.GENERIC_KILL)) {
            return reduced;
        }
        return Math.max(0.0F, reduced - MAX_UPGRADE_LEVEL);
    }

    @Override
    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        this.bossInfo.setName(this.getDisplayName());
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.bossInfo.setVisible(MainConfig.SpecialBossBar.get() && !this.isSitting());
        if (this.isConverting()) {
            ++this.timeInOverworld;
        } else {
            this.timeInOverworld = 0;
        }
        if (this.timeInOverworld > 300 && ForgeEventFactory.canLivingConvert(this, this.getConversionTarget(),
                timer -> this.timeInOverworld = timer)) {
            this.playConvertedSound();
            this.finishConversion((ServerLevel) this.level());
        }
    }

    public boolean isConverting() {
        return !this.level().dimensionType().piglinSafe() && !this.isNoAi();
    }

    protected EntityType<? extends GuzzlerBoss> getConversionTarget() {
        return ModEntityTypes.ZGUZZLER_BOSS.get();
    }

    protected void playConvertedSound() {
        this.playSound(ModSounds.GUZZLER_HURT.get(), 1.0F, this.getVoicePitch());
    }

    public void playSkillConversionSound() {
        this.playSound(ModSounds.GUZZLER_BREATHE.get(), this.getSoundVolume(), this.getVoicePitch());
    }

    public void playSkillConvertedSound() {
        this.playConvertedSound();
    }

    protected void finishConversion(ServerLevel serverLevel) {
        ZGuzzlerBoss zombified = new ZGuzzlerBoss(ModEntityTypes.ZGUZZLER_BOSS.get(), serverLevel);
        zombified.copyPosition(this);
        zombified.setNoAi(this.isNoAi());
        if (this.hasCustomName()) {
            zombified.setCustomName(this.getCustomName());
            zombified.setCustomNameVisible(this.isCustomNameVisible());
        }
        if (this.isPersistenceRequired()) {
            zombified.setPersistenceRequired();
        }
        zombified.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(this.blockPosition()),
                MobSpawnType.CONVERSION, null, null);
        zombified.setAnimState(this.isSitting() ? ANIM_REST : ANIM_IDLE);
        zombified.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
        zombified.setHealth(zombified.getMaxHealth());
        zombified.setYHeadRot(this.getYHeadRot());
        zombified.setYBodyRot(this.yBodyRot);
        zombified.yHeadRotO = this.yHeadRotO;
        zombified.yBodyRotO = this.yBodyRotO;
        zombified.yRotO = this.yRotO;
        zombified.xRotO = this.xRotO;
        ForgeEventFactory.onLivingConvert(this, zombified);
        this.discard();
        serverLevel.addFreshEntity(zombified);
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("SlamCooldown", this.slamCooldown);
        tag.putInt("SpinCooldown", this.spinCooldown);
        tag.putInt("JumpCooldown", this.jumpCooldown);
        tag.putInt("TimeInOverworld", this.timeInOverworld);
        tag.putBoolean("Sleeping", this.isSitting());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.slamCooldown = tag.getInt("SlamCooldown");
        this.spinCooldown = tag.getInt("SpinCooldown");
        this.jumpCooldown = tag.getInt("JumpCooldown");
        this.timeInOverworld = tag.getInt("TimeInOverworld");
        this.setAnimState(tag.contains("Sleeping") && !tag.getBoolean("Sleeping") ? ANIM_IDLE : ANIM_REST);
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
        if (!this.level().isClientSide && this.deathTime == DEATH_SHAKE_TICK) {
            CameraShake.cameraShake(this.level(), this.position(), 25.0F, 0.15F, 0, 20);
        }
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

    @Override
    public void knockback(double strength, double x, double z) {
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    public int getUpgradeLevel() {
        return MAX_UPGRADE_LEVEL;
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
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 3.3F;
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
        CameraShake.cameraShake(this.level(), this.position(), 10.0F, 0.01F, 5, 0);
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
