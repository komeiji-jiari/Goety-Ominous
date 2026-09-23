package com.qiuyue.goetyominous.client.sound;

import com.qiuyue.goetyominous.common.entities.ally.of.TerrorServant;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;

import java.util.Iterator;

public class TerrorServantSawSoundHandler {

    private static final Int2ObjectMap<TerrorServantSawSound> SOUND_INSTANCE_MAP = new Int2ObjectOpenHashMap<>();

    public static void playFor(TerrorServant terror) {
        int id = terror.getId();
        TerrorServantSawSound existing = SOUND_INSTANCE_MAP.get(id);
        if (existing == null || existing.isStopped() || !existing.isSameEntity(terror)) {
            TerrorServantSawSound sound = new TerrorServantSawSound(terror);
            SOUND_INSTANCE_MAP.put(id, sound);
            Minecraft.getInstance().getSoundManager().queueTickingSound(sound);
        }
    }

    public static void clearFor(TerrorServant terror) {
        TerrorServantSawSound removed = SOUND_INSTANCE_MAP.remove(terror.getId());
        if (removed != null) {
            removed.stopSound();
        }
        if (!SOUND_INSTANCE_MAP.isEmpty()) {
            Iterator<TerrorServantSawSound> it = SOUND_INSTANCE_MAP.values().iterator();
            while (it.hasNext()) {
                if (it.next().isStopped()) {
                    it.remove();
                }
            }
        }
    }
}
