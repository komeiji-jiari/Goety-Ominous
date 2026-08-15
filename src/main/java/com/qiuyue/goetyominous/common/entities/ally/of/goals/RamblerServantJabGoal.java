package com.qiuyue.goetyominous.common.entities.ally.of.goals;

import com.qiuyue.goetyominous.common.entities.ally.of.RamblerServant;
import com.unusualmodding.opposing_force.entity.utils.OPPoses;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class RamblerServantJabGoal extends RamblerServantAttackGoal {
    private final RamblerServant rambler;

    public RamblerServantJabGoal(RamblerServant rambler) {
        super(rambler);
        this.rambler = rambler;
    }

    public boolean canUse() {
        return super.canUse() && this.rambler.flailCooldown > 0 && this.rambler.getPose() == Pose.STANDING;
    }

    public boolean canContinueToUse() {
        return super.canContinueToUse() && this.rambler.flailCooldown > 0;
    }

    public void start() {
        super.start();
        this.rambler.setPose(Pose.STANDING);
    }

    public void stop() {
        super.stop();
        this.rambler.setPose(Pose.STANDING);
    }

    public void tick() {
        LivingEntity target = this.rambler.getTarget();
        if (target != null) {
            int attackState = this.rambler.getAttackState();
            double distance = this.rambler.distanceToSqr(target.getX(), target.getY(), target.getZ());
            this.rambler.getNavigation().moveTo(target, 1.4);
            this.rambler.lookAt(target, 30.0F, 30.0F);
            this.rambler.getLookControl().setLookAt(target, 30.0F, 30.0F);
            if (this.rambler.getAttackState() == 1) {
                ++this.timer;
                if (this.timer == 1) {
                    this.rambler.setPose(OPPoses.JAB.get());
                }
                if (this.timer == 7 && this.isInAttackRange(target, 2.3)) {
                    target.hurt(this.rambler.damageSources().mobAttack(this.rambler), (float)this.rambler.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.5F);
                    target.knockback(this.rambler.getAttribute(Attributes.ATTACK_KNOCKBACK).getValue() * 0.5, (double)Mth.sin(this.rambler.getYRot() * 0.017453292F), (double)(-Mth.cos(this.rambler.getYRot() * 0.017453292F)));
                    this.rambler.setDeltaMovement(this.rambler.getDeltaMovement().multiply(0.6, 1.0, 0.6));
                    this.rambler.swing(InteractionHand.MAIN_HAND);
                }
                if (this.timer == 20) {
                    this.timer = 0;
                    this.rambler.setAttackState(0);
                }
            } else if (this.rambler.getAttackState() == 2) {
                ++this.timer;
                this.rambler.getNavigation().stop();
                if (this.timer == 1) {
                    this.rambler.setPose(OPPoses.JAB_RUSH.get());
                }
                if (this.timer == 10 && this.isInAttackRange(target, 2.5)) {
                    this.rambler.doHurtTarget(target);
                    this.rambler.swing(InteractionHand.MAIN_HAND);
                }
                if (this.timer == 30) {
                    this.timer = 0;
                    this.rambler.setAttackState(0);
                }
            } else if (distance <= this.getAttackReachSqr(target) && attackState == 0) {
                if (this.rambler.getRandom().nextFloat() < 0.33F) {
                    this.rambler.setAttackState(2);
                } else {
                    this.rambler.setAttackState(1);
                }
            }
        }
    }

    protected double getAttackReachSqr(LivingEntity target) {
        return (double)(this.monster.getBbWidth() * 1.5F * this.monster.getBbWidth() * 1.5F + target.getBbWidth());
    }
}