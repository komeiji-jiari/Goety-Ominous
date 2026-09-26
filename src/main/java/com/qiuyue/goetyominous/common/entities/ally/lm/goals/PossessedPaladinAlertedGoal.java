package com.qiuyue.goetyominous.common.entities.ally.lm.goals;

import com.qiuyue.goetyominous.common.entities.ally.lm.PossessedPaladinServant;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class PossessedPaladinAlertedGoal extends Goal {

    protected final PossessedPaladinServant entity;
    private final int getattackstate;
    private final int attackstate;
    protected final int attackendstate;
    private final int attackfinaltick;
    protected final int attackseetick;
    private final boolean canAllertedState;
    private final double allertedStateChance;
    public float velocity = 0.15F;

    public PossessedPaladinAlertedGoal(PossessedPaladinServant entity, int getattackstate, int attackstate,
                                       int attackendstate, int attackfinaltick, int attackseetick,
                                       boolean canAllertedState, double allertedStateChance) {
        this.entity = entity;
        this.canAllertedState = canAllertedState;
        this.allertedStateChance = allertedStateChance;
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
        if (this.entity.getRandom().nextInt() * 100 < 50) {
            this.velocity *= -1.0F;
        }
        if (this.getattackstate != this.attackstate) {
            this.entity.setAttackState(this.attackstate);
        }
    }

    public boolean canBeAlerted() {
        return (double) (this.entity.getRandom().nextInt() * 100) < this.allertedStateChance
                && this.canAllertedState;
    }

    @Override
    public void stop() {
        this.entity.setAttackState(this.entity.hasParried ? 5 : this.attackendstate);
        this.entity.attackTicks = 0;
        this.entity.attackCooldown = 0;
    }

    @Override
    public boolean canContinueToUse() {
        return this.attackfinaltick > 0
                ? this.entity.attackTicks <= this.attackfinaltick + this.entity.getRandom().nextInt(-7, 10)
                : this.canUse();
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
            float radius = 8.0F;
            double angle = Math.toRadians((double) (-this.entity.yBodyRot + 90.0F));
            double sin = (double) (Mth.sin((float) angle) * radius);
            double cos = (double) (Mth.cos((float) angle) * radius);
            Vec3 sideOffset = new Vec3(target.getX() + sin - this.entity.getX(), 0.0D,
                    target.getZ() + cos - this.entity.getZ());
            Vec3 finalPos = sideOffset.normalize().scale((double) this.velocity);
            this.entity.setDeltaMovement(finalPos);
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}
