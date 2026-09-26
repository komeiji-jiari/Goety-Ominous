package com.qiuyue.goetyominous.common.entities.ally.lm;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IMoveGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IStateGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.PossessedPaladinAlertedGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.PossessedPaladinAttackGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.PossessedPaladinAttackMinGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.PossessedPaladinDoubleSlashSlamAttackGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.PossessedPaladinFinisherAttackGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.PossessedPaladinFlipSmashGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.PossessedPaladinJumpFallGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.PossessedPaladinJumpSmashComboGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.PossessedPaladinSecondPhaseGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.PossessedPaladinSideRollSpinGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.PossessedPaladinSlamAttackGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.PossessedPaladinSlashFromGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.PossessedPaladinStabGrabGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.PossessedPaladinThrowDaggersGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.PossessedPaladinStateGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.SoulStrike;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.SoulTrident;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.ThrownPhantomDagger;
import com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.Advancement;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.miauczel.legendary_monsters.damagetype.ModDamageTypes;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Projectile.ThrownEntity.EntityThrownEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.miauczel.legendary_monsters.Particle.custom.Circle;
import net.miauczel.legendary_monsters.Particle.custom.SoulSweepParticle;
import net.miauczel.legendary_monsters.Particle.custom.SoulSweepRedParticle;
import net.miauczel.legendary_monsters.Particle.ModParticles;
import net.miauczel.legendary_monsters.config.ModConfig;
import net.miauczel.legendary_monsters.effect.ModEffects;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.AnimatedEntity.FallingSoulBladeEntity;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.AnimatedEntity.SoulBladeEntity;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.AnimatedEntity.SoulShieldEntity;
import net.miauczel.legendary_monsters.effect.DynamicCameraZoomEntity;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Effect.CameraShakeEntity;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Projectile.SoulPillarEntity;
import net.miauczel.legendary_monsters.item.ModItems;
import net.miauczel.legendary_monsters.sound.ModSounds;
import net.miauczel.legendary_monsters.util.EntityUtil;
import net.miauczel.legendary_monsters.util.MathUtils;
import net.miauczel.legendary_monsters.util.ParticleUtils;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PossessedPaladinServant extends IAnimatedBossServant {

    private static final EntityDataAccessor<Integer> PHASE =
            SynchedEntityData.defineId(PossessedPaladinServant.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Boolean> AWAKENED =
            SynchedEntityData.defineId(PossessedPaladinServant.class, EntityDataSerializers.BOOLEAN);

    public static final int SECOND_PHASE = 2;

    public float uR = 1.0F;
    public float uG = 0.0F;
    public float uB = 0.0F;

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

    public final int PARRY_COOLDOWN;
    public int parry_cooldown;
    public final int THROW_COOLDOWN;
    public int throw_cooldown;
    public final int STAB_GRAB_COOLDOWN;
    public int stab_grab_cooldown;
    public final int SLAM_COOLDOWN;
    public int slam_cooldown;
    public final int FLIP_SMASH_COOLDOWN;
    public int flip_smash_cooldown;
    public final int SIDE_ROLL_SPIN_COOLDOWN;
    public int side_roll_spin_cooldown;
    public final int BACKSTEP_COOLDOWN;
    public int backstep_cooldown;
    public final int SLASH_FROM_COOLDOWN;
    public int slash_from_cooldown;
    public final int DOUBLE_SLASH_COOLDOWN;
    public int double_slash_cooldown;
    public final int JUMP_COOLDOWN;
    public int jump_cooldown;
    public final int SHIELD_SMASH_COOLDOWN;
    public int shield_smash_cooldown;
    public final int FINISHER_COOLDOWN;
    public int finisher_cooldown;
    public final int TRIDENT_THROW_SPIN;
    public int trident_throw_spin;

    public final RandomSource random1 = RandomSource.create();

    public final ControlledAnim ghostItemFade = new ControlledAnim(10);

    public final ControlledAnim telegraphFadeAway = new ControlledAnim(15);

    public int rayAmount;

    public int SideRollSpinRandom;
    public int FinisherRandom;
    public int DoubleSlashRandom;
    public int SlashFromRandom;
    public int ShieldSmashRandom;
    public int JumpRandom;
    public int backflipRandom;
    public int throwRandom;
    public int slamRandom;
    public int flipSmashRandom;
    public int throwTridentSpinRandom;

    public AnimationState idleAnimationState;
    public AnimationState DoubleSlashAnimationState;
    public AnimationState DoubleSlashEndAnimationState;
    public AnimationState DoubleSlashSlamEndAnimationState;
    public AnimationState ParryAnimationState;
    public AnimationState deathAnimationState;
    public AnimationState SwordSlamAnimationState;
    public AnimationState SwordSlamEndAnimationState;
    public AnimationState SwordSlamCounterEndAnimationState;
    public AnimationState SwordSlamCounterReleaseAnimationState;
    public AnimationState AllertedAnimationState;
    public AnimationState BackflipAnimationState;
    public AnimationState BackflipEndAnimationState;
    public AnimationState BackflipDoubleAnimationState;
    public AnimationState FlyAwaySlashAnimationState;
    public AnimationState FlipSmashAnimationState;
    public AnimationState FlipSmashEndAnimationState;
    public AnimationState FlipSmashFlipAnimationState;
    public AnimationState SlashFromAnimationState;
    public AnimationState SlashFromEndAnimationState;
    public AnimationState SlashFromStabAnimationState;
    public AnimationState SlashFromStabGrabAnimationState;
    public AnimationState SlashFromStabGrabFailAnimationState;
    public AnimationState SlashFromStabGrabStabFailAnimationState;
    public AnimationState SlashFromStabGrabSuccessAnimationState;
    public AnimationState ThrowAnimationState;
    public AnimationState ThrowDoubleAnimationState;
    public AnimationState JumpPreAnimationState;
    public AnimationState JumpFallAnimationState;
    public AnimationState JumpSmashAnimationState;
    public AnimationState JumpSmashComboAnimationState;
    public AnimationState ShieldSmashAnimationState;
    public AnimationState SecondPhaseAnimationState;
    public AnimationState SideRollSpinAnimationState;
    public AnimationState LeftSideRollSpinAnimationState;
    public AnimationState SleepAnimationState;
    public AnimationState AwakenAnimationState;
    public AnimationState DeathAnimationState;
    public AnimationState FinisherAnimationState;
    public AnimationState TridentThrowSpinAnimationState;

    public int DoubleSlashType;
    public int SideRollSpinType;
    public boolean hasParried;
    public boolean succedGrabbing;

    public boolean executedByOwner;

    public int deathTicks;

    public int BossInvulnerabilityTime;

    public static final int BOSS_INVULNERABILITY_TICKS = 10;
    public int soulRaysCount;

    public boolean shouldAttackMore;

    public boolean hasHurt;

    public double lastTargetX;
    public double lastTargetY;
    public double lastTargetZ;

    public PossessedPaladinServant(EntityType<? extends IAnimatedBossServant> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.xpReward = 100;
        this.setMaxUpStep(2.0F);
        this.setPersistenceRequired();

        this.PARRY_COOLDOWN = toTicks(5.0F);
        this.parry_cooldown = this.PARRY_COOLDOWN;
        this.THROW_COOLDOWN = toTicks(5.0F);
        this.throw_cooldown = 0;
        this.STAB_GRAB_COOLDOWN = toTicks(10.0F);
        this.stab_grab_cooldown = 0;
        this.SLAM_COOLDOWN = toTicks(6.0F);
        this.slam_cooldown = 0;
        this.FLIP_SMASH_COOLDOWN = toTicks(4.0F);
        this.flip_smash_cooldown = 0;
        this.SIDE_ROLL_SPIN_COOLDOWN = toTicks(4.0F);
        this.side_roll_spin_cooldown = 0;
        this.BACKSTEP_COOLDOWN = toTicks(2.0F);
        this.backstep_cooldown = 0;
        this.SLASH_FROM_COOLDOWN = toTicks(1.0F);
        this.slash_from_cooldown = 0;
        this.DOUBLE_SLASH_COOLDOWN = toTicks(3.0F);
        this.double_slash_cooldown = 0;
        this.JUMP_COOLDOWN = toTicks(6.0F);
        this.jump_cooldown = 0;
        this.SHIELD_SMASH_COOLDOWN = toTicks(6.0F);
        this.shield_smash_cooldown = 0;
        this.FINISHER_COOLDOWN = toTicks(16.0F);
        this.finisher_cooldown = 0;
        this.TRIDENT_THROW_SPIN = toTicks(8.0F);
        this.trident_throw_spin = 0;

        this.SideRollSpinRandom = 16;
        this.FinisherRandom = 25;
        this.DoubleSlashRandom = 10;
        this.SlashFromRandom = 15;
        this.ShieldSmashRandom = 15;
        this.JumpRandom = 10;
        this.backflipRandom = 10;
        this.throwRandom = 10;
        this.slamRandom = 10;
        this.flipSmashRandom = 10;
        this.throwTridentSpinRandom = 15;

        this.idleAnimationState = new AnimationState();
        this.DoubleSlashAnimationState = new AnimationState();
        this.DoubleSlashEndAnimationState = new AnimationState();
        this.DoubleSlashSlamEndAnimationState = new AnimationState();
        this.ParryAnimationState = new AnimationState();
        this.deathAnimationState = new AnimationState();
        this.SwordSlamAnimationState = new AnimationState();
        this.SwordSlamEndAnimationState = new AnimationState();
        this.SwordSlamCounterEndAnimationState = new AnimationState();
        this.SwordSlamCounterReleaseAnimationState = new AnimationState();
        this.AllertedAnimationState = new AnimationState();
        this.BackflipAnimationState = new AnimationState();
        this.BackflipEndAnimationState = new AnimationState();
        this.BackflipDoubleAnimationState = new AnimationState();
        this.FlyAwaySlashAnimationState = new AnimationState();
        this.FlipSmashAnimationState = new AnimationState();
        this.FlipSmashEndAnimationState = new AnimationState();
        this.FlipSmashFlipAnimationState = new AnimationState();
        this.SlashFromAnimationState = new AnimationState();
        this.SlashFromEndAnimationState = new AnimationState();
        this.SlashFromStabAnimationState = new AnimationState();
        this.SlashFromStabGrabAnimationState = new AnimationState();
        this.SlashFromStabGrabFailAnimationState = new AnimationState();
        this.SlashFromStabGrabStabFailAnimationState = new AnimationState();
        this.SlashFromStabGrabSuccessAnimationState = new AnimationState();
        this.ThrowAnimationState = new AnimationState();
        this.ThrowDoubleAnimationState = new AnimationState();
        this.JumpPreAnimationState = new AnimationState();
        this.JumpFallAnimationState = new AnimationState();
        this.JumpSmashAnimationState = new AnimationState();
        this.JumpSmashComboAnimationState = new AnimationState();
        this.ShieldSmashAnimationState = new AnimationState();
        this.SecondPhaseAnimationState = new AnimationState();
        this.SideRollSpinAnimationState = new AnimationState();
        this.LeftSideRollSpinAnimationState = new AnimationState();
        this.SleepAnimationState = new AnimationState();
        this.AwakenAnimationState = new AnimationState();
        this.DeathAnimationState = new AnimationState();
        this.FinisherAnimationState = new AnimationState();
        this.TridentThrowSpinAnimationState = new AnimationState();

        this.DoubleSlashType = 1;
        this.SideRollSpinType = 1;
        this.hasParried = false;
        this.succedGrabbing = false;
        this.hasHurt = false;
        this.shouldAttackMore = false;
    }

    private static int toTicks(float seconds) {
        return (int) (seconds * 20.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.PossessedPaladinServantHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.PossessedPaladinServantArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.PossessedPaladinServantArmorToughness.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.PossessedPaladinServantDamage.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.PossessedPaladinServantMovementSpeed.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.PossessedPaladinServantFollowRange.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.PossessedPaladinServantKnockbackResistance.get())
                .add(Attributes.ATTACK_KNOCKBACK, AttributesConfig.PossessedPaladinServantAttackKnockback.get());
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.PossessedPaladinServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.PossessedPaladinServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.PossessedPaladinServantDamage.get());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PHASE, 1);
        this.entityData.define(AWAKENED, true);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("phase", this.getPhase());

        pCompound.putBoolean("is_Sleep", this.isSleep());

        ListTag saidList = new ListTag();
        for (String name : this.saidBossLines) {
            saidList.add(StringTag.valueOf(name));
        }
        pCompound.put("said_boss_lines", saidList);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setPhase(pCompound.contains("phase") ? pCompound.getInt("phase") : 1);

        if (pCompound.getBoolean("is_Sleep")) {
            this.setSleep(true);

            this.setAwakened(false);
        } else {
            this.setSleep(false);
        }

        this.saidBossLines.clear();
        ListTag saidList = pCompound.getList("said_boss_lines", Tag.TAG_STRING);
        for (int i = 0; i < saidList.size(); ++i) {
            this.saidBossLines.add(saidList.getString(i));
        }
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }

    @Override
    protected void positionRider(Entity pPassenger, Entity.MoveFunction pCallback) {
        float f = Mth.cos(this.yBodyRot * ((float) Math.PI / 180F));
        float f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180F));
        double theta = (double) this.yBodyRot * (Math.PI / 180D);
        ++theta;
        double vecX = Math.cos(theta);
        double vecZ = Math.sin(theta);
        float vec = 1.0F;
        float offset = 0.0F;
        if (this.hasPassenger(pPassenger)) {
            pCallback.accept(pPassenger,
                    this.getX() + (double) vec * vecX + (double) (f * offset),
                    this.getY() + (double) 1.0F,
                    this.getZ() + (double) vec * vecZ + (double) (f1 * offset));
        }
    }

    public void setPhase(int phase) {
        this.entityData.set(PHASE, phase);
    }

    public int getPhase() {
        return this.entityData.get(PHASE);
    }

    public boolean getIsSecondPhase() {
        return this.getPhase() >= SECOND_PHASE;
    }

    public Crackiness getCrackiness() {
        return Crackiness.byFraction(this.getHealth() / this.getMaxHealth());
    }

    public boolean shouldEnterSecondPhase() {
        if (this.getPhase() > 1) {
            return false;
        }
        Crackiness crackiness = this.getCrackiness();
        return crackiness == Crackiness.MEDIUM || crackiness == Crackiness.HIGH;
    }

    public boolean getIsAwakened() {
        return this.entityData.get(AWAKENED);
    }

    public void setAwakened(boolean awakened) {
        this.entityData.set(AWAKENED, awakened);
    }

    public boolean isSleep() {
        int state = this.getAttackState();
        return state == 34 || state == 35;
    }

    public void setSleep(boolean sleep) {
        this.setAttackState(sleep ? 34 : 0);
    }

    private boolean tryAwaken(Player pPlayer, ItemStack itemStack, boolean isOwner) {
        if (!isOwner || this.getAttackState() != 34) {
            return false;
        }

        if (!itemStack.is(ModItems.CORRUPTED_SOUL.get())) {
            return false;
        }

        if (!pPlayer.getAbilities().instabuild) {
            itemStack.shrink(1);
        }

        this.setAttackState(35);
        this.setAwakened(true);

        this.playSound(SoundEvents.SOUL_ESCAPE, 1.0F, 0.6F);

        if (this.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 12; ++i) {
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                double d2 = this.random.nextGaussian() * 0.02D;
                serverLevel.sendParticles(ParticleTypes.SOUL,
                        this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D),
                        0, d0, d1, d2, 0.5F);
            }
        }

        return true;
    }

    @Override
    public void setTarget(@Nullable LivingEntity pTarget) {
        if (pTarget != null && this.isStaying() && pTarget != this.getLastHurtByMob()) {
            return;
        }
        if (pTarget != null && this.isSleep()) {
            return;
        }
        super.setTarget(pTarget);
    }

    @Override
    public boolean canBeSeenAsEnemy() {
        return !this.isSleep() && super.canBeSeenAsEnemy();
    }

    public AnimationState getAnimationState(String input) {
        if (input.equals("idle")) {
            return this.idleAnimationState;
        } else if (input.equals("allerted")) {
            return this.AllertedAnimationState;
        } else if (input.equals("double_slash")) {
            return this.DoubleSlashAnimationState;
        } else if (input.equals("double_slash_end")) {
            return this.DoubleSlashEndAnimationState;
        } else if (input.equals("double_slash_slam_end")) {
            return this.DoubleSlashSlamEndAnimationState;
        } else if (input.equals("parry")) {
            return this.ParryAnimationState;
        } else if (input.equals("sword_slam_cut")) {
            return this.SwordSlamAnimationState;
        } else if (input.equals("sword_slam_end")) {
            return this.SwordSlamEndAnimationState;
        } else if (input.equals("sword_slam_counter_end")) {
            return this.SwordSlamCounterEndAnimationState;
        } else if (input.equals("sword_slam_counter_release")) {
            return this.SwordSlamCounterReleaseAnimationState;
        } else if (input.equals("backflip")) {
            return this.BackflipAnimationState;
        } else if (input.equals("fly_away_slash")) {
            return this.FlyAwaySlashAnimationState;
        } else if (input.equals("flip_smash")) {
            return this.FlipSmashAnimationState;
        } else if (input.equals("flip_smash_end")) {
            return this.FlipSmashEndAnimationState;
        } else if (input.equals("flip_smash_flip")) {
            return this.FlipSmashFlipAnimationState;
        } else if (input.equals("throw")) {
            return this.ThrowAnimationState;
        } else if (input.equals("throw_double")) {
            return this.ThrowDoubleAnimationState;
        } else if (input.equals("slash_from")) {
            return this.SlashFromAnimationState;
        } else if (input.equals("slash_from_end")) {
            return this.SlashFromEndAnimationState;
        } else if (input.equals("slash_from_stab")) {
            return this.SlashFromStabAnimationState;
        } else if (input.equals("slash_from_stab_grab_pre")) {
            return this.SlashFromStabGrabAnimationState;
        } else if (input.equals("slash_from_stab_grab_fail")) {
            return this.SlashFromStabGrabFailAnimationState;
        } else if (input.equals("slash_from_stab_grab_stab_fail")) {
            return this.SlashFromStabGrabStabFailAnimationState;
        } else if (input.equals("slash_from_stab_grab_success")) {
            return this.SlashFromStabGrabSuccessAnimationState;
        } else if (input.equals("jump_pre")) {
            return this.JumpPreAnimationState;
        } else if (input.equals("jump_fall")) {
            return this.JumpFallAnimationState;
        } else if (input.equals("jump_smash")) {
            return this.JumpSmashAnimationState;
        } else if (input.equals("shield_smash")) {
            return this.ShieldSmashAnimationState;
        } else if (input.equals("backflip_double")) {
            return this.BackflipDoubleAnimationState;
        } else if (input.equals("backflip_end")) {
            return this.BackflipEndAnimationState;
        } else if (input.equals("second_phase")) {
            return this.SecondPhaseAnimationState;
        } else if (input.equals("side_roll_spin")) {
            return this.SideRollSpinAnimationState;
        } else if (input.equals("left_side_roll_spin")) {
            return this.LeftSideRollSpinAnimationState;
        } else if (input.equals("jump_smash_combo")) {
            return this.JumpSmashComboAnimationState;
        } else if (input.equals("sleep")) {
            return this.SleepAnimationState;
        } else if (input.equals("awaken")) {
            return this.AwakenAnimationState;
        } else if (input.equals("death")) {
            return this.DeathAnimationState;
        } else if (input.equals("finisher")) {
            return this.FinisherAnimationState;
        } else if (input.equals("trident_throw_spin")) {
            return this.TridentThrowSpinAnimationState;
        }
        return new AnimationState();
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        if (ATTACK_STATE.equals(pKey) && this.level().isClientSide) {
            switch (this.getAttackState()) {
                case 0 -> this.stopAllAnimationStates();
                case 1 -> {
                    this.stopAllAnimationStates();
                    this.idleAnimationState.startIfStopped(this.tickCount);
                }
                case 2 -> {
                    this.stopAllAnimationStates();
                    this.DoubleSlashAnimationState.startIfStopped(this.tickCount);
                }
                case 3 -> {
                    this.stopAllAnimationStates();
                    this.DoubleSlashEndAnimationState.startIfStopped(this.tickCount);
                }
                case 4 -> {
                    this.stopAllAnimationStates();
                    this.DoubleSlashSlamEndAnimationState.startIfStopped(this.tickCount);
                }
                case 5 -> {
                    this.stopAllAnimationStates();
                    this.ParryAnimationState.startIfStopped(this.tickCount);
                }
                case 6 -> {
                    this.stopAllAnimationStates();
                    this.SwordSlamAnimationState.startIfStopped(this.tickCount);
                }
                case 7 -> {
                    this.stopAllAnimationStates();
                    this.SwordSlamEndAnimationState.startIfStopped(this.tickCount);
                }
                case 8 -> {
                    this.stopAllAnimationStates();
                    this.SwordSlamCounterEndAnimationState.startIfStopped(this.tickCount);
                }
                case 9 -> {
                    this.stopAllAnimationStates();
                    this.AllertedAnimationState.startIfStopped(this.tickCount);
                }
                case 10 -> {
                    this.stopAllAnimationStates();
                    this.BackflipAnimationState.startIfStopped(this.tickCount);
                }
                case 11 -> {
                    this.stopAllAnimationStates();
                    this.FlyAwaySlashAnimationState.startIfStopped(this.tickCount);
                }
                case 12 -> {
                    this.stopAllAnimationStates();
                    this.FlipSmashAnimationState.startIfStopped(this.tickCount);
                }
                case 13 -> {
                    this.stopAllAnimationStates();
                    this.FlipSmashEndAnimationState.startIfStopped(this.tickCount);
                }
                case 14 -> {
                    this.stopAllAnimationStates();
                    this.FlipSmashFlipAnimationState.startIfStopped(this.tickCount);
                }
                case 15 -> {
                    this.stopAllAnimationStates();
                    this.ThrowAnimationState.startIfStopped(this.tickCount);
                }
                case 16 -> {
                    this.stopAllAnimationStates();
                    this.SlashFromAnimationState.startIfStopped(this.tickCount);
                }
                case 17 -> {
                    this.stopAllAnimationStates();
                    this.SlashFromEndAnimationState.startIfStopped(this.tickCount);
                }
                case 18 -> {
                    this.stopAllAnimationStates();
                    this.SlashFromStabAnimationState.startIfStopped(this.tickCount);
                }
                case 19 -> {
                    this.stopAllAnimationStates();
                    this.SlashFromStabGrabAnimationState.startIfStopped(this.tickCount);
                }
                case 20 -> {
                    this.stopAllAnimationStates();
                    this.SlashFromStabGrabSuccessAnimationState.startIfStopped(this.tickCount);
                }
                case 21 -> {
                    this.stopAllAnimationStates();
                    this.SlashFromStabGrabFailAnimationState.startIfStopped(this.tickCount);
                }
                case 22 -> {
                    this.stopAllAnimationStates();
                    this.JumpPreAnimationState.startIfStopped(this.tickCount);
                }
                case 23 -> {
                    this.stopAllAnimationStates();
                    this.JumpFallAnimationState.startIfStopped(this.tickCount);
                }
                case 24 -> {
                    this.stopAllAnimationStates();
                    this.JumpSmashAnimationState.startIfStopped(this.tickCount);
                }
                case 25 -> {
                    this.stopAllAnimationStates();
                    this.ShieldSmashAnimationState.startIfStopped(this.tickCount);
                }
                case 26 -> {
                    this.stopAllAnimationStates();
                    this.SecondPhaseAnimationState.startIfStopped(this.tickCount);
                }
                case 27 -> {
                    this.stopAllAnimationStates();
                    this.BackflipEndAnimationState.startIfStopped(this.tickCount);
                }
                case 28 -> {
                    this.stopAllAnimationStates();
                    this.ThrowDoubleAnimationState.startIfStopped(this.tickCount);
                }
                case 29 -> {
                    this.stopAllAnimationStates();
                    this.SideRollSpinAnimationState.startIfStopped(this.tickCount);
                }
                case 30 -> {
                    this.stopAllAnimationStates();
                    this.LeftSideRollSpinAnimationState.startIfStopped(this.tickCount);
                }
                case 31 -> {
                    this.stopAllAnimationStates();
                    this.SwordSlamCounterReleaseAnimationState.startIfStopped(this.tickCount);
                }
                case 32 -> {
                    this.stopAllAnimationStates();
                    this.JumpSmashComboAnimationState.startIfStopped(this.tickCount);
                }
                case 33 -> {
                    this.stopAllAnimationStates();
                    this.SlashFromStabGrabStabFailAnimationState.startIfStopped(this.tickCount);
                }
                case 34 -> {
                    this.stopAllAnimationStates();
                    this.SleepAnimationState.startIfStopped(this.tickCount);
                }
                case 35 -> {
                    this.stopAllAnimationStates();
                    this.AwakenAnimationState.startIfStopped(this.tickCount);
                }
                case 36 -> {
                    this.stopAllAnimationStates();
                    this.DeathAnimationState.startIfStopped(this.tickCount);
                }
                case 37 -> {
                    this.stopAllAnimationStates();
                    this.FinisherAnimationState.startIfStopped(this.tickCount);
                }
                case 38 -> {
                    this.stopAllAnimationStates();
                    this.TridentThrowSpinAnimationState.startIfStopped(this.tickCount);
                }
                default -> {
                }
            }
        }

        super.onSyncedDataUpdated(pKey);
    }

    public void stopAllAnimationStates() {
        this.ThrowDoubleAnimationState.stop();
        this.idleAnimationState.stop();
        this.DoubleSlashAnimationState.stop();
        this.deathAnimationState.stop();
        this.DoubleSlashEndAnimationState.stop();
        this.DoubleSlashSlamEndAnimationState.stop();
        this.ParryAnimationState.stop();
        this.SwordSlamEndAnimationState.stop();
        this.SwordSlamCounterEndAnimationState.stop();
        this.SwordSlamAnimationState.stop();
        this.AllertedAnimationState.stop();
        this.BackflipAnimationState.stop();
        this.FlyAwaySlashAnimationState.stop();
        this.FlipSmashEndAnimationState.stop();
        this.FlipSmashFlipAnimationState.stop();
        this.FlipSmashAnimationState.stop();
        this.ThrowAnimationState.stop();
        this.SlashFromEndAnimationState.stop();
        this.SlashFromAnimationState.stop();
        this.SlashFromStabAnimationState.stop();
        this.SlashFromStabGrabSuccessAnimationState.stop();
        this.SlashFromStabGrabAnimationState.stop();
        this.SlashFromStabGrabFailAnimationState.stop();
        this.JumpPreAnimationState.stop();
        this.JumpFallAnimationState.stop();
        this.JumpSmashAnimationState.stop();
        this.BackflipDoubleAnimationState.stop();
        this.BackflipEndAnimationState.stop();
        this.ShieldSmashAnimationState.stop();
        this.SecondPhaseAnimationState.stop();
        this.SideRollSpinAnimationState.stop();
        this.LeftSideRollSpinAnimationState.stop();
        this.SwordSlamCounterReleaseAnimationState.stop();
        this.JumpSmashComboAnimationState.stop();
        this.SlashFromStabGrabStabFailAnimationState.stop();
        this.AwakenAnimationState.stop();
        this.SleepAnimationState.stop();
        this.DeathAnimationState.stop();
        this.FinisherAnimationState.stop();
        this.TridentThrowSpinAnimationState.stop();
    }

    public int getNextDoubleSlashType() {
        return this.DoubleSlashType;
    }

    public int getNextSideRollSpinType() {
        return this.SideRollSpinType;
    }

    public void randomizeNextDoubleSlashType(int rolls) {
        switch (this.getRandom().nextInt(rolls)) {
            case 0 -> this.DoubleSlashType = 1;
            case 1 -> this.DoubleSlashType = 2;
            default -> this.DoubleSlashType = 1;
        }
    }

    public void randomizeNextSideRollSpinType(int rolls) {
        switch (this.getRandom().nextInt(rolls)) {
            case 0 -> this.SideRollSpinType = 1;
            case 1 -> this.SideRollSpinType = 2;
            default -> this.SideRollSpinType = 1;
        }
    }

    public void randomizeAttacks() {
        this.randomizeNextSideRollSpinType(2);
        this.randomizeNextDoubleSlashType(2);
    }

    public boolean hasDagger() {
        return this.getAttackState() == 15 || this.getAttackState() == 28;
    }

    public boolean hasShield() {
        return this.getAttackState() == 25;
    }

    public boolean hasTrident() {
        return this.getAttackState() == 37 || this.getAttackState() == 38;
    }

    public boolean hasWings() {
        return this.getAttackState() == 37;
    }

    public boolean canRenderTelegraph() {
        return this.attackTicks >= 48 && this.attackTicks <= 60 && this.getAttackState() == 32
                || this.getAttackState() == 38 && this.attackTicks >= 48 && this.attackTicks <= 69;
    }

    @Override
    protected PathNavigation createNavigation(Level pLevel) {
        return new GroundPathNavigation(this, pLevel);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(6, new Summoned.WanderGoal<>(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));

        this.goalSelector.addGoal(2, new IMoveGoal(this, false, 1.0D));

        this.goalSelector.addGoal(0, new PossessedPaladinSecondPhaseGoal(this, 0, 26, 0,
                MathUtils.toTicks(7.38F), 0));

        this.goalSelector.addGoal(0, new PossessedPaladinStateGoal(this, 8, 8, 0,
                MathUtils.toTicks(3.38F), 18, true, 50.0D) {
            @Override
            public void stop() {
                PossessedPaladinServant.this.parry_cooldown = PossessedPaladinServant.this.PARRY_COOLDOWN;
                super.stop();
            }
        });

        this.goalSelector.addGoal(0, new PossessedPaladinStateGoal(this, 31, 31, 0,
                MathUtils.toTicks(4.17F), 18, true, 50.0D) {
            @Override
            public void stop() {
                PossessedPaladinServant.this.parry_cooldown = PossessedPaladinServant.this.PARRY_COOLDOWN;
                super.stop();
            }
        });

        this.goalSelector.addGoal(0, new PossessedPaladinStateGoal(this, 7, 7, 0,
                MathUtils.toTicks(1.29F), 0, true, 50.0D) {
            @Override
            public void stop() {
                PossessedPaladinServant.this.parry_cooldown = PossessedPaladinServant.this.PARRY_COOLDOWN;
                super.stop();
            }
        });

        this.goalSelector.addGoal(1, new PossessedPaladinAttackGoal(this, 0, 2, 0, 68, 68, 5.0F, false, 10.0D) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && PossessedPaladinServant.this.getRandom().nextFloat() * 100.0F
                        < (float) PossessedPaladinServant.this.DoubleSlashRandom
                        && PossessedPaladinServant.this.getNextDoubleSlashType() == 1
                        && PossessedPaladinServant.this.double_slash_cooldown <= 0;
            }

            @Override
            public void stop() {
                PossessedPaladinServant.this.double_slash_cooldown = PossessedPaladinServant.this.DOUBLE_SLASH_COOLDOWN;
                super.stop();
            }
        });

        this.goalSelector.addGoal(1, new PossessedPaladinDoubleSlashSlamAttackGoal(this, 0, 3, 0,
                MathUtils.toTicks(3.46F), 55, 5.0F, false, 10.0D) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && PossessedPaladinServant.this.getRandom().nextFloat() * 40.0F
                        < (float) PossessedPaladinServant.this.DoubleSlashRandom
                        && PossessedPaladinServant.this.getNextDoubleSlashType() == 2
                        && PossessedPaladinServant.this.double_slash_cooldown <= 0;
            }

            @Override
            public void stop() {
                PossessedPaladinServant.this.double_slash_cooldown = PossessedPaladinServant.this.DOUBLE_SLASH_COOLDOWN;
                super.stop();
            }
        });

        this.goalSelector.addGoal(1, new PossessedPaladinSlamAttackGoal(this, 0, 6, 0, 30, 20, 7.0F));

        this.goalSelector.addGoal(1, new PossessedPaladinAlertedGoal(this, 9, 9, 0, 20, 20, false, 0.0D));

        this.goalSelector.addGoal(1, new PossessedPaladinAttackGoal(this, 0, 10, 0,
                MathUtils.toTicks(1.42F), 20, 5.0F, true, 35.0D) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && PossessedPaladinServant.this.getRandom().nextFloat() * 100.0F
                        < (float) PossessedPaladinServant.this.backflipRandom
                        && PossessedPaladinServant.this.backstep_cooldown <= 0;
            }

            @Override
            public void stop() {
                PossessedPaladinServant.this.backstep_cooldown = PossessedPaladinServant.this.BACKSTEP_COOLDOWN;
                super.stop();
            }
        });

        this.goalSelector.addGoal(1, new PossessedPaladinAttackGoal(this, 0, 38, 0,
                MathUtils.toTicks(5.42F), 72, 16.0F, true, 35.0D) {
            @Override
            public boolean canUse() {
                if (PossessedPaladinServant.this.getPhase() < 2) {
                    return false;
                }
                return super.canUse()
                        && PossessedPaladinServant.this.getRandom().nextFloat() * 100.0F
                        < (float) PossessedPaladinServant.this.throwTridentSpinRandom
                        && PossessedPaladinServant.this.trident_throw_spin <= 0
                        && PossessedPaladinServant.this.targetIsNotNull()
                        && PossessedPaladinServant.this.distanceTo(PossessedPaladinServant.this.target()) >= 7.0F;
            }

            @Override
            public void stop() {
                PossessedPaladinServant.this.trident_throw_spin = PossessedPaladinServant.this.TRIDENT_THROW_SPIN;
                super.stop();
            }
        });

        this.goalSelector.addGoal(1, new PossessedPaladinFlipSmashGoal(this, 0, 12, 0,
                MathUtils.toTicks(1.63F), 13, 16.0F, false, 10.0D) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && PossessedPaladinServant.this.getRandom().nextFloat() * 100.0F
                        < (float) PossessedPaladinServant.this.flipSmashRandom
                        && PossessedPaladinServant.this.flip_smash_cooldown <= 0;
            }
        });

        this.goalSelector.addGoal(0, new PossessedPaladinStateGoal(this, 13, 13, 0,
                MathUtils.toTicks(1.04F), 20, true, 35.0D) {
            @Override
            public void stop() {
                PossessedPaladinServant.this.flip_smash_cooldown = PossessedPaladinServant.this.FLIP_SMASH_COOLDOWN;
                super.stop();
            }
        });

        this.goalSelector.addGoal(0, new PossessedPaladinStateGoal(this, 14, 14, 0,
                MathUtils.toTicks(2.38F), 8, true, 35.0D) {
            @Override
            public void stop() {
                PossessedPaladinServant.this.flip_smash_cooldown = PossessedPaladinServant.this.FLIP_SMASH_COOLDOWN;
                super.stop();
            }
        });

        this.goalSelector.addGoal(1, new PossessedPaladinThrowDaggersGoal(this, 0, 15, 0,
                MathUtils.toTicks(2.04F), 30, 12.0F, true, 25.0D) {
            @Override
            public boolean canUse() {
                LivingEntity target = PossessedPaladinServant.this.getTarget();
                return super.canUse()
                        && PossessedPaladinServant.this.getRandom().nextFloat() * 40.0F
                        < (float) PossessedPaladinServant.this.throwRandom
                        && PossessedPaladinServant.this.throw_cooldown <= 0
                        && PossessedPaladinServant.this.getPhase() < 2
                        && target != null;
            }

            @Override
            public void stop() {
                PossessedPaladinServant.this.throw_cooldown = PossessedPaladinServant.this.THROW_COOLDOWN;
                super.stop();
            }
        });

        this.goalSelector.addGoal(1, new PossessedPaladinThrowDaggersGoal(this, 0, 28, 0,
                MathUtils.toTicks(3.5F), 50, 12.0F, true, 25.0D) {
            @Override
            public boolean canUse() {
                LivingEntity target = PossessedPaladinServant.this.getTarget();
                return super.canUse()
                        && PossessedPaladinServant.this.getRandom().nextFloat() * 40.0F
                        < (float) PossessedPaladinServant.this.throwRandom
                        && PossessedPaladinServant.this.throw_cooldown <= 0
                        && PossessedPaladinServant.this.getPhase() >= 2
                        && target != null;
            }

            @Override
            public void stop() {
                PossessedPaladinServant.this.throw_cooldown = PossessedPaladinServant.this.THROW_COOLDOWN;
                super.stop();
            }
        });

        this.goalSelector.addGoal(1, new PossessedPaladinSlashFromGoal(this, 0, 16, 0,
                20, 20, 5.0F, false, 10.0F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && PossessedPaladinServant.this.getRandom().nextFloat() * 100.0F
                        < (float) PossessedPaladinServant.this.SlashFromRandom
                        && PossessedPaladinServant.this.slash_from_cooldown <= 0;
            }
        });

        this.goalSelector.addGoal(0, new PossessedPaladinStateGoal(this, 18, 18, 0,
                MathUtils.toTicks(2.5F), 10, true, 25.0D) {
            @Override
            public void stop() {
                PossessedPaladinServant.this.slash_from_cooldown = PossessedPaladinServant.this.SLASH_FROM_COOLDOWN;
                super.stop();
            }
        });

        this.goalSelector.addGoal(0, new PossessedPaladinStateGoal(this, 17, 17, 0,
                30, 11, true, 15.0D) {
            @Override
            public void stop() {
                PossessedPaladinServant.this.slash_from_cooldown = PossessedPaladinServant.this.SLASH_FROM_COOLDOWN;
                super.stop();
            }
        });

        this.goalSelector.addGoal(0, new PossessedPaladinStabGrabGoal(this, 19, 19, 0,
                28, 20, false, 25.0D) {
            @Override
            public void stop() {
                PossessedPaladinServant.this.slash_from_cooldown = PossessedPaladinServant.this.SLASH_FROM_COOLDOWN;
                super.stop();
            }
        });

        this.goalSelector.addGoal(0, new PossessedPaladinStateGoal(this, 20, 20, 0,
                MathUtils.toTicks(7.5F), 0, true, 25.0D) {
            @Override
            public void start() {
                PossessedPaladinServant.this.succedGrabbing = false;
                super.start();
            }

            @Override
            public void stop() {
                PossessedPaladinServant.this.stab_grab_cooldown = PossessedPaladinServant.this.STAB_GRAB_COOLDOWN;
                PossessedPaladinServant.this.succedGrabbing = false;
                PossessedPaladinServant.this.slash_from_cooldown = PossessedPaladinServant.this.SLASH_FROM_COOLDOWN;
                super.stop();
            }
        });

        this.goalSelector.addGoal(0, new PossessedPaladinStateGoal(this, 21, 21, 0,
                MathUtils.toTicks(1.5F), 0, true, 25.0D) {
            @Override
            public void stop() {
                PossessedPaladinServant.this.stab_grab_cooldown = PossessedPaladinServant.this.STAB_GRAB_COOLDOWN;
                PossessedPaladinServant.this.succedGrabbing = false;
                PossessedPaladinServant.this.slash_from_cooldown = PossessedPaladinServant.this.SLASH_FROM_COOLDOWN;
                super.stop();
            }
        });

        this.goalSelector.addGoal(0, new PossessedPaladinStateGoal(this, 33, 33, 0,
                MathUtils.toTicks(2.63F), 12, false, 25.0D) {
            @Override
            public void stop() {
                PossessedPaladinServant.this.stab_grab_cooldown = PossessedPaladinServant.this.STAB_GRAB_COOLDOWN;
                PossessedPaladinServant.this.succedGrabbing = false;
                PossessedPaladinServant.this.slash_from_cooldown = PossessedPaladinServant.this.SLASH_FROM_COOLDOWN;
                super.stop();
            }
        });

        this.goalSelector.addGoal(1, new PossessedPaladinAttackMinGoal(this, 0, 22, 23,
                MathUtils.toTicks(1.67F), MathUtils.toTicks(1.67F), 16.0F, 6.0F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && PossessedPaladinServant.this.getRandom().nextFloat() * 100.0F
                                < (float) PossessedPaladinServant.this.JumpRandom
                        && PossessedPaladinServant.this.jump_cooldown <= 0
                        && PossessedPaladinServant.this.getTarget() != null;
            }
        });

        this.goalSelector.addGoal(1, new PossessedPaladinJumpFallGoal(this, 23, 23, 24,
                100, 0, false, 0.0D));

        this.goalSelector.addGoal(0, new PossessedPaladinStateGoal(this, 24, 24, 0,
                MathUtils.toTicks(3.25F), 0, true, 25.0D) {
            @Override
            public void stop() {
                PossessedPaladinServant.this.jump_cooldown = PossessedPaladinServant.this.JUMP_COOLDOWN;
                super.stop();
            }
        });

        this.goalSelector.addGoal(0, new PossessedPaladinJumpSmashComboGoal(this, 32, 32, 0,
                MathUtils.toTicks(5.79F), 0, true, 25.0D) {
            @Override
            public void stop() {
                PossessedPaladinServant.this.jump_cooldown = PossessedPaladinServant.this.JUMP_COOLDOWN;
                super.stop();
            }
        });

        this.goalSelector.addGoal(1, new PossessedPaladinAttackGoal(this, 0, 25, 0,
                MathUtils.toTicks(5.04F), MathUtils.toTicks(3.75F), 6.0F, false, 0.0D) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && PossessedPaladinServant.this.getRandom().nextFloat() * 100.0F
                        < (float) PossessedPaladinServant.this.ShieldSmashRandom
                        && PossessedPaladinServant.this.shield_smash_cooldown <= 0;
            }

            @Override
            public void stop() {
                PossessedPaladinServant.this.shield_smash_cooldown = PossessedPaladinServant.this.SHIELD_SMASH_COOLDOWN;
                super.stop();
            }
        });

        this.goalSelector.addGoal(1, new PossessedPaladinSideRollSpinGoal(this, 0, 29, 0,
                MathUtils.toTicks(4.88F), 0, 6.0F, true, 10.0D) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && PossessedPaladinServant.this.getRandom().nextFloat() * 100.0F
                        < (float) PossessedPaladinServant.this.SideRollSpinRandom
                        && PossessedPaladinServant.this.getNextSideRollSpinType() == 1
                        && PossessedPaladinServant.this.side_roll_spin_cooldown <= 0;
            }

            @Override
            public void stop() {
                PossessedPaladinServant.this.side_roll_spin_cooldown =
                        PossessedPaladinServant.this.SIDE_ROLL_SPIN_COOLDOWN;
                super.stop();
            }
        });

        this.goalSelector.addGoal(1, new PossessedPaladinSideRollSpinGoal(this, 0, 30, 0,
                MathUtils.toTicks(4.88F), 0, 6.0F, true, 10.0D) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && PossessedPaladinServant.this.getRandom().nextFloat() * 100.0F
                        < (float) PossessedPaladinServant.this.SideRollSpinRandom
                        && PossessedPaladinServant.this.getNextSideRollSpinType() == 2
                        && PossessedPaladinServant.this.side_roll_spin_cooldown <= 0;
            }

            @Override
            public void stop() {
                PossessedPaladinServant.this.side_roll_spin_cooldown =
                        PossessedPaladinServant.this.SIDE_ROLL_SPIN_COOLDOWN;
                super.stop();
            }
        });

        this.goalSelector.addGoal(1, new PossessedPaladinFinisherAttackGoal(this, 0, 37, 0,
                MathUtils.toTicks(16.33F), 0, 7.0F, false, 0.0D) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && PossessedPaladinServant.this.getPhase() >= 2
                        && PossessedPaladinServant.this.getRandom().nextFloat() * 100.0F
                        < (float) PossessedPaladinServant.this.FinisherRandom
                        && PossessedPaladinServant.this.finisher_cooldown <= 0;
            }

            @Override
            public void stop() {
                PossessedPaladinServant.this.finisher_cooldown = PossessedPaladinServant.this.FINISHER_COOLDOWN;
                super.stop();
            }
        });

        this.goalSelector.addGoal(0, new IStateGoal(this, 5, 5, 0,
                MathUtils.toTicks(2.92F), MathUtils.toTicks(3.71F)) {
            @Override
            public void stop() {
                PossessedPaladinServant.this.hasParried = false;
                PossessedPaladinServant.this.parry_cooldown = PossessedPaladinServant.this.PARRY_COOLDOWN;
                super.stop();
            }
        });

        this.goalSelector.addGoal(1, new IStateGoal(this, 34, 34, 35, 0, 0) {
            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && !PossessedPaladinServant.this.getIsAwakened();
            }

            @Override
            public void tick() {
                this.entity.setDeltaMovement(0.0D, this.entity.getDeltaMovement().y, 0.0D);
            }
        });

        this.goalSelector.addGoal(0, new IStateGoal(this, 35, 35, 0, MathUtils.toTicks(10.21F), 0) {
            @Override
            public void tick() {
                this.entity.setDeltaMovement(0.0D, this.entity.getDeltaMovement().y, 0.0D);
            }
        });

        this.goalSelector.addGoal(0, new IStateGoal(this, 36, 36, 0, MathUtils.toTicks(12.0F), 0));

    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
                                        MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData,
                                        @Nullable CompoundTag pDataTag) {
        if (pReason == MobSpawnType.MOB_SUMMONED && this.getTrueOwner() instanceof Player player) {
            if (countServants(player) >= MobsConfig.PossessedPaladinServantLimit.get()) {
                return null;
            }
        }

        SpawnGroupData spawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);

        if (pReason == MobSpawnType.MOB_SUMMONED) {
            this.setSleep(true);

            this.setAwakened(false);
        }

        return spawnData;
    }

    private int countServants(Player player) {
        int count = 0;
        if (player.level() instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof PossessedPaladinServant servant
                        && servant.getTrueOwner() == player) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide && this.isStaying()) {
            LivingEntity currentTarget = this.getTarget();
            if (currentTarget != null && currentTarget != this.getLastHurtByMob()) {
                this.setTarget(null);
            }
        }

        if (this.isVehicle() && this.getFirstPassenger() != null) {
            this.getFirstPassenger().setShiftKeyDown(false);
        }

        if (this.getAttackState() != 20 && this.getAttackState() != 19) {
            this.ejectPassengers();
        }

        if (this.parry_cooldown > 0) {
            --this.parry_cooldown;
        }
        if (this.slam_cooldown > 0) {
            --this.slam_cooldown;
        }
        if (this.throw_cooldown > 0) {
            --this.throw_cooldown;
        }
        if (this.stab_grab_cooldown > 0) {
            --this.stab_grab_cooldown;
        }
        if (this.flip_smash_cooldown > 0) {
            --this.flip_smash_cooldown;
        }
        if (this.side_roll_spin_cooldown > 0) {
            --this.side_roll_spin_cooldown;
        }
        if (this.backstep_cooldown > 0) {
            --this.backstep_cooldown;
        }
        if (this.slash_from_cooldown > 0) {
            --this.slash_from_cooldown;
        }
        if (this.double_slash_cooldown > 0) {
            --this.double_slash_cooldown;
        }
        if (this.jump_cooldown > 0) {
            --this.jump_cooldown;
        }
        if (this.shield_smash_cooldown > 0) {
            --this.shield_smash_cooldown;
        }
        if (this.finisher_cooldown > 0) {
            --this.finisher_cooldown;
        }
        if (this.trident_throw_spin > 0) {
            --this.trident_throw_spin;
        }

        if (this.BossInvulnerabilityTime > 0) {
            --this.BossInvulnerabilityTime;
        }

        this.tickBossLine();
        this.tickIdleTalk();

        if (this.level().isClientSide) {
            this.idleAnimationState.animateWhen(this.getAttackState() == 0, this.tickCount);
        }
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        setPersistenceRequired();
    }

    @Override
    public void die(DamageSource pDamageSource) {
        super.die(pDamageSource);
        this.deathTicks = 0;
        this.deathTime = 0;
        this.setAttackState(36);
        this.setNoGravity(false);
    }

    @Override
    protected void tickDeath() {
        ++this.deathTicks;

        this.deathTime = 0;

        if (this.deathTicks == MathUtils.toTicks(14.0F)) {
            this.remove(Entity.RemovalReason.KILLED);
            this.gameEvent(GameEvent.ENTITY_DIE);
        }
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return false;
    }

    private static final float CURSED_METAL_REPAIR_AMOUNT = 30.0F;

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);

        boolean isOwner = this.getTrueOwner() != null && pPlayer == this.getTrueOwner();

        if (this.tryAwaken(pPlayer, itemstack, isOwner)) {
            return InteractionResult.SUCCESS;
        }

        if (isOwner
                && this.getHealth() < this.getMaxHealth()
                && !this.isDeadOrDying()
                && itemstack.is(ModBlocks.CURSED_METAL_BLOCK.get().asItem())) {
            if (!pPlayer.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
            this.playSound(SoundEvents.IRON_GOLEM_REPAIR, 1.0F, 1.0F);
            this.heal(CURSED_METAL_REPAIR_AMOUNT);

            if (this.level() instanceof ServerLevel serverLevel) {
                for (int i = 0; i < 7; ++i) {
                    double d0 = this.random.nextGaussian() * 0.02D;
                    double d1 = this.random.nextGaussian() * 0.02D;
                    double d2 = this.random.nextGaussian() * 0.02D;
                    serverLevel.sendParticles(ParticleTypes.SOUL,
                            this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D),
                            0, d0, d1, d2, 0.5F);
                }
            }

            return InteractionResult.SUCCESS;
        }

        if (this.tryRevertToFirstPhase(pPlayer, itemstack, isOwner)) {
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(pPlayer, pHand);
    }

    private boolean tryRevertToFirstPhase(Player pPlayer, ItemStack itemstack, boolean isOwner) {
        if (!isOwner
                || this.isDeadOrDying()
                || !this.getIsSecondPhase()
                || this.getHealth() < this.getMaxHealth()
                || this.getAttackState() != 0
                || !itemstack.is(ModItems.METAL_DEBRIS.get())) {
            return false;
        }

        if (!pPlayer.getAbilities().instabuild) {
            itemstack.shrink(1);
        }

        this.setPhase(1);

        this.playSound(SoundEvents.BEACON_DEACTIVATE, 1.0F, 1.0F);

        if (this.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 24; ++i) {
                double angle = Math.PI * 2.0D * ((double) i / 24.0D);
                double dirX = Math.cos(angle);
                double dirZ = Math.sin(angle);
                serverLevel.sendParticles(ParticleTypes.SOUL,
                        this.getX() + dirX * 1.2D, this.getY() + 0.2D, this.getZ() + dirZ * 1.2D,
                        0, dirX * 0.05D, 0.08D, dirZ * 0.05D, 1.0F);
            }

            for (int i = 0; i < 15; ++i) {
                double d0 = this.random.nextGaussian() * 0.06D;
                double d1 = this.random.nextGaussian() * 0.06D;
                double d2 = this.random.nextGaussian() * 0.06D;
                serverLevel.sendParticles(ParticleTypes.SOUL,
                        this.getRandomX(1.2D), this.getRandomY() + 0.5D, this.getRandomZ(1.2D),
                        0, d0, d1, d2, 0.6F);
            }
        }

        return true;
    }

    @Override
    public boolean isAlliedTo(Entity pEntity) {
        if (super.isAlliedTo(pEntity)) {
            return true;
        }
        if (pEntity == null) {
            return false;
        }
        LivingEntity owner = this.getOwner();
        if (owner != null) {
            if (pEntity == owner) {
                return true;
            }
            if (pEntity instanceof OwnableEntity) {
                OwnableEntity other = (OwnableEntity) pEntity;
                if (other.getOwner() == owner) {
                    return true;
                }
            }
            if (owner.isAlliedTo(pEntity)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int attackDelayTicksValue() {
        return this.parry_cooldown <= 0 ? 3 : 1;
    }

    public boolean canParry() {
        return !this.hasParried
                && this.parry_cooldown <= 0
                && (this.getAttackState() == 0 || this.getAttackState() == 9);
    }

    public boolean isBlockin() {
        return this.getAttackState() == 5
                && this.attackTicks < MathUtils.toTicks(0.92F);
    }

    public void saveTargetPos() {
        if (targetIsNotNull()) {
            this.lastTargetX = this.target().getX();
            this.lastTargetY = this.target().getY();
            this.lastTargetZ = this.target().getZ();
        }
    }

    public void saveTargetPos(double x, double y, double z) {
        if (targetIsNotNull()) {
            this.lastTargetX = x;
            this.lastTargetY = y;
            this.lastTargetZ = z;
        }
    }

    public Vec3 lastTargetPos() {
        return new Vec3(this.lastTargetX, this.lastTargetY, this.lastTargetZ);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.UpdateWithAttack();
    }

    public void UpdateWithAttack() {
        float sweepSize = 2.0F;
        float sweepRot = 20.0F;
        float bigSweepHeight = 3.0F;
        float bigSweepAdditionalY = 1.0F;
        float doubleSlashRange = 3.5F;
        float smashRange = 5.0F;
        float hitBoxWidth = 0.5F;
        float verticalAttackHeight = 2.0F;
        float doubleSlashAttack1 = 20.0F;
        float doubleSlashAttack2 = 36.0F;
        float doubleSlashAttack3 = 62.0F;

        float f = Mth.cos(this.yBodyRot * ((float) Math.PI / 180F));
        float f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180F));
        double theta = (double) this.yBodyRot * (Math.PI / 180D);
        ++theta;
        double vecX = Math.cos(theta);
        double vecZ = Math.sin(theta);

        int slamAttackTick = MathUtils.toTicks(1.17F);

        if (this.getAttackState() == 6) {
            if (this.attackTicks == slamAttackTick - 5) {
                this.saveTargetPos();
            }

            if (this.attackTicks == slamAttackTick - 3) {
                double d0 = this.getX();
                double d1 = this.getY() + (double) bigSweepAdditionalY;
                double d2 = this.getZ();
                float yaw = (float) Math.toRadians((double) (-this.yBodyRot + 90.0F));
                float pitch = (float) Math.toRadians((double) (-this.getXRot() + 180.0F));
                if (this.level().isClientSide) {
                    if (this.getPhase() < 2) {
                        this.level().addParticle(new SoulSweepParticle.SweepData(2.0F, yaw, pitch),
                                d0, d1, d2, 0.0D, 0.0D, 0.0D);
                    } else {
                        this.level().addParticle(new SoulSweepRedParticle.SweepData(2.0F, yaw, pitch),
                                d0, d1, d2, 0.0D, 0.0D, 0.0D);
                    }
                }
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.0F);
            }

            if (this.attackTicks == slamAttackTick) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.15F, 5, 10);
                this.spawnSoulPillar(3.0F, 0.0F, 5);
                this.playSound(ModSounds.POWERFUL_SWORD_IMPACT2.get(), 1.0F, 1.0F);
                this.StraightLineAreaAttack(-0.35F, 2.5F, smashRange, 100, 18.0F, true, 1.5F);
            }
        }

        int counterTick = 24;
        if (this.getAttackState() == 8) {
            if (this.attackTicks > 5 && this.attackTicks < 20 && this.targetIsNotNull()) {
                if (this.tickCount % 3 == 0) {
                    this.spawnSoulPillar(-1.0F, 0.0F, 1);
                }
                float distance = this.distanceTo(this.target());
                float multiplier = Math.min(distance * 0.025F, 0.15F);
                this.calculatedDash(multiplier);
            }

            if (this.attackTicks == counterTick - 4) {
                this.saveTargetPos();
            }

            if (this.attackTicks == counterTick - 3) {
                double d0 = this.getX();
                double d1 = this.getY() + (double) bigSweepAdditionalY;
                double d2 = this.getZ();
                float yaw = (float) Math.toRadians((double) (-this.yBodyRot + 90.0F));
                float pitch = (float) Math.toRadians((double) (-this.getXRot() + 180.0F));
                if (this.level().isClientSide) {
                    if (this.getPhase() < 2) {
                        this.level().addParticle(new SoulSweepParticle.SweepData(2.0F, yaw, pitch),
                                d0, d1, d2, 0.0D, 0.0D, 0.0D);
                    } else {
                        this.level().addParticle(new SoulSweepRedParticle.SweepData(2.0F, yaw, pitch),
                                d0, d1, d2, 0.0D, 0.0D, 0.0D);
                    }
                }
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.0F);
            }

            if (this.attackTicks == counterTick) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.15F, 5, 10);
                this.spawnCircleParticle(1.5F, 0.0F, 30.0F, true, 1.0F,
                        this.getPhase() >= 2 ? this.uR : 0.0F,
                        this.getPhase() >= 2 ? this.uG : 0.9F,
                        this.getPhase() >= 2 ? this.uB : 0.8F,
                        0.8F, Circle.EnumRingBehavior.GROW, 20);
                this.playSound(SoundEvents.TOTEM_USE, 1.0F, 0.65F);
                this.SideAreaAttack(3.5F, 3.0F, 100.0F, 0.0F, 0.0F, 20.0F, 100,
                        SoundEvents.EMPTY, 1.0F, true, 2.0F);
                this.randomizedSoulStrike(3, 4, 3);
                this.randomizedSoulStrike(2, 3, 5);
                this.randomizedSoulStrike(5, 6, 2);
                ParticleUtils.controlledSmashParticles(this, 2.5F, 0.0F, 0.0F, 0.5F, 1.0F);
            }
        }

        if (this.getAttackState() == 31) {
            if (this.attackTicks > 5 && this.attackTicks < 20 && this.targetIsNotNull()) {
                if (this.tickCount % 3 == 0) {
                    this.spawnSoulPillar(-1.0F, 0.0F, 1);
                }
                float distance = this.distanceTo(this.target());
                float multiplier = Math.min(distance * 0.025F, 0.15F);
                this.calculatedDash(multiplier);
            }

            if (this.attackTicks == counterTick - 4) {
                this.saveTargetPos();
            }

            if (this.attackTicks == counterTick - 3) {
                double d0 = this.getX();
                double d1 = this.getY() + (double) bigSweepAdditionalY;
                double d2 = this.getZ();
                float yaw = (float) Math.toRadians((double) (-this.yBodyRot + 90.0F));
                float pitch = (float) Math.toRadians((double) (-this.getXRot() + 180.0F));
                if (this.level().isClientSide) {
                    if (this.getPhase() < 2) {
                        this.level().addParticle(new SoulSweepParticle.SweepData(2.0F, yaw, pitch),
                                d0, d1, d2, 0.0D, 0.0D, 0.0D);
                    } else {
                        this.level().addParticle(new SoulSweepRedParticle.SweepData(2.0F, yaw, pitch),
                                d0, d1, d2, 0.0D, 0.0D, 0.0D);
                    }
                }
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.0F);
            }

            if (this.attackTicks == counterTick) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.15F, 5, 10);
                this.spawnCircleParticle(1.5F, 0.0F, 30.0F, true, 1.0F,
                        this.getPhase() >= 2 ? this.uR : 0.0F,
                        this.getPhase() >= 2 ? this.uG : 0.9F,
                        this.getPhase() >= 2 ? this.uB : 0.8F,
                        0.8F, Circle.EnumRingBehavior.GROW, 20);
                this.playSound(SoundEvents.TOTEM_USE, 1.0F, 0.65F);
                this.SideAreaAttack(3.5F, 3.0F, 100.0F, 0.0F, 0.0F, 20.0F, 100,
                        SoundEvents.EMPTY, 1.0F, true, 2.0F);
                this.randomizedSoulStrike(3, 4, 3);
                this.randomizedSoulStrike(2, 3, 5);
                this.randomizedSoulStrike(5, 6, 2);
                ParticleUtils.controlledSmashParticles(this, 2.5F, 0.0F, 0.0F, 0.5F, 1.0F);
            }

            if (this.attackTicks == 45) {
                this.spawnCircleParticle(0.0F, 0.0F, 100.0F, true, 2.0F,
                        this.getPhase() >= 2 ? this.uR : 0.0F,
                        this.getPhase() >= 2 ? this.uG : 0.9F,
                        this.getPhase() >= 2 ? this.uB : 0.8F,
                        0.8F, Circle.EnumRingBehavior.SHRINK, 30);
            }

            if (this.attackTicks == 53) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.1F, 5, 5);
                this.playSound(SoundEvents.WITHER_SHOOT, 1.0F, 1.0F);
                this.soulStrikeRing(1.0F, 15, 0.0F);
            }
        }

        if (this.getAttackState() == 25) {
            double totalRadius = 12.0D;
            int points = 16;
            int smashTick1 = 35;
            int smashTick2 = 59;

            if (this.attackTicks == 1) {
                this.ghostItemFade.setTimer(5);
            }
            if (this.attackTicks >= 1 && this.attackTicks <= 70 && this.ghostItemFade.getTimer() > 0) {
                this.ghostItemFade.decreaseTimer();
            }
            if (this.attackTicks == 64) {
                this.ghostItemFade.setTimer(0);
            }
            if (this.attackTicks >= 65) {
                this.ghostItemFade.increaseTimer();
            }

            if (this.attackTicks == smashTick1 - 7) {
                this.spawnSoulShieldRing(0, 2, points, totalRadius, false, true);
            }
            if (this.attackTicks == smashTick1 - 3) {
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.0F);
            }
            if (this.attackTicks == smashTick1) {
                this.SideAreaAttack(3.25F, 3.0F, 180.0F, 0.0F, 0.0F, 20.0F, 100,
                        SoundEvents.EMPTY, 0.0F, true, 1.0F);
                this.playSound(ModSounds.SOUL_SHIELD_SMASH.get(), 1.0F, 1.0F);
                this.spawnCircleParticle(0.0F, 0.0F, 100.0F, true, 2.0F,
                        this.getPhase() >= 2 ? this.uR : 0.0F,
                        this.getPhase() >= 2 ? this.uG : 0.9F,
                        this.getPhase() >= 2 ? this.uB : 0.8F,
                        0.8F, Circle.EnumRingBehavior.SHRINK, 30);
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.15F, 5, 5);
                this.spawnSoulShieldRing(0, 2, points, totalRadius, false, false);
            }

            if (this.attackTicks == smashTick2 - 7) {
                this.spawnSoulShieldRing(1, 2, points, 2.0D, true, true);
            }
            if (this.attackTicks == smashTick2 - 3) {
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.0F);
            }
            if (this.attackTicks == smashTick2) {
                this.playSound(ModSounds.STAB_HIT.get(), 1.0F, 1.0F);
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.15F, 5, 5);
                this.spawnCircleParticle(0.0F, 0.0F, 100.0F, true, 2.0F,
                        this.getPhase() >= 2 ? this.uR : 0.0F,
                        this.getPhase() >= 2 ? this.uG : 0.9F,
                        this.getPhase() >= 2 ? this.uB : 0.8F,
                        0.8F, Circle.EnumRingBehavior.GROW, 30);
                this.spawnSoulShieldRing(1, 2, points, 2.0D, true, false);
            }
        }

        if (this.getAttackState() == 26) {
            if (this.attackTicks == 30) {
                this.sendAdvancedHotBarMessage("message.goetyominous.possessed_paladin_servant.phase2.1",
                        ChatFormatting.AQUA, 10.0F);
            }

            if (this.attackTicks == 49) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.15F, 5, 5);
                this.SideAreaAttack(3.0F, 4.0F, 360.0F, 0.0F, 0.0F, 15.0F, 0,
                        SoundEvents.EMPTY, 0.0F, false, 0.0F);
                this.playSound(SoundEvents.TOTEM_USE, 1.0F, 0.75F);
                this.earthquakeEffect(15.0F, 0.25F);
            }

            if (this.attackTicks >= 49 && this.attackTicks < 51) {
                this.SphereParticle(ModParticles.GHOSTLY_SOUL.get(), 0.0F, 2.0F, 6.0F);
            }

            if (this.attackTicks >= 51 && this.attackTicks <= 53) {
                this.SphereParticle(ModParticles.GHOSTLY_SOUL_RED.get(), 0.0F, 2.0F, 6.0F);
            }

            if (this.attackTicks >= 51 && this.attackTicks < 55) {
                float f9 = (this.random.nextFloat() - 0.5F) * 8.0F;
                float f10 = (this.random.nextFloat() - 0.5F) * 4.0F;
                float f2 = (this.random.nextFloat() - 0.5F) * 8.0F;
                float f8 = (this.random.nextFloat() - 0.75F) * 5.0F;
                float f6 = (this.random.nextFloat() - 0.75F) * 3.0F;
                float f7 = (this.random.nextFloat() - 0.75F) * 5.0F;
                this.level().addParticle(this.getPhase() >= 2
                                ? ModParticles.SOUL_EXPLOSION_RED.get() : ModParticles.SOUL_EXPLOSION.get(),
                        this.getX() + (double) f9, this.getY() + 2.0D + (double) f10,
                        this.getZ() + (double) f2, 0.0D, 0.0D, 0.0D);
                this.level().addParticle(this.getPhase() >= 2
                                ? ModParticles.SOUL_EXPLOSION_RED.get() : ModParticles.SOUL_EXPLOSION.get(),
                        this.getX() + (double) f8, this.getY() + 2.0D + (double) f6,
                        this.getZ() + (double) f7, 0.0D, 0.0D, 0.0D);
                this.level().addParticle(this.getPhase() >= 2
                                ? ModParticles.GHOSTLY_SOUL_RED.get() : ModParticles.GHOSTLY_SOUL.get(),
                        this.getX() + (double) f9, this.getY() + 2.0D + (double) f10,
                        this.getZ() + (double) f2, 0.0D, 0.5D, 0.0D);
                this.level().addParticle(this.getPhase() >= 2
                                ? ModParticles.GHOSTLY_SOUL_RED.get() : ModParticles.GHOSTLY_SOUL.get(),
                        this.getX() + (double) f8, this.getY() + 2.0D + (double) f6,
                        this.getZ() + (double) f7, 0.0D, 0.5D, 0.0D);
            }

            if (this.attackTicks == MathUtils.toTicks(4.13F)) {
                this.sendAdvancedHotBarMessage("message.goetyominous.possessed_paladin_servant.phase2.2",
                        ChatFormatting.AQUA, 10.0F);
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.1F, 5, 5);
                this.playSound(SoundEvents.WITHER_SHOOT, 1.0F, 1.0F);
                this.soulStrikeRing(1.0F, 15, 0.0F);
            }
        }

        if (this.getAttackState() == 2) {
            if ((float) this.attackTicks == doubleSlashAttack1 - 3.0F) {
                this.createSweep(0.0F, 0.0F, bigSweepHeight, (double) bigSweepAdditionalY, false,
                        sweepSize, sweepRot, false);
                this.playSound(ModSounds.POSSESSED_PALADIN_SWING.get(), 1.0F, 0.75F);
                this.calculatedDash(0.25F);
            }

            if ((float) this.attackTicks == doubleSlashAttack1) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.05F, 0, 10);
                this.SideAreaAttack(doubleSlashRange, 3.0F, 180.0F, 0.0F, 0.0F, 15.0F, 100,
                        SoundEvents.EMPTY, 1.0F, false, 0.0F);
            }

            if ((float) this.attackTicks == doubleSlashAttack2 - 3.0F) {
                this.createSweep(0.0F, 0.0F, bigSweepHeight, (double) bigSweepAdditionalY, true,
                        sweepSize, sweepRot, false);
                this.playSound(ModSounds.POSSESSED_PALADIN_SWING.get(), 1.0F, 0.75F);
                this.calculatedDash(0.25F);
            }

            if ((float) this.attackTicks == doubleSlashAttack2) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.05F, 0, 10);
                this.SideAreaAttack(doubleSlashRange, 3.0F, 180.0F, 0.0F, 0.0F, 17.0F, 100,
                        SoundEvents.EMPTY, 1.0F, false, 0.0F);
            }
        }

        if (this.getAttackState() == 3) {
            if ((float) this.attackTicks == doubleSlashAttack1 - 3.0F) {
                this.createSweep(0.0F, 0.0F, bigSweepHeight, (double) bigSweepAdditionalY, false,
                        sweepSize, sweepRot, false);
                this.playSound(ModSounds.POSSESSED_PALADIN_SWING.get(), 1.0F, 0.75F);
                this.calculatedDash(0.25F);
            }

            if ((float) this.attackTicks == doubleSlashAttack1) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.05F, 0, 10);
                this.SideAreaAttack(doubleSlashRange, 3.0F, 180.0F, 0.0F, 0.0F, 15.0F, 100,
                        SoundEvents.EMPTY, 1.0F, false, 0.0F);
            }

            if ((float) this.attackTicks == doubleSlashAttack2 - 3.0F) {
                this.createSweep(0.0F, 0.0F, bigSweepHeight, (double) bigSweepAdditionalY, true,
                        sweepSize, sweepRot, false);
                this.playSound(ModSounds.POSSESSED_PALADIN_SWING.get(), 1.0F, 0.75F);
                this.calculatedDash(0.25F);
            }

            if ((float) this.attackTicks == doubleSlashAttack2) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.05F, 0, 10);
                this.SideAreaAttack(doubleSlashRange, 3.0F, 180.0F, 0.0F, 0.0F, 17.0F, 100,
                        SoundEvents.EMPTY, 1.0F, false, 0.0F);
            }

            if ((float) this.attackTicks == doubleSlashAttack3 - 5.0F) {
                this.saveTargetPos();
            }

            if ((float) this.attackTicks == doubleSlashAttack3 - 3.0F) {
                double d0 = this.getX();
                double d1 = this.getY() + (double) bigSweepAdditionalY;
                double d2 = this.getZ();
                float yaw = (float) Math.toRadians((double) (-this.yBodyRot + 90.0F));
                float pitch = (float) Math.toRadians((double) (-this.getXRot() + 180.0F));
                if (this.level().isClientSide) {
                    if (this.getPhase() < 2) {
                        this.level().addParticle(new SoulSweepParticle.SweepData(2.0F, yaw, pitch),
                                d0, d1, d2, 0.0D, 0.0D, 0.0D);
                    } else {
                        this.level().addParticle(new SoulSweepRedParticle.SweepData(2.0F, yaw, pitch),
                                d0, d1, d2, 0.0D, 0.0D, 0.0D);
                    }
                }
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.0F);
                this.calculatedDashToPositon(0.15F, this.lastTargetPos());
            }

            if ((float) this.attackTicks == doubleSlashAttack3) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.15F, 5, 10);
                this.spawnSoulPillar(3.0F, 0.0F, 5);
                this.playSound(ModSounds.POWERFUL_SWORD_IMPACT2.get(), 1.0F, 1.0F);
                this.StraightLineAreaAttack(-0.35F, 2.5F, smashRange, 100, 18.0F, true, 1.5F);
            }
        }

        if (this.getAttackState() == 10 && this.attackTicks == 8) {
            this.backStep(-1.5F, 0.2F);
        }

        if (this.getAttackState() == 5) {
            if (this.attackTicks == 27) {
                this.createSweep(0.0F, 0.0F, bigSweepHeight, (double) bigSweepAdditionalY, true,
                        sweepSize, sweepRot, false);
                this.playSound(ModSounds.POSSESSED_PALADIN_SWING.get(), 1.0F, 0.75F);
                this.calculatedDash(0.25F);
            }
            if (this.attackTicks == 30) {
                this.throwDaggers(1.0F, this.getX(), this.getY(), this.getZ(), 6, 30.0F, 0.0F, 10);
                this.SideAreaAttack(doubleSlashRange, 3.0F, 180.0F, 0.0F, 0.0F, 20.0F, 100,
                        SoundEvents.ANVIL_LAND, 1.0F, true, 2.0F);
            }
        }

        if (this.getAttackState() == 15) {
            if (this.attackTicks == 10) {
                this.backStep(-1.0F, 0.0F);
            }
            if (this.attackTicks == 12) {
                int distance = this.targetIsNotNull() ? (int) this.distanceTo(this.target()) : 0;
                this.throwDaggers(1.0F, this.getX(), this.getY(), this.getZ(), 3, 30.0F, 0.0F,
                        10 + distance);
                this.playSound(ModSounds.DAGGER_THROW.get(), 1.0F, 0.75F);
            }
        }

        if (this.getAttackState() == 28) {
            if (this.attackTicks == 10) {
                this.backStep(1.0F, 0.0F);
            }
            if (this.attackTicks == 12) {
                int distance = this.targetIsNotNull() ? (int) this.distanceTo(this.target()) : 0;
                this.throwDaggers(1.0F, this.getX(), this.getY(), this.getZ(), 3, 30.0F, 0.0F,
                        10 + distance);
                this.playSound(ModSounds.DAGGER_THROW.get(), 1.0F, 0.75F);
            }
            if (this.attackTicks == 28) {
                this.backStep(1.0F, 0.0F);
            }
            if (this.attackTicks == 30) {
                int distance = this.targetIsNotNull() ? (int) this.distanceTo(this.target()) : 0;
                this.throwDaggers(1.0F, this.getX(), this.getY(), this.getZ(), 4, 30.0F, 0.0F,
                        15 + distance);
                this.playSound(ModSounds.DAGGER_THROW.get(), 1.0F, 0.75F);
            }
            if (this.attackTicks == 32) {
                int distance = this.targetIsNotNull() ? (int) this.distanceTo(this.target()) : 0;
                this.throwDaggers(1.0F, this.getX(), this.getY(), this.getZ(), 3, 30.0F, 0.0F,
                        10 + distance);
            }
        }

        if (this.getAttackState() == 12) {
            if (this.attackTicks == 6 && this.targetIsNotNull()) {
                this.jumpTowardsPosition(this.target().getX(), this.target().getY(), this.target().getZ());
            }

            if (this.attackTicks == 16) {
                double d0 = this.getX();
                double d1 = this.getY() + (double) bigSweepAdditionalY;
                double d2 = this.getZ();
                float yaw = (float) Math.toRadians((double) (-this.yBodyRot + 90.0F));
                float pitch = (float) Math.toRadians((double) (-this.getXRot() + 180.0F));
                if (this.level().isClientSide) {
                    if (this.getPhase() < 2) {
                        this.level().addParticle(new SoulSweepParticle.SweepData(2.0F, yaw, pitch),
                                d0, d1, d2, 0.0D, 0.0D, 0.0D);
                    } else {
                        this.level().addParticle(new SoulSweepRedParticle.SweepData(2.0F, yaw, pitch),
                                d0, d1, d2, 0.0D, 0.0D, 0.0D);
                    }
                }
            }

            if (this.attackTicks == 19) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.15F, 5, 10);
                this.spawnSoulPillar(3.0F, 0.0F, 5);
                this.playSound(ModSounds.POWERFUL_SWORD_IMPACT2.get(), 1.0F, 1.0F);
                this.SideAreaAttack(1.0F, 3.0F, 70.0F, -180.0F, -0.5F, 18.0F, 100,
                        SoundEvents.EMPTY, 1.0F, true, 1.5F);
                this.StraightLineAreaAttack(-hitBoxWidth, verticalAttackHeight, smashRange, 100, 18.0F, true, 1.5F);
            }
        }

        if (this.getAttackState() == 14) {
            if (this.attackTicks == 2 && this.targetIsNotNull()) {
                this.jumpTowardsPosition(this.target().getX(), this.target().getY(), this.target().getZ());
            }

            if (this.attackTicks == 11) {
                double d0 = this.getX();
                double d1 = this.getY() + (double) bigSweepAdditionalY;
                double d2 = this.getZ();
                float yaw = (float) Math.toRadians((double) (-this.yBodyRot + 90.0F));
                float pitch = (float) Math.toRadians((double) (-this.getXRot() + 180.0F));
                if (this.level().isClientSide) {
                    if (this.getPhase() < 2) {
                        this.level().addParticle(new SoulSweepParticle.SweepData(2.0F, yaw, pitch),
                                d0, d1, d2, 0.0D, 0.0D, 0.0D);
                    } else {
                        this.level().addParticle(new SoulSweepRedParticle.SweepData(2.0F, yaw, pitch),
                                d0, d1, d2, 0.0D, 0.0D, 0.0D);
                    }
                }
            }

            if (this.attackTicks == 14) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.15F, 5, 10);
                this.spawnSoulPillar(3.0F, 0.0F, 5);
                this.playSound(ModSounds.POWERFUL_SWORD_IMPACT2.get(), 1.0F, 1.0F);
                this.SideAreaAttack(1.0F, 3.0F, 70.0F, -180.0F, -0.5F, 18.0F, 100,
                        SoundEvents.EMPTY, 1.0F, true, 1.5F);
                this.StraightLineAreaAttack(-hitBoxWidth, verticalAttackHeight, smashRange, 100, 21.0F, true, 1.5F);
            }
        }

        int slashFromAttack = 18;
        int stabAttack = 18;

        if (this.getAttackState() == 16) {
            if (this.attackTicks == slashFromAttack - 3) {
                this.createSweep(0.0F, 0.0F, bigSweepHeight, (double) bigSweepAdditionalY, true,
                        sweepSize, sweepRot, false);
                this.playSound(ModSounds.POSSESSED_PALADIN_SWING.get(), 1.0F, 0.75F);
                this.calculatedDash(0.25F);
            }

            if (this.attackTicks == slashFromAttack) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.05F, 0, 10);
                this.GambitedSideAreaAttack(doubleSlashRange, 3.0F, 180.0F, 0.0F, 0.0F, 17.0F, 100,
                        SoundEvents.EMPTY, 1.0F, false, 0.0F);
            }
        }

        if (this.getAttackState() == 18) {
            if (this.attackTicks == stabAttack - 8) {
                this.saveTargetPos();
            }

            if (this.attackTicks == stabAttack - 3) {
                this.playSound(ModSounds.POSSESSED_PALADIN_STAB.get(), 1.0F, 0.8F);
                this.calculatedDashToPositon(0.25F, this.lastTargetPos());
            }

            if (this.attackTicks == stabAttack) {
                this.StraightLineAreaAttack(-0.6F, 2.5F, smashRange, 100, 17.0F, true, 1.35F);
            }
        }

        int stabGrabAttack = 28;
        int stabStabAttack = 20;

        if (this.getAttackState() == 19) {
            if (this.attackTicks == stabGrabAttack - 8) {
                this.saveTargetPos();
            }

            if (this.attackTicks == stabGrabAttack - 3) {
                this.playSound(ModSounds.POSSESSED_PALADIN_STAB.get(), 1.0F, 0.8F);
                this.calculatedDashToPositon(0.35F, this.lastTargetPos());
            }

            if (this.attackTicks >= stabGrabAttack - 3 && this.attackTicks < stabGrabAttack) {
                float g = (float) Math.toRadians((double) (-this.getYRot() + 180.0F));
                double spawnX = this.getX() + vecX * 1.5D;
                double spawnZ = this.getZ() + vecZ * 1.5D;
                this.level().addParticle(new Circle.RingData(g, 0.0F, 30,
                                this.getPhase() >= 2 ? this.uR : 0.0F,
                                this.getPhase() >= 2 ? this.uG : 0.9F,
                                this.getPhase() >= 2 ? this.uB : 0.8F,
                                1.0F, 40.0F, false,
                                Circle.EnumRingBehavior.GROW_THEN_SHRINK),
                        spawnX, this.getY() + 1.0D, spawnZ, 0.0D, 0.0D, 0.0D);
            }

            if (this.attackTicks == stabGrabAttack) {
                this.StabGrab(-0.6F, 3.0F, 4.0F, 100, 17.0F, true, 1.35F);
            }
        }

        if (this.getAttackState() == 33) {
            if (this.attackTicks == stabStabAttack - 8) {
                this.saveTargetPos();
            }

            if (this.attackTicks == stabAttack - 2) {
                this.playSound(ModSounds.POSSESSED_PALADIN_STAB.get(), 1.0F, 0.8F);
            }

            if (this.attackTicks == stabAttack - 3) {
                this.calculatedDashToPositon(0.25F, this.lastTargetPos());
            }

            if (this.attackTicks == stabStabAttack) {
                this.StraightLineAreaAttack(-0.6F, 2.5F, smashRange, 100, 17.0F, true, 1.35F);
            }
        }

        if (this.getAttackState() == 20) {
            if (this.attackTicks < 20) {
                this.soulRaysCount = 0;
            }

            if (this.attackTicks == 20 || this.attackTicks == 30 || this.attackTicks == 40) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 10.0F, 0.1F, 0, 10);

                for (Entity entity : this.getPassengers()) {
                    if (!(entity instanceof LivingEntity livingPassenger)) {
                        continue;
                    }
                    boolean hurt = livingPassenger.hurt(ModDamageTypes.causeGhostlyDamage(this, this),
                            (float) (2.0D + (double) MathUtils.entityBasedHpDamage(livingPassenger, 5.0F)
                                    * (Double) ModConfig.MOB_CONFIG.PosessedPaladinDamageMutliplier.get()));
                    if (hurt) {
                        this.heal(3.0F + MathUtils.entityBasedHpDamage(livingPassenger, 0.25F));
                    }
                }

                this.spawnCircleParticle(2.0F, -1.0F, 30.0F, false, 1.0F,
                        this.getPhase() >= 2 ? this.uR : 0.0F,
                        this.getPhase() >= 2 ? this.uG : 0.9F,
                        this.getPhase() >= 2 ? this.uB : 0.8F,
                        0.8F, Circle.EnumRingBehavior.GROW, 20, 4.0D, true);
                ++this.soulRaysCount;
            }

            if (this.attackTicks == 20) {
                this.playSound(SoundEvents.RESPAWN_ANCHOR_CHARGE, 1.0F, 0.75F);
            }
            if (this.attackTicks == 30) {
                this.playSound(SoundEvents.RESPAWN_ANCHOR_CHARGE, 1.0F, 1.0F);
            }
            if (this.attackTicks == 40) {
                this.playSound(SoundEvents.RESPAWN_ANCHOR_CHARGE, 1.0F, 1.25F);
            }
            if (this.attackTicks == 60) {
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.0F);
            }

            if (this.attackTicks == 63) {
                this.soulRaysCount = 0;
                double d0 = this.getX();
                double d1 = this.getY() + (double) bigSweepAdditionalY;
                double d2 = this.getZ();
                float yaw = (float) Math.toRadians((double) (-this.yBodyRot + 90.0F));
                float pitch = (float) Math.toRadians((double) (-this.getXRot() + 180.0F));
                if (this.level().isClientSide) {
                    if (this.getPhase() < 2) {
                        this.level().addParticle(new SoulSweepParticle.SweepData(2.0F, yaw, pitch),
                                d0, d1, d2, 0.0D, 0.0D, 0.0D);
                    } else {
                        this.level().addParticle(new SoulSweepRedParticle.SweepData(2.0F, yaw, pitch),
                                d0, d1, d2, 0.0D, 0.0D, 0.0D);
                    }
                }
                this.playSound(SoundEvents.TOTEM_USE, 1.0F, 1.0F);
                Vec3 pos = new Vec3(this.getX(), this.getY(), this.getZ());
                CameraShakeEntity.cameraShake(this.level(), pos, 20.0F, 0.25F, 0, 20);
                this.spawnCircleParticle(1.5F, -0.25F, 50.0F, true, 5.0F,
                        this.getPhase() >= 2 ? this.uR : 0.25F,
                        this.getPhase() >= 2 ? this.uG : 1.0F,
                        this.getPhase() >= 2 ? this.uB : 0.75F, 1.0F);
                this.SideAreaAttack(4.0F, 4.0F, 360.0F, 0.0F, 1.0F, 10.0F, 0,
                        SoundEvents.EMPTY, 0.0F, false, 0.0F);
                ParticleUtils.controlledSmashParticles(this, 2.0F, 0.0F, 0.0F, 7.5F, 3.5F);
            }

            if (this.attackTicks >= 63 && this.attackTicks <= 68) {
                this.SphereParticle(this.getPhase() >= 2
                                ? ModParticles.GHOSTLY_SOUL_RED.get()
                                : ModParticles.GHOSTLY_SOUL.get(),
                        0.35F, 2.0F, 3.0F);
            }

            if (this.attackTicks == 106) {
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.0F);
            }

            if (this.attackTicks == 109) {
                this.createSweep(0.0F, 0.0F, bigSweepHeight, (double) bigSweepAdditionalY, false,
                        sweepSize, sweepRot, false);
                float destVec = 15.0F;
                float destoffset = 0.0F;
                float vec = 3.0F;
                float offset = 0.0F;
                Entity firstPassenger = this.getFirstPassenger();
                if (firstPassenger instanceof LivingEntity livingPassenger
                        && !this.level().isClientSide) {
                    this.throwAnGravityEntity(1.0F,
                            this.getX() + (double) destVec * vecX + (double) (f * destoffset),
                            this.getY() + 2.0D,
                            this.getZ() + (double) destVec * vecZ + (double) (f1 * destoffset),
                            this.getX() + (double) vec * vecX + (double) (f * offset),
                            this.getY() + 1.0D,
                            this.getZ() + (double) vec * vecZ + (double) (f1 * offset),
                            1.0F, livingPassenger);
                }
            }
        }

        if (this.getAttackState() == 22 && this.attackTicks == 24) {
            LivingEntity jumpTarget = this.getTarget();
            if (jumpTarget != null) {
                double d0 = jumpTarget.getX() - this.getX();
                double d1 = jumpTarget.getY() - this.getY();
                double d2 = jumpTarget.getZ() - this.getZ();
                double mult = 0.2D;
                this.setDeltaMovement(new Vec3(d0,
                        0.7D + Mth.clamp(d1 * 0.075D, 0.0D, 10.0D), d2).multiply(mult, 1.0D, mult));
            } else {
                this.setDeltaMovement(new Vec3(0.0D, 0.7D, 0.0D));
            }
        }

        if (this.getAttackState() == 23 && this.onGround()) {
            this.setAttackState(this.getPhase() >= 2 ? 32 : 24);
        }

        if (this.getAttackState() == 24) {
            if (this.attackTicks == 4) {
                this.SideAreaAttack(3.0F, 3.0F, 180.0F, 0.0F, 0.0F, 24.0F, 100,
                        SoundEvents.EMPTY, 0.0F, true, 1.5F);
                this.playSound(SoundEvents.TOTEM_USE, 1.0F, 0.75F);
                this.strikeZigzagXBlades(12, 1.0D, false, 10.0F, 4.0F, 1, 2.0D, true);
                this.spawnCircleParticle(1.5F, -0.25F, 60.0F, true, 5.0F,
                        this.getPhase() >= 2 ? this.uR : 0.25F,
                        this.getPhase() >= 2 ? this.uG : 1.0F,
                        this.getPhase() >= 2 ? this.uB : 0.75F, 1.0F);
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.25F, 0, 20);
                ParticleUtils.controlledSmashParticles(this, 1.0F, 0.0F, 0.0F, 7.5F, 3.0F);
            }

            if (this.attackTicks == 7) {
                this.strikeZigzagXBlades(12, 1.0D, false, 12.0F, 4.0F, 1, 2.0D, false);
            }
        }

        if (this.getAttackState() == 32) {
            if (this.attackTicks == 1) {
                this.telegraphFadeAway.resetTimer();
            }

            if (this.attackTicks == 4) {
                this.SideAreaAttack(3.0F, 3.0F, 180.0F, 0.0F, 0.0F, 24.0F, 100,
                        SoundEvents.EMPTY, 0.0F, true, 1.5F);
                this.playSound(SoundEvents.TOTEM_USE, 1.0F, 0.75F);
                this.strikeZigzagXBlades(12, 1.0D, false, 10.0F, 4.0F, 1, 2.0D, true);
                this.spawnCircleParticle(1.5F, -0.25F, 60.0F, true, 5.0F,
                        this.getPhase() >= 2 ? this.uR : 0.25F,
                        this.getPhase() >= 2 ? this.uG : 1.0F,
                        this.getPhase() >= 2 ? this.uB : 0.75F, 1.0F);
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.25F, 0, 20);
                ParticleUtils.controlledSmashParticles(this, 1.0F, 0.0F, 0.0F, 7.5F, 3.0F);
            }

            if (this.attackTicks == 7) {
                this.strikeZigzagXBlades(12, 1.0D, false, 12.0F, 4.0F, 1, 2.0D, false);
            }

            if (this.attackTicks == 30) {
                float offset = -0.5F;
                float vec = this.targetIsNotNull() ? 5.0F + this.distanceTo(this.target()) * 0.5F : 5.0F;
                this.saveTargetPos(
                        this.getX() + (double) vec * vecX + (double) (f * offset),
                        this.getY(),
                        this.getZ() + (double) vec * vecZ + (double) (f1 * offset));
            }

            if (this.attackTicks == 38) {
                this.playSound(ModSounds.POSSESSED_PALADIN_SWING.get(), 1.0F, 0.75F);
                this.calculatedDashToPositon(0.25F, this.lastTargetPos());
            }

            if (this.attackTicks == 41) {
                this.SideAreaAttack(3.0F, 3.0F, 180.0F, 0.0F, 0.0F, 20.0F, 100,
                        SoundEvents.EMPTY, 0.0F, true, 1.5F);
            }

            if (this.attackTicks == 50) {
                this.spawnSoulPillar(0.0F, -2.0F, 1);
                this.spawnSoulPillar(0.0F, 2.0F, 1);
            }

            if (this.attackTicks >= 55 && this.attackTicks <= 56) {
                this.saveTargetPos();
            }

            if (this.attackTicks >= 56) {
                this.telegraphFadeAway.increaseTimer();
            }

            if (this.attackTicks >= 58 && this.attackTicks < 62) {
                this.spawnSoulPillar(0.0F, -2.0F, 1);
                this.spawnSoulPillar(0.0F, 2.0F, 1);
                this.spawnSoulPillar(0.0F, 0.0F, 1);
                this.calculatedDashToPositon(0.2F, this.lastTargetPos());
            }

            if (this.attackTicks == 67) {
                this.SideAreaAttack(3.0F, 3.0F, 100.0F, 0.0F, 0.0F, 24.0F, 100,
                        SoundEvents.EMPTY, 0.0F, true, 1.5F);
                this.playSound(SoundEvents.TOTEM_USE, 1.0F, 0.75F);
                this.strikeZigzagXBlades(12, 1.0D, true, 10.0F, 4.0F, 1, 2.0D, true);
                this.spawnCircleParticle(1.5F, -0.25F, 60.0F, true, 5.0F,
                        this.getPhase() >= 2 ? this.uR : 0.25F,
                        this.getPhase() >= 2 ? this.uG : 1.0F,
                        this.getPhase() >= 2 ? this.uB : 0.75F, 1.0F);
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.25F, 0, 20);
                ParticleUtils.controlledSmashParticles(this, 1.0F, 0.0F, 0.0F, 7.5F, 3.0F);
            }

            if (this.attackTicks == 70) {
                this.strikeZigzagXBlades(12, 1.0D, true, 12.0F, 4.0F, 1, 2.0D, false);
            }
        }

        if (this.getAttackState() == 29) {
            float attackTick1 = 41.0F;
            float attackTick2 = 54.0F;
            float dashA = 0.6F;
            int arc = 360;

            if (this.attackTicks == 4) {
                float vec = 0.0F;
                float offset = 2.0F;
                float scale = 1.0F;
                Vec3 rollPos = new Vec3(
                        this.getX() + (double) vec * vecX + (double) (f * offset),
                        this.getY(),
                        this.getZ() + (double) vec * vecZ + (double) (f1 * offset));
                Vec3 sub = this.position().subtract(rollPos);
                Vec3 finalPos = sub.scale((double) scale);
                this.setDeltaMovement(finalPos.x, this.getDeltaMovement().y, finalPos.z);
                this.playSound(ModSounds.POSSESSED_PALADIN_ROLL.get(), 1.0F, 1.0F);
            }

            if ((float) this.attackTicks == attackTick1 - 3.0F) {
                this.createSweep(0.0F, 0.0F, bigSweepHeight, (double) bigSweepAdditionalY, true,
                        sweepSize, sweepRot, false);
                this.playSound(ModSounds.POSSESSED_PALADIN_SWING.get(), 1.0F, 0.75F);
            }

            if ((float) this.attackTicks >= attackTick1 - 3.0F && (float) this.attackTicks < attackTick2) {
                this.basicDash(dashA, 0.0F, false);
                if (this.tickCount % 3 == 0) {
                    this.spawnSoulPillar(-1.0F, 0.0F, 1);
                    this.spawnSoulPillar(-1.0F, 2.0F, 1);
                    this.spawnSoulPillar(-1.0F, -2.0F, 1);
                }
            }

            if ((float) this.attackTicks == attackTick1) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.05F, 0, 10);
                this.SideAreaAttack(doubleSlashRange, 3.0F, (float) arc, 0.0F, 0.0F, 17.0F, 100,
                        SoundEvents.EMPTY, 1.0F, false, 0.0F);
            }

            if ((float) this.attackTicks == attackTick2) {
                this.createSweep(0.0F, 0.0F, bigSweepHeight, (double) bigSweepAdditionalY, true,
                        sweepSize, sweepRot, false);
                this.playSound(ModSounds.POSSESSED_PALADIN_SWING.get(), 1.0F, 0.75F);
            }

            if ((float) this.attackTicks == attackTick2) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.05F, 0, 10);
                this.SideAreaAttack(doubleSlashRange, 3.0F, (float) arc, 0.0F, 0.0F, 17.0F, 100,
                        SoundEvents.EMPTY, 1.0F, false, 0.0F);
            }
        }

        if (this.getAttackState() == 30) {
            float attackTick1 = 41.0F;
            float attackTick2 = 54.0F;
            float dashA = 0.6F;
            int arc = 360;

            if (this.attackTicks == 4) {
                float vec = 0.0F;
                float offset = -2.0F;
                float scale = 1.0F;
                Vec3 rollPos = new Vec3(
                        this.getX() + (double) vec * vecX + (double) (f * offset),
                        this.getY(),
                        this.getZ() + (double) vec * vecZ + (double) (f1 * offset));
                Vec3 sub = this.position().subtract(rollPos);
                Vec3 finalPos = sub.scale((double) scale);
                this.setDeltaMovement(finalPos.x, this.getDeltaMovement().y, finalPos.z);
                this.playSound(ModSounds.POSSESSED_PALADIN_ROLL.get(), 1.0F, 1.0F);
            }

            if ((float) this.attackTicks == attackTick1 - 3.0F) {
                this.createSweep(0.0F, 0.0F, bigSweepHeight, (double) bigSweepAdditionalY, true,
                        sweepSize, sweepRot, false);
                this.playSound(ModSounds.POSSESSED_PALADIN_SWING.get(), 1.0F, 0.75F);
            }

            if ((float) this.attackTicks >= attackTick1 - 3.0F && (float) this.attackTicks < attackTick2) {
                this.basicDash(dashA, 0.0F, false);
                if (this.tickCount % 3 == 0) {
                    this.spawnSoulPillar(-1.0F, 0.0F, 1);
                    this.spawnSoulPillar(-1.0F, 2.0F, 1);
                    this.spawnSoulPillar(-1.0F, -2.0F, 1);
                }
            }

            if ((float) this.attackTicks == attackTick1) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.05F, 0, 10);
                this.SideAreaAttack(doubleSlashRange, 3.0F, (float) arc, 0.0F, 0.0F, 18.0F, 100,
                        SoundEvents.EMPTY, 1.0F, false, 0.0F);
            }

            if ((float) this.attackTicks == attackTick2) {
                this.createSweep(0.0F, 0.0F, bigSweepHeight, (double) bigSweepAdditionalY, true,
                        sweepSize, sweepRot, false);
                this.playSound(ModSounds.POSSESSED_PALADIN_SWING.get(), 1.0F, 0.75F);
            }

            if ((float) this.attackTicks == attackTick2) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.05F, 0, 10);
                this.SideAreaAttack(doubleSlashRange, 3.0F, (float) arc, 0.0F, 0.0F, 18.0F, 100,
                        SoundEvents.EMPTY, 1.0F, false, 0.0F);
            }
        }

        if (this.getAttackState() == 35) {
            float playerHearTalking = 10.0F;

            if (this.attackTicks == 1) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.075F, 0, 20);
            }

            if (this.attackTicks == 40) {
                this.sendAdvancedHotBarMessage("message.goetyominous.possessed_paladin_servant.awaken.1",
                        ChatFormatting.AQUA, playerHearTalking);
            }

            if (this.attackTicks == 80) {
                this.sendAdvancedHotBarMessage("message.goetyominous.possessed_paladin_servant.awaken.2",
                        ChatFormatting.AQUA, playerHearTalking);
            }

            if (this.attackTicks == 143) {
                this.createSweep(0.0F, 0.0F, bigSweepHeight, (double) bigSweepAdditionalY, true,
                        sweepSize, sweepRot, false);
                this.playSound(ModSounds.POSSESSED_PALADIN_SWING.get(), 1.0F, 0.75F);
            }

            if (this.attackTicks == 146) {
                this.SideAreaAttack(doubleSlashRange, 3.0F, 180.0F, 0.0F, 0.0F, 18.0F, 100,
                        SoundEvents.EMPTY, 1.0F, false, 0.0F);
            }
        }

        if (this.getAttackState() == 36) {
            float playerHearTalking = 10.0F;

            float vec = 1.0F;
            float offset = 0.0F;
            float f3 = (this.random.nextFloat() - 0.0F) * 0.5F;
            float f4 = (this.random.nextFloat() - 0.0F) * 0.5F;
            float f5 = (this.random.nextFloat() - 0.0F) * 0.5F;
            int stab1 = 85;
            int stab2 = 115;

            if (this.attackTicks == 1) {
                this.rayAmount = 0;
            }

            if (this.attackTicks >= 1 && this.tickCount % 10 == 0) {
                float f9 = (this.random.nextFloat() - 0.5F) * 4.0F;
                float f10 = (this.random.nextFloat() - 0.5F) * 2.0F;
                float f2 = (this.random.nextFloat() - 0.5F) * 4.0F;
                this.level().addParticle(this.getPhase() >= 2
                                ? ModParticles.SOUL_EXPLOSION_RED.get() : ModParticles.SOUL_EXPLOSION.get(),
                        this.getX() + (double) f9, this.getY() + 2.0D + (double) f10,
                        this.getZ() + (double) f2, 0.0D, 0.0D, 0.0D);
                this.level().addParticle(this.getPhase() >= 2
                                ? ModParticles.GHOSTLY_SOUL_RED.get() : ModParticles.GHOSTLY_SOUL.get(),
                        this.getX() + (double) f9, this.getY() + 2.0D + (double) f10,
                        this.getZ() + (double) f2, 0.0D, 0.5D, 0.0D);
            }

            if (this.attackTicks == 20) {
                this.sendAdvancedHotBarMessage("message.goetyominous.possessed_paladin_servant.death.1",
                        ChatFormatting.RED, playerHearTalking);
            }

            if (this.attackTicks == stab1) {
                this.playSound(ModSounds.POSSESSED_PALADIN_STAB.get(), 1.0F, 0.75F);
                if (this.level().isClientSide) {
                    this.level().addParticle(this.getPhase() >= 2
                                    ? ModParticles.SOUL_EXPLOSION_RED.get() : ModParticles.SOUL_EXPLOSION.get(),
                            this.getX() + (double) vec * vecX + (double) (f * offset) + (double) f3,
                            this.getY() + 1.0D + (double) f4,
                            this.getZ() + (double) vec * vecZ + (double) (f1 * offset) + (double) f5,
                            0.0D, 0.0D, 0.0D);
                }
            }

            if (this.attackTicks >= stab1) {
                for (int i = 0; (double) i < 0.5; ++i) {
                    if (this.level().isClientSide) {
                        this.level().addParticle(this.getPhase() >= 2
                                        ? ModParticles.GHOSTLY_SOUL_RED.get() : ModParticles.GHOSTLY_SOUL.get(),
                                this.getRandomX(1.0D), this.getRandomY(), this.getRandomZ(1.0D),
                                0.0D, 0.025D, 0.0D);
                    }
                }
            }

            if (this.attackTicks == stab2) {
                if (this.level().isClientSide) {
                    this.level().addParticle(this.getPhase() >= 2
                                    ? ModParticles.SOUL_EXPLOSION_RED.get() : ModParticles.SOUL_EXPLOSION.get(),
                            this.getX() + (double) vec * vecX + (double) (f * offset) + (double) f3,
                            this.getY() + 1.0D + (double) f4,
                            this.getZ() + (double) vec * vecZ + (double) (f1 * offset) + (double) f5,
                            0.0D, 0.0D, 0.0D);
                }
                this.playSound(ModSounds.STAB_HIT.get(), 1.0F, 1.0F);
            }

            if (this.attackTicks == 180) {
                ++this.rayAmount;
                this.playSound(SoundEvents.RESPAWN_ANCHOR_CHARGE, 1.0F, 0.75F);
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.1F, 5, 5);
                this.sendAdvancedHotBarMessage("message.goetyominous.possessed_paladin_servant.death.2",
                        ChatFormatting.RED, playerHearTalking);
            }

            if (this.attackTicks == 240) {
                this.level().addParticle(this.getPhase() >= 2
                                ? ModParticles.SOUL_EXPLOSION_RED.get() : ModParticles.SOUL_EXPLOSION.get(),
                        this.getX() + (double) vec * vecX + (double) (f * offset) + (double) f3,
                        this.getY() + 2.0D + (double) f4,
                        this.getZ() + (double) vec * vecZ + (double) (f1 * offset) + (double) f5,
                        0.0D, 0.0D, 0.0D);
                ++this.rayAmount;
                this.playSound(SoundEvents.RESPAWN_ANCHOR_CHARGE, 1.0F, 0.75F);
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.1F, 5, 5);
            }

            if (this.attackTicks == 250) {
                ++this.rayAmount;
                this.playSound(SoundEvents.RESPAWN_ANCHOR_CHARGE, 1.0F, 1.0F);
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.1F, 5, 5);
            }

            if (this.attackTicks == 260) {
                this.level().addParticle(this.getPhase() >= 2
                                ? ModParticles.SOUL_EXPLOSION_RED.get() : ModParticles.SOUL_EXPLOSION.get(),
                        this.getX() + (double) vec * vecX + (double) (f * offset) + (double) f3,
                        this.getY() + 2.0D + (double) f4,
                        this.getZ() + (double) vec * vecZ + (double) (f1 * offset) + (double) f5,
                        0.0D, 0.0D, 0.0D);
                ++this.rayAmount;
                this.playSound(SoundEvents.RESPAWN_ANCHOR_CHARGE, 1.0F, 1.25F);
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.1F, 5, 5);
            }

            if (this.attackTicks == 270) {
                this.level().addParticle(this.getPhase() >= 2
                                ? ModParticles.SOUL_EXPLOSION_RED.get() : ModParticles.SOUL_EXPLOSION.get(),
                        this.getX() + (double) vec * vecX + (double) (f * offset) + (double) f3,
                        this.getY() + 2.0D + (double) f4,
                        this.getZ() + (double) vec * vecZ + (double) (f1 * offset) + (double) f5,
                        0.0D, 0.0D, 0.0D);
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.25F, 5, 10);
                this.playSound(SoundEvents.DRAGON_FIREBALL_EXPLODE, 1.0F, 0.5F);
            }

            if (this.attackTicks >= 270) {
                float f9 = (this.random.nextFloat() - 0.5F) * 8.0F;
                float f10 = (this.random.nextFloat() - 0.5F) * 4.0F;
                float f2 = (this.random.nextFloat() - 0.5F) * 8.0F;
                float f8 = (this.random.nextFloat() - 0.75F) * 5.0F;
                float f6 = (this.random.nextFloat() - 0.75F) * 3.0F;
                float f7 = (this.random.nextFloat() - 0.75F) * 5.0F;
                this.level().addParticle(this.getPhase() >= 2
                                ? ModParticles.SOUL_EXPLOSION_RED.get() : ModParticles.SOUL_EXPLOSION.get(),
                        this.getX() + (double) f9, this.getY() + 2.0D + (double) f10,
                        this.getZ() + (double) f2, 0.0D, 0.0D, 0.0D);
                this.level().addParticle(this.getPhase() >= 2
                                ? ModParticles.SOUL_EXPLOSION_RED.get() : ModParticles.SOUL_EXPLOSION.get(),
                        this.getX() + (double) f8, this.getY() + 2.0D + (double) f6,
                        this.getZ() + (double) f7, 0.0D, 0.0D, 0.0D);
                this.level().addParticle(this.getPhase() >= 2
                                ? ModParticles.GHOSTLY_SOUL_RED.get() : ModParticles.GHOSTLY_SOUL.get(),
                        this.getX() + (double) f9, this.getY() + 2.0D + (double) f10,
                        this.getZ() + (double) f2, 0.0D, 0.5D, 0.0D);
                this.level().addParticle(this.getPhase() >= 2
                                ? ModParticles.GHOSTLY_SOUL_RED.get() : ModParticles.GHOSTLY_SOUL.get(),
                        this.getX() + (double) f8, this.getY() + 2.0D + (double) f6,
                        this.getZ() + (double) f7, 0.0D, 0.5D, 0.0D);
            }
        }

        if (this.getAttackState() == 37) {
            int crossSlash = 53;
            int tridentSwing1 = 89;
            int swordSwing1 = 92;
            int tridentSwing2 = 120;
            int swordSwing2 = 123;
            int swordSwing3 = 133;
            int swordStab = 148;
            int tridentUppercut = 168;
            int crossSlash2 = 190;
            int landSlam = 265;
            int doubleUppercut = 292;
            float tridentRange = 4.0F;

            if (this.attackTicks == 1) {
                this.ghostItemFade.setTimer(5);
            }
            if (this.attackTicks >= 5 && this.attackTicks <= 70 && this.ghostItemFade.getTimer() > 0) {
                this.ghostItemFade.decreaseTimer();
            }
            if (this.attackTicks == 312) {
                this.ghostItemFade.setTimer(0);
            }
            if (this.attackTicks >= 312) {
                this.ghostItemFade.increaseTimer();
            }

            if (this.attackTicks == 25) {
                this.sendAdvancedHotBarMessage(this.getRandom().nextBoolean()
                                ? "message.goetyominous.possessed_paladin_servant.finisher.1"
                                : "message.goetyominous.possessed_paladin_servant.finisher.2",
                        ChatFormatting.RED, 10.0F);
            }

            if (this.attackTicks == crossSlash - 3) {
                this.calculatedDash(0.25F);
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.0F);
                this.playSound(ModSounds.POSSESSED_PALADIN_SWING.get(), 1.0F, 0.75F);
                this.createSweep(0.0F, 0.0F, bigSweepHeight, bigSweepAdditionalY, true, sweepSize, sweepRot, false);
                this.createSweep(0.0F, 0.0F, bigSweepHeight, bigSweepAdditionalY, false, sweepSize, sweepRot, false);
            }
            if (this.attackTicks == crossSlash) {
                this.spawnCircleParticle(0.0F, 0.0F, 100.0F, true, 2.0F,
                        this.getPhase() >= 2 ? this.uR : 0.0F,
                        this.getPhase() >= 2 ? this.uG : 0.9F,
                        this.getPhase() >= 2 ? this.uB : 0.8F,
                        0.8F, Circle.EnumRingBehavior.CONSTANT, 30);
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.05F, 0, 10);
                this.SideAreaAttack(3.5F, 3.5F, 180.0F, 0.0F, 0.0F, 17.0F, 100,
                        ModSounds.EMPTY, 1.0F, false, 1.0F);
            }

            if (this.attackTicks == tridentSwing1 - 3) {
                this.calculatedDash(0.25F);
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.0F);
                this.createSweep(0.0F, 0.0F, bigSweepHeight, bigSweepAdditionalY, false,
                        sweepSize + 0.5F, sweepRot, false);
            }
            if (this.attackTicks == tridentSwing1) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.05F, 0, 10);
                this.SideAreaAttack(tridentRange, tridentRange, 180.0F, 0.0F, 0.0F, 15.0F, 100,
                        ModSounds.EMPTY, 1.0F, false, 1.0F);
            }

            if (this.attackTicks == swordSwing1 - 3) {
                this.calculatedDash(0.15F);
                this.playSound(ModSounds.POSSESSED_PALADIN_SWING.get(), 1.0F, 0.75F);
                this.createSweep(0.0F, 0.0F, bigSweepHeight, bigSweepAdditionalY, false,
                        sweepSize, sweepRot, false);
            }
            if (this.attackTicks == swordSwing1) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.05F, 0, 10);
                this.SideAreaAttack(3.5F, 3.5F, 180.0F, 0.0F, 0.0F, 17.0F, 100,
                        ModSounds.EMPTY, 1.0F, false, 1.0F);
            }

            if (this.attackTicks == 100) {
                this.advancedDash(this, -3.0F, 2.5F, 0.75F);
            }

            if (this.attackTicks == tridentSwing2 - 3) {
                this.calculatedDash(0.25F);
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.0F);
                this.createSweep(0.0F, 0.0F, bigSweepHeight, bigSweepAdditionalY, false,
                        sweepSize + 0.5F, sweepRot, false);
            }
            if (this.attackTicks == tridentSwing2) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.05F, 0, 10);
                this.SideAreaAttack(tridentRange, tridentRange, 180.0F, 0.0F, 0.0F, 15.0F, 100,
                        ModSounds.EMPTY, 1.0F, false, 1.0F);
            }

            if (this.attackTicks == swordSwing2 - 3) {
                this.calculatedDash(0.15F);
                this.playSound(ModSounds.POSSESSED_PALADIN_SWING.get(), 1.0F, 0.75F);
                this.createSweep(0.0F, 0.0F, bigSweepHeight, bigSweepAdditionalY, false,
                        sweepSize, sweepRot, false);
            }
            if (this.attackTicks == swordSwing2) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.05F, 0, 10);
                this.SideAreaAttack(3.5F, 3.5F, 180.0F, 0.0F, 0.0F, 19.0F, 100,
                        ModSounds.EMPTY, 1.0F, false, 1.0F);
            }

            if (this.attackTicks == swordSwing3 - 3) {
                this.advancedDash(this, -3.0F, -2.5F, 0.75F);
                this.playSound(ModSounds.POSSESSED_PALADIN_SWING.get(), 1.0F, 0.75F);
                this.createSweep(0.0F, 0.0F, bigSweepHeight, bigSweepAdditionalY, true,
                        sweepSize, sweepRot, false);
            }
            if (this.attackTicks == swordSwing3) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.05F, 0, 10);
                this.SideAreaAttack(3.0F, 3.5F, 180.0F, 0.0F, 0.0F, 17.0F, 100,
                        ModSounds.EMPTY, 1.0F, false, 1.0F);
            }

            if (this.attackTicks == swordStab - 8) {
                this.saveTargetPos();
            }
            if (this.attackTicks == swordStab - 3) {
                this.calculatedDashToPositon(0.25F, this.lastTargetPos());
                this.playSound(ModSounds.POSSESSED_PALADIN_STAB.get(), 1.0F, 0.8F);
                this.createSweep(0.0F, 0.0F, bigSweepHeight, bigSweepAdditionalY, true,
                        sweepSize, sweepRot, false);
            }
            if (this.attackTicks == swordStab) {
                this.StraightLineAreaAttack(-0.6F, 2.5F, smashRange, 100, 17.0F, true, 1.35F);
            }

            if (this.attackTicks == tridentUppercut - 8) {
                this.saveTargetPos();
            }
            if (this.attackTicks == tridentUppercut - 3) {
                this.calculatedDashToPositon(0.25F, this.lastTargetPos());
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.0F);
            }
            if (this.attackTicks == tridentUppercut) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.05F, 0, 10);
                this.StraightLineAreaAttack(-0.6F, 2.5F, 5.0F, 100, 15.0F, true, 1.35F);
            }

            if (this.attackTicks == crossSlash2 - 3) {
                this.calculatedDash(0.25F);
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.0F);
                this.playSound(ModSounds.POSSESSED_PALADIN_SWING.get(), 1.0F, 0.75F);
                this.createSweep(0.0F, 0.0F, bigSweepHeight, bigSweepAdditionalY, true, sweepSize, sweepRot, false);
                this.createSweep(0.0F, 0.0F, bigSweepHeight, bigSweepAdditionalY, false, sweepSize, sweepRot, false);
            }
            if (this.attackTicks == crossSlash2) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.05F, 0, 10);
                this.SideAreaAttack(3.5F, 3.5F, 180.0F, 0.0F, 0.0F, 18.0F, 100,
                        ModSounds.EMPTY, 1.0F, false, 1.0F);
            }

            if (this.attackTicks == landSlam - 36) {
                this.setNoGravity(true);
            }
            if (this.attackTicks == landSlam - 30) {
                DynamicCameraZoomEntity.dynamicCameraZoom(this.level(), this.position(),
                        50.0F, 4.0F, 30, 55, 5.0F, false, this);
            }
            if (this.attackTicks >= landSlam - 36 && this.attackTicks <= landSlam - 25) {
                this.setDeltaMovement(this.getDeltaMovement().x, 0.35D, this.getDeltaMovement().z);
                this.attractParticles(ModParticles.GHOSTLY_SOUL_RED.get(), 5, 4, 0.0F, 0.0F,
                        5.0F, 5.0F, 0.075F);
                this.attractParticles(ModParticles.GHOSTLY_SOUL_RED.get(), 5, 4, 0.0F, 0.0F,
                        3.0F, 5.0F, 0.075F);
                this.attractParticles(ModParticles.GHOSTLY_SOUL_RED.get(), 5, 4, 0.0F, 0.0F,
                        2.0F, 5.0F, 0.075F);
            }
            if (this.attackTicks == landSlam - 20) {
                this.playSound(ModSounds.OMINOUS_WIND_UP.get(), 1.0F, 1.0F);
            }
            if (this.attackTicks == landSlam - 15) {
                this.saveTargetPos();
            }
            if (this.attackTicks == landSlam - 10) {
                this.setNoGravity(false);
                if (this.targetIsNotNull()) {
                    Vec3 start = this.position();
                    Vec3 sub = this.lastTargetPos().subtract(start);
                    Vec3 normal = sub.normalize();
                    float fl1 = this.distanceTo(this.target()) * 0.25F;
                    this.setDeltaMovement(this.getDeltaMovement().add(
                            normal.x * (double) fl1, normal.y * (double) fl1, normal.z * (double) fl1));
                } else {
                    this.setDeltaMovement(this.getDeltaMovement().x, -1.0D, this.getDeltaMovement().z);
                }
            }

            if (this.attackTicks == landSlam) {
                this.spawnChainedStrike(4.0F, 0.0F, 1, 5, 10, true);
                this.spawnChainedStrike(-4.0F, 0.0F, 1, 5, 10, true);
                this.spawnChainedStrike(0.0F, -4.0F, 1, 5, 10, true);
                this.spawnChainedStrike(0.0F, 4.0F, 1, 5, 10, true);
                CameraShakeEntity.cameraShake(this.level(), this.position(), 10.0F, 0.15F, 10, 10);
                this.playSound(ModSounds.STAB_HIT.get(), 1.0F, 1.0F);
                this.SideAreaAttack(3.0F, 3.0F, 360.0F, 0.0F, 0.0F, 25.0F, 150,
                        ModSounds.EMPTY, 0.0F, false, 0.0F);
                this.spawnCircleParticle(0.0F, 0.0F, 100.0F, true, 2.0F,
                        this.getPhase() >= 2 ? this.uR : 0.0F,
                        this.getPhase() >= 2 ? this.uG : 0.9F,
                        this.getPhase() >= 2 ? this.uB : 0.8F,
                        0.8F, Circle.EnumRingBehavior.GROW, 30);
            }

            if (this.attackTicks == doubleUppercut - 3) {
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.0F);
                this.basicDash(1.5F, 3.0F, true);
            }
            if (this.attackTicks == doubleUppercut) {
                this.spawnChainedStrike(3.0F, 0.0F, 3, 4, 10, true);
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.05F, 0, 10);
                this.StraightLineAreaAttack(-0.6F, 2.5F, 5.0F, 100, 17.0F, true, 1.35F);
            }
        }

        if (this.getAttackState() == 38) {
            int throwAttack = 28;
            int slash1 = 54;
            int slash2 = 69;

            if (this.attackTicks == 1) {
                this.ghostItemFade.setTimer(5);
            }
            if (this.attackTicks >= 5 && this.attackTicks <= 70 && this.ghostItemFade.getTimer() > 0) {
                this.ghostItemFade.decreaseTimer();
            }

            if (this.attackTicks == 1) {
                this.telegraphFadeAway.resetTimer();
            }

            if (this.attackTicks == throwAttack && this.targetIsNotNull()) {
                this.throwSoulTrident(this.target(), 1.0F);
            }

            if (this.attackTicks == slash1 - 3) {
                this.createSweep(0.0F, 0.0F, bigSweepHeight, (double) bigSweepAdditionalY,
                        true, sweepSize, sweepRot, false);
                this.playSound(ModSounds.POSSESSED_PALADIN_SWING.get(), 1.0F, 0.75F);
                this.calculatedDash(0.25F);
            }

            if (this.attackTicks == slash1) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.05F, 0, 10);
                this.SideAreaAttack(doubleSlashRange, 3.0F, 90.0F, 0.0F, 0.0F, 18.0F, 100,
                        SoundEvents.EMPTY, 1.0F, false, 0.0F);
            }

            if (this.attackTicks == 62) {
                this.advancedDash(this, 1.5F, -3.0F, 0.75F);
            }

            if (this.attackTicks == slash2 - 3) {
                this.createSweep(0.0F, 0.0F, bigSweepHeight, (double) bigSweepAdditionalY,
                        true, sweepSize, sweepRot, false);
                this.playSound(ModSounds.POSSESSED_PALADIN_SWING.get(), 1.0F, 0.75F);
                this.calculatedDash(0.25F);
            }

            if (this.attackTicks == slash2) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, 0.05F, 0, 10);
                this.SideAreaAttack(doubleSlashRange, 3.0F, 90.0F, 0.0F, 0.0F, 18.0F, 100,
                        SoundEvents.EMPTY, 1.0F, false, 0.0F);
            }

            if (this.attackTicks >= 64 && this.attackTicks <= 69) {
                this.telegraphFadeAway.increaseTimer();
            }

            for (int i = 69; i <= 83; i += 2) {
                if (this.attackTicks == i) {
                    int distance = i - 67;
                    this.flameRadagonShockwave(0.2F, distance, 1.0F, 5, 0.0F, 0.0F, 6.0F);
                }
            }
        }
    }

    private void StraightLineAreaAttack(float boxWidth, float yHeight, float range, int brokenShieldTicks,
                                        float damage, boolean launch, float launchPower) {
        double rad = Math.toRadians((double) (this.getYRot() + 90.0F));
        double xRange = (double) range * Math.cos(rad);
        double zRange = (double) range * Math.sin(rad);
        AABB attackRange = this.getBoundingBox()
                .inflate((double) boxWidth, (double) yHeight, (double) boxWidth)
                .expandTowards(xRange, 0.0D, zRange);

        for (LivingEntity entityHit : this.level().getEntitiesOfClass(LivingEntity.class, attackRange)) {
            if (!this.isAlliedTo(entityHit) && entityHit != this) {
                boolean flag = entityHit.hurt(this.damageSources().mobAttack(this),
                        (float) ((double) damage
                                + (double) MathUtils.entityBasedHpDamage(entityHit, 3.0F)
                                * (Double) ModConfig.MOB_CONFIG.PosessedPaladinDamageMutliplier.get()));
                if (flag) {
                    EntityUtil.cancelBuffs(entityHit);
                    entityHit.invulnerableTime = 0;
                    this.shouldAttackMore = true;
                    this.applyStackingEffect(entityHit, ModEffects.SOUL_FRACTURE.get(), 1, 4,
                            MathUtils.toTicks(10.0F));
                }
                if (flag && launch) {
                    this.launch(entityHit, true);
                }
                if (entityHit instanceof Player && entityHit.isBlocking() && brokenShieldTicks > 0) {
                    disableShield(entityHit, brokenShieldTicks);
                }
            }
        }
    }

    public void spawnSoulPillar(float vec, float offset, int amount) {
        float f = Mth.cos(this.yBodyRot * ((float) Math.PI / 180F));
        float f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180F));
        double theta = (double) this.yBodyRot * (Math.PI / 180D);
        ++theta;
        double vecX = Math.cos(theta);
        double vecZ = Math.sin(theta);
        int standingOnY = Mth.floor(this.getY()) - 1;
        double headY = this.getY() + 1.0D;
        float yawRadians = (float) Math.toRadians((double) (90.0F + this.getYRot()));

        for (int l = 0; l < amount; ++l) {
            double d2 = 1.25D * (double) (l + 1);
            this.spawnSoulPillars(
                    this.getX() + (double) vec * vecX + (double) (f * offset) + (double) Mth.cos(yawRadians) * d2,
                    headY,
                    this.getZ() + (double) vec * vecZ + (double) (f1 * offset) + (double) Mth.sin(yawRadians) * d2,
                    standingOnY, yawRadians, l, this.level(), this);
        }
    }

    public void randomizedSoulStrike(int firstRandomMin, int secondRandomMin, int amount) {
        int uCap = 5;
        int rX = this.random1.nextInt(-uCap, uCap);
        int rX2 = this.random1.nextInt(-uCap, uCap);
        int rZ = this.random1.nextInt(-uCap, uCap);
        int rZ2 = this.random1.nextInt(-uCap, uCap);
        Vec3 randomPos = new Vec3(this.getX() + (double) rX, this.getY(), this.getZ() + (double) rZ);
        Vec3 atanPos = new Vec3(randomPos.x + (double) rX2, randomPos.y, randomPos.z + (double) rZ2);
        double lowestYCheck = this.getY() + 1.0D;
        float f = (float) Mth.atan2(atanPos.z - randomPos.z, atanPos.x - randomPos.x);

        for (int l = 0; l < amount; ++l) {
            double d2 = 1.25D * (double) (l + 1);
            int warmup = l + 10;
            this.spawnSoulPillars(randomPos.x + (double) Mth.cos(f) * d2, this.getY(),
                    randomPos.z + (double) Mth.sin(f) * d2, (int) lowestYCheck, f, warmup,
                    this.level(), this);
        }
    }

    public void soulStrikeRing(float vec, int quake, float math) {
        float f = Mth.cos(this.yBodyRot * ((float) Math.PI / 180F));
        float f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180F));
        double theta = (double) this.yBodyRot * (Math.PI / 180D);
        ++theta;
        double vecX = Math.cos(theta);
        double vecZ = Math.sin(theta);
        float angle = 360.0F / (float) quake;

        for (int i = 0; i < quake; ++i) {
            SoulStrike strike = new SoulStrike(LmEntityRegistry.SOUL_STRIKE.get(), this.level());
            strike.setDamage(12.0F);
            strike.shootFromRotation(this, 0.0F, angle * (float) i, 0.0F, 0.45F, 0.0F);
            strike.setPos(this.getX() + (double) vec * vecX + (double) (f * math),
                    this.getY() + 0.3D,
                    this.getZ() + (double) vec * vecZ + (double) (f1 * math));
            strike.setOwner(this);
            strike.setRed(this.getPhase() >= 2);
            this.level().addFreshEntity(strike);
        }
    }

    private boolean spawnSoulPillars(double x, double y, double z, int lowestYCheck, float yRot,
                                     int warmupDelayTicks, Level world, LivingEntity player) {
        BlockPos blockpos = BlockPos.containing(x, y, z);
        boolean flag = false;
        double d0 = 0.0D;

        do {
            BlockPos blockpos1 = blockpos.below();
            BlockState blockstate = world.getBlockState(blockpos1);
            if (blockstate.isFaceSturdy(world, blockpos1, Direction.UP)) {
                if (!world.isEmptyBlock(blockpos)) {
                    BlockState blockstate1 = world.getBlockState(blockpos);
                    VoxelShape voxelshape = blockstate1.getCollisionShape(world, blockpos);
                    if (!voxelshape.isEmpty()) {
                        d0 = voxelshape.max(Direction.Axis.Y);
                    }
                }
                flag = true;
                break;
            }
            blockpos = blockpos.below();
        } while (blockpos.getY() >= lowestYCheck);

        if (flag) {
            world.addFreshEntity(new SoulPillarEntity(world, x, (double) blockpos.getY() + d0, z, yRot,
                    warmupDelayTicks, this, 20, 8.0F, this.getIsSecondPhase()));
            return true;
        }
        return false;
    }

    private void spawnSoulShieldRing(int waveIndex, int wavesTotal, int totalPoints, double radius,
                                     boolean isOuter, boolean isWarningParticle) {
        double step = (Math.PI * 2D) / (double) totalPoints;
        int pointsThisWave = totalPoints / wavesTotal;
        double base = -(Math.PI / 7.5D);
        double waveOffset = (double) waveIndex * step;

        for (int i = 0; i < pointsThisWave; ++i) {
            double angle = base + waveOffset + (double) i * (double) wavesTotal * step;
            double spawnX = this.getX() + Math.cos(angle) * radius;
            double spawnZ = this.getZ() + Math.sin(angle) * radius;
            double spawnY = (double) Mth.floor(this.getY());
            double headY = this.getY() + 1.0D;
            double dx = this.getX() - spawnX;
            double dz = this.getZ() - spawnZ;
            float yawRad = (float) Math.atan2(dz, dx);
            float f2 = (float) i * ((float) Math.PI * 2F) / (float) pointsThisWave
                    + (((float) Math.PI * 2F) / (float) pointsThisWave - 1.0F);
            this.spawnSoulShields(spawnX, spawnZ, spawnY, headY, isOuter ? f2 : yawRad, 4,
                    (float) this.getX(), (float) this.getY(), (float) this.getZ(),
                    isOuter, isWarningParticle);
        }
    }

    public void spawnSoulShields(double x, double z, double minY, double maxY, float rotation, int delay,
                                 float destX, float destY, float destZ, boolean isOuter,
                                 boolean isWarningParticle) {
        BlockPos blockpos = BlockPos.containing(x, maxY, z);
        boolean flag = false;
        double d0 = 0.0D;

        do {
            BlockPos blockpos1 = blockpos.below();
            BlockState blockstate = this.level().getBlockState(blockpos1);
            if (blockstate.isFaceSturdy(this.level(), blockpos1, Direction.UP)) {
                if (!this.level().isEmptyBlock(blockpos)) {
                    BlockState blockstate1 = this.level().getBlockState(blockpos);
                    VoxelShape voxelshape = blockstate1.getCollisionShape(this.level(), blockpos);
                    if (!voxelshape.isEmpty()) {
                        d0 = voxelshape.max(Direction.Axis.Y);
                    }
                }
                flag = true;
                break;
            }
            blockpos = blockpos.below();
        } while (blockpos.getY() >= Mth.floor(minY) - 1);

        if (!flag) {
            return;
        }

        if (isWarningParticle) {
            if (this.level().isClientSide) {
                this.level().addParticle(
                        this.getPhase() >= 2
                                ? (ParticleOptions) ModParticles.GROUNDSOUL_RED.get()
                                : (ParticleOptions) ModParticles.GROUNDSOUL.get(),
                        x, (double) blockpos.getY() + 2.0D + d0, z, 0.0D, 0.0D, 0.0D);
                this.level().addParticle(
                        new Circle.RingData(0.0F, (float) Math.PI / 2F, 35,
                                this.getPhase() >= 2 ? this.uR : 0.0F,
                                this.getPhase() >= 2 ? this.uG : 0.9F,
                                this.getPhase() >= 2 ? this.uB : 0.8F,
                                0.8F, 15.0F, false, Circle.EnumRingBehavior.SHRINK),
                        x, (double) blockpos.getY() + 0.25D + d0, z, 0.0D, 0.0D, 0.0D);
            }
        } else {
            this.level().addFreshEntity(new SoulShieldEntity(this.level(), x,
                    (double) blockpos.getY() + d0, z, rotation, delay, this, 10.0F,
                    destX, destY, destZ, isOuter, this.getPhase() >= 2));
        }
    }

    public void backStep(float v, float y) {
        float yaw = (float) Math.toRadians((double) (this.getYRot() + 90.0F));
        Vec3 dodgePos = this.getDeltaMovement().add((double) v * Math.cos((double) yaw), (double) y,
                (double) v * Math.sin((double) yaw));
        this.setDeltaMovement(dodgePos.x, dodgePos.y, dodgePos.z);
    }

    public void addShootParticle(float vec, float offset) {
        float f = Mth.cos(this.yBodyRot * ((float) Math.PI / 180F));
        float f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180F));
        double theta = (double) this.yBodyRot * (Math.PI / 180D);
        ++theta;
        double vecX = Math.cos(theta);
        double vecZ = Math.sin(theta);
        this.level().addParticle(
                this.getPhase() >= 2 ? ModParticles.SOUL_SHOOT_RED.get() : ModParticles.SOUL_SHOOT.get(),
                this.getX() + (double) vec * vecX + (double) (f * offset),
                this.getY() + (double) 1.75F,
                this.getZ() + (double) vec * vecZ + (double) (f1 * offset),
                0.0D, 0.0D, 0.0D);
    }

    public void throwDaggers(float velocity, double x, double y, double z, int daggerCount,
                             float angleBetween, float elevationAngle, int returnTick) {
        float offset = 2.0F;
        float vec = 2.0F;
        float behindVec = 0.5F;
        this.addShootParticle(vec, 0.0F);
        this.addShootParticle(vec - behindVec, offset);
        this.addShootParticle(vec - behindVec, -offset);

        if (this.targetIsNotNull()) {
            double dx = this.target().getX() - x;
            double dz = this.target().getZ() - z;
            double dy = this.target().getY() - y;
            Vec3 flatDir = new Vec3(dx, dy, dz).normalize();
            double elevRad = Math.toRadians(dy);
            double totalSpread = (double) (angleBetween * (float) (daggerCount - 1));
            double startYaw = -totalSpread * 0.5D;

            for (int i = 0; i < daggerCount; ++i) {
                double yawOffset = startYaw + (double) ((float) i * angleBetween);
                Vec3 dirYaw = MathUtils.rotateYaw(flatDir, yawOffset);
                double cosP = Math.cos(elevRad);
                double sinP = Math.sin(elevRad);
                Vec3 finalVec = new Vec3(dirYaw.x * cosP, sinP, dirYaw.z * cosP);

                ThrownPhantomDagger dagger = new ThrownPhantomDagger(
                        LmEntityRegistry.THROWN_PHANTOM_DAGGER.get(), this.level());
                dagger.setPosRaw(x, y + (double) (this.getBbHeight() / 2.0F), z);
                dagger.setReturnEntity(this);
                dagger.setReturnTick(returnTick);
                dagger.setDamage(6.0F);
                dagger.shoot(finalVec.x, finalVec.y, finalVec.z, velocity, 0.0F);
                dagger.setOwner(this);
                dagger.setRed(this.getPhase() >= 2);
                this.level().addFreshEntity(dagger);
            }
        }
    }

    public void jumpTowardsPosition(double x, double y, double z) {
        Vec3 start = new Vec3(this.getX(), this.getY(), this.getZ());
        Vec3 end = new Vec3(x, y, z);
        Vec3 sub = end.subtract(start);
        Vec3 finalPos = sub.scale(0.8);
        double d0 = finalPos.x;
        double d1 = finalPos.y;
        double d2 = finalPos.z;
        Vec3 vec3 = new Vec3(d0, 0.3 + Mth.clamp(d1 * 0.075, 0.0, 10.0), d2)
                .multiply(0.2, 1.0, 0.2);
        this.setDeltaMovement(vec3);
    }

    @Override
    public boolean canBeCollidedWith() {
        return this.getAttackState() == 12 || this.getAttackState() == 14;
    }

    public void createSweep(float pos, float posOffset, float yHeight, double additionalY, boolean reverse,
                            float scale, float rot, boolean small) {
        float f = Mth.cos(this.yBodyRot * ((float) Math.PI / 180F));
        float f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180F));
        double theta = (double) this.yBodyRot * (Math.PI / 180D);
        ++theta;
        double vecX = Math.cos(theta);
        double vecZ = Math.sin(theta);
        double x = this.getX() + (double) pos * vecX + (double) (f * posOffset);
        double z = this.getZ() + (double) pos * vecZ + (double) (f1 * posOffset);
        if (this.level().isClientSide) {
            double d1 = this.getY() + (double) (this.getBbHeight() / 2.0F) + additionalY;
            float yaw = (float) Math.toRadians((double) (-this.yBodyRot + (reverse ? rot : 180.0F)));
            double lookX = -Math.cos((double) yaw);
            double lookZ = -Math.sin((double) yaw);
            float pitch = (float) (reverse ? -1 : 1)
                    * (float) Math.atan2((double) yHeight, Math.sqrt(lookX * lookX + lookZ * lookZ));
            if (this.getPhase() >= 2) {
                this.level().addParticle(new SoulSweepRedParticle.SweepData(2.0F * scale, yaw, pitch),
                        x, d1, z, 0.0D, 0.0D, 0.0D);
            } else {
                this.level().addParticle(new SoulSweepParticle.SweepData(2.0F * scale, yaw, pitch),
                        x, d1, z, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    private void StabGrab(float boxWidth, float yHeight, float range, int brokenShieldTicks,
                          float damage, boolean launch, float launchPower) {
        double rad = Math.toRadians((double) (this.getYRot() + 90.0F));
        double xRange = (double) range * Math.cos(rad);
        double zRange = (double) range * Math.sin(rad);
        AABB attackRange = this.getBoundingBox()
                .inflate((double) boxWidth, (double) yHeight, (double) boxWidth)
                .expandTowards(xRange, 0.0D, zRange);

        for (LivingEntity entityHit : this.level().getEntitiesOfClass(LivingEntity.class, attackRange)) {
            if (!this.isAlliedTo(entityHit) && entityHit != this) {
                boolean flag = entityHit.hurt(this.damageSources().mobAttack(this),
                        (float) ((double) damage
                                + (double) MathUtils.entityBasedHpDamage(entityHit, 3.0F)
                                * (Double) ModConfig.MOB_CONFIG.PosessedPaladinDamageMutliplier.get()));
                if (flag) {
                    this.applyStackingEffect(entityHit, ModEffects.SOUL_FRACTURE.get(), 1, 4,
                            MathUtils.toTicks(10.0F));
                    EntityUtil.cancelBuffs(entityHit);
                    boolean mounted = entityHit.startRiding(this, true);
                    if (mounted) {
                        entityHit.setShiftKeyDown(false);
                        this.succedGrabbing = true;
                    } else {
                        this.succedGrabbing = false;
                    }
                } else {
                    this.succedGrabbing = false;
                }

                if (entityHit instanceof Player && entityHit.isBlocking() && brokenShieldTicks > 0) {
                    disableShield(entityHit, brokenShieldTicks);
                }
            }
        }
    }

    private void SphereParticle(ParticleOptions particleType, float height, float vec, float size) {
        if (this.level().isClientSide && this.tickCount % 2 == 0) {
            double d0 = this.getX();
            double d1 = this.getY() + (double) height;
            double d2 = this.getZ();
            double theta = (double) this.yBodyRot * (Math.PI / 180D);
            ++theta;
            double vecX = Math.cos(theta);
            double vecZ = Math.sin(theta);

            for (float i = -size; i <= size; ++i) {
                for (float j = -size; j <= size; ++j) {
                    for (float k = -size; k <= size; ++k) {
                        double d3 = (double) j + (this.random.nextDouble() - this.random.nextDouble()) * 0.5D;
                        double d4 = (double) i + (this.random.nextDouble() - this.random.nextDouble()) * 0.5D;
                        double d5 = (double) k + (this.random.nextDouble() - this.random.nextDouble()) * 0.5D;
                        double d6 = (double) Mth.sqrt((float) (d3 * d3 + d4 * d4 + d5 * d5)) / 0.5D
                                + this.random.nextGaussian() * 0.05D;
                        this.level().addParticle(particleType,
                                d0 + (double) vec * vecX, d1, d2 + (double) vec * vecZ,
                                d3 / d6, d4 / d6, d5 / d6);
                        if (i != -size && i != size && j != -size && j != size) {
                            k += size * 2.0F - 1.0F;
                        }
                    }
                }
            }
        }
    }

    public void throwAnGravityEntity(float velocity, double destX, double destY, double destZ,
                                     double x, double y, double z, float damage, LivingEntity passenger) {
        if (passenger != null) {
            EntityThrownEntity thrownEntity = new EntityThrownEntity(this.level(), this, x, y, z,
                    damage, 1.0F, passenger);
            thrownEntity.setPosRaw(x, y, z);
            double d0 = destX - x;
            double d1 = destY + 0.5D - thrownEntity.getY();
            double d2 = destZ - z;
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            thrownEntity.shoot(d0, d1 + d3 * 0.2D, d2, velocity,
                    (float) (14 - this.level().getDifficulty().getId() * 4));
            thrownEntity.setOwner(this);
            this.level().addFreshEntity(thrownEntity);
        }
    }

    public int getStunDuration() {
        return 55;
    }

    private void strikeZigzagXBlades(int rune, double time, boolean isFalling, float damage,
                                     float divider, int offset, double amplitude, boolean particleWarning) {
        for (int i = 0; i < rune; ++i) {
            float throwAngle = (float) i * (float) Math.PI / ((float) rune / divider);
            float perpX = -Mth.sin(throwAngle);
            float perpZ = Mth.cos(throwAngle);

            for (int k = 0; k < 8; ++k) {
                double forward = 1.25F * (double) (k + 1);
                int group = k / offset % 2;
                double lateral = amplitude * (double) (group == 0 ? 1 : -1);
                double xOff = (double) Mth.cos(throwAngle) * forward + (double) perpX * lateral;
                double zOff = (double) Mth.sin(throwAngle) * forward + (double) perpZ * lateral;
                int delay = (int) (time * (double) (k + 1));
                this.spawnBlades(this.getX() + xOff, this.getZ() + zOff, this.getY(),
                        this.getY() + 2.0D, throwAngle, delay, isFalling, damage, particleWarning);
            }
        }
    }

    private void spawnBlades(double x, double z, double minY, double maxY, float rotation,
                             int delay, boolean falling, float damage, boolean warning) {
        BlockPos blockpos = new BlockPos((int) x, (int) maxY, (int) z);
        boolean foundGround = false;
        double groundOffset = 0.0D;

        do {
            BlockPos below = blockpos.below();
            BlockState belowState = this.level().getBlockState(below);
            if (belowState.isFaceSturdy(this.level(), below, Direction.UP)) {
                if (!this.level().isEmptyBlock(blockpos)) {
                    BlockState hereState = this.level().getBlockState(blockpos);
                    VoxelShape shape = hereState.getCollisionShape(this.level(), blockpos);
                    if (!shape.isEmpty()) {
                        groundOffset = shape.max(Axis.Y);
                    }
                }
                foundGround = true;
                break;
            }
            blockpos = blockpos.below();
        } while (blockpos.getY() >= Mth.floor(minY) - 1);

        if (foundGround) {
            double spawnY = (double) blockpos.getY() + groundOffset;

            if (warning) {
                this.level().addAlwaysVisibleParticle(this.getPhase() >= 2
                                ? ModParticles.GROUNDSOUL_RED.get() : ModParticles.GROUNDSOUL.get(),
                        x, spawnY + 2.0D, z, 0.0D, 0.0D, 0.0D);
                this.level().addParticle(new Circle.RingData(0.0F, (float) Math.PI / 2F, 35,
                                this.getPhase() >= 2 ? this.uR : 0.0F,
                                this.getPhase() >= 2 ? this.uG : 0.9F,
                                this.getPhase() >= 2 ? this.uB : 0.8F,
                                0.8F, 15.0F, false, Circle.EnumRingBehavior.SHRINK),
                        x, spawnY + 0.25D, z, 0.0D, 0.0D, 0.0D);
            } else if (falling) {
                this.level().addFreshEntity(new FallingSoulBladeEntity(this.level(), x, spawnY, z,
                        rotation, delay, this, damage, this.getPhase() >= 2));
            } else {
                this.level().addFreshEntity(new SoulBladeEntity(this.level(), x, spawnY, z,
                        rotation, delay, this, damage, this.getPhase() >= 2));
            }
        }
    }

    public void earthquakeEffect(float range, float amplitude) {
        for (LivingEntity livingEntity : this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate((double) range))) {
            if (this.isAlliedTo(livingEntity)) {
                continue;
            }
            Vec3 delta = livingEntity.getDeltaMovement();
            livingEntity.setDeltaMovement(delta.x, delta.y + (double) amplitude, delta.z);
            EntityUtil.applyPlayerDeltaMovement(livingEntity);
        }
    }

    public boolean isMagicAttack() {
        return this.getAttackState() == 20 || this.getAttackState() == 26;
    }

    public void SideAreaAttack(float range, float height, float arc, float boxOffset, float forwardOffset,
                               float damage, int brokenShieldTicks, SoundEvent soundEvent, float pitch,
                               boolean launch, float launchPower) {
        this.sideAreaAttack(range, height, arc, boxOffset, forwardOffset, damage, brokenShieldTicks,
                soundEvent, pitch, launch, launchPower, false);
    }

    public void GambitedSideAreaAttack(float range, float height, float arc, float boxOffset, float forwardOffset,
                                       float damage, int brokenShieldTicks, SoundEvent soundEvent, float pitch,
                                       boolean launch, float launchPower) {
        this.sideAreaAttack(range, height, arc, boxOffset, forwardOffset, damage, brokenShieldTicks,
                soundEvent, pitch, launch, launchPower, true);
    }

    private void sideAreaAttack(float range, float height, float arc, float boxOffset, float forwardOffset,
                                float damage, int brokenShieldTicks, SoundEvent soundEvent, float pitch,
                                boolean launch, float launchPower, boolean gambited) {
        double theta = Math.toRadians((double) this.yBodyRot) + (Math.PI / 2D);
        double forwardX = Math.cos(theta) * (double) forwardOffset;
        double forwardZ = Math.sin(theta) * (double) forwardOffset;

        for (LivingEntity entityHit : this.getEntityLivingBaseNearby(
                (double) range, (double) height, (double) range, (double) range)) {
            double dx = entityHit.getX() - (this.getX() + forwardX);
            double dz = entityHit.getZ() - (this.getZ() + forwardZ);
            float entityHitAngle = (float) ((Math.toDegrees(Math.atan2(dz, dx)) - (double) 90.0F)
                    % (double) 360.0F);
            if (entityHitAngle < 0.0F) {
                entityHitAngle += 360.0F;
            }

            float entityAttackingAngle = (this.yBodyRot - boxOffset) % 360.0F;
            if (entityAttackingAngle < 0.0F) {
                entityAttackingAngle += 360.0F;
            }

            float entityHitDistance = (float) Math.sqrt(dx * dx + dz * dz);
            float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
            if (entityHitDistance <= range
                    && (entityRelativeAngle <= arc / 2.0F && entityRelativeAngle >= -arc / 2.0F
                    || entityRelativeAngle >= 360.0F - arc / 2.0F
                    || entityRelativeAngle <= -360.0F + arc / 2.0F)
                    && !this.isAlliedTo(entityHit)
                    && !(entityHit instanceof PossessedPaladinServant)
                    && entityHit != this) {
                DamageSource damageSource = !gambited && this.isMagicAttack()
                        ? ModDamageTypes.causeGhostlyDamage(this, this)
                        : this.damageSources().mobAttack(this);
                boolean flag = entityHit.hurt(damageSource,
                        (float) ((double) damage
                                + (double) MathUtils.entityBasedHpDamage(entityHit, 3.0F)
                                * (Double) ModConfig.MOB_CONFIG.PosessedPaladinDamageMutliplier.get()));
                if (flag) {
                    EntityUtil.cancelBuffs(entityHit);
                    entityHit.invulnerableTime = 0;
                    if (!gambited && this.getAttackState() == 20) {
                        this.heal(3.0F + MathUtils.entityBasedHpDamage(entityHit, 1.0F));
                    }
                    if (gambited) {
                        this.hasHurt = true;
                    }
                    if (launch) {
                        this.launch(entityHit, true);
                    }
                    if (this.getAttackState() == 5) {
                        entityHit.addEffect(new MobEffectInstance(ModEffects.STUN.get(),
                                this.getStunDuration(), 0));
                    }
                    this.applyStackingEffect(entityHit, ModEffects.SOUL_FRACTURE.get(), 1, 4,
                            MathUtils.toTicks(10.0F));
                    this.playSound(soundEvent, 1.0F, pitch);
                }
                if (entityHit instanceof Player && entityHit.isBlocking() && brokenShieldTicks > 0) {
                    disableShield(entityHit, brokenShieldTicks);
                }
            }
        }
    }

    public void throwSoulTrident(LivingEntity target, float velocity) {
        SoulTrident soulTrident = new SoulTrident(LmEntityRegistry.SOUL_TRIDENT.get(), this.level());

        soulTrident.setPos(this.getX(), this.getEyeY() - 0.1D, this.getZ());
        soulTrident.setOwner(this);

        double d0 = target.getX() - this.getX();
        double d1 = target.getY(0.3333333333333333D) - soulTrident.getY();
        double d2 = target.getZ() - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        soulTrident.shoot(d0, d1 + d3 * 0.2D, d2, velocity,
                (float) (14 - this.level().getDifficulty().getId() * 4));
        this.playSound(SoundEvents.DROWNED_SHOOT, 1.0F,
                0.75F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level().addFreshEntity(soulTrident);
    }

    private void flameRadagonShockwave(float spreadarc, int distance, float vec, int delay,
                                       float pos, float offset, float damage) {
        float f = Mth.cos(this.yBodyRot * ((float) Math.PI / 180F));
        float f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180F));
        double theta1 = (double) this.yBodyRot * (Math.PI / 180D);
        ++theta1;
        double vecX = Math.cos(theta1);
        double vecZ = Math.sin(theta1);

        double x = this.getX() + (double) pos * vecX + (double) (f * offset);
        double z = this.getZ() + (double) pos * vecZ + (double) (f1 * offset);

        double facingAngle = (double) this.yBodyRot * (Math.PI / 180D) + (Math.PI / 2D);
        double spread = Math.PI * (double) spreadarc;
        int arcLen = Mth.ceil((double) distance * spread);

        for (int i = 0; i < arcLen; ++i) {
            double theta = ((double) i / ((double) arcLen - 1.0D) - 0.5D) * spread + facingAngle;
            double vx = Math.cos(theta);
            double vz = Math.sin(theta);
            double px = x + vx * (double) distance
                    + (double) vec * Math.cos((double) (this.yBodyRot + 90.0F) * Math.PI / 180D);
            double pz = z + vz * (double) distance
                    + (double) vec * Math.sin((double) (this.yBodyRot + 90.0F) * Math.PI / 180D);
            this.spawnSoulPillars((double) Mth.floor(px) + 0.5D, this.getY(),
                    (double) Mth.floor(pz) + 0.5D, (int) this.getY() - 1, (float) theta,
                    delay, this.level(), null);
        }
    }

    public void spawnChainedStrike(float startVec, float startOffset, int reps, int amount, int delay,
                                   boolean centeredStrike) {
        float f = Mth.cos(this.yBodyRot * ((float) Math.PI / 180F));
        float f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180F));
        double theta = (double) this.yBodyRot * (Math.PI / 180D);
        ++theta;
        double vecX = Math.cos(theta);
        double vecZ = Math.sin(theta);
        double lowestYCheck = this.getY() - 1.0D;
        double unusedD0 = Math.min(this.getY(), this.getY() - 3.0D);

        for (int k = 0; k < reps; ++k) {
            float r = (float) amount;
            float rMove = 0.0F;
            float yaw = (float) Math.toRadians(90.0D);
            int squareMove = k * amount * 2;
            double rawX = this.getX() + (double) ((float) squareMove + startVec) * vecX
                    + (double) (f * startOffset);
            double rawZ = this.getZ() + (double) ((float) squareMove + startVec) * vecZ
                    + (double) (f1 * startOffset);
            double x = rawX + (double) rMove * Math.cos((double) yaw);
            double z = rawZ + (double) rMove * Math.sin((double) yaw);
            double y = this.getY();
            double div = (double) r / Math.sqrt(2.0F);
            Vec3 v1 = new Vec3(x + div, y, z + div);
            Vec3 v2 = new Vec3(x - div, y, z + div);
            Vec3 v3 = new Vec3(x - div, y, z - div);
            Vec3 v4 = new Vec3(x + div, y, z - div);
            float v1v2atan2 = (float) Mth.atan2(v2.z - v1.z, v2.x - v1.x);
            float v2v3atan2 = (float) Mth.atan2(v3.z - v2.z, v3.x - v2.x);
            float v1v4atan2 = (float) Mth.atan2(v4.z - v1.z, v4.x - v1.x);
            float v4v3atan2 = (float) Mth.atan2(v3.z - v4.z, v3.x - v4.x);
            int loopDelay = k * 5;

            for (int l = 0; l < amount; ++l) {
                if (l % 2 == 0) {
                    double d2 = 1.25D * (double) (l + 1);
                    double lowestY = this.getY();
                    this.spawnSoulPillars(v1.x + (double) Mth.cos(v1v2atan2) * d2, lowestY,
                            v1.z + (double) Mth.sin(v1v2atan2) * d2, (int) lowestYCheck, f,
                            delay + loopDelay, this.level(), this);
                    this.spawnSoulPillars(v2.x + (double) Mth.cos(v2v3atan2) * d2, lowestY,
                            v2.z + (double) Mth.sin(v2v3atan2) * d2, (int) lowestYCheck, f,
                            delay + loopDelay, this.level(), this);
                    this.spawnSoulPillars(v1.x + (double) Mth.cos(v1v4atan2) * d2, lowestY,
                            v1.z + (double) Mth.sin(v1v4atan2) * d2, (int) lowestYCheck, f,
                            delay + loopDelay, this.level(), this);
                    this.spawnSoulPillars(v4.x + (double) Mth.cos(v4v3atan2) * d2, lowestY,
                            v4.z + (double) Mth.sin(v4v3atan2) * d2, (int) lowestYCheck, f,
                            delay + loopDelay, this.level(), this);
                }
            }

            if (centeredStrike) {
                if (this.level().isClientSide) {
                    this.level().addParticle(ModParticles.SOUL_EXPLOSION_RED.get(),
                            x, this.getY() + 0.5D, z, 0.0D, 0.0D, 0.0D);
                }
                this.spawnSoulPillars(x, this.getY() - 1.0D, z, (int) lowestYCheck, f,
                        delay, this.level(), this);
            }
        }
    }

    public void attractParticles(ParticleOptions particleOptions, int cap, int reps, float vec, float offset,
                                 float startY, float endY, float velocity) {
        float f = Mth.cos(this.yBodyRot * ((float) Math.PI / 180F));
        float f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180F));
        double theta = (double) this.yBodyRot * (Math.PI / 180D);
        ++theta;
        double vecX = Math.cos(theta);
        double vecZ = Math.sin(theta);
        int rX = this.random1.nextInt(-cap, cap);
        int rZ = this.random1.nextInt(-cap, cap);
        float f2 = (this.random.nextFloat() - 0.0F) * 0.5F;
        double d1 = this.getX() + (double) rX;
        double d2 = this.getY() + (double) startY + (double) f2;
        double d3 = this.getZ() + (double) rZ;
        Vec3 from = new Vec3(d1, d2, d3);
        Vec3 to = new Vec3(this.getX() + (double) vec * vecX + (double) (f * offset),
                this.position().y + (double) endY,
                this.getZ() + (double) vec * vecZ + (double) (f1 * offset));
        Vec3 v = to.subtract(from).scale((double) velocity);

        for (int i = 0; i <= reps; ++i) {
            if (this.level().isClientSide) {
                this.level().addParticle(particleOptions, d1, d2, d3, v.x, v.y, v.z);
            }
        }
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return ModSounds.LIVING_ARMOR_HURT.get();
    }

    @Override
    public double damageCap() {
        return AttributesConfig.PossessedPaladinServantDamageCap.get();
    }

    @Override
    public int adaptationFactor() {
        return 20;
    }

    @Override
    public void tryKill(Player player) {
        if (this.killChance <= 0) {
            this.warnKill(player);
        } else {
            this.executedByOwner = true;
            super.tryKill(player);
            if (this.isAlive()) {
                this.executedByOwner = false;
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.executedByOwner) {
            return super.hurt(source, amount);
        }

        if (this.isSleep() && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
        }

        if (this.getAttackState() == 26) {
            return false;
        }

        if (this.BossInvulnerabilityTime > 0
                && ModConfig.MOB_CONFIG.PossessedPaladinInvulnerabilityTime.get()) {
            return false;
        }

        if (this.isBlockin()) {
            this.playSound(ModSounds.BLOCK.get(), 1.0F, 1.0F);
            CameraShakeEntity.cameraShake(this.level(), this.position(), 10.0F, 0.15F, 5, 5);
            return false;
        }

        if (!this.level().isClientSide && this.canParry() && amount > 1.0F) {
            this.hasParried = true;
            this.setAttackState(5);
            this.stopAllAnimationStates();
            this.playSound(ModSounds.BLOCK.get(), 1.0F, 1.0F);
            CameraShakeEntity.cameraShake(this.level(), this.position(), 10.0F, 0.15F, 5, 5);

            return false;
        }

        boolean hurt1 = super.hurt(source, amount);
        if (hurt1 && !this.level().isClientSide
                && ModConfig.MOB_CONFIG.PossessedPaladinInvulnerabilityTime.get()
                && this.BossInvulnerabilityTime <= 0) {
            this.BossInvulnerabilityTime = BOSS_INVULNERABILITY_TICKS;
        }
        return hurt1;
    }

    private static final float PLAYER_HEAR_TALKING = 10.0F;

    private static final Map<String, String> BOSS_LINES = Map.of(
            "legendary_monsters:the_obliterator",
            "message.goetyominous.possessed_paladin_servant.boss.obliterator",
            "legendary_monsters:cloud_golem",
            "message.goetyominous.possessed_paladin_servant.boss.cloud_golem",
            "goety:ender_keeper",
            "message.goetyominous.possessed_paladin_servant.boss.ender_keeper",
            "goety:heresiarch",
            "message.goetyominous.possessed_paladin_servant.boss.heresiarch",
            "goety:apostle",
            "message.goetyominous.possessed_paladin_servant.boss.apostle");

    private final Set<String> saidBossLines = new HashSet<>();

    private void tickBossLine() {
        if (this.level().isClientSide) {
            return;
        }

        LivingEntity target = this.getTarget();
        if (target == null) {
            return;
        }

        ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(target.getType());
        if (id == null) {
            return;
        }

        String name = id.toString();
        String line = BOSS_LINES.get(name);
        if (line == null) {
            return;
        }

        if (this.saidBossLines.add(name)) {
            this.sendAdvancedHotBarMessage(line, ChatFormatting.AQUA, PLAYER_HEAR_TALKING);
        }
    }

    private static final int IDLE_TALK_INTERVAL = 400;

    public int idle_talk_cooldown = IDLE_TALK_INTERVAL;

    private String pendingIdleTalk;

    private int pendingIdleTalkDelay;

    private static final float IDLE_TALK_CHANCE = 0.35F;

    private static final int IDLE_TALK_GAP = 40;

    private static final float IDLE_SPECIAL_CHANCE = 0.25F;

    private static final String[][] IDLE_LINES = {
            {"message.goetyominous.possessed_paladin_servant.idle.1"},
            {"message.goetyominous.possessed_paladin_servant.idle.2"},
            {"message.goetyominous.possessed_paladin_servant.idle.3"},
            {"message.goetyominous.possessed_paladin_servant.idle.4a",
                    "message.goetyominous.possessed_paladin_servant.idle.4b"},
            {"message.goetyominous.possessed_paladin_servant.idle.5"},
            {"message.goetyominous.possessed_paladin_servant.idle.6a",
                    "message.goetyominous.possessed_paladin_servant.idle.6b"},
    };

    private static final String IDLE_APOSTLE_SLAIN =
            "message.goetyominous.possessed_paladin_servant.idle.apostle_slain";

    private static final ResourceLocation KILL_APOSTLE_ADVANCEMENT =
            new ResourceLocation("goety", "kill_apostle");

    private void tickIdleTalk() {
        if (this.level().isClientSide) {
            return;
        }

        if (this.pendingIdleTalk != null) {
            if (--this.pendingIdleTalkDelay <= 0) {
                this.sendAdvancedHotBarMessage(this.pendingIdleTalk, ChatFormatting.AQUA, PLAYER_HEAR_TALKING);
                this.pendingIdleTalk = null;
            }
            return;
        }

        boolean idle = this.getAttackState() == 0
                && this.getTarget() == null
                && !this.isSleep();

        if (!idle) {
            this.idle_talk_cooldown = IDLE_TALK_INTERVAL;
            return;
        }

        if (this.idle_talk_cooldown > 0) {
            --this.idle_talk_cooldown;
            return;
        }

        this.idle_talk_cooldown = IDLE_TALK_INTERVAL;
        if (this.getRandom().nextFloat() >= IDLE_TALK_CHANCE) {
            return;
        }

        String[] group = this.pickIdleLine();
        this.sendAdvancedHotBarMessage(group[0], ChatFormatting.AQUA, PLAYER_HEAR_TALKING);

        if (group.length > 1) {
            this.pendingIdleTalk = group[1];
            this.pendingIdleTalkDelay = IDLE_TALK_GAP;
        }
    }

    private String[] pickIdleLine() {
        if (this.getRandom().nextFloat() < IDLE_SPECIAL_CHANCE && this.ownerHasSlainApostle()) {
            return new String[]{IDLE_APOSTLE_SLAIN};
        }
        return IDLE_LINES[this.getRandom().nextInt(IDLE_LINES.length)];
    }

    private boolean ownerHasSlainApostle() {
        if (!(this.getTrueOwner() instanceof ServerPlayer owner)) {
            return false;
        }

        MinecraftServer server = owner.getServer();
        if (server == null) {
            return false;
        }

        Advancement advancement = server.getAdvancements().getAdvancement(KILL_APOSTLE_ADVANCEMENT);
        if (advancement == null) {
            return false;
        }

        return owner.getAdvancements().getOrStartProgress(advancement).isDone();
    }
}
