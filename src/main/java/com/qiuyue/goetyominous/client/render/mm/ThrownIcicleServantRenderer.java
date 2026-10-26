package com.qiuyue.goetyominous.client.render.mm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.mm.ThrownIcicleServantModel;
import com.qiuyue.goetyominous.common.entities.ally.mm.ThrownIcicleServant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ThrownIcicleServantRenderer extends EntityRenderer<ThrownIcicleServant> {
    private static final ResourceLocation SERVANT_TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/mutant_frozen_zombie_servant.png");

    private final ThrownIcicleServantModel model;

    public ThrownIcicleServantRenderer(EntityRendererProvider.Context p_174296_) {
        super(p_174296_);
        this.model = new ThrownIcicleServantModel(p_174296_.bakeLayer(ModEntityLayers.THROWN_ICICLE_SERVANT_LAYER));
    }

    public void render(ThrownIcicleServant p_116111_, float p_116112_, float p_116113_, PoseStack p_116114_, MultiBufferSource p_116115_, int p_116116_) {
        p_116114_.pushPose();
        p_116114_.mulPose(Axis.YP.rotationDegrees(Mth.lerp(p_116113_, p_116111_.yRotO, p_116111_.getYRot()) + 180.0F));
        p_116114_.mulPose(Axis.XP.rotationDegrees(Mth.lerp(p_116113_, p_116111_.xRotO, p_116111_.getXRot())));
        p_116114_.translate(0.0, -0.8, 1.0);
        RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(this.getTextureLocation(p_116111_));
        VertexConsumer vertexconsumer = p_116115_.getBuffer(RENDER_TYPE);
        this.model.renderToBuffer(p_116114_, vertexconsumer, p_116116_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        p_116114_.popPose();
        super.render(p_116111_, p_116112_, p_116113_, p_116114_, p_116115_, p_116116_);
    }

    public ResourceLocation getTextureLocation(ThrownIcicleServant entity) {
        return SERVANT_TEXTURE;
    }
}
