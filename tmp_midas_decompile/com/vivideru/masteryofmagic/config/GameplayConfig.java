/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.common.ForgeConfigSpec
 *  net.minecraftforge.common.ForgeConfigSpec$BooleanValue
 *  net.minecraftforge.common.ForgeConfigSpec$Builder
 *  net.minecraftforge.common.ForgeConfigSpec$ConfigValue
 *  net.minecraftforge.common.ForgeConfigSpec$DoubleValue
 *  net.minecraftforge.common.ForgeConfigSpec$IntValue
 *  net.minecraftforge.fml.ModLoadingContext
 *  net.minecraftforge.fml.config.IConfigSpec
 *  net.minecraftforge.fml.config.ModConfig$Type
 */
package com.vivideru.masteryofmagic.config;

import java.util.List;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public final class GameplayConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue SOUL_ENERGY_CHALICE_REGENERATION_ENABLED;
    public static final ForgeConfigSpec.IntValue SOUL_ENERGY_CHALICE_REGENERATION_AMOUNT;
    public static final ForgeConfigSpec.IntValue SOUL_ENERGY_CHALICE_REGENERATION_INTERVAL;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> IMPROVED_FORGING_RING_BLACKLIST;
    public static final ForgeConfigSpec.DoubleValue GOLDIFICATION_MAX_COMMAND_RADIUS;
    public static final ForgeConfigSpec.IntValue GOLDIFICATION_MAX_BLOCKS_PER_COMMAND;
    public static final ForgeConfigSpec.IntValue GOLDIFICATION_BLOCKS_PER_TICK;
    public static final ForgeConfigSpec.BooleanValue GOLDIFICATION_GOLDIFY_PLAYERS;
    public static final ForgeConfigSpec.BooleanValue GOLDIFICATION_GOLDIFY_BLOCK_ENTITIES;
    public static final ForgeConfigSpec.IntValue GOLDIFICATION_NUGGET_DROP_MIN;
    public static final ForgeConfigSpec.IntValue GOLDIFICATION_NUGGET_DROP_MAX;
    public static final ForgeConfigSpec.IntValue GOLDIFICATION_DEFAULT_DURATION_TICKS;
    public static final ForgeConfigSpec.BooleanValue GOLDIFICATION_ALLOW_AUTO_SHATTER;

    private GameplayConfig() {
    }

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, (IConfigSpec)SPEC, "Goety_MOfM_Config/GameplayConfig.toml");
    }

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("soul_energy_chalice");
        SOUL_ENERGY_CHALICE_REGENERATION_ENABLED = builder.comment("Whether the Soul Energy Chalice permanently regenerates Soul Energy for Liches that drank it.").define("soulEnergyChaliceRegenerationEnabled", true);
        SOUL_ENERGY_CHALICE_REGENERATION_AMOUNT = builder.comment("Soul Energy restored at each regeneration interval.").defineInRange("soulEnergyChaliceRegenerationAmount", 5, 0, Integer.MAX_VALUE);
        SOUL_ENERGY_CHALICE_REGENERATION_INTERVAL = builder.comment("Delay between Soul Energy regeneration pulses, in ticks (20 ticks = 1 second).").defineInRange("soulEnergyChaliceRegenerationInterval", 20, 1, Integer.MAX_VALUE);
        builder.pop();
        builder.push("improved_forging_ring");
        IMPROVED_FORGING_RING_BLACKLIST = builder.comment(new String[]{"Weapons and armor that cannot be stored in the improved forging ring.", "Supported formats: exact item IDs (modid:item), namespace wildcards (modid:*),", "path wildcards (modid:*sword* or modid:dark_*), and item tags (#namespace:tag).", "Matching uses only the registered item ID or item tags; name, durability, enchantments and NBT are ignored.", "Invalid entries are ignored and logged as warnings."}).defineListAllowEmpty("improvedForgingRingBlacklist", List.of("#goety_mastery_of_magic:improved_forging_ring_blacklist"), value -> value instanceof String);
        builder.pop();
        builder.push("goldification");
        GOLDIFICATION_MAX_COMMAND_RADIUS = builder.comment("Maximum radius accepted by the administrative /goldify command.").defineInRange("maxCommandRadius", 64.0, 0.5, 256.0);
        GOLDIFICATION_MAX_BLOCKS_PER_COMMAND = builder.comment("Maximum number of blocks a single /goldify command may goldify.").defineInRange("maxBlocksPerCommand", 100000, 1, 1000000);
        GOLDIFICATION_BLOCKS_PER_TICK = builder.comment("Maximum number of block positions inspected per tick by each goldification area job.").defineInRange("blocksPerTick", 4096, 64, 65536);
        GOLDIFICATION_GOLDIFY_PLAYERS = builder.comment("Whether survival players may be goldified. Creative and spectator players are always immune.").define("goldifyPlayers", false);
        GOLDIFICATION_GOLDIFY_BLOCK_ENTITIES = builder.comment("Whether blocks containing BlockEntities, including inventories, may be goldified.").define("goldifyBlockEntities", true);
        GOLDIFICATION_NUGGET_DROP_MIN = builder.comment("Minimum number of gold nuggets dropped by a shattered target.").defineInRange("nuggetDropMin", 1, 0, 64);
        GOLDIFICATION_NUGGET_DROP_MAX = builder.comment("Maximum number of gold nuggets dropped by a shattered target.").defineInRange("nuggetDropMax", 4, 0, 64);
        GOLDIFICATION_DEFAULT_DURATION_TICKS = builder.comment("Default duration used by future callers that do not provide one explicitly.").defineInRange("defaultDurationTicks", 200, 1, Integer.MAX_VALUE);
        GOLDIFICATION_ALLOW_AUTO_SHATTER = builder.comment("Allows future Midas abilities to request delayed automatic shattering.").define("allowAutoShatter", true);
        builder.pop();
        SPEC = builder.build();
    }
}

