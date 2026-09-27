package com.qiuyue.goetyominous.common.entities.ally.lm.goals.obliterator;

import com.qiuyue.goetyominous.common.entities.ally.lm.TheObliteratorServant;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class UltimateAttackStateGoal extends Goal {
    protected final TheObliteratorServant entity;
    private final int getattackstate;
    private final int attackstate;
    protected final int attackendstate;
    private final int attackfinaltick;
    protected final int attackseetick;

    public UltimateAttackStateGoal(TheObliteratorServant entity, int getattackstate, int attackstate,
                                   int attackendstate, int attackfinaltick, int attackseetick) {
        this.entity = entity;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
        this.getattackstate = getattackstate;
        this.attackstate = attackstate;
        this.attackendstate = attackendstate;
        this.attackfinaltick = attackfinaltick;
        this.attackseetick = attackseetick;
    }

    public UltimateAttackStateGoal(TheObliteratorServant entity, int getattackstate, int attackstate,
                                   int attackendstate, int attackfinaltick, int attackseetick,
                                   boolean interruptsAI) {
        this.entity = entity;
        if (interruptsAI) {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
        }
        this.getattackstate = getattackstate;
        this.attackstate = attackstate;
        this.attackendstate = attackendstate;
        this.attackfinaltick = attackfinaltick;
        this.attackseetick = attackseetick;
    }

    @Override
    public boolean canUse() {
        return this.entity.getAttackState() == this.getattackstate;
    }

    @Override
    public void start() {
        if (this.getattackstate != this.attackstate) {
            this.entity.setAttackState(this.attackstate);
        }
    }

    @Override
    public void stop() {
        this.entity.setAttackState(this.attackendstate);
        this.entity.attackTicks = 0;
        this.entity.attackCooldown = 0;
    }

    @Override
    public boolean canContinueToUse() {
        return this.attackfinaltick > 0 ? this.entity.attackTicks <= this.attackfinaltick : this.canUse();
    }

    @Override
    public void tick() {
        LivingEntity target = this.entity.getTarget();
        if ((this.entity.attackTicks < 80 || this.entity.attackTicks > 110 && this.entity.attackTicks < 130) && target != null) {
            this.entity.getLookControl().setLookAt(target, 30.0F, 30.0F);
            this.entity.lookAt(target, 30.0F, 30.0F);
        } else {
            this.entity.setYRot(this.entity.yRotO);
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}
