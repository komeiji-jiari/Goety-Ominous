package com.qiuyue.goetyominous.common.entities.ally.of.goals;

import com.qiuyue.goetyominous.common.entities.ally.of.UmberSpiderServant;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.level.LightLayer;

public class UmberSpiderServantRandomStrollGoal extends WaterAvoidingRandomStrollGoal {
    private final UmberSpiderServant umberSpider;

    public UmberSpiderServantRandomStrollGoal(UmberSpiderServant umberSpider) {
        super(umberSpider, 1.0D, 0.001F);
        this.umberSpider = umberSpider;
    }

    @Override
    public boolean canUse() {
        return !this.umberSpider.isVehicle()
                && this.canStroll()
                && super.canUse();
    }

    private boolean canStroll() {
        return this.umberSpider.isElite()
                || this.umberSpider.level().getBrightness(LightLayer.BLOCK, this.umberSpider.blockPosition()) <= this.umberSpider.getLightThreshold();
    }
}
