package com.qiuyue.goetyominous.common.entities.ally.lm.goals;

import com.qiuyue.goetyominous.common.entities.ally.lm.PossessedPaladinServant;

public class PossessedPaladinSlashFromGoal extends PossessedPaladinAttackGoal {

    public PossessedPaladinSlashFromGoal(PossessedPaladinServant entity, int getattackstate,
                                         int attackstate, int attackendstate, int attackMaxtick,
                                         int attackseetick, float attackrange,
                                         boolean canAllertedState, double allertedStateChance) {
        super(entity, getattackstate, attackstate, attackendstate, attackMaxtick, attackseetick,
                attackrange, canAllertedState, allertedStateChance);
    }

    @Override
    public void stop() {
        this.entity.randomizeAttacks();
        this.entity.setAttackState(
                this.entity.getRandom().nextInt() * 100 >= 50 && !this.entity.hasHurt
                        ? (this.entity.getRandom().nextInt() * 100 < 70 ? 18 : 17)
                        : (this.entity.stab_grab_cooldown <= 0 ? 19 : 18));
        this.entity.hasHurt = false;
        this.entity.attackCooldown = 0;
    }
}
