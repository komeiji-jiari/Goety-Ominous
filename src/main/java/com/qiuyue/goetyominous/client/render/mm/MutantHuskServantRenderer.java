package com.qiuyue.goetyominous.client.render.mm;

import com.alexander.mutantmore.advanced_animation_utils.armour_utils.AdvancedArmourLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.mm.MutantHuskServantModel;
import com.qiuyue.goetyominous.client.render.model.mm.MutantWitherSkeletonServantModel;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.MutantHuskServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MutantHuskServantRenderer extends MobRenderer<MutantHuskServant, MutantHuskServantModel<MutantHuskServant>> {

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
        return new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/mutant_husk_servant" + entity.getSandType() + ".png");
    }
}
