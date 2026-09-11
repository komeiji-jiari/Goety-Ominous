package com.qiuyue.goetyominous.common.entities.ally.ac;

import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class AtlatitanServantMeleeGoal extends Goal {
    private final AtlatitanServant atlatitan;

    public AtlatitanServantMeleeGoal(AtlatitanServant atlatitan) {
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.atlatitan = atlatitan;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.atlatitan.getTarget();
        return target != null && target.isAlive()
                && !this.atlatitan.isStaying() && !this.atlatitan.isCommanded()
                && this.atlatitan.getControllingPassenger() == null && !this.atlatitan.isImmobile();
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && this.atlatitan.getTarget() != null
                && !this.atlatitan.isStaying() && !this.atlatitan.isCommanded()
                && this.atlatitan.getControllingPassenger() == null && !this.atlatitan.isImmobile();
    }

    @Override
    public void stop() {
        this.atlatitan.turningFast = false;
    }

    @Override
    public void tick() {
        LivingEntity target = this.atlatitan.getTarget();
        if (target == null || !target.isAlive()) {
            return;
        }
        double distance = this.atlatitan.distanceTo(target);
        double attackDistance = this.atlatitan.getBbWidth() + target.getBbWidth();
        if (this.atlatitan.getAnimation() == AtlatitanServant.ANIMATION_LEFT_KICK
                || this.atlatitan.getAnimation() == AtlatitanServant.ANIMATION_RIGHT_KICK
                || this.atlatitan.getAnimation() == AtlatitanServant.ANIMATION_LEFT_WHIP
                || this.atlatitan.getAnimation() == AtlatitanServant.ANIMATION_RIGHT_WHIP) {
            this.atlatitan.turningFast = true;
            Vec3 vec3 = target.position().subtract(this.atlatitan.position());
            this.atlatitan.yBodyRotO = this.atlatitan.yBodyRot = Mth.approachDegrees(this.atlatitan.yBodyRot,
                    -((float) Mth.atan2(vec3.x, vec3.z)) * 57.295776F, 15.0F);
            this.atlatitan.getLookControl().setLookAt(target.getX(), target.getEyeY(), target.getZ());
        } else {
            this.atlatitan.turningFast = false;
        }
        if (distance > attackDistance) {
            this.atlatitan.getNavigation().moveTo(target, 1.0D);
        }
        if (this.atlatitan.getAnimation() == IAnimatedEntity.NO_ANIMATION && distance < attackDistance + 4.0D) {
            float random = this.atlatitan.getRandom().nextFloat();
            if (random < 0.5F && distance < attackDistance + 1.0D) {
                this.atlatitan.playSound(ACSoundRegistry.ATLATITAN_KICK.get(), 3.0F, this.atlatitan.getVoicePitch());
                this.atlatitan.setAnimation(this.atlatitan.getRandom().nextBoolean()
                        ? AtlatitanServant.ANIMATION_LEFT_KICK : AtlatitanServant.ANIMATION_RIGHT_KICK);
            } else {
                this.atlatitan.playSound(ACSoundRegistry.ATLATITAN_TAIL.get(), 3.0F, this.atlatitan.getVoicePitch());
                this.atlatitan.setAnimation(this.atlatitan.getRandom().nextBoolean()
                        ? AtlatitanServant.ANIMATION_RIGHT_WHIP : AtlatitanServant.ANIMATION_LEFT_WHIP);
            }
        }
    }
}
