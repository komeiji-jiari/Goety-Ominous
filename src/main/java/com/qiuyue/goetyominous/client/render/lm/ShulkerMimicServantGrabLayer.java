package com.qiuyue.goetyominous.client.render.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.client.render.model.lm.ShulkerMimicServantModel;
import com.qiuyue.goetyominous.common.entities.ally.lm.ShulkerMimicServant;
import net.miauczel.legendary_monsters.LegendaryMonsters;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ShulkerMimicServantGrabLayer extends RenderLayer<ShulkerMimicServant, ShulkerMimicServantModel<ShulkerMimicServant>> {

    private final EntityRenderDispatcher dispatcher;

    public ShulkerMimicServantGrabLayer(RenderLayerParent<ShulkerMimicServant, ShulkerMimicServantModel<ShulkerMimicServant>> renderer, EntityRenderDispatcher dispatcher) {
        super(renderer);
        this.dispatcher = dispatcher;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, ShulkerMimicServant entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        for (Entity passenger : entity.getPassengers()) {
            if (passenger == Minecraft.getInstance().player && Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
                continue;
            }
            this.renderHeldMob(passenger, poseStack, partialTicks, buffer, packedLight);
        }
    }

    private void renderHeldMob(Entity held, PoseStack poseStack, float partialTicks, MultiBufferSource buffer, int packedLight) {
        LegendaryMonsters.PROXY.releaseRenderingEntity(held.getUUID());
        poseStack.pushPose();
        this.getParentModel().translateModel(poseStack);
        poseStack.translate(held.getBbHeight() / 2.0F, -1.0D, 0.0D);
        poseStack.scale(0.7F, 0.7F, 0.7F);
        poseStack.mulPose(Axis.XP.rotation(90.0F));
        poseStack.mulPose(Axis.ZP.rotation(90.0F));
        this.dispatcher.render(held, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks, poseStack, buffer, packedLight);
        poseStack.popPose();
        LegendaryMonsters.PROXY.blockRenderingEntity(held.getUUID());
    }
}
