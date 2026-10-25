package com.qiuyue.goetyominous.common.explore;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface IExpeditionLootHolder {
    List<ItemStack> getExpeditionLoot();

    default boolean hasExpeditionLoot() {
        return !getExpeditionLoot().isEmpty();
    }

    void addExpeditionLoot(ItemStack stack);

    void clearExpeditionLoot();
}
