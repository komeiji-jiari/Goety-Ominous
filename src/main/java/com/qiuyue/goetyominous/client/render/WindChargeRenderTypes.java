package com.qiuyue.goetyominous.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class WindChargeRenderTypes extends RenderType {
    private WindChargeRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufSize,
                                  boolean affectsCrumbling, boolean sortOnUpload, Runnable setup, Runnable clear) {
        super(name, format, mode, bufSize, affectsCrumbling, sortOnUpload, setup, clear);
    }

    public static RenderType breezeWind(ResourceLocation texture, float u, float v) {
        return create("breeze_wind", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 1536,
                false, true, RenderType.CompositeState.builder()
                        .setShaderState(new RenderStateShard.ShaderStateShard(WindChargeShaders::breezeWind))
                        .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                        .setTexturingState(new RenderStateShard.OffsetTexturingStateShard(u, v))
                        .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                        .setCullState(RenderStateShard.NO_CULL)
                        .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                        .setLightmapState(RenderStateShard.LIGHTMAP)
                        .createCompositeState(false));
    }
}
