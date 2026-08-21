package com.qiuyue.goetyominous.client.sound;

import com.qiuyue.goetyominous.common.entities.ally.ac.NucleeperServant;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;

import java.util.Iterator;

public class NucleeperServantSoundHandler {

    private static final Int2ObjectMap<NucleeperServantSirenSound> SIREN_SOUND_INSTANCE_MAP = new Int2ObjectOpenHashMap<>();

    public static void startSirenFor(NucleeperServant nucleeper) {
        int id = nucleeper.getId();
        NucleeperServantSirenSound existing = SIREN_SOUND_INSTANCE_MAP.get(id);
        if (existing == null || existing.isStopped() || !existing.isSameEntity(nucleeper)) {
            NucleeperServantSirenSound sound = new NucleeperServantSirenSound(nucleeper);
            SIREN_SOUND_INSTANCE_MAP.put(id, sound);
            Minecraft.getInstance().getSoundManager().queueTickingSound(sound);
        }
    }

    public static void clearSoundFor(NucleeperServant nucleeper) {
        int id = nucleeper.getId();
        NucleeperServantSirenSound removed = SIREN_SOUND_INSTANCE_MAP.remove(id);
        if (removed != null) {
            removed.stopSound();
        }
        if (!SIREN_SOUND_INSTANCE_MAP.isEmpty()) {
            Iterator<NucleeperServantSirenSound> it = SIREN_SOUND_INSTANCE_MAP.values().iterator();
            while (it.hasNext()) {
                if (it.next().isStopped()) {
                    it.remove();
                }
            }
        }
    }
}
