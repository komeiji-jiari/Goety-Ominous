package com.qiuyue.goetyominous.client.render.of;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.of.SkyvernServantHeadModel;
import com.qiuyue.goetyominous.common.entities.ally.of.SkyvernServant;
import com.unusualmodding.opposing_force.entity.Skyvern.SkyvernVariant;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class SkyvernServantRenderer extends MobRenderer<SkyvernServant, SkyvernServantHeadModel> {
    private static final ResourceLocation CLOUDY = new ResourceLocation("opposing_force", "textures/entity/skyvern/cloudy.png");
    private static final ResourceLocation AZURE = new ResourceLocation("opposing_force", "textures/entity/skyvern/azure.png");
    private static final ResourceLocation THUNDER = new ResourceLocation("opposing_force", "textures/entity/skyvern/thunder.png");

    public SkyvernServantRenderer(EntityRendererProvider.Context context) {
        super(context, new SkyvernServantHeadModel(context.bakeLayer(ModEntityLayers.SKYVERN_SERVANT_LAYER)), 0.5F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SkyvernServant entity) {
        return switch (entity.getVariant()) {
            case CLOUDY -> CLOUDY;
            case AZURE -> AZURE;
            case THUNDER -> THUNDER;
        };
    }

    @Override
    protected RenderType getRenderType(@NotNull SkyvernServant entity, boolean visible, boolean translucent, boolean glowing) {
        if (entity.isGhost()) {
            return RenderType.entityTranslucent(this.getTextureLocation(entity));
        }
        return super.getRenderType(entity, visible, translucent, glowing);
    }

    @Override
    protected float getFlipDegrees(@NotNull SkyvernServant entity) {
        return 0.0F;
    }

    @Override
    protected void setupRotations(@NotNull SkyvernServant entity, @NotNull PoseStack poseStack, float bob, float yaw, float partialTicks) {
        if (this.isShaking(entity)) {
            yaw += (float) (Math.cos((double) entity.tickCount * 3.25D) * Math.PI * 0.4000000059604645D);
        }
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
        poseStack.translate(0.0F, 1.0F, 0.0F);
        poseStack.mulPose(Axis.XP.rotationDegrees(-entity.getViewXRot(partialTicks)));
        poseStack.translate(0.0F, -1.0F, 0.0F);
        if (LivingEntityRenderer.isEntityUpsideDown(entity)) {
            poseStack.translate(0.0F, entity.getBbHeight() + 0.1F, 0.0F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        }
    }

    public static ResourceLocation textureFor(SkyvernVariant variant) {
        return switch (variant) {
            case CLOUDY -> CLOUDY;
            case AZURE -> AZURE;
            case THUNDER -> THUNDER;
        };
    }
}
