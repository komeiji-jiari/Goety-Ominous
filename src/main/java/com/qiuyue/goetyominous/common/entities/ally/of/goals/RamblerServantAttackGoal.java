package com.qiuyue.goetyominous.common.entities.ally.of.goals;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public abstract class RamblerServantAttackGoal extends Goal {
    protected int timer;
    protected final Mob monster;

    public RamblerServantAttackGoal(Mob monster) {
        this.monster = monster;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.monster.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.monster.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void start() {
        this.timer = 0;
    }

    @Override
    public void stop() {
        this.timer = 0;
    }

    protected double getAttackReachSqr(LivingEntity target) {
        return (double)(this.monster.getBbWidth() * 2.0F * this.monster.getBbWidth() * 2.0F + target.getBbWidth());
    }

    protected boolean isInAttackRange(LivingEntity target, double range) {
        return this.monster.hasLineOfSight(target)
                && this.monster.distanceTo(target) <= this.monster.getBbWidth() + target.getBbWidth() + range;
    }
}
