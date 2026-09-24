package com.qiuyue.goetyominous.client.sound;

import com.qiuyue.goetyominous.common.entities.projectile.VoltServantElectricCharge;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;

import java.util.Iterator;

public class VoltServantElectricChargeSoundHandler {

    private static final Int2ObjectMap<VoltServantElectricChargeSound> SOUND_INSTANCE_MAP = new Int2ObjectOpenHashMap<>();

    public static void playFor(VoltServantElectricCharge charge) {
        int id = charge.getId();
        VoltServantElectricChargeSound existing = SOUND_INSTANCE_MAP.get(id);
        if (existing == null || existing.isStopped() || !existing.isSameEntity(charge)) {
            VoltServantElectricChargeSound sound = new VoltServantElectricChargeSound(charge);
            SOUND_INSTANCE_MAP.put(id, sound);
            Minecraft.getInstance().getSoundManager().queueTickingSound(sound);
        }
    }

    public static void clearFor(VoltServantElectricCharge charge) {
        VoltServantElectricChargeSound removed = SOUND_INSTANCE_MAP.remove(charge.getId());
        if (removed != null) {
            removed.stopSound();
        }
        if (!SOUND_INSTANCE_MAP.isEmpty()) {
            Iterator<VoltServantElectricChargeSound> it = SOUND_INSTANCE_MAP.values().iterator();
            while (it.hasNext()) {
                if (it.next().isStopped()) {
                    it.remove();
                }
            }
        }
    }
}
