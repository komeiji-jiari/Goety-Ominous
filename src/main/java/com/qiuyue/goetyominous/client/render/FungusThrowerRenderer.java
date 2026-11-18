package com.qiuyue.goetyominous.client.render;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.FungusThrowerServantModel;
import com.qiuyue.goetyominous.common.entities.ally.neutral.AbstractPiglinServant;
import com.qiuyue.goetyominous.common.entities.hostile.piglin.HostileFungusThrower;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FungusThrowerRenderer extends HumanoidMobRenderer<Mob, FungusThrowerServantModel<Mob>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID,
            "textures/entity/piglin/fungus_thrower.png");
    private static final ResourceLocation HOSTILE_TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID,
            "textures/entity/piglin/hostile_fungus_thrower.png");

    public FungusThrowerRenderer(EntityRendererProvider.Context context) {
        super(context, new FungusThrowerServantModel<>(context.bakeLayer(ModEntityLayers.PIGLIN_SERVANT_LAYER)),
                0.5F, 1.0019531F, 1.0F, 1.0019531F);
        this.addLayer(new HumanoidArmorLayer<>(this,
                new HumanoidModel<>(context.bakeLayer(ModEntityLayers.PIGLIN_SERVANT_INNER_ARMOR_LAYER)),
                new HumanoidModel<>(context.bakeLayer(ModEntityLayers.PIGLIN_SERVANT_OUTER_ARMOR_LAYER)),
                context.getModelManager()));
    }

    @Override
    protected boolean isShaking(Mob entity) {
        return super.isShaking(entity)
                || entity instanceof AbstractPiglinServant servant && servant.isConverting()
                || entity instanceof HostileFungusThrower thrower && thrower.isConverting();
    }

    @Override
    public ResourceLocation getTextureLocation(Mob entity) {
        return entity instanceof HostileFungusThrower ? HOSTILE_TEXTURE : TEXTURE;
    }
}
