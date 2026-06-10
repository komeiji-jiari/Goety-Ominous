package com.qiuyue.someillagerservants.client.render.sar;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.someillagerservants.client.init.ModEntityLayers;
import com.qiuyue.someillagerservants.client.render.model.sar.ExecutionerServantModel;
import com.qiuyue.someillagerservants.common.entities.ally.sar.ExecutionerServant;
import com.teamabnormals.savage_and_ravage.core.SavageAndRavage;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ExecutionerServantRenderer extends MobRenderer<ExecutionerServant, ExecutionerServantModel> {

    private static final ResourceLocation TEXTURE = SavageAndRavage.location("textures/entity/executioner.png");

    public ExecutionerServantRenderer(EntityRendererProvider.Context context) {
        super(context, new ExecutionerServantModel(context.bakeLayer(ModEntityLayers.EXECUTIONER_SERVANT_LAYER)), 0.5f);
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()) {
            @Override
            public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, ExecutionerServant entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
                if (entity.isAggressive()) {
                    super.render(matrixStackIn, bufferIn, packedLightIn, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
                }
            }
        });
        this.addLayer(new BannerHeadLayer(this, context));
    }

    @Override
    public ResourceLocation getTextureLocation(ExecutionerServant entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(ExecutionerServant entity, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(0.9375F, 0.9375F, 0.9375F);
    }

    public static class BannerHeadLayer extends net.minecraft.client.renderer.entity.layers.RenderLayer<ExecutionerServant, ExecutionerServantModel> {
        private final EntityRendererProvider.Context context;

        public BannerHeadLayer(net.minecraft.client.renderer.entity.RenderLayerParent<ExecutionerServant, ExecutionerServantModel> renderer, EntityRendererProvider.Context context) {
            super(renderer);
            this.context = context;
        }

        @Override
        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, ExecutionerServant entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            ItemStack headItem = entity.getItemBySlot(EquipmentSlot.HEAD);
            if (headItem.is(ItemTags.BANNERS)) {
                matrixStackIn.pushPose();
                this.getParentModel().head.translateAndRotate(matrixStackIn);
                matrixStackIn.translate(0.0D, -0.25D, 0.0D);
                matrixStackIn.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180.0F));
                matrixStackIn.scale(0.625F, -0.625F, -0.625F);
                this.context.getItemInHandRenderer().renderItem(entity, headItem, ItemDisplayContext.HEAD, false, matrixStackIn, bufferIn, packedLightIn);
                matrixStackIn.popPose();
            }
        }
    }
}
