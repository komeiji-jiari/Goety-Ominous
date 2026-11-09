package com.qiuyue.goetyominous.client.render.layer;

import com.Polarice3.Goety.common.items.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.client.render.model.WargModel;
import com.qiuyue.goetyominous.common.entities.ally.mobs.Warg;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class WargSwordLayer extends RenderLayer<Warg, WargModel> {
    private final ItemInHandRenderer itemRenderer;

    public WargSwordLayer(RenderLayerParent<Warg, WargModel> parent, ItemInHandRenderer itemRenderer) {
        super(parent);
        this.itemRenderer = itemRenderer;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Warg warg, float limbSwing,
                       float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!warg.hasSword()) {
            return;
        }
        ItemStack sword = warg.getMainHandItem();
        poseStack.pushPose();
        this.getParentModel().translateToSword(poseStack);
        poseStack.translate(0.3125F, -0.03125F, 0.3125F);
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        if (sword.is(ModItems.BLADE_OF_ENDER.get())) {
            poseStack.translate(-0.084375F, -0.084375F, 0.0F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(-45.0F));
        }
        this.itemRenderer.renderItem(warg, sword, ItemDisplayContext.NONE, false, poseStack, buffer, packedLight);
        poseStack.popPose();
    }
}
