package com.qiuyue.goetyominous.compat.ac;

import com.qiuyue.goetyominous.common.blocks.ac.MineGuardianBlock;
import com.qiuyue.goetyominous.common.blocks.ac.ServantEggBlock;
import com.qiuyue.goetyominous.common.init.CompatItemSource;
import com.qiuyue.goetyominous.common.items.ac.AcItems;
import com.qiuyue.goetyominous.common.items.ac.RaycatAmuletItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;

public class AcCompatItems implements CompatItemSource {

    @Override
    public DeferredRegister<Item> registry() {
        return AcItems.AC_ITEMS;
    }

    @Override
    public boolean isRevive(Item item) {
        return CompatItemSource.super.isRevive(item) || item instanceof RaycatAmuletItem;
    }

    @Override
    public boolean reviveLast(Item item) {
        return item instanceof RaycatAmuletItem;
    }

    @Override
    public boolean isSummon(Block block) {
        return CompatItemSource.super.isSummon(block) || block instanceof MineGuardianBlock;
    }

    @Override
    public boolean isDinosaurEgg(Block block) {
        return block instanceof ServantEggBlock;
    }
}
