package com.qiuyue.goetyominous.common.init;

import com.Polarice3.Goety.common.blocks.TrainingBlock;
import com.Polarice3.Goety.common.items.revive.ReviveServantItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;

public interface CompatItemSource {

    DeferredRegister<Item> registry();

    default boolean isRevive(Item item) {
        return item instanceof ReviveServantItem;
    }

    default boolean reviveLast(Item item) {
        return false;
    }

    default boolean isSummon(Block block) {
        return block instanceof TrainingBlock;
    }

    default boolean isDinosaurEgg(Block block) {
        return false;
    }

    static CompatItemSource of(DeferredRegister<Item> registry) {
        return () -> registry;
    }
}
