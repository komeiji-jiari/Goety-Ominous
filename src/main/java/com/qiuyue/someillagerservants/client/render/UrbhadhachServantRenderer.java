package com.qiuyue.someillagerservants.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.someillagerservants.SomeIllagerServants;
import com.qiuyue.someillagerservants.client.init.ModEntityLayers;
import com.qiuyue.someillagerservants.client.render.layer.UrbhadhachServantVisageLayer;
import com.qiuyue.someillagerservants.client.render.model.UrbhadhachServantModel;
import com.qiuyue.someillagerservants.common.entities.ally.mobs.UrbhadhachServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class UrbhadhachServantRenderer extends MobRenderer<UrbhadhachServant, UrbhadhachServantModel<UrbhadhachServant>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(SomeIllagerServants.MOD_ID, "textures/entity/urbhadhach_servant.png");

    public UrbhadhachServantRenderer(EntityRendererProvider.Context context) {
        super(context, new UrbhadhachServantModel<>(context.bakeLayer(ModEntityLayers.URBHADHACH_SERVANT_LAYER)), 0.5F);
        this.addLayer(new UrbhadhachServantVisageLayer(this));
    }

    @Override
    protected void scale(UrbhadhachServant entity, PoseStack poseStack, float partialTickTime) {
        poseStack.scale(1.2F, 1.2F, 1.2F);
        super.scale(entity, poseStack, partialTickTime);
    }

    @Override
    public ResourceLocation getTextureLocation(UrbhadhachServant entity) {
        return TEXTURE;
    }
}
