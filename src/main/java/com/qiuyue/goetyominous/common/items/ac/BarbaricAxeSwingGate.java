package com.qiuyue.goetyominous.common.items.ac;

public final class BarbaricAxeSwingGate {

    private static boolean blockSwing;

    private BarbaricAxeSwingGate() {
    }

    public static void setBlockSwing(boolean value) {
        blockSwing = value;
    }

    public static boolean isBlockSwing() {
        return blockSwing;
    }
}
