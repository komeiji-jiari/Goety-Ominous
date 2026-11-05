package com.qiuyue.goetyominous.common.entities.util;

import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public final class FireWallShape {
    private FireWallShape() {
    }

    public static List<Vec3> line(Level level, Vec3 center, Vec3 look, float length, int segments) {
        Vec3 forward = horizontal(look);
        Vec3 side = new Vec3(-forward.z, 0.0D, forward.x);
        List<Vec3> points = new ArrayList<>();
        int count = Math.max(1, segments);
        for (int i = 0; i <= count; i++) {
            double t = (double) i / (double) count - 0.5D;
            points.add(snapToGround(level, center.add(side.scale(length * t))));
        }
        return points;
    }

    public static List<Vec3> arc(Level level, Vec3 center, Vec3 look, float radius, float angleDegrees, int segments) {
        Vec3 forward = horizontal(look);
        List<Vec3> points = new ArrayList<>();
        int count = Math.max(1, segments);
        float start = (float) Math.toRadians(-angleDegrees * 0.5F);
        float step = (float) Math.toRadians(angleDegrees) / (float) count;
        for (int i = 0; i <= count; i++) {
            Vec3 dir = forward.yRot(start + step * (float) i);
            points.add(snapToGround(level, center.add(dir.scale(radius))));
        }
        return points;
    }

    public static List<Vec3> circle(Level level, Vec3 center, float radius, int segments) {
        List<Vec3> points = new ArrayList<>();
        int count = Math.max(3, segments);
        for (int i = 0; i <= count; i++) {
            double angle = Math.PI * 2.0D * (double) i / (double) count;
            Vec3 offset = new Vec3(Math.cos(angle) * radius, 0.0D, Math.sin(angle) * radius);
            points.add(snapToGround(level, center.add(offset)));
        }
        return points;
    }

    public static Vec3 snapToGround(Level level, Vec3 point) {
        HitResult hit = level.clip(new ClipContext(
                point.add(0.0D, 1.0D, 0.0D),
                point.add(0.0D, -15.0D, 0.0D),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                null));
        return hit.getType() == HitResult.Type.MISS ? point : hit.getLocation();
    }

    public static boolean hasGround(Level level, Vec3 point) {
        return level.clip(new ClipContext(
                point.add(0.0D, 1.0D, 0.0D),
                point.add(0.0D, -15.0D, 0.0D),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                null)).getType() != HitResult.Type.MISS;
    }

    private static Vec3 horizontal(Vec3 look) {
        Vec3 horizontal = new Vec3(look.x, 0.0D, look.z);
        return horizontal.lengthSqr() < 1.0E-6D ? new Vec3(0.0D, 0.0D, 1.0D) : horizontal.normalize();
    }
}
