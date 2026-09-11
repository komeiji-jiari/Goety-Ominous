package com.qiuyue.goetyominous.client.render.curios;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.client.render.block.PlushieBlockEntityRenderer;
import com.qiuyue.goetyominous.common.blocks.PlushieBlock;
import com.qiuyue.goetyominous.common.items.ModItems;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.RegistryObject;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.client.ICurioRenderer;

@OnlyIn(Dist.CLIENT)
public class PlushieCurioRenderer implements ICurioRenderer {

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(
            ItemStack stack, SlotContext slotContext, PoseStack poseStack,
            RenderLayerParent<T, M> renderLayerParent, MultiBufferSource buffer, int light,
            float limbSwing, float limbSwingAmount, float partialTicks,
            float ageInTicks, float netHeadYaw, float headPitch) {

        EntityModel<T> model = renderLayerParent.getModel();
        if (!(model instanceof HeadedModel headModel)) {
            return;
        }
        Item item = stack.getItem();
        if (stack.isEmpty() || !(item instanceof BlockItem blockItem)) {
            return;
        }
        Block block = blockItem.getBlock();
        if (!(block instanceof PlushieBlock)) {
            return;
        }
        poseStack.pushPose();
        headModel.getHead().translateAndRotate(poseStack);
        float size = 1.875F;
        poseStack.scale(size, -size, -size);
        poseStack.translate(-0.5D, 0.255D, -0.5D);
        PlushieBlockEntityRenderer.renderItemPlushie(stack, block.defaultBlockState(),
                180.0F, poseStack, buffer, light);
        poseStack.popPose();
    }

    public static void register() {
        for (RegistryObject<Item> entry : ModItems.ITEMS.getEntries()) {
            Item item = entry.get();
            if (item instanceof BlockItem blockItem && blockItem.getBlock() instanceof PlushieBlock) {
                CuriosRendererRegistry.register(item, PlushieCurioRenderer::new);
            }
        }
    }
}
