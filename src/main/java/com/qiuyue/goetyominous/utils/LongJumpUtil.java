package com.qiuyue.goetyominous.utils;

import java.util.Optional;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

public final class LongJumpUtil {

    private LongJumpUtil() {
    }

    public static Optional<Vec3> calculateJumpVectorForAngle(Mob mob, Vec3 targetPos, float maxJumpVelocity, int angle, boolean checkClearance) {
        Vec3 vec3 = mob.position();
        Vec3 vec3_1 = new Vec3(targetPos.x - vec3.x, 0.0D, targetPos.z - vec3.z).normalize().scale(0.5D);
        Vec3 vec3_2 = targetPos.subtract(vec3_1);
        Vec3 vec3_3 = vec3_2.subtract(vec3);
        float f = (float) angle * (float) Math.PI / 180.0F;
        double d0 = Math.atan2(vec3_3.z, vec3_3.x);
        double d1 = vec3_3.subtract(0.0D, vec3_3.y, 0.0D).lengthSqr();
        double d2 = Math.sqrt(d1);
        double d3 = vec3_3.y;
        double d4 = mob.getAttributeValue(ForgeMod.ENTITY_GRAVITY.get());
        double d5 = Math.sin(2.0F * f);
        double d6 = Math.pow(Math.cos(f), 2.0D);
        double d7 = Math.sin(f);
        double d8 = Math.cos(f);
        double d9 = Math.sin(d0);
        double d10 = Math.cos(d0);
        double d11 = d1 * d4 / (d2 * d5 - 2.0D * d3 * d6);
        if (d11 < 0.0D) {
            return Optional.empty();
        }
        double d12 = Math.sqrt(d11);
        if (d12 > (double) maxJumpVelocity) {
            return Optional.empty();
        }
        double d13 = d12 * d8;
        double d14 = d12 * d7;
        if (checkClearance) {
            int i = Mth.ceil(d2 / d13) * 2;
            double d15 = 0.0D;
            Vec3 vec3_4 = null;
            EntityDimensions entitydimensions = mob.getDimensions(Pose.LONG_JUMPING);
            for (int j = 0; j < i - 1; ++j) {
                d15 += d2 / (double) i;
                double d16 = d7 / d8 * d15 - Math.pow(d15, 2.0D) * d4 / (2.0D * d11 * Math.pow(d8, 2.0D));
                double d17 = d15 * d10;
                double d18 = d15 * d9;
                Vec3 vec3_5 = new Vec3(vec3.x + d17, vec3.y + d16, vec3.z + d18);
                if (vec3_4 != null && !isPathClear(mob, entitydimensions, vec3_4, vec3_5)) {
                    return Optional.empty();
                }
                vec3_4 = vec3_5;
            }
        }
        return Optional.of(new Vec3(d13 * d10, d14, d13 * d9).scale(0.95F));
    }

    private static boolean isPathClear(Mob mob, EntityDimensions dimensions, Vec3 from, Vec3 to) {
        Vec3 vec3 = to.subtract(from);
        double d0 = (double) Math.min(dimensions.width, dimensions.height);
        int i = Mth.ceil(vec3.length() / d0);
        Vec3 vec3_1 = vec3.normalize();
        Vec3 vec3_2 = from;
        for (int j = 0; j < i; ++j) {
            vec3_2 = j == i - 1 ? to : vec3_2.add(vec3_1.scale(d0 * 0.9D));
            if (!mob.level().noCollision(mob, dimensions.makeBoundingBox(vec3_2))) {
                return false;
            }
        }
        return true;
    }
}
