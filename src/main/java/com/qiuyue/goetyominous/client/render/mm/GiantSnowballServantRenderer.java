package com.qiuyue.goetyominous.client.render.mm;

import com.alexander.mutantmore.animation.sine_wave_animations.SineWaveAnimationUtils;
import com.alexander.mutantmore.animation.sine_wave_animations.SineWaveMotionTypes;
import com.alexander.mutantmore.util.MiscUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.mm.GiantSnowballServantModel;
import com.qiuyue.goetyominous.common.entities.ally.mm.GiantSnowballServant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GiantSnowballServantRenderer extends EntityRenderer<GiantSnowballServant> {
    private static final ResourceLocation SERVANT_TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/mutant_frozen_zombie_servant.png");

    private final GiantSnowballServantModel model;

    public GiantSnowballServantRenderer(EntityRendererProvider.Context p_174296_) {
        super(p_174296_);
        this.model = new GiantSnowballServantModel(p_174296_.bakeLayer(ModEntityLayers.GIANT_SNOWBALL_SERVANT_LAYER));
    }

    public void render(GiantSnowballServant p_115373_, float p_115374_, float p_115375_, PoseStack p_115376_, MultiBufferSource p_115377_, int p_115378_) {
        p_115376_.pushPose();
        float scale = p_115373_.getSizeScale();
        p_115376_.scale(scale, scale, scale);
        p_115376_.translate(0.0, -0.25, 0.0);
        p_115376_.mulPose(Axis.YP.rotationDegrees(Mth.lerp(p_115375_, p_115373_.yRotO, p_115373_.getYRot()) - 90.0F));
        VertexConsumer vertexconsumer = p_115377_.getBuffer(this.model.renderType(this.getTextureLocation(p_115373_)));
        this.model.snowBlock.xRot = 0.0F;
        this.model.snowBlock.yRot = MiscUtils.degToRad(90.0F);
        this.model.snowBlock.zRot = 0.0F;
        SineWaveAnimationUtils.addConstantMotionToModelPart(this.model.snowBlock, 400.0F, SineWaveAnimationUtils.getTick(p_115373_.tickCount, true), 1.0F, 1.0F, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.addConstantMotionToModelPart(this.model.snowBlock, 100.0F, SineWaveAnimationUtils.getTick(p_115373_.tickCount, true), 1.0F, 1.0F, SineWaveMotionTypes.ROTATION_Y);
        this.model.renderToBuffer(p_115376_, vertexconsumer, p_115378_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        p_115376_.popPose();
        super.render(p_115373_, p_115374_, p_115375_, p_115376_, p_115377_, p_115378_);
    }

    public ResourceLocation getTextureLocation(GiantSnowballServant entity) {
        return SERVANT_TEXTURE;
    }
}
