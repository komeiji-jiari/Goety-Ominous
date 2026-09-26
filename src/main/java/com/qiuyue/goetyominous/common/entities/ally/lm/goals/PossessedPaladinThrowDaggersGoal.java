package com.qiuyue.goetyominous.common.entities.ally.lm.goals;

import com.qiuyue.goetyominous.common.entities.ally.lm.PossessedPaladinServant;
import net.minecraft.world.entity.LivingEntity;

public class PossessedPaladinThrowDaggersGoal extends PossessedPaladinAttackGoal {

    public PossessedPaladinThrowDaggersGoal(PossessedPaladinServant entity, int getattackstate,
                                            int attackstate, int attackendstate, int attackMaxtick,
                                            int attackseetick, float attackrange,
                                            boolean canAllertedState, double allertedStateChance) {
        super(entity, getattackstate, attackstate, attackendstate, attackMaxtick, attackseetick,
                attackrange, canAllertedState, allertedStateChance);
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.entity.getTarget();
        return super.canUse()
                && this.entity.distanceTo(target) > 7.0F;
    }
}
