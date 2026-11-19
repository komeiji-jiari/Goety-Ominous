package com.qiuyue.goetyominous.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.ModModelLayer;
import com.Polarice3.Goety.client.render.SkullLordRenderer;
import com.Polarice3.Goety.client.render.model.SkullLordModel;
import com.Polarice3.Goety.common.entities.hostile.BoneLord;
import com.qiuyue.goetyominous.common.entities.hostile.OminousSkullLord;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class OminousSkullLordRenderer extends MobRenderer<OminousSkullLord, SkullLordModel<OminousSkullLord>> {

    private static final ResourceLocation LOCATION = Goety.location("textures/entity/skull_lord/skull_lord.png");
    private static final ResourceLocation VULNERABLE = Goety.location("textures/entity/skull_lord/skull_lord_vulnerable.png");
    private static final ResourceLocation CHARGE = Goety.location("textures/entity/skull_lord/skull_lord_charging.png");

    public OminousSkullLordRenderer(EntityRendererProvider.Context context) {
        super(context, new SkullLordModel<>(context.bakeLayer(ModModelLayer.SKULL_LORD)), 0.5F);
    }

    @Override
    public void render(OminousSkullLord entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        BoneLord boneLord = entity.getBoneLord();
        if (boneLord == null) {
            return;
        }
        poseStack.pushPose();
        Vec3 camPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        Vec3 start = new Vec3(Mth.lerp(partialTicks, entity.xOld, entity.getX()),
                Mth.lerp(partialTicks, entity.yOld, entity.getY()),
                Mth.lerp(partialTicks, entity.zOld, entity.getZ()));
        poseStack.translate(-start.x, -start.y, -start.z);
        Vec3 end = new Vec3(Mth.lerp(partialTicks, boneLord.xOld, boneLord.getX()),
                Mth.lerp(partialTicks, boneLord.yOld, boneLord.getY()) + boneLord.getBbHeight(),
                Mth.lerp(partialTicks, boneLord.zOld, boneLord.getZ()));
        VertexConsumer vertexConsumer = buffer.getBuffer(SkullLordRenderer.CONNECTION);
        Vec3 offset = end.subtract(start);
        Vec3 sight = camPos.subtract(start).scale(-1.0D);
        Vec3 sideOffset = offset.cross(sight).normalize();
        float age = (float) entity.tickCount + partialTicks;
        float uOffset = age * 0.05F;
        PoseStack.Pose pose = poseStack.last();
        this.vertex(vertexConsumer, pose, start.add(sideOffset), uOffset, 0.0F, packedLight);
        this.vertex(vertexConsumer, pose, start.add(sideOffset.scale(-1.0D)), uOffset, 1.0F, packedLight);
        this.vertex(vertexConsumer, pose, end.add(sideOffset.scale(-1.0D)), (float) (offset.length() / 2.0D) + uOffset, 1.0F, packedLight);
        this.vertex(vertexConsumer, pose, end.add(sideOffset), (float) (offset.length() / 2.0D) + uOffset, 0.0F, packedLight);
        poseStack.popPose();
    }

    private void vertex(VertexConsumer consumer, PoseStack.Pose pose, Vec3 vec, float u, float v, int light) {
        consumer.vertex(pose.pose(), (float) vec.x, (float) vec.y, (float) vec.z)
                .color(-1)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(pose.normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    @Override
    protected int getBlockLightLevel(OminousSkullLord entity, BlockPos pos) {
        return 15;
    }

    @Override
    protected float getWhiteOverlayProgress(OminousSkullLord entity, float partialTicks) {
        if (entity.isShockWave()) {
            float swelling = entity.getSwelling(partialTicks);
            return (int) (swelling * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(swelling, 0.5F, 1.0F);
        }
        return super.getWhiteOverlayProgress(entity, partialTicks);
    }

    @Override
    public ResourceLocation getTextureLocation(OminousSkullLord entity) {
        if (entity.isCharging()) {
            return CHARGE;
        }
        return entity.isInvulnerable() ? LOCATION : VULNERABLE;
    }
}
