/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.EntityModel
 *  net.minecraft.client.renderer.entity.LivingEntityRenderer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.vivideru.masteryofmagic.mixins;

import com.vivideru.masteryofmagic.TimeFreezeRenderAnimationState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={LivingEntityRenderer.class})
public abstract class TimeFreezeLivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {
    @Inject(method={"m_6930_(Lnet/minecraft/world/entity/LivingEntity;F)F"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private void goetyMasteryOfMagic$freezeBob(T entity, float partialTick, CallbackInfoReturnable<Float> cir) {
        float currentAgeInTicks = (float)((LivingEntity)entity).f_19797_ + partialTick;
        cir.setReturnValue((Object)Float.valueOf(TimeFreezeRenderAnimationState.getFrozenAgeInTicks(entity.m_19879_(), currentAgeInTicks)));
    }

    @Redirect(method={"m_7392_(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/model/EntityModel;m_6839_(Lnet/minecraft/world/entity/Entity;FFF)V", remap=false), remap=false)
    private void goetyMasteryOfMagic$freezePrepareMobModel(EntityModel<T> model, Entity entity, float limbSwing, float limbSwingAmount, float partialTick) {
        TimeFreezeRenderAnimationState.RenderState state = TimeFreezeRenderAnimationState.getPrepareState(entity.m_19879_(), limbSwing, limbSwingAmount, partialTick);
        model.m_6839_((Entity)((LivingEntity)entity), state.prepareLimbSwing, state.prepareLimbSwingAmount, state.preparePartialTick);
    }

    @Redirect(method={"m_7392_(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/model/EntityModel;m_6973_(Lnet/minecraft/world/entity/Entity;FFFFF)V", remap=false), remap=false)
    private void goetyMasteryOfMagic$freezeSetupAnim(EntityModel<T> model, Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        TimeFreezeRenderAnimationState.RenderState state = TimeFreezeRenderAnimationState.getSetupState(entity.m_19879_(), limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        model.m_6973_((Entity)((LivingEntity)entity), state.setupLimbSwing, state.setupLimbSwingAmount, state.ageInTicks, state.netHeadYaw, state.headPitch);
        TimeFreezeRenderAnimationState.captureOrApplyModelPose(entity.m_19879_(), model);
    }
}

