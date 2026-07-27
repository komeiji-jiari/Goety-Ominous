package com.qiuyue.goetyominus.client.render;

import com.google.common.collect.Maps;
import com.qiuyue.goetyominus.GoetyOminous;
import com.qiuyue.goetyominus.client.init.ModEntityLayers;
import com.qiuyue.goetyominus.client.render.model.AxolotlServantModel;
import com.qiuyue.goetyominus.common.entities.ally.mobs.AxolotlServant;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.Util;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Locale;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class AxolotlServantRenderer extends MobRenderer<AxolotlServant, AxolotlServantModel<AxolotlServant>> {

    private static final Map<AxolotlServant.Variant, ResourceLocation> TEXTURE_BY_TYPE = Util.make(Maps.newHashMap(), (map) -> {
        for (AxolotlServant.Variant variant : AxolotlServant.Variant.values()) {
            map.put(variant, new ResourceLocation(String.format(Locale.ROOT,
                    "textures/entity/axolotl/axolotl_%s.png", variant.getName())));
        }
    });

    public AxolotlServantRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new AxolotlServantModel<>(pContext.bakeLayer(ModEntityLayers.AXOLOTL_SERVANT_LAYER)), 0.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(AxolotlServant pEntity) {
        return TEXTURE_BY_TYPE.getOrDefault(pEntity.getVariant(),
                TEXTURE_BY_TYPE.get(AxolotlServant.Variant.LUCY));
    }

    @Override
    protected void setupRotations(AxolotlServant pEntity, PoseStack pPoseStack,
                                  float pAgeInTicks, float pRotationYaw, float pPartialTicks) {
        super.setupRotations(pEntity, pPoseStack, pAgeInTicks, pRotationYaw, pPartialTicks);

        if (!pEntity.isInWater()) {
            pPoseStack.translate(0.0F, 0.1F, 0.0F);
            pPoseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
        }
    }
}
