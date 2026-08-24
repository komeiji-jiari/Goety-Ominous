/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.level.block.Block
 *  net.minecraftforge.common.ForgeSpawnEggItem
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.RegistryObject
 */
package com.vivideru.masteryofmagic.init;

import com.vivideru.masteryofmagic.EmpoweredForgeRingItem;
import com.vivideru.masteryofmagic.SpellRingItem;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModBlocks;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModEntities;
import com.vivideru.masteryofmagic.item.ChillingTimesDiskItem;
import com.vivideru.masteryofmagic.item.DeepMasteryScrollIIIItem;
import com.vivideru.masteryofmagic.item.DeepMasteryScrollIIItem;
import com.vivideru.masteryofmagic.item.DeepMasteryScrollIItem;
import com.vivideru.masteryofmagic.item.FrostMasteryScrollIIIItem;
import com.vivideru.masteryofmagic.item.FrostMasteryScrollIIItem;
import com.vivideru.masteryofmagic.item.FrostMasteryScrollIItem;
import com.vivideru.masteryofmagic.item.GeomancyMasteryScrollIIIItem;
import com.vivideru.masteryofmagic.item.GeomancyMasteryScrollIIItem;
import com.vivideru.masteryofmagic.item.GeomancyMasteryScrollIItem;
import com.vivideru.masteryofmagic.item.GhiaccioSpawnEggItem;
import com.vivideru.masteryofmagic.item.GlacialUnholyBloodItem;
import com.vivideru.masteryofmagic.item.GobletofAntifreezeItem;
import com.vivideru.masteryofmagic.item.GobletofBatsItem;
import com.vivideru.masteryofmagic.item.GobletofFlightItem;
import com.vivideru.masteryofmagic.item.GobletofSoulEnergyItem;
import com.vivideru.masteryofmagic.item.GoldenGobletFilledItem;
import com.vivideru.masteryofmagic.item.GoldenGobletItem;
import com.vivideru.masteryofmagic.item.MasterStaffItem;
import com.vivideru.masteryofmagic.item.NecromancyMasteryScrollIIIItem;
import com.vivideru.masteryofmagic.item.NecromancyMasteryScrollIIItem;
import com.vivideru.masteryofmagic.item.NecromancyMasteryScrollIItem;
import com.vivideru.masteryofmagic.item.NetherMasteryScrollIIIItem;
import com.vivideru.masteryofmagic.item.NetherMasteryScrollIIItem;
import com.vivideru.masteryofmagic.item.NetherMasteryScrollIItem;
import com.vivideru.masteryofmagic.item.PermaLazethystItem;
import com.vivideru.masteryofmagic.item.SkyMasteryScrollIIIItem;
import com.vivideru.masteryofmagic.item.SkyMasteryScrollIIItem;
import com.vivideru.masteryofmagic.item.SkyMasteryScrollIItem;
import com.vivideru.masteryofmagic.item.StormMasteryScrollIIIItem;
import com.vivideru.masteryofmagic.item.StormMasteryScrollIIItem;
import com.vivideru.masteryofmagic.item.StormMasteryScrollIItem;
import com.vivideru.masteryofmagic.item.UndeadBloodBucketItem;
import com.vivideru.masteryofmagic.item.UndeadBloodVialItem;
import com.vivideru.masteryofmagic.item.VoidMasteryScrollIIIItem;
import com.vivideru.masteryofmagic.item.VoidMasteryScrollIIItem;
import com.vivideru.masteryofmagic.item.VoidMasteryScrollIItem;
import com.vivideru.masteryofmagic.item.WildMasteryScrollIIIItem;
import com.vivideru.masteryofmagic.item.WildMasteryScrollIIItem;
import com.vivideru.masteryofmagic.item.WildMasteryScrollIItem;
import com.vivideru.masteryofmagic.item.WizardryMasteryScrollItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

public class GoetyMasteryOfMagicModItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create((IForgeRegistry)ForgeRegistries.ITEMS, (String)"goety_mastery_of_magic");
    public static final RegistryObject<Item> NECROMANCY_MASTERY_SCROLL_I = REGISTRY.register("necromancy_mastery_scroll_i", () -> new NecromancyMasteryScrollIItem());
    public static final RegistryObject<Item> NECROMANCY_MASTERY_SCROLL_II = REGISTRY.register("necromancy_mastery_scroll_ii", () -> new NecromancyMasteryScrollIIItem());
    public static final RegistryObject<Item> NECROMANCY_MASTERY_SCROLL_III = REGISTRY.register("necromancy_mastery_scroll_iii", () -> new NecromancyMasteryScrollIIIItem());
    public static final RegistryObject<Item> FROST_MASTERY_SCROLL_I = REGISTRY.register("frost_mastery_scroll_i", () -> new FrostMasteryScrollIItem());
    public static final RegistryObject<Item> FROST_MASTERY_SCROLL_II = REGISTRY.register("frost_mastery_scroll_ii", () -> new FrostMasteryScrollIIItem());
    public static final RegistryObject<Item> FROST_MASTERY_SCROLL_III = REGISTRY.register("frost_mastery_scroll_iii", () -> new FrostMasteryScrollIIIItem());
    public static final RegistryObject<Item> NETHER_MASTERY_SCROLL_I = REGISTRY.register("nether_mastery_scroll_i", () -> new NetherMasteryScrollIItem());
    public static final RegistryObject<Item> NETHER_MASTERY_SCROLL_II = REGISTRY.register("nether_mastery_scroll_ii", () -> new NetherMasteryScrollIIItem());
    public static final RegistryObject<Item> NETHER_MASTERY_SCROLL_III = REGISTRY.register("nether_mastery_scroll_iii", () -> new NetherMasteryScrollIIIItem());
    public static final RegistryObject<Item> DEEP_MASTERY_SCROLL_I = REGISTRY.register("deep_mastery_scroll_i", () -> new DeepMasteryScrollIItem());
    public static final RegistryObject<Item> DEEP_MASTERY_SCROLL_II = REGISTRY.register("deep_mastery_scroll_ii", () -> new DeepMasteryScrollIIItem());
    public static final RegistryObject<Item> DEEP_MASTERY_SCROLL_III = REGISTRY.register("deep_mastery_scroll_iii", () -> new DeepMasteryScrollIIIItem());
    public static final RegistryObject<Item> STORM_MASTERY_SCROLL_I = REGISTRY.register("storm_mastery_scroll_i", () -> new StormMasteryScrollIItem());
    public static final RegistryObject<Item> STORM_MASTERY_SCROLL_II = REGISTRY.register("storm_mastery_scroll_ii", () -> new StormMasteryScrollIIItem());
    public static final RegistryObject<Item> STORM_MASTERY_SCROLL_III = REGISTRY.register("storm_mastery_scroll_iii", () -> new StormMasteryScrollIIIItem());
    public static final RegistryObject<Item> SKY_MASTERY_SCROLL_I = REGISTRY.register("sky_mastery_scroll_i", () -> new SkyMasteryScrollIItem());
    public static final RegistryObject<Item> SKY_MASTERY_SCROLL_II = REGISTRY.register("sky_mastery_scroll_ii", () -> new SkyMasteryScrollIIItem());
    public static final RegistryObject<Item> SKY_MASTERY_SCROLL_III = REGISTRY.register("sky_mastery_scroll_iii", () -> new SkyMasteryScrollIIIItem());
    public static final RegistryObject<Item> GEOMANCY_MASTERY_SCROLL_I = REGISTRY.register("geomancy_mastery_scroll_i", () -> new GeomancyMasteryScrollIItem());
    public static final RegistryObject<Item> GEOMANCY_MASTERY_SCROLL_II = REGISTRY.register("geomancy_mastery_scroll_ii", () -> new GeomancyMasteryScrollIIItem());
    public static final RegistryObject<Item> GEOMANCY_MASTERY_SCROLL_III = REGISTRY.register("geomancy_mastery_scroll_iii", () -> new GeomancyMasteryScrollIIIItem());
    public static final RegistryObject<Item> WILD_MASTERY_SCROLL_I = REGISTRY.register("wild_mastery_scroll_i", () -> new WildMasteryScrollIItem());
    public static final RegistryObject<Item> WILD_MASTERY_SCROLL_II = REGISTRY.register("wild_mastery_scroll_ii", () -> new WildMasteryScrollIIItem());
    public static final RegistryObject<Item> WILD_MASTERY_SCROLL_III = REGISTRY.register("wild_mastery_scroll_iii", () -> new WildMasteryScrollIIIItem());
    public static final RegistryObject<Item> VOID_MASTERY_SCROLL_I = REGISTRY.register("void_mastery_scroll_i", () -> new VoidMasteryScrollIItem());
    public static final RegistryObject<Item> VOID_MASTERY_SCROLL_II = REGISTRY.register("void_mastery_scroll_ii", () -> new VoidMasteryScrollIIItem());
    public static final RegistryObject<Item> VOID_MASTERY_SCROLL_III = REGISTRY.register("void_mastery_scroll_iii", () -> new VoidMasteryScrollIIIItem());
    public static final RegistryObject<Item> WIZARDRY_MASTERY_SCROLL_I = REGISTRY.register("wizardry_mastery_scroll_i", () -> new WizardryMasteryScrollItem(1));
    public static final RegistryObject<Item> WIZARDRY_MASTERY_SCROLL_II = REGISTRY.register("wizardry_mastery_scroll_ii", () -> new WizardryMasteryScrollItem(2));
    public static final RegistryObject<Item> WIZARDRY_MASTERY_SCROLL_III = REGISTRY.register("wizardry_mastery_scroll_iii", () -> new WizardryMasteryScrollItem(3));
    public static final RegistryObject<Item> SUPREME_NETHER_MASTERY_TOKEN = REGISTRY.register("supreme_nether_mastery_token", () -> new Item(new Item.Properties().m_41487_(1)));
    public static final RegistryObject<Item> SUPREME_SKIES_ATTUNEMENT_TOKEN = REGISTRY.register("supreme_skies_attunement_token", () -> new Item(new Item.Properties().m_41487_(1)));
    public static final RegistryObject<Item> SUPREME_SKIES_MASTERY_TOKEN = REGISTRY.register("supreme_skies_mastery_token", () -> new Item(new Item.Properties().m_41487_(1)));
    public static final RegistryObject<Item> SUPREME_PLANET_WILD_TOKEN = REGISTRY.register("supreme_planet_wild_token", () -> new Item(new Item.Properties().m_41487_(1)));
    public static final RegistryObject<Item> SUPREME_PLANET_GEOMANCY_TOKEN = REGISTRY.register("supreme_planet_geomancy_token", () -> new Item(new Item.Properties().m_41487_(1)));
    public static final RegistryObject<Item> SUPREME_PLANET_DEEP_TOKEN = REGISTRY.register("supreme_planet_deep_token", () -> new Item(new Item.Properties().m_41487_(1)));
    public static final RegistryObject<Item> THERIANTHROPY_RITUAL_TOKEN = REGISTRY.register("therianthropy_ritual_token", () -> new Item(new Item.Properties().m_41487_(1)));
    public static final RegistryObject<Item> CHARGED_RUNED_LAZETHYST_BLOCK = GoetyMasteryOfMagicModItems.block(GoetyMasteryOfMagicModBlocks.CHARGED_RUNED_LAZETHYST_BLOCK);
    public static final RegistryObject<Item> RUNED_LAZETHYST_BLOCK = GoetyMasteryOfMagicModItems.block(GoetyMasteryOfMagicModBlocks.RUNED_LAZETHYST_BLOCK);
    public static final RegistryObject<Item> NETHER_RUNED_LAZETHYST_BLOCK = GoetyMasteryOfMagicModItems.block(GoetyMasteryOfMagicModBlocks.NETHER_RUNED_LAZETHYST_BLOCK);
    public static final RegistryObject<Item> CRYPT_RUNED_LAZETHYST_BLOCK = GoetyMasteryOfMagicModItems.block(GoetyMasteryOfMagicModBlocks.CRYPT_RUNED_LAZETHYST_BLOCK);
    public static final RegistryObject<Item> VOID_RUNED_LAZETHYST_BLOCK = GoetyMasteryOfMagicModItems.block(GoetyMasteryOfMagicModBlocks.VOID_RUNED_LAZETHYST_BLOCK);
    public static final RegistryObject<Item> SKY_RUNED_LAZETHYST_BLOCK = GoetyMasteryOfMagicModItems.block(GoetyMasteryOfMagicModBlocks.SKY_RUNED_LAZETHYST_BLOCK);
    public static final RegistryObject<Item> DEEP_RUNED_LAZETHYST_BLOCK = GoetyMasteryOfMagicModItems.block(GoetyMasteryOfMagicModBlocks.DEEP_RUNED_LAZETHYST_BLOCK);
    public static final RegistryObject<Item> STORM_RUNED_LAZETHYST_BLOCK = GoetyMasteryOfMagicModItems.block(GoetyMasteryOfMagicModBlocks.STORM_RUNED_LAZETHYST_BLOCK);
    public static final RegistryObject<Item> GEOMANCY_RUNED_LAZETHYST_BLOCK = GoetyMasteryOfMagicModItems.block(GoetyMasteryOfMagicModBlocks.GEOMANCY_RUNED_LAZETHYST_BLOCK);
    public static final RegistryObject<Item> WILD_RUNED_LAZETHYST_BLOCK = GoetyMasteryOfMagicModItems.block(GoetyMasteryOfMagicModBlocks.WILD_RUNED_LAZETHYST_BLOCK);
    public static final RegistryObject<Item> OMINOUS_RUNED_LAZETHYST_BLOCK = GoetyMasteryOfMagicModItems.block(GoetyMasteryOfMagicModBlocks.OMINOUS_RUNED_LAZETHYST_BLOCK);
    public static final RegistryObject<Item> NETHER_RUNED_LAZETHYST_BLOCK_CHARGED = GoetyMasteryOfMagicModItems.block(GoetyMasteryOfMagicModBlocks.NETHER_RUNED_LAZETHYST_BLOCK_CHARGED);
    public static final RegistryObject<Item> CRYPT_RUNED_LAZETHYST_BLOCK_CHARGED = GoetyMasteryOfMagicModItems.block(GoetyMasteryOfMagicModBlocks.CRYPT_RUNED_LAZETHYST_BLOCK_CHARGED);
    public static final RegistryObject<Item> VOID_RUNED_LAZETHYST_BLOCK_CHARGED = GoetyMasteryOfMagicModItems.block(GoetyMasteryOfMagicModBlocks.VOID_RUNED_LAZETHYST_BLOCK_CHARGED);
    public static final RegistryObject<Item> SKY_RUNED_LAZETHYST_BLOCK_CHARGED = GoetyMasteryOfMagicModItems.block(GoetyMasteryOfMagicModBlocks.SKY_RUNED_LAZETHYST_BLOCK_CHARGED);
    public static final RegistryObject<Item> DEEP_RUNED_LAZETHYST_BLOCK_CHARGED = GoetyMasteryOfMagicModItems.block(GoetyMasteryOfMagicModBlocks.DEEP_RUNED_LAZETHYST_BLOCK_CHARGED);
    public static final RegistryObject<Item> STORM_RUNED_LAZETHYST_BLOCK_CHARGED = GoetyMasteryOfMagicModItems.block(GoetyMasteryOfMagicModBlocks.STORM_RUNED_LAZETHYST_BLOCK_CHARGED);
    public static final RegistryObject<Item> GEOMANCY_RUNED_LAZETHYST_BLOCK_CHARGED = GoetyMasteryOfMagicModItems.block(GoetyMasteryOfMagicModBlocks.GEOMANCY_RUNED_LAZETHYST_BLOCK_CHARGED);
    public static final RegistryObject<Item> WILD_RUNED_LAZETHYST_BLOCK_CHARGED = GoetyMasteryOfMagicModItems.block(GoetyMasteryOfMagicModBlocks.WILD_RUNED_LAZETHYST_BLOCK_CHARGED);
    public static final RegistryObject<Item> OMINOUS_RUNED_LAZETHYST_BLOCK_CHARGED = GoetyMasteryOfMagicModItems.block(GoetyMasteryOfMagicModBlocks.OMINOUS_RUNED_LAZETHYST_BLOCK_CHARGED);
    public static final RegistryObject<Item> FROST_RUNED_LAZETHYST_BLOCK = GoetyMasteryOfMagicModItems.block(GoetyMasteryOfMagicModBlocks.FROST_RUNED_LAZETHYST_BLOCK);
    public static final RegistryObject<Item> FROST_RUNED_LAZETHYST_BLOCK_CHARGED = GoetyMasteryOfMagicModItems.block(GoetyMasteryOfMagicModBlocks.FROST_RUNED_LAZETHYST_BLOCK_CHARGED);
    public static final RegistryObject<Item> LAZETHYST_BLOCK = GoetyMasteryOfMagicModItems.block(GoetyMasteryOfMagicModBlocks.LAZETHYST_BLOCK);
    public static final RegistryObject<Item> POLISHED_LAZETHYST_BLOCK = GoetyMasteryOfMagicModItems.block(GoetyMasteryOfMagicModBlocks.POLISHED_LAZETHYST_BLOCK);
    public static final RegistryObject<Item> PERMA_LAZETHYST = REGISTRY.register("perma_lazethyst", () -> new PermaLazethystItem());
    public static final RegistryObject<Item> BLACKSTONE_CHALICE = GoetyMasteryOfMagicModItems.block(GoetyMasteryOfMagicModBlocks.BLACKSTONE_CHALICE);
    public static final RegistryObject<Item> UNDEAD_BLOOD_VIAL = REGISTRY.register("undead_blood_vial", () -> new UndeadBloodVialItem());
    public static final RegistryObject<Item> VAMPIRATOR_SERVANT_SPAWN_EGG = REGISTRY.register("vampirator_servant_spawn_egg", () -> new ForgeSpawnEggItem(GoetyMasteryOfMagicModEntities.VAMPIRATOR_SERVANT, -6750208, -10066330, new Item.Properties()));
    public static final RegistryObject<Item> UNDEAD_BLOOD_BUCKET = REGISTRY.register("undead_blood_bucket", () -> new UndeadBloodBucketItem());
    public static final RegistryObject<Item> GOLDEN_GOBLET = REGISTRY.register("golden_goblet", () -> new GoldenGobletItem());
    public static final RegistryObject<Item> GOLDEN_GOBLET_FILLED = REGISTRY.register("golden_goblet_filled", () -> new GoldenGobletFilledItem());
    public static final RegistryObject<Item> GOBLETOF_FLIGHT = REGISTRY.register("gobletof_flight", () -> new GobletofFlightItem());
    public static final RegistryObject<Item> GOBLETOF_BATS = REGISTRY.register("gobletof_bats", () -> new GobletofBatsItem());
    public static final RegistryObject<Item> GOBLETOF_ANTIFREEZE = REGISTRY.register("gobletof_antifreeze", () -> new GobletofAntifreezeItem());
    public static final RegistryObject<Item> GOBLETOF_SOUL_ENERGY = REGISTRY.register("gobletof_soul_energy", () -> new GobletofSoulEnergyItem());
    public static final RegistryObject<Item> GAZER_SPAWN_EGG = REGISTRY.register("gazer_spawn_egg", () -> new ForgeSpawnEggItem(GoetyMasteryOfMagicModEntities.GAZER, -16252905, -16711936, new Item.Properties()));
    public static final RegistryObject<Item> MOVEMENT_FORCER = GoetyMasteryOfMagicModItems.block(GoetyMasteryOfMagicModBlocks.MOVEMENT_FORCER);
    public static final RegistryObject<Item> GLACIAL_UNHOLY_BLOOD = REGISTRY.register("glacial_unholy_blood", () -> new GlacialUnholyBloodItem());
    public static final RegistryObject<Item> CHILLING_TIMES_DISK = REGISTRY.register("chilling_times_disk", () -> new ChillingTimesDiskItem());
    public static final RegistryObject<Item> GHIACCIO_SPAWN_EGG = REGISTRY.register("ghiaccio_spawn_egg", () -> new GhiaccioSpawnEggItem());
    public static final RegistryObject<Item> ICE_MONARCH_SPAWN_EGG = REGISTRY.register("ice_monarch_spawn_egg", () -> new ForgeSpawnEggItem(GoetyMasteryOfMagicModEntities.ICE_MONARCH, -13395457, -3342337, new Item.Properties()));
    public static final RegistryObject<Item> SOUL_BARRIER_BLOCK = GoetyMasteryOfMagicModItems.block(GoetyMasteryOfMagicModBlocks.SOUL_BARRIER_BLOCK);
    public static final RegistryObject<Item> EMPOWERED_FORGE_RING = REGISTRY.register("empowered_forge_ring", EmpoweredForgeRingItem::new);
    public static final RegistryObject<Item> SPELL_RING = REGISTRY.register("spell_ring", SpellRingItem::new);
    public static final RegistryObject<Item> MASTER_STAFF = REGISTRY.register("master_staff", MasterStaffItem::new);

    private static RegistryObject<Item> block(RegistryObject<Block> block) {
        return REGISTRY.register(block.getId().m_135815_(), () -> new BlockItem((Block)block.get(), new Item.Properties()));
    }
}

