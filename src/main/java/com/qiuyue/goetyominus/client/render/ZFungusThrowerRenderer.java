package com.qiuyue.goetyominus.client.render;

import com.Polarice3.Goety.client.render.ModModelLayer;
import com.Polarice3.Goety.client.render.model.ZPiglinModel;
import com.qiuyue.goetyominus.GoetyOminous;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ZFungusThrowerRenderer extends HumanoidMobRenderer<Mob, ZPiglinModel<Mob>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID,
            "textures/entity/piglin/zfungus_thrower.png");

    public ZFungusThrowerRenderer(EntityRendererProvider.Context context) {
        super(context, new ZPiglinModel<>(context.bakeLayer(ModModelLayer.ZPIGLIN_SERVANT)), 0.5F);
        this.addLayer(new net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer<>(this,
                new net.minecraft.client.model.HumanoidModel<>(context.bakeLayer(net.minecraft.client.model.geom.ModelLayers.ZOMBIFIED_PIGLIN_INNER_ARMOR)),
                new net.minecraft.client.model.HumanoidModel<>(context.bakeLayer(net.minecraft.client.model.geom.ModelLayers.ZOMBIFIED_PIGLIN_OUTER_ARMOR)),
                context.getModelManager()));
    }

    @Override
    public ResourceLocation getTextureLocation(Mob entity) {
        return TEXTURE;
    }
}
