package com.qiuyue.goetyominous.common.entities.ally.lm.goals.obliterator;

import com.qiuyue.goetyominous.common.entities.ally.lm.TheObliteratorServant;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class SingleShotLaserGoal extends Goal {
    protected final TheObliteratorServant entity;
    private final int getattackstate;
    private final int attackstate;
    private final int attackendstate;
    private final int attackMaxtick;
    private final int attackseetick;
    private final float attackrange;

    public SingleShotLaserGoal(TheObliteratorServant entity, int getattackstate, int attackstate, int attackendstate,
                               int attackMaxtick, int attackseetick, float attackrange) {
        this.entity = entity;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
        this.getattackstate = getattackstate;
        this.attackstate = attackstate;
        this.attackendstate = attackendstate;
        this.attackMaxtick = attackMaxtick;
        this.attackseetick = attackseetick;
        this.attackrange = attackrange;
    }

    public SingleShotLaserGoal(TheObliteratorServant entity, int getattackstate, int attackstate, int attackendstate,
                               int attackMaxtick, int attackseetick, float attackrange,
                               EnumSet<Goal.Flag> interruptFlagTypes) {
        this.entity = entity;
        this.setFlags(interruptFlagTypes);
        this.getattackstate = getattackstate;
        this.attackstate = attackstate;
        this.attackendstate = attackendstate;
        this.attackMaxtick = attackMaxtick;
        this.attackseetick = attackseetick;
        this.attackrange = attackrange;
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
        this.entity.setAttackState(this.attackstate);
    }

    @Override
    public void stop() {
        switch (this.entity.getRandom().nextInt(this.entity.getPhase() >= 2 ? 2 : 1)) {
            case 0: {
                this.entity.setAttackState(this.entity.getRandom().nextInt() * 100 < 50 ? 45 : 44);
                break;
            }
            case 1: {
                this.entity.setAttackState(this.entity.quad_beam_cooldown <= 0 ? 46
                        : (this.entity.getRandom().nextInt() * 100 < 50 ? 45 : 44));
            }
        }
        this.entity.attackCooldown = 0;
    }

    @Override
    public boolean canContinueToUse() {
        return this.entity.attackTicks < this.attackMaxtick;
    }

    @Override
    public void tick() {
        int attackTicks = this.entity.getAttackTicks();
        LivingEntity target = this.entity.getTarget();
        if ((this.entity.attackTicks < this.attackseetick || this.entity.isTargetCheesing(-4.0F, 4.0F))
                && target != null) {
            this.entity.getLookControl().setLookAt(target, 30.0F, 30.0F);
            this.entity.lookAt(target, 30.0F, 180.0F);
        } else {
            this.entity.setYRot(this.entity.yRotO);
        }
        if (attackTicks == 6) {
            float f = Mth.cos(this.entity.yBodyRot * ((float) Math.PI / 180));
            float f1 = Mth.sin(this.entity.yBodyRot * ((float) Math.PI / 180));
            double theta = (double) this.entity.yBodyRot * (Math.PI / 180);
            double vecX = Math.cos(theta += 1.5707963267948966);
            double vecZ = Math.sin(theta);
            float vec = -8.0F;
            float offset = 0.0F;
            this.entity.teleport(this.entity.getX() + (double) vec * vecX + (double) (f * offset),
                    this.entity.getY(),
                    this.entity.getZ() + (double) vec * vecZ + (double) (f1 * offset));
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return false;
    }
}
