package com.qiuyue.goetyominous.common.entities.ally.lm.goals.obliterator;

import com.qiuyue.goetyominous.common.entities.ally.lm.TheObliteratorServant;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class AmbushSwapGoal extends Goal {
    protected final TheObliteratorServant entity;
    private final int getattackstate;
    private final int attackstate;
    private final int attackendstate;
    private final int attackMaxtick;
    private final int attackseetick;
    private final float attackrange;

    public AmbushSwapGoal(TheObliteratorServant entity, int getattackstate, int attackstate, int attackendstate,
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

    public AmbushSwapGoal(TheObliteratorServant entity, int getattackstate, int attackstate, int attackendstate,
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
        int clonesAmount;
        int bonusClones;
        int randomOffset;
        int tp1 = 32;
        int tp2 = 52;
        int tp3 = 74;
        int tp4 = 96;
        LivingEntity target = this.entity.getTarget();
        if (this.entity.attackTicks < this.attackseetick && target != null) {
            this.entity.getLookControl().setLookAt(target, 30.0F, 30.0F);
            this.entity.lookAt(target, 30.0F, 30.0F);
        } else {
            this.entity.setYRot(this.entity.yRotO);
        }
        int attackTicks = this.entity.getAttackTicks();
        if (attackTicks == tp1 - 2) {
            this.entity.saveTeleportPos(this.entity, this.entity.isAlive(), -8.0F, 0.0F);
        }
        if (attackTicks == tp1) {
            this.entity.setInvulnerable(true);
            this.entity.teleport(this.entity.teleportX, this.entity.teleportY, this.entity.teleportZ);
        }
        if (attackTicks == tp2 - 2) {
            this.entity.saveTeleportPos(this.entity.getTarget(), this.entity.targetIsNotNull(), 6.0F, 0.0F);
        }
        if (attackTicks == tp2 && this.entity.getTarget() != null) {
            this.entity.setInvulnerable(false);
            this.entity.teleportRandomly(this.entity, 10.0F, 10.0F);
            this.entity.setNoGravity(false);
        }
        if (attackTicks == tp3 - 2) {
            this.entity.saveTeleportPos(this.entity, this.entity.isAlive(), -8.0F, 0.0F);
        }
        if (attackTicks == 91) {
        }
        if (attackTicks == tp3 && this.entity.getTarget() != null) {
            this.entity.getNavigation().stop();
            this.entity.setInvulnerable(true);
            this.entity.teleport(this.entity.teleportX, this.entity.teleportY, this.entity.teleportZ);
        }
        if (attackTicks == tp4 - 2) {
            this.entity.saveTeleportPos(this.entity.target(), this.entity.targetIsNotNull(), -6.0F, 0.0F);
        }
        if (attackTicks == 100) {
        }
        if (attackTicks == tp4) {
            this.entity.getNavigation().stop();
            if (this.entity.getTarget() != null) {
                this.entity.setInvulnerable(false);
                this.entity.teleport(this.entity.teleportX, this.entity.teleportY, this.entity.teleportZ);
                this.entity.setNoGravity(false);
            }
        }
        if (attackTicks == 31) {
        }
        if (attackTicks == 32 && this.entity.getTarget() != null) {
            randomOffset = this.entity.getRandom().nextInt(3);
            bonusClones = this.entity.getRandom().nextInt(2);
            clonesAmount = 10 + bonusClones;
            for (int k = 0; k < clonesAmount; ++k) {
                float angleOnCircle = (float) k * (float) Math.PI * -2.0F / 8.0F
                        + (float) randomOffset + -0.41887903F;
                double spawnX = this.entity.getTarget().getX() + Math.cos(angleOnCircle) * 8.5;
                double spawnZ = this.entity.getTarget().getZ() + Math.sin(angleOnCircle) * 8.5;
                int standingOnY = Mth.floor(this.entity.getY());
                double spawnY = standingOnY;
                double headY = this.entity.getTarget().getY() + 1.0;
                double dx = this.entity.getTarget().getX() - spawnX;
                double dz = this.entity.getTarget().getZ() - spawnZ;
                float yawRad = (float) Math.atan2(dz, dx);
                this.entity.spawnDuplicateVersions(spawnX, spawnZ, spawnY, headY, yawRad, 4,
                        (float) this.entity.getTarget().getX(), (float) this.entity.getTarget().getY(),
                        (float) this.entity.getTarget().getZ());
            }
        }
        if (attackTicks == tp3 + 2) {
            totalPoints = 16;
            double radius = 8.5;
            this.spawnRingWave(0, 2, totalPoints, radius);
        }
        if (attackTicks == tp3 + 18) {
            totalPoints = 16;
            double radius = 8.5;
            this.spawnRingWave(1, 2, totalPoints, radius);
        }
        if (attackTicks == 65) {
            this.entity.setInvulnerable(false);
            this.entity.setNoGravity(false);
        }
        if (attackTicks == 92) {
        }
        if (attackTicks == tp3 + 2 && this.entity.getTarget() != null) {
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
