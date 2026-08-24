/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemStack
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.vivideru.masteryofmagic.mixins;

import com.vivideru.masteryofmagic.TimeFreezeRenderAnimationState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={LivingEntity.class})
public abstract class TimeFreezeLivingEntityUseItemMixin {
    @Inject(method={"m_6117_()Z"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private void freezeIsUsingItem(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity)this;
        if (TimeFreezeRenderAnimationState.isAnimationFrozen(entity.m_19879_())) {
            cir.setReturnValue((Object)TimeFreezeRenderAnimationState.getState((int)entity.m_19879_()).usingItem);
        }
    }

    @Inject(method={"m_21212_()I"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private void freezeRemaining(CallbackInfoReturnable<Integer> cir) {
        LivingEntity entity = (LivingEntity)this;
        if (TimeFreezeRenderAnimationState.isAnimationFrozen(entity.m_19879_())) {
            cir.setReturnValue((Object)TimeFreezeRenderAnimationState.getState((int)entity.m_19879_()).useItemRemainingTicks);
        }
    }

    @Inject(method={"m_21252_()I"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private void freezeTicks(CallbackInfoReturnable<Integer> cir) {
        LivingEntity entity = (LivingEntity)this;
        if (TimeFreezeRenderAnimationState.isAnimationFrozen(entity.m_19879_())) {
            cir.setReturnValue((Object)TimeFreezeRenderAnimationState.getState((int)entity.m_19879_()).ticksUsingItem);
        }
    }

    @Inject(method={"m_21211_()Lnet/minecraft/world/item/ItemStack;"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private void freezeUseItem(CallbackInfoReturnable<ItemStack> cir) {
        LivingEntity entity = (LivingEntity)this;
        if (TimeFreezeRenderAnimationState.isAnimationFrozen(entity.m_19879_())) {
            cir.setReturnValue((Object)TimeFreezeRenderAnimationState.getState((int)entity.m_19879_()).useItem);
        }
    }

    @Inject(method={"m_21324_(F)F"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private void freezeAttack(float partialTick, CallbackInfoReturnable<Float> cir) {
        LivingEntity entity = (LivingEntity)this;
        if (TimeFreezeRenderAnimationState.isAnimationFrozen(entity.m_19879_())) {
            TimeFreezeRenderAnimationState.RenderState s = TimeFreezeRenderAnimationState.getState(entity.m_19879_());
            cir.setReturnValue((Object)Float.valueOf(s.oAttackAnim + (s.attackAnim - s.oAttackAnim) * partialTick));
        }
    }
}

