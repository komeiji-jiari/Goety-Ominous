/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.renderer.entity.LightningBoltRenderer
 *  net.minecraft.util.Mth
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package com.vivideru.masteryofmagic.mixins;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.vivideru.masteryofmagic.client.midas.MidasEclipseClientState;
import net.minecraft.client.renderer.entity.LightningBoltRenderer;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={LightningBoltRenderer.class})
public abstract class MidasEclipseLightningRendererMixin {
    @Redirect(method={"quad"}, at=@At(value="INVOKE", target="Lcom/mojang/blaze3d/vertex/VertexConsumer;color(FFFF)Lcom/mojang/blaze3d/vertex/VertexConsumer;"))
    private static VertexConsumer masteryOfMagic$colorMidasLightning(VertexConsumer consumer, float red, float green, float blue, float alpha) {
        float intensity = MidasEclipseClientState.intensity(1.0f);
        if (intensity <= 0.001f) {
            return consumer.m_85950_(red, green, blue, alpha);
        }
        float pulse = MidasEclipseClientState.pulse(1.0f);
        return consumer.m_85950_(Mth.m_14179_((float)intensity, (float)red, (float)1.0f), Mth.m_14179_((float)intensity, (float)green, (float)Mth.m_14179_((float)pulse, (float)0.025f, (float)0.12f)), Mth.m_14179_((float)intensity, (float)blue, (float)Mth.m_14179_((float)pulse, (float)0.78f, (float)1.0f)), Mth.m_14179_((float)intensity, (float)alpha, (float)0.48f));
    }
}

