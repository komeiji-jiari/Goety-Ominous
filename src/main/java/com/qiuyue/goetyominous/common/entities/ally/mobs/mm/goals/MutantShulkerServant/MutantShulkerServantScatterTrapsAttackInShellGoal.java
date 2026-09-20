package com.qiuyue.goetyominous.common.entities.ally.mobs.mm.goals.MutantShulkerServant;

import com.alexander.mutantmore.config.mutant_shulker.MutantShulkerCommonConfig;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.MutantShulkerServantTrap;
import com.alexander.mutantmore.init.SoundEventInit;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.MutantShulkerServant;
import com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.scores.Scoreboard;

public class MutantShulkerServantScatterTrapsAttackInShellGoal extends Goal {
    public MutantShulkerServant mob;
    @Nullable
    public LivingEntity target;

    public MutantShulkerServantScatterTrapsAttackInShellGoal(MutantShulkerServant mob) {
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
        return this.target != null && !this.target.isRemoved() && !this.target.isDeadOrDying() && this.mob.getRandom().nextInt(MutantShulkerCommonConfig.scatter_traps_in_shell_chance.get()) == 0 && this.animationsUseable() && this.mob.isInBox();
    }

    public boolean canContinueToUse() {
        return this.target != null && !this.target.isRemoved() && !this.target.isDeadOrDying() && !this.animationsUseable();
    }

    public void start() {
        this.mob.playSound(SoundEventInit.MUTANT_SHULKER_OPEN.get());
        this.mob.summonTrapsAnimationTickInShell = this.mob.summonTrapsAnimationLengthInShell;
        this.mob.level().broadcastEntityEvent(this.mob, (byte)10);
    }

    public void tick() {
        this.target = this.mob.getTarget();
        this.mob.getNavigation().stop();
        if (this.target != null) {
            this.mob.getLookControl().setLookAt(this.target);
        }
        if (this.target != null && this.mob.summonTrapsAnimationTickInShell > this.mob.summonTrapsAnimationLengthInShell - 40 && this.mob.summonTrapsAnimationTickInShell < this.mob.summonTrapsAnimationLengthInShell - 20 && this.mob.getRandom().nextBoolean()) {
            this.mob.playSound(SoundEventInit.MUTANT_SHULKER_SHOOT_TRAP.get(), 1.0F, 1.0F);
            MutantShulkerServantTrap trap = new MutantShulkerServantTrap(MmEntityRegistry.MUTANT_SHULKER_SERVANT_TRAP.get(), this.mob.level());
            if (this.mob != null && this.mob.getTeam() != null) {
                Scoreboard scoreboard = this.mob.level().getScoreboard();
                scoreboard.addPlayerToTeam(trap.getScoreboardName(), scoreboard.getPlayerTeam(this.mob.getTeam().getName()));
            }
            if (this.mob.getTrueOwner() != null) {
                trap.setOwnerUUID(this.mob.getTrueOwner().getUUID());
            }
            trap.moveTo(this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
            trap.setDeltaMovement(this.mob.getRandom().nextGaussian() * 1.5, 0.6, this.mob.getRandom().nextGaussian() * 1.5);
            trap.setSpawnedByMutantShulker(true);
            trap.setTarget(this.target);
            ((ServerLevel)this.mob.level()).addFreshEntityWithPassengers(trap);
        }
        if (this.mob.summonTrapsAnimationTickInShell == 15) {
            this.mob.playSound(SoundEventInit.MUTANT_SHULKER_CLOSE.get());
        }
    }

    public boolean animationsUseable() {
        return this.mob.summonTrapsAnimationTickInShell <= 0;
    }
}
