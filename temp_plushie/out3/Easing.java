package com.Polarice3.Goety.utils;

@FunctionalInterface
public interface Easing {
    Easing IN_SINE = (x) -> (float)(1.0D - Math.cos((double)x * Math.PI / 2.0D));
    Easing OUT_SINE = (x) -> (float)Math.sin((double)x * Math.PI / 2.0D);
    Easing IN_OUT_SINE = (x) -> (float)(-(Math.cos(Math.PI * (double)x) - 1.0D) / 2.0D);
    Easing IN_QUAD = (x) -> x * x;
    Easing OUT_QUAD = (x) -> 1.0F - (1.0F - x) * (1.0F - x);
    Easing IN_OUT_QUAD = (x) -> (float)((double)x < 0.5D ? (double)(2.0F * x * x) : 1.0D - Math.pow((double)(-2.0F * x + 2.0F), 2.0D) / 2.0D);
    Easing IN_CUBIC = (x) -> x * x * x;
    Easing OUT_CUBIC = (x) -> (float)(1.0D - Math.pow((double)(1.0F - x), 3.0D));
    Easing IN_OUT_CUBIC = (x) -> (float)((double)x < 0.5D ? (double)(4.0F * x * x * x) : 1.0D - Math.pow((double)(-2.0F * x + 2.0F), 3.0D) / 2.0D);
    Easing IN_QUART = (x) -> x * x * x * x;
    Easing OUT_QUART = (x) -> (float)(1.0D - Math.pow((double)(1.0F - x), 4.0D));
    Easing IN_OUT_QUART = (x) -> (float)((double)x < 0.5D ? (double)(8.0F * x * x * x * x) : 1.0D - Math.pow((double)(-2.0F * x + 2.0F), 4.0D) / 2.0D);
    Easing IN_QUINT = (x) -> x * x * x * x * x;
    Easing OUT_QUINT = (x) -> (float)(1.0D - Math.pow((double)(1.0F - x), 5.0D));
    Easing IN_OUT_QUINT = (x) -> (float)((double)x < 0.5D ? (double)(16.0F * x * x * x * x * x) : 1.0D - Math.pow((double)(-2.0F * x + 2.0F), 5.0D) / 2.0D);
    Easing IN_EXPO = (x) -> (float)(x == 0.0F ? 0.0D : Math.pow(2.0D, (double)(10.0F * x - 10.0F)));
    Easing OUT_EXPO = (x) -> (float)(x == 1.0F ? 1.0D : 1.0D - Math.pow(2.0D, (double)(-10.0F * x)));
    Easing IN_OUT_EXPO = (x) -> (float)(x == 0.0F ? 0.0D : (x == 1.0F ? 1.0D : ((double)x < 0.5D ? Math.pow(2.0D, (double)(20.0F * x - 10.0F)) / 2.0D : (2.0D - Math.pow(2.0D, (double)(-20.0F * x + 10.0F))) / 2.0D)));
    Easing IN_CIRC = (x) -> (float)(1.0D - Math.sqrt(1.0D - Math.pow((double)x, 2.0D)));
    Easing OUT_CIRC = (x) -> (float)Math.sqrt(1.0D - Math.pow((double)(x - 1.0F), 2.0D));
    Easing IN_OUT_CIRC = (x) -> (float)((double)x < 0.5D ? (1.0D - Math.sqrt(1.0D - Math.pow((double)(2.0F * x), 2.0D))) / 2.0D : (Math.sqrt(1.0D - Math.pow((double)(-2.0F * x + 2.0F), 2.0D)) + 1.0D) / 2.0D);
    Easing IN_BACK = (x) -> {
        float c1 = 1.70158F;
        float c3 = c1 + 1.0F;
        return c3 * x * x * x - c1 * x * x;
    };
    Easing OUT_BACK = (x) -> {
        float c1 = 1.70158F;
        float c3 = c1 + 1.0F;
        return (float)(1.0D + (double)c3 * Math.pow((double)(x - 1.0F), 3.0D) + (double)c1 * Math.pow((double)(x - 1.0F), 2.0D));
    };
    Easing IN_OUT_BACK = (x) -> {
        float c1 = 1.70158F;
        float c2 = c1 * 1.525F;
        return (float)((double)x < 0.5D ? Math.pow((double)(2.0F * x), 2.0D) * (double)((c2 + 1.0F) * 2.0F * x - c2) / 2.0D : (Math.pow((double)(2.0F * x - 2.0F), 2.0D) * (double)((c2 + 1.0F) * (x * 2.0F - 2.0F) + c2) + 2.0D) / 2.0D);
    };
    Easing IN_ELASTIC = (x) -> {
        float c4 = 2.0943952F;
        return x == 0.0F ? 0.0F : (float)(x == 1.0F ? 1.0D : -Math.pow(2.0D, (double)(10.0F * x - 10.0F)) * Math.sin(((double)(x * 10.0F) - 10.75D) * (double)c4));
    };
    Easing OUT_ELASTIC = (x) -> {
        float c4 = 2.0943952F;
        return x == 0.0F ? 0.0F : (float)(x == 1.0F ? 1.0D : Math.pow(2.0D, (double)(-10.0F * x)) * Math.sin(((double)(x * 10.0F) - 0.75D) * (double)c4) + 1.0D);
    };
    Easing IN_OUT_ELASTIC = (x) -> {
        float c5 = 1.3962634F;
        double v = Math.sin(((double)(20.0F * x) - 11.125D) * (double)c5);
        return x == 0.0F ? 0.0F : (float)(x == 1.0F ? 1.0D : ((double)x < 0.5D ? -(Math.pow(2.0D, (double)(20.0F * x - 10.0F)) * v) / 2.0D : Math.pow(2.0D, (double)(-20.0F * x + 10.0F)) * v / 2.0D + 1.0D));
    };
    Easing OUT_BOUNCE = (x) -> {
        float n1 = 7.5625F;
        float d1 = 2.75F;
        if (x < 1.0F / d1) {
            return n1 * x * x;
        } else if (x < 2.0F / d1) {
            float var5;
            return n1 * (var5 = x - (float)(1.5D / (double)d1)) * var5 + 0.75F;
        } else {
            float var3;
            float var4;
            return (double)x < 2.5D / (double)d1 ? n1 * (var3 = x - (float)(2.25D / (double)d1)) * var3 + 0.9375F : n1 * (var4 = x - (float)(2.625D / (double)d1)) * var4 + 0.984375F;
        }
    };
    Easing IN_BOUNCE = (x) -> 1.0F - OUT_BOUNCE.calculate(1.0F - x);
    Easing IN_OUT_BOUNCE = (x) -> (double)x < 0.5D ? (1.0F - OUT_BOUNCE.calculate(1.0F - 2.0F * x)) / 2.0F : (1.0F + OUT_BOUNCE.calculate(2.0F * x - 1.0F)) / 2.0F;

    float calculate(float var1);

    default float interpolate(float f, float from, float to) {
        return from + this.calculate(f) * (to - from);
    }
}
