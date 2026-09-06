package com.qiuyue.goetyominous.client.sound;

import com.qiuyue.goetyominous.common.entities.ally.ac.CandicornServant;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;

import java.util.Iterator;

/**
 * 糖果独角兽仆从冲刺循环音管理:按实体 id 缓存 CandicornServantChargeSound,
 * 只在未播放时排队;实体移除时 clear 停止并清理已停止项。接线方式同 NucleeperServantSoundHandler。
 */
public class CandicornServantChargeSoundHandler {

    private static final Int2ObjectMap<CandicornServantChargeSound> SOUND_INSTANCE_MAP = new Int2ObjectOpenHashMap<>();

    public static void startChargeFor(CandicornServant servant) {
        int id = servant.getId();
        CandicornServantChargeSound existing = SOUND_INSTANCE_MAP.get(id);
        if (existing == null || existing.isStopped() || !existing.isSameEntity(servant)) {
            CandicornServantChargeSound sound = new CandicornServantChargeSound(servant);
            SOUND_INSTANCE_MAP.put(id, sound);
            Minecraft.getInstance().getSoundManager().queueTickingSound(sound);
        }
    }

    public static void clearChargeFor(CandicornServant servant) {
        int id = servant.getId();
        CandicornServantChargeSound removed = SOUND_INSTANCE_MAP.remove(id);
        if (removed != null) {
            removed.stopSound();
        }
        if (!SOUND_INSTANCE_MAP.isEmpty()) {
            Iterator<CandicornServantChargeSound> it = SOUND_INSTANCE_MAP.values().iterator();
            while (it.hasNext()) {
                if (it.next().isStopped()) {
                    it.remove();
                }
            }
        }
    }
}
