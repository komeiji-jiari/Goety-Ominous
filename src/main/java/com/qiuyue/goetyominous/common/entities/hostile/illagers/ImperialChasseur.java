package com.qiuyue.goetyominous.common.entities.hostile.illagers;

import com.Polarice3.Goety.common.entities.hostile.illagers.HuntingIllagerEntity;
import com.Polarice3.Goety.common.entities.projectiles.HeavyArrow;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.common.entities.projectile.ChasseurArrow;
import com.qiuyue.goetyominous.config.AttributesConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;

import java.util.EnumSet;

public class ImperialChasseur extends HuntingIllagerEntity {
    public static final int ANIM_NONE = 0;
    public static final int ANIM_RELOAD_AIM = 1;
    public static final int ANIM_MELEE = 2;
    public static final int ANIM_MELEE_AIM = 3;
    public static final int ANIM_SHOOT = 4;
    public static final int ANIM_RETURN_IDLE = 5;
    public static final int ANIM_AIM_RELOAD = 6;

    private static final double ATTACK_RANGE = 32.0D;
    private static final double MELEE_RANGE = 3.0D;
    private static final int MELEE_COOLDOWN = 10;
    private static final double CHASE_SPEED = 1.25D;
    private static final double SWEEP_RADIUS = 3.0D;
    private static final double SWEEP_HEIGHT = 1.0D;
    private static final double SWEEP_ANGLE = 45.0D;
    private static final int HEAVY_SHOTS_BEFORE_CHASSEUR = 2;

    private static final EntityDataAccessor<Integer> DATA_ANIM_STATE =
            SynchedEntityData.defineId(ImperialChasseur.class, EntityDataSerializers.INT);

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState reloadAnimationState = new AnimationState();
    public final AnimationState meleeAnimationState = new AnimationState();
    public final AnimationState meleeAimAnimationState = new AnimationState();
    public final AnimationState shootAnimationState = new AnimationState();
    public final AnimationState returnIdleAnimationState = new AnimationState();
    public final AnimationState aimReloadAnimationState = new AnimationState();

    private int meleeCooldown;
    private int heavyShots;

