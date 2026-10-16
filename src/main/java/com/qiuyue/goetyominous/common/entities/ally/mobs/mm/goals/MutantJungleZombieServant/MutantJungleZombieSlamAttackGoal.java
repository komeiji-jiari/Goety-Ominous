package com.qiuyue.goetyominous.common.entities.ally.mobs.mm.goals.MutantJungleZombieServant;

import com.alexander.mutantmore.config.mutant_jungle_zombie.MutantJungleZombieCommonConfig;
import com.alexander.mutantmore.util.MiscUtils;
import com.alexander.mutantmore.util.PositionUtils;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.MutantJungleZombieServant;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class MutantJungleZombieSlamAttackGoal extends Goal {
    public MutantJungleZombieServant mob;
    @Nullable
    public LivingEntity target;
    public int nextUseTime;
    public boolean hitTarget;

    public MutantJungleZombieSlamAttackGoal(MutantJungleZombieServant mob) {
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
                && this.mob.notCurrentlyPlayingKeyframeAnimation()
                && this.mob.tickCount >= this.nextUseTime
                && !this.target.isRemoved()
                && !this.target.isDeadOrDying()
                && (double) this.mob.distanceTo(this.target) <= (Double) MutantJungleZombieCommonConfig.max_slam_attack_distance.get()
                && this.animationsUseable()
                && this.mob.hasLineOfSight(this.target);
    }

    public boolean canContinueToUse() {
        return !this.mob.shouldBeStationary() && this.target != null && !this.target.isRemoved() && !this.target.isDeadOrDying() && (this.mob.trySlamAnimationTick > 0 || this.mob.slamAnimationTick > 0);
    }

    public void start() {
        this.mob.trySlamAnimationTick = 30;
        this.mob.level().broadcastEntityEvent(this.mob, (byte) 15);
        this.hitTarget = false;
    }

    public void tick() {
        this.target = this.mob.getTarget();
        this.mob.getNavigation().stop();
        if (this.target != null && this.mob.trySlamAnimationTick >= 24) {
            this.mob.lookAt(Anchor.EYES, this.target.position());
            Vec3 armPos = PositionUtils.getOffsetPos(this.mob, 0.0, (double) this.mob.getEyeHeight(), 0.0, 0.0F, this.mob.yBodyRot);
            double d4 = this.target.getX() - armPos.x;
            double d5 = this.target.getEyeY() - armPos.y;
            double d6 = this.target.getZ() - armPos.z;
            double d7 = Math.sqrt(d4 * d4 + d6 * d6);
            float f1 = (float) (-(Mth.atan2(d5, d7) * 57.2957763671875));
            this.mob.setGrabArmXRot(f1);
        }
        if (this.target != null && this.mob.trySlamAnimationTick <= this.mob.trySlamAnimationActionPointStart && this.mob.trySlamAnimationTick >= this.mob.trySlamAnimationActionPointStop) {
            int distance = ((Double) MutantJungleZombieCommonConfig.max_slam_attack_grab_distance.get()).intValue();
            for (int i = 0; i < distance; ++i) {
                Vec3 boundingBoxPos = PositionUtils.getOffsetPos(this.mob, 0.0, (double) this.mob.getEyeHeight(), (double) i, this.mob.getGrabArmXRot(), this.mob.yBodyRot);
                AABB boundingBox = new AABB(boundingBoxPos.add(-0.5, -1.5, -0.5), boundingBoxPos.add(0.5, 0.5, 0.5));
                if (!boundingBox.intersects(this.target.getBoundingBox())) continue;
                this.hitTarget = true;
                this.mob.trySlamAnimationTick = 0;
                this.mob.slamAnimationTick = 80;
                this.mob.level().broadcastEntityEvent(this.mob, (byte) 16);
                this.target.hurtMarked = true;
                Vec3 targetPos = PositionUtils.getOffsetPos(this.mob, -1.0, 0.5, 2.0, 0.0F, this.mob.yBodyRot);
                this.target.setPos(targetPos);
                this.target.setDeltaMovement(0.0, 0.0, 0.0);
                if (this.target instanceof ServerPlayer) {
                    ((ServerPlayer) this.target).teleportTo(targetPos.x, targetPos.y, targetPos.z);
                }
            }
        }
        if (this.target != null && this.mob.slamAnimationTick > 0) {
            if (this.target.isPassenger()) {
                this.target.stopRiding();
            }
            if (this.mob.slamAnimationTick == 68 || this.mob.slamAnimationTick == 57 || this.mob.slamAnimationTick == 46 || this.mob.slamAnimationTick == 36) {
                if (MutantJungleZombieCommonConfig.ignores_invulnerability_time.get()) {
                    this.target.invulnerableTime = 0;
                }
                this.target.hurt(this.mob.damageSources().flyIntoWall(), ((Double) MutantJungleZombieCommonConfig.slam_attack_damage.get()).floatValue());
            }
            if (this.mob.slamAnimationTick == 24 && (double) this.mob.distanceTo(this.target) <= (Double) MutantJungleZombieCommonConfig.max_punch_attack_distance.get() + 2.0) {
                if (MutantJungleZombieCommonConfig.ignores_invulnerability_time.get()) {
                    this.target.invulnerableTime = 0;
                }
                if (this.mob.doHurtTarget(this.target)) {
                    this.target.addEffect(new MobEffectInstance(MobEffects.POISON, (Integer) MutantJungleZombieCommonConfig.punch_attack_poison_length.get(), (Integer) MutantJungleZombieCommonConfig.punch_attack_poison_level.get()));
                }
                MiscUtils.disableShield(this.target, (Integer) MutantJungleZombieCommonConfig.punch_attack_disable_shield_length.get());
                Vec3 knockback = PositionUtils.getOffsetMotion(this.target, 0.0, 0.0, 0.4, 0.0F, this.mob.yBodyRot);
                this.target.hurtMarked = true;
                this.target.push(knockback.x, knockback.y, knockback.z);
            }
            if (this.mob.slamAnimationTick == 78) {
                Vec3 targetPos = PositionUtils.getOffsetPos(this.mob, -1.0, 0.5, 2.0, 0.0F, this.mob.yBodyRot);
                this.target.setPos(targetPos);
                this.target.setDeltaMovement(0.0, 0.0, 0.0);
                if (this.target instanceof ServerPlayer) {
                    ((ServerPlayer) this.target).teleportTo(targetPos.x, targetPos.y, targetPos.z);
                }
                Vec3 motion = PositionUtils.getOffsetMotion(this.mob, 0.0, 1.0, -0.25, 0.0F, this.mob.yBodyRot);
                this.target.hurtMarked = true;
                this.target.setDeltaMovement(motion);
            }
            if (this.mob.slamAnimationTick == 71) {
                Vec3 motion = PositionUtils.getOffsetMotion(this.mob, -0.5, -2.0, 0.5, 0.0F, this.mob.yBodyRot);
                this.target.hurtMarked = true;
                this.target.setDeltaMovement(motion);
            }
            if (this.mob.slamAnimationTick == 68) {
                Vec3 motion = PositionUtils.getOffsetMotion(this.mob, 0.25, 1.0, -0.5, 0.0F, this.mob.yBodyRot);
                this.target.hurtMarked = true;
                this.target.setDeltaMovement(motion);
            }
            if (this.mob.slamAnimationTick == 61) {
                Vec3 motion = PositionUtils.getOffsetMotion(this.mob, 0.25, -2.0, 0.75, 0.0F, this.mob.yBodyRot);
                this.target.hurtMarked = true;
                this.target.setDeltaMovement(motion);
            }
            if (this.mob.slamAnimationTick == 57) {
                Vec3 motion = PositionUtils.getOffsetMotion(this.mob, -0.25, 1.1, -0.75, 0.0F, this.mob.yBodyRot);
                this.target.hurtMarked = true;
                this.target.setDeltaMovement(motion);
            }
            if (this.mob.slamAnimationTick == 50) {
                Vec3 motion = PositionUtils.getOffsetMotion(this.mob, 0.0, -2.0, 0.75, 0.0F, this.mob.yBodyRot);
                this.target.hurtMarked = true;
                this.target.setDeltaMovement(motion);
            }
            if (this.mob.slamAnimationTick == 46) {
                Vec3 motion = PositionUtils.getOffsetMotion(this.mob, 0.0, 1.2, -1.25, 0.0F, this.mob.yBodyRot);
                this.target.hurtMarked = true;
                this.target.setDeltaMovement(motion);
            }
            if (this.mob.slamAnimationTick == 39) {
                Vec3 motion = PositionUtils.getOffsetMotion(this.mob, 0.5, -2.0, 1.25, 0.0F, this.mob.yBodyRot);
                this.target.hurtMarked = true;
                this.target.setDeltaMovement(motion);
            }
        }
    }

    public void stop() {
        super.stop();
        int randomAddedCooldown = (Integer) MutantJungleZombieCommonConfig.max_slam_attack_cooldown.get() - (Integer) MutantJungleZombieCommonConfig.min_slam_attack_cooldown.get() <= 0 ? 0 : this.mob.getRandom().nextInt((Integer) MutantJungleZombieCommonConfig.max_slam_attack_cooldown.get() - (Integer) MutantJungleZombieCommonConfig.min_slam_attack_cooldown.get());
        int cooldown = (Integer) MutantJungleZombieCommonConfig.min_slam_attack_cooldown.get() + randomAddedCooldown;
        this.nextUseTime = this.mob.tickCount + cooldown;
        this.mob.failedAttacks = !this.hitTarget ? ++this.mob.failedAttacks : 0;
    }

    public boolean animationsUseable() {
        return this.mob.trySlamAnimationTick <= 0;
    }
}
