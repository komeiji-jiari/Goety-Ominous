package com.qiuyue.goetyominous.client.render.am;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.client.render.model.am.ModelTusklinServant;
import com.qiuyue.goetyominous.common.entities.ally.am.TusklinServant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderTusklinServant extends MobRenderer<TusklinServant, ModelTusklinServant> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("goetyominous:textures/entity/tusklin_servant.png");

    public RenderTusklinServant(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelTusklinServant(), 1.0F);
        this.addLayer(new LayerTusklinServantGear(this));
    }

    @Override
    protected boolean isShaking(TusklinServant entity) {
        return entity.isInNether();
    }

    @Override
    protected void scale(TusklinServant entity, PoseStack matrixStack, float partialTicks) {
        this.model.young = entity.isBaby();
    }

    @Override
    public void render(TusklinServant entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
        this.shadowRadius = entity.isBaby() ? 0.29F : 0.65F;
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(TusklinServant entity) {
        return TEXTURE;
    }
}
