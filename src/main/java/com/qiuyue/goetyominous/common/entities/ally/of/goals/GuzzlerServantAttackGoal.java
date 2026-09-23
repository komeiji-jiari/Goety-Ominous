package com.qiuyue.goetyominous.common.entities.ally.of.goals;

import com.Polarice3.Goety.common.entities.util.CameraShake;
import com.qiuyue.goetyominous.common.entities.ally.of.FireSlimeServant;
import com.qiuyue.goetyominous.common.entities.ally.of.GuzzlerServant;
import com.qiuyue.goetyominous.common.init.of.OfEntityRegistry;
import com.unusualmodding.opposing_force.registry.OPSoundEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.EnumSet;

public class GuzzlerServantAttackGoal extends Goal {
    private static final int FIRE_SLIME_LIFE = 250;

    private final GuzzlerServant guzzler;
    private final float maxAttackDistance;
    private int timer;
    private int seeTime;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;
    private int spewCooldown;

    public GuzzlerServantAttackGoal(GuzzlerServant guzzler, float maxAttackDistance) {
        this.guzzler = guzzler;
        this.maxAttackDistance = maxAttackDistance * maxAttackDistance;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.guzzler.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.guzzler.getTarget();
        if (target == null) {
            return false;
        }
        if (!target.isAlive()) {
            return false;
        }
        if (!this.guzzler.isWithinRestriction(target.blockPosition())) {
            return false;
        }
        if (target instanceof Player player && (player.isSpectator() || player.isCreative())) {
            return !this.guzzler.getNavigation().isDone();
        }
        return true;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void start() {
        this.guzzler.setAggressive(true);
        this.timer = 0;
        this.guzzler.setAttackState(0);
    }

    @Override
    public void stop() {
        LivingEntity target = this.guzzler.getTarget();
        if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target)) {
            this.guzzler.setTarget(null);
        }
        this.guzzler.setAggressive(false);
        this.guzzler.getNavigation().stop();
        this.guzzler.setAttackState(0);
    }

    @Override
    public void tick() {
        LivingEntity target = this.guzzler.getTarget();
        if (target == null) {
            return;
        }
        double distance = this.guzzler.distanceToSqr(target.getX(), target.getY(), target.getZ());
        boolean hasLineOfSight = this.guzzler.hasLineOfSight(target);
        boolean seenBefore = this.seeTime > 0;
        if (hasLineOfSight != seenBefore) {
            this.seeTime = 0;
        }
        this.seeTime = hasLineOfSight ? ++this.seeTime : --this.seeTime;
        if (distance <= (double) this.maxAttackDistance && this.seeTime >= 20) {
            this.guzzler.getNavigation().stop();
            ++this.strafingTime;
        } else {
            this.guzzler.getNavigation().moveTo(target, 0.5D);
            this.strafingTime = -1;
        }
        if (this.strafingTime >= 20) {
            if ((double) this.guzzler.getRandom().nextFloat() < 0.3D) {
                this.strafingClockwise = !this.strafingClockwise;
            }
            if ((double) this.guzzler.getRandom().nextFloat() < 0.3D) {
                this.strafingBackwards = !this.strafingBackwards;
            }
            this.strafingTime = 0;
        }
        if (this.strafingTime > -1) {
            if (distance > (double) (this.maxAttackDistance * 0.75F)) {
                this.strafingBackwards = false;
            } else if (distance < (double) (this.maxAttackDistance * 0.25F)) {
                this.strafingBackwards = true;
            }
            if (this.guzzler.getAttackState() != 2) {
                this.guzzler.getMoveControl().strafe(this.strafingBackwards ? -0.5F : 0.5F, this.strafingClockwise ? 0.5F : -0.5F);
            }
            this.guzzler.lookAt(target, 30.0F, 30.0F);
        } else {
            this.guzzler.getLookControl().setLookAt(target, 30.0F, 30.0F);
        }
        if (this.guzzler.getAttackState() == 1) {
            this.tickSpewAttack();
        } else if (this.guzzler.getAttackState() == 2) {
            this.tickSlamAttack();
        } else {
            --this.spewCooldown;
            if (this.spewCooldown <= 0) {
                this.guzzler.setAttackState(1);
            } else if (distance < this.getAttackReachSqr(target)) {
                this.guzzler.setAttackState(2);
            }
        }
    }

    private void tickSpewAttack() {
        ++this.timer;
        this.spewCooldown = 28;
        LivingEntity target = this.guzzler.getTarget();
        if (target != null && this.timer == 7) {
            FireSlimeServant slime = OfEntityRegistry.FIRE_SLIME_SERVANT.get().create(this.guzzler.level());
            if (slime != null) {
                slime.setTrueOwner(this.guzzler);
                slime.moveTo(this.guzzler.getX(), this.guzzler.getEyeY(), this.guzzler.getZ(),
                        this.guzzler.getYRot(), this.guzzler.getXRot());
                double d0 = target.getEyeY() - 1.1D;
                double d1 = target.getX() - this.guzzler.getX();
                double d2 = d0 - slime.getY();
                double d3 = target.getZ() - this.guzzler.getZ();
                float f3 = Mth.sqrt((float) (d1 * d1 + d2 * d2 + d3 * d3)) * 0.23F;
                this.guzzler.gameEvent(GameEvent.ENTITY_ROAR);
                this.guzzler.playSound(OPSoundEvents.GUZZLER_SPEW.get(), 2.0F,
                        1.0F / (this.guzzler.getRandom().nextFloat() * 0.4F + 0.8F));
                float speed = 0.1F + 0.005F * (float) this.guzzler.distanceToSqr(target.getX(), target.getY(), target.getZ());
                slime.shootFromOwner(d1, d2 + (double) f3, d3, Mth.clamp(speed, 0.1F, 1.25F));
                slime.setYRot(this.guzzler.getYRot() % 360.0F);
                slime.setXRot(Mth.clamp(this.guzzler.getYRot(), -90.0F, 90.0F) % 360.0F);
                if (target.isAlive() && !target.isPassengerOfSameVehicle(this.guzzler) && !target.isPassengerOfSameVehicle(slime)) {
                    slime.setTarget(target);
                }
                slime.setLimitedLife(FIRE_SLIME_LIFE);
                if (this.guzzler.level() instanceof ServerLevel serverLevel) {
                    ForgeEventFactory.onFinalizeSpawn(slime, serverLevel,
                            serverLevel.getCurrentDifficultyAt(this.guzzler.blockPosition()),
                            MobSpawnType.MOB_SUMMONED, null, null);
                    serverLevel.addFreshEntity(slime);
                }
            }
        }
        if (this.timer > 3 && this.timer < 9) {
            this.guzzler.level().broadcastEntityEvent(this.guzzler, (byte) 39);
        }
        if (this.timer > 15) {
            this.timer = 0;
            this.guzzler.setAttackState(0);
        }
    }

    private void tickSlamAttack() {
        ++this.timer;
        this.guzzler.getNavigation().stop();
        this.guzzler.setDeltaMovement(0.0D, this.guzzler.getDeltaMovement().y, 0.0D);
        if (this.timer == 31) {
            for (LivingEntity entity : this.guzzler.level().getEntitiesOfClass(LivingEntity.class,
                    this.guzzler.getBoundingBox().inflate(3.5D))) {
                if (entity == this.guzzler || this.guzzler.isAlliedTo(entity)) {
                    continue;
                }
                boolean reachable = entity.getY() > this.guzzler.getY()
                        && (double) entity.distanceTo(this.guzzler) > 3.5D;
                if (reachable) {
                    continue;
                }
                Vec3 vec3 = this.guzzler.position().add(0.0D, 1.5D, 0.0D);
                Vec3 vec32 = entity.getEyePosition().subtract(vec3);
                Vec3 vec33 = vec32.normalize();
                this.guzzler.doHurtTarget(entity);
                double knockbackResistanceY = 0.25D * (1.0D - entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                double knockbackResistance = 2.0D * (1.0D - entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                entity.push(vec33.x * knockbackResistance, vec33.y * knockbackResistanceY, vec33.z * knockbackResistance);
            }
            this.guzzler.playSound(OPSoundEvents.GUZZLER_SLAM.get(), 2.0F,
                    1.0F / (this.guzzler.getRandom().nextFloat() * 0.4F + 0.8F));
            CameraShake.cameraShake(this.guzzler.level(), this.guzzler.position(), 16.0F, 0.12F, 0, 20);
            this.guzzler.level().broadcastEntityEvent(this.guzzler, (byte) 40);
        }
        if (this.timer > 60) {
            this.timer = 0;
            this.guzzler.setAttackState(0);
        }
    }

    private double getAttackReachSqr(LivingEntity target) {
        return (double) (this.guzzler.getBbWidth() * 2.0F * this.guzzler.getBbWidth() * 2.0F + target.getBbWidth());
    }
}
