package com.qiuyue.goetyominous.common.entities.ally.mm.goals.MutantShulkerServant;

import com.alexander.mutantmore.config.mutant_shulker.MutantShulkerCommonConfig;
import com.alexander.mutantmore.entities.MutantShulkerBullet;
import com.alexander.mutantmore.init.SoundEventInit;
import com.qiuyue.goetyominous.common.entities.ally.mm.MutantShulkerServant;
import com.qiuyue.goetyominous.common.entities.ally.mm.MutantShulkerServantBullet;
import java.util.EnumSet;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class MutantShulkerServantShootAttackGoal extends Goal {
    public static final Predicate<Entity> MUTANT_SHULKER_BULLET = p_33346_ -> p_33346_ instanceof MutantShulkerBullet
            || (p_33346_ instanceof MutantShulkerServantBullet && ((MutantShulkerServantBullet) p_33346_).getOwner() instanceof MutantShulkerServant);
    public MutantShulkerServant mob;
    @Nullable
    public LivingEntity target;

    public MutantShulkerServantShootAttackGoal(MutantShulkerServant mob) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK));
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
        if (this.target != null && !this.target.isRemoved() && !this.target.isDeadOrDying()
                && this.mob.distanceTo(this.target) <= MutantShulkerCommonConfig.max_bite_distance.get()) {
            this.mob.pendingShootTicks = 0;
            return false;
        }
        int nearbyBullets = this.mob.level().getEntities(this.mob, this.mob.getBoundingBox().inflate(100.0), MUTANT_SHULKER_BULLET).size();
        return this.target != null && !this.target.isRemoved() && !this.target.isDeadOrDying() && nearbyBullets <= MutantShulkerCommonConfig.shoot_max_nearby_mutant_shulker_bullets.get() && this.mob.pendingShootTicks > 0 && this.animationsUseable() && !this.mob.isInBox();
    }

    public boolean canContinueToUse() {
        return this.target != null && !this.target.isRemoved() && !this.target.isDeadOrDying() && !this.animationsUseable();
    }

    public void start() {
        this.mob.pendingShootTicks = 0;
        this.mob.playSound(SoundEventInit.MUTANT_SHULKER_IDLE.get());
        this.mob.shootAnimationTick = this.mob.shootAnimationLength;
        this.mob.level().broadcastEntityEvent(this.mob, (byte)33);
    }

    public void tick() {
        this.target = this.mob.getTarget();
        this.mob.getNavigation().stop();
        if (this.target != null) {
            this.mob.getLookControl().setLookAt(this.target);
        }
        if (this.target != null && this.mob.shootAnimationTick == this.mob.shootAnimationActionPoint1) {
            this.mob.shootMutantShulkerProjectile(0.0F, null, true);
        }
        if (this.target != null && this.mob.shootAnimationTick == this.mob.shootAnimationActionPoint2) {
            this.mob.shootMutantShulkerProjectile(-10.0F, null, true);
        }
        if (this.target != null && this.mob.shootAnimationTick == this.mob.shootAnimationActionPoint3) {
            this.mob.shootMutantShulkerProjectile(10.0F, null, true);
        }
    }

    public boolean animationsUseable() {
        return this.mob.shootAnimationTick <= 0;
    }
}
