package com.qiuyue.goetyominous.common.entities.ally.mobs;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.ZPiglinServant;
import com.qiuyue.goetyominous.config.AttributesConfig;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ZEliteFungusThrower extends ZFungusThrower implements ThrowerAnimatable {

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState throwAnimationState = new AnimationState();
    public final AnimationState healAnimationState = new AnimationState();

    private static final EntityDataAccessor<Boolean> DATA_THROWING =
            SynchedEntityData.defineId(ZEliteFungusThrower.class, EntityDataSerializers.BOOLEAN);

    private boolean lastThrowing;
    private float walkWeight;

    public ZEliteFungusThrower(EntityType<? extends ZPiglinServant> type, Level level) {
        super(type, level);
        this.refreshDimensions();
    }

    @Override
    public float getScale() {
        return 1.25F;
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        EntityDimensions base = super.getDimensions(pose);
        return EntityDimensions.scalable(base.width * 1.25F, base.height * 1.25F - 0.5F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.PiglinBruteServantHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.PiglinBruteServantArmor.get())
                .add(Attributes.FOLLOW_RANGE, 16.0)
                .add(Attributes.MOVEMENT_SPEED, 0.35)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.FungusThrowerServantDamage.get());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_THROWING, false);
    }

    public boolean isThrowing() {
        return this.entityData.get(DATA_THROWING);
    }

    public void setThrowing(boolean throwing) {
        this.entityData.set(DATA_THROWING, throwing);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.removeAllGoals(goal ->
                goal instanceof com.Polarice3.Goety.common.entities.ai.ModMeleeAttackGoal
                        || goal instanceof LookAtPlayerGoal
                        || goal instanceof Summoned.WanderGoal);
        this.goalSelector.addGoal(1, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, net.minecraft.world.entity.LivingEntity.class, 8.0F, 1.2D, 1.2D,
                target -> target == this.getTarget() && target != this.getTrueOwner()));
        this.goalSelector.addGoal(2, new ThrowAcidGoal(this));
        this.goalSelector.addGoal(8, new Summoned.WanderGoal<>(this, 0.6D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 15.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 15.0F));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.setupAnimationStates();
        }
    }

    private void setupAnimationStates() {
        boolean throwing = this.isThrowing();
        if (throwing && !this.lastThrowing) {
            this.throwAnimationState.start(this.tickCount);
        } else if (!throwing && this.lastThrowing) {
            this.throwAnimationState.stop();
            this.idleAnimationState.start(this.tickCount);
        }
        this.walkAnimationState.startIfStopped(this.tickCount);
        this.idleAnimationState.startIfStopped(this.tickCount);
        this.walkWeight = approach(this.walkWeight, this.walkAnimation.speed() > 0.01F ? 1.0F : 0.0F, 0.2F);
        this.lastThrowing = throwing;
    }

    private static float approach(float value, float target, float step) {
        return value < target ? Math.min(value + step, target) : Math.max(value - step, target);
    }

    @Override
    public boolean isHealing() {
        return false;
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
    public AnimationState getHealAnimationState() {
        return this.healAnimationState;
    }

    public AnimationState getThrowAnimationState() {
        return this.throwAnimationState;
    }
}
