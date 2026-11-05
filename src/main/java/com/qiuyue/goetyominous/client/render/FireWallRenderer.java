package com.qiuyue.goetyominous.client.render;

import com.qiuyue.goetyominous.common.entities.util.FireWallEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FireWallRenderer extends EntityRenderer<FireWallEntity> {
    public FireWallRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
    }

    @Override
    public ResourceLocation getTextureLocation(FireWallEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
