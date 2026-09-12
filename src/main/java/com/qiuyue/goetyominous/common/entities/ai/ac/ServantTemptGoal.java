package com.qiuyue.goetyominous.common.entities.ai.ac;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.item.crafting.Ingredient;

public class ServantTemptGoal extends TemptGoal {
    private final Summoned summoned;

    public ServantTemptGoal(Summoned summoned, double speedModifier, Ingredient ingredient, boolean canScare) {
        super(summoned, speedModifier, ingredient, canScare);
        this.summoned = summoned;
    }

    @Override
    public boolean canUse() {
        return this.isIdle() && super.canUse() && this.player == this.summoned.getTrueOwner();
    }

    private boolean isIdle() {
        return !this.summoned.isStaying() && !this.summoned.isCommanded()
                && this.summoned.getControllingPassenger() == null;
    }
}
