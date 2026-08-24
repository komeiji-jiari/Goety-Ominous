/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.dimension.DimensionType
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.vivideru.masteryofmagic.mixins;

import com.vivideru.masteryofmagic.client.midas.MidasEclipseClientState;
import net.minecraft.util.Mth;
import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={DimensionType.class})
public abstract class MidasEclipseTimeMixin {
    @Inject(method={"timeOfDay"}, at={@At(value="RETURN")}, cancellable=true)
    private void masteryOfMagic$fixMidasMidnight(long dayTime, CallbackInfoReturnable<Float> callback) {
        float intensity = MidasEclipseClientState.intensity(1.0f);
        if (intensity > 0.001f) {
            callback.setReturnValue((Object)Float.valueOf(Mth.m_14179_((float)intensity, (float)((Float)callback.getReturnValue()).floatValue(), (float)0.5f)));
        }
    }

    @Inject(method={"moonPhase"}, at={@At(value="RETURN")}, cancellable=true)
    private void masteryOfMagic$fixMidasNewMoon(long dayTime, CallbackInfoReturnable<Integer> callback) {
        if (MidasEclipseClientState.intensity(1.0f) > 0.001f) {
            callback.setReturnValue((Object)4);
        }
    }
}

