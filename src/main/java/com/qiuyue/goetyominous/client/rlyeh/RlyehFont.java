package com.qiuyue.goetyominous.client.rlyeh;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import com.qiuyue.goetyominous.GoetyOminous;

public final class RlyehFont {

    public static final ResourceLocation ABYSS =
            new ResourceLocation(GoetyOminous.MOD_ID, "rlyeh_abyss");

    private static final int[] PALETTE = {
            0x2E6E9E, 0x4F9AD6, 0x8FD0FF, 0xE6F5FF, 0x5AA0E0, 0x2E6E9E
    };
    private static final double LOOP_SECONDS = 7.0;
    private static final double TWINKLE_SECONDS = 5.5;

    private RlyehFont() {
    }

    public static MutableComponent abyss(String text) {
        MutableComponent out = Component.empty();
        int n = Math.max(text.length(), 1);
        long now = System.currentTimeMillis();
        double phase = (now % (long) (LOOP_SECONDS * 1000.0)) / (LOOP_SECONDS * 1000.0);
        double twinklePhase = (now % (long) (TWINKLE_SECONDS * 1000.0)) / (TWINKLE_SECONDS * 1000.0) * Math.PI * 2.0;
        for (int i = 0; i < text.length(); i++) {
            double t = (double) i / n - phase;
            int colour = sample(PALETTE, t);
            double twinkle = 0.90 + 0.10 * Math.sin(twinklePhase + i * 0.35);
            colour = scale(colour, twinkle);
            out.append(Component.literal(String.valueOf(text.charAt(i)))
                    .withStyle(Style.EMPTY.withColor(colour).withFont(ABYSS)));
        }
        return out;
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
