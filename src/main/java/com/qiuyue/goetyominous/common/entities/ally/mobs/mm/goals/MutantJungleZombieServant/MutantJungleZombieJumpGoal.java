package com.qiuyue.goetyominous.common.entities.ally.mobs.mm.goals.MutantJungleZombieServant;

import com.alexander.mutantmore.config.mutant_jungle_zombie.MutantJungleZombieCommonConfig;
import com.alexander.mutantmore.init.MMDamageTypes;
import com.alexander.mutantmore.init.TagInit.EntityTypes;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.AreaDamage;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.MutantJungleZombieServant;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class MutantJungleZombieJumpGoal extends Goal {
    public MutantJungleZombieServant mob;
    @Nullable
    public LivingEntity target;

    public MutantJungleZombieJumpGoal(MutantJungleZombieServant mob) {
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
        return !this.mob.jumping
                && this.target != null
                && !this.target.isRemoved()
                && !this.target.isDeadOrDying()
                && this.mob.onGround()
                && ((double) this.mob.distanceTo(this.target) >= (Double) MutantJungleZombieCommonConfig.min_jump_distance.get() || (double) this.distanceToY(this.target) >= (Double) MutantJungleZombieCommonConfig.min_jump_distance_y.get())
                && this.animationsUseable();
    }

    public float distanceToY(Entity p_20271_) {
        float f1 = (float) (this.mob.getY() - p_20271_.getY());
        return Mth.sqrt(f1 * f1);
    }

    public boolean canContinueToUse() {
        return !this.mob.shouldBeStationary() && this.target != null && !this.target.isRemoved() && !this.target.isDeadOrDying() && !this.animationsUseable();
    }

    public void start() {
        this.mob.jumpAnimationTick = this.mob.jumpAnimationLength;
        this.mob.level().broadcastEntityEvent(this.mob, (byte) 12);
    }

    public void tick() {
        this.target = this.mob.getTarget();
        this.mob.getNavigation().stop();
        if (this.target != null) {
            this.mob.lookAt(Anchor.EYES, this.target.position());
        }
        if (this.target != null && this.mob.jumpAnimationTick == this.mob.jumpAnimationActionPoint) {
            double d0 = this.target.getX() - this.mob.getX();
            double d1 = this.target.getY() - this.mob.getY();
            double d2 = this.target.getZ() - this.mob.getZ();
            AreaDamage areaDamage = AreaDamage.spawnAreaDamage(this.mob.level(), this.mob.position(), this.mob, ((Double) MutantJungleZombieCommonConfig.jump_poison_cloud_damage.get()).floatValue(), MMDamageTypes.poisonCloud(this.mob.damageSources()), 0.0F, 5.0F, 0.2F, 1.5F, 80, 0, true, false, 0.0, 0.0, false, false, 0, false, EntityTypes.MUTANT_JUNGLE_ZOMBIE_CANT_HURT, 3);
            ((ServerLevel) this.mob.level()).addFreshEntityWithPassengers(areaDamage);
            this.mob.setDeltaMovement(d0 * 0.15, 0.6 + Mth.clamp(d1 * 0.075, 0.0, 10.0), d2 * 0.15);
            this.mob.jumping = true;
        }
    }

    public boolean animationsUseable() {
        return this.mob.jumpAnimationTick <= 0;
    }
}
