package com.qiuyue.goetyominous.client.render.projectile;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class WindChargeRenderTypes extends RenderType {
    private static final VertexFormat BREEZE_WIND_FORMAT = new VertexFormat(ImmutableMap.<String, VertexFormatElement>builder()
            .put("Position", DefaultVertexFormat.ELEMENT_POSITION)
            .put("Color", DefaultVertexFormat.ELEMENT_COLOR)
            .put("UV0", DefaultVertexFormat.ELEMENT_UV0)
            .put("UV2", DefaultVertexFormat.ELEMENT_UV2)
            .build());

    private WindChargeRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufSize,
                                  boolean affectsCrumbling, boolean sortOnUpload, Runnable setup, Runnable clear) {
        super(name, format, mode, bufSize, affectsCrumbling, sortOnUpload, setup, clear);
    }

    public static RenderType breezeWind(ResourceLocation texture, float u, float v) {
        return create("breeze_wind", BREEZE_WIND_FORMAT, VertexFormat.Mode.QUADS, 1536,
                false, true, RenderType.CompositeState.builder()
                        .setShaderState(new RenderStateShard.ShaderStateShard(com.qiuyue.goetyominous.client.render.WindChargeShaders::breezeWind))
                        .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                        .setTexturingState(new RenderStateShard.OffsetTexturingStateShard(u, v))
                        .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                        .setCullState(RenderStateShard.NO_CULL)
                        .setLightmapState(RenderStateShard.LIGHTMAP)
                        .createCompositeState(false));
    }
}
