package com.qiuyue.goetyominous.utils;

import net.minecraft.world.item.ItemStack;

public enum WolfArmorCrackiness {
    NONE, LOW, MEDIUM, HIGH;

    private static final float FRACTION_LOW = 0.95F;
    private static final float FRACTION_MEDIUM = 0.69F;
    private static final float FRACTION_HIGH = 0.32F;

    public static WolfArmorCrackiness byDamage(ItemStack stack) {
        if (stack.isEmpty()) return NONE;
        float fraction = (float) (stack.getMaxDamage() - stack.getDamageValue()) / (float) stack.getMaxDamage();
        if (fraction < FRACTION_HIGH) return HIGH;
        if (fraction < FRACTION_MEDIUM) return MEDIUM;
        if (fraction < FRACTION_LOW) return LOW;
        return NONE;
    }
}
