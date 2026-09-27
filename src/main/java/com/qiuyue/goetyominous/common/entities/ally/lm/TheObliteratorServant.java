package com.qiuyue.goetyominous.common.entities.ally.lm;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IMoveGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.obliterator.AmbushGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.obliterator.AmbushLongerGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.obliterator.AmbushSwapGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.obliterator.ArmBlockGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.obliterator.BombShootGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.obliterator.CloseTeleportGrabGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IAttackGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IStateGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.obliterator.ITwoHitAttackGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.obliterator.JumpTeleportGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.obliterator.KickSmashGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.obliterator.QuadLaserBeamStateGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.obliterator.SecondPhaseStateGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.obliterator.SingleShotLaserGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.obliterator.StompGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.obliterator.StompKickGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.obliterator.StompStunGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.obliterator.TeleportAwaySingleLaserShotGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.obliterator.TeleportDoubleSlashGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.obliterator.TeleportGrabGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.obliterator.TheObliteratorCrushGrabStateGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.obliterator.ThirdPhaseStateGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.obliterator.UltimateAttackStateGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.obliterator.TheObliteratorClone;
import com.qiuyue.goetyominous.common.entities.ally.lm.obliterator.TheObliteratorCloneArmed;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.AnnihilationBomb;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.AnnihilationFlameStrike;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.EntityThrown;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.SmallAnnihilationBomb;
import com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.miauczel.legendary_monsters.Particle.ModParticles;
import net.miauczel.legendary_monsters.Particle.custom.AnnihilationBombTrail;
import net.miauczel.legendary_monsters.Particle.custom.AnnihilationSweepParticle;
import net.miauczel.legendary_monsters.Particle.custom.Circle;
import net.miauczel.legendary_monsters.Particle.custom.LightningParticle;
import net.miauczel.legendary_monsters.Particle.custom.MovingTrailParticle;
import net.miauczel.legendary_monsters.damagetype.ModDamageTypes;
import net.miauczel.legendary_monsters.effect.DynamicCameraZoomEntity;
import net.miauczel.legendary_monsters.effect.ModEffects;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Chorusling.TheWarpedOne.TheWarpedOneOld;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Effect.CameraShakeEntity;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.IAnimatedBoss.TheObliterator.TheObliteratorUtils;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Projectile.AnnihilationBeamEntity;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Projectile.AnnihilationGeyserEntity;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Projectile.AnnihilationGroundNukeStrikeEntity;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Projectile.AnnihilationPortalEntity;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Projectile.FlyingArmorEntity;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Projectile.PlasmaOrbEntity;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Projectile.TrackingBombEntity;
import net.miauczel.legendary_monsters.entity.ModEntities;
import net.miauczel.legendary_monsters.entity.client.ControlledAnim;
import net.miauczel.legendary_monsters.util.EntityUtil;
import net.miauczel.legendary_monsters.util.ParticleUtils;
import net.miauczel.legendary_monsters.sound.ModSounds;
import net.miauczel.legendary_monsters.util.MathUtils;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class TheObliteratorServant extends IAnimatedBossServant {

    private static final EntityDataAccessor<Integer> PHASE =
            SynchedEntityData.defineId(TheObliteratorServant.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Boolean> IS_QUAD_BEAM_RIGHT =
            SynchedEntityData.defineId(TheObliteratorServant.class, EntityDataSerializers.BOOLEAN);

    public static final int SECOND_PHASE = 2;
    public static final int THIRD_PHASE = 3;

    public enum Crackiness {
        NONE(1.0F),
        LOW(0.75F),
        MEDIUM(0.65F),
        HIGH(0.3F);

        private static final java.util.List<Crackiness> BY_DAMAGE = java.util.Arrays.stream(values())
                .sorted(java.util.Comparator.comparingDouble(c -> c.fraction))
                .toList();

        public final float fraction;

        Crackiness(float fraction) {
            this.fraction = fraction;
        }

        public static Crackiness byFraction(float fraction) {
            for (Crackiness crackiness : BY_DAMAGE) {
                if (fraction < crackiness.fraction) {
                    return crackiness;
                }
            }
            return NONE;
        }
    }

    public Crackiness getCrackiness() {
        return Crackiness.byFraction(this.getHealth() / this.getMaxHealth());
    }

    public boolean canShootTwice = false;
    public boolean succedGrabbing = false;

    public final int TELEPORT_FALL_COOLDOWN = MathUtils.toTicks(5.0F);
    public final int DIMENSIONAL_SHOOT_COOLDOWN = MathUtils.toTicks(3.5F);
    public final int AMBUSH_COOLDOWN = MathUtils.toTicks(0.0F);
    public final int TELEPORT_GRAB_COOLDOWN = MathUtils.toTicks(8.0F);
    public final int JUMP_TELEPORT_COOLDOWN = MathUtils.toTicks(7.5F);
    public final int CLONE_BURST_GRAB_COOLDOWN = MathUtils.toTicks(10.0F);
    public final int KICK_SMASH_GRAB_COOLDOWN = 0;
    public final int BACKSTEP_STOMP_COOLDOWN = MathUtils.toTicks(4.0F);
    public final int SPIN_SMASH_COOLDOWN = 0;
    public final int DOUBLE_SLASH_COOLDOWN = MathUtils.toTicks(5.0F);
    public final int STOMP_COOLDOWN = MathUtils.toTicks(0.0F);
    public final int SINGLE_SHOT_LASER_COOLDOWN = MathUtils.toTicks(8.0F);
    public final int STOMP_STUN_COOLDOWN = MathUtils.toTicks(8.0F);
    public final int ARM_BLOCK_COOLDOWN = MathUtils.toTicks(10.0F);
    public final int QUAD_BEAM_COOLDOWN = 0;
    public final int ULTIMATE_COOLDOWN = MathUtils.toTicks(30.0F);

    public int stomp_stun_cooldown = 0;
    public int dimensional_shoot_cooldown = 0;
    public int ambush_cooldown = 0;
    public int teleport_fall_cooldown = 0;
    public int kick_smash_grab_cooldown = 0;
    public int jump_teleport_cooldown = 0;
    public int teleport_uppercut_cooldown = 0;
    public int stomp_cooldown = 0;
    public int tracking_ball_charge_cooldown = 0;
    public int double_slash_cooldown = 0;
    public int spin_smash_cooldown = 0;
    public int single_shot_laser_cooldown = 0;
    public int clone_burst_grab_cooldown = 0;
    public int backstep_teleport_slash = 0;
    public int arm_block_cooldown = 0;
    public int ultimate_cooldown = 0;
    public int quad_beam_cooldown = 0;
    public boolean suppressTeleportShake;

    public int InvulnerabilityTime = 0;

    public final Random random1 = new Random();

    public int backstepStompRandom = 25;
    public int singleShotLaserRandom = 12;
    public int spinSmashRandom = 32;
    public int doubleSlashRandom = 22;
    public int jumpTeleportRandom = 5;
    public int cloneBurstRandom = 8;
    public int kickSmashRandom = 15;
    public int StompRandom = 20;
    public int teleportGrabRandom = 25;
    public int dimensionalShootRandom = 9;
    public int roarTeleportSmashRandom = 12;
    public int ambushRandom = 5;

    public int spinSmashAttackType = 1;
    public int dimensionalShootType = 2;
    public int doubleSlashType = 1;
    public int doubleSlashRightType = 1;
    public int roar_teleport_type = 1;
    public int ambushType = 2;
    public int backstepStompType = 1;

    public boolean hasStartedSecondTeleport = false;
    public boolean hasStartedSecondTeleportSmash = false;

    public boolean isRightUppercut;
    public int renderProgress = 0;
    public ControlledAnim controlledAnim = new ControlledAnim(15);
    public ControlledAnim QuadLaserShineUp = new ControlledAnim(45);

    public float partXRot = 0.0F;
    public float partYRot = 0.0F;
    public float vertexSize = 1.0F;

    public double lastX;
    public double lastY;
    public double lastZ;
    public double lastTargetX;
    public double lastTargetY;
    public double lastTargetZ;
    public double teleportX;
    public double teleportY;
    public double teleportZ;

    public int deathTicks;
    public int BossInvulnerabilityTime;

    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState spinSmashLeftAnimationState = new AnimationState();
    public AnimationState spinSmashRightAnimationState = new AnimationState();
    public AnimationState shootOnceAnimationState = new AnimationState();
    public AnimationState shootDoubleAnimationState = new AnimationState();
    public AnimationState doubleLeftHookComboAnimationState = new AnimationState();
    public AnimationState roarTeleportAnimationState = new AnimationState();
    public AnimationState landAnimationState = new AnimationState();
    public AnimationState landAgainAnimationState = new AnimationState();
    public AnimationState fallAnimationState = new AnimationState();
    public AnimationState ambushAnimationState = new AnimationState();
    public AnimationState ambushSwapAnimationState = new AnimationState();
    public AnimationState slashComboAnimationState = new AnimationState();
    public AnimationState teleportUppercutAnimationState = new AnimationState();
    public AnimationState teleportGrabPreAnimationState = new AnimationState();
    public AnimationState teleportGrabSuccessAnimationState = new AnimationState();
    public AnimationState teleportGrabFallAnimationState = new AnimationState();
    public AnimationState teleportGrabLandAnimationState = new AnimationState();
    public AnimationState teleportGrabFailRiseUpAnimationState = new AnimationState();
    public AnimationState jumpTeleportAnimationState = new AnimationState();
    public AnimationState teleportSlashComboAnimationState = new AnimationState();
    public AnimationState cloneBurstGrabAnimationState = new AnimationState();
    public AnimationState kickSmashGrabAnimationState = new AnimationState();
    public AnimationState rightSpinTeleportSmashAnimationState = new AnimationState();
    public AnimationState backstepTeleportSlashP2AnimationState = new AnimationState();
    public AnimationState backstepStompLeftAnimationState = new AnimationState();
    public AnimationState backstepStompRightAnimationState = new AnimationState();
    public AnimationState teleportGrabFailCrossSlashAnimationState = new AnimationState();
    public AnimationState teleportSlashComboRightAnimationState = new AnimationState();
    public AnimationState slashComboRightAnimationState = new AnimationState();
    public AnimationState trackingBallChargeAnimationState = new AnimationState();
    public AnimationState stompAnimationState = new AnimationState();
    public AnimationState stunStompAnimationState = new AnimationState();
    public AnimationState stompKickAnimationState = new AnimationState();
    public AnimationState spawnAnimationState = new AnimationState();
    public AnimationState stompAfterkickLeftAnimationState = new AnimationState();
    public AnimationState kickSmashAnimationState = new AnimationState();
    public AnimationState grabAfterKickSmashAnimationState = new AnimationState();
    public AnimationState kickSmashByeByeAnimationState = new AnimationState();
    public AnimationState p2AnimationState = new AnimationState();
    public AnimationState teleportGrabAfterKickSmashAnimationState = new AnimationState();
    public AnimationState crushGrabSuccessAnimationState = new AnimationState();
    public AnimationState crushGrabFailAnimationState = new AnimationState();
    public AnimationState singleShotLaserAnimationState = new AnimationState();
    public AnimationState singleShotLaserAfterStompAnimationState = new AnimationState();
    public AnimationState singleShotLaserTeleportEndAnimationState = new AnimationState();
    public AnimationState singleShotLaserEndAnimationState = new AnimationState();
    public AnimationState teleportAwaySingleShotLaserAnimationState = new AnimationState();
    public AnimationState singleShotLaserQuadEndAnimationState = new AnimationState();
    public AnimationState closeGrabPreAnimationState = new AnimationState();
    public AnimationState backstepStompRightSpinAnimationState = new AnimationState();
    public AnimationState p3AnimationState = new AnimationState();
    public AnimationState LeftgrabAfterKickSmashAnimationState = new AnimationState();
    public AnimationState LeftcrushGrabSuccessAnimationState = new AnimationState();
    public AnimationState LeftcrushGrabFailAnimationState = new AnimationState();
    public AnimationState ArmBlockAnimationState = new AnimationState();
    public AnimationState PortalUltimateAnimationState = new AnimationState();
    public AnimationState BombShootOnceEndAnimationState = new AnimationState();
    public AnimationState BombShootDoubleEndAnimationState = new AnimationState();
    public AnimationState DeathAnimationState = new AnimationState();
    public AnimationState TpGrabFail0AnimationState = new AnimationState();
    public AnimationState AmbushLongerAnimationState = new AnimationState();
    public int deathTime;

    public TheObliteratorServant(EntityType<? extends Summoned> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public double damageMultiplier() {
        return MobsConfig.TheObliteratorServantDamageMultiplier.get();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.TheObliteratorServantHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.TheObliteratorServantArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.TheObliteratorServantArmorToughness.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.TheObliteratorServantDamage.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.TheObliteratorServantMovementSpeed.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.TheObliteratorServantFollowRange.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.TheObliteratorServantKnockbackResistance.get())
                .add(Attributes.ATTACK_KNOCKBACK, AttributesConfig.TheObliteratorServantAttackKnockback.get());
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.TheObliteratorServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.TheObliteratorServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.TheObliteratorServantDamage.get());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PHASE, 1);
        this.entityData.define(IS_QUAD_BEAM_RIGHT, false);
    }

    public int getPhase() {
        return this.entityData.get(PHASE);
    }

    public void setPhase(int phase) {
        this.entityData.set(PHASE, phase);
    }

    public boolean getIsSecondPhase() {
        return this.getPhase() == SECOND_PHASE;
    }

    public boolean getIsThirdPhase() {
        return this.getPhase() > SECOND_PHASE;
    }

    public boolean getIsQuadBeamRight() {
        return this.entityData.get(IS_QUAD_BEAM_RIGHT);
    }

    public void setIsQuadBeamRight(boolean right) {
        this.entityData.set(IS_QUAD_BEAM_RIGHT, right);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("phase", this.getPhase());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setPhase(compound.contains("phase") ? compound.getInt("phase") : 1);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(6, new Summoned.WanderGoal<>(this, this.getFollowSpeed()));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));

        this.goalSelector.addGoal(2, new IMoveGoal(this, false, 1.0D));

        this.goalSelector.addGoal(0, new IStateGoal(this, 1, 1, 0,
                MathUtils.toTicks(8.0F), MathUtils.toTicks(8.0F)));

        this.goalSelector.addGoal(0, new IStateGoal(this, 60, 60, 0,
                MathUtils.toTicks(5.08F), MathUtils.toTicks(5.08F)));

        this.goalSelector.addGoal(0, new UltimateAttackStateGoal(this, 0, 53, 0, 130, 60) {
            @Override
            public boolean canUse() {
                return super.canUse() && TheObliteratorServant.this.canUltimate();
            }

            @Override
            public void stop() {
                super.stop();
                TheObliteratorServant.this.ultimate_cooldown = TheObliteratorServant.this.ULTIMATE_COOLDOWN;
            }
        });

        this.goalSelector.addGoal(0, new ArmBlockGoal(this, 52, 52, 0, 23, 23) {
            @Override
            public void stop() {
                super.stop();
                TheObliteratorServant.this.arm_block_cooldown = TheObliteratorServant.this.ARM_BLOCK_COOLDOWN;
            }
        });

        this.goalSelector.addGoal(1, new TeleportAwaySingleLaserShotGoal(this, 0, 42, 0, 35, 22, 30.0F, 7.0F) {
            @Override
            public void stop() {
                TheObliteratorServant.this.randomizeAllAttackTypes();
                super.stop();
            }

            @Override
            public boolean canUse() {
                return super.canUse()
                        && TheObliteratorServant.this.getRandom().nextFloat() * 100.0F
                        < (float) TheObliteratorServant.this.singleShotLaserRandom
                        && TheObliteratorServant.this.hasTarget()
                        && TheObliteratorServant.this.single_shot_laser_cooldown <= 0
                        && !TheObliteratorServant.this.canUltimate();
            }
        });

        this.goalSelector.addGoal(1, new SingleShotLaserGoal(this, 0, 21, 0, 49, 38, 6.0F) {
            @Override
            public void stop() {
                TheObliteratorServant.this.randomizeAllAttackTypes();
                super.stop();
            }

            @Override
            public boolean canUse() {
                return super.canUse()
                        && TheObliteratorServant.this.getRandom().nextFloat() * 100.0F
                        < (float) TheObliteratorServant.this.singleShotLaserRandom
                        && TheObliteratorServant.this.hasTarget()
                        && TheObliteratorServant.this.single_shot_laser_cooldown <= 0
                        && !TheObliteratorServant.this.canUltimate();
            }
        });

        this.goalSelector.addGoal(0, new IStateGoal(this, 45, 45, 0, 18, 5) {
            @Override
            public void stop() {
                super.stop();
                TheObliteratorServant.this.single_shot_laser_cooldown = TheObliteratorServant.this.SINGLE_SHOT_LASER_COOLDOWN;
            }
        });

        this.goalSelector.addGoal(0, new QuadLaserBeamStateGoal(this, 46, 46, 0, 111, 46) {
            @Override
            public void stop() {
                super.stop();
                TheObliteratorServant.this.quad_beam_cooldown = 0;
                TheObliteratorServant.this.single_shot_laser_cooldown = TheObliteratorServant.this.SINGLE_SHOT_LASER_COOLDOWN;
            }
        });

        this.goalSelector.addGoal(0, new IStateGoal(this, 44, 44, 0, 32, 24) {
            @Override
            public void stop() {
                super.stop();
                TheObliteratorServant.this.single_shot_laser_cooldown = TheObliteratorServant.this.SINGLE_SHOT_LASER_COOLDOWN;
            }
        });

        this.goalSelector.addGoal(0, new SecondPhaseStateGoal(this, 0, 2, 0, 88, 0));

        this.goalSelector.addGoal(0, new ThirdPhaseStateGoal(this, 0, 48, 0, 132, 132));

        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 28, 0, 68, 38, 5.0F) {
            @Override
            public void stop() {
                TheObliteratorServant.this.randomizeAllAttackTypes();
                TheObliteratorServant.this.backstep_teleport_slash = TheObliteratorServant.this.BACKSTEP_STOMP_COOLDOWN;
                super.stop();
            }

            @Override
            public boolean canUse() {
                return super.canUse()
                        && TheObliteratorServant.this.getRandom().nextFloat() * 100.0F
                        < (float) TheObliteratorServant.this.backstepStompRandom
                        && TheObliteratorServant.this.hasTarget()
                        && TheObliteratorServant.this.backstep_teleport_slash <= 0
                        && TheObliteratorServant.this.getNextbackstepStompType() == 1
                        && !TheObliteratorServant.this.canUltimate();
            }
        });

        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 29, 0, 68, 40, 5.0F) {
            @Override
            public void stop() {
                TheObliteratorServant.this.randomizeAllAttackTypes();
                TheObliteratorServant.this.backstep_teleport_slash = TheObliteratorServant.this.BACKSTEP_STOMP_COOLDOWN;
                super.stop();
            }

            @Override
            public boolean canUse() {
                return super.canUse()
                        && TheObliteratorServant.this.getRandom().nextFloat() * 100.0F
                        < (float) TheObliteratorServant.this.backstepStompRandom
                        && TheObliteratorServant.this.hasTarget()
                        && TheObliteratorServant.this.backstep_teleport_slash <= 0
                        && TheObliteratorServant.this.getNextbackstepStompType() == 2
                        && !TheObliteratorServant.this.canUltimate();
            }
        });

        this.goalSelector.addGoal(1, new KickSmashGoal(this, 0, 36, 0, 43, 7, 30, 101, 5.0F) {
            @Override
            public void stop() {
                TheObliteratorServant.this.randomizeAllAttackTypes();
                super.stop();
            }

            @Override
            public boolean canUse() {
                return super.canUse()
                        && TheObliteratorServant.this.getRandom().nextFloat() * 100.0F
                        < (float) TheObliteratorServant.this.kickSmashRandom
                        && TheObliteratorServant.this.hasTarget()
                        && TheObliteratorServant.this.kick_smash_grab_cooldown <= 0
                        && !TheObliteratorServant.this.canUltimate();
            }
        });

        this.goalSelector.addGoal(0, new TheObliteratorCrushGrabStateGoal(this, 37, 37, 41, 26, 26) {
            @Override
            public void stop() {
                super.stop();
                TheObliteratorServant.this.kick_smash_grab_cooldown = 0;
            }
        });

        this.goalSelector.addGoal(0, new TheObliteratorCrushGrabStateGoal(this, 49, 49, 51, 26, 26) {
            @Override
            public void stop() {
                super.stop();
                TheObliteratorServant.this.kick_smash_grab_cooldown = 0;
            }
        });

        this.goalSelector.addGoal(0, new IStateGoal(this, 41, 41, 0, 26, 0) {
            @Override
            public void stop() {
                super.stop();
                TheObliteratorServant.this.kick_smash_grab_cooldown = 0;
            }
        });

        this.goalSelector.addGoal(0, new IStateGoal(this, 40, 40, 0, 85, 0) {
            @Override
            public void stop() {
                super.stop();
                TheObliteratorServant.this.kick_smash_grab_cooldown = 0;
            }
        });

        this.goalSelector.addGoal(0, new IStateGoal(this, 51, 51, 0, 27, 0) {
            @Override
            public void stop() {
                super.stop();
                TheObliteratorServant.this.kick_smash_grab_cooldown = 0;
            }
        });

        this.goalSelector.addGoal(0, new IStateGoal(this, 50, 50, 0, 85, 0) {
            @Override
            public void stop() {
                super.stop();
                TheObliteratorServant.this.kick_smash_grab_cooldown = 0;
            }
        });

        this.goalSelector.addGoal(0, new TheObliteratorCrushGrabStateGoal(this, 39, 39, 41, 31, 31) {
            @Override
            public void stop() {
                super.stop();
                TheObliteratorServant.this.kick_smash_grab_cooldown = 0;
            }
        });

        this.goalSelector.addGoal(0, new IStateGoal(this, 38, 38, 0, 17, 5) {
            @Override
            public void stop() {
                super.stop();
                TheObliteratorServant.this.kick_smash_grab_cooldown = 0;
            }
        });

        this.goalSelector.addGoal(1, new StompGoal(this, 0, 34, 0, 23, 23, 5.0F) {
            @Override
            public void stop() {
                TheObliteratorServant.this.randomizeAllAttackTypes();
                if (!TheObliteratorServant.this.level().isClientSide) {
                    if (TheObliteratorServant.this.stomp_stun_cooldown <= 0) {
                        switch (this.entity.getRandom().nextInt(2)) {
                            case 0: {
                                this.entity.setAttackState(33);
                                break;
                            }
                            case 1: {
                                this.entity.setAttackState(35);
                            }
                        }
                    } else {
                        this.entity.setAttackState(33);
                    }
                }
                super.stop();
            }

            @Override
            public boolean canUse() {
                return super.canUse()
                        && TheObliteratorServant.this.getRandom().nextFloat() * 100.0F
                        < (float) TheObliteratorServant.this.StompRandom
                        && TheObliteratorServant.this.hasTarget()
                        && TheObliteratorServant.this.stomp_cooldown <= 0
                        && !TheObliteratorServant.this.canUltimate();
            }
        });

        this.goalSelector.addGoal(0, new StompStunGoal(this, 35, 35, 0, 65, 35) {
            @Override
            public void stop() {
                super.stop();
                TheObliteratorServant.this.stomp_stun_cooldown = TheObliteratorServant.this.STOMP_STUN_COOLDOWN;
                TheObliteratorServant.this.stomp_cooldown = TheObliteratorServant.this.STOMP_COOLDOWN;
            }
        });

        this.goalSelector.addGoal(0, new StompKickGoal(this, 33, 33, 0, 67, 24) {
            @Override
            public void stop() {
                super.stop();
                TheObliteratorServant.this.stomp_cooldown = TheObliteratorServant.this.STOMP_COOLDOWN;
            }
        });

        this.goalSelector.addGoal(0, new StompKickGoal(this, 43, 43, 0, 53, 33) {
            @Override
            public void stop() {
                super.stop();
                TheObliteratorServant.this.stomp_cooldown = TheObliteratorServant.this.STOMP_COOLDOWN;
            }
        });

        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 24, 0, 135, 95, 8.0F) {
            @Override
            public void stop() {
                TheObliteratorServant.this.randomizeAllAttackTypes();
                TheObliteratorServant.this.clone_burst_grab_cooldown = TheObliteratorServant.this.CLONE_BURST_GRAB_COOLDOWN;
                super.stop();
            }

            @Override
            public boolean canUse() {
                return super.canUse()
                        && TheObliteratorServant.this.getRandom().nextFloat() * 100.0F
                        < (float) TheObliteratorServant.this.cloneBurstRandom
                        && TheObliteratorServant.this.hasTarget()
                        && TheObliteratorServant.this.clone_burst_grab_cooldown <= 0
                        && TheObliteratorServant.this.getPhase() >= 2
                        && !TheObliteratorServant.this.canUltimate();
            }
        });

        this.goalSelector.addGoal(1, new JumpTeleportGoal(this, 0, 22, 0, 150, 55, 80, 100, 16.0F) {
            @Override
            public void stop() {
                TheObliteratorServant.this.randomizeAllAttackTypes();
                super.stop();
                TheObliteratorServant.this.jump_teleport_cooldown = TheObliteratorServant.this.JUMP_TELEPORT_COOLDOWN;
            }

            @Override
            public boolean canUse() {
                return super.canUse()
                        && TheObliteratorServant.this.getRandom().nextFloat() * 100.0F
                        < (float) TheObliteratorServant.this.jumpTeleportRandom
                        && TheObliteratorServant.this.hasTarget()
                        && TheObliteratorServant.this.jump_teleport_cooldown <= 0
                        && !TheObliteratorServant.this.canUltimate();
            }
        });

        float distance = 4.0F;

        this.goalSelector.addGoal(1, new CloseTeleportGrabGoal(this, 0, 17,
                this.targetIsNotNull() && this.distanceTo(this.target()) > distance ? 61 : 30, 30, 14, 8.0F, 4.6F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && TheObliteratorServant.this.getRandom().nextFloat() * 100.0F
                        < (float) TheObliteratorServant.this.teleportGrabRandom
                        && TheObliteratorServant.this.teleport_uppercut_cooldown <= 0
                        && TheObliteratorServant.this.getTarget() != null
                        && !TheObliteratorServant.this.canUltimate();
            }

            @Override
            public void stop() {
                TheObliteratorServant.this.randomizeAllAttackTypes();
                super.stop();
            }
        });

        this.goalSelector.addGoal(1, new TeleportGrabGoal(this, 0, 47,
                this.targetIsNotNull() && this.distanceTo(this.target()) > distance ? 61 : 30, 70, 53, 4.5F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && TheObliteratorServant.this.getRandom().nextFloat() * 100.0F
                        < (float) TheObliteratorServant.this.teleportGrabRandom
                        && TheObliteratorServant.this.teleport_uppercut_cooldown <= 0
                        && TheObliteratorServant.this.getTarget() != null
                        && !TheObliteratorServant.this.canUltimate();
            }

            @Override
            public void stop() {
                TheObliteratorServant.this.randomizeAllAttackTypes();
                super.stop();
            }
        });

        this.goalSelector.addGoal(0, new IStateGoal(this, 30, 30, 0, 47, 18) {
            @Override
            public void stop() {
                super.stop();
                TheObliteratorServant.this.teleport_uppercut_cooldown = TheObliteratorServant.this.TELEPORT_GRAB_COOLDOWN;
            }
        });

        this.goalSelector.addGoal(0, new IStateGoal(this, 61, 61, 0, 20, 20) {
            @Override
            public void stop() {
                super.stop();
                TheObliteratorServant.this.teleport_uppercut_cooldown = TheObliteratorServant.this.TELEPORT_GRAB_COOLDOWN;
            }
        });

        this.goalSelector.addGoal(1, new IStateGoal(this, 18, 18, 19, 18, 19));

        this.goalSelector.addGoal(1, new IStateGoal(this, 19, 19, 20, 9, 0));

        this.goalSelector.addGoal(0, new IStateGoal(this, 20, 20, 0, 32, 0) {
            @Override
            public void stop() {
                TheObliteratorServant.this.teleport_uppercut_cooldown = TheObliteratorServant.this.TELEPORT_GRAB_COOLDOWN;
                super.stop();
            }
        });

        int slashComboTick = 68;

        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 15, 0, slashComboTick, slashComboTick, 6.0F) {
            @Override
            public void stop() {
                TheObliteratorServant.this.randomizeAllAttackTypes();
                TheObliteratorServant.this.double_slash_cooldown = TheObliteratorServant.this.DOUBLE_SLASH_COOLDOWN;
                super.stop();
            }

            @Override
            public boolean canUse() {
                return super.canUse()
                        && TheObliteratorServant.this.getRandom().nextFloat() * 100.0F
                        < (float) TheObliteratorServant.this.doubleSlashRandom
                        && TheObliteratorServant.this.hasTarget()
                        && TheObliteratorServant.this.getNextDoubleSlashType() == 1
                        && TheObliteratorServant.this.double_slash_cooldown <= 0
                        && !TheObliteratorServant.this.canUltimate();
            }
        });

        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 31, 0, slashComboTick, slashComboTick, 6.0F) {
            @Override
            public void stop() {
                TheObliteratorServant.this.randomizeAllAttackTypes();
                TheObliteratorServant.this.double_slash_cooldown = TheObliteratorServant.this.DOUBLE_SLASH_COOLDOWN;
                super.stop();
            }

            @Override
            public boolean canUse() {
                return super.canUse()
                        && TheObliteratorServant.this.getRandom().nextFloat() * 100.0F
                        < (float) TheObliteratorServant.this.doubleSlashRandom
                        && TheObliteratorServant.this.hasTarget()
                        && TheObliteratorServant.this.getNextDoubleSlashRightType() == 1
                        && TheObliteratorServant.this.double_slash_cooldown <= 0
                        && !TheObliteratorServant.this.canUltimate();
            }
        });

        this.goalSelector.addGoal(1, new TeleportDoubleSlashGoal(this, 0, 23, 0, 84, 84, 6.0F) {
            @Override
            public void stop() {
                TheObliteratorServant.this.randomizeAllAttackTypes();
                TheObliteratorServant.this.double_slash_cooldown = TheObliteratorServant.this.DOUBLE_SLASH_COOLDOWN;
                super.stop();
            }

            @Override
            public boolean canUse() {
                return super.canUse()
                        && TheObliteratorServant.this.getRandom().nextFloat() * 100.0F
                        < (float) TheObliteratorServant.this.doubleSlashRandom
                        && TheObliteratorServant.this.hasTarget()
                        && TheObliteratorServant.this.getNextDoubleSlashType() == 2
                        && TheObliteratorServant.this.double_slash_cooldown <= 0
                        && !TheObliteratorServant.this.canUltimate();
            }
        });

        this.goalSelector.addGoal(1, new TeleportDoubleSlashGoal(this, 0, 32, 0, 84, 84, 6.0F) {
            @Override
            public void stop() {
                TheObliteratorServant.this.randomizeAllAttackTypes();
                TheObliteratorServant.this.double_slash_cooldown = TheObliteratorServant.this.DOUBLE_SLASH_COOLDOWN;
                super.stop();
            }

            @Override
            public boolean canUse() {
                return super.canUse()
                        && TheObliteratorServant.this.getRandom().nextFloat() * 100.0F
                        < (float) TheObliteratorServant.this.doubleSlashRandom
                        && TheObliteratorServant.this.hasTarget()
                        && TheObliteratorServant.this.double_slash_cooldown <= 0
                        && !TheObliteratorServant.this.canUltimate()
                        && TheObliteratorServant.this.getNextDoubleSlashRightType() == 2;
            }
        });

        this.goalSelector.addGoal(1, new ITwoHitAttackGoal(this, 0, 4, 0, 69, 30, 30, 30, 6.0F) {
            @Override
            public void stop() {
                TheObliteratorServant.this.randomizeAllAttackTypes();
                TheObliteratorServant.this.spin_smash_cooldown = 0;
                super.stop();
            }

            @Override
            public boolean canUse() {
                return super.canUse()
                        && TheObliteratorServant.this.getRandom().nextFloat() * 100.0F
                        < (float) TheObliteratorServant.this.spinSmashRandom
                        && TheObliteratorServant.this.hasTarget()
                        && TheObliteratorServant.this.getNextSpinSmashType() == 1
                        && TheObliteratorServant.this.spin_smash_cooldown <= 0
                        && !TheObliteratorServant.this.canUltimate();
            }
        });

        this.goalSelector.addGoal(1, new ITwoHitAttackGoal(this, 0, 5, 0, 69, 30, 30, 30, 6.0F) {
            @Override
            public void stop() {
                TheObliteratorServant.this.randomizeAllAttackTypes();
                TheObliteratorServant.this.spin_smash_cooldown = 0;
                super.stop();
            }

            @Override
            public boolean canUse() {
                return super.canUse()
                        && TheObliteratorServant.this.getRandom().nextFloat() * 100.0F
                        < (float) TheObliteratorServant.this.spinSmashRandom
                        && TheObliteratorServant.this.hasTarget()
                        && TheObliteratorServant.this.getNextSpinSmashType() == 2
                        && TheObliteratorServant.this.spin_smash_cooldown <= 0
                        && !TheObliteratorServant.this.canUltimate();
            }
        });

        this.goalSelector.addGoal(1, new BombShootGoal(this, 0, 6, 0, 38, 63, 12.0F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && TheObliteratorServant.this.getRandom().nextFloat() * 100.0F
                        < (float) TheObliteratorServant.this.dimensionalShootRandom
                        && TheObliteratorServant.this.hasTarget()
                        && TheObliteratorServant.this.dimensional_shoot_cooldown <= 0
                        && !TheObliteratorServant.this.canUltimate();
            }
        });

        this.goalSelector.addGoal(0, new IStateGoal(this, 54, 54, 0, 24, 10) {
            @Override
            public void stop() {
                TheObliteratorServant.this.randomizeAllAttackTypes();
                TheObliteratorServant.this.dimensional_shoot_cooldown = TheObliteratorServant.this.DIMENSIONAL_SHOOT_COOLDOWN;
                super.stop();
            }
        });

        this.goalSelector.addGoal(0, new IStateGoal(this, 55, 55, 0, 60, 60) {
            @Override
            public void start() {
                TheObliteratorServant.this.canShootTwice = false;
                super.start();
            }

            @Override
            public void stop() {
                TheObliteratorServant.this.canShootTwice = false;
                TheObliteratorServant.this.randomizeAllAttackTypes();
                TheObliteratorServant.this.dimensional_shoot_cooldown = TheObliteratorServant.this.DIMENSIONAL_SHOOT_COOLDOWN;
                super.stop();
            }
        });

        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 9, 10, 100, 54, 8.0F) {
            @Override
            public void stop() {
                TheObliteratorServant.this.setNoGravity(false);
                TheObliteratorServant.this.setInvulnerable(false);
                TheObliteratorServant.this.setDeltaMovement(0.0D, -0.1F, 0.0D);
                super.stop();
            }

            @Override
            public boolean canUse() {
                return super.canUse()
                        && TheObliteratorServant.this.getRandom().nextFloat() * 100.0F
                        < (float) TheObliteratorServant.this.roarTeleportSmashRandom
                        && TheObliteratorServant.this.teleport_fall_cooldown <= 0
                        && TheObliteratorServant.this.getTarget() != null
                        && !TheObliteratorServant.this.canUltimate();
            }
        });

        this.goalSelector.addGoal(1, new IStateGoal(this, 10, 10, 11, 100, 100) {
            @Override
            public boolean canUse() {
                return super.canUse() && TheObliteratorServant.this.getNextRoarAttackType() == 1;
            }
        });

        this.goalSelector.addGoal(1, new IStateGoal(this, 10, 10, 12, 100, 100) {
            @Override
            public void start() {
                TheObliteratorServant.this.hasStartedSecondTeleportSmash = false;
                super.start();
            }

            @Override
            public boolean canUse() {
                return super.canUse()
                        && TheObliteratorServant.this.getNextRoarAttackType() == 2
                        && !TheObliteratorServant.this.getHasStartedSecondTeleportSmash();
            }
        });

        this.goalSelector.addGoal(1, new IStateGoal(this, 10, 10, 11, 100, 100) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && TheObliteratorServant.this.getNextRoarAttackType() == 2
                        && TheObliteratorServant.this.getHasStartedSecondTeleportSmash();
            }
        });

        this.goalSelector.addGoal(0, new IStateGoal(this, 12, 12, 10, 24, 100) {
            @Override
            public void start() {
                TheObliteratorServant.this.hasStartedSecondTeleportSmash = true;
                super.start();
            }

            @Override
            public boolean canUse() {
                return super.canUse() && TheObliteratorServant.this.getNextRoarAttackType() == 2;
            }
        });

        this.goalSelector.addGoal(0, new IStateGoal(this, 11, 11, 0, 40, 0) {
            @Override
            public void stop() {
                super.stop();
                TheObliteratorServant.this.randomizeAllAttackTypes();
                TheObliteratorServant.this.teleport_fall_cooldown = TheObliteratorServant.this.TELEPORT_FALL_COOLDOWN;
            }
        });

        this.goalSelector.addGoal(1, new AmbushGoal(this, 0, 13, 0, 81, 81, 12.0F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && TheObliteratorServant.this.getRandom().nextFloat() * 100.0F
                        < (float) TheObliteratorServant.this.ambushRandom
                        && TheObliteratorServant.this.hasTarget()
                        && TheObliteratorServant.this.ambush_cooldown <= 0
                        && TheObliteratorServant.this.getNextAmbushType() == 2
                        && !TheObliteratorServant.this.canUltimate()
                        && TheObliteratorServant.this.getPhase() <= 1;
            }

            @Override
            public void stop() {
                TheObliteratorServant.this.ambush_cooldown = TheObliteratorServant.this.AMBUSH_COOLDOWN;
                TheObliteratorServant.this.randomizeAllAttackTypes();
                super.stop();
            }
        });

        this.goalSelector.addGoal(1, new AmbushLongerGoal(this, 0, 62, 0, 100, 100, 12.0F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && TheObliteratorServant.this.getRandom().nextFloat() * 100.0F
                        < (float) TheObliteratorServant.this.ambushRandom
                        && TheObliteratorServant.this.hasTarget()
                        && TheObliteratorServant.this.ambush_cooldown <= 0
                        && TheObliteratorServant.this.getNextAmbushType() == 2
                        && !TheObliteratorServant.this.canUltimate()
                        && TheObliteratorServant.this.getPhase() >= 2;
            }

            @Override
            public void stop() {
                TheObliteratorServant.this.ambush_cooldown = TheObliteratorServant.this.AMBUSH_COOLDOWN;
                TheObliteratorServant.this.randomizeAllAttackTypes();
                super.stop();
            }
        });

        this.goalSelector.addGoal(1, new AmbushSwapGoal(this, 0, 14, 0, 130, 130, 12.0F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && TheObliteratorServant.this.getRandom().nextFloat() * 100.0F
                        < (float) TheObliteratorServant.this.ambushRandom
                        && TheObliteratorServant.this.hasTarget()
                        && TheObliteratorServant.this.ambush_cooldown <= 0
                        && TheObliteratorServant.this.getNextAmbushType() == 22;
            }

            @Override
            public void stop() {
                TheObliteratorServant.this.ambush_cooldown = TheObliteratorServant.this.AMBUSH_COOLDOWN;
                TheObliteratorServant.this.randomizeAllAttackTypes();
                super.stop();
            }
        });
    }

    @Override
    public void tick() {
        if (this.getAttackState() == 21 && this.attackTicks == 6) {
            this.teleport(this.lastX, this.lastY, this.lastZ);
        }
        super.tick();

        AttributeInstance armorToughness = this.getAttribute(Attributes.ARMOR_TOUGHNESS);
        if (armorToughness != null) {
            armorToughness.setBaseValue(this.getPhase() >= SECOND_PHASE
                    ? AttributesConfig.TheObliteratorServantArmorToughness.get() * 0.5D
                    : AttributesConfig.TheObliteratorServantArmorToughness.get());
        }

        if (this.isVehicle() && this.getFirstPassenger() != null) {
            this.getFirstPassenger().setShiftKeyDown(false);
        }

        if (this.vertexSize != 0.0F && this.level().isClientSide && this.getAttackState() != 2) {
            this.vertexSize = 0.0F;
        }

        if (this.stomp_stun_cooldown > 0) {
            --this.stomp_stun_cooldown;
        }
        if (this.teleport_uppercut_cooldown > 0) {
            --this.teleport_uppercut_cooldown;
        }
        if (this.single_shot_laser_cooldown > 0) {
            --this.single_shot_laser_cooldown;
        }
        if (this.tracking_ball_charge_cooldown > 0) {
            --this.tracking_ball_charge_cooldown;
        }
        if (this.dimensional_shoot_cooldown > 0) {
            --this.dimensional_shoot_cooldown;
        }
        if (this.teleport_fall_cooldown > 0) {
            --this.teleport_fall_cooldown;
        }
        if (this.ambush_cooldown > 0) {
            --this.ambush_cooldown;
        }
        if (this.jump_teleport_cooldown > 0) {
            --this.jump_teleport_cooldown;
        }
        if (this.clone_burst_grab_cooldown > 0) {
            --this.clone_burst_grab_cooldown;
        }
        if (this.kick_smash_grab_cooldown > 0) {
            --this.kick_smash_grab_cooldown;
        }
        if (this.backstep_teleport_slash > 0) {
            --this.backstep_teleport_slash;
        }
        if (this.spin_smash_cooldown > 0) {
            --this.spin_smash_cooldown;
        }
        if (this.double_slash_cooldown > 0) {
            --this.double_slash_cooldown;
        }
        if (this.stomp_cooldown > 0) {
            --this.stomp_cooldown;
        }
        if (this.arm_block_cooldown > 0 && this.getPhase() >= THIRD_PHASE) {
            --this.arm_block_cooldown;
        }
        if (this.ultimate_cooldown > 0) {
            --this.ultimate_cooldown;
        }
        if (this.quad_beam_cooldown > 0) {
            --this.quad_beam_cooldown;
        }

        if (!this.level().isClientSide && this.InvulnerabilityTime > 0) {
            --this.InvulnerabilityTime;
        }

        if (this.level().isClientSide) {
            this.idleAnimationState.animateWhen(this.getAttackState() == 0, this.tickCount);
        }

        if (!this.level().isClientSide && (this.getAttackState() == 1 || this.getAttackState() == 60)) {
            this.facePlayerDuringAnimation();
        }
    }

    private void facePlayerDuringAnimation() {
        Player player = this.playerToFaceDuringAnimation();
        if (player == null) {
            return;
        }
        Vec3 vec3 = player.position().subtract(this.position());
        float yaw = -((float) Mth.atan2(vec3.x, vec3.z)) * (180.0F / (float) Math.PI);
        float targetRot = Mth.approachDegrees(this.getYRot(), yaw, 20.0F);
        this.setYRot(targetRot);
        this.setYHeadRot(targetRot);
        this.yBodyRot = Mth.approachDegrees(this.yBodyRot, targetRot, 10.0F);
    }

    private Player playerToFaceDuringAnimation() {
        if (this.getTrueOwner() instanceof Player owner && owner.isAlive()) {
            return owner;
        }
        return this.level().getNearestPlayer(this, 32.0D);
    }

    @Override
    public void aiStep() {
        this.UpdateWithAttack();

        if (this.isDuringTeleportation(this.level().isClientSide)
                && this.getAttackState() != 13
                && this.getAttackState() != 14
                && this.getAttackState() != 62) {
            float f = Mth.cos(this.yBodyRot * ((float) Math.PI / 180F));
            float f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180F));
            double theta = (double) this.yBodyRot * (Math.PI / 180D);
            double vecX = Math.cos(theta += 1.5707963267948966);
            double vecZ = Math.sin(theta);
            float vec = 0.0F;
            float offset = 2.0F;
            float offset2 = -2.0F;
            double dx = this.getX() + (double) vec * vecX + (double) (f * offset);
            double dx2 = this.getX() + (double) vec * vecX + (double) (f * offset2);
            double dy = this.getY() + 1.5;
            double dz = this.getZ() + (double) vec * vecZ + (double) (f1 * offset);
            double dz2 = this.getZ() + (double) vec * vecZ + (double) (f1 * offset2);
            float ran = 0.4F;
            float r = 0.0F;
            float g = 0.7647059F + this.getRandom().nextFloat() * ran;
            float b = 0.0F;
            if (this.level().isClientSide) {
                this.level().addParticle(new AnnihilationBombTrail.OrbData(r, g, b, 0.1F, 2.5F, this.getId()), dx, dy, dz, 0.0D, 0.0D, 0.0D);
                this.level().addParticle(new AnnihilationBombTrail.OrbData(r, g, b, 0.1F, 2.5F, this.getId()), dx2, dy, dz2, 0.0D, 0.0D, 0.0D);
            }
        }

        if (this.level().isClientSide) {
            if (this.isDuringTeleportation(true) && !this.isInvisible()) {
                this.setInvisible(true);
            } else if (!this.isDuringTeleportation(true) && this.isInvisible()) {
                this.setInvisible(false);
            }
        } else if (!this.isDuringTeleportation(false) && this.isInvulnerable()) {
            this.setInvulnerable(false);
        }

        if (!this.isDuringTeleportation(this.level().isClientSide)) {
            if (this.getIsSecondPhase()) {
                if (this.tickCount % 10 == 0) {
                    if (this.level().isClientSide) {
                        this.level().addParticle(ModParticles.BIG_ANNIHILATION_FLAME.get(),
                                this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 0.0D, 0.025D, 0.0D);
                    }
                }
            } else if (this.getIsThirdPhase() && this.tickCount % 3 == 0) {
                if (this.level().isClientSide) {
                    this.level().addParticle(ModParticles.BIG_ANNIHILATION_FLAME.get(),
                            this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 0.0D, 0.025D, 0.0D);
                }
            }
        }

        super.aiStep();
    }

    public boolean isDuringTeleportation(boolean statement) {
        return statement && this.getAttackState() == 7 && this.attackTicks > 51 && this.attackTicks < 54
                || this.getAttackState() == 13 && this.attackTicks > 30 && this.attackTicks < 61
                || this.getAttackState() == 14 && this.attackTicks > 30 && this.attackTicks < 57
                || this.getAttackState() == 14 && this.attackTicks > 73 && this.attackTicks < 100
                || this.getAttackState() == 21 && this.attackTicks >= 5 && this.attackTicks < 8
                || this.getAttackState() == 44 && this.attackTicks >= 10 && this.attackTicks < 16
                || this.getAttackState() == 52 && this.attackTicks >= 12 && this.attackTicks < 18
                || this.getAttackState() == 53 && this.attackTicks >= 105 && this.attackTicks < 113
                || this.getAttackState() == 47 && this.attackTicks >= 34 && this.attackTicks < 37
                || this.getAttackState() == 22 && this.attackTicks >= 78 && this.attackTicks < 86
                || this.getAttackState() == 22 && this.attackTicks >= 45 && this.attackTicks < 50
                || this.getAttackState() == 23 && this.attackTicks >= 14 && this.attackTicks < 18
                || this.getAttackState() == 32 && this.attackTicks >= 14 && this.attackTicks < 18
                || this.getAttackState() == 55 && this.attackTicks >= 11 && this.attackTicks < 16
                || this.getAttackState() == 35 && this.attackTicks >= 16 && this.attackTicks <= 18
                || this.getAttackState() == 33 && this.attackTicks >= 13 && this.attackTicks <= 17
                || this.getAttackState() == 46 && this.attackTicks >= 9 && this.attackTicks <= 14
                || this.getAttackState() == 12 && this.attackTicks >= 18 && this.attackTicks <= 21
                || this.getAttackState() == 62 && this.attackTicks > 30 && this.attackTicks < 80;
    }

    public boolean isShootingClusterBomb() {
        return this.getAttackState() == 6 || this.getAttackState() == 7 || this.getAttackState() == 55;
    }

    public boolean isHittingWithBlades() {
        int state = this.getAttackState();
        return state == 15 || state == 23 || state == 31 || state == 32
                || state == 4 || state == 5 || state == 30 || state == 35;
    }

    public Vec3 findTeleportSpot(LivingEntity entity, float range, float iteractions) {
        Vec3 entityPos = entity.position();
        Level level = this.level();
        for (int i = 0; (float) i < iteractions; ++i) {
            double x = entityPos.x() + (this.getRandom().nextDouble() - 0.5D) * (double) range;
            double z = entityPos.z() + (this.getRandom().nextDouble() - 0.5D) * (double) range;
            double y = entityPos.y();
            BlockPos pos = new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z));
            if (level.isEmptyBlock(pos) && !level.getBlockState(pos.below()).isAir()) {
                return new Vec3(x, y, z);
            }
        }
        return null;
    }

    public void teleportRandomly(LivingEntity entity, float range, float iteractions) {
        Vec3 spot = this.findTeleportSpot(entity, range, iteractions);
        if (spot != null) {
            this.teleport(spot.x, spot.y, spot.z);
        }
    }

    public boolean teleport(double x, double y, double z) {
        Level level = this.level();
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ModParticles.TELEPORT_EFFECT.get(), this.getX(), this.getY() + 3.0D, this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos(x, y, z);
        while (mutablePos.getY() > level.getMinBuildHeight() && !level.getBlockState(mutablePos).blocksMotion()) {
            mutablePos.move(Direction.DOWN);
        }
        BlockState state = level.getBlockState(mutablePos);
        if (!state.blocksMotion()) {
            return false;
        }
        EntityTeleportEvent.EnderEntity event = ForgeEventFactory.onEnderTeleport(this, x, y, z);
        if (event.isCanceled()) {
            return false;
        }
        Vec3 oldPos = this.position();
        boolean teleported = this.teleportBoolean(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);
        if (teleported) {
            level.gameEvent(GameEvent.TELEPORT, oldPos, GameEvent.Context.of(this));
            if (!this.isSilent()) {
                if (!this.suppressTeleportShake) {
                    CameraShakeEntity.cameraShake(level, this.position(), 10.0F, 0.1F, 5, 5);
                }
                this.playSound(SoundEvents.SHULKER_TELEPORT, 4.0F, 1.0F);
            }
        }
        return teleported;
    }

    public boolean teleportBoolean(double x, double y, double z, boolean playSound) {
        double oldX = this.getX();
        double oldY = this.getY();
        double oldZ = this.getZ();
        double targetY = y;
        boolean success = false;
        BlockPos pos = BlockPos.containing(x, y, z);
        Level level = this.level();
        if (level.hasChunkAt(pos)) {
            boolean foundGround = false;
            while (!foundGround && pos.getY() > level.getMinBuildHeight()) {
                BlockPos below = pos.below();
                if (level.getBlockState(below).blocksMotion()) {
                    foundGround = true;
                    continue;
                }
                targetY -= 1.0D;
                pos = below;
            }
            if (foundGround) {
                this.teleportTo(x, targetY, z);
                if (level.noCollision(this) && !level.containsAnyLiquid(this.getBoundingBox())) {
                    success = true;
                }
            }
        }
        if (!success) {
            this.teleportTo(oldX, oldY, oldZ);
            return false;
        }
        if (playSound) {
            level.broadcastEntityEvent(this, (byte) 46);
        }
        this.getNavigation().stop();
        return true;
    }

    public void saveTeleportPositions(double x, double y, double z) {
        this.lastX = x;
        this.lastY = y;
        this.lastZ = z;
    }

    public void saveTeleportPos(LivingEntity target, boolean statement, float vec1, float offset1) {
        if (statement) {
            float f = Mth.cos(target.yBodyRot * ((float) Math.PI / 180));
            float f1 = Mth.sin(target.yBodyRot * ((float) Math.PI / 180));
            double theta = (double) target.yBodyRot * (Math.PI / 180);
            double vecX = Math.cos(theta += 1.5707963267948966);
            double vecZ = Math.sin(theta);
            float vec = vec1;
            float offset = offset1;
            this.teleportX = target.getX() + (double) vec * vecX + (double) (f * offset);
            this.teleportZ = target.getZ() + (double) vec * vecZ + (double) (f1 * offset);
            this.teleportY = target.getY();
        }
    }

    public void spawnDuplicateVersions(double x, double z, double minY, double maxY, float rotation, int delay, float destX, float destY, float destZ) {
        BlockPos pos = new BlockPos((int) x, (int) maxY, (int) z);
        boolean found = false;
        double offset = 0.0D;
        do {
            BlockPos below = pos.below();
            if (!this.level().getBlockState(below).isFaceSturdy(this.level(), below, Direction.UP)) {
                continue;
            }
            if (!this.level().isEmptyBlock(pos)) {
                VoxelShape shape = this.level().getBlockState(pos).getCollisionShape(this.level(), pos);
                if (!shape.isEmpty()) {
                    offset = shape.max(Direction.Axis.Y);
                }
            }
            found = true;
            break;
        } while ((pos = pos.below()).getY() >= Mth.floor(minY) - 1);
        if (found) {
            this.level().addFreshEntity(new TheObliteratorClone(this.level(), x, (double) pos.getY() + offset, z,
                    rotation, delay, this, 16.0F, destX, destY, destZ));
        }
    }

    public void spawnArmedClones(double x, double z, double minY, double maxY, float rotation, int delay, double destX, double destY, double destZ, int animation, int life) {
        this.level().addFreshEntity(new TheObliteratorCloneArmed(this.level(), x, this.getY(), z,
                rotation, delay, this, 16.0F, (float) destX, (float) destY, (float) destZ, animation, life));
    }

    public boolean hasTarget() {
        return this.getTarget() != null;
    }

    @Override
    public LivingEntity target() {
        return this.getTarget();
    }

    public boolean canUltimate() {
        return this.getIsThirdPhase()
                && this.ultimate_cooldown <= 0
                && this.targetIsNotNull()
                && this.distanceTo(this.getTarget()) < 7.0F;
    }

    public void randomizeAllAttackTypes() {
        this.randomizeNextSpinSmashType(2);
        this.randomizeNextDimensionalShootType(2);
        this.randomizeNextRoarTeleportSmashType(2);
        this.randomizeNextAmbushType(2);
        this.randomizeNextDoubleSlashType(2);
        this.randomizeNextbackstepStompType(2);
        this.randomizeNextDoubleSlashRightType(2);
    }

    public int getNextSpinSmashType() {
        return this.spinSmashAttackType;
    }

    public void randomizeNextSpinSmashType(int rolls) {
        switch (this.getRandom().nextInt(rolls)) {
            case 0: {
                this.spinSmashAttackType = 1;
                break;
            }
            case 1: {
                this.spinSmashAttackType = 2;
            }
        }
    }

    public int getNextDimensionalShootType() {
        return this.spinSmashAttackType;
    }

    public void randomizeNextDimensionalShootType(int rolls) {
        switch (this.getRandom().nextInt(rolls)) {
            case 0: {
                this.dimensionalShootType = 2;
                break;
            }
            case 1: {
                this.dimensionalShootType = 1;
            }
        }
    }

    public int getNextDoubleSlashType() {
        return this.doubleSlashType;
    }

    public void randomizeNextDoubleSlashType(int rolls) {
        switch (this.getRandom().nextInt(rolls)) {
            case 0: {
                this.doubleSlashType = 1;
                break;
            }
            case 1: {
                this.doubleSlashType = 2;
            }
        }
    }

    public int getNextDoubleSlashRightType() {
        return this.doubleSlashRightType;
    }

    public void randomizeNextDoubleSlashRightType(int rolls) {
        switch (this.getRandom().nextInt(rolls)) {
            case 0: {
                this.doubleSlashRightType = 1;
                break;
            }
            case 1: {
                this.doubleSlashRightType = 2;
            }
        }
    }

    public boolean getHasStartedSecondTeleportSmash() {
        return this.hasStartedSecondTeleportSmash;
    }

    public int getNextRoarAttackType() {
        return this.roar_teleport_type;
    }

    public void randomizeNextRoarTeleportSmashType(int rolls) {
        switch (this.getRandom().nextInt(rolls)) {
            case 0: {
                this.roar_teleport_type = 1;
                break;
            }
            case 1: {
                this.hasStartedSecondTeleportSmash = false;
                this.roar_teleport_type = 2;
            }
        }
    }

    public int getNextAmbushType() {
        return this.ambushType;
    }

    public void randomizeNextAmbushType(int rolls) {
        switch (this.getRandom().nextInt(rolls)) {
            case 0: {
                this.ambushType = 2;
                break;
            }
            case 1: {
                this.ambushType = 2;
            }
        }
    }

    public int getNextbackstepStompType() {
        return this.backstepStompType;
    }

    public void randomizeNextbackstepStompType(int rolls) {
        block0:
        switch (this.getRandom().nextInt(rolls)) {
            case 0: {
                this.backstepStompType = 1;
                break;
            }
            case 1: {
                switch (this.getRandom().nextInt(2)) {
                    case 0: {
                        this.backstepStompType = 2;
                        break block0;
                    }
                    case 1: {
                        this.backstepStompType = 3;
                    }
                }
            }
        }
    }

    public int getStunDuration() {
        return 50;
    }

    public boolean canArmBlock() {
        return this.getAttackState() == 0
                && this.arm_block_cooldown <= 0
                && !this.level().isClientSide
                && this.getTarget() != null
                && this.distanceTo(this.getTarget()) < 6.0F
                && this.getPhase() >= THIRD_PHASE;
    }

    public boolean canReduceDamageDuringPhaseTransition() {
        Crackiness crackiness = this.getCrackiness();
        return this.getPhase() <= 1 && crackiness == Crackiness.MEDIUM
                || this.getPhase() <= 2 && crackiness == Crackiness.HIGH;
    }

    @Override
    public int attackDelayTicksValue() {
        return this.arm_block_cooldown <= 0 ? 3 : 1;
    }

    @Override
    public double damageCap() {
        return AttributesConfig.TheObliteratorServantDamageCap.get();
    }

    @Override
    public int adaptationFactor() {
        return 20 * AttributesConfig.TheObliteratorServantAdaptationFactorMultiplier.get();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.getAttackState() == 53 && source.is(DamageTypeTags.BYPASSES_ARMOR)
                && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)
                || this.canReduceDamageDuringPhaseTransition()) {
            amount *= 0.5F;
        }

        if (!this.level().isClientSide
                && this.InvulnerabilityTime > 0
                && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
        }

        if (!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)
                && (this.getAttackState() == 52 && this.attackTicks <= 12
                || this.getAttackState() == 19
                || this.getAttackState() == 40
                || this.getAttackState() == 2
                || this.getAttackState() == 48
                || this.getAttackState() == 1
                || this.isVehicle()
                || this.isDuringTeleportation(!this.level().isClientSide))) {
            return false;
        }

        if (this.canArmBlock() && amount > 1.0F) {
            this.setAttackState(52);
            this.playSound(ModSounds.BLOCK.get(), 1.0F, 1.0F);
            return false;
        }

        if ((source.is(DamageTypes.ARROW) || source.is(DamageTypes.MOB_PROJECTILE))
                && this.getPhase() == 1
                && MobsConfig.TheObliteratorServantFirstPhaseProjectileImmunity.get()) {
            return false;
        }

        boolean hurt1 = super.hurt(source, amount);
        if (hurt1 && !this.level().isClientSide && this.InvulnerabilityTime <= 0) {
            this.InvulnerabilityTime = 10;
        }
        return hurt1;
    }

    @Override
    public void push(double pX, double pY, double pZ) {
    }

    @Override
    protected void positionRider(Entity passenger, Entity.MoveFunction moveFunc) {
        if (!this.hasPassenger(passenger)) {
            return;
        }

        if (this.getAttackState() == 20 || this.getAttackState() == 18 || this.getAttackState() == 19) {
            float offset = 0.0F;
            float startPos = 3.5F;
            float cos = Mth.cos(this.yBodyRot * ((float) Math.PI / 180F));
            float sin = Mth.sin(this.yBodyRot * ((float) Math.PI / 180F));
            double theta = Math.toRadians((double) this.yBodyRot + 90.0D);
            double vecX = Math.cos(theta);
            double vecZ = Math.sin(theta);
            passenger.fallDistance = 0.0F;
            float startYOffset = 3.0F;
            float endYOffset = 0.0F;
            float yOffset;
            if (this.getAttackState() == 20) {
                float t = Mth.clamp((float) this.attackTicks / 5.0F, 0.0F, 1.0F);
                yOffset = Mth.lerp(t, startYOffset, endYOffset);
            } else {
                yOffset = startYOffset;
            }
            moveFunc.accept(passenger,
                    this.getX() + (double) startPos * vecX + (double) (cos * offset),
                    this.getY() + (double) yOffset,
                    this.getZ() + (double) startPos * vecZ + (double) (sin * offset));
        }

        if (this.getAttackState() == 40 || this.getAttackState() == 37) {
            this.positionSideGrabRider(passenger, moveFunc, 1.0F);
        }

        if (this.getAttackState() == 50 || this.getAttackState() == 49 || this.getAttackState() == 39) {
            this.positionSideGrabRider(passenger, moveFunc, -1.0F);
        }

        if (this.getAttackState() == 20 && this.attackTicks == 8) {
            passenger.stopRiding();
        }

        if (this.getAttackState() == 0) {
            passenger.stopRiding();
        }
    }

    private void positionSideGrabRider(Entity passenger, Entity.MoveFunction moveFunc, float endXOffset) {
        double theta = Math.toRadians((double) this.yBodyRot + 90.0D);
        double forwardX = Math.cos(theta);
        double forwardZ = Math.sin(theta);
        float cos = Mth.cos(this.yBodyRot * ((float) Math.PI / 180F));
        float sin = Mth.sin(this.yBodyRot * ((float) Math.PI / 180F));
        int distanceTicks = 10;
        int sideTicks = 10;
        int upTicks = 11;
        int pauseTicks = 5;
        int downTicks = 6;
        int verticalTicks = upTicks + pauseTicks + downTicks;
        int tD = Mth.clamp(this.attackTicks, 0, distanceTicks);
        int tX = Mth.clamp(this.attackTicks, 0, sideTicks);
        int tY = Mth.clamp(this.attackTicks, 0, verticalTicks);
        float distance = Mth.lerp((float) tD / (float) distanceTicks, 3.0F, 2.0F);
        float xOffset = Mth.lerp((float) tX / (float) sideTicks, 0.0F, endXOffset);
        float yOffset;
        if (tY < upTicks) {
            yOffset = Mth.lerp((float) tY / (float) upTicks, 0.0F, 5.0F);
        } else if (tY < upTicks + pauseTicks) {
            yOffset = 5.0F;
        } else {
            yOffset = Mth.lerp((float) (tY - (upTicks + pauseTicks)) / (float) downTicks, 5.0F, 3.0F);
        }
        moveFunc.accept(passenger,
                this.getX() + (double) distance * forwardX + (double) (cos * xOffset),
                this.getY() + (double) yOffset,
                this.getZ() + (double) distance * forwardZ + (double) (sin * xOffset));
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        double theta = (double) this.yBodyRot * (Math.PI / 180D);
        theta += 1.5707963267948966D;
        double vecX = Math.cos(theta);
        double vecZ = Math.sin(theta);
        return new Vec3(this.getX() + 3.5D * vecX, this.getY(), this.getZ() + 3.5D * vecZ);
    }

    public void setSleep(boolean sleep) {
        this.setAttackState(sleep ? 1 : 0);
    }

    public boolean isSleep() {
        return this.getAttackState() == 1;
    }

    public void launch(LivingEntity entity, boolean huge, float launchMultiplier, float yPower) {
        double deltaX = entity.getX() - this.getX();
        double deltaZ = entity.getZ() - this.getZ();
        double distanceSquared = Math.max(deltaX * deltaX + deltaZ * deltaZ, 0.001);
        float multiplier = huge ? launchMultiplier : 0.5F;
        entity.push(deltaX / distanceSquared * (double) multiplier, huge ? (double) yPower : 0.2, deltaZ / distanceSquared * (double) multiplier);
    }

    public void calculatedDashTowardsLocation(float multiplier, float locX, float locZ) {
        if (this.getTarget() != null) {
            this.setDeltaMovement(((double) locX - this.getX()) * (double) multiplier, 0.0D, ((double) locZ - this.getZ()) * (double) multiplier);
        }
    }

    public void backStep(float backstepStrength, float yStrength) {
        float yaw = (float) Math.toRadians(this.getYRot() + 90.0F);
        Vec3 dodgePos = this.getDeltaMovement().add((double) backstepStrength * Math.cos(yaw), (double) yStrength, (double) backstepStrength * Math.sin(yaw));
        this.setDeltaMovement(dodgePos.x, dodgePos.y, dodgePos.z);
    }

    private void runAway() {
        if (!this.level().isClientSide) {
            if (this.onGround()) {
                Vec3 randomShake = new Vec3(this.getRandom().nextFloat() - 0.5F, 0.0D, this.getRandom().nextFloat() - 0.5F).scale(0.1F);
                this.setDeltaMovement(this.getDeltaMovement().multiply(2.0D, 1.0D, 2.0D).add(randomShake));
            }
            if (this.getNavigation().isDone()) {
                Vec3 vec = net.minecraft.world.entity.ai.util.LandRandomPos.getPosAway(this, 15, 7, this.position());
                if (vec != null) {
                    this.getNavigation().moveTo(vec.x, vec.y, vec.z, 2.0D);
                }
            }
        }
    }

    public void savePreSwapPositions(boolean statement) {
        if (statement && this.getTarget() != null) {
            this.lastTargetX = this.getTarget().getX();
            this.lastTargetY = this.getTarget().getY();
            this.lastTargetZ = this.getTarget().getZ();
            this.lastX = this.getX();
            this.lastY = this.getY();
            this.lastZ = this.getZ();
        }
    }

    public void swapPositions(boolean statement) {
        if (statement) {
            this.teleport(this.lastTargetX, this.lastTargetY, this.lastTargetZ);
            this.target().teleportTo(this.lastX, this.lastY, this.lastZ);
        }
    }

    @Override
    public boolean isPushable() {
        return !this.isDuringTeleportation(!this.level().isClientSide);
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return this.getAttackState() != 19 && super.causeFallDamage(fallDistance, multiplier, source);
    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        if (super.isAlliedTo(entity)) {
            return true;
        }
        if (entity == null) {
            return false;
        }
        LivingEntity owner = this.getTrueOwner();
        if (owner != null) {
            if (entity == owner) {
                return true;
            }
            if (entity instanceof net.minecraft.world.entity.OwnableEntity ownable && ownable.getOwner() == owner) {
                return true;
            }
            return owner.isAlliedTo(entity);
        }
        return false;
    }

    public void setPartXRot(float partXRot) {
        this.partXRot = partXRot;
    }

    public float getPartXRot() {
        return this.partXRot;
    }

    public void setPartYRot(float partYRot) {
        this.partYRot = partYRot;
    }

    public float getPartYRot() {
        return this.partYRot;
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        return null;
    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
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
    public ItemEntity spawnAtLocation(ItemStack stack) {
        ItemEntity itemEntity = this.spawnAtLocation(stack, 0.0F);
        if (itemEntity != null) {
            itemEntity.setGlowingTag(true);
            itemEntity.setExtendedLifetime();
        }
        return itemEntity;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        this.setConfigurableAttributes();
        this.setPersistenceRequired();
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType reason, @Nullable SpawnGroupData spawnData,
                                        @Nullable CompoundTag dataTag) {
        if (reason == MobSpawnType.MOB_SUMMONED && this.getTrueOwner() instanceof Player player) {
            if (countServants(player) >= MobsConfig.TheObliteratorServantLimit.get()) {
                return null;
            }
        }
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
        this.setAttackState(1);
        return data;
    }

    private int countServants(Player player) {
        int count = 0;
        if (player.level() instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof TheObliteratorServant servant && servant.getTrueOwner() == player) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
        this.setAttackState(60);
        this.deathTime = 0;
        this.stopAllAnimationStates();
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 102) {
            this.remove(Entity.RemovalReason.KILLED);
            this.gameEvent(net.minecraft.world.level.gameevent.GameEvent.ENTITY_DIE);
        }
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return false;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSounds.THE_WARPED_ONE_HURT.get();
    }

    public void stopAllAnimationStates() {
        this.idleAnimationState.stop();
        this.spinSmashLeftAnimationState.stop();
        this.spinSmashRightAnimationState.stop();
        this.shootOnceAnimationState.stop();
        this.shootDoubleAnimationState.stop();
        this.doubleLeftHookComboAnimationState.stop();
        this.roarTeleportAnimationState.stop();
        this.landAnimationState.stop();
        this.landAgainAnimationState.stop();
        this.fallAnimationState.stop();
        this.ambushAnimationState.stop();
        this.ambushSwapAnimationState.stop();
        this.slashComboAnimationState.stop();
        this.teleportUppercutAnimationState.stop();
        this.teleportGrabPreAnimationState.stop();
        this.teleportGrabSuccessAnimationState.stop();
        this.teleportGrabFallAnimationState.stop();
        this.teleportGrabLandAnimationState.stop();
        this.teleportGrabFailRiseUpAnimationState.stop();
        this.jumpTeleportAnimationState.stop();
        this.teleportSlashComboAnimationState.stop();
        this.cloneBurstGrabAnimationState.stop();
        this.kickSmashGrabAnimationState.stop();
        this.rightSpinTeleportSmashAnimationState.stop();
        this.backstepTeleportSlashP2AnimationState.stop();
        this.backstepStompLeftAnimationState.stop();
        this.backstepStompRightAnimationState.stop();
        this.teleportGrabFailCrossSlashAnimationState.stop();
        this.teleportSlashComboRightAnimationState.stop();
        this.slashComboRightAnimationState.stop();
        this.trackingBallChargeAnimationState.stop();
        this.stompAnimationState.stop();
        this.stunStompAnimationState.stop();
        this.stompKickAnimationState.stop();
        this.spawnAnimationState.stop();
        this.stompAfterkickLeftAnimationState.stop();
        this.kickSmashAnimationState.stop();
        this.grabAfterKickSmashAnimationState.stop();
        this.kickSmashByeByeAnimationState.stop();
        this.p2AnimationState.stop();
        this.teleportGrabAfterKickSmashAnimationState.stop();
        this.crushGrabSuccessAnimationState.stop();
        this.crushGrabFailAnimationState.stop();
        this.singleShotLaserAnimationState.stop();
        this.singleShotLaserAfterStompAnimationState.stop();
        this.singleShotLaserTeleportEndAnimationState.stop();
        this.singleShotLaserEndAnimationState.stop();
        this.teleportAwaySingleShotLaserAnimationState.stop();
        this.singleShotLaserQuadEndAnimationState.stop();
        this.closeGrabPreAnimationState.stop();
        this.backstepStompRightSpinAnimationState.stop();
        this.p3AnimationState.stop();
        this.LeftgrabAfterKickSmashAnimationState.stop();
        this.LeftcrushGrabSuccessAnimationState.stop();
        this.LeftcrushGrabFailAnimationState.stop();
        this.ArmBlockAnimationState.stop();
        this.PortalUltimateAnimationState.stop();
        this.BombShootOnceEndAnimationState.stop();
        this.BombShootDoubleEndAnimationState.stop();
        this.DeathAnimationState.stop();
        this.TpGrabFail0AnimationState.stop();
        this.AmbushLongerAnimationState.stop();
    }


    public void UpdateWithAttack() {
        double spawnZ;
        double vecZ;
        double vecX;
        int uniformDuration;
        float f4;
        float f3;
        float g;
        float offset;
        double vecZ2;
        double vecX2;
        float f1;
        float offset2;
        float vec;
        double vecZ3;
        double vecX3;
        double theta;
        float f;
        SoundEvent swingSound;
        float vec2;
        double vecZ4;
        double vecX4;
        float g2;
        float f2;
        int k;
        float standingOnY;
        float offset3;
        float vec3;
        double vecZ5;
        double vecX5;
        double theta2;
        float f12;
        float f5;
        float doubleSlashFirstArc = 200.0f;
        float teleportDoubleSlashFirstArc = 200.0f;
        float doubleSlashSecondArc = 200.0f;
        float doubleSlashRange = 3.75f;
        float doubleSlashDamage = 18.0f;
        float spinSmashArc = 290.0f;
        float spinSmashPushDamage = 19.0f;
        float spinSmashSlamDamage = 21.0f;
        float kickRange = 3.0f;
        float flameStompGroundDamage = 5.0f;
        float removeMeleeDamage = 2.0f;
        float stunAttackDamage = 21.0f;
        float plasmaBallDamage = 8.0f;
        float flamewaveOffset = 1.75f;
        float singleShotLaserDamage = 8.5f;
        SoundEvent spinSmashImpactSound = SoundEvents.EMPTY;
        float KnockAttackRange = 4.5f;
        if (this.getAttackState() == 4) {
            if (this.attackTicks == 38) {
                f5 = Mth.cos((float) (this.yBodyRot * ((float) Math.PI / 180)));
                f12 = Mth.sin((float) (this.yBodyRot * ((float) Math.PI / 180)));
                theta2 = (double) this.yBodyRot * (Math.PI / 180);
                vecX5 = Math.cos(theta2 += 1.5707963267948966);
                vecZ5 = Math.sin(theta2);
                vec3 = 3.0f;
                offset3 = 0.0f;
                standingOnY = Mth.floor((double) this.getY());
                for (k = 0; k < 3; ++k) {
                    f2 = (float) k * (float) Math.PI * 2.0f / 3.0f + 2.5132742f;
                    this.spawnFlames(this.getX() + (double) vec3 * vecX5 + (double) (f5 * offset3) + (double) Mth.cos((float) f2) * 1.25, this.getZ() + (double) vec3 * vecZ5 + (double) (f12 * offset3) + (double) Mth.sin((float) f2) * 1.25, standingOnY, this.getY() + 1.0, f2, 0, flameStompGroundDamage, false, ModParticles.GROUND_ANNIHILATION_NUKE.get());
                }
                ParticleUtils.controlledSmashParticles(this, 3.0f, 0.0f, 0.0f, 3.5f, 1.5f);
            }
            if (this.attackTicks == 22) {
                this.createSweep(3.0f, 1.5f, -5.0f, true, 1.0f);
                this.playSound(ModSounds.HEAVY_SWING.get(), 1.0f, 1.0f);
                this.calculatedDash(0.25f);
            }
            if (this.attackTicks == 24) {
                this.SideAreaAttack(KnockAttackRange, 3.0f, 180.0f, 0.0f, spinSmashPushDamage - removeMeleeDamage, 3.0f, 100, false, false, ModSounds.POSESSED_PALADIN_ATTACK3.get(), 0.5f);
            }
            if (this.attackTicks == 35) {
                this.calculatedDash(0.15f);
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0f, 1.0f);
            }
            if (this.attackTicks == 38) {
                f5 = Mth.cos((float) (this.yBodyRot * ((float) Math.PI / 180)));
                f12 = Mth.sin((float) (this.yBodyRot * ((float) Math.PI / 180)));
                theta2 = (double) this.yBodyRot * (Math.PI / 180);
                vecX5 = Math.cos(theta2 += 1.5707963267948966);
                vecZ5 = Math.sin(theta2);
                vec3 = 4.5f;
                offset3 = 0.0f;
                if (this.level() instanceof ServerLevel serverLevel) {
                    g2 = (float) Math.toRadians(-this.getYRot() + 180.0f);
                    serverLevel.sendParticles(new Circle.RingData(g2, 0.0f, 30, 0.0f, 1.0f, 0.0f, 1.0f, 60.0f, true, Circle.EnumRingBehavior.GROW_THEN_SHRINK), this.getX() + (double) vec3 * vecX5 + (double) (f5 * offset3), this.getY(), this.getZ() + (double) vec3 * vecZ5 + (double) (f12 * offset3), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
                ParticleUtils.controlledSmashParticles(this, 3.0f, 0.0f, 0.0f, 3.5f, 1.5f);
                this.playSound(ModSounds.ENERGY_EXPLOSION.get(), 1.0f, 1.0f);
                this.SideAreaAttack(5.0f, 4.0f, spinSmashArc, -90.0f, spinSmashSlamDamage - removeMeleeDamage, 3.0f, 100, false, false, spinSmashImpactSound, 0.5f);
                Vec3 entityPosition = this.position();
                CameraShakeEntity.cameraShake(this.level(), entityPosition, 20.0f, 0.15f, 0, 20);
            }
        }
        if (this.getAttackState() == 26) {
            if (this.attackTicks == 38) {
                f5 = Mth.cos((float) (this.yBodyRot * ((float) Math.PI / 180)));
                f12 = Mth.sin((float) (this.yBodyRot * ((float) Math.PI / 180)));
                theta2 = (double) this.yBodyRot * (Math.PI / 180);
                vecX5 = Math.cos(theta2 += 1.5707963267948966);
                vecZ5 = Math.sin(theta2);
                vec3 = 3.0f;
                offset3 = 0.0f;
                standingOnY = Mth.floor((double) this.getY());
                for (k = 0; k < 3; ++k) {
                    f2 = (float) k * (float) Math.PI * 2.0f / 3.0f + 2.5132742f;
                    this.spawnFlames(this.getX() + (double) vec3 * vecX5 + (double) (f5 * offset3) + (double) Mth.cos((float) f2) * 1.25, this.getZ() + (double) vec3 * vecZ5 + (double) (f12 * offset3) + (double) Mth.sin((float) f2) * 1.25, standingOnY, this.getY() + 1.0, f2, 0, flameStompGroundDamage, false, ModParticles.GROUND_ANNIHILATION_NUKE.get());
                }
            }
            if (this.attackTicks == 22) {
                this.createSweep(3.0f, 1.5f, -5.0f, true, 1.0f);
                this.playSound(ModSounds.HEAVY_SWING.get(), 1.0f, 1.0f);
                this.calculatedDash(0.25f);
            }
            if (this.attackTicks == 24) {
                this.AreaAttack(KnockAttackRange, 3.0f, 180.0f, spinSmashPushDamage - removeMeleeDamage, 3.0f, 100, false, false);
            }
            if (this.attackTicks == 33 && this.targetIsNotNull()) {
                f5 = Mth.cos((float) (this.target().yBodyRot * ((float) Math.PI / 180)));
                f12 = Mth.sin((float) (this.target().yBodyRot * ((float) Math.PI / 180)));
                theta2 = (double) this.target().yBodyRot * (Math.PI / 180);
                vecX5 = Math.cos(theta2 += 1.5707963267948966);
                vecZ5 = Math.sin(theta2);
                vec3 = -5.0f;
                offset3 = 0.0f;
                this.teleport(this.target().getX() + (double) vec3 * vecX5 + (double) (f5 * offset3), this.target().getY(), this.target().getZ() + (double) vec3 * vecZ5 + (double) (f12 * offset3));
            }
            if (this.attackTicks == 43) {
                this.calculatedDash(0.15f);
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0f, 1.0f);
            }
            if (this.attackTicks == 46) {
                f5 = Mth.cos((float) (this.yBodyRot * ((float) Math.PI / 180)));
                f12 = Mth.sin((float) (this.yBodyRot * ((float) Math.PI / 180)));
                theta2 = (double) this.yBodyRot * (Math.PI / 180);
                vecX5 = Math.cos(theta2 += 1.5707963267948966);
                vecZ5 = Math.sin(theta2);
                vec3 = 4.5f;
                offset3 = 0.0f;
                if (this.level() instanceof ServerLevel serverLevel) {
                    g2 = (float) Math.toRadians(-this.getYRot() + 180.0f);
                    serverLevel.sendParticles(new Circle.RingData(g2, 0.0f, 30, 0.0f, 1.0f, 0.0f, 1.0f, 60.0f, true, Circle.EnumRingBehavior.GROW_THEN_SHRINK), this.getX() + (double) vec3 * vecX5 + (double) (f5 * offset3), this.getY(), this.getZ() + (double) vec3 * vecZ5 + (double) (f12 * offset3), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
                this.playSound(ModSounds.ENERGY_EXPLOSION.get(), 1.0f, 1.0f);
                this.SideAreaAttack(5.0f, 4.0f, spinSmashArc, -90.0f, spinSmashSlamDamage - removeMeleeDamage, 3.0f, 100, false, false, spinSmashImpactSound, 0.5f);
                Vec3 entityPosition = this.position();
                CameraShakeEntity.cameraShake(this.level(), entityPosition, 20.0f, 0.15f, 0, 20);
            }
        }
        if (this.getAttackState() == 5) {
            if (this.attackTicks == 38) {
                f5 = Mth.cos((float) (this.yBodyRot * ((float) Math.PI / 180)));
                f12 = Mth.sin((float) (this.yBodyRot * ((float) Math.PI / 180)));
                theta2 = (double) this.yBodyRot * (Math.PI / 180);
                vecX5 = Math.cos(theta2 += 1.5707963267948966);
                vecZ5 = Math.sin(theta2);
                vec3 = 3.0f;
                offset3 = 0.0f;
                float standingOnY2 = Mth.floor((double) this.getY());
                for (k = 0; k < 3; ++k) {
                    f2 = (float) k * (float) Math.PI * 2.0f / 3.0f + 2.5132742f;
                    this.spawnFlames(this.getX() + (double) vec3 * vecX5 + (double) (f5 * offset3) + (double) Mth.cos((float) f2) * 1.25, this.getZ() + (double) vec3 * vecZ5 + (double) (f12 * offset3) + (double) Mth.sin((float) f2) * 1.25, standingOnY2, this.getY() + 1.0, f2, 0, flameStompGroundDamage, false, ModParticles.GROUND_ANNIHILATION_NUKE.get());
                }
                ParticleUtils.controlledSmashParticles(this, 3.0f, 0.0f, 0.0f, 3.5f, 1.5f);
            }
            if (this.attackTicks == 22) {
                this.createSweep(3.0f, -1.5f, -5.0f, false, 1.0f);
                this.playSound(ModSounds.HEAVY_SWING.get(), 1.0f, 1.0f);
                this.calculatedDash(0.25f);
            }
            if (this.attackTicks == 24) {
                this.SideAreaAttack(KnockAttackRange, 3.0f, 180.0f, 0.0f, spinSmashPushDamage - removeMeleeDamage, 3.0f, 100, false, false, ModSounds.POSESSED_PALADIN_ATTACK3.get(), 0.5f);
            }
            if (this.attackTicks == 35) {
                this.calculatedDash(0.15f);
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0f, 1.0f);
            }
            if (this.attackTicks == 38) {
                f5 = Mth.cos((float) (this.yBodyRot * ((float) Math.PI / 180)));
                f12 = Mth.sin((float) (this.yBodyRot * ((float) Math.PI / 180)));
                theta2 = (double) this.yBodyRot * (Math.PI / 180);
                vecX5 = Math.cos(theta2 += 1.5707963267948966);
                vecZ5 = Math.sin(theta2);
                vec3 = 4.5f;
                offset3 = 0.0f;
                if (this.level() instanceof ServerLevel serverLevel) {
                    float g3 = (float) Math.toRadians(-this.getYRot() + 180.0f);
                    serverLevel.sendParticles(new Circle.RingData(g3, 0.0f, 30, 0.0f, 1.0f, 0.0f, 1.0f, 60.0f, true, Circle.EnumRingBehavior.GROW_THEN_SHRINK), this.getX() + (double) vec3 * vecX5 + (double) (f5 * offset3), this.getY(), this.getZ() + (double) vec3 * vecZ5 + (double) (f12 * offset3), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
                this.playSound(ModSounds.ENERGY_EXPLOSION.get(), 1.0f, 1.0f);
                this.SideAreaAttack(5.0f, 4.0f, spinSmashArc, 90.0f, spinSmashSlamDamage - removeMeleeDamage, 3.0f, 100, false, false, spinSmashImpactSound, 0.5f);
                Vec3 entityPosition = this.position();
                CameraShakeEntity.cameraShake(this.level(), entityPosition, 20.0f, 0.15f, 0, 20);
            }
        }
        float bombVel = 1.0f;
        if (this.getAttackState() == 6) {
            if (this.attackTicks > 30) {
                List<LivingEntity> entities = this.getEntitiesNearby(LivingEntity.class, 5.5, 5.5, 5.5, 5.0);
                for (LivingEntity livingEntity : entities) {
                    if (livingEntity != this.target() || !this.targetIsNotNull()) continue;
                    this.canShootTwice = true;
                }
            }
            if (this.attackTicks == 10) {
                this.level().addParticle(new Circle.RingData(0.0f, 1.5707964f, 20, 0.0f, 1.0f, 0.0f, 1.0f, 80.0f, false, Circle.EnumRingBehavior.SHRINK), this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
            }
            if (this.attackTicks == 20) {
                this.level().addParticle(new Circle.RingData(0.0f, 1.5707964f, 20, 0.0f, 1.0f, 0.0f, 1.0f, 80.0f, false, Circle.EnumRingBehavior.SHRINK), this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
            }
            if (this.attackTicks == 30) {
                this.level().addParticle(new Circle.RingData(0.0f, 1.5707964f, 20, 0.0f, 1.0f, 0.0f, 1.0f, 80.0f, false, Circle.EnumRingBehavior.SHRINK), this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
            }
            if (this.attackTicks == 17) {
                this.playSound(ModSounds.DIMENSIONAL_SHOOT_CHARGE.get(), 1.0f, 1.0f);
            }
            if (this.attackTicks == 37) {
                this.backStep(-1.0f, 0.0f);
                float f6 = Mth.cos((float)(this.yBodyRot * ((float)Math.PI / 180)));
                float f13 = Mth.sin((float)(this.yBodyRot * ((float)Math.PI / 180)));
                double theta3 = (double)this.yBodyRot * (Math.PI / 180);
                vecX4 = Math.cos(theta3 += 1.5707963267948966);
                vecZ4 = Math.sin(theta3);
                vec2 = 0.0f;
                float offset4 = 0.0f;
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ModParticles.ANNIHILATION_EXPLOSION.get(), this.getX() + (double)vec2 * vecX4 + (double)(f6 * offset4), this.getY() + 3.0, this.getZ() + (double)vec2 * vecZ4 + (double)(f13 * offset4), 1, 0.0, 0.0, 0.0, 0.0);
                }
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ModParticles.ANNIHILATION_EXPLOSION.get(), this.getX() + (double)vec2 * vecX4 + (double)(f6 * offset4), this.getY() + 3.0, this.getZ() + (double)vec2 * vecZ4 + (double)(f13 * offset4), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
                this.shootAnnihilationBomb(bombVel, (float)(this.getX() + (double)vec2 * vecX4 + (double)(f6 * offset4)), (float)(this.getY() + 1.5), (float)(this.getZ() + (double)vec2 * vecZ4 + (double)(f13 * offset4)));
                this.playSound(ModSounds.THE_WARPED_ONE_SHOOT.get(), 3.0f, 1.0f);
            }
        }
        if (this.getAttackState() == 55) {
            if (this.attackTicks == 11 && this.targetIsNotNull()) {
                float f7 = Mth.cos((float)(this.target().yBodyRot * ((float)Math.PI / 180)));
                float f14 = Mth.sin((float)(this.target().yBodyRot * ((float)Math.PI / 180)));
                double theta4 = (double)this.target().yBodyRot * (Math.PI / 180);
                vecX4 = Math.cos(theta4 += 1.5707963267948966);
                vecZ4 = Math.sin(theta4);
                vec2 = -6.0f;
                float offset5 = 0.0f;
                float vec1 = 2.0f;
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ModParticles.ANNIHILATION_EXPLOSION.get(), this.getX() + (double)vec2 * vecX4 + (double)(f7 * offset5), this.getY() + 3.0, this.getZ() + (double)vec2 * vecZ4 + (double)(f14 * offset5), 1, 0.0, 0.0, 0.0, 0.0);
                }
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ModParticles.ANNIHILATION_EXPLOSION.get(), this.getX() + (double)vec1 * vecX4 + (double)(f7 * offset5), this.getY() + 3.0, this.getZ() + (double)vec1 * vecZ4 + (double)(f14 * offset5), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
                if (!this.level().isClientSide) {
                    this.teleport((float)(this.target().getX() + (double)vec2 * vecX4 + (double)(f7 * offset5)), this.target().getY(), (float)(this.target().getZ() + (double)vec2 * vecZ4 + (double)(f14 * offset5)));
                }
            }
            if (this.attackTicks == 33) {
                this.backStep(-1.0f, 0.0f);
                float f8 = Mth.cos((float)(this.yBodyRot * ((float)Math.PI / 180)));
                float f15 = Mth.sin((float)(this.yBodyRot * ((float)Math.PI / 180)));
                double theta5 = (double)this.yBodyRot * (Math.PI / 180);
                vecX4 = Math.cos(theta5 += 1.5707963267948966);
                vecZ4 = Math.sin(theta5);
                vec2 = 0.0f;
                float offset6 = 0.0f;
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ModParticles.ANNIHILATION_EXPLOSION.get(), this.getX() + (double)vec2 * vecX4 + (double)(f8 * offset6), this.getY() + 3.0, this.getZ() + (double)vec2 * vecZ4 + (double)(f15 * offset6), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
                this.shootAnnihilationBomb(bombVel, (float)(this.getX() + (double)vec2 * vecX4 + (double)(f8 * offset6)), (float)(this.getY() + 1.5), (float)(this.getZ() + (double)vec2 * vecZ4 + (double)(f15 * offset6)));
                this.playSound(ModSounds.THE_WARPED_ONE_SHOOT.get(), 3.0f, 1.0f);
            }
        }
        if (this.getAttackState() == 7) {
            if (this.attackTicks == 10) {
                this.level().addParticle(new Circle.RingData(0.0f, 1.5707964f, 20, 0.0f, 1.0f, 0.0f, 1.0f, 80.0f, false, Circle.EnumRingBehavior.SHRINK), this.getX(), this.getY() + 0.5, this.getZ(), 0.0, 0.0, 0.0);
            }
            if (this.attackTicks == 20) {
                this.level().addParticle(new Circle.RingData(0.0f, 1.5707964f, 20, 0.0f, 1.0f, 0.0f, 1.0f, 80.0f, false, Circle.EnumRingBehavior.SHRINK), this.getX(), this.getY() + 0.5, this.getZ(), 0.0, 0.0, 0.0);
            }
            if (this.attackTicks == 30) {
                this.level().addParticle(new Circle.RingData(0.0f, 1.5707964f, 20, 0.0f, 1.0f, 0.0f, 1.0f, 80.0f, false, Circle.EnumRingBehavior.SHRINK), this.getX(), this.getY() + 0.5, this.getZ(), 0.0, 0.0, 0.0);
            }
            if (this.attackTicks == 17) {
                this.playSound(ModSounds.DIMENSIONAL_SHOOT_CHARGE.get(), 1.0f, 1.0f);
            }
            if (this.attackTicks == 37) {
                this.backStep(-1.0f, 0.0f);
                float f9 = Mth.cos((float)(this.yBodyRot * ((float)Math.PI / 180)));
                float f16 = Mth.sin((float)(this.yBodyRot * ((float)Math.PI / 180)));
                double theta6 = (double)this.yBodyRot * (Math.PI / 180);
                vecX4 = Math.cos(theta6 += 1.5707963267948966);
                vecZ4 = Math.sin(theta6);
                vec2 = 0.0f;
                float offset7 = 0.0f;
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ModParticles.ANNIHILATION_EXPLOSION.get(), this.getX() + (double)vec2 * vecX4 + (double)(f9 * offset7), this.getY() + 3.0, this.getZ() + (double)vec2 * vecZ4 + (double)(f16 * offset7), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
                this.shootAnnihilationBomb(bombVel, (float)(this.getX() + (double)vec2 * vecX4 + (double)(f9 * offset7)), (float)(this.getY() + 1.5), (float)(this.getZ() + (double)vec2 * vecZ4 + (double)(f16 * offset7)));
                this.playSound(ModSounds.THE_WARPED_ONE_SHOOT.get(), 3.0f, 1.0f);
            }
            if (this.attackTicks >= 51 && this.attackTicks <= 54) {
                this.setInvisible(true);
                if (this.targetIsNotNull()) {
                    float f10 = Mth.cos((float)(this.target().yBodyRot * ((float)Math.PI / 180)));
                    float f17 = Mth.sin((float)(this.target().yBodyRot * ((float)Math.PI / 180)));
                    double theta7 = (double)this.target().yBodyRot * (Math.PI / 180);
                    vecX4 = Math.cos(theta7 += 1.5707963267948966);
                    vecZ4 = Math.sin(theta7);
                    vec2 = -6.0f;
                    float offset8 = 0.0f;
                    float vec1 = 2.0f;
                    if (this.level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ModParticles.ANNIHILATION_EXPLOSION.get(), this.getX() + (double)vec1 * vecX4 + (double)(f10 * offset8), this.getY() + 3.0, this.getZ() + (double)vec1 * vecZ4 + (double)(f17 * offset8), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                    }
                    if (!this.level().isClientSide) {
                        this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0f, 1.0f);
                        this.setPos((float)(this.target().getX() + (double)vec2 * vecX4 + (double)(f10 * offset8)), this.target().getY(), (float)(this.target().getZ() + (double)vec2 * vecZ4 + (double)(f17 * offset8)));
                    }
                }
            }
            if (this.attackTicks == 56) {
                this.playSound(ModSounds.DIMENSIONAL_SHOOT_CHARGE.get(), 1.0f, 1.0f);
            }
            if (this.attackTicks == 68) {
                this.backStep(-1.0f, 0.0f);
                float f11 = Mth.cos((float)(this.yBodyRot * ((float)Math.PI / 180)));
                float f18 = Mth.sin((float)(this.yBodyRot * ((float)Math.PI / 180)));
                double theta8 = (double)this.yBodyRot * (Math.PI / 180);
                vecX4 = Math.cos(theta8 += 1.5707963267948966);
                vecZ4 = Math.sin(theta8);
                vec2 = 0.0f;
                float offset9 = 0.0f;
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ModParticles.ANNIHILATION_EXPLOSION.get(), this.getX() + (double)vec2 * vecX4 + (double)(f11 * offset9), this.getY() + 3.0, this.getZ() + (double)vec2 * vecZ4 + (double)(f18 * offset9), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
                this.shootAnnihilationBomb(1.0f, (float)(this.getX() + (double)vec2 * vecX4 + (double)(f11 * offset9)), (float)(this.getY() + 1.5), (float)(this.getZ() + (double)vec2 * vecZ4 + (double)(f18 * offset9)));
                this.playSound(ModSounds.THE_WARPED_ONE_SHOOT.get(), 3.0f, 1.0f);
            }
        }
        if (this.getAttackState() == 47) {
            if (this.attackTicks == 20) {
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0f, 1.0f);
                this.calculatedDash(0.25f);
            }
            if (this.attackTicks == 23) {
                this.SideAreaAttack(4.0f, 3.0f, 180.0f, 0.0f, 0.0f, 20.0f - removeMeleeDamage, 4.0f, 120, false, false, ModSounds.THE_OBLITERATOR_STUN.get(), 1.0f);
            }
            if (this.getAttackTicks() == 35) {
                float f13 = Mth.cos((float)(this.yBodyRot * ((float)Math.PI / 180)));
                float f19 = Mth.sin((float)(this.yBodyRot * ((float)Math.PI / 180)));
                double theta9 = (double)this.yBodyRot * (Math.PI / 180);
                vecX4 = Math.cos(theta9 += 1.5707963267948966);
                vecZ4 = Math.sin(theta9);
                vec2 = -4.0f;
                float offset10 = 0.0f;
                this.teleport(this.getX() + (double)vec2 * vecX4 + (double)(f13 * offset10), this.getY(), this.getZ() + (double)vec2 * vecZ4 + (double)(f19 * offset10));
            }
            if (this.attackTicks == 47) {
                this.savePreSwapPositions(this.targetIsNotNull());
            }
            if (this.attackTicks == 59) {
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0f, 1.0f);
                if (this.targetIsNotNull()) {
                    this.setDeltaMovement((this.lastTargetX - this.getX()) * (double)0.35f, 0.0, (this.lastTargetZ - this.getZ()) * (double)0.35f);
                }
            }
            if (this.attackTicks == 65) {
                this.succedGrabbing = false;
                this.PreGrab(-0.25f, 2.0f, 3.0f, 100, 18.0f - removeMeleeDamage);
            }
            if (this.attackTicks == 39) {
                this.playSound(SoundEvents.ENDERMAN_TELEPORT, 2.0f, 0.5f);
            }
            if (this.attackTicks == 38) {
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ModParticles.TELEPORT_EFFECT.get(), this.getX(), this.getY() + 3.0, this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
            }
        }
        if (this.getAttackState() == 8) {
            if (this.attackTicks == 14) {
                this.calculatedDash(0.15F);
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.0F);
            }
            if (this.attackTicks == 31) {
                this.calculatedDash(0.25F);
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.0F);
            }
        }
        if (this.getAttackState() == 9) {
            if (this.attackTicks == 20) {
                this.playSound(ModSounds.THE_WARPED_ONE_ROAR.get(), 2.0F, 1.0F);
                Vec3 entityPosition = this.position();
                CameraShakeEntity.cameraShake(this.level(), entityPosition, 20.0F, 0.15F, 30, 0);
            }
            if (this.attackTicks > 20 && this.attackTicks < 30) {
                List<LivingEntity> entities = this.getEntityLivingBaseNearby(10.0, 3.0, 10.0, 10.0);
                for (LivingEntity entity : entities) {
                    if (entity == this) continue;
                    double angle = (this.getAngleBetweenEntities(this, entity) + 90.0) * Math.PI / 180.0;
                    double distance = this.distanceTo(entity) - 2.0F;
                    entity.setDeltaMovement(entity.getDeltaMovement().add(Math.min(1.0 / (distance * distance), 1.0) * -1.0 * Math.cos(angle), 0.0D, Math.min(1.0 / (distance * distance), 1.0) * -1.0 * Math.sin(angle)));
                }
            }
            if (this.attackTicks >= 55 && this.attackTicks <= 70) {
                this.runAway();
            }
            if (this.attackTicks == 75) {
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ModParticles.TELEPORT_EFFECT.get(), this.getX(), this.getY() + 3.0D, this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
            }
            if (this.attackTicks == 76) {
                this.playSound(SoundEvents.ENDERMAN_TELEPORT, 2.0F, 0.5F);
                this.setNoGravity(true);
                if (!this.level().isClientSide) {
                    if (this.targetIsNotNull()) {
                        this.setPos(this.target().getX(), this.target().getY() + 10.0D, this.target().getZ());
                    } else {
                        this.setPos(this.getX(), this.getY() + 10.0D, this.getZ());
                    }
                }
            }
        }
        if (this.getAttackState() == 10 && this.onGround()) {
            if (this.getNextRoarAttackType() == 2 && !this.hasStartedSecondTeleportSmash && this.getAttackState() != 8) {
                this.setAttackState(12);
            } else {
                this.setAttackState(11);
            }
        }
        if (this.getAttackState() == 11) {
            if (this.attackTicks == 3) {
                Random random = new Random();
                int los = random.nextInt(4) + 3;
                this.shootPlasmaBall(6, 45.0F, true, los, plasmaBallDamage);
            }
            if (this.attackTicks == 9) {
                Random random = new Random();
                int los = random.nextInt(4) + 3;
                this.shootPlasmaBall(6, 90.0F, false, los, plasmaBallDamage);
            }
            if (this.attackTicks == 3) {
                float f15 = Mth.cos((float)(this.yBodyRot * ((float)Math.PI / 180)));
                float f111 = Mth.sin((float)(this.yBodyRot * ((float)Math.PI / 180)));
                double theta11 = (double)this.yBodyRot * (Math.PI / 180);
                vecX4 = Math.cos(theta11 += 1.5707963267948966);
                vecZ4 = Math.sin(theta11);
                vec2 = 2.5F;
                float offset12 = 0.0F;
                if (this.level() instanceof ServerLevel serverLevel) {
                    float g4 = (float)Math.toRadians(-this.getYRot() + 180.0F);
                    serverLevel.sendParticles(new Circle.RingData(g4, 0.0F, 30, 0.0F, 1.0F, 0.0F, 1.0F, 60.0F, true, Circle.EnumRingBehavior.GROW_THEN_SHRINK), this.getX() + (double)vec2 * vecX4 + (double)(f15 * offset12), this.getY(), this.getZ() + (double)vec2 * vecZ4 + (double)(f111 * offset12), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
                this.playSound(ModSounds.THE_OBLITERATOR_STUN.get(), 1.0F, 1.0F);
                this.AreaAttack(4.0F, 3.0F, 180.0F, 16.0F, 5.0F, 100, false, true);
                ParticleUtils.controlledSmashParticles(this, 0.0F, -1.0F, 0.0F, 7.5F, 3.0F);
                Vec3 entityPosition = this.position();
                CameraShakeEntity.cameraShake(this.level(), entityPosition, 20.0F, 0.15F, 0, 20);
            }
        }
        if (this.getAttackState() == 12) {
            if (this.attackTicks == 3) {
                Random random = new Random();
                int los = random.nextInt(4) + 3;
                this.shootPlasmaBall(6, 90.0F, true, los, plasmaBallDamage);
            }
            if (this.attackTicks == 9) {
                Random random = new Random();
                int los = random.nextInt(4) + 3;
                this.shootPlasmaBall(4, 30.0F, true, los, plasmaBallDamage);
            }
            if (this.attackTicks == 3) {
                float f16 = Mth.cos((float)(this.yBodyRot * ((float)Math.PI / 180)));
                float f112 = Mth.sin((float)(this.yBodyRot * ((float)Math.PI / 180)));
                double theta12 = (double)this.yBodyRot * (Math.PI / 180);
                vecX4 = Math.cos(theta12 += 1.5707963267948966);
                vecZ4 = Math.sin(theta12);
                vec2 = 2.5F;
                float offset13 = 0.0F;
                if (this.level() instanceof ServerLevel serverLevel) {
                    float g5 = (float)Math.toRadians(-this.getYRot() + 180.0F);
                    serverLevel.sendParticles(new Circle.RingData(g5, 0.0F, 30, 0.0F, 1.0F, 0.0F, 1.0F, 60.0F, true, Circle.EnumRingBehavior.GROW_THEN_SHRINK), this.getX() + (double)vec2 * vecX4 + (double)(f16 * offset13), this.getY(), this.getZ() + (double)vec2 * vecZ4 + (double)(f112 * offset13), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
                this.playSound(ModSounds.THE_OBLITERATOR_STUN.get(), 1.0F, 1.0F);
                this.AreaAttack(4.0F, 3.0F, 180.0F, 16.0F, 5.0F, 100, false, true);
                ParticleUtils.controlledSmashParticles(this, 0.0F, 1.0F, 0.0F, 7.5F, 3.0F);
                Vec3 entityPosition = this.position();
                CameraShakeEntity.cameraShake(this.level(), entityPosition, 20.0F, 0.15F, 0, 20);
            }
            if (this.attackTicks == 21) {
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ModParticles.TELEPORT_EFFECT.get(), this.getX(), this.getY() + 3.0D, this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
            }
            if (this.attackTicks == 18) {
                this.playSound(SoundEvents.ENDERMAN_TELEPORT, 2.0F, 0.5F);
                if (!this.level().isClientSide) {
                    if (this.targetIsNotNull()) {
                        this.setPos(this.target().getX(), this.target().getY() + 10.0D, this.target().getZ());
                    } else {
                        this.setPos(this.getX(), this.getY() + 10.0D, this.getZ());
                    }
                }
            }
        }
        if (this.getAttackState() == 13) {
            if (this.attackTicks == 58) {
                this.doTeleportEffects(this.getIsSecondPhase());
            }
            if (this.attackTicks == 65) {
                this.setNoGravity(false);
            }
        }
        if (this.getAttackState() == 62) {
            if (this.attackTicks == 75) {
                this.doTeleportEffects(this.getIsSecondPhase());
            }
            if (this.attackTicks == 65) {
                this.setNoGravity(false);
            }
        }
        if (this.getAttackState() == 16) {
            if (this.attackTicks == 8) {
                this.savePreSwapPositions(this.targetIsNotNull());
            }
            if (this.attackTicks == 20) {
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.0F);
                if (this.targetIsNotNull()) {
                    this.setDeltaMovement((this.lastTargetX - this.getX()) * (double) 0.35F, 0.0D, (this.lastTargetZ - this.getZ()) * (double) 0.35F);
                }
            }
            if (this.attackTicks == 39) {
                this.playSound(SoundEvents.ENDERMAN_TELEPORT, 2.0F, 0.5F);
            }
            if (this.attackTicks >= 36 && this.attackTicks <= 39 && this.targetIsNotNull()) {
                float f17 = Mth.cos(this.target().yBodyRot * ((float) Math.PI / 180));
                float f113 = Mth.sin(this.target().yBodyRot * ((float) Math.PI / 180));
                double theta13 = (double) this.target().yBodyRot * (Math.PI / 180);
                vecX4 = Math.cos(theta13 += 1.5707963267948966);
                vecZ4 = Math.sin(theta13);
                vec2 = 2.0F;
                float offset14 = 0.0F;
                this.teleport(this.target().getX() + (double) vec2 * vecX4 + (double) (f17 * offset14), this.target().getY(), this.target().getZ() + (double) vec2 * vecZ4 + (double) (f113 * offset14));
            }
            if (this.attackTicks == 42) {
                this.playSound(ModSounds.GROUND_IMPACT.get(), 1.0F, 1.0F);
            }
        }
        if (this.getAttackState() == 17) {
            if (this.attackTicks == 8) {
                this.savePreSwapPositions(this.targetIsNotNull());
            }
            if (this.attackTicks == 20) {
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.0F);
                if (this.targetIsNotNull()) {
                    this.setDeltaMovement((this.lastTargetX - this.getX()) * (double) 0.35F, 0.0D, (this.lastTargetZ - this.getZ()) * (double) 0.35F);
                }
            }
            if (this.attackTicks == 26) {
                this.succedGrabbing = false;
                this.PreGrab(-0.25F, 2.0F, 3.0F, 100, 19.0F - removeMeleeDamage);
            }
            if (this.attackTicks == 39) {
                this.playSound(SoundEvents.ENDERMAN_TELEPORT, 2.0F, 0.5F);
            }
            if (this.attackTicks == 38) {
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ModParticles.TELEPORT_EFFECT.get(), this.getX(), this.getY() + 3.0D, this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
            }
        }
        if (this.getAttackState() == 18) {
            if (this.attackTicks == 5 && this.targetIsNotNull()) {
                float f18 = Mth.cos(this.target().yBodyRot * ((float) Math.PI / 180));
                float f114 = Mth.sin(this.target().yBodyRot * ((float) Math.PI / 180));
                double theta14 = (double) this.target().yBodyRot * (Math.PI / 180);
                vecX4 = Math.cos(theta14 += 1.5707963267948966);
                vecZ4 = Math.sin(theta14);
                vec2 = 2.0F;
                float offset15 = 0.0F;
                this.teleportTo(this.target().getX() + (double) vec2 * vecX4 + (double) (f18 * offset15), this.target().getY() + 1.0D, this.target().getZ() + (double) vec2 * vecZ4 + (double) (f114 * offset15));
            }
            if (this.attackTicks == 15) {
                this.Grab(2.0F, 6.0F, 0, 5.0F);
            }
        }
        if (this.getAttackState() == 19 && this.onGround()) {
            this.setAttackState(20);
        }
        if (this.getAttackState() == 20 && this.attackTicks == 3) {
            this.playSound(ModSounds.ENERGY_EXPLOSION.get(), 1.0F, 1.0F);
            Vec3 entityPosition = this.position();
            CameraShakeEntity.cameraShake(this.level(), entityPosition, 20.0F, 0.15F, 0, 20);
            this.spawnCircleParticle(2.0F, 0.0F, 80.0F, true, 7.0F, 0.0F, 1.0F, 0.0F, 1.0F);
            this.AreaAttack(4.0F, 5.0F, 360.0F, 12.0F, 10.0F, 100, false, false);
            double multiplier = 8.0D;
            float size = 4.0F;
            int amountOfFlames = 10;
            for (int k2 = 0; k2 < amountOfFlames; ++k2) {
                float f32 = (float) k2 * (float) Math.PI * size / (float) amountOfFlames + (float) Math.PI * size / 10.0F;
                int standingOnY3 = Mth.floor(this.getY());
                this.spawnFlames(this.getX() + (double) Mth.cos(f32) * multiplier, this.getZ() + (double) Mth.sin(f32) * multiplier, standingOnY3, this.getY() + 1.0D, f32, 2, flameStompGroundDamage, false, ModParticles.GROUND_ANNIHILATION_NUKE.get());
            }
            ParticleUtils.controlledSmashParticles(this, 3.0F, 0.0F, 0.0F, 10.0F, 3.0F);
        }
        if (this.getAttackState() == 22) {
            if (this.attackTicks == 2) {
                switch (this.random.nextInt(2)) {
                    case 0: {
                        this.isRightUppercut = false;
                        break;
                    }
                    case 1: {
                        this.isRightUppercut = true;
                    }
                }
            }
            if (this.attackTicks == 56) {
                Vec3 entityPosition = this.position();
                CameraShakeEntity.cameraShake(this.level(), entityPosition, 20.0F, 0.15F, 0, 20);
                this.doSmashEffects(4.5F, 0.0F, ModSounds.HUGE_ENERGY_EXPLOSION.get());
                this.AreaAttack(5.0F, 4.0F, 180.0F, 23.0F - removeMeleeDamage, 5.0F, 160, false, true);
                ParticleUtils.controlledSmashParticles(this, 0.0F, 0.0F, 0.0F, 7.5F, 4.0F);
                double multiplier = 4.0D;
                float size = 2.0F;
                int amountOfFlames = 6;
                for (int k3 = 0; k3 < amountOfFlames; ++k3) {
                    float f33 = (float) k3 * (float) Math.PI * size / (float) amountOfFlames + (float) Math.PI * size / 10.0F;
                    int standingOnY4 = Mth.floor(this.getY());
                    this.spawnFlames(this.getX() + (double) Mth.cos(f33) * multiplier, this.getZ() + (double) Mth.sin(f33) * multiplier, standingOnY4, this.getY() + 1.0D, f33, 2, flameStompGroundDamage, false, ModParticles.GROUND_ANNIHILATION_NUKE.get());
                }
                if (this.targetIsNotNull()) {
                    float f19 = Mth.cos(this.yBodyRot * ((float) Math.PI / 180));
                    float f115 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180));
                    double theta15 = (double) this.yBodyRot * (Math.PI / 180);
                    double vecX6 = Math.cos(theta15 += 1.5707963267948966);
                    double vecZ6 = Math.sin(theta15);
                    float vec4 = 0.0F;
                    float offset16 = this.isRightUppercut ? -3.0F : 3.0F;
                    int standingOnY5 = Mth.floor(this.getY());
                    this.spawnArmedClones(this.getX() + (double) vec4 * vecX6 + (double) (f19 * offset16), this.getZ() + (double) vec4 * vecZ6 + (double) (f115 * offset16), standingOnY5, this.getY() + 2.0D, 1.0F, 0, this.target().getX(), this.getY(), this.target().getZ(), this.isRightUppercut ? 3 : 1, 45);
                }
            }
            if (this.attackTicks == 81) {
                this.doTeleportEffects(this.getIsSecondPhase());
            }
            if (this.attackTicks == 83) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 10.0F, 0.1F, 5, 5);
            }
            if (this.attackTicks == 88 && this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(new Circle.RingData(0.0F, 1.5707964F, 20, 0.0F, 1.0F, 0.0F, 1.0F, 100.0F, false, Circle.EnumRingBehavior.GROW), this.getX(), this.getY() + (double) 0.1F, this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
            if (this.attackTicks == 105) {
                this.basicDash(2.0F, 2.0F, 0.0F, false);
            }
            if (this.attackTicks == 108) {
                this.playSound(ModSounds.DIMENSIONAL_BOMB_EXPLODE.get());
                SoundEvent soundEvent = ModSounds.THE_OBLITERATOR_STUN.get();
                this.SideAreaAttack(4.25F, 5.0F, 90.0F, 0.0F, 21.0F - removeMeleeDamage, 4.0F, 160, false, true, soundEvent, 0.5F);
                Random random = new Random();
                int randomStrenght = random.nextInt(4) + 3;
                this.shootPlasmaBall(2, 20.0F, true, 1.0F, 4.0F);
                this.shootPlasmaBall(2, 20.0F, false, 1.0F, 4.0F);
            }
            if (this.attackTicks == 112) {
                this.shootPlasmaBall(1, 30.0F, false, 0.0F, 4.0F);
            }
        }
        int hitTick1 = 25;
        int dashTick1 = hitTick1 - 3;
        int hitTick2 = 39;
        int dashTick2 = hitTick2 - 3;
        int tpHit1 = 40;
        int tpDash1 = tpHit1 - 3;
        int tpHit2 = 54;
        int tpDash2 = tpHit2 - 3;
        int tpOffsetRight = -5;
        float tpOffsetLeft = 5.0f;
        float tpVecLeft = 0.5f;
        float tpVecRight = 0.5f;
        if (this.getAttackState() == 15) {
            swingSound = ModSounds.HEAVY_SWING.get();
            SoundEvent impactSound = ModSounds.WEAPON_IMPACT.get();
            if (this.attackTicks == dashTick1) {
                this.calculatedDash(0.25f);
                this.createSweep(3.0f, -1.5f, -5.0f, false, 1.0f);
                this.playSound(swingSound, 1.0f, 1.0f);
            }
            if (this.attackTicks == hitTick1) {
                this.SideAreaAttack(doubleSlashRange, 4.0f, doubleSlashFirstArc, -90.0f, 20.0f - removeMeleeDamage, 3.0f, 100, false, false, impactSound, 0.5f);
            }
            if (this.attackTicks == dashTick2) {
                this.createSweep(3.0f, -1.0f, -5.0f, false, 1.0f);
                this.calculatedDash(0.25f);
                this.playSound(swingSound, 1.0f, 1.0f);
            }
            if (this.attackTicks == hitTick2) {
                this.SideAreaAttack(doubleSlashRange, 4.0f, doubleSlashSecondArc, -90.0f, 20.0f - removeMeleeDamage, 3.0f, 100, false, false, impactSound, 0.5f);
            }
        }
        if (this.getAttackState() == 31) {
            swingSound = ModSounds.HEAVY_SWING.get();
            SoundEvent impactSound = ModSounds.WEAPON_IMPACT.get();
            if (this.attackTicks == dashTick1) {
                this.calculatedDash(0.25f);
                this.createSweep(3.0f, 1.5f, -5.0f, true, 1.0f);
                this.playSound(swingSound, 1.0f, 1.0f);
            }
            if (this.attackTicks == hitTick1) {
                this.SideAreaAttack(doubleSlashRange, 4.0f, doubleSlashFirstArc, 90.0f, 20.0f - removeMeleeDamage, 3.0f, 100, false, false, impactSound, 0.5f);
            }
            if (this.attackTicks == dashTick2) {
                this.createSweep(3.0f, 1.0f, -5.0f, true, 1.0f);
                this.calculatedDash(0.25f);
                this.playSound(swingSound, 1.0f, 1.0f);
            }
            if (this.attackTicks == hitTick2) {
                this.SideAreaAttack(doubleSlashRange, 4.0f, doubleSlashSecondArc, 90.0f, 20.0f - removeMeleeDamage, 3.0f, 100, false, false, impactSound, 0.5f);
            }
        }
        if (this.getAttackState() == 23) {
            swingSound = ModSounds.HEAVY_SWING.get();
            SoundEvent impactSound = ModSounds.WEAPON_IMPACT.get();
            if (this.attackTicks == 14) {
                this.doTeleportEffects(this.getIsSecondPhase());
            }
            if (this.attackTicks == 15 && this.targetIsNotNull()) {
                f = Mth.cos((float)(this.target().yBodyRot * ((float)Math.PI / 180)));
                float f116 = Mth.sin((float)(this.target().yBodyRot * ((float)Math.PI / 180)));
                theta = (double)this.target().yBodyRot * (Math.PI / 180);
                vecX3 = Math.cos(theta += 1.5707963267948966);
                vecZ3 = Math.sin(theta);
                vec = tpVecLeft;
                offset2 = tpOffsetLeft;
                this.teleport(this.target().getX() + (double)vec * vecX3 + (double)(f * offset2), this.target().getY(), this.target().getZ() + (double)vec * vecZ3 + (double)(f116 * offset2));
            }
            if (this.attackTicks == tpDash1) {
                this.calculatedDash(0.25f);
                this.createSweep(3.0f, -1.5f, -5.0f, false, 1.0f);
                this.playSound(swingSound, 1.0f, 1.0f);
            }
            if (this.attackTicks == tpHit1) {
                this.SideAreaAttack(doubleSlashRange, 4.0f, teleportDoubleSlashFirstArc, -90.0f, 20.0f - removeMeleeDamage, 3.0f, 100, false, false, impactSound, 0.5f);
            }
            if (this.attackTicks == tpDash2) {
                this.createSweep(3.0f, -1.0f, -5.0f, false, 1.0f);
                this.calculatedDash(0.25f);
                this.playSound(swingSound, 1.0f, 1.0f);
            }
            if (this.attackTicks == tpHit2) {
                this.SideAreaAttack(doubleSlashRange, 4.0f, doubleSlashSecondArc, -90.0f, 20.0f - removeMeleeDamage, 3.0f, 100, false, false, impactSound, 0.5f);
            }
        }
        if (this.getAttackState() == 32) {
            swingSound = ModSounds.HEAVY_SWING.get();
            SoundEvent impactSound = ModSounds.WEAPON_IMPACT.get();
            if (this.attackTicks == 14) {
                this.doTeleportEffects(this.getIsSecondPhase());
            }
            if (this.attackTicks == 15 && this.targetIsNotNull()) {
                f = Mth.cos((float)(this.target().yBodyRot * ((float)Math.PI / 180)));
                float f117 = Mth.sin((float)(this.target().yBodyRot * ((float)Math.PI / 180)));
                theta = (double)this.target().yBodyRot * (Math.PI / 180);
                vecX3 = Math.cos(theta += 1.5707963267948966);
                vecZ3 = Math.sin(theta);
                vec = tpVecRight;
                offset2 = tpOffsetRight;
                this.teleport(this.target().getX() + (double)vec * vecX3 + (double)(f * offset2), this.target().getY(), this.target().getZ() + (double)vec * vecZ3 + (double)(f117 * offset2));
            }
            if (this.attackTicks == tpDash1) {
                this.calculatedDash(0.25f);
                this.createSweep(3.0f, 1.5f, -5.0f, true, 1.0f);
                this.playSound(swingSound, 1.0f, 1.0f);
            }
            if (this.attackTicks == tpHit1) {
                this.SideAreaAttack(4.0f, 4.0f, teleportDoubleSlashFirstArc, 90.0f, 20.0f - removeMeleeDamage, 3.0f, 100, false, false, impactSound, 0.5f);
            }
            if (this.attackTicks == tpDash2) {
                this.createSweep(3.0f, 1.0f, -5.0f, true, 1.0f);
                this.calculatedDash(0.25f);
                this.playSound(swingSound, 1.0f, 1.0f);
            }
            if (this.attackTicks == tpHit2) {
                this.SideAreaAttack(4.0f, 4.0f, doubleSlashSecondArc, 90.0f, 20.0f - removeMeleeDamage, 3.0f, 100, false, false, impactSound, 0.5f);
            }
        }
        if (this.getAttackState() == 24) {
            float offset22;
            float vec22;
            if (this.attackTicks == 10) {
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(new Circle.RingData(0.0f, 1.5707964f, 20, 0.0f, 1.0f, 0.0f, 1.0f, 100.0f, false, Circle.EnumRingBehavior.SHRINK), this.getX(), this.getY() + 0.5, this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
            }
            if (this.attackTicks == 20) {
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(new Circle.RingData(0.0f, 1.5707964f, 20, 0.0f, 1.0f, 0.0f, 1.0f, 100.0f, false, Circle.EnumRingBehavior.SHRINK), this.getX(), this.getY() + 0.5, this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
            }
            float f20 = Mth.cos((float)(this.yBodyRot * ((float)Math.PI / 180)));
            f1 = Mth.sin((float)(this.yBodyRot * ((float)Math.PI / 180)));
            double theta16 = (double)this.yBodyRot * (Math.PI / 180);
            vecX2 = Math.cos(theta16 += 1.5707963267948966);
            vecZ2 = Math.sin(theta16);
            int floor = Mth.floor((double)this.getY());
            if (this.attackTicks == 22) {
                float vec5 = 2.0f;
                offset = 0.0f;
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(new Circle.RingData(0.0f, 0.0f, 30, 0.0f, 1.0f, 0.0f, 1.0f, 40.0f, true, Circle.EnumRingBehavior.GROW_THEN_SHRINK), this.getX() + (double)vec5 * vecX2 + (double)(f20 * offset), this.getY() + 2.0, this.getZ() + (double)vec5 * vecZ2 + (double)(f1 * offset), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
            }
            if (this.attackTicks == 28) {
                float vec1 = 2.0f;
                float offset1 = 0.0f;
                this.spawnCircleParticle(0.0f, 0.0f, 100.0f, false, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f);
                this.playSound(SoundEvents.END_PORTAL_SPAWN, 1.0f, 1.0f);
                this.AreaAttack(8.0f, 6.0f, 360.0f, 10.0f, 8.0f, 100, false, false);
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0f, 0.15f, 0, 20);
                if (this.targetIsNotNull()) {
                    this.spawnArmedClones(this.getX() + (double)vec1 * vecX2 + (double)(f20 * offset1), this.getZ() + (double)vec1 * vecZ2 + (double)(f1 * offset1), floor, this.getY() + 2.0, 0.0f, 0, this.target().getX(), this.getY(), this.target().getZ(), 1, 45);
                }
            }
            if (this.attackTicks >= 28 && this.attackTicks <= 32) {
                this.Sphereparticle(ModParticles.SMALL_ANNIHILATION_FLAME.get(), 0.35f, 2.0f, 3.0f);
            }
            if (this.attackTicks == 46) {
                vec22 = 0.0f;
                offset22 = -3.0f;
                if (this.targetIsNotNull()) {
                    this.spawnArmedClones(this.getX() + (double)vec22 * vecX2 + (double)(f20 * offset22), this.getZ() + (double)vec22 * vecZ2 + (double)(f1 * offset22), floor, this.getY() + 2.0, 0.0f, 0, this.target().getX(), this.getY(), this.target().getZ(), 1, 45);
                }
            }
            if (this.attackTicks == 56) {
                vec22 = 2.0f;
                offset22 = 0.0f;
                if (this.getTarget() != null) {
                    float f23 = Mth.cos((float)(this.getTarget().yBodyRot * ((float)Math.PI / 180)));
                    float f34 = Mth.sin((float)(this.getTarget().yBodyRot * ((float)Math.PI / 180)));
                    double theta22 = (double)this.getTarget().yBodyRot * (Math.PI / 180);
                    double vecX22 = Math.cos(theta22 += 1.5707963267948966);
                    double vecZ22 = Math.sin(theta22);
                    float vec33 = 3.0f;
                    float offset32 = -5.0f;
                    this.spawnArmedClones(this.getX() + (double)vec22 * vecX2 + (double)(f20 * offset22), this.getZ() + (double)vec22 * vecZ2 + (double)(f1 * offset22), floor, this.getY() + 2.0, 0.0f, 0, this.getTarget().getX() + (double)vec33 * vecX22 + (double)(f23 * offset32), this.getY(), this.getTarget().getZ() + (double)vec33 * vecZ22 + (double)(f34 * offset32), 2, 50);
                }
            }
            if (this.attackTicks == 97) {
                this.calculatedDash(0.35f);
            }
            if (this.attackTicks == 100) {
                SoundEvent soundEvent = ModSounds.WEAPON_IMPACT.get();
                this.SideAreaAttack(5.0f, 4.0f, doubleSlashFirstArc, -90.0f, 20.0f - removeMeleeDamage, 3.0f, 160, false, true, soundEvent, 0.5f);
            }
        }
        if (this.getAttackState() == 25) {
            if (this.attackTicks == 7) {
                this.savePreSwapPositions(this.targetIsNotNull());
            }
            if (this.attackTicks == 15) {
                this.calculatedDashTowardsLocation(0.25f, (float)this.lastTargetX, (float)this.lastTargetZ);
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 3.0f, 1.0f);
            }
            if (this.attackTicks == 18) {
                this.StraightLineAreaAttack(2.5E-4f, 1.0, kickRange, 150, 20.0f - removeMeleeDamage, 4.0f, false, ModSounds.THE_OBLITERATOR_STUN.get());
            }
            if (this.attackTicks == 35) {
                this.calculatedDash(0.25f);
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 3.0f, 1.0f);
            }
            if (this.attackTicks == 38) {
                this.createCircularLightningParticle(2.0f, 0.0f, 10, 2.0f);
                this.doFlamesEffect(3.0, 2.0f, 7, false, ModParticles.ANNIHILATION_FLAME_STRIKE.get(), 2.0);
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0f, 0.25f, 0, 20);
                this.spawnCircleParticle(0.0f, 0.0f, 100.0f, false, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f);
                this.playSound(ModSounds.HUGE_ENERGY_EXPLOSION.get(), 3.0f, 1.0f);
                this.AreaAttack(3.8f, 5.0f, 180.0f, 22.0f, 4.0f, 150, false, false);
                ParticleUtils.controlledSmashParticles(this, 3.0f, 0.0f, 0.0f, 0.5f, 1.0f);
            }
            if (this.attackTicks == 70) {
                this.calculatedDash(0.35f);
            }
            if (this.attackTicks == 73) {
                this.SideAreaAttack(5.0f, 4.0f, 215.0f, -90.0f, 20.0f - removeMeleeDamage, 4.0f, 160, false, true, ModSounds.WEAPON_IMPACT.get(), 0.5f);
            }
        }
        if (this.getAttackState() == 36) {
            if (this.attackTicks == 7) {
                this.savePreSwapPositions(this.targetIsNotNull());
            }
            if (this.attackTicks == 15) {
                this.calculatedDashTowardsLocation(0.25F, (float) this.lastTargetX, (float) this.lastTargetZ);
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 3.0F, 1.0F);
            }
            if (this.attackTicks == 18) {
                this.StraightLineAreaAttack(-0.6F, 4.0, kickRange, 150, 20.0F - removeMeleeDamage, 4.0F, false,
                        ModSounds.THE_OBLITERATOR_STUN.get());
            }
            if (this.attackTicks == 35) {
                this.calculatedDash(0.25F);
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 3.0F, 1.0F);
            }
            if (this.attackTicks == 38) {
                ParticleUtils.controlledSmashParticles(this, 1.0F, 0.0F, 0.0F, 2.5F, 1.5F);
                this.doFlamesEffect(3.0, 2.0F, 7, false, ModParticles.ANNIHILATION_FLAME_STRIKE.get(), 2.0);
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.25F, 0, 20);
                this.spawnCircleParticle(0.0F, 0.0F, 100.0F, false, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F);
                this.playSound(ModSounds.HUGE_ENERGY_EXPLOSION.get(), 3.0F, 1.0F);
                this.AreaAttack(3.8F, 5.0F, 180.0F, 21.0F - removeMeleeDamage, 4.0F, 150, false, false);
            }
        }
        if (this.getAttackState() == 37) {
            if (this.attackTicks == 24) {
                this.calculatedDash(0.35F);
            }
            if (this.attackTicks == 27) {
                this.SideGrab(5.0F, 4.0F, 225.0F, -90.0F, 20.0F - removeMeleeDamage, 160,
                        SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F);
            }
        }
        if (this.getAttackState() == 49) {
            if (this.attackTicks == 24) {
                this.calculatedDash(0.35F);
            }
            if (this.attackTicks == 27) {
                this.SideGrab(5.0F, 4.0F, 225.0F, 90.0F, 20.0F - removeMeleeDamage, 160,
                        SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F);
            }
        }
        if (this.getAttackState() == 39) {
            if (this.attackTicks == 13) {
                this.doTeleportEffects(this.getIsSecondPhase());
            }
            if (this.attackTicks == 14 && this.targetIsNotNull()) {
                float f21 = Mth.cos(this.target().yBodyRot * ((float) Math.PI / 180));
                f1 = Mth.sin(this.target().yBodyRot * ((float) Math.PI / 180));
                double theta17 = (double) this.target().yBodyRot * (Math.PI / 180);
                vecX2 = Math.cos(theta17 += 1.5707963267948966);
                vecZ2 = Math.sin(theta17);
                float vec6 = -4.0F;
                offset = 0.0F;
                this.teleport(this.target().getX() + (double) vec6 * vecX2 + (double) (f21 * offset),
                        this.target().getY(),
                        this.target().getZ() + (double) vec6 * vecZ2 + (double) (f1 * offset));
            }
            if (this.attackTicks == 28) {
                this.calculatedDash(0.35F);
            }
            if (this.attackTicks == 28) {
                this.calculatedDash(0.35F);
            }
            if (this.attackTicks == 31) {
                this.SideGrab(5.0F, 4.0F, 215.0F, -90.0F, 20.0F - removeMeleeDamage, 160,
                        ModSounds.WEAPON_IMPACT.get(), 0.5F);
            }
        }
        if (this.getAttackState() == 40) {
            if (this.attackTicks == 18) {
                float f22 = Mth.cos(this.yBodyRot * ((float) Math.PI / 180));
                f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180));
                double theta18 = (double) this.yBodyRot * (Math.PI / 180);
                vecX2 = Math.cos(theta18 += 1.5707963267948966);
                vecZ2 = Math.sin(theta18);
                float vec7 = 2.5F;
                float offset17 = 0.0F;
                this.SideAreaAttack(5.0F, 6.0F, 180.0F, 0.0F, 8.0F, 10.0F, 0, false, false, SoundEvents.EMPTY, 1.0F);
                this.playSound(ModSounds.ENERGY_EXPLOSION.get());
                g = (float) Math.toRadians(-this.getYRot() + 180.0F);
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(new Circle.RingData(g, 0.0F, 30, 0.0F, 1.0F, 0.0F, 1.0F, 80.0F, true,
                                    Circle.EnumRingBehavior.GROW_THEN_SHRINK),
                            this.getX() + (double) vec7 * vecX2 + (double) (f22 * offset17), this.getY() + 5.0,
                            this.getZ() + (double) vec7 * vecZ2 + (double) (f1 * offset17),
                            1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
            }
            if (this.attackTicks >= 18 && this.attackTicks <= 22) {
                float f23 = Mth.cos(this.yBodyRot * ((float) Math.PI / 180));
                f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180));
                double theta19 = (double) this.yBodyRot * (Math.PI / 180);
                vecX2 = Math.cos(theta19 += 1.5707963267948966);
                vecZ2 = Math.sin(theta19);
                float vec8 = 2.5F;
                float offset18 = 0.0F;
                f3 = (this.random.nextFloat() - 0.5F) * 4.0F;
                f4 = (this.random.nextFloat() - 0.5F) * 2.0F;
                float f52 = (this.random.nextFloat() - 0.5F) * 4.0F;
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ModParticles.ANNIHILATION_EXPLOSION.get(),
                            this.getX() + (double) vec8 * vecX2 + (double) (f23 * offset18) + (double) f3,
                            this.getY() + 5.0 + (double) f4,
                            this.getZ() + (double) vec8 * vecZ2 + (double) (f1 * offset18) + (double) f52,
                            1, 0.0D, 0.0D, 0.0D, 0.0D);
                    serverLevel.sendParticles(ModParticles.ANNIHILATION_EXPLOSION.get(),
                            this.getX() + (double) vec8 * vecX2 + (double) (f23 * offset18) + (double) f3,
                            this.getY() + 2.0 + (double) f4,
                            this.getZ() + (double) vec8 * vecZ2 + (double) (f1 * offset18) + (double) f52,
                            1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
            }
            if (this.attackTicks == 54) {
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.0F);
            }
            if (this.attackTicks == 57) {
                float f24 = Mth.cos(this.yBodyRot * ((float) Math.PI / 180));
                f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180));
                double theta20 = (double) this.yBodyRot * (Math.PI / 180);
                vecX2 = Math.cos(theta20 += 1.5707963267948966);
                vecZ2 = Math.sin(theta20);
                float destVec = 15.0F;
                float destoffset = 0.0F;
                vec = 2.25F;
                offset2 = -0.5F;
                Entity theta22 = this.getFirstPassenger();
                if (theta22 instanceof LivingEntity livingEntity && this.getFirstPassenger() != null
                        && !this.level().isClientSide) {
                    this.throwAnGravityEntity(0.75F,
                            this.getX() + (double) destVec * vecX2 + (double) (f24 * destoffset), this.getY() + 1.0,
                            this.getZ() + (double) destVec * vecZ2 + (double) (f1 * destoffset),
                            this.getX() + (double) vec * vecX2 + (double) (f24 * offset2), this.getY() + 2.0,
                            this.getZ() + (double) vec * vecZ2 + (double) (f1 * offset2), 1.0F, livingEntity);
                }
            }
        }
        if (this.getAttackState() == 50) {
            if (this.attackTicks == 18) {
                float f25 = Mth.cos(this.yBodyRot * ((float) Math.PI / 180));
                f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180));
                double theta21 = (double) this.yBodyRot * (Math.PI / 180);
                vecX2 = Math.cos(theta21 += 1.5707963267948966);
                vecZ2 = Math.sin(theta21);
                float vec9 = 2.5F;
                float offset19 = 0.0F;
                this.SideAreaAttack(5.0F, 6.0F, 180.0F, 0.0F, 8.0F, 10.0F, 0, false, false, SoundEvents.EMPTY, 1.0F);
                this.playSound(ModSounds.ENERGY_EXPLOSION.get());
                g = (float) Math.toRadians(-this.getYRot() + 180.0F);
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(new Circle.RingData(g, 0.0F, 30, 0.0F, 1.0F, 0.0F, 1.0F, 80.0F, true,
                                    Circle.EnumRingBehavior.GROW_THEN_SHRINK),
                            this.getX() + (double) vec9 * vecX2 + (double) (f25 * offset19), this.getY() + 5.0,
                            this.getZ() + (double) vec9 * vecZ2 + (double) (f1 * offset19),
                            1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
            }
            if (this.attackTicks >= 18 && this.attackTicks <= 22) {
                float f26 = Mth.cos(this.yBodyRot * ((float) Math.PI / 180));
                f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180));
                double theta22 = (double) this.yBodyRot * (Math.PI / 180);
                vecX2 = Math.cos(theta22 += 1.5707963267948966);
                vecZ2 = Math.sin(theta22);
                float vec10 = 2.5F;
                float offset20 = 0.0F;
                f3 = (this.random.nextFloat() - 0.5F) * 4.0F;
                f4 = (this.random.nextFloat() - 0.5F) * 2.0F;
                float f53 = (this.random.nextFloat() - 0.5F) * 4.0F;
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ModParticles.ANNIHILATION_EXPLOSION.get(),
                            this.getX() + (double) vec10 * vecX2 + (double) (f26 * offset20) + (double) f3,
                            this.getY() + 5.0 + (double) f4,
                            this.getZ() + (double) vec10 * vecZ2 + (double) (f1 * offset20) + (double) f53,
                            1, 0.0D, 0.0D, 0.0D, 0.0D);
                    serverLevel.sendParticles(ModParticles.ANNIHILATION_EXPLOSION.get(),
                            this.getX() + (double) vec10 * vecX2 + (double) (f26 * offset20) + (double) f3,
                            this.getY() + 2.0 + (double) f4,
                            this.getZ() + (double) vec10 * vecZ2 + (double) (f1 * offset20) + (double) f53,
                            1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
            }
            if (this.attackTicks == 54) {
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.0F);
            }
            if (this.attackTicks == 57) {
                float f27 = Mth.cos(this.yBodyRot * ((float) Math.PI / 180));
                f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180));
                double theta23 = (double) this.yBodyRot * (Math.PI / 180);
                vecX2 = Math.cos(theta23 += 1.5707963267948966);
                vecZ2 = Math.sin(theta23);
                float destVec = 15.0F;
                float destoffset = 0.0F;
                vec = 2.25F;
                offset2 = -0.5F;
                Entity theta22 = this.getFirstPassenger();
                if (theta22 instanceof LivingEntity livingEntity && this.getFirstPassenger() != null
                        && !this.level().isClientSide) {
                    this.throwAnGravityEntity(0.75F,
                            this.getX() + (double) destVec * vecX2 + (double) (f27 * destoffset), this.getY() + 1.0,
                            this.getZ() + (double) destVec * vecZ2 + (double) (f1 * destoffset),
                            this.getX() + (double) vec * vecX2 + (double) (f27 * offset2), this.getY() + 2.0,
                            this.getZ() + (double) vec * vecZ2 + (double) (f1 * offset2), 1.0F, livingEntity);
                }
            }
        }
        if (this.getAttackState() == 27) {
            if (this.attackTicks == 3) {
                this.backStep(-2.0F, 0.0F);
            }
            if (this.attackTicks == 34) {
                this.setDeltaMovement(0.0D, 1.3D, 0.0D);
            }
            if (this.attackTicks == 45) {
                float f29 = Mth.cos((float) (this.yBodyRot * ((float) Math.PI / 180)));
                float f119 = Mth.sin((float) (this.yBodyRot * ((float) Math.PI / 180)));
                double theta25 = (double) this.yBodyRot * (Math.PI / 180);
                double vecX8 = Math.cos(theta25 += 1.5707963267948966);
                vecZ2 = Math.sin(theta25);
                float vec12 = 2.0F;
                offset = 0.0F;
                TrackingBombEntity trackingBombEntity = new TrackingBombEntity(ModEntities.TRACKING_BOMB.get(), this.level(), this, 10.0F, 20);
                trackingBombEntity.setPosRaw(this.getX() + (double) vec12 * vecX8 + (double) (f29 * offset), this.getY(), this.getZ() + (double) vec12 * vecZ2 + (double) (f119 * offset));
                this.level().addFreshEntity(trackingBombEntity);
            }
            if (this.attackTicks == 55) {
                this.setNoGravity(true);
            }
            if (this.attackTicks == 63) {
                this.setNoGravity(false);
                if (this.targetIsNotNull()) {
                    float f30 = Mth.cos((float) (this.target().yBodyRot * ((float) Math.PI / 180)));
                    float f120 = Mth.sin((float) (this.target().yBodyRot * ((float) Math.PI / 180)));
                    double theta26 = (double) this.target().yBodyRot * (Math.PI / 180);
                    double vecX9 = Math.cos(theta26 += 1.5707963267948966);
                    vecZ2 = Math.sin(theta26);
                        float vec13 = 2.0F;
                    offset = 0.0F;
                    this.teleport(this.target().getX() + (double) vec13 * vecX9 + (double) (f30 * offset), this.target().getY(), this.target().getZ() + (double) vec13 * vecZ2 + (double) (f120 * offset));
                }
                if (this.attackTicks == 71) {
                    this.StraightLineAreaAttack(0.005F, 1.0, 4.0F, 100, 20.0F - removeMeleeDamage, 3.0F, true, ModSounds.THE_OBLITERATOR_STUN.get());
                }
            }
        }
        if (this.getAttackState() == 28) {
            if (this.attackTicks >= 6 && this.attackTicks <= 8) {
                this.attractingParticles(ModParticles.SMALL_ANNIHILATION_FLAME.get(), 1.0F, 1.0F);
            }
            if (this.attackTicks == 30) {
                float f31 = Mth.cos((float) (this.yBodyRot * ((float) Math.PI / 180)));
                float f121 = Mth.sin((float) (this.yBodyRot * ((float) Math.PI / 180)));
                double theta27 = (double) this.yBodyRot * (Math.PI / 180);
                double vecX10 = Math.cos(theta27 += 1.5707963267948966);
                vecZ2 = Math.sin(theta27);
                float vec14 = 0.0F;
                offset = 3.0F;
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ModParticles.ANNIHILATION_EXPLOSION.get(), this.getX() + (double) vec14 * vecX10 + (double) (f31 * offset), this.getY() + 3.0, this.getZ() + (double) vec14 * vecZ2 + (double) (f121 * offset), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
            }
            if (this.attackTicks == 6) {
                this.backStep(-1.5F, 0.6F);
            }
            for (int i = 39; i <= 54; i += 2) {
                if (this.attackTicks != i) {
                    continue;
                }
                int d = i - 37;
                this.flameRadagonShockwave(0.25F, d, 1.0F, 2, 0.0F, flamewaveOffset, 7.0F, false);
            }
            if (this.attackTicks == 39) {
                ParticleUtils.controlledSmashParticles(this, 1.0F, 1.75F, 0.0F, 2.5F, 1.5F);
                this.createCircularLightningParticle(2.0F, 2.0F, 10, 2.0F);
                this.doFlamesEffect(3.0, 2.0F, 7, false, ModParticles.ANNIHILATION_FLAME_STRIKE.get(), 2.0);
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.25F, 0, 20);
                this.spawnCircleParticle(0.0F, 0.0F, 100.0F, false, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F);
                this.playSound(ModSounds.HUGE_ENERGY_EXPLOSION.get(), 3.0F, 1.0F);
                this.SideAreaAttack(4.0F, 5.0F, 210.0F, -90.0F, 22.0F - removeMeleeDamage, 4.0F, 150, false, false, SoundEvents.EMPTY, 1.0F);
            }
        }
        if (this.getAttackState() == 29) {
            if (this.attackTicks >= 6 && this.attackTicks <= 8) {
                this.attractingParticles(ModParticles.SMALL_ANNIHILATION_FLAME.get(), 1.0F, 1.0F);
            }
            if (this.attackTicks == 6) {
                this.backStep(-1.5F, 0.6F);
            }
            if (this.attackTicks == 30) {
                float f32 = Mth.cos((float) (this.yBodyRot * ((float) Math.PI / 180)));
                float f122 = Mth.sin((float) (this.yBodyRot * ((float) Math.PI / 180)));
                double theta28 = (double) this.yBodyRot * (Math.PI / 180);
                double vecX11 = Math.cos(theta28 += 1.5707963267948966);
                vecZ2 = Math.sin(theta28);
                float vec15 = 0.0F;
                offset = -3.0F;
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ModParticles.ANNIHILATION_EXPLOSION.get(), this.getX() + (double) vec15 * vecX11 + (double) (f32 * offset), this.getY() + 3.0, this.getZ() + (double) vec15 * vecZ2 + (double) (f122 * offset), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
            }
            for (int i = 39; i <= 54; i += 2) {
                if (this.attackTicks != i) {
                    continue;
                }
                int d = i - 37;
                this.flameRadagonShockwave(0.25F, d, 1.0F, 2, 0.0F, -flamewaveOffset, 7.0F, false);
            }
            if (this.attackTicks == 39) {
                ParticleUtils.controlledSmashParticles(this, 1.0F, -1.75F, 0.0F, 2.5F, 1.5F);
                this.createCircularLightningParticle(2.0F, -2.0F, 10, 2.0F);
                this.doFlamesEffect(3.0, 2.0F, 7, false, ModParticles.ANNIHILATION_FLAME_STRIKE.get(), 2.0);
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.25F, 0, 20);
                this.spawnCircleParticle(0.0F, 0.0F, 100.0F, false, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F);
                this.playSound(ModSounds.HUGE_ENERGY_EXPLOSION.get(), 3.0F, 1.0F);
                this.SideAreaAttack(4.0F, 5.0F, 210.0F, 90.0F, 22.0F - removeMeleeDamage, 4.0F, 150, false, false, SoundEvents.EMPTY, 1.0F);
            }
        }
        if (this.getAttackState() == 30) {
            if (this.attackTicks == 15) {
                this.playSound(ModSounds.HEAVY_SWING.get(), 3.0F, 1.0F);
                this.calculatedDash(0.25F);
            }
            if (this.attackTicks == 18) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 10.0F, 0.1F, 0, 10);
                this.createSweep(3.0F, -2.0F, -5.0F, false, 1.0F);
                this.createSweep(3.0F, 2.0F, -5.0F, true, 1.0F);
                this.SideAreaAttack(4.0F, 4.0F, 160.0F, 90.0F, stunAttackDamage - removeMeleeDamage, 6.0F, 120, true, true, ModSounds.THE_OBLITERATOR_STUN.get(), 1.0F);
                this.SideAreaAttack(4.0F, 4.0F, 160.0F, -90.0F, stunAttackDamage - removeMeleeDamage, 6.0F, 120, true, true, ModSounds.THE_OBLITERATOR_STUN.get(), 1.0F);
                this.SideAreaAttack(4.0F, 4.0F, 180.0F, 0.0F, 1.25F, stunAttackDamage - removeMeleeDamage, 6.0F, 100, true, true, ModSounds.THE_OBLITERATOR_STUN.get(), 1.0F);
            }
        }
        if (this.getAttackState() == 34) {
            if (this.attackTicks == 16) {
                ParticleUtils.controlledSmashParticles(this, 3.0F, 0.0F, 0.0F, 2.5F, 1.5F);
                this.createCircularLightningParticle(2.0F, 0.0F, 10, 2.0F);
                this.doFlamesEffect(3.0, 2.0F, 7, false, ModParticles.ANNIHILATION_FLAME_STRIKE.get(), 2.0);
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.25F, 0, 20);
                this.spawnCircleParticle(0.0F, 0.0F, 100.0F, false, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F);
                this.playSound(ModSounds.HUGE_ENERGY_EXPLOSION.get(), 3.0F, 1.0F);
                this.SideAreaAttack(5.0F, 4.0F, 180.0F, 0.0F, 19.0F, 4.0F, 100, false, false, SoundEvents.EMPTY, 1.0F);
            }
            if (this.attackTicks == 28) {
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.0F);
                this.calculatedDash(0.25F);
            }
            if (this.attackTicks == 31) {
                this.SideAreaAttack(5.0F, 4.0F, 180.0F, 0.0F, 18.0F, 4.0F, 100, false, false, SoundEvents.EMPTY, 1.0F);
            }
        }
        if (this.getAttackState() == 35) {
            if (this.attackTicks == 20) {
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(new Circle.RingData(0.0F, 1.5707964F, 20, 0.0F, 1.0F, 0.0F, 1.0F, 150.0F, false, Circle.EnumRingBehavior.SHRINK), this.getX(), this.getY() + 0.5, this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
            }
            if (this.attackTicks == 35) {
                this.playSound(ModSounds.HEAVY_SWING.get(), 3.0F, 1.0F);
                this.calculatedDash(0.25F);
            }
            if (this.attackTicks == 38) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 10.0F, 0.1F, 0, 10);
                this.createSweep(3.0F, -2.0F, -5.0F, false, 1.0F);
                this.createSweep(3.0F, 2.0F, -5.0F, true, 1.0F);
                this.SideAreaAttack(3.75F, 4.0F, 160.0F, 90.0F, stunAttackDamage - removeMeleeDamage, 6.0F, 120, true, true, ModSounds.THE_OBLITERATOR_STUN.get(), 1.0F);
                this.SideAreaAttack(3.75F, 4.0F, 160.0F, -90.0F, stunAttackDamage - removeMeleeDamage, 6.0F, 120, true, true, ModSounds.THE_OBLITERATOR_STUN.get(), 1.0F);
                this.SideAreaAttack(3.75F, 4.0F, 180.0F, 0.0F, 1.25F, stunAttackDamage - removeMeleeDamage, 6.0F, 100, true, true, ModSounds.THE_OBLITERATOR_STUN.get(), 1.0F);
            }
        }
        if (this.getAttackState() == 33) {
            if (this.attackTicks == 24) {
                this.savePreSwapPositions(this.targetIsNotNull());
            }
            if (this.attackTicks == 31) {
                this.calculatedDashTowardsLocation(0.25F, (float) this.lastTargetX, (float) this.lastTargetZ);
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 3.0F, 1.0F);
            }
            if (this.attackTicks == 34) {
                this.StraightLineAreaAttack(-0.6F, 0.95F, kickRange, 150, 18.0F, 4.0F, false, ModSounds.THE_OBLITERATOR_STUN.get());
            }
        }
        if (this.getAttackState() == 2) {
            if (this.attackTicks > 31 && this.attackTicks < 41) {
                this.Sphereparticle(ModParticles.SMALL_ANNIHILATION_FLAME.get(), 4.0F, 1.0F, 5.0F);
            }
            if (this.attackTicks > 2 && this.attackTicks < 30) {
                int ucap = 5;
                int uReps = 5;
                int uReps2 = 1;
                ParticleOptions small = ModParticles.SMALL_ANNIHILATION_FLAME.get();
                ParticleOptions big = ModParticles.BIG_ANNIHILATION_FLAME.get();
                this.attractParticles(small, ucap, uReps, 0.0F, 0.0F, 5.0F, 3.0F, 0.075F);
                this.attractParticles(small, ucap, uReps, 0.0F, 0.0F, 3.0F, 3.0F, 0.075F);
                this.attractParticles(small, ucap, uReps, 0.0F, 0.0F, 2.0F, 3.0F, 0.075F);
                this.attractParticles(big, ucap, uReps2, 0.0F, 0.0F, 5.0F, 3.0F, 0.075F);
                this.attractParticles(big, ucap, uReps2, 0.0F, 0.0F, 3.0F, 3.0F, 0.075F);
                this.attractParticles(big, ucap, uReps2, 0.0F, 0.0F, 2.0F, 3.0F, 0.075F);
            }
            if (this.attackTicks == 33) {
                int lowerArmorAmount = 4;
                for (int i = 0; i < lowerArmorAmount; ++i) {
                    float throwAngle = (float) i * (float) Math.PI / (float) (lowerArmorAmount / 2) + 90.0F;
                    double sx = this.getX() + (double) (Mth.cos(throwAngle) * 1.0F);
                    double sy = this.getY() + 3.0D;
                    double sz = this.getZ() + (double) (Mth.sin(throwAngle) * 1.0F);
                    double vx = Mth.cos(throwAngle);
                    double vy = 0.0F + this.getRandom().nextFloat() * 0.3F;
                    double vz = Mth.sin(throwAngle);
                    double v3 = Mth.sqrt((float) (vx * vx + vz * vz));
                    FlyingArmorEntity projectile = new FlyingArmorEntity(ModEntities.FLYING_ARMOR.get(), this.level(), this, 12.0F, 1);
                    projectile.moveTo(sx, sy, sz, (float) i * 11.25F, this.getXRot());
                    float speed = 0.7F;
                    projectile.shoot(vx, vy + v3 * 0.2D, vz, speed, 1.0F);
                    this.level().addFreshEntity(projectile);
                }
                int upperArmorAmount = 4;
                for (int i = 0; i < upperArmorAmount; ++i) {
                    float throwAngle = (float) i * (float) Math.PI / (float) (upperArmorAmount / 2) + 90.0F;
                    double sx = this.getX() + (double) (Mth.cos(throwAngle) * 1.0F);
                    double sy = this.getY() + 6.0D;
                    double sz = this.getZ() + (double) (Mth.sin(throwAngle) * 1.0F);
                    double vx = Mth.cos(throwAngle);
                    double vy = 0.0F + this.getRandom().nextFloat() * 0.3F;
                    double vz = Mth.sin(throwAngle);
                    double v3 = Mth.sqrt((float) (vx * vx + vz * vz));
                    FlyingArmorEntity projectile = new FlyingArmorEntity(ModEntities.FLYING_ARMOR.get(), this.level(), this, 12.0F, 3);
                    projectile.moveTo(sx, sy, sz, (float) i * 11.25F, this.getXRot());
                    float speed = 0.7F;
                    projectile.shoot(vx, vy + v3 * 0.2D, vz, speed, 1.0F);
                    this.level().addFreshEntity(projectile);
                }
                this.SideAreaAttack(5.0F, 5.0F, 360.0F, 0.0F, 0.0F, 20.0F, 7.0F, 0, false, false, SoundEvents.EMPTY, 1.0F);
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.15F, 20, 10);
                this.playSound(SoundEvents.TOTEM_USE, 2.0F, 1.0F);
                this.setPhase(2);
            }
        }
        uniformDuration = this.isTargetCheesing(-4.0F, 4.0F) ? 10 : 10;
        if (this.getAttackState() == 42) {
            if (this.attackTicks == 1) {
                this.playSound(ModSounds.ANNIHILATION_LASER_CHARGE.get(), 3.0F, 1.5F);
            }
            if (this.attackTicks >= 5 && this.attackTicks <= 23) {
                this.attractParticles(ModParticles.SMALL_ANNIHILATION_FLAME.get(), 2, 5, 0.0F, 0.0F, 5.0F, 3.0F, 0.075F);
            }
            if (this.attackTicks == 23) {
                this.playSound(ModSounds.ANNIHILATION_LASER_SINGLE_SHOOT.get(), 3.0F, 1.0F);
                float f33 = Mth.cos(this.yBodyRot * ((float) Math.PI / 180));
                float f123 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180));
                double theta29 = this.yBodyRot * (Math.PI / 180);
                vecX = Math.cos(theta29 += 1.5707963267948966);
                vecZ = Math.sin(theta29);
                float vec16 = 2.0F;
                offset = 0.0F;
                double spawnX = this.getX() + (double) vec16 * vecX + (double) (f33 * offset);
                double spawnY = this.getY();
                spawnZ = this.getZ() + (double) vec16 * vecZ + (double) (f123 * offset);
                TheObliteratorServant entity = this;
                Vec3 entityPosition = this.position();
                CameraShakeEntity.cameraShake(this.level(), entityPosition, 30.0F, 0.25F, 10, 5);
                AnnihilationBeamEntity energyBeamEntity = new AnnihilationBeamEntity(ModEntities.ANNIHILATION_BEAM.get(),
                        entity.level(), entity, spawnX, this.getY() + 2.0D, spawnZ,
                        (float) ((entity.yHeadRot + 90.0F) * Math.PI / 180),
                        (float) (-entity.getXRot() * Math.PI / 180),
                        uniformDuration, singleShotLaserDamage, 5.0F, 1, false, 0.0F, 0.0F, 0.0F, false, 30.0F);
                entity.level().addFreshEntity(energyBeamEntity);
            }
        }
        if (this.getAttackState() == 43) {
            if (this.attackTicks == 1) {
                this.playSound(ModSounds.ANNIHILATION_LASER_CHARGE.get(), 3.0F, 1.5F);
            }
            if (this.attackTicks == 35) {
                this.playSound(ModSounds.ANNIHILATION_LASER_SINGLE_SHOOT.get(), 3.0F, 1.0F);
                float f34 = Mth.cos(this.yBodyRot * ((float) Math.PI / 180));
                float f124 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180));
                double theta30 = this.yBodyRot * (Math.PI / 180);
                vecX = Math.cos(theta30 += 1.5707963267948966);
                vecZ = Math.sin(theta30);
                float vec17 = 2.0F;
                offset = 0.0F;
                double spawnX = this.getX() + (double) vec17 * vecX + (double) (f34 * offset);
                double spawnY = this.getY();
                spawnZ = this.getZ() + (double) vec17 * vecZ + (double) (f124 * offset);
                TheObliteratorServant entity = this;
                Vec3 entityPosition = this.position();
                CameraShakeEntity.cameraShake(this.level(), entityPosition, 30.0F, 0.25F, 10, 5);
                AnnihilationBeamEntity energyBeamEntity = new AnnihilationBeamEntity(ModEntities.ANNIHILATION_BEAM.get(),
                        entity.level(), entity, spawnX, this.getY() + 2.0D, spawnZ,
                        (float) ((entity.yHeadRot + 90.0F) * Math.PI / 180),
                        (float) (-entity.getXRot() * Math.PI / 180),
                        uniformDuration, 12.0F, 5.0F, 1, false, 0.0F, 0.0F, 0.0F, false, 30.0F);
                entity.level().addFreshEntity(energyBeamEntity);
            }
        }
        if (this.getAttackState() == 44 && this.attackTicks == 10 && this.targetIsNotNull()) {
            float f35 = Mth.cos(this.target().yBodyRot * ((float) Math.PI / 180));
            float f125 = Mth.sin(this.target().yBodyRot * ((float) Math.PI / 180));
            double theta31 = this.target().yBodyRot * (Math.PI / 180);
            vecX = Math.cos(theta31 += 1.5707963267948966);
            vecZ = Math.sin(theta31);
            float vec18 = -6.0F;
            offset = 0.0F;
            if (!this.level().isClientSide) {
                this.teleport(this.target().getX() + (double) vec18 * vecX + (double) (f35 * offset), this.target().getY(),
                        this.target().getZ() + (double) vec18 * vecZ + (double) (f125 * offset));
            }
        }
        if (this.getAttackState() == 21) {
            if (this.attackTicks == 11) {
                this.playSound(ModSounds.ANNIHILATION_LASER_CHARGE.get(), 3.0F, 1.5F);
            }
            if (this.attackTicks >= 21 && this.attackTicks <= 39) {
                this.attractParticles(ModParticles.SMALL_ANNIHILATION_FLAME.get(), 2, 5, 0.0F, 0.0F, 5.0F, 3.0F, 0.075F);
            }
            if (this.attackTicks == 39) {
                this.playSound(ModSounds.ANNIHILATION_LASER_SINGLE_SHOOT.get(), 3.0F, 1.0F);
                float f36 = Mth.cos(this.yBodyRot * ((float) Math.PI / 180));
                float f126 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180));
                double theta32 = this.yBodyRot * (Math.PI / 180);
                vecX = Math.cos(theta32 += 1.5707963267948966);
                vecZ = Math.sin(theta32);
                float vec19 = 2.0F;
                offset = 0.0F;
                double spawnX = this.getX() + (double) vec19 * vecX + (double) (f36 * offset);
                double spawnY = this.getY();
                spawnZ = this.getZ() + (double) vec19 * vecZ + (double) (f126 * offset);
                TheObliteratorServant entity = this;
                Vec3 entityPosition = this.position();
                CameraShakeEntity.cameraShake(this.level(), entityPosition, 30.0F, 0.25F, 10, 5);
                AnnihilationBeamEntity energyBeamEntity = new AnnihilationBeamEntity(ModEntities.ANNIHILATION_BEAM.get(),
                        entity.level(), entity, spawnX, this.getY() + 2.0D, spawnZ,
                        (float) ((entity.yHeadRot + 90.0F) * Math.PI / 180),
                        (float) (-entity.getXRot() * Math.PI / 180),
                        uniformDuration, singleShotLaserDamage, 5.0F, 1, false, 0.0F, 0.0F, 0.0F, false, 30.0F);
                entity.level().addFreshEntity(energyBeamEntity);
            }
        }
        if (this.getAttackState() == 46) {
            if (this.attackTicks == 1) {
                this.QuadLaserShineUp.resetTimer();
            }
            if (this.attackTicks >= 1 && this.attackTicks < 46) {
                this.QuadLaserShineUp.increaseTimer();
            }
            if (this.attackTicks == 1) {
                this.renderProgress = 0;
            }
            LivingEntity target = this.getTarget();
            if (this.attackTicks > 36) {
                this.controlledAnim.increaseTimer();
            }
            if (this.attackTicks > 12) {
                if (this.getIsQuadBeamRight()) {
                    ++this.renderProgress;
                }
                if (!this.getIsQuadBeamRight()) {
                    --this.renderProgress;
                }
            }
            if (this.attackTicks == 9) {
                DynamicCameraZoomEntity.dynamicCameraZoom(this.level(), this.position(), 50.0F, 4.0F, 40, 40, 5.0F, false, this);
                this.playSound(ModSounds.ANNIHILATION_LASER_CHARGE.get(), 3.0F, 1.0F);
                if (target != null) {
                    float f37 = Mth.cos(target.yHeadRot * ((float) Math.PI / 180));
                    float f127 = Mth.sin(target.yHeadRot * ((float) Math.PI / 180));
                    double theta33 = target.yHeadRot * (Math.PI / 180);
                    vecX3 = Math.cos(theta33 += 1.5707963267948966);
                    double vecZ7 = Math.sin(theta33);
                    vec = 3.0F;
                    float uniformOffset = 5.0F;
                    float offset23 = this.getRandom().nextInt() * 100 < 50 ? -uniformOffset : uniformOffset;
                    this.teleport(target.getX() + (double) vec * vecX3 + (double) (f37 * offset23), target.getY(),
                            target.getZ() + (double) vec * vecZ7 + (double) (f127 * offset23));
                }
            }
            if (this.attackTicks > 10 && this.attackTicks < 30) {
                int ucap = 5;
                this.attractParticles(this.getIsQuadBeamRight() ? ModParticles.BIG_ANNIHILATION_FLAME.get()
                        : ModParticles.SMALL_ANNIHILATION_FLAME.get(), ucap, 4, 0.0F, 0.0F, 5.0F, 3.0F, 0.075F);
                this.attractParticles(this.getIsQuadBeamRight() ? ModParticles.BIG_ANNIHILATION_FLAME.get()
                        : ModParticles.SMALL_ANNIHILATION_FLAME.get(), ucap, 4, 0.0F, 0.0F, 3.0F, 3.0F, 0.075F);
                this.attractParticles(this.getIsQuadBeamRight() ? ModParticles.BIG_ANNIHILATION_FLAME.get()
                        : ModParticles.SMALL_ANNIHILATION_FLAME.get(), ucap, 4, 0.0F, 0.0F, 2.0F, 3.0F, 0.075F);
            }
            if (this.attackTicks == 6 || this.attackTicks == 16 || this.attackTicks == 26 || this.attackTicks == 36) {
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(new Circle.RingData(0.0F, 1.5707964F, 20, 0.0F, 1.0F, 0.0F, 1.0F, 100.0F,
                            false, this.getIsQuadBeamRight() ? Circle.EnumRingBehavior.GROW : Circle.EnumRingBehavior.SHRINK),
                            this.getX(), this.getY() + 0.5D, this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
            }
            if (this.attackTicks == 46 || this.attackTicks == 56 || this.attackTicks == 66 || this.attackTicks == 76 || this.attackTicks == 86) {
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(new Circle.RingData(0.0F, 1.5707964F, 20, 0.0F, 1.0F, 0.0F, 1.0F, 100.0F,
                            true, Circle.EnumRingBehavior.SHRINK),
                            this.getX(), this.getY() + 2.0D, this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
            }
            if (this.attackTicks >= 46 && this.attackTicks <= 52) {
                this.Sphereparticle(ModParticles.SMALL_ANNIHILATION_FLAME.get(), 0.35F, 2.0F, 3.0F);
            }
            if (this.attackTicks == 46) {
                TheObliteratorServant entity2 = this;
                double targetSpeedValue = this.targetIsNotNull() && this.target().getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED)
                        ? this.target().getAttribute(Attributes.MOVEMENT_SPEED).getValue() * 5.0D : 0.0D;
                float uniformPrecentage = 0.5F;
                float uniformSpeed = (float) (2.6F + targetSpeedValue);
                float uniformDamage = 7.5F;
                this.playSound(ModSounds.QUAD_ANNIHILATION_LASER_SHOOT.get(), 3.0F, 1.0F);
                Vec3 entityPosition = this.position();
                CameraShakeEntity.cameraShake(this.level(), entityPosition, 30.0F, 0.15F, 40, 10);
                AnnihilationBeamEntity energyBeamEntity1 = new AnnihilationBeamEntity(ModEntities.ANNIHILATION_BEAM.get(),
                        entity2.level(), entity2, this.getX(), this.getY() + 2.0D, this.getZ(),
                        (float) ((entity2.yHeadRot + 90.0F) * Math.PI / 180),
                        (float) (-entity2.getXRot() * Math.PI / 180),
                        50, uniformDamage, 5.0F, 1, true, uniformSpeed, 90.0F, uniformPrecentage,
                        this.getIsQuadBeamRight(), 30.0F);
                entity2.level().addFreshEntity(energyBeamEntity1);
                AnnihilationBeamEntity energyBeamEntity2 = new AnnihilationBeamEntity(ModEntities.ANNIHILATION_BEAM.get(),
                        entity2.level(), entity2, this.getX(), this.getY() + 2.0D, this.getZ(),
                        (float) ((entity2.yHeadRot + 90.0F) * Math.PI / 180),
                        (float) (-entity2.getXRot() * Math.PI / 180),
                        50, uniformDamage, 5.0F, 1, true, uniformSpeed, 0.0F, uniformPrecentage,
                        this.getIsQuadBeamRight(), 30.0F);
                entity2.level().addFreshEntity(energyBeamEntity2);
                AnnihilationBeamEntity energyBeamEntity3 = new AnnihilationBeamEntity(ModEntities.ANNIHILATION_BEAM.get(),
                        entity2.level(), entity2, this.getX(), this.getY() + 2.0D, this.getZ(),
                        (float) ((entity2.yHeadRot + 90.0F) * Math.PI / 180),
                        (float) (-entity2.getXRot() * Math.PI / 180),
                        50, uniformDamage, 5.0F, 1, true, uniformSpeed, 180.0F, uniformPrecentage,
                        this.getIsQuadBeamRight(), 30.0F);
                entity2.level().addFreshEntity(energyBeamEntity3);
                AnnihilationBeamEntity energyBeamEntity4 = new AnnihilationBeamEntity(ModEntities.ANNIHILATION_BEAM.get(),
                        entity2.level(), entity2, this.getX(), this.getY() + 2.0D, this.getZ(),
                        (float) ((entity2.yHeadRot + 90.0F) * Math.PI / 180),
                        (float) (-entity2.getXRot() * Math.PI / 180),
                        50, uniformDamage, 5.0F, 1, true, uniformSpeed, 270.0F, uniformPrecentage,
                        this.getIsQuadBeamRight(), 30.0F);
                entity2.level().addFreshEntity(energyBeamEntity4);
            }
        }
        if (this.getAttackState() == 48) {
            if (this.attackTicks == 30) {
                this.sendAdvancedHotBarMessage("legendary_monsters.message.obliterator_p3_1_message", ChatFormatting.AQUA, 10.0F);
            }
            if (this.attackTicks == 93) {
                this.sendAdvancedHotBarMessage("legendary_monsters.message.obliterator_p3_2_message", ChatFormatting.AQUA, 10.0F);
            }
            if (this.attackTicks >= 30 && this.attackTicks <= 32 || this.attackTicks >= 58 && this.attackTicks <= 60) {
                float f38 = Mth.cos((float) (this.yBodyRot * ((float) Math.PI / 180F)));
                float f128 = Mth.sin((float) (this.yBodyRot * ((float) Math.PI / 180F)));
                double theta34 = (double) this.yBodyRot * (Math.PI / 180);
                double vecX12 = Math.cos(theta34 += 1.5707963267948966);
                double vecZ8 = Math.sin(theta34);
                float vec20 = 2.0F;
                boolean ticks = this.attackTicks >= 30 && this.attackTicks <= 34;
                float offset24 = ticks ? 3.0F : -3.0F;
                float f35 = (this.random.nextFloat() - 0.0F) * 0.5F;
                float f42 = (this.random.nextFloat() - 0.0F) * 0.5F;
                float f54 = (this.random.nextFloat() - 0.0F) * 0.5F;
                this.SideAreaAttack(5.0F, 5.0F, 190.0F, this.attackTicks >= 30 && this.attackTicks <= 32 ? 90.0F : -90.0F, 0.0F, 8.0F, 8.0F, 0, false, false, SoundEvents.EMPTY, 1.0F);
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ModParticles.ANNIHILATION_EXPLOSION.get(), this.getX() + (double) vec20 * vecX12 + (double) (f38 * offset24) + (double) f35, this.getY() + 3.0 + (double) f42, this.getZ() + (double) vec20 * vecZ8 + (double) (f128 * offset24) + (double) f54, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                    serverLevel.sendParticles(ModParticles.ANNIHILATION_EXPLOSION.get(), this.getX() + (double) vec20 * vecX12 + (double) (f38 * offset24) + (double) f35, this.getY() + 1.0 + (double) f42, this.getZ() + (double) vec20 * vecZ8 + (double) (f128 * offset24) + (double) f54, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
            }
            if (this.attackTicks == 30 || this.attackTicks == 58) {
                float f39 = Mth.cos((float) (this.yBodyRot * ((float) Math.PI / 180F)));
                float f129 = Mth.sin((float) (this.yBodyRot * ((float) Math.PI / 180F)));
                double theta35 = (double) this.yBodyRot * (Math.PI / 180);
                double vecX13 = Math.cos(theta35 += 1.5707963267948966);
                double vecZ9 = Math.sin(theta35);
                float vec21 = 2.0F;
                float offset25 = this.attackTicks == 30 ? 3.0F : -3.0F;
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ModParticles.ANNIHILATION_EXPLOSION.get(), this.getX() + (double) vec21 * vecX13 + (double) (f39 * offset25), this.getY() + 3.0, this.getZ() + (double) vec21 * vecZ9 + (double) (f129 * offset25), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
                this.playSound(ModSounds.OBLITERATOR_ARM_SHOOT.get(), 3.0F, 1.0F);
            }
            if (this.attackTicks == 80) {
                this.spawnCircleParticle(0.0F, 0.0F, 100.0F, false, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, Circle.EnumRingBehavior.SHRINK, 20);
            }
            if (this.attackTicks == 93) {
                this.spawnCircleParticle(0.0F, 0.0F, 100.0F, false, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F);
                this.doFlamesEffect(5.0, 2.0F, 8, true, ModParticles.ANNIHILATION_NUKE.get(), 4.0);
                this.playSound(ModSounds.OMINOUS_EXPLOSION.get(), 3.0F, 1.0F);
                this.playSound(ModSounds.FLAME_BURST.get(), 3.0F, 1.0F);
            }
            if (this.attackTicks == 96) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 10.0F, 0.1F, 0, 10);
                this.createSweep(3.0F, -2.0F, -5.0F, false, 1.0F);
                this.createSweep(3.0F, 2.0F, -5.0F, true, 1.0F);
                this.SideAreaAttack(4.0F, 4.0F, 160.0F, 90.0F, stunAttackDamage - removeMeleeDamage, 6.0F, 120, true, true, ModSounds.THE_OBLITERATOR_STUN.get(), 1.0F);
                this.SideAreaAttack(4.0F, 4.0F, 160.0F, -90.0F, stunAttackDamage - removeMeleeDamage, 6.0F, 120, true, true, ModSounds.THE_OBLITERATOR_STUN.get(), 1.0F);
                this.SideAreaAttack(4.0F, 4.0F, 180.0F, 0.0F, 1.25F, stunAttackDamage - removeMeleeDamage, 6.0F, 100, true, true, ModSounds.THE_OBLITERATOR_STUN.get(), 1.0F);
            }
        }
        if (this.getAttackState() == 53) {
            int i;
            if (this.attackTicks == 8) {
                this.sendAdvancedHotBarMessage("legendary_monsters.message.obliterator_ultimate", ChatFormatting.AQUA, 10.0F);
            }
            if (this.attackTicks < 50) {
                for (int k4 = 0; k4 < 3; ++k4) {
                    float d1 = Mth.sqrt((float) k4);
                    float ran = 0.4F;
                    float r = 0.0F;
                    float g6 = 0.7647059F + this.random.nextFloat() * ran;
                    float b = 0.0F;
                    this.level().addParticle(new MovingTrailParticle.TrailData(r, g6, b, 0.2F, 0.1F), this.getX(), this.getY(), this.getZ(), (double) Mth.sin((float) k4), 0.0D, (double) (d1 * 0.01F));
                }
            }
            if (this.attackTicks == 10) {
                this.level().addParticle(new Circle.RingData(0.0F, 1.5707964F, 20, 0.0F, 1.0F, 0.0F, 1.0F, 150.0F, false, Circle.EnumRingBehavior.SHRINK), this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
            }
            if (this.attackTicks == 20) {
                this.level().addParticle(new Circle.RingData(0.0F, 1.5707964F, 20, 0.0F, 1.0F, 0.0F, 1.0F, 150.0F, false, Circle.EnumRingBehavior.SHRINK), this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
            }
            if (this.attackTicks == 30) {
                this.level().addParticle(new Circle.RingData(0.0F, 1.5707964F, 20, 0.0F, 1.0F, 0.0F, 1.0F, 150.0F, false, Circle.EnumRingBehavior.SHRINK), this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
            }
            if (this.attackTicks == 2) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.15F, 20, 6);
            }
            if (this.attackTicks == 15) {
                AnnihilationPortalEntity annihilationPortalEntity = new AnnihilationPortalEntity(this.level(), this.getX(), this.getY(), this.getZ(), 0.0F, 12, this, 50, 20.0F, true, 6.0F);
                this.level().addFreshEntity(annihilationPortalEntity);
            }
            if (this.attackTicks == 21) {
                DynamicCameraZoomEntity.dynamicCameraZoom(this.level(), this.position(), 50.0F, 4.0F, 40, 40, 5.0F, true, this);
                this.doPortalEffect(15.0, 2.0F, 5, 7, 3.5F, 50, 15.0F);
                this.doPortalEffect(8.0, 2.0F, 5, 7, 3.5F, 50, 15.0F);
            }
            if (this.attackTicks > 2 && this.attackTicks < 21) {
                for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(7.0))) {
                    if (!entity.onGround() || entity == this || entity instanceof Player player && player.getAbilities().invulnerable) continue;
                    Vec3 diff = entity.position().subtract(this.position().add(0.0, 0.0, 0.0));
                    diff = diff.normalize().scale(0.015);
                    entity.setDeltaMovement(entity.getDeltaMovement().subtract(diff));
                    EntityUtil.applyPlayerDeltaMovement(entity);
                }
            }
            if (this.attackTicks == 48) {
                this.doFlamesEffect(5.0, 2.0F, 6, true, ModParticles.ANNIHILATION_NUKE.get(), 4.0);
                this.Sphereparticle(ModParticles.SMALL_ANNIHILATION_FLAME.get(), 3.0F, 2.0F, 3.0F);
                this.SideAreaAttack(6.0F, 4.0F, 380.0F, 0.0F, 0.0F, 12.0F, 8.0F, 120, false, false, ModSounds.FLAME_BURST.get(), 1.0F);
                CameraShakeEntity.cameraShake(this.level(), this.position(), 30.0F, 0.2F, 10, 5);
                this.createCircularLightningParticle(3.5F, 0.0F, 5, 2.5F);
                this.playSound(ModSounds.ULTIMATE_FLAME_IMPACT.get(), 3.0F, 1.0F);
                for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(25.0))) {
                    if (!entity.onGround() || entity == this || entity instanceof Player player && player.getAbilities().invulnerable) continue;
                    entity.setDeltaMovement(0.0, (double) 0.35F, 0.0);
                    EntityUtil.applyPlayerDeltaMovement(entity);
                }
            }
            for (i = 60; i <= 78; i += 2) {
                if (this.attackTicks != i) continue;
                int d = i - 58;
                this.flameRadagonShockwave(2.0F, d, 1.0F, 2, 0.0F, 2.0F, 17.0F, true);
            }
            for (i = 80; i <= 98; i += 2) {
                if (this.attackTicks != i) continue;
                int d = i - 78;
                this.flameRadagonShockwave(2.0F, d, 1.0F, 2, 0.0F, 2.0F, 17.0F, false);
            }
            if (this.attackTicks == 80) {
                this.SideAreaAttack(6.0F, 4.0F, 380.0F, 0.0F, 0.0F, 12.0F, 8.0F, 120, false, false, ModSounds.FLAME_BURST.get(), 1.0F);
                CameraShakeEntity.cameraShake(this.level(), this.position(), 30.0F, 0.15F, 20, 5);
                this.playSound(ModSounds.OMINOUS_EXPLOSION.get(), 3.0F, 1.0F);
                this.createCircularLightningParticle(3.5F, 0.0F, 5, 2.5F);
            }
            if (this.attackTicks == 105 && this.targetIsNotNull()) {
                float f41 = Mth.cos((float) (this.target().yBodyRot * ((float) Math.PI / 180F)));
                float f130 = Mth.sin((float) (this.target().yBodyRot * ((float) Math.PI / 180F)));
                double theta36 = (double) this.target().yBodyRot * (Math.PI / 180);
                double vecX14 = Math.cos(theta36 += 1.5707963267948966);
                double vecZ10 = Math.sin(theta36);
                float vec22 = -4.0F;
                float offset26 = 0.0F;
                if (!this.level().isClientSide) {
                    this.teleport(this.target().getX() + (double) vec22 * vecX14 + (double) (f41 * offset26), this.target().getY(), this.target().getZ() + (double) vec22 * vecZ10 + (double) (f130 * offset26));
                }
            }
        }
        if (this.getAttackState() == 52) {
            LivingEntity target = this.getTarget();
            if (this.attackTicks == 13 && target != null) {
                float f42 = Mth.cos((float) (target.yBodyRot * ((float) Math.PI / 180F)));
                float f131 = Mth.sin((float) (target.yBodyRot * ((float) Math.PI / 180F)));
                double theta37 = (double) target.yBodyRot * (Math.PI / 180);
                double vecX15 = Math.cos(theta37 += 1.5707963267948966);
                double vecZ11 = Math.sin(theta37);
                float vec23 = -4.0F;
                float offset27 = 0.0F;
                this.teleport(target.getX() + (double) vec23 * vecX15 + (double) (f42 * offset27), this.getY(), target.getZ() + (double) vec23 * vecZ11 + (double) (f131 * offset27));
            }
        }
        if (this.getAttackState() == 1) {
            if (this.attackTicks == 15) {
                this.playSound(SoundEvents.SHULKER_TELEPORT, 1.0F, 1.0F);
            }
            if (this.attackTicks > 82 && this.attackTicks < 114 && this.tickCount % 3 == 0) {
                this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
            }
        }
        if (this.getAttackState() == 60) {
            if (this.attackTicks == 5) {
                this.teleportRandomly(this, 7.0F, 10.0F);
            }
            if (this.attackTicks > MathUtils.toTicks(2.17F) && this.tickCount % 10 == 0) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 10.0F, 0.1F, 0, 5);
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ModParticles.TELEPORT_EFFECT.get(), this.getX(), this.getY() + 3.0, this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
                this.playSound(SoundEvents.SHULKER_TELEPORT, 1.0F, 1.0F);
            }
            if (this.attackTicks == 15) {
                this.shootDeathProjectiles(15.0F, 2, 2.5F, false);
            }
            if (this.attackTicks == 25) {
                this.shootDeathProjectiles(35.0F, 2, 2.0F, false);
            }
            if (this.attackTicks == 35) {
                this.shootDeathProjectiles(90.0F, 2, 1.0F, false);
            }
            if (this.attackTicks == 100) {
                this.playSound(ModSounds.ENERGY_EXPLOSION.get(), 1.0F, 1.0F);
                this.shootDeathProjectiles(90.0F, 4, 1.0F, true);
            }
        }
    }

    private void Grab(float RangeXZ, float range, int brokenShieldTicks, float damage) {
        if (this.level().isClientSide) {
            return;
        }
        boolean hitAny = false;
        double rad = Math.toRadians(this.getYRot() + 90.0F);
        double xRange = (double) range * Math.cos(rad);
        double zRange = (double) range * Math.sin(rad);
        AABB attackRange = this.getBoundingBox().inflate((double) RangeXZ, 4.0D, (double) RangeXZ).expandTowards(xRange, 0.0D, zRange);
        for (LivingEntity entityHit : this.level().getEntitiesOfClass(LivingEntity.class, attackRange)) {
            if (this.isAlliedTo(entityHit) || entityHit == this) continue;
            hitAny = true;
            boolean flag = entityHit.hurt(this.damageSources().mobAttack(this), (float) (((double) damage * ServantMath.opArmorNerf(entityHit, this.getAttackState()) + (double) ServantMath.entityBasedHpDamage(entityHit, 3.0F))));
            boolean entityHitisTarget = entityHit == this.target();
            boolean mounted = entityHitisTarget && entityHit.startRiding(this, true);
            if (flag && this.getAttackState() == 17) {
                EntityUtil.cancelBuffs(entityHit);
                double x = entityHit.getX() - this.getX();
                double z = entityHit.getZ() - this.getZ();
                double d = Math.sqrt(x * x + z * z);
                entityHit.setDeltaMovement(x / d * 0.8, 1.25, z / d * 0.8);
                EntityUtil.applyPlayerDeltaMovement(entityHit);
            }
            this.succedGrabbing = flag && mounted;
            if (!(entityHit instanceof Player) || !entityHit.isBlocking() || brokenShieldTicks <= 0) break;
            IAnimatedMonsterServant.disableShield(entityHit, brokenShieldTicks);
            break;
        }
        if (!hitAny) {
            this.succedGrabbing = false;
        }
    }

    private void PreGrab(float RangeXZ, float y, float range, int brokenShieldTicks, float damage) {
        if (this.level().isClientSide) {
            return;
        }
        boolean hitAny = false;
        double rad = Math.toRadians(this.getYRot() + 90.0F);
        double xRange = (double) range * Math.cos(rad);
        double zRange = (double) range * Math.sin(rad);
        AABB attackRange = this.getBoundingBox().inflate((double) RangeXZ, (double) y, (double) RangeXZ).expandTowards(xRange, 0.0D, zRange);
        for (LivingEntity entityHit : this.level().getEntitiesOfClass(LivingEntity.class, attackRange)) {
            if (this.isAlliedTo(entityHit) || entityHit == this) continue;
            hitAny = true;
            boolean flag = entityHit.hurt(this.damageSources().mobAttack(this), (float) (((double) damage * ServantMath.opArmorNerf(entityHit, this.getAttackState()) + (double) ServantMath.entityBasedHpDamage(entityHit, 3.0F))));
            if (flag && (this.getAttackState() == 17 || this.getAttackState() == 47)) {
                double x = entityHit.getX() - this.getX();
                double z = entityHit.getZ() - this.getZ();
                double d = Math.sqrt(x * x + z * z);
                entityHit.setDeltaMovement(x / d * 0.8, 1.25, z / d * 0.8);
                EntityUtil.applyPlayerDeltaMovement(entityHit);
            }
            if (flag) {
                EntityUtil.cancelBuffs(entityHit);
                TheObliteratorUtils.applyAnnihilationEffect(entityHit, ModEffects.ANNIHILATION.get(), 1, true);
                this.succedGrabbing = true;
            } else {
                this.succedGrabbing = false;
            }
            if (!(entityHit instanceof Player) || !entityHit.isBlocking() || brokenShieldTicks <= 0) break;
            IAnimatedMonsterServant.disableShield(entityHit, brokenShieldTicks);
            break;
        }
        if (!hitAny) {
            this.succedGrabbing = false;
        }
    }

    private void StraightLineAreaAttack(float boxWidth, float range, int brokenShieldTicks, float damage, float precentage, boolean launch) {
        double rad = Math.toRadians(this.getYRot() + 90.0F);
        double xRange = (double) range * Math.cos(rad);
        double zRange = (double) range * Math.sin(rad);
        AABB attackRange = this.getBoundingBox().inflate((double) boxWidth, 4.0D, (double) boxWidth).expandTowards(xRange, 0.0D, zRange);
        for (LivingEntity entityHit : this.level().getEntitiesOfClass(LivingEntity.class, attackRange)) {
            if (this.isAlliedTo(entityHit) || entityHit == this) continue;
            boolean flag = entityHit.hurt(this.damageSources().mobAttack(this), (float) (((double) damage + ServantMath.opArmorNerf(entityHit, this.getAttackState()) + (double) ServantMath.entityBasedHpDamage(entityHit, precentage))));
            if (flag) {
                TheObliteratorUtils.applyAnnihilationEffect(entityHit, ModEffects.ANNIHILATION.get(), 1, true);
            }
            if (flag && this.getAttackState() == 17) {
                this.succedGrabbing = true;
                double x = entityHit.getX() - this.getX();
                double z = entityHit.getZ() - this.getZ();
                double d = Math.sqrt(x * x + z * z);
                entityHit.setDeltaMovement(x / d * 0.8, 1.25, z / d * 0.8);
                EntityUtil.applyPlayerDeltaMovement(entityHit);
            } else {
                this.succedGrabbing = false;
            }
            if (flag && launch) {
                this.launch(entityHit, true);
            }
            if (!(entityHit instanceof Player) || !entityHit.isBlocking() || brokenShieldTicks <= 0) continue;
            IAnimatedMonsterServant.disableShield(entityHit, brokenShieldTicks);
        }
    }

    private void StraightLineAreaAttack(float boxWidth, double inflateY, float range, int brokenShieldTicks, float damage, float precentage, boolean launch, SoundEvent soundEvent) {
        double rad = Math.toRadians(this.getYRot() + 90.0F);
        double xRange = (double) range * Math.cos(rad);
        double zRange = (double) range * Math.sin(rad);
        AABB attackRange = this.getBoundingBox().inflate((double) boxWidth, inflateY, (double) boxWidth).expandTowards(xRange, 0.0D, zRange);
        for (LivingEntity entityHit : this.level().getEntitiesOfClass(LivingEntity.class, attackRange)) {
            if (this.isAlliedTo(entityHit) || entityHit == this) continue;
            boolean flag = entityHit.hurt(this.damageSources().mobAttack(this), (float) (((double) damage + ServantMath.opArmorNerf(entityHit, this.getAttackState()) + (double) ServantMath.entityBasedHpDamage(entityHit, precentage))));
            if (flag) {
                TheObliteratorUtils.applyAnnihilationEffect(entityHit, ModEffects.ANNIHILATION.get(), 1, true);
            }
            if (flag && this.getAttackState() == 17) {
                this.succedGrabbing = true;
                double x = entityHit.getX() - this.getX();
                double z = entityHit.getZ() - this.getZ();
                double d = Math.sqrt(x * x + z * z);
                entityHit.setDeltaMovement(x / d * 0.8, 1.25, z / d * 0.8);
                EntityUtil.applyPlayerDeltaMovement(entityHit);
            } else {
                this.succedGrabbing = false;
            }
            if (flag && launch) {
                EntityUtil.cancelBuffs(entityHit);
                this.launch(entityHit, true);
            }
            if (this.targetIsNotNull() && entityHit == this.target()) {
                this.playSound(soundEvent, 1.0F, 1.0F);
            }
            if (!(entityHit instanceof Player) || !entityHit.isBlocking() || brokenShieldTicks <= 0) continue;
            IAnimatedMonsterServant.disableShield(entityHit, brokenShieldTicks);
        }
    }

    public void AreaAttack(float range, float height, float arc, float damage, float precentage, int brokenShieldTicks, boolean canStun, boolean canlaunch) {
        List<LivingEntity> entitiesHit = this.getEntityLivingBaseNearby(range, height, range, range);
        for (LivingEntity entityHit : entitiesHit) {
            float entityHitAngle = (float) ((Math.atan2(entityHit.getZ() - this.getZ(), entityHit.getX() - this.getX()) * 57.29577951308232 - 90.0) % 360.0);
            float entityAttackingAngle = this.yBodyRot % 360.0F;
            if (entityHitAngle < 0.0F) {
                entityHitAngle += 360.0F;
            }
            if (entityAttackingAngle < 0.0F) {
                entityAttackingAngle += 360.0F;
            }
            float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
            float entityHitDistance = (float) Math.sqrt((entityHit.getZ() - this.getZ()) * (entityHit.getZ() - this.getZ()) + (entityHit.getX() - this.getX()) * (entityHit.getX() - this.getX()));
            if (!(entityHitDistance <= range && entityRelativeAngle <= arc / 2.0F && entityRelativeAngle >= -arc / 2.0F || entityRelativeAngle >= 360.0F - arc / 2.0F) && !(entityRelativeAngle <= -360.0F + arc / 2.0F) || this.isAlliedTo(entityHit) || entityHit instanceof TheWarpedOneOld || entityHit == this) continue;
            if (this.targetIsNotNull() && entityHit == this.target() && canStun) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.15F, 0, 20);
            }
            boolean canPlaySound = this.getAttackState() != 22;
            boolean flag = entityHit.hurt(this.getAttackState() == 14 || this.getAttackState() == 20 || this.getAttackState() == 24 ? ModDamageTypes.causeAnnihilationDamage(this, this) : this.damageSources().mobAttack(this), (float) (((double) damage * ServantMath.opArmorNerf(entityHit, this.getAttackState()) + (double) ServantMath.entityBasedHpDamage(entityHit, precentage))));
            if (this.getAttackState() == 14) {
                this.swapPositions(flag && this.targetIsNotNull());
            }
            if (flag) {
                EntityUtil.cancelBuffs(entityHit);
                TheObliteratorUtils.applyAnnihilationEffect(entityHit, ModEffects.ANNIHILATION.get(), 1, true);
                if (canlaunch) {
                    if (this.getAttackState() == 8) {
                        this.launch(entityHit, true, 1.5F, 0.25F);
                    } else {
                        this.launch(entityHit, true, 2.0F, 0.5F);
                    }
                }
                if (!canStun && canPlaySound) {
                    this.playSound(ModSounds.POSESSED_PALADIN_ATTACK3.get(), 1.0F, 0.5F);
                }
                if (canStun) {
                    this.playSound(ModSounds.THE_OBLITERATOR_STUN.get(), 1.0F, 1.0F);
                    entityHit.addEffect(new MobEffectInstance(ModEffects.STUN.get(), this.getStunDuration(), 1));
                }
            }
            if (!(entityHit instanceof Player) || !entityHit.isBlocking() || brokenShieldTicks <= 0) continue;
            IAnimatedMonsterServant.disableShield(entityHit, brokenShieldTicks);
        }
    }

    public void SideAreaAttack(float range, float height, float arc, float boxOffset, float damage, float precentage, int brokenShieldTicks, boolean canStun, boolean canlaunch, SoundEvent soundEvent, float pitch) {
        List<LivingEntity> entitiesHit = this.getEntityLivingBaseNearby(range, height, range, range);
        for (LivingEntity entityHit : entitiesHit) {
            float entityHitAngle = (float) ((Math.atan2(entityHit.getZ() - this.getZ(), entityHit.getX() - this.getX()) * 57.29577951308232 - 90.0) % 360.0);
            float entityAttackingAngle = (this.yBodyRot - boxOffset) % 360.0F;
            if (entityHitAngle < 0.0F) {
                entityHitAngle += 360.0F;
            }
            if (entityAttackingAngle < 0.0F) {
                entityAttackingAngle += 360.0F;
            }
            float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
            float entityHitDistance = (float) Math.sqrt((entityHit.getZ() - this.getZ()) * (entityHit.getZ() - this.getZ()) + (entityHit.getX() - this.getX()) * (entityHit.getX() - this.getX()));
            if (!(entityHitDistance <= range && entityRelativeAngle <= arc / 2.0F && entityRelativeAngle >= -arc / 2.0F || entityRelativeAngle >= 360.0F - arc / 2.0F) && !(entityRelativeAngle <= -360.0F + arc / 2.0F) || this.isAlliedTo(entityHit) || entityHit instanceof TheObliteratorServant || entityHit == this) continue;
            boolean flag = entityHit.hurt(this.getAttackState() == 14 || this.getAttackState() == 40 || this.getAttackState() == 50 ? ModDamageTypes.causeAnnihilationDamage(this, this) : this.damageSources().mobAttack(this), (float) (((double) damage * ServantMath.opArmorNerf(entityHit, this.getAttackState()) + (double) ServantMath.entityBasedHpDamage(entityHit, precentage))));
            if (flag) {
                EntityUtil.cancelBuffs(entityHit);
                TheObliteratorUtils.applyAnnihilationEffect(entityHit, ModEffects.ANNIHILATION.get(), 1, true);
                if (this.isHittingWithBlades() && this.getIsThirdPhase()) {
                    entityHit.setSecondsOnFire(3);
                }
                if (canlaunch) {
                    if (this.getAttackState() == 8) {
                        this.launch(entityHit, true, 1.5F, 0.25F);
                    } else if (this.getAttackState() != 22) {
                        this.launch(entityHit, true, 2.0F, 0.5F);
                    } else {
                        Vec3 getDeltaMovement = new Vec3(this.getDeltaMovement().x, this.getDeltaMovement().y, this.getDeltaMovement().z);
                        entityHit.push(getDeltaMovement.x * 1.1, 0.5, getDeltaMovement.z * 1.1);
                    }
                }
                this.playSound(soundEvent, 1.0F, pitch);
                if (canStun) {
                    entityHit.addEffect(new MobEffectInstance(ModEffects.STUN.get(), this.getStunDuration(), 1));
                }
            }
            if (!(entityHit instanceof Player) || !entityHit.isBlocking() || brokenShieldTicks <= 0) continue;
            IAnimatedMonsterServant.disableShield(entityHit, brokenShieldTicks);
        }
    }

    public void SideGrab(float range, float height, float arc, float boxOffset, float damage, int brokenShieldTicks, SoundEvent soundEvent, float pitch) {
        if (this.level().isClientSide) {
            return;
        }
        boolean hitAny = false;
        List<LivingEntity> entitiesHit = this.getEntityLivingBaseNearby(range, height, range, range);
        for (LivingEntity entityHit : entitiesHit) {
            float entityHitAngle = (float) ((Math.atan2(entityHit.getZ() - this.getZ(), entityHit.getX() - this.getX()) * 57.29577951308232 - 90.0) % 360.0);
            float entityAttackingAngle = (this.yBodyRot - boxOffset) % 360.0F;
            if (entityHitAngle < 0.0F) {
                entityHitAngle += 360.0F;
            }
            if (entityAttackingAngle < 0.0F) {
                entityAttackingAngle += 360.0F;
            }
            float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
            float entityHitDistance = (float) Math.sqrt((entityHit.getZ() - this.getZ()) * (entityHit.getZ() - this.getZ()) + (entityHit.getX() - this.getX()) * (entityHit.getX() - this.getX()));
            if (!(entityHitDistance <= range && entityRelativeAngle <= arc / 2.0F && entityRelativeAngle >= -arc / 2.0F || entityRelativeAngle >= 360.0F - arc / 2.0F) && !(entityRelativeAngle <= -360.0F + arc / 2.0F) || this.isAlliedTo(entityHit) || entityHit instanceof TheObliteratorServant || entityHit == this) continue;
            hitAny = true;
            boolean entityHitisTarget = entityHit == this.target();
            boolean mounted = entityHitisTarget && entityHit.startRiding(this, true);
            boolean flag = entityHit.hurt(this.damageSources().mobAttack(this), (float) (((double) damage * ServantMath.opArmorNerf(entityHit, this.getAttackState()) + (double) ServantMath.entityBasedHpDamage(entityHit, 3.0F))));
            if (flag && mounted) {
                EntityUtil.cancelBuffs(entityHit);
                entityHit.setShiftKeyDown(false);
                this.playSound(soundEvent, 1.0F, pitch);
                this.succedGrabbing = true;
            } else {
                this.succedGrabbing = false;
            }
            if (!(entityHit instanceof Player) || !entityHit.isBlocking() || brokenShieldTicks <= 0) continue;
            IAnimatedMonsterServant.disableShield(entityHit, brokenShieldTicks);
        }
        if (!hitAny) {
            this.succedGrabbing = false;
        }
    }

    public void SideAreaAttack(float range, float height, float arc, float boxOffset, float forwardOffset, float damage, float precentage, int brokenShieldTicks, boolean canStun, boolean canlaunch, SoundEvent soundEvent, float pitch) {
        double theta = Math.toRadians(this.yBodyRot) + 1.5707963267948966;
        double forwardX = Math.cos(theta) * (double) forwardOffset;
        double forwardZ = Math.sin(theta) * (double) forwardOffset;
        List<LivingEntity> entitiesHit = this.getEntityLivingBaseNearby(range, height, range, range);
        for (LivingEntity entityHit : entitiesHit) {
            double dx = entityHit.getX() - (this.getX() + forwardX);
            double dz = entityHit.getZ() - (this.getZ() + forwardZ);
            float entityHitAngle = (float) ((Math.toDegrees(Math.atan2(dz, dx)) - 90.0) % 360.0);
            if (entityHitAngle < 0.0F) {
                entityHitAngle += 360.0F;
            }
            float entityAttackingAngle = (this.yBodyRot - boxOffset) % 360.0F;
            if (entityAttackingAngle < 0.0F) {
                entityAttackingAngle += 360.0F;
            }
            float entityHitDistance = (float) Math.sqrt(dx * dx + dz * dz);
            float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
            if (!(entityHitDistance <= range) || !(entityRelativeAngle <= arc / 2.0F && entityRelativeAngle >= -arc / 2.0F || entityRelativeAngle >= 360.0F - arc / 2.0F) && !(entityRelativeAngle <= -360.0F + arc / 2.0F) || this.isAlliedTo(entityHit) || entityHit instanceof TheObliteratorServant || entityHit == this) continue;
            boolean flag = entityHit.hurt(this.getAttackState() == 14 || this.getAttackState() == 53 ? ModDamageTypes.causeAnnihilationDamage(this, this) : this.damageSources().mobAttack(this), (float) (((double) damage * ServantMath.opArmorNerf(entityHit, this.getAttackState()) + (double) ServantMath.entityBasedHpDamage(entityHit, precentage))));
            if (flag) {
                EntityUtil.cancelBuffs(entityHit);
                TheObliteratorUtils.applyAnnihilationEffect(entityHit, ModEffects.ANNIHILATION.get(), 1, true);
                if (this.isHittingWithBlades() && this.getIsThirdPhase()) {
                    entityHit.setSecondsOnFire(3);
                }
                if (canlaunch) {
                    if (this.getAttackState() == 8) {
                        this.launch(entityHit, true, 1.5F, 0.25F);
                    } else {
                        this.launch(entityHit, true, 2.0F, 0.5F);
                    }
                }
                this.playSound(soundEvent, 1.0F, pitch);
                if (canStun) {
                    entityHit.addEffect(new MobEffectInstance(ModEffects.STUN.get(), this.getStunDuration(), 1));
                }
            }
            if (!(entityHit instanceof Player) || !entityHit.isBlocking() || brokenShieldTicks <= 0) continue;
            IAnimatedMonsterServant.disableShield(entityHit, brokenShieldTicks);
        }
    }

    public void shootAnnihilationBomb(float velocity, float x, float y, float z) {
        if (this.targetIsNotNull()) {
            AnnihilationBomb chorusBomb = new AnnihilationBomb(LmEntityRegistry.ANNIHILATION_BOMB.get(), this.level(), this, 10.0F, 16, false);
            chorusBomb.setPosRaw(x, y, z);
            double d0 = this.target().getX() - (double) x;
            double d4 = (this.target().getY() - (double) y) * 0.5;
            double d1 = this.target().getBoundingBox().minY + (double) (this.target().getBbHeight() / 2.0F) - chorusBomb.getY();
            double d2 = this.target().getZ() - (double) z;
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            chorusBomb.shoot(d0, d1 + d3 * 0.2, d2, velocity, 14 - this.level().getDifficulty().getId() * 4);
            chorusBomb.setOwner(this);
            this.level().addFreshEntity(chorusBomb);
        }
    }

    public void shootAngledBombs(float velocity, double x, double y, double z, int bombCount, float angleBetween, float elevationAngle) {
        if (!this.targetIsNotNull()) {
            return;
        }
        double dx = this.target().getX() - x;
        double dz = this.target().getZ() - z;
        Vec3 flatDir = new Vec3(dx, 0.0, dz).normalize();
        double elevRad = Math.toRadians(elevationAngle);
        double totalSpread = angleBetween * (float) (bombCount - 1);
        double startYaw = -totalSpread * 0.5;
        for (int i = 0; i < bombCount; ++i) {
            double yawOffset = startYaw + (double) ((float) i * angleBetween);
            Vec3 dirYaw = this.rotateYaw(flatDir, yawOffset);
            double cosP = Math.cos(elevRad);
            double sinP = Math.sin(elevRad);
            Vec3 finalDir = new Vec3(dirYaw.x * cosP, sinP, dirYaw.z * cosP);
            SmallAnnihilationBomb bomb = new SmallAnnihilationBomb(this.level(), this, 3.0F);
            bomb.setTurnRate(0.0F);
            bomb.setPosRaw(x, y, z);
            bomb.shoot(finalDir.x, finalDir.y, finalDir.z, velocity, 0.0F);
            bomb.setOwner(this);
            this.level().addFreshEntity(bomb);
        }
    }

    private Vec3 rotateYaw(Vec3 vec, double angleDeg) {
        double rad = Math.toRadians(angleDeg);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);
        double x = vec.x * cos - vec.z * sin;
        double z = vec.x * sin + vec.z * cos;
        return new Vec3(x, vec.y, z);
    }

    private void Sphereparticle(ParticleOptions particleType, float height, float vec, float size) {
        if (this.level() instanceof ServerLevel serverLevel && this.tickCount % 2 == 0) {
            double d0 = this.getX();
            double d1 = this.getY() + (double) height;
            double d2 = this.getZ();
            double theta = (double) this.yBodyRot * (Math.PI / 180);
            double vecX = Math.cos(theta += 1.5707963267948966);
            double vecZ = Math.sin(theta);
            for (float i = -size; i <= size; i += 1.0F) {
                for (float j = -size; j <= size; j += 1.0F) {
                    for (float k = -size; k <= size; k += 1.0F) {
                        double d3 = (double) j + (this.random.nextDouble() - this.random.nextDouble()) * 0.5;
                        double d4 = (double) i + (this.random.nextDouble() - this.random.nextDouble()) * 0.5;
                        double d5 = (double) k + (this.random.nextDouble() - this.random.nextDouble()) * 0.5;
                        double d6 = (double) Mth.sqrt((float) (d3 * d3 + d4 * d4 + d5 * d5)) / 0.5 + this.random.nextGaussian() * 0.05;
                        serverLevel.sendParticles(particleType, d0 + (double) vec * vecX, d1, d2 + (double) vec * vecZ, 0, d3 / d6, d4 / d6, d5 / d6, 1.0D);
                        if (i == -size || i == size || j == -size || j == size) {
                            continue;
                        }
                        k += size * 2.0F - 1.0F;
                    }
                }
            }
        }
    }

    private void spawnFlames(double x, double z, double minY, double maxY, float rotation, int delay, float damage, boolean particle, ParticleOptions particleOptions) {
        BlockPos blockpos = new BlockPos((int) x, (int) maxY, (int) z);
        boolean flag = false;
        double d0 = 0.0;
        do {
            BlockPos blockpos1 = blockpos.below();
            BlockState blockstate = this.level().getBlockState(blockpos1);
            if (!blockstate.isFaceSturdy(this.level(), blockpos1, Direction.UP)) {
                continue;
            }
            if (!this.level().isEmptyBlock(blockpos)) {
                BlockState blockstate1 = this.level().getBlockState(blockpos);
                VoxelShape voxelshape = blockstate1.getCollisionShape(this.level(), blockpos);
                if (!voxelshape.isEmpty()) {
                    d0 = voxelshape.max(Direction.Axis.Y);
                }
            }
            flag = true;
            break;
        } while ((blockpos = blockpos.below()).getY() >= Mth.floor(minY) - 1);
        if (flag) {
            if (!particle) {
                if (this.getAttackState() != 53 && this.getAttackState() != 59) {
                    this.level().addFreshEntity(new AnnihilationFlameStrike(this.level(), x, (double) blockpos.getY() + d0, z, rotation, delay, this, 20, damage + 2.0F));
                } else {
                    this.level().addFreshEntity(new AnnihilationGroundNukeStrikeEntity(this.level(), x, (double) blockpos.getY() + d0, z, rotation, delay, this, 20, damage));
                }
            } else if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(new Circle.RingData(0.0F, 1.5707964F, 30, 0.0F, 1.0F, 0.0F, 1.0F, 30.0F, false, Circle.EnumRingBehavior.SHRINK), x, (double) blockpos.getY() + d0, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    public void shootPlasmaBall(int numberOfProjectiles, float spawnAngle, boolean turnLeft, float turnStrenght, float damage) {
        double theta = (double) this.yBodyRot * (Math.PI / 180);
        double vecX = Math.cos(theta += 1.5707963267948966);
        double vecZ = Math.sin(theta);
        int numberOfSkulls = numberOfProjectiles;
        float angleStep = spawnAngle;
        for (int i = 0; i < numberOfSkulls; ++i) {
            float angle = this.yBodyRot + (float) (i - numberOfSkulls / 2) * angleStep;
            float rad = (float) Math.toRadians(angle);
            double dx = -Math.sin(rad);
            double dz = Math.cos(rad);
            PlasmaOrbEntity witherskull = new PlasmaOrbEntity(this, dx, 0.0, dz, this.level(), 6.0F, angle, 20.0F);
            double spawnX = this.getX() + vecX * 1.0;
            double spawnY = this.getY(0.15);
            double spawnZ = this.getZ() + vecZ * 1.0;
            witherskull.setTurnLeft(turnLeft);
            witherskull.setTurnStrength(turnStrenght);
            witherskull.setPos(spawnX, spawnY, spawnZ);
            this.level().addFreshEntity(witherskull);
        }
    }

    public void shootAnnihilationGeysers(int numberOfProjectiles, float spawnAngle, boolean turnLeft, float turnStrenght, float damage, int life) {
        double theta = (double) this.yBodyRot * (Math.PI / 180);
        double vecX = Math.cos(theta += 1.5707963267948966);
        double vecZ = Math.sin(theta);
        int numberOfSkulls = numberOfProjectiles;
        float angleStep = spawnAngle;
        for (int i = 0; i < numberOfSkulls; ++i) {
            float angle = this.yBodyRot + (float) (i - numberOfSkulls / 2) * angleStep;
            float rad = (float) Math.toRadians(angle);
            double dx = -Math.sin(rad);
            double dz = Math.cos(rad);
            AnnihilationGeyserEntity witherskull = new AnnihilationGeyserEntity(this, dx, 0.0, dz, this.level(), damage, angle, life);
            double spawnX = this.getX() + vecX * 1.0;
            double spawnY = this.getY();
            double spawnZ = this.getZ() + vecZ * 1.0;
            witherskull.setTurnLeft(turnLeft);
            witherskull.setTurnStrength(turnStrenght);
            witherskull.setPos(spawnX, spawnY, spawnZ);
            this.level().addFreshEntity(witherskull);
        }
    }

    public void doSmashEffects(float circleVec, float circleOffset, SoundEvent soundEvent) {
        float f = Mth.cos((float)(this.yBodyRot * ((float)Math.PI / 180)));
        float f1 = Mth.sin((float)(this.yBodyRot * ((float)Math.PI / 180)));
        double theta = (double)this.yBodyRot * (Math.PI / 180);
        double vecX = Math.cos(theta += 1.5707963267948966);
        double vecZ = Math.sin(theta);
        float vec = circleVec;
        float offset = circleOffset;
        if (this.level() instanceof ServerLevel serverLevel) {
            float g = (float)Math.toRadians(-this.getYRot() + 180.0f);
            serverLevel.sendParticles(new Circle.RingData(g, 0.0f, 30, 0.0f, 1.0f, 0.0f, 1.0f, 60.0f, true, Circle.EnumRingBehavior.GROW_THEN_SHRINK), this.getX() + (double)vec * vecX + (double)(f * offset), this.getY(), this.getZ() + (double)vec * vecZ + (double)(f1 * offset), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
        this.spawnCircleParticle(2.0f, 0.0f, 80.0f, true, 7.0f, 0.0f, 1.0f, 0.0f, 1.0f);
        this.playSound(soundEvent, 1.0f, 1.0f);
    }

    public void createSweep(float pos, float posOffset, float yHeight, boolean reverse, float scale) {
        float f = Mth.cos((float)(this.yBodyRot * ((float)Math.PI / 180)));
        float f1 = Mth.sin((float)(this.yBodyRot * ((float)Math.PI / 180)));
        double theta = (double)this.yBodyRot * (Math.PI / 180);
        double vecX = Math.cos(theta += 1.5707963267948966);
        double vecZ = Math.sin(theta);
        float vec = pos;
        float offset = posOffset;
        double x = this.getX() + (double)vec * vecX + (double)(f * offset);
        double z = this.getZ() + (double)vec * vecZ + (double)(f1 * offset);
        if (this.level() instanceof ServerLevel serverLevel) {
            double d0 = x;
            double d1 = this.getY() + (double)(this.getBbHeight() / 2.0f) + 0.4;
            double d2 = z;
            float yaw = (float)Math.toRadians(-this.yBodyRot + (float)(reverse ? 180 : 0));
            double lookX = -Math.cos(yaw);
            double lookZ = -Math.sin(yaw);
            float pitch = (float)(reverse ? -1 : 1) * (float)Math.atan2(yHeight, Math.sqrt(lookX * lookX + lookZ * lookZ));
            serverLevel.sendParticles(new AnnihilationSweepParticle.SweepData(this.getScale() * scale, yaw, pitch), d0, d1, d2, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }

    public void doTeleportEffects(boolean shouldSendProjectiles) {
        this.playSound(SoundEvents.ENDERMAN_TELEPORT, 3.0f, 1.0f);
    }

    public void doPortalEffect(double multiplier, float size, int amountOfPortals, int warmup, float scale, int life, float damage) {
        for (int k = 0; k < amountOfPortals; ++k) {
            float f3 = (float)k * (float)Math.PI * size / (float)amountOfPortals + (float)Math.PI * size / 10.0f;
            this.createAnnihilationPortal(this.getX() + (double)Mth.cos((float)f3) * multiplier, this.getZ() + (double)Mth.sin((float)f3) * multiplier, this.getY() - 5.0, this.getY() + 5.0, life, warmup, scale, damage);
        }
    }

    public void doFlamesEffect(double multiplier, float size, int amountOfFlames, boolean particle, ParticleOptions particleOptions, double particleAdditionalY) {
        if (particle) {
            for (int k = 0; k < amountOfFlames; ++k) {
                float f3 = (float)k * (float)Math.PI * size / (float)amountOfFlames + (float)Math.PI * size / 10.0f;
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(particleOptions, this.getX() + (double)Mth.cos((float)f3) * multiplier, this.getY() + particleAdditionalY, this.getZ() + (double)Mth.sin((float)f3) * multiplier, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
            }
        } else {
            for (int k = 0; k < amountOfFlames; ++k) {
                float f3 = (float)k * (float)Math.PI * size / (float)amountOfFlames + (float)Math.PI * size / 10.0f;
                int standingOnY = Mth.floor((double)this.getY());
                this.spawnFlames(this.getX() + (double)Mth.cos((float)f3) * multiplier, this.getZ() + (double)Mth.sin((float)f3) * multiplier, standingOnY, this.getY() + 1.0, f3, 2, 5.0f, false, ModParticles.ANNIHILATION_NUKE.get());
            }
        }
    }

    public void attractingParticles(ParticleOptions particleType, float speed, float range) {
        if (this.level() instanceof ServerLevel serverLevel) {
            Vec3 center = this.position().add(0.0, this.getY(), 0.0);
            for (int i = 0; i < 20; ++i) {
                double x = center.x + (this.level().random.nextDouble() - 0.5) * 2.0 * (double)range;
                double y = center.y + (this.level().random.nextDouble() - 0.5) * 2.0 * (double)range;
                double z = center.z + (this.level().random.nextDouble() - 0.5) * 2.0 * (double)range;
                Vec3 from = new Vec3(x, y, z);
                Vec3 dir = center.subtract(from).normalize().scale((double)speed);
                serverLevel.sendParticles(particleType, x, y, z, 0, dir.x, dir.y, dir.z, 1.0D);
            }
        }
    }

    public void pullParticles(ParticleOptions particleOptions, float speed, float range) {
        if (this.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 80; ++i) {
                double startX = this.getX() + (this.random.nextDouble() - 0.5) * (double)range;
                double startY = this.getY() + (this.random.nextDouble() - 0.5) * (double)range;
                double startZ = this.getZ() + (this.random.nextDouble() - 0.5) * (double)range;
                double dirX = this.getX() - startX;
                double dirY = this.getY() - startY;
                double dirZ = this.getZ() - startZ;
                double length = Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
                double speedX = (dirX /= length) * (double)speed;
                double speedY = (dirY /= length) * (double)speed;
                double speedZ = (dirZ /= length) * (double)speed;
                serverLevel.sendParticles(particleOptions, startX, startY, startZ, 0, speedX, speedY, speedZ, 1.0D);
            }
        }
    }

    public void throwAnGravityEntity(float velocity, double destX, double destY, double destZ, double x, double y, double z, float damage, LivingEntity passenger) {
        if (passenger != null) {
            EntityThrown thrownEntity = new EntityThrown(this.level(), this, x, y, z, damage, passenger);
            thrownEntity.setPosRaw(x, y, z);
            double d0 = destX - x;
            double d1 = destY + 0.5D - thrownEntity.getY();
            double d2 = destZ - z;
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            thrownEntity.shoot(d0, d1 + d3 * 0.2D, d2, velocity, 14 - this.level().getDifficulty().getId() * 4);
            thrownEntity.setOwner(this);
            this.level().addFreshEntity(thrownEntity);
        }
    }

    public void shootDeathProjectiles(float angle, int amount, float y, boolean biggieCheese) {
        int upperArmorAmount = amount;
        for (int i = 0; i < upperArmorAmount; ++i) {
            float throwAngle = (float) i * (float) Math.PI / (float) (upperArmorAmount / 2) + angle;
            double sx = this.getX() + (double) (Mth.cos(throwAngle) * 1.0F);
            double sy = this.getY() + (double) y;
            double sz = this.getZ() + (double) (Mth.sin(throwAngle) * 1.0F);
            double vx = Mth.cos(throwAngle);
            double vy = 0.0F + this.getRandom().nextFloat() * 0.3F;
            double vz = Mth.sin(throwAngle);
            double v3 = Mth.sqrt((float) (vx * vx + vz * vz));
            if (biggieCheese) {
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ModParticles.GROUND_ANNIHILATION_NUKE.get(), sx, sy + 4.0D, sz, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
                AnnihilationBomb projectile = new AnnihilationBomb(LmEntityRegistry.ANNIHILATION_BOMB.get(), this.level(), this, 10.0F, 16, false);
                CameraShakeEntity.cameraShake(this.level(), this.position(), 10.0F, 0.15F, 10, 5);
                projectile.moveTo(sx, sy, sz, (float) i * 11.25F, this.getXRot());
                projectile.shoot(vx, vy + v3 * 0.2D, vz, 0.7F, 1.0F);
                this.level().addFreshEntity(projectile);
                continue;
            }
            this.playSound(ModSounds.DIMENSIONAL_BOMB_EXPLODE_SMALL.get(), 1.0F, 1.0F);
            CameraShakeEntity.cameraShake(this.level(), this.position(), 10.0F, 0.1F, 5, 5);
            SmallAnnihilationBomb projectile = new SmallAnnihilationBomb(this.level(), this, 8.0F);
            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ModParticles.ANNIHILATION_EXPLOSION.get(), sx, sy, sz, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
            projectile.moveTo(sx, sy, sz, (float) i * 11.25F, this.getXRot());
            projectile.shoot(vx, vy + v3 * 0.2D, vz, 0.7F, 1.0F);
            projectile.setOwner(this);
            this.level().addFreshEntity(projectile);
        }
    }

    public void createCircularLightningParticle(float vec, float offset, int part, float size) {
        for (int i = 0; i < 360; ++i) {
            if (i % part != 0) continue;
            float f = Mth.cos(this.yBodyRot * ((float) Math.PI / 180));
            float f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180));
            double theta = (double) this.yBodyRot * (Math.PI / 180);
            double vecX = Math.cos(theta += 1.5707963267948966);
            double vecZ = Math.sin(theta);
            double d0 = (double) (this.random.nextFloat() - 0.5F) + this.getDeltaMovement().x;
            double d1 = (double) (this.random.nextFloat() - 0.5F) + this.getDeltaMovement().y;
            double d2 = (double) (this.random.nextFloat() - 0.5F) + this.getDeltaMovement().z;
            double dist = 1.0F + this.random.nextFloat() * 0.2F;
            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(new LightningParticle.OrbData(0, 255, 0), this.getX() + (double) vec * vecX + (double) (f * offset), this.getY(), this.getZ() + (double) vec * vecZ + (double) (f1 * offset), 0, (double) (Mth.sin((float) i) * size), d1 * dist, (double) (Mth.cos((float) i) * size), 1.0D);
            }
        }
    }

    private void createAnnihilationPortal(double pX, double pZ, double pMinY, double pMaxY, int life, int pWarmupDelay, float scale, float damage) {
        BlockPos blockpos = BlockPos.containing(pX, pMaxY, pZ);
        boolean flag = false;
        double d0 = 0.0D;
        do {
            BlockPos blockpos1 = blockpos.below();
            BlockState blockstate = this.level().getBlockState(blockpos1);
            if (!blockstate.isFaceSturdy(this.level(), blockpos1, Direction.UP)) continue;
            BlockState blockstate1 = this.level().getBlockState(blockpos);
            VoxelShape voxelshape;
            if (!this.level().isEmptyBlock(blockpos) && !(voxelshape = blockstate1.getCollisionShape(this.level(), blockpos)).isEmpty()) {
                d0 = voxelshape.max(Direction.Axis.Y);
            }
            flag = true;
            break;
        } while ((blockpos = blockpos.below()).getY() >= Mth.floor(pMinY) - 1);
        if (flag) {
            this.level().addFreshEntity(new AnnihilationPortalEntity(this.level(), pX, (double) blockpos.getY() + d0, pZ, 0.0F, pWarmupDelay, this, life, damage, true, scale));
        }
    }

    private void flameRadagonShockwave(float spreadarc, int distance, float vec, int delay, float pos, float offset, float damage, boolean warningParticle) {
        float f = Mth.cos(this.yBodyRot * ((float) Math.PI / 180));
        float f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180));
        double theta1 = (double) this.yBodyRot * (Math.PI / 180);
        double vecX = Math.cos(theta1 += 1.5707963267948966);
        double vecZ = Math.sin(theta1);
        double x = this.getX() + (double) pos * vecX + (double) (f * offset);
        double z = this.getZ() + (double) pos * vecZ + (double) (f1 * offset);
        double perpFacing = (double) this.yBodyRot * (Math.PI / 180);
        double facingAngle = perpFacing + 1.5707963267948966;
        double spread = Math.PI * (double) spreadarc;
        int arcLen = this.getAttackState() == 53 ? Mth.ceil((double) distance * spread * (double) 0.15F) : Mth.ceil((double) distance * spread);
        for (int i = 0; i < arcLen; ++i) {
            double theta = ((double) i / ((double) arcLen - 1.0D) - 0.5D) * spread + facingAngle;
            double vx = Math.cos(theta);
            double vz = Math.sin(theta);
            double px = x + vx * (double) distance + (double) vec * Math.cos((double) (this.yBodyRot + 90.0F) * Math.PI / 180.0D);
            double pz = z + vz * (double) distance + (double) vec * Math.sin((double) (this.yBodyRot + 90.0F) * Math.PI / 180.0D);
            int hitX = Mth.floor(px);
            int hitZ = Mth.floor(pz);
            this.spawnFlames((double) hitX + 0.5D, (double) hitZ + 0.5D, this.getY() - 5.0D, this.getY() + 3.0D, (float) theta, delay, damage, warningParticle, ModParticles.GROUND_ANNIHILATION_NUKE.get());
        }
    }

    public void attractParticles(ParticleOptions particleOptions, int cap, int reps, float vec, float offset, float startY, float endY, float velocity) {
        float f = Mth.cos(this.yBodyRot * ((float) Math.PI / 180));
        float f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180));
        double theta = (double) this.yBodyRot * (Math.PI / 180);
        double vecX = Math.cos(theta += 1.5707963267948966);
        double vecZ = Math.sin(theta);
        int rX = this.random1.nextInt(-cap, cap);
        int rZ = this.random1.nextInt(-cap, cap);
        float f2 = (this.random.nextFloat() - 0.0F) * 0.5F;
        double d1 = this.getX() + (double) rX;
        double d2 = this.getY() + (double) startY + (double) f2;
        double d3 = this.getZ() + (double) rZ;
        Vec3 vec3 = new Vec3(d1, d2, d3);
        Vec3 vec4 = new Vec3(this.getX() + (double) vec * vecX + (double) (f * offset),
                this.position().y + (double) endY,
                this.getZ() + (double) vec * vecZ + (double) (f1 * offset));
        Vec3 vf = vec4.subtract(vec3);
        Vec3 v = vf.scale((double) velocity);
        if (this.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i <= reps; ++i) {
                serverLevel.sendParticles(particleOptions, d1, d2, d3, 0, v.x, v.y, v.z, 1.0D);
            }
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (ATTACK_STATE.equals(accessor) && this.level().isClientSide) {
            switch (this.getAttackState()) {
                case 0 -> this.stopAllAnimationStates();
                case 1 -> this.startAnimation(this.spawnAnimationState);
                case 2 -> this.startAnimation(this.p2AnimationState);
                case 3 -> this.startAnimation(this.idleAnimationState);
                case 4 -> this.startAnimation(this.spinSmashRightAnimationState);
                case 5 -> this.startAnimation(this.spinSmashLeftAnimationState);
                case 6 -> this.startAnimation(this.shootOnceAnimationState);
                case 7 -> this.startAnimation(this.shootDoubleAnimationState);
                case 8 -> this.startAnimation(this.doubleLeftHookComboAnimationState);
                case 9 -> this.startAnimation(this.roarTeleportAnimationState);
                case 10 -> this.startAnimation(this.fallAnimationState);
                case 11 -> this.startAnimation(this.landAnimationState);
                case 12 -> this.startAnimation(this.landAgainAnimationState);
                case 13 -> this.startAnimation(this.ambushAnimationState);
                case 14 -> this.startAnimation(this.ambushSwapAnimationState);
                case 15 -> this.startAnimation(this.slashComboAnimationState);
                case 16 -> this.startAnimation(this.teleportUppercutAnimationState);
                case 17 -> this.startAnimation(this.teleportGrabPreAnimationState);
                case 18 -> this.startAnimation(this.teleportGrabSuccessAnimationState);
                case 19 -> this.startAnimation(this.teleportGrabFallAnimationState);
                case 20 -> this.startAnimation(this.teleportGrabLandAnimationState);
                case 21 -> this.startAnimation(this.teleportAwaySingleShotLaserAnimationState);
                case 22 -> this.startAnimation(this.jumpTeleportAnimationState);
                case 23 -> this.startAnimation(this.teleportSlashComboAnimationState);
                case 24 -> this.startAnimation(this.cloneBurstGrabAnimationState);
                case 25 -> this.startAnimation(this.kickSmashGrabAnimationState);
                case 26 -> this.startAnimation(this.rightSpinTeleportSmashAnimationState);
                case 27 -> this.startAnimation(this.backstepTeleportSlashP2AnimationState);
                case 28 -> this.startAnimation(this.backstepStompLeftAnimationState);
                case 29 -> this.startAnimation(this.backstepStompRightAnimationState);
                case 30 -> this.startAnimation(this.teleportGrabFailCrossSlashAnimationState);
                case 31 -> this.startAnimation(this.slashComboRightAnimationState);
                case 32 -> this.startAnimation(this.teleportSlashComboRightAnimationState);
                case 33 -> this.startAnimation(this.stompKickAnimationState);
                case 34 -> this.startAnimation(this.stompAnimationState);
                case 35 -> this.startAnimation(this.stunStompAnimationState);
                case 36 -> this.startAnimation(this.kickSmashAnimationState);
                case 37 -> this.startAnimation(this.grabAfterKickSmashAnimationState);
                case 38 -> this.startAnimation(this.kickSmashByeByeAnimationState);
                case 39 -> this.startAnimation(this.teleportGrabAfterKickSmashAnimationState);
                case 40 -> this.startAnimation(this.crushGrabSuccessAnimationState);
                case 41 -> this.startAnimation(this.crushGrabFailAnimationState);
                case 42 -> this.startAnimation(this.singleShotLaserAnimationState);
                case 43 -> this.startAnimation(this.singleShotLaserAfterStompAnimationState);
                case 44 -> this.startAnimation(this.singleShotLaserTeleportEndAnimationState);
                case 45 -> this.startAnimation(this.singleShotLaserEndAnimationState);
                case 46 -> this.startAnimation(this.singleShotLaserQuadEndAnimationState);
                case 47 -> this.startAnimation(this.closeGrabPreAnimationState);
                case 48 -> this.startAnimation(this.p3AnimationState);
                case 49 -> this.startAnimation(this.LeftgrabAfterKickSmashAnimationState);
                case 50 -> this.startAnimation(this.LeftcrushGrabSuccessAnimationState);
                case 51 -> this.startAnimation(this.LeftcrushGrabFailAnimationState);
                case 52 -> this.startAnimation(this.ArmBlockAnimationState);
                case 53 -> this.startAnimation(this.PortalUltimateAnimationState);
                case 54 -> this.startAnimation(this.BombShootOnceEndAnimationState);
                case 55 -> this.startAnimation(this.BombShootDoubleEndAnimationState);
                case 60 -> this.startAnimation(this.DeathAnimationState);
                case 61 -> this.startAnimation(this.TpGrabFail0AnimationState);
                case 62 -> this.startAnimation(this.AmbushLongerAnimationState);
            }
        }
        super.onSyncedDataUpdated(accessor);
    }

    private void startAnimation(AnimationState state) {
        this.stopAllAnimationStates();
        state.startIfStopped(this.tickCount);
    }
    public AnimationState getAnimationState(String input) {
        if (input.equals("spin_smash")) {
            return this.spinSmashRightAnimationState;
        }
        if (input.equals("idle")) {
            return this.idleAnimationState;
        }
        if (input.equals("spin_smash_left")) {
            return this.spinSmashLeftAnimationState;
        }
        if (input.equals("shoot_once")) {
            return this.shootOnceAnimationState;
        }
        if (input.equals("shoot_double")) {
            return this.shootDoubleAnimationState;
        }
        if (input.equals("left_hook_db_combo")) {
            return this.doubleLeftHookComboAnimationState;
        }
        if (input.equals("land")) {
            return this.landAnimationState;
        }
        if (input.equals("roar_teleport")) {
            return this.roarTeleportAnimationState;
        }
        if (input.equals("land_again")) {
            return this.landAgainAnimationState;
        }
        if (input.equals("fall")) {
            return this.fallAnimationState;
        }
        if (input.equals("ambush")) {
            return this.ambushAnimationState;
        }
        if (input.equals("ambush_swap")) {
            return this.ambushSwapAnimationState;
        }
        if (input.equals("slash_combo")) {
            return this.slashComboAnimationState;
        }
        if (input.equals("teleport_uppercut")) {
            return this.teleportUppercutAnimationState;
        }
        if (input.equals("teleport_grab_fall")) {
            return this.teleportGrabFallAnimationState;
        }
        if (input.equals("teleport_grab_pre")) {
            return this.teleportGrabPreAnimationState;
        }
        if (input.equals("teleport_grab_land")) {
            return this.teleportGrabLandAnimationState;
        }
        if (input.equals("teleport_grab_success")) {
            return this.teleportGrabSuccessAnimationState;
        }
        if (input.equals("jump_teleport")) {
            return this.jumpTeleportAnimationState;
        }
        if (input.equals("teleport_slash_combo")) {
            return this.teleportSlashComboAnimationState;
        }
        if (input.equals("clone_burst_grab")) {
            return this.cloneBurstGrabAnimationState;
        }
        if (input.equals("kick_smash_grab")) {
            return this.kickSmashGrabAnimationState;
        }
        if (input.equals("right_spin_teleport_smash")) {
            return this.rightSpinTeleportSmashAnimationState;
        }
        if (input.equals("backstep_teleport_slash_p2")) {
            return this.backstepTeleportSlashP2AnimationState;
        }
        if (input.equals("backstep_stomp_left")) {
            return this.backstepStompLeftAnimationState;
        }
        if (input.equals("backstep_stomp_right")) {
            return this.backstepStompRightAnimationState;
        }
        if (input.equals("teleport_grab_fail_cross_slash")) {
            return this.teleportGrabFailCrossSlashAnimationState;
        }
        if (input.equals("slash_combo_right")) {
            return this.slashComboRightAnimationState;
        }
        if (input.equals("teleport_slash_combo_right")) {
            return this.teleportSlashComboRightAnimationState;
        }
        if (input.equals("stomp_kick")) {
            return this.stompKickAnimationState;
        }
        if (input.equals("stomp")) {
            return this.stompAnimationState;
        }
        if (input.equals("stomp_stun_teleport")) {
            return this.stunStompAnimationState;
        }
        if (input.equals("spawn")) {
            return this.spawnAnimationState;
        }
        if (input.equals("kick_smash")) {
            return this.kickSmashAnimationState;
        }
        if (input.equals("grab_after_kick_smash")) {
            return this.grabAfterKickSmashAnimationState;
        }
        if (input.equals("kick_smash_bye_bye")) {
            return this.kickSmashByeByeAnimationState;
        }
        if (input.equals("p2")) {
            return this.p2AnimationState;
        }
        if (input.equals("teleport_grab_after_kick_smash")) {
            return this.teleportGrabAfterKickSmashAnimationState;
        }
        if (input.equals("crush_grab_success")) {
            return this.crushGrabSuccessAnimationState;
        }
        if (input.equals("crush_grab_fail")) {
            return this.crushGrabFailAnimationState;
        }
        if (input.equals("single_shot_laser")) {
            return this.singleShotLaserAnimationState;
        }
        if (input.equals("single_shot_laser_after_stomp")) {
            return this.singleShotLaserAfterStompAnimationState;
        }
        if (input.equals("single_shot_laser_end")) {
            return this.singleShotLaserEndAnimationState;
        }
        if (input.equals("single_shot_laser_teleport_end")) {
            return this.singleShotLaserTeleportEndAnimationState;
        }
        if (input.equals("teleport_away_single_shot_laser")) {
            return this.teleportAwaySingleShotLaserAnimationState;
        }
        if (input.equals("single_shot_laser_quad_end")) {
            return this.singleShotLaserQuadEndAnimationState;
        }
        if (input.equals("close_pre_teleport_grab")) {
            return this.closeGrabPreAnimationState;
        }
        if (input.equals("left_grab_after_kick_smash")) {
            return this.LeftgrabAfterKickSmashAnimationState;
        }
        if (input.equals("left_crush_grab_success")) {
            return this.LeftcrushGrabSuccessAnimationState;
        }
        if (input.equals("left_crush_grab_fail")) {
            return this.LeftcrushGrabFailAnimationState;
        }
        if (input.equals("p3")) {
            return this.p3AnimationState;
        }
        if (input.equals("arm_block")) {
            return this.ArmBlockAnimationState;
        }
        if (input.equals("ultimate")) {
            return this.PortalUltimateAnimationState;
        }
        if (input.equals("shoot_once_end")) {
            return this.BombShootOnceEndAnimationState;
        }
        if (input.equals("shoot_double_end")) {
            return this.BombShootDoubleEndAnimationState;
        }
        if (input.equals("death")) {
            return this.DeathAnimationState;
        }
        if (input.equals("teleport_grab_fail_0")) {
            return this.TpGrabFail0AnimationState;
        }
        if (input.equals("ambush_longer")) {
            return this.AmbushLongerAnimationState;
        }
        if (input.equals("tracking_ball_charge")) {
            return this.trackingBallChargeAnimationState;
        }
        if (input.equals("teleport_grab_fail_rise_up")) {
            return this.teleportGrabFailRiseUpAnimationState;
        }
        if (input.equals("stomp_after_kick_left")) {
            return this.stompAfterkickLeftAnimationState;
        }
        if (input.equals("stomp_after_kick_right")) {
            return this.stompAfterkickLeftAnimationState;
        }
        if (input.equals("backstep_stomp_right_spin")) {
            return this.backstepStompRightSpinAnimationState;
        }
        return new AnimationState();
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return this.getBoundingBox().inflate(2.0D);
    }
}
