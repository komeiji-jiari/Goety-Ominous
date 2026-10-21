package com.qiuyue.goetyominous.client.render.pp;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.pp.PiglinAlchemistServantModel;
import com.qiuyue.goetyominous.common.entities.ally.pp.PiglinAlchemistServant;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PiglinAlchemistServantRenderer<T extends PiglinAlchemistServant>
        extends HumanoidMobRenderer<T, PiglinAlchemistServantModel<T>> {

    protected static final ResourceLocation TEXTURE =
            new ResourceLocation("piglinproliferation", "textures/entity/piglin/alchemist/alchemist.png");

    public PiglinAlchemistServantRenderer(EntityRendererProvider.Context context) {
        super(context, new PiglinAlchemistServantModel<>(context.bakeLayer(ModEntityLayers.PIGLIN_ALCHEMIST_SERVANT_LAYER)),
                0.5F, 1.0019531F, 1.0F, 1.0019531F);
        this.addLayer(new HumanoidArmorLayer<>(this,
                new HumanoidArmorModel<>(context.bakeLayer(ModelLayers.PIGLIN_INNER_ARMOR)),
                new HumanoidArmorModel<>(context.bakeLayer(ModEntityLayers.PIGLIN_SERVANT_PP_OUTER_ARMOR_LAYER)),
                context.getModelManager()));
        this.addLayer(new BeltRenderLayer<>(this, context.getModelSet(), context.getItemInHandRenderer()));
    }

    private static HumanoidModel.ArmPose getArmPose(PiglinAlchemistServant mob, InteractionHand hand) {
        ItemStack stack = mob.getItemInHand(hand);
        HumanoidModel.ArmPose pose = HumanoidModel.ArmPose.EMPTY;
        if (!mob.isUsingItem() && !mob.isGonnaThrowPotion() && mob.isAggressive()) {
            if (mob.isHolding(item -> item.getItem() instanceof CrossbowItem) && CrossbowItem.isCharged(stack)) {
                pose = HumanoidModel.ArmPose.CROSSBOW_HOLD;
            }
            if (mob.isHolding(item -> item.getItem() instanceof BowItem)
                    && mob.getDeltaMovement().x <= 0.0D
                    && mob.getDeltaMovement().y <= 0.0D
                    && mob.getDeltaMovement().z <= 0.0D) {
                pose = HumanoidModel.ArmPose.BOW_AND_ARROW;
            }
        }
        if (mob.getUsedItemHand() == hand && mob.getUseItemRemainingTicks() > 0) {
            pose = switch (stack.getUseAnimation()) {
                case BOW -> HumanoidModel.ArmPose.BOW_AND_ARROW;
                case CROSSBOW -> HumanoidModel.ArmPose.CROSSBOW_CHARGE;
                case BLOCK -> HumanoidModel.ArmPose.BLOCK;
                case SPYGLASS -> HumanoidModel.ArmPose.SPYGLASS;
                case SPEAR -> HumanoidModel.ArmPose.THROW_SPEAR;
                default -> HumanoidModel.ArmPose.EMPTY;
            };
        }
        return pose;
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        this.setBipedArmPoses(entity);
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    protected void setBipedArmPoses(T mob) {
        HumanoidModel.ArmPose mainPose = getArmPose(mob, InteractionHand.MAIN_HAND);
        HumanoidModel.ArmPose offPose = getArmPose(mob, InteractionHand.OFF_HAND);
        if (mainPose.isTwoHanded()) {
            offPose = mob.getOffhandItem().isEmpty() ? HumanoidModel.ArmPose.EMPTY : HumanoidModel.ArmPose.ITEM;
        }
        if (mob.getMainArm() == HumanoidArm.RIGHT) {
            this.model.rightArmPose = mainPose;
            this.model.leftArmPose = offPose;
        } else {
            this.model.rightArmPose = offPose;
            this.model.leftArmPose = mainPose;
        }
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }

    @Override
    protected boolean isShaking(T entity) {
        return super.isShaking(entity) || entity.isConverting();
    }
}
