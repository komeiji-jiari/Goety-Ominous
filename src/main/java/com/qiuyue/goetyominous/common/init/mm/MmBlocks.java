package com.qiuyue.goetyominous.common.init.mm;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.blocks.mm.ServantQuicksandBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MmBlocks {

    private static final DeferredRegister<Block> MM_BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, GoetyOminous.MOD_ID);

    public static final RegistryObject<Block> SERVANT_QUICKSAND = MM_BLOCKS.register("servant_quicksand",
            () -> new ServantQuicksandBlock(BlockBehaviour.Properties.copy(Blocks.SAND)
                    .noCollission()
                    .strength(0.3F)
                    .noOcclusion()
                    .noLootTable()
                    .sound(SoundType.SAND)));

    public static final RegistryObject<Block> SERVANT_RED_QUICKSAND = MM_BLOCKS.register("servant_red_quicksand",
            () -> new ServantQuicksandBlock(BlockBehaviour.Properties.copy(Blocks.SAND)
                    .noCollission()
                    .strength(0.3F)
                    .noOcclusion()
                    .noLootTable()
                    .sound(SoundType.SAND)));

    public static void register(IEventBus modEventBus) {
        MM_BLOCKS.register(modEventBus);
    }
}
