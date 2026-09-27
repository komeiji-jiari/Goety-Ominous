package com.qiuyue.goetyominous.common.entities.ally.lm.goals.obliterator;

import com.qiuyue.goetyominous.common.entities.ally.lm.TheObliteratorServant;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IAttackGoal;

public class TeleportGrabGoal extends IAttackGoal {
    protected final TheObliteratorServant entity;

    public TeleportGrabGoal(TheObliteratorServant entity, int getattackstate, int attackstate, int attackendstate,
                            int attackMaxtick, int attackseetick, float attackrange) {
        super(entity, getattackstate, attackstate, attackendstate, attackMaxtick, attackseetick, attackrange);
        this.entity = entity;
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void stop() {
        if (this.entity.succedGrabbing) {
            if (!this.entity.level().isClientSide) {
                this.entity.setAttackState(18);
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
