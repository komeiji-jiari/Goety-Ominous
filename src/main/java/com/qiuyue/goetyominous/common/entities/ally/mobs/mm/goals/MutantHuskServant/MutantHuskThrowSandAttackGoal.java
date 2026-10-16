package com.qiuyue.goetyominous.common.entities.ally.mobs.mm.goals.MutantHuskServant;

import com.alexander.mutantmore.config.MutantMoreGroupedOptionsCommonConfig;
import com.alexander.mutantmore.config.mutant_husk.MutantHuskCommonConfig;
import com.alexander.mutantmore.init.SoundEventInit;
import com.alexander.mutantmore.util.MiscUtils;
import com.alexander.mutantmore.util.PositionUtils;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.MutantHuskServant;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.SandBoulderServant;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class MutantHuskThrowSandAttackGoal extends Goal {
    public MutantHuskServant mob;
    @Nullable
    public LivingEntity target;
    public int nextUseTime;

    public MutantHuskThrowSandAttackGoal(MutantHuskServant mob) {
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
        return this.target != null && this.mob.tickCount >= this.nextUseTime && !this.target.isRemoved() && !this.target.isDeadOrDying() && (double) this.mob.distanceTo(this.target) >= (Double) MutantHuskCommonConfig.min_sand_throw_distance.get() && (double) this.mob.distanceTo(this.target) <= (Double) MutantHuskCommonConfig.max_sand_throw_distance.get() && this.animationsUseable() && this.mob.hasLineOfSight(this.target);
    }

    public boolean canContinueToUse() {
        return !this.animationsUseable();
    }

    public void start() {
        this.mob.throwSandAnimationTick = this.mob.throwSandAnimationLength;
        this.mob.noveltyAnimationTick = 0;
        this.mob.level().broadcastEntityEvent(this.mob, (byte) 6);
        BlockPos sandPos = PositionUtils.getOffsetBlockPos(this.mob, 0.0, -1.0, 3.0, 0.0F, this.mob.yBodyRot);
        this.mob.setSandType(MiscUtils.sandStringForPos(this.mob.level(), sandPos));
        this.mob.playSound(SoundEventInit.MUTANT_HUSK_ATTACK.get(), 2.0F, MiscUtils.randomSoundPitch());
    }

    public void tick() {
        this.target = this.mob.getTarget();
        this.mob.getNavigation().stop();
        if (this.target != null) {
            this.mob.lookAt(Anchor.EYES, this.target.position());
        }
        if (this.target != null && this.mob.throwSandAnimationTick == this.mob.throwSandAnimationActionPoint) {
            this.mob.playSound(SoundEventInit.MUTANT_HUSK_SAND_THROW.get(), 2.0F, MiscUtils.randomSoundPitch());
            SandBoulderServant boulder = new SandBoulderServant(this.mob.level(), this.mob);
            Vec3 boulderPos = this.mob.position().add(0.0, 6.0, 0.0);
            double d0 = this.target.getX() - boulderPos.x;
            double d1 = this.target.getY(0.3333333333333333) - boulderPos.y;
            double d2 = this.target.getZ() - boulderPos.z;
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            boulder.moveTo(boulderPos);
            boulder.shoot(d0, d1 + d3 * 0.1, d2, 1.0F, 1.5F);
            boulder.setSandType(this.mob.getSandType());
            boulder.damage = ((Double) MutantHuskCommonConfig.sand_boulder_damage.get()).floatValue();
            boulder.lifeSpan = (Integer) MutantHuskCommonConfig.sand_boulder_lifespan.get();
            boulder.griefing = (Boolean) MutantHuskCommonConfig.sand_boulder_griefing.get() && !MutantMoreGroupedOptionsCommonConfig.quicksand_griefing_off.get();
            boulder.ignoresInvulTime = (Boolean) MutantHuskCommonConfig.ignores_invulnerability_time.get();
            this.mob.level().addFreshEntity(boulder);
        }
    }

    public void stop() {
        super.stop();
        int randomAddedCooldown = (Integer) MutantHuskCommonConfig.max_sand_throw_cooldown.get() - (Integer) MutantHuskCommonConfig.min_sand_throw_cooldown.get() <= 0 ? 0 : this.mob.getRandom().nextInt((Integer) MutantHuskCommonConfig.max_sand_throw_cooldown.get() - (Integer) MutantHuskCommonConfig.min_sand_throw_cooldown.get());
        int cooldown = (Integer) MutantHuskCommonConfig.min_sand_throw_cooldown.get() + randomAddedCooldown;
        this.nextUseTime = this.mob.tickCount + cooldown;
    }

    public boolean animationsUseable() {
        return this.mob.throwSandAnimationTick <= 0;
    }
}
