package com.qiuyue.goetyominous.common.entities.ally.lm.goals.obliterator;

import com.qiuyue.goetyominous.common.entities.ally.lm.TheObliteratorServant;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IStateGoal;

public class TheObliteratorCrushGrabStateGoal extends IStateGoal {
    protected final TheObliteratorServant entity;

    public TheObliteratorCrushGrabStateGoal(TheObliteratorServant entity, int getattackstate, int attackstate,
                                            int attackendstate, int attackMaxtick, int attackseetick) {
        super(entity, getattackstate, attackstate, attackendstate, attackMaxtick, attackseetick);
        this.entity = entity;
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() {
        if (this.entity.succedGrabbing) {
            if (!this.entity.level().isClientSide) {
                this.entity.setAttackState(this.entity.getAttackState() == 37 ? 40 : 50);
            }
            this.entity.succedGrabbing = false;
        } else {
            super.stop();
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return false;
    }
}
