package com.qiuyue.goetyominous.common.entities.ally.mobs.mm.goals.MutantHoglinServant;

import com.alexander.mutantmore.config.mutant_hoglin.MutantHoglinCommonConfig;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.MutantHoglinServant;
import java.util.EnumSet;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class MutantHoglinMeleeAttackGoal extends Goal {
    public MutantHoglinServant mob;
    @Nullable
    public LivingEntity target;
    public int nextUseTime;

    public MutantHoglinMeleeAttackGoal(MutantHoglinServant mob) {
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
        return this.target != null && this.mob.notCurrentlyPlayingKeyframeAnimation() && this.mob.tickCount >= this.nextUseTime && !this.target.isRemoved() && !this.target.isDeadOrDying() && (double)this.mob.distanceTo(this.target) <= (Double)MutantHoglinCommonConfig.max_basic_attack_distance.get() && this.animationsUseable() && this.mob.hasLineOfSight(this.target);
    }

    public boolean canContinueToUse() {
        return this.target != null && !this.target.isRemoved() && !this.target.isDeadOrDying() && !this.animationsUseable();
    }

    public void start() {
        MutantHoglinServant var10000 = this.mob;
        Objects.requireNonNull(this.mob);
        var10000.attackAnimationTick = 22;
        this.mob.level().broadcastEntityEvent(this.mob, (byte)6);
    }

    public void tick() {
        this.target = this.mob.getTarget();
        this.mob.getNavigation().stop();
        if (this.target != null) {
            this.mob.getLookControl().setLookAt(this.target);
        }

        if (this.target != null && (double)this.mob.distanceTo(this.target) <= (Double)MutantHoglinCommonConfig.max_basic_attack_damage_distance.get()) {
            int var10000 = this.mob.attackAnimationTick;
            Objects.requireNonNull(this.mob);
            if (var10000 == 13) {
                if ((Boolean)MutantHoglinCommonConfig.ignores_invulnerability_time.get()) {
                    this.target.invulnerableTime = 0;
                }

                this.mob.doHurtTarget(this.target);
            }
        }

    }

    public void stop() {
        super.stop();
        this.nextUseTime = this.mob.tickCount + (Integer)MutantHoglinCommonConfig.basic_attack_cooldown.get();
    }

    public boolean animationsUseable() {
        return this.mob.attackAnimationTick <= 0;
    }
}
