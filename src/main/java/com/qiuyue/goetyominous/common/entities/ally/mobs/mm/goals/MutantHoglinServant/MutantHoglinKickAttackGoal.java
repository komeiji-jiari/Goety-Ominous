package com.qiuyue.goetyominous.common.entities.ally.mobs.mm.goals.MutantHoglinServant;

import com.alexander.mutantmore.config.mutant_hoglin.MutantHoglinCommonConfig;
import com.alexander.mutantmore.events.ShakeCameraEvent;
import com.alexander.mutantmore.init.EffectInit;
import com.alexander.mutantmore.util.MiscUtils;
import com.alexander.mutantmore.util.PositionUtils;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.MutantHoglinServant;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class MutantHoglinKickAttackGoal extends Goal {
    public MutantHoglinServant mob;
    @Nullable
    public LivingEntity target;
    public int nextUseTime;

    public MutantHoglinKickAttackGoal(MutantHoglinServant mob) {
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
                && (double) this.mob.distanceTo(this.target) <= (Double) MutantHoglinCommonConfig.max_kick_attack_distance.get()
                && this.animationsUseable()
                && this.mob.hasLineOfSight(this.target);
    }

    public boolean canContinueToUse() {
        return this.target != null && !this.target.isRemoved() && !this.target.isDeadOrDying() && !this.animationsUseable();
    }

    public void start() {
        MutantHoglinServant var10000 = this.mob;
        Objects.requireNonNull(this.mob);
        var10000.kickAnimationTick = 45;
        this.mob.level().broadcastEntityEvent(this.mob, (byte)9);
    }

    public void tick() {
        this.target = this.mob.getTarget();
        this.mob.getNavigation().stop();
        if (this.target != null) {
            this.mob.lookAt(Anchor.EYES, this.target.position());
        }

        int var10000 = this.mob.kickAnimationTick;
        Objects.requireNonNull(this.mob);
        if (var10000 == 24) {
            Vec3 kickAttackBoundingBoxOffset = PositionUtils.getOffsetPos(this.mob, 0.0, 0.0, 3.25, 0.0F, this.mob.yBodyRot);
            AABB kickAttackBoundingBox = this.mob.getBoundingBox().deflate(0.0, 1.0, 0.0).move(0.0, -1.0, 0.0).inflate(1.0, 0.0, 1.0).move(kickAttackBoundingBoxOffset.x - this.mob.getX(), kickAttackBoundingBoxOffset.y - this.mob.getY(), kickAttackBoundingBoxOffset.z - this.mob.getZ());
            ShakeCameraEvent.shake(this.mob.level(), 13, 0.15F, this.mob.blockPosition(), 10);
            Iterator var3 = this.mob.level().getNearbyEntities(LivingEntity.class, TargetingConditions.forCombat(), this.mob, kickAttackBoundingBox).iterator();

            while(var3.hasNext()) {
                LivingEntity entity = (LivingEntity)var3.next();
                if (entity != this.mob && this.mob.canHarm(entity)) {
                    if ((Boolean)MutantHoglinCommonConfig.ignores_invulnerability_time.get()) {
                        entity.invulnerableTime = 0;
                    }

                    boolean flag = entity.hurt(this.mob.damageSources().mobAttack(this.mob), ((Double)MutantHoglinCommonConfig.kick_attack_damage.get()).floatValue() * this.mob.enragedDamageMultiplier());
                    Vec3 knockback = PositionUtils.getOffsetMotion(entity, 0.0, 0.0, (double)(3.0F * this.mob.enragedKnockbackMultiplier()), 0.0F, this.mob.yBodyRot);
                    entity.hurtMarked = true;
                    entity.push(knockback.x, knockback.y, knockback.z);
                    if (flag && entity == this.target) {
                        this.mob.wantsToCharge = true;
                    }

                    if (flag) {
                        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, (Integer)MutantHoglinCommonConfig.kick_slowness_length.get(), (Integer)MutantHoglinCommonConfig.kick_slowness_level.get()));
                        entity.addEffect(new MobEffectInstance((MobEffect)EffectInit.JUMPING_FATIGUE.get(), (Integer)MutantHoglinCommonConfig.kick_slowness_length.get(), 0));
                    }

                    MiscUtils.disableShield(this.target, (Integer)MutantHoglinCommonConfig.kick_disable_shield_length.get());
                }
            }
        }

    }

    public void stop() {
        super.stop();
        this.nextUseTime = this.mob.tickCount + (Integer)MutantHoglinCommonConfig.kick_cooldown.get();
    }

    public boolean animationsUseable() {
        return this.mob.kickAnimationTick <= 0;
    }
}
