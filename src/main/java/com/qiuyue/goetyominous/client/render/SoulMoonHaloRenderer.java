package com.qiuyue.goetyominous.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;

public final class SoulMoonHaloRenderer {

    private static final float MOON_HALF = 20.0F * DarkSceneRenderer.MOON_SCALE;

    private SoulMoonHaloRenderer() {
    }

    public static void draw(PoseStack poseStack, float partialTick) {
        ShaderInstance shader = SoulMoonShaders.halo();
        if (shader == null || Minecraft.getInstance().level == null) {
            return;
        }

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(
                Minecraft.getInstance().level.getTimeOfDay(partialTick) * 360.0F));
        org.joml.Matrix4f pose = poseStack.last().pose();

        int phase = Minecraft.getInstance().level.getMoonPhase();
        int col = phase % 4;
        int row = phase / 4 % 2;
        // UV of the current phase cell inside one 4x2 state band. The shader
        // picks the normal (upper) or eclipse (lower) band via the Eclipse uniform.
        float u0 = col / 4.0F;
        float v0 = row / 2.0F;
        float u1 = (col + 1) / 4.0F;
        float v1 = (row + 1) / 2.0F;

        int previousTexture = RenderSystem.getShaderTexture(0);
        ShaderInstance previousShader = RenderSystem.getShader();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        // Alpha blend so the opaque disc genuinely covers stars; the halo's
        // partial alpha still reads as a soft glow against the night sky.
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderTexture(0, SoulMoonShaders.MOON_TEXTURE);
        RenderSystem.setShader(() -> shader);

        // darkness() tracks the slice darken/release transition (0..1), so the
        // eclipse fades in exactly while the space-cut skill is active.
        var eclipseUniform = shader.safeGetUniform("Eclipse");
        if (eclipseUniform != null) {
            eclipseUniform.set(DarkSceneRenderer.darkness());
        }

        BufferBuilder builder = Tesselator.getInstance().getBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        try {
            vertex(builder, pose, -MOON_HALF, MOON_HALF, u1, v1);
            vertex(builder, pose, MOON_HALF, MOON_HALF, u0, v1);
            vertex(builder, pose, MOON_HALF, -MOON_HALF, u0, v0);
            vertex(builder, pose, -MOON_HALF, -MOON_HALF, u1, v0);
            BufferUploader.drawWithShader(builder.end());
        } finally {
            RenderSystem.setShaderTexture(0, previousTexture);
            RenderSystem.defaultBlendFunc();
            // 🔴 Keep blending ENABLED on exit: renderSky runs with blend on the
            // whole way through, and calling disableBlend() here leaked that state
            // into the rest of the frame (particles drawn later stopped blending).
            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            if (previousShader != null) {
                RenderSystem.setShader(() -> previousShader);
            }
            poseStack.popPose();
        }
    }

    private static void vertex(BufferBuilder builder, org.joml.Matrix4f pose,
                               float x, float z, float u, float v) {
        builder.vertex(pose, x, -100.0F, z).uv(u, v).endVertex();
    }
}
