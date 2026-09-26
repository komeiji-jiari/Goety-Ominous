package com.qiuyue.goetyominous.client.render.layer.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.client.render.lm.LmServantRenderTypes;
import com.qiuyue.goetyominous.client.render.lm.LmServantSoulRays;
import com.qiuyue.goetyominous.client.render.model.lm.PossessedPaladinServantModel;
import com.qiuyue.goetyominous.common.entities.ally.lm.PossessedPaladinServant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class PossessedPaladinServantGrabLayer
        extends RenderLayer<PossessedPaladinServant, PossessedPaladinServantModel<PossessedPaladinServant>> {

    private final EntityRenderDispatcher dispatcher;

    private static UUID currentlyRendering = null;

    public PossessedPaladinServantGrabLayer(
            RenderLayerParent<PossessedPaladinServant, PossessedPaladinServantModel<PossessedPaladinServant>> parent,
            EntityRenderDispatcher dispatcher) {
        super(parent);
        this.dispatcher = dispatcher;
    }

    public static boolean isCurrentlyRendering(UUID uuid) {
        return currentlyRendering != null && currentlyRendering.equals(uuid);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                       PossessedPaladinServant entity, float limbSwing, float limbSwingAmount,
                       float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        for (Entity passenger : entity.getPassengers()) {
            if (passenger == Minecraft.getInstance().player
                    && Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
                continue;
            }
            this.renderHeldMob(passenger, poseStack, buffer, packedLight, partialTicks, entity);
        }
    }

    private void renderHeldMob(Entity passenger, PoseStack poseStack, MultiBufferSource buffer,
                               int packedLight, float partialTicks, PossessedPaladinServant paladin) {
        UUID previous = currentlyRendering;
        currentlyRendering = passenger.getUUID();
        poseStack.pushPose();
        try {
            this.getParentModel().translateModel(poseStack);
            poseStack.scale(0.7F, 0.7F, 0.7F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
            poseStack.translate(-1.0F, -1.0F, 1.25F);

            if (paladin.soulRaysCount > 0) {
                VertexConsumer consumer = buffer.getBuffer(LmServantRenderTypes.LIGHTNING_NO_CULL);
                LmServantSoulRays.render(consumer, poseStack, paladin.soulRaysCount, 1.0F,
                        paladin.getPhase() >= 2, paladin.attackTicks, partialTicks);
            }

            this.dispatcher.render(passenger, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks,
                    poseStack, buffer, packedLight);
        } finally {
            poseStack.popPose();
            currentlyRendering = previous;
        }
    }
}
