package com.qiuyue.goetyominous.common.entities.ally.of.goals;

import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.common.entities.ally.of.TremblerServant;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Objects;

public class TremblerServantRollGoal extends RamblerServantAttackGoal {
    protected final TremblerServant trembler;
    private Vec3 rollDirection;

    public TremblerServantRollGoal(TremblerServant trembler) {
        super(trembler);
        this.rollDirection = Vec3.ZERO;
        this.trembler = trembler;
    }

    @Override
    public boolean canUse() {
        return super.canUse() && this.trembler.getStunnedTicks() <= 0;
    }

    @Override
    public void start() {
        super.start();
        this.trembler.setRolling(false);
        this.trembler.setSprinting(false);
    }

    @Override
    public void stop() {
        super.stop();
        this.trembler.setRolling(false);
        this.trembler.setSprinting(false);
    }

    @Override
    public void tick() {
        LivingEntity target = this.trembler.getTarget();
        if (target != null) {
            double distanceSqr = this.trembler.distanceToSqr(target.getX(), target.getY(), target.getZ());
            float f = 0.15F * (float) (this.getSpeedEffect(MobEffects.MOVEMENT_SPEED) - this.getSpeedEffect(MobEffects.MOVEMENT_SLOWDOWN));
            BlockPos blockPos = this.trembler.blockPosition();

            if (this.trembler.isRolling()) {
                ++this.timer;
                this.trembler.getNavigation().stop();

                if (this.timer < 12) {
                    this.trembler.lookAt(target, 360.0F, 30.0F);
                    this.trembler.getLookControl().setLookAt(target, 30.0F, 30.0F);
                }

                if (this.timer == 12) {
                    Vec3 targetPos = target.position();
                    this.rollDirection = new Vec3(
                            (double) blockPos.getX() - targetPos.x,
                            0.0D,
                            (double) blockPos.getZ() - targetPos.z).normalize();
                    this.trembler.setSprinting(true);
                }

                if (this.timer >= 12) {
                    this.trembler.setDeltaMovement(
                            this.rollDirection.x * (-0.6D - (double) f),
                            this.trembler.getDeltaMovement().y,
                            this.rollDirection.z * (-0.6D - (double) f));
                    this.tryToHurt();
                }

                if (this.timer > 53 || this.trembler.horizontalCollision) {
                    this.trembler.setSprinting(false);
                    this.trembler.getNavigation().stop();
                    this.rollDirection = Vec3.ZERO;
                }

                if (this.timer > 69 || this.trembler.horizontalCollision) {
                    this.timer = 0;
                    this.trembler.setRolling(false);
                    this.trembler.rollCooldown();
                }
            } else {
                if (distanceSqr < 80.0D && this.trembler.getRollCooldown() <= 0
                        && this.trembler.isWithinYRange(target) && this.trembler.onGround()) {
                    this.trembler.setRolling(true);
                } else if (distanceSqr < 16.0D) {
                    this.trembler.getNavigation().moveTo(target, 1.0);
                } else {
                    this.trembler.getNavigation().moveTo(target, 1.4);
                }
                this.trembler.lookAt(Objects.requireNonNull(target), 30.0F, 30.0F);
                this.trembler.getLookControl().setLookAt(target, 30.0F, 30.0F);
            }
        }
    }

    private int getSpeedEffect(MobEffect effect) {
        return this.trembler.hasEffect(effect) ? this.trembler.getEffect(effect).getAmplifier() + 1 : 0;
    }

    private void tryToHurt() {
        List<LivingEntity> list = this.trembler.level().getEntitiesOfClass(
                LivingEntity.class, this.trembler.getBoundingBox(),
                living -> TargetingConditions.forCombat().test(this.trembler, living));
        if (!list.isEmpty()) {
            LivingEntity victim = list.get(0);
            if (victim instanceof TremblerServant) {
                return;
            }
            if (MobUtil.areAllies(this.trembler, victim)) {
                return;
            }

            float f = 0.15F * (float) (this.getSpeedEffect(MobEffects.MOVEMENT_SPEED) - this.getSpeedEffect(MobEffects.MOVEMENT_SLOWDOWN));
            float dmg = Mth.clamp(this.trembler.getSpeed() * 1.65F, 0.2F, 3.0F) + f;
            boolean blocked = victim.isBlocking();
            float knockback = blocked ? 1.75F : 2.25F;

            victim.hurt(victim.damageSources().mobAttack(this.trembler),
                    (float) this.trembler.getAttributeValue(Attributes.ATTACK_DAMAGE));
            victim.push((double) (knockback * dmg * 1.5F), this.rollDirection.x, this.rollDirection.z);
            if (blocked && victim instanceof Player player) {
                player.disableShield(true);
            }
            this.trembler.swing(InteractionHand.MAIN_HAND);
        }
    }
}
