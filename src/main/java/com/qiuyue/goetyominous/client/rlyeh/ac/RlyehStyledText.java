package com.qiuyue.goetyominous.client.rlyeh.ac;

import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.common.mixin.FontAccessor;
import com.qiuyue.goetyominous.common.mixin.ac.GameRendererAccessor;
import com.qiuyue.goetyominous.compat.mod.ModernUiTextCompat;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.font.glyphs.EmptyGlyph;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;

public final class RlyehStyledText {

    private static final int FILL_DEFAULT = 0xFFFFFFFF;
    private static final int FULL_BRIGHT = 15728880;

    private static final int OUTLINE_ARGB = 0xFF03150E;
    private static final float OUTLINE_OFFSET = 0.5F;
    private static final float OUTLINE_ALPHA = 0.85F;

    private static final float SHADOW_OFFSET = 0.5F;

    private static final BufferBuilder GLYPH_BUILDER = new BufferBuilder(512);

    private RlyehStyledText() {
    }

    public static void draw(Font font, FormattedCharSequence seq, int x, int y, Matrix4f matrix) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        emitOutline(font, seq, x, y, matrix);

        MultiBufferSource.BufferSource shadow = MultiBufferSource.immediate(GLYPH_BUILDER);
        emitRun(font, seq, x + SHADOW_OFFSET, y + SHADOW_OFFSET, matrix, shadow,
                FILL_DEFAULT, false, false, true, 1.0F);
        shadow.endBatch();

