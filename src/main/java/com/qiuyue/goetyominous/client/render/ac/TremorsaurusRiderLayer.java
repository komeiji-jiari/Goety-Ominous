package com.qiuyue.goetyominous.client.render.ac;

import com.github.alexmodguy.alexscaves.AlexsCaves;
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
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 乘骑层:把骑手(玩家/友军)渲染到撼地龙背部。移植自 Alex's Caves TremorsaurusRiderLayer。
 */
@OnlyIn(Dist.CLIENT)
public class TremorsaurusRiderLayer extends RenderLayer<TremorsaurusServant, ModelTremorsaurusServant> {

    public TremorsaurusRiderLayer(RenderTremorsaurusServant renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, TremorsaurusServant entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        float bodyRot = entitylivingbaseIn.yBodyRotO + (entitylivingbaseIn.yBodyRot - entitylivingbaseIn.yBodyRotO) * partialTicks;
        if (entitylivingbaseIn.isVehicle()) {
            Vec3 pos = this.getParentModel().getRiderPosition(new Vec3(0.0D, -0.5D, -0.75D));
            for (Entity passenger : entitylivingbaseIn.getPassengers()) {
                if (passenger == Minecraft.getInstance().player && Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
                    continue;
                }
                AlexsCaves.PROXY.releaseRenderingEntity(passenger.getUUID());
                matrixStackIn.pushPose();
                matrixStackIn.translate(pos.x, pos.y - 1.65F + passenger.getBbHeight(), pos.z);
                matrixStackIn.mulPose(Axis.XN.rotationDegrees(180.0F));
                matrixStackIn.mulPose(Axis.YN.rotationDegrees(360.0F - bodyRot));
                renderPassenger(passenger, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks, matrixStackIn, bufferIn, packedLightIn);
                matrixStackIn.popPose();
                AlexsCaves.PROXY.blockRenderingEntity(passenger.getUUID());
            }
        }
    }

    public static <E extends Entity> void renderPassenger(E entityIn, double x, double y, double z, float rotationYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferIn, int packedLightIn) {
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
