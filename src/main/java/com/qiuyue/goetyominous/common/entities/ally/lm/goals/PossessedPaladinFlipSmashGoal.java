package com.qiuyue.goetyominous.common.entities.ally.lm.goals;

import com.qiuyue.goetyominous.common.entities.ally.lm.PossessedPaladinServant;
import net.minecraft.world.entity.LivingEntity;

public class PossessedPaladinFlipSmashGoal extends PossessedPaladinAttackGoal {

    public PossessedPaladinFlipSmashGoal(PossessedPaladinServant entity, int getattackstate,
                                         int attackstate, int attackendstate, int attackMaxtick,
                                         int attackseetick, float attackrange,
                                         boolean canAllertedState, double allertedStateChance) {
        super(entity, getattackstate, attackstate, attackendstate, attackMaxtick, attackseetick,
                attackrange, canAllertedState, allertedStateChance);
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.entity.getTarget();
        return target != null
                && super.canUse()
                && this.entity.distanceTo(target) > 5.0F;
    }

    @Override
    public void stop() {
        this.entity.randomizeAttacks();
        this.entity.setAttackState(
                this.entity.getRandom().nextInt() * 100 >= 50 && !this.entity.shouldAttackMore ? 13 : 14);
        this.entity.attackCooldown = 0;
    }
}
