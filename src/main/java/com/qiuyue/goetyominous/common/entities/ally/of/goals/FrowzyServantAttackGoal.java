package com.qiuyue.goetyominous.common.entities.ally.of.goals;

import com.qiuyue.goetyominous.common.entities.ally.of.FrowzyServant;
import com.unusualmodding.opposing_force.entity.utils.OPPoses;
import com.unusualmodding.opposing_force.registry.OPSoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;

public class FrowzyServantAttackGoal extends RamblerServantAttackGoal {
    private final FrowzyServant frowzy;

    public FrowzyServantAttackGoal(FrowzyServant frowzy) {
        super(frowzy);
        this.frowzy = frowzy;
    }

    @Override
    public boolean canUse() {
        return !this.frowzy.isCommanded() && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return !this.frowzy.isCommanded() && super.canContinueToUse();
    }

    @Override
    public void start() {
        super.start();
        this.frowzy.setPose(Pose.STANDING);
    }

    @Override
    public void stop() {
        super.stop();
        this.frowzy.setPose(Pose.STANDING);
    }

    @Override
    public void tick() {
        LivingEntity target = this.frowzy.getTarget();
        if (target == null) {
            return;
        }
        this.frowzy.getLookControl().setLookAt(target.getX(), target.getEyeY(), target.getZ());
        double distance = this.frowzy.distanceToSqr(target);
        int attackState = this.frowzy.getAttackState();
        if (distance > this.getAttackReachSqr(target)) {
            this.frowzy.getNavigation().moveTo(target, 1.0D);
        }
        if (attackState == 1) {
            ++this.timer;
            if (this.timer == 1) {
                this.frowzy.setPose(OPPoses.ATTACKING.get());
            }
            if (this.timer == 8) {
                this.frowzy.playSound(OPSoundEvents.FROWZY_ATTACK.get(), 1.0F, this.frowzy.getVoicePitch());
            }
            if (this.timer == 10 && this.frowzy.distanceToSqr(target) < this.getAttackReachSqr(target)) {
                this.frowzy.doHurtTarget(target);
                this.frowzy.swing(InteractionHand.MAIN_HAND);
            }
            if (this.timer > 20) {
                this.timer = 0;
                this.frowzy.setAttackState(0);
            }
        } else if (distance < this.getAttackReachSqr(target)) {
            this.frowzy.setAttackState(1);
        }
    }
}
