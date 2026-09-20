package com.qiuyue.goetyominous.common.entities.ally.of.goals;

import com.qiuyue.goetyominous.common.entities.ally.of.VoltServant;
import com.unusualmodding.opposing_force.registry.OPSoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class VoltServantLeapGoal extends Goal {

    private final VoltServant volt;

    public VoltServantLeapGoal(VoltServant volt) {
        this.volt = volt;
        this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.volt.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }
        if (this.volt.leapCooldown > 0) {
            return false;
        }
        if (!this.volt.onGround() || this.volt.isInWater()) {
            return false;
        }
        return this.volt.distanceTo(target) < 30.0D;
    }

    @Override
    public boolean canContinueToUse() {
        return !this.volt.onGround() && !this.volt.isInWater();
    }

    @Override
    public void start() {
        LivingEntity target = this.volt.getTarget();
        if (target != null) {
            float f1 = -1.0F;
            float f2 = (float) Math.toRadians((f1 + 90.0F) + this.volt.getRandom().nextFloat() * 150.0F - 75.0F);
            float f3 = 1.5F;
            this.volt.playSound(OPSoundEvents.VOLT_SQUISH.get(), 0.2F, 1.0F);
            Vec3 vec3 = this.volt.getDeltaMovement()
                    .add((double) f3 * Math.cos((double) f2), 0.0D, (double) f3 * Math.sin((double) f2));
            this.volt.setPose(Pose.LONG_JUMPING);
            this.volt.setDeltaMovement(vec3.x, 0.9D, vec3.z);
            this.volt.getNavigation().stop();
            this.volt.leapCooldown = 40 + this.volt.getRandom().nextInt(20);
        }
    }

    @Override
    public void tick() {
        LivingEntity target = this.volt.getTarget();
        if (target != null) {
            this.volt.getLookControl().setLookAt(target, 30.0F, 30.0F);
        }
    }
}
