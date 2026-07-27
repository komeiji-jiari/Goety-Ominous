package com.qiuyue.goetyominus.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominus.GoetyOminous;
import com.qiuyue.goetyominus.client.init.ModEntityLayers;
import com.qiuyue.goetyominus.client.render.model.ChannellerModel;
import com.qiuyue.goetyominus.common.entities.hostile.cultists.Channeller;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChannellerRenderer extends MobRenderer<Channeller, ChannellerModel<Channeller>> {
    private static final ResourceLocation CHAIN_TEXTURE =
            new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/cultist/channeller_connection.png");
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/cultist/channeller.png");
    private static final RenderType CHAIN_RENDER_TYPE = RenderType.entityCutoutNoCull(CHAIN_TEXTURE, false);

    public ChannellerRenderer(EntityRendererProvider.Context context) {
        super(context, new ChannellerModel<>(context.bakeLayer(ModEntityLayers.CHANNELLER_LAYER)), 0.5F);
    }

    private Vec3 getLerpedPosition(LivingEntity entity, double heightOffset, float partialTicks) {
        double x = Mth.lerp(partialTicks, entity.xo, entity.getX());
        double y = Mth.lerp(partialTicks, entity.yo, entity.getY()) + heightOffset;
        double z = Mth.lerp(partialTicks, entity.zo, entity.getZ());
        return new Vec3(x, y, z);
    }

    @Override
    public void render(Channeller entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);

        LivingEntity ally = entity.getAlly();
        if (ally == null || !entity.isPraying()) return;

        Vec3 cameraPos = this.entityRenderDispatcher.camera.getPosition();

        double startHeight = entity.getBbHeight() * 0.5D;
        Vec3 start = this.getLerpedPosition(entity, startHeight, partialTicks);
        Vec3 end = this.getLerpedPosition(ally, ally.getBbHeight() * 0.5D, partialTicks);

        poseStack.pushPose();
        poseStack.translate(-start.x, -(start.y - startHeight), -start.z);

        Vec3 diff = end.subtract(start);
        Vec3 camVec = start.subtract(cameraPos);
        Vec3 crossDir = diff.cross(camVec).normalize().scale(0.25D);

        float length = (float) diff.length();
        float age = entity.tickCount + partialTicks;
        float offset = -(age * 0.06F);

        VertexConsumer consumer = buffer.getBuffer(CHAIN_RENDER_TYPE);
        PoseStack.Pose pose = poseStack.last();

        this.vertex(consumer, pose, start.add(crossDir), offset, 0);
        this.vertex(consumer, pose, start.subtract(crossDir), offset, 1);
        this.vertex(consumer, pose, end.subtract(crossDir), length * 2.0F + offset, 1);
        this.vertex(consumer, pose, end.add(crossDir), length * 2.0F + offset, 0);

        poseStack.popPose();
    }

    private void vertex(VertexConsumer consumer, PoseStack.Pose pose, Vec3 pos, float u, float v) {
        consumer.vertex(pose.pose(), (float) pos.x, (float) pos.y, (float) pos.z)
                .color(-1)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(pose.normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(Channeller entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(Channeller entity, PoseStack poseStack, float partialTickTime) {
        poseStack.scale(0.9375F, 0.9375F, 0.9375F);
    }
}
