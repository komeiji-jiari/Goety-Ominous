package com.qiuyue.goetyominous.common.entities.ally.lm.goals;

import com.qiuyue.goetyominous.common.entities.ally.lm.PossessedPaladinServant;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class PossessedPaladinFinisherAttackGoal extends Goal {

    protected final PossessedPaladinServant entity;
    private final int getattackstate;
    private final int attackstate;
    protected final int attackendstate;
    private final int attackfinaltick;
    protected final int attackseetick;
    private final boolean canAllertedState;
    private final double allertedStateChance;

    public PossessedPaladinFinisherAttackGoal(PossessedPaladinServant entity, int getattackstate, int attackstate,
                                              int attackendstate, int attackfinaltick, int attackseetick,
                                              float attackrange, boolean canAllertedState,
                                              double allertedStateChance) {
        this.entity = entity;
        this.canAllertedState = canAllertedState;
        this.allertedStateChance = allertedStateChance;
        this.getattackstate = getattackstate;
        this.attackstate = attackstate;
        this.attackendstate = attackendstate;
        this.attackfinaltick = attackfinaltick;
        this.attackseetick = attackseetick;
        this.attackrange = attackrange;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
    }

    private final float attackrange;

    @Override
    public boolean canUse() {
        LivingEntity target = this.entity.getTarget();
        return target != null
                && target.isAlive()
                && this.entity.distanceTo(target) < this.attackrange
                && this.entity.getAttackState() == this.getattackstate
                && this.entity.getAttackDelayTicks() <= 0;
    }

    @Override
    public boolean canContinueToUse() {
        return this.entity.attackTicks < this.attackfinaltick;
    }

    @Override
    public void start() {
        this.entity.setAttackState(this.attackstate);
    }

    public boolean canBeAlerted() {
        return (double) (this.entity.getRandom().nextInt() * 100) < this.allertedStateChance
                && this.canAllertedState;
    }

    @Override
    public void stop() {
        this.entity.randomizeAttacks();
        this.entity.setAttackState(this.canBeAlerted() ? 9 : this.attackendstate);
        this.entity.attackCooldown = 0;
        this.entity.setNoGravity(false);
    }

    @Override
    public void tick() {
        LivingEntity target = this.entity.getTarget();
        if (this.entity.attackTicks < 140
                || this.entity.attackTicks > 160 && this.entity.attackTicks < 168
                || this.entity.attackTicks > 168 && this.entity.attackTicks < 260
                || this.entity.attackTicks > 275 && this.entity.attackTicks < 290) {
            if (target != null) {
                this.entity.getLookControl().setLookAt(target, 30.0F, 30.0F);
                this.entity.lookAt(target, 30.0F, 30.0F);
            }
        } else {
            this.entity.setYRot(this.entity.yRotO);
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return false;
    }
}
