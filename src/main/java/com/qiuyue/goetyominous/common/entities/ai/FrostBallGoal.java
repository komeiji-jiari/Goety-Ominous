package com.qiuyue.goetyominous.common.entities.ai;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;

public class FrostBallGoal extends RangedAttackGoal {
    private final RangedAttackMob ghost;
    private final float distance;

    public FrostBallGoal(RangedAttackMob mob, float distance) {
        super(mob, 1.0D, 20, 20, 20.0F);
        this.ghost = mob;
        this.distance = distance;
    }

    @Override
    public boolean canUse() {
        if (this.ghost instanceof Mob mob) {
            LivingEntity target = mob.getTarget();
            return super.canUse() && target != null
                    && mob.distanceToSqr(target) >= Mth.square(this.distance);
        }
        return false;
    }
}
