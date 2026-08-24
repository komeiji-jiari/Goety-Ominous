/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.resources.sounds.SimpleSoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance$Attenuation
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.ClientPlayerNetworkEvent$LoggingOut
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package com.vivideru.masteryofmagic;

import com.vivideru.masteryofmagic.entity.GhiaccioEntity;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="goety_mastery_of_magic", value={Dist.CLIENT}, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class GhiaccioBossMusicHandler {
    private static SoundInstance currentMusic;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft minecraft = Minecraft.m_91087_();
        if (minecraft.f_91073_ == null || minecraft.f_91074_ == null) {
            GhiaccioBossMusicHandler.stopMusic(minecraft);
            return;
        }
        boolean shouldPlay = false;
        for (Entity entity : minecraft.f_91073_.m_104735_()) {
            GhiaccioEntity ghiaccio;
            if (!(entity instanceof GhiaccioEntity) || !(ghiaccio = (GhiaccioEntity)entity).m_6084_()) continue;
            shouldPlay = true;
            break;
        }
        if (shouldPlay) {
            minecraft.m_91397_().m_120186_();
            if (currentMusic == null || !minecraft.m_91106_().m_120403_(currentMusic)) {
                currentMusic = new SimpleSoundInstance(((SoundEvent)GoetyMasteryOfMagicModSounds.THEME_OF_GHIACCIO.get()).m_11660_(), SoundSource.RECORDS, 1.0f, 1.0f, SoundInstance.m_235150_(), false, 0, SoundInstance.Attenuation.NONE, 0.0, 0.0, 0.0, true);
                minecraft.m_91106_().m_120367_(currentMusic);
            }
        } else {
            GhiaccioBossMusicHandler.stopMusic(minecraft);
        }
    }

    @SubscribeEvent
    public static void onClientDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
        GhiaccioBossMusicHandler.stopMusic(Minecraft.m_91087_());
    }

    private static void stopMusic(Minecraft minecraft) {
        if (currentMusic != null) {
            minecraft.m_91106_().m_120399_(currentMusic);
            currentMusic = null;
        }
    }
}

