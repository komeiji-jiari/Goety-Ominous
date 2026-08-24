/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 *  net.minecraft.client.renderer.LightTexture
 *  net.minecraft.util.Mth
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.vivideru.masteryofmagic.mixins;

import com.mojang.blaze3d.platform.NativeImage;
import com.vivideru.masteryofmagic.client.midas.MidasEclipseClientState;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={LightTexture.class})
public abstract class MidasEclipseLightTextureMixin {
    @Shadow
    @Final
    private NativeImage f_109871_;

    @Inject(method={"updateLightTexture"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/texture/DynamicTexture;upload()V")})
    private void masteryOfMagic$tintMidasLight(float partialTick, CallbackInfo callback) {
        float intensity = MidasEclipseClientState.intensity(partialTick);
        if (intensity <= 0.001f) {
            return;
        }
        float pulse = MidasEclipseClientState.pulse(partialTick);
        float blend = intensity * Mth.m_14179_((float)pulse, (float)0.43f, (float)0.54f);
        for (int y = 0; y < 16; ++y) {
            for (int x = 0; x < 16; ++x) {
                int packed = this.f_109871_.m_84985_(x, y);
                int red = packed & 0xFF;
                int green = packed >>> 8 & 0xFF;
                int blue = packed >>> 16 & 0xFF;
                int alpha = packed >>> 24 & 0xFF;
                float luminance = ((float)red * 0.299f + (float)green * 0.587f + (float)blue * 0.114f) / 255.0f;
                int philosopherRed = Math.min(255, Math.round(luminance * 342.0f));
                int philosopherGreen = Math.min(255, Math.round(luminance * 55.0f));
                int philosopherBlue = Math.min(255, Math.round(luminance * 286.0f));
                int tintedRed = Math.round(Mth.m_14179_((float)blend, (float)red, (float)philosopherRed));
                int tintedGreen = Math.round(Mth.m_14179_((float)blend, (float)green, (float)philosopherGreen));
                int tintedBlue = Math.round(Mth.m_14179_((float)blend, (float)blue, (float)philosopherBlue));
                this.f_109871_.m_84988_(x, y, alpha << 24 | tintedBlue << 16 | tintedGreen << 8 | tintedRed);
            }
        }
    }
}

