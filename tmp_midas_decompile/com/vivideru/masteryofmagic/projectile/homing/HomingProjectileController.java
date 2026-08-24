/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.common.entities.projectiles.ScytheSlash
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.projectile.Projectile
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package com.vivideru.masteryofmagic.projectile.homing;

import com.Polarice3.Goety.common.entities.projectiles.ScytheSlash;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class HomingProjectileController {
    private static final boolean DEBUG = false;

    public static void execute(Projectile projectile) {
        if (projectile.m_9236_().m_5776_()) {
            return;
        }
        Level level = projectile.m_9236_();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        if (projectile instanceof ScytheSlash) {
            HomingProjectileController.removeHoming(projectile);
            return;
        }
        CompoundTag persistentData = projectile.getPersistentData();
        if (persistentData.m_128471_("GoetyMasteryHomingHitTarget")) {
            HomingProjectileController.removeHoming(projectile);
            return;
        }
        if (projectile.m_213877_()) {
            HomingProjectileController.removeHoming(projectile);
            return;
        }
        int homingLevel = persistentData.m_128451_("GoetyMasteryHomingLevel");
        if (homingLevel <= 0) {
            HomingProjectileController.debug("Projectile tick skipped: no homing tag. projectile=" + projectile.m_6095_().m_147048_() + " id=" + projectile.m_19879_());
            return;
        }
        HomingProjectileController.debug("Projectile tick: homingLevel=" + homingLevel + " projectile=" + projectile.m_6095_().m_147048_() + " id=" + projectile.m_19879_());
        Vec3 motion = projectile.m_20184_();
        if (motion.m_82556_() < 1.0E-4) {
            HomingProjectileController.debug("Projectile tick skipped: motion too small. motion=" + motion + " projectile=" + projectile.m_6095_().m_147048_() + " id=" + projectile.m_19879_());
            return;
        }
        LivingEntity target = HomingProjectileController.getOrFindTarget(serverLevel, projectile, homingLevel);
        if (target == null) {
            HomingProjectileController.debug("Projectile tick: no valid target found. projectile=" + projectile.m_6095_().m_147048_() + " id=" + projectile.m_19879_());
            persistentData.m_128473_("GoetyMasteryHomingTarget");
            return;
        }
        Vec3 currentDirection = motion.m_82541_();
        Vec3 targetPosition = target.m_20191_().m_82399_();
        Vec3 wantedDirection = targetPosition.m_82546_(projectile.m_20182_()).m_82541_();
        double turnStrength = homingLevel == 1 ? 0.175 : (homingLevel == 2 ? 0.335 : 0.525);
        double speed = motion.m_82553_();
        Vec3 newDirection = currentDirection.m_82490_(1.0 - turnStrength).m_82549_(wantedDirection.m_82490_(turnStrength)).m_82541_();
        projectile.m_20256_(newDirection.m_82490_(speed));
        projectile.f_19812_ = true;
        HomingProjectileController.debug("Projectile homing applied: target=" + target.m_6095_().m_147048_() + " targetId=" + target.m_19879_() + " speed=" + speed + " turnStrength=" + turnStrength + " oldMotion=" + motion + " newMotion=" + projectile.m_20184_());
    }

    private static LivingEntity getOrFindTarget(ServerLevel serverLevel, Projectile projectile, int homingLevel) {
        LivingEntity target;
        CompoundTag persistentData = projectile.getPersistentData();
        if (persistentData.m_128403_("GoetyMasteryHomingTarget")) {
            LivingEntity livingEntity;
            UUID uuid;
            Entity entity = serverLevel.m_8791_(uuid = persistentData.m_128342_("GoetyMasteryHomingTarget"));
            HomingProjectileController.debug("Checking stored homing target uuid=" + uuid + " found=" + (entity != null));
            if (entity instanceof LivingEntity && HomingProjectileController.isValidTarget(projectile, livingEntity = (LivingEntity)entity)) {
                HomingProjectileController.debug("Stored homing target valid: target=" + livingEntity.m_6095_().m_147048_() + " id=" + livingEntity.m_19879_());
                return livingEntity;
            }
            HomingProjectileController.debug("Stored homing target invalid, removing target tag.");
            persistentData.m_128473_("GoetyMasteryHomingTarget");
        }
        if ((target = HomingProjectileController.findTarget(projectile, homingLevel)) != null) {
            HomingProjectileController.debug("New homing target locked: target=" + target.m_6095_().m_147048_() + " id=" + target.m_19879_());
            persistentData.m_128362_("GoetyMasteryHomingTarget", target.m_20148_());
        }
        return target;
    }

    private static void removeHoming(Projectile projectile) {
        projectile.getPersistentData().m_128473_("GoetyMasteryHomingLevel");
        projectile.getPersistentData().m_128473_("GoetyMasteryHomingTarget");
    }

    private static double angleScore(Vec3 origin, Vec3 direction, Vec3 point) {
        Vec3 toPoint = point.m_82546_(origin);
        if (toPoint.m_82556_() < 1.0E-4) {
            return Double.MAX_VALUE;
        }
        double dot = direction.m_82526_(toPoint.m_82541_());
        return 1.0 - dot;
    }

    private static LivingEntity findTarget(Projectile projectile, int homingLevel) {
        double maxConeRadius;
        double startConeRadius;
        double range;
        Vec3 motion = projectile.m_20184_();
        if (motion.m_82556_() < 1.0E-4) {
            HomingProjectileController.debug("findTarget skipped: motion too small.");
            return null;
        }
        Vec3 origin = projectile.m_20182_();
        Vec3 direction = motion.m_82541_();
        if (homingLevel == 1) {
            range = 40.0;
            startConeRadius = 1.5;
            maxConeRadius = 6.0;
        } else if (homingLevel == 2) {
            range = 55.0;
            startConeRadius = 2.0;
            maxConeRadius = 8.0;
        } else {
            range = 70.0;
            startConeRadius = 2.5;
            maxConeRadius = 10.0;
        }
        AABB searchBox = projectile.m_20191_().m_82400_(range);
        List targets = projectile.m_9236_().m_6443_(LivingEntity.class, searchBox, livingEntity -> HomingProjectileController.isValidTarget(projectile, livingEntity) && HomingProjectileController.isInsideCone(origin, direction, livingEntity, range, startConeRadius, maxConeRadius));
        Optional<LivingEntity> result = targets.stream().min(Comparator.comparingDouble(livingEntity -> HomingProjectileController.distanceFromLine(origin, direction, livingEntity.m_20191_().m_82399_())).thenComparingDouble(livingEntity -> livingEntity.m_20280_((Entity)projectile)));
        if (result.isPresent()) {
            LivingEntity target = result.get();
            HomingProjectileController.debug("findTarget result: target=" + target.m_6095_().m_147048_() + " id=" + target.m_19879_() + " lineDistanceSqr=" + HomingProjectileController.distanceFromLine(origin, direction, target.m_20191_().m_82399_()) + " distanceSqr=" + target.m_20280_((Entity)projectile));
        } else {
            HomingProjectileController.debug("findTarget result: none.");
        }
        return result.orElse(null);
    }

    private static boolean isValidTarget(Projectile projectile, LivingEntity target) {
        if (!target.m_6084_()) {
            HomingProjectileController.debug("Invalid target: not alive. entity=" + target.m_6095_().m_147048_() + " id=" + target.m_19879_());
            return false;
        }
        if (target.m_5833_()) {
            HomingProjectileController.debug("Invalid target: spectator. entity=" + target.m_6095_().m_147048_() + " id=" + target.m_19879_());
            return false;
        }
        Entity owner = projectile.m_19749_();
        if (target == owner) {
            HomingProjectileController.debug("Invalid target: target is owner. entity=" + target.m_6095_().m_147048_() + " id=" + target.m_19879_());
            return false;
        }
        if (owner instanceof LivingEntity) {
            LivingEntity livingOwner = (LivingEntity)owner;
            if (livingOwner.m_7307_((Entity)target)) {
                HomingProjectileController.debug("Invalid target: owner allied to target. entity=" + target.m_6095_().m_147048_() + " id=" + target.m_19879_());
                return false;
            }
            if (target.m_7307_((Entity)livingOwner)) {
                HomingProjectileController.debug("Invalid target: target allied to owner. entity=" + target.m_6095_().m_147048_() + " id=" + target.m_19879_());
                return false;
            }
            boolean lineOfSight = livingOwner.m_142582_((Entity)target);
            if (!lineOfSight) {
                HomingProjectileController.debug("Invalid target: owner has no line of sight. entity=" + target.m_6095_().m_147048_() + " id=" + target.m_19879_());
            }
            return lineOfSight;
        }
        return true;
    }

    private static boolean isInsideCone(Vec3 origin, Vec3 direction, LivingEntity target, double range, double startConeRadius, double maxConeRadius) {
        double coneProgress;
        double allowedRadius;
        Vec3 targetCenter = target.m_20191_().m_82399_();
        Vec3 toTarget = targetCenter.m_82546_(origin);
        if (toTarget.m_82556_() < 1.0E-4) {
            HomingProjectileController.debug("Cone check failed: target too close to origin. entity=" + target.m_6095_().m_147048_() + " id=" + target.m_19879_());
            return false;
        }
        double projectedDistance = toTarget.m_82526_(direction);
        if (projectedDistance <= 0.0) {
            HomingProjectileController.debug("Cone check failed: target behind projectile. entity=" + target.m_6095_().m_147048_() + " id=" + target.m_19879_() + " projectedDistance=" + projectedDistance);
            return false;
        }
        if (projectedDistance > range) {
            HomingProjectileController.debug("Cone check failed: target too far forward. entity=" + target.m_6095_().m_147048_() + " id=" + target.m_19879_() + " projectedDistance=" + projectedDistance + " range=" + range);
            return false;
        }
        Vec3 closestPointOnLine = origin.m_82549_(direction.m_82490_(projectedDistance));
        double distanceFromLine = targetCenter.m_82554_(closestPointOnLine);
        boolean result = distanceFromLine <= (allowedRadius = startConeRadius + (maxConeRadius - startConeRadius) * (coneProgress = projectedDistance / range));
        HomingProjectileController.debug("Cone check: entity=" + target.m_6095_().m_147048_() + " id=" + target.m_19879_() + " projectedDistance=" + projectedDistance + " distanceFromLine=" + distanceFromLine + " allowedRadius=" + allowedRadius + " result=" + result);
        return result;
    }

    private static double distanceFromLine(Vec3 origin, Vec3 direction, Vec3 point) {
        Vec3 toPoint = point.m_82546_(origin);
        double projectedLength = toPoint.m_82526_(direction);
        Vec3 closestPoint = origin.m_82549_(direction.m_82490_(projectedLength));
        return point.m_82557_(closestPoint);
    }

    private static void debug(String message) {
    }
}

