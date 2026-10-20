package com.qiuyue.goetyominous.client.render.mm;

import com.alexander.mutantmore.advanced_animation_utils.armour_utils.AdvancedArmourLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.mm.MutantHuskServantModel;
import com.qiuyue.goetyominous.client.render.model.mm.MutantWitherSkeletonServantModel;
import com.qiuyue.goetyominous.common.entities.ally.mm.MutantHuskServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import com.qiuyue.goetyominous.config.mm.MmConfig;

@OnlyIn(Dist.CLIENT)
public class MutantHuskServantRenderer extends MobRenderer<MutantHuskServant, MutantHuskServantModel<MutantHuskServant>> {
    private static final String SERVANT_TEXTURE = "textures/entity/mutant_husk_servant";
    private static final String ORIGINAL_TEXTURE = "textures/entities/mutant_husk";

    public MutantHuskServantRenderer(EntityRendererProvider.Context context) {
        super(context, new MutantHuskServantModel(context.bakeLayer(ModEntityLayers.MUTANT_HUSK_SERVANT_LAYER)), 1.5F);
        AdvancedArmourLayer.addCustomArmourLayers(this, context, MutantWitherSkeletonServantModel.INNER_ARMOUR, MutantWitherSkeletonServantModel.OUTER_ARMOUR);
        this.addLayer(new ItemInHandLayer(this, context.getItemInHandRenderer()));
    }

    protected void scale(MutantHuskServant p_115314_, PoseStack p_115315_, float p_115316_) {
        super.scale(p_115314_, p_115315_, p_115316_);
        float scaleFactor = 1.3F;
        p_115315_.scale(scaleFactor, scaleFactor, scaleFactor);
    }

    protected float getFlipDegrees(MutantHuskServant p_115337_) {
        return 0.0F;
    }

    public ResourceLocation getTextureLocation(MutantHuskServant entity) {
        if (MmConfig.MutantHuskServantOriginalTexture.get()) {
            return new ResourceLocation("mutantmore", ORIGINAL_TEXTURE + entity.getSandType() + ".png");
        }
        return new ResourceLocation(GoetyOminous.MOD_ID, SERVANT_TEXTURE + entity.getSandType() + ".png");
    }
}
