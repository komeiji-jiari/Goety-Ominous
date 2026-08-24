/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.damagesource.DamageType
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package com.vivideru.masteryofmagic.util;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class AreaAttackUtil {
    private AreaAttackUtil() {
    }

    public static int attackInFront(Entity attacker, int damage, ResourceKey<DamageType> damageType, float minDistance, float maxDistance, float wideness) {
        if (attacker == null) {
            return 0;
        }
        Level level = attacker.m_9236_();
        if (!(level instanceof ServerLevel)) {
            return 0;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        if (maxDistance <= minDistance) {
            return 0;
        }
        if (wideness <= 0.0f) {
            return 0;
        }
        Vec3 origin = attacker.m_146892_();
        Vec3 forward = attacker.m_20154_().m_82541_();
        Vec3 start = origin.m_82549_(forward.m_82490_((double)minDistance));
        Vec3 end = origin.m_82549_(forward.m_82490_((double)maxDistance));
        double halfWidth = (double)wideness * 0.5;
        AABB searchBox = new AABB(start, end).m_82377_(halfWidth, halfWidth, halfWidth);
        List targets = serverLevel.m_6443_(LivingEntity.class, searchBox, target -> {
            if (target == null) {
                return false;
            }
            if (target == attacker) {
                return false;
            }
            if (!target.m_6084_()) {
                return false;
            }
            return AreaAttackUtil.isInsideForwardArea(origin, forward, (Entity)target, minDistance, maxDistance, halfWidth);
        });
        DamageSource source = AreaAttackUtil.createDamageSource(attacker, damageType);
        int hitCount = 0;
        for (LivingEntity target2 : targets) {
            if (!target2.m_6469_(source, (float)damage)) continue;
            ++hitCount;
        }
        return hitCount;
    }

    public static int attackInFrontScalingDamage(Entity attacker, int minDamage, int maxDamage, ResourceKey<DamageType> damageType, float minDistance, float maxDistance, float wideness, int fireSeconds) {
        if (attacker == null) {
            return 0;
        }
        Level level = attacker.m_9236_();
        if (!(level instanceof ServerLevel)) {
            return 0;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        if (maxDistance <= minDistance) {
            return 0;
        }
        if (wideness <= 0.0f) {
            return 0;
        }
        if (maxDamage < minDamage) {
            return 0;
        }
        Vec3 origin = attacker.m_146892_();
        Vec3 forward = attacker.m_20154_().m_82541_();
        Vec3 start = origin.m_82549_(forward.m_82490_((double)minDistance));
        Vec3 end = origin.m_82549_(forward.m_82490_((double)maxDistance));
        double halfWidth = (double)wideness * 0.5;
        AABB searchBox = new AABB(start, end).m_82377_(halfWidth, halfWidth, halfWidth);
        List targets = serverLevel.m_6443_(LivingEntity.class, searchBox, target -> {
            if (target == null) {
                return false;
            }
            if (target == attacker) {
                return false;
            }
            if (!target.m_6084_()) {
                return false;
            }
            return AreaAttackUtil.isInsideForwardArea(origin, forward, (Entity)target, minDistance, maxDistance, halfWidth);
        });
        DamageSource source = AreaAttackUtil.createDamageSource(attacker, damageType);
        int hitCount = 0;
        for (LivingEntity target2 : targets) {
            int damage;
            Vec3 targetCenter = target2.m_20191_().m_82399_();
            Vec3 delta = targetCenter.m_82546_(origin);
            double forwardDistance = delta.m_82526_(forward);
            double progress = (forwardDistance - (double)minDistance) / (double)(maxDistance - minDistance);
            if (progress < 0.0) {
                progress = 0.0;
            }
            if (progress > 1.0) {
                progress = 1.0;
            }
            if (!target2.m_6469_(source, (float)(damage = (int)Math.round((double)maxDamage - (double)(maxDamage - minDamage) * progress)))) continue;
            if (fireSeconds > 0) {
                target2.m_20254_(fireSeconds);
            }
            ++hitCount;
        }
        return hitCount;
    }

    public static int attackAtPosition(Entity attacker, int damage, ResourceKey<DamageType> damageType, double x, double y, double z, float radius) {
        if (attacker == null) {
            return 0;
        }
        Level level = attacker.m_9236_();
        if (!(level instanceof ServerLevel)) {
            return 0;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        if (radius <= 0.0f) {
            return 0;
        }
        Vec3 center = new Vec3(x, y, z);
        double radiusSq = radius * radius;
        AABB searchBox = new AABB(x - (double)radius, y - (double)radius, z - (double)radius, x + (double)radius, y + (double)radius, z + (double)radius);
        List targets = serverLevel.m_6443_(LivingEntity.class, searchBox, target -> {
            if (target == null) {
                return false;
            }
            if (target == attacker) {
                return false;
            }
            if (!target.m_6084_()) {
                return false;
            }
            return target.m_20182_().m_82557_(center) <= radiusSq;
        });
        DamageSource source = AreaAttackUtil.createDamageSource(attacker, damageType);
        int hitCount = 0;
        for (LivingEntity target2 : targets) {
            if (!target2.m_6469_(source, (float)damage)) continue;
            ++hitCount;
        }
        return hitCount;
    }

    public static int attackAtBlockPos(Entity attacker, int damage, ResourceKey<DamageType> damageType, BlockPos pos, float radius) {
        return AreaAttackUtil.attackAtPosition(attacker, damage, damageType, (double)pos.m_123341_() + 0.5, (double)pos.m_123342_() + 0.5, (double)pos.m_123343_() + 0.5, radius);
    }

    private static DamageSource createDamageSource(Entity attacker, ResourceKey<DamageType> damageType) {
        Holder.Reference damageTypeHolder = attacker.m_9236_().m_9598_().m_175515_(Registries.f_268580_).m_246971_(damageType);
        return new DamageSource((Holder)damageTypeHolder, attacker);
    }

    private static boolean isInsideForwardArea(Vec3 origin, Vec3 forward, Entity target, float minDistance, float maxDistance, double halfWidth) {
        Vec3 targetCenter = target.m_20191_().m_82399_();
        Vec3 delta = targetCenter.m_82546_(origin);
        double forwardDistance = delta.m_82526_(forward);
        if (forwardDistance < (double)minDistance || forwardDistance > (double)maxDistance) {
            return false;
        }
        Vec3 projected = forward.m_82490_(forwardDistance);
        Vec3 perpendicular = delta.m_82546_(projected);
        return perpendicular.m_82556_() <= halfWidth * halfWidth;
    }

    public static int attackInFrontScalingDamageKnockback(Entity attacker, int minDamage, int maxDamage, ResourceKey<DamageType> damageType, float minDistance, float maxDistance, float wideness, int fireSeconds, double knockback) {
        if (attacker == null) {
            return 0;
        }
        Level level = attacker.m_9236_();
        if (!(level instanceof ServerLevel)) {
            return 0;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        if (maxDistance <= minDistance) {
            return 0;
        }
        if (wideness <= 0.0f) {
            return 0;
        }
        if (maxDamage < minDamage) {
            return 0;
        }
        Vec3 origin = attacker.m_146892_();
        Vec3 forward = attacker.m_20154_().m_82541_();
        Vec3 start = origin.m_82549_(forward.m_82490_((double)minDistance));
        Vec3 end = origin.m_82549_(forward.m_82490_((double)maxDistance));
        double halfWidth = (double)wideness * 0.5;
        AABB searchBox = new AABB(start, end).m_82377_(halfWidth, halfWidth, halfWidth);
        List targets = serverLevel.m_6443_(LivingEntity.class, searchBox, target -> {
            if (target == null) {
                return false;
            }
            if (target == attacker) {
                return false;
            }
            if (!target.m_6084_()) {
                return false;
            }
            return AreaAttackUtil.isInsideForwardArea(origin, forward, (Entity)target, minDistance, maxDistance, halfWidth);
        });
        DamageSource source = AreaAttackUtil.createDamageSource(attacker, damageType);
        int hitCount = 0;
        for (LivingEntity target2 : targets) {
            Vec3 knockbackDirection;
            int damage;
            Vec3 targetCenter = target2.m_20191_().m_82399_();
            Vec3 delta = targetCenter.m_82546_(origin);
            double forwardDistance = delta.m_82526_(forward);
            double progress = (forwardDistance - (double)minDistance) / (double)(maxDistance - minDistance);
            if (progress < 0.0) {
                progress = 0.0;
            }
            if (progress > 1.0) {
                progress = 1.0;
            }
            if (!target2.m_6469_(source, (float)(damage = (int)Math.round((double)maxDamage - (double)(maxDamage - minDamage) * progress)))) continue;
            if (fireSeconds > 0) {
                target2.m_20254_(fireSeconds);
            }
            if ((knockbackDirection = targetCenter.m_82546_(origin)).m_82556_() < 0.001) {
                knockbackDirection = forward;
            }
            if ((knockbackDirection = new Vec3(knockbackDirection.f_82479_, 0.0, knockbackDirection.f_82481_).m_82541_()).m_82556_() < 0.001) {
                knockbackDirection = new Vec3(forward.f_82479_, 0.0, forward.f_82481_).m_82541_();
            }
            target2.m_5997_(knockbackDirection.f_82479_ * knockback, 0.18, knockbackDirection.f_82481_ * knockback);
            target2.f_19864_ = true;
            ++hitCount;
        }
        return hitCount;
    }
}

