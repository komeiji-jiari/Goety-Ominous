package com.qiuyue.goetyominous.common.entities.ally.of.goals;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.common.entities.ally.of.TerrorServant;
import com.unusualmodding.opposing_force.entity.utils.OPPoses;
import com.unusualmodding.opposing_force.registry.OPSoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class TerrorServantAttackGoal extends RamblerServantAttackGoal {
    private final TerrorServant terror;
    private int cooldown;

    public TerrorServantAttackGoal(TerrorServant terror) {
        super(terror);
        this.terror = terror;
    }

    @Override
    public boolean canUse() {
        return super.canUse() && this.terror.getPose() == Pose.STANDING;
    }

    @Override
    public void start() {
        super.start();
        this.cooldown = 0;
        this.terror.setSawing(false);
        this.terror.setRunning(false);
        this.terror.setPose(Pose.STANDING);
    }

    @Override
    public void stop() {
        super.stop();
        this.cooldown = 0;
        this.terror.setSawing(false);
        this.terror.setRunning(false);
        this.terror.setPose(Pose.STANDING);
    }

    @Override
    public void tick() {
        LivingEntity target = this.terror.getTarget();
        if (target == null) {
            return;
        }

        double distance = this.terror.distanceToSqr(target.getX(), target.getY(), target.getZ());
        this.terror.lookAt(target, 30.0F, 30.0F);
        this.terror.getLookControl().setLookAt(target, 30.0F, 30.0F);

        if (this.terror.isSawing()) {
            ++this.timer;
            if (this.timer == 1) {
                this.terror.playSound(OPSoundEvents.TERROR_SAW_START.get(), 1.0F, 1.0F);
                this.terror.setPose(OPPoses.START_SAWING.get());
            }

            if (this.timer < 20) {
                this.terror.getNavigation().stop();
            }

            if (this.timer > 20 && this.timer < 100) {
                this.terror.getNavigation().moveTo(target, 1.5D);
                this.terror.setRunning(true);
                this.hurtNearbyEntities();
            }

            if (this.timer == 100) {
                this.terror.setPose(OPPoses.RECOVERING.get());
                this.terror.playSound(OPSoundEvents.TERROR_SAW_END.get(), 1.0F, 1.0F);
            }

            if (this.timer > 100) {
                this.terror.getNavigation().moveTo(target, 0.75D);
                this.terror.setRunning(false);
            }

            if (this.timer > 150) {
                this.timer = 0;
                this.terror.setSawing(false);
                this.cooldown = 5 + this.terror.getRandom().nextInt(10);
            }
        } else {
            if (this.cooldown > 0) {
                --this.cooldown;
            }

            this.terror.getNavigation().moveTo(target, 1.25D);
            if (distance < this.getAttackReachSqr(target) && this.terror.getPose() == Pose.STANDING && this.cooldown == 0) {
                this.terror.setSawing(true);
            }
        }
    }

    private void hurtNearbyEntities() {
        List<LivingEntity> list = this.terror.level().getNearbyEntities(LivingEntity.class, TargetingConditions.forCombat(),
                this.terror, this.terror.getBoundingBox().inflate(1.1D));
        for (LivingEntity entity : list) {
            if (this.isFriendly(entity)) {
                continue;
            }

            if (entity.hurt(this.terror.getServantAttack(), (float) this.terror.getAttributeValue(Attributes.ATTACK_DAMAGE))) {
                if (entity.isDamageSourceBlocked(this.terror.getServantAttack()) && entity instanceof Player player) {
                    player.disableShield(true);
                }

                this.terror.swing(InteractionHand.MAIN_HAND);
            }
            return;
        }
    }

    private boolean isFriendly(LivingEntity victim) {
        if (victim instanceof TerrorServant) {
            return true;
        }
        LivingEntity owner = this.terror.getTrueOwner();
        if (owner == null) {
            return false;
        }
        if (victim == owner) {
            return true;
        }
        if (victim instanceof IOwned owned
                && (owned.getTrueOwner() == owner || MobUtil.ownerStack(owned, this.terror))) {
            return true;
        }
        return victim instanceof OwnableEntity ownable && ownable.getOwner() == owner;
    }

    @Override
    protected double getAttackReachSqr(LivingEntity target) {
        return this.terror.getBbWidth() * 3.0F * this.terror.getBbWidth() * 3.0F + target.getBbWidth();
    }
}
