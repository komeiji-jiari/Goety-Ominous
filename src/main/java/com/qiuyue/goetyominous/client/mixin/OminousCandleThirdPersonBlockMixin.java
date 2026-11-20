package com.qiuyue.goetyominous.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.client.render.item.OminousCandleBlockRenderer;
import com.qiuyue.goetyominous.common.items.OminousCandleItem;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.PlayerItemInHandLayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerItemInHandLayer.class)
public abstract class OminousCandleThirdPersonBlockMixin<T extends Player,
        M extends EntityModel<T> & ArmedModel & HeadedModel> extends ItemInHandLayer<T, M> {

    @Shadow
    @Final
    private ItemInHandRenderer itemInHandRenderer;

    protected OminousCandleThirdPersonBlockMixin(RenderLayerParent<T, M> parent,
                                                 ItemInHandRenderer itemInHandRenderer) {
        super(parent, itemInHandRenderer);
    }

    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
    protected void goetyominous$ominousCandleThirdPerson(LivingEntity entity, ItemStack stack,
                                                          ItemDisplayContext transform, HumanoidArm arm,
                                                          PoseStack poseStack, MultiBufferSource multiBufferSource,
                                                          int packedLight, CallbackInfo ci) {
        if (!(entity instanceof Player player) || stack.isEmpty()
                || !(stack.getItem() instanceof OminousCandleItem)) {
            return;
        }
        if (!player.isUsingItem() || !player.getUseItem().is(stack.getItem())) {
            return;
        }
        InteractionHand handForArm = arm == player.getMainArm()
                ? InteractionHand.MAIN_HAND
                : InteractionHand.OFF_HAND;
        if (player.getUsedItemHand() != handForArm) {
            return;
        }
        OminousCandleBlockRenderer.renderBlockingWithSword(this.itemInHandRenderer,
                (ArmedModel) this.getParentModel(), entity, stack, transform, arm, poseStack,
                multiBufferSource, packedLight);
        ci.cancel();
    }
}
