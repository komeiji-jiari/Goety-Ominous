package com.qiuyue.goetyominous.client.render;

import com.google.common.collect.ImmutableMap;
import java.util.Map;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.render.model.PiglinServantModel;
import com.qiuyue.goetyominous.common.entities.ally.neutral.AbstractPiglinServant;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PiglinServantRenderer extends HumanoidMobRenderer<Mob, PiglinServantModel<Mob>> {
    private static final Map<EntityType<?>, ResourceLocation> TEXTURES;

    public PiglinServantRenderer(EntityRendererProvider.Context context, ModelLayerLocation mainLayer,
                                 ModelLayerLocation innerArmor, ModelLayerLocation outerArmor) {
        super(context, new PiglinServantModel(context.bakeLayer(mainLayer)), 0.5F, 1.0019531F, 1.0F, 1.0019531F);
        this.addLayer(new HumanoidArmorLayer<>(this,
                new HumanoidModel<>(context.bakeLayer(innerArmor)),
                new HumanoidModel<>(context.bakeLayer(outerArmor)),
                context.getModelManager()));
    }

    private static PiglinServantModel<Mob> createModel(EntityModelSet p_174350_, ModelLayerLocation p_174351_, boolean p_174352_) {
        PiglinServantModel<Mob> $$3 = new PiglinServantModel(p_174350_.bakeLayer(p_174351_));
        if (p_174352_) {
            $$3.rightEar.visible = false;
        }

        return $$3;
    }

    public ResourceLocation getTextureLocation(Mob p_115708_) {
        ResourceLocation $$1 = TEXTURES.get(p_115708_.getType());
        if ($$1 == null) {
            throw new IllegalArgumentException("I don't know what texture to use for " + p_115708_.getType());
        }
        return $$1;
    }

    protected boolean isShaking(Mob p_115712_) {
        return super.isShaking(p_115712_) || p_115712_ instanceof AbstractPiglinServant && ((AbstractPiglinServant) p_115712_).isConverting();
    }

    static {
        TEXTURES = ImmutableMap.<EntityType<?>, ResourceLocation>builder()
                .put(com.qiuyue.goetyominous.common.init.ModEntityTypes.PIGLIN_SERVANT.get(),
                        new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/piglin/piglin_servant.png"))
                .put(com.qiuyue.goetyominous.common.init.ModEntityTypes.PIGLIN_BRUTE_SERVANT.get(),
                        new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/piglin/piglin_brute_servant.png"))
                .put(com.qiuyue.goetyominous.common.init.ModEntityTypes.STRONG_PIGLIN_BRUTE_SERVANT.get(),
                        new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/piglin/piglin_brute_servant_strong.png"))
                .put(com.qiuyue.goetyominous.common.init.ModEntityTypes.ELITE_PIGLIN_BRUTE_SERVANT.get(),
                        new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/piglin/piglin_brute_servant_elite.png"))
                .put(com.qiuyue.goetyominous.common.init.ModEntityTypes.PIGLIN_HUNTER_SERVANT.get(),
                        new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/piglin/piglin_brute_servant.png"))
                .put(com.qiuyue.goetyominous.common.init.ModEntityTypes.STRONG_PIGLIN_HUNTER_SERVANT.get(),
                        new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/piglin/piglin_brute_servant_strong.png"))
                .put(com.qiuyue.goetyominous.common.init.ModEntityTypes.ELITE_PIGLIN_HUNTER_SERVANT.get(),
                        new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/piglin/piglin_brute_servant_elite.png"))
                .put(com.qiuyue.goetyominous.common.init.ModEntityTypes.FUNGUS_THROWER.get(),
                        new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/piglin/fungus_thrower.png"))
                .build();
    }
}
