package com.qiuyue.goetyominous.common.blocks.trial;

import net.minecraft.core.BlockPos;

public final class RaidStartContext {

    private static final ThreadLocal<BlockPos> ORIGIN = new ThreadLocal<>();

    private RaidStartContext() {
    }

    public static void run(BlockPos origin, Runnable action) {
        BlockPos previous = ORIGIN.get();
        ORIGIN.set(origin);
        try {
            action.run();
        } finally {
            if (previous == null) {
                ORIGIN.remove();
            } else {
                ORIGIN.set(previous);
            }
        }
    }

    public static BlockPos current() {
        return ORIGIN.get();
    }
}
