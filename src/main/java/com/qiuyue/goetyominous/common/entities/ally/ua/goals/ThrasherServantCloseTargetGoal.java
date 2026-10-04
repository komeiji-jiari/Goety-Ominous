package com.qiuyue.goetyominous.common.entities.ally.ua.goals;

import com.qiuyue.goetyominous.common.entities.ally.ua.ThrasherServant;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public class ThrasherServantCloseTargetGoal extends NearestAttackableTargetGoal<LivingEntity> {
    public ThrasherServantCloseTargetGoal(ThrasherServant thrasher) {
        super(thrasher, LivingEntity.class, 5, true, false, thrasher::isValidTarget);
        this.targetConditions = TargetingConditions.forCombat().range(ThrasherServant.BITE_RANGE).selector(thrasher::isValidTarget);
    }
}
