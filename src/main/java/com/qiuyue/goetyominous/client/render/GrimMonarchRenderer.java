package com.qiuyue.goetyominous.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.render.layer.GrimMonarchGlowLayer;
import com.qiuyue.goetyominous.client.render.model.GrimMonarchModel;
import com.qiuyue.goetyominous.common.entities.hostile.grimmonarch.GrimMonarchEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GrimMonarchRenderer extends MobRenderer<GrimMonarchEntity, GrimMonarchModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(
            GoetyOminous.MOD_ID, "textures/entity/grim_monarch.png");
    public static final ResourceLocation ECHO_TEXTURE = new ResourceLocation(
            GoetyOminous.MOD_ID, "textures/entity/grim_monarch_echo.png");

    public GrimMonarchRenderer(EntityRendererProvider.Context context) {
        super(context, new GrimMonarchModel(context.bakeLayer(GrimMonarchModel.LAYER_LOCATION)), 0.8F);
        this.addLayer(new GrimMonarchGlowLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(GrimMonarchEntity entity) {
        return entity.isEcho() ? ECHO_TEXTURE : TEXTURE;
    }

    @Override
    protected RenderType getRenderType(GrimMonarchEntity entity, boolean visible, boolean translucent, boolean glowing) {
        if (entity.isEcho() && visible) {
            return GrimMonarchEchoRenderTypes.body(this.getTextureLocation(entity));
        }
        return super.getRenderType(entity, visible, translucent, glowing);
    }

    @Override
    protected float getFlipDegrees(GrimMonarchEntity entity) {
        return 0.0F;
    }

    @Override
    public void render(GrimMonarchEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        if (!entity.ready()) {
            return;
        }
        GrimMonarchEchoShaders.setDissolve(entity.isEcho() ? entity.echoDissolveProgress(partialTicks) : 0.0F);
        int hurtTime = entity.hurtTime;
        int deathTime = entity.deathTime;
        boolean dying = entity.isDeadOrDying();
        if (dying) {
            entity.hurtTime = 0;
            entity.deathTime = 0;
        }
        try {
            super.render(entity, entityYaw, partialTicks, poseStack, buffer,
                    entity.isEcho() ? LightTexture.FULL_BRIGHT : packedLight);
        } finally {
            entity.hurtTime = hurtTime;
            entity.deathTime = deathTime;
        }
    }
}
