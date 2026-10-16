package com.qiuyue.goetyominous.common.entities.ally.mobs.mm.goals.MutantFrozenZombieServant;

import com.alexander.mutantmore.config.mutant_frozen_zombie.MutantFrozenZombieCommonConfig;
import com.alexander.mutantmore.util.PositionUtils;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.GiantSnowballServant;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.MutantFrozenZombieServant;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class MutantFrozenZombieSnowThrowAttackGoal extends Goal {
    public MutantFrozenZombieServant mob;
    @Nullable
    public LivingEntity target;
    public int nextUseTime;
    public double lastTargetX = 0.0;
    public double lastTargetZ = 0.0;

    public MutantFrozenZombieSnowThrowAttackGoal(MutantFrozenZombieServant mob) {
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
        this.mob = mob;
        this.target = mob.getTarget();
    }

    public boolean isInterruptable() {
        return false;
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public boolean canUse() {
        this.target = this.mob.getTarget();
        return this.target != null && this.mob.tickCount >= this.nextUseTime && !this.target.isRemoved() && !this.target.isDeadOrDying() && this.animationsUseable() && this.mob.hasLineOfSight(this.target) && this.target.isFullyFrozen();
    }

    public boolean canContinueToUse() {
        return !this.animationsUseable();
    }

    public void start() {
        this.mob.snowThrowAnimationTick = this.mob.snowThrowAnimationLength;
        this.mob.noveltyAnimationTick = 0;
        this.mob.level().broadcastEntityEvent(this.mob, (byte) 11);
    }

    public void tick() {
        this.target = this.mob.getTarget();
        this.mob.getNavigation().stop();
        if (this.target != null) {
            this.mob.lookAt(Anchor.EYES, this.target.position());
        }
        if (this.target != null && this.mob.snowThrowAnimationTick == this.mob.snowThrowAnimationActionPoint) {
            GiantSnowballServant snowball = new GiantSnowballServant(this.mob.level(), this.mob);
            Vec3 snowballPos = PositionUtils.getOffsetPos(this.mob, 0.0, 2.0, 3.0, 0.0F, this.mob.yBodyRot);
            double predictionMultiplier = (double) this.mob.distanceTo(this.target) * 0.65;
            double d0 = this.target.getY(0.1);
            double d1 = this.target.getX() + (this.target.getX() - this.lastTargetX) * predictionMultiplier - snowballPos.x;
            double d2 = d0 - snowballPos.y;
            double d3 = this.target.getZ() + (this.target.getZ() - this.lastTargetZ) * predictionMultiplier - snowballPos.z;
            double d4 = Math.sqrt(d1 * d1 + d3 * d3) * (double) 0.2F;
            snowball.shoot(d1, d2 + d4, d3, 2.0F, 0.0F);
            snowball.moveTo(snowballPos);
            this.mob.level().addFreshEntity(snowball);
        }
        if (this.target != null) {
            this.lastTargetX = this.target.getX();
            this.lastTargetZ = this.target.getZ();
        }
    }

    public void stop() {
        super.stop();
        this.nextUseTime = this.mob.tickCount + MutantFrozenZombieCommonConfig.snow_throw_cooldown.get();
    }

    public boolean animationsUseable() {
        return this.mob.snowThrowAnimationTick <= 0;
    }
}
