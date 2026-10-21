package com.qiuyue.goetyominous.client.render.lm;

import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.lm.HauntedGuardServantModel;
import com.qiuyue.goetyominous.common.entities.ally.lm.HauntedGuardServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HauntedGuardServantRenderer
        extends MobRenderer<HauntedGuardServant, HauntedGuardServantModel<HauntedGuardServant>> {

    private static final ResourceLocation TEXTURE_IRON = new ResourceLocation(
            "legendary_monsters", "textures/entity/variant/haunted_guard/haunted_guard_iron.png");
    private static final ResourceLocation TEXTURE_GOLDEN = new ResourceLocation(
            "legendary_monsters", "textures/entity/variant/haunted_guard/haunted_guard_golden.png");
    private static final ResourceLocation TEXTURE_DIAMOND = new ResourceLocation(
            "legendary_monsters", "textures/entity/variant/haunted_guard/haunted_guard_diamond.png");

    public HauntedGuardServantRenderer(EntityRendererProvider.Context context) {
        super(context, new HauntedGuardServantModel<>(
                context.bakeLayer(ModEntityLayers.HAUNTED_GUARD_SERVANT_LAYER)), 1.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(HauntedGuardServant entity) {
        return switch (entity.getTextureVariant()) {
            case 1 -> TEXTURE_GOLDEN;
            case 2 -> TEXTURE_DIAMOND;
            default -> TEXTURE_IRON;
        };
    }
}
