package com.qiuyue.goetyominus.client.render;

import com.qiuyue.goetyominus.GoetyOminous;
import com.qiuyue.goetyominus.client.init.ModEntityLayers;
import com.qiuyue.goetyominus.client.render.layer.mm.MutantHoglinEnragedLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominus.client.render.model.mm.MutantHoglinServantModel;
import com.qiuyue.goetyominus.common.entities.ally.mobs.mm.MutantHoglinServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MutantHoglinServantRenderer extends MobRenderer<MutantHoglinServant, MutantHoglinServantModel<MutantHoglinServant>> {
    public MutantHoglinServantRenderer(EntityRendererProvider.Context context) {
        super(context, new MutantHoglinServantModel<>(context.bakeLayer(ModEntityLayers.MUTANT_HOGLIN_SERVANT_LAYER)), 2.25F);
        this.addLayer(new MutantHoglinEnragedLayer(this));
    }

    protected void scale(MutantHoglinServant p_115314_, PoseStack p_115315_, float p_115316_) {
        super.scale(p_115314_, p_115315_, p_115316_);
        float scaleFactor = 1.1F;
        p_115315_.scale(scaleFactor, scaleFactor, scaleFactor);
    }

    protected float getFlipDegrees(MutantHoglinServant p_115337_) {
        return 0.0F;
    }

    public ResourceLocation getTextureLocation(MutantHoglinServant entity) {
        return new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/mutant_hoglin_servant.png");
    }
}
