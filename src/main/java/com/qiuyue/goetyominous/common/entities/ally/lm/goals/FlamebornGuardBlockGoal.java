package com.qiuyue.goetyominous.common.entities.ally.lm.goals;

import com.qiuyue.goetyominous.common.entities.ally.lm.AbstractFlamebornServant;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class FlamebornGuardBlockGoal extends Goal {
    protected final AbstractFlamebornServant entity;
    private final int getattackstate;
    private final int attackstate;
    protected final int attackendstate;
    private final int attackfinaltick;
    protected final int attackseetick;

    public FlamebornGuardBlockGoal(AbstractFlamebornServant entity, int getattackstate, int attackstate,
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
    public boolean canContinueToUse() {
        if (this.attackfinaltick > 0) {
            return this.entity.attackTicks <= this.attackfinaltick;
        }
        return this.canUse();
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
    public void tick() {
        int attackTicks = this.entity.getAttackTicks();
        LivingEntity target = this.entity.getTarget();
        if (this.entity.attackTicks < this.attackseetick && target != null) {
            this.entity.getLookControl().setLookAt(target, 30.0F, 30.0F);
            this.entity.lookAt(target, 30.0F, 30.0F);
        } else {
            this.entity.setYRot(this.entity.yRotO);
        }
        if (attackTicks == 11 && target != null) {
            float f = Mth.cos(target.yBodyRot * ((float) Math.PI / 180));
            float f1 = Mth.sin(target.yBodyRot * ((float) Math.PI / 180));
            double theta = target.yBodyRot * (Math.PI / 180) + 1.5707963267948966;
            double vecX = Math.cos(theta);
            double vecZ = Math.sin(theta);
            float vec = -4.0F;
            float offset = 0.0F;
            this.entity.teleport(target.getX() + (double) vec * vecX + (double) (f * offset),
                    this.entity.getY(),
                    target.getZ() + (double) vec * vecZ + (double) (f1 * offset));
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}
