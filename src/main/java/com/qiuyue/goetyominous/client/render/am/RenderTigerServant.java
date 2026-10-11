package com.qiuyue.goetyominous.client.render.am;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.client.render.model.am.ModelTigerServant;
import com.qiuyue.goetyominous.common.entities.ally.am.TigerServant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class RenderTigerServant extends MobRenderer<TigerServant, ModelTigerServant> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/tiger/tiger.png");
    private static final ResourceLocation TEXTURE_ANGRY = new ResourceLocation("alexsmobs:textures/entity/tiger/tiger_angry.png");
    private static final ResourceLocation TEXTURE_WHITE = new ResourceLocation("alexsmobs:textures/entity/tiger/tiger_white.png");
    private static final ResourceLocation TEXTURE_ANGRY_WHITE = new ResourceLocation("alexsmobs:textures/entity/tiger/tiger_white_angry.png");

    public RenderTigerServant(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelTigerServant(), 0.6F);
        this.addLayer(new LayerTigerServantEyes(this));
    }

    @Override
    protected void scale(TigerServant entity, PoseStack matrixStack, float partialTicks) {
        this.model.young = entity.isBaby();
    }

    @Override
    public void render(TigerServant entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
        float stealthLevel = entity.prevStealthProgress + (entity.stealthProgress - entity.prevStealthProgress) * partialTicks;
        this.shadowRadius = (entity.isBaby() ? 0.3F : 0.6F) * (1.0F - stealthLevel * 0.1F);
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    @Nullable
    @Override
    protected RenderType getRenderType(TigerServant entity, boolean bodyVisible, boolean translucent, boolean glowing) {
        if (entity.isStealth()) {
            return RenderType.entityTranslucent(this.getTextureLocation(entity));
        }
        return super.getRenderType(entity, bodyVisible, translucent, glowing);
    }

    @Override
    public ResourceLocation getTextureLocation(TigerServant entity) {
        boolean angry = entity.getTarget() != null && entity.getTarget().isAlive();
        if (angry) {
            return entity.isWhite() ? TEXTURE_ANGRY_WHITE : TEXTURE_ANGRY;
        }
        return entity.isWhite() ? TEXTURE_WHITE : TEXTURE;
    }
}
