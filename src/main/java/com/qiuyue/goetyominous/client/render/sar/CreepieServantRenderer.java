package com.qiuyue.goetyominous.client.render.sar;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.layer.sar.CreepieChargeLayer;
import com.qiuyue.goetyominous.client.render.layer.sar.SproutLayer;
import com.qiuyue.goetyominous.client.render.model.sar.CreepieServantModel;
import com.qiuyue.goetyominous.common.entities.ally.sar.CreepieServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CreepieServantRenderer extends MobRenderer<CreepieServant, CreepieServantModel> {
    private static final ResourceLocation CREEPIE_TEXTURE = new ResourceLocation("textures/entity/creeper/creeper.png");
    public CreepieServantRenderer(EntityRendererProvider.Context context) {
        super(context, new CreepieServantModel(context.bakeLayer(ModEntityLayers.CREEPIE_SERVANT_LAYER)), 0.3F);
        this.addLayer(new CreepieChargeLayer(this, context.getModelSet()));
        this.addLayer(new SproutLayer<>(this));
    }

    @Override
    protected void scale(CreepieServant entityLivingBaseIn, PoseStack matrixStackIn, float partialTickTime) {
        float creeperFlashIntensity = entityLivingBaseIn.getCreeperFlashIntensity(partialTickTime);
        float mathsThing = 1.0f + Mth.sin(creeperFlashIntensity * 100.0f) * creeperFlashIntensity * 0.01f;
        creeperFlashIntensity = Mth.clamp(creeperFlashIntensity, 0.0f, 1.0f);
        creeperFlashIntensity = creeperFlashIntensity * creeperFlashIntensity;
        creeperFlashIntensity = creeperFlashIntensity * creeperFlashIntensity;
        float multipliedByMathsThing = (1.0f + creeperFlashIntensity * 0.4f) * mathsThing;
        float dividedByMathsThing = (1.0f + creeperFlashIntensity * 0.1f) / mathsThing;
        matrixStackIn.scale(multipliedByMathsThing, dividedByMathsThing, multipliedByMathsThing);
        matrixStackIn.scale(0.5F, 0.5F, 0.5F);
    }

    @Override
    protected float getWhiteOverlayProgress(CreepieServant livingEntityIn, float partialTicks) {
        float flashIntensity = livingEntityIn.getCreeperFlashIntensity(partialTicks);
        return (int) (flashIntensity * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(flashIntensity, 0.5F, 1.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(CreepieServant entity) {
        return CREEPIE_TEXTURE;
    }
}
