package com.qiuyue.goetyominous.client.render;

import com.qiuyue.goetyominous.client.render.model.DispenserServantModel;
import com.qiuyue.goetyominous.common.entities.ally.mobs.DispenserServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Calendar;

@OnlyIn(Dist.CLIENT)
public class DispenserServantRenderer extends MobRenderer<DispenserServant, DispenserServantModel<DispenserServant>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("illageandspillage", "textures/entity/magispeller/dispenser.png");
    private static final ResourceLocation CHRISTMAS = new ResourceLocation("illageandspillage", "textures/entity/magispeller/christmas/dispenser_christmas.png");

    public DispenserServantRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new DispenserServantModel<>(renderManagerIn.bakeLayer(DispenserServantModel.LAYER_LOCATION)), 0.5F);
    }

    @Override
    protected float getFlipDegrees(DispenserServant p_115337_) {
        return 0;
    }

    public ResourceLocation getTextureLocation(DispenserServant p_110775_1_) {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH) + 1 == 12 ? CHRISTMAS : TEXTURE;
    }
}
