package com.qiuyue.goetyominous.client.render.mm;

import com.alexander.mutantmore.entities.MutantHoglin;
import com.alexander.mutantmore.init.ModelLayerInit;
import com.alexander.mutantmore.models.entities.MutantHoglinModel;
import com.alexander.mutantmore.renderers.layers.MutantHoglinEnragedLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.client.render.model.mm.FixedMutantHoglinModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FixedMutantHoglinRenderer extends MobRenderer<MutantHoglin, MutantHoglinModel<MutantHoglin>> {
    public FixedMutantHoglinRenderer(EntityRendererProvider.Context context) {
        super(context, new FixedMutantHoglinModel(context.bakeLayer(ModelLayerInit.MUTANT_HOGLIN)), 2.25F);
        this.addLayer(new MutantHoglinEnragedLayer<>(this));
    }

    protected void scale(MutantHoglin entity, PoseStack poseStack, float partialTicks) {
        super.scale(entity, poseStack, partialTicks);
        poseStack.scale(1.1F, 1.1F, 1.1F);
    }

    protected float getFlipDegrees(MutantHoglin entity) {
        return 0.0F;
    }

    public ResourceLocation getTextureLocation(MutantHoglin entity) {
        return new ResourceLocation("mutantmore", "textures/entities/mutant_hoglin.png");
    }
}
