package com.qiuyue.goetyominous.client.render.projectile;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class QuadStarRenderTypes extends RenderType {

    public static final ResourceLocation BEAM_TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID,
            "textures/entity/projectile/quad_star_line.png");
    public static final ResourceLocation GLOW_TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID,
            "textures/entity/projectile/quad_star_glow.png");
    public static final ResourceLocation TRAIL_TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID,
            "textures/entity/projectile/quad_star_white.png");

    private static final RenderType STAR = star("quad_star", false);
    private static final RenderType STAR_GHOST = star("quad_star_ghost", true);

    private static final RenderType BEAM_CORE = beam("quad_star_beam_core", BEAM_TEXTURE,
            RenderStateShard.TRANSLUCENT_TRANSPARENCY);

    private static final RenderType BEAM_GLOW = beam("quad_star_beam_glow", GLOW_TEXTURE,
            RenderStateShard.LIGHTNING_TRANSPARENCY);

    private static final RenderType BEAM_TRAIL = beam("quad_star_beam_trail", TRAIL_TEXTURE,
            RenderStateShard.LIGHTNING_TRANSPARENCY);

    private QuadStarRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufSize,
                                boolean affectsCrumbling, boolean sortOnUpload, Runnable setup, Runnable clear) {
        super(name, format, mode, bufSize, affectsCrumbling, sortOnUpload, setup, clear);
    }

    private static RenderType star(String name, boolean ghost) {
        return create(name, DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS, 256,
                false, false, RenderType.CompositeState.builder()
                        .setShaderState(new RenderStateShard.ShaderStateShard(FourPointStarShaders::fourPointStar))
                        .setTransparencyState(RenderStateShard.LIGHTNING_TRANSPARENCY)
                        .setCullState(RenderStateShard.NO_CULL)
                        .setWriteMaskState(ghost ? RenderStateShard.COLOR_WRITE : RenderStateShard.COLOR_DEPTH_WRITE)
                        .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                        .createCompositeState(false));
    }

    private static RenderType beam(String name, ResourceLocation texture,
            RenderStateShard.TransparencyStateShard transparency) {
        return create(name, DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256,
                false, true, RenderType.CompositeState.builder()
                        .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
                        .setTextureState(new RenderStateShard.TextureStateShard(texture, true, false))
                        .setTransparencyState(transparency)
                        .setCullState(RenderStateShard.NO_CULL)
                        .setLightmapState(RenderStateShard.LIGHTMAP)
                        .setOverlayState(RenderStateShard.OVERLAY)
                        .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                        .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                        .createCompositeState(true));
    }

    public static RenderType star() {
        return STAR;
    }

    public static RenderType starGhost() {
        return STAR_GHOST;
    }

    public static RenderType beamCore() {
        return BEAM_CORE;
    }

    public static RenderType beamGlow() {
        return BEAM_GLOW;
    }

    public static RenderType beamTrail() {
        return BEAM_TRAIL;
    }
}
