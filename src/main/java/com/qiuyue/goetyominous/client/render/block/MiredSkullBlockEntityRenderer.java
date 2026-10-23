package com.qiuyue.goetyominous.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.blocks.MiredSkullBlock;
import com.qiuyue.goetyominous.common.blocks.MiredWallSkullBlock;
import com.qiuyue.goetyominous.common.blocks.entities.MiredSkullBlockEntity;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.ModelLayers;
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

public class MiredSkullBlockEntityRenderer implements BlockEntityRenderer<MiredSkullBlockEntity> {
    public static final ResourceLocation TEXTURE =
            new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/mired_skull.png");
    private final SkullModel model;

    public MiredSkullBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new SkullModel(context.bakeLayer(ModelLayers.PLAYER_HEAD));
    }

    @Override
    public void render(MiredSkullBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        BlockState state = blockEntity.getBlockState();
        boolean wall = state.getBlock() instanceof MiredWallSkullBlock;
        Direction direction = wall ? state.getValue(MiredWallSkullBlock.FACING) : null;
        int segment = wall
                ? RotationSegment.convertToSegment(direction.getOpposite())
                : state.getValue(MiredSkullBlock.ROTATION);
        renderSkull(this.model, direction, RotationSegment.convertToDegrees(segment), poseStack, buffer, packedLight);
    }

    public static void renderSkull(SkullModel model, @Nullable Direction direction, float yRot,
                                   PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
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
        poseStack.popPose();
    }
}
