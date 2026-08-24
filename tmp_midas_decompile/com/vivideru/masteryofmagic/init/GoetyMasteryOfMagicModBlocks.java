/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.Block
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.RegistryObject
 */
package com.vivideru.masteryofmagic.init;

import com.vivideru.masteryofmagic.block.BlackstoneChaliceBlock;
import com.vivideru.masteryofmagic.block.ChargedCryptRunedLazethystBlock;
import com.vivideru.masteryofmagic.block.ChargedDeepRunedLazethystBlock;
import com.vivideru.masteryofmagic.block.ChargedFrostRunedLazethystBlock;
import com.vivideru.masteryofmagic.block.ChargedGeomancyRunedLazethystBlock;
import com.vivideru.masteryofmagic.block.ChargedNetherRunedLazethystBlock;
import com.vivideru.masteryofmagic.block.ChargedOminousRunedLazethystBlock;
import com.vivideru.masteryofmagic.block.ChargedRunedLazethystBlock;
import com.vivideru.masteryofmagic.block.ChargedSkyRunedLazethystBlock;
import com.vivideru.masteryofmagic.block.ChargedStormRunedLazethystBlock;
import com.vivideru.masteryofmagic.block.ChargedVoidRunedLazethystBlock;
import com.vivideru.masteryofmagic.block.ChargedWildRunedLazethystBlock;
import com.vivideru.masteryofmagic.block.CryptRunedLazethystBlock;
import com.vivideru.masteryofmagic.block.DeepRunedLazethystBlock;
import com.vivideru.masteryofmagic.block.FrostRunedLazethystBlock;
import com.vivideru.masteryofmagic.block.GeomancyRunedLazethystBlock;
import com.vivideru.masteryofmagic.block.LazethystBlock;
import com.vivideru.masteryofmagic.block.MovementForcerBlock;
import com.vivideru.masteryofmagic.block.NetherRunedLazethystBlock;
import com.vivideru.masteryofmagic.block.OminousRunedLazethystBlock;
import com.vivideru.masteryofmagic.block.PolishedLazethystBlock;
import com.vivideru.masteryofmagic.block.RunedLazethystBlock;
import com.vivideru.masteryofmagic.block.SkyRunedLazethystBlock;
import com.vivideru.masteryofmagic.block.SoulBarrierBlock;
import com.vivideru.masteryofmagic.block.StormRunedLazethystBlock;
import com.vivideru.masteryofmagic.block.VoidRunedLazethystBlock;
import com.vivideru.masteryofmagic.block.WildRunedLazethystBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

