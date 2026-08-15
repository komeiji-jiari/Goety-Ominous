package com.qiuyue.goetyominous.common.entities.ally.mobs.mm.goals.MutantHoglinServant;

import com.alexander.mutantmore.config.mutant_hoglin.MutantHoglinCommonConfig;
import com.alexander.mutantmore.events.ShakeCameraEvent;
import com.alexander.mutantmore.init.MMDamageTypes;
import com.alexander.mutantmore.init.ParticleTypeInit;
import com.alexander.mutantmore.util.MiscUtils;
import com.alexander.mutantmore.util.PositionUtils;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.MutantHoglinServant;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class MutantHoglinStompAttackGoal extends Goal {
    public MutantHoglinServant mob;
    @Nullable
    public LivingEntity target;
    public int nextUseTime;

    public MutantHoglinStompAttackGoal(MutantHoglinServant mob) {
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
        return this.target != null
                && !this.mob.isVehicle()
                && this.mob.notCurrentlyPlayingKeyframeAnimation()
                && this.mob.tickCount >= this.nextUseTime
                && !this.target.isRemoved()
                && !this.target.isDeadOrDying()
                && this.animationsUseable();
    }

    public boolean canContinueToUse() {
        return !this.animationsUseable();
    }

    public void start() {
        MutantHoglinServant var10000 = this.mob;
        Objects.requireNonNull(this.mob);
        var10000.stompAnimationTick = 50;
        this.mob.level().broadcastEntityEvent(this.mob, (byte)11);
    }

    public void tick() {
        this.target = this.mob.getTarget();
        this.mob.getNavigation().stop();
        if (this.target != null) {
            this.mob.getLookControl().setLookAt(this.target);
        }

        int var10000 = this.mob.stompAnimationTick;
        Objects.requireNonNull(this.mob);
        if (var10000 != 39) {
            var10000 = this.mob.stompAnimationTick;
            Objects.requireNonNull(this.mob);
            if (var10000 != 24) {
                var10000 = this.mob.stompAnimationTick;
                Objects.requireNonNull(this.mob);
                if (var10000 != 13) {
                    return;
                }
            }
        }

        Vec3 particlePos = PositionUtils.getOffsetPos(this.mob, 1.375, 0.0, 1.1, 0.0F, this.mob.yBodyRot);
        ((ServerLevel)this.mob.level()).sendParticles((SimpleParticleType)ParticleTypeInit.SHOCKWAVE.get(), particlePos.x, particlePos.y, particlePos.z, 1, 0.0, 0.0, 0.0, 0.0);
        this.mob.level().broadcastEntityEvent(this.mob, (byte)15);
        ShakeCameraEvent.shake(this.mob.level(), 6, 0.15F, this.mob.blockPosition(), ((Double)MutantHoglinCommonConfig.stomp_attack_range.get()).intValue());
        Iterator var2 = this.mob.level().getEntitiesOfClass(LivingEntity.class, this.mob.getBoundingBox().inflate((Double)MutantHoglinCommonConfig.stomp_attack_range.get(), (Double)MutantHoglinCommonConfig.stomp_attack_range.get(), (Double)MutantHoglinCommonConfig.stomp_attack_range.get()), MiscUtils.ALIVE).iterator();

        while(true) {
            Entity entity;
            do {
                do {
                    if (!var2.hasNext()) {
                        return;
                    }

                    entity = (Entity)var2.next();
                } while(entity == this.mob);
            } while(!entity.onGround() && (!entity.isPassenger() || !entity.getRootVehicle().onGround()));

            if (this.mob.canHarm(entity)) {
                if ((Boolean)MutantHoglinCommonConfig.ignores_invulnerability_time.get()) {
                    entity.invulnerableTime = 0;
                }

                entity.hurt(MMDamageTypes.earthquakeAttack(this.mob.damageSources(), this.mob), ((Double)MutantHoglinCommonConfig.stomp_attack_damage.get()).floatValue() * this.mob.enragedDamageMultiplier());
            }

            entity.push(this.mob.getRandom().nextGaussian() * 0.5, 0.5 * (double)this.mob.enragedKnockbackMultiplier(), this.mob.getRandom().nextGaussian() * 0.5);
        }
    }

    public void stop() {
        super.stop();
        if (this.mob.hasSoulJar()) {
            this.mob.summonFlameRings();
        }
        int randomAddedCooldown = (Integer)MutantHoglinCommonConfig.max_stomp_cooldown.get() - (Integer)MutantHoglinCommonConfig.min_stomp_cooldown.get() <= 0 ? 0 : this.mob.getRandom().nextInt((Integer)MutantHoglinCommonConfig.max_stomp_cooldown.get() - (Integer)MutantHoglinCommonConfig.min_stomp_cooldown.get());
        int cooldown = (Integer)MutantHoglinCommonConfig.min_stomp_cooldown.get() + randomAddedCooldown;
        this.nextUseTime = this.mob.tickCount + cooldown;
    }

    public boolean animationsUseable() {
        return this.mob.stompAnimationTick <= 0;
    }
}
