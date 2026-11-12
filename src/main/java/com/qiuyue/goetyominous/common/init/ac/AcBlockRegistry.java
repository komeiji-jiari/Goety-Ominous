package com.qiuyue.goetyominous.common.init.ac;

import com.github.alexmodguy.alexscaves.server.block.ACSoundTypes;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.blocks.ac.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
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

    public static final RegistryObject<Block> TITAN_BOMB =
            AC_BLOCKS.register("titan_bomb",
                    () -> new TitanBombBlock(
                            BlockBehaviour.Properties.of()
                                    .mapColor(MapColor.METAL)
                                    .strength(8.0F, 1001.0F)
                                    .sound(SoundType.METAL)
                                    .noOcclusion()));

    public static final RegistryObject<Block> GAMMAROACH_NEST =
            AC_BLOCKS.register("gammaroach_nest", GammaroachNestBlock::new);

    public static final RegistryObject<Block> DARK_ALTAR_ABYSSMARINE =
            AC_BLOCKS.register("dark_altar_abyssmarine", () -> new AcDarkAltarBlock(abyssmarineProps()));

    public static final RegistryObject<Block> DARK_ALTAR_GUANOSTONE =
            AC_BLOCKS.register("dark_altar_guanostone", () -> new AcDarkAltarBlock(guanostoneProps()));

    public static final RegistryObject<Block> DARK_ALTAR_GINGERBREAD =
            AC_BLOCKS.register("dark_altar_gingerbread", () -> new AcDarkAltarBlock(gingerbreadProps()));

    public static final RegistryObject<Block> DARK_ALTAR_RADROCK =
            AC_BLOCKS.register("dark_altar_radrock", () -> new AcDarkAltarBlock(radrockProps()));

    public static final RegistryObject<Block> DARK_ALTAR_LIMESTONE =
            AC_BLOCKS.register("dark_altar_limestone", () -> new AcDarkAltarBlock(limestoneProps()));

    public static final RegistryObject<Block> DARK_ALTAR_GALENA =
            AC_BLOCKS.register("dark_altar_galena", () -> new AcDarkAltarBlock(galenaProps()));

    public static final RegistryObject<Block> PEDESTAL_ABYSSMARINE =
            AC_BLOCKS.register("pedestal_abyssmarine", () -> new AcPedestalBlock(abyssmarineProps()));

    public static final RegistryObject<Block> PEDESTAL_GUANOSTONE =
            AC_BLOCKS.register("pedestal_guanostone", () -> new AcPedestalBlock(guanostoneProps()));

    public static final RegistryObject<Block> PEDESTAL_GINGERBREAD =
            AC_BLOCKS.register("pedestal_gingerbread", () -> new AcPedestalBlock(gingerbreadProps()));

    public static final RegistryObject<Block> PEDESTAL_RADROCK =
            AC_BLOCKS.register("pedestal_radrock", () -> new AcPedestalBlock(radrockProps()));

    public static final RegistryObject<Block> PEDESTAL_LIMESTONE =
            AC_BLOCKS.register("pedestal_limestone", () -> new AcPedestalBlock(limestoneProps()));

    public static final RegistryObject<Block> PEDESTAL_GALENA =
            AC_BLOCKS.register("pedestal_galena", () -> new AcPedestalBlock(galenaProps()));

    private static BlockBehaviour.Properties abyssmarineProps() {
        return BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE)
                .requiresCorrectToolForDrops().strength(2.5F, 50.0F)
                .sound(SoundType.DEEPSLATE).noOcclusion();
    }

    private static BlockBehaviour.Properties guanostoneProps() {
        return BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_CYAN)
                .requiresCorrectToolForDrops().strength(1.3F, 2.0F)
                .sound(SoundType.BASALT).noOcclusion();
    }

    private static BlockBehaviour.Properties gingerbreadProps() {
        return BlockBehaviour.Properties.of().mapColor(MapColor.DIRT)
                .strength(1.0F, 1.5F).sound(ACSoundTypes.DENSE_CANDY)
                .instrument(NoteBlockInstrument.BASS).noOcclusion();
    }

    private static BlockBehaviour.Properties radrockProps() {
        return BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN)
                .requiresCorrectToolForDrops().strength(4.0F, 11.0F)
                .sound(ACSoundTypes.RADROCK).noOcclusion();
    }

    private static BlockBehaviour.Properties limestoneProps() {
        return BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_YELLOW)
                .requiresCorrectToolForDrops().strength(1.2F, 4.5F)
                .sound(SoundType.DRIPSTONE_BLOCK).noOcclusion();
    }

    private static BlockBehaviour.Properties galenaProps() {
        return BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE)
                .requiresCorrectToolForDrops().strength(3.5F, 10.0F)
                .sound(SoundType.DEEPSLATE).noOcclusion();
    }

    public static void register(IEventBus modEventBus) {
        AC_BLOCKS.register(modEventBus);
    }
}
