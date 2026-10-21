package com.qiuyue.goetyominous.common.entities.ai.pp;

import com.qiuyue.goetyominous.common.entities.ally.pp.PiglinTravelerServant;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class TravelerKickGoal extends Goal {

    private final PiglinTravelerServant traveler;
    private LivingEntity target;

    public TravelerKickGoal(PiglinTravelerServant traveler) {
        this.traveler = traveler;
    }

    @Override
    public boolean canUse() {
        this.target = this.traveler.getTarget();
        return this.target != null && this.target.isAlive()
                && this.traveler.getKickCoolDown() <= 0
                && this.traveler.distanceToSqr(this.target) <= 4.0D
                && this.traveler.hasLineOfSight(this.target);
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }

    @Override
    public void start() {
        if (this.target == null) {
            return;
        }
        this.traveler.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
        if (this.traveler.doHurtTarget(this.target)) {
            this.traveler.setKickTicks(10);
            this.target.knockback(1.0D,
                    Mth.sin(this.traveler.getYRot() * ((float) Math.PI / 180.0F)),
                    -Mth.cos(this.traveler.getYRot() * ((float) Math.PI / 180.0F)));
            this.traveler.lookAt(this.target, 90.0F, 90.0F);
        }
        this.traveler.setKickCoolDown(100);
        this.traveler.setYHeadRot(Mth.lerp(0.0F, this.traveler.getYRot(), this.traveler.getYHeadRot()));
    }

    @Override
    public void stop() {
        this.target = null;
    }
}
