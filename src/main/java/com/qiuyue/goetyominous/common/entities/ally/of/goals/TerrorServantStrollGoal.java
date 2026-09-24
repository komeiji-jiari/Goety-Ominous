package com.qiuyue.goetyominous.common.entities.ally.of.goals;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.qiuyue.goetyominous.common.entities.ally.of.TerrorServant;

public class TerrorServantStrollGoal extends Summoned.WanderGoal<TerrorServant> {
    private final TerrorServant terror;

    public TerrorServantStrollGoal(TerrorServant terror, double speedModifier, int interval, float probability) {
        super(terror, speedModifier, interval, probability);
        this.terror = terror;
    }

    @Override
    public boolean canUse() {
        return !this.terror.isInWater() && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return !this.terror.isInWater() && super.canContinueToUse();
    }
}
