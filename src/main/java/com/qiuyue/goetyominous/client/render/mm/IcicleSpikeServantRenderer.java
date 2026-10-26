package com.qiuyue.goetyominous.client.render.mm;

import com.alexander.mutantmore.util.MiscUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.mm.IcicleSpikeServantModel;
import com.qiuyue.goetyominous.common.entities.ally.mm.IcicleSpikeServant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IcicleSpikeServantRenderer extends EntityRenderer<IcicleSpikeServant> {
    private static final ResourceLocation SERVANT_TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/mutant_frozen_zombie_servant.png");

    private final IcicleSpikeServantModel<IcicleSpikeServant> model;

    public IcicleSpikeServantRenderer(EntityRendererProvider.Context p_174420_) {
        super(p_174420_);
        this.model = new IcicleSpikeServantModel(p_174420_.bakeLayer(ModEntityLayers.ICICLE_SPIKE_SERVANT_LAYER));
    }

    public void render(IcicleSpikeServant p_116111_, float p_116112_, float p_116113_, PoseStack p_116114_, MultiBufferSource p_116115_, int p_116116_) {
        p_116114_.pushPose();
        p_116114_.scale(-1.0F, -1.0F, 1.0F);
        p_116114_.translate(0.0, -1.8F, 0.0);
        this.model.setupAnim(p_116111_, 0.0F, 0.0F, (float) p_116111_.tickCount + p_116113_, 0.0F, 0.0F);
        this.model.root.xRot += MiscUtils.degToRad(p_116111_.getXRot());
        this.model.root.yRot += MiscUtils.degToRad(p_116111_.getYRot());
        if (p_116111_.spikeAnimationState.isStarted()) {
            VertexConsumer vertexconsumer = p_116115_.getBuffer(this.model.renderType(this.getTextureLocation(p_116111_)));
            this.model.renderToBuffer(p_116114_, vertexconsumer, p_116116_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }
        p_116114_.popPose();
        super.render(p_116111_, p_116112_, p_116113_, p_116114_, p_116115_, p_116116_);
    }

    public ResourceLocation getTextureLocation(IcicleSpikeServant entity) {
        return SERVANT_TEXTURE;
    }
}
