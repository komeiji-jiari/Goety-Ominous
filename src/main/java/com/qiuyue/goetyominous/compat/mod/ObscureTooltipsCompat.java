package com.qiuyue.goetyominous.compat.mod;

import net.minecraftforge.fml.ModList;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class ObscureTooltipsCompat {

    private static final Object ENABLED = resolveEnabled();

    private static final Method GETTER = resolveMethod("get");

    private static final Method SETTER = resolveMethod("set", Object.class);

    private static boolean savedEnabled = false;
    private static boolean suppressed = false;

    private ObscureTooltipsCompat() {
    }

    private static Object resolveEnabled() {
        if (ModList.get() == null || !ModList.get().isLoaded("obscure_tooltips")) {
            return null;
        }
        try {
            Field f = Class.forName("dev.obscuria.tooltips.config.ClientConfig").getField("ENABLED");
            return f.get(null);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static Method resolveMethod(String name, Class<?>... parameterTypes) {
        if (ENABLED == null) {
            return null;
        }
        try {
            return ENABLED.getClass().getMethod(name, parameterTypes);
        } catch (Throwable ignored) {
            return null;
        }
    }

    public static boolean active() {
        return SETTER != null;
    }

    public static void suppress() {
        if (SETTER == null || suppressed) {
            return;
        }
        try {
            savedEnabled = Boolean.TRUE.equals(GETTER.invoke(ENABLED));
            if (savedEnabled) {
                SETTER.invoke(ENABLED, Boolean.FALSE);
            }
            suppressed = true;
        } catch (Throwable ignored) {
        }
    }

    public static void restore() {
        if (SETTER == null || !suppressed) {
            return;
        }
        try {
            if (savedEnabled) {
                SETTER.invoke(ENABLED, Boolean.TRUE);
            }
        } catch (Throwable ignored) {
        } finally {
            suppressed = false;
        }
    }
}
