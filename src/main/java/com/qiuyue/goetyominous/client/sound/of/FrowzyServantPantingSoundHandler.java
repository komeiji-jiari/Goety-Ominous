package com.qiuyue.goetyominous.client.sound.of;

import com.qiuyue.goetyominous.common.entities.ally.of.FrowzyServant;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;

import java.util.Iterator;

public class FrowzyServantPantingSoundHandler {

    private static final Int2ObjectMap<FrowzyServantPantingSound> SOUND_INSTANCE_MAP = new Int2ObjectOpenHashMap<>();

    public static void playFor(FrowzyServant frowzy) {
        int id = frowzy.getId();
        FrowzyServantPantingSound existing = SOUND_INSTANCE_MAP.get(id);
        if (existing == null || existing.isStopped() || !existing.isSameEntity(frowzy)) {
            FrowzyServantPantingSound sound = new FrowzyServantPantingSound(frowzy);
            SOUND_INSTANCE_MAP.put(id, sound);
            Minecraft.getInstance().getSoundManager().queueTickingSound(sound);
        }
    }

    public static void clearFor(FrowzyServant frowzy) {
        FrowzyServantPantingSound removed = SOUND_INSTANCE_MAP.remove(frowzy.getId());
        if (removed != null) {
            removed.stopSound();
        }
        if (!SOUND_INSTANCE_MAP.isEmpty()) {
            Iterator<FrowzyServantPantingSound> it = SOUND_INSTANCE_MAP.values().iterator();
            while (it.hasNext()) {
                if (it.next().isStopped()) {
                    it.remove();
                }
            }
        }
    }
}
