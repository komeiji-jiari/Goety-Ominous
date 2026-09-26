package com.qiuyue.goetyominous.common.entities.ally.lm.goals;

import com.qiuyue.goetyominous.common.entities.ally.lm.PossessedPaladinServant;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class PossessedPaladinSlamAttackGoal extends Goal {

    protected final PossessedPaladinServant entity;
    private final int getattackstate;
    private final int attackstate;
    private final int attackendstate;
    private final int attackMaxtick;
    private final int attackseetick;
    private final float attackrange;

    public PossessedPaladinSlamAttackGoal(PossessedPaladinServant entity, int getattackstate, int attackstate,
                                          int attackendstate, int attackMaxtick, int attackseetick,
                                          float attackrange) {
        this.entity = entity;
        this.getattackstate = getattackstate;
        this.attackstate = attackstate;
        this.attackendstate = attackendstate;
        this.attackMaxtick = attackMaxtick;
        this.attackseetick = attackseetick;
        this.attackrange = attackrange;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.entity.getTarget();
        return target != null
                && target.isAlive()
                && this.entity.distanceTo(target) < this.attackrange
                && this.entity.getAttackState() == this.getattackstate
                && this.entity.getAttackDelayTicks() <= 0
                && this.entity.getRandom().nextFloat() * 100.0F < (float) this.entity.slamRandom
                && this.entity.slam_cooldown <= 0;
    }

    @Override
    public boolean canContinueToUse() {
        return this.entity.attackTicks < this.attackMaxtick;
    }

    @Override
    public void start() {
        this.entity.shouldAttackMore = false;
        this.entity.setAttackState(this.attackstate);
    }

    @Override
    public void tick() {
        LivingEntity target = this.entity.getTarget();
        if (this.entity.attackTicks < this.attackseetick && target != null) {
            this.entity.getLookControl().setLookAt(target, 30.0F, 30.0F);
            this.entity.lookAt(target, 30.0F, 30.0F);
        } else {
            this.entity.setYRot(this.entity.yRotO);
        }
    }

    @Override
    public void stop() {
        this.entity.slam_cooldown = this.entity.SLAM_COOLDOWN;
        this.entity.randomizeAttacks();

        boolean endState = !this.entity.targetIsNotNull()
                || !(this.entity.distanceTo(this.entity.target()) >= 6.0F);
        if (this.entity.getPhase() <= 1) {
            this.entity.setAttackState(endState && !this.entity.shouldAttackMore ? 7 : 8);
        } else {
            this.entity.setAttackState(endState && !this.entity.shouldAttackMore ? 7 : 31);
        }

        this.entity.attackCooldown = 0;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return false;
    }
}
