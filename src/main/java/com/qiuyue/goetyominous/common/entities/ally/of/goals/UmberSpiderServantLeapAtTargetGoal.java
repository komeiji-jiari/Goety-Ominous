package com.qiuyue.goetyominous.common.entities.ally.of.goals;

import com.qiuyue.goetyominous.common.entities.ally.of.UmberSpiderServant;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * 跳扑目标：复刻 OF 原版 UmberSpiderLeapAtTargetGoal（原版蜘蛛 LeapAtTargetGoal 的改版）。
 * 距离 4~16 格、在地面、且每 5 tick 摇一次奖才触发；目标是跳起来扑向目标脸。
 * 非精英只在"目标站在暗处"时才会扑（和怕光主题呼应），精英任何时候都会扑。
 * 扑出去后只要还在空中、脚下还够暗、攻击状态还挂着，就保持本目标。
 */
public class UmberSpiderServantLeapAtTargetGoal extends Goal {
    private final UmberSpiderServant umberSpider;
    private LivingEntity target;

    public UmberSpiderServantLeapAtTargetGoal(UmberSpiderServant umberSpider) {
        this.umberSpider = umberSpider;
        this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.umberSpider.getTarget();
        if (target == null) {
            return false;
        }
        this.target = target;
        if (!this.canLeap(target)) {
            return false;
        }
        double distance = this.umberSpider.distanceToSqr(target);
        if (distance < 4.0D || distance > 16.0D) {
            return false;
        }
        if (!this.umberSpider.onGround()) {
            return false;
        }
        return this.umberSpider.getRandom().nextInt(reducedTickDelay(5)) == 0;
    }

    private boolean canLeap(LivingEntity target) {
        if (this.umberSpider.isElite()) {
            return !this.umberSpider.isVehicle();
        } else {
            return target.level().getBrightness(LightLayer.BLOCK, target.blockPosition()) <= this.umberSpider.getLightThreshold()
                    && !this.umberSpider.isOnFire()
                    && !this.umberSpider.isVehicle();
        }
    }

    @Override
    public boolean canContinueToUse() {
        return !this.umberSpider.onGround()
                && this.umberSpider.level().getBrightness(LightLayer.BLOCK, this.umberSpider.blockPosition()) < this.umberSpider.getLightThreshold()
                && this.umberSpider.isAttacking();
    }

    @Override
    public void start() {
        Vec3 current = this.umberSpider.getDeltaMovement();
        Vec3 direction = new Vec3(this.target.getX() - this.umberSpider.getX(), 0.0D, this.target.getZ() - this.umberSpider.getZ());
        if (direction.lengthSqr() > 1.0E-7D) {
            direction = direction.normalize().scale(0.5D).add(current.scale(0.2D));
        }
        this.umberSpider.setDeltaMovement(direction.x, 0.4D, direction.z);
    }
}
