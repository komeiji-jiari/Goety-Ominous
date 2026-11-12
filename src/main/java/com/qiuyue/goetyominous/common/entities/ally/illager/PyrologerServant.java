package com.qiuyue.goetyominous.common.entities.ally.illager;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ai.AvoidTargetGoal;
import com.Polarice3.Goety.common.entities.ally.illager.SpellcasterIllagerServant;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.spells.nether.FireBreathSpell;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.WandUtil;
import com.qiuyue.goetyominous.common.entities.projectile.PyromancerFireball;
import com.qiuyue.goetyominous.common.entities.util.FireWallEntity;
import com.qiuyue.goetyominous.common.entities.util.FireWallShape;
import com.qiuyue.goetyominous.common.init.ModEffects;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.utils.ServantAllyUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PyrologerServant extends SpellcasterIllagerServant {
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(PyrologerServant.class,
            EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_BREATH_LOCK = SynchedEntityData.defineId(
            PyrologerServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> DATA_BREATH_YAW = SynchedEntityData.defineId(
            PyrologerServant.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_BREATH_PITCH = SynchedEntityData.defineId(
            PyrologerServant.class, EntityDataSerializers.FLOAT);
    private static final double SUPPORT_RANGE = 16.0D;
    private static final int SUPPORT_MAX_SERVANTS = 7;
    private static final int FIRE_BREATH_CHARGE_TICKS = 13;
    private static final int FIRE_BREATH_LOOP_TICKS = 20;
    private static final int FIRE_BREATH_MAX_SPRAY_TICKS = 100;
    private static final int ANIM_NONE = 0;
    private static final int ANIM_FIREBALL = 1;
    private static final int ANIM_FIRE_WALL = 2;
    private static final int ANIM_FIERY_HIDE = 3;
    private static final int ANIM_FIRE_BREATH_START = 4;
    private static final int ANIM_FIRE_BREATH_LOOP = 5;
    private static final int ANIM_FIRE_BREATH_END = 6;
    private static final int FIRE_BREATH_END_TICKS = 12;
    private static final byte EVENT_ICE_DEATH = 61;
    private final AnimationState idleAnimationState = new AnimationState();
    private final AnimationState fireWallAnimationState = new AnimationState();
    private final AnimationState fireballAnimationState = new AnimationState();
    private final AnimationState fieryHideAnimationState = new AnimationState();
    private final AnimationState fireBreathStartAnimationState = new AnimationState();
    private final AnimationState fireBreathLoopAnimationState = new AnimationState();
    private final AnimationState fireBreathEndAnimationState = new AnimationState();
    private final AnimationState deathAnimationState = new AnimationState();
    private int spellCool;
    private int animResetTicks;
    private boolean playDeathAnimation;
    private float deathRotation;
    private int iceDeathTime;
    private boolean fireBreathCasting;

    public PyrologerServant(EntityType<? extends PyrologerServant> type, Level level) {
        super(type, level);
        this.xpReward = 20;
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
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
        this.goalSelector.addGoal(3, new AvoidTargetGoal(this, LivingEntity.class, 8.0F, 0.6D, 1.0D));
        this.goalSelector.addGoal(4, new FireBreathGoal());
        this.goalSelector.addGoal(5, new FireWallGoal());
        this.goalSelector.addGoal(6, new FireballGoal());
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
    public boolean fireImmune() {
        return true;
    }

    @Override
    public void spellParticles() {
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

    @Override
    public EntityType<? extends Mob> getNecroConvert() {
        return ModEntityTypes.BOUND_PYROLOGER.get();
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
        if (this.isDeadOrDying() && (this.playDeathAnimation || this.deathAnimationState.isStarted())) {
            for (AnimationState state : this.getAllAnimations()) {
                if (state != this.deathAnimationState) {
                    state.stop();
                }
            }
            this.setYRot(this.deathRotation);
            this.setYBodyRot(this.deathRotation);
            if (!this.level().isClientSide) {
                this.getNavigation().stop();
                this.getMoveControl().strafe(0.0F, 0.0F);
            }
        }
        if (!this.isDeadOrDying() && this.entityData.get(DATA_BREATH_LOCK)) {
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

    private List<AnimationState> getAllAnimations() {
        List<AnimationState> states = new ArrayList<>();
        states.add(this.idleAnimationState);
        states.add(this.fireWallAnimationState);
        states.add(this.fireballAnimationState);
        states.add(this.fieryHideAnimationState);
        states.add(this.fireBreathStartAnimationState);
        states.add(this.fireBreathLoopAnimationState);
        states.add(this.fireBreathEndAnimationState);
        states.add(this.deathAnimationState);
        return states;
    }

    private void stopMostAnimation(@Nullable AnimationState exception) {
        for (AnimationState state : this.getAllAnimations()) {
            if (state != exception && state != this.deathAnimationState) {
                state.stop();
            }
        }
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

    public AnimationState getDeathState() {
        return this.deathAnimationState;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.spellCool = tag.getInt("SpellCool");
        this.animResetTicks = tag.getInt("AnimResetTicks");
        this.setConfigurableAttributes();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("SpellCool", this.spellCool);
        tag.putInt("AnimResetTicks", this.animResetTicks);
    }

    @Override
    protected void dropCustomDeathLoot(net.minecraft.world.damagesource.DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
    }

    @Override
    public void die(net.minecraft.world.damagesource.DamageSource source) {
        boolean play = !this.level().isClientSide
                && this.getTrueOwner() instanceof Player
                && source.is(DamageTypeTags.IS_FREEZING)
                && source.getEntity() instanceof LivingEntity
                && this.random.nextFloat() < 0.5F;
        super.die(source);
        if (play) {
            this.playDeathAnimation = true;
            this.iceDeathTime = 0;
            this.deathRotation = this.getYRot();
            this.hurtTime = 0;
            this.hurtDuration = 0;
            this.deathTime = 0;
            this.level().broadcastEntityEvent(this, EVENT_ICE_DEATH);
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == EVENT_ICE_DEATH) {
            this.stopMostAnimation(this.deathAnimationState);
            this.deathAnimationState.start(this.tickCount);
            this.deathRotation = this.getYRot();
            this.hurtTime = 0;
            this.hurtDuration = 0;
            this.deathTime = 0;
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    protected void tickDeath() {
        boolean extended = this.playDeathAnimation || this.deathAnimationState.isStarted();
        if (extended) {
            ++this.iceDeathTime;
            this.deathTime = 0;
            this.setYRot(this.deathRotation);
            this.setYBodyRot(this.deathRotation);
            if (this.iceDeathTime >= 30 && !this.level().isClientSide) {
                this.spawnAnim();
                this.remove(RemovalReason.KILLED);
            }
            return;
        }
        if (this.deathTime >= 19 && !this.level().isClientSide) {
            this.spawnAnim();
        }
        super.tickDeath();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        return super.mobInteract(player, hand);
    }

    private static boolean canSupport(LivingEntity entity) {
        return !entity.isDeadOrDying()
                && !entity.hasEffect(ModEffects.FIERY_HIDE.get())
                && entity.getAttribute(Attributes.ATTACK_DAMAGE) != null
                && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(entity);
    }

    private List<LivingEntity> getSupportTargets() {
        List<LivingEntity> targets = new ArrayList<>();
        if (canSupport(this)) {
            targets.add(this);
        }
        LivingEntity owner = this.getTrueOwner();
        if (owner != null
                && this.getBoundingBox().inflate(SUPPORT_RANGE).intersects(owner.getBoundingBox())
                && !owner.isDeadOrDying()
                && owner.getAttribute(Attributes.ATTACK_DAMAGE) != null
                && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(owner)) {
            targets.add(owner);
        }
        List<LivingEntity> servants = this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(SUPPORT_RANGE),
                entity -> entity != this
                        && entity != owner
                        && owner != null
                        && entity instanceof Summoned
                        && !(entity instanceof PyrologerServant)
                        && (MobUtil.getOwner(entity) == owner || MobUtil.areAllies(owner, entity))
                        && canSupport(entity));
        servants.sort(Comparator.comparingDouble(LivingEntity::getHealth).reversed());
        if (servants.size() > SUPPORT_MAX_SERVANTS) {
            servants = new ArrayList<>(servants.subList(0, SUPPORT_MAX_SERVANTS));
        }
        targets.addAll(servants);
        return targets;
    }

    private boolean supportCombatTriggered(List<LivingEntity> targets) {
        if (this.getTarget() != null) {
            return true;
        }
        for (LivingEntity ally : targets) {
            LivingEntity lastHurtBy = ally.getLastHurtByMob();
            if (lastHurtBy != null && !MobUtil.areAllies(lastHurtBy, this)) {
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

    private class CastingSpellGoal extends SpellcasterCastingSpellGoal {
        @Override
        public boolean canContinueToUse() {
            return PyrologerServant.this.getSpellCastingTime() > 0;
        }

        @Override
        public void tick() {
            if (PyrologerServant.this.entityData.get(DATA_BREATH_LOCK)) {
                return;
            }
            LivingEntity target = PyrologerServant.this.getTarget();
            if (target != null) {
                MobUtil.instaLook(PyrologerServant.this, target);
            }
            PyrologerServant.this.getNavigation().stop();
            PyrologerServant.this.getMoveControl().strafe(0.0F, 0.0F);
        }

        @Override
        public void stop() {
            if (PyrologerServant.this.getSpellCastingTime() <= 0) {
                super.stop();
            }
        }
    }

    private class FireballGoal extends SpellcasterUseSpellGoal {
        private int animationTick;
        private boolean launched;
        @Nullable
        private PyromancerFireball fireball;
        @Nullable
        private Vec3 aimPos;

        @Override
        public boolean canUse() {
            if (PyrologerServant.this.level().isClientSide) {
                return false;
            }
            if (PyrologerServant.this.fireBreathCasting) {
                return false;
            }
            return PyrologerServant.this.spellCool <= 0 && super.canUse();
        }

        @Override
        public void start() {
            super.start();
            this.animationTick = 0;
            this.launched = false;
            this.fireball = null;
            this.aimPos = null;
            PyrologerServant.this.setAnimationState(ANIM_FIREBALL);
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
            LivingEntity target = PyrologerServant.this.getTarget();
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
            PyrologerServant.this.setAnimationState(ANIM_NONE);
            PyrologerServant.this.spellCastingTickCount = 0;
            PyrologerServant.this.setIsCastingSpell(IllagerServantSpell.NONE);
            PyrologerServant.this.spellCool = 40;
            super.stop();
        }

        private void spawnHeldFireball() {
            LivingEntity target = PyrologerServant.this.getTarget();
            if (target == null) {
                return;
            }
            this.aimPos = target.getBoundingBox().getCenter();
            Vec3 look = PyrologerServant.this.getLookAngle();
            Vec3 right = new Vec3(-look.z, 0.0D, look.x).normalize();
            Vec3 position = PyrologerServant.this.getEyePosition()
                    .add(look.scale(0.55D))
                    .add(right.scale(0.35D))
                    .add(0.0D, -0.35D, 0.0D);
            PyromancerFireball ball = new PyromancerFireball(PyrologerServant.this.level(), PyrologerServant.this);
            ball.setPos(position.x, position.y, position.z);
            ball.setDamage((float) PyrologerServant.this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 2.0F);
            ball.setHomingTarget(target);
            ball.setHeld(true);
            PyrologerServant.this.level().addFreshEntity(ball);
            this.fireball = ball;
        }

        private void launchFireball() {
            if (this.fireball == null || this.fireball.isRemoved()) {
                this.fireball = null;
                return;
            }
            LivingEntity target = PyrologerServant.this.getTarget();
            Vec3 aim = target != null && target.isAlive()
                    ? target.getBoundingBox().getCenter()
                    : this.aimPos;
            if (aim == null) {
                aim = PyrologerServant.this.position().add(PyrologerServant.this.getLookAngle().scale(8.0D));
            }
            this.fireball.launch(aim);
            this.launched = true;
            PyrologerServant.this.playSound(SoundEvents.EVOKER_CAST_SPELL, 1.0F, 1.0F);
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
        protected void performSpellCasting() {
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_ATTACK;
        }

        @Override
        protected IllagerServantSpell getSpell() {
            return IllagerServantSpell.FANGS;
        }
    }

    private class FireWallGoal extends SpellcasterUseSpellGoal {
        @Nullable
        private Integer wallMode;

        @Override
        public boolean canUse() {
            if (PyrologerServant.this.level().isClientSide) {
                return false;
            }
            if (PyrologerServant.this.fireBreathCasting) {
                return false;
            }
            if (PyrologerServant.this.spellCool > 0) {
                return false;
            }
            if (!super.canUse() || !PyrologerServant.this.canPlaceFireWall()) {
                return false;
            }
            int mode = PyrologerServant.this.random.nextInt(3);
            if (!PyrologerServant.this.validWallPoints(PyrologerServant.this.buildFireWallPoints(mode))) {
                return false;
            }
            this.wallMode = mode;
            return true;
        }

        @Override
        public void start() {
            super.start();
            PyrologerServant.this.setAnimationState(ANIM_FIRE_WALL);
        }

        @Override
        public void stop() {
            this.wallMode = null;
            PyrologerServant.this.setAnimationState(ANIM_NONE);
            PyrologerServant.this.spellCastingTickCount = 0;
            PyrologerServant.this.setIsCastingSpell(IllagerServantSpell.NONE);
            PyrologerServant.this.spellCool = 40;
            super.stop();
        }

        @Override
        protected void performSpellCasting() {
            PyrologerServant.this.spawnFireWall(this.wallMode == null
                    ? PyrologerServant.this.random.nextInt(3) : this.wallMode);
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

        @Override
        protected IllagerServantSpell getSpell() {
            return IllagerServantSpell.FANGS;
        }
    }

    private class SupportGoal extends SpellcasterUseSpellGoal {
        private int animationTick;

        @Override
        public boolean canUse() {
            if (PyrologerServant.this.level().isClientSide) {
                return false;
            }
            if (PyrologerServant.this.fireBreathCasting) {
                return false;
            }
            if (PyrologerServant.this.spellCool > 0) {
                return false;
            }
            if (PyrologerServant.this.isCastingSpell()) {
                return false;
            }
            if (PyrologerServant.this.tickCount < this.nextAttackTickCount) {
                return false;
            }
            List<LivingEntity> targets = PyrologerServant.this.getSupportTargets();
            return !targets.isEmpty()
                    && PyrologerServant.this.supportCombatTriggered(targets);
        }

        @Override
        public boolean canContinueToUse() {
            return this.animationTick < 34;
        }

        @Override
        public void start() {
            super.start();
            this.animationTick = 0;
            PyrologerServant.this.setAnimationState(ANIM_FIERY_HIDE);
        }

        @Override
        public void tick() {
            ++this.animationTick;
            super.tick();
        }

        @Override
        public void stop() {
            PyrologerServant.this.setAnimationState(ANIM_NONE);
            PyrologerServant.this.spellCastingTickCount = 0;
            PyrologerServant.this.setIsCastingSpell(IllagerServantSpell.NONE);
            super.stop();
        }

        @Override
        protected void performSpellCasting() {
            PyrologerServant.this.applySupport();
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

        @Override
        protected IllagerServantSpell getSpell() {
            return IllagerServantSpell.FANGS;
        }
    }

    private class FireBreathGoal extends SpellcasterUseSpellGoal {
        private final FireBreathSpell spell = new FireBreathSpell() {
            @Override
            public List<Entity> getBreathTarget(LivingEntity caster, double range) {
                return MobUtil.getTargets(caster.level(), caster, range, 3.0D,
                        entity -> entity != caster && !ServantAllyUtil.areAllied(caster, entity));
            }

            @Override
            public void SpellResult(ServerLevel level, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
                int burning = spellStat.getBurning();
                float damage = this.damage + spellStat.getPotency();
                for (Entity target : this.getBreathTarget(caster, spellStat.getRange())) {
                    if (!(target instanceof LivingEntity living)) {
                        continue;
                    }
                    DamageSource source = ModDamageSource.fireBreath(caster, caster);
                    if (!living.hurt(source, damage)) {
                        continue;
                    }
                    living.addEffect(new MobEffectInstance(
                            GoetyEffects.SOUL_BURN.get(), 5 * burning * 20, 0), caster);
                }
            }
        };

        private int chargeTicks;
        private int sprayTicks;
        private int sprayDuration;

        @Override
        public boolean canUse() {
            if (PyrologerServant.this.level().isClientSide) {
                return false;
            }
            if (PyrologerServant.this.fireBreathCasting) {
                return false;
            }
            if (PyrologerServant.this.spellCool > 0) {
                return false;
            }
            if (!super.canUse()) {
                return false;
            }
            return this.spell.conditionsMet(PyrologerServant.this.level(), PyrologerServant.this);
        }

        @Override
        public boolean canContinueToUse() {
            if (this.sprayTicks >= this.sprayDuration) {
                return false;
            }
            if (this.sprayTicks > 0) {
                return true;
            }
            LivingEntity target = PyrologerServant.this.getTarget();
            return target != null && target.isAlive()
                    && this.spell.conditionsMet(PyrologerServant.this.level(), PyrologerServant.this);
        }

        @Override
        public void start() {
            int configured = this.spell.shotsNumber(PyrologerServant.this, ItemStack.EMPTY);
            int loops = Math.max(1, Math.min(configured, FIRE_BREATH_MAX_SPRAY_TICKS) / FIRE_BREATH_LOOP_TICKS);
            this.sprayDuration = loops * FIRE_BREATH_LOOP_TICKS;
            this.chargeTicks = FIRE_BREATH_CHARGE_TICKS;
            this.sprayTicks = 0;
            PyrologerServant.this.fireBreathCasting = true;
            super.start();
            PyrologerServant.this.setAnimationState(ANIM_FIRE_BREATH_START);
        }

        @Override
        public void tick() {
            PyrologerServant.this.getNavigation().stop();
            PyrologerServant.this.getMoveControl().strafe(0.0F, 0.0F);
            if (this.chargeTicks > 0) {
                --this.chargeTicks;
                if (this.chargeTicks == 0) {
                    PyrologerServant.this.setAnimationState(ANIM_FIRE_BREATH_LOOP);
                    LivingEntity target = PyrologerServant.this.getTarget();
                    if (target != null) {
                        MobUtil.instaLook(PyrologerServant.this, target);
                    }
                    PyrologerServant.this.setFireBreathLock(true,
                            PyrologerServant.this.getYRot(), PyrologerServant.this.getXRot());
                }
            }
            if (this.chargeTicks <= 0 && this.sprayTicks < this.sprayDuration) {
                SpellStat stats = WandUtil.getStats(PyrologerServant.this, this.spell);
                this.spell.mobSpellResult(PyrologerServant.this, ItemStack.EMPTY, stats);
                this.spell.showWandBreath(PyrologerServant.this, ItemStack.EMPTY, stats);
                ++this.sprayTicks;
            }
            this.spell.useParticle(PyrologerServant.this.level(), PyrologerServant.this, ItemStack.EMPTY);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void stop() {
            PyrologerServant.this.fireBreathCasting = false;
            PyrologerServant.this.setAnimationState(ANIM_FIRE_BREATH_END);
            PyrologerServant.this.spellCastingTickCount = 0;
            PyrologerServant.this.setIsCastingSpell(IllagerServantSpell.NONE);
            PyrologerServant.this.spellCool = 40;
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
            return this.spell.CastingSound(PyrologerServant.this);
        }

        @Override
        protected IllagerServantSpell getSpell() {
            return IllagerServantSpell.FANGS;
        }
    }
}
