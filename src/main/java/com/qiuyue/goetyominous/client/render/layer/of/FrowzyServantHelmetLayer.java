package com.qiuyue.goetyominous.client.render.layer.of;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.render.model.of.FrowzyServantModel;
import com.qiuyue.goetyominous.common.entities.ally.of.FrowzyServant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import javax.annotation.Nullable;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class FrowzyServantHelmetLayer extends RenderLayer<FrowzyServant, FrowzyServantModel> {
    private static final Map<String, ResourceLocation> ARMOR_TEXTURE_RES_MAP = Maps.newHashMap();
    private final HumanoidModel<?> defaultBipedModel;

    public FrowzyServantHelmetLayer(RenderLayerParent<FrowzyServant, FrowzyServantModel> renderer, EntityRendererProvider.Context context) {
        super(renderer);
        this.defaultBipedModel = new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR));
    }

    public static ResourceLocation getArmorResource(Entity entity, ItemStack stack, EquipmentSlot slot, @Nullable String type) {
        ArmorItem item = (ArmorItem) stack.getItem();
        String texture = item.getMaterial().getName();
        String domain = "minecraft";
        int idx = texture.indexOf(':');
        if (idx != -1) {
            domain = texture.substring(0, idx);
            texture = texture.substring(idx + 1);
        }
        String s1 = String.format("%s:textures/models/armor/%s_layer_%d%s.png", domain, texture, 1,
                type == null ? "" : String.format("_%s", type));
        s1 = ForgeHooksClient.getArmorTexture(entity, stack, s1, slot, type);
        ResourceLocation resourceLocation = ARMOR_TEXTURE_RES_MAP.get(s1);
        if (resourceLocation == null) {
            resourceLocation = new ResourceLocation(s1);
            ARMOR_TEXTURE_RES_MAP.put(s1, resourceLocation);
        }
        return resourceLocation;
    }

    @Override
    public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight,
                       @NotNull FrowzyServant entity, float limbSwing, float limbSwingAmount, float partialTicks,
                       float ageInTicks, float netHeadYaw, float headPitch) {
        poseStack.pushPose();
        ItemStack helmet = entity.getItemBySlot(EquipmentSlot.HEAD);
        if (helmet.getItem() instanceof ArmorItem armorItem && helmet.canEquip(EquipmentSlot.HEAD, entity)) {
            HumanoidModel<?> bipedModel = this.getArmorModelHook(entity, helmet, this.defaultBipedModel);
            this.setModelSlotVisible(bipedModel, EquipmentSlot.HEAD);
            this.getParentModel().translateToHead(poseStack);
            boolean enchanted = helmet.hasFoil();
            if (armorItem instanceof DyeableLeatherItem dyeableLeatherItem) {
                int dyeColor = dyeableLeatherItem.getColor(helmet);
                float red = (float) (dyeColor >> 16 & 0xFF) / 255.0F;
                float green = (float) (dyeColor >> 8 & 0xFF) / 255.0F;
                float blue = (float) (dyeColor & 0xFF) / 255.0F;
                this.renderHelmet(poseStack, bufferSource, packedLight, enchanted, bipedModel, red, green, blue,
                        getArmorResource(entity, helmet, EquipmentSlot.HEAD, null));
                this.renderHelmet(poseStack, bufferSource, packedLight, enchanted, bipedModel, 1.0F, 1.0F, 1.0F,
                        getArmorResource(entity, helmet, EquipmentSlot.HEAD, "overlay"));
            } else {
                this.renderHelmet(poseStack, bufferSource, packedLight, enchanted, bipedModel, 1.0F, 1.0F, 1.0F,
                        getArmorResource(entity, helmet, EquipmentSlot.HEAD, null));
            }
        } else if (!helmet.isEmpty()) {
            this.getParentModel().translateToHead(poseStack);
            poseStack.translate(0.0F, -0.25F, 0.0F);
            poseStack.mulPose(new Quaternionf().rotateX((float) Math.PI));
            poseStack.mulPose(new Quaternionf().rotateY((float) Math.PI));
            poseStack.scale(1.15F, 1.15F, 1.15F);
            Minecraft.getInstance().getItemRenderer().renderStatic(helmet, ItemDisplayContext.FIXED, packedLight,
                    OverlayTexture.NO_OVERLAY, poseStack, bufferSource, entity.level(), 0);
        }
        poseStack.popPose();
    }

    private void renderHelmet(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, boolean glint,
                              HumanoidModel<?> humanoidModel, float red, float green, float blue,
                              ResourceLocation armorResource) {
        VertexConsumer vertexConsumer = ItemRenderer.getArmorFoilBuffer(bufferSource,
                RenderType.entityCutoutNoCull(armorResource), false, glint);
        this.getParentModel().copyPropertiesTo((EntityModel) humanoidModel);
        humanoidModel.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY,
                red, green, blue, 1.0F);
    }

    protected void setModelSlotVisible(HumanoidModel<?> model, EquipmentSlot slot) {
        this.setModelVisible(model);
        if (slot == EquipmentSlot.HEAD) {
            model.head.visible = true;
            model.hat.visible = true;
        }
    }

    protected void setModelVisible(HumanoidModel<?> model) {
        model.setAllVisible(false);
    }

    protected HumanoidModel<?> getArmorModelHook(LivingEntity entity, ItemStack itemStack, HumanoidModel<?> model) {
        Model basicModel = ForgeHooksClient.getArmorModel(entity, itemStack, EquipmentSlot.HEAD, model);
        return basicModel instanceof HumanoidModel<?> humanoidModel ? humanoidModel : model;
    }
}
