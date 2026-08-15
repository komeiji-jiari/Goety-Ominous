package com.qiuyue.goetyominous.common.entities.ally.of.goals;

import com.qiuyue.goetyominous.common.entities.ally.of.RamblerServant;
import com.unusualmodding.opposing_force.entity.utils.OPPoses;
import com.unusualmodding.opposing_force.registry.OPSoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class RamblerServantFlailGoal extends RamblerServantAttackGoal {
    private final RamblerServant rambler;

    public RamblerServantFlailGoal(RamblerServant rambler) {
        super(rambler);
        this.rambler = rambler;
    }

    public boolean canUse() {
        return super.canUse() && this.rambler.flailCooldown == 0 && this.rambler.getPose() == Pose.STANDING;
    }

    public boolean canContinueToUse() {
        return super.canContinueToUse() && this.rambler.flailCooldown == 0;
    }

    public void start() {
        super.start();
        this.rambler.setFlailing(false);
        this.rambler.setPose(Pose.STANDING);
    }

    public void stop() {
        super.stop();
        this.rambler.setFlailing(false);
        this.rambler.setPose(Pose.STANDING);
        this.rambler.flailCooldown = 200 + this.rambler.getRandom().nextInt(200);
    }

    public void tick() {
        LivingEntity target = this.rambler.getTarget();
        if (target != null) {
            this.rambler.lookAt(target, 30.0F, 30.0F);
            this.rambler.getLookControl().setLookAt(target, 30.0F, 30.0F);
            double distance = this.rambler.distanceToSqr(target.getX(), target.getY(), target.getZ());
            if (this.rambler.isFlailing()) {
                ++this.timer;
                this.rambler.getNavigation().moveTo(target, 2.0);
                if (this.timer == 1) {
                    this.rambler.setPose(OPPoses.START_FLAILING.get());
                }
                if (this.timer > 1 && this.timer < 20 && this.rambler.tickCount % 8 == 0) {
                    this.rambler.playSound(OPSoundEvents.RAMBLER_ATTACK.get(), 1.0F, 1.0F / (this.rambler.getRandom().nextFloat() * 0.4F + 0.8F));
                }
                if (this.timer > 20 && this.timer < 120) {
                    this.hurtNearbyEntities();
                    if (this.rambler.tickCount % 4 == 0) {
                        this.rambler.playSound(OPSoundEvents.RAMBLER_ATTACK.get(), 1.0F, 1.0F / (this.rambler.getRandom().nextFloat() * 0.4F + 0.8F));
                    }
                }
                if (this.timer == 120) {
                    this.rambler.setPose(OPPoses.STOP_FLAILING.get());
                }
                if (this.timer > 120 && this.timer < 215) {
                    this.rambler.getNavigation().stop();
                }
                if (this.timer > 215) {
                    this.timer = 0;
                    this.rambler.flailCooldown = 200 + this.rambler.getRandom().nextInt(200);
                    this.rambler.setFlailing(false);
                }
            } else if (this.rambler.flailCooldown == 0) {
                this.rambler.getNavigation().moveTo(target, 1.25);
                if (this.isInAttackRange(target, 3.0)) {
                    this.rambler.setFlailing(true);
                }
            } else {
                this.rambler.getNavigation().stop();
                this.rambler.setPose(Pose.STANDING);
            }
        }
    }

    private void hurtNearbyEntities() {
        List<LivingEntity> nearbyEntities = this.rambler.level().getNearbyEntities(
                LivingEntity.class, TargetingConditions.forCombat(), this.rambler, this.rambler.getBoundingBox().inflate(1.25));
        if (!nearbyEntities.isEmpty()) {
            LivingEntity entity = nearbyEntities.get(0);
            if (!(entity instanceof RamblerServant)) {
                entity.hurt(entity.damageSources().mobAttack(this.rambler), (float)this.rambler.getAttributeValue(Attributes.ATTACK_DAMAGE));
                entity.knockback((double)((float)this.rambler.getAttribute(Attributes.ATTACK_KNOCKBACK).getValue()),
                        this.rambler.position().x - entity.getX(), this.rambler.position().z - entity.getZ());
                if (entity.isDamageSourceBlocked(this.rambler.damageSources().mobAttack(this.rambler)) && entity instanceof Player) {
                    ((Player) entity).disableShield(true);
                }
                this.rambler.swing(InteractionHand.MAIN_HAND);
            }
        }
    }

    protected double getAttackReachSqr(LivingEntity target) {
        return (double)(this.monster.getBbWidth() * 2.5F * this.monster.getBbWidth() * 2.5F + target.getBbWidth());
    }
}
