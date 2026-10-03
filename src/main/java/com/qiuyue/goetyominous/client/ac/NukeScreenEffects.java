package com.qiuyue.goetyominous.client.ac;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.ClientProxy;
import com.github.alexmodguy.alexscaves.client.sound.NuclearExplosionSound;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;

public class NukeScreenEffects {

    public static void play(double x, double y, double z, float size) {
        float scale = size + 0.2F;
        int lifetime = (int) Math.ceil(133.33F * scale);
        ClientProxy.renderNukeFlashFor = 16;
        ClientProxy.muteNonNukeSoundsFor = 50;
        if (AlexsCaves.CLIENT_CONFIG.nuclearBombFlash.get()) {
            playSound(ACSoundRegistry.NUCLEAR_EXPLOSION_RINGING.get(), x, y, z, 100, 50, 0.05F, true);
        }
        playSound((scale > 2.0F ? ACSoundRegistry.LARGE_NUCLEAR_EXPLOSION : ACSoundRegistry.NUCLEAR_EXPLOSION).get(),
                x, y, z, lifetime - 20, lifetime, 0.2F, false);
        playSound(ACSoundRegistry.NUCLEAR_EXPLOSION_RUMBLE.get(), x, y, z, lifetime + 100, lifetime, 0.1F, true);
    }

    private static void playSound(SoundEvent sound, double x, double y, double z,
                                  int duration, int fadesAt, float fadeInBy, boolean looping) {
        Minecraft.getInstance().getSoundManager()
                .play(new NuclearExplosionSound(sound, x, y, z, duration, fadesAt, fadeInBy, looping));
    }
}
