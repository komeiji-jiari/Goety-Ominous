package com.qiuyue.goetyominous.client.rlyeh.ac;

import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;

public final class ExtinctionFont {

    public static final ResourceLocation ASH =
            new ResourceLocation(GoetyOminous.MOD_ID, "extinction_ash");

    public static final int ASH_TEXT_COLOUR = 0x2B0E06;

    private static final int[] PALETTE = {
            0x3A1A10, 0x6B2A12, 0x9C4318, 0xC96A22, 0xE8A54B, 0x6B2A12
    };
    private static final double LOOP_SECONDS = 11.0;
    private static final double TWINKLE_SECONDS = 17.0;
    private static final double TWINKLE_DEPTH = 0.06;

    private ExtinctionFont() {
    }

    public static MutableComponent ash(String text) {
        MutableComponent out = Component.empty();
        int n = Math.max(text.length(), 1);
        long now = System.currentTimeMillis();
        double phase = (now % (long) (LOOP_SECONDS * 1000.0)) / (LOOP_SECONDS * 1000.0);
        double twinklePhase = (now % (long) (TWINKLE_SECONDS * 1000.0)) / (TWINKLE_SECONDS * 1000.0) * Math.PI * 2.0;
        for (int i = 0; i < text.length(); i++) {
            double t = (double) i / n - phase;
            int colour = sample(PALETTE, t);
            double twinkle = (1.0 - TWINKLE_DEPTH * 0.5) + TWINKLE_DEPTH * 0.5 * Math.sin(twinklePhase + i * 0.19);
            colour = scale(colour, twinkle);
            out.append(Component.literal(String.valueOf(text.charAt(i)))
                    .withStyle(Style.EMPTY.withColor(colour).withFont(ASH)));
        }
        return out;
    }

    public static boolean isExtinctionFont(ResourceLocation font) {
        return font != null
                && GoetyOminous.MOD_ID.equals(font.getNamespace())
                && font.getPath().startsWith("extinction_");
    }

    public static boolean isAshMark(Style style) {
        TextColor colour = style.getColor();
        return colour != null && colour.getValue() == ASH_TEXT_COLOUR;
    }

    private static int sample(int[] palette, double t) {
        t -= Math.floor(t);
        double scaled = t * (palette.length - 1);
        int idx = (int) Math.floor(scaled);
        double f = scaled - idx;
        return lerp(palette[idx], palette[Math.min(idx + 1, palette.length - 1)], f);
    }

    private static int lerp(int a, int b, double f) {
        int ar = (a >> 16) & 0xFF, ag = (a >> 8) & 0xFF, ab = a & 0xFF;
        int br = (b >> 16) & 0xFF, bg = (b >> 8) & 0xFF, bb = b & 0xFF;
        int r = (int) Math.round(ar + (br - ar) * f);
        int g = (int) Math.round(ag + (bg - ag) * f);
        int bl = (int) Math.round(ab + (bb - ab) * f);
        return (r << 16) | (g << 8) | bl;
    }

    private static int scale(int rgb, double k) {
        int r = Math.min(255, (int) Math.round(((rgb >> 16) & 0xFF) * k));
        int g = Math.min(255, (int) Math.round(((rgb >> 8) & 0xFF) * k));
        int b = Math.min(255, (int) Math.round((rgb & 0xFF) * k));
        return (r << 16) | (g << 8) | b;
    }
}
