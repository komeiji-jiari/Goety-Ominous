package com.qiuyue.goetyominous.client.render.ac;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.client.render.model.ac.ModelGummyBearServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.GummyBearServant;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GummyBearServantHeldMobLayer extends RenderLayer<GummyBearServant, ModelGummyBearServant> {

    public GummyBearServantHeldMobLayer(RenderGummyBearServant renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, GummyBearServant bear, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        Entity heldMob = bear.getHeldMob();
        if (heldMob != null) {
            AlexsCaves.PROXY.releaseRenderingEntity(heldMob.getUUID());
            float bearScale = bear.getScale();
            matrixStackIn.pushPose();
            this.getParentModel().translateToHand(HumanoidArm.RIGHT, matrixStackIn);
            matrixStackIn.translate(0.1F * bearScale, 0.7F * bearScale, -0.3F * bearScale);
            matrixStackIn.mulPose(Axis.XN.rotationDegrees(180.0F));
            matrixStackIn.mulPose(Axis.YN.rotationDegrees(-90.0F));
            matrixStackIn.mulPose(Axis.XN.rotationDegrees(-10.0F));
            if (!AlexsCaves.PROXY.isFirstPersonPlayer(heldMob)) {
                this.renderEntity(heldMob, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks, matrixStackIn, bufferIn, packedLightIn);
            }
            matrixStackIn.popPose();
            AlexsCaves.PROXY.blockRenderingEntity(heldMob.getUUID());
        }
    }

    public <E extends Entity> void renderEntity(E entityIn, double x, double y, double z, float rotationYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferIn, int packedLightIn) {
        EntityRenderer<? super E> renderer = null;
        EntityRenderDispatcher entityrenderermanager = Minecraft.getInstance().getEntityRenderDispatcher();
        try {
            renderer = entityrenderermanager.getRenderer(entityIn);
            if (renderer != null) {
                renderer.render(entityIn, rotationYaw, partialTicks, matrixStack, bufferIn, packedLightIn);
            }
        } catch (Throwable throwable1) {
            throw new ReportedException(CrashReport.forThrowable(throwable1, "Rendering entity in world"));
        }
    }
}
