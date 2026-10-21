package com.qiuyue.goetyominous.common.entities.ai.pp;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;

public class LookAtPlayerWhenIdleGoal extends LookAtPlayerGoal {

    public LookAtPlayerWhenIdleGoal(Mob mob, Class<? extends LivingEntity> lookAtType, float lookDistance) {
        super(mob, lookAtType, lookDistance);
    }

    public LookAtPlayerWhenIdleGoal(Mob mob, Class<? extends LivingEntity> lookAtType, float lookDistance, float probability) {
        super(mob, lookAtType, lookDistance, probability);
    }

    @Override
    public boolean canUse() {
        return this.mob.getTarget() == null && super.canUse();
    }
}
