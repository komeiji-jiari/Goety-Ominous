package com.qiuyue.goetyominous.common.entities.ally.mobs.mm.goals.MutantShulkerServant;

import com.alexander.mutantmore.config.mutant_shulker.MutantShulkerCommonConfig;
import com.alexander.mutantmore.init.ParticleTypeInit;
import com.alexander.mutantmore.init.SoundEventInit;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.MutantShulkerServant;
import java.util.EnumSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class MutantShulkerServantFlyGoal extends Goal {
    public MutantShulkerServant mob;
    public LivingEntity target;
    public int moveDelay = 0;
    public Vec3 movement = Vec3.ZERO;
    public int flyingFor = 0;
    public int nextUseTime = 0;

    public MutantShulkerServantFlyGoal(MutantShulkerServant mob) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
        this.mob = mob;
        this.target = mob.getTarget();
    }

    @Override
    public boolean canUse() {
        return this.mob.shouldBeStationary();
    }

    @Override
    public boolean canContinueToUse() {
        return true;
    }

    @Override
    public boolean isInterruptable() {
        this.target = this.mob.getTarget();
        if (this.target == null || this.target.isRemoved() || this.target.isDeadOrDying()) {
            return false;
        }
        if (this.mob.getHealth() > this.mob.getMaxHealth() * (MutantShulkerCommonConfig.fly_health_threshold.get() / 100.0F)) {
            return false;
        }
        if (this.mob.tickCount < this.nextUseTime) {
            return false;
        }
        if (!this.animationsUseable()) {
            return false;
        }
        if (this.mob.flying) {
            return false;
        }
        return !this.mob.isInBox();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return this.target != null && !this.target.isRemoved() && !this.target.isDeadOrDying()
                && (!this.animationsUseable() || this.mob.flying);
    }

    @Override
    public void start() {
        this.mob.playSound(SoundEventInit.MUTANT_SHULKER_PREPARE_FLIGHT.get(), 2.0F, 1.0F);
        this.mob.prepareFlyAnimationTick = this.mob.prepareFlyAnimationLength;
        this.mob.level().broadcastEntityEvent(this.mob, (byte) 7);
    }

    @Override
    public void stop() {
        this.target = this.mob.getTarget();
        this.mob.getNavigation().stop();
        if (this.target != null) {
            if (this.mob.prepareFlyAnimationTick == this.mob.prepareFlyActionPoint) {
                this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(0.0D, 1.0D, 0.0D));
            }
            if (this.mob.prepareFlyAnimationTick == 1) {
                this.mob.flying = true;
                this.mob.level().broadcastEntityEvent(this.mob, (byte) 5);
            }
            if (this.mob.flying) {
                ++this.flyingFor;
                ++this.moveDelay;
                if (this.flyingFor >= MutantShulkerCommonConfig.stop_flying_time.get()) {
                    this.flyingFor = 0;
                    this.mob.flying = false;
                    this.mob.level().broadcastEntityEvent(this.mob, (byte) 6);
                    if (MutantShulkerCommonConfig.enters_shell_after_flying.get() && this.mob.tickCount >= this.mob.nextEnterShellTime) {
                        this.mob.setInBox(true);
                    }
                    this.tick();
                }
                ((ServerLevel) this.mob.level()).sendParticles(ParticleTypeInit.MUTANT_SHULKER_BULLET.get(),
                        this.mob.getX(), this.mob.getY(), this.mob.getZ(), 1, 0.2D, 0.2D, 0.2D, 0.0D);
                if (!this.mob.level().getBlockState(this.mob.blockPosition().below()).isAir()) {
                    this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(0.0D, 0.1D, 0.0D));
                }
                if (this.moveDelay >= 15) {
                    double d0 = this.target.getX() - this.mob.getX();
                    double d1 = this.target.getY() + this.target.getEyeHeight() / 2.0F - this.mob.getY();
                    double d2 = this.target.getZ() - this.mob.getZ();
                    double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                    if (this.moveDelay == 15) {
                        double speed = MutantShulkerCommonConfig.flying_movement_speed.get();
                        this.movement = new Vec3(d0 / d3 * speed, d1 / d3 * speed, d2 / d3 * speed);
                    }
                    this.mob.setDeltaMovement(0.0D, 0.0D, 0.0D);
                    this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(this.movement));
                    if (this.moveDelay >= 55) {
                        this.moveDelay = 0;
                        this.movement = Vec3.ZERO;
                    }
                }
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.flyingFor = 0;
        this.mob.flying = false;
        this.mob.level().broadcastEntityEvent(this.mob, (byte) 6);
        if (MutantShulkerCommonConfig.enters_shell_after_flying.get() && this.mob.tickCount >= this.mob.nextEnterShellTime) {
            this.mob.setInBox(true);
        }
        this.nextUseTime = this.mob.tickCount + MutantShulkerCommonConfig.fly_cooldown.get();
    }

    public boolean animationsUseable() {
        return this.mob.prepareFlyAnimationTick <= 0;
    }
}
