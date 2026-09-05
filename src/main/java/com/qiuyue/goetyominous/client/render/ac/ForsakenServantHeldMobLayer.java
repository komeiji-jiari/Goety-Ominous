package com.qiuyue.goetyominous.client.render.ac;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.client.render.model.ac.ModelForsakenServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.ForsakenServant;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ForsakenServantHeldMobLayer extends RenderLayer<ForsakenServant, ModelForsakenServant> {

    public ForsakenServantHeldMobLayer(RenderForsakenServant renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, ForsakenServant forsaken, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        Entity heldMob = forsaken.getHeldMob();
        if (heldMob != null) {
            AlexsCaves.PROXY.releaseRenderingEntity(heldMob.getUUID());
            float vehicleRot = forsaken.yBodyRotO + (forsaken.yBodyRot - forsaken.yBodyRotO) * partialTicks;
            float riderRot = 0.0F;
            float animationIntensity = ACMath.cullAnimationTick(forsaken.getAnimationTick(), 1.0F, forsaken.getAnimation(), partialTicks, 25, 30) * 0.75F;
            boolean right = forsaken.getAnimation() == ForsakenServant.ANIMATION_RIGHT_PICKUP;
            if (heldMob instanceof LivingEntity) {
                LivingEntity living = (LivingEntity) heldMob;
                riderRot = living.yBodyRotO + (living.yBodyRot - living.yBodyRotO) * partialTicks;
            }
            matrixStackIn.pushPose();
            Vec3 offset = right ? new Vec3(0.8F + animationIntensity, 0.8F - animationIntensity, 0.35F * heldMob.getBbHeight() - animationIntensity * 0.5F) : new Vec3(-0.8F - animationIntensity, 0.8F - animationIntensity, 0.35F * heldMob.getBbHeight() - animationIntensity * 0.5F);
            Vec3 handPosition = ((ModelForsakenServant) this.getParentModel()).getHandPosition(right, offset);
            matrixStackIn.translate(handPosition.x, handPosition.y, handPosition.z);
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180.0F));
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(vehicleRot - riderRot));
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
