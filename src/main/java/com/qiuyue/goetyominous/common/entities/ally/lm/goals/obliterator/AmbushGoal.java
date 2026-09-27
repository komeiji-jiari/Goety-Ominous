package com.qiuyue.goetyominous.common.entities.ally.lm.goals.obliterator;

import com.qiuyue.goetyominous.common.entities.ally.lm.TheObliteratorServant;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class AmbushGoal extends Goal {
    protected final TheObliteratorServant entity;
    private final int getattackstate;
    private final int attackstate;
    private final int attackendstate;
    private final int attackMaxtick;
    private final int attackseetick;
    private final float attackrange;

    public AmbushGoal(TheObliteratorServant entity, int getattackstate, int attackstate, int attackendstate,
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

    public AmbushGoal(TheObliteratorServant entity, int getattackstate, int attackstate, int attackendstate,
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
        this.entity.setAttackState(this.attackendstate);
        this.entity.attackCooldown = 0;
    }

    @Override
    public boolean canContinueToUse() {
        return this.entity.attackTicks < this.attackMaxtick;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        int totalPoints;
        LivingEntity target = this.entity.getTarget();
        if (this.entity.attackTicks < this.attackseetick && target != null) {
            this.entity.getLookControl().setLookAt(target, 30.0F, 30.0F);
            this.entity.lookAt(target, 30.0F, 30.0F);
        } else {
            this.entity.setYRot(this.entity.yRotO);
        }
        int attackTicks = this.entity.getAttackTicks();
        if (attackTicks >= 57 && attackTicks <= 58) {
            this.entity.saveTeleportPos(this.entity.getTarget(), this.entity.targetIsNotNull(), 6.0F, 0.0F);
        }
        if (attackTicks == 57) {
            this.entity.getNavigation().stop();
            this.entity.playSound(SoundEvents.ENDERMAN_TELEPORT, 2.0F, 1.0F);
            if (this.entity.getTarget() != null && !this.entity.level().isClientSide
                    && this.entity.level() instanceof ServerLevel) {
                this.entity.setInvulnerable(false);
                if (this.entity.targetIsNotNull()) {
                    this.entity.teleportRandomly(this.entity.getTarget(), 10.0F, 10.0F);
                }
            }
        }
        if (attackTicks >= 29 && attackTicks <= 30) {
            this.entity.saveTeleportPos(this.entity, this.entity.isAlive(), -8.0F, 0.0F);
        }
        if (attackTicks == 29) {
            this.entity.teleport(this.entity.teleportX, this.entity.teleportY, this.entity.teleportZ);
        }
        if (attackTicks == 33) {
            totalPoints = 16;
            double radius = 8.5;
            this.spawnRingWave(0, 2, totalPoints, radius);
        }
        if (this.entity.getPhase() >= 2 && attackTicks == 49) {
        }
    }

    private void spawnRingWave(int waveIndex, int wavesTotal, int totalPoints, double radius) {
        LivingEntity target = this.entity.getTarget();
        if (target == null) {
            return;
        }
        double step = Math.PI * 2 / (double) totalPoints;
        int pointsThisWave = totalPoints / wavesTotal;
        double base = -0.41887902047863906;
        double waveOffset = (double) waveIndex * step;
        for (int i = 0; i < pointsThisWave; ++i) {
            double angle = base + waveOffset + (double) i * ((double) wavesTotal * step);
            double spawnX = target.getX() + Math.cos(angle) * radius;
            double spawnZ = target.getZ() + Math.sin(angle) * radius;
            double spawnY = Mth.floor(this.entity.getY());
            double headY = target.getY() + 1.0;
            double dx = target.getX() - spawnX;
            double dz = target.getZ() - spawnZ;
            float yawRad = (float) Math.atan2(dz, dx);
            this.entity.spawnDuplicateVersions(spawnX, spawnZ, spawnY, headY, yawRad, 4,
                    (float) target.getX(), (float) target.getY(), (float) target.getZ());
        }
    }
}
