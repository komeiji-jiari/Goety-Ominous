package com.qiuyue.goetyominous.common.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class BrokenShamanTotemItem extends Item {
    public BrokenShamanTotemItem() {
        super(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1));
    }
}
