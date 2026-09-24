package com.qiuyue.goetyominous.common.entities.ally.ac;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.entity.PartEntity;

public class PartProjectileHits {

    public static float projectileMultiplier(LivingEntity parent, DamageSource source, float bodyMultiplier, float partMultiplier) {
        Entity projectile = source.getDirectEntity();
        return projectile != null && hitsPart(parent, projectile) ? partMultiplier : bodyMultiplier;
    }

    private static boolean hitsPart(LivingEntity parent, Entity projectile) {
        PartEntity<?>[] parts = parent.getParts();
        if (parts.length == 0) {
            return false;
        }
        AABB sweep = projectile.getBoundingBox().expandTowards(projectile.getDeltaMovement());
        for (PartEntity<?> part : parts) {
            if (sweep.intersects(part.getBoundingBox())) {
                return true;
            }
        }
        return false;
    }
}
