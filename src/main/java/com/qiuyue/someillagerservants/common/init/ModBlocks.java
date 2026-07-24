package com.qiuyue.someillagerservants.common.init;

import com.qiuyue.someillagerservants.SomeIllagerServants;
import com.qiuyue.someillagerservants.common.blocks.PiglinMerchantSpawnerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, SomeIllagerServants.MOD_ID);

    public static final RegistryObject<Block> PIGLIN_MERCHANT_SPAWNER =
            BLOCKS.register("piglin_merchant_spawner", PiglinMerchantSpawnerBlock::new);

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}