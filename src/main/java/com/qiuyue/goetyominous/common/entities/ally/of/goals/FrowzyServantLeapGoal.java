package com.qiuyue.goetyominous.common.entities.ally.of.goals;

import com.qiuyue.goetyominous.common.entities.ally.of.FrowzyServant;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class FrowzyServantLeapGoal extends Goal {
    private final FrowzyServant frowzy;

    public FrowzyServantLeapGoal(FrowzyServant frowzy) {
        this.frowzy = frowzy;
        this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.frowzy.getTarget();
        return !this.frowzy.isCommanded() && target != null && target.isAlive() && this.frowzy.jumpCooldown <= 0;
    }

    @Override
    public boolean canContinueToUse() {
        return !this.frowzy.onGround();
    }

    @Override
    public void start() {
        LivingEntity target = this.frowzy.getTarget();
        if (target != null && this.frowzy.onGround()) {
            this.frowzy.getLookControl().setLookAt(target.getX(), target.getEyeY(), target.getZ());
            double distance = this.frowzy.distanceToSqr(target.getX(), target.getY(), target.getZ());
            if (distance > this.getAttackReachSqr(target) && this.frowzy.jumpCooldown <= 0) {
                this.frowzy.getNavigation().stop();
                Vec3 vec3 = this.frowzy.getDeltaMovement();
                Vec3 leapVec = new Vec3(target.getX() - this.frowzy.getX(), 0.0D, target.getZ() - this.frowzy.getZ());
                if (leapVec.lengthSqr() > 1.0E-7D) {
                    leapVec = leapVec.normalize().scale(1.0D).add(vec3.scale(0.75D));
                }
                this.frowzy.setDeltaMovement(leapVec.x, 0.4D, leapVec.z);
            }
        }
    }

    @Override
    public void stop() {
        this.frowzy.jumpCooldown = 100 + this.frowzy.getRandom().nextInt(80);
    }

    protected double getAttackReachSqr(LivingEntity target) {
        return this.frowzy.getBbWidth() * 4.0F * this.frowzy.getBbWidth() * 4.0F + target.getBbWidth();
    }
}
