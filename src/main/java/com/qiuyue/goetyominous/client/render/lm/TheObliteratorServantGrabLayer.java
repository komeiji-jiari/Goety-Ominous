package com.qiuyue.goetyominous.client.render.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.client.render.model.lm.TheObliteratorServantModel;
import com.qiuyue.goetyominous.common.entities.ally.lm.TheObliteratorServant;
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
public class TheObliteratorServantGrabLayer extends RenderLayer<TheObliteratorServant, TheObliteratorServantModel<TheObliteratorServant>> {

    private final EntityRenderDispatcher dispatcher;

    public TheObliteratorServantGrabLayer(RenderLayerParent<TheObliteratorServant, TheObliteratorServantModel<TheObliteratorServant>> renderer, EntityRenderDispatcher dispatcher) {
        super(renderer);
        this.dispatcher = dispatcher;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, TheObliteratorServant entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        for (Entity passenger : entity.getPassengers()) {
            if (passenger == Minecraft.getInstance().player && Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
                continue;
            }
            this.renderHeldMob(entity, passenger, poseStack, partialTicks, buffer, packedLight);
        }
    }

    private void renderHeldMob(TheObliteratorServant entity, Entity held, PoseStack poseStack, float partialTicks, MultiBufferSource buffer, int packedLight) {
        if (entity.getAttackState() == 50 || entity.getAttackState() == 40) {
            LegendaryMonsters.PROXY.releaseRenderingEntity(held.getUUID());
            poseStack.pushPose();
            if (entity.getAttackState() == 50) {
                this.getParentModel().translateModelToLeftArm(poseStack);
            } else if (entity.getAttackState() == 40) {
                this.getParentModel().translateModelToRightArm(poseStack);
            }
            poseStack.scale(1.2F, 1.2F, 1.2F);
            poseStack.translate(1.0F, 0.25F, 0.5F);
            poseStack.mulPose(Axis.XP.rotation(90.0F));
            poseStack.mulPose(Axis.ZP.rotation(90.0F));
            this.dispatcher.render(held, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks, poseStack, buffer, packedLight);
            poseStack.popPose();
            LegendaryMonsters.PROXY.blockRenderingEntity(held.getUUID());
        }
    }
}
