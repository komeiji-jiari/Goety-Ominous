package com.qiuyue.goetyominous.client.render.am;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.client.render.model.am.ModelCrimsonMosquitoServant;
import com.qiuyue.goetyominous.common.entities.ally.am.CrimsonMosquitoServant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderCrimsonMosquitoServant extends MobRenderer<CrimsonMosquitoServant, ModelCrimsonMosquitoServant> {
    public static final ResourceLocation TEXTURE = new ResourceLocation("goetyominous", "textures/entity/crimson_mosquito_servant.png");
    public static final ResourceLocation TEXTURE_SICK = new ResourceLocation("goetyominous", "textures/entity/crimson_mosquito_servant_blue.png");
    public static final ResourceLocation TEXTURE_FLY = new ResourceLocation("goetyominous", "textures/entity/crimson_mosquito_servant_fly.png");
    public static final ResourceLocation TEXTURE_SICK_FLY = new ResourceLocation("goetyominous", "textures/entity/crimson_mosquito_servant_fly_blue.png");

    public RenderCrimsonMosquitoServant(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelCrimsonMosquitoServant(), 0.6F);
        this.addLayer(new LayerCrimsonMosquitoServantBlood(this));
    }

    @Override
    public void render(CrimsonMosquitoServant entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
        this.model.renderPartialTicks = partialTicks;
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    @Override
    protected void scale(CrimsonMosquitoServant entity, PoseStack matrixStack, float partialTicks) {
        float f = Mth.lerp(partialTicks, entity.prevMosquitoScale, entity.getMosquitoScale()) * 1.2F;
        matrixStack.scale(f, f, f);
    }

    @Override
    protected boolean isShaking(CrimsonMosquitoServant entity) {
        return entity.isSick() || entity.getFleeingEntityId() != -1;
    }

    @Override
    protected void setupRotations(CrimsonMosquitoServant entity, PoseStack matrixStack, float ageInTicks, float rotationYaw, float partialTicks) {
        if (this.isShaking(entity)) {
            partialTicks += (float) (Math.cos((double) entity.tickCount * 7.0D) * Math.PI * 0.9D);
            float f1 = 0.05F * entity.getMosquitoScale();
            matrixStack.translate((entity.getRandom().nextFloat() - 0.5F) * f1, (entity.getRandom().nextFloat() - 0.5F) * f1, (entity.getRandom().nextFloat() - 0.5F) * f1);
        }
        super.setupRotations(entity, matrixStack, ageInTicks, rotationYaw, partialTicks);
    }

    @Override
    public ResourceLocation getTextureLocation(CrimsonMosquitoServant entity) {
        if (entity.isSick()) {
            return entity.isFlying() ? TEXTURE_SICK_FLY : TEXTURE_SICK;
        }
        return entity.isFlying() ? TEXTURE_FLY : TEXTURE;
    }
}
