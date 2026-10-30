package com.qiuyue.goetyominous.client.render;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.ImperialChasseurModel;
import com.qiuyue.goetyominous.common.entities.ally.illager.ImperialChasseurServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

public class ImperialChasseurRenderer<T extends Mob> extends MobRenderer<T, ImperialChasseurModel<T>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID,
            "textures/entity/illager/imperial_chasseur.png");
    private static final ResourceLocation SERVANT_TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID,
            "textures/entity/illager/imperial_chasseur_servant.png");

    public ImperialChasseurRenderer(EntityRendererProvider.Context context) {
        super(context, new ImperialChasseurModel<>(context.bakeLayer(ModEntityLayers.IMPERIAL_CHASSEUR_LAYER)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return entity instanceof ImperialChasseurServant ? SERVANT_TEXTURE : TEXTURE;
    }
}
