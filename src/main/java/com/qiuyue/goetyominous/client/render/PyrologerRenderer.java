package com.qiuyue.goetyominous.client.render;

import com.Polarice3.Goety.client.render.layer.HierarchicalArmorLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.PyrologerModel;
import com.qiuyue.goetyominous.common.entities.ally.illager.PyrologerServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PyrologerRenderer<T extends Mob> extends MobRenderer<T, PyrologerModel<T>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("goetyominous", "textures/entity/illager/pyrologer.png");
    private static final ResourceLocation SERVANT_TEXTURE =
            new ResourceLocation("goetyominous", "textures/entity/illager/pyrologer_servant.png");

    public PyrologerRenderer(EntityRendererProvider.Context context) {
        this(context, true);
    }

    public PyrologerRenderer(EntityRendererProvider.Context context, boolean armorLayer) {
        super(context, new PyrologerModel<>(context.bakeLayer(ModEntityLayers.PYROLOGER_LAYER)), 0.5F);
        if (armorLayer) {
            this.addLayer(new HierarchicalArmorLayer<>(this, context));
        }
    }

    @Override
    protected void scale(T entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(0.9375F, 0.9375F, 0.9375F);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return entity instanceof PyrologerServant ? SERVANT_TEXTURE : TEXTURE;
    }
}
