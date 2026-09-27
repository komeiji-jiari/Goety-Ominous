package com.qiuyue.goetyominous.common.entities.ally.lm.goals.obliterator;

import com.qiuyue.goetyominous.common.entities.ally.lm.TheObliteratorServant;
import net.miauczel.legendary_monsters.Particle.ModParticles;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class JumpTeleportGoal extends Goal {
    protected final TheObliteratorServant entity;
    private final int getattackstate;
    private final int attackstate;
    private final int attackendstate;
    private final int attackMaxtick;
    private final int attackseetick;
    private final int attackseetickattack3;
    private final int attackseetickattack2;
    private final float attackrange;

    public JumpTeleportGoal(TheObliteratorServant entity, int getattackstate, int attackstate, int attackendstate,
                            int attackMaxtick, int attackseetick, int attackseetick2, int attackseetick3,
                            float attackrange) {
        this.entity = entity;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
        this.getattackstate = getattackstate;
        this.attackstate = attackstate;
        this.attackendstate = attackendstate;
        this.attackMaxtick = attackMaxtick;
        this.attackseetick = attackseetick;
        this.attackrange = attackrange;
        this.attackseetickattack2 = attackseetick2;
        this.attackseetickattack3 = attackseetick3;
    }

    public JumpTeleportGoal(TheObliteratorServant entity, int getattackstate, int attackstate, int attackendstate,
                            int attackMaxtick, int attackseetick, float attackrange, int attackseetick2,
                            int attackseetick3, EnumSet<Goal.Flag> interruptFlagTypes) {
        this.entity = entity;
        this.setFlags(interruptFlagTypes);
        this.getattackstate = getattackstate;
        this.attackstate = attackstate;
        this.attackendstate = attackendstate;
        this.attackMaxtick = attackMaxtick;
        this.attackseetick = attackseetick;
        this.attackseetickattack3 = attackseetick3;
        this.attackseetickattack2 = attackseetick2;
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
        this.entity.setAttackState(this.attackendstate);
        this.entity.attackCooldown = 0;
    }

    @Override
    public boolean canContinueToUse() {
        return this.entity.attackTicks < this.attackMaxtick;
    }

    @Override
    public void tick() {
        LivingEntity target = this.entity.getTarget();
        if (target != null && (this.entity.attackTicks < this.attackseetick
                || this.entity.attackTicks > this.attackseetickattack2
                && this.entity.attackTicks < this.attackseetickattack3)) {
            this.entity.getLookControl().setLookAt(target, 30.0F, 30.0F);
            this.entity.lookAt(target, 30.0F, 30.0F);
        } else {
            this.entity.setYRot(this.entity.yRotO);
        }
        int attackTicks = this.entity.getAttackTicks();
        if (attackTicks == 20) {
            if (target != null) {
                this.entity.getLookControl().setLookAt(target, 60.0F, 30.0F);
                this.entity.setDeltaMovement((target.getX() - this.entity.getX()) * 0.15, 1.3,
                        (target.getZ() - this.entity.getZ()) * 0.15);
            } else {
                this.entity.setDeltaMovement(this.entity.getDeltaMovement().add(0.0, 1.3, 0.0));
            }
        }
        if (attackTicks == 48) {
            if (this.entity.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ModParticles.TELEPORT_EFFECT.get(), this.entity.getX(),
                        this.entity.getY() + 3.0, this.entity.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }
        if (attackTicks == 49 && target != null) {
        }
        if (attackTicks == 81) {
            if (this.entity.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ModParticles.TELEPORT_EFFECT.get(), this.entity.getX(),
                        this.entity.getY() + 3.0, this.entity.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }
        if (attackTicks == 82 && target != null) {
            float f = Mth.cos(target.yBodyRot * ((float) Math.PI / 180));
            float f1 = Mth.sin(target.yBodyRot * ((float) Math.PI / 180));
            double theta = (double) target.yBodyRot * (Math.PI / 180);
            double vecX = Math.cos(theta += 1.5707963267948966);
            double vecZ = Math.sin(theta);
            float vec = -4.0F;
            float offset = 0.0F;
            float y = (float) Mth.floor(target.getY());
            this.entity.teleport(target.getX() + (double) vec * vecX + (double) (f * offset),
                    y,
                    target.getZ() + (double) vec * vecZ + (double) (f1 * offset));
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return false;
    }
}
