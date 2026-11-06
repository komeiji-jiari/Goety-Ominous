package com.qiuyue.goetyominous.common.entities.ally.mobs;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.ZPiglinServant;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.util.RandomSource;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class ZPiglinDrummer extends ZPiglinServant implements DrummerAnimatable {

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState drumAnimationState = new AnimationState();
    public final AnimationState cheerAnimationState = new AnimationState();

    private static final EntityDataAccessor<Boolean> DATA_DRUMMING =
            SynchedEntityData.defineId(ZPiglinDrummer.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_CHEERING =
            SynchedEntityData.defineId(ZPiglinDrummer.class, EntityDataSerializers.BOOLEAN);

    private static final int DRUM_DURATION = 55;
    private static final int DRUM_BUFF_TICK = 45;
    private static final int DRUM_COOLDOWN = 240;
    private static final int CHEER_DURATION = 60;
    private static final int CHEER_COOLDOWN = 200;
    private static final double BUFF_RADIUS = 12.0D;
    private static final int DRUM_EFFECT_DURATION = 200;
    private static final int[] DRUM_HIT_TICKS = {10, 28, 39};

    private int drumCooldown;
    private int cheerCooldown;
    private boolean lastDrumming;
    private boolean lastCheering;
    private float walkWeight;

    public ZPiglinDrummer(EntityType<? extends ZPiglinServant> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 18.0D)
                .add(Attributes.ARMOR, 0.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 0.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.38D)
                .add(Attributes.ATTACK_DAMAGE, 1.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_DRUMMING, false);
        this.entityData.define(DATA_CHEERING, false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.removeAllGoals(goal ->
                goal instanceof com.Polarice3.Goety.common.entities.ai.ModMeleeAttackGoal
                        || goal instanceof LookAtPlayerGoal
                        || goal instanceof Summoned.WanderGoal);
        this.goalSelector.addGoal(1, new DrumGoal(this));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, LivingEntity.class, 8.0F, 0.6D, 1.0D,
                target -> target == this.getTarget() && target != this.getTrueOwner()));
        this.goalSelector.addGoal(3, new CheerGoal(this));
        this.goalSelector.addGoal(8, new Summoned.WanderGoal<>(this, 0.6D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 15.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 15.0F));
    }

    @Override
    public void populateDefaultWeapons(RandomSource random, DifficultyInstance difficulty) {
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    public boolean isDrumming() {
        return this.entityData.get(DATA_DRUMMING);
    }

    public void setDrumming(boolean drumming) {
        this.entityData.set(DATA_DRUMMING, drumming);
    }

    public boolean isCheering() {
        return this.entityData.get(DATA_CHEERING);
    }

    public void setCheering(boolean cheering) {
        this.entityData.set(DATA_CHEERING, cheering);
    }

    public boolean isMovingForAnimation() {
        return this.walkAnimation.speed() > 0.01F;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.setupAnimationStates();
        } else {
            if (this.drumCooldown > 0) {
                --this.drumCooldown;
            }
            if (this.cheerCooldown > 0) {
                --this.cheerCooldown;
            }
        }
    }

    private void setupAnimationStates() {
        boolean drumming = this.isDrumming();
        boolean cheering = this.isCheering();

        if (drumming && !this.lastDrumming) {
            this.drumAnimationState.start(this.tickCount);
        } else if (!drumming && this.lastDrumming) {
            this.drumAnimationState.stop();
            this.idleAnimationState.start(this.tickCount);
        }

        if (cheering && !this.lastCheering) {
            this.cheerAnimationState.start(this.tickCount);
        } else if (!cheering && this.lastCheering) {
            this.cheerAnimationState.stop();
            this.idleAnimationState.start(this.tickCount);
        }

        this.walkAnimationState.startIfStopped(this.tickCount);
        this.idleAnimationState.startIfStopped(this.tickCount);
        this.walkWeight = approach(this.walkWeight, this.isMovingForAnimation() ? 1.0F : 0.0F, 0.2F);

        this.lastDrumming = drumming;
        this.lastCheering = cheering;
    }

    public float getWalkWeight() {
        return this.walkWeight;
    }

    @Override
    public AnimationState getIdleAnimationState() {
        return this.idleAnimationState;
    }

    @Override
    public AnimationState getWalkAnimationState() {
        return this.walkAnimationState;
    }

    @Override
    public AnimationState getDrumAnimationState() {
        return this.drumAnimationState;
    }

    @Override
    public AnimationState getCheerAnimationState() {
        return this.cheerAnimationState;
    }

    private static float approach(float value, float target, float step) {
        return value < target ? Math.min(value + step, target) : Math.max(value - step, target);
    }

    public void applyDrumBuffs() {
        if (this.level().isClientSide) {
            return;
        }
        List<LivingEntity> allies = this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(BUFF_RADIUS),
                living -> living != this && living.isAlive() && MobUtil.areAllies(this, living));
        for (LivingEntity ally : allies) {
            ally.addEffect(new MobEffectInstance(GoetyEffects.RAMPAGE.get(), DRUM_EFFECT_DURATION, 0));
            ally.addEffect(new MobEffectInstance(GoetyEffects.RALLIED.get(), DRUM_EFFECT_DURATION, 1));
            ally.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, DRUM_EFFECT_DURATION, 0));
        }
    }

    @Nullable
    public LivingEntity findFriendly() {
        List<LivingEntity> allies = this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(BUFF_RADIUS),
                living -> living != this && living.isAlive()
                        && !(living instanceof ZPiglinDrummer)
                        && MobUtil.areAllies(this, living));
        allies.sort(Comparator.comparingDouble(this::distanceToSqr));
        return allies.isEmpty() ? null : allies.get(0);
    }

    public int getDrumCooldown() {
        return this.drumCooldown;
    }

    public int getCheerCooldown() {
        return this.cheerCooldown;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.isAggressive() ? SoundEvents.ZOMBIFIED_PIGLIN_ANGRY : SoundEvents.ZOMBIFIED_PIGLIN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(net.minecraft.world.damagesource.DamageSource source) {
        return SoundEvents.ZOMBIFIED_PIGLIN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIFIED_PIGLIN_DEATH;
    }

    public static class DrumGoal extends Goal {
        private final ZPiglinDrummer drummer;
        private LivingEntity friendly;
        private int approachTick;
        private int drumTick;
        private boolean started;

        public DrumGoal(ZPiglinDrummer drummer) {
            this.drummer = drummer;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (this.drummer.getDrumCooldown() > 0) {
                return false;
            }
            if (this.drummer.getTarget() == null || !this.drummer.getTarget().isAlive()) {
                return false;
            }
            this.friendly = this.drummer.findFriendly();
            return this.friendly != null;
        }

        @Override
        public void start() {
            this.approachTick = 0;
            this.drumTick = 0;
            this.started = false;
        }

        @Override
        public void tick() {
            if (this.friendly == null || !this.friendly.isAlive()) {
                this.stop();
                return;
            }
            if (!this.started) {
                ++this.approachTick;
                if (this.drummer.distanceTo(this.friendly) <= 3.0D || this.approachTick > 80) {
                    this.started = true;
                    this.drummer.getNavigation().stop();
                    this.drummer.setDrumming(true);
                    this.drummer.playSound(SoundEvents.PIGLIN_CELEBRATE, 1.0F, 1.0F);
                } else {
                    this.drummer.getNavigation().moveTo(this.friendly, 1.2D);
                    this.drummer.getLookControl().setLookAt(this.friendly, 30.0F, 30.0F);
                }
            } else {
                ++this.drumTick;
                this.drummer.getNavigation().stop();
                if (this.friendly != null && this.friendly.isAlive()) {
                    this.drummer.getLookControl().setLookAt(this.friendly, 30.0F, 30.0F);
                }
                for (int hit : DRUM_HIT_TICKS) {
                    if (this.drumTick == hit) {
                        this.drummer.playSound(SoundEvents.NOTE_BLOCK_BASEDRUM.get(), 1.0F, 0.9F);
                    }
                }
                if (this.drumTick == DRUM_BUFF_TICK) {
                    this.drummer.applyDrumBuffs();
                }
                if (this.drumTick >= DRUM_DURATION) {
                    this.stop();
                }
            }
        }

        @Override
        public void stop() {
            if (this.started) {
                this.drummer.setDrumming(false);
            }
            this.drummer.drumCooldown = DRUM_COOLDOWN;
            this.friendly = null;
            this.approachTick = 0;
            this.drumTick = 0;
            this.started = false;
        }

        @Override
        public boolean canContinueToUse() {
            return this.friendly != null && this.friendly.isAlive();
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    public static class CheerGoal extends Goal {
        private final ZPiglinDrummer drummer;
        private int tick;

        public CheerGoal(ZPiglinDrummer drummer) {
            this.drummer = drummer;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (this.drummer.isDrumming()) {
                return false;
            }
            if (this.drummer.getDrumCooldown() <= 0) {
                return false;
            }
            if (this.drummer.getCheerCooldown() > 0) {
                return false;
            }
            LivingEntity target = this.drummer.getTarget();
            if (target == null || !target.isAlive()) {
                return false;
            }
            double distance = this.drummer.distanceTo(target);
            if (distance < 8.0D || distance > 12.0D) {
                return false;
            }
            return this.drummer.getRandom().nextInt(80) == 0;
        }

        @Override
        public void start() {
            this.tick = 0;
            this.drummer.setCheering(true);
            this.drummer.getNavigation().stop();
            this.drummer.playSound(SoundEvents.PIGLIN_CELEBRATE, 1.0F, 1.0F);
        }

        @Override
        public void tick() {
            ++this.tick;
            this.drummer.getNavigation().stop();
            LivingEntity target = this.drummer.getTarget();
            if (target != null && target.isAlive()) {
                this.drummer.getLookControl().setLookAt(target, 30.0F, 30.0F);
            }
            if (this.tick >= CHEER_DURATION) {
                this.stop();
            }
        }

        @Override
        public void stop() {
            this.drummer.setCheering(false);
            this.drummer.cheerCooldown = CHEER_COOLDOWN;
            this.tick = 0;
        }

        @Override
        public boolean canContinueToUse() {
            return this.tick < CHEER_DURATION && this.drummer.getTarget() != null;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }
}
