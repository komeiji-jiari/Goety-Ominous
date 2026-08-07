package com.qiuyue.goetyominous.client.render;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.ArchGeomancerModel;
import com.qiuyue.goetyominous.common.entities.hostile.illagers.ArchGeomancerEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ArchGeomancerRenderer extends MobRenderer<ArchGeomancerEntity, ArchGeomancerModel<ArchGeomancerEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/illager/arch_geomancer.png");

    public ArchGeomancerRenderer(EntityRendererProvider.Context context) {
        super(context, new ArchGeomancerModel<>(context.bakeLayer(ModEntityLayers.ARCH_GEOMANCER_LAYER)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(ArchGeomancerEntity entity) {
        return TEXTURE;
    }
}
