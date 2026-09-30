package com.qiuyue.goetyominous.compat.mod;

import net.minecraftforge.fml.ModList;

import java.lang.reflect.Field;

public final class ModernUiTooltipCompat {

    private static final boolean LOADED =
            ModList.get() != null && ModList.get().isLoaded("modernui");

    private static final Field S_TOOLTIP = resolveField();

    private static boolean savedEnabled = false;
    private static boolean suppressed = false;

    private ModernUiTooltipCompat() {
    }

    private static Field resolveField() {
        if (!LOADED) {
            return null;
        }
        try {
            Field f = Class.forName("icyllis.modernui.mc.TooltipRenderer").getField("sTooltip");
            f.setAccessible(true);
            return f;
        } catch (Throwable ignored) {
            return null;
        }
    }

    public static boolean active() {
        return S_TOOLTIP != null;
    }

    public static void suppress() {
        if (S_TOOLTIP == null || suppressed) {
            return;
        }
        try {
            savedEnabled = S_TOOLTIP.getBoolean(null);
            if (savedEnabled) {
                S_TOOLTIP.setBoolean(null, false);
            }
            suppressed = true;
        } catch (Throwable ignored) {
        }
    }

    public static void restore() {
        if (S_TOOLTIP == null || !suppressed) {
            return;
        }
        try {
            if (savedEnabled) {
                S_TOOLTIP.setBoolean(null, true);
            }
        } catch (Throwable ignored) {
        } finally {
            suppressed = false;
        }
    }
}
