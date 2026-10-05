package com.qiuyue.goetyominous.common.blocks.trial;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.items.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class TrialBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, GoetyOminous.MOD_ID);

    private static final WeatheringCopper.WeatherState UNAFFECTED = WeatheringCopper.WeatherState.UNAFFECTED;
    private static final WeatheringCopper.WeatherState EXPOSED = WeatheringCopper.WeatherState.EXPOSED;
    private static final WeatheringCopper.WeatherState WEATHERED = WeatheringCopper.WeatherState.WEATHERED;
    private static final WeatheringCopper.WeatherState OXIDIZED = WeatheringCopper.WeatherState.OXIDIZED;

    public static final RegistryObject<Block> TUFF_SLAB = block("tuff_slab", () -> new SlabBlock(tuffProps()));
    public static final RegistryObject<Block> TUFF_STAIRS = block("tuff_stairs", () -> new StairBlock(Blocks.TUFF.defaultBlockState(), tuffProps()));
    public static final RegistryObject<Block> TUFF_WALL = block("tuff_wall", () -> new WallBlock(tuffProps().forceSolidOn()));

    public static final RegistryObject<Block> POLISHED_TUFF = block("polished_tuff", () -> new Block(polishedTuffProps()));
    public static final RegistryObject<Block> POLISHED_TUFF_SLAB = block("polished_tuff_slab", () -> new SlabBlock(polishedTuffProps()));
    public static final RegistryObject<Block> POLISHED_TUFF_STAIRS = block("polished_tuff_stairs", () -> new StairBlock(POLISHED_TUFF.get().defaultBlockState(), polishedTuffProps()));
    public static final RegistryObject<Block> POLISHED_TUFF_WALL = block("polished_tuff_wall", () -> new WallBlock(polishedTuffProps().forceSolidOn()));

    public static final RegistryObject<Block> CHISELED_TUFF = block("chiseled_tuff", () -> new Block(tuffProps()));

    public static final RegistryObject<Block> TUFF_BRICKS = block("tuff_bricks", () -> new Block(tuffBricksProps()));
    public static final RegistryObject<Block> TUFF_BRICK_SLAB = block("tuff_brick_slab", () -> new SlabBlock(tuffBricksProps()));
    public static final RegistryObject<Block> TUFF_BRICK_STAIRS = block("tuff_brick_stairs", () -> new StairBlock(TUFF_BRICKS.get().defaultBlockState(), tuffBricksProps()));
    public static final RegistryObject<Block> TUFF_BRICK_WALL = block("tuff_brick_wall", () -> new WallBlock(tuffBricksProps().forceSolidOn()));
    public static final RegistryObject<Block> CHISELED_TUFF_BRICKS = block("chiseled_tuff_bricks", () -> new Block(tuffBricksProps()));

    public static final RegistryObject<Block> CHISELED_COPPER = block("chiseled_copper", () -> new OminousWeatheringCopperFullBlock(UNAFFECTED, copperProps(UNAFFECTED)));
    public static final RegistryObject<Block> EXPOSED_CHISELED_COPPER = block("exposed_chiseled_copper", () -> new OminousWeatheringCopperFullBlock(EXPOSED, copperProps(EXPOSED)));
    public static final RegistryObject<Block> WEATHERED_CHISELED_COPPER = block("weathered_chiseled_copper", () -> new OminousWeatheringCopperFullBlock(WEATHERED, copperProps(WEATHERED)));
    public static final RegistryObject<Block> OXIDIZED_CHISELED_COPPER = block("oxidized_chiseled_copper", () -> new OminousWeatheringCopperFullBlock(OXIDIZED, copperProps(OXIDIZED)));
    public static final RegistryObject<Block> WAXED_CHISELED_COPPER = block("waxed_chiseled_copper", () -> new Block(copperProps(UNAFFECTED)));
    public static final RegistryObject<Block> WAXED_EXPOSED_CHISELED_COPPER = block("waxed_exposed_chiseled_copper", () -> new Block(copperProps(EXPOSED)));
    public static final RegistryObject<Block> WAXED_WEATHERED_CHISELED_COPPER = block("waxed_weathered_chiseled_copper", () -> new Block(copperProps(WEATHERED)));
    public static final RegistryObject<Block> WAXED_OXIDIZED_CHISELED_COPPER = block("waxed_oxidized_chiseled_copper", () -> new Block(copperProps(OXIDIZED)));

    public static final RegistryObject<Block> COPPER_GRATE = block("copper_grate", () -> new OminousWeatheringCopperGrateBlock(UNAFFECTED, grateProps(UNAFFECTED)));
    public static final RegistryObject<Block> EXPOSED_COPPER_GRATE = block("exposed_copper_grate", () -> new OminousWeatheringCopperGrateBlock(EXPOSED, grateProps(EXPOSED)));
    public static final RegistryObject<Block> WEATHERED_COPPER_GRATE = block("weathered_copper_grate", () -> new OminousWeatheringCopperGrateBlock(WEATHERED, grateProps(WEATHERED)));
    public static final RegistryObject<Block> OXIDIZED_COPPER_GRATE = block("oxidized_copper_grate", () -> new OminousWeatheringCopperGrateBlock(OXIDIZED, grateProps(OXIDIZED)));
    public static final RegistryObject<Block> WAXED_COPPER_GRATE = block("waxed_copper_grate", () -> new CopperGrateBlock(grateProps(UNAFFECTED)));
    public static final RegistryObject<Block> WAXED_EXPOSED_COPPER_GRATE = block("waxed_exposed_copper_grate", () -> new CopperGrateBlock(grateProps(EXPOSED)));
    public static final RegistryObject<Block> WAXED_WEATHERED_COPPER_GRATE = block("waxed_weathered_copper_grate", () -> new CopperGrateBlock(grateProps(WEATHERED)));
    public static final RegistryObject<Block> WAXED_OXIDIZED_COPPER_GRATE = block("waxed_oxidized_copper_grate", () -> new CopperGrateBlock(grateProps(OXIDIZED)));

    public static final RegistryObject<Block> COPPER_BULB = block("copper_bulb", () -> new OminousWeatheringCopperBulbBlock(UNAFFECTED, bulbProps(UNAFFECTED)));
    public static final RegistryObject<Block> EXPOSED_COPPER_BULB = block("exposed_copper_bulb", () -> new OminousWeatheringCopperBulbBlock(EXPOSED, bulbProps(EXPOSED)));
    public static final RegistryObject<Block> WEATHERED_COPPER_BULB = block("weathered_copper_bulb", () -> new OminousWeatheringCopperBulbBlock(WEATHERED, bulbProps(WEATHERED)));
    public static final RegistryObject<Block> OXIDIZED_COPPER_BULB = block("oxidized_copper_bulb", () -> new OminousWeatheringCopperBulbBlock(OXIDIZED, bulbProps(OXIDIZED)));
    public static final RegistryObject<Block> WAXED_COPPER_BULB = block("waxed_copper_bulb", () -> new CopperBulbBlock(bulbProps(UNAFFECTED)));
    public static final RegistryObject<Block> WAXED_EXPOSED_COPPER_BULB = block("waxed_exposed_copper_bulb", () -> new CopperBulbBlock(bulbProps(EXPOSED)));
    public static final RegistryObject<Block> WAXED_WEATHERED_COPPER_BULB = block("waxed_weathered_copper_bulb", () -> new CopperBulbBlock(bulbProps(WEATHERED)));
    public static final RegistryObject<Block> WAXED_OXIDIZED_COPPER_BULB = block("waxed_oxidized_copper_bulb", () -> new CopperBulbBlock(bulbProps(OXIDIZED)));

    public static final RegistryObject<Block> COPPER_DOOR = block("copper_door", () -> new OminousWeatheringCopperDoorBlock(TrialBlockSetTypes.COPPER, UNAFFECTED, doorProps(UNAFFECTED)));
    public static final RegistryObject<Block> EXPOSED_COPPER_DOOR = block("exposed_copper_door", () -> new OminousWeatheringCopperDoorBlock(TrialBlockSetTypes.COPPER, EXPOSED, doorProps(EXPOSED)));
    public static final RegistryObject<Block> WEATHERED_COPPER_DOOR = block("weathered_copper_door", () -> new OminousWeatheringCopperDoorBlock(TrialBlockSetTypes.COPPER, WEATHERED, doorProps(WEATHERED)));
    public static final RegistryObject<Block> OXIDIZED_COPPER_DOOR = block("oxidized_copper_door", () -> new OminousWeatheringCopperDoorBlock(TrialBlockSetTypes.COPPER, OXIDIZED, doorProps(OXIDIZED)));
    public static final RegistryObject<Block> WAXED_COPPER_DOOR = block("waxed_copper_door", () -> new CopperDoorBlock(TrialBlockSetTypes.COPPER, doorProps(UNAFFECTED)));
    public static final RegistryObject<Block> WAXED_EXPOSED_COPPER_DOOR = block("waxed_exposed_copper_door", () -> new CopperDoorBlock(TrialBlockSetTypes.COPPER, doorProps(EXPOSED)));
    public static final RegistryObject<Block> WAXED_WEATHERED_COPPER_DOOR = block("waxed_weathered_copper_door", () -> new CopperDoorBlock(TrialBlockSetTypes.COPPER, doorProps(WEATHERED)));
    public static final RegistryObject<Block> WAXED_OXIDIZED_COPPER_DOOR = block("waxed_oxidized_copper_door", () -> new CopperDoorBlock(TrialBlockSetTypes.COPPER, doorProps(OXIDIZED)));

    public static final RegistryObject<Block> COPPER_TRAPDOOR = block("copper_trapdoor", () -> new OminousWeatheringCopperTrapDoorBlock(TrialBlockSetTypes.COPPER, UNAFFECTED, trapdoorProps(UNAFFECTED)));
    public static final RegistryObject<Block> EXPOSED_COPPER_TRAPDOOR = block("exposed_copper_trapdoor", () -> new OminousWeatheringCopperTrapDoorBlock(TrialBlockSetTypes.COPPER, EXPOSED, trapdoorProps(EXPOSED)));
    public static final RegistryObject<Block> WEATHERED_COPPER_TRAPDOOR = block("weathered_copper_trapdoor", () -> new OminousWeatheringCopperTrapDoorBlock(TrialBlockSetTypes.COPPER, WEATHERED, trapdoorProps(WEATHERED)));
    public static final RegistryObject<Block> OXIDIZED_COPPER_TRAPDOOR = block("oxidized_copper_trapdoor", () -> new OminousWeatheringCopperTrapDoorBlock(TrialBlockSetTypes.COPPER, OXIDIZED, trapdoorProps(OXIDIZED)));
    public static final RegistryObject<Block> WAXED_COPPER_TRAPDOOR = block("waxed_copper_trapdoor", () -> new TrapDoorBlock(trapdoorProps(UNAFFECTED), TrialBlockSetTypes.COPPER));
    public static final RegistryObject<Block> WAXED_EXPOSED_COPPER_TRAPDOOR = block("waxed_exposed_copper_trapdoor", () -> new TrapDoorBlock(trapdoorProps(EXPOSED), TrialBlockSetTypes.COPPER));
    public static final RegistryObject<Block> WAXED_WEATHERED_COPPER_TRAPDOOR = block("waxed_weathered_copper_trapdoor", () -> new TrapDoorBlock(trapdoorProps(WEATHERED), TrialBlockSetTypes.COPPER));
    public static final RegistryObject<Block> WAXED_OXIDIZED_COPPER_TRAPDOOR = block("waxed_oxidized_copper_trapdoor", () -> new TrapDoorBlock(trapdoorProps(OXIDIZED), TrialBlockSetTypes.COPPER));

    public static final Supplier<BiMap<Block, Block>> WAXABLES = Suppliers.memoize(TrialBlocks::waxingMap);
    public static final Supplier<BiMap<Block, Block>> WAX_OFF_BY_BLOCK = Suppliers.memoize(() -> WAXABLES.get().inverse());

    private static BlockBehaviour.Properties tuffProps() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.TERRACOTTA_GRAY)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .sound(SoundType.TUFF)
                .requiresCorrectToolForDrops()
                .strength(1.5F, 6.0F);
    }

    private static BlockBehaviour.Properties polishedTuffProps() {
        return tuffProps().sound(TrialSoundTypes.POLISHED_TUFF);
    }

    private static BlockBehaviour.Properties tuffBricksProps() {
        return tuffProps().sound(TrialSoundTypes.TUFF_BRICKS);
    }

    private static MapColor copperColor(WeatheringCopper.WeatherState state) {
        return switch (state) {
            case EXPOSED -> MapColor.TERRACOTTA_LIGHT_GRAY;
            case WEATHERED -> MapColor.WARPED_STEM;
            case OXIDIZED -> MapColor.WARPED_NYLIUM;
            default -> MapColor.COLOR_ORANGE;
        };
    }

    private static int bulbEmission(WeatheringCopper.WeatherState state) {
        return switch (state) {
            case EXPOSED -> 12;
            case WEATHERED -> 8;
            case OXIDIZED -> 4;
            default -> 15;
        };
    }

    private static BlockBehaviour.Properties copperProps(WeatheringCopper.WeatherState state) {
        return BlockBehaviour.Properties.of()
                .mapColor(copperColor(state))
                .requiresCorrectToolForDrops()
                .strength(3.0F, 6.0F)
                .sound(SoundType.COPPER);
    }

    private static BlockBehaviour.Properties grateProps(WeatheringCopper.WeatherState state) {
        return BlockBehaviour.Properties.of()
                .mapColor(copperColor(state))
                .strength(3.0F, 6.0F)
                .sound(TrialSoundTypes.COPPER_GRATE)
                .noOcclusion()
                .requiresCorrectToolForDrops()
                .isValidSpawn((state0, level, pos, type) -> false)
                .isRedstoneConductor((state0, level, pos) -> false)
                .isSuffocating((state0, level, pos) -> false)
                .isViewBlocking((state0, level, pos) -> false);
    }

    private static BlockBehaviour.Properties bulbProps(WeatheringCopper.WeatherState state) {
        int emission = bulbEmission(state);
        return BlockBehaviour.Properties.of()
                .mapColor(copperColor(state))
                .strength(3.0F, 6.0F)
                .sound(TrialSoundTypes.COPPER_BULB)
                .requiresCorrectToolForDrops()
                .isRedstoneConductor((state0, level, pos) -> false)
                .lightLevel(state0 -> state0.getValue(CopperBulbBlock.LIT) ? emission : 0);
    }

    private static BlockBehaviour.Properties doorProps(WeatheringCopper.WeatherState state) {
        return BlockBehaviour.Properties.of()
                .mapColor(copperColor(state))
                .strength(3.0F, 6.0F)
                .noOcclusion()
                .requiresCorrectToolForDrops()
                .pushReaction(PushReaction.DESTROY);
    }

    private static BlockBehaviour.Properties trapdoorProps(WeatheringCopper.WeatherState state) {
        return BlockBehaviour.Properties.of()
                .mapColor(copperColor(state))
                .strength(3.0F, 6.0F)
                .requiresCorrectToolForDrops()
                .noOcclusion()
                .isValidSpawn((state0, level, pos, type) -> false);
    }

    private static RegistryObject<Block> block(String name, Supplier<Block> supplier) {
        RegistryObject<Block> registered = BLOCKS.register(name, supplier);
        ModItems.ITEMS.register(name, () -> new BlockItem(registered.get(), new Item.Properties()));
        return registered;
    }

    public static ImmutableBiMap<Block, Block> waxingMap() {
        ImmutableBiMap.Builder<Block, Block> builder = ImmutableBiMap.builder();
        builder.put(CHISELED_COPPER.get(), WAXED_CHISELED_COPPER.get());
        builder.put(EXPOSED_CHISELED_COPPER.get(), WAXED_EXPOSED_CHISELED_COPPER.get());
        builder.put(WEATHERED_CHISELED_COPPER.get(), WAXED_WEATHERED_CHISELED_COPPER.get());
        builder.put(OXIDIZED_CHISELED_COPPER.get(), WAXED_OXIDIZED_CHISELED_COPPER.get());
        builder.put(COPPER_GRATE.get(), WAXED_COPPER_GRATE.get());
        builder.put(EXPOSED_COPPER_GRATE.get(), WAXED_EXPOSED_COPPER_GRATE.get());
        builder.put(WEATHERED_COPPER_GRATE.get(), WAXED_WEATHERED_COPPER_GRATE.get());
        builder.put(OXIDIZED_COPPER_GRATE.get(), WAXED_OXIDIZED_COPPER_GRATE.get());
        builder.put(COPPER_BULB.get(), WAXED_COPPER_BULB.get());
        builder.put(EXPOSED_COPPER_BULB.get(), WAXED_EXPOSED_COPPER_BULB.get());
        builder.put(WEATHERED_COPPER_BULB.get(), WAXED_WEATHERED_COPPER_BULB.get());
        builder.put(OXIDIZED_COPPER_BULB.get(), WAXED_OXIDIZED_COPPER_BULB.get());
        builder.put(COPPER_DOOR.get(), WAXED_COPPER_DOOR.get());
        builder.put(EXPOSED_COPPER_DOOR.get(), WAXED_EXPOSED_COPPER_DOOR.get());
        builder.put(WEATHERED_COPPER_DOOR.get(), WAXED_WEATHERED_COPPER_DOOR.get());
        builder.put(OXIDIZED_COPPER_DOOR.get(), WAXED_OXIDIZED_COPPER_DOOR.get());
        builder.put(COPPER_TRAPDOOR.get(), WAXED_COPPER_TRAPDOOR.get());
        builder.put(EXPOSED_COPPER_TRAPDOOR.get(), WAXED_EXPOSED_COPPER_TRAPDOOR.get());
        builder.put(WEATHERED_COPPER_TRAPDOOR.get(), WAXED_WEATHERED_COPPER_TRAPDOOR.get());
        builder.put(OXIDIZED_COPPER_TRAPDOOR.get(), WAXED_OXIDIZED_COPPER_TRAPDOOR.get());
        return builder.build();
    }

    public static ImmutableBiMap<Block, Block> copperAgingMap() {
        ImmutableBiMap.Builder<Block, Block> builder = ImmutableBiMap.builder();
        aging(builder, CHISELED_COPPER, EXPOSED_CHISELED_COPPER, WEATHERED_CHISELED_COPPER, OXIDIZED_CHISELED_COPPER);
        aging(builder, COPPER_GRATE, EXPOSED_COPPER_GRATE, WEATHERED_COPPER_GRATE, OXIDIZED_COPPER_GRATE);
        aging(builder, COPPER_BULB, EXPOSED_COPPER_BULB, WEATHERED_COPPER_BULB, OXIDIZED_COPPER_BULB);
        aging(builder, COPPER_DOOR, EXPOSED_COPPER_DOOR, WEATHERED_COPPER_DOOR, OXIDIZED_COPPER_DOOR);
        aging(builder, COPPER_TRAPDOOR, EXPOSED_COPPER_TRAPDOOR, WEATHERED_COPPER_TRAPDOOR, OXIDIZED_COPPER_TRAPDOOR);
        return builder.build();
    }

    private static void aging(ImmutableBiMap.Builder<Block, Block> builder, RegistryObject<Block> first, RegistryObject<Block> second, RegistryObject<Block> third, RegistryObject<Block> fourth) {
        builder.put(first.get(), second.get());
        builder.put(second.get(), third.get());
        builder.put(third.get(), fourth.get());
    }

    public static BlockState getWaxed(BlockState state) {
        Block waxed = WAXABLES.get().get(state.getBlock());
        return waxed == null ? null : waxed.withPropertiesOf(state);
    }

    public static BlockState getUnwaxed(BlockState state) {
        Block unwaxed = WAX_OFF_BY_BLOCK.get().get(state.getBlock());
        return unwaxed == null ? null : unwaxed.withPropertiesOf(state);
    }

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}
