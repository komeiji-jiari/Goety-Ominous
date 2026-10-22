package com.qiuyue.goetyominous.client.render.pp;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.pp.PiglinAlchemistServantModel;
import com.qiuyue.goetyominous.common.entities.ally.pp.AlchemistBeltHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BeltRenderLayer<T extends Mob & AlchemistBeltHolder, M extends PiglinAlchemistServantModel<T>> extends RenderLayer<T, M> {

    private static final ResourceLocation BELT_TEXTURE =
            new ResourceLocation("piglinproliferation", "textures/entity/piglin/alchemist/belt.png");

    private final ItemInHandRenderer itemInHandRenderer;
    private final PiglinAlchemistServantModel<T> layerModel;

    public BeltRenderLayer(RenderLayerParent<T, M> parent, EntityModelSet modelSet, ItemInHandRenderer itemInHandRenderer) {
        super(parent);
        this.itemInHandRenderer = itemInHandRenderer;
        this.layerModel = new PiglinAlchemistServantModel<>(modelSet.bakeLayer(ModEntityLayers.PIGLIN_ALCHEMIST_SERVANT_BELT_LAYER));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity, float limbSwing,
                       float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        for (int slot = 0; slot < entity.getBeltSize(); ++slot) {
            ItemStack stack = entity.getBeltItem(slot);
            if (!stack.isEmpty()) {
                poseStack.pushPose();
                if (this.getParentModel().young) {
                    poseStack.translate(0.0D, 0.75D, 0.0D);
                    poseStack.scale(0.5F, 0.5F, 0.5F);
                }
                this.renderBeltItem(entity, stack, ItemDisplayContext.GROUND, poseStack, buffer, packedLight, slot);
                poseStack.popPose();
            }
            HumanoidArm arm = entity.isLeftHanded() ? HumanoidArm.RIGHT : HumanoidArm.LEFT;
            ItemDisplayContext displayContext = entity.isLeftHanded()
                    ? ItemDisplayContext.THIRD_PERSON_RIGHT_HAND
                    : ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
            if (!entity.isGonnaThrowPotion()) {
                continue;
            }
            poseStack.pushPose();
            if (this.getParentModel().young) {
                poseStack.translate(0.0D, 0.75D, 0.0D);
                poseStack.scale(0.5F, 0.5F, 0.5F);
            }
            this.renderArmWithItem(entity, entity.getItemShownOnOffhand(), displayContext, arm, poseStack, buffer, packedLight);
            poseStack.popPose();
        }
        coloredCutoutModelCopyLayerRender(this.getParentModel(), this.layerModel, BELT_TEXTURE, poseStack, buffer,
                packedLight, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTick,
                1.0F, 1.0F, 1.0F);
    }

    protected void renderBeltItem(T entity, ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack,
                                  MultiBufferSource buffer, int packedLight, int slot) {
        if (stack.isEmpty()) {
            return;
        }
        poseStack.pushPose();
        this.getParentModel().body.translateAndRotate(poseStack);
        poseStack.scale(0.8F, 0.8F, 0.8F);
        if (slot > 2 || slot != 5) {
            double d0 = -0.24D + (double) slot * 0.44D;
            boolean leggingsOnly = entity.hasItemInSlot(EquipmentSlot.LEGS) && !entity.hasItemInSlot(EquipmentSlot.CHEST);
            double inflation = leggingsOnly ? 0.215D : (entity.hasItemInSlot(EquipmentSlot.CHEST) ? 0.245D : 0.165D);
            poseStack.translate(slot > 2 && slot != 5 ? -d0 + 1.25D : d0, 0.8D, slot > 2 && slot != 5 ? inflation : -inflation);
        }
        if (slot == 2 || slot == 5) {
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            boolean leggingsOnly = entity.hasItemInSlot(EquipmentSlot.LEGS) && !entity.hasItemInSlot(EquipmentSlot.CHEST);
            double secondSlotInflation = leggingsOnly ? -0.29D : (entity.hasItemInSlot(EquipmentSlot.CHEST) ? -0.265D : -0.325D);
            double fifthSlotInflation = leggingsOnly ? -2.31D : (entity.hasItemInSlot(EquipmentSlot.CHEST) ? -2.335D : -2.275D);
            poseStack.translate(-0.2D, 0.4D, slot == 2 ? secondSlotInflation : fifthSlotInflation);
        }
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        itemRenderer.renderStatic(entity, stack, displayContext, false, poseStack, buffer, entity.level(), packedLight,
                LivingEntityRenderer.getOverlayCoords(entity, 0.0F), entity.getId());
        poseStack.popPose();
    }

    protected void renderArmWithItem(T entity, ItemStack stack, ItemDisplayContext displayContext, HumanoidArm arm,
                                     PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (stack.isEmpty()) {
            return;
        }
        poseStack.pushPose();
        this.getParentModel().translateToHand(arm, poseStack);
        poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        boolean leftHand = arm == HumanoidArm.LEFT;
        poseStack.translate((float) (leftHand ? -1 : 1) / 16.0F, 0.125D, -0.625D);
        this.itemInHandRenderer.renderItem(entity, stack, displayContext, leftHand, poseStack, buffer, packedLight);
        poseStack.popPose();
    }
}
