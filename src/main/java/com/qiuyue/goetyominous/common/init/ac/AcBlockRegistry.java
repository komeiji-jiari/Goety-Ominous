package com.qiuyue.goetyominous.common.init.ac;

import com.github.alexmodguy.alexscaves.server.block.ACSoundTypes;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.blocks.ac.*;
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

    public static final RegistryObject<Block> MINE_GUARDIAN_BLOCK =
            AC_BLOCKS.register("mine_guardian_block",
                    () -> new MineGuardianBlock(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .requiresCorrectToolForDrops()
                            .strength(5.0F, 15.0F)
                            .sound(ACSoundTypes.SCRAP_METAL)
                            .noOcclusion()));

    public static final RegistryObject<Block> ATLATITAN_SERVANT_EGG =
            AC_BLOCKS.register("atlatitan_servant_egg",
                    () -> new AtlatitanServantEggBlock(
                            BlockBehaviour.Properties.of()
                                    .mapColor(MapColor.TERRACOTTA_WHITE)
                                    .strength(0.5F)
                                    .sound(SoundType.METAL)
                                    .randomTicks()));

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

    public static final RegistryObject<Block> RELICHEIRUS_SERVANT_EGG =
            AC_BLOCKS.register("relicheirus_servant_egg",
                    () -> new RelicheirusServantEggBlock(
                            BlockBehaviour.Properties.of()
                                    .mapColor(MapColor.TERRACOTTA_WHITE)
                                    .strength(0.5F)
                                    .sound(SoundType.METAL)
                                    .randomTicks()));

    public static final RegistryObject<Block> VALLUMRAPTOR_SERVANT_EGG =
            AC_BLOCKS.register("vallumraptor_servant_egg",
                    () -> new VallumraptorServantEggBlock(
                            BlockBehaviour.Properties.of()
                                    .mapColor(MapColor.TERRACOTTA_WHITE)
                                    .strength(0.5F)
                                    .sound(SoundType.METAL)
                                    .randomTicks()));

    public static final RegistryObject<Block> ANNIHILATION_BOMB =
            AC_BLOCKS.register("annihilation_core",
                    () -> new AnnihilationBombBlock(
                            BlockBehaviour.Properties.of()
                                    .mapColor(MapColor.METAL)
                                    .strength(8.0F, 1001.0F)
                                    .sound(SoundType.METAL)
                                    .noOcclusion()));

    public static final RegistryObject<Block> GAMMAROACH_NEST =
            AC_BLOCKS.register("gammaroach_nest", GammaroachNestBlock::new);

    public static void register(IEventBus modEventBus) {
        AC_BLOCKS.register(modEventBus);
    }
}
