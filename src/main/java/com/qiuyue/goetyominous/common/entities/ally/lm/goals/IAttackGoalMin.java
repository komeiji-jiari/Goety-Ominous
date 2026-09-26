package com.qiuyue.goetyominous.common.entities.ally.lm.goals;

import com.qiuyue.goetyominous.common.entities.ally.lm.IAnimatedMonsterServant;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class IAttackGoalMin extends Goal {
    protected final IAnimatedMonsterServant entity;
    private final int getattackstate;
    private final int attackstate;
    private final int attackendstate;
    private final int attackMaxtick;
    private final int attackseetick;
    private final float attackrange;
    private final float attackrangemin;

    public IAttackGoalMin(IAnimatedMonsterServant entity, int getattackstate, int attackstate,
                          int attackendstate, int attackMaxtick, int attackseetick,
                          float attackrange, float attackrangemin) {
        this.entity = entity;
        this.getattackstate = getattackstate;
        this.attackstate = attackstate;
        this.attackendstate = attackendstate;
        this.attackMaxtick = attackMaxtick;
        this.attackseetick = attackseetick;
        this.attackrange = attackrange;
        this.attackrangemin = attackrangemin;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
    }

    public IAttackGoalMin(IAnimatedMonsterServant entity, int getattackstate, int attackstate,
                          int attackendstate, int attackMaxtick, int attackseetick,
                          float attackrange, float attackrangemin, EnumSet<Goal.Flag> interruptFlagTypes) {
        this.entity = entity;
        this.getattackstate = getattackstate;
        this.attackstate = attackstate;
        this.attackendstate = attackendstate;
        this.attackMaxtick = attackMaxtick;
        this.attackseetick = attackseetick;
        this.attackrange = attackrange;
        this.attackrangemin = attackrangemin;
        this.setFlags(interruptFlagTypes);
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.entity.getTarget();
        return target != null
                && target.isAlive()
                && this.entity.distanceTo(target) < this.attackrange
                && this.entity.getAttackState() == this.getattackstate
                && this.entity.distanceTo(target) > this.attackrangemin
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
    public void tick() {
        LivingEntity target = this.entity.getTarget();
        if (this.entity.attackTicks < this.attackseetick && target != null) {
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
