package com.qiuyue.goetyominous.client.render.ac;

import com.github.alexmodguy.alexscaves.client.render.entity.NuclearBombRenderer;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.NuclearBombEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.common.entities.projectile.AnnihilationBombEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.RenderTypeHelper;
import net.minecraftforge.client.model.data.ModelData;

@OnlyIn(Dist.CLIENT)
public class RenderAnnihilationBomb extends EntityRenderer<AnnihilationBombEntity> {

    public RenderAnnihilationBomb(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(AnnihilationBombEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        float progress = ((float) entity.getTime() + partialTicks) / (float) NuclearBombEntity.MAX_TIME;
        float expandScale = 1.0F + Mth.sin(progress * progress * (float) Math.PI) * 0.5F;
        float red = 1.0F - progress * 0.5F;
        float green = 1.0F + progress;
        float blue = 1.0F - progress;
        BlockState state = ACBlockRegistry.TREMORZILLA_EGG.get().defaultBlockState();
        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
        poseStack.pushPose();
        poseStack.scale(1.0F + progress * 0.03F, 1.0F, 1.0F + progress * 0.03F);
        poseStack.pushPose();
        poseStack.scale(expandScale, expandScale - progress * 0.3F, expandScale);
        poseStack.translate(-0.5D, 0.0D, -0.5D);
        for (RenderType renderType : model.getRenderTypes(state, RandomSource.create(42L), ModelData.EMPTY)) {
            NuclearBombRenderer.renderModel(
                    poseStack.last(),
                    buffer.getBuffer(RenderTypeHelper.getEntityRenderType(renderType, false)),
                    state, model, red, green, blue, 240, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, renderType);
        }
        poseStack.popPose();
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(AnnihilationBombEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
