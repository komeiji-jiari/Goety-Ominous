package com.qiuyue.goetyominous.common.entities.ally.lm;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IAttackGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IMoveGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IStateGoal;
import com.qiuyue.goetyominous.config.AttributesConfig;
import net.miauczel.legendary_monsters.config.ModConfig;
import net.miauczel.legendary_monsters.sound.ModSounds;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.List;

public class WanderingEyeServant extends AbstractFlamebornServant {

    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState rollAnimationState = new AnimationState();
    public AnimationState deathAnimationState = new AnimationState();

    public WanderingEyeServant(EntityType<? extends Summoned> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 3;
        this.setPersistenceRequired();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(6, new Summoned.WanderGoal<>(this, this.getFollowSpeed(), 40, 0.001F));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(2, new IMoveGoal(this, false, 3.0D));
        this.goalSelector.addGoal(1, new IStateGoal(this, 3, 3, 0, 60, 0));
        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 2, 0, 55, 55, 2.5F) {
            @Override
            public boolean canUse() {
                return super.canUse() && WanderingEyeServant.this.getTarget() != null;
            }
        });
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.idleAnimationState.animateWhen(this.getAttackState() == 0, this.tickCount);
        }
        this.updateWithAttack();
    }

    public void updateWithAttack() {
        if (this.getAttackState() == 2) {
            if (this.attackTicks == 19) {
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.35F);
                this.calculatedDash(0.25F);
            }
            if (this.attackTicks == 22) {
                this.SideAreaAttack(2.5F, 2.5F, 180.0F, 0.0F, 0.0F, 12.0F, 0, SoundEvents.EMPTY, 1.0F);
            }
        }
    }

    public void SideAreaAttack(float range, float height, float arc, float boxOffset, float forwardOffset, float damage, int brokenShieldTicks, SoundEvent soundEvent, float pitch) {
        double theta = Math.toRadians(this.yBodyRot) + 1.5707963267948966D;
        double forwardX = Math.cos(theta) * (double) forwardOffset;
        double forwardZ = Math.sin(theta) * (double) forwardOffset;
        List<LivingEntity> entitiesHit = this.getEntityLivingBaseNearby(range, height, range, range);
        for (LivingEntity entityHit : entitiesHit) {
            double dx = entityHit.getX() - (this.getX() + forwardX);
            double dz = entityHit.getZ() - (this.getZ() + forwardZ);
            float entityHitAngle = (float) ((Math.toDegrees(Math.atan2(dz, dx)) - 90.0D) % 360.0D);
            if (entityHitAngle < 0.0F) {
                entityHitAngle += 360.0F;
            }
            float entityAttackingAngle = (this.yBodyRot - boxOffset) % 360.0F;
            if (entityAttackingAngle < 0.0F) {
                entityAttackingAngle += 360.0F;
            }
            float entityHitDistance = (float) Math.sqrt(dx * dx + dz * dz);
            float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
            if (entityHitDistance > range) {
                continue;
            }
            if (!(entityRelativeAngle <= arc / 2.0F && entityRelativeAngle >= -arc / 2.0F)
                    && !(entityRelativeAngle >= 360.0F - arc / 2.0F)
                    && !(entityRelativeAngle <= -360.0F + arc / 2.0F)) {
                continue;
            }
            if (MobUtil.areAllies(this, entityHit) || entityHit == this) {
                continue;
            }
            boolean flag = entityHit.hurt(this.damageSources().mobAttack(this),
                    (float) ((double) damage * ModConfig.MOB_CONFIG.WanderingEyeDamageMutliplier.get()));
            if (flag) {
                this.playSound(soundEvent, 1.0F, pitch);
            }
            if (entityHit instanceof Player && entityHit.isBlocking() && brokenShieldTicks > 0) {
                disableShield(entityHit, brokenShieldTicks);
            }
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.WanderingEyeServantHealth.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.WanderingEyeServantKnockbackResistance.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.WanderingEyeServantFollowRange.get())
                .add(Attributes.ARMOR, AttributesConfig.WanderingEyeServantArmor.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.WanderingEyeServantMovementSpeed.get())
                .add(Attributes.ATTACK_KNOCKBACK, AttributesConfig.WanderingEyeServantAttackKnockback.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.WanderingEyeServantDamage.get());
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.WanderingEyeServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.WanderingEyeServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.WanderingEyeServantDamage.get());
    }

    @Override
    public double getFollowSpeed() {
        return 3.0D;
    }

    @Override
    public double getCommandSpeed() {
        return 3.0D;
    }

    public AnimationState getAnimationState(String input) {
        if (input.equals("idle")) {
            return this.idleAnimationState;
        } else if (input.equals("roll")) {
            return this.rollAnimationState;
        } else if (input.equals("death")) {
            return this.deathAnimationState;
        }
        return new AnimationState();
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (ATTACK_STATE.equals(accessor) && this.level().isClientSide) {
            switch (this.getAttackState()) {
                case 0:
                    this.stopAllAnimationStates();
                    break;
                case 1:
                    this.stopAllAnimationStates();
                    this.idleAnimationState.startIfStopped(this.tickCount);
                    break;
                case 2:
                    this.stopAllAnimationStates();
                    this.rollAnimationState.startIfStopped(this.tickCount);
                    break;
                case 3:
                    this.stopAllAnimationStates();
                    this.deathAnimationState.startIfStopped(this.tickCount);
                    break;
                default:
                    break;
            }
        }
        super.onSyncedDataUpdated(accessor);
    }

    public void stopAllAnimationStates() {
        this.idleAnimationState.stop();
        this.rollAnimationState.stop();
        this.deathAnimationState.stop();
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        this.deathTime = 0;
        this.setAttackState(3);
        this.stopAllAnimationStates();
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 60) {
            this.remove(Entity.RemovalReason.KILLED);
            this.gameEvent(GameEvent.ENTITY_DIE);
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.WANDERING_EYE_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.WANDERING_EYE_AMBIENT.get();
    }
}
