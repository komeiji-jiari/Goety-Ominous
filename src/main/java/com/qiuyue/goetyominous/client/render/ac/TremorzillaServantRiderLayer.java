package com.qiuyue.goetyominous.client.render.ac;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.client.render.model.ac.ModelTremorzillaServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.TremorzillaServant;
import com.qiuyue.goetyominous.common.events.TremorzillaRenderEvents;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 特雷莫兹拉仆从骑乘者渲染层。忠实移植 AC 原版 TremorzillaRiderLayer,
 * 将骑乘者渲染在颈部(translateToNeck)并根据燃烧/游泳进度调整位置与姿态。
 * 通过 TremorzillaRenderEvents 的 block/release 渲染锁,阻止骑乘者自身的
 * Level 渲染通道重复渲染(否则会在颈部与座位各渲染一次)。
 */
@OnlyIn(Dist.CLIENT)
public class TremorzillaServantRiderLayer extends RenderLayer<TremorzillaServant, ModelTremorzillaServant> {

    public TremorzillaServantRiderLayer(RenderTremorzillaServant render) {
        super(render);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, TremorzillaServant entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        float bodyYaw = entity.yBodyRotO + (entity.yBodyRot - entity.yBodyRotO) * partialTicks;
        if (entity.isVehicle()) {
            float swimProgress = entity.getSwimAmount(partialTicks);
            float burnProgress = entity.getBeamProgress(partialTicks);
            for (Entity passenger : entity.getPassengers()) {
                if (passenger == Minecraft.getInstance().player && Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
                    continue;
                }
                poseStack.pushPose();
                this.getParentModel().translateToNeck(poseStack);
                poseStack.translate(0.0F, 0.5F - burnProgress * 0.5F - swimProgress * 0.5F, 0.35F - burnProgress * 0.5F - swimProgress * 0.5F);
                poseStack.mulPose(Axis.XN.rotationDegrees(190.0F - burnProgress * 40.0F));
                poseStack.mulPose(Axis.YN.rotationDegrees(360.0F - bodyYaw));
                TremorzillaRenderEvents.releaseRenderingEntity(passenger.getUUID());
                TremorzillaRenderEvents.setCurrentRenderingPassenger(passenger.getUUID());
                try {
                    renderPassenger(passenger, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks, poseStack, bufferIn, packedLightIn);
                } finally {
                    TremorzillaRenderEvents.clearCurrentRenderingPassenger();
                }
                TremorzillaRenderEvents.blockRenderingEntity(passenger.getUUID());
                poseStack.popPose();
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public static <E extends Entity> void renderPassenger(E entityIn, double x, double y, double z, float yaw, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferIn, int packedLight) {
        EntityRenderer render = null;
        EntityRenderDispatcher manager = Minecraft.getInstance().getEntityRenderDispatcher();
        try {
            render = manager.getRenderer(entityIn);
            if (render == null) {
                return;
            }
            try {
                render.render(entityIn, yaw, partialTicks, matrixStack, bufferIn, packedLight);
            } catch (Throwable throwable1) {
                throw new ReportedException(CrashReport.forThrowable(throwable1, "Rendering entity in world"));
            }
        } catch (Throwable throwable3) {
            CrashReport crashreport = CrashReport.forThrowable(throwable3, "Rendering entity in world");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Entity being rendered");
            entityIn.fillCrashReportCategory(crashreportcategory);
            CrashReportCategory crashreportcategory1 = crashreport.addCategory("Renderer details");
            crashreportcategory1.setDetail("Assigned renderer", render);
            crashreportcategory1.setDetail("Rotation", Float.valueOf(yaw));
            crashreportcategory1.setDetail("Delta", Float.valueOf(partialTicks));
            throw new ReportedException(crashreport);
        }
    }
}
