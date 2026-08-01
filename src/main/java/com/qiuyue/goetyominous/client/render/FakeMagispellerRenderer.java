package com.qiuyue.goetyominous.client.render;

import com.qiuyue.goetyominous.client.render.layer.FakeMagispellerLayer;
import com.qiuyue.goetyominous.client.render.model.FakeMagispellerModel;
import com.qiuyue.goetyominous.common.entities.ally.mobs.FakeMagispeller;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FakeMagispellerRenderer extends MobRenderer<FakeMagispeller, FakeMagispellerModel<FakeMagispeller>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("illageandspillage", "textures/entity/magispeller/magispeller_nothing.png");

    public FakeMagispellerRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new FakeMagispellerModel<>(renderManagerIn.bakeLayer(FakeMagispellerModel.LAYER_LOCATION)), 0.5F);
        this.addLayer(new FakeMagispellerLayer<>(this));
        this.addLayer(new CustomHeadLayer<>(this, renderManagerIn.getModelSet(), renderManagerIn.getItemInHandRenderer()));
        this.addLayer(new ItemInHandLayer<>(this, renderManagerIn.getItemInHandRenderer()));
    }

    @Override
    protected float getFlipDegrees(FakeMagispeller p_115337_) {
        return 0;
    }

    public ResourceLocation getTextureLocation(FakeMagispeller p_110775_1_) {
        return TEXTURE;
    }
}