package com.qiuyue.goetyominous.common.entities.hostile.grimmonarch;

import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

final class GrimMonarchClawGeometry {
    record Segment(Vec3 from, Vec3 to) {
    }

    private static final String[] LEFT_CHAIN = {
            "root", "hip", "body", "shoulders", "left_arm", "left_claw"
    };
    private static final String[] RIGHT_CHAIN = {
            "root", "hip", "body", "shoulders", "right_arm", "right_claw"
    };

    private GrimMonarchClawGeometry() {
    }

    static List<Segment> segments(GrimMonarchEntity boss, float actionTime, Vec3 origin, float yaw) {
        GrimMonarchAnimationTable.Sample sample = GrimMonarchAnimationTable.sample(boss.action(), actionTime);
        float[][] pose = GrimMonarchClipData.sample(sample.clip(), sample.time());
        List<Segment> result = new ArrayList<>(10);
        append(result, origin, yaw, pose, LEFT_CHAIN, false);
        append(result, origin, yaw, pose, RIGHT_CHAIN, true);
        return result;
    }

    private static void append(List<Segment> out, Vec3 origin, float yaw, float[][] pose,
                               String[] chain, boolean right) {
        Matrix4f frame = new Matrix4f()
                .rotateY((float) Math.toRadians(180.0F - yaw))
                .scale(-1.0F, -1.0F, 1.0F)
                .translate(0.0F, -1.5F, 0.0F);
        for (String part : chain) {
            Vec3 initial = initial(part);
            float[] offset = pose[index(part)];
            frame.translate(
                    (float) ((initial.x + offset[0]) / 16.0D),
                    (float) ((initial.y + offset[1]) / 16.0D),
                    (float) ((initial.z + offset[2]) / 16.0D));
            frame.rotateZYX(offset[5], offset[4], offset[3]);
        }
        float y;
        float minX;
        float maxX;
        if (right) {
            frame.translate(0.0F, -1.0F / 16.0F, 0.0F);
            frame.rotateZYX(0.0F, 0.7854F, 1.5708F);
            y = 0.1213F;
            minX = -4.976F;
            maxX = 7.024F;
        } else {
            frame.translate(-1.0F / 16.0F, -1.0F / 16.0F, -2.0F / 16.0F);
            frame.rotateZYX(-3.1416F, 0.7854F, -1.5708F);
            y = -1.2787F;
            minX = -6.024F;
            maxX = 5.976F;
        }
        for (int strip = 0; strip < 5; ++strip) {
            float x = minX + (maxX - minX) * strip / 4.0F;
            Vec3 base = point(frame, x, y, 2.0F, origin);
            Vec3 tip = point(frame, x, y, -44.0F, origin);
            out.add(new Segment(base, tip));
        }
    }

    private static Vec3 point(Matrix4f frame, float x, float y, float z, Vec3 origin) {
        Vector3f point = frame.transformPosition(new Vector3f(x / 16.0F, y / 16.0F, z / 16.0F));
        return new Vec3(point.x + origin.x, point.y + origin.y, point.z + origin.z);
    }

    private static Vec3 initial(String part) {
        return switch (part) {
            case "root" -> new Vec3(0.0D, 24.0D, 0.0D);
            case "hip" -> new Vec3(0.0D, -47.0D, 0.0D);
            case "body" -> new Vec3(0.0D, -1.0D, 0.0D);
            case "shoulders" -> new Vec3(0.0D, -17.0D, 0.0D);
            case "left_arm" -> new Vec3(9.2263D, 0.0D, 0.4645D);
            case "left_claw" -> new Vec3(1.4831D, 22.0D, -1.4831D);
            case "right_arm" -> new Vec3(-8.1415D, 0.0D, 0.5493D);
            case "right_claw" -> new Vec3(-1.5679D, 22.0D, -1.5679D);
            default -> Vec3.ZERO;
        };
    }

    private static int index(String part) {
        return switch (part) {
            case "root" -> 0;
            case "hip" -> 1;
            case "body" -> 4;
            case "shoulders" -> 5;
            case "left_arm" -> 8;
            case "left_claw" -> 9;
            case "right_arm" -> 10;
            case "right_claw" -> 11;
            default -> 0;
        };
    }
}
