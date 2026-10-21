package com.qiuyue.goetyominous.client.render.pp;

import com.qiuyue.goetyominous.common.entities.ally.pp.ZPiglinTravelerServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ZPiglinTravelerServantRenderer extends PiglinTravelerServantRenderer<ZPiglinTravelerServant> {

    public ZPiglinTravelerServantRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model.rightEar.visible = false;
    }

    @Override
    public ResourceLocation getTextureLocation(ZPiglinTravelerServant entity) {
        return TEXTURE;
    }
}
