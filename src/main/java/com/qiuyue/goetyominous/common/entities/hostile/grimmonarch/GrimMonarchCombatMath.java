package com.qiuyue.goetyominous.common.entities.hostile.grimmonarch;

public final class GrimMonarchCombatMath {
    private static final double[] RISE_TIME = {0.0, 0.25, 0.5, 0.6, 0.7, 0.8, 1.0};
    private static final double[] RISE_VALUE = {0.0, 0.172, 0.506, 0.771, 0.917, 0.979, 1.0};

    private GrimMonarchCombatMath() {
    }

    public static double smooth(double t) {
        double u = clamp(t, 0.0, 1.0);
        return u * u * (3.0 - 2.0 * u);
    }

    public static double turn(double from, double to, double step) {
        return from + clamp(wrap(to - from), -step, step);
    }

    public static double rise(double t) {
        return curve(RISE_TIME, RISE_VALUE, clamp(t, 0.0, 1.0));
    }

    public static double flightTravel(double t) {
        return Math.pow(clamp(t, 0.0, 1.0), 1.5);
    }

    public static boolean segmentBox(double ax, double ay, double az, double bx, double by, double bz,
                                     double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return Double.isFinite(boxEntry(ax, ay, az, bx, by, bz, minX, minY, minZ, maxX, maxY, maxZ));
    }

    public static double sweepCircleContact(double ax, double ay, double az, double bx, double by, double bz,
                                            double radius, double height,
                                            double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        double bottom = minY - height;
        double top = maxY + 0.5;
        double best = boxEntry(ax, ay, az, bx, by, bz,
                minX - radius, bottom, minZ, maxX + radius, top, maxZ);
        best = earlier(best, boxEntry(ax, ay, az, bx, by, bz,
                minX, bottom, minZ - radius, maxX, top, maxZ + radius));
        double low = 0.0;
        double high = 1.0;
        double dy = by - ay;
        if (Math.abs(dy) < 1.0E-10) {
            if (ay < bottom || ay > top) {
                return Double.NaN;
            }
        } else {
            double enter = (bottom - ay) / dy;
            double exit = (top - ay) / dy;
            low = Math.max(low, Math.min(enter, exit));
            high = Math.min(high, Math.max(enter, exit));
        }
        if (low > high) {
            return Double.NaN;
        }
        double dx = bx - ax;
        double dz = bz - az;
        double length = dx * dx + dz * dz;
        for (int corner = 0; corner < 4; ++corner) {
            double x = ax - (corner < 2 ? minX : maxX);
            double z = az - (corner % 2 == 0 ? minZ : maxZ);
            double offset = x * x + z * z - radius * radius;
            if (length < 1.0E-20) {
                if (offset <= 0.0) {
                    best = earlier(best, low);
                }
                continue;
            }
            double projection = x * dx + z * dz;
            double discriminant = projection * projection - length * offset;
            if (discriminant < 0.0) {
                continue;
            }
            double root = Math.sqrt(discriminant);
            double enter = Math.max(low, (-projection - root) / length);
            double exit = Math.min(high, (-projection + root) / length);
            if (enter <= exit) {
                best = earlier(best, enter);
            }
        }
        return best;
    }

    public static double boxDistanceSquaredXZ(double x, double z, double minX, double minZ, double maxX, double maxZ) {
        double nearestX = clamp(x, minX, maxX);
        double nearestZ = clamp(z, minZ, maxZ);
        double dx = x - nearestX;
        double dz = z - nearestZ;
        return dx * dx + dz * dz;
    }

    private static double boxEntry(double ax, double ay, double az, double bx, double by, double bz,
                                   double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        double[] start = {ax, ay, az};
        double[] end = {bx, by, bz};
        double[] low = {minX, minY, minZ};
        double[] high = {maxX, maxY, maxZ};
        double enter = 0.0;
        double exit = 1.0;
        for (int axis = 0; axis < 3; ++axis) {
            double delta = end[axis] - start[axis];
            if (Math.abs(delta) < 1.0E-10) {
                if (start[axis] < low[axis] || start[axis] > high[axis]) {
                    return Double.NaN;
                }
                continue;
            }
            double first = (low[axis] - start[axis]) / delta;
            double second = (high[axis] - start[axis]) / delta;
            enter = Math.max(enter, Math.min(first, second));
            exit = Math.min(exit, Math.max(first, second));
            if (enter > exit) {
                return Double.NaN;
            }
        }
        return enter;
    }

    private static double earlier(double first, double second) {
        if (!Double.isFinite(first)) {
            return second;
        }
        if (!Double.isFinite(second)) {
            return first;
        }
        return Math.min(first, second);
    }

    private static double curve(double[] times, double[] values, double time) {
        int last = times.length - 1;
        if (time <= times[0]) {
            return values[0];
        }
        if (time >= times[last]) {
            return values[last];
        }
        int index = 0;
        while (index < last - 1 && time > times[index + 1]) {
            ++index;
        }
        double span = times[index + 1] - times[index];
        double u = (time - times[index]) / span;
        double u2 = u * u;
        double u3 = u2 * u;
        double startSlope = slope(times, values, index);
        double endSlope = slope(times, values, index + 1);
        return (2.0 * u3 - 3.0 * u2 + 1.0) * values[index]
                + (u3 - 2.0 * u2 + u) * span * startSlope
                + (-2.0 * u3 + 3.0 * u2) * values[index + 1]
                + (u3 - u2) * span * endSlope;
    }

    private static double slope(double[] times, double[] values, int index) {
        if (index <= 0) {
            return (values[1] - values[0]) / (times[1] - times[0]);
        }
        if (index >= times.length - 1) {
            return (values[index] - values[index - 1]) / (times[index] - times[index - 1]);
        }
        return (values[index + 1] - values[index - 1]) / (times[index + 1] - times[index - 1]);
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private static double wrap(double degrees) {
        double wrapped = degrees % 360.0;
        if (wrapped >= 180.0) {
            return wrapped - 360.0;
        }
        return wrapped < -180.0 ? wrapped + 360.0 : wrapped;
    }
}
