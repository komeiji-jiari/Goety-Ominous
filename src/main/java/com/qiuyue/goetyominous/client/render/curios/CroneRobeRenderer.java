package com.qiuyue.goetyominous.client.render.curios;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.render.model.curios.CroneRobeModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.client.ICurioRenderer;

@OnlyIn(Dist.CLIENT)
public class CroneRobeRenderer implements ICurioRenderer {
    public static final ResourceLocation TEXTURE =
            new ResourceLocation("goetyominous", "textures/models/curios/crone_robe.png");
    public static final ResourceLocation TEXTURE_ALT =
            new ResourceLocation("goetyominous", "textures/models/curios/crone_robe_alt.png");

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(
            ItemStack stack, SlotContext slotContext, PoseStack poseStack,
            RenderLayerParent<T, M> renderLayerParent, MultiBufferSource buffer,
            int light, float limbSwing, float limbSwingAmount, float partialTicks,
            float ageInTicks, float netHeadYaw, float headPitch) {
        LivingEntity entity = slotContext.entity();
        EntityModelSet models = Minecraft.getInstance().getEntityModels();
        CroneRobeModel model = new CroneRobeModel(models.bakeLayer(CroneRobeModel.LAYER_LOCATION));

        model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        poseStack.pushPose();
        ResourceLocation texture = stack.is(com.qiuyue.goetyominous.common.items.ModItems.CRONE_ROBE_ALT.get())
                ? TEXTURE_ALT : TEXTURE;
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
        model.renderToBuffer(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
    }

    public static void register() {
        CuriosRendererRegistry.register(com.qiuyue.goetyominous.common.items.ModItems.CRONE_ROBE.get(),
                () -> new CroneRobeRenderer());
        CuriosRendererRegistry.register(com.qiuyue.goetyominous.common.items.ModItems.CRONE_ROBE_ALT.get(),
                () -> new CroneRobeRenderer());
    }
}
