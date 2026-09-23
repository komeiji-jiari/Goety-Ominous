package com.qiuyue.goetyominous.client.sound;

import com.qiuyue.goetyominous.common.entities.ally.of.SkyvernServant;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;

import java.util.Iterator;

public class SkyvernServantLoopSoundHandler {

    private static final Int2ObjectMap<SkyvernServantLoopSound> SOUND_INSTANCE_MAP = new Int2ObjectOpenHashMap<>();

    public static void playFor(SkyvernServant skyvern) {
        int id = skyvern.getId();
        SkyvernServantLoopSound existing = SOUND_INSTANCE_MAP.get(id);
        if (existing != null && !existing.isStopped() && existing.isSameEntity(skyvern)) {
            return;
        }
        if (!skyvern.isSilent()) {
            SkyvernServantLoopSound sound = new SkyvernServantLoopSound(skyvern);
            SOUND_INSTANCE_MAP.put(id, sound);
            Minecraft.getInstance().getSoundManager().queueTickingSound(sound);
        }
    }

    public static void clearFor(SkyvernServant skyvern) {
        SkyvernServantLoopSound removed = SOUND_INSTANCE_MAP.remove(skyvern.getId());
        if (removed != null) {
            removed.stopSound();
        }
        if (!SOUND_INSTANCE_MAP.isEmpty()) {
            Iterator<SkyvernServantLoopSound> iterator = SOUND_INSTANCE_MAP.values().iterator();
            while (iterator.hasNext()) {
                if (iterator.next().isStopped()) {
                    iterator.remove();
                }
            }
        }
    }
}
