package com.qiuyue.goetyominous.common.entities.ally.lm.goals.knight;

import com.qiuyue.goetyominous.common.entities.ally.lm.BeheadedKnightServant;
import com.qiuyue.goetyominous.common.entities.ally.lm.ResurrectedKnightServant;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class BHGrabAndThrowGoal extends Goal {
    protected final BeheadedKnightServant entity;
    private final int getattackstate;
    private final int attackstate;
    private final int attackMaxtick;
    private final int attackseetick;
    private final float attackrange;

    public BHGrabAndThrowGoal(BeheadedKnightServant entity, int getattackstate, int attackstate,
                              int attackMaxtick, int attackseetick, float attackrange) {
        this.entity = entity;
        this.getattackstate = getattackstate;
        this.attackstate = attackstate;
        this.attackMaxtick = attackMaxtick;
        this.attackseetick = attackseetick;
        this.attackrange = attackrange;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        ResurrectedKnightServant target = this.entity.synchronisedDuoKnight();
        return target != null
                && target.isAlive()
                && this.entity.distanceTo(target) < this.attackrange
                && this.entity.getAttackState() == this.getattackstate
                && this.entity.getAttackDelayTicks() <= 0;
    }

    @Override
    public boolean canContinueToUse() {
        return this.entity.attackTicks < this.attackMaxtick;
    }

    @Override
    public void start() {
        this.entity.setAttackState(this.attackstate);
    }

    @Override
    public void stop() {
        this.entity.setAttackState(this.entity.grab ? 12 : 13);
        this.entity.attackCooldown = 0;
    }

    @Override
    public void tick() {
        ResurrectedKnightServant target = this.entity.synchronisedDuoKnight();
        if (this.entity.attackTicks < this.attackseetick && target != null) {
            this.entity.getLookControl().setLookAt(target, 30.0F, 30.0F);
            this.entity.lookAt(target, 30.0F, 30.0F);
        } else {
            this.entity.setYRot(this.entity.yRotO);
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return false;
    }
}
