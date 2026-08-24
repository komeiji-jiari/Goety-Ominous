package com.qiuyue.goetyominous.common.entities.ally.of.goals;

import com.qiuyue.goetyominous.common.entities.ally.of.DicerServant;
import com.unusualmodding.opposing_force.entity.utils.OPPoses;
import com.unusualmodding.opposing_force.registry.OPSoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;

import java.util.List;

/**
 * 复刻原版 DicerAttackGoal：攻击状态 1=斩击、2=尾旋、3=十字斩冲刺。
 * 继承仆从自己的 RamblerServantAttackGoal（等效于原版 OF AttackGoal 基类）。
 */
public class DicerServantAttackGoal extends RamblerServantAttackGoal {
    private final DicerServant dicer;

    public DicerServantAttackGoal(DicerServant dicer) {
        super(dicer);
        this.dicer = dicer;
    }

    public boolean canUse() {
        return super.canUse() && this.dicer.laserCooldown > 0 && this.dicer.getPose() == Pose.STANDING;
    }

    public boolean canContinueToUse() {
        return super.canContinueToUse() && this.dicer.laserCooldown > 0;
    }

    public void start() {
        super.start();
        this.dicer.setRunning(true);
        this.dicer.setPose(Pose.STANDING);
    }

    public void stop() {
        super.stop();
        this.dicer.setRunning(false);
        this.dicer.setPose(Pose.STANDING);
    }

    public void tick() {
        LivingEntity target = this.dicer.getTarget();
        if (target != null) {
            double distance = this.dicer.distanceToSqr(target.getX(), target.getY(), target.getZ());
            int attackState = this.dicer.getAttackState();
            if (attackState != 3) {
                this.dicer.lookAt(target, 30.0F, 30.0F);
                this.dicer.getLookControl().setLookAt(target, 30.0F, 30.0F);
            }

            if (attackState == 1) {
                ++this.timer;
                this.dicer.getNavigation().moveTo(target, 1.3);
                if (this.timer == 1) {
                    this.dicer.setPose(OPPoses.SLASHING.get());
                }

                if (this.timer == 9 && this.isInAttackRange(target, 1.5)) {
                    this.dicer.doHurtTarget(target);
                    this.dicer.swing(InteractionHand.MAIN_HAND);
                }

                if (this.timer > 20) {
                    this.timer = 0;
                    this.dicer.setAttackState(0);
                    this.dicer.slashCooldown = 12;
                }
            } else if (attackState == 2) {
                ++this.timer;
                this.dicer.getNavigation().moveTo(target, 2.0);
                if (this.timer == 1) {
                    this.dicer.setPose(OPPoses.TAIL_SPINNING.get());
                }

                if (this.timer == 8 && this.isInAttackRange(target, 1.25)) {
                    this.dicer.doHurtTarget(target);
                    this.dicer.swing(InteractionHand.MAIN_HAND);
                }

                if (this.timer > 20) {
                    this.timer = 0;
                    this.dicer.setAttackState(0);
                    this.dicer.tailSpinCooldown = 50 + this.dicer.getRandom().nextInt(20);
                }
            } else if (attackState == 3) {
                ++this.timer;
                this.dicer.getNavigation().stop();
                if (this.timer == 1) {
                    this.dicer.setPose(OPPoses.CROSS_SLASHING.get());
                }

                if (this.timer < 26) {
                    this.dicer.lookAt(target, 30.0F, 30.0F);
                    this.dicer.getLookControl().setLookAt(target, 30.0F, 30.0F);
                }

                if (this.timer == 28) {
                    this.dicer.setDeltaMovement(this.dicer.getLookAngle().scale(3.25).multiply(1.0, 0.0, 1.0));
                }

                if (this.timer > 28 && this.timer < 32) {
                    this.hurtNearbyEntities();
                }

                if (this.timer > 50) {
                    this.timer = 0;
                    this.dicer.setAttackState(0);
                    this.dicer.crossSlashCooldown = 80 + this.dicer.getRandom().nextInt(50);
                }
            } else {
                // 原版：待机时无条件先追击目标，靠近到攻击范围才随机选招
                this.dicer.getNavigation().moveTo(target, 2.0);
                if (distance < this.getAttackReachSqr(target)) {
                    if (this.dicer.getRandom().nextFloat() < 0.25F && this.dicer.crossSlashCooldown == 0) {
                        this.dicer.setAttackState(3);
                    } else if (this.dicer.getRandom().nextFloat() < 0.5F && this.dicer.tailSpinCooldown == 0) {
                        this.dicer.setAttackState(2);
                    } else if (this.dicer.slashCooldown == 0) {
                        this.dicer.setAttackState(1);
                    }
                }
            }
        }
    }

    private void hurtNearbyEntities() {
        List<LivingEntity> list = this.dicer.level().getNearbyEntities(LivingEntity.class, TargetingConditions.forCombat(), this.dicer, this.dicer.getBoundingBox().inflate(2.0));
        if (list.isEmpty()) {
            return;
        }

        LivingEntity entity = list.get(0);
        if (entity instanceof DicerServant) {
            return;
        }

        if (entity.hurt(entity.damageSources().mobAttack(this.dicer), (float)this.dicer.getAttributeValue(Attributes.ATTACK_DAMAGE))) {
            this.dicer.playSound((SoundEvent)OPSoundEvents.DICER_ATTACK.get(), 1.0F, 1.0F / (this.dicer.getRandom().nextFloat() * 0.4F + 0.8F));
            if (this.dicer.isElite()) {
                entity.setSecondsOnFire(5);
            }

            entity.knockback(0.3, this.dicer.position().x - entity.getX(), this.dicer.position().z - entity.getZ());
            if (entity.isDamageSourceBlocked(entity.damageSources().mobAttack(this.dicer)) && entity instanceof Player) {
                ((Player)entity).disableShield(true);
            }

            this.dicer.swing(InteractionHand.MAIN_HAND);
        }
    }
}
