package com.qiuyue.goetyominous.client.render.pp;

import com.qiuyue.goetyominous.GoetyOminous;
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

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/zpiglin_traveler_servant.png");

    @Override
    public ResourceLocation getTextureLocation(ZPiglinTravelerServant entity) {
        return TEXTURE;
    }
}
