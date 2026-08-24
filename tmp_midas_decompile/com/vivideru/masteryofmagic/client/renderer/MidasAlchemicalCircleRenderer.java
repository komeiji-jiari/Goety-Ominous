/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.PoseStack$Pose
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.math.Axis
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
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

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.vivideru.masteryofmagic.entity.MidasAlchemicalCircleEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public final class MidasAlchemicalCircleRenderer
extends EntityRenderer<MidasAlchemicalCircleEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("goety_mastery_of_magic", "textures/entities/midas_alchemical_orb.png");
    private static final int LATITUDE_SEGMENTS = 16;
    private static final int LONGITUDE_SEGMENTS = 32;

    public MidasAlchemicalCircleRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.f_114477_ = 0.0f;
    }

    public void render(MidasAlchemicalCircleEntity entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int light) {
        float age = (float)entity.f_19797_ + partialTick;
        float pulse = 1.0f + 0.07f * (float)Math.sin(age * 0.22f);
        poseStack.m_85836_();
        poseStack.m_252781_(Axis.f_252436_.m_252977_(entity.getSpinDegrees(partialTick)));
        poseStack.m_252781_(Axis.f_252529_.m_252977_(18.0f + 12.0f * (float)Math.sin(age * 0.037f)));
        poseStack.m_85841_(pulse, pulse, pulse);
        MidasAlchemicalCircleRenderer.renderSphere(poseStack.m_85850_(), buffers.m_6299_(RenderType.m_110488_((ResourceLocation)TEXTURE)), 1.0f, age * 0.0018f);
        poseStack.m_85849_();
        super.m_7392_((Entity)entity, yaw, partialTick, poseStack, buffers, 0xF000F0);
    }

    private static void renderSphere(PoseStack.Pose pose, VertexConsumer consumer, float radius, float uOffset) {
        Matrix4f matrix = pose.m_252922_();
        Matrix3f normal = pose.m_252943_();
        for (int latitude = 0; latitude < 16; ++latitude) {
            double phi0 = -1.5707963267948966 + Math.PI * (double)latitude / 16.0;
            double phi1 = -1.5707963267948966 + Math.PI * (double)(latitude + 1) / 16.0;
            float y0 = radius * (float)Math.sin(phi0);
            float y1 = radius * (float)Math.sin(phi1);
            float ring0 = radius * (float)Math.cos(phi0);
            float ring1 = radius * (float)Math.cos(phi1);
            float v0 = 1.0f - (float)latitude / 16.0f;
            float v1 = 1.0f - (float)(latitude + 1) / 16.0f;
            for (int longitude = 0; longitude < 32; ++longitude) {
                double theta0 = Math.PI * 2 * (double)longitude / 32.0;
                double theta1 = Math.PI * 2 * (double)(longitude + 1) / 32.0;
                float u0 = (float)longitude / 32.0f + uOffset;
                float u1 = (float)(longitude + 1) / 32.0f + uOffset;
                MidasAlchemicalCircleRenderer.vertex(consumer, matrix, normal, ring0 * (float)Math.cos(theta0), y0, ring0 * (float)Math.sin(theta0), u0, v0);
                MidasAlchemicalCircleRenderer.vertex(consumer, matrix, normal, ring0 * (float)Math.cos(theta1), y0, ring0 * (float)Math.sin(theta1), u1, v0);
                MidasAlchemicalCircleRenderer.vertex(consumer, matrix, normal, ring1 * (float)Math.cos(theta1), y1, ring1 * (float)Math.sin(theta1), u1, v1);
                MidasAlchemicalCircleRenderer.vertex(consumer, matrix, normal, ring1 * (float)Math.cos(theta0), y1, ring1 * (float)Math.sin(theta0), u0, v1);
            }
        }
    }

    private static void vertex(VertexConsumer consumer, Matrix4f matrix, Matrix3f normal, float x, float y, float z, float u, float v) {
        float length = (float)Math.sqrt(x * x + y * y + z * z);
        consumer.m_252986_(matrix, x, y, z).m_6122_(255, 255, 255, 255).m_7421_(u, v).m_86008_(OverlayTexture.f_118083_).m_85969_(0xF000F0).m_252939_(normal, x / length, y / length, z / length).m_5752_();
    }

    protected int getBlockLightLevel(MidasAlchemicalCircleEntity e, BlockPos p) {
        return 15;
    }

    public ResourceLocation getTextureLocation(MidasAlchemicalCircleEntity e) {
        return TEXTURE;
    }
}

