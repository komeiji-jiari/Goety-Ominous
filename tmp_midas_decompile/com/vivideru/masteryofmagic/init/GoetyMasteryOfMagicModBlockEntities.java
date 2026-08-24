/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.entity.BlockEntityType$BlockEntitySupplier
 *  net.minecraft.world.level.block.entity.BlockEntityType$Builder
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.RegistryObject
 */
package com.vivideru.masteryofmagic.init;

import com.vivideru.masteryofmagic.block.entity.BlackstoneChaliceBlockEntity;
import com.vivideru.masteryofmagic.block.entity.ChargedCryptRunedLazethystBlockEntity;
import com.vivideru.masteryofmagic.block.entity.ChargedDeepRunedLazethystBlockEntity;
import com.vivideru.masteryofmagic.block.entity.ChargedFrostRunedLazethystBlockEntity;
import com.vivideru.masteryofmagic.block.entity.ChargedGeomancyRunedLazethystBlockEntity;
import com.vivideru.masteryofmagic.block.entity.ChargedNetherRunedLazethystBlockEntity;
import com.vivideru.masteryofmagic.block.entity.ChargedOminousRunedLazethystBlockEntity;
import com.vivideru.masteryofmagic.block.entity.ChargedRunedLazethystBlockEntity;
import com.vivideru.masteryofmagic.block.entity.ChargedSkyRunedLazethystBlockEntity;
import com.vivideru.masteryofmagic.block.entity.ChargedStormRunedLazethystBlockEntity;
import com.vivideru.masteryofmagic.block.entity.ChargedVoidRunedLazethystBlockEntity;
import com.vivideru.masteryofmagic.block.entity.ChargedWildRunedLazethystBlockEntity;
import com.vivideru.masteryofmagic.block.entity.MovementForcerBlockEntity;
import com.vivideru.masteryofmagic.block.entity.SoulBarrierBlockEntity;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

public class GoetyMasteryOfMagicModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create((IForgeRegistry)ForgeRegistries.BLOCK_ENTITY_TYPES, (String)"goety_mastery_of_magic");
    public static final RegistryObject<BlockEntityType<?>> CHARGED_RUNED_LAZETHYST_BLOCK = GoetyMasteryOfMagicModBlockEntities.register("charged_runed_lazethyst_block", GoetyMasteryOfMagicModBlocks.CHARGED_RUNED_LAZETHYST_BLOCK, ChargedRunedLazethystBlockEntity::new);
    public static final RegistryObject<BlockEntityType<?>> NETHER_RUNED_LAZETHYST_BLOCK_CHARGED = GoetyMasteryOfMagicModBlockEntities.register("nether_runed_lazethyst_block_charged", GoetyMasteryOfMagicModBlocks.NETHER_RUNED_LAZETHYST_BLOCK_CHARGED, ChargedNetherRunedLazethystBlockEntity::new);
    public static final RegistryObject<BlockEntityType<?>> CRYPT_RUNED_LAZETHYST_BLOCK_CHARGED = GoetyMasteryOfMagicModBlockEntities.register("crypt_runed_lazethyst_block_charged", GoetyMasteryOfMagicModBlocks.CRYPT_RUNED_LAZETHYST_BLOCK_CHARGED, ChargedCryptRunedLazethystBlockEntity::new);
    public static final RegistryObject<BlockEntityType<?>> VOID_RUNED_LAZETHYST_BLOCK_CHARGED = GoetyMasteryOfMagicModBlockEntities.register("void_runed_lazethyst_block_charged", GoetyMasteryOfMagicModBlocks.VOID_RUNED_LAZETHYST_BLOCK_CHARGED, ChargedVoidRunedLazethystBlockEntity::new);
    public static final RegistryObject<BlockEntityType<?>> SKY_RUNED_LAZETHYST_BLOCK_CHARGED = GoetyMasteryOfMagicModBlockEntities.register("sky_runed_lazethyst_block_charged", GoetyMasteryOfMagicModBlocks.SKY_RUNED_LAZETHYST_BLOCK_CHARGED, ChargedSkyRunedLazethystBlockEntity::new);
    public static final RegistryObject<BlockEntityType<?>> DEEP_RUNED_LAZETHYST_BLOCK_CHARGED = GoetyMasteryOfMagicModBlockEntities.register("deep_runed_lazethyst_block_charged", GoetyMasteryOfMagicModBlocks.DEEP_RUNED_LAZETHYST_BLOCK_CHARGED, ChargedDeepRunedLazethystBlockEntity::new);
    public static final RegistryObject<BlockEntityType<?>> STORM_RUNED_LAZETHYST_BLOCK_CHARGED = GoetyMasteryOfMagicModBlockEntities.register("storm_runed_lazethyst_block_charged", GoetyMasteryOfMagicModBlocks.STORM_RUNED_LAZETHYST_BLOCK_CHARGED, ChargedStormRunedLazethystBlockEntity::new);
    public static final RegistryObject<BlockEntityType<?>> GEOMANCY_RUNED_LAZETHYST_BLOCK_CHARGED = GoetyMasteryOfMagicModBlockEntities.register("geomancy_runed_lazethyst_block_charged", GoetyMasteryOfMagicModBlocks.GEOMANCY_RUNED_LAZETHYST_BLOCK_CHARGED, ChargedGeomancyRunedLazethystBlockEntity::new);
    public static final RegistryObject<BlockEntityType<?>> WILD_RUNED_LAZETHYST_BLOCK_CHARGED = GoetyMasteryOfMagicModBlockEntities.register("wild_runed_lazethyst_block_charged", GoetyMasteryOfMagicModBlocks.WILD_RUNED_LAZETHYST_BLOCK_CHARGED, ChargedWildRunedLazethystBlockEntity::new);
    public static final RegistryObject<BlockEntityType<?>> OMINOUS_RUNED_LAZETHYST_BLOCK_CHARGED = GoetyMasteryOfMagicModBlockEntities.register("ominous_runed_lazethyst_block_charged", GoetyMasteryOfMagicModBlocks.OMINOUS_RUNED_LAZETHYST_BLOCK_CHARGED, ChargedOminousRunedLazethystBlockEntity::new);
    public static final RegistryObject<BlockEntityType<?>> FROST_RUNED_LAZETHYST_BLOCK_CHARGED = GoetyMasteryOfMagicModBlockEntities.register("frost_runed_lazethyst_block_charged", GoetyMasteryOfMagicModBlocks.FROST_RUNED_LAZETHYST_BLOCK_CHARGED, ChargedFrostRunedLazethystBlockEntity::new);
    public static final RegistryObject<BlockEntityType<?>> BLACKSTONE_CHALICE = GoetyMasteryOfMagicModBlockEntities.register("blackstone_chalice", GoetyMasteryOfMagicModBlocks.BLACKSTONE_CHALICE, BlackstoneChaliceBlockEntity::new);
    public static final RegistryObject<BlockEntityType<?>> MOVEMENT_FORCER = GoetyMasteryOfMagicModBlockEntities.register("movement_forcer", GoetyMasteryOfMagicModBlocks.MOVEMENT_FORCER, MovementForcerBlockEntity::new);
    public static final RegistryObject<BlockEntityType<?>> SOUL_BARRIER_BLOCK = GoetyMasteryOfMagicModBlockEntities.register("soul_barrier_block", GoetyMasteryOfMagicModBlocks.SOUL_BARRIER_BLOCK, SoulBarrierBlockEntity::new);

    private static RegistryObject<BlockEntityType<?>> register(String registryname, RegistryObject<Block> block, BlockEntityType.BlockEntitySupplier<?> supplier) {
        return REGISTRY.register(registryname, () -> BlockEntityType.Builder.m_155273_((BlockEntityType.BlockEntitySupplier)supplier, (Block[])new Block[]{(Block)block.get()}).m_58966_(null));
    }
}

