/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 */
package com.vivideru.masteryofmagic.client.midas;

import com.vivideru.masteryofmagic.entity.PhilosopherKingMidasEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public final class MidasEclipseClientState {
    private static final float FADE_PER_TICK = 0.035f;
    private static float previousIntensity;
    private static float intensity;

    private MidasEclipseClientState() {
    }

    public static void clientTick(Minecraft minecraft) {
        previousIntensity = intensity;
        float target = MidasEclipseClientState.isMidasFightTracked(minecraft) ? 1.0f : 0.0f;
        intensity = Mth.m_14036_((float)(intensity + Mth.m_14036_((float)(target - intensity), (float)-0.035f, (float)0.035f)), (float)0.0f, (float)1.0f);
        if (minecraft.f_91073_ == null) {
            previousIntensity = 0.0f;
            intensity = 0.0f;
        }
    }

    private static boolean isMidasFightTracked(Minecraft minecraft) {
        if (minecraft.f_91073_ == null || minecraft.f_91074_ == null) {
            return false;
        }
        for (Entity entity : minecraft.f_91073_.m_104735_()) {
            PhilosopherKingMidasEntity midas;
            if (!(entity instanceof PhilosopherKingMidasEntity) || !(midas = (PhilosopherKingMidasEntity)entity).m_6084_() || midas.m_213877_()) continue;
            return true;
        }
        return false;
    }

    public static float intensity(float partialTick) {
        return Mth.m_14179_((float)partialTick, (float)previousIntensity, (float)intensity);
    }

    public static float pulse(float partialTick) {
        Minecraft minecraft = Minecraft.m_91087_();
        float time = minecraft.f_91073_ == null ? 0.0f : (float)minecraft.f_91073_.m_46467_() + partialTick;
        return 0.5f + 0.5f * Mth.m_14031_((float)(time * 0.075f));
    }
}

