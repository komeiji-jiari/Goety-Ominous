package com.qiuyue.goetyominous.common.entities.ally.lm;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;

public class ServantMoveControl extends MoveControl {

    private static final float MIN_TURN = 12.0F;

    public ServantMoveControl(Mob mob) {
        super(mob);
    }

    @Override
    protected float rotlerp(float sourceAngle, float targetAngle, float maxChange) {
        double speed = this.mob.getDeltaMovement().horizontalDistance();
        float radius = Math.max(0.25F, this.mob.getBbWidth() * 0.25F);
        float limit = Math.max(MIN_TURN, (float) Math.toDegrees(speed / (double) radius));
        return super.rotlerp(sourceAngle, targetAngle, Math.min(maxChange, limit));
    }
}
