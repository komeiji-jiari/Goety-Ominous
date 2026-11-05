package com.qiuyue.goetyominous.common.entities.hostile.illagers;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.entities.hostile.illagers.HuntingIllagerEntity;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.spells.nether.FireBreathSpell;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.qiuyue.goetyominous.common.entities.projectile.PyromancerFireball;
import com.qiuyue.goetyominous.common.entities.util.FireWallEntity;
import com.qiuyue.goetyominous.common.entities.util.FireWallShape;
import com.qiuyue.goetyominous.common.init.ModEffects;
import com.qiuyue.goetyominous.config.AttributesConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.SpellcasterIllager;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class Pyrologer extends HuntingIllagerEntity {
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(
            Pyrologer.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_BREATH_LOCK = SynchedEntityData.defineId(
            Pyrologer.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> DATA_BREATH_YAW = SynchedEntityData.defineId(
            Pyrologer.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_BREATH_PITCH = SynchedEntityData.defineId(
            Pyrologer.class, EntityDataSerializers.FLOAT);
    private static final double SUPPORT_RANGE = 13.0D;
    private static final int FIRE_BREATH_CHARGE_TICKS = 13;
    private static final int FIRE_BREATH_LOOP_TICKS = 20;
    private static final int FIRE_BREATH_MAX_SPRAY_TICKS = 100;
    private static final int FIRE_BREATH_END_TICKS = 12;
    private static final int ANIM_NONE = 0;
    private static final int ANIM_FIREBALL = 1;
    private static final int ANIM_FIRE_WALL = 2;
    private static final int ANIM_FIERY_HIDE = 3;
    private static final int ANIM_FIRE_BREATH_START = 4;
    private static final int ANIM_FIRE_BREATH_LOOP = 5;
    private static final int ANIM_FIRE_BREATH_END = 6;

    private final AnimationState idleAnimationState = new AnimationState();
    private final AnimationState fireWallAnimationState = new AnimationState();
    private final AnimationState fireballAnimationState = new AnimationState();
    private final AnimationState fieryHideAnimationState = new AnimationState();
    private final AnimationState fireBreathStartAnimationState = new AnimationState();
    private final AnimationState fireBreathLoopAnimationState = new AnimationState();
    private final AnimationState fireBreathEndAnimationState = new AnimationState();
    private int spellCool;
    private int animResetTicks;
    private boolean fireBreathCasting;

    public Pyrologer(EntityType<? extends Pyrologer> type, Level level) {
        super(type, level);
        this.xpReward = 20;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.PyrologerServantHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.PyrologerServantArmor.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.PyrologerServantSpeed.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.PyrologerServantFollowRange.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.PyrologerServantAttackDamage.get());
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.PyrologerServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.PyrologerServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MOVEMENT_SPEED), AttributesConfig.PyrologerServantSpeed.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.FOLLOW_RANGE), AttributesConfig.PyrologerServantFollowRange.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.PyrologerServantAttackDamage.get());
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new CastingSpellGoal());
        this.goalSelector.addGoal(2, new SupportGoal());
        this.goalSelector.addGoal(5, new FireBreathGoal());
        this.goalSelector.addGoal(6, new FireWallGoal());
        this.goalSelector.addGoal(7, new FireballGoal());
    }

    @Override
    public void extraGoals() {
    }

    @Override
    public void setRider(boolean rider) {
    }

    @Override
    public void applyRaidBuffs(int wave, boolean noClue) {
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return SoundEvents.EVOKER_CAST_SPELL;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.EVOKER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(net.minecraft.world.damagesource.DamageSource source) {
        return SoundEvents.EVOKER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.EVOKER_DEATH;
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.EVOKER_AMBIENT;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIM_STATE, ANIM_NONE);
        this.entityData.define(DATA_BREATH_LOCK, false);
        this.entityData.define(DATA_BREATH_YAW, 0.0F);
        this.entityData.define(DATA_BREATH_PITCH, 0.0F);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("SpellCool", this.spellCool);
        tag.putInt("AnimResetTicks", this.animResetTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.spellCool = tag.getInt("SpellCool");
        this.animResetTicks = tag.getInt("AnimResetTicks");
        this.setConfigurableAttributes();
    }

    private void setFireBreathLock(boolean locked, float yaw, float pitch) {
        this.entityData.set(DATA_BREATH_LOCK, locked);
        if (locked) {
            this.entityData.set(DATA_BREATH_YAW, yaw);
            this.entityData.set(DATA_BREATH_PITCH, pitch);
        }
    }

    public void setAnimationState(int state) {
        if (state == ANIM_NONE
                && this.getCurrentAnimation() == ANIM_FIRE_BREATH_END
                && this.animResetTicks > 0) {
            return;
        }
        if (state != ANIM_FIRE_BREATH_LOOP && state != ANIM_FIRE_BREATH_END) {
            this.setFireBreathLock(false, 0.0F, 0.0F);
        }
        this.entityData.set(ANIM_STATE, state);
        this.animResetTicks = state == ANIM_FIRE_BREATH_END ? FIRE_BREATH_END_TICKS : 0;
    }

    public int getCurrentAnimation() {
        return this.entityData.get(ANIM_STATE);
    }

    public AnimationState getIdleState() {
        return this.idleAnimationState;
    }

    public AnimationState getFireWallState() {
        return this.fireWallAnimationState;
    }

    public AnimationState getFireballState() {
        return this.fireballAnimationState;
    }

    public AnimationState getFieryHideState() {
        return this.fieryHideAnimationState;
    }

    public AnimationState getFireBreathStartState() {
        return this.fireBreathStartAnimationState;
    }

    public AnimationState getFireBreathLoopState() {
        return this.fireBreathLoopAnimationState;
    }

    public AnimationState getFireBreathEndState() {
        return this.fireBreathEndAnimationState;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (ANIM_STATE.equals(accessor) && this.level().isClientSide) {
            switch (this.getCurrentAnimation()) {
                case ANIM_FIREBALL -> {
                    this.fireballAnimationState.start(this.tickCount);
                    this.stopMostAnimation(this.fireballAnimationState);
                }
                case ANIM_FIRE_WALL -> {
                    this.fireWallAnimationState.start(this.tickCount);
                    this.stopMostAnimation(this.fireWallAnimationState);
                }
                case ANIM_FIERY_HIDE -> {
                    this.fieryHideAnimationState.start(this.tickCount);
                    this.stopMostAnimation(this.fieryHideAnimationState);
                }
                case ANIM_FIRE_BREATH_START -> {
                    this.fireBreathStartAnimationState.start(this.tickCount);
                    this.stopMostAnimation(this.fireBreathStartAnimationState);
                }
                case ANIM_FIRE_BREATH_LOOP -> {
                    this.fireBreathLoopAnimationState.start(this.tickCount);
                    this.stopMostAnimation(this.fireBreathLoopAnimationState);
                }
                case ANIM_FIRE_BREATH_END -> {
                    this.fireBreathEndAnimationState.start(this.tickCount);
                    this.stopMostAnimation(this.fireBreathEndAnimationState);
                }
                default -> this.stopMostAnimation(null);
            }
        }
        super.onSyncedDataUpdated(accessor);
    }

    private List<AnimationState> getAllAnimations() {
        List<AnimationState> states = new ArrayList<>();
        states.add(this.idleAnimationState);
        states.add(this.fireWallAnimationState);
        states.add(this.fireballAnimationState);
        states.add(this.fieryHideAnimationState);
        states.add(this.fireBreathStartAnimationState);
        states.add(this.fireBreathLoopAnimationState);
        states.add(this.fireBreathEndAnimationState);
        return states;
    }

    private void stopMostAnimation(@Nullable AnimationState exception) {
        for (AnimationState state : this.getAllAnimations()) {
            if (state != exception) {
                state.stop();
            }
        }
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide) {
            if (this.spellCool > 0) {
                --this.spellCool;
            }
            if (this.animResetTicks > 0 && --this.animResetTicks <= 0) {
                this.setAnimationState(ANIM_NONE);
            }
        }
        if (this.level().isClientSide) {
            if (this.walkAnimation.isMoving() || this.getCurrentAnimation() != ANIM_NONE) {
                this.idleAnimationState.stop();
            } else {
                this.idleAnimationState.animateWhen(true, this.tickCount);
            }
        }
        super.tick();
        if (this.entityData.get(DATA_BREATH_LOCK)) {
            float yaw = this.entityData.get(DATA_BREATH_YAW);
            float pitch = this.entityData.get(DATA_BREATH_PITCH);
            this.setYRot(yaw);
            this.setYBodyRot(yaw);
            this.setYHeadRot(yaw);
            this.setXRot(pitch);
            if (!this.level().isClientSide) {
                this.getNavigation().stop();
                this.getMoveControl().strafe(0.0F, 0.0F);
            }
        }
    }

    private static boolean isHostileAlly(Entity source, Entity other) {
        if (source == other) {
            return true;
        }
        if (!(source instanceof Raider) || source instanceof IOwned) {
            return false;
        }
        return other instanceof Raider && !(other instanceof IOwned);
    }

    private List<LivingEntity> getSupportTargets() {
        List<LivingEntity> targets = new ArrayList<>();
        if (!this.isDeadOrDying()
                && !this.hasEffect(ModEffects.FIERY_HIDE.get())
                && this.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
            targets.add(this);
        }
        targets.addAll(this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(SUPPORT_RANGE),
                entity -> entity != this
                        && isHostileAlly(this, entity)
                        && !entity.isDeadOrDying()
                        && !entity.hasEffect(ModEffects.FIERY_HIDE.get())
                        && entity.getAttribute(Attributes.ATTACK_DAMAGE) != null
                        && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(entity)));
        return targets;
    }

    private boolean supportCombatTriggered(List<LivingEntity> targets) {
        if (this.getTarget() != null) {
            return true;
        }
        for (LivingEntity ally : targets) {
            LivingEntity lastHurtBy = ally.getLastHurtByMob();
            if (lastHurtBy != null && !isHostileAlly(this, lastHurtBy)) {
                return true;
            }
            if (ally instanceof Mob mob
                    && mob.getTarget() != null
                    && mob.getTarget().isAlive()) {
                return true;
            }
        }
        return false;
    }

    private boolean canPlaceFireWall() {
        LivingEntity target = this.getTarget();
        if (target == null) {
            return false;
        }
        HitResult ground = this.level().clip(new ClipContext(
                target.position().add(0.0D, 1.0D, 0.0D),
                target.position().add(0.0D, -15.0D, 0.0D),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                this));
        if (ground.getType() == HitResult.Type.MISS) {
            return false;
        }
        return target.getY() - ground.getLocation().y <= 4.0D;
    }

    private void applySupport() {
        for (LivingEntity target : this.getSupportTargets()) {
            target.addEffect(new MobEffectInstance(ModEffects.FIERY_HIDE.get(), 200, 1, false, true, true), this);
        }
    }

    private void spawnFireWall(int mode) {
        List<Vec3> points = this.buildFireWallPoints(mode);
        if (!this.validWallPoints(points)) {
            return;
        }
        float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.5F;
        FireWallEntity wall = new FireWallEntity(this.level(), this, points, damage, 140);
        this.level().addFreshEntity(wall);
    }

    private List<Vec3> buildFireWallPoints(int mode) {
        LivingEntity target = this.getTarget();
        if (target == null) {
            return List.of();
        }
        Vec3 look = this.getLookAngle();
        Vec3 horizontal = new Vec3(look.x, 0.0D, look.z);
        if (horizontal.lengthSqr() < 1.0E-4D) {
            horizontal = new Vec3(0.0D, 0.0D, 1.0D);
        }
        horizontal = horizontal.normalize();
        if (mode == 0) {
            return FireWallShape.circle(this.level(), target.position(), 3.0F, 20);
        } else if (mode == 1) {
            Vec3 direction = target.position().subtract(this.position());
            direction = new Vec3(direction.x, 0.0D, direction.z);
            if (direction.lengthSqr() < 1.0E-4D) {
                direction = horizontal;
            }
            return FireWallShape.arc(this.level(), this.position(), direction, 4.0F, 90.0F, 12);
        } else {
            Vec3 center = this.position().add(horizontal.scale(3.0D));
            return FireWallShape.line(this.level(), center, horizontal, 7.0F, 7);
        }
    }

    private boolean validWallPoints(List<Vec3> points) {
        if (points.size() < 2) {
            return false;
        }
        for (Vec3 point : points) {
            if (!FireWallShape.hasGround(this.level(), point)) {
                return false;
            }
        }
        return true;
    }

    private class CastingSpellGoal extends Goal {
        private CastingSpellGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return Pyrologer.this.getSpellCastingTime() > 0;
        }

        @Override
        public boolean canContinueToUse() {
            return Pyrologer.this.getSpellCastingTime() > 0;
        }

        @Override
        public void tick() {
            if (Pyrologer.this.entityData.get(DATA_BREATH_LOCK)) {
                return;
            }
            LivingEntity target = Pyrologer.this.getTarget();
            if (target != null) {
                MobUtil.instaLook(Pyrologer.this, target);
            }
            Pyrologer.this.getNavigation().stop();
            Pyrologer.this.getMoveControl().strafe(0.0F, 0.0F);
        }

        @Override
        public void stop() {
            if (Pyrologer.this.getSpellCastingTime() <= 0) {
                Pyrologer.this.setIsCastingSpell(SpellcasterIllager.IllagerSpell.NONE);
            }
        }
    }

    private abstract class SpellGoal extends Goal {
        protected int attackWarmupDelay;
        protected int nextAttackTickCount;

        @Override
        public boolean canUse() {
            LivingEntity target = Pyrologer.this.getTarget();
            if (target != null && target.isAlive()) {
                if (Pyrologer.this.isCastingSpell()) {
                    return false;
                }
                return Pyrologer.this.tickCount >= this.nextAttackTickCount;
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = Pyrologer.this.getTarget();
            return target != null && target.isAlive() && this.attackWarmupDelay > 0;
        }

        @Override
        public void start() {
            this.attackWarmupDelay = this.adjustedTickDelay(this.getCastWarmupTime());
            Pyrologer.this.spellCastingTickCount = this.getCastingTime();
            this.nextAttackTickCount = Pyrologer.this.tickCount + this.getCastingInterval();
            SoundEvent sound = this.getSpellPrepareSound();
            if (sound != null) {
                Pyrologer.this.playSound(sound, 1.0F, 1.0F);
            }
            Pyrologer.this.setIsCastingSpell(SpellcasterIllager.IllagerSpell.FANGS);
        }

        @Override
        public void tick() {
            --this.attackWarmupDelay;
            if (this.attackWarmupDelay == 0) {
                this.performSpellCasting();
                Pyrologer.this.playSound(Pyrologer.this.getCastingSoundEvent(), 1.0F, 1.0F);
            }
        }

        protected abstract void performSpellCasting();

        protected int getCastWarmupTime() {
            return 20;
        }

        protected abstract int getCastingTime();

        protected abstract int getCastingInterval();

        @Nullable
        protected abstract SoundEvent getSpellPrepareSound();
    }

    private class SupportGoal extends SpellGoal {
        private int animationTick;

        @Override
        public boolean canUse() {
            if (Pyrologer.this.level().isClientSide) {
                return false;
            }
            if (Pyrologer.this.fireBreathCasting || Pyrologer.this.spellCool > 0
                    || Pyrologer.this.isCastingSpell()
                    || Pyrologer.this.tickCount < this.nextAttackTickCount) {
                return false;
            }
            List<LivingEntity> targets = Pyrologer.this.getSupportTargets();
            return !targets.isEmpty() && Pyrologer.this.supportCombatTriggered(targets);
        }

        @Override
        public boolean canContinueToUse() {
            return this.animationTick < 34;
        }

        @Override
        public void start() {
            super.start();
            this.animationTick = 0;
            Pyrologer.this.setAnimationState(ANIM_FIERY_HIDE);
        }

        @Override
        public void tick() {
            ++this.animationTick;
            super.tick();
        }

        @Override
        public void stop() {
            Pyrologer.this.setAnimationState(ANIM_NONE);
            Pyrologer.this.spellCastingTickCount = 0;
            Pyrologer.this.setIsCastingSpell(SpellcasterIllager.IllagerSpell.NONE);
            super.stop();
        }

        @Override
        protected void performSpellCasting() {
            Pyrologer.this.applySupport();
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        protected int getCastWarmupTime() {
            return 20;
        }

        @Override
        protected int getCastingTime() {
            return 34;
        }

        @Override
        protected int getCastingInterval() {
            return 300;
        }

        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_SUMMON;
        }
    }

    private class FireBreathGoal extends SpellGoal {
        private final FireBreathSpell spell = new FireBreathSpell() {
            @Override
            public List<Entity> getBreathTarget(LivingEntity caster, double range) {
                return MobUtil.getTargets(caster.level(), caster, range, 3.0D,
                        entity -> entity != caster
                                && !MobUtil.areAllies(caster, entity)
                                && !isHostileAlly(caster, entity));
            }
        };
        private int chargeTicks;
        private int sprayTicks;
        private int sprayDuration;

        @Override
        public boolean canUse() {
            if (Pyrologer.this.level().isClientSide) {
                return false;
            }
            if (Pyrologer.this.fireBreathCasting || Pyrologer.this.spellCool > 0) {
                return false;
            }
            if (!super.canUse()) {
                return false;
            }
            return this.spell.conditionsMet(Pyrologer.this.level(), Pyrologer.this);
        }

        @Override
        public boolean canContinueToUse() {
            if (this.sprayTicks >= this.sprayDuration) {
                return false;
            }
            if (this.sprayTicks > 0) {
                return true;
            }
            LivingEntity target = Pyrologer.this.getTarget();
            return target != null && target.isAlive()
                    && this.spell.conditionsMet(Pyrologer.this.level(), Pyrologer.this);
        }

        @Override
        public void start() {
            int configured = this.spell.shotsNumber(Pyrologer.this, ItemStack.EMPTY);
            int loops = Math.max(1, Math.min(configured, FIRE_BREATH_MAX_SPRAY_TICKS) / FIRE_BREATH_LOOP_TICKS);
            this.sprayDuration = loops * FIRE_BREATH_LOOP_TICKS;
            this.chargeTicks = FIRE_BREATH_CHARGE_TICKS;
            this.sprayTicks = 0;
            Pyrologer.this.fireBreathCasting = true;
            super.start();
            Pyrologer.this.setAnimationState(ANIM_FIRE_BREATH_START);
        }

        @Override
        public void tick() {
            Pyrologer.this.getNavigation().stop();
            Pyrologer.this.getMoveControl().strafe(0.0F, 0.0F);
            if (this.chargeTicks > 0) {
                --this.chargeTicks;
                if (this.chargeTicks == 0) {
                    Pyrologer.this.setAnimationState(ANIM_FIRE_BREATH_LOOP);
                    LivingEntity target = Pyrologer.this.getTarget();
                    if (target != null) {
                        MobUtil.instaLook(Pyrologer.this, target);
                    }
                    Pyrologer.this.setFireBreathLock(true,
                            Pyrologer.this.getYRot(), Pyrologer.this.getXRot());
                }
            }
            if (this.chargeTicks <= 0 && this.sprayTicks < this.sprayDuration) {
                SpellStat stats = WandUtil.getStats(Pyrologer.this, this.spell);
                this.spell.mobSpellResult(Pyrologer.this, ItemStack.EMPTY, stats);
                this.spell.showWandBreath(Pyrologer.this, ItemStack.EMPTY, stats);
                ++this.sprayTicks;
            }
            this.spell.useParticle(Pyrologer.this.level(), Pyrologer.this, ItemStack.EMPTY);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void stop() {
            Pyrologer.this.fireBreathCasting = false;
            Pyrologer.this.setAnimationState(ANIM_FIRE_BREATH_END);
            Pyrologer.this.spellCastingTickCount = 0;
            Pyrologer.this.setIsCastingSpell(SpellcasterIllager.IllagerSpell.NONE);
            Pyrologer.this.spellCool = 40;
            super.stop();
        }

        @Override
        protected void performSpellCasting() {
        }

        @Override
        protected int getCastWarmupTime() {
            return FIRE_BREATH_CHARGE_TICKS;
        }

        @Override
        protected int getCastingTime() {
            return FIRE_BREATH_CHARGE_TICKS + this.sprayDuration;
        }

        @Override
        protected int getCastingInterval() {
            return this.spell.defaultSpellCooldown() * 2;
        }

        @Override
        protected SoundEvent getSpellPrepareSound() {
            return this.spell.CastingSound(Pyrologer.this);
        }
    }

    private class FireWallGoal extends SpellGoal {
        @Nullable
        private Integer wallMode;

        @Override
        public boolean canUse() {
            if (Pyrologer.this.level().isClientSide) {
                return false;
            }
            if (Pyrologer.this.fireBreathCasting || Pyrologer.this.spellCool > 0) {
                return false;
            }
            if (!super.canUse() || !Pyrologer.this.canPlaceFireWall()) {
                return false;
            }
            int mode = Pyrologer.this.random.nextInt(3);
            if (!Pyrologer.this.validWallPoints(Pyrologer.this.buildFireWallPoints(mode))) {
                return false;
            }
            this.wallMode = mode;
            return true;
        }

        @Override
        public void start() {
            super.start();
            Pyrologer.this.setAnimationState(ANIM_FIRE_WALL);
        }

        @Override
        public void stop() {
            this.wallMode = null;
            Pyrologer.this.setAnimationState(ANIM_NONE);
            Pyrologer.this.spellCastingTickCount = 0;
            Pyrologer.this.setIsCastingSpell(SpellcasterIllager.IllagerSpell.NONE);
            Pyrologer.this.spellCool = 40;
            super.stop();
        }

        @Override
        protected void performSpellCasting() {
            Pyrologer.this.spawnFireWall(this.wallMode == null
                    ? Pyrologer.this.random.nextInt(3) : this.wallMode);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        protected int getCastWarmupTime() {
            return 33;
        }

        @Override
        protected int getCastingTime() {
            return 33;
        }

        @Override
        protected int getCastingInterval() {
            return 200;
        }

        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_ATTACK;
        }
    }

    private class FireballGoal extends SpellGoal {
        private int animationTick;
        private boolean launched;
        @Nullable
        private PyromancerFireball fireball;
        @Nullable
        private Vec3 aimPos;

        @Override
        public boolean canUse() {
            if (Pyrologer.this.level().isClientSide) {
                return false;
            }
            if (Pyrologer.this.fireBreathCasting) {
                return false;
            }
            return Pyrologer.this.spellCool <= 0 && super.canUse();
        }

        @Override
        public void start() {
            super.start();
            this.animationTick = 0;
            this.launched = false;
            this.fireball = null;
            this.aimPos = null;
            Pyrologer.this.setAnimationState(ANIM_FIREBALL);
        }

        @Override
        public boolean canContinueToUse() {
            if (this.animationTick >= 30) {
                return false;
            }
            if (this.fireball != null && !this.fireball.isRemoved()) {
                return true;
            }
            return super.canContinueToUse();
        }

        @Override
        public void tick() {
            ++this.animationTick;
            LivingEntity target = Pyrologer.this.getTarget();
            if (this.animationTick < 20 && target != null && target.isAlive()) {
                this.aimPos = target.getBoundingBox().getCenter();
            }
            if (this.animationTick == 5) {
                this.spawnHeldFireball();
            } else if (this.animationTick == 20) {
                this.launchFireball();
            }
            super.tick();
        }

        @Override
        public void stop() {
            if (!this.launched && this.fireball != null && !this.fireball.isRemoved()) {
                this.fireball.discard();
            }
            this.fireball = null;
            Pyrologer.this.setAnimationState(ANIM_NONE);
            Pyrologer.this.spellCastingTickCount = 0;
            Pyrologer.this.setIsCastingSpell(SpellcasterIllager.IllagerSpell.NONE);
            Pyrologer.this.spellCool = 40;
            super.stop();
        }

        private void spawnHeldFireball() {
            LivingEntity target = Pyrologer.this.getTarget();
            if (target == null) {
                return;
            }
            this.aimPos = target.getBoundingBox().getCenter();
            Vec3 look = Pyrologer.this.getLookAngle();
            Vec3 right = new Vec3(-look.z, 0.0D, look.x).normalize();
            Vec3 position = Pyrologer.this.getEyePosition()
                    .add(look.scale(0.55D))
                    .add(right.scale(0.35D))
                    .add(0.0D, -0.35D, 0.0D);
            PyromancerFireball ball = new PyromancerFireball(Pyrologer.this.level(), Pyrologer.this);
            ball.setPos(position.x, position.y, position.z);
            ball.setDamage((float) Pyrologer.this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 2.0F);
            ball.setHomingTarget(target);
            ball.setHeld(true);
            Pyrologer.this.level().addFreshEntity(ball);
            this.fireball = ball;
        }

        private void launchFireball() {
            if (this.fireball == null || this.fireball.isRemoved()) {
                this.fireball = null;
                return;
            }
            LivingEntity target = Pyrologer.this.getTarget();
            Vec3 aim = target != null && target.isAlive()
                    ? target.getBoundingBox().getCenter()
                    : this.aimPos;
            if (aim == null) {
                aim = Pyrologer.this.position().add(Pyrologer.this.getLookAngle().scale(8.0D));
            }
            this.fireball.launch(aim);
            this.launched = true;
            Pyrologer.this.playSound(SoundEvents.EVOKER_CAST_SPELL, 1.0F, 1.0F);
        }

        @Override
        protected void performSpellCasting() {
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        protected int getCastWarmupTime() {
            return 30;
        }

        @Override
        protected int getCastingTime() {
            return 30;
        }

        @Override
        protected int getCastingInterval() {
            return 40;
        }

        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_ATTACK;
        }
    }
}