    public ImperialChasseur(EntityType<? extends ImperialChasseur> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.ImperialChasseurHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.ImperialChasseurArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.ImperialChasseurToughness.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.ImperialChasseurDamage.get())
                .add(Attributes.ATTACK_KNOCKBACK, AttributesConfig.ImperialChasseurAttackKnockback.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.ImperialChasseurSpeed.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.ImperialChasseurFollowRange.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.ImperialChasseurKnockbackResistance.get());
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.ImperialChasseurHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.ImperialChasseurArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS), AttributesConfig.ImperialChasseurToughness.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.ImperialChasseurDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_KNOCKBACK), AttributesConfig.ImperialChasseurAttackKnockback.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MOVEMENT_SPEED), AttributesConfig.ImperialChasseurSpeed.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.FOLLOW_RANGE), AttributesConfig.ImperialChasseurFollowRange.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.KNOCKBACK_RESISTANCE), AttributesConfig.ImperialChasseurKnockbackResistance.get());
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new CombatGoal(this));
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
    protected SoundEvent getCastingSoundEvent() {
        return SoundEvents.EMPTY;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ANIM_STATE, ANIM_NONE);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.meleeCooldown = compound.getInt("MeleeCooldown");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("MeleeCooldown", this.meleeCooldown);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.idleAnimationState.animateWhen(this.getAnimState() == ANIM_NONE, this.tickCount);
        } else if (this.meleeCooldown > 0) {
            --this.meleeCooldown;
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (DATA_ANIM_STATE.equals(accessor) && this.level().isClientSide) {
            this.stopActionAnimations();
            switch (this.getAnimState()) {
                case ANIM_RELOAD_AIM -> this.reloadAnimationState.startIfStopped(this.tickCount);
                case ANIM_MELEE -> this.meleeAnimationState.startIfStopped(this.tickCount);
                case ANIM_MELEE_AIM -> this.meleeAimAnimationState.startIfStopped(this.tickCount);
                case ANIM_SHOOT -> this.shootAnimationState.startIfStopped(this.tickCount);
                case ANIM_RETURN_IDLE -> this.returnIdleAnimationState.startIfStopped(this.tickCount);
                case ANIM_AIM_RELOAD -> this.aimReloadAnimationState.startIfStopped(this.tickCount);
                default -> {
                }
            }
        }
        super.onSyncedDataUpdated(accessor);
    }

    private void stopActionAnimations() {
        this.reloadAnimationState.stop();
        this.meleeAnimationState.stop();
        this.meleeAimAnimationState.stop();
        this.shootAnimationState.stop();
        this.returnIdleAnimationState.stop();
        this.aimReloadAnimationState.stop();
    }

    public int getAnimState() {
        return this.entityData.get(DATA_ANIM_STATE);
    }

    private void setAnimState(int state) {
        this.entityData.set(DATA_ANIM_STATE, state);
    }

    private int getMeleeCooldown() {
        return this.meleeCooldown;
    }

    private void setMeleeCooldown(int ticks) {
        this.meleeCooldown = ticks;
    }

    private void setHeavyShots(int shots) {
        this.heavyShots = shots;
    }

    private void performRangedAttack(LivingEntity target) {
        AbstractArrow arrow;
        if (this.heavyShots >= HEAVY_SHOTS_BEFORE_CHASSEUR) {
            arrow = new ChasseurArrow(this.level(), this);
            this.heavyShots = 0;
        } else {
            HeavyArrow heavyArrow = new HeavyArrow(this.level(), this);
            heavyArrow.setBaseDamage(AttributesConfig.ImperialChasseurRangeDamage.get());
            heavyArrow.setPierceLevel((byte) 4);
            arrow = heavyArrow;
            ++this.heavyShots;
        }
        arrow.setSoundEvent(SoundEvents.CROSSBOW_HIT);
        arrow.setShotFromCrossbow(true);
        double dx = target.getX() - this.getX();
        double dy = target.getY() + (double) target.getBbHeight() / 2.0D - this.getEyeY();
        double dz = target.getZ() - this.getZ();
        arrow.shoot(dx, dy, dz, 4.8F, 0.0F);
        this.playSound(SoundEvents.CROSSBOW_SHOOT, 1.0F,
                1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level().addFreshEntity(arrow);
    }

    private void sweep() {
        for (LivingEntity target : MobUtil.getAttackableLivingEntitiesNearby(this, SWEEP_RADIUS, SWEEP_HEIGHT, SWEEP_RADIUS, SWEEP_RADIUS)) {
            float angle = MobUtil.getRelativeAngle(this, target);
            if (Math.abs(angle) <= SWEEP_ANGLE || angle >= 360.0F - SWEEP_ANGLE || angle <= -(360.0F - SWEEP_ANGLE)) {
                this.doHurtTarget(target);
            }
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.PILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.PILLAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PILLAGER_DEATH;
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.PILLAGER_CELEBRATE;
    }

    public static class CombatGoal extends Goal {
        private final ImperialChasseur mob;
        private int actionTick;

        public CombatGoal(ImperialChasseur mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.mob.getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public boolean canContinueToUse() {
            if (this.mob.getAnimState() != ANIM_NONE) {
                return true;
            }
            LivingEntity target = this.mob.getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public void start() {
            this.actionTick = 0;
        }

        @Override
        public void stop() {
            this.actionTick = 0;
            this.mob.setAnimState(ANIM_NONE);
            this.mob.setHeavyShots(0);
            this.mob.getNavigation().stop();
        }

        @Override
        public void tick() {
            if (this.mob.getAnimState() == ANIM_NONE) {
                this.tickIdle();
            } else {
                this.tickAction();
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        private void tickIdle() {
            LivingEntity target = this.mob.getTarget();
            if (target == null || !target.isAlive()) {
                this.mob.setHeavyShots(0);
                this.mob.getNavigation().stop();
                return;
            }
            double distance = this.mob.distanceTo(target);
            if (distance > ATTACK_RANGE) {
                this.mob.getNavigation().moveTo(target, CHASE_SPEED);
                return;
            }
            this.mob.getNavigation().stop();
            if (distance <= MELEE_RANGE) {
                if (this.mob.getMeleeCooldown() <= 0) {
                    this.startMelee(false);
                }
                return;
            }
            if (this.mob.getMeleeCooldown() <= 0 && this.mob.hasLineOfSight(target)) {
                this.startReloadAim();
            }
        }

        private void tickAction() {
            LivingEntity target = this.mob.getTarget();
            ++this.actionTick;
            this.mob.getNavigation().stop();
            if (target != null && target.isAlive()) {
                MobUtil.instaLook(this.mob, target);
            }
            switch (this.mob.getAnimState()) {
                case ANIM_RELOAD_AIM, ANIM_AIM_RELOAD -> {
                    if (this.actionTick >= 35) {
                        if (target != null && target.isAlive()) {
                            if (this.mob.distanceTo(target) <= MELEE_RANGE) {
                                this.startMelee(true);
                            } else {
                                this.startShoot();
                            }
                        } else {
                            this.startReturnIdle();
                        }
                    }
                }
                case ANIM_MELEE, ANIM_MELEE_AIM -> {
                    if (this.actionTick == 18 && target != null && target.isAlive()) {
                        this.mob.sweep();
                    }
                    if (this.actionTick >= 30) {
                        if (this.mob.getAnimState() == ANIM_MELEE) {
                            this.mob.setMeleeCooldown(MELEE_COOLDOWN);
                            this.finishAction();
                        } else if (target != null && target.isAlive()) {
                            this.startShoot();
                        } else {
                            this.startReturnIdle();
                        }
                    }
                }
                case ANIM_SHOOT -> {
                    if (this.actionTick == 12 && target != null && target.isAlive()) {
                        this.mob.performRangedAttack(target);
                    }
                    if (this.actionTick >= 22) {
                        if (target != null && target.isAlive()) {
                            if (this.mob.distanceTo(target) <= MELEE_RANGE) {
                                this.startMelee(true);
                            } else {
                                this.startAimReload();
                            }
                        } else {
                            this.startReturnIdle();
                        }
                    }
                }
                case ANIM_RETURN_IDLE -> {
                    if (this.actionTick >= 20) {
                        if (target != null && target.isAlive()) {
                            double distance = this.mob.distanceTo(target);
                            if (distance <= MELEE_RANGE) {
                                if (this.mob.getMeleeCooldown() <= 0) {
                                    this.startMelee(false);
                                }
                            } else if (distance <= ATTACK_RANGE && this.mob.getMeleeCooldown() <= 0 && this.mob.hasLineOfSight(target)) {
                                this.startReloadAim();
                            } else {
                                this.finishAction();
                            }
                        } else {
                            this.finishAction();
                        }
                    }
                }
                default -> this.finishAction();
            }
        }

        private void startReloadAim() {
            this.mob.setAnimState(ANIM_RELOAD_AIM);
            this.mob.playSound(SoundEvents.CROSSBOW_LOADING_START);
            this.actionTick = 0;
            this.mob.getNavigation().stop();
        }

        private void startAimReload() {
            this.mob.setAnimState(ANIM_AIM_RELOAD);
            this.mob.playSound(SoundEvents.CROSSBOW_LOADING_START);
            this.actionTick = 0;
            this.mob.getNavigation().stop();
        }

        private void startMelee(boolean aim) {
            this.mob.setAnimState(aim ? ANIM_MELEE_AIM : ANIM_MELEE);
            this.actionTick = 0;
            this.mob.getNavigation().stop();
        }

        private void startShoot() {
            this.mob.setAnimState(ANIM_SHOOT);
            this.actionTick = 0;
            this.mob.getNavigation().stop();
        }

        private void startReturnIdle() {
            this.mob.setAnimState(ANIM_RETURN_IDLE);
            this.actionTick = 0;
            this.mob.getNavigation().stop();
        }

        private void finishAction() {
            this.mob.setAnimState(ANIM_NONE);
            this.actionTick = 0;
        }
    }
}
