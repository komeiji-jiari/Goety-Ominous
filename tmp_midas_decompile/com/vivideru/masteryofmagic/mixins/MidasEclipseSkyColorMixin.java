/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.util.Mth
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.vivideru.masteryofmagic.mixins;

import com.vivideru.masteryofmagic.client.midas.MidasEclipseClientState;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ClientLevel.class})
public abstract class MidasEclipseSkyColorMixin {
    @Inject(method={"getSkyDarken"}, at={@At(value="RETURN")}, cancellable=true)
    private void masteryOfMagic$keepMidasWorldBright(float partialTick, CallbackInfoReturnable<Float> callback) {
        float intensity = MidasEclipseClientState.intensity(partialTick);
        if (intensity > 0.001f) {
            callback.setReturnValue((Object)Float.valueOf(Mth.m_14179_((float)intensity, (float)((Float)callback.getReturnValue()).floatValue(), (float)1.0f)));
        }
    }

    @Inject(method={"getSkyColor"}, at={@At(value="RETURN")}, cancellable=true)
    private void masteryOfMagic$colorMidasSky(Vec3 cameraPosition, float partialTick, CallbackInfoReturnable<Vec3> callback) {
        float intensity = MidasEclipseClientState.intensity(partialTick);
        if (intensity <= 0.001f) {
            return;
        }
        float pulse = MidasEclipseClientState.pulse(partialTick);
        Vec3 original = (Vec3)callback.getReturnValue();
        Vec3 philosopherSky = new Vec3((double)Mth.m_14179_((float)pulse, (float)0.085f, (float)0.155f), (double)Mth.m_14179_((float)pulse, (float)0.002f, (float)0.008f), (double)Mth.m_14179_((float)pulse, (float)0.125f, (float)0.225f));
        double blend = (double)intensity * 0.92;
        callback.setReturnValue((Object)original.m_82490_(1.0 - blend).m_82549_(philosopherSky.m_82490_(blend)));
    }

    @Inject(method={"getCloudColor"}, at={@At(value="RETURN")}, cancellable=true)
    private void masteryOfMagic$colorMidasClouds(float partialTick, CallbackInfoReturnable<Vec3> callback) {
        float intensity = MidasEclipseClientState.intensity(partialTick);
        if (intensity <= 0.001f) {
            return;
        }
        float pulse = MidasEclipseClientState.pulse(partialTick);
        Vec3 original = (Vec3)callback.getReturnValue();
        double originalLight = Mth.m_14008_((double)(original.f_82479_ * 0.299 + original.f_82480_ * 0.587 + original.f_82481_ * 0.114), (double)0.08, (double)1.0);
        Vec3 philosopherClouds = new Vec3((double)Mth.m_14179_((float)pulse, (float)0.37f, (float)0.515f), (double)Mth.m_14179_((float)pulse, (float)0.018f, (float)0.038f), (double)Mth.m_14179_((float)pulse, (float)0.37f, (float)0.555f)).m_82490_(0.72 + originalLight * 0.38);
        double blend = (double)intensity * 0.88;
        callback.setReturnValue((Object)original.m_82490_(1.0 - blend).m_82549_(philosopherClouds.m_82490_(blend)));
    }
}

