package com.qiuyue.goetyominous.common.entities.ally.lm.goals.knight;

import com.qiuyue.goetyominous.common.entities.ally.lm.HauntedKnightServant;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class KnightParryGoal extends Goal {

    protected final HauntedKnightServant entity;
    private final int getattackstate;
    private final int attackstate;
    private final int attackMaxtick;
    private final int attackseetick;
    private final float attackrange;

    public KnightParryGoal(HauntedKnightServant entity, int getattackstate, int attackstate,
                           int attackendstate, int attackMaxtick, int attackseetick, float attackrange) {
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
        LivingEntity target = this.entity.getTarget();
        return target != null
                && target.isAlive()
                && this.entity.distanceTo(target) < this.attackrange
                && this.entity.getAttackState() == this.getattackstate
                && this.entity.getAttackDelayTicks() <= 0;
    }

    @Override
    public void start() {
        this.entity.gambitParry = false;
        this.entity.setAttackState(this.attackstate);
    }

    @Override
    public void stop() {
        this.entity.setAttackState(this.entity.gambitParry ? 8 : 5);
        this.entity.attackCooldown = 0;
    }

    @Override
    public boolean canContinueToUse() {
        return this.entity.attackTicks < this.attackMaxtick;
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
    public boolean requiresUpdateEveryTick() {
        return false;
    }
}
