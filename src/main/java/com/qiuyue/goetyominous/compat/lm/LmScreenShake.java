package com.qiuyue.goetyominous.compat.lm;

import com.qiuyue.goetyominous.config.MobsConfig;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Effect.CameraShakeEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class LmScreenShake {

    public static void shake(Level level, Vec3 pos, float radius, float strength, int duration, int fadeDuration) {
        if (level.isClientSide || !MobsConfig.LmServantScreenShake.get()) {
            return;
        }
        level.addFreshEntity(new CameraShakeEntity(level, pos, radius, strength, duration, fadeDuration));
    }
}
