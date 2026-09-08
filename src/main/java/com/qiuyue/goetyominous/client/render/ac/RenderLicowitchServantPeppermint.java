package com.qiuyue.goetyominous.client.render.ac;

import com.github.alexmodguy.alexscaves.client.ClientProxy;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexthe666.citadel.client.shader.PostEffectRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.common.entities.projectile.LicowitchServantPeppermint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

import javax.annotation.Nullable;
import java.util.List;

public class RenderLicowitchServantPeppermint extends EntityRenderer<LicowitchServantPeppermint> {

    public RenderLicowitchServantPeppermint(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(LicowitchServantPeppermint entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource source, int lightIn) {
        super.render(entity, entityYaw, partialTicks, poseStack, source, lightIn);
        PostEffectRegistry.renderEffectForNextTick(ClientProxy.PURPLE_WITCH_SHADER);
        float ageInTicks = partialTicks + (float) entity.tickCount;
        float despawnsIn = entity.getDespawnTime(partialTicks);
        float minAge = Math.min(1.0F, Math.min(ageInTicks, despawnsIn) / 10.0F);
        poseStack.pushPose();
        poseStack.scale(minAge, minAge, minAge);
        poseStack.translate(0.0D, 0.5D, 0.0D);
        if (entity.isStraight()) {
            poseStack.mulPose(Axis.YN.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) + 90.0F));
            poseStack.mulPose(Axis.ZN.rotationDegrees((float) ((double) Mth.lerp(partialTicks, entity.xRotO, entity.getXRot()) + 5.0D * Math.sin(ageInTicks * 0.2D))));
            poseStack.mulPose(Axis.ZP.rotationDegrees(ageInTicks * -4.0F * entity.getSpinSpeed()));
            poseStack.translate(0.0D, 0.0D, -0.25D);
        } else {
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(ageInTicks * -4.0F * entity.getSpinSpeed()));
            poseStack.mulPose(Axis.XP.rotationDegrees((float) Math.sin(ageInTicks * 0.8F) * 8.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees((float) Math.cos(ageInTicks * 0.8F) * 8.0F));
        }
        poseStack.translate(-0.5D, -0.5D, -0.5D);
        BakedModel bakedmodel = Minecraft.getInstance().getItemRenderer().getModel(entity.peppermintRenderStack, entity.level(), null, 0);
        int redOverlay = OverlayTexture.pack(OverlayTexture.u(0.0F), OverlayTexture.v(true));
        for (RenderType rt : bakedmodel.getRenderTypes(entity.peppermintRenderStack, false)) {
            renderModel(poseStack.last(), source.getBuffer(Sheets.translucentItemSheet()), 1.0F, null, bakedmodel,
                    1.0F, minAge, 1.0F, lightIn, redOverlay, ModelData.EMPTY, rt);
        }
        RenderType purpleWitch = ACRenderTypes.getPurpleWitch(TextureAtlas.LOCATION_BLOCKS);
        renderModel(poseStack.last(), source.getBuffer(purpleWitch), 1.0F, null, bakedmodel,
                1.0F, 1.0F, 1.0F, lightIn, redOverlay, ModelData.EMPTY, purpleWitch);
        poseStack.popPose();
    }

    private static void renderModel(PoseStack.Pose pose, VertexConsumer consumer, float alpha, @Nullable BlockState state,
                                    BakedModel model, float r, float g, float b, int light, int overlay,
                                    ModelData modelData, RenderType renderType) {
        RandomSource randomsource = RandomSource.create();
        for (Direction direction : Direction.values()) {
            randomsource.setSeed(42L);
            renderQuadList(pose, consumer, r, g, b, alpha, model.getQuads(state, direction, randomsource, modelData, renderType), light, overlay);
        }
        randomsource.setSeed(42L);
        renderQuadList(pose, consumer, r, g, b, alpha, model.getQuads(state, null, randomsource, modelData, renderType), light, overlay);
    }

    private static void renderQuadList(PoseStack.Pose pose, VertexConsumer consumer, float r, float g, float b, float alpha,
                                       List<BakedQuad> quads, int light, int overlay) {
        for (BakedQuad quad : quads) {
            float f = Mth.clamp(r, 0.0F, 1.0F);
            float f1 = Mth.clamp(g, 0.0F, 1.0F);
            float f2 = Mth.clamp(b, 0.0F, 1.0F);
            consumer.putBulkData(pose, quad, new float[]{1.0F, 1.0F, 1.0F, 1.0F}, f, f1, f2, alpha,
                    new int[]{light, light, light, light}, overlay, false);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(LicowitchServantPeppermint entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
