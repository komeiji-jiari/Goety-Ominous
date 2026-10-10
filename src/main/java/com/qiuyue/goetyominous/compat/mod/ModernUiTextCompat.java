package com.qiuyue.goetyominous.compat.mod;

import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.fml.ModList;

import java.lang.reflect.Field;

public final class ModernUiTextCompat {

    private static final boolean LOADED =
            ModList.get() != null && ModList.get().isLoaded("modernui");

    private static final Field S_SHADER_NORMAL = resolveField();

    private ModernUiTextCompat() {
    }

    private static Field resolveField() {
        if (!LOADED) {
            return null;
        }
        try {
            Field f = Class.forName("icyllis.modernui.mc.text.TextRenderType").getDeclaredField("sShaderNormal");
            f.setAccessible(true);
            return f;
        } catch (Throwable ignored) {
            return null;
        }
    }

    public static boolean active() {
        return S_SHADER_NORMAL != null;
    }

    public static ShaderInstance getNormalShader() {
        if (S_SHADER_NORMAL == null) {
            return null;
        }
        try {
            return (ShaderInstance) S_SHADER_NORMAL.get(null);
        } catch (Throwable ignored) {
            return null;
        }
    }

    public static void setNormalShader(ShaderInstance shader) {
        if (S_SHADER_NORMAL == null) {
            return;
        }
        try {
            S_SHADER_NORMAL.set(null, shader);
        } catch (Throwable ignored) {
        }
    }
}
