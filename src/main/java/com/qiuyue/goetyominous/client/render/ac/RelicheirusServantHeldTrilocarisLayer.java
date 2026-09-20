package com.qiuyue.goetyominous.client.render.ac;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.entity.living.TrilocarisEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.client.render.model.ac.ModelRelicheirusServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.RelicheirusServant;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RelicheirusServantHeldTrilocarisLayer extends RenderLayer<RelicheirusServant, ModelRelicheirusServant> {

    public RelicheirusServantHeldTrilocarisLayer(RenderRelicheirusServant renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, RelicheirusServant relicheirus, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        Entity heldMob = relicheirus.getHeldMob();
        if (heldMob instanceof TrilocarisEntity && relicheirus.getAnimation() == RelicheirusServant.ANIMATION_EAT_TRILOCARIS && relicheirus.getAnimationTick() > 15) {
            float riderRot = heldMob.yRotO + (heldMob.getYRot() - heldMob.yRotO) * partialTicks;
            AlexsCaves.PROXY.releaseRenderingEntity(heldMob.getUUID());
            matrixStackIn.pushPose();
            this.getParentModel().translateToMouth(matrixStackIn);
            matrixStackIn.translate(0.0F, -1.34F, -1.0F);
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180.0F));
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(riderRot + 180.0F));
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
            matrixStackIn.translate(0.0F, -heldMob.getBbHeight() * 0.5F, 0.0F);
            this.renderEntity(heldMob, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks, matrixStackIn, bufferIn, packedLightIn);
            matrixStackIn.popPose();
            AlexsCaves.PROXY.blockRenderingEntity(heldMob.getUUID());
        }
    }

    public <E extends Entity> void renderEntity(E entityIn, double x, double y, double z, float yaw, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferIn, int packedLight) {
        EntityRenderer<? super E> render = null;
        EntityRenderDispatcher manager = Minecraft.getInstance().getEntityRenderDispatcher();
        try {
            render = manager.getRenderer(entityIn);
            if (render != null) {
                render.render(entityIn, yaw, partialTicks, matrixStack, bufferIn, packedLight);
            }
        } catch (Throwable throwable) {
            throw new ReportedException(CrashReport.forThrowable(throwable, "Rendering entity in world"));
        }
    }
}
