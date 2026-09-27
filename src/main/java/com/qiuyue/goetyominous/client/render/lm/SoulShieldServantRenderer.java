package com.qiuyue.goetyominous.client.render.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.client.render.model.lm.SoulShieldModel;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.SoulShield;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SoulShieldServantRenderer extends EntityRenderer<SoulShield> {

    private static final ResourceLocation SOUL_SHIELD = new ResourceLocation(
            "legendary_monsters", "textures/entity/posessed_paladin/soul_shield.png");
    private static final ResourceLocation SOUL_SHIELD_RED = new ResourceLocation(
            "legendary_monsters", "textures/entity/posessed_paladin/soul_shield_red.png");

    private static final float MODEL_SCALE = 1.75F;

    private final SoulShieldModel model;

    public SoulShieldServantRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new SoulShieldModel(SoulShieldModel.createBodyLayer().bakeRoot());
    }

    @Override
    public void render(SoulShield entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        double dx = (double) entity.getDestinationX() - entity.getX();
        double dy = (double) entity.getDestinationY() - entity.getY();
        double dz = (double) entity.getDestinationZ() - entity.getZ();
        float horizontalDist = (float) Math.sqrt(dx * dx + dz * dz);
        float yawRad = (float) Math.toRadians(Math.toDegrees(Math.atan2(dz, dx)) - 90.0D);
        float pitchRad = (float) Math.toRadians(-Math.toDegrees(Math.atan2(dy, horizontalDist)));

        float alpha = 1.0F - Math.min(entity.controlledAnim.getAnimationFraction(), 1.0F);
        ResourceLocation texture = this.getTextureLocation(entity);

        poseStack.pushPose();
        poseStack.scale(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);
        poseStack.mulPose(Axis.YP.rotation(-yawRad + (float) Math.PI));
        poseStack.mulPose(Axis.XP.rotation(-pitchRad));
        poseStack.mulPose(Axis.ZP.rotation((float) Math.PI));
        poseStack.translate(0.0D, -1.5D, 0.0D);

        if (Minecraft.getInstance().shouldEntityAppearGlowing(entity)) {
            VertexConsumer outline = buffer.getBuffer(RenderType.outline(texture));
            this.model.renderToBuffer(poseStack, outline, packedLight, OverlayTexture.NO_OVERLAY,
                    1.0F, 1.0F, 1.0F, 1.0F);
        }

        this.model.setupAnim(entity, 0.0F, 0.0F, (float) entity.tickCount + partialTicks, 0.0F, 0.0F);
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucentEmissive(texture));
        this.model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, Mth.clamp(alpha - 0.5F, 0.0F, 1.0F));

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(SoulShield entity) {
        return entity.getRed() ? SOUL_SHIELD_RED : SOUL_SHIELD;
    }
}
