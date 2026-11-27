package com.qiuyue.goetyominous.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.render.model.AllyPiglinShamanModel;
import com.qiuyue.goetyominous.common.entities.ally.mobs.AllyPiglinShaman;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AllyPiglinShamanRenderer extends MobRenderer<AllyPiglinShaman, AllyPiglinShamanModel<AllyPiglinShaman>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(
            GoetyOminous.MOD_ID, "textures/entity/piglin/ally_shaman.png");

    public AllyPiglinShamanRenderer(EntityRendererProvider.Context context) {
        super(context, new AllyPiglinShamanModel<>(context.bakeLayer(AllyPiglinShamanModel.LAYER_LOCATION)), 1.0F);
        this.addLayer(new AllyPiglinShamanGlowLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(AllyPiglinShaman entity) {
        return TEXTURE;
    }

    @Override
    protected float getFlipDegrees(AllyPiglinShaman entity) {
        return 0.0F;
    }

    @Override
    protected void scale(AllyPiglinShaman entity, PoseStack poseStack, float partialTicks) {
        poseStack.scale(1.25F, 1.25F, 1.25F);
    }

    @Override
    public void render(AllyPiglinShaman entity, float entityYaw, float partialTicks, PoseStack poseStack,
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
