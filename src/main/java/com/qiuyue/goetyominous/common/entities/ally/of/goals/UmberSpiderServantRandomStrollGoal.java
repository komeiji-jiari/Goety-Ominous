package com.qiuyue.goetyominous.common.entities.ally.of.goals;

import com.qiuyue.goetyominous.common.entities.ally.of.UmberSpiderServant;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.level.LightLayer;

/**
 * 怕光闲逛目标：复刻 OF 原版 UmberSpiderRandomStrollGoal。
 * 非精英只在脚下够暗时才肯乱逛（和怕光主题一致），精英随时乱逛；
 * 被骑乘时不会乱逛。
 */
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
