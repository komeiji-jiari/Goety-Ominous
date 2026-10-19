package com.qiuyue.goetyominous.compat.mm;

import com.alexander.mutantmore.events.ShakeCameraEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import com.qiuyue.goetyominous.config.mm.MmConfig;

public class MmScreenShake {

    public static void shake(Level level, int duration, float amount, BlockPos pos, int range) {
        if (!MmConfig.MutantMoreServantScreenShake.get()) {
            return;
        }
        ShakeCameraEvent.shake(level, duration, amount, pos, range);
    }
}
