package com.qiuyue.goetyominous.client.render.ac;

import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.client.render.model.ac.ModelAtlatitanServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.AtlatitanServant;
import com.qiuyue.goetyominous.common.events.AtlatitanRenderEvents;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
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

@OnlyIn(Dist.CLIENT)
public class AtlatitanServantRiderLayer extends RenderLayer<AtlatitanServant, ModelAtlatitanServant> {

    public AtlatitanServantRiderLayer(RenderAtlatitanServant render) {
        super(render);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, AtlatitanServant entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        float bodyYaw = entity.yBodyRotO + (entity.yBodyRot - entity.yBodyRotO) * partialTicks;
        if (entity.isVehicle()) {
            float animationIntensity = 0.0F;
            if (entity.getAnimation() == AtlatitanServant.ANIMATION_STOMP) {
                animationIntensity = ACMath.cullAnimationTick(entity.getAnimationTick(), 1.0F, AtlatitanServant.ANIMATION_STOMP, partialTicks, 0, 30);
            }
            Vec3 offset = new Vec3(0.0D, -5.75D, -0.5F - 0.7F * animationIntensity);
            Vec3 ridePos = this.getParentModel().getRiderPosition(offset);
            for (Entity passenger : entity.getPassengers()) {
                if (passenger == Minecraft.getInstance().player && Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
                    continue;
                }
                poseStack.pushPose();
                poseStack.translate(ridePos.x, ridePos.y - 1.65D + passenger.getBbHeight(), ridePos.z);
                poseStack.mulPose(Axis.XN.rotationDegrees(180.0F));
                poseStack.mulPose(Axis.YN.rotationDegrees(360.0F - bodyYaw));
                AtlatitanRenderEvents.releaseRenderingEntity(passenger.getUUID());
                AtlatitanRenderEvents.setCurrentRenderingPassenger(passenger.getUUID());
                try {
                    renderPassenger(passenger, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks, poseStack, bufferIn, packedLightIn);
                } finally {
                    AtlatitanRenderEvents.clearCurrentRenderingPassenger();
                }
                AtlatitanRenderEvents.blockRenderingEntity(passenger.getUUID());
                poseStack.popPose();
            }
        }
    }

    public static <E extends Entity> void renderPassenger(E entityIn, double x, double y, double z, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLight) {
        EntityRenderer renderer = null;
        EntityRenderDispatcher manager = Minecraft.getInstance().getEntityRenderDispatcher();
        try {
            renderer = manager.getRenderer(entityIn);
            if (renderer != null) {
                try {
                    renderer.render(entityIn, yaw, partialTicks, poseStack, bufferIn, packedLight);
                } catch (Throwable throwable1) {
                    throw new ReportedException(CrashReport.forThrowable(throwable1, "Rendering entity in world"));
                }
            }
        } catch (Throwable throwable3) {
            CrashReport crashreport = CrashReport.forThrowable(throwable3, "Rendering entity in world");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Entity being rendered");
            entityIn.fillCrashReportCategory(crashreportcategory);
            CrashReportCategory crashreportcategory1 = crashreport.addCategory("Renderer details");
            crashreportcategory1.setDetail("Assigned renderer", renderer);
            crashreportcategory1.setDetail("Rotation", yaw);
            crashreportcategory1.setDetail("Delta", partialTicks);
            throw new ReportedException(crashreport);
        }
    }
}
