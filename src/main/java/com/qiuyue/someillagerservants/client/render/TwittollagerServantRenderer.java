package com.qiuyue.someillagerservants.client.render;

import com.qiuyue.someillagerservants.client.render.model.TwittollagerServantModel;
import com.qiuyue.someillagerservants.client.render.layer.TwittollagerPhoneLayer;
import com.qiuyue.someillagerservants.common.entities.ally.illager.TwittollagerServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TwittollagerServantRenderer extends MobRenderer<TwittollagerServant, TwittollagerServantModel<TwittollagerServant>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("someillagerservants", "textures/entity/illager/twittollager.png");
    private static final ResourceLocation URTSARTEXTURE = new ResourceLocation("someillagerservants", "textures/entity/illager/twittollager_urtsar.png");

    public TwittollagerServantRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new TwittollagerServantModel<>(renderManagerIn.bakeLayer(TwittollagerServantModel.LAYER_LOCATION)), 0.5F);
        this.addLayer(new CustomHeadLayer<>(this, renderManagerIn.getModelSet(), renderManagerIn.getItemInHandRenderer()));
        this.addLayer(new TwittollagerPhoneLayer<>(this));
    }

    public Vec3 getRenderOffset(TwittollagerServant twittollager, float p_114337_) {
        double d0 = 0.01;
        if (twittollager.getGRRRRRRRRRR() > 0) {
            return new Vec3(twittollager.getRandom().nextGaussian() * 6.666666666666666E-4 * (double) twittollager.getGRRRRRRRRRR(), 0.0, twittollager.getRandom().nextGaussian() * 6.666666666666666E-4 * (double) twittollager.getGRRRRRRRRRR());
        } else if (twittollager.isHmm()) {
            return super.getRenderOffset(twittollager, p_114337_);
        } else {
            return twittollager.isStaring() ? new Vec3(twittollager.getRandom().nextGaussian() * 0.04, 0.0, twittollager.getRandom().nextGaussian() * 0.04) : new Vec3(twittollager.getRandom().nextGaussian() * d0, 0.0, twittollager.getRandom().nextGaussian() * d0);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(TwittollagerServant entity) {
        if (entity.hasCustomName()) {
            String name = entity.getCustomName().getString().toLowerCase();
            if (name.equals("urtsar")){
                return URTSARTEXTURE;
            }
        }
        return TEXTURE;
    }
}
