package com.qiuyue.goetyominous.common.entities.ally.of.goals;

import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.common.entities.ally.of.SkyvernServant;
import com.unusualmodding.opposing_force.entity.utils.OPPoses;
import com.unusualmodding.opposing_force.registry.OPSoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

public class SkyvernServantChargeGoal extends Goal {
    private final SkyvernServant skyvern;
    private Vec3 startOrbitFrom;
    private int orbitTime;
    private int maxOrbitTime;
    private int timer;
    private int collisionTicks;
    private boolean clockwise;

    public SkyvernServantChargeGoal(SkyvernServant entity) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        this.skyvern = entity;
    }

    @Override
    public void start() {
        this.orbitTime = 0;
        this.maxOrbitTime = 40;
        this.collisionTicks = 0;
        this.startOrbitFrom = null;
        this.skyvern.setPose(Pose.STANDING);
    }

    @Override
    public void stop() {
        LivingEntity target = this.skyvern.getTarget();
        if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target)) {
            this.skyvern.setTarget(null);
        }
        this.skyvern.setAggressive(false);
        this.skyvern.getNavigation().stop();
        this.skyvern.setPose(Pose.STANDING);
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.skyvern.getTarget();
        return target != null && target.isAlive() && !this.skyvern.isPassenger();
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.skyvern.getTarget();
        if (target == null) {
            return false;
        }
        if (!target.isAlive()) {
            return false;
        }
        if (!this.skyvern.isWithinRestriction(target.blockPosition())) {
            return false;
        }
        return !(target instanceof Player) || (!target.isSpectator() && !((Player) target).isCreative()) || !this.skyvern.getNavigation().isDone();
    }

    @Override
    public void tick() {
        LivingEntity target = this.skyvern.getTarget();
        int attackState = this.skyvern.getAttackState();
        if (target != null) {
            if (this.startOrbitFrom == null) {
                this.skyvern.getNavigation().moveTo(target, 1.5D);
            } else if (this.orbitTime < this.maxOrbitTime) {
                ++this.orbitTime;
                Vec3 orbitPos = this.orbitAroundPos(48.0F);
                this.skyvern.getNavigation().moveTo(orbitPos.x, orbitPos.y, orbitPos.z, 1.5D);
            } else {
                this.orbitTime = 0;
                this.startOrbitFrom = null;
            }
            if (attackState == 1) {
                this.tickCharge();
            } else if (attackState == 0 && this.isWithinRange(target) && this.orbitTime == 0) {
                this.skyvern.setAttackState(1);
                this.skyvern.holdMoveControl();
            }
        }
    }

    private void tickCharge() {
        ++this.timer;
        LivingEntity target = this.skyvern.getTarget();
        if (this.skyvern.verticalCollisionBelow || this.skyvern.horizontalCollision || this.skyvern.verticalCollision || this.skyvern.minorHorizontalCollision) {
            ++this.collisionTicks;
        }
        if (this.timer == 7) {
            this.skyvern.setPose(OPPoses.ATTACKING.get());
            this.skyvern.playSound(OPSoundEvents.SKYVERN_CHARGE_WARN.get(), 3.0F, 0.9F + this.skyvern.getRandom().nextFloat() * 0.3F);
        }
        if (this.timer < 9) {
            this.skyvern.getNavigation().stop();
            this.skyvern.setDeltaMovement(0.0D, 0.0D, 0.0D);
            this.skyvern.setYRot(Mth.rotLerp(1.0F, this.skyvern.getYRot(), (float) (Mth.atan2(target.getZ() - this.skyvern.getZ(), target.getX() - this.skyvern.getX()) * 57.29577951308232D) - 90.0F));
            this.skyvern.getLookControl().setLookAt(target, 360.0F, 90.0F);
        }
        if (this.timer == 9) {
            this.skyvern.playSound(OPSoundEvents.SKYVERN_WHOOSH.get(), 3.0F, 0.9F + this.skyvern.getRandom().nextFloat() * 0.2F);
        }
        if (this.timer > 9) {
            Vec3 chargeDirection = new Vec3(target.getX() - this.skyvern.getX(), target.getY(), target.getZ() - this.skyvern.getZ()).normalize();
            float yRot = Mth.approachDegrees(this.skyvern.getYRot(), (float) (Mth.atan2(chargeDirection.z, chargeDirection.x) * 57.29577951308232D) - 90.0F, 0.5F);
            float speed = 2.0F;
            this.skyvern.setYRot(yRot);
            this.skyvern.setYBodyRot(yRot);
            this.skyvern.setDeltaMovement(-Mth.sin(yRot * ((float) Math.PI / 180.0F)) * speed, this.skyvern.getDeltaMovement().y, Mth.cos(yRot * ((float) Math.PI / 180.0F)) * speed);
            this.hurtNearbyEntities();
        }
        if (this.timer == 28 || this.collisionTicks > 10) {
            this.clockwise = this.skyvern.getRandom().nextBoolean();
            this.skyvern.setPose(Pose.STANDING);
            this.maxOrbitTime = 30 + this.skyvern.getRandom().nextInt(30);
            this.startOrbitFrom = target.getEyePosition();
            this.timer = 0;
            this.skyvern.setAttackState(0);
        }
    }

    public Vec3 orbitAroundPos(float circleDistance) {
        float angle = 2.0F * (float) Math.toRadians((this.clockwise ? -this.orbitTime : this.orbitTime) * 2.0F);
        double extraX = circleDistance * Mth.sin(angle);
        double extraZ = circleDistance * Mth.cos(angle);
        return this.startOrbitFrom.add(extraX, 16.0D, extraZ);
    }

    private void hurtNearbyEntities() {
        List<LivingEntity> nearbyEntities = this.skyvern.level().getNearbyEntities(LivingEntity.class, TargetingConditions.forCombat(), this.skyvern, this.skyvern.getBoundingBox().inflate(1.5D));
        if (!nearbyEntities.isEmpty()) {
            LivingEntity entity = nearbyEntities.get(0);
            if (!(entity instanceof SkyvernServant) && !MobUtil.areAllies(this.skyvern, entity)) {
                entity.hurt(entity.damageSources().mobAttack(this.skyvern), (float) this.skyvern.getAttributeValue(Attributes.ATTACK_DAMAGE));
                if (entity.isDamageSourceBlocked(this.skyvern.damageSources().mobAttack(this.skyvern)) && entity instanceof Player player) {
                    player.disableShield(true);
                }
                this.skyvern.swing(InteractionHand.MAIN_HAND);
            }
        }
    }

    private boolean isWithinRange(LivingEntity target) {
        if (target == null) {
            return false;
        }
        return Math.abs(target.getY() - this.skyvern.getY()) < 5.0D
                && this.skyvern.distanceTo(target) < 256.0F
                && !(this.skyvern.getY() < target.getY() + 0.5D);
    }
}
