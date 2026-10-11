package com.qiuyue.goetyominous.client;

import net.minecraft.Util;

import java.util.HashMap;
import java.util.Map;

public final class BarbaricAxeSwingClient {

    private static final float SWEEP_DURATION = 1.64F;
    private static final float SLAM_DURATION = 1.64F;

    private static final float[][] SWEEP_THIRD = {
            {0.00F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
            {0.32F, -8.0F, 0.0F, -50.0F, -60.0F, 20.0F},
            {0.88F, 12.0F, 0.0F, -55.0F, 70.0F, 60.0F},
            {1.20F, 12.0F, 0.0F, -55.0F, 70.0F, 60.0F},
            {1.64F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
    };

    private static final float[][] SLAM_THIRD = {
            {0.00F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
            {0.32F, -17.5F, 0.0F, -130.0F, -10.0F, 32.5F},
            {0.76F, 62.5F, 0.0F, 37.5F, 22.5F, 30.0F},
            {1.24F, 53.6F, 0.0F, 33.3F, 21.9F, 26.7F},
            {1.64F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F},
    };

    private static final float[][] SWEEP_FIRST = {
            {0.00F, 0.0F, 0.0F, 0.0F},
            {0.32F, -40.0F, -45.0F, 0.0F},
            {0.88F, -45.0F, 60.0F, 20.0F},
            {1.20F, -45.0F, 60.0F, 20.0F},
            {1.64F, 0.0F, 0.0F, 0.0F},
    };

    private static final float[][] SLAM_FIRST = {
            {0.00F, 10.0F, 0.0F, 0.0F},
            {0.32F, -70.0F, 0.0F, 0.0F},
            {0.76F, 20.0F, 0.0F, 0.0F},
            {1.24F, 18.0F, 0.0F, 0.0F},
            {1.64F, 0.0F, 0.0F, 0.0F},
    };

    private static final Map<Integer, long[]> ACTIVE = new HashMap<>();

    private BarbaricAxeSwingClient() {
    }

    public static void start(int entityId, int type) {
        ACTIVE.put(entityId, new long[]{Util.getMillis(), type});
    }

    public static float[] pose(int entityId, boolean firstPerson) {
        long[] state = ACTIVE.get(entityId);
        if (state == null) {
            return null;
        }
        int type = (int) state[1];
        float t = (Util.getMillis() - state[0]) / 1000.0F;
        if (t >= duration(type)) {
            ACTIVE.remove(entityId);
            return null;
        }
        float[][] keys = firstPerson
                ? (type == 1 ? SLAM_FIRST : SWEEP_FIRST)
                : (type == 1 ? SLAM_THIRD : SWEEP_THIRD);
        return sample(keys, t);
    }

    private static float duration(int type) {
        return type == 1 ? SLAM_DURATION : SWEEP_DURATION;
    }

    private static float[] sample(float[][] keys, float t) {
        int last = keys.length - 1;
        if (t <= keys[0][0]) {
            return tail(keys[0]);
        }
        if (t >= keys[last][0]) {
            return tail(keys[last]);
        }
        for (int i = 0; i < last; i++) {
            float t0 = keys[i][0];
            float t1 = keys[i + 1][0];
            if (t >= t0 && t <= t1) {
                float f = t1 <= t0 ? 0.0F : (t - t0) / (t1 - t0);
                float[] out = new float[keys[i].length - 1];
                for (int c = 1; c < keys[i].length; c++) {
                    out[c - 1] = keys[i][c] + (keys[i + 1][c] - keys[i][c]) * f;
                }
                return out;
            }
        }
        return tail(keys[last]);
    }

    private static float[] tail(float[] key) {
        float[] out = new float[key.length - 1];
        System.arraycopy(key, 1, out, 0, out.length);
        return out;
    }
}
