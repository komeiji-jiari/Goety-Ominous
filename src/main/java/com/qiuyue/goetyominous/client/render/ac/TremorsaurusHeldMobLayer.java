package com.qiuyue.goetyominous.client.render.ac;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.entity.living.SubterranodonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.client.render.model.ac.ModelTremorsaurusServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.TremorsaurusServant;
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
public class TremorsaurusHeldMobLayer extends RenderLayer<TremorsaurusServant, ModelTremorsaurusServant> {

    public TremorsaurusHeldMobLayer(RenderTremorsaurusServant renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, TremorsaurusServant entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        Entity heldMob = entitylivingbaseIn.getHeldMob();
        if (heldMob != null) {
            float heldRot = heldMob.yRotO + (heldMob.getYRot() - heldMob.yRotO) * partialTicks;
            boolean tall = heldMob.getBbHeight() > heldMob.getBbWidth() + 0.2F;
            AlexsCaves.PROXY.releaseRenderingEntity(heldMob.getUUID());
            matrixStackIn.pushPose();
            this.getParentModel().translateToMouth(matrixStackIn);
            matrixStackIn.translate(0, heldMob.getBbWidth() * 0.35F + 0.2F, -1.0F);
            if (heldMob instanceof SubterranodonEntity subterranodon) {
                matrixStackIn.translate(0, subterranodon.getFlyProgress(partialTicks) * -0.5F, 0);
            }
            if (tall) {
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees(90.0F));
            }
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180.0F));
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(heldRot + 180.0F));
            if (!tall) {
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
            }
            matrixStackIn.translate(0, -heldMob.getBbHeight() * 0.5F, 0);
            if (!AlexsCaves.PROXY.isFirstPersonPlayer(heldMob)) {
                renderEntity(heldMob, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks, matrixStackIn, bufferIn, packedLightIn);
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
