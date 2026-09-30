package com.qiuyue.goetyominous.client.sound;

import com.qiuyue.goetyominous.common.events.TremorzillaBreathHandler;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class TremorzillaBreathLoopSoundHandler {

    private static final Int2ObjectMap<TremorzillaBreathLoopSound> SOUND_INSTANCE_MAP = new Int2ObjectOpenHashMap<>();

    public static void playFor(int casterId) {
        TremorzillaBreathLoopSound existing = SOUND_INSTANCE_MAP.get(casterId);
        if (existing != null && !existing.isStopped()) {
            return;
        }
        if (Minecraft.getInstance().level == null) {
            return;
        }
        Entity caster = Minecraft.getInstance().level.getEntity(casterId);
        if (caster == null) {
            return;
        }
        TremorzillaBreathLoopSound sound = new TremorzillaBreathLoopSound(casterId, caster);
        SOUND_INSTANCE_MAP.put(casterId, sound);
        Minecraft.getInstance().getSoundManager().queueTickingSound(sound);
    }

    public static void retainOnly(List<TremorzillaBreathHandler.ClientBeam> beams) {
        if (SOUND_INSTANCE_MAP.isEmpty()) {
            return;
        }
        Set<Integer> alive = new HashSet<>();
        for (TremorzillaBreathHandler.ClientBeam beam : beams) {
            alive.add(beam.casterId());
        }
        Iterator<Int2ObjectMap.Entry<TremorzillaBreathLoopSound>> iterator = SOUND_INSTANCE_MAP.int2ObjectEntrySet().iterator();
        while (iterator.hasNext()) {
            Int2ObjectMap.Entry<TremorzillaBreathLoopSound> entry = iterator.next();
            if (!alive.contains(entry.getIntKey()) || entry.getValue().isStopped()) {
                entry.getValue().stopSound();
                iterator.remove();
            }
        }
    }
}
