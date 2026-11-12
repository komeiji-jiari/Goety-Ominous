package com.qiuyue.goetyominous.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public final class GrimMonarchEchoRenderTypes extends RenderType {
    private static final Map<ResourceLocation, RenderType> BODY = new HashMap<>();
    private static final Map<ResourceLocation, RenderType> GLOW = new HashMap<>();

    private GrimMonarchEchoRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int size,
                                       boolean affectsCrumbling, boolean sortOnUpload,
                                       Runnable setup, Runnable clear) {
        super(name, format, mode, size, affectsCrumbling, sortOnUpload, setup, clear);
    }

    public static RenderType body(ResourceLocation texture) {
        if (GrimMonarchEchoShaders.body() == null) {
            return RenderType.entityTranslucent(texture);
        }
        return BODY.computeIfAbsent(texture, value -> create("grimmonarch_echo_body/" + value,
                DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 16384, false, false,
                CompositeState.builder()
                        .setShaderState(new RenderStateShard.ShaderStateShard(() -> {
                            ShaderInstance shader = GrimMonarchEchoShaders.body();
                            if (shader != null) {
                                var uniform = shader.safeGetUniform("Dissolve");
                                if (uniform != null) {
                                    uniform.set(GrimMonarchEchoShaders.dissolve());
                                }
                            }
                            return shader;
                        }))
                        .setTextureState(new RenderStateShard.TextureStateShard(value, false, false))
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setCullState(NO_CULL)
                        .setWriteMaskState(COLOR_WRITE)
                        .setLightmapState(LIGHTMAP)
                        .createCompositeState(false)));
    }

    public static RenderType glow(ResourceLocation texture) {
        if (GrimMonarchEchoShaders.glow() == null) {
            return RenderType.entityTranslucentEmissive(texture);
        }
        return GLOW.computeIfAbsent(texture, value -> create("grimmonarch_echo_glow/" + value,
                DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 16384, false, false,
                CompositeState.builder()
                        .setShaderState(new RenderStateShard.ShaderStateShard(() -> {
                            ShaderInstance shader = GrimMonarchEchoShaders.glow();
                            if (shader != null) {
                                var uniform = shader.safeGetUniform("Dissolve");
                                if (uniform != null) {
                                    uniform.set(GrimMonarchEchoShaders.dissolve());
                                }
                            }
                            return shader;
                        }))
                        .setTextureState(new RenderStateShard.TextureStateShard(value, false, false))
                        .setTransparencyState(ADDITIVE_TRANSPARENCY)
                        .setCullState(NO_CULL)
                        .setWriteMaskState(COLOR_WRITE)
                        .setLightmapState(LIGHTMAP)
                        .createCompositeState(false)));
    }
}
