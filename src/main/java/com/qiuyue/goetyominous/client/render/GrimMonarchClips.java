package com.qiuyue.goetyominous.client.render;

import com.qiuyue.goetyominous.client.render.model.GrimMonarchModel;
import com.qiuyue.goetyominous.common.entities.hostile.grimmonarch.GrimMonarchClipData;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

@OnlyIn(Dist.CLIENT)
public final class GrimMonarchClips {
    private GrimMonarchClips() {
    }

    public static float[][] sample(String name, float seconds) {
        return GrimMonarchClipData.sample(name, seconds);
    }

    public static float[][] copy(float[][] pose) {
        float[][] copy = new float[pose.length][];
        for (int i = 0; i < pose.length; ++i) {
            copy[i] = pose[i].clone();
        }
        return copy;
    }

    public static void blend(float[][] from, float[][] to, float weight) {
        if (weight <= 0.0F) {
            return;
        }
        for (int i = 0; i < from.length; ++i) {
            float[] a = from[i];
            float[] b = to[i];
            for (int j = 0; j < 3; ++j) {
                a[j] = a[j] + (b[j] - a[j]) * weight;
            }
            if (a[3] == b[3] && a[4] == b[4] && a[5] == b[5]) {
                continue;
            }
            Quaternionf rotation = new Quaternionf().rotationZYX(a[5], a[4], a[3]);
            rotation.slerp(new Quaternionf().rotationZYX(b[5], b[4], b[3]), weight);
            setRotation(a, rotation);
        }
    }

    public static void apply(GrimMonarchModel model, float[][] pose) {
        for (int i = 0; i < GrimMonarchClipData.PART_NAMES.length; ++i) {
            ModelPart part = model.part(GrimMonarchClipData.PART_NAMES[i]);
            if (part == null) {
                continue;
            }
            PartPose initial = part.getInitialPose();
            part.x = initial.x + pose[i][0];
            part.y = initial.y + pose[i][1];
            part.z = initial.z + pose[i][2];
            part.xRot = initial.xRot + pose[i][3];
            part.yRot = initial.yRot + pose[i][4];
            part.zRot = initial.zRot + pose[i][5];
            part.xScale = 1.0F;
            part.yScale = 1.0F;
            part.zScale = 1.0F;
        }
    }

    private static void setRotation(float[] pose, Quaternionf rotation) {
        Matrix4f matrix = new Matrix4f().rotation(rotation);
        float[] euler = eulerZYX(matrix);
        pose[3] = euler[0];
        pose[4] = euler[1];
        pose[5] = euler[2];
    }

    private static float[] eulerZYX(Matrix4f matrix) {
        float r00 = matrix.get(0, 0);
        float r10 = matrix.get(0, 1);
        float r20 = matrix.get(0, 2);
        float r11 = matrix.get(1, 1);
        float r21 = matrix.get(1, 2);
        float r22 = matrix.get(2, 2);
        float r12 = matrix.get(2, 1);
        float y = (float) Math.asin(Math.max(-1.0F, Math.min(1.0F, -r20)));
        float x;
        float z;
        if (Math.abs(r20) < 0.9999999F) {
            x = (float) Math.atan2(r21, r22);
            z = (float) Math.atan2(r10, r00);
        } else {
            x = (float) Math.atan2(-r12, r11);
            z = 0.0F;
        }
        return new float[]{x, y, z};
    }
}
