package com.qiuyue.goetyominous.common.entities.ally.lm.goals;

import com.qiuyue.goetyominous.common.entities.ally.lm.IAnimatedMonsterServant;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class IMoveGoal extends Goal {
    private final IAnimatedMonsterServant monster;
    private final boolean followingTargetEvenIfNotSeen;
    private final double moveSpeed;

    public IMoveGoal(IAnimatedMonsterServant monster, boolean followingTargetEvenIfNotSeen, double moveSpeed) {
        this.monster = monster;
        this.followingTargetEvenIfNotSeen = followingTargetEvenIfNotSeen;
        this.moveSpeed = moveSpeed;
        this.setFlags(EnumSet.of(Goal.Flag.LOOK, Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.monster.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.monster.getTarget();
        if (target == null || !target.isAlive()) return false;
        if (!this.followingTargetEvenIfNotSeen) {
            return !this.monster.getNavigation().isDone();
        }
        if (!this.monster.isWithinRestriction(target.blockPosition())) return false;
        if (target instanceof net.minecraft.world.entity.player.Player player) {
            return !target.isSpectator() && !player.isCreative();
        }
        return true;
    }

    @Override
    public void start() {
        this.monster.setAggressive(true);
    }

    private int delayCounter;

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity target = this.monster.getTarget();
        if (target != null) {
            this.monster.getLookControl().setLookAt(target, 30.0F, 30.0F);
            double distSq = this.monster.distanceToSqr(target.getX(), target.getBoundingBox().minY, target.getZ());
            if (--this.delayCounter <= 0) {
                this.delayCounter = 4 + this.monster.getRandom().nextInt(7);
                if (distSq > Math.pow(this.monster.getAttributeValue(Attributes.FOLLOW_RANGE), 2.0D)) {
                    if (!this.monster.isPathFinding() && !this.monster.getNavigation().moveTo(target, 1.0D)) {
                        this.delayCounter += 5;
                    }
                } else {
                    this.monster.getNavigation().moveTo(target, this.moveSpeed);
                }
            }
        }
    }

    @Override
    public void stop() {
        this.monster.getNavigation().stop();
        LivingEntity target = this.monster.getTarget();
        if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target)) {
            this.monster.setTarget(null);
        }
        this.monster.setAggressive(false);
        this.monster.getNavigation().stop();
    }

    @Override
    public boolean isInterruptable() {
        return true;
    }
}
