package com.qiuyue.goetyominous.client.sound;

import com.qiuyue.goetyominous.common.entities.projectile.DicerServantLaser;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;

import java.util.Iterator;

public class DicerServantLaserSoundHandler {

    private static final Int2ObjectMap<DicerServantLaserSound> SOUND_INSTANCE_MAP = new Int2ObjectOpenHashMap<>();

    public static void playFor(DicerServantLaser laser) {
        int id = laser.getId();
        DicerServantLaserSound existing = SOUND_INSTANCE_MAP.get(id);
        if (existing == null || existing.isStopped() || !existing.isSameEntity(laser)) {
            DicerServantLaserSound sound = new DicerServantLaserSound(laser);
            SOUND_INSTANCE_MAP.put(id, sound);
            Minecraft.getInstance().getSoundManager().queueTickingSound(sound);
        }
    }

    public static void clearFor(DicerServantLaser laser) {
        DicerServantLaserSound removed = SOUND_INSTANCE_MAP.remove(laser.getId());
        if (removed != null) {
            removed.stopSound();
        }
        if (!SOUND_INSTANCE_MAP.isEmpty()) {
            Iterator<DicerServantLaserSound> it = SOUND_INSTANCE_MAP.values().iterator();
            while (it.hasNext()) {
                if (it.next().isStopped()) {
                    it.remove();
                }
            }
        }
    }
}
