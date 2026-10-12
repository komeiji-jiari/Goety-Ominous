package com.qiuyue.goetyominous.client.ac;

import com.qiuyue.goetyominous.config.WeaponConfig;
import net.minecraft.Util;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashMap;
import java.util.Map;

public final class BarbaricAxeSwingClient {

    public static final int TYPE_SWEEP = 0;
    public static final int TYPE_SLAM = 1;

    private static final float WINDUP_PUSH_SHARE = 0.7F;

    private static final Map<Integer, long[]> ACTIVE = new HashMap<>();

    private static boolean firstPersonHand;

    private BarbaricAxeSwingClient() {
    }

    public static void setFirstPersonHand(boolean value) {
        firstPersonHand = value;
    }

    public static boolean isFirstPersonHand() {
        return firstPersonHand;
    }

    public static void start(int entityId, int type) {
        ACTIVE.put(entityId, new long[]{Util.getMillis(), type});
    }

    public static boolean swinging(int entityId) {
        return elapsed(entityId) >= 0.0F;
    }

    public static float[] firstPersonPose(int entityId) {
        long[] state = ACTIVE.get(entityId);
        if (state == null) {
            return null;
        }
        int type = (int) state[1];
        float t = elapsed(entityId);
        if (t < 0.0F) {
            return null;
        }
        float a = curve(t / duration(type), shareWindup(type), shareStrike(type), windupRatio(type));
        float tilt = (float) Math.toRadians(clamp(firstTilt(type), 0.0F, 90.0F));
        float out = Math.max(0.0F, a);
        float back = Math.max(0.0F, -a);
        float push = firstPushOut(type);
        return new float[]{
                (float) Math.toRadians(firstAmplitude(type) * a),
                -(float) Math.sin(tilt),
                0.0F,
                (float) Math.cos(tilt),
                -firstSide(type) * a,
                -firstLift(type) * a,
                -push * out - push * WINDUP_PUSH_SHARE * back
        };
    }

    public static float[] thirdPersonPose(int entityId) {
        long[] state = ACTIVE.get(entityId);
        if (state == null) {
            return null;
        }
        int type = (int) state[1];
        float t = elapsed(entityId);
        if (t < 0.0F) {
            return null;
        }
        float w = windupRatio(type);
        float a = curve(t / duration(type), shareWindup(type), shareStrike(type), w);
        float bodyYaw = channel(a, w, thirdBodyYawWindup(type), thirdBodyYawStrike(type));
        return new float[]{
                channel(a, w, thirdBodyPitchWindup(type), thirdBodyPitchStrike(type)),
                bodyYaw,
                channel(a, w, thirdArmPitchWindup(type), thirdArmPitchStrike(type)),
                channel(a, w, thirdArmYawWindup(type), thirdArmYawStrike(type)) + bodyYaw,
                channel(a, w, thirdArmRollWindup(type), thirdArmRollStrike(type)),
                WeaponConfig.AnimThirdLeftArmFollow.get().floatValue()
        };
    }

    private static float elapsed(int entityId) {
        long[] state = ACTIVE.get(entityId);
        if (state == null) {
            return -1.0F;
        }
        float t = (Util.getMillis() - state[0]) / 1000.0F;
        if (t >= duration((int) state[1])) {
            ACTIVE.remove(entityId);
            return -1.0F;
        }
        return t;
    }

    private static float curve(float p, float windupShare, float strikeShare, float windupRatio) {
        float w = clamp(windupShare, 0.02F, 0.90F);
        float g = clamp(strikeShare, 0.02F, Math.max(0.02F, 0.98F - w));
        if (p <= 0.0F || p >= 1.0F) {
            return 0.0F;
        }
        if (p < w) {
            return -windupRatio * smooth(p / w);
        }
        if (p < w + g) {
            float u = (p - w) / g;
            return -windupRatio + (1.0F + windupRatio) * u * u;
        }
        float u = (p - w - g) / Math.max(1.0E-4F, 1.0F - w - g);
        return 1.0F - smooth(clamp(u, 0.0F, 1.0F));
    }

    private static float channel(float a, float windupRatio, float windupValue, float strikeValue) {
        if (a > 0.0F) {
            return strikeValue * a;
        }
        if (windupRatio <= 1.0E-4F) {
            return 0.0F;
        }
        return windupValue * clamp(a / -windupRatio, 0.0F, 1.0F);
    }

