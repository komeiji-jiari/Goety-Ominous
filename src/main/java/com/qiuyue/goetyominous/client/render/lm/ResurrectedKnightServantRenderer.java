package com.qiuyue.goetyominous.client.render.lm;

import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.layer.lm.ResurrectedKnightServantBodyLayer;
import com.qiuyue.goetyominous.client.render.model.lm.ResurrectedKnightServantModel;
import com.qiuyue.goetyominous.common.entities.ally.lm.ResurrectedKnightServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ResurrectedKnightServantRenderer
        extends MobRenderer<ResurrectedKnightServant, ResurrectedKnightServantModel<ResurrectedKnightServant>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(
            "legendary_monsters", "textures/entity/resurrected_knight/helmet.png");
    private static final ResourceLocation TEXTURE_GOLDEN = new ResourceLocation(
            "legendary_monsters", "textures/entity/resurrected_knight/helmet_golden.png");
    private static final ResourceLocation TEXTURE_DIAMOND = new ResourceLocation(
            "legendary_monsters", "textures/entity/resurrected_knight/helmet_diamond.png");

    public ResurrectedKnightServantRenderer(EntityRendererProvider.Context context) {
        super(context, new ResurrectedKnightServantModel<>(
                context.bakeLayer(ModEntityLayers.RESURRECTED_KNIGHT_SERVANT_LAYER)), 1.5F);
        this.addLayer(new ResurrectedKnightServantBodyLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(ResurrectedKnightServant entity) {
        return switch (entity.getTextureVariant()) {
            case 2 -> TEXTURE_GOLDEN;
            case 3 -> TEXTURE_DIAMOND;
            default -> TEXTURE;
        };
    }
}
