package com.qiuyue.goetyominous.common.entities.ally.of.goals;

import com.unusualmodding.opposing_force.entity.utils.AttackState;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

/**
 * 共享攻击基类，逐字节复刻 OF 原版 AttackGoal：
 * 构造器设置 MOVE+LOOK 旗标；start 设 aggressive 并重置攻击状态；
 * stop 在目标是创造/观察者玩家时清目标、复位 aggressive、停导航、重置攻击状态；
 * canContinueToUse 带归属地检查，并对创造/观察者玩家在导航到位后停止追击。
 */
public abstract class RamblerServantAttackGoal extends Goal {
    protected int timer;
    protected final Mob monster;

    public RamblerServantAttackGoal(Mob monster) {
        this.timer = 0;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
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
        if (target == null) {
            return false;
        }
        if (!target.isAlive()) {
            return false;
        }
        if (!this.monster.isWithinRestriction(target.blockPosition())) {
            return false;
        }
        if (target instanceof Player player && (player.isSpectator() || player.isCreative())) {
            return !this.monster.getNavigation().isDone();
        }
        return true;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void start() {
        this.monster.setAggressive(true);
        this.timer = 0;
        if (this.monster instanceof AttackState attackState) {
            attackState.setAttackState(0);
        }
    }

    @Override
    public void stop() {
        LivingEntity target = this.monster.getTarget();
        if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target)) {
            this.monster.setTarget(null);
        }
        this.monster.setAggressive(false);
        this.monster.getNavigation().stop();
        if (this.monster instanceof AttackState attackState) {
            attackState.setAttackState(0);
        }
    }

    protected double getAttackReachSqr(LivingEntity target) {
        return (double)(this.monster.getBbWidth() * 2.0F * this.monster.getBbWidth() * 2.0F + target.getBbWidth());
    }

    protected boolean isInAttackRange(LivingEntity target, double range) {
        return this.monster.hasLineOfSight(target)
                && this.monster.distanceTo(target) <= this.monster.getBbWidth() + target.getBbWidth() + range;
    }
}
