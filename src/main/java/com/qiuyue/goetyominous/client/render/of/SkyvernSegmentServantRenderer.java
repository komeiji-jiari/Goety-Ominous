package com.qiuyue.goetyominous.client.render.of;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.of.SkyvernServantBodyModel;
import com.qiuyue.goetyominous.client.render.model.of.SkyvernServantTailModel;
import com.qiuyue.goetyominous.common.entities.ally.of.SkyvernSegmentServant;
import com.qiuyue.goetyominous.common.entities.ally.of.SkyvernServant;
import com.unusualmodding.opposing_force.entity.Skyvern.SkyvernVariant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class SkyvernSegmentServantRenderer extends EntityRenderer<SkyvernSegmentServant> {
    private final SkyvernServantBodyModel BODY;
    private final SkyvernServantTailModel TAIL;

    public SkyvernSegmentServantRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.BODY = new SkyvernServantBodyModel(context.bakeLayer(ModEntityLayers.SKYVERN_SERVANT_BODY_LAYER));
        this.TAIL = new SkyvernServantTailModel(context.bakeLayer(ModEntityLayers.SKYVERN_SERVANT_TAIL_LAYER));
    }

    @Override
    public boolean shouldRender(@NotNull SkyvernSegmentServant entity, @NotNull Frustum camera, double x, double y, double z) {
        if (super.shouldRender(entity, camera, x, y, z)) {
            return true;
        }
        Entity front = entity.getFrontEntity();
        if (front != null) {
            Vec3 back = entity.position();
            Vec3 frontPos = front.position();
            return camera.isVisible(new AABB(frontPos.x, frontPos.y, frontPos.z, back.x, back.y, back.z));
        }
        return false;
    }

    @Override
    public void render(@NotNull SkyvernSegmentServant entity, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        Entity back = entity.getBackEntity();
        float yRotLerp = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());
        float xRotLerp = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
        poseStack.pushPose();
        poseStack.translate(0.0D, 1.0D, 0.0D);
        poseStack.mulPose(Axis.YN.rotationDegrees(yRotLerp));
        poseStack.mulPose(Axis.XP.rotationDegrees(xRotLerp + 180.0F));
        poseStack.translate(0.0D, -0.5D, 0.0D);
        Entity head = entity.getHeadEntity();
        if (head instanceof LivingEntity living && LivingEntityRenderer.isEntityUpsideDown(living)) {
            poseStack.translate(0.0F, entity.getBbHeight() + 1.25F, 0.0F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        }
        ResourceLocation texture = this.getTextureLocation(entity);
        VertexConsumer vertexConsumer = bufferSource.getBuffer(entity.isGhost()
                ? RenderType.entityTranslucent(texture)
                : RenderType.entityCutoutNoCull(texture));
        if (back == null) {
            this.TAIL.setupAnim(entity, 0.0F, 0.0F, (float) entity.tickCount + partialTicks, 0.0F, 0.0F);
            this.TAIL.renderToBuffer(poseStack, vertexConsumer, packedLight, getOverlayCoords(entity), 1.0F, 1.0F, 1.0F, 1.0F);
        } else {
            this.BODY.setupAnim(entity, 0.0F, 0.0F, (float) entity.tickCount + partialTicks, 0.0F, 0.0F);
            this.BODY.renderToBuffer(poseStack, vertexConsumer, packedLight, getOverlayCoords(entity), 1.0F, 1.0F, 1.0F, 1.0F);
        }
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SkyvernSegmentServant entity) {
        Entity head = entity.getHeadEntity();
        if (head instanceof SkyvernServant skyvern) {
            return SkyvernServantRenderer.textureFor(skyvern.getVariant());
        }
        return SkyvernServantRenderer.textureFor(SkyvernVariant.CLOUDY);
    }

    public static int getOverlayCoords(SkyvernSegmentServant segment) {
        return OverlayTexture.pack(OverlayTexture.u(0.0F), OverlayTexture.v(segment.renderHurtFlag));
    }
}
