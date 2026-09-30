package com.qiuyue.goetyominous.client.render.lm;

import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.layer.lm.BeheadedKnightServantGhostArmLayer;
import com.qiuyue.goetyominous.client.render.layer.lm.BeheadedKnightServantGrabLayer;
import com.qiuyue.goetyominous.client.render.model.lm.BeheadedKnightServantModel;
import com.qiuyue.goetyominous.common.entities.ally.lm.BeheadedKnightServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BeheadedKnightServantRenderer
        extends MobRenderer<BeheadedKnightServant, BeheadedKnightServantModel<BeheadedKnightServant>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(
            "legendary_monsters", "textures/entity/beheaded_knight/beheaded_knight.png");
    private static final ResourceLocation TEXTURE_GOLDEN = new ResourceLocation(
            "legendary_monsters", "textures/entity/beheaded_knight/beheaded_knight_golden.png");
    private static final ResourceLocation TEXTURE_DIAMOND = new ResourceLocation(
            "legendary_monsters", "textures/entity/beheaded_knight/beheaded_knight_diamond.png");

    public BeheadedKnightServantRenderer(EntityRendererProvider.Context context) {
        super(context, new BeheadedKnightServantModel<>(
                context.bakeLayer(ModEntityLayers.BEHEADED_KNIGHT_SERVANT_LAYER)), 1.5F);
        this.addLayer(new BeheadedKnightServantGhostArmLayer(this));
        this.addLayer(new BeheadedKnightServantGrabLayer(this, context.getEntityRenderDispatcher()));
    }

    @Override
    public ResourceLocation getTextureLocation(BeheadedKnightServant entity) {
        return switch (entity.getTextureVariant()) {
            case 2 -> TEXTURE_GOLDEN;
            case 3 -> TEXTURE_DIAMOND;
            default -> TEXTURE;
        };
    }
}
