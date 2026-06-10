package com.qiuyue.someillagerservants.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.someillagerservants.SomeIllagerServants;
import com.qiuyue.someillagerservants.client.init.ModEntityLayers;
import com.qiuyue.someillagerservants.client.render.layer.AcolyteServantEyesLayer;
import com.qiuyue.someillagerservants.client.render.model.AcolyteServantModel;
import com.qiuyue.someillagerservants.common.entities.ally.mobs.AcolyteServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AcolyteServantRenderer extends MobRenderer<AcolyteServant, AcolyteServantModel<AcolyteServant>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(SomeIllagerServants.MOD_ID, "textures/entity/acolyte_servant.png");

    public AcolyteServantRenderer(EntityRendererProvider.Context context) {
        super(context, new AcolyteServantModel<>(context.bakeLayer(ModEntityLayers.ACOLYTE_SERVANT_LAYER)), 0.6F);
        this.addLayer(new AcolyteServantEyesLayer(this));
    }

    @Override
    protected void scale(AcolyteServant acolyte, PoseStack poseStack, float partialTick) {
        poseStack.scale(1.0F, 1.0F, 1.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(AcolyteServant acolyte) {
        return TEXTURE;
    }
}