/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.vivideru.masteryofmagic.mixins;

import com.vivideru.masteryofmagic.TimeFreezeRenderAnimationState;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Minecraft.class})
public abstract class TimeFreezeMinecraftPartialTickMixin {
    @Inject(method={"m_91296_()F"}, at={@At(value="RETURN")}, cancellable=true, remap=false)
    private void freezeFrame(CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue((Object)Float.valueOf(TimeFreezeRenderAnimationState.getFrozenPartialTick(cir.getReturnValueF())));
    }
}

