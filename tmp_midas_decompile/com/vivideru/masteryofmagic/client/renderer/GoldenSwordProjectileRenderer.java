/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.client.renderer.entity.ItemRenderer
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.client.renderer.texture.TextureAtlas
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.phys.Vec3
 */
package com.vivideru.masteryofmagic.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.vivideru.masteryofmagic.entity.GoldenSwordProjectileEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;

public final class GoldenSwordProjectileRenderer
extends EntityRenderer<GoldenSwordProjectileEntity> {
    private final ItemRenderer itemRenderer;

    public GoldenSwordProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.m_174025_();
        this.f_114477_ = 0.0f;
    }

    public void render(GoldenSwordProjectileEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        poseStack.m_85836_();
        VecRotation rotation = GoldenSwordProjectileRenderer.rotationFromDirection(entity.getRenderDirection(partialTick));
        poseStack.m_252781_(Axis.f_252436_.m_252977_(-rotation.yaw));
        poseStack.m_252781_(Axis.f_252403_.m_252977_(rotation.pitch - 45.0f));
        float dissolveScale = entity.getDissolveScale(partialTick);
        poseStack.m_85841_(2.35f * dissolveScale, 2.35f * dissolveScale, 2.35f * dissolveScale);
        this.itemRenderer.m_269128_(entity.m_7846_(), ItemDisplayContext.GROUND, 0xF000F0, OverlayTexture.f_118083_, poseStack, buffers, entity.m_9236_(), entity.m_19879_());
        poseStack.m_85849_();
        super.m_7392_((Entity)entity, entityYaw, partialTick, poseStack, buffers, packedLight);
    }

    private static VecRotation rotationFromDirection(Vec3 direction) {
        double x = direction.f_82479_;
        double y = direction.f_82480_;
        double z = direction.f_82481_;
        double horizontal = Math.sqrt(x * x + z * z);
        float yaw = (float)(Mth.m_14136_((double)z, (double)x) * 57.2957763671875);
        float pitch = (float)(Mth.m_14136_((double)y, (double)horizontal) * 57.2957763671875);
        return new VecRotation(yaw, pitch);
    }

    public ResourceLocation getTextureLocation(GoldenSwordProjectileEntity entity) {
        return TextureAtlas.f_118259_;
    }

    private record VecRotation(float yaw, float pitch) {
    }
}

