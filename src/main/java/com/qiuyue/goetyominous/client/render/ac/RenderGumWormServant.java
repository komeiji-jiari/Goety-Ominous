package com.qiuyue.goetyominous.client.render.ac;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.client.render.model.ac.ModelGumWormServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.GumWormServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderGumWormServant extends MobRenderer<GumWormServant, ModelGumWormServant> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("alexscaves:textures/entity/gum_worm.png");

    public RenderGumWormServant(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelGumWormServant(), 1.2F);
    }

    @Override
    protected float getFlipDegrees(GumWormServant entity) {
        return 0.0F;
    }

    @Override
    protected void setupRotations(GumWormServant entity, PoseStack poseStack, float bob, float yawIn, float partialTicks) {
        if (this.isShaking(entity)) {
            yawIn += (float) (Math.cos((double) entity.tickCount * 3.25D) * Math.PI * (double) 0.4F);
        }
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yawIn));
        poseStack.translate(0.0F, 1.0F, 0.0F);
        poseStack.mulPose(Axis.XP.rotationDegrees(-entity.getViewXRot(partialTicks)));
        poseStack.translate(0.0F, -1.0F, 0.0F);
        if (isEntityUpsideDown(entity)) {
            poseStack.translate(0.0F, entity.getBbHeight() + 0.1F, 0.0F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        }
    }

    @Override
    public ResourceLocation getTextureLocation(GumWormServant entity) {
        return TEXTURE;
    }
}
