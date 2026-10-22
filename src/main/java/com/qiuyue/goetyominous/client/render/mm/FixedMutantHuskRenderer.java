package com.qiuyue.goetyominous.client.render.mm;

import com.alexander.mutantmore.advanced_animation_utils.armour_utils.AdvancedArmourLayer;
import com.alexander.mutantmore.entities.MutantHusk;
import com.alexander.mutantmore.init.ModelLayerInit;
import com.alexander.mutantmore.models.entities.MutantHuskModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.client.render.model.mm.FixedMutantHuskModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FixedMutantHuskRenderer extends MobRenderer<MutantHusk, MutantHuskModel<MutantHusk>> {
    public FixedMutantHuskRenderer(EntityRendererProvider.Context context) {
        super(context, new FixedMutantHuskModel(context.bakeLayer(ModelLayerInit.MUTANT_HUSK)), 1.5F);
        AdvancedArmourLayer.addCustomArmourLayers(this, context, ModelLayerInit.INNER_ARMOUR, ModelLayerInit.OUTER_ARMOUR);
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    protected void scale(MutantHusk entity, PoseStack poseStack, float partialTicks) {
        super.scale(entity, poseStack, partialTicks);
        poseStack.scale(1.3F, 1.3F, 1.3F);
    }

    protected float getFlipDegrees(MutantHusk entity) {
        return 0.0F;
    }

    public ResourceLocation getTextureLocation(MutantHusk entity) {
        return new ResourceLocation("mutantmore", "textures/entities/mutant_husk" + entity.getSandType() + ".png");
    }
}
