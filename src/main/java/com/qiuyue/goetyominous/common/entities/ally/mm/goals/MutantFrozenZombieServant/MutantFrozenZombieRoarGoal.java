package com.qiuyue.goetyominous.common.entities.ally.mm.goals.MutantFrozenZombieServant;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.alexander.mutantmore.config.mutant_frozen_zombie.MutantFrozenZombieCommonConfig;
import com.qiuyue.goetyominous.compat.mm.MmScreenShake;
import com.alexander.mutantmore.util.PositionUtils;
import com.qiuyue.goetyominous.common.entities.ally.mm.MutantFrozenZombieServant;
import com.qiuyue.goetyominous.common.entities.ally.mm.ZombieResurrectionServant;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class MutantFrozenZombieRoarGoal extends Goal {
    public MutantFrozenZombieServant mob;
    @Nullable
    public LivingEntity target;
    public int nextUseTime;

    public MutantFrozenZombieRoarGoal(MutantFrozenZombieServant mob) {
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
        for (Entity entity : this.mob.resurrectedMobs) {
            if (entity != null && !entity.isRemoved()) continue;
            this.mob.resurrectedMobs.remove(entity);
            break;
        }
        return this.target != null && this.mob.tickCount >= this.nextUseTime && !this.target.isRemoved() && !this.target.isDeadOrDying() && this.animationsUseable() && this.mob.hasLineOfSight(this.target);
    }

    public boolean canContinueToUse() {
        return !this.animationsUseable();
    }

    public void start() {
        this.mob.roarAnimationTick = this.mob.roarAnimationLength;
        this.mob.noveltyAnimationTick = 0;
        this.mob.level().broadcastEntityEvent(this.mob, (byte) 10);
    }

    public void tick() {
        this.target = this.mob.getTarget();
        this.mob.getNavigation().stop();
        if (this.target != null) {
            this.mob.lookAt(Anchor.EYES, this.target.position());
        }
        if (this.mob.roarAnimationTick == this.mob.roarAnimationActionPoint && this.mob.resurrectedMobs.isEmpty()) {
            int randomAddedFrozenZombies = MutantFrozenZombieCommonConfig.roar_max_summoned_zombies.get() - MutantFrozenZombieCommonConfig.roar_min_summoned_zombies.get() <= 0 ? 0 : this.mob.getRandom().nextInt(MutantFrozenZombieCommonConfig.roar_max_summoned_zombies.get() - MutantFrozenZombieCommonConfig.roar_min_summoned_zombies.get());
            int frozenZombies = MutantFrozenZombieCommonConfig.roar_min_summoned_zombies.get() + randomAddedFrozenZombies;
            for (int i = 0; i < frozenZombies; ++i) {
                ZombieResurrectionServant resurrection = new ZombieResurrectionServant(this.mob.level(), this.mob.getRandom().nextInt(100), this.mob);
                resurrection.moveTo(this.mob.position().add((double) (-MutantFrozenZombieCommonConfig.roar_summon_zombies_range.get() + this.mob.getRandom().nextInt(MutantFrozenZombieCommonConfig.roar_summon_zombies_range.get() * 2)), 0.0, (double) (-MutantFrozenZombieCommonConfig.roar_summon_zombies_range.get() + this.mob.getRandom().nextInt(MutantFrozenZombieCommonConfig.roar_summon_zombies_range.get() * 2))));
                resurrection.setEntityType(ModEntityType.FROZEN_ZOMBIE_SERVANT.get());
                resurrection.setYRot(this.mob.getRandom().nextFloat() * 360.0F);
                PositionUtils.moveToCorrectHeight(resurrection, true);
                ((ServerLevel) this.mob.level()).addFreshEntityWithPassengers(resurrection);
                MmScreenShake.shake(this.mob.level(), 66, 0.025F, this.mob.blockPosition(), 25);
            }
        }
    }

    public void stop() {
        super.stop();
        int randomAddedCooldown = MutantFrozenZombieCommonConfig.max_roar_cooldown.get() - MutantFrozenZombieCommonConfig.min_roar_cooldown.get() <= 0 ? 0 : this.mob.getRandom().nextInt(MutantFrozenZombieCommonConfig.max_roar_cooldown.get() - MutantFrozenZombieCommonConfig.min_roar_cooldown.get());
        int cooldown = MutantFrozenZombieCommonConfig.min_roar_cooldown.get() + randomAddedCooldown;
        this.nextUseTime = this.mob.tickCount + cooldown;
    }

    public boolean animationsUseable() {
        return this.mob.roarAnimationTick <= 0;
    }
}
