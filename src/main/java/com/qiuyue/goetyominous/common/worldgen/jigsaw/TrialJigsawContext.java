package com.qiuyue.goetyominous.common.worldgen.jigsaw;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Supplier;

public final class TrialJigsawContext {

    private static final ThreadLocal<int[]> DEPTH = ThreadLocal.withInitial(() -> new int[1]);
    private static final ThreadLocal<Deque<TrialDimensionPadding>> PADDING = ThreadLocal.withInitial(ArrayDeque::new);

    public static boolean prioritiesActive() {
        return DEPTH.get()[0] > 0;
    }

    public static TrialDimensionPadding padding() {
        TrialDimensionPadding padding = PADDING.get().peek();
        return padding == null ? TrialDimensionPadding.ZERO : padding;
    }

    public static <T> T withPriorities(Supplier<T> action) {
        DEPTH.get()[0]++;
        try {
            return action.get();
        } finally {
            DEPTH.get()[0]--;
        }
    }

    public static void run(TrialDimensionPadding padding, Runnable action) {
        DEPTH.get()[0]++;
        PADDING.get().push(padding);
        try {
            action.run();
        } finally {
            PADDING.get().pop();
            DEPTH.get()[0]--;
        }
    }

    private TrialJigsawContext() {
    }
}
