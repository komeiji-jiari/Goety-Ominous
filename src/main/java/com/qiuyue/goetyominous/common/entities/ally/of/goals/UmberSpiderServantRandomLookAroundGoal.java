package com.qiuyue.goetyominous.common.entities.ally.of.goals;

import com.qiuyue.goetyominous.common.entities.ally.of.UmberSpiderServant;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.level.LightLayer;

import java.util.EnumSet;

public class UmberSpiderServantRandomLookAroundGoal extends RandomLookAroundGoal {
    private final UmberSpiderServant umberSpider;

    public UmberSpiderServantRandomLookAroundGoal(UmberSpiderServant umberSpider) {
        super(umberSpider);
        this.umberSpider = umberSpider;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return !this.umberSpider.isVehicle()
                && this.canLookAround()
                && super.canUse();
    }

    private boolean canLookAround() {
        return this.umberSpider.isElite()
                || this.umberSpider.level().getBrightness(LightLayer.BLOCK, this.umberSpider.blockPosition()) <= this.umberSpider.getLightThreshold();
    }
}
