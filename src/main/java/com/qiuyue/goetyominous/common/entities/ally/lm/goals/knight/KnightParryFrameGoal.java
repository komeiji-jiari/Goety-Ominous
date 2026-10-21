package com.qiuyue.goetyominous.common.entities.ally.lm.goals.knight;

import com.qiuyue.goetyominous.common.entities.ally.lm.HauntedKnightServant;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class KnightParryFrameGoal extends Goal {

    protected final HauntedKnightServant entity;
    private final int getattackstate;
    private final int attackstate;
    private final int attackendstate;
    private final int attackfinaltick;
    private final int attackseetick;

    public KnightParryFrameGoal(HauntedKnightServant entity, int getattackstate, int attackstate,
                                int attackendstate, int attackfinaltick, int attackseetick) {
        this.entity = entity;
        this.getattackstate = getattackstate;
        this.attackstate = attackstate;
        this.attackendstate = attackendstate;
        this.attackfinaltick = attackfinaltick;
        this.attackseetick = attackseetick;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
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
        this.entity.setAttackState(this.entity.gambitParry ? 8 : 7);
        this.entity.attackTicks = 0;
        this.entity.attackCooldown = 0;
    }

    @Override
    public boolean canContinueToUse() {
        return this.attackfinaltick > 0
                ? this.entity.attackTicks <= this.attackfinaltick
                : this.canUse();
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
        return true;
    }
}
