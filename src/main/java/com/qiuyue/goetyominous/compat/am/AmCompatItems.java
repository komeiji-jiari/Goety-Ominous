package com.qiuyue.goetyominous.compat.am;

import com.qiuyue.goetyominous.common.init.CompatItemSource;
import com.qiuyue.goetyominous.common.items.am.AmItems;
import com.qiuyue.goetyominous.common.items.am.WarpedSteroidsItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;

public class AmCompatItems implements CompatItemSource {

    @Override
    public DeferredRegister<Item> registry() {
        return AmItems.AM_ITEMS;
    }

    @Override
    public boolean isRevive(Item item) {
        return CompatItemSource.super.isRevive(item) || item instanceof WarpedSteroidsItem;
    }
}
