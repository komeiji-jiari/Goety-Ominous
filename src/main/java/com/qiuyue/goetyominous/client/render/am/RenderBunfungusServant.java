package com.qiuyue.goetyominous.client.render.am;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.client.render.model.am.ModelBunfungusServant;
import com.qiuyue.goetyominous.common.entities.ally.am.BunfungusServant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderBunfungusServant extends MobRenderer<BunfungusServant, ModelBunfungusServant> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/bunfungus.png");
    private static final ResourceLocation TEXTURE_SLEEPING = new ResourceLocation("alexsmobs:textures/entity/bunfungus_sleeping.png");

    public RenderBunfungusServant(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelBunfungusServant(), 0.6F);
        this.addLayer(new LayerHeldItem(this));
    }

    @Override
    public ResourceLocation getTextureLocation(BunfungusServant entity) {
        return entity.isSleeping() ? TEXTURE_SLEEPING : TEXTURE;
    }

    public static class LayerHeldItem extends RenderLayer<BunfungusServant, ModelBunfungusServant> {

        public LayerHeldItem(RenderBunfungusServant p_i50928_1_) {
            super(p_i50928_1_);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, BunfungusServant entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            ItemStack stack = entitylivingbaseIn.getItemBySlot(EquipmentSlot.MAINHAND);
            matrixStackIn.pushPose();
            if (entitylivingbaseIn.isBaby()) {
                matrixStackIn.scale(0.5F, 0.5F, 0.5F);
                matrixStackIn.translate(0.0D, 1.5D, 0.0D);
            }
            matrixStackIn.pushPose();
            translateToHand(matrixStackIn);
            matrixStackIn.translate(0.3F, 0.45F, -0.15F);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(-90.0F));
            matrixStackIn.scale(1.15F, 1.15F, 1.15F);
            ItemInHandRenderer itemRenderer = Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer();
            itemRenderer.renderItem(entitylivingbaseIn, stack, ItemDisplayContext.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
            matrixStackIn.popPose();
            matrixStackIn.popPose();
        }

        protected void translateToHand(PoseStack matrixStack) {
            this.getParentModel().root.translateAndRotate(matrixStack);
            this.getParentModel().body.translateAndRotate(matrixStack);
            this.getParentModel().right_arm.translateAndRotate(matrixStack);
        }
    }
}
