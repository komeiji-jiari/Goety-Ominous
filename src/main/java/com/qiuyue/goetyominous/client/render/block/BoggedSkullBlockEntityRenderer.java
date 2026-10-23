package com.qiuyue.goetyominous.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.common.blocks.BoggedSkullBlock;
import com.qiuyue.goetyominous.common.blocks.BoggedWallSkullBlock;
import com.qiuyue.goetyominous.common.blocks.entities.BoggedSkullBlockEntity;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import org.jetbrains.annotations.Nullable;

public class BoggedSkullBlockEntityRenderer implements BlockEntityRenderer<BoggedSkullBlockEntity> {
    public static final ResourceLocation TEXTURE =
            new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/bogged_skull.png");
    public static final ResourceLocation MUSHROOM_TEXTURE =
            new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/bogged.png");
    private final SkullModel model;
    private final ModelPart mushrooms;

    public BoggedSkullBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new SkullModel(context.bakeLayer(ModelLayers.SKELETON_SKULL));
        this.mushrooms = context.bakeLayer(ModEntityLayers.BOGGED_LAYER)
                .getChild("head").getChild("mushrooms");
        this.mushrooms.getChild("brown_mushroom_1").visible = false;
        this.mushrooms.getChild("brown_mushroom_2").visible = false;
        this.mushrooms.getChild("brown_mushroom_3").visible = false;
        this.mushrooms.getChild("brown_mushroom_4").visible = false;
    }

    @Override
    public void render(BoggedSkullBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        BlockState state = blockEntity.getBlockState();
        boolean wall = state.getBlock() instanceof BoggedWallSkullBlock;
        Direction direction = wall ? state.getValue(BoggedWallSkullBlock.FACING) : null;
        int segment = wall
                ? RotationSegment.convertToSegment(direction.getOpposite())
                : state.getValue(BoggedSkullBlock.ROTATION);
        renderSkull(this.model, this.mushrooms, direction, RotationSegment.convertToDegrees(segment),
                poseStack, buffer, packedLight);
    }

    public static void renderSkull(SkullModel model, @Nullable ModelPart mushrooms, @Nullable Direction direction,
                                   float yRot, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        if (direction == null) {
            poseStack.translate(0.5D, 0.0D, 0.5D);
        } else {
            float f = 0.25F;
            poseStack.translate(0.5F - direction.getStepX() * f, f, 0.5F - direction.getStepZ() * f);
        }
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCullZOffset(TEXTURE));
        model.setupAnim(0.0F, yRot, 0.0F);
        model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        if (mushrooms != null) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
            VertexConsumer mushroomConsumer =
                    buffer.getBuffer(RenderType.entityCutoutNoCullZOffset(MUSHROOM_TEXTURE));
            mushrooms.render(poseStack, mushroomConsumer, packedLight, OverlayTexture.NO_OVERLAY,
                    1.0F, 1.0F, 1.0F, 1.0F);
            poseStack.popPose();
        }
        poseStack.popPose();
    }
}
