package com.qiuyue.goetyominous.client.render.pp;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.ally.pp.PiglinAlchemistServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PiglinAlchemistServantRenderer<T extends PiglinAlchemistServant>
        extends AbstractPiglinAlchemistServantRenderer<T> {

    protected static final ResourceLocation TEXTURE =
            new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/piglin_alchemist_servant.png");

    public PiglinAlchemistServantRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.addLayer(new BeltRenderLayer<>(this, context.getModelSet(), context.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }
}
