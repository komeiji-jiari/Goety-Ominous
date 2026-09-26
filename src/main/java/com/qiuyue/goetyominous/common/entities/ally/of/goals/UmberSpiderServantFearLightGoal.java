package com.qiuyue.goetyominous.common.entities.ally.of.goals;

import com.qiuyue.goetyominous.common.entities.ally.of.UmberSpiderServant;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * 怕光逃跑目标：复刻 OF 原版 UmberSpiderFearLightGoal。
 * 非精英、没被骑乘时，如果身边块亮度超过阈值或着火了（且记录过光的位置），
 * 就锁定逃跑：设 fleeLightFor=50 倒计时、关掉攻击状态，并朝远离光的方向随机找位置快跑(1.6 倍速)。
 */
public class UmberSpiderServantFearLightGoal extends Goal {
    private final UmberSpiderServant umberSpider;

    public UmberSpiderServantFearLightGoal(UmberSpiderServant umberSpider) {
        this.umberSpider = umberSpider;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return !this.umberSpider.isElite()
                && !this.umberSpider.isVehicle()
                && (this.umberSpider.level().getBrightness(LightLayer.BLOCK, this.umberSpider.blockPosition()) > this.umberSpider.getLightThreshold()
                    || this.umberSpider.isOnFire())
                && this.umberSpider.fleeFromPosition != null;
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public void stop() {
        this.umberSpider.fleeFromPosition = null;
        this.umberSpider.fleeLightFor = 50;
    }

    @Override
    public void tick() {
        this.umberSpider.fleeLightFor = 50;
        this.umberSpider.setAttacking(false);
        if (this.umberSpider.getNavigation().isDone()) {
            Vec3 pos = LandRandomPos.getPosAway(this.umberSpider, 10, 7, this.umberSpider.fleeFromPosition);
            if (pos != null) {
                this.umberSpider.getNavigation().moveTo(pos.x, pos.y, pos.z, 1.6D);
            }
        }
    }
}
