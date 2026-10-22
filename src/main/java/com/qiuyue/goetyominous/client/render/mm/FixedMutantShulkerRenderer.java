package com.qiuyue.goetyominous.client.render.mm;

import com.alexander.mutantmore.entities.MutantShulker;
import com.alexander.mutantmore.models.entities.MutantShulkerModel;
import com.alexander.mutantmore.renderers.layers.MutantShulkerNormanFaceLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.client.render.model.mm.FixedMutantShulkerModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FixedMutantShulkerRenderer extends MobRenderer<MutantShulker, MutantShulkerModel<MutantShulker>> {
    public FixedMutantShulkerRenderer(EntityRendererProvider.Context context) {
        super(context, new FixedMutantShulkerModel(context.bakeLayer(MutantShulkerModel.LAYER_LOCATION)), 2.75F);
        this.addLayer(new MutantShulkerNormanFaceLayer<>(this));
    }

    protected void scale(MutantShulker entity, PoseStack poseStack, float partialTicks) {
        poseStack.scale(1.25F, 1.25F, 1.25F);
        super.scale(entity, poseStack, partialTicks);
    }

    protected float getFlipDegrees(MutantShulker entity) {
        return 0.0F;
    }

    public ResourceLocation getTextureLocation(MutantShulker entity) {
        DyeColor color = entity.getColor();
        return color == null
                ? new ResourceLocation("mutantmore", "textures/entities/mutant_shulker.png")
                : new ResourceLocation("mutantmore", "textures/entities/mutant_shulker_" + color.getName() + ".png");
    }
}
