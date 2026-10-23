package com.qiuyue.goetyominous.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.block.BoggedSkullBlockEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BoggedSkullISTER extends BlockEntityWithoutLevelRenderer {
    private static BoggedSkullISTER instance;

    private final SkullModel model;
    private final ModelPart mushrooms;

    public static BoggedSkullISTER get() {
        if (instance == null) {
            instance = new BoggedSkullISTER();
        }
        return instance;
    }

    public BoggedSkullISTER() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
        this.model = new SkullModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.SKELETON_SKULL));
        this.mushrooms = Minecraft.getInstance().getEntityModels().bakeLayer(ModEntityLayers.BOGGED_LAYER)
                .getChild("head").getChild("mushrooms");
        this.mushrooms.getChild("brown_mushroom_1").visible = false;
        this.mushrooms.getChild("brown_mushroom_2").visible = false;
        this.mushrooms.getChild("brown_mushroom_3").visible = false;
        this.mushrooms.getChild("brown_mushroom_4").visible = false;
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack,
                             MultiBufferSource buffer, int light, int overlay) {
        BoggedSkullBlockEntityRenderer.renderSkull(this.model, this.mushrooms, null, 180.0F, poseStack, buffer, light);
    }
}
