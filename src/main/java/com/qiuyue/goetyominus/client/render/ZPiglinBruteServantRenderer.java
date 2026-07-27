package com.qiuyue.goetyominus.client.render;

import com.google.common.collect.ImmutableMap;
import java.util.Map;

import com.Polarice3.Goety.client.render.ModModelLayer;
import com.Polarice3.Goety.client.render.model.ZPiglinModel;
import com.qiuyue.goetyominus.GoetyOminous;
import com.qiuyue.goetyominus.client.render.model.ZPiglinHunterModel;
import com.qiuyue.goetyominus.common.init.ModEntityTypes;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ZPiglinBruteServantRenderer extends HumanoidMobRenderer<Mob, ZPiglinModel<Mob>> {

    private static final Map<EntityType<?>, ResourceLocation> TEXTURES;

    public ZPiglinBruteServantRenderer(EntityRendererProvider.Context context) {
        super(context, new ZPiglinHunterModel(context.bakeLayer(ModModelLayer.ZPIGLIN_SERVANT)), 0.5F, 1.0019531F, 1.0F, 1.0019531F);
        this.model.rightEar.visible = false;
        this.addLayer(new HumanoidArmorLayer<>(this,
                new HumanoidModel<>(context.bakeLayer(ModelLayers.ZOMBIFIED_PIGLIN_INNER_ARMOR)),
                new HumanoidModel<>(context.bakeLayer(ModelLayers.ZOMBIFIED_PIGLIN_OUTER_ARMOR)),
                context.getModelManager()));
    }

    @Override
    public ResourceLocation getTextureLocation(Mob entity) {
        ResourceLocation tex = TEXTURES.get(entity.getType());
        if (tex == null) {
            throw new IllegalArgumentException("No texture for " + entity.getType());
        }
        return tex;
    }

    static {
        TEXTURES = ImmutableMap.<EntityType<?>, ResourceLocation>builder()
                .put(ModEntityTypes.STRONG_ZPIGLIN_BRUTE_SERVANT.get(),
                        new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/piglin/zpiglin_brute_servant_strong.png"))
                .put(ModEntityTypes.ELITE_ZPIGLIN_BRUTE_SERVANT.get(),
                        new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/piglin/zpiglin_brute_servant_elite.png"))
                .put(ModEntityTypes.ZPIGLIN_HUNTER_SERVANT.get(),
                        new ResourceLocation("goety", "textures/entity/servants/zombie/zpiglin_brute_servant.png"))
                .put(ModEntityTypes.STRONG_ZPIGLIN_HUNTER_SERVANT.get(),
                        new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/piglin/zpiglin_brute_servant_strong.png"))
                .put(ModEntityTypes.ELITE_ZPIGLIN_HUNTER_SERVANT.get(),
                        new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/piglin/zpiglin_brute_servant_elite.png"))
                .build();
    }
}
