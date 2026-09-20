package com.qiuyue.goetyominous.client.render;

import com.Polarice3.Goety.client.render.ModModelLayer;
import com.Polarice3.Goety.client.render.model.PlayerZombieModel;
import com.qiuyue.goetyominous.client.render.layer.MiredEyesLayer;
import com.qiuyue.goetyominous.common.entities.ally.mobs.MiredServant;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;

public class MiredServantRenderer extends HumanoidMobRenderer<MiredServant, PlayerZombieModel<MiredServant>> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation("goetyominous", "textures/entity/mired_servant.png");
    private static final ResourceLocation HOSTILE_TEXTURE =
            new ResourceLocation("goetyominous", "textures/entity/mired.png");

    public MiredServantRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerZombieModel<>(context.bakeLayer(ModModelLayer.PLAYER_ZOMBIE)), 0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this,
                new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
                new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)),
                context.getModelManager()));
        this.addLayer(new MiredEyesLayer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(MiredServant entity) {
        return entity.isHostile() ? HOSTILE_TEXTURE : TEXTURE;
    }
}
