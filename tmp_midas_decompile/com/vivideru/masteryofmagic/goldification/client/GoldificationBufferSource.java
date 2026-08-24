/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.resources.ResourceLocation
 */
package com.vivideru.masteryofmagic.goldification.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.vivideru.masteryofmagic.goldification.client.GoldificationVertexConsumer;
import java.util.Locale;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public final class GoldificationBufferSource
implements MultiBufferSource {
    private static final ResourceLocation GOLD_TEXTURE = new ResourceLocation("minecraft", "textures/block/gold_block.png");
    private final MultiBufferSource delegate;

    private GoldificationBufferSource(MultiBufferSource delegate) {
        this.delegate = delegate;
    }

    public static MultiBufferSource wrap(MultiBufferSource source) {
        return source instanceof GoldificationBufferSource ? source : new GoldificationBufferSource(source);
    }

    public VertexConsumer m_6299_(RenderType renderType) {
        String name = GoldificationBufferSource.renderTypeName(renderType);
        if (name.contains("text") || name.contains("line") || name.contains("shadow")) {
            return this.delegate.m_6299_(renderType);
        }
        RenderType goldRenderType = GoldificationBufferSource.goldRenderType(name);
        if (goldRenderType != null) {
            return this.delegate.m_6299_(goldRenderType);
        }
        return GoldificationVertexConsumer.wrap(this.delegate.m_6299_(renderType));
    }

    private static String renderTypeName(RenderType renderType) {
        String description;
        int openingBracket = (description = renderType.toString().toLowerCase(Locale.ROOT)).indexOf(91);
        int nameStart = openingBracket >= 0 ? openingBracket + 1 : 0;
        int nameEnd = description.indexOf(58, nameStart);
        if (nameEnd < 0) {
            nameEnd = description.indexOf(91, nameStart);
        }
        return nameEnd > nameStart ? description.substring(nameStart, nameEnd) : description.substring(nameStart);
    }

    private static RenderType goldRenderType(String name) {
        if (name.contains("glint")) {
            return null;
        }
        if (name.contains("eyes")) {
            return RenderType.m_110488_((ResourceLocation)GOLD_TEXTURE);
        }
        if (name.contains("entity_translucent")) {
            return RenderType.m_110473_((ResourceLocation)GOLD_TEXTURE);
        }
        if (name.contains("entity_solid")) {
            return RenderType.m_110446_((ResourceLocation)GOLD_TEXTURE);
        }
        if (name.contains("entity_cutout_no_cull_z_offset")) {
            return RenderType.m_110464_((ResourceLocation)GOLD_TEXTURE);
        }
        if (name.contains("entity_cutout") || name.contains("armor_cutout")) {
            return RenderType.m_110458_((ResourceLocation)GOLD_TEXTURE);
        }
        return null;
    }
}

