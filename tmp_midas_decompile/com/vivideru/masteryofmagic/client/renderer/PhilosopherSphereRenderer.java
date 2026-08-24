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
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  org.joml.Matrix3f
 *  org.joml.Matrix4f
 */
package com.vivideru.masteryofmagic.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.vivideru.masteryofmagic.entity.PhilosopherSphereEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public final class PhilosopherSphereRenderer
extends EntityRenderer<PhilosopherSphereEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("goety_mastery_of_magic", "textures/misc/philosophers_stone.png");

    public PhilosopherSphereRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.f_114477_ = 0.0f;
    }

    public void render(PhilosopherSphereEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        poseStack.m_85836_();
        poseStack.m_85837_(0.0, (double)entity.m_20206_() * 0.5, 0.0);
        poseStack.m_252781_(this.f_114476_.m_253208_());
        poseStack.m_252781_(Axis.f_252436_.m_252977_(180.0f));
        float charge = entity.getBeamChargeProgress(partialTick);
        float pulseSpeed = Mth.m_14179_((float)charge, (float)0.28f, (float)0.82f);
        float pulse = 2.65f + charge * 0.52f + (0.12f + charge * 0.16f) * Mth.m_14031_((float)(((float)entity.f_19797_ + partialTick) * pulseSpeed));
        poseStack.m_85841_(pulse, pulse, pulse);
        PoseStack.Pose pose = poseStack.m_85850_();
        VertexConsumer consumer = buffers.m_6299_(RenderType.m_110488_((ResourceLocation)TEXTURE));
        PhilosopherSphereRenderer.vertex(consumer, pose.m_252922_(), pose.m_252943_(), -0.5f, -0.5f, 0.0f, 0.0f, 1.0f);
        PhilosopherSphereRenderer.vertex(consumer, pose.m_252922_(), pose.m_252943_(), 0.5f, -0.5f, 0.0f, 1.0f, 1.0f);
        PhilosopherSphereRenderer.vertex(consumer, pose.m_252922_(), pose.m_252943_(), 0.5f, 0.5f, 0.0f, 1.0f, 0.0f);
        PhilosopherSphereRenderer.vertex(consumer, pose.m_252922_(), pose.m_252943_(), -0.5f, 0.5f, 0.0f, 0.0f, 0.0f);
        poseStack.m_85849_();
        super.m_7392_((Entity)entity, entityYaw, partialTick, poseStack, buffers, packedLight);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f matrix, Matrix3f normal, float x, float y, float z, float u, float v) {
        consumer.m_252986_(matrix, x, y, z).m_6122_(255, 255, 255, 255).m_7421_(u, v).m_86008_(OverlayTexture.f_118083_).m_85969_(0xF000F0).m_252939_(normal, 0.0f, 1.0f, 0.0f).m_5752_();
    }

    protected int getBlockLightLevel(PhilosopherSphereEntity entity, BlockPos position) {
        return 15;
    }

    public ResourceLocation getTextureLocation(PhilosopherSphereEntity entity) {
        return TEXTURE;
    }
}

