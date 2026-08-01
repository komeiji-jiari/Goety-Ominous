package com.qiuyue.goetyominous.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.render.layer.CrimsonSpiderEyesLayer;
import com.qiuyue.goetyominous.common.entities.ally.spider.CrimsonSpiderServant;
import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CrimsonSpiderServantRenderer extends MobRenderer<CrimsonSpiderServant, SpiderModel<CrimsonSpiderServant>> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/cultist/crimson_spider.png");

    public CrimsonSpiderServantRenderer(EntityRendererProvider.Context context) {
        super(context, new SpiderModel<>(context.bakeLayer(ModelLayers.SPIDER)), 0.8F);
        this.addLayer(new CrimsonSpiderEyesLayer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(CrimsonSpiderServant entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(CrimsonSpiderServant entity, PoseStack poseStack, float partialTickTime) {
        poseStack.scale(1.2F, 1.2F, 1.2F);
    }

    @Override
    protected float getFlipDegrees(CrimsonSpiderServant pLivingEntity) {
        return 180.0F;
    }
}
