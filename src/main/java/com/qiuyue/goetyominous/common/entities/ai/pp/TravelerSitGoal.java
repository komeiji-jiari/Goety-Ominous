package com.qiuyue.goetyominous.common.entities.ai.pp;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.qiuyue.goetyominous.common.entities.ally.pp.TravelerAnimatable;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class TravelerSitGoal<T extends Summoned & TravelerAnimatable> extends Goal {

    private static final int MAX_SIT_TICKS = 1200;
    private final T traveler;
    private int sitTicks;

    public TravelerSitGoal(T traveler) {
        this.traveler = traveler;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        return this.traveler.getNoActionTime() >= 120
                && this.traveler.isStaying()
                && !this.traveler.isAggressive()
                && this.traveler.getNavigation().isDone()
                && !this.traveler.isSitting();
    }

    @Override
    public boolean canContinueToUse() {
        return this.sitTicks <= MAX_SIT_TICKS
                && this.traveler.isSitting()
                && this.traveler.isStaying()
                && !this.traveler.isAggressive()
                && this.traveler.getNavigation().isDone();
    }

    @Override
    public void start() {
        this.sitTicks = 0;
        this.traveler.sit(true);
        this.traveler.getNavigation().stop();
    }

    @Override
    public void tick() {
        ++this.sitTicks;
        this.traveler.getNavigation().stop();
        this.traveler.setTarget(null);
    }

    @Override
    public void stop() {
        this.traveler.sit(false);
        this.sitTicks = 0;
    }
}
