package com.qiuyue.goetyominous.common.init.ac;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.blocks.ac.GrottoceratopsServantEggBlock;
import com.qiuyue.goetyominous.common.blocks.ac.TremorsaurusServantEggBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class AcBlockRegistry {

    private static final DeferredRegister<Block> AC_BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, GoetyOminous.MOD_ID);


    public static final RegistryObject<Block> GROTTOCERATOPS_SERVANT_EGG =
            AC_BLOCKS.register("grottoceratops_servant_egg",
                    () -> new GrottoceratopsServantEggBlock(
                            BlockBehaviour.Properties.of()
                                    .mapColor(MapColor.TERRACOTTA_WHITE)
                                    .strength(0.5F)
                                    .sound(SoundType.METAL)
                                    .randomTicks()));

    public static final RegistryObject<Block> TREMORSAURUS_SERVANT_EGG =
            AC_BLOCKS.register("tremorsaurus_servant_egg",
                    () -> new TremorsaurusServantEggBlock(
                            BlockBehaviour.Properties.of()
                                    .mapColor(MapColor.TERRACOTTA_WHITE)
                                    .strength(0.5F)
                                    .sound(SoundType.METAL)
                                    .randomTicks()));

    public static void register(IEventBus modEventBus) {
        AC_BLOCKS.register(modEventBus);
    }
}
