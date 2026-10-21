package com.qiuyue.goetyominous.client.render.lm;

import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.lm.HauntedKnightServantModel;
import com.qiuyue.goetyominous.common.entities.ally.lm.HauntedKnightServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HauntedKnightServantRenderer
        extends MobRenderer<HauntedKnightServant, HauntedKnightServantModel<HauntedKnightServant>> {

    private static final ResourceLocation TEXTURE_IRON = new ResourceLocation(
            "legendary_monsters", "textures/entity/variant/living_armor/living_armor_iron2.png");
    private static final ResourceLocation TEXTURE_GOLDEN = new ResourceLocation(
            "legendary_monsters", "textures/entity/variant/living_armor/living_armor_golden2.png");
    private static final ResourceLocation TEXTURE_DIAMOND = new ResourceLocation(
            "legendary_monsters", "textures/entity/variant/living_armor/living_armor_diamond3.png");

    public HauntedKnightServantRenderer(EntityRendererProvider.Context context) {
        super(context, new HauntedKnightServantModel<>(
                context.bakeLayer(ModEntityLayers.HAUNTED_KNIGHT_SERVANT_LAYER)), 1.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(HauntedKnightServant entity) {
        return switch (entity.getTextureVariant()) {
            case 1 -> TEXTURE_GOLDEN;
            case 2 -> TEXTURE_DIAMOND;
            default -> TEXTURE_IRON;
        };
    }
}
