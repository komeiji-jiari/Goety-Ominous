package com.qiuyue.goetyominous.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.render.model.PiglinShamanModel;
import com.qiuyue.goetyominous.common.entities.hostile.piglin.PiglinShaman;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PiglinShamanRenderer extends MobRenderer<PiglinShaman, PiglinShamanModel<PiglinShaman>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(
            GoetyOminous.MOD_ID, "textures/entity/piglin/piglin_shaman.png");

    public PiglinShamanRenderer(EntityRendererProvider.Context context) {
        super(context, new PiglinShamanModel<>(context.bakeLayer(PiglinShamanModel.LAYER_LOCATION)), 1.0F);
        this.addLayer(new PiglinShamanGlowLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(PiglinShaman entity) {
        return TEXTURE;
    }

    @Override
    protected float getFlipDegrees(PiglinShaman entity) {
        return 0.0F;
    }

    @Override
    protected void scale(PiglinShaman entity, PoseStack poseStack, float partialTicks) {
        poseStack.scale(1.25F, 1.25F, 1.25F);
    }

    @Override
    public void render(PiglinShaman entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        int hurtTime = entity.hurtTime;
        int deathTime = entity.deathTime;
        if (entity.isDeadOrDying()) {
            entity.hurtTime = 0;
            entity.deathTime = 0;
        }
        try {
            super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        } finally {
            entity.hurtTime = hurtTime;
            entity.deathTime = deathTime;
        }
    }
}
