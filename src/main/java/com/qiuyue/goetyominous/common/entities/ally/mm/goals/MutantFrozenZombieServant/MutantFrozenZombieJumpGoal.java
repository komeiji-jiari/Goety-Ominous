package com.qiuyue.goetyominous.common.entities.ally.mm.goals.MutantFrozenZombieServant;

import com.alexander.mutantmore.config.mutant_frozen_zombie.MutantFrozenZombieCommonConfig;
import com.alexander.mutantmore.entities.IceCube;
import com.alexander.mutantmore.init.EffectInit;
import com.alexander.mutantmore.util.MiscUtils;
import com.qiuyue.goetyominous.common.entities.ally.mm.MutantFrozenZombieServant;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class MutantFrozenZombieJumpGoal extends Goal {
    public MutantFrozenZombieServant mob;
    @Nullable
    public LivingEntity target;
    public boolean slammed;

    public MutantFrozenZombieJumpGoal(MutantFrozenZombieServant mob) {
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
        this.mob = mob;
        this.target = mob.getTarget();
    }

    public boolean isInterruptable() {
        return this.mob.shouldBeStationary();
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public boolean canUse() {
        this.target = this.mob.getTarget();
        return this.target != null && !this.target.isRemoved() && !this.target.isDeadOrDying() && this.animationsUseable() && this.mob.hasLineOfSight(this.target) && this.mob.wantsToJump;
    }

    public boolean canContinueToUse() {
        return this.target != null && !this.target.isRemoved() && !this.target.isDeadOrDying() && (this.mob.jumpAnimationTick > 0 || this.mob.jumping || this.mob.slamAnimationTick > 0);
    }

    public void start() {
        this.mob.jumpAnimationTick = this.mob.jumpAnimationLength;
        this.mob.noveltyAnimationTick = 0;
        this.mob.jumping = true;
        this.slammed = false;
        this.mob.wantsToJump = false;
        this.mob.level().broadcastEntityEvent(this.mob, (byte) 15);
    }

    public void tick() {
        this.target = this.mob.getTarget();
        this.mob.getNavigation().stop();
        if (this.target != null) {
            this.mob.getLookControl().setLookAt(this.target);
            if (this.mob.jumpAnimationTick == this.mob.jumpAnimationActionPoint) {
                double d0 = this.target.getX() - this.mob.getX();
                double d1 = this.target.getY() - this.mob.getY();
                double d2 = this.target.getZ() - this.mob.getZ();
                this.mob.setDeltaMovement(d0 * 0.175, 0.6 + d1 * 0.1, d2 * 0.175);
            }
            if (this.mob.jumping && this.mob.distanceTo(this.target) <= 3.0F && !this.slammed) {
                this.mob.slamAnimationTick = this.mob.slamAnimationLength;
                this.mob.noveltyAnimationTick = 0;
                this.mob.jumping = false;
                this.mob.level().broadcastEntityEvent(this.mob, (byte) 16);
                this.slammed = true;
            }
            if (this.mob.slamAnimationTick == this.mob.slamAnimationActionPoint) {
                MiscUtils.disableShield(this.target, MutantFrozenZombieCommonConfig.ice_smash_disable_shield_length.get());
                this.target.hurt(this.mob.damageSources().mobAttack(this.mob), MutantFrozenZombieCommonConfig.ice_smash_damage.get().floatValue());
                for (IceCube iceCube : this.mob.level().getEntitiesOfClass(IceCube.class, this.target.getBoundingBox())) {
                    if (iceCube == null || iceCube.getTarget() == null || iceCube.getTarget() != this.target) continue;
                    iceCube.discard();
                }
                if (this.target.hasEffect(EffectInit.FREEZING.get())) {
                    this.target.removeEffect(EffectInit.FREEZING.get());
                }
                this.target.setIsInPowderSnow(false);
                this.target.setTicksFrozen(0);
            }
        }
    }

    public void stop() {
        super.stop();
    }

    public boolean animationsUseable() {
        return this.mob.jumpAnimationTick <= 0;
    }
}
