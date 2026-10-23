package com.qiuyue.goetyominous.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.client.render.block.MiredSkullBlockEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MiredSkullISTER extends BlockEntityWithoutLevelRenderer {
    private static MiredSkullISTER instance;

    private final SkullModel model;

    public static MiredSkullISTER get() {
        if (instance == null) {
            instance = new MiredSkullISTER();
        }
        return instance;
    }

    public MiredSkullISTER() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
        this.model = new SkullModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_HEAD));
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack,
                             MultiBufferSource buffer, int light, int overlay) {
        MiredSkullBlockEntityRenderer.renderSkull(this.model, null, 180.0F, poseStack, buffer, light);
    }
}
