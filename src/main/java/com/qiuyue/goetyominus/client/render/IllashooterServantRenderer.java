package com.qiuyue.goetyominus.client.render;

import com.qiuyue.goetyominus.client.render.model.IllashooterServantModel;
import com.qiuyue.goetyominus.common.entities.ally.mobs.IllashooterServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IllashooterServantRenderer extends MobRenderer<IllashooterServant, IllashooterServantModel<IllashooterServant>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("illageandspillage", "textures/entity/magispeller/illashooter.png");

    public IllashooterServantRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new IllashooterServantModel<>(renderManagerIn.bakeLayer(IllashooterServantModel.LAYER_LOCATION)), 0.5F);
    }

    public ResourceLocation getTextureLocation(IllashooterServant p_110775_1_) {
        return TEXTURE;
    }
}
