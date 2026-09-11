package com.Polarice3.Goety.client.render.block;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.model.PlushieModel;
import com.Polarice3.Goety.common.blocks.PlushieBlock;
import com.Polarice3.Goety.common.blocks.entities.PlushieBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class PlushieBlockEntityRenderer implements BlockEntityRenderer<PlushieBlockEntity> {
    protected static final ResourceLocation TEXTURE = Goety.location("textures/entity/plushie/0.png");

    public PlushieBlockEntityRenderer(BlockEntityRendererProvider.Context p_i226015_1_) {
    }

    public void render(PlushieBlockEntity pBlockEntity, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pCombinedLight, int pCombinedOverlay) {
        BlockState blockstate = pBlockEntity.m_58900_();
        float f1 = 22.5F * (float)((Integer)blockstate.m_61143_(PlushieBlock.ROTATION)).intValue();
        renderPlushie(pBlockEntity, pPartialTicks, f1, pMatrixStack, pBuffer, pCombinedLight);
    }

    public static void renderPlushie(PlushieBlockEntity pBlockEntity, float pPartialTicks, float rotateY, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pLight) {
        PlushieModel plushieModel = new PlushieModel(Minecraft.m_91087_().m_167973_().m_171103_(ModBlockLayer.PLUSHIE));
        pMatrixStack.m_85836_();
        pMatrixStack.m_85837_(0.5D, 0.0D, 0.5D);
        pMatrixStack.m_85841_(-1.0F, -1.0F, 1.0F);
        float f = pBlockEntity.getAnimation(pPartialTicks);
        VertexConsumer consumer = pBuffer.m_6299_(RenderType.m_110464_(getTexture(pBlockEntity.m_58900_())));
        plushieModel.m_6251_(f, rotateY, 0.0F);
        plushieModel.m_7695_(pMatrixStack, consumer, pLight, OverlayTexture.f_118083_, 1.0F, 1.0F, 1.0F, 1.0F);
        pMatrixStack.m_85849_();
    }

    public static void renderItemPlushie(ItemStack stack, BlockState blockState, float rotateY, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pCombinedLight) {
        PlushieModel plushieModel = new PlushieModel(Minecraft.m_91087_().m_167973_().m_171103_(ModBlockLayer.PLUSHIE));
        pMatrixStack.m_85836_();
        pMatrixStack.m_85837_(0.5D, 0.0D, 0.5D);
        pMatrixStack.m_85841_(-1.0F, -1.0F, 1.0F);
        pMatrixStack.m_85841_(0.5F, 0.5F, 0.5F);
        VertexConsumer vertexConsumer = ItemRenderer.m_115222_(pBuffer, RenderType.m_110473_(getTexture(blockState)), true, stack.m_41790_());
        plushieModel.m_6251_(0.0F, rotateY, 0.0F);
        plushieModel.m_7695_(pMatrixStack, vertexConsumer, pCombinedLight, OverlayTexture.f_118083_, 1.0F, 1.0F, 1.0F, 1.0F);
        pMatrixStack.m_85849_();
    }

    public static ResourceLocation getTexture(BlockState blockState) {
        ResourceLocation texture = TEXTURE;
        Block var3 = blockState.m_60734_();
        if (var3 instanceof PlushieBlock plushieBlock) {
            texture = Goety.location("textures/entity/plushie/" + plushieBlock.getPlushieType() + ".png");
        }

        return texture;
    }
}
