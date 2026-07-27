package com.qiuyue.goetyominus.common.items.revive;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class MysteriousContract extends Item {
    public MysteriousContract() {
        super(new Properties()
                .rarity(Rarity.UNCOMMON)
                .stacksTo(1));
    }
}
