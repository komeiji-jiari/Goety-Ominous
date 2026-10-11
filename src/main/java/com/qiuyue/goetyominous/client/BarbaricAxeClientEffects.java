package com.qiuyue.goetyominous.client;

import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.Util;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, value = Dist.CLIENT)
public class BarbaricAxeClientEffects {

    private static final float YAW_SCALE = 1.0F;
    private static final float PITCH_SCALE = 0.75F;
    private static final float ROLL_SCALE = 0.5F;

    private static long shakeStart;
    private static long shakeEnd;
    private static float shakeStrength;

    public static void play(float strength, int ticks) {
        long now = Util.getMillis();
        long end = now + (long) ticks * 50L;
        if (end <= shakeEnd && strength <= shakeStrength) {
            return;
        }
        shakeStart = now;
        shakeEnd = end;
        shakeStrength = strength;
    }

    @SubscribeEvent
    public static void onComputeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        long now = Util.getMillis();
        if (now >= shakeEnd) {
            return;
        }
        float fade = 1.0F - (float) (now - shakeStart) / (float) Math.max(1L, shakeEnd - shakeStart);
        float amplitude = shakeStrength * Mth.clamp(fade, 0.0F, 1.0F) * Mth.clamp(fade, 0.0F, 1.0F);
        if (amplitude <= 0.0F) {
            return;
        }
        float phase = (float) (now % 100000L) * 0.06F + (float) event.getPartialTick();
        event.setYaw(event.getYaw() + Mth.sin(phase * 2.3F) * amplitude * YAW_SCALE);
        event.setPitch(event.getPitch() + Mth.cos(phase * 2.9F) * amplitude * PITCH_SCALE);
        event.setRoll(event.getRoll() + Mth.sin(phase * 1.7F) * amplitude * ROLL_SCALE);
    }
}
