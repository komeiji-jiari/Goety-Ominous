package com.Polarice3.Goety.common.blocks;

import com.Polarice3.Goety.common.blocks.ModBlocks.FacingBlock;
import com.Polarice3.Goety.common.blocks.ModBlocks.LootTableType;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.block.BlackCrystalItem;
import com.Polarice3.Goety.common.items.block.BlockISTERItem;
import com.Polarice3.Goety.common.items.block.BlockItemBase;
import com.Polarice3.Goety.common.items.block.CurioISTERItem;
import com.Polarice3.Goety.common.items.block.EnchantableBlockItem;
import com.Polarice3.Goety.common.items.block.HauntedJugItem;
import com.Polarice3.Goety.common.items.block.OminousIdolBlockItem;
import com.Polarice3.Goety.common.items.block.ResonanceBlockItem;
import com.Polarice3.Goety.common.world.features.trees.ChorusTree;
import com.Polarice3.Goety.common.world.features.trees.HauntedTree;
import com.Polarice3.Goety.common.world.features.trees.PineTree;
import com.Polarice3.Goety.common.world.features.trees.RottenTree;
import com.Polarice3.Goety.common.world.features.trees.WindsweptTree;
import com.Polarice3.Goety.init.ModSoundTypes;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.GlassBlock;
import net.minecraft.world.level.block.GlazedTerracottaBlock;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.MangroveRootsBlock;
import net.minecraft.world.level.block.PoweredBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.WoolCarpetBlock;
import net.minecraft.world.level.block.PressurePlateBlock.Sensitivity;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.OffsetType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, "goety");
    public static final Map<ResourceLocation, ModBlocks.BlockLootSetting> BLOCK_LOOT = new HashMap();
    public static final RegistryObject<Block> ARCA_BLOCK = register("arca", ArcaBlock::new);
    public static final RegistryObject<Block> CURSED_INFUSER = register("cursed_infuser", CursedInfuserBlock::new);
    public static final RegistryObject<Block> GRIM_INFUSER = register("grim_infuser", GrimInfuserBlock::new);
    public static final RegistryObject<Block> CURSED_CAGE_BLOCK = register("cursed_cage", CursedCageBlock::new);
    public static final RegistryObject<Block> DARK_ALTAR = register("dark_altar", DarkAltarBlock::new);
    public static final RegistryObject<Block> DARK_ALTAR_STONE = register("dark_altar_stone", () -> new DarkAltarBlock(Properties.m_60926_(Blocks.f_50222_).m_60955_()));
    public static final RegistryObject<Block> DARK_ALTAR_DEEPSLATE = register("dark_altar_deepslate", () -> new DarkAltarBlock(Properties.m_60926_(Blocks.f_152589_).m_60955_()));
    public static final RegistryObject<Block> DARK_ALTAR_OMINOUS_STONE = register("dark_altar_ominous_stone", () -> new DarkAltarBlock(OminousStoneProperties().m_60955_()));
    public static final RegistryObject<Block> DARK_ALTAR_NETHER_BRICK = register("dark_altar_nether_brick", () -> new DarkAltarBlock(Properties.m_60926_(Blocks.f_50197_).m_60955_()));
    public static final RegistryObject<Block> DARK_ALTAR_BLACKSTONE = register("dark_altar_blackstone", () -> new DarkAltarBlock(Properties.m_60926_(Blocks.f_50735_).m_60955_()));
    public static final RegistryObject<Block> DARK_ALTAR_END_STONE = register("dark_altar_end_stone", () -> new DarkAltarBlock(Properties.m_60926_(Blocks.f_50443_).m_60955_()));
    public static final RegistryObject<Block> DARK_ALTAR_HIGHROCK = register("dark_altar_highrock", () -> new DarkAltarBlock(HighrockProperties().m_60955_()));
    public static final RegistryObject<Block> DARK_ALTAR_MARBLE = register("dark_altar_marble", () -> new DarkAltarBlock(MarbleProperties().m_60955_()));
    public static final RegistryObject<Block> DARK_ALTAR_PRISMARINE = register("dark_altar_prismarine", () -> new DarkAltarBlock(Properties.m_60926_(Blocks.f_50378_).m_60955_()));
    public static final RegistryObject<Block> DARK_ALTAR_CRYPT_STONE = register("dark_altar_crypt_stone", () -> new DarkAltarBlock(CryptStoneProperties().m_60955_()));
    public static final RegistryObject<Block> PEDESTAL = register("pedestal", PedestalBlock::new);
    public static final RegistryObject<Block> PEDESTAL_STONE = register("pedestal_stone", () -> new PedestalBlock(Properties.m_60926_(Blocks.f_50222_).m_60955_()));
    public static final RegistryObject<Block> PEDESTAL_DEEPSLATE = register("pedestal_deepslate", () -> new PedestalBlock(Properties.m_60926_(Blocks.f_152589_).m_60955_()));
    public static final RegistryObject<Block> PEDESTAL_OMINOUS_STONE = register("pedestal_ominous_stone", () -> new PedestalBlock(OminousStoneProperties().m_60955_()));
    public static final RegistryObject<Block> PEDESTAL_NETHER_BRICK = register("pedestal_nether_brick", () -> new PedestalBlock(Properties.m_60926_(Blocks.f_50197_).m_60955_()));
    public static final RegistryObject<Block> PEDESTAL_BLACKSTONE = register("pedestal_blackstone", () -> new PedestalBlock(Properties.m_60926_(Blocks.f_50735_).m_60955_()));
    public static final RegistryObject<Block> PEDESTAL_END_STONE = register("pedestal_end_stone", () -> new PedestalBlock(Properties.m_60926_(Blocks.f_50443_).m_60955_()));
    public static final RegistryObject<Block> PEDESTAL_HIGHROCK = register("pedestal_highrock", () -> new PedestalBlock(HighrockProperties().m_60955_()));
    public static final RegistryObject<Block> PEDESTAL_MARBLE = register("pedestal_marble", () -> new PedestalBlock(MarbleProperties().m_60955_()));
    public static final RegistryObject<Block> PEDESTAL_PRISMARINE = register("pedestal_prismarine", () -> new PedestalBlock(Properties.m_60926_(Blocks.f_50378_).m_60955_()));
    public static final RegistryObject<Block> PEDESTAL_CRYPT_STONE = register("pedestal_crypt_stone", () -> new PedestalBlock(CryptStoneProperties().m_60955_()));
    public static final RegistryObject<Block> SOUL_ABSORBER = register("soul_absorber", SoulAbsorberBlock::new);
    public static final RegistryObject<Block> SOUL_MENDER = register("soul_mender", SoulMenderBlock::new);
    public static final RegistryObject<Block> ICE_BOUQUET_TRAP = register("ice_bouquet_trap", IceBouquetTrapBlock::new);
    public static final RegistryObject<Block> WIND_BLOWER = register("wind_blower", () -> new WindBlowerBlock(ShadeStoneProperties()));
    public static final RegistryObject<Block> MARBLE_WIND_BLOWER = register("marble_wind_blower", () -> new WindBlowerBlock(MarbleProperties()));
    public static final RegistryObject<Block> RESONANCE_CRYSTAL = register("resonance_crystal", ResonanceCrystalBlock::new, false, LootTableType.EMPTY);
    public static final RegistryObject<Block> SCULK_DEVOURER = enchantedRegister("sculk_devourer", SculkDevourerBlock::new);
    public static final RegistryObject<Block> SCULK_CONVERTER = enchantedRegister("sculk_converter", SculkConverterBlock::new);
    public static final RegistryObject<Block> SCULK_RELAY = register("sculk_relay", SculkRelayBlock::new, true, LootTableType.EMPTY);
    public static final RegistryObject<Block> SCULK_GROWER = enchantedRegister("sculk_grower", SculkGrowerBlock::new);
    public static final RegistryObject<Block> SPIDER_NEST = register("spider_nest", SpiderNestBlock::new, true, LootTableType.EMPTY);
    public static final RegistryObject<Block> SHADE_GRAVESTONE = register("shade_gravestone", GravestoneBlock::new);
    public static final RegistryObject<Block> SHADE_OSSUARY = register("shade_ossuary", OssuaryBlock::new);
    public static final RegistryObject<Block> BLAZING_CAGE = register("blazing_cage", BlazingCageBlock::new);
    public static final RegistryObject<Block> OMINOUS_PYRE = register("ominous_pyre", OminousPyreBlock::new);
    public static final RegistryObject<Block> OMINOUS_IDOL = register("ominous_idol", OminousIdolBlock::new, false, LootTableType.EMPTY);
    public static final RegistryObject<ModChestBlock> RAIDING_CHEST = isterRegister("raiding_chest", () -> new ModChestBlock(Properties.m_284310_().m_280658_(NoteBlockInstrument.BASS).m_60978_(2.5F).m_60918_(SoundType.f_56736_)));
    public static final RegistryObject<ModTrappedChestBlock> TRAPPED_RAIDING_CHEST = isterRegister("trapped_raiding_chest", () -> new ModTrappedChestBlock(Properties.m_284310_().m_280658_(NoteBlockInstrument.BASS).m_60978_(2.5F).m_60918_(SoundType.f_56736_)));
    public static final RegistryObject<Block> FORBIDDEN_GRASS = register("forbidden_grass", ForbiddenGrassBlock::new, true, LootTableType.EMPTY);
    public static final RegistryObject<Block> HOOK_BELL = register("hook_bell", HookBellBlock::new);
    public static final RegistryObject<Block> SHRIEKING_OBELISK = register("shriek_obelisk", ShriekObeliskBlock::new);
    public static final RegistryObject<Block> NECRO_BRAZIER = register("necro_brazier", NecroBrazierBlock::new);
    public static final RegistryObject<Block> ANIMATOR = register("animator", AnimatorBlock::new);
    public static final RegistryObject<Block> BLACK_CRYSTAL = register("black_crystal", BlackCrystalBlock::new, false, LootTableType.EMPTY);
    public static final RegistryObject<Block> DARK_ANVIL = register("dark_anvil", DarkAnvilBlock::new);
    public static final RegistryObject<Block> CHIPPED_DARK_ANVIL = register("chipped_dark_anvil", DarkAnvilBlock::new);
    public static final RegistryObject<Block> DAMAGED_DARK_ANVIL = register("damaged_dark_anvil", DarkAnvilBlock::new);
    public static final RegistryObject<Block> SOUL_CANDLESTICK = register("soul_candlestick", SoulCandlestickBlock::new);
    public static final RegistryObject<Block> WITCH_POLE = register("witch_pole", WitchPoleBlock::new);
    public static final RegistryObject<Block> BREWING_CAULDRON = register("witch_cauldron", BrewCauldronBlock::new);
    public static final RegistryObject<Block> CRYSTAL_BALL = register("crystal_ball", CrystalBallBlock::new, true);
    public static final RegistryObject<Block> HAUNTED_MIRROR = register("haunted_mirror", HauntedMirrorBlock::new);
    public static final RegistryObject<Block> HAUNTED_JUG = register("haunted_jug", HauntedJugBlock::new, false);
    public static final RegistryObject<Block> MAGIC_THORN = register("magic_thorn", MagicThornBlock::new, true, LootTableType.EMPTY);
    public static final RegistryObject<Block> HARDENED_LEAVES = register("hardened_leaves", () -> new LeavesBlock(Properties.m_284310_().m_284180_(MapColor.f_283915_).m_278183_().m_278166_(PushReaction.DESTROY).m_60978_(2.0F).m_60977_().m_60918_(SoundType.f_56740_).m_60955_().m_60922_(ModBlocks::ocelotOrParrot).m_60960_(ModBlocks::never).m_60971_(ModBlocks::never)), true, LootTableType.EMPTY);
    public static final RegistryObject<Block> OVERGROWN_ROOTS = register("overgrown_roots", () -> new MangroveRootsBlock(Properties.m_284310_().m_284180_(MapColor.f_283915_).m_60978_(2.0F).m_60918_(SoundType.f_222467_).m_278183_()), true);
    public static final RegistryObject<Block> CORPSE_BLOSSOM = register("corpse_blossom", () -> new CorpseBlossomBlock(Properties.m_284310_().m_284180_(MapColor.f_283915_).m_60966_().m_60910_().m_60918_(SoundType.f_154665_).m_278166_(PushReaction.DESTROY)), true);
    public static final RegistryObject<Block> HOLE = register("hole", HoleBlock::new, false, LootTableType.EMPTY);
    public static final RegistryObject<Block> PART_LIQUID = register("part_liquid", PartLiquidBlock::new, false, LootTableType.EMPTY);
    public static final RegistryObject<Block> PITHOS = register("pithos", PithosBlock::new);
    public static final RegistryObject<Block> SPIDER_MOTHER_DEN = register("spider_mother_den", SpiderMotherDenBlock::new, true, LootTableType.EMPTY);
    public static final RegistryObject<Block> VOID_SPAWNER = register("void_spawner", VoidSpawnerBlock::new, true, LootTableType.EMPTY);
    public static final RegistryObject<Block> VOID_VAULT = register("void_vault", VoidVaultBlock::new, true, LootTableType.EMPTY);
    public static final RegistryObject<Block> VOID_FRAME = register("void_frame", VoidFrameBlock::new, true, LootTableType.EMPTY);
    public static final RegistryObject<Block> VOID_SHRINE = register("void_shrine", VoidShrineBlock::new, true, LootTableType.EMPTY);
    public static final RegistryObject<Block> NIGHT_BEACON = register("night_beacon", NightBeaconBlock::new, false);
    public static final RegistryObject<Block> TALL_SKULL_BLOCK = register("tall_skull", TallSkullBlock::new, false);
    public static final RegistryObject<Block> WALL_TALL_SKULL_BLOCK = register("wall_tall_skull", WallTallSkullBlock::new, false, LootTableType.EMPTY);
    public static final RegistryObject<Block> REDSTONE_GOLEM_SKULL_BLOCK = register("redstone_golem_skull", RedstoneGolemSkullBlock::new, false, LootTableType.EMPTY);
    public static final RegistryObject<Block> WALL_REDSTONE_GOLEM_SKULL_BLOCK = register("wall_redstone_golem_skull", WallRedstoneGolemSkullBlock::new, false, LootTableType.EMPTY);
    public static final RegistryObject<Block> GRAVE_GOLEM_SKULL_BLOCK = register("grave_golem_skull", GraveGolemSkullBlock::new, false, LootTableType.EMPTY);
    public static final RegistryObject<Block> WALL_GRAVE_GOLEM_SKULL_BLOCK = register("wall_grave_golem_skull", WallGraveGolemSkullBlock::new, false, LootTableType.EMPTY);
    public static final RegistryObject<Block> REDSTONE_MONSTROSITY_HEAD_BLOCK = register("redstone_monstrosity_head", RedstoneMonstrosityHeadBlock::new, false, LootTableType.EMPTY);
    public static final RegistryObject<Block> WALL_REDSTONE_MONSTROSITY_HEAD_BLOCK = register("wall_redstone_monstrosity_head", WallRedstoneMonstrosityHeadBlock::new, false, LootTableType.EMPTY);
    public static final RegistryObject<Block> SNAP_WARTS = register("snap_warts", SnapWartsBlock::new, false, LootTableType.EMPTY);
    public static final RegistryObject<Block> HENBANE = register("henbane", HenbaneBlock::new, false, LootTableType.EMPTY);
    public static final RegistryObject<Block> DEADLY_NIGHTSHADE = register("deadly_nightshade", NightshadeBlock::new, false, LootTableType.EMPTY);
    public static final RegistryObject<Block> SIENNA_GRASS = register("sienna_grass", SiennaGrassBlock::new, true, LootTableType.EMPTY);
    public static final RegistryObject<Block> TALL_SIENNA_GRASS = register("tall_sienna_grass", LargeSiennaPlantBlock::new, true, LootTableType.EMPTY);
    public static final RegistryObject<Block> SIENNA_FERN = register("sienna_fern", SiennaGrassBlock::new, true, LootTableType.EMPTY);
    public static final RegistryObject<Block> LARGE_SIENNA_FERN = register("large_sienna_fern", LargeSiennaPlantBlock::new, true, LootTableType.EMPTY);
    public static final RegistryObject<Block> WINDSWEPT_DEAD_BUSH = register("windswept_dead_bush", WindsweptDeadBushBlock::new, true, LootTableType.EMPTY);
    public static final RegistryObject<Block> SIENNA_VINE = register("sienna_vine", () -> new VineBlock(Properties.m_284310_().m_284180_(MapColor.f_283913_).m_280170_().m_60910_().m_60977_().m_60978_(0.2F).m_60918_(SoundType.f_56760_).m_278183_().m_278166_(PushReaction.DESTROY)), true, LootTableType.EMPTY);
    public static final RegistryObject<Block> FIRETHORN = register("firethorn", FirethornBushBlock::new, false, LootTableType.EMPTY);
    public static final RegistryObject<Block> POTTED_HENBANE = register("potted_henbane", () -> new FlowerPotBlock(() -> (FlowerPotBlock)ForgeRegistries.BLOCKS.getDelegateOrThrow(Blocks.f_50276_).get(), HENBANE, Properties.m_284310_().m_278166_(PushReaction.DESTROY).m_60955_().m_60966_()), false, LootTableType.EMPTY);
    public static final RegistryObject<Block> POTTED_DEADLY_NIGHTSHADE = register("potted_deadly_nightshade", () -> new FlowerPotBlock(() -> (FlowerPotBlock)ForgeRegistries.BLOCKS.getDelegateOrThrow(Blocks.f_50276_).get(), DEADLY_NIGHTSHADE, Properties.m_284310_().m_278166_(PushReaction.DESTROY).m_60955_().m_60966_()), false, LootTableType.EMPTY);
    public static final RegistryObject<Block> POTTED_SIENNA_GRASS = register("potted_sienna_grass", () -> new FlowerPotBlock(() -> (FlowerPotBlock)ForgeRegistries.BLOCKS.getDelegateOrThrow(Blocks.f_50276_).get(), SIENNA_GRASS, Properties.m_284310_().m_278166_(PushReaction.DESTROY).m_60953_((l) -> 4).m_60955_().m_60966_()), false, LootTableType.EMPTY);
    public static final RegistryObject<Block> POTTED_SIENNA_FERN = register("potted_sienna_fern", () -> new FlowerPotBlock(() -> (FlowerPotBlock)ForgeRegistries.BLOCKS.getDelegateOrThrow(Blocks.f_50276_).get(), SIENNA_FERN, Properties.m_284310_().m_278166_(PushReaction.DESTROY).m_60955_().m_60966_()), false, LootTableType.EMPTY);
    public static final RegistryObject<Block> POTTED_WINDSWEPT_DEAD_BUSH = register("potted_windswept_dead_bush", () -> new FlowerPotBlock(() -> (FlowerPotBlock)ForgeRegistries.BLOCKS.getDelegateOrThrow(Blocks.f_50276_).get(), WINDSWEPT_DEAD_BUSH, Properties.m_284310_().m_278166_(PushReaction.DESTROY).m_60955_().m_60966_()), false, LootTableType.EMPTY);
    public static final RegistryObject<Block> CHORUS_SPROUT = register("chorus_sprout", ChorusSproutBlock::new);
    public static final RegistryObject<Block> CHORUS_STALK = register("chorus_stalk", ChorusStalkBlock::new);
    public static final RegistryObject<Block> LARGE_CHORUS_STALK = register("large_chorus_stalk", LargeChorusStalkBlock::new);
    public static final RegistryObject<Block> END_GRASS_SPROUT = register("end_grass_sprout", EndGrassBlock::new, true, LootTableType.EMPTY);
    public static final RegistryObject<Block> END_GRASS = register("end_grass", EndGrassBlock::new, true, LootTableType.EMPTY);
    public static final RegistryObject<Block> TALL_END_GRASS = register("tall_end_grass", TallEndGrassBlock::new, true, LootTableType.EMPTY);
    public static final RegistryObject<Block> CHORUS_TALL_GRASS = register("chorus_tall_grass", () -> new EndPlantBlock(Properties.m_284310_().m_284180_(MapColor.f_283832_).m_280170_().m_60910_().m_60966_().m_60918_(SoundType.f_56740_).m_222979_(OffsetType.XYZ).m_278166_(PushReaction.DESTROY)), true, LootTableType.EMPTY);
    public static final RegistryObject<Block> CHORUS_FERN_SPROUT = register("chorus_fern_sprout", ChorusFernBlock::new, true, LootTableType.EMPTY);
    public static final RegistryObject<Block> CHORUS_FERN = register("chorus_fern", ChorusFernBlock::new, true, LootTableType.EMPTY);
    public static final RegistryObject<Block> LARGE_CHORUS_FERN = register("large_chorus_fern", LargeChorusFernBlock::new, true, LootTableType.EMPTY);
    public static final RegistryObject<Block> POTTED_CHORUS_STALK = register("potted_chorus_stalk", () -> new FlowerPotBlock(() -> (FlowerPotBlock)ForgeRegistries.BLOCKS.getDelegateOrThrow(Blocks.f_50276_).get(), CHORUS_STALK, Properties.m_284310_().m_278166_(PushReaction.DESTROY).m_60953_((l) -> 4).m_60955_().m_60966_()), false, LootTableType.EMPTY);
    public static final RegistryObject<Block> POTTED_CHORUS_FERN = register("potted_chorus_fern", () -> new FlowerPotBlock(() -> (FlowerPotBlock)ForgeRegistries.BLOCKS.getDelegateOrThrow(Blocks.f_50276_).get(), CHORUS_FERN, Properties.m_284310_().m_278166_(PushReaction.DESTROY).m_60955_().m_60966_()), false, LootTableType.EMPTY);
    public static final RegistryObject<Block> AWAKENED_EMERALD_BLOCK = register("awakened_emerald_block", () -> new Block(Properties.m_284310_().m_284180_(MapColor.f_283812_).m_280658_(NoteBlockInstrument.BIT).m_60999_().m_60913_(5.0F, 6.0F).m_60918_(SoundType.f_56743_)));
    public static final RegistryObject<Block> CURSED_METAL_BLOCK = register("cursed_metal_block", ModBlocks.CursedMetalBlock::new);
    public static final RegistryObject<Block> PALE_STEEL_BLOCK = register("pale_steel_block", ModBlocks.PaleSteelBlock::new);
    public static final RegistryObject<Block> DARK_ALLOY_BLOCK = register("dark_metal_block", ModBlocks.DarkMetalBlock::new);
    public static final RegistryObject<Block> HAUNTED_GLASS = register("haunted_glass", () -> new HauntedGlassBlock(glassProperties(), true, false));
    public static final RegistryObject<Block> TINTED_HAUNTED_GLASS = register("haunted_glass_tinted", () -> new HauntedGlassBlock(tintedGlassProperties(), true, true));
    public static final RegistryObject<Block> HAUNTED_GLASS_MOB = register("haunted_glass_mob", () -> new HauntedGlassBlock(glassProperties(), false, false));
    public static final RegistryObject<Block> TINTED_HAUNTED_GLASS_MOB = register("haunted_glass_mob_tinted", () -> new HauntedGlassBlock(tintedGlassProperties(), false, true));
    public static final RegistryObject<Block> FREEZING_LAMP = register("freezing_lamp", FreezeLampBlock::new);
    public static final RegistryObject<Block> SHADE_BRAZIER = register("shade_brazier", BrazierBlock::new);
    public static final RegistryObject<Block> SHADE_SOUL_BRAZIER = register("shade_soul_brazier", () -> new BrazierBlock(true));
    public static final RegistryObject<Block> STONE_BRAZIER = register("stone_brazier", BrazierBlock::new);
    public static final RegistryObject<Block> STONE_SOUL_BRAZIER = register("stone_soul_brazier", () -> new BrazierBlock(true));
    public static final RegistryObject<Block> BRICK_BRAZIER = register("brick_brazier", BrazierBlock::new);
    public static final RegistryObject<Block> BRICK_SOUL_BRAZIER = register("brick_soul_brazier", () -> new BrazierBlock(true));
    public static final RegistryObject<Block> DEEPSLATE_BRAZIER = register("deepslate_brazier", BrazierBlock::new);
    public static final RegistryObject<Block> DEEPSLATE_SOUL_BRAZIER = register("deepslate_soul_brazier", () -> new BrazierBlock(true));
    public static final RegistryObject<Block> OMINOUS_STONE_BRAZIER = register("ominous_stone_brazier", BrazierBlock::new);
    public static final RegistryObject<Block> OMINOUS_STONE_SOUL_BRAZIER = register("ominous_stone_soul_brazier", () -> new BrazierBlock(true));
    public static final RegistryObject<Block> NETHER_BRICK_BRAZIER = register("nether_brick_brazier", BrazierBlock::new);
    public static final RegistryObject<Block> NETHER_BRICK_SOUL_BRAZIER = register("nether_brick_soul_brazier", () -> new BrazierBlock(true));
    public static final RegistryObject<Block> BLACKSTONE_BRAZIER = register("blackstone_brazier", BrazierBlock::new);
    public static final RegistryObject<Block> BLACKSTONE_SOUL_BRAZIER = register("blackstone_soul_brazier", () -> new BrazierBlock(true));
    public static final RegistryObject<Block> MARBLE_BRAZIER = register("marble_brazier", BrazierBlock::new);
    public static final RegistryObject<Block> MARBLE_SOUL_BRAZIER = register("marble_soul_brazier", () -> new BrazierBlock(true));
    public static final RegistryObject<Block> IRON_DUNGEON_TORCH = register("iron_dungeon_torch", () -> new DungeonTorchBlock(Properties.m_284310_().m_60910_().m_60966_().m_60953_((state) -> state.m_61138_(BlockStateProperties.f_61443_) && state.m_61143_(BlockStateProperties.f_61443_) ? 14 : 0).m_60918_(SoundType.f_56743_).m_278166_(PushReaction.DESTROY)), false);
    public static final RegistryObject<Block> WALL_IRON_DUNGEON_TORCH = register("wall_iron_dungeon_torch", () -> new WallDungeonTorchBlock(Properties.m_284310_().m_60910_().m_60966_().m_60953_((state) -> state.m_61138_(BlockStateProperties.f_61443_) && state.m_61143_(BlockStateProperties.f_61443_) ? 14 : 0).m_60918_(SoundType.f_56743_).m_278166_(PushReaction.DESTROY)), false);
    public static final RegistryObject<Block> GOLD_DUNGEON_TORCH = register("gold_dungeon_torch", () -> new DungeonTorchBlock(Properties.m_284310_().m_60910_().m_60966_().m_60953_((state) -> state.m_61138_(BlockStateProperties.f_61443_) && state.m_61143_(BlockStateProperties.f_61443_) ? 14 : 0).m_60918_(SoundType.f_56743_).m_284180_(MapColor.f_283757_).m_278166_(PushReaction.DESTROY)), false);
    public static final RegistryObject<Block> WALL_GOLD_DUNGEON_TORCH = register("wall_gold_dungeon_torch", () -> new WallDungeonTorchBlock(Properties.m_284310_().m_60910_().m_60966_().m_60953_((state) -> state.m_61138_(BlockStateProperties.f_61443_) && state.m_61143_(BlockStateProperties.f_61443_) ? 14 : 0).m_60918_(SoundType.f_56743_).m_284180_(MapColor.f_283757_).m_278166_(PushReaction.DESTROY)), false);
    public static final RegistryObject<Block> GOLD_CANDLESTICK = register("gold_candlestick", () -> new CandlestickBlock(Properties.m_284310_().m_60918_(SoundType.f_56743_).m_60966_().m_284180_(MapColor.f_283757_).m_278166_(PushReaction.DESTROY), 6), false);
    public static final RegistryObject<Block> WALL_GOLD_CANDLESTICK = register("wall_gold_candlestick", () -> new WallCandlestickBlock(Properties.m_284310_().m_60918_(SoundType.f_56743_).m_60966_().m_284180_(MapColor.f_283757_).m_278166_(PushReaction.DESTROY), 6), false);
    public static final RegistryObject<Block> GOLD_CANDELABRA = register("gold_candelabra", () -> new CandelabraBlock(Properties.m_284310_().m_60918_(SoundType.f_56743_).m_60913_(3.0F, 6.0F).m_284180_(MapColor.f_283757_).m_278166_(PushReaction.DESTROY), 14));
    public static final RegistryObject<Block> GOLD_CHANDELIER = register("gold_chandelier", () -> new ChandelierBlock(Properties.m_284310_().m_60918_(SoundType.f_56743_).m_60913_(3.0F, 6.0F).m_284180_(MapColor.f_283757_).m_278166_(PushReaction.DESTROY), 14));
    public static final RegistryObject<Block> STEEP_SCONCE = register("steep_sconce", SteepSconceBlock::new);
    public static final RegistryObject<Block> JADE_LIGHT = register("jade_light", JadeLightBlock::new);
    public static final RegistryObject<Block> PINE_LANTERN = register("pine_lantern", PineLanternBlock::new);
    public static final RegistryObject<Block> JADE_CRYSTAL_LAMP = register("jade_crystal_lamp", JadeCrystalLamp::new);
    public static final RegistryObject<Block> HALF_JADE_CRYSTAL_LAMP = register("half_jade_crystal_lamp", HalfJadeCrystalLamp::new);
    public static final RegistryObject<Block> NECROTIC_GOLD_CANDLESTICK = register("necrotic_gold_candlestick", () -> new NecroticCandlestick(Properties.m_284310_().m_60918_(SoundType.f_56743_).m_60966_().m_284180_(MapColor.f_283757_).m_278166_(PushReaction.DESTROY)), false);
    public static final RegistryObject<Block> WALL_NECROTIC_GOLD_CANDLESTICK = register("wall_necrotic_gold_candlestick", () -> new WallNecroticCandlestick(Properties.m_284310_().m_60918_(SoundType.f_56743_).m_60966_().m_284180_(MapColor.f_283757_).m_278166_(PushReaction.DESTROY)), false);
    public static final RegistryObject<Block> IRON_DUNGEON_CHAIN = register("iron_dungeon_chain", () -> new ChainBlock(Properties.m_284310_().m_280606_().m_60999_().m_60913_(5.0F, 6.0F).m_60918_(SoundType.f_56728_).m_284180_(MapColor.f_283947_).m_60955_()));
    public static final RegistryObject<Block> GOLD_DUNGEON_CHAIN = register("gold_dungeon_chain", () -> new ChainBlock(Properties.m_284310_().m_280606_().m_60999_().m_60913_(5.0F, 6.0F).m_60918_(SoundType.f_56728_).m_284180_(MapColor.f_283757_).m_60955_()));
    public static final RegistryObject<Block> SKULL_PILE = register("skull_pile", () -> new Block(Properties.m_284310_().m_284180_(MapColor.f_283761_).m_60978_(2.0F).m_60918_(SoundType.f_56724_).m_280658_(NoteBlockInstrument.BASEDRUM)), true, LootTableType.EMPTY);
    public static final RegistryObject<Block> CRYPT_URN = register("crypt_urn", UrnBlock::new, true, LootTableType.EMPTY);
    public static final RegistryObject<CryptChestBlock> CRYPT_CHEST = isterRegister("crypt_chest", CryptChestBlock::new, LootTableType.EMPTY);
    public static final RegistryObject<LoftyChestBlock> LOFTY_CHEST = isterRegister("lofty_chest", () -> new LoftyChestBlock(Properties.m_284310_().m_284180_(MapColor.f_283927_).m_280658_(NoteBlockInstrument.BASS).m_60913_(5.0F, 3600000.0F).m_60918_(SoundType.f_56721_).m_60953_((light) -> 6)), LootTableType.EMPTY);
    public static final RegistryObject<Block> SPIDER_SAC = register("spider_sac", SpiderSacBlock::new, true, LootTableType.EMPTY);
    public static final RegistryObject<Block> STASH_URN = register("stash_urn", StashUrnBlock::new, true, LootTableType.EMPTY);
    public static final RegistryObject<Block> SOUL_LIGHT_BLOCK = register("soul_light", SoulLightBlock::new, false, LootTableType.EMPTY);
    public static final RegistryObject<Block> GLOW_LIGHT_BLOCK = register("glow_light", GlowLightBlock::new, false, LootTableType.EMPTY);
    public static final RegistryObject<Block> WALL_SHRINE = register("wall_shrine", WallShrineBlock::new);
    public static final RegistryObject<Block> MANDALA = register("mandala", MandalaBlock::new);
    public static final RegistryObject<Block> OMINOUS_STATUE = register("ominous_statue", OminousStatueBlock::new);
    public static final RegistryObject<Block> OMINOUS_BRAZIER_STATUE = register("ominous_brazier_statue", OminousBrazierStatueBlock::new);
    public static final RegistryObject<Block> DIAMOND_MOLD_BLOCK = register("diamond_mold_block", () -> new Block(Properties.m_60926_(Blocks.f_50090_)));
    public static final RegistryObject<Block> REINFORCED_REDSTONE_BLOCK = register("reinforced_redstone_block", () -> new PoweredBlock(Properties.m_284310_().m_284180_(MapColor.f_283816_).m_60999_().m_60913_(50.0F, 1200.0F).m_60918_(SoundType.f_56743_).m_60924_(ModBlocks::never)));
    public static final RegistryObject<Block> ROYAL_CARPET = register("royal_carpet", () -> new WoolCarpetBlock(DyeColor.RED, Properties.m_60926_(Blocks.f_50350_)));
    public static final RegistryObject<Block> ROYAL_CARPET_CORNER = register("royal_carpet_corner", () -> new FancyCarpetBlock(DyeColor.RED, Properties.m_60926_(Blocks.f_50350_)));
    public static final RegistryObject<Block> ROYAL_CARPET_EDGE = register("royal_carpet_edge", () -> new FancyCarpetBlock(DyeColor.RED, Properties.m_60926_(Blocks.f_50350_)));
    public static final RegistryObject<Block> ROYAL_CARPET_INNER_CORNER = register("royal_carpet_inner_corner", () -> new FancyCarpetBlock(DyeColor.RED, Properties.m_60926_(Blocks.f_50350_)));
    public static final RegistryObject<Block> FROSTY_CARPET = register("frosty_carpet", () -> new WoolCarpetBlock(DyeColor.BLUE, Properties.m_60926_(Blocks.f_50347_)));
    public static final RegistryObject<Block> FROSTY_CARPET_CORNER = register("frosty_carpet_corner", () -> new FancyCarpetBlock(DyeColor.BLUE, Properties.m_60926_(Blocks.f_50347_)));
    public static final RegistryObject<Block> FROSTY_CARPET_EDGE = register("frosty_carpet_edge", () -> new FancyCarpetBlock(DyeColor.BLUE, Properties.m_60926_(Blocks.f_50347_)));
    public static final RegistryObject<Block> FROSTY_CARPET_INNER_CORNER = register("frosty_carpet_inner_corner", () -> new FancyCarpetBlock(DyeColor.BLUE, Properties.m_60926_(Blocks.f_50347_)));
    public static final RegistryObject<Block> SHADE_THRONE = enchantedRegister("shade_throne", () -> new StoneThroneBlock(ShadeStoneProperties().m_60955_()));
    public static final RegistryObject<Block> STONE_THRONE = enchantedRegister("stone_throne", () -> new StoneThroneBlock(Properties.m_60926_(Blocks.f_50069_).m_60955_()));
    public static final RegistryObject<Block> DEEPSLATE_THRONE = enchantedRegister("deepslate_throne", () -> new StoneThroneBlock(Properties.m_60926_(Blocks.f_152589_).m_60955_()));
    public static final RegistryObject<Block> OMINOUS_THRONE = enchantedRegister("ominous_throne", () -> new StoneThroneBlock(OminousStoneProperties().m_60955_()));
    public static final RegistryObject<Block> BLACKSTONE_THRONE = enchantedRegister("blackstone_throne", () -> new StoneThroneBlock(Properties.m_60926_(Blocks.f_50734_).m_60955_()));
    public static final RegistryObject<Block> HIGHROCK_THRONE = enchantedRegister("highrock_throne", () -> new StoneThroneBlock(HighrockProperties().m_60955_()));
    public static final RegistryObject<Block> MARBLE_THRONE = enchantedRegister("marble_throne", () -> new StoneThroneBlock(MarbleProperties().m_60955_()));
    public static final RegistryObject<Block> ROYAL_THRONE = enchantedRegister("royal_throne", () -> new RoyalThroneBlock(Properties.m_284310_().m_280658_(NoteBlockInstrument.BASS).m_60913_(2.0F, 3.0F).m_60918_(SoundType.f_56736_).m_284180_(MapColor.f_283913_).m_60955_()));
    public static final RegistryObject<Block> FROSTED_THRONE = enchantedRegister("frosted_throne", () -> new RoyalThroneBlock(Properties.m_284310_().m_280658_(NoteBlockInstrument.BASS).m_60913_(2.0F, 3.0F).m_60918_(SoundType.f_56736_).m_284180_(MapColor.f_283743_).m_60955_()));
    public static final RegistryObject<Block> CREEPER_TOTEM = register("creeper_totem", () -> new ToweringBlock(Properties.m_60926_(Blocks.f_50069_)));
    public static final RegistryObject<Block> COBBLED_DIRT = register("cobbled_dirt", () -> new Block(Properties.m_284310_().m_284180_(MapColor.f_283762_).m_60918_(SoundType.f_56718_).m_60999_().m_60978_(1.5F)), true, LootTableType.EMPTY);
    public static final RegistryObject<Block> SNOWY_DIRT = register("snowy_dirt", () -> new Block(Properties.m_60926_(Blocks.f_50493_)), true, LootTableType.EMPTY);
    public static final RegistryObject<Block> DARK_DIRT = register("dark_dirt", () -> new Block(Properties.m_60926_(Blocks.f_50493_).m_284180_(MapColor.f_283771_)));
    public static final RegistryObject<Block> COBBLED_DARK_DIRT = register("cobbled_dark_dirt", () -> new Block(Properties.m_284310_().m_284180_(MapColor.f_283771_).m_60918_(SoundType.f_56718_).m_60999_().m_60978_(1.5F)), true, LootTableType.EMPTY);
    public static final RegistryObject<Block> SNOWY_DARK_DIRT = register("snowy_dark_dirt", () -> new Block(Properties.m_60926_((BlockBehaviour)DARK_DIRT.get())), true, LootTableType.EMPTY);
    public static final RegistryObject<Block> GRAVE_SOIL = register("grave_soil", () -> new GraveSoilBlock(Properties.m_60926_((BlockBehaviour)DARK_DIRT.get()).m_60918_(SoundType.f_56717_)));
    public static final RegistryObject<Block> DETRITUS = register("detritus", () -> new Block(Properties.m_284310_().m_284180_(MapColor.f_283774_).m_60978_(1.25F).m_60918_(SoundType.f_56746_)));
    public static final RegistryObject<Block> DETRITUS_DUST = register("detritus_dust", () -> new LayerBlock(Properties.m_60926_((BlockBehaviour)DETRITUS.get()), true), true, LootTableType.EMPTY);
    public static final RegistryObject<Block> SOILED_SPRUCE_PLANKS = register("soiled_spruce_planks", () -> new Block(Properties.m_60926_(Blocks.f_50741_)));
    public static final RegistryObject<Block> SOILED_SPRUCE_PLANKS_HEAVY = register("soiled_spruce_planks_heavy", () -> new Block(Properties.m_60926_(Blocks.f_50741_)));
    public static final RegistryObject<Block> VOID_BLOCK = register("void_block", VoidBlock::new);
    public static final RegistryObject<Block> VOID_MINOR_SPREAD = register("void_minor_spread", VoidSpreadBlock::new);
    public static final RegistryObject<Block> VOID_MAJOR_SPREAD = register("void_major_spread", VoidSpreadBlock::new);
    public static final RegistryObject<Block> VOID_FLAME = register("void_flame", VoidFlameBlock::new, false, LootTableType.EMPTY);
    public static final RegistryObject<LiquidBlock> VOID_FLUID = register("void_fluid", VoidFluidBlock::new, false, LootTableType.EMPTY);
    public static final RegistryObject<Block> VOID_BARREL = register("void_barrel", VoidBarrelBlock::new, true, LootTableType.EMPTY);
    public static final RegistryObject<Block> VOID_CAULDRON = register("void_cauldron", () -> new VoidCauldronBlock(Properties.m_60926_(Blocks.f_50256_).m_60991_((state, world, pos) -> true).m_60953_((state) -> 1)), false, LootTableType.EMPTY);
    public static final RegistryObject<Block> END_BASALT = register("end_basalt", () -> new RotatedPillarBlock(Properties.m_284310_().m_284180_(MapColor.f_283889_).m_60999_().m_60913_(3.0F, 9.0F).m_60918_(SoundType.f_56718_)));
    public static final RegistryObject<Block> END_BASALT_STAIRS = registerStairs("end_basalt_stairs", END_BASALT);
    public static final RegistryObject<Block> END_BASALT_SLAB = registerSlabs("end_basalt_slab", END_BASALT);
    public static final RegistryObject<Block> SOILED_END_BASALT_SLIGHT = register("soiled_end_basalt_slight", () -> new Block(Properties.m_60926_((BlockBehaviour)END_BASALT.get())));
    public static final RegistryObject<Block> SOILED_END_BASALT = register("soiled_end_basalt", () -> new Block(Properties.m_60926_((BlockBehaviour)END_BASALT.get())));
    public static final RegistryObject<Block> TOP_SOILED_END_BASALT = register("top_soiled_end_basalt", () -> new Block(Properties.m_60926_((BlockBehaviour)END_BASALT.get()).m_284180_(MapColor.f_283818_)));
    public static final RegistryObject<Block> BOTTOM_SOILED_END_BASALT = register("bottom_soiled_end_basalt", () -> new Block(Properties.m_60926_((BlockBehaviour)END_BASALT.get())));
    public static final RegistryObject<Block> TOP_DIRTY_END_BASALT = register("top_dirty_end_basalt", () -> new Block(Properties.m_60926_((BlockBehaviour)END_BASALT.get()).m_284180_(MapColor.f_283889_)));
    public static final RegistryObject<Block> BOTTOM_DIRTY_END_BASALT = register("bottom_dirty_end_basalt", () -> new Block(Properties.m_60926_((BlockBehaviour)END_BASALT.get())));
    public static final RegistryObject<Block> GRASSY_END_BASALT = register("grassy_end_basalt", () -> new Block(Properties.m_60926_((BlockBehaviour)END_BASALT.get())));
    public static final RegistryObject<Block> DIRTY_END_BASALT = register("dirty_end_basalt", () -> new Block(Properties.m_60926_((BlockBehaviour)END_BASALT.get())));
    public static final RegistryObject<Block> END_BASALT_BRICKS = register("end_basalt_bricks", () -> new Block(Properties.m_284310_().m_284180_(MapColor.f_283889_).m_60999_().m_60913_(3.0F, 9.0F).m_60918_(SoundType.f_56721_)));
    public static final RegistryObject<Block> END_BASALT_BRICK_STAIRS = registerStairs("end_basalt_brick_stairs", END_BASALT_BRICKS);
    public static final RegistryObject<Block> END_BASALT_BRICK_SLAB = registerSlabs("end_basalt_brick_slab", END_BASALT_BRICKS);
    public static final RegistryObject<Block> SOILED_END_BASALT_BRICKS = register("soiled_end_basalt_bricks", () -> new Block(Properties.m_60926_((BlockBehaviour)END_BASALT_BRICKS.get())));
    public static final RegistryObject<Block> SMOOTH_END_BASALT_BRICKS = register("smooth_end_basalt_bricks", () -> new Block(Properties.m_60926_((BlockBehaviour)END_BASALT_BRICKS.get())));
    public static final RegistryObject<Block> END_ROCK = register("end_rock", () -> new RotatedPillarBlock(Properties.m_284310_().m_284180_(MapColor.f_283856_).m_60913_(3.0F, 9.0F).m_60918_(SoundType.f_56742_)));
    public static final RegistryObject<Block> END_ROCK_STAIRS = registerStairs("end_rock_stairs", END_ROCK);
    public static final RegistryObject<Block> END_ROCK_SLAB = registerSlabs("end_rock_slab", END_ROCK);
    public static final RegistryObject<Block> SOILED_END_ROCK = register("soiled_end_rock", () -> new Block(Properties.m_60926_((BlockBehaviour)END_ROCK.get())));
    public static final RegistryObject<Block> GRASSY_END_ROCK = register("grassy_end_rock", () -> new Block(Properties.m_60926_((BlockBehaviour)END_ROCK.get())));
    public static final RegistryObject<Block> GROWN_END_ROCK = register("grown_end_rock", () -> new Block(Properties.m_60926_((BlockBehaviour)END_ROCK.get())));
    public static final RegistryObject<Block> DIRTY_END_ROCK = register("dirty_end_rock", () -> new Block(Properties.m_60926_((BlockBehaviour)END_ROCK.get())));
    public static final RegistryObject<Block> END_ROCK_SLATE = register("end_rock_slate", () -> new Block(Properties.m_60926_((BlockBehaviour)END_ROCK.get())));
    public static final RegistryObject<Block> END_ROCK_BRICKS = register("end_rock_bricks", () -> new RotatedPillarBlock(Properties.m_284310_().m_284180_(MapColor.f_283856_).m_60913_(3.0F, 9.0F).m_60918_(SoundType.f_56742_)));
    public static final RegistryObject<Block> END_ROCK_BRICK_STAIRS = registerStairs("end_rock_brick_stairs", END_ROCK_BRICKS);
    public static final RegistryObject<Block> END_ROCK_BRICK_SLAB = registerSlabs("end_rock_brick_slab", END_ROCK_BRICKS);
    public static final RegistryObject<Block> END_ROCK_BRICK_WALL_BLOCK = registerWalls("end_rock_brick_wall", END_ROCK_BRICKS);
    public static final RegistryObject<Block> END_ROCK_BRICK_FENCE = register("end_rock_brick_fence", () -> new FenceBlock(Properties.m_60926_((BlockBehaviour)END_ROCK_BRICKS.get())));
    public static final RegistryObject<Block> SOILED_END_ROCK_BRICKS = register("soiled_end_rock_bricks", () -> new RotatedPillarBlock(Properties.m_60926_((BlockBehaviour)END_ROCK_BRICKS.get())));
    public static final RegistryObject<Block> GRASSY_END_ROCK_BRICKS = register("grassy_end_rock_bricks", () -> new RotatedPillarBlock(Properties.m_60926_((BlockBehaviour)END_ROCK_BRICKS.get())));
    public static final RegistryObject<Block> END_ROCK_CHISELED = register("end_rock_chiseled", () -> new Block(Properties.m_284310_().m_284180_(MapColor.f_283856_).m_60913_(3.0F, 9.0F).m_60918_(SoundType.f_56742_)));
    public static final RegistryObject<Block> SOILED_END_ROCK_CHISELED = register("soiled_end_rock_chiseled", () -> new Block(Properties.m_60926_((BlockBehaviour)END_ROCK_CHISELED.get())));
    public static final RegistryObject<Block> END_GROWTH_BLOCK = register("end_growth_block", EndGrowthBlock::new, true, LootTableType.EMPTY);
    public static final RegistryObject<Block> END_GROWTH_VINES = register("end_growth_vines", () -> new EndGrowthVinesBlock(Properties.m_284310_().m_284180_(MapColor.f_283889_).m_60977_().m_60910_().m_60966_().m_60918_(SoundType.f_56715_).m_278166_(PushReaction.DESTROY)), true, LootTableType.EMPTY);
    public static final RegistryObject<Block> END_GROWTH_VINES_PLANT = register("end_growth_vines_plant", () -> new EndGrowthVinesPlantBlock(Properties.m_284310_().m_284180_(MapColor.f_283889_).m_60977_().m_60910_().m_60966_().m_60918_(SoundType.f_56715_).m_278166_(PushReaction.DESTROY)), false, LootTableType.EMPTY);
    public static final RegistryObject<Block> END_SOIL = register("end_soil", EndSoilBlock::new);
    public static final RegistryObject<Block> END_SOIL_DEBRIS = register("end_soil_debris", () -> new LayerBlock(Properties.m_60926_((BlockBehaviour)END_SOIL.get()), true), true, LootTableType.EMPTY);
    public static final RegistryObject<Block> END_MUD = register("end_mud", () -> new EndBonemealableBlock(Properties.m_284310_().m_284180_(MapColor.f_283779_).m_60978_(1.25F).m_60918_(SoundType.f_222469_)));
    public static final RegistryObject<Block> END_MUD_SLAB = registerSlabs("end_mud_slab", END_MUD);
    public static final RegistryObject<LiquidBlock> END_MUD_FLUID = register("end_mud_fluid", EndMudFluidBlock::new, false, LootTableType.EMPTY);
    public static final RegistryObject<Block> END_MUD_CAULDRON = register("end_mud_cauldron", () -> new EndMudCauldronBlock(Properties.m_60926_(Blocks.f_50256_)), false, LootTableType.EMPTY);
    public static final RegistryObject<Block> END_DIRT = register("end_dirt", () -> new EndBonemealableBlock(Properties.m_284310_().m_284180_(MapColor.f_283889_).m_60978_(1.25F).m_60918_(SoundType.f_56739_)));
    public static final RegistryObject<Block> END_DIRT_SLAB = registerSlabs("end_dirt_slab", END_DIRT);
    public static final RegistryObject<Block> SOILED_END_DIRT = register("soiled_end_dirt", () -> new Block(Properties.m_60926_((BlockBehaviour)END_DIRT.get())));
    public static final RegistryObject<Block> JADE_ORE = register("jade_ore", ModBlocks.StoneOreBlock::new, true, LootTableType.EMPTY);
    public static final RegistryObject<Block> JADE_TILES = register("jade_tiles", ModBlocks.JadeStoneBlock::new);
    public static final RegistryObject<Block> JADE_BLOCK = register("jade_block", () -> new Block(Properties.m_284310_().m_284180_(MapColor.f_283772_).m_60999_().m_60913_(5.0F, 6.0F).m_60918_(SoundType.f_56743_)));
    public static final RegistryObject<Block> JADE_PILLAR = register("jade_pillar", () -> new RotatedPillarBlock(JadeStoneProperties()));
    public static final RegistryObject<Block> JADE_STAIRS = registerStairs("jade_stairs", JADE_TILES);
    public static final RegistryObject<Block> JADE_SLAB = registerSlabs("jade_slab", JADE_TILES);
    public static final RegistryObject<Block> SILT_JADE_TILES = register("silt_jade_tiles", ModBlocks.JadeStoneBlock::new);
    public static final RegistryObject<Block> SILT_JADE_STAIRS = registerStairs("silt_jade_stairs", SILT_JADE_TILES);
    public static final RegistryObject<Block> SILT_JADE_SLAB = registerSlabs("silt_jade_slab", SILT_JADE_TILES);
    public static final RegistryObject<Block> SNOWY_JADE_TILES = register("snowy_jade_tiles", ModBlocks.JadeStoneBlock::new);
    public static final RegistryObject<Block> SNOWY_JADE_STAIRS = registerStairs("snowy_jade_stairs", SNOWY_JADE_TILES);
    public static final RegistryObject<Block> SNOWY_JADE_SLAB = registerSlabs("snowy_jade_slab", SNOWY_JADE_TILES);
    public static final RegistryObject<Block> RUSTY_IRON_GRATE = register("rusty_iron_grate", () -> new HalfTransparentBlock(Properties.m_284310_().m_284180_(MapColor.f_283819_).m_60999_().m_60913_(5.0F, 6.0F).m_60955_().m_60988_().m_60918_(ModSoundTypes.MOD_METAL)));
    public static final RegistryObject<Block> HAUNTED_PLANKS = register("haunted_planks", () -> new Block(Properties.m_284310_().m_284180_(MapColor.f_283818_).m_280658_(NoteBlockInstrument.BASS).m_60913_(2.0F, 3.0F).m_60918_(SoundType.f_56736_)));
    public static final RegistryObject<Block> HAUNTED_LOG = register("haunted_log", () -> fireProofLog(MapColor.f_283818_));
    public static final RegistryObject<Block> STRIPPED_HAUNTED_LOG = register("stripped_haunted_log", () -> fireProofLog(MapColor.f_283818_));
    public static final RegistryObject<Block> HAUNTED_WOOD = register("haunted_wood", () -> new RotatedPillarBlock(Properties.m_284310_().m_284180_(MapColor.f_283818_).m_280658_(NoteBlockInstrument.BASS).m_60978_(2.0F).m_60918_(SoundType.f_56736_)));
    public static final RegistryObject<Block> STRIPPED_HAUNTED_WOOD = register("stripped_haunted_wood", () -> new RotatedPillarBlock(Properties.m_284310_().m_284180_(MapColor.f_283818_).m_280658_(NoteBlockInstrument.BASS).m_60978_(2.0F).m_60918_(SoundType.f_56736_)));
    public static final RegistryObject<Block> HAUNTED_PRESSURE_PLATE = register("haunted_pressure_plate", () -> new PressurePlateBlock(Sensitivity.EVERYTHING, Properties.m_284310_().m_284180_(((Block)HAUNTED_PLANKS.get()).m_284356_()).m_280606_().m_280658_(NoteBlockInstrument.BASS).m_60910_().m_60978_(0.5F).m_278166_(PushReaction.DESTROY), ModBlockSetType.HAUNTED));
    public static final RegistryObject<Block> HAUNTED_TRAPDOOR = register("haunted_trapdoor", () -> new TrapDoorBlock(Properties.m_284310_().m_284180_(MapColor.f_283818_).m_280658_(NoteBlockInstrument.BASS).m_60978_(3.0F).m_60918_(SoundType.f_56736_).m_60955_(), ModBlockSetType.HAUNTED));
    public static final RegistryObject<Block> HAUNTED_BUTTON = register("haunted_button", () -> woodenButton(ModBlockSetType.HAUNTED));
    public static final RegistryObject<Block> HAUNTED_STAIRS = registerStairs("haunted_stairs", HAUNTED_PLANKS);
    public static final RegistryObject<Block> HAUNTED_SLAB = registerSlabs("haunted_slab", HAUNTED_PLANKS);
    public static final RegistryObject<Block> HAUNTED_FENCE_GATE = register("haunted_fence_gate", () -> new FenceGateBlock(Properties.m_284310_().m_284180_(((Block)HAUNTED_PLANKS.get()).m_284356_()).m_280658_(NoteBlockInstrument.BASS).m_60913_(2.0F, 3.0F).m_60918_(SoundType.f_56736_), ModWoodType.HAUNTED));
    public static final RegistryObject<Block> HAUNTED_FENCE = register("haunted_fence", () -> new FenceBlock(Properties.m_284310_().m_284180_(((Block)HAUNTED_PLANKS.get()).m_284356_()).m_280658_(NoteBlockInstrument.BASS).m_60913_(2.0F, 3.0F).m_60918_(SoundType.f_56736_)));
    public static final RegistryObject<Block> HAUNTED_DOOR = register("haunted_door", () -> new DoorBlock(Properties.m_284310_().m_284180_(((Block)HAUNTED_PLANKS.get()).m_284356_()).m_280658_(NoteBlockInstrument.BASS).m_60978_(3.0F).m_60918_(SoundType.f_56736_).m_60955_(), ModBlockSetType.HAUNTED));
    public static final RegistryObject<Block> HAUNTED_BOOKSHELF = register("haunted_bookshelf", () -> new BookshelfBlock(Properties.m_284310_().m_284180_(((Block)HAUNTED_PLANKS.get()).m_284356_()).m_280658_(NoteBlockInstrument.BASS).m_60978_(1.5F).m_60918_(SoundType.f_56736_)));
    public static final RegistryObject<ModChestBlock> HAUNTED_CHEST = isterRegister("haunted_chest", () -> new ModChestBlock(Properties.m_284310_().m_280658_(NoteBlockInstrument.BASS).m_60978_(2.5F).m_60918_(SoundType.f_56736_)));
    public static final RegistryObject<ModTrappedChestBlock> TRAPPED_HAUNTED_CHEST = isterRegister("trapped_haunted_chest", () -> new ModTrappedChestBlock(Properties.m_284310_().m_280658_(NoteBlockInstrument.BASS).m_60978_(2.5F).m_60918_(SoundType.f_56736_)));
    public static final RegistryObject<Block> HAUNTED_SIGN = register("haunted_sign", () -> new ModStandSignBlock(Properties.m_284310_().m_284180_(((Block)HAUNTED_PLANKS.get()).m_284356_()).m_280658_(NoteBlockInstrument.BASS).m_60910_().m_60978_(1.0F).m_60918_(SoundType.f_56736_), ModWoodType.HAUNTED), false);
    public static final RegistryObject<Block> HAUNTED_WALL_SIGN = register("haunted_wall_sign", () -> new ModWallSignBlock(Properties.m_284310_().m_284180_(((Block)HAUNTED_PLANKS.get()).m_284356_()).m_280658_(NoteBlockInstrument.BASS).m_60910_().m_60978_(1.0F).m_60918_(SoundType.f_56736_).lootFrom(HAUNTED_SIGN), ModWoodType.HAUNTED), false);
    public static final RegistryObject<Block> HAUNTED_HANGING_SIGN = register("haunted_hanging_sign", () -> new ModHangingSignBlock(Properties.m_284310_().m_284180_(((Block)HAUNTED_LOG.get()).m_284356_()).m_280606_().m_280658_(NoteBlockInstrument.BASS).m_60910_().m_60978_(1.0F), ModWoodType.HAUNTED), false);
    public static final RegistryObject<Block> HAUNTED_WALL_HANGING_SIGN = register("haunted_wall_hanging_sign", () -> new ModWallHangingSignBlock(Properties.m_284310_().m_284180_(((Block)HAUNTED_LOG.get()).m_284356_()).m_280606_().m_280658_(NoteBlockInstrument.BASS).m_60910_().m_60978_(1.0F).lootFrom(HAUNTED_HANGING_SIGN), ModWoodType.HAUNTED), false);
    public static final RegistryObject<Block> HAUNTED_SAPLING = register("haunted_sapling", () -> sapling(new HauntedTree()));
    public static final RegistryObject<Block> POTTED_HAUNTED_SAPLING = register("potted_haunted_sapling", () -> new FlowerPotBlock(() -> (FlowerPotBlock)ForgeRegistries.BLOCKS.getDelegateOrThrow(Blocks.f_50276_).get(), HAUNTED_SAPLING, Properties.m_284310_().m_278166_(PushReaction.DESTROY).m_60955_().m_60966_()), false, LootTableType.DROP);
    public static final RegistryObject<Block> HAUNTED_LAMP = register("haunted_lamp", () -> new LampBlock(Properties.m_60926_((BlockBehaviour)HAUNTED_WOOD.get())));
    public static final RegistryObject<Block> DARK_PRESSURE_PLATE = register("dark_pressure_plate", () -> new DarkPressurePlateBlock(Properties.m_284310_().m_284180_(((Block)HAUNTED_PLANKS.get()).m_284356_()).m_280606_().m_280658_(NoteBlockInstrument.BASS).m_60910_().m_60978_(0.5F).m_278166_(PushReaction.DESTROY)));
    public static final RegistryObject<Block> APPARITION_DOOR = register("apparition_door", () -> new ApparitionDoorBlock(Properties.m_284310_().m_284180_(MapColor.f_283818_).m_280658_(NoteBlockInstrument.BASS).m_60913_(2.0F, 3.0F).m_60918_(SoundType.f_56736_)));
    public static final RegistryObject<Block> ROTTEN_PLANKS = register("rotten_planks", () -> new Block(Properties.m_284310_().m_284180_(MapColor.f_283784_).m_280658_(NoteBlockInstrument.BASS).m_60913_(2.0F, 3.0F).m_60918_(SoundType.f_244244_).m_278183_()));
    public static final RegistryObject<Block> ROTTEN_LOG = register("rotten_log", () -> log(MapColor.f_283784_, MapColor.f_283784_, SoundType.f_56763_));
    public static final RegistryObject<Block> STRIPPED_ROTTEN_LOG = register("stripped_rotten_log", () -> log(MapColor.f_283784_, MapColor.f_283784_, SoundType.f_56763_));
    public static final RegistryObject<Block> ROTTEN_WOOD = register("rotten_wood", () -> new RotatedPillarBlock(Properties.m_284310_().m_284180_(MapColor.f_283784_).m_280658_(NoteBlockInstrument.BASS).m_60978_(2.0F).m_60918_(SoundType.f_56763_).m_278183_()));
    public static final RegistryObject<Block> STRIPPED_ROTTEN_WOOD = register("stripped_rotten_wood", () -> new RotatedPillarBlock(Properties.m_284310_().m_284180_(MapColor.f_283784_).m_280658_(NoteBlockInstrument.BASS).m_60978_(2.0F).m_60918_(SoundType.f_56763_).m_278183_()));
    public static final RegistryObject<Block> ROTTEN_LEAVES = register("rotten_leaves", () -> leaves(SoundType.f_56740_), true, LootTableType.EMPTY);
    public static final RegistryObject<Block> ROTTEN_PRESSURE_PLATE = register("rotten_pressure_plate", () -> new PressurePlateBlock(Sensitivity.EVERYTHING, Properties.m_284310_().m_284180_(((Block)ROTTEN_PLANKS.get()).m_284356_()).m_280606_().m_280658_(NoteBlockInstrument.BASS).m_60910_().m_60978_(0.5F).m_278166_(PushReaction.DESTROY).m_278183_(), ModBlockSetType.ROTTEN));
    public static final RegistryObject<Block> ROTTEN_TRAPDOOR = register("rotten_trapdoor", () -> new TrapDoorBlock(Properties.m_284310_().m_284180_(MapColor.f_283784_).m_280658_(NoteBlockInstrument.BASS).m_60978_(3.0F).m_60918_(SoundType.f_244244_).m_278183_().m_60955_(), ModBlockSetType.ROTTEN));
    public static final RegistryObject<Block> ROTTEN_BUTTON = register("rotten_button", () -> woodenButton(ModBlockSetType.ROTTEN));
    public static final RegistryObject<Block> ROTTEN_STAIRS = registerStairs("rotten_stairs", ROTTEN_PLANKS);
    public static final RegistryObject<Block> ROTTEN_SLAB = registerSlabs("rotten_slab", ROTTEN_PLANKS);
    public static final RegistryObject<Block> ROTTEN_FENCE_GATE = register("rotten_fence_gate", () -> new FenceGateBlock(Properties.m_284310_().m_284180_(((Block)ROTTEN_PLANKS.get()).m_284356_()).m_280658_(NoteBlockInstrument.BASS).m_60913_(2.0F, 3.0F).m_60918_(SoundType.f_244244_).m_278183_(), ModWoodType.ROTTEN));
    public static final RegistryObject<Block> ROTTEN_FENCE = register("rotten_fence", () -> new FenceBlock(Properties.m_284310_().m_284180_(((Block)ROTTEN_PLANKS.get()).m_284356_()).m_280658_(NoteBlockInstrument.BASS).m_60913_(2.0F, 3.0F).m_60918_(SoundType.f_244244_).m_278183_()));
    public static final RegistryObject<Block> ROTTEN_DOOR = register("rotten_door", () -> new DoorBlock(Properties.m_284310_().m_284180_(((Block)ROTTEN_PLANKS.get()).m_284356_()).m_280658_(NoteBlockInstrument.BASS).m_60978_(3.0F).m_60918_(SoundType.f_244244_).m_278183_().m_60955_(), ModBlockSetType.ROTTEN));
    public static final RegistryObject<Block> ROTTEN_BOOKSHELF = register("rotten_bookshelf", () -> new BookshelfBlock(Properties.m_284310_().m_284180_(((Block)ROTTEN_PLANKS.get()).m_284356_()).m_280658_(NoteBlockInstrument.BASS).m_60978_(1.5F).m_60918_(SoundType.f_244244_).m_278183_()), true, LootTableType.EMPTY);
    public static final RegistryObject<ModChestBlock> ROTTEN_CHEST = isterRegister("rotten_chest", () -> new ModChestBlock(Properties.m_284310_().m_280658_(NoteBlockInstrument.BASS).m_60978_(2.5F).m_60918_(SoundType.f_244244_).m_278183_()));
    public static final RegistryObject<ModTrappedChestBlock> TRAPPED_ROTTEN_CHEST = isterRegister("trapped_rotten_chest", () -> new ModTrappedChestBlock(Properties.m_284310_().m_280658_(NoteBlockInstrument.BASS).m_60978_(2.5F).m_60918_(SoundType.f_244244_).m_278183_()));
    public static final RegistryObject<Block> ROTTEN_SIGN = register("rotten_sign", () -> new ModStandSignBlock(Properties.m_284310_().m_284180_(((Block)ROTTEN_PLANKS.get()).m_284356_()).m_280658_(NoteBlockInstrument.BASS).m_60910_().m_60978_(1.0F).m_60918_(SoundType.f_244244_).m_278183_(), ModWoodType.ROTTEN), false);
    public static final RegistryObject<Block> ROTTEN_WALL_SIGN = register("rotten_wall_sign", () -> new ModWallSignBlock(Properties.m_284310_().m_284180_(((Block)ROTTEN_PLANKS.get()).m_284356_()).m_280658_(NoteBlockInstrument.BASS).m_60910_().m_60978_(1.0F).m_60918_(SoundType.f_244244_).lootFrom(ROTTEN_SIGN).m_278183_(), ModWoodType.ROTTEN), false);
    public static final RegistryObject<Block> ROTTEN_HANGING_SIGN = register("rotten_hanging_sign", () -> new ModHangingSignBlock(Properties.m_284310_().m_284180_(((Block)ROTTEN_LOG.get()).m_284356_()).m_280606_().m_280658_(NoteBlockInstrument.BASS).m_60910_().m_60978_(1.0F).m_278183_(), ModWoodType.ROTTEN), false);
    public static final RegistryObject<Block> ROTTEN_WALL_HANGING_SIGN = register("rotten_wall_hanging_sign", () -> new ModWallHangingSignBlock(Properties.m_284310_().m_284180_(((Block)ROTTEN_LOG.get()).m_284356_()).m_280606_().m_280658_(NoteBlockInstrument.BASS).m_60910_().m_60978_(1.0F).lootFrom(ROTTEN_HANGING_SIGN).m_278183_(), ModWoodType.ROTTEN), false);
    public static final RegistryObject<Block> ROTTEN_SAPLING = register("rotten_sapling", () -> sapling(new RottenTree()));
    public static final RegistryObject<Block> POTTED_ROTTEN_SAPLING = register("potted_rotten_sapling", () -> new FlowerPotBlock(() -> (FlowerPotBlock)ForgeRegistries.BLOCKS.getDelegateOrThrow(Blocks.f_50276_).get(), ROTTEN_SAPLING, Properties.m_284310_().m_278166_(PushReaction.DESTROY).m_60955_().m_60966_()), false, LootTableType.EMPTY);
    public static final RegistryObject<Block> WINDSWEPT_PLANKS = register("windswept_planks", () -> new Block(Properties.m_284310_().m_284180_(MapColor.f_283832_).m_280658_(NoteBlockInstrument.BASS).m_60913_(2.0F, 3.0F).m_60918_(SoundType.f_56736_).m_278183_()));
    public static final RegistryObject<Block> COMPACTED_WINDSWEPT_PLANKS = register("compacted_windswept_planks", () -> new RotatedPillarBlock(Properties.m_284310_().m_284180_(MapColor.f_283832_).m_280658_(NoteBlockInstrument.BASS).m_60913_(2.0F, 3.0F).m_60918_(SoundType.f_56736_).m_278183_()));
    public static final RegistryObject<Block> COMPACTED_WINDSWEPT_SLAB = registerSlabs("compacted_windswept_slab", COMPACTED_WINDSWEPT_PLANKS);
    public static final RegistryObject<Block> WINDSWEPT_LOG = register("windswept_log", () -> log(MapColor.f_283748_, MapColor.f_283748_));
    public static final RegistryObject<Block> STRIPPED_WINDSWEPT_LOG = register("stripped_windswept_log", () -> log(MapColor.f_283748_, MapColor.f_283748_));
    public static final RegistryObject<Block> WINDSWEPT_WOOD = register("windswept_wood", () -> new RotatedPillarBlock(Properties.m_284310_().m_284180_(MapColor.f_283748_).m_280658_(NoteBlockInstrument.BASS).m_60978_(2.0F).m_60918_(SoundType.f_56736_).m_278183_()));
    public static final RegistryObject<Block> STRIPPED_WINDSWEPT_WOOD = register("stripped_windswept_wood", () -> new RotatedPillarBlock(Properties.m_284310_().m_284180_(MapColor.f_283832_).m_280658_(NoteBlockInstrument.BASS).m_60978_(2.0F).m_60918_(SoundType.f_56736_).m_278183_()));
    public static final RegistryObject<Block> WINDSWEPT_LEAVES = register("windswept_leaves", () -> leaves(SoundType.f_56740_), true, LootTableType.EMPTY);
    public static final RegistryObject<Block> WINDSWEPT_PRESSURE_PLATE = register("windswept_pressure_plate", () -> new PressurePlateBlock(Sensitivity.EVERYTHING, Properties.m_284310_().m_284180_(((Block)WINDSWEPT_PLANKS.get()).m_284356_()).m_280606_().m_280658_(NoteBlockInstrument.BASS).m_60910_().m_60978_(0.5F).m_278166_(PushReaction.DESTROY).m_278183_(), ModBlockSetType.WINDSWEPT));
    public static final RegistryObject<Block> WINDSWEPT_TRAPDOOR = register("windswept_trapdoor", () -> new TrapDoorBlock(Properties.m_284310_().m_284180_(MapColor.f_283832_).m_280658_(NoteBlockInstrument.BASS).m_60978_(3.0F).m_60918_(SoundType.f_56736_).m_278183_().m_60955_(), ModBlockSetType.WINDSWEPT));
    public static final RegistryObject<Block> WINDSWEPT_BUTTON = register("windswept_button", () -> woodenButton(ModBlockSetType.WINDSWEPT));
    public static final RegistryObject<Block> WINDSWEPT_STAIRS = registerStairs("windswept_stairs", WINDSWEPT_PLANKS);
    public static final RegistryObject<Block> WINDSWEPT_SLAB = registerSlabs("windswept_slab", WINDSWEPT_PLANKS);
    public static final RegistryObject<Block> WINDSWEPT_FENCE_GATE = register("windswept_fence_gate", () -> new FenceGateBlock(Properties.m_284310_().m_284180_(((Block)WINDSWEPT_PLANKS.get()).m_284356_()).m_280658_(NoteBlockInstrument.BASS).m_60913_(2.0F, 3.0F).m_60918_(SoundType.f_56736_).m_278183_(), ModWoodType.WINDSWEPT));
    public static final RegistryObject<Block> WINDSWEPT_FENCE = register("windswept_fence", () -> new FenceBlock(Properties.m_284310_().m_284180_(((Block)WINDSWEPT_PLANKS.get()).m_284356_()).m_280658_(NoteBlockInstrument.BASS).m_60913_(2.0F, 3.0F).m_60918_(SoundType.f_56736_).m_278183_()));
    public static final RegistryObject<Block> WINDSWEPT_DOOR = register("windswept_door", () -> new DoorBlock(Properties.m_284310_().m_284180_(((Block)WINDSWEPT_PLANKS.get()).m_284356_()).m_280658_(NoteBlockInstrument.BASS).m_60978_(3.0F).m_60918_(SoundType.f_56736_).m_278183_().m_60955_(), ModBlockSetType.WINDSWEPT));
    public static final RegistryObject<Block> WINDSWEPT_BOOKSHELF = register("windswept_bookshelf", () -> new BookshelfBlock(Properties.m_284310_().m_284180_(((Block)WINDSWEPT_PLANKS.get()).m_284356_()).m_280658_(NoteBlockInstrument.BASS).m_60978_(1.5F).m_60918_(SoundType.f_56736_).m_278183_()), true, LootTableType.EMPTY);
    public static final RegistryObject<ModChestBlock> WINDSWEPT_CHEST = isterRegister("windswept_chest", () -> new ModChestBlock(Properties.m_284310_().m_280658_(NoteBlockInstrument.BASS).m_60978_(2.5F).m_60918_(SoundType.f_56736_).m_278183_()));
    public static final RegistryObject<ModTrappedChestBlock> TRAPPED_WINDSWEPT_CHEST = isterRegister("trapped_windswept_chest", () -> new ModTrappedChestBlock(Properties.m_284310_().m_280658_(NoteBlockInstrument.BASS).m_60978_(2.5F).m_60918_(SoundType.f_56736_).m_278183_()));
    public static final RegistryObject<Block> WINDSWEPT_SIGN = register("windswept_sign", () -> new ModStandSignBlock(Properties.m_284310_().m_284180_(((Block)WINDSWEPT_PLANKS.get()).m_284356_()).m_280658_(NoteBlockInstrument.BASS).m_60910_().m_60978_(1.0F).m_60918_(SoundType.f_56736_).m_278183_(), ModWoodType.WINDSWEPT), false);
    public static final RegistryObject<Block> WINDSWEPT_WALL_SIGN = register("windswept_wall_sign", () -> new ModWallSignBlock(Properties.m_284310_().m_284180_(((Block)WINDSWEPT_PLANKS.get()).m_284356_()).m_280658_(NoteBlockInstrument.BASS).m_60910_().m_60978_(1.0F).m_60918_(SoundType.f_56736_).lootFrom(WINDSWEPT_SIGN).m_278183_(), ModWoodType.WINDSWEPT), false);
    public static final RegistryObject<Block> WINDSWEPT_HANGING_SIGN = register("windswept_hanging_sign", () -> new ModHangingSignBlock(Properties.m_284310_().m_284180_(((Block)WINDSWEPT_LOG.get()).m_284356_()).m_280606_().m_280658_(NoteBlockInstrument.BASS).m_60910_().m_60978_(1.0F).m_278183_(), ModWoodType.WINDSWEPT), false);
    public static final RegistryObject<Block> WINDSWEPT_WALL_HANGING_SIGN = register("windswept_wall_hanging_sign", () -> new ModWallHangingSignBlock(Properties.m_284310_().m_284180_(((Block)WINDSWEPT_LOG.get()).m_284356_()).m_280606_().m_280658_(NoteBlockInstrument.BASS).m_60910_().m_60978_(1.0F).lootFrom(WINDSWEPT_HANGING_SIGN).m_278183_(), ModWoodType.WINDSWEPT), false);
    public static final RegistryObject<Block> WINDSWEPT_SAPLING = register("windswept_sapling", () -> redMossSapling(new WindsweptTree()));
    public static final RegistryObject<Block> POTTED_WINDSWEPT_SAPLING = register("potted_windswept_sapling", () -> new FlowerPotBlock(() -> (FlowerPotBlock)ForgeRegistries.BLOCKS.getDelegateOrThrow(Blocks.f_50276_).get(), WINDSWEPT_SAPLING, Properties.m_284310_().m_278166_(PushReaction.DESTROY).m_60955_().m_60966_()), false, LootTableType.EMPTY);
    public static final RegistryObject<Block> WINDSWEPT_PLANK_WALL_BLOCK = registerWalls("windswept_plank_wall", WINDSWEPT_PLANKS);
    public static final RegistryObject<Block> SNOWY_WINDSWEPT_PLANK_WALL_BLOCK = registerWalls("snowy_windswept_plank_wall", WINDSWEPT_PLANKS);
    public static final RegistryObject<Block> WINDSWEPT_LAMP = register("windswept_lamp", () -> new LampBlock(Properties.m_60926_((BlockBehaviour)WINDSWEPT_PLANKS.get())));
    public static final RegistryObject<Block> PINE_PLANKS = register("pine_planks", () -> new Block(Properties.m_284310_().m_284180_(MapColor.f_283748_).m_280658_(NoteBlockInstrument.BASS).m_60913_(2.0F, 3.0F).m_60918_(SoundType.f_56736_).m_278183_()));
    public static final RegistryObject<Block> COMPACTED_PINE_PLANKS = register("compacted_pine_planks", () -> new RotatedPillarBlock(Properties.m_284310_().m_284180_(MapColor.f_283748_).m_280658_(NoteBlockInstrument.BASS).m_60913_(2.0F, 3.0F).m_60918_(SoundType.f_56736_).m_278183_()));
    public static final RegistryObject<Block> COMPACTED_PINE_SLAB = registerSlabs("compacted_pine_slab", COMPACTED_PINE_PLANKS);
    public static final RegistryObject<Block> THATCHED_PINE_PLANKS = register("thatched_pine_planks", () -> new Block(Properties.m_284310_().m_284180_(MapColor.f_283748_).m_280658_(NoteBlockInstrument.BASS).m_60913_(2.0F, 3.0F).m_60918_(SoundType.f_56736_).m_278183_()));
    public static final RegistryObject<Block> PINE_LOG = register("pine_log", () -> log(MapColor.f_283748_, MapColor.f_283748_));
    public static final RegistryObject<Block> STRIPPED_PINE_LOG = register("stripped_pine_log", () -> log(MapColor.f_283748_, MapColor.f_283748_));
    public static final RegistryObject<Block> PINE_WOOD = register("pine_wood", () -> new RotatedPillarBlock(Properties.m_284310_().m_284180_(MapColor.f_283748_).m_280658_(NoteBlockInstrument.BASS).m_60978_(2.0F).m_60918_(SoundType.f_56736_).m_278183_()));
    public static final RegistryObject<Block> STRIPPED_PINE_WOOD = register("stripped_pine_wood", () -> new RotatedPillarBlock(Properties.m_284310_().m_284180_(MapColor.f_283748_).m_280658_(NoteBlockInstrument.BASS).m_60978_(2.0F).m_60918_(SoundType.f_56736_).m_278183_()));
    public static final RegistryObject<Block> PINE_LEAVES = register("pine_leaves", () -> leaves(SoundType.f_56740_), true, LootTableType.EMPTY);
    public static final RegistryObject<Block> PINE_PRESSURE_PLATE = register("pine_pressure_plate", () -> new PressurePlateBlock(Sensitivity.EVERYTHING, Properties.m_284310_().m_284180_(((Block)PINE_PLANKS.get()).m_284356_()).m_280606_().m_280658_(NoteBlockInstrument.BASS).m_60910_().m_60978_(0.5F).m_278166_(PushReaction.DESTROY).m_278183_(), ModBlockSetType.PINE));
    public static final RegistryObject<Block> PINE_TRAPDOOR = register("pine_trapdoor", () -> new TrapDoorBlock(Properties.m_284310_().m_284180_(MapColor.f_283748_).m_280658_(NoteBlockInstrument.BASS).m_60978_(3.0F).m_60918_(SoundType.f_56736_).m_60955_().m_278183_(), ModBlockSetType.PINE));
    public static final RegistryObject<Block> PINE_BUTTON = register("pine_button", () -> woodenButton(ModBlockSetType.PINE));
    public static final RegistryObject<Block> PINE_STAIRS = registerStairs("pine_stairs", PINE_PLANKS);
    public static final RegistryObject<Block> PINE_SLAB = registerSlabs("pine_slab", PINE_PLANKS);
    public static final RegistryObject<Block> PINE_FENCE_GATE = register("pine_fence_gate", () -> new FenceGateBlock(Properties.m_284310_().m_284180_(((Block)PINE_PLANKS.get()).m_284356_()).m_280658_(NoteBlockInstrument.BASS).m_60913_(2.0F, 3.0F).m_60918_(SoundType.f_56736_).m_278183_(), ModWoodType.PINE));
    public static final RegistryObject<Block> PINE_FENCE = register("pine_fence", () -> new FenceBlock(Properties.m_284310_().m_284180_(((Block)PINE_PLANKS.get()).m_284356_()).m_280658_(NoteBlockInstrument.BASS).m_60913_(2.0F, 3.0F).m_60918_(SoundType.f_56736_).m_278183_()));
    public static final RegistryObject<Block> PINE_DOOR = register("pine_door", () -> new DoorBlock(Properties.m_284310_().m_284180_(((Block)PINE_PLANKS.get()).m_284356_()).m_280658_(NoteBlockInstrument.BASS).m_60978_(3.0F).m_60918_(SoundType.f_56736_).m_60955_().m_278183_(), ModBlockSetType.PINE));
    public static final RegistryObject<Block> PINE_BOOKSHELF = register("pine_bookshelf", () -> new BookshelfBlock(Properties.m_284310_().m_284180_(((Block)PINE_PLANKS.get()).m_284356_()).m_280658_(NoteBlockInstrument.BASS).m_60978_(1.5F).m_60918_(SoundType.f_56736_).m_278183_()), true, LootTableType.EMPTY);
    public static final RegistryObject<ModChestBlock> PINE_CHEST = isterRegister("pine_chest", () -> new ModChestBlock(Properties.m_284310_().m_280658_(NoteBlockInstrument.BASS).m_60978_(2.5F).m_60918_(SoundType.f_56736_).m_278183_()));
    public static final RegistryObject<ModTrappedChestBlock> TRAPPED_PINE_CHEST = isterRegister("trapped_pine_chest", () -> new ModTrappedChestBlock(Properties.m_284310_().m_280658_(NoteBlockInstrument.BASS).m_60978_(2.5F).m_60918_(SoundType.f_56736_).m_278183_()));
    public static final RegistryObject<Block> PINE_SIGN = register("pine_sign", () -> new ModStandSignBlock(Properties.m_284310_().m_284180_(((Block)PINE_PLANKS.get()).m_284356_()).m_280658_(NoteBlockInstrument.BASS).m_60910_().m_60978_(1.0F).m_60918_(SoundType.f_56736_).m_278183_(), ModWoodType.PINE), false);
    public static final RegistryObject<Block> PINE_WALL_SIGN = register("pine_wall_sign", () -> new ModWallSignBlock(Properties.m_284310_().m_284180_(((Block)PINE_PLANKS.get()).m_284356_()).m_280658_(NoteBlockInstrument.BASS).m_60910_().m_60978_(1.0F).m_60918_(SoundType.f_56736_).m_278183_().lootFrom(PINE_SIGN), ModWoodType.PINE), false);
    public static final RegistryObject<Block> PINE_HANGING_SIGN = register("pine_hanging_sign", () -> new ModHangingSignBlock(Properties.m_284310_().m_284180_(((Block)PINE_LOG.get()).m_284356_()).m_280606_().m_280658_(NoteBlockInstrument.BASS).m_60910_().m_60978_(1.0F).m_278183_(), ModWoodType.PINE), false);
    public static final RegistryObject<Block> PINE_WALL_HANGING_SIGN = register("pine_wall_hanging_sign", () -> new ModWallHangingSignBlock(Properties.m_284310_().m_284180_(((Block)PINE_LOG.get()).m_284356_()).m_280606_().m_280658_(NoteBlockInstrument.BASS).m_60910_().m_60978_(1.0F).lootFrom(PINE_HANGING_SIGN).m_278183_(), ModWoodType.PINE), false);
    public static final RegistryObject<Block> PINE_SAPLING = register("pine_sapling", () -> redMossSapling(new PineTree()));
    public static final RegistryObject<Block> POTTED_PINE_SAPLING = register("potted_pine_sapling", () -> new FlowerPotBlock(() -> (FlowerPotBlock)ForgeRegistries.BLOCKS.getDelegateOrThrow(Blocks.f_50276_).get(), PINE_SAPLING, Properties.m_284310_().m_278166_(PushReaction.DESTROY).m_60955_().m_60966_().m_278183_()), false, LootTableType.EMPTY);
    public static final RegistryObject<Block> SNOWY_PINE_PILLAR = register("snowy_pine_pillar", () -> new ThinPillarBlock(Properties.m_284310_().m_284180_(MapColor.f_283748_).m_280658_(NoteBlockInstrument.BASS).m_60978_(2.0F).m_60918_(SoundType.f_56736_).m_278183_().m_60955_()));
    public static final RegistryObject<Block> PINE_FIREWOOD = register("pine_firewood", () -> new FirewoodBlock(true, Properties.m_284310_().m_284180_(MapColor.f_283748_).m_60953_(litBlockEmission(15)).m_280658_(NoteBlockInstrument.BASS).m_60978_(2.0F).m_60918_(SoundType.f_56736_).m_278183_().m_60955_()));
    public static final RegistryObject<Block> STEEP_PLANKS = register("steep_planks", () -> new Block(Properties.m_284310_().m_284180_(MapColor.f_283774_).m_280658_(NoteBlockInstrument.BASS).m_60913_(2.0F, 3.0F).m_60918_(SoundType.f_56736_)));
    public static final RegistryObject<Block> DENSE_STEEP_PLANKS = register("dense_steep_planks", () -> new Block(Properties.m_284310_().m_284180_(MapColor.f_283774_).m_280658_(NoteBlockInstrument.BASS).m_60913_(3.0F, 5.0F).m_60918_(SoundType.f_56736_)));
    public static final RegistryObject<Block> DENSE_BANDED_STEEP_PLANKS = register("dense_banded_steep_planks", () -> new DBSPlanksBlock(Properties.m_60926_((BlockBehaviour)DENSE_STEEP_PLANKS.get())));
    public static final RegistryObject<Block> STEEP_WOOD = register("steep_wood", () -> new RotatedPillarBlock(Properties.m_284310_().m_284180_(MapColor.f_283774_).m_280658_(NoteBlockInstrument.BASS).m_60978_(2.0F).m_60918_(SoundType.f_56736_)));
    public static final RegistryObject<Block> STEEP_WOOD_SLAB = registerSlabs("steep_wood_slab", STEEP_WOOD);
    public static final RegistryObject<Block> STUDDED_STEEP_WOOD = register("studded_steep_wood", () -> new RotatedPillarBlock(Properties.m_284310_().m_284180_(MapColor.f_283774_).m_280658_(NoteBlockInstrument.BASS).m_60978_(2.0F).m_60918_(SoundType.f_56736_)));
    public static final RegistryObject<Block> LINED_STEEP_WOOD = register("lined_steep_wood", () -> new ModBlocks.FacingBlock(Properties.m_284310_().m_284180_(MapColor.f_283774_).m_280658_(NoteBlockInstrument.BASS).m_60978_(2.0F).m_60918_(SoundType.f_56736_)));
    public static final RegistryObject<Block> RIMMED_STEEP_WOOD = register("rimmed_steep_wood", () -> new ModBlocks.FacingBlock(Properties.m_284310_().m_284180_(MapColor.f_283774_).m_280658_(NoteBlockInstrument.BASS).m_60978_(2.0F).m_60918_(SoundType.f_56736_)));
    public static final RegistryObject<Block> STEEP_PRESSURE_PLATE = register("steep_pressure_plate", () -> new PressurePlateBlock(Sensitivity.EVERYTHING, Properties.m_284310_().m_284180_(((Block)STEEP_PLANKS.get()).m_284356_()).m_280606_().m_280658_(NoteBlockInstrument.BASS).m_60910_().m_60978_(0.5F).m_278166_(PushReaction.DESTROY), ModBlockSetType.WINDSWEPT));
    public static final RegistryObject<Block> STEEP_BUTTON = register("steep_button", () -> woodenButton(ModBlockSetType.WINDSWEPT));
    public static final RegistryObject<Block> STEEP_STAIRS = registerStairs("steep_stairs", STEEP_PLANKS);
    public static final RegistryObject<Block> STEEP_SLAB = registerSlabs("steep_slab", STEEP_PLANKS);
    public static final RegistryObject<Block> STEEP_FENCE_GATE = register("steep_fence_gate", () -> new FenceGateBlock(Properties.m_284310_().m_284180_(((Block)STEEP_PLANKS.get()).m_284356_()).m_280658_(NoteBlockInstrument.BASS).m_60913_(2.0F, 3.0F).m_60918_(SoundType.f_56736_), SoundEvents.f_11872_, SoundEvents.f_11873_));
    public static final RegistryObject<Block> STEEP_FENCE = register("steep_fence", () -> new FenceBlock(Properties.m_284310_().m_284180_(((Block)STEEP_PLANKS.get()).m_284356_()).m_280658_(NoteBlockInstrument.BASS).m_60913_(2.0F, 3.0F).m_60918_(SoundType.f_56736_)));
    public static final RegistryObject<Block> STEEP_WALL_BLOCK = registerWalls("steep_wall", STEEP_WOOD);
    public static final RegistryObject<Block> STUDDED_STEEP_WALL_BLOCK = registerWalls("studded_steep_wall", STUDDED_STEEP_WOOD);
    public static final RegistryObject<Block> LINED_STEEP_WALL_BLOCK = registerWalls("lined_steep_wall", LINED_STEEP_WOOD);
    public static final RegistryObject<Block> RIMMED_STEEP_WALL_BLOCK = registerWalls("rimmed_steep_wall", RIMMED_STEEP_WOOD);
    public static final RegistryObject<Block> STEEP_LAMP = register("steep_lamp", () -> new LampBlock(Properties.m_60926_((BlockBehaviour)STEEP_WOOD.get())));
    public static final RegistryObject<Block> STEEP_FIREWOOD = register("steep_firewood", () -> new SteepFirewoodBlock(Properties.m_284310_().m_284180_(MapColor.f_283774_).m_280658_(NoteBlockInstrument.BASS).m_60978_(2.0F).m_60918_(SoundType.f_56736_).m_60953_(litBlockEmission(15)).m_60955_().m_60988_()));
    public static final RegistryObject<Block> SKY_WOOD_PLANKS = register("sky_wood_planks", () -> new Block(Properties.m_284310_().m_284180_(MapColor.f_283908_).m_280658_(NoteBlockInstrument.BASS).m_60913_(2.0F, 3.0F).m_60918_(SoundType.f_56736_).m_278183_()));
    public static final RegistryObject<Block> SKY_WOOD_SLAB = registerSlabs("sky_wood_slab", SKY_WOOD_PLANKS);
    public static final RegistryObject<Block> SKY_WOOD_STAIRS = registerStairs("sky_wood_stairs", SKY_WOOD_PLANKS);
    public static final RegistryObject<Block> CHORUS_PLANKS = register("chorus_planks", () -> new Block(Properties.m_284310_().m_284180_(MapColor.f_283889_).m_280658_(NoteBlockInstrument.BASS).m_60913_(2.0F, 3.0F).m_60918_(SoundType.f_56736_)));
    public static final RegistryObject<Block> SOILED_CHORUS_PLANKS = register("soiled_chorus_planks", () -> new Block(Properties.m_60926_((BlockBehaviour)CHORUS_PLANKS.get())));
    public static final RegistryObject<Block> SOILED_CHORUS_PLANKS_HEAVY = register("soiled_chorus_planks_heavy", () -> new Block(Properties.m_60926_((BlockBehaviour)CHORUS_PLANKS.get())));
    public static final RegistryObject<Block> THATCHED_CHORUS_PLANKS = register("thatched_chorus_planks", () -> new Block(Properties.m_284310_().m_284180_(MapColor.f_283889_).m_280658_(NoteBlockInstrument.BASS).m_60913_(2.0F, 3.0F).m_60918_(SoundType.f_56736_)));
    public static final RegistryObject<Block> CHORUS_LOG = register("chorus_log", () -> fireProofLog(MapColor.f_283889_));
    public static final RegistryObject<Block> CHORUS_WOOD = register("chorus_wood", () -> new RotatedPillarBlock(Properties.m_284310_().m_284180_(MapColor.f_283889_).m_280658_(NoteBlockInstrument.BASS).m_60978_(2.0F).m_60918_(SoundType.f_56736_)));
    public static final RegistryObject<Block> BLOSSOMING_CHORUS_WOOD = register("blossoming_chorus_wood", () -> new RotatedPillarBlock(Properties.m_284310_().m_284180_(MapColor.f_283889_).m_280658_(NoteBlockInstrument.BASS).m_60978_(2.0F).m_60918_(SoundType.f_56736_)));
    public static final RegistryObject<Block> BLOOMING_CHORUS_WOOD = register("blooming_chorus_wood", () -> new RotatedPillarBlock(Properties.m_284310_().m_284180_(MapColor.f_283889_).m_280658_(NoteBlockInstrument.BASS).m_60978_(2.0F).m_60918_(SoundType.f_56736_)));
    public static final RegistryObject<Block> STUDDED_CHORUS_WOOD = register("studded_chorus_wood", () -> new RotatedPillarBlock(Properties.m_284310_().m_284180_(MapColor.f_283889_).m_280658_(NoteBlockInstrument.BASS).m_60978_(2.0F).m_60918_(SoundType.f_56736_)));
    public static final RegistryObject<Block> CHORUS_WOOD_STAIRS = registerStairs("chorus_wood_stairs", CHORUS_WOOD);
    public static final RegistryObject<Block> CHORUS_WOOD_SLAB = registerSlabs("chorus_wood_slab", CHORUS_WOOD);
    public static final RegistryObject<Block> CHORUS_LEAVES = register("chorus_leaves", () -> new ChorusLeavesBlock(Properties.m_284310_().m_284180_(MapColor.f_283832_).m_60978_(0.2F).m_60977_().m_60918_(SoundType.f_271239_).m_60955_().m_60922_(ModBlocks::ocelotOrParrot).m_60960_(ModBlocks::never).m_60971_(ModBlocks::never).m_278166_(PushReaction.DESTROY).m_60924_(ModBlocks::never)), true, LootTableType.EMPTY);
    public static final RegistryObject<Block> CHORUS_VINE = register("chorus_vine", ChorusVineBlock::new, true, LootTableType.EMPTY);
    public static final RegistryObject<Block> CHORUS_PRESSURE_PLATE = register("chorus_pressure_plate", () -> new PressurePlateBlock(Sensitivity.EVERYTHING, Properties.m_284310_().m_284180_(((Block)CHORUS_PLANKS.get()).m_284356_()).m_280606_().m_280658_(NoteBlockInstrument.BASS).m_60910_().m_60978_(0.5F).m_278166_(PushReaction.DESTROY), ModBlockSetType.CHORUS));
    public static final RegistryObject<Block> CHORUS_TRAPDOOR = register("chorus_trapdoor", () -> new TrapDoorBlock(Properties.m_284310_().m_284180_(MapColor.f_283889_).m_280658_(NoteBlockInstrument.BASS).m_60978_(3.0F).m_60918_(SoundType.f_56736_).m_60955_(), ModBlockSetType.CHORUS));
    public static final RegistryObject<Block> CHORUS_BUTTON = register("chorus_button", () -> woodenButton(ModBlockSetType.CHORUS));
    public static final RegistryObject<Block> CHORUS_STAIRS = registerStairs("chorus_stairs", CHORUS_PLANKS);
    public static final RegistryObject<Block> CHORUS_SLAB = registerSlabs("chorus_slab", CHORUS_PLANKS);
    public static final RegistryObject<Block> CHORUS_FENCE_GATE = register("chorus_fence_gate", () -> new FenceGateBlock(Properties.m_284310_().m_284180_(((Block)CHORUS_PLANKS.get()).m_284356_()).m_280658_(NoteBlockInstrument.BASS).m_60913_(2.0F, 3.0F).m_60918_(SoundType.f_56736_), ModWoodType.CHORUS));
    public static final RegistryObject<Block> CHORUS_FENCE = register("chorus_fence", () -> new FenceBlock(Properties.m_284310_().m_284180_(((Block)CHORUS_PLANKS.get()).m_284356_()).m_280658_(NoteBlockInstrument.BASS).m_60913_(2.0F, 3.0F).m_60918_(SoundType.f_56736_)));
    public static final RegistryObject<Block> CHORUS_DOOR = register("chorus_door", () -> new DoorBlock(Properties.m_284310_().m_284180_(((Block)CHORUS_PLANKS.get()).m_284356_()).m_280658_(NoteBlockInstrument.BASS).m_60978_(3.0F).m_60918_(SoundType.f_56736_).m_60955_(), ModBlockSetType.CHORUS));
    public static final RegistryObject<Block> CHORUS_BOOKSHELF = register("chorus_bookshelf", () -> new BookshelfBlock(Properties.m_284310_().m_284180_(((Block)CHORUS_PLANKS.get()).m_284356_()).m_280658_(NoteBlockInstrument.BASS).m_60978_(1.5F).m_60918_(SoundType.f_56736_)), true, LootTableType.EMPTY);
    public static final RegistryObject<ModChestBlock> CHORUS_CHEST = isterRegister("chorus_chest", () -> new ModChestBlock(Properties.m_284310_().m_280658_(NoteBlockInstrument.BASS).m_60978_(2.5F).m_60918_(SoundType.f_56736_)));
    public static final RegistryObject<ModTrappedChestBlock> TRAPPED_CHORUS_CHEST = isterRegister("trapped_chorus_chest", () -> new ModTrappedChestBlock(Properties.m_284310_().m_280658_(NoteBlockInstrument.BASS).m_60978_(2.5F).m_60918_(SoundType.f_56736_)));
    public static final RegistryObject<Block> CHORUS_SIGN = register("chorus_sign", () -> new ModStandSignBlock(Properties.m_284310_().m_284180_(((Block)CHORUS_PLANKS.get()).m_284356_()).m_280658_(NoteBlockInstrument.BASS).m_60910_().m_60978_(1.0F).m_60918_(SoundType.f_56736_), ModWoodType.CHORUS), false);
    public static final RegistryObject<Block> CHORUS_WALL_SIGN = register("chorus_wall_sign", () -> new ModWallSignBlock(Properties.m_284310_().m_284180_(((Block)CHORUS_PLANKS.get()).m_284356_()).m_280658_(NoteBlockInstrument.BASS).m_60910_().m_60978_(1.0F).m_60918_(SoundType.f_56736_).lootFrom(CHORUS_SIGN), ModWoodType.CHORUS), false);
    public static final RegistryObject<Block> CHORUS_HANGING_SIGN = register("chorus_hanging_sign", () -> new ModHangingSignBlock(Properties.m_284310_().m_284180_(((Block)CHORUS_LOG.get()).m_284356_()).m_280606_().m_280658_(NoteBlockInstrument.BASS).m_60910_().m_60978_(1.0F), ModWoodType.CHORUS), false);
    public static final RegistryObject<Block> CHORUS_WALL_HANGING_SIGN = register("chorus_wall_hanging_sign", () -> new ModWallHangingSignBlock(Properties.m_284310_().m_284180_(((Block)CHORUS_LOG.get()).m_284356_()).m_280606_().m_280658_(NoteBlockInstrument.BASS).m_60910_().m_60978_(1.0F).lootFrom(CHORUS_HANGING_SIGN), ModWoodType.CHORUS), false);
    public static final RegistryObject<SaplingBlock> CHORUS_SAPLING = register("chorus_sapling", () -> endSapling(new ChorusTree()));
    public static final RegistryObject<Block> POTTED_CHORUS_SAPLING = register("potted_chorus_sapling", () -> new FlowerPotBlock(() -> (FlowerPotBlock)ForgeRegistries.BLOCKS.getDelegateOrThrow(Blocks.f_50276_).get(), CHORUS_SAPLING, Properties.m_284310_().m_278166_(PushReaction.DESTROY).m_60955_().m_60966_()), false, LootTableType.EMPTY);
    public static final RegistryObject<Block> CORRUPT_CHORUS_PLANKS = register("corrupt_chorus_planks", () -> new Block(Properties.m_284310_().m_284180_(MapColor.f_283869_).m_280658_(NoteBlockInstrument.BASS).m_60913_(2.0F, 3.0F).m_60918_(SoundType.f_56736_)));
    public static final RegistryObject<Block> SOILED_CORRUPT_CHORUS_PLANKS = register("soiled_corrupt_chorus_planks", () -> new Block(Properties.m_60926_((BlockBehaviour)CORRUPT_CHORUS_PLANKS.get())));
    public static final RegistryObject<Block> CORRUPT_CHORUS_LOG = register("corrupt_chorus_log", () -> fireProofLog(MapColor.f_283869_));
    public static final RegistryObject<Block> CORRUPT_CHORUS_WOOD = register("corrupt_chorus_wood", () -> new RotatedPillarBlock(Properties.m_284310_().m_284180_(MapColor.f_283869_).m_280658_(NoteBlockInstrument.BASS).m_60978_(2.0F).m_60918_(SoundType.f_56736_)));
    public static final RegistryObject<Block> CORRUPT_CHORUS_PRESSURE_PLATE = register("corrupt_chorus_pressure_plate", () -> new PressurePlateBlock(Sensitivity.EVERYTHING, Properties.m_284310_().m_284180_(((Block)CORRUPT_CHORUS_PLANKS.get()).m_284356_()).m_280606_().m_280658_(NoteBlockInstrument.BASS).m_60910_().m_60978_(0.5F).m_278166_(PushReaction.DESTROY), ModBlockSetType.CHORUS));
    public static final RegistryObject<Block> CORRUPT_CHORUS_TRAPDOOR = register("corrupt_chorus_trapdoor", () -> new TrapDoorBlock(Properties.m_284310_().m_284180_(MapColor.f_283869_).m_280658_(NoteBlockInstrument.BASS).m_60978_(3.0F).m_60918_(SoundType.f_56736_).m_60955_(), ModBlockSetType.CHORUS));
    public static final RegistryObject<Block> CORRUPT_CHORUS_BUTTON = register("corrupt_chorus_button", () -> woodenButton(ModBlockSetType.CHORUS));
    public static final RegistryObject<Block> CORRUPT_CHORUS_STAIRS = registerStairs("corrupt_chorus_stairs", CORRUPT_CHORUS_PLANKS);
    public static final RegistryObject<Block> CORRUPT_CHORUS_SLAB = registerSlabs("corrupt_chorus_slab", CORRUPT_CHORUS_PLANKS);
    public static final RegistryObject<Block> CORRUPT_CHORUS_FENCE_GATE = register("corrupt_chorus_fence_gate", () -> new FenceGateBlock(Properties.m_284310_().m_284180_(((Block)CORRUPT_CHORUS_PLANKS.get()).m_284356_()).m_280658_(NoteBlockInstrument.BASS).m_60913_(2.0F, 3.0F).m_60918_(SoundType.f_56736_), ModWoodType.CORRUPT_CHORUS));
    public static final RegistryObject<Block> CORRUPT_CHORUS_FENCE = register("corrupt_chorus_fence", () -> new FenceBlock(Properties.m_284310_().m_284180_(((Block)CORRUPT_CHORUS_PLANKS.get()).m_284356_()).m_280658_(NoteBlockInstrument.BASS).m_60913_(2.0F, 3.0F).m_60918_(SoundType.f_56736_)));
    public static final RegistryObject<Block> CORRUPT_CHORUS_DOOR = register("corrupt_chorus_door", () -> new DoorBlock(Properties.m_284310_().m_284180_(((Block)CORRUPT_CHORUS_PLANKS.get()).m_284356_()).m_280658_(NoteBlockInstrument.BASS).m_60978_(3.0F).m_60918_(SoundType.f_56736_).m_60955_(), ModBlockSetType.CORRUPT_CHORUS));
    public static final RegistryObject<Block> CORRUPT_CHORUS_BOOKSHELF = register("corrupt_chorus_bookshelf", () -> new BookshelfBlock(Properties.m_284310_().m_284180_(((Block)CORRUPT_CHORUS_PLANKS.get()).m_284356_()).m_280658_(NoteBlockInstrument.BASS).m_60978_(1.5F).m_60918_(SoundType.f_56736_)), true, LootTableType.EMPTY);
    public static final RegistryObject<ModChestBlock> CORRUPT_CHORUS_CHEST = isterRegister("corrupt_chorus_chest", () -> new ModChestBlock(Properties.m_284310_().m_280658_(NoteBlockInstrument.BASS).m_60978_(2.5F).m_60918_(SoundType.f_56736_)));
    public static final RegistryObject<ModTrappedChestBlock> TRAPPED_CORRUPT_CHORUS_CHEST = isterRegister("trapped_corrupt_chorus_chest", () -> new ModTrappedChestBlock(Properties.m_284310_().m_280658_(NoteBlockInstrument.BASS).m_60978_(2.5F).m_60918_(SoundType.f_56736_)));
    public static final RegistryObject<Block> CORRUPT_CHORUS_SIGN = register("corrupt_chorus_sign", () -> new ModStandSignBlock(Properties.m_284310_().m_284180_(((Block)CORRUPT_CHORUS_PLANKS.get()).m_284356_()).m_280658_(NoteBlockInstrument.BASS).m_60910_().m_60978_(1.0F).m_60918_(SoundType.f_56736_), ModWoodType.CORRUPT_CHORUS), false);
    public static final RegistryObject<Block> CORRUPT_CHORUS_WALL_SIGN = register("corrupt_chorus_wall_sign", () -> new ModWallSignBlock(Properties.m_284310_().m_284180_(((Block)CORRUPT_CHORUS_PLANKS.get()).m_284356_()).m_280658_(NoteBlockInstrument.BASS).m_60910_().m_60978_(1.0F).m_60918_(SoundType.f_56736_).lootFrom(CORRUPT_CHORUS_SIGN), ModWoodType.CORRUPT_CHORUS), false);
    public static final RegistryObject<Block> CORRUPT_CHORUS_HANGING_SIGN = register("corrupt_chorus_hanging_sign", () -> new ModHangingSignBlock(Properties.m_284310_().m_284180_(((Block)CORRUPT_CHORUS_LOG.get()).m_284356_()).m_280606_().m_280658_(NoteBlockInstrument.BASS).m_60910_().m_60978_(1.0F), ModWoodType.CORRUPT_CHORUS), false);
    public static final RegistryObject<Block> CORRUPT_CHORUS_WALL_HANGING_SIGN = register("corrupt_chorus_wall_hanging_sign", () -> new ModWallHangingSignBlock(Properties.m_284310_().m_284180_(((Block)CORRUPT_CHORUS_LOG.get()).m_284356_()).m_280606_().m_280658_(NoteBlockInstrument.BASS).m_60910_().m_60978_(1.0F).lootFrom(CORRUPT_CHORUS_HANGING_SIGN), ModWoodType.CORRUPT_CHORUS), false);
    public static final RegistryObject<Block> CHORUS_BLOSSOM_LEAVES = register("chorus_blossom_leaves", () -> new ChorusLeavesBlock(Properties.m_284310_().m_284180_(MapColor.f_283942_).m_60978_(0.2F).m_60977_().m_60918_(SoundType.f_271239_).m_60955_().m_60922_(ModBlocks::ocelotOrParrot).m_60960_(ModBlocks::never).m_60971_(ModBlocks::never).m_278166_(PushReaction.DESTROY).m_60924_(ModBlocks::never)), true, LootTableType.EMPTY);
    public static final RegistryObject<Block> CHORUS_BLOSSOM_VINES = register("chorus_blossom_vines", () -> new ReedBlock(Properties.m_284310_().m_284180_(MapColor.f_283942_).m_60977_().m_60910_().m_60966_().m_60918_(SoundType.f_271239_).m_278166_(PushReaction.DESTROY)));
    public static final RegistryObject<Block> CHORUS_BLOSSOM_VINES_PRUNED = register("chorus_blossom_vines_pruned", () -> new ReedBlock(Properties.m_284310_().m_284180_(MapColor.f_283942_).m_60977_().m_60910_().m_60966_().m_60918_(SoundType.f_271239_).m_278166_(PushReaction.DESTROY)));
    public static final RegistryObject<Block> SHADE_STONE_BLOCK = register("shade_stone", ModBlocks.ShadeStoneBlock::new);
    public static final RegistryObject<Block> SHADE_STONE_POLISHED_BLOCK = register("shade_stone_polished", ModBlocks.ShadeStoneBlock::new);
    public static final RegistryObject<Block> SHADE_STONE_CHISELED_BLOCK = register("shade_stone_chiseled", ModBlocks.ShadeStoneBlock::new);
    public static final RegistryObject<Block> SHADE_STONE_BRICK_BLOCK = register("shade_stone_bricks", ModBlocks.ShadeStoneBlock::new);
    public static final RegistryObject<Block> SHADE_BRICK_BLOCK = register("shade_bricks", ModBlocks.ShadeStoneBlock::new);
    public static final RegistryObject<Block> SHADE_TILES_BLOCK = register("shade_tiles", ModBlocks.ShadeStoneBlock::new);
    public static final RegistryObject<Block> SHADE_PILLAR_BLOCK = register("shade_pillar", () -> pillar(ShadeStoneProperties()));
    public static final RegistryObject<Block> SHADE_GLASS_BLOCK = register("shade_glass", () -> new GlassBlock(Properties.m_60926_(Blocks.f_50058_)));
    public static final RegistryObject<Block> CRYPT_STONE_BLOCK = register("crypt_stone", ModBlocks.CryptStoneBlock::new);
    public static final RegistryObject<Block> CRYPT_STONE_POLISHED_BLOCK = register("crypt_stone_polished", ModBlocks.CryptStoneBlock::new);
    public static final RegistryObject<Block> CRYPT_STONE_CHISELED_BLOCK = register("crypt_stone_chiseled", ModBlocks.CryptStoneBlock::new);
    public static final RegistryObject<Block> CRYPT_BRICKS_BLOCK = register("crypt_bricks", ModBlocks.CryptStoneBlock::new);
    public static final RegistryObject<Block> CRYPT_TILES_BLOCK = register("crypt_tiles", ModBlocks.CryptStoneBlock::new);
    public static final RegistryObject<Block> CRYPT_PLINTH_BLOCK = register("crypt_plinth", ModBlocks.CryptStoneBlock::new);
    public static final RegistryObject<Block> CRYPT_PILLAR_BLOCK = register("crypt_pillar", () -> pillar(CryptStoneProperties()));
    public static final RegistryObject<Block> CRYPT_BOOKSHELF = register("crypt_bookshelf", () -> new BookshelfBlock(CryptStoneProperties(), 2.0F), true, LootTableType.EMPTY);
    public static final RegistryObject<Block> MARBLE_BLOCK = register("marble", ModBlocks.MarbleBlock::new);
    public static final RegistryObject<Block> CRACKED_MARBLE_BLOCK = register("cracked_marble", ModBlocks.MarbleBlock::new);
    public static final RegistryObject<Block> GOLD_ARCH_MARBLE_BLOCK = register("gold_arch_marble", ModBlocks.MarbleBlock::new);
    public static final RegistryObject<Block> GOLD_BANDED_MARBLE_BLOCK = register("gold_banded_marble", ModBlocks.MarbleBlock::new);
    public static final RegistryObject<Block> GOLD_HOLDER_MARBLE_BLOCK = register("gold_holder_marble", ModBlocks.MarbleBlock::new);
    public static final RegistryObject<Block> GOLD_COVERED_MARBLE_BLOCK = register("gold_covered_marble", ModBlocks.MarbleBlock::new);
    public static final RegistryObject<Block> GOLD_PLATED_MARBLE_BLOCK = register("gold_plated_marble", ModBlocks.MarbleBlock::new);
    public static final RegistryObject<Block> SLATE_MARBLE_BLOCK = register("slate_marble", ModBlocks.SlateMarbleBlock::new);
    public static final RegistryObject<Block> WORN_SLATE_MARBLE_BLOCK = register("worn_slate_marble", ModBlocks.SlateMarbleBlock::new);
    public static final RegistryObject<Block> WEATHERED_SLATE_MARBLE_BLOCK = register("weathered_slate_marble", ModBlocks.SlateMarbleBlock::new);
    public static final RegistryObject<Block> WASHED_SLATE_MARBLE_BLOCK = register("washed_slate_marble", () -> new ModBlocks.FacingBlock(Properties.m_284310_().m_284495_((state) -> state.m_61143_(FacingBlock.f_52588_) == Direction.DOWN ? MapColor.f_283846_ : MapColor.f_283919_).m_60999_().m_60913_(3.0F, 6.0F).m_60918_(SoundType.f_56742_)));
    public static final RegistryObject<Block> SLATE_CONNECTED_MARBLE_BLOCK = register("slate_connected_marble", ModBlocks.MarbleBlock::new);
    public static final RegistryObject<Block> SLATE_PATTERNED_MARBLE_BLOCK = register("slate_patterned_marble", ModBlocks.MarbleBlock::new);
    public static final RegistryObject<Block> SLATE_CORNERED_MARBLE_BLOCK = register("slate_cornered_marble", ModBlocks.MarbleBlock::new);
    public static final RegistryObject<Block> SLATE_GLAZED_MARBLE_BLOCK = register("slate_glazed_marble", () -> new GlazedTerracottaBlock(MarbleProperties()));
    public static final RegistryObject<Block> SILT_MARBLE_SLIGHT_BLOCK = register("silt_marble_slight", ModBlocks.MarbleBlock::new);
    public static final RegistryObject<Block> SILT_MARBLE_BLOCK = register("silt_marble", ModBlocks.MarbleBlock::new);
    public static final RegistryObject<Block> SILT_MARBLE_HEAVY_BLOCK = register("silt_marble_heavy", ModBlocks.SiltstoneBlock::new);
    public static final RegistryObject<Block> SILT_SLATE_MARBLE_BLOCK = register("silt_slate_marble", ModBlocks.SlateMarbleBlock::new);
    public static final RegistryObject<Block> INDENTED_GOLD_BLOCK = register("indented_gold", ModBlocks.IndentedGoldBlock::new);
    public static final RegistryObject<Block> CHISELED_INDENTED_GOLD_BLOCK = register("chiseled_indented_gold", ModBlocks.IndentedGoldBlock::new);
    public static final RegistryObject<Block> CARVED_INDENTED_GOLD_BLOCK = register("carved_indented_gold", ModBlocks.IndentedGoldBlock::new);
    public static final RegistryObject<Block> CRAGROCKS_BLOCK = register("cragrocks", ModBlocks.CragBlock::new);
    public static final RegistryObject<Block> MOSSY_CRAGROCKS_SLIGHT_BLOCK = register("mossy_cragrocks_slight", ModBlocks.CragBlock::new);
    public static final RegistryObject<Block> MOSSY_CRAGROCKS_BLOCK = register("mossy_cragrocks", ModBlocks.CragBlock::new);
    public static final RegistryObject<Block> MOSSY_CRAGROCKS_HEAVY_BLOCK = register("mossy_cragrocks_heavy", ModBlocks.CragBlock::new);
    public static final RegistryObject<Block> CRAG_TILES_BLOCK = register("crag_tiles", ModBlocks.CragBlock::new);
    public static final RegistryObject<Block> MOSSY_CRAG_TILES_SLIGHT_BLOCK = register("mossy_crag_tiles_slight", ModBlocks.CragBlock::new);
    public static final RegistryObject<Block> MOSSY_CRAG_TILES_BLOCK = register("mossy_crag_tiles", ModBlocks.CragBlock::new);
    public static final RegistryObject<Block> CRAG_BRICKS_BLOCK = register("crag_bricks", ModBlocks.CragBlock::new);
    public static final RegistryObject<Block> SNOWY_CRAG_BRICKS_BLOCK = register("snowy_crag_bricks", ModBlocks.CragBlock::new);
    public static final RegistryObject<Block> CRAG_PAVEMENT_BLOCK = register("crag_pavement", ModBlocks.CragBlock::new);
    public static final RegistryObject<Block> CRACKED_CRAG_PAVEMENT_BLOCK = register("cracked_crag_pavement", ModBlocks.CragBlock::new);
    public static final RegistryObject<Block> SNOWY_CRAG_PAVEMENT_BLOCK = register("snowy_crag_pavement", ModBlocks.CragBlock::new);
    public static final RegistryObject<Block> MOSSY_CRAG_PAVEMENT_BLOCK = register("mossy_crag_pavement", ModBlocks.CragBlock::new);
    public static final RegistryObject<Block> MOSSY_CRAG_PAVEMENT_HEAVY_BLOCK = register("mossy_crag_pavement_heavy", ModBlocks.CragBlock::new);
    public static final RegistryObject<Block> SILT_STUDDED_CRAG_TILES_BLOCK = register("silt_studded_crag_tiles", ModBlocks.CragBlock::new);
    public static final RegistryObject<Block> BRACED_CRAG_TILE_PILLAR = register("braced_crag_tile_pillar", () -> new DoubleCubeBlock(CragProperties()));
    public static final RegistryObject<Block> HIGHROCK_BLOCK = register("highrock", ModBlocks.HighrockBlock::new);
    public static final RegistryObject<Block> POLISHED_HIGHROCK_BLOCK = register("polished_highrock", ModBlocks.HighrockBlock::new);
    public static final RegistryObject<Block> HIGHROCK_BRICKS_BLOCK = register("highrock_bricks", ModBlocks.HighrockBlock::new);
    public static final RegistryObject<Block> HIGHROCK_INDENTED_BRICKS_BLOCK = register("highrock_indented_bricks", ModBlocks.HighrockBlock::new);
    public static final RegistryObject<Block> SNOWY_HIGHROCK_BRICKS_SLIGHT_BLOCK = register("snowy_highrock_bricks_slight", ModBlocks.HighrockBlock::new);
    public static final RegistryObject<Block> SNOWY_HIGHROCK_BRICKS_BLOCK = register("snowy_highrock_bricks", ModBlocks.HighrockBlock::new);
    public static final RegistryObject<Block> GOLD_INDENTED_HIGHROCK_BRICKS_BLOCK = register("gold_indented_highrock_bricks", ModBlocks.HighrockBlock::new);
    public static final RegistryObject<Block> GOLD_CHISELED_HIGHROCK_BRICKS_BLOCK = register("gold_chiseled_highrock_bricks", ModBlocks.HighrockBlock::new);
    public static final RegistryObject<Block> SILT_HIGHROCK_BLOCK = register("silt_highrock", ModBlocks.HighrockBlock::new);
    public static final RegistryObject<Block> SILTSTONE_BLOCK = register("siltstone", ModBlocks.SiltstoneBlock::new);
    public static final RegistryObject<Block> SNOWY_SILTSTONE_SLIGHT_BLOCK = register("snowy_siltstone_slight", ModBlocks.SiltstoneBlock::new);
    public static final RegistryObject<Block> SNOWY_SILTSTONE_BLOCK = register("snowy_siltstone", ModBlocks.SiltstoneBlock::new);
    public static final RegistryObject<Block> DIRTY_SILTSTONE_BLOCK = register("dirty_siltstone", ModBlocks.SiltstoneBlock::new);
    public static final RegistryObject<Block> DIRTY_SILTSTONE_BLOCK_HEAVY = register("dirty_siltstone_heavy", ModBlocks.SiltstoneBlock::new);
    public static final RegistryObject<Block> SMOOTH_SILTSTONE_BLOCK = register("smooth_siltstone", ModBlocks.SiltstoneBlock::new, true, LootTableType.EMPTY);
    public static final RegistryObject<Block> COBBLED_SILTSTONE_BLOCK = register("cobbled_siltstone", ModBlocks.SiltstoneBlock::new);
    public static final RegistryObject<Block> SNOWY_COBBLED_SILTSTONE_BLOCK = register("snowy_cobbled_siltstone", ModBlocks.SiltstoneBlock::new);
    public static final RegistryObject<Block> MOSSY_COBBLED_SILTSTONE_BLOCK = register("mossy_cobbled_siltstone", ModBlocks.SiltstoneBlock::new);
    public static final RegistryObject<Block> DIRTY_COBBLED_SILTSTONE_BLOCK = register("dirty_cobbled_siltstone", ModBlocks.SiltstoneBlock::new);
    public static final RegistryObject<Block> SILTSTONE_BRICKS_BLOCK = register("siltstone_bricks", ModBlocks.SiltstoneBlock::new);
    public static final RegistryObject<Block> SNOWY_SILTSTONE_BRICKS_SLIGHT_BLOCK = register("snowy_siltstone_bricks_slight", ModBlocks.SiltstoneBlock::new);
    public static final RegistryObject<Block> SNOWY_SILTSTONE_BRICKS_BLOCK = register("snowy_siltstone_bricks", ModBlocks.SiltstoneBlock::new);
    public static final RegistryObject<Block> SILTSTONE_TILES_BLOCK = register("siltstone_tiles", ModBlocks.SiltstoneBlock::new);
    public static final RegistryObject<Block> SILTSTONE_PAVEMENT_BLOCK = register("siltstone_pavement", ModBlocks.SiltstoneBlock::new);
    public static final RegistryObject<Block> CHISELED_SILTSTONE_BLOCK = register("chiseled_siltstone", ModBlocks.SiltstoneBlock::new);
    public static final RegistryObject<Block> SNOWY_CHISELED_SILTSTONE_BLOCK = register("snowy_chiseled_siltstone", ModBlocks.SiltstoneBlock::new);
    public static final RegistryObject<Block> DIRTY_CHISELED_SILTSTONE_BLOCK = register("dirty_chiseled_siltstone", ModBlocks.SiltstoneBlock::new);
    public static final RegistryObject<Block> CHISELED_SILTSTONE_BRICKS_BLOCK = register("chiseled_siltstone_bricks", ModBlocks.SiltstoneBlock::new);
    public static final RegistryObject<Block> SILTSTONE_PILLAR_BLOCK = register("siltstone_pillar", () -> pillar(SiltstoneProperties()));
    public static final RegistryObject<Block> SNOWY_SILTSTONE_PILLAR_BLOCK = register("snowy_siltstone_pillar", () -> pillar(SiltstoneProperties()));
    public static final RegistryObject<Block> RED_SILTSTONE_PILLAR_BLOCK = register("red_siltstone_pillar", () -> pillar(SiltstoneProperties().m_284180_(MapColor.f_283913_)));
    public static final RegistryObject<Block> BLUE_SILTSTONE_PILLAR_BLOCK = register("blue_siltstone_pillar", () -> pillar(SiltstoneProperties().m_284180_(MapColor.f_283772_)));
    public static final RegistryObject<Block> GREEN_SILTSTONE_PILLAR_BLOCK = register("green_siltstone_pillar", () -> pillar(SiltstoneProperties().m_284180_(MapColor.f_283916_)));
    public static final RegistryObject<Block> RED_MOSS_SILTSTONE = register("red_moss_siltstone", () -> new SnowyRedMossBlock(SiltstoneProperties().m_284180_(MapColor.f_283913_).m_60977_(), ((Block)COBBLED_SILTSTONE_BLOCK.get()).m_49966_()));
    public static final RegistryObject<Block> RED_MOSS_HIGHROCK = register("red_moss_highrock", () -> new SnowyRedMossBlock(HighrockProperties().m_284180_(MapColor.f_283913_).m_60977_(), ((Block)HIGHROCK_BLOCK.get()).m_49966_()));
    public static final RegistryObject<Block> RED_MOSS_DIRT = register("red_moss_dirt", () -> new SnowyRedMossBlock(Properties.m_60926_(Blocks.f_50440_).m_284180_(MapColor.f_283913_).m_60977_(), Blocks.f_50493_.m_49966_()));
    public static final RegistryObject<Block> RED_MOSS_BLOCK = register("red_moss_block", () -> new Block(Properties.m_60926_(Blocks.f_152544_).m_284180_(MapColor.f_283913_)));
    public static final RegistryObject<Block> SNOWY_RED_MOSS_BLOCK_SLIGHT = register("snowy_red_moss_block_slight", () -> new Block(Properties.m_60926_(Blocks.f_152544_).m_284180_(MapColor.f_283913_)));
    public static final RegistryObject<Block> SNOWY_RED_MOSS_BLOCK = register("snowy_red_moss_block", () -> new Block(Properties.m_60926_(Blocks.f_152544_).m_284180_(MapColor.f_283913_)));
    public static final RegistryObject<Block> SNOWY_RED_MOSS_BLOCK_HEAVY = register("snowy_red_moss_block_heavy", () -> new Block(Properties.m_60926_(Blocks.f_152544_).m_284180_(MapColor.f_283811_)));
    public static final RegistryObject<Block> OMINOUS_STONE_BLOCK = register("ominous_stone", ModBlocks.OminousStoneBlock::new, true, LootTableType.EMPTY);
    public static final RegistryObject<Block> DIRTY_OMINOUS_STONE_SLIGHT_BLOCK = register("dirty_ominous_stone_slight", ModBlocks.OminousStoneBlock::new);
    public static final RegistryObject<Block> DIRTY_OMINOUS_STONE_BLOCK = register("dirty_ominous_stone", ModBlocks.OminousStoneBlock::new);
    public static final RegistryObject<Block> COBBLED_OMINOUS_STONE_BLOCK = register("cobbled_ominous_stone", ModBlocks.OminousStoneBlock::new);
    public static final RegistryObject<Block> POLISHED_OMINOUS_STONE_BLOCK = register("polished_ominous_stone", ModBlocks.OminousStoneBlock::new);
    public static final RegistryObject<Block> POLISHED_DARK_OMINOUS_STONE_BLOCK = register("polished_dark_ominous_stone", ModBlocks.OminousStoneBlock::new);
    public static final RegistryObject<Block> CRACKED_POLISHED_OMINOUS_STONE_BLOCK = register("cracked_polished_ominous_stone", ModBlocks.OminousStoneBlock::new);
    public static final RegistryObject<Block> SOILED_POLISHED_OMINOUS_STONE_BLOCK = register("soiled_polished_ominous_stone", ModBlocks.OminousStoneBlock::new);
    public static final RegistryObject<Block> SOILED_POLISHED_OMINOUS_STONE_HEAVY_BLOCK = register("soiled_polished_ominous_stone_heavy", ModBlocks.OminousStoneBlock::new);
    public static final RegistryObject<Block> OMINOUS_STONE_BRICKS_BLOCK = register("ominous_stone_bricks", ModBlocks.OminousStoneBlock::new);
    public static final RegistryObject<Block> CRACKED_OMINOUS_STONE_BRICKS_BLOCK = register("cracked_ominous_stone_bricks", ModBlocks.OminousStoneBlock::new);
    public static final RegistryObject<Block> DIRTY_OMINOUS_STONE_BRICKS_SLIGHT_BLOCK = register("dirty_ominous_stone_bricks_slight", ModBlocks.OminousStoneBlock::new);
    public static final RegistryObject<Block> DIRTY_OMINOUS_STONE_BRICKS_BLOCK = register("dirty_ominous_stone_bricks", ModBlocks.OminousStoneBlock::new);
    public static final RegistryObject<Block> DIRTY_OMINOUS_STONE_BRICKS_HEAVY_BLOCK = register("dirty_ominous_stone_bricks_heavy", ModBlocks.OminousStoneBlock::new);
    public static final RegistryObject<Block> OMINOUS_STONE_TILES_BLOCK = register("ominous_stone_tiles", ModBlocks.OminousStoneBlock::new);
    public static final RegistryObject<Block> DIRTY_OMINOUS_STONE_TILES_SLIGHT_BLOCK = register("dirty_ominous_stone_tiles_slight", ModBlocks.OminousStoneBlock::new);
    public static final RegistryObject<Block> DIRTY_OMINOUS_STONE_TILES_BLOCK = register("dirty_ominous_stone_tiles", ModBlocks.OminousStoneBlock::new);
    public static final RegistryObject<Block> DIRTY_OMINOUS_STONE_TILES_HEAVY_BLOCK = register("dirty_ominous_stone_tiles_heavy", ModBlocks.OminousStoneBlock::new);
    public static final RegistryObject<Block> OMINOUS_STONE_PAVEMENT_BLOCK = register("ominous_stone_pavement", ModBlocks.OminousStoneBlock::new);
    public static final RegistryObject<Block> DIRTY_OMINOUS_STONE_PAVEMENT_BLOCK = register("dirty_ominous_stone_pavement", ModBlocks.OminousStoneBlock::new);
    public static final RegistryObject<Block> DIRTY_OMINOUS_STONE_PAVEMENT_HEAVY_BLOCK = register("dirty_ominous_stone_pavement_heavy", ModBlocks.OminousStoneBlock::new);
    public static final RegistryObject<Block> OMINOUS_STONE_PILLAR_BLOCK = register("ominous_stone_pillar", () -> pillar(OminousStoneProperties()));
    public static final RegistryObject<Block> CHISELED_OMINOUS_STONE_BLOCK = register("chiseled_ominous_stone", ModBlocks.OminousStoneBlock::new);
    public static final RegistryObject<Block> CHISELED_POLISHED_OMINOUS_STONE_BLOCK = register("chiseled_polished_ominous_stone", ModBlocks.OminousStoneBlock::new);
    public static final RegistryObject<Block> CHISELED_OMINOUS_STONE_BRICKS_BLOCK = register("chiseled_ominous_stone_bricks", ModBlocks.OminousStoneBlock::new);
    public static final RegistryObject<Block> COBBLED_OMINOUS_STONE_PATH_BLOCK = register("cobbled_ominous_stone_path", () -> new BlockPathBlock(OminousStoneProperties().m_60971_(ModBlocks::always).m_60960_(ModBlocks::always), ((Block)COBBLED_OMINOUS_STONE_BLOCK.get()).m_49966_()), true, LootTableType.EMPTY);
    public static final RegistryObject<Block> SNOW_BRICKS_BLOCK = register("snow_bricks", ModBlocks.SnowBrickBlock::new);
    public static final RegistryObject<Block> BIG_END_STONE_BRICKS_BLOCK = register("big_end_stone_bricks", ModBlocks.EndStoneBlock::new);
    public static final RegistryObject<Block> MESSY_END_STONE_BRICKS_BLOCK = register("messy_end_stone_bricks", ModBlocks.EndStoneBlock::new);
    public static final RegistryObject<Block> CHISELED_END_STONE_BLOCK = register("chiseled_end_stone", ModBlocks.EndStoneBlock::new);
    public static final RegistryObject<Block> CHISELED_END_STONE_BRICKS_BLOCK = register("chiseled_end_stone_bricks", ModBlocks.EndStoneBlock::new);
    public static final RegistryObject<Block> SOILED_END_STONE_BRICKS_SLIGHT_BLOCK = register("soiled_end_stone_bricks_slight", ModBlocks.EndStoneBlock::new);
    public static final RegistryObject<Block> SOILED_END_STONE_BRICKS_BLOCK = register("soiled_end_stone_bricks", ModBlocks.EndStoneBlock::new);
    public static final RegistryObject<Block> SOILED_END_STONE_BRICKS_HEAVY_BLOCK = register("soiled_end_stone_bricks_heavy", () -> new Block(EndStoneProperties().m_284180_(MapColor.f_283818_)));
    public static final RegistryObject<Block> GRASSY_END_STONE_BRICKS_BLOCK = register("grassy_end_stone_bricks", ModBlocks.EndStoneBlock::new);
    public static final RegistryObject<Block> GRASSY_END_STONE_BRICKS_HEAVY_BLOCK = register("grassy_end_stone_bricks_heavy", () -> new Block(EndStoneProperties().m_284180_(MapColor.f_283750_)));
    public static final RegistryObject<Block> END_STONE_TILES_BLOCK = register("end_stone_tiles", ModBlocks.EndStoneBlock::new);
    public static final RegistryObject<Block> SOILED_END_STONE_TILES_BLOCK = register("soiled_end_stone_tiles", ModBlocks.EndStoneBlock::new);
    public static final RegistryObject<Block> GRASSY_END_STONE_TILES_BLOCK = register("grassy_end_stone_tiles", ModBlocks.EndStoneBlock::new);
    public static final RegistryObject<Block> SMOOTH_END_STONE_BLOCK = register("smooth_end_stone", ModBlocks.EndStoneBlock::new);
    public static final RegistryObject<Block> MUDDY_SMOOTH_END_STONE_BLOCK = register("muddy_smooth_end_stone", ModBlocks.EndStoneBlock::new);
    public static final RegistryObject<Block> GRASSY_SMOOTH_END_STONE_BLOCK = register("grassy_smooth_end_stone", ModBlocks.EndStoneBlock::new);
    public static final RegistryObject<Block> GRASSY_SMOOTH_END_STONE_HEAVY_BLOCK = register("grassy_smooth_end_stone_heavy", () -> new Block(EndStoneProperties().m_284180_(MapColor.f_283750_)));
    public static final RegistryObject<Block> MIXED_END_STONE_BLOCK = register("mixed_end_stone", ModBlocks.EndStoneBlock::new);
    public static final RegistryObject<Block> END_STONE_SLATE_BLOCK = register("end_stone_slate", () -> new RotatedPillarBlock(EndStoneProperties()), true, LootTableType.EMPTY);
    public static final RegistryObject<Block> END_STONE_SLATE_ROCK = register("end_stone_slate_rock", ModBlocks.EndStoneBlock::new);
    public static final RegistryObject<Block> COBBLED_END_STONE_BLOCK = register("cobbled_end_stone", ModBlocks.EndStoneBlock::new);
    public static final RegistryObject<Block> SOILED_COBBLED_END_STONE_SLIGHT_BLOCK = register("soiled_cobbled_end_stone_slight", ModBlocks.EndStoneBlock::new);
    public static final RegistryObject<Block> SOILED_COBBLED_END_STONE_BLOCK = register("soiled_cobbled_end_stone", ModBlocks.EndStoneBlock::new);
    public static final RegistryObject<Block> SOILED_COBBLED_END_STONE_HEAVY_BLOCK = register("soiled_cobbled_end_stone_heavy", () -> new Block(EndStoneProperties().m_284180_(MapColor.f_283818_)));
    public static final RegistryObject<Block> GRASSY_COBBLED_END_STONE_BLOCK = register("grassy_cobbled_end_stone", ModBlocks.EndStoneBlock::new);
    public static final RegistryObject<Block> GROWN_COBBLED_END_STONE_BLOCK = register("grown_cobbled_end_stone", ModBlocks.EndStoneBlock::new);
    public static final RegistryObject<Block> DIRTY_COBBLED_END_STONE_BLOCK = register("dirty_cobbled_end_stone", ModBlocks.EndStoneBlock::new);
    public static final RegistryObject<Block> DIRTY_COBBLED_END_STONE_HEAVY_BLOCK = register("dirty_cobbled_end_stone_heavy", ModBlocks.EndStoneBlock::new);
    public static final RegistryObject<Block> END_STONE_PILLAR_BLOCK = register("end_stone_pillar", () -> new RotatedPillarBlock(EndStoneProperties()));
    public static final RegistryObject<Block> CHORUS_END_STONE_PILLAR_BLOCK = register("chorus_end_stone_pillar", () -> new RotatedPillarBlock(EndStoneProperties()));
    public static final RegistryObject<Block> INFUSED_END_STONE_PILLAR_BLOCK = register("infused_end_stone_pillar", () -> new RotatedPillarBlock(EndStoneProperties().m_284180_(MapColor.f_283812_)));
    public static final RegistryObject<Block> CHORUS_GRASS_BLOCK = register("chorus_grass_block", ChorusGrassBlock::new, true, LootTableType.EMPTY);
    public static final RegistryObject<Block> CHORUS_GRASS_DIRT = register("chorus_grass_dirt", () -> new ChorusGrassBlock(Properties.m_60926_((BlockBehaviour)END_DIRT.get()).m_284180_(MapColor.f_283750_).m_60977_().m_60918_(SoundType.f_56740_)), true, LootTableType.EMPTY);
    public static final RegistryObject<Block> COBBLED_CHORUS_GRASS_BLOCK = register("cobbled_chorus_grass_block", () -> new Block(EndStoneProperties().m_284180_(MapColor.f_283750_)));
    public static final RegistryObject<Block> END_ROD_BLOCK = register("end_rod_block", () -> new Block(Properties.m_284310_().m_60953_((state) -> 14).m_60918_(SoundType.f_56736_).m_284180_(MapColor.f_283761_).m_280658_(NoteBlockInstrument.PLING).m_60978_(2.0F).m_60924_(ModBlocks::never)));
    public static final RegistryObject<Block> END_LAMP_BLOCK = register("end_lamp", () -> new Block(Properties.m_284310_().m_60953_((state) -> 15).m_60978_(0.3F).m_60918_(SoundType.f_56744_).m_284180_(MapColor.f_283778_).m_60922_(ModBlocks::always)));
    public static final RegistryObject<Block> SOILED_PURPUR_BLOCK = register("soiled_purpur_block", () -> new Block(Properties.m_60926_(Blocks.f_50492_)));
    public static final RegistryObject<Block> GRASSY_PURPUR_BLOCK = register("grassy_purpur_block", () -> new Block(Properties.m_60926_(Blocks.f_50492_)));
    public static final RegistryObject<Block> PURPUR_LAMP_BLOCK = register("purpur_lamp", () -> new PurpurLampBlock(Properties.m_60926_(Blocks.f_50492_)));
    public static final RegistryObject<Block> PURPUR_END_ROD_BLOCK = register("purpur_end_rod_block", () -> new PurpurEndRodBlock(Properties.m_60926_(Blocks.f_50492_).m_60953_((state) -> 14)));
    public static final RegistryObject<Block> SHADE_STONE_SLAB_BLOCK = registerShadeSlabs("shade_stone_slab");
    public static final RegistryObject<Block> SHADE_STONE_POLISHED_SLAB_BLOCK = registerShadeSlabs("shade_stone_polished_slab");
    public static final RegistryObject<Block> SHADE_STONE_BRICK_SLAB_BLOCK = registerShadeSlabs("shade_stone_bricks_slab");
    public static final RegistryObject<Block> SHADE_BRICK_SLAB_BLOCK = registerShadeSlabs("shade_bricks_slab");
    public static final RegistryObject<Block> SHADE_TILES_SLAB_BLOCK = registerShadeSlabs("shade_tiles_slab");
    public static final RegistryObject<Block> CRYPT_STONE_SLAB_BLOCK = registerCryptSlabs("crypt_stone_slab");
    public static final RegistryObject<Block> CRYPT_STONE_POLISHED_SLAB_BLOCK = registerCryptSlabs("crypt_stone_polished_slab");
    public static final RegistryObject<Block> CRYPT_BRICKS_SLAB_BLOCK = registerCryptSlabs("crypt_bricks_slab");
    public static final RegistryObject<Block> CRYPT_TILES_SLAB_BLOCK = registerCryptSlabs("crypt_tiles_slab");
    public static final RegistryObject<Block> MARBLE_SLAB = registerSlabs("marble_slab", MARBLE_BLOCK);
    public static final RegistryObject<Block> CRACKED_MARBLE_SLAB = registerSlabs("cracked_marble_slab", CRACKED_MARBLE_BLOCK);
    public static final RegistryObject<Block> SMOOTH_MARBLE_SLAB = registerSlabs("smooth_marble_slab", MARBLE_BLOCK);
    public static final RegistryObject<Block> SLATE_MARBLE_SLAB = registerSlabs("slate_marble_slab", SLATE_MARBLE_BLOCK);
    public static final RegistryObject<Block> CRAGROCKS_SLAB = registerSlabs("cragrocks_slab", CRAGROCKS_BLOCK);
    public static final RegistryObject<Block> MOSSY_CRAGROCKS_SLIGHT_SLAB = registerSlabs("mossy_cragrocks_slight_slab", MOSSY_CRAGROCKS_SLIGHT_BLOCK);
    public static final RegistryObject<Block> MOSSY_CRAGROCKS_SLAB = registerSlabs("mossy_cragrocks_slab", MOSSY_CRAGROCKS_BLOCK);
    public static final RegistryObject<Block> MOSSY_CRAGROCKS_HEAVY_SLAB = registerSlabs("mossy_cragrocks_heavy_slab", MOSSY_CRAGROCKS_HEAVY_BLOCK);
    public static final RegistryObject<Block> CRAG_TILE_SLAB = registerSlabs("crag_tile_slab", CRAG_TILES_BLOCK);
    public static final RegistryObject<Block> CRAG_BRICK_SLAB = registerSlabs("crag_brick_slab", CRAG_BRICKS_BLOCK);
    public static final RegistryObject<Block> SNOWY_CRAG_BRICK_SLAB = registerSlabs("snowy_crag_brick_slab", SNOWY_CRAG_BRICKS_BLOCK);
    public static final RegistryObject<Block> CRAG_PAVEMENT_SLAB = registerSlabs("crag_pavement_slab", CRAG_PAVEMENT_BLOCK);
    public static final RegistryObject<Block> CRACKED_CRAG_PAVEMENT_SLAB = registerSlabs("cracked_crag_pavement_slab", CRACKED_CRAG_PAVEMENT_BLOCK);
    public static final RegistryObject<Block> HIGHROCK_SLAB = registerSlabs("highrock_slab", HIGHROCK_BLOCK);
    public static final RegistryObject<Block> POLISHED_HIGHROCK_SLAB = registerSlabs("polished_highrock_slab", POLISHED_HIGHROCK_BLOCK);
    public static final RegistryObject<Block> HIGHROCK_BRICK_SLAB = registerSlabs("highrock_brick_slab", HIGHROCK_BRICKS_BLOCK);
    public static final RegistryObject<Block> SNOWY_HIGHROCK_BRICK_SLIGHT_SLAB = registerSlabs("snowy_highrock_brick_slight_slab", SNOWY_HIGHROCK_BRICKS_SLIGHT_BLOCK);
    public static final RegistryObject<Block> SNOWY_HIGHROCK_BRICK_SLAB = registerSlabs("snowy_highrock_brick_slab", SNOWY_HIGHROCK_BRICKS_BLOCK);
    public static final RegistryObject<Block> SILT_HIGHROCK_SLAB = registerSlabs("silt_highrock_slab", SILT_HIGHROCK_BLOCK);
    public static final RegistryObject<Block> SILTSTONE_SLAB = registerSlabs("siltstone_slab", SILTSTONE_BLOCK);
    public static final RegistryObject<Block> SMOOTH_SILTSTONE_SLAB = registerSlabs("smooth_siltstone_slab", SMOOTH_SILTSTONE_BLOCK);
    public static final RegistryObject<Block> COBBLED_SILTSTONE_SLAB = registerSlabs("cobbled_siltstone_slab", COBBLED_SILTSTONE_BLOCK);
    public static final RegistryObject<Block> SNOWY_COBBLED_SILTSTONE_SLAB = registerSlabs("snowy_cobbled_siltstone_slab", SNOWY_COBBLED_SILTSTONE_BLOCK);
    public static final RegistryObject<Block> SILTSTONE_BRICK_SLAB = registerSlabs("siltstone_brick_slab", SILTSTONE_BRICKS_BLOCK);
    public static final RegistryObject<Block> SNOWY_SILTSTONE_BRICK_SLIGHT_SLAB = registerSlabs("snowy_siltstone_brick_slight_slab", SNOWY_SILTSTONE_BRICKS_SLIGHT_BLOCK);
    public static final RegistryObject<Block> SNOWY_SILTSTONE_BRICK_SLAB = registerSlabs("snowy_siltstone_brick_slab", SNOWY_SILTSTONE_BRICKS_BLOCK);
    public static final RegistryObject<Block> SILTSTONE_TILE_SLAB = registerSlabs("siltstone_tile_slab", SILTSTONE_TILES_BLOCK);
    public static final RegistryObject<Block> SILTSTONE_PAVEMENT_SLAB = registerSlabs("siltstone_pavement_slab", SILTSTONE_PAVEMENT_BLOCK);
    public static final RegistryObject<Block> INDENTED_GOLD_SLAB = registerSlabs("indented_gold_slab", INDENTED_GOLD_BLOCK);
    public static final RegistryObject<Block> RED_MOSS_SLAB = registerSlabs("red_moss_slab", RED_MOSS_BLOCK);
    public static final RegistryObject<Block> SNOWY_RED_MOSS_SLIGHT_SLAB = registerSlabs("snowy_red_moss_slight_slab", SNOWY_RED_MOSS_BLOCK_SLIGHT);
    public static final RegistryObject<Block> SNOWY_RED_MOSS_SLAB = registerSlabs("snowy_red_moss_slab", SNOWY_RED_MOSS_BLOCK);
    public static final RegistryObject<Block> SNOWY_RED_MOSS_HEAVY_SLAB = registerSlabs("snowy_red_moss_heavy_slab", SNOWY_RED_MOSS_BLOCK_HEAVY);
    public static final RegistryObject<Block> OMINOUS_STONE_SLAB = registerSlabs("ominous_stone_slab", OMINOUS_STONE_BLOCK);
    public static final RegistryObject<Block> COBBLED_OMINOUS_STONE_SLAB = registerSlabs("cobbled_ominous_stone_slab", COBBLED_OMINOUS_STONE_BLOCK);
    public static final RegistryObject<Block> POLISHED_OMINOUS_STONE_SLAB = registerSlabs("polished_ominous_stone_slab", POLISHED_OMINOUS_STONE_BLOCK);
    public static final RegistryObject<Block> POLISHED_DARK_OMINOUS_STONE_SLAB = registerSlabs("polished_dark_ominous_stone_slab", POLISHED_DARK_OMINOUS_STONE_BLOCK);
    public static final RegistryObject<Block> OMINOUS_STONE_BRICK_SLAB = registerSlabs("ominous_stone_brick_slab", OMINOUS_STONE_BRICKS_BLOCK);
    public static final RegistryObject<Block> OMINOUS_STONE_TILE_SLAB = registerSlabs("ominous_stone_tile_slab", OMINOUS_STONE_TILES_BLOCK);
    public static final RegistryObject<Block> OMINOUS_STONE_PAVEMENT_SLAB = registerSlabs("ominous_stone_pavement_slab", OMINOUS_STONE_PAVEMENT_BLOCK);
    public static final RegistryObject<Block> OMINOUS_STONE_PILLAR_SLAB = registerSlabs("ominous_stone_pillar_slab", OMINOUS_STONE_PILLAR_BLOCK);
    public static final RegistryObject<Block> DARK_DIRT_SLAB = registerSlabs("dark_dirt_slab", DARK_DIRT);
    public static final RegistryObject<Block> SNOW_BRICK_SLAB = registerSlabs("snow_brick_slab", SNOW_BRICKS_BLOCK);
    public static final RegistryObject<Block> END_STONE_SLAB = registerSlabs("end_stone_slab", Blocks.f_50259_);
    public static final RegistryObject<Block> END_STONE_TILE_SLAB = registerSlabs("end_stone_tile_slab", END_STONE_TILES_BLOCK);
    public static final RegistryObject<Block> SMOOTH_END_STONE_SLAB = registerSlabs("smooth_end_stone_slab", SMOOTH_END_STONE_BLOCK);
    public static final RegistryObject<Block> COBBLED_END_STONE_SLAB = registerSlabs("cobbled_end_stone_slab", COBBLED_END_STONE_BLOCK);
    public static final RegistryObject<Block> CHORUS_GRASS_BLOCK_SLAB = registerSlabs("chorus_grass_block_slab", CHORUS_GRASS_BLOCK);
    public static final RegistryObject<Block> CHORUS_GRASS_SLAB = register("chorus_grass_slab", () -> new SlabBlock(Properties.m_284310_().m_280658_(NoteBlockInstrument.BASEDRUM).m_60978_(0.6F).m_60918_(SoundType.f_56740_).m_284180_(MapColor.f_283750_)));
    public static final RegistryObject<Block> SHADE_STONE_STAIRS_BLOCK = registerStairs("shade_stone_stairs", SHADE_STONE_BLOCK);
    public static final RegistryObject<Block> SHADE_STONE_POLISHED_STAIRS_BLOCK = registerStairs("shade_stone_polished_stairs", SHADE_STONE_POLISHED_BLOCK);
    public static final RegistryObject<Block> SHADE_STONE_BRICK_STAIRS_BLOCK = registerStairs("shade_stone_bricks_stairs", SHADE_STONE_BRICK_BLOCK);
    public static final RegistryObject<Block> SHADE_BRICK_STAIRS_BLOCK = registerStairs("shade_bricks_stairs", SHADE_BRICK_BLOCK);
    public static final RegistryObject<Block> SHADE_TILES_STAIRS_BLOCK = registerStairs("shade_tiles_stairs", SHADE_TILES_BLOCK);
    public static final RegistryObject<Block> CRYPT_STONE_STAIRS_BLOCK = registerStairs("crypt_stone_stairs", CRYPT_STONE_BLOCK);
    public static final RegistryObject<Block> CRYPT_STONE_POLISHED_STAIRS_BLOCK = registerStairs("crypt_stone_polished_stairs", CRYPT_STONE_POLISHED_BLOCK);
    public static final RegistryObject<Block> CRYPT_BRICKS_STAIRS_BLOCK = registerStairs("crypt_bricks_stairs", CRYPT_BRICKS_BLOCK);
    public static final RegistryObject<Block> CRYPT_TILES_STAIRS_BLOCK = registerStairs("crypt_tiles_stairs", CRYPT_TILES_BLOCK);
    public static final RegistryObject<Block> MARBLE_STAIRS_BLOCK = registerStairs("marble_stairs", MARBLE_BLOCK);
    public static final RegistryObject<Block> SLATE_MARBLE_STAIRS_BLOCK = registerStairs("slate_marble_stairs", SLATE_MARBLE_BLOCK);
    public static final RegistryObject<Block> CRAGROCKS_STAIRS = registerStairs("cragrocks_stairs", CRAGROCKS_BLOCK);
    public static final RegistryObject<Block> CRAG_TILE_STAIRS = registerStairs("crag_tile_stairs", CRAG_TILES_BLOCK);
    public static final RegistryObject<Block> CRAG_BRICK_STAIRS = registerStairs("crag_brick_stairs", CRAG_BRICKS_BLOCK);
    public static final RegistryObject<Block> SNOWY_CRAG_BRICK_STAIRS = registerStairs("snowy_crag_brick_stairs", SNOWY_CRAG_BRICKS_BLOCK);
    public static final RegistryObject<Block> HIGHROCK_STAIRS = registerStairs("highrock_stairs", HIGHROCK_BLOCK);
    public static final RegistryObject<Block> POLISHED_HIGHROCK_STAIRS = registerStairs("polished_highrock_stairs", POLISHED_HIGHROCK_BLOCK);
    public static final RegistryObject<Block> HIGHROCK_BRICK_STAIRS = registerStairs("highrock_brick_stairs", HIGHROCK_BRICKS_BLOCK);
    public static final RegistryObject<Block> SNOWY_HIGHROCK_BRICK_SLIGHT_STAIRS = registerStairs("snowy_highrock_brick_slight_stairs", SNOWY_HIGHROCK_BRICKS_SLIGHT_BLOCK);
    public static final RegistryObject<Block> SNOWY_HIGHROCK_BRICK_STAIRS = registerStairs("snowy_highrock_brick_stairs", SNOWY_HIGHROCK_BRICKS_BLOCK);
    public static final RegistryObject<Block> SILTSTONE_STAIRS = registerStairs("siltstone_stairs", SILTSTONE_BLOCK);
    public static final RegistryObject<Block> SMOOTH_SILTSTONE_STAIRS = registerStairs("smooth_siltstone_stairs", SMOOTH_SILTSTONE_BLOCK);
    public static final RegistryObject<Block> COBBLED_SILTSTONE_STAIRS = registerStairs("cobbled_siltstone_stairs", COBBLED_SILTSTONE_BLOCK);
    public static final RegistryObject<Block> SNOWY_COBBLED_SILTSTONE_STAIRS = registerStairs("snowy_cobbled_siltstone_stairs", SNOWY_COBBLED_SILTSTONE_BLOCK);
    public static final RegistryObject<Block> SILTSTONE_TILE_STAIRS = registerStairs("siltstone_tile_stairs", SILTSTONE_TILES_BLOCK);
    public static final RegistryObject<Block> SILTSTONE_BRICK_STAIRS = registerStairs("siltstone_brick_stairs", SILTSTONE_BRICKS_BLOCK);
    public static final RegistryObject<Block> SNOWY_SILTSTONE_BRICK_SLIGHT_STAIRS = registerStairs("snowy_siltstone_brick_slight_stairs", SNOWY_SILTSTONE_BRICKS_SLIGHT_BLOCK);
    public static final RegistryObject<Block> SNOWY_SILTSTONE_BRICK_STAIRS = registerStairs("snowy_siltstone_brick_stairs", SNOWY_SILTSTONE_BRICKS_BLOCK);
    public static final RegistryObject<Block> SILTSTONE_PAVEMENT_STAIRS = registerStairs("siltstone_pavement_stairs", SILTSTONE_PAVEMENT_BLOCK);
    public static final RegistryObject<Block> INDENTED_GOLD_STAIRS_BLOCK = registerStairs("indented_gold_stairs", INDENTED_GOLD_BLOCK);
    public static final RegistryObject<Block> OMINOUS_STONE_STAIRS = registerStairs("ominous_stone_stairs", OMINOUS_STONE_BLOCK);
    public static final RegistryObject<Block> COBBLED_OMINOUS_STONE_STAIRS = registerStairs("cobbled_ominous_stone_stairs", COBBLED_OMINOUS_STONE_BLOCK);
    public static final RegistryObject<Block> POLISHED_OMINOUS_STONE_STAIRS = registerStairs("polished_ominous_stone_stairs", POLISHED_OMINOUS_STONE_BLOCK);
    public static final RegistryObject<Block> POLISHED_DARK_OMINOUS_STONE_STAIRS = registerStairs("polished_dark_ominous_stone_stairs", POLISHED_DARK_OMINOUS_STONE_BLOCK);
    public static final RegistryObject<Block> OMINOUS_STONE_BRICK_STAIRS = registerStairs("ominous_stone_brick_stairs", OMINOUS_STONE_BRICKS_BLOCK);
    public static final RegistryObject<Block> OMINOUS_STONE_TILE_STAIRS = registerStairs("ominous_stone_tile_stairs", OMINOUS_STONE_TILES_BLOCK);
    public static final RegistryObject<Block> OMINOUS_STONE_PAVEMENT_STAIRS = registerStairs("ominous_stone_pavement_stairs", OMINOUS_STONE_PAVEMENT_BLOCK);
    public static final RegistryObject<Block> SNOW_BRICK_STAIRS_BLOCK = registerStairs("snow_brick_stairs", SNOW_BRICKS_BLOCK);
    public static final RegistryObject<Block> END_STONE_TILE_STAIRS_BLOCK = registerStairs("end_stone_tile_stairs", END_STONE_TILES_BLOCK);
    public static final RegistryObject<Block> SMOOTH_END_STONE_STAIRS_BLOCK = registerStairs("smooth_end_stone_stairs", SMOOTH_END_STONE_BLOCK);
    public static final RegistryObject<Block> COBBLED_END_STONE_STAIRS_BLOCK = registerStairs("cobbled_end_stone_stairs", COBBLED_END_STONE_BLOCK);
    public static final RegistryObject<Block> SHADE_BRICK_WALL_BLOCK = registerWalls("shade_bricks_wall", SHADE_BRICK_BLOCK);
    public static final RegistryObject<Block> SHADE_STONE_BRICK_WALL_BLOCK = registerWalls("shade_stone_bricks_wall", SHADE_STONE_BRICK_BLOCK);
    public static final RegistryObject<Block> CRYPT_STONE_POLISHED_WALL_BLOCK = registerWalls("crypt_stone_polished_wall", CRYPT_STONE_POLISHED_BLOCK);
    public static final RegistryObject<Block> CRYPT_BRICKS_WALL_BLOCK = registerWalls("crypt_bricks_wall", CRYPT_BRICKS_BLOCK);
    public static final RegistryObject<Block> CRYPT_TILES_WALL_BLOCK = registerWalls("crypt_tiles_wall", CRYPT_TILES_BLOCK);
    public static final RegistryObject<Block> CRAGROCKS_WALL_BLOCK = registerWalls("cragrocks_wall", CRAGROCKS_BLOCK);
    public static final RegistryObject<Block> CRAG_TILE_WALL_BLOCK = registerWalls("crag_tile_wall", CRAG_TILES_BLOCK);
    public static final RegistryObject<Block> CRAG_BRICK_WALL_BLOCK = registerWalls("crag_brick_wall", CRAG_BRICKS_BLOCK);
    public static final RegistryObject<Block> SNOWY_CRAG_BRICK_WALL_BLOCK = registerWalls("snowy_crag_brick_wall", SNOWY_CRAG_BRICKS_BLOCK);
    public static final RegistryObject<Block> CRAGROCKS_FENCE = register("cragrocks_fence", () -> new FenceBlock(Properties.m_60926_((BlockBehaviour)CRAGROCKS_BLOCK.get())));
    public static final RegistryObject<Block> HIGHROCK_WALL_BLOCK = registerWalls("highrock_wall", HIGHROCK_BLOCK);
    public static final RegistryObject<Block> POLISHED_HIGHROCK_WALL_BLOCK = registerWalls("polished_highrock_wall", POLISHED_HIGHROCK_BLOCK);
    public static final RegistryObject<Block> HIGHROCK_BRICK_WALL_BLOCK = registerWalls("highrock_brick_wall", HIGHROCK_BRICKS_BLOCK);
    public static final RegistryObject<Block> SNOWY_HIGHROCK_BRICK_SLIGHT_WALL_BLOCK = registerWalls("snowy_highrock_brick_slight_wall", SNOWY_HIGHROCK_BRICKS_SLIGHT_BLOCK);
    public static final RegistryObject<Block> SNOWY_HIGHROCK_BRICK_WALL_BLOCK = registerWalls("snowy_highrock_brick_wall", SNOWY_HIGHROCK_BRICKS_BLOCK);
    public static final RegistryObject<Block> SILTSTONE_WALL_BLOCK = registerWalls("siltstone_wall", SILTSTONE_BLOCK);
    public static final RegistryObject<Block> SMOOTH_SILTSTONE_WALL_BLOCK = registerWalls("smooth_siltstone_wall", SMOOTH_SILTSTONE_BLOCK);
    public static final RegistryObject<Block> COBBLED_SILTSTONE_WALL_BLOCK = registerWalls("cobbled_siltstone_wall", COBBLED_SILTSTONE_BLOCK);
    public static final RegistryObject<Block> SNOWY_COBBLED_SILTSTONE_WALL_BLOCK = registerWalls("snowy_cobbled_siltstone_wall", SNOWY_COBBLED_SILTSTONE_BLOCK);
    public static final RegistryObject<Block> SILTSTONE_BRICK_WALL_BLOCK = registerWalls("siltstone_brick_wall", SILTSTONE_BRICKS_BLOCK);
    public static final RegistryObject<Block> SNOWY_SILTSTONE_BRICK_SLIGHT_WALL_BLOCK = registerWalls("snowy_siltstone_brick_slight_wall", SNOWY_SILTSTONE_BRICKS_SLIGHT_BLOCK);
    public static final RegistryObject<Block> SNOWY_SILTSTONE_BRICK_WALL_BLOCK = registerWalls("snowy_siltstone_brick_wall", SNOWY_SILTSTONE_BRICKS_BLOCK);
    public static final RegistryObject<Block> SILTSTONE_TILE_WALL_BLOCK = registerWalls("siltstone_tile_wall", SILTSTONE_TILES_BLOCK);
    public static final RegistryObject<Block> SILTSTONE_PAVEMENT_WALL_BLOCK = registerWalls("siltstone_pavement_wall", SILTSTONE_PAVEMENT_BLOCK);
    public static final RegistryObject<Block> INDENTED_GOLD_WALL_BLOCK = registerWalls("indented_gold_wall", INDENTED_GOLD_BLOCK);
    public static final RegistryObject<Block> GOLD_TRAPDOOR = register("gold_trapdoor", () -> new TrapDoorBlock(Properties.m_284310_().m_284180_(MapColor.f_283757_).m_60999_().m_60978_(5.0F).m_60955_().m_60922_(ModBlocks::never), ModBlockSetType.MOD_METAL));
    public static final RegistryObject<Block> COBBLED_OMINOUS_STONE_WALL_BLOCK = registerWalls("cobbled_ominous_stone_wall", COBBLED_OMINOUS_STONE_BLOCK);
    public static final RegistryObject<Block> POLISHED_OMINOUS_STONE_WALL_BLOCK = registerWalls("polished_ominous_stone_wall", POLISHED_OMINOUS_STONE_BLOCK);
    public static final RegistryObject<Block> POLISHED_DARK_OMINOUS_STONE_WALL_BLOCK = registerWalls("polished_dark_ominous_stone_wall", POLISHED_DARK_OMINOUS_STONE_BLOCK);
    public static final RegistryObject<Block> OMINOUS_STONE_BRICK_WALL_BLOCK = registerWalls("ominous_stone_brick_wall", OMINOUS_STONE_BRICKS_BLOCK);
    public static final RegistryObject<Block> OMINOUS_STONE_TILE_WALL_BLOCK = registerWalls("ominous_stone_tile_wall", OMINOUS_STONE_TILES_BLOCK);
    public static final RegistryObject<Block> SNOW_BRICK_WALL_BLOCK = registerWalls("snow_brick_wall", SNOW_BRICKS_BLOCK);
    public static final RegistryObject<Block> SMOOTH_END_STONE_WALL_BLOCK = registerWalls("smooth_end_stone_wall", SMOOTH_END_STONE_BLOCK);
    public static final RegistryObject<Block> COBBLED_END_STONE_WALL_BLOCK = registerWalls("cobbled_end_stone_wall", COBBLED_END_STONE_BLOCK);
    public static final RegistryObject<Block> SMOOTH_END_STONE_FENCE = register("smooth_end_stone_fence", () -> new FenceBlock(Properties.m_60926_((BlockBehaviour)SMOOTH_END_STONE_BLOCK.get())));
    public static final RegistryObject<Block> PURPUR_WALL = register("purpur_wall", () -> new WallBlock(Properties.m_60926_(Blocks.f_50492_)));
    public static final RegistryObject<Block> CURSED_BARS_BLOCK = register("cursed_bars", () -> new IronBarsBlock(Properties.m_284310_().m_284180_(MapColor.f_283906_).m_60999_().m_60913_(5.0F, 6.0F).m_60918_(SoundType.f_56743_).m_60955_()));
    public static final RegistryObject<Block> SHADE_GLASS_PANE = register("shade_glass_pane", () -> new IronBarsBlock(Properties.m_60926_(Blocks.f_50185_)));
    public static final RegistryObject<Block> RUSTY_IRON_BARS_BLOCK = register("rusty_iron_bars", () -> new IronBarsBlock(Properties.m_284310_().m_284180_(MapColor.f_283819_).m_60999_().m_60913_(5.0F, 6.0F).m_60918_(ModSoundTypes.MOD_METAL).m_60955_()));
    public static final RegistryObject<PlushieBlock> PLUSHIE = curioIsterRegister("plushie", PlushieBlock::new);
    public static final RegistryObject<PlushieBlock> PLUSHIE_1 = curioIsterRegister("plushie_1", () -> new PlushieBlock(1));
    public static final RegistryObject<PlushieBlock> PLUSHIE_2 = curioIsterRegister("plushie_2", () -> new PlushieBlock(2));
    public static final RegistryObject<PlushieBlock> PLUSHIE_3 = curioIsterRegister("plushie_3", () -> new PlushieBlock(3));
    public static final RegistryObject<PlushieBlock> PLUSHIE_4 = curioIsterRegister("plushie_4", () -> new PlushieBlock(4));
    public static final RegistryObject<PlushieBlock> PLUSHIE_5 = curioIsterRegister("plushie_5", () -> new PlushieBlock(5));
    public static final RegistryObject<PlushieBlock> PLUSHIE_6 = curioIsterRegister("plushie_6", () -> new PlushieBlock(6));
    public static final RegistryObject<PlushieBlock> PLUSHIE_7 = curioIsterRegister("plushie_7", () -> new PlushieBlock(7));
    public static final RegistryObject<PlushieBlock> PLUSHIE_8 = curioIsterRegister("plushie_8", () -> new PlushieBlock(8));
    public static final RegistryObject<PlushieBlock> PLUSHIE_9 = curioIsterRegister("plushie_9", () -> new PlushieBlock(9));
    public static final RegistryObject<PlushieBlock> PLUSHIE_10 = curioIsterRegister("plushie_10", () -> new PlushieBlock(10));
    public static final RegistryObject<SculpturedStatueBlock> SCULPTURED_STATUE = isterRegister("sculptured_statue", SculpturedStatueBlock::new);
    public static final RegistryObject<SculpturedStatueBlock> STATUE_1 = isterRegister("sculptured_statue_1", () -> new SculpturedStatueBlock(1));
    public static final RegistryObject<SculpturedStatueBlock> STATUE_2 = isterRegister("sculptured_statue_2", () -> new SculpturedStatueBlock(2));
    public static final RegistryObject<SculpturedStatueBlock> STATUE_3 = isterRegister("sculptured_statue_3", () -> new SculpturedStatueBlock(3, true));
    public static final RegistryObject<SculpturedStatueBlock> STATUE_4 = isterRegister("sculptured_statue_4", () -> new SculpturedStatueBlock(4));
    public static final RegistryObject<Item> SNAP_WARTS_ITEM = ModItems.ITEMS.register("snap_warts", () -> new ItemNameBlockItem((Block)SNAP_WARTS.get(), new Item.Properties()));
    public static final RegistryObject<Item> HENBANE_SEEDS = ModItems.ITEMS.register("henbane_seeds", () -> new ItemNameBlockItem((Block)HENBANE.get(), new Item.Properties()));
    public static final RegistryObject<Item> NIGHTSHADE_SEEDS = ModItems.ITEMS.register("nightshade_seeds", () -> new ItemNameBlockItem((Block)DEADLY_NIGHTSHADE.get(), new Item.Properties()));
    public static final RegistryObject<Item> FIRETHORN_BERRIES = ModItems.ITEMS.register("firethorn_berries", () -> new ItemNameBlockItem((Block)FIRETHORN.get(), (new Item.Properties()).m_41489_(Foods.f_38808_)));
    public static final RegistryObject<Item> IRON_DUNGEON_TORCH_ITEM = ModItems.ITEMS.register("iron_dungeon_torch", () -> new StandingAndWallBlockItem((Block)IRON_DUNGEON_TORCH.get(), (Block)WALL_IRON_DUNGEON_TORCH.get(), new Item.Properties(), Direction.DOWN));
    public static final RegistryObject<Item> GOLD_DUNGEON_TORCH_ITEM = ModItems.ITEMS.register("gold_dungeon_torch", () -> new StandingAndWallBlockItem((Block)GOLD_DUNGEON_TORCH.get(), (Block)WALL_GOLD_DUNGEON_TORCH.get(), new Item.Properties(), Direction.DOWN));
    public static final RegistryObject<Item> GOLD_CANDLESTICK_ITEM = ModItems.ITEMS.register("gold_candlestick", () -> new StandingAndWallBlockItem((Block)GOLD_CANDLESTICK.get(), (Block)WALL_GOLD_CANDLESTICK.get(), new Item.Properties(), Direction.DOWN));
    public static final RegistryObject<Item> NECROTIC_GOLD_CANDLESTICK_ITEM = ModItems.ITEMS.register("necrotic_gold_candlestick", () -> new StandingAndWallBlockItem((Block)NECROTIC_GOLD_CANDLESTICK.get(), (Block)WALL_NECROTIC_GOLD_CANDLESTICK.get(), new Item.Properties(), Direction.DOWN));
    public static final RegistryObject<Item> RESONANCE_CRYSTAL_ITEM = ModItems.ITEMS.register("resonance_crystal", ResonanceBlockItem::new);
    public static final RegistryObject<Item> OMINOUS_IDOL_ITEM = ModItems.ITEMS.register("ominous_idol", OminousIdolBlockItem::new);
    public static final RegistryObject<Item> HAUNTED_JUG_ITEM = ModItems.ITEMS.register("haunted_jug", HauntedJugItem::new);
    public static final RegistryObject<Item> BLACK_CRYSTAL_ITEM = ModItems.ITEMS.register("black_crystal", BlackCrystalItem::new);
    public static final RegistryObject<Item> NIGHT_BEACON_ITEM = ModItems.ITEMS.register("night_beacon", () -> new BlockItem((Block)NIGHT_BEACON.get(), (new Item.Properties()).m_41486_()));
    public static final RegistryObject<Item> HAUNTED_SIGN_ITEM = ModItems.ITEMS.register("haunted_sign", () -> new SignItem((new Item.Properties()).m_41487_(16), (Block)HAUNTED_SIGN.get(), (Block)HAUNTED_WALL_SIGN.get()));
    public static final RegistryObject<Item> HAUNTED_HANGING_SIGN_ITEM = ModItems.ITEMS.register("haunted_hanging_sign", () -> new HangingSignItem((Block)HAUNTED_HANGING_SIGN.get(), (Block)HAUNTED_WALL_HANGING_SIGN.get(), (new Item.Properties()).m_41487_(16)));
    public static final RegistryObject<Item> ROTTEN_SIGN_ITEM = ModItems.ITEMS.register("rotten_sign", () -> new SignItem((new Item.Properties()).m_41487_(16), (Block)ROTTEN_SIGN.get(), (Block)ROTTEN_WALL_SIGN.get()));
    public static final RegistryObject<Item> ROTTEN_HANGING_SIGN_ITEM = ModItems.ITEMS.register("rotten_hanging_sign", () -> new HangingSignItem((Block)ROTTEN_HANGING_SIGN.get(), (Block)ROTTEN_WALL_HANGING_SIGN.get(), (new Item.Properties()).m_41487_(16)));
    public static final RegistryObject<Item> WINDSWEPT_SIGN_ITEM = ModItems.ITEMS.register("windswept_sign", () -> new SignItem((new Item.Properties()).m_41487_(16), (Block)WINDSWEPT_SIGN.get(), (Block)WINDSWEPT_WALL_SIGN.get()));
    public static final RegistryObject<Item> WINDSWEPT_HANGING_SIGN_ITEM = ModItems.ITEMS.register("windswept_hanging_sign", () -> new HangingSignItem((Block)WINDSWEPT_HANGING_SIGN.get(), (Block)WINDSWEPT_WALL_HANGING_SIGN.get(), (new Item.Properties()).m_41487_(16)));
    public static final RegistryObject<Item> PINE_SIGN_ITEM = ModItems.ITEMS.register("pine_sign", () -> new SignItem((new Item.Properties()).m_41487_(16), (Block)PINE_SIGN.get(), (Block)PINE_WALL_SIGN.get()));
    public static final RegistryObject<Item> PINE_HANGING_SIGN_ITEM = ModItems.ITEMS.register("pine_hanging_sign", () -> new HangingSignItem((Block)PINE_HANGING_SIGN.get(), (Block)PINE_WALL_HANGING_SIGN.get(), (new Item.Properties()).m_41487_(16)));
    public static final RegistryObject<Item> CHORUS_SIGN_ITEM = ModItems.ITEMS.register("chorus_sign", () -> new SignItem((new Item.Properties()).m_41487_(16), (Block)CHORUS_SIGN.get(), (Block)CHORUS_WALL_SIGN.get()));
    public static final RegistryObject<Item> CHORUS_HANGING_SIGN_ITEM = ModItems.ITEMS.register("chorus_hanging_sign", () -> new HangingSignItem((Block)CHORUS_HANGING_SIGN.get(), (Block)CHORUS_WALL_HANGING_SIGN.get(), (new Item.Properties()).m_41487_(16)));
    public static final RegistryObject<Item> CORRUPT_CHORUS_SIGN_ITEM = ModItems.ITEMS.register("corrupt_chorus_sign", () -> new SignItem((new Item.Properties()).m_41487_(16), (Block)CORRUPT_CHORUS_SIGN.get(), (Block)CORRUPT_CHORUS_WALL_SIGN.get()));
    public static final RegistryObject<Item> CORRUPT_CHORUS_HANGING_SIGN_ITEM = ModItems.ITEMS.register("corrupt_chorus_hanging_sign", () -> new HangingSignItem((Block)CORRUPT_CHORUS_HANGING_SIGN.get(), (Block)CORRUPT_CHORUS_WALL_HANGING_SIGN.get(), (new Item.Properties()).m_41487_(16)));
    public static final RegistryObject<Item> TALL_SKULL_ITEM = ModItems.ITEMS.register("tall_skull", () -> new ModBlocks.1((Block)TALL_SKULL_BLOCK.get(), (Block)WALL_TALL_SKULL_BLOCK.get(), (new Item.Properties()).m_41497_(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> REDSTONE_GOLEM_SKULL_ITEM = ModItems.ITEMS.register("redstone_golem_skull", () -> new ModBlocks.2((new Item.Properties()).m_41497_(Rarity.UNCOMMON).m_41486_()));
    public static final RegistryObject<Item> GRAVE_GOLEM_SKULL_ITEM = ModItems.ITEMS.register("grave_golem_skull", () -> new ModBlocks.3((new Item.Properties()).m_41497_(Rarity.UNCOMMON).m_41486_()));
    public static final RegistryObject<Item> REDSTONE_MONSTROSITY_HEAD_ITEM = ModItems.ITEMS.register("redstone_monstrosity_head", () -> new ModBlocks.4((new Item.Properties()).m_41497_(Rarity.EPIC).m_41486_()));

    public static void init() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    private static SaplingBlock sapling(AbstractTreeGrower tree) {
        return new SaplingBlock(tree, Properties.m_284310_().m_284180_(MapColor.f_283915_).m_60910_().m_60977_().m_60966_().m_60918_(SoundType.f_56740_).m_278166_(PushReaction.DESTROY));
    }

    private static RedMossSaplingBlock redMossSapling(AbstractTreeGrower tree) {
        return new RedMossSaplingBlock(tree, Properties.m_284310_().m_284180_(MapColor.f_283915_).m_60910_().m_60977_().m_60966_().m_60918_(SoundType.f_56740_).m_278166_(PushReaction.DESTROY));
    }

    private static EndSaplingBlock endSapling(AbstractTreeGrower tree) {
        return new EndSaplingBlock(tree, Properties.m_284310_().m_284180_(MapColor.f_283915_).m_60910_().m_60977_().m_60966_().m_60918_(SoundType.f_56740_).m_278166_(PushReaction.DESTROY));
    }

    private static RotatedPillarBlock pillar(BlockBehaviour.Properties properties) {
        return new RotatedPillarBlock(properties);
    }

    private static Block fireProofLog(MapColor p_285125_) {
        return new RotatedPillarBlock(Properties.m_284310_().m_284495_((p_152620_) -> p_285125_).m_280658_(NoteBlockInstrument.BASS).m_60978_(2.0F).m_60918_(SoundType.f_56736_));
    }

    private static RotatedPillarBlock log(MapColor p_285370_, MapColor p_285126_) {
        return log(p_285370_, p_285126_, SoundType.f_56736_);
    }

    private static RotatedPillarBlock log(MapColor p_285370_, MapColor p_285126_, SoundType soundType) {
        return new RotatedPillarBlock(Properties.m_284310_().m_284495_((p_152624_) -> p_152624_.m_61143_(RotatedPillarBlock.f_55923_) == Axis.Y ? p_285370_ : p_285126_).m_280658_(NoteBlockInstrument.BASS).m_60978_(2.0F).m_60918_(soundType).m_278183_());
    }

    public static <T extends Block> RegistryObject<Block> registerShadeSlabs(String string) {
        return register(string, () -> new SlabBlock(ShadeStoneProperties()), true);
    }

    public static <T extends Block> RegistryObject<Block> registerCryptSlabs(String string) {
        return register(string, () -> new SlabBlock(CryptStoneProperties()), true);
    }

    public static <T extends Block> RegistryObject<Block> registerSlabs(String string, Block block) {
        return register(string, () -> new SlabBlock(Properties.m_60926_(block)), true);
    }

    public static <T extends Block> RegistryObject<Block> registerSlabs(String string, RegistryObject<T> block) {
        return register(string, () -> new SlabBlock(Properties.m_60926_((BlockBehaviour)block.get())), true);
    }

    public static <T extends Block> RegistryObject<Block> registerStairs(String name, RegistryObject<T> block) {
        return register(name, () -> new StairBlock(() -> ((Block)block.get()).m_49966_(), Properties.m_60926_((BlockBehaviour)block.get())));
    }

    public static <T extends Block> RegistryObject<Block> registerWalls(String name, RegistryObject<T> block) {
        return register(name, () -> new WallBlock(Properties.m_60926_((BlockBehaviour)block.get())));
    }

    public static <T extends Block> RegistryObject<T> register(String string, Supplier<? extends T> sup) {
        return register(string, sup, true);
    }

    public static <T extends Block> RegistryObject<T> register(String string, Supplier<? extends T> sup, boolean blockItemDefault) {
        return register(string, sup, blockItemDefault, LootTableType.DROP);
    }

    public static <T extends Block> RegistryObject<T> register(String string, Supplier<? extends T> sup, boolean blockItemDefault, ModBlocks.LootTableType lootTableType) {
        RegistryObject<T> block = BLOCKS.register(string, sup);
        BLOCK_LOOT.put(block.getId(), new ModBlocks.BlockLootSetting(blockItemDefault, lootTableType));
        if (blockItemDefault) {
            ModItems.ITEMS.register(string, () -> new BlockItemBase((Block)block.get()));
        }

        return block;
    }

    public static <T extends Block> RegistryObject<T> isterRegister(String string, Supplier<? extends T> sup) {
        return isterRegister(string, sup, LootTableType.DROP);
    }

    public static <T extends Block> RegistryObject<T> isterRegister(String string, Supplier<? extends T> sup, ModBlocks.LootTableType lootTableType) {
        RegistryObject<T> block = BLOCKS.register(string, sup);
        BLOCK_LOOT.put(block.getId(), new ModBlocks.BlockLootSetting(false, lootTableType));
        ModItems.ITEMS.register(string, () -> new BlockISTERItem((Block)block.get()));
        return block;
    }

    public static <T extends Block> RegistryObject<T> curioIsterRegister(String string, Supplier<? extends T> sup) {
        return curioIsterRegister(string, sup, LootTableType.DROP);
    }

    public static <T extends Block> RegistryObject<T> curioIsterRegister(String string, Supplier<? extends T> sup, ModBlocks.LootTableType lootTableType) {
        RegistryObject<T> block = BLOCKS.register(string, sup);
        BLOCK_LOOT.put(block.getId(), new ModBlocks.BlockLootSetting(false, lootTableType));
        ModItems.ITEMS.register(string, () -> new CurioISTERItem((Block)block.get()));
        return block;
    }

    public static <T extends Block> RegistryObject<T> enchantedRegister(String string, Supplier<? extends T> sup) {
        RegistryObject<T> block = BLOCKS.register(string, sup);
        BLOCK_LOOT.put(block.getId(), new ModBlocks.BlockLootSetting(false, LootTableType.EMPTY));
        ModItems.ITEMS.register(string, () -> new EnchantableBlockItem((Block)block.get()));
        return block;
    }

    private static LeavesBlock leaves(SoundType p_152615_) {
        return new LeavesBlock(Properties.m_284310_().m_284180_(MapColor.f_283915_).m_60978_(0.2F).m_60977_().m_60918_(p_152615_).m_60955_().m_60922_(ModBlocks::ocelotOrParrot).m_60960_(ModBlocks::never).m_60971_(ModBlocks::never).m_278183_().m_278166_(PushReaction.DESTROY).m_60924_(ModBlocks::never));
    }

    private static boolean ocelotOrParrot(BlockState blockState, BlockGetter iBlockReader, BlockPos blockPos, EntityType<?> entityType) {
        return entityType == EntityType.f_20505_ || entityType == EntityType.f_20508_;
    }

    public static boolean never(BlockState blockState, BlockGetter iBlockReader, BlockPos blockPos) {
        return false;
    }

    public static Boolean never(BlockState blockState, BlockGetter iBlockReader, BlockPos blockPos, EntityType<?> p_235427_3_) {
        return false;
    }

    public static boolean always(BlockState p_50775_, BlockGetter p_50776_, BlockPos p_50777_) {
        return true;
    }

    public static boolean always(BlockState state, BlockGetter blockGetter, BlockPos blockPos, EntityType<?> entityType) {
        return true;
    }

    private static ButtonBlock woodenButton(BlockSetType p_278239_, FeatureFlag... p_278229_) {
        BlockBehaviour.Properties blockbehaviour$properties = Properties.m_284310_().m_60910_().m_60978_(0.5F).m_278166_(PushReaction.DESTROY);
        if (p_278229_.length > 0) {
            blockbehaviour$properties = blockbehaviour$properties.m_246843_(p_278229_);
        }

        return new ButtonBlock(blockbehaviour$properties, p_278239_, 30, true);
    }

    public static BlockBehaviour.Properties ShadeStoneProperties() {
        return Properties.m_284310_().m_284180_(MapColor.f_283947_).m_280658_(NoteBlockInstrument.BASEDRUM).m_60999_().m_60913_(5.0F, 100.0F).m_60918_(SoundType.f_56742_);
    }

    public static BlockBehaviour.Properties CryptStoneProperties() {
        return Properties.m_284310_().m_284180_(MapColor.f_283947_).m_280658_(NoteBlockInstrument.BASEDRUM).m_60999_().m_60913_(50.0F, 1200.0F).m_60918_(SoundType.f_56742_);
    }

    public static BlockBehaviour.Properties JadeStoneProperties() {
        return Properties.m_284310_().m_284180_(MapColor.f_283772_).m_60999_().m_60913_(1.5F, 6.0F).m_60918_(SoundType.f_56742_);
    }

    public static BlockBehaviour.Properties MarbleProperties() {
        return Properties.m_284310_().m_284180_(MapColor.f_283919_).m_60999_().m_280658_(NoteBlockInstrument.BASEDRUM).m_60913_(3.0F, 6.0F).m_60918_(SoundType.f_56742_);
    }

    public static BlockBehaviour.Properties SlateMarbleProperties() {
        return Properties.m_284310_().m_284180_(MapColor.f_283846_).m_60999_().m_280658_(NoteBlockInstrument.BASEDRUM).m_60913_(3.0F, 6.0F).m_60918_(SoundType.f_56742_);
    }

    public static BlockBehaviour.Properties CragProperties() {
        return Properties.m_284310_().m_284180_(MapColor.f_283771_).m_280658_(NoteBlockInstrument.BASEDRUM).m_60999_().m_60918_(SoundType.f_56742_).m_60913_(1.5F, 6.0F);
    }

    public static BlockBehaviour.Properties HighrockProperties() {
        return Properties.m_284310_().m_284180_(MapColor.f_283947_).m_280658_(NoteBlockInstrument.BASEDRUM).m_60999_().m_60913_(1.5F, 6.0F);
    }

    public static BlockBehaviour.Properties SiltstoneProperties() {
        return Properties.m_284310_().m_284180_(MapColor.f_283843_).m_280658_(NoteBlockInstrument.BASEDRUM).m_60999_().m_60913_(1.5F, 6.0F);
    }

    public static BlockBehaviour.Properties IndentedGoldProperties() {
        return Properties.m_284310_().m_284180_(MapColor.f_283757_).m_60999_().m_280658_(NoteBlockInstrument.BELL).m_60913_(3.0F, 6.0F).m_60918_(SoundType.f_56743_);
    }

    public static BlockBehaviour.Properties OminousStoneProperties() {
        return Properties.m_284310_().m_284180_(MapColor.f_283947_).m_280658_(NoteBlockInstrument.BASEDRUM).m_60999_().m_60913_(2.0F, 6.0F).m_60918_(SoundType.f_56742_);
    }

    public static BlockBehaviour.Properties SnowBrickProperties() {
        return Properties.m_284310_().m_284180_(MapColor.f_283811_).m_60999_().m_280658_(NoteBlockInstrument.BASEDRUM).m_60913_(1.5F, 6.0F).m_60918_(SoundType.f_56742_);
    }

    public static BlockBehaviour.Properties EndStoneProperties() {
        return Properties.m_60926_(Blocks.f_50259_);
    }

    public static BlockBehaviour.Properties glassProperties() {
        return Properties.m_284310_().m_280658_(NoteBlockInstrument.HAT).m_60978_(0.3F).m_60918_(SoundType.f_56744_).m_60955_().m_60922_(ModBlocks::never).m_60924_(ModBlocks::never).m_60960_(ModBlocks::never).m_60971_(ModBlocks::never);
    }

    public static BlockBehaviour.Properties tintedGlassProperties() {
        return Properties.m_60926_(Blocks.f_50058_).m_284180_(MapColor.f_283818_).m_60955_().m_60922_(ModBlocks::never).m_60924_(ModBlocks::never).m_60960_(ModBlocks::never).m_60971_(ModBlocks::never);
    }

    public static ToIntFunction<BlockState> litBlockEmission(int p_50760_) {
        return (p_50763_) -> p_50763_.m_61143_(BlockStateProperties.f_61443_) ? p_50760_ : 0;
    }
}