public class GoetyMasteryOfMagicModBlocks {
    public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create((IForgeRegistry)ForgeRegistries.BLOCKS, (String)"goety_mastery_of_magic");
    public static final RegistryObject<Block> CHARGED_RUNED_LAZETHYST_BLOCK = REGISTRY.register("charged_runed_lazethyst_block", () -> new ChargedRunedLazethystBlock());
    public static final RegistryObject<Block> RUNED_LAZETHYST_BLOCK = REGISTRY.register("runed_lazethyst_block", () -> new RunedLazethystBlock());
    public static final RegistryObject<Block> NETHER_RUNED_LAZETHYST_BLOCK = REGISTRY.register("nether_runed_lazethyst_block", () -> new NetherRunedLazethystBlock());
    public static final RegistryObject<Block> CRYPT_RUNED_LAZETHYST_BLOCK = REGISTRY.register("crypt_runed_lazethyst_block", () -> new CryptRunedLazethystBlock());
    public static final RegistryObject<Block> VOID_RUNED_LAZETHYST_BLOCK = REGISTRY.register("void_runed_lazethyst_block", () -> new VoidRunedLazethystBlock());
    public static final RegistryObject<Block> SKY_RUNED_LAZETHYST_BLOCK = REGISTRY.register("sky_runed_lazethyst_block", () -> new SkyRunedLazethystBlock());
    public static final RegistryObject<Block> DEEP_RUNED_LAZETHYST_BLOCK = REGISTRY.register("deep_runed_lazethyst_block", () -> new DeepRunedLazethystBlock());
    public static final RegistryObject<Block> STORM_RUNED_LAZETHYST_BLOCK = REGISTRY.register("storm_runed_lazethyst_block", () -> new StormRunedLazethystBlock());
    public static final RegistryObject<Block> GEOMANCY_RUNED_LAZETHYST_BLOCK = REGISTRY.register("geomancy_runed_lazethyst_block", () -> new GeomancyRunedLazethystBlock());
    public static final RegistryObject<Block> WILD_RUNED_LAZETHYST_BLOCK = REGISTRY.register("wild_runed_lazethyst_block", () -> new WildRunedLazethystBlock());
    public static final RegistryObject<Block> OMINOUS_RUNED_LAZETHYST_BLOCK = REGISTRY.register("ominous_runed_lazethyst_block", () -> new OminousRunedLazethystBlock());
    public static final RegistryObject<Block> NETHER_RUNED_LAZETHYST_BLOCK_CHARGED = REGISTRY.register("nether_runed_lazethyst_block_charged", () -> new ChargedNetherRunedLazethystBlock());
    public static final RegistryObject<Block> CRYPT_RUNED_LAZETHYST_BLOCK_CHARGED = REGISTRY.register("crypt_runed_lazethyst_block_charged", () -> new ChargedCryptRunedLazethystBlock());
    public static final RegistryObject<Block> VOID_RUNED_LAZETHYST_BLOCK_CHARGED = REGISTRY.register("void_runed_lazethyst_block_charged", () -> new ChargedVoidRunedLazethystBlock());
    public static final RegistryObject<Block> SKY_RUNED_LAZETHYST_BLOCK_CHARGED = REGISTRY.register("sky_runed_lazethyst_block_charged", () -> new ChargedSkyRunedLazethystBlock());
    public static final RegistryObject<Block> DEEP_RUNED_LAZETHYST_BLOCK_CHARGED = REGISTRY.register("deep_runed_lazethyst_block_charged", () -> new ChargedDeepRunedLazethystBlock());
    public static final RegistryObject<Block> STORM_RUNED_LAZETHYST_BLOCK_CHARGED = REGISTRY.register("storm_runed_lazethyst_block_charged", () -> new ChargedStormRunedLazethystBlock());
    public static final RegistryObject<Block> GEOMANCY_RUNED_LAZETHYST_BLOCK_CHARGED = REGISTRY.register("geomancy_runed_lazethyst_block_charged", () -> new ChargedGeomancyRunedLazethystBlock());
    public static final RegistryObject<Block> WILD_RUNED_LAZETHYST_BLOCK_CHARGED = REGISTRY.register("wild_runed_lazethyst_block_charged", () -> new ChargedWildRunedLazethystBlock());
    public static final RegistryObject<Block> OMINOUS_RUNED_LAZETHYST_BLOCK_CHARGED = REGISTRY.register("ominous_runed_lazethyst_block_charged", () -> new ChargedOminousRunedLazethystBlock());
    public static final RegistryObject<Block> FROST_RUNED_LAZETHYST_BLOCK = REGISTRY.register("frost_runed_lazethyst_block", () -> new FrostRunedLazethystBlock());
    public static final RegistryObject<Block> FROST_RUNED_LAZETHYST_BLOCK_CHARGED = REGISTRY.register("frost_runed_lazethyst_block_charged", () -> new ChargedFrostRunedLazethystBlock());
    public static final RegistryObject<Block> LAZETHYST_BLOCK = REGISTRY.register("lazethyst_block", () -> new LazethystBlock());
    public static final RegistryObject<Block> POLISHED_LAZETHYST_BLOCK = REGISTRY.register("polished_lazethyst_block", () -> new PolishedLazethystBlock());
    public static final RegistryObject<Block> BLACKSTONE_CHALICE = REGISTRY.register("blackstone_chalice", () -> new BlackstoneChaliceBlock());
    public static final RegistryObject<Block> MOVEMENT_FORCER = REGISTRY.register("movement_forcer", () -> new MovementForcerBlock());
    public static final RegistryObject<Block> SOUL_BARRIER_BLOCK = REGISTRY.register("soul_barrier_block", () -> new SoulBarrierBlock());
}

