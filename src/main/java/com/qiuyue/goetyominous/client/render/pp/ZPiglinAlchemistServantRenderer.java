package com.qiuyue.goetyominous.client.render.pp;

import com.qiuyue.goetyominous.common.entities.ally.pp.ZPiglinAlchemistServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ZPiglinAlchemistServantRenderer extends PiglinAlchemistServantRenderer<ZPiglinAlchemistServant> {

    public ZPiglinAlchemistServantRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model.rightEar.visible = false;
    }

    @Override
    public ResourceLocation getTextureLocation(ZPiglinAlchemistServant entity) {
        return TEXTURE;
    }
}
