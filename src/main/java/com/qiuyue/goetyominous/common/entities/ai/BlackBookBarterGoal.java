package com.qiuyue.goetyominous.common.entities.ai;

import com.qiuyue.goetyominous.common.events.BlackBookBarterEvents;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

public class BlackBookBarterGoal extends Goal {
    private final Mob mob;

    public BlackBookBarterGoal(Mob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        return this.mob.getPersistentData().getInt(BlackBookBarterEvents.BARTER_TIMER) > 0;
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (this.mob.getPersistentData().hasUUID(BlackBookBarterEvents.BARTER_TRADER)) {
            Player trader = this.mob.level().getPlayerByUUID(
                    this.mob.getPersistentData().getUUID(BlackBookBarterEvents.BARTER_TRADER));
            if (trader != null) {
                this.mob.getLookControl().setLookAt(trader, 30.0F, 30.0F);
            }
        }
    }
}
