package com.qiuyue.goetyominous.common.entities.ally.mobs.mm.goals.MutantBlazeServant;

import com.alexander.mutantmore.config.mutant_blaze.MutantBlazeCommonConfig;
import com.alexander.mutantmore.init.ParticleTypeInit;
import com.alexander.mutantmore.init.SoundEventInit;
import com.alexander.mutantmore.particles.AdvancedParticleOption;
import com.alexander.mutantmore.util.PositionUtils;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;

import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.MutantBlazeServant;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class MutantBlazeServantShootAttackGoal extends Goal {
    public MutantBlazeServant mob;
    @Nullable
    public LivingEntity target;
    public int nextUseTime;

    public MutantBlazeServantShootAttackGoal(MutantBlazeServant mob) {
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
        return this.target != null && !this.target.isRemoved() && !this.target.isDeadOrDying() && this.mob.tickCount >= this.nextUseTime && this.animationsUseable();
    }

    public boolean canContinueToUse() {
        return this.target != null && !this.target.isRemoved() && !this.target.isDeadOrDying() && !this.animationsUseable();
    }

    public void start() {
        this.mob.playSound((SoundEvent)SoundEventInit.MUTANT_BLAZE_PREPARE_SHOOT.get(), 2.0F, 1.0F);
        this.mob.shootAnimationTick = this.mob.shootAnimationLength;
        this.mob.level().broadcastEntityEvent(this.mob, (byte)4);
    }

    public void tick() {
        this.target = this.mob.getTarget();
        this.mob.getNavigation().stop();
        Player nearestPlayer = this.mob.level().getNearestPlayer(this.mob, 100.0);
        Vec3 particlePos = nearestPlayer != null ? nearestPlayer.position() : PositionUtils.getOffsetPos(this.mob, 0.0, 2.5, -1.0, 0.0F, this.mob.yBodyRot);
        if (this.mob.shootAnimationTick == this.mob.shootAnimationLength - 15) {
            ((ServerLevel)this.mob.level()).sendParticles(new AdvancedParticleOption(ParticleTypeInit.MUTANT_BLAZE_CHARGE_SHOT, List.of((float)this.mob.getId(), 0.0F, 2.5F, -1.0F)), particlePos.x, particlePos.y, particlePos.z, 1, 0.0, 0.0, 0.0, 0.0);
            this.mob.playSound((SoundEvent)SoundEventInit.MUTANT_BLAZE_FLARE.get(), 2.0F, this.mob.getVoicePitch());
        }

        if (this.mob.getHealth() <= this.mob.getMaxHealth() * (((Integer)MutantBlazeCommonConfig.fireball_attack_extra_fireballs_health_threshold_1.get()).floatValue() / 100.0F) && (this.mob.shootAnimationTick == this.mob.shootAnimationLength - 20 || this.mob.shootAnimationTick == this.mob.shootAnimationLength - 25)) {
            ((ServerLevel)this.mob.level()).sendParticles(new AdvancedParticleOption(ParticleTypeInit.MUTANT_BLAZE_CHARGE_SHOT, List.of((float)this.mob.getId(), 0.0F, 2.5F, -1.0F)), particlePos.x, particlePos.y, particlePos.z, 1, 0.0, 0.0, 0.0, 0.0);
            this.mob.playSound((SoundEvent)SoundEventInit.MUTANT_BLAZE_FLARE.get(), 2.0F, this.mob.getVoicePitch());
        }

        if (this.mob.getHealth() <= this.mob.getMaxHealth() * (((Integer)MutantBlazeCommonConfig.fireball_attack_extra_fireballs_health_threshold_2.get()).floatValue() / 100.0F) && (this.mob.shootAnimationTick == this.mob.shootAnimationLength - 30 || this.mob.shootAnimationTick == this.mob.shootAnimationLength - 35)) {
            ((ServerLevel)this.mob.level()).sendParticles(new AdvancedParticleOption(ParticleTypeInit.MUTANT_BLAZE_CHARGE_SHOT, List.of((float)this.mob.getId(), 0.0F, 2.5F, -1.0F)), particlePos.x, particlePos.y, particlePos.z, 1, 0.0, 0.0, 0.0, 0.0);
            this.mob.playSound((SoundEvent)SoundEventInit.MUTANT_BLAZE_FLARE.get(), 2.0F, this.mob.getVoicePitch());
        }

        if (this.target != null) {
            this.mob.getLookControl().setLookAt(this.target);
            double d1 = this.target.getX() - this.mob.getX();
            double d2 = this.target.getY(0.5) - this.mob.getY(0.5);
            double d3 = this.target.getZ() - this.mob.getZ();
            if (this.mob.shootAnimationTick == this.mob.shootAnimationActionPoint) {
                this.mob.shootMutantBlazeFireball(d1, d2, d3, false);
            }

            if (this.mob.getHealth() <= this.mob.getMaxHealth() * (((Integer)MutantBlazeCommonConfig.fireball_attack_extra_fireballs_health_threshold_1.get()).floatValue() / 100.0F) && (this.mob.shootAnimationTick == this.mob.shootAnimationActionPoint - 6 || this.mob.shootAnimationTick == this.mob.shootAnimationActionPoint - 12)) {
                this.mob.shootMutantBlazeFireball(d1, d2, d3, false);
            }

            if (this.mob.getHealth() <= this.mob.getMaxHealth() * (((Integer)MutantBlazeCommonConfig.fireball_attack_extra_fireballs_health_threshold_2.get()).floatValue() / 100.0F) && (this.mob.shootAnimationTick == this.mob.shootAnimationActionPoint - 18 || this.mob.shootAnimationTick == this.mob.shootAnimationActionPoint - 24)) {
                this.mob.shootMutantBlazeFireball(d1, d2, d3, false);
            }
        }

    }

    public void stop() {
        this.nextUseTime = this.mob.tickCount + (Integer)MutantBlazeCommonConfig.fireball_attack_cooldown.get();
    }

    public boolean animationsUseable() {
        return this.mob.shootAnimationTick <= 0;
    }
}
