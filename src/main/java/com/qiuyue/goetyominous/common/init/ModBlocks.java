package com.qiuyue.goetyominous.common.init;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.blocks.PiglinMerchantSpawnerBlock;
import com.qiuyue.goetyominous.common.blocks.WolfTotemBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, GoetyOminous.MOD_ID);

    public static final RegistryObject<Block> PIGLIN_MERCHANT_SPAWNER =
            BLOCKS.register("piglin_merchant_spawner", PiglinMerchantSpawnerBlock::new);

    public static final RegistryObject<WolfTotemBlock> WOLF_TOTEM =
            BLOCKS.register("wolf_totem", WolfTotemBlock::new);

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}