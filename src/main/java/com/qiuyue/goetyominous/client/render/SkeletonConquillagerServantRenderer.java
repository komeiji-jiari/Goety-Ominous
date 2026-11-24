package com.qiuyue.goetyominous.client.render;

import com.Polarice3.Goety.client.render.ModModelLayer;
import com.Polarice3.Goety.client.render.model.VillagerArmorModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.ConquillagerServantModel;
import com.qiuyue.goetyominous.common.entities.ally.illager.SkeletonConquillagerServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SkeletonConquillagerServantRenderer
        extends MobRenderer<SkeletonConquillagerServant, ConquillagerServantModel<SkeletonConquillagerServant>> {

    public SkeletonConquillagerServantRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn,
                new ConquillagerServantModel<>(
                        renderManagerIn.bakeLayer(ModEntityLayers.SKELETON_CONQUILLAGER_SERVANT_LAYER)),
                0.5F);
        this.addLayer(new ItemInHandLayer<>(this, renderManagerIn.getItemInHandRenderer()));
        this.addLayer(
                new CustomHeadLayer<>(this, renderManagerIn.getModelSet(), renderManagerIn.getItemInHandRenderer()));
        this.addLayer(new HumanoidArmorLayer<>(this,
                new VillagerArmorModel<>(renderManagerIn.bakeLayer(ModModelLayer.VILLAGER_ARMOR_INNER)),
                new VillagerArmorModel<>(renderManagerIn.bakeLayer(ModModelLayer.VILLAGER_ARMOR_OUTER)),
                renderManagerIn.getModelManager()));
    }

    @Override
    protected void scale(SkeletonConquillagerServant entitylivingbaseIn, PoseStack matrixStackIn,
            float partialTickTime) {
        matrixStackIn.scale(0.9375F, 0.9375F, 0.9375F);
    }

    @Override
    protected void setupRotations(SkeletonConquillagerServant pEntityLiving, PoseStack pMatrixStack,
            float pAgeInTicks, float pRotationYaw, float pPartialTicks) {
        super.setupRotations(pEntityLiving, pMatrixStack, pAgeInTicks, pRotationYaw, pPartialTicks);
        float f = pEntityLiving.getSwimAmount(pPartialTicks);
        if (f > 0.0F) {
            pMatrixStack.mulPose(
                    Axis.XP.rotationDegrees(Mth.lerp(f, pEntityLiving.getXRot(), -10.0F - pEntityLiving.getXRot())));
        }
    }

    @Override
    public ResourceLocation getTextureLocation(SkeletonConquillagerServant entity) {
        return entity.getResourceLocation();
    }
}
