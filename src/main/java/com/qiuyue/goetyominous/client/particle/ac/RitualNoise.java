package com.qiuyue.goetyominous.client.particle.ac;

public final class RitualNoise {

    private RitualNoise() {
    }

    public static double noise(double x, double y, double z) {
        double a = value(x, y, z);
        double b = value(x * 2.13 + 31.7, y * 2.05 + 17.3, z * 2.21 + 11.9);
        return a * 0.6 + b * 0.4;
    }

    private static double value(double x, double y, double z) {
        int xi = (int) Math.floor(x);
        int yi = (int) Math.floor(y);
        int zi = (int) Math.floor(z);
        double xf = x - xi;
        double yf = y - yi;
        double zf = z - zi;
        double u = xf * xf * (3.0 - 2.0 * xf);
        double v = yf * yf * (3.0 - 2.0 * yf);
        double w = zf * zf * (3.0 - 2.0 * zf);

        double n000 = hash(xi, yi, zi);
        double n100 = hash(xi + 1, yi, zi);
        double n010 = hash(xi, yi + 1, zi);
        double n110 = hash(xi + 1, yi + 1, zi);
        double n001 = hash(xi, yi, zi + 1);
        double n101 = hash(xi + 1, yi, zi + 1);
        double n011 = hash(xi, yi + 1, zi + 1);
        double n111 = hash(xi + 1, yi + 1, zi + 1);

        double nx00 = lerp(n000, n100, u);
        double nx10 = lerp(n010, n110, u);
        double nx01 = lerp(n001, n101, u);
        double nx11 = lerp(n011, n111, u);
        double nxy0 = lerp(nx00, nx10, v);
        double nxy1 = lerp(nx01, nx11, v);
        return lerp(nxy0, nxy1, w);
    }

    private static double hash(int x, int y, int z) {
        int h = x * 0x27d4eb2d + y * 0x165667b1 + z * 0x9e3779b9;
        h = (h ^ (h >>> 15)) * 0x85ebca6b;
        h = h ^ (h >>> 13);
        return (h & 0xFFFFFF) / (double) 0xFFFFFF;
    }

    private static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }
}
