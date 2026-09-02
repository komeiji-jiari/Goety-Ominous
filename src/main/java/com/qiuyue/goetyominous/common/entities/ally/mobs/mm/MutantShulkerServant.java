package com.qiuyue.goetyominous.common.entities.ally.mobs.mm;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.MobUtil;
import com.alexander.mutantmore.ai.goals.AllDirectionsTargetGoal;
import com.alexander.mutantmore.ai.goals.ApproachTargetGoal;
import com.alexander.mutantmore.ai.goals.GroundPullAntiCheeseGoal;
import com.alexander.mutantmore.ai.goals.LookAtTargetGoal;
import com.alexander.mutantmore.config.MutantMoreGroupedOptionsCommonConfig;
import com.alexander.mutantmore.config.mutant_shulker.MutantShulkerClientConfig;
import com.alexander.mutantmore.config.mutant_shulker.MutantShulkerCommonConfig;
import com.alexander.mutantmore.entities.MutantShulkerBullet;
import com.alexander.mutantmore.events.ShakeCameraEvent;
import com.alexander.mutantmore.init.ItemInit;
import com.alexander.mutantmore.init.MMDamageTypes;
import com.alexander.mutantmore.init.ParticleTypeInit;
import com.alexander.mutantmore.init.SoundEventInit;
import com.alexander.mutantmore.init.TagInit;
import com.alexander.mutantmore.util.MiscUtils;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.goals.MutantShulkerServant.*;
import com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
import com.google.common.collect.Lists;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.Control;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class MutantShulkerServant extends AbstractMutantServant {
    private static final ForgeConfigSpec.BooleanValue DISABLED;
    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        DISABLED = builder.define("no_grief", false);
        builder.build();
    }

    private static final EntityDataAccessor<Boolean> IN_BOX = SynchedEntityData.defineId(MutantShulkerServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ANVIL_ATTACKING = SynchedEntityData.defineId(MutantShulkerServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Byte> COLOR_ID = SynchedEntityData.defineId(MutantShulkerServant.class, EntityDataSerializers.BYTE);
    public final AnimationState idleAnimation = new AnimationState();
    public final AnimationState idleRareAnimation = new AnimationState();
    public final AnimationState shulkerWalkAnimation = new AnimationState();
    public final AnimationState shootAnimation = new AnimationState();
    public final AnimationState biteAnimation = new AnimationState();
    public final AnimationState deathAnimation = new AnimationState();
    public final AnimationState idleInShellAnimation = new AnimationState();
    public final AnimationState idleRareInShellAnimation = new AnimationState();
    public final AnimationState shootInShellAnimation = new AnimationState();
    public final AnimationState anvilCrushAnimation = new AnimationState();
    public final AnimationState summonTrapsAnimation = new AnimationState();
    public final AnimationState summonTrapsInShellAnimation = new AnimationState();
    public final AnimationState spinningAnimation = new AnimationState();
    public final AnimationState enterSpinAnimation = new AnimationState();
    public float currentSpeed;
    public float currentSpeedChangeSpeed = 0.01f;
    public int specialAnimationTick;
    public final int specialAnimationLength = 45;
    public int introAnimationTick;
    public final int introAnimationLength = 45;
    public int shootAnimationTick;
    public int shootAnimationLength = 45;
    public int shootAnimationActionPoint1 = 25;
    public int shootAnimationActionPoint2 = 15;
    public int shootAnimationActionPoint3 = 10;
    public int biteAnimationTick;
    public int biteAnimationLength = 55;
    public int biteAnimationActionPoint = 38;
    public int summonTrapsAnimationTick;
    public int summonTrapsAnimationLength = 50;
    public int specialAnimationTickInShell;
    public int specialAnimationLengthInShell = 50;
    public int shootAnimationTickInShell;
    public int shootAnimationLengthInShell = 47;
    public int shootAnimationActionPointInShell = 20;
    public int summonTrapsAnimationTickInShell;
    public int summonTrapsAnimationLengthInShell = 50;
    public int anvilCrushAnimationTick;
    public int anvilCrushAnimationLength = 43;
    public int anvilCrushAnimationActionPoint = 28;
    public float healthHealedInShell = 0.0f;
    public int anvilCrushSoundCooldown = 0;
    public int nextEnterShellTime;
    public int prepareFlyAnimationTick;
    public int prepareFlyAnimationLength = 21;
    public int prepareFlyActionPoint = 8;
    public boolean flying = false;
    public DamageSource killedBy = this.damageSources().cramming();
    public List<LivingEntity> alreadyCrushed = Lists.newArrayList();
    public static EntityDimensions inBoxDimensions = EntityDimensions.scalable(1.9F, 1.9F);
    public int stunnedTicks;
    public int stunLength = 120;
    private static final EntityDataAccessor<Boolean> DATA_VOID_ECHO =
            SynchedEntityData.defineId(MutantShulkerServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_VOID_EYE =
            SynchedEntityData.defineId(MutantShulkerServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_VOID_SHARD =
            SynchedEntityData.defineId(MutantShulkerServant.class, EntityDataSerializers.BOOLEAN);
    private static final UUID VOID_ECHO_HEALTH_UUID = UUID.fromString("d4444444-0000-4000-8000-000000000001");
    private static final UUID VOID_EYE_HEALTH_UUID = UUID.fromString("e5555555-0000-4000-8000-000000000001");
    private static final UUID VOID_SHARD_HEALTH_UUID = UUID.fromString("f6666666-0000-4000-8000-000000000001");
    private int voidEchoInvulnTime = 0;

    private void addModIfMissing(AttributeInstance instance, UUID uuid, String name, double value) {
        if (instance != null && instance.getModifier(uuid) == null) {
            instance.addPermanentModifier(new AttributeModifier(uuid, name, value, AttributeModifier.Operation.ADDITION));
        }
    }

    public MutantShulkerServant(EntityType<? extends Owned> p_i50189_1_, Level p_i50189_2_) {
        super(p_i50189_1_, p_i50189_2_);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new RemainStationaryGoal());
        this.goalSelector.addGoal(1, new GroundPullAntiCheeseGoal(this));
        if (MutantShulkerCommonConfig.enters_shell.get()) {
            this.goalSelector.addGoal(1, new EnterShellGoal());
        }
        if (MutantShulkerCommonConfig.leaves_shell.get()) {
            this.goalSelector.addGoal(1, new LeaveShellGoal());
        }
        if (MutantShulkerCommonConfig.uses_fly.get()) {
            this.goalSelector.addGoal(2, new MutantShulkerServantFlyGoal(this));
        }
        if (MutantShulkerCommonConfig.uses_anvil_crush.get()) {
            this.goalSelector.addGoal(2, new MutantShulkerServantAnvilCrushAttackGoal(this));
        }
        if (MutantShulkerCommonConfig.uses_scatter_traps.get()) {
            this.goalSelector.addGoal(3, new MutantShulkerServantScatterTrapsAttackGoal(this));
        }
        if (MutantShulkerCommonConfig.uses_scatter_traps_in_shell.get()) {
            this.goalSelector.addGoal(3, new MutantShulkerServantScatterTrapsAttackInShellGoal(this));
        }
        if (MutantShulkerCommonConfig.uses_bite.get()) {
            this.goalSelector.addGoal(4, new MutantShulkerServantBiteAttackGoal(this));
        }
        if (MutantShulkerCommonConfig.uses_shoot.get()) {
            this.goalSelector.addGoal(5, new MutantShulkerServantShootAttackGoal(this));
        }
        if (MutantShulkerCommonConfig.uses_shoot_in_shell.get()) {
            this.goalSelector.addGoal(5, new MutantShulkerServantShootAttackGoalInShell(this));
        }
        this.goalSelector.addGoal(7, new LookAtTargetGoal(this));
        this.goalSelector.addGoal(6, new ApproachTargetGoal(this, MutantShulkerCommonConfig.follow_target_wanted_distance.get(), MutantShulkerCommonConfig.following_movement_speed_multiplier.get(), true) {
            @Override
            public boolean canUse() {
                return super.canUse() && !MutantShulkerServant.this.isInBox();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && !MutantShulkerServant.this.isInBox();
            }
        });
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Mob.class, 7.5F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0) {
            public boolean canUse() {
                return super.canUse() && !MutantShulkerServant.this.isInBox() && !MutantShulkerServant.this.isIdleStandby();
            }

            public boolean canContinueToUse() {
                return super.canContinueToUse() && !MutantShulkerServant.this.isInBox() && !MutantShulkerServant.this.isIdleStandby();
            }
        });
        this.targetSelector.addGoal(0, new HurtByTargetGoal(this, new Class[0]).setUnseenMemoryTicks(6000));
        if (MutantShulkerCommonConfig.attacks_players.get() && !MutantMoreGroupedOptionsCommonConfig.mutants_attack_players_off.get()) {
            this.targetSelector.addGoal(1, (new AllDirectionsTargetGoal(this, Player.class, true)).setUnseenMemoryTicks(6000));
        }
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<LivingEntity>(this, LivingEntity.class, 20, false, false, entity -> MobUtil.isOwnedTargetable(this, entity)) {
            protected AABB getTargetSearchArea(double p_26069_) {
                return this.mob.getBoundingBox().inflate(MutantShulkerCommonConfig.follow_non_player_distance.get(), MutantShulkerCommonConfig.follow_non_player_distance.get(), MutantShulkerCommonConfig.follow_non_player_distance.get());
            }
        });
    }

    public boolean canHarm(Entity target) {
        if (target instanceof LivingEntity living && this.isAlliedTo(living)) {
            return false;
        }
        return MiscUtils.canHarmBasedOnTeamAndTag(TagInit.EntityTypes.MUTANT_SHULKER_CANT_HURT, this, target, this, null);
    }

    public boolean canAttack(LivingEntity target) {
        return this.canTarget(target) && super.canAttack(target);
    }

    public boolean canTarget(Entity target) {
        if (target instanceof LivingEntity living && this.isAlliedTo(living)) {
            return false;
        }
        return MiscUtils.canHarmBasedOnTeamAndTag(TagInit.EntityTypes.MUTANT_SHULKER_CANT_TARGET, this, target, this, null);
    }

    protected BodyRotationControl createBodyControl() {
        return new MutantShulkerServantBodyRotationControl(this);
    }

    public boolean shouldBodyMoveWithHead() {
        return this.biteAnimationTick > 0 || this.introAnimationTick > 0;
    }

    public int getMaxHeadYRot() {
        return Integer.MAX_VALUE;
    }

    public int getMaxHeadXRot() {
        return Integer.MAX_VALUE;
    }

    public boolean shouldBeStationary() {
        return this.introAnimationTick > 0 || this.stunnedTicks > 0;
    }

    public boolean isIdleStandby() {
        return this.getTarget() == null && this.isStaying();
    }

    private boolean animationsUseable() {
        return this.introAnimationTick <= 0
                && this.shootAnimationTick <= 0
                && this.biteAnimationTick <= 0
                && this.summonTrapsAnimationTick <= 0
                && this.anvilCrushAnimationTick <= 0
                && this.specialAnimationTick <= 0;
    }

    @Override
    public void followGoal() {

        this.goalSelector.addGoal(5, new MutantShulkerServantFollowOwnerGoal(this, 1.0, 10.0F, 2.0F));
    }

    public static AttributeSupplier.Builder createConfiguredAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.MutantShulkerServantHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.MutantShulkerServantArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.MutantShulkerServantArmorToughness.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.MutantShulkerServantKnockbackResistance.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.MutantShulkerServantFollowRange.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.MutantShulkerServantMovementSpeed.get());
    }

    protected SoundEvent getAmbientSound() {
        return this.isInBox() ? null : SoundEventInit.MUTANT_SHULKER_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource p_33034_) {
        return SoundEventInit.MUTANT_SHULKER_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return null;
    }

    public float getVoicePitch() {
        return this.isDeadOrDying() ? 1.0F : super.getVoicePitch();
    }

    protected float getSoundVolume() {
        return this.isDeadOrDying() ? 3.0F : super.getSoundVolume();
    }

    protected void playStepSound(BlockPos p_20135_, BlockState p_20136_) {
        this.playSound(SoundEventInit.MUTANT_SHULKER_STEP.get(), 0.5F, 1.0F);
    }

    public EntityDimensions getDimensions(Pose p_19975_) {
        return this.isInBox() ? inBoxDimensions : super.getDimensions(p_19975_);
    }

    public void refreshDimensions() {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        super.refreshDimensions();
        this.setPos(d0, d1, d2);
    }

    public void die(DamageSource source) {
        super.die(source);
        if (source != null && (source.is(DamageTypes.GENERIC_KILL) || source.is(DamageTypes.FELL_OUT_OF_WORLD))) {
            this.playSound(SoundEvents.HOSTILE_DEATH, this.getSoundVolume(), this.getVoicePitch());
        } else {
            this.playSound(SoundEventInit.MUTANT_SHULKER_DEATH.get(), this.getSoundVolume(), 1.0F);
        }
        this.killedBy = source;
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.getLastDamageSource() != null && (this.getLastDamageSource().is(DamageTypes.GENERIC_KILL) || this.getLastDamageSource().is(DamageTypes.FELL_OUT_OF_WORLD))) {
            if (this.deathTime == 20 && !this.level().isClientSide()) {
                this.level().broadcastEntityEvent(this, (byte) 60);
                this.remove(Entity.RemovalReason.KILLED);
            }
        } else {
            this.setDeltaMovement(0.0, this.getDeltaMovement().y, 0.0);
            if (!this.level().isClientSide && this.deathTime > 20 && this.deathTime < 90 && this.deathTime % 2 == 0) {
                for (int i = 0; i < 2; ++i) {
                    this.shootMutantShulkerProjectile(this.random.nextInt(360),
                            BlockPos.containing(this.getX() - 50.0 + this.random.nextInt(100),
                                    this.getY() + 2.0 + this.random.nextInt(20),
                                    this.getZ() - 50.0 + this.random.nextInt(100)), false);
                }
            }
            if (this.deathTime == 113) {
                ShakeCameraEvent.shake(this.level(), 26, 0.03F, this.blockPosition(), 25);
                if (!this.level().isClientSide) {
                    MiscUtils.customExplosion(this.level(), this, this.damageSources().explosion(this, this),
                            null, this.getX(), this.getY(), this.getZ(), 15.0F, false,
                            this.deathExplosionBlockInteraction(), null, this.getSoundSource(),
                            ParticleTypes.EXPLOSION, ParticleTypes.EXPLOSION_EMITTER,
                            MutantShulkerCommonConfig.death_explosion_damage.get().floatValue(), true, true);
                }
            }
            if (this.deathTime == 123) {
                if (!this.level().isClientSide) {
                    this.spawnAtLocation(new ItemStack(ItemInit.MUTANT_SHULKER_SHELL.get(), 2));
                    this.remove(Entity.RemovalReason.KILLED);
                }
                for (int i = 0; i < 20; ++i) {
                    double d0 = this.random.nextGaussian() * 0.02;
                    double d1 = this.random.nextGaussian() * 0.02;
                    double d2 = this.random.nextGaussian() * 0.02;
                    this.level().addParticle(ParticleTypes.POOF, this.getRandomX(1.0), this.getRandomY(), this.getRandomZ(1.0), d0, d1, d2);
                }
            }
        }
    }

    public Explosion.BlockInteraction deathExplosionBlockInteraction() {
        return Explosion.BlockInteraction.KEEP;
    }

    @Override
    public void tryKill(Player player) {
        if (this.killChance <= 0) {
            this.warnKill(player);
        } else {
            super.tryKill(player);
        }
    }

    public boolean inBoxButOpen() {
        return this.isInBox() && (this.summonTrapsAnimationTickInShell > 0 || this.specialAnimationTickInShell > 0 || this.shootAnimationTickInShell > 0 || this.anvilCrushAnimationTick > 0);
    }

    @Override
    public boolean hurt(DamageSource p_21016_, float p_21017_) {
        if (this.hasVoidEcho()) {
            if (this.voidEchoInvulnTime > 0) {
                --this.voidEchoInvulnTime;
                return false;
            }
            this.voidEchoInvulnTime = 5;
            if (this.level().dimension() == Level.END) {
                p_21017_ *= 0.5F;
            } else {
                p_21017_ *= 0.85F;
            }
        }
        if (p_21016_.getDirectEntity() instanceof MutantShulkerBullet
                || p_21016_.getDirectEntity() instanceof MutantShulkerServantBullet) {
            this.stunnedTicks = 60;
        }
        if (p_21016_.getDirectEntity() instanceof ShulkerBullet) {
            return false;
        }
        if (this.isInBox() && !this.inBoxButOpen() && !p_21016_.is(DamageTypes.GENERIC_KILL) && !p_21016_.is(DamageTypes.FELL_OUT_OF_WORLD)) {
            this.playSound(SoundEvents.SHULKER_HURT_CLOSED, 1.0F, 0.5F);
            return false;
        }
        if (!this.level().isClientSide && MutantShulkerCommonConfig.teleports_when_hit.get() && !this.isStaying() && this.random.nextInt(MutantShulkerCommonConfig.teleport_when_hit_chance.get()) == 0 && !p_21016_.is(DamageTypes.GENERIC_KILL) && !p_21016_.is(DamageTypes.FELL_OUT_OF_WORLD) && !this.isDeadOrDying()) {
            this.teleport();
            return false;
        }
        if (this.flying && MutantShulkerCommonConfig.getting_hit_cancels_flying.get() && this.tickCount >= this.nextEnterShellTime) {
            this.flying = false;
            this.setInBox(true);
            this.level().broadcastEntityEvent(this, (byte) 6);
            return super.hurt(p_21016_, p_21017_);
        }
        return super.hurt(p_21016_, p_21017_);
    }

    protected float getStandingEyeHeight(Pose p_34186_, EntityDimensions p_34187_) {
        return this.isInBox() ? 1.0F : 1.8F;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IN_BOX, false);
        this.entityData.define(ANVIL_ATTACKING, false);
        this.entityData.define(COLOR_ID, (byte) 16);
        this.entityData.define(DATA_VOID_ECHO, false);
        this.entityData.define(DATA_VOID_EYE, false);
        this.entityData.define(DATA_VOID_SHARD, false);
    }

    public void addAdditionalSaveData(CompoundTag p_29495_) {
        super.addAdditionalSaveData(p_29495_);
        p_29495_.putBoolean("InBox", this.isInBox());
        p_29495_.putByte("Color", this.entityData.get(COLOR_ID));
        p_29495_.putBoolean("VoidEcho", this.hasVoidEcho());
        p_29495_.putBoolean("VoidEye", this.hasVoidEye());
        p_29495_.putBoolean("VoidShard", this.hasVoidShard());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag p_29478_) {
        super.readAdditionalSaveData(p_29478_);
        this.setInBox(p_29478_.getBoolean("InBox"));
        if (p_29478_.contains("Color", 99)) {
            this.entityData.set(COLOR_ID, p_29478_.getByte("Color"));
        }
        this.setVoidEcho(p_29478_.getBoolean("VoidEcho"));
        this.setVoidEye(p_29478_.getBoolean("VoidEye"));
        this.setVoidShard(p_29478_.getBoolean("VoidShard"));
    }

    public boolean hasVoidEcho() {
        return this.getEntityData().get(DATA_VOID_ECHO);
    }

    public void setVoidEcho(boolean value) {
        this.getEntityData().set(DATA_VOID_ECHO, value);
    }

    public boolean hasVoidEye() {
        return this.getEntityData().get(DATA_VOID_EYE);
    }

    public void setVoidEye(boolean value) {
        this.getEntityData().set(DATA_VOID_EYE, value);
    }

    public boolean hasVoidShard() {
        return this.getEntityData().get(DATA_VOID_SHARD);
    }

    public void setVoidShard(boolean value) {
        this.getEntityData().set(DATA_VOID_SHARD, value);
    }

    private void applyEnhancementModifiers() {
        AttributeInstance health = this.getAttribute(Attributes.MAX_HEALTH);
        if (this.hasVoidEcho()) {
            this.addModIfMissing(health, VOID_ECHO_HEALTH_UUID, "Void Echo Health", MobsConfig.MutantShulkerVoidEchoHealth.get());
        }
        if (this.hasVoidEye()) {
            this.addModIfMissing(health, VOID_EYE_HEALTH_UUID, "Void Eye Health", MobsConfig.MutantShulkerVoidEyeHealth.get());
        }
        if (this.hasVoidShard()) {
            this.addModIfMissing(health, VOID_SHARD_HEALTH_UUID, "Void Shard Health", MobsConfig.MutantShulkerVoidShardHealth.get());
        }
    }

    public boolean isInBox() {
        return this.getEntityData().get(IN_BOX);
    }

    public void setInBox(boolean inBox) {
        if (inBox) {
            this.healthHealedInShell = 0.0F;
        }
        this.getEntityData().set(IN_BOX, inBox);
    }

    public boolean isAnvilAttacking() {
        return this.getEntityData().get(ANVIL_ATTACKING);
    }

    public void setAnvilAttacking(boolean inBox) {
        this.getEntityData().set(ANVIL_ATTACKING, inBox);
    }

    private void setColor(@Nullable DyeColor color) {
        if (color == null) {
            this.entityData.set(COLOR_ID, (byte) 16);
        } else {
            this.entityData.set(COLOR_ID, (byte) color.getId());
        }
    }

    @Nullable
    public DyeColor getColor() {
        byte b = this.entityData.get(COLOR_ID);
        return b != 16 && b <= 15 ? DyeColor.byId(b) : null;
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (this.getMasterOwner() == player) {
            if (itemstack.is(com.Polarice3.Goety.common.items.ModItems.VOID_ECHO.get()) && !this.hasVoidEcho()) {
                if (!player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                this.setVoidEcho(true);
                this.applyEnhancementModifiers();
                this.heal(50.0F);
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
            if (itemstack.is(com.Polarice3.Goety.common.items.ModItems.VOIDED_EYE.get()) && !this.hasVoidEye()) {
                if (!player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                this.setVoidEye(true);
                this.applyEnhancementModifiers();
                this.heal(50.0F);
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
            if (itemstack.is(com.Polarice3.Goety.common.items.ModItems.VOID_SHARD.get()) && !this.hasVoidShard()) {
                if (!player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                this.setVoidShard(true);
                this.applyEnhancementModifiers();
                this.heal(50.0F);
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        }
        if (this.getOwner() == player && itemstack.getItem() == Items.ENDER_PEARL && this.getHealth() < this.getMaxHealth()) {
            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
            this.playSound(SoundEventInit.MUTANT_SHULKER_IDLE.get(), 1.0F, 1.0F);
            this.heal(2.0F);
            if (this.level() instanceof ServerLevel serverLevel) {
                for (int i = 0; i < 7; ++i) {
                    double d0 = this.random.nextGaussian() * 0.02D;
                    double d1 = this.random.nextGaussian() * 0.02D;
                    double d2 = this.random.nextGaussian() * 0.02D;
                    serverLevel.sendParticles(ParticleTypes.HEART, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                }
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        if (this.getOwner() == player && itemstack.getItem() instanceof DyeItem && ((DyeItem) itemstack.getItem()).getDyeColor() != this.getColor()) {
            this.setColor(((DyeItem) itemstack.getItem()).getDyeColor());
            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        if (this.getOwner() == player && itemstack.getItem() == Items.WATER_BUCKET && this.getColor() != null) {
            this.setColor(null);
            if (!player.isCreative()) {
                player.setItemInHand(hand, Items.BUCKET.getDefaultInstance());
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        for (Entity entity : this.level().getEntities(this, this.getBoundingBox())) {
            if (!this.canHarm(entity)) continue;
            if (!this.isAnvilAttacking() || this.alreadyCrushed.contains(entity)) continue;
            if (entity instanceof LivingEntity) {
                this.alreadyCrushed.add((LivingEntity)entity);
                if (MutantShulkerCommonConfig.ignores_invulnerability_time.get()) {
                    entity.invulnerableTime = 0;
                }
            }
            ShakeCameraEvent.shake(this.level(), 10, 0.015F, this.blockPosition(), 5);
            if (this.anvilCrushSoundCooldown <= 0) {
                this.playSound(SoundEventInit.MUTANT_SHULKER_ANVIL_CRUSH.get(), 1.0F, 1.0F);
                this.anvilCrushSoundCooldown = 40;
            }
            entity.hurt(MMDamageTypes.mutantShulkerAnvilCrushAttack(this.damageSources(), this), MutantShulkerCommonConfig.anvil_crush_damage.get().floatValue());
        }
        if (!this.level().isClientSide && MutantShulkerCommonConfig.teleports_to_avoid_falling_into_the_void.get() && this.getTarget() != null && this.isFallingToDoom() && !this.isAnvilAttacking()) {
            this.teleportTowards(this.getTarget());
        }
        if (this.isInWater() && MutantShulkerCommonConfig.teleports_in_water.get() && !this.isIdleStandby()) {
            if (!this.tryTeleportOutOfWater()) {

                this.sinkInWater();
            }
        }
        if (!this.level().isClientSide && this.getTarget() != null && this.distanceTo(this.getTarget()) > 5.0F && this.random.nextInt(MutantShulkerCommonConfig.basic_teleport_chance.get()) == 0 && !this.isDeadOrDying() && !this.isAnvilAttacking() && !this.isStaying() && !(this.flying && !MutantShulkerCommonConfig.teleports_while_flying.get())) {
            if (this.isInBox() && MutantShulkerCommonConfig.teleports_while_in_shell.get()) {
                this.teleport(this.getTarget().getX() - 15.0 + this.random.nextInt(30), this.getY(), this.getTarget().getZ() - 15.0 + this.random.nextInt(30));
            } else if (MutantShulkerCommonConfig.teleports_to_follow_target.get()) {
                this.teleportTowards(this.getTarget());
            }
        }
    }

    boolean isFallingToDoom() {
        boolean blockBeneath = false;
        for (int i = 0; i < 64; ++i) {
            if (this.level().getBlockState(new BlockPos(this.blockPosition().getX(), this.blockPosition().getY() - i, this.blockPosition().getZ())).isAir()) continue;
            blockBeneath = true;
        }
        return !this.level().isClientSide && !blockBeneath;
    }

    @Override
    public void baseTick() {
        super.baseTick();
        if (!this.level().isClientSide && this.verticalCollisionBelow && this.isAnvilAttacking()) {
            ShakeCameraEvent.shake(this.level(), 6, 0.01F, this.blockPosition(), 10);
            this.playSound(SoundEventInit.MUTANT_SHULKER_ANVIL_CRUSH.get(), 1.0F, 1.0F);
            this.alreadyCrushed.clear();
            this.setAnvilAttacking(false);
        }
        this.refreshDimensions();
        if (!this.level().isClientSide && (this.isInBox() || this.isDeadOrDying())) {
            this.getNavigation().stop();
        }
        if (!this.level().isClientSide && this.isInBox() && this.tickCount % MutantShulkerCommonConfig.shell_heal_interval.get() == 0 && this.getHealth() < this.getMaxHealth()) {
            ((ServerLevel)this.level()).sendParticles((ParticleOptions)((SimpleParticleType)ParticleTypeInit.MUTANT_SHULKER_HEAL.get()), this.getRandomX(1.25), this.getRandomY(), this.getRandomZ(1.25), 1, 0.1, 0.1, 0.1, 0.0);
            this.heal(MutantShulkerCommonConfig.shell_heal_amount.get().floatValue() * (this.hasVoidEcho() ? 2.0F : 1.0F));
            this.healthHealedInShell += MutantShulkerCommonConfig.shell_heal_amount.get().floatValue();
        }
        Vec3 velocity = this.getDeltaMovement();
        float groundSpeed = Mth.sqrt((float)(velocity.x * velocity.x + velocity.z * velocity.z));
        if (!this.level().isClientSide && groundSpeed <= 0.1F && this.specialAnimationTick <= 0 && this.random.nextInt(400) == 0 && (this.getTarget() == null || this.getTarget().isDeadOrDying() || this.getTarget().isRemoved())) {
            if (this.isInBox()) {
                this.playSound(SoundEventInit.MUTANT_SHULKER_OPEN.get(), 1.0F, 1.0F);
                this.specialAnimationTickInShell = this.specialAnimationLengthInShell;
                this.level().broadcastEntityEvent(this, (byte)36);
            } else {
                this.playSound(SoundEventInit.MUTANT_SHULKER_ROAR.get(), 1.0F, 1.0F);
                this.specialAnimationTick = 45;
                this.level().broadcastEntityEvent(this, (byte)4);
            }
        }
        if (this.specialAnimationTickInShell == 20 && groundSpeed <= 0.1F && (this.getTarget() == null || this.getTarget().isDeadOrDying() || this.getTarget().isRemoved())) {
            this.playSound(SoundEventInit.MUTANT_SHULKER_CLOSE.get(), 1.0F, 1.0F);
        }
        if (this.anvilCrushSoundCooldown > 0) {
            --this.anvilCrushSoundCooldown;
        }
        this.tickDownAnimTimers();
    }

    public void shootMutantShulkerProjectile(float angle, BlockPos shootToPos, boolean aimForTarget) {
        if (aimForTarget && this.getTarget() != null) {
            MutantShulkerServantBullet projectile = new MutantShulkerServantBullet(MmEntityRegistry.MUTANT_SHULKER_SERVANT_BULLET.get(), this.level());
            projectile.damage = MutantShulkerCommonConfig.mutant_shulker_bullet_damage.get().floatValue();
            projectile.explosionSize = MutantShulkerCommonConfig.mutant_shulker_bullet_explosion_size.get().floatValue();
            if (this.hasVoidShard()) {
                projectile.explosionSize += 2.0F;
            }
            projectile.levitationLength = MutantShulkerCommonConfig.mutant_shulker_bullet_levitation_length.get();
            projectile.levitationLevel = MutantShulkerCommonConfig.mutant_shulker_bullet_levitation_level.get();
            projectile.ignoresInvulTime = MutantShulkerCommonConfig.ignores_invulnerability_time.get();
            projectile.setRemainingHits(MutantShulkerCommonConfig.mutant_shulker_bullet_hits.get());
            projectile.moveDelay = 60;
            projectile.setPos(this.getX(), this.getEyeY(), this.getZ());
            projectile.setTarget(this.getTarget());
            projectile.setOwner(this);
            this.shootMutantShulkerProjectile(this.getTarget().blockPosition(), projectile, angle);
            this.level().addFreshEntity(projectile);
        } else {
            MutantShulkerServantBullet projectile = new MutantShulkerServantBullet(MmEntityRegistry.MUTANT_SHULKER_SERVANT_BULLET.get(), this.level());
            projectile.damage = MutantShulkerCommonConfig.mutant_shulker_bullet_damage.get().floatValue();
            projectile.explosionSize = MutantShulkerCommonConfig.mutant_shulker_bullet_explosion_size.get().floatValue();
            if (this.hasVoidShard()) {
                projectile.explosionSize += 2.0F;
            }
            projectile.levitationLength = MutantShulkerCommonConfig.mutant_shulker_bullet_levitation_length.get();
            projectile.levitationLevel = MutantShulkerCommonConfig.mutant_shulker_bullet_levitation_level.get();
            projectile.ignoresInvulTime = MutantShulkerCommonConfig.ignores_invulnerability_time.get();
            projectile.setRemainingHits(MutantShulkerCommonConfig.mutant_shulker_bullet_hits.get());
            projectile.moveDelay = 60;
            projectile.setPos(this.getX(), this.getEyeY(), this.getZ());
            projectile.setOwner(this);
            this.shootMutantShulkerProjectile(shootToPos, projectile, angle);
            this.level().addFreshEntity(projectile);
        }
    }

    public void shootMutantShulkerProjectile(BlockPos p_33275_, Projectile p_33277_, float p_33278_) {
        this.shootMutantShulkerProjectile((LivingEntity)this, p_33275_, p_33277_, p_33278_, 1.0F);
    }

    public void shootMutantShulkerProjectile(LivingEntity p_32323_, BlockPos p_32324_, Projectile p_32325_, float p_32326_, float p_32327_) {
        double d0 = p_32324_.getX() - p_32323_.getX();
        double d1 = p_32324_.getZ() - p_32323_.getZ();
        double d2 = Math.sqrt(d0 * d0 + d1 * d1);
        double d3 = p_32324_.getY() - p_32325_.getY() + d2 * 0.2F;
        Vector3f vector3f = this.getProjectileShotVector(p_32323_, new Vec3(d0, d3, d1), p_32326_);
        p_32325_.shoot(vector3f.x(), vector3f.y(), vector3f.z(), p_32327_, 14 - p_32323_.level().getDifficulty().getId() * 4);
        p_32323_.playSound(SoundEventInit.MUTANT_SHULKER_SHOOT.get(), 2.0F, 1.0F / (p_32323_.getRandom().nextFloat() * 0.4F + 0.8F));
    }

    public Vector3f getProjectileShotVector(LivingEntity p_32333_, Vec3 p_32334_, float p_32335_) {
        Vec3 vec3 = p_32334_.normalize();
        Vec3 vec31 = vec3.cross(new Vec3(0.0, 1.0, 0.0));
        if (vec31.lengthSqr() <= 1.0E-7) {
            vec31 = vec3.cross(p_32333_.getUpVector(1.0F));
        }
        Quaternionf quaternion = new Quaternionf().setAngleAxis(0.7853981852531433, vec31.x, vec31.y, vec31.z);
        Vector3f vector3f = vec3.toVector3f();
        vector3f.rotate((Quaternionfc)quaternion);
        Quaternionf quaternion1 = new Quaternionf().setAngleAxis((float)Math.toRadians(p_32335_), vector3f.x, vector3f.y, vector3f.z);
        return vec3.toVector3f().rotate((Quaternionfc)quaternion1);
    }

    public void handleEntityEvent(byte p_28844_) {
        if (p_28844_ == 4) {
            this.specialAnimationTick = 45;
        } else if (p_28844_ == 11) {
            this.introAnimationTick = 45;
        } else if (p_28844_ == 33) {
            this.shootAnimationTick = this.shootAnimationLength;
        } else if (p_28844_ == 44) {
            this.biteAnimationTick = this.biteAnimationLength;
        } else if (p_28844_ == 8) {
            this.summonTrapsAnimationTick = this.summonTrapsAnimationLength;
        } else if (p_28844_ == 9) {
            this.stunnedTicks = this.stunLength;
        } else if (p_28844_ == 36) {
            this.specialAnimationTickInShell = this.specialAnimationLengthInShell;
        } else if (p_28844_ == 12) {
            this.shootAnimationTickInShell = this.shootAnimationLengthInShell;
        } else if (p_28844_ == 10) {
            this.summonTrapsAnimationTickInShell = this.summonTrapsAnimationLengthInShell;
        } else if (p_28844_ == 37) {
            this.anvilCrushAnimationTick = this.anvilCrushAnimationLength;
        } else if (p_28844_ == 5) {
            this.flying = true;
        } else if (p_28844_ == 6) {
            this.flying = false;
        } else if (p_28844_ == 7) {
            this.prepareFlyAnimationTick = this.prepareFlyAnimationLength;
        } else {
            super.handleEntityEvent(p_28844_);
        }
    }

    public boolean canBeAffected(MobEffectInstance p_34192_) {
        return p_34192_.getEffect() == MobEffects.LEVITATION ? false : super.canBeAffected(p_34192_);
    }

    public boolean canBeCollidedWith() {
        return this.isAlive() && this.isInBox();
    }

    @Override
    protected void updateAnimations() {
        boolean somethingAnimating = false;
        this.deathAnimation.animateWhen(this.isDeadOrDying(), this.tickCount);
        somethingAnimating = this.deathAnimation.isStarted();
        this.idleRareAnimation.animateWhen(this.introAnimationTick > 0 && !somethingAnimating, this.tickCount);
        somethingAnimating = somethingAnimating || this.idleRareAnimation.isStarted();
        this.spinningAnimation.animateWhen(this.flying && !somethingAnimating, this.tickCount);
        somethingAnimating = somethingAnimating || this.spinningAnimation.isStarted();
        this.anvilCrushAnimation.animateWhen(this.isInBox() && this.anvilCrushAnimationTick > 0 && !somethingAnimating, this.tickCount);
        somethingAnimating = somethingAnimating || this.anvilCrushAnimation.isStarted();
        this.summonTrapsInShellAnimation.animateWhen(this.isInBox() && this.summonTrapsAnimationTickInShell > 0 && !somethingAnimating, this.tickCount);
        somethingAnimating = somethingAnimating || this.summonTrapsInShellAnimation.isStarted();
        this.shootInShellAnimation.animateWhen(this.isInBox() && this.shootAnimationTickInShell > 0 && !somethingAnimating, this.tickCount);
        somethingAnimating = somethingAnimating || this.shootInShellAnimation.isStarted();
        this.idleRareInShellAnimation.animateWhen(this.isInBox() && this.specialAnimationTickInShell > 0 && !somethingAnimating, this.tickCount);
        somethingAnimating = somethingAnimating || this.idleRareInShellAnimation.isStarted();
        this.idleInShellAnimation.animateWhen(this.isInBox() && !somethingAnimating, this.tickCount);
        somethingAnimating = somethingAnimating || this.idleInShellAnimation.isStarted();
        this.enterSpinAnimation.animateWhen(!this.isInBox() && this.prepareFlyAnimationTick > 0 && !somethingAnimating, this.tickCount);
        somethingAnimating = somethingAnimating || this.enterSpinAnimation.isStarted();
        this.summonTrapsAnimation.animateWhen(!this.isInBox() && this.summonTrapsAnimationTick > 0 && !somethingAnimating, this.tickCount);
        somethingAnimating = somethingAnimating || this.summonTrapsAnimation.isStarted();
        this.biteAnimation.animateWhen(!this.isInBox() && this.biteAnimationTick > 0 && !somethingAnimating, this.tickCount);
        somethingAnimating = somethingAnimating || this.biteAnimation.isStarted();
        this.shootAnimation.animateWhen(!this.isInBox() && this.shootAnimationTick > 0 && !somethingAnimating, this.tickCount);
        somethingAnimating = somethingAnimating || this.shootAnimation.isStarted();
        this.idleRareAnimation.animateWhen(!this.isInBox() && this.specialAnimationTick > 0 && !somethingAnimating, this.tickCount);
        somethingAnimating = somethingAnimating || this.idleRareAnimation.isStarted();
        this.idleAnimation.animateWhen(!this.isInBox() && !somethingAnimating, this.tickCount);
    }

    public void tickDownAnimTimers() {
        if (this.specialAnimationTick > 0) {
            --this.specialAnimationTick;
        }
        if (this.introAnimationTick > 0) {
            --this.introAnimationTick;
        }
        if (this.shootAnimationTick > 0) {
            --this.shootAnimationTick;
        }
        if (this.biteAnimationTick > 0) {
            --this.biteAnimationTick;
        }
        if (this.summonTrapsAnimationTick > 0) {
            --this.summonTrapsAnimationTick;
        }
        if (this.specialAnimationTickInShell > 0) {
            --this.specialAnimationTickInShell;
        }
        if (this.shootAnimationTickInShell > 0) {
            --this.shootAnimationTickInShell;
        }
        if (this.summonTrapsAnimationTickInShell > 0) {
            --this.summonTrapsAnimationTickInShell;
        }
        if (this.anvilCrushAnimationTick > 0) {
            --this.anvilCrushAnimationTick;
        }
        if (this.prepareFlyAnimationTick > 0) {
            --this.prepareFlyAnimationTick;
        }
        if (this.stunnedTicks > 0) {
            this.stunEffect();
            --this.stunnedTicks;
        }
    }

    private void stunEffect() {
        if (this.random.nextInt(6) == 0) {
            double d0 = this.getX() - this.getBbWidth() * Math.sin(this.yBodyRot * ((float)Math.PI / 180)) + (this.random.nextDouble() * 0.6 - 0.3);
            double d1 = this.getY() + this.getBbHeight() - 0.3;
            double d2 = this.getZ() + this.getBbWidth() * Math.cos(this.yBodyRot * ((float)Math.PI / 180)) + (this.random.nextDouble() * 0.6 - 0.3);
            this.level().addParticle(ParticleTypes.ENTITY_EFFECT, d0, d1, d2, 0.4980392156862745, 0.5137254901960784, 0.5725490196078431);
        }
    }

    @Override
    public void onMutated() {
        this.introAnimationTick = 45;
        this.level().broadcastEntityEvent(this, (byte)11);
    }

    @Override
    public SpawnGroupData finalizeSpawn(net.minecraft.world.level.ServerLevelAccessor pLevel, net.minecraft.world.DifficultyInstance pDifficulty, net.minecraft.world.entity.MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        if (pReason == net.minecraft.world.entity.MobSpawnType.MOB_SUMMONED && this.getTrueOwner() instanceof Player player) {
            if (countServants(player) >= MobsConfig.MutantShulkerServantLimit.get()) {
                return null;
            }
        }
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    private int countServants(Player player) {
        int count = 0;
        if (player.level() instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof MutantShulkerServant servant && servant.getTrueOwner() == player) {
                    count++;
                }
            }
        }
        return count;
    }

    private boolean tryTeleportOutOfWater() {
        BlockPos escape = this.findWaterEscapeSpot();
        if (escape == null) {
            return false;
        }
        return this.teleport(escape.getX() + 0.5, escape.getY() + 1.0, escape.getZ() + 0.5);
    }

    @Nullable
    private BlockPos findWaterEscapeSpot() {
        for (int i = 0; i < 10; ++i) {
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(
                    this.getX() - 15.0 + this.random.nextInt(30),
                    this.getY() - 10.0 + this.random.nextInt(20),
                    this.getZ() - 15.0 + this.random.nextInt(30));
            while (pos.getY() > this.level().getMinBuildHeight() && !this.level().getBlockState(pos).blocksMotion()) {
                pos.move(Direction.DOWN);
            }
            BlockState ground = this.level().getBlockState(pos);
            if (ground.blocksMotion() && !ground.getFluidState().is(FluidTags.WATER)
                    && !this.level().getBlockState(pos.above()).getFluidState().is(FluidTags.WATER)) {
                return pos.immutable();
            }
        }
        return null;
    }

    private void sinkInWater() {
        if (!this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.9, 1.0, 0.9).add(0.0, -0.7, 0.0));
        }
    }

    protected boolean teleport() {
        if (!this.level().isClientSide() && this.isAlive()) {
            double d0 = this.getX() - 15.0 + this.random.nextInt(30);
            double d1 = this.getY() - 10.0 + this.random.nextInt(20);
            double d2 = this.getZ() - 15.0 + this.random.nextInt(30);
            return this.teleport(d0, d1, d2);
        }
        return false;
    }

    public void teleportTowards(Entity p_32501_) {
        Vec3 vec3 = new Vec3(this.getX() - p_32501_.getX(), this.getY(0.5) - p_32501_.getEyeY(), this.getZ() - p_32501_.getZ());
        vec3 = vec3.normalize();
        double d1 = this.getX() + (this.random.nextDouble() - 0.5) * 8.0 - vec3.x * 16.0;
        double d2 = this.getY() + this.random.nextInt(16) - 8 - vec3.y * 16.0;
        double d3 = this.getZ() + (this.random.nextDouble() - 0.5) * 8.0 - vec3.z * 16.0;
        this.teleport(d1, d2, d3);
    }

    private boolean teleport(double p_32544_, double p_32545_, double p_32546_) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(p_32544_, p_32545_, p_32546_);
        while (blockpos$mutableblockpos.getY() > this.level().getMinBuildHeight() && !this.level().getBlockState(blockpos$mutableblockpos).blocksMotion()) {
            blockpos$mutableblockpos.move(Direction.DOWN);
        }
        BlockState blockstate = this.level().getBlockState(blockpos$mutableblockpos);
        boolean flag = blockstate.blocksMotion();
        boolean flag1 = blockstate.getFluidState().is(FluidTags.WATER);
        if (flag && !flag1) {
            EntityTeleportEvent.EnderEntity event = ForgeEventFactory.onEnderTeleport(this, p_32544_, p_32545_, p_32546_);
            if (event.isCanceled()) {
                return false;
            }
            Vec3 vec3 = this.position();
            boolean flag2 = this.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), false);
            if (flag2) {
                this.level().gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(this));
                if (!this.isSilent()) {
                    this.level().playSound(null, this.xo, this.yo, this.zo, SoundEventInit.MUTANT_SHULKER_TELEPORT.get(), this.getSoundSource(), 1.0F, 1.0F);
                    this.playSound(SoundEventInit.MUTANT_SHULKER_TELEPORT.get(), 1.0F, 1.0F);
                }
            }
            return flag2;
        }
        return false;
    }

    @Override
    public TagKey<Block> walksThroughTag() {
        return TagInit.Blocks.MUTANT_SHULKER_WALKS_THROUGH;
    }

    @Override
    public ForgeConfigSpec.ConfigValue<Boolean> walkGriefingConfig() {
        return DISABLED;
    }

    @Override
    public ForgeConfigSpec.ConfigValue<Boolean> walkGriefingDropsBlocksConfig() {
        return DISABLED;
    }

    @Override
    public ForgeConfigSpec.ConfigValue<Boolean> hurtGriefingConfig() {
        return DISABLED;
    }

    @Override
    public ForgeConfigSpec.ConfigValue<Boolean> hurtGriefingDropsBlocksConfig() {
        return DISABLED;
    }

    @Override
    public ForgeConfigSpec.ConfigValue<Boolean> showHealthBarConfig() {
        return MutantShulkerClientConfig.show_health_bar;
    }

    @Override
    public ForgeConfigSpec.ConfigValue<Boolean> despawnsConfig() {
        return MutantShulkerCommonConfig.despawns;
    }

    @Override
    public NodeEvaluatorDimensions getNodeEvaluatorDimensions() {
        return null;
    }

    class RemainStationaryGoal extends Goal {
        public RemainStationaryGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.TARGET, Goal.Flag.JUMP));
        }

        public void tick() {
            super.tick();
            MutantShulkerServant.this.getNavigation().stop();
        }

        public boolean canUse() {
            return MutantShulkerServant.this.shouldBeStationary();
        }
    }

    class EnterShellGoal extends Goal {
        public EnterShellGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.TARGET, Goal.Flag.JUMP));
        }

        public boolean canUse() {
            if (MutantShulkerServant.this.level().isClientSide || MutantShulkerServant.this.isInBox()) {
                return false;
            }

            if (MutantShulkerServant.this.isIdleStandby()) {
                return MutantShulkerServant.this.animationsUseable();
            }

            if (MutantShulkerServant.this.tickCount < MutantShulkerServant.this.nextEnterShellTime) {
                return false;
            }
            return MutantShulkerServant.this.getTarget() != null
                    && MutantShulkerServant.this.getHealth() < MutantShulkerServant.this.getMaxHealth()
                            * (MutantShulkerCommonConfig.enter_shell_health_threshold.get() / 100.0F)
                    && MutantShulkerServant.this.animationsUseable()
                    && MutantShulkerServant.this.random.nextInt(MutantShulkerCommonConfig.targeting_enter_shell_chance.get()) == 0;
        }

        public void start() {
            super.start();
            MutantShulkerServant.this.playSound(SoundEventInit.MUTANT_SHULKER_CLOSE.get());
            MutantShulkerServant.this.setInBox(true);
        }
    }

    class LeaveShellGoal extends Goal {
        public LeaveShellGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.TARGET, Goal.Flag.JUMP));
        }

        public boolean canUse() {
            if (MutantShulkerServant.this.level().isClientSide || !MutantShulkerServant.this.isInBox()) {
                return false;
            }

            if (MutantShulkerServant.this.getTarget() != null) {
                return MutantShulkerServant.this.healthHealedInShell >= MutantShulkerCommonConfig.leave_shell_health_healed.get()
                        || MutantShulkerServant.this.getHealth() >= MutantShulkerServant.this.getMaxHealth();
            }

            return !MutantShulkerServant.this.isIdleStandby();
        }

        public void start() {
            super.start();
            MutantShulkerServant.this.nextEnterShellTime = MutantShulkerServant.this.tickCount + MutantShulkerCommonConfig.enter_shell_cooldown.get();
            MutantShulkerServant.this.playSound(SoundEventInit.MUTANT_SHULKER_OPEN.get());
            MutantShulkerServant.this.setInBox(false);
        }
    }

    public class MutantShulkerServantBodyRotationControl extends BodyRotationControl implements Control {
        private final Mob mob;
        private int headStableTime;
        private float lastStableYHeadRot;

        public MutantShulkerServantBodyRotationControl(Mob p_24879_) {
            super(p_24879_);
            this.mob = p_24879_;
        }

        public void clientTick() {
            if (this.isMoving()) {
                this.mob.yBodyRot = this.mob.getYRot();
                this.rotateHeadIfNecessary();
                this.lastStableYHeadRot = this.mob.yHeadRot;
                this.headStableTime = 0;
            } else if (this.notCarryingMobPassengers()) {
                if (Math.abs(this.mob.yHeadRot - this.lastStableYHeadRot) > 15.0F) {
                    this.headStableTime = 0;
                    this.lastStableYHeadRot = this.mob.yHeadRot;
                    this.rotateBodyIfNecessary();
                } else {
                    ++this.headStableTime;
                    if (this.headStableTime > 10) {
                        this.rotateHeadTowardsFront();
                    }
                }
            }
        }

        private void rotateBodyIfNecessary() {
            if (this.mob instanceof MutantShulkerServant && ((MutantShulkerServant)this.mob).shouldBodyMoveWithHead()) {
                this.mob.yBodyRot = Mth.rotateIfNecessary(this.mob.yBodyRot, this.mob.yHeadRot, 75.0F);
            }
        }

        private void rotateHeadIfNecessary() {
            if (this.mob instanceof MutantShulkerServant && ((MutantShulkerServant)this.mob).shouldBodyMoveWithHead()) {
                this.mob.yHeadRot = Mth.rotateIfNecessary(this.mob.yHeadRot, this.mob.yBodyRot, 75.0F);
            }
        }

        private void rotateHeadTowardsFront() {
            if (this.mob instanceof MutantShulkerServant && ((MutantShulkerServant)this.mob).shouldBodyMoveWithHead()) {
                int i = this.headStableTime - 10;
                float f = Mth.clamp((float)((float)i / 10.0F), 0.0F, 1.0F);
                float f1 = 75.0F * (1.0F - f);
                this.mob.yBodyRot = Mth.rotateIfNecessary(this.mob.yBodyRot, this.mob.yHeadRot, f1);
            }
        }

        private boolean notCarryingMobPassengers() {
            return !(this.mob.getFirstPassenger() instanceof Mob);
        }

        private boolean isMoving() {
            double d0 = this.mob.getX() - this.mob.xo;
            double d1 = this.mob.getZ() - this.mob.zo;
            return d0 * d0 + d1 * d1 > 2.500000277905201E-7;
        }
    }
}
