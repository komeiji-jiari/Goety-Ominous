package com.qiuyue.goetyominous.common.entities.ally.of.goals;

import com.qiuyue.goetyominous.common.entities.ally.of.UmberSpiderServant;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LightLayer;

/**
 * 撕咬攻击目标：复刻 OF 原版 UmberSpiderAttackGoal（继承 OF AttackGoal）。
 * 只有"不在怕光冷却中(fleeLightFor<=0)"且"目标站在暗处(非精英)"才肯攻击；
 * 精英无此限制。attackState==1 时走攻击计时器：第 4 tick 够得着就咬一口并挥主手，
 * 满 20 tick 收手；没进攻击态时一旦距离够近就切到攻击态。
 */
public class UmberSpiderServantAttackGoal extends RamblerServantAttackGoal {
    private final UmberSpiderServant umberSpider;

    public UmberSpiderServantAttackGoal(UmberSpiderServant umberSpider) {
        super(umberSpider);
        this.umberSpider = umberSpider;
    }

    @Override
    public boolean canUse() {
        return super.canUse() && this.umberSpider.fleeLightFor <= 0 && this.canAttack();
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && this.umberSpider.fleeLightFor <= 0 && this.canAttack();
    }

    private boolean canAttack() {
        if (this.umberSpider.isElite()) {
            return true;
        }
        LivingEntity target = this.umberSpider.getTarget();
        return target != null
                && target.level().getBrightness(LightLayer.BLOCK, target.blockPosition()) <= this.umberSpider.getLightThreshold()
                && !this.umberSpider.isOnFire();
    }

    @Override
    public void start() {
        super.start();
        this.umberSpider.setAttacking(true);
    }

    @Override
    public void stop() {
        super.stop();
        this.umberSpider.setAttacking(false);
    }

    @Override
    public void tick() {
        LivingEntity target = this.umberSpider.getTarget();
        if (target != null) {
            this.umberSpider.setAttacking(true);
            this.umberSpider.lookAt(target, 30.0F, 30.0F);
            this.umberSpider.getLookControl().setLookAt(target, 30.0F, 30.0F);
            double distanceSqr = this.umberSpider.distanceToSqr(target.getX(), target.getY(), target.getZ());
            int attackState = this.umberSpider.getAttackState();
            this.umberSpider.getNavigation().moveTo(target, 1.2D);
            if (attackState == 1) {
                ++this.timer;
                if (this.timer == 4 && this.umberSpider.distanceTo(target) < this.getAttackReachSqr(target)) {
                    this.umberSpider.doHurtTarget(target);
                    this.umberSpider.swing(InteractionHand.MAIN_HAND);
                }
                if (this.timer >= 20) {
                    this.timer = 0;
                    this.umberSpider.setAttackState(0);
                }
            } else if (distanceSqr <= this.getAttackReachSqr(target)) {
                this.umberSpider.setAttackState(1);
            }
        }
    }
}
