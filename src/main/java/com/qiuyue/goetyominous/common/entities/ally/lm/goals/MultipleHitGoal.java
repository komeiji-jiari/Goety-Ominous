package com.qiuyue.goetyominous.common.entities.ally.lm.goals;

import com.qiuyue.goetyominous.common.entities.ally.lm.IAnimatedMonsterServant;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class MultipleHitGoal extends Goal {
    protected final IAnimatedMonsterServant entity;
    private final int getattackstate;
    private final int attackstate;
    private final int attackendstate;
    private final int attackMaxtick;
    private final int attackseetick;
    private final int attackseetickattack4;
    private final int attackseetickattack3;
    private final int attackseetickattack2;
    private final float attackrange;

    public MultipleHitGoal(IAnimatedMonsterServant entity, int getattackstate, int attackstate,
                           int attackendstate, int attackMaxtick, int attackseetick,
                           int attackseetick2, int attackseetick3, int attackseetick4,
                           float attackrange) {
        this.entity = entity;
        this.getattackstate = getattackstate;
        this.attackstate = attackstate;
        this.attackendstate = attackendstate;
        this.attackMaxtick = attackMaxtick;
        this.attackseetick = attackseetick;
        this.attackseetickattack2 = attackseetick2;
        this.attackseetickattack3 = attackseetick3;
        this.attackseetickattack4 = attackseetick4;
        this.attackrange = attackrange;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
    }

    public MultipleHitGoal(IAnimatedMonsterServant entity, int getattackstate, int attackstate,
                           int attackendstate, int attackMaxtick, int attackseetick,
                           float attackrange, int attackseetick2, int attackseetick3, int attackseetick4,
                           EnumSet<Goal.Flag> interruptFlagTypes) {
        this.entity = entity;
        this.getattackstate = getattackstate;
        this.attackstate = attackstate;
        this.attackendstate = attackendstate;
        this.attackMaxtick = attackMaxtick;
        this.attackseetick = attackseetick;
        this.attackseetickattack4 = attackseetick4;
        this.attackseetickattack3 = attackseetick3;
        this.attackseetickattack2 = attackseetick2;
        this.attackrange = attackrange;
        this.setFlags(interruptFlagTypes);
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.entity.getTarget();
        return target != null
                && target.isAlive()
                && this.entity.distanceTo(target) < this.attackrange
                && this.entity.getAttackState() == this.getattackstate;
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
    public void tick() {
        LivingEntity target = this.entity.getTarget();
        if (target != null && (this.entity.attackTicks < this.attackseetick
                || this.entity.attackTicks > this.attackseetickattack2 && this.entity.attackTicks < this.attackseetickattack3)) {
            this.entity.getLookControl().setLookAt(target, 30.0F, 30.0F);
            this.entity.lookAt(target, 30.0F, 30.0F);
        } else {
            this.entity.setYRot(this.entity.yBodyRot);
        }
    }

    @Override
    public void stop() {
        this.entity.setAttackState(this.attackendstate);
        this.entity.attackCooldown = 0;
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }
}