        ShaderInstance material = RlyehShaders.textGlyph();
        if (material == null) {
            MultiBufferSource.BufferSource fill = MultiBufferSource.immediate(GLYPH_BUILDER);
            emitRun(font, seq, x, y, matrix, fill, FILL_DEFAULT, false, false, false, 1.0F);
            fill.endBatch();
            return;
        }
        RenderSystem.disableDepthTest();
        material.safeGetUniform("uTheme").set(RlyehShaders.RLYEH_THEME);
        material.safeGetUniform("uModernUi").set(1.0F);
        swapModernUiNormal(material, () -> swapVanillaText(material, () -> {
            MultiBufferSource.BufferSource immediate = MultiBufferSource.immediate(GLYPH_BUILDER);
            emitRun(font, seq, x, y, matrix, immediate, FILL_DEFAULT, false, true, false, 1.0F);
            immediate.endBatch();
        }));
        RenderSystem.enableDepthTest();
    }

    public static void drawNative(Font font, FormattedCharSequence seq, int x, int y, Matrix4f matrix) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        emitOutline(font, seq, x, y, matrix);

        MultiBufferSource.BufferSource shadow = MultiBufferSource.immediate(GLYPH_BUILDER);
        emitRun(font, seq, x + SHADOW_OFFSET, y + SHADOW_OFFSET, matrix, shadow,
                FILL_DEFAULT, false, false, true, 1.0F);
        shadow.endBatch();

        ShaderInstance material = RlyehShaders.textGlyph();
        if (material == null) {
            MultiBufferSource.BufferSource fill = MultiBufferSource.immediate(GLYPH_BUILDER);
            emitRun(font, seq, x, y, matrix, fill, FILL_DEFAULT, false, false, false, 1.0F);
            fill.endBatch();
            return;
        }
        RenderSystem.disableDepthTest();
        material.safeGetUniform("uTheme").set(RlyehShaders.RLYEH_THEME);
        material.safeGetUniform("uModernUi").set(0.0F);
        swapVanillaText(material, () -> {
            MultiBufferSource.BufferSource immediate = MultiBufferSource.immediate(GLYPH_BUILDER);
            emitRun(font, seq, x, y, matrix, immediate, FILL_DEFAULT, false, true, false, 1.0F);
            immediate.endBatch();
        });
        RenderSystem.enableDepthTest();
    }

    private static void swapVanillaText(ShaderInstance replacement, Runnable draw) {
        ShaderInstance previous = GameRendererAccessor.getRlyehRendertypeTextShader();
        GameRendererAccessor.setRlyehRendertypeTextShader(replacement);
        try {
            draw.run();
        } finally {
            GameRendererAccessor.setRlyehRendertypeTextShader(previous);
        }
    }

    private static void swapModernUiNormal(ShaderInstance replacement, Runnable draw) {
        ShaderInstance previous = ModernUiTextCompat.getNormalShader();
        ModernUiTextCompat.setNormalShader(replacement);
        try {
            draw.run();
        } finally {
            ModernUiTextCompat.setNormalShader(previous);
        }
    }

    private static void emitOutline(Font font, FormattedCharSequence seq, float x, float y, Matrix4f matrix) {
        MultiBufferSource.BufferSource outline = MultiBufferSource.immediate(GLYPH_BUILDER);
        for (int i = 0; i < 8; i++) {
            float angle = (float) (i * Math.PI / 4.0);
            float ox = (float) (Math.cos(angle) * OUTLINE_OFFSET);
            float oy = (float) (Math.sin(angle) * OUTLINE_OFFSET);
            emitRun(font, seq, x + ox, y + oy, matrix, outline, OUTLINE_ARGB, true, false, false,
                    OUTLINE_ALPHA);
        }
        outline.endBatch();
    }

    private static void emitRun(Font font, FormattedCharSequence seq, float x, float y, Matrix4f matrix,
                                MultiBufferSource source, int colour, boolean forced, boolean jadeOrdinal,
                                boolean shadow, float alpha) {
        FontSet set = ((FontAccessor) font).rlyeh$getFontSet(Style.DEFAULT_FONT);
        boolean filter = ((FontAccessor) font).rlyeh$getFilterFishyGlyphs();
        float[] pen = {x};
        seq.accept((position, style, codePoint) -> {
            GlyphInfo info = set.getGlyphInfo(codePoint, filter);
            boolean bold = style.isBold();
            BakedGlyph glyph = set.getGlyph(codePoint);
            if (!(glyph instanceof EmptyGlyph)) {
                int argb = forced ? colour
                        : (jadeOrdinal ? jadeProtocol(position) : glyphColour(style));
                if (shadow) {
                    argb = shadowColor(argb);
                }
                if (alpha < 1.0F) {
                    int a = (int) (((argb >>> 24) & 0xFF) * alpha);
                    argb = (a << 24) | (argb & 0x00FFFFFF);
                }
                float a = ((argb >> 24) & 0xFF) / 255.0F;
                if (a <= 0.0F) {
                    a = 1.0F;
                }
                float r = ((argb >> 16) & 0xFF) / 255.0F;
                float g = ((argb >> 8) & 0xFF) / 255.0F;
                float b = (argb & 0xFF) / 255.0F;
                float boldOffset = bold ? info.getBoldOffset() : 0.0F;
                RenderType type = glyph.renderType(Font.DisplayMode.NORMAL);
                VertexConsumer buffer = source.getBuffer(type);
                ((FontAccessor) font).rlyeh$renderChar(glyph, bold, style.isItalic(), boldOffset,
                        pen[0], y, matrix, buffer, r, g, b, a, FULL_BRIGHT);
            }
            pen[0] += info.getAdvance(bold);
            return true;
        });
    }

    private static int glyphColour(Style style) {
        TextColor colour = style.getColor();
        return colour != null ? (0xFF000000 | colour.getValue()) : FILL_DEFAULT;
    }

    private static int shadowColor(int argb) {
        int r = (int) (((argb >> 16) & 0xFF) * 0.25F);
        int g = (int) (((argb >> 8) & 0xFF) * 0.25F);
        int b = (int) ((argb & 0xFF) * 0.25F);
        return (argb & 0xFF000000) | (r << 16) | (g << 8) | b;
    }

    private static int jadeProtocol(int ordinal) {
        int clamped = Math.max(0, Math.min(255, ordinal));
        return (0xFF << 24) | (32 << 16) | (clamped << 8);
    }
}
