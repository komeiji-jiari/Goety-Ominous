/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.client.render.ModRenderType
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.PoseStack$Pose
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.core.BlockPos
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.Entity
 *  org.joml.Matrix3f
 *  org.joml.Matrix4f
 */
package com.vivideru.masteryofmagic.client.renderer;

import com.Polarice3.Goety.client.render.ModRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.vivideru.masteryofmagic.entity.PhilosopherWindSlashEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public final class PhilosopherWindSlashRenderer
extends EntityRenderer<PhilosopherWindSlashEntity> {
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[]{new ResourceLocation("goety", "textures/entity/projectiles/slash/1.png"), new ResourceLocation("goety", "textures/entity/projectiles/slash/2.png"), new ResourceLocation("goety", "textures/entity/projectiles/slash/3.png"), new ResourceLocation("goety", "textures/entity/projectiles/slash/4.png")};

    public PhilosopherWindSlashRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.f_114477_ = 0.0f;
    }

    public void render(PhilosopherWindSlashEntity entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        poseStack.m_85836_();
        poseStack.m_252781_(entity.getRenderOrientation());
        ++entity.animationTime;
        float width = 28.0f;
        PhilosopherWindSlashRenderer.drawSlash(poseStack.m_85850_(), entity, buffers, width, 0, -0.105f, 255, 25, 255, 232);
        PhilosopherWindSlashRenderer.drawSlash(poseStack.m_85850_(), entity, buffers, width * 0.76f, 1, -0.095f, 255, 224, 255, 220);
        poseStack.m_85849_();
        super.m_7392_((Entity)entity, yaw, partialTick, poseStack, buffers, packedLight);
    }

    private static void drawSlash(PoseStack.Pose pose, PhilosopherWindSlashEntity entity, MultiBufferSource buffers, float width, int frameOffset, float planeOffset, int red, int green, int blue, int alpha) {
        Matrix4f matrix = pose.m_252922_();
        Matrix3f normal = pose.m_252943_();
        VertexConsumer consumer = buffers.m_6299_(ModRenderType.wraith((ResourceLocation)PhilosopherWindSlashRenderer.texture(entity, frameOffset)));
        float halfWidth = width * 0.5f;
        float halfDepth = 1.75f;
        PhilosopherWindSlashRenderer.vertex(consumer, matrix, normal, -halfWidth, planeOffset, -halfDepth, 0.0f, 1.0f, red, green, blue, alpha);
        PhilosopherWindSlashRenderer.vertex(consumer, matrix, normal, halfWidth, planeOffset, -halfDepth, 1.0f, 1.0f, red, green, blue, alpha);
        PhilosopherWindSlashRenderer.vertex(consumer, matrix, normal, halfWidth, planeOffset, halfDepth, 1.0f, 0.0f, red, green, blue, alpha);
        PhilosopherWindSlashRenderer.vertex(consumer, matrix, normal, -halfWidth, planeOffset, halfDepth, 0.0f, 0.0f, red, green, blue, alpha);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f matrix, Matrix3f normal, float x, float y, float z, float u, float v, int red, int green, int blue, int alpha) {
        consumer.m_252986_(matrix, x, y, z).m_6122_(red, green, blue, alpha).m_7421_(u, v).m_86008_(OverlayTexture.f_118083_).m_85969_(0xF000F0).m_252939_(normal, 0.0f, 1.0f, 0.0f).m_5752_();
    }

    private static ResourceLocation texture(PhilosopherWindSlashEntity entity, int offset) {
        int frame = (entity.animationTime / 6 + offset) % TEXTURES.length;
        return TEXTURES[frame];
    }

    protected int getBlockLightLevel(PhilosopherWindSlashEntity entity, BlockPos position) {
        return 15;
    }

    public ResourceLocation getTextureLocation(PhilosopherWindSlashEntity entity) {
        return PhilosopherWindSlashRenderer.texture(entity, 0);
    }
}

