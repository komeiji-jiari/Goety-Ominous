package com.qiuyue.goetyominous.client.render.curios;

import com.Polarice3.Goety.client.render.ModModelLayer;
import com.Polarice3.Goety.client.render.model.EternalCauldronModel;
import com.Polarice3.Goety.common.items.curios.EternalCauldronItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class NetherEternalCauldronCurioRenderer implements ICurioRenderer {
    private static final ResourceLocation FILLED = new ResourceLocation(GoetyOminous.MOD_ID,
            "textures/models/curios/eternal_cauldron.png");
    private static final ResourceLocation EMPTY = new ResourceLocation(GoetyOminous.MOD_ID,
            "textures/models/curios/eternal_cauldron_empty.png");
    private final HumanoidModel<LivingEntity> model;

    public NetherEternalCauldronCurioRenderer() {
        this.model = new EternalCauldronModel(Minecraft.getInstance().getEntityModels()
                .bakeLayer(ModModelLayer.ETERNAL_CAULDRON));
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext,
                                                                          PoseStack poseStack,
                                                                          RenderLayerParent<T, M> renderLayerParent,
                                                                          MultiBufferSource buffer, int light,
                                                                          float limbSwing, float limbSwingAmount,
                                                                          float partialTicks, float ageInTicks,
                                                                          float netHeadYaw, float headPitch) {
        LivingEntity entity = slotContext.entity();
        this.model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
        ICurioRenderer.followBodyRotations(entity, new HumanoidModel[]{this.model});
        ResourceLocation texture = EternalCauldronItem.getBottle(stack).isEmpty() ? EMPTY : FILLED;
        RenderType renderType = this.model.renderType(texture);
        this.model.renderToBuffer(poseStack, buffer.getBuffer(renderType), light, OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 1.0F);
    }
}
