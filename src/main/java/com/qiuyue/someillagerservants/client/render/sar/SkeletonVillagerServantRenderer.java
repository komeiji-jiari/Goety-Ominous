package com.qiuyue.someillagerservants.client.render.sar;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.someillagerservants.client.init.ModEntityLayers;
import com.qiuyue.someillagerservants.client.render.model.sar.SkeletonVillagerServantModel;
import com.qiuyue.someillagerservants.common.entities.ally.sar.SkeletonVillagerServant;
import com.teamabnormals.savage_and_ravage.client.model.VillagerArmorModel;
import com.teamabnormals.savage_and_ravage.core.SavageAndRavage;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SkeletonVillagerServantRenderer extends MobRenderer<SkeletonVillagerServant, SkeletonVillagerServantModel> {
    private static final ResourceLocation SKELETON_VILLAGER_TEXTURES = SavageAndRavage.location("textures/entity/skeleton_villager.png");

    public SkeletonVillagerServantRenderer(EntityRendererProvider.Context context) {
        super(context, new SkeletonVillagerServantModel(context.bakeLayer(ModEntityLayers.SKELETON_VILLAGER_SERVANT_LAYER)), 0.5f);
        this.addLayer(new HumanoidArmorLayer<>(this, new VillagerArmorModel<>(context.bakeLayer(ModEntityLayers.VILLAGER_INNER_ARMOR_LAYER)), new VillagerArmorModel<>(context.bakeLayer(ModEntityLayers.VILLAGER_OUTER_ARMOR_LAYER)), context.getModelManager()));
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()) {
            @Override
            public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, SkeletonVillagerServant entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
                if (entity.isAggressive()) {
                    super.render(matrixStackIn, bufferIn, packedLightIn, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
                }
            }
        });
    }

    @Override
    protected void scale(SkeletonVillagerServant entity, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(0.9375F, 0.9375F, 0.9375F);
    }

    @Override
    public ResourceLocation getTextureLocation(SkeletonVillagerServant entity) {
        return SKELETON_VILLAGER_TEXTURES;
    }
}
