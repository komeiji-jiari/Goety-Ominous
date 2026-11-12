package com.qiuyue.goetyominous.client.render;

import com.qiuyue.goetyominous.common.entities.ally.illager.BoundPyrologer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BoundPyrologerRenderer extends PyrologerRenderer<BoundPyrologer> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("goetyominous", "textures/entity/illager/bound_pyrologer.png");
    private static final ResourceLocation ATTACKING =
            new ResourceLocation("goetyominous", "textures/entity/illager/bound_pyrologer_attacking.png");

    public BoundPyrologerRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(BoundPyrologer entity) {
        return entity.isCastingSpell() ? ATTACKING : TEXTURE;
    }
}
