package com.qiuyue.goetyominous.client.render.ac;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.render.model.ac.ModelCorrodentServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.CorrodentServant;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class RenderCorrodentServant extends MobRenderer<CorrodentServant, ModelCorrodentServant> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("alexscaves:textures/entity/corrodent.png");
    private static final ResourceLocation TEXTURE_EYES = new ResourceLocation("alexscaves:textures/entity/corrodent_eyes.png");
    private static final Map<BlockPos, Integer> allDugBlocksOnScreen = new HashMap<>();

    public RenderCorrodentServant(EntityRendererProvider.Context context) {
        super(context, new ModelCorrodentServant(), 0.5F);
        this.addLayer(new LayerGlow(this));
    }

    @Override
    public ResourceLocation getTextureLocation(CorrodentServant entity) {
        return TEXTURE;
    }

    @Override
    public void render(CorrodentServant entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
        double x = Mth.lerp(partialTicks, entity.xOld, entity.getX());
        double y = Mth.lerp(partialTicks, entity.yOld, entity.getY());
        double z = Mth.lerp(partialTicks, entity.zOld, entity.getZ());
        float digAmount = entity.getDigAmount(partialTicks);
        if (digAmount > 0) {
            double digEffectDistance = 3;
            for (BlockPos blockPos : BlockPos.betweenClosed((int) Math.floor(x - digEffectDistance), (int) Math.floor(y - digEffectDistance), (int) Math.floor(z - digEffectDistance), (int) Math.floor(x + digEffectDistance), (int) Math.floor(y + digEffectDistance), (int) Math.floor(z + digEffectDistance))) {
                int amount = (int) (entity.getCorrosionAmount(blockPos) * digAmount);
                if (amount >= 0) {
                    allDugBlocksOnScreen.put(blockPos.immutable(), Math.max(allDugBlocksOnScreen.getOrDefault(blockPos, -1), amount));
                }
            }
        }
    }

    public static void renderEntireBatch(LevelRenderer levelRenderer, PoseStack poseStack, int renderTick, Camera camera, float partialTick) {
        if (!allDugBlocksOnScreen.isEmpty()) {
            poseStack.pushPose();
            Vec3 cameraPos = camera.getPosition();
            poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
            MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().crumblingBufferSource();
            for (Map.Entry<BlockPos, Integer> posAndInt : allDugBlocksOnScreen.entrySet()) {
                int progress = posAndInt.getValue() - 1;
                if (progress >= 0 && progress < 10) {
                    poseStack.pushPose();
                    BlockPos pos = posAndInt.getKey();
                    poseStack.translate(pos.getX(), pos.getY(), pos.getZ());
                    PoseStack.Pose pose = poseStack.last();
                    VertexConsumer vertexConsumer = new SheetedDecalTextureGenerator(bufferSource.getBuffer(ModelBakery.DESTROY_TYPES.get(progress)), pose.pose(), pose.normal(), 1.0F);
                    ModelData modelData = Minecraft.getInstance().level.getModelDataManager().getAt(pos);
                    Minecraft.getInstance().getBlockRenderer().renderBreakingTexture(Minecraft.getInstance().level.getBlockState(pos), pos, Minecraft.getInstance().level, poseStack, vertexConsumer, modelData == null ? ModelData.EMPTY : modelData);
                    poseStack.popPose();
                }
            }
            poseStack.popPose();
        }
        allDugBlocksOnScreen.clear();
    }

    public static class LayerGlow extends RenderLayer<CorrodentServant, ModelCorrodentServant> {

        public LayerGlow(RenderLayerParent<CorrodentServant, ModelCorrodentServant> renderLayerParent) {
            super(renderLayerParent);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CorrodentServant entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            VertexConsumer eyesBuffer = bufferSource.getBuffer(RenderType.eyes(TEXTURE_EYES));
            this.getParentModel().renderToBuffer(poseStack, eyesBuffer, packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
