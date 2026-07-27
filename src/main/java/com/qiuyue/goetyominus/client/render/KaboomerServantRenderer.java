package com.qiuyue.goetyominus.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominus.client.render.model.KaboomerServantModel;
import com.qiuyue.goetyominus.common.entities.ally.mobs.KaboomerServant;
import com.qiuyue.goetyominus.client.render.layer.KaboomerHissLayer;
import com.qiuyue.goetyominus.client.render.layer.KaboomerLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KaboomerServantRenderer extends MobRenderer<KaboomerServant, KaboomerServantModel<KaboomerServant>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("illageandspillage", "textures/entity/magispeller/magispeller_nothing.png");

    public KaboomerServantRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new KaboomerServantModel<>(renderManagerIn.bakeLayer(KaboomerServantModel.LAYER_LOCATION)), 0.0F);
        this.addLayer(new KaboomerLayer<>(this));
        this.addLayer(new KaboomerHissLayer<>(this));
    }

    protected void scale(KaboomerServant p_114046_, PoseStack p_114047_, float p_114048_) {
        float $$3 = p_114046_.getSwelling(p_114048_);
        float $$4 = 1.0F + Mth.sin($$3 * 200.0F) * $$3 * 0.01F;
        $$3 = Mth.clamp($$3, 0.0F, 1.0F);
        $$3 *= $$3;
        $$3 *= $$3;
        float $$5 = (1.0F + $$3 * 0.6F) * $$4;
        float $$6 = (1.0F + $$3 * 0.2F) / $$4;
        p_114047_.scale($$5, $$6, $$5);
    }

    protected float getWhiteOverlayProgress(KaboomerServant p_114043_, float p_114044_) {
        float $$2 = p_114043_.getSwelling(p_114044_);
        return (int) ($$2 * 20.0F) % 2 == 0 ? 0.0F : Mth.clamp($$2, 0.5F, 1.0F);
    }

    public ResourceLocation getTextureLocation(KaboomerServant p_110775_1_) {
        return TEXTURE;
    }
}
