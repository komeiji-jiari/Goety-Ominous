package com.qiuyue.goetyominous.client.render.mm;

import com.alexander.mutantmore.advanced_animation_utils.armour_utils.AdvancedArmourLayer;
import com.alexander.mutantmore.entities.MutantJungleZombie;
import com.alexander.mutantmore.init.ModelLayerInit;
import com.alexander.mutantmore.models.entities.MutantJungleZombieModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.client.render.model.mm.FixedMutantJungleZombieModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FixedMutantJungleZombieRenderer extends MobRenderer<MutantJungleZombie, MutantJungleZombieModel<MutantJungleZombie>> {
    public FixedMutantJungleZombieRenderer(EntityRendererProvider.Context context) {
        super(context, new FixedMutantJungleZombieModel(context.bakeLayer(ModelLayerInit.MUTANT_JUNGLE_ZOMBIE)), 1.25F);
        AdvancedArmourLayer.addCustomArmourLayers(this, context, ModelLayerInit.INNER_ARMOUR, ModelLayerInit.OUTER_ARMOUR);
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    protected void scale(MutantJungleZombie entity, PoseStack poseStack, float partialTicks) {
        super.scale(entity, poseStack, partialTicks);
        poseStack.scale(1.3F, 1.3F, 1.3F);
    }

    protected float getFlipDegrees(MutantJungleZombie entity) {
        return 0.0F;
    }

    public ResourceLocation getTextureLocation(MutantJungleZombie entity) {
        return new ResourceLocation("mutantmore", "textures/entities/mutant_jungle_zombie.png");
    }
}
