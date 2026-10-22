package com.qiuyue.goetyominous.common.entities.ally.pp;

import net.minecraft.world.item.ItemStack;

public interface AlchemistBeltHolder extends AlchemistAnimatable {

    int getBeltSize();

    ItemStack getBeltItem(int slot);

    ItemStack getItemShownOnOffhand();
}
