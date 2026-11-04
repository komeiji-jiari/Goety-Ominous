package com.qiuyue.goetyominous.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.layer.UrbhadhachVisageLayer;
import com.qiuyue.goetyominous.client.render.model.UrbhadhachModel;
import com.qiuyue.goetyominous.common.entities.ally.mobs.UrbhadhachServant;
import com.qiuyue.goetyominous.common.entities.ally.neutral.AbstractUrbhadhach;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class UrbhadhachRenderer<T extends AbstractUrbhadhach> extends MobRenderer<T, UrbhadhachModel<T>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/urbhadhach.png");
    private static final ResourceLocation SERVANT_TEXTURE =
            new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/urbhadhach_servant.png");

    public UrbhadhachRenderer(EntityRendererProvider.Context context) {
        super(context, new UrbhadhachModel<>(context.bakeLayer(ModEntityLayers.URBHADHACH_LAYER)), 0.5F);
        this.addLayer(new UrbhadhachVisageLayer<>(this));
    }

    @Override
    protected void scale(T entity, PoseStack poseStack, float partialTickTime) {
        poseStack.scale(1.2F, 1.2F, 1.2F);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return entity instanceof UrbhadhachServant ? SERVANT_TEXTURE : TEXTURE;
    }
}
