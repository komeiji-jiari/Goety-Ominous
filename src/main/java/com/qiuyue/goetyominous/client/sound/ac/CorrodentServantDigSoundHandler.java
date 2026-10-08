package com.qiuyue.goetyominous.client.sound.ac;

import com.qiuyue.goetyominous.common.entities.ally.ac.CorrodentServant;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;

import java.util.Iterator;

public class CorrodentServantDigSoundHandler {

    private static final Int2ObjectMap<CorrodentServantDigSound> SOUND_INSTANCE_MAP = new Int2ObjectOpenHashMap<>();

    public static void startDigFor(CorrodentServant servant) {
        int id = servant.getId();
        CorrodentServantDigSound existing = SOUND_INSTANCE_MAP.get(id);
        if (existing == null || existing.isStopped() || !existing.isSameEntity(servant)) {
            CorrodentServantDigSound sound = new CorrodentServantDigSound(servant);
            SOUND_INSTANCE_MAP.put(id, sound);
            Minecraft.getInstance().getSoundManager().queueTickingSound(sound);
        }
    }

    public static void clearDigFor(CorrodentServant servant) {
        int id = servant.getId();
        CorrodentServantDigSound removed = SOUND_INSTANCE_MAP.remove(id);
        if (removed != null) {
            removed.stopSound();
        }
        if (!SOUND_INSTANCE_MAP.isEmpty()) {
            Iterator<CorrodentServantDigSound> it = SOUND_INSTANCE_MAP.values().iterator();
            while (it.hasNext()) {
                if (it.next().isStopped()) {
                    it.remove();
                }
            }
        }
    }
}
