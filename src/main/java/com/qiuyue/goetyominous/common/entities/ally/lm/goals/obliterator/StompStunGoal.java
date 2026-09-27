package com.qiuyue.goetyominous.common.entities.ally.lm.goals.obliterator;

import com.qiuyue.goetyominous.common.entities.ally.lm.TheObliteratorServant;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class StompStunGoal extends Goal {
    protected final TheObliteratorServant entity;
    private final int getattackstate;
    private final int attackstate;
    protected final int attackendstate;
    private final int attackfinaltick;
    protected final int attackseetick;

    public StompStunGoal(TheObliteratorServant entity, int getattackstate, int attackstate, int attackendstate,
                         int attackfinaltick, int attackseetick) {
        this.entity = entity;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
        this.getattackstate = getattackstate;
        this.attackstate = attackstate;
        this.attackendstate = attackendstate;
        this.attackfinaltick = attackfinaltick;
        this.attackseetick = attackseetick;
    }

    public StompStunGoal(TheObliteratorServant entity, int getattackstate, int attackstate, int attackendstate,
                         int attackfinaltick, int attackseetick, boolean interruptsAI) {
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
        if (this.entity.attackTicks < this.attackseetick && target != null) {
            this.entity.getLookControl().setLookAt(target, 30.0F, 30.0F);
            this.entity.lookAt(target, 30.0F, 30.0F);
        } else {
            this.entity.setYRot(this.entity.yRotO);
        }
        int attackTicks = this.entity.getAttackTicks();
        if (attackTicks == 15 && this.entity.targetIsNotNull()) {
            float f = Mth.cos(this.entity.target().yHeadRot * ((float) Math.PI / 180));
            float f1 = Mth.sin(this.entity.target().yHeadRot * ((float) Math.PI / 180));
            double theta = (double) this.entity.target().yHeadRot * (Math.PI / 180);
            double vecX = Math.cos(theta += 1.5707963267948966);
            double vecZ = Math.sin(theta);
            switch (this.entity.getRandom().nextInt(2)) {
                case 0: {
                    float vec = 1.0F;
                    float offset = 4.0F;
                    this.entity.teleport(this.entity.target().getX() + (double) vec * vecX + (double) (f * offset),
                            this.entity.target().getY(),
                            this.entity.target().getZ() + (double) vec * vecZ + (double) (f1 * offset));
                    break;
                }
                case 1: {
                    float vec = 1.0F;
                    float offset = -4.0F;
                    this.entity.teleport(this.entity.target().getX() + (double) vec * vecX + (double) (f * offset),
                            this.entity.target().getY(),
                            this.entity.target().getZ() + (double) vec * vecZ + (double) (f1 * offset));
                }
            }
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}
