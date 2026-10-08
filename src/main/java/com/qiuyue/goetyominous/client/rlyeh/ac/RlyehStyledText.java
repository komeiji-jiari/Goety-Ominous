package com.qiuyue.goetyominous.client.rlyeh.ac;

import com.qiuyue.goetyominous.common.mixin.FontAccessor;
import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.font.glyphs.EmptyGlyph;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;

public final class RlyehStyledText {

    private static final int OUTLINE_ARGB = 0xFF000000;
    private static final int FILL_DEFAULT = 0xFFFFFFFF;
    private static final int FULL_BRIGHT = 15728880;

    private RlyehStyledText() {
    }

    public static void draw(Font font, FormattedCharSequence seq, int x, int y, Matrix4f matrix,
                            MultiBufferSource source) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        MultiBufferSource.BufferSource outline = MultiBufferSource.immediate(new BufferBuilder(256));
        for (int oy = -1; oy <= 1; oy++) {
            for (int ox = -1; ox <= 1; ox++) {
                if (ox == 0 && oy == 0) {
                    continue;
                }
                emitRun(font, seq, x + ox, y + oy, matrix, outline, OUTLINE_ARGB, true);
            }
        }
        outline.endBatch();

        MultiBufferSource.BufferSource fill = MultiBufferSource.immediate(new BufferBuilder(256));
        emitRun(font, seq, x, y, matrix, fill, FILL_DEFAULT, false);
        fill.endBatch();
    }

    private static void emitRun(Font font, FormattedCharSequence seq, float x, float y, Matrix4f matrix,
                                MultiBufferSource source, int colour, boolean forced) {
        FontSet set = ((FontAccessor) font).rlyeh$getFontSet(Style.DEFAULT_FONT);
        boolean filter = ((FontAccessor) font).rlyeh$getFilterFishyGlyphs();
        float[] pen = {x};
        seq.accept((position, style, codePoint) -> {
            GlyphInfo info = set.getGlyphInfo(codePoint, filter);
            boolean bold = style.isBold();
            BakedGlyph glyph = set.getGlyph(codePoint);
            if (!(glyph instanceof EmptyGlyph)) {
                int argb = forced ? colour : glyphColour(style);
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
}
