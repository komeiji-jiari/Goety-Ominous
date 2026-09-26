package com.qiuyue.goetyominous.common.entities.ally.lm.goals;

import com.qiuyue.goetyominous.common.entities.ally.lm.PossessedPaladinServant;

public class PossessedPaladinDoubleSlashSlamAttackGoal extends PossessedPaladinAttackGoal {

    public PossessedPaladinDoubleSlashSlamAttackGoal(PossessedPaladinServant entity, int getattackstate,
                                                     int attackstate, int attackendstate, int attackMaxtick,
                                                     int attackseetick, float attackrange,
                                                     boolean canAllertedState, double allertedStateChance) {
        super(entity, getattackstate, attackstate, attackendstate, attackMaxtick, attackseetick,
                attackrange, canAllertedState, allertedStateChance);
    }

    @Override
    public void start() {
        this.entity.shouldAttackMore = false;
        super.start();
    }

    @Override
    public void stop() {
        this.entity.randomizeAttacks();
        this.entity.setAttackState(
                (!this.entity.targetIsNotNull()
                        || this.entity.getPhase() < 2
                        || !(this.entity.distanceTo(this.entity.target()) >= 6.0F))
                        && (!this.entity.shouldAttackMore || this.entity.getPhase() < 2)
                        ? 7 : 8);
        this.entity.attackCooldown = 0;
    }
}
