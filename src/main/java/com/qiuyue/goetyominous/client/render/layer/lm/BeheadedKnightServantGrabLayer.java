package com.qiuyue.goetyominous.client.render.layer.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.client.render.model.lm.BeheadedKnightServantModel;
import com.qiuyue.goetyominous.common.entities.ally.lm.BeheadedKnightServant;
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
public class BeheadedKnightServantGrabLayer
        extends RenderLayer<BeheadedKnightServant, BeheadedKnightServantModel<BeheadedKnightServant>> {

    private final EntityRenderDispatcher dispatcher;

    private static UUID currentlyRendering = null;

    public BeheadedKnightServantGrabLayer(
            RenderLayerParent<BeheadedKnightServant, BeheadedKnightServantModel<BeheadedKnightServant>> parent,
            EntityRenderDispatcher dispatcher) {
        super(parent);
        this.dispatcher = dispatcher;
    }

    public static boolean isCurrentlyRendering(UUID uuid) {
        return currentlyRendering != null && currentlyRendering.equals(uuid);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                       BeheadedKnightServant entity, float limbSwing, float limbSwingAmount,
                       float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        boolean inHands = entity.getAttackState() == 11 || entity.getAttackState() == 12;
        for (Entity passenger : entity.getPassengers()) {
            if (passenger == Minecraft.getInstance().player
                    && Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
                continue;
            }
            this.renderHeldMob(passenger, poseStack, buffer, packedLight, partialTicks, !inHands);
        }
    }

    private void renderHeldMob(Entity passenger, PoseStack poseStack, MultiBufferSource buffer,
                               int packedLight, float partialTicks, boolean onNeck) {
        UUID previous = currentlyRendering;
        currentlyRendering = passenger.getUUID();
        poseStack.pushPose();
        try {
            if (onNeck) {
                this.getParentModel().translateToNeck(poseStack);
            } else {
                this.getParentModel().translateModel(poseStack);
            }
            this.dispatcher.render(passenger, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks,
                    poseStack, buffer, packedLight);
        } finally {
            poseStack.popPose();
            currentlyRendering = previous;
        }
    }
}
