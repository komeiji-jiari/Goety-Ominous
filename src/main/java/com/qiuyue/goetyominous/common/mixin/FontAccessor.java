package com.qiuyue.goetyominous.common.mixin;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Font.class)
public interface FontAccessor {

    @Invoker("getFontSet")
    FontSet rlyeh$getFontSet(ResourceLocation fontLocation);

    @Accessor("filterFishyGlyphs")
    boolean rlyeh$getFilterFishyGlyphs();

    @Invoker("renderChar")
    void rlyeh$renderChar(BakedGlyph glyph, boolean bold, boolean italic, float boldOffset,
                          float x, float y, Matrix4f matrix, VertexConsumer buffer,
                          float red, float green, float blue, float alpha, int packedLight);
}