    private static float smooth(float u) {
        return u * u * (3.0F - 2.0F * u);
    }

    private static float clamp(float v, float lo, float hi) {
        return v < lo ? lo : (v > hi ? hi : v);
    }

    private static float read(ForgeConfigSpec.ConfigValue<Double> value) {
        return value.get().floatValue();
    }

    private static float duration(int type) {
        return Math.max(0.05F, read(type == TYPE_SLAM ? WeaponConfig.AnimSlamDuration : WeaponConfig.AnimSweepDuration));
    }

    private static float shareWindup(int type) {
        return read(type == TYPE_SLAM ? WeaponConfig.AnimSlamWindupShare : WeaponConfig.AnimSweepWindupShare);
    }

    private static float shareStrike(int type) {
        return read(type == TYPE_SLAM ? WeaponConfig.AnimSlamStrikeShare : WeaponConfig.AnimSweepStrikeShare);
    }

    private static float windupRatio(int type) {
        return read(type == TYPE_SLAM ? WeaponConfig.AnimSlamWindupRatio : WeaponConfig.AnimSweepWindupRatio);
    }

    private static float firstAmplitude(int type) {
        return read(type == TYPE_SLAM ? WeaponConfig.AnimFirstSlamAmplitude : WeaponConfig.AnimFirstSweepAmplitude);
    }

    private static float firstTilt(int type) {
        return read(type == TYPE_SLAM ? WeaponConfig.AnimFirstSlamTilt : WeaponConfig.AnimFirstSweepTilt);
    }

    private static float firstPushOut(int type) {
        return read(type == TYPE_SLAM ? WeaponConfig.AnimFirstSlamPushOut : WeaponConfig.AnimFirstSweepPushOut);
    }

    private static float firstLift(int type) {
        return read(type == TYPE_SLAM ? WeaponConfig.AnimFirstSlamLift : WeaponConfig.AnimFirstSweepLift);
    }

    private static float firstSide(int type) {
        return read(type == TYPE_SLAM ? WeaponConfig.AnimFirstSlamSide : WeaponConfig.AnimFirstSweepSide);
    }

    private static float thirdBodyPitchWindup(int type) {
        return read(type == TYPE_SLAM ? WeaponConfig.AnimThirdSlamBodyPitchWindup : WeaponConfig.AnimThirdSweepBodyPitchWindup);
    }

    private static float thirdBodyPitchStrike(int type) {
        return read(type == TYPE_SLAM ? WeaponConfig.AnimThirdSlamBodyPitchStrike : WeaponConfig.AnimThirdSweepBodyPitchStrike);
    }

    private static float thirdBodyYawWindup(int type) {
        return read(type == TYPE_SLAM ? WeaponConfig.AnimThirdSlamBodyYawWindup : WeaponConfig.AnimThirdSweepBodyYawWindup);
    }

    private static float thirdBodyYawStrike(int type) {
        return read(type == TYPE_SLAM ? WeaponConfig.AnimThirdSlamBodyYawStrike : WeaponConfig.AnimThirdSweepBodyYawStrike);
    }

    private static float thirdArmPitchWindup(int type) {
        return read(type == TYPE_SLAM ? WeaponConfig.AnimThirdSlamArmPitchWindup : WeaponConfig.AnimThirdSweepArmPitchWindup);
    }

    private static float thirdArmPitchStrike(int type) {
        return read(type == TYPE_SLAM ? WeaponConfig.AnimThirdSlamArmPitchStrike : WeaponConfig.AnimThirdSweepArmPitchStrike);
    }

    private static float thirdArmYawWindup(int type) {
        return read(type == TYPE_SLAM ? WeaponConfig.AnimThirdSlamArmYawWindup : WeaponConfig.AnimThirdSweepArmYawWindup);
    }

    private static float thirdArmYawStrike(int type) {
        return read(type == TYPE_SLAM ? WeaponConfig.AnimThirdSlamArmYawStrike : WeaponConfig.AnimThirdSweepArmYawStrike);
    }

    private static float thirdArmRollWindup(int type) {
        return read(type == TYPE_SLAM ? WeaponConfig.AnimThirdSlamArmRollWindup : WeaponConfig.AnimThirdSweepArmRollWindup);
    }

    private static float thirdArmRollStrike(int type) {
        return read(type == TYPE_SLAM ? WeaponConfig.AnimThirdSlamArmRollStrike : WeaponConfig.AnimThirdSweepArmRollStrike);
    }
}
