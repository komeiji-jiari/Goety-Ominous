package com.qiuyue.goetyominous.common.entities.ally.lm.goals.knight;

import com.qiuyue.goetyominous.common.entities.ally.lm.ResurrectedKnightServant;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class RSAlertedStateGoal extends Goal {
    protected final ResurrectedKnightServant entity;
    private final int getattackstate;
    private final int attackstate;
    protected final int attackendstate;
    private final int attackfinaltick;
    protected final int attackseetick;
    public float velocity = 0.15F;

    public RSAlertedStateGoal(ResurrectedKnightServant entity, int getattackstate, int attackstate,
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
        return this.attackfinaltick > 0
                ? this.entity.attackTicks <= this.attackfinaltick + this.entity.getRandom().nextInt(-7, 10)
                : this.canUse();
    }

    @Override
    public void start() {
        if (this.entity.getRandom().nextInt() * 100 < 50) {
            this.velocity *= -1.0F;
        }
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
        LivingEntity target = this.entity.getTarget();
        if (this.entity.attackTicks < this.attackseetick && target != null) {
            this.entity.getLookControl().setLookAt(target, 30.0F, 30.0F);
            this.entity.lookAt(target, 30.0F, 30.0F);
        } else {
            this.entity.setYRot(this.entity.yRotO);
        }
        if (target != null) {
            double angle = Math.toRadians(-this.entity.yBodyRot + 90.0F);
            double sin = Mth.sin((float) angle) * 8.0D;
            double cos = Mth.cos((float) angle) * 8.0D;
            Vec3 pos = new Vec3(target.getX() + sin, this.entity.getY(), target.getZ() + cos);
            Vec3 posSub = pos.subtract(this.entity.position());
            this.entity.setDeltaMovement(posSub.normalize().scale(this.velocity));
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}
