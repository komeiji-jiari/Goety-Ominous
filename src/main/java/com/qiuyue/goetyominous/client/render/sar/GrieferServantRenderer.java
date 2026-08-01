package com.qiuyue.goetyominous.client.render.sar;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.sar.GrieferServantModel;
import com.qiuyue.goetyominous.client.render.model.sar.VillagerArmorModel;
import com.qiuyue.goetyominous.common.entities.ally.sar.GrieferServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GrieferServantRenderer extends HumanoidMobRenderer<GrieferServant, GrieferServantModel> {
    private static final ResourceLocation GRIEFER_TEXTURE = new ResourceLocation("savage_and_ravage", "textures/entity/griefer/griefer.png");
    private static final ResourceLocation APESHIT_MODE_TEXTURE = new ResourceLocation("savage_and_ravage", "textures/entity/griefer/griefer_melee.png");

    public GrieferServantRenderer(EntityRendererProvider.Context context) {
        super(context, new GrieferServantModel(context.bakeLayer(ModEntityLayers.GRIEFER_SERVANT_LAYER)), 0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this, new VillagerArmorModel<>(context.bakeLayer(ModEntityLayers.VILLAGER_INNER_ARMOR_LAYER)), new VillagerArmorModel<>(context.bakeLayer(ModEntityLayers.VILLAGER_OUTER_ARMOR_LAYER)), context.getModelManager()));
    }

    @Override
    public ResourceLocation getTextureLocation(GrieferServant entity) {
        return entity.isApeshit() ? APESHIT_MODE_TEXTURE : GRIEFER_TEXTURE;
    }

    @Override
    protected void scale(GrieferServant entity, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(0.9375F, 0.9375F, 0.9375F);
    }
}
