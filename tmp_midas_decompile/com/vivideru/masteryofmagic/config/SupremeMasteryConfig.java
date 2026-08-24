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

public final class SupremeMasteryConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue ENABLED;
    public static final ForgeConfigSpec.BooleanValue SERVANT_VARIETY_ENABLED;
    public static final ForgeConfigSpec.IntValue RITUAL_RADIUS;
    public static final ForgeConfigSpec.IntValue SERVANT_FULL_VALUE_COPIES;
    public static final ForgeConfigSpec.DoubleValue SERVANT_DUPLICATE_SCORE_DECAY;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Integer>> SERVANT_SCORE_REQUIRED;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Integer>> EVOKERS_REQUIRED;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Integer>> MAGIC_SERVANTS_REQUIRED;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Integer>> ENCHANTING_POWER_REQUIRED;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Integer>> LECTERNS_REQUIRED;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Integer>> CHARGED_LAZETHYST_REQUIRED;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Integer>> OMINOUS_LAZETHYST_REQUIRED;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Integer>> MAGIC_VESSELS_REQUIRED;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> SERVANT_SCORES;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> MAGIC_SERVANTS;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> MAGIC_VESSEL_BLOCKS;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> ENCHANTING_POWER_BLOCKS;

    private SupremeMasteryConfig() {
    }

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, (IConfigSpec)SPEC, "goety-mastery-of-magic-supreme-masteries.toml");
    }

    public static int forLevel(ForgeConfigSpec.ConfigValue<List<? extends Integer>> value, int level) {
        List list = (List)value.get();
        return (Integer)list.get(Math.max(0, Math.min(list.size() - 1, level - 1)));
    }

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
        b.push("supremeMasteries");
        ENABLED = b.define("enabled", true);
        RITUAL_RADIUS = b.defineInRange("ritualScanRadius", 96, 16, 192);
        SERVANT_VARIETY_ENABLED = b.comment("Whether repeated servants of the same entity type receive diminishing score").define("servantVarietyEnabled", true);
        SERVANT_FULL_VALUE_COPIES = b.comment("How many servants of each entity type grant their full configured score").defineInRange("servantFullValueCopiesPerType", 3, 1, 64);
        SERVANT_DUPLICATE_SCORE_DECAY = b.comment("Multiplier applied successively after the full-value copies. With 0.5, later copies grant 50%, 25%, 12.5%, and so on").defineInRange("servantDuplicateScoreDecay", 0.5, 0.0, 0.95);
        SERVANT_SCORE_REQUIRED = b.defineList("servantScoreRequiredPerLevel", List.of(Integer.valueOf(20), Integer.valueOf(50), Integer.valueOf(100)), o -> o instanceof Integer && (Integer)o >= 0);
        EVOKERS_REQUIRED = b.defineList("evokersRequiredPerLevel", List.of(Integer.valueOf(4), Integer.valueOf(8), Integer.valueOf(15)), o -> o instanceof Integer && (Integer)o >= 0);
        MAGIC_SERVANTS_REQUIRED = b.defineList("magicServantsRequiredPerLevel", List.of(Integer.valueOf(0), Integer.valueOf(4), Integer.valueOf(0)), o -> o instanceof Integer && (Integer)o >= 0);
        ENCHANTING_POWER_REQUIRED = b.defineList("enchantingPowerRequiredPerLevel", List.of(Integer.valueOf(30), Integer.valueOf(50), Integer.valueOf(100)), o -> o instanceof Integer && (Integer)o >= 0);
        LECTERNS_REQUIRED = b.defineList("lecternsWithBooksRequiredPerLevel", List.of(Integer.valueOf(0), Integer.valueOf(8), Integer.valueOf(20)), o -> o instanceof Integer && (Integer)o >= 0);
        CHARGED_LAZETHYST_REQUIRED = b.defineList("chargedLazethystRequiredPerLevel", List.of(Integer.valueOf(1), Integer.valueOf(4), Integer.valueOf(10)), o -> o instanceof Integer && (Integer)o >= 0);
        OMINOUS_LAZETHYST_REQUIRED = b.defineList("ominousChargedLazethystRequiredPerLevel", List.of(Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(4)), o -> o instanceof Integer && (Integer)o >= 0);
        MAGIC_VESSELS_REQUIRED = b.defineList("magicVesselsRequiredPerLevel", List.of(Integer.valueOf(0), Integer.valueOf(20), Integer.valueOf(40)), o -> o instanceof Integer && (Integer)o >= 0);
        SERVANT_SCORES = b.defineList("servantScores", List.of("goety:pillager_servant=1", "goety:vindicator_servant=1", "goety:vindicator_chef_servant=1", "goety:piker_servant=1", "goety:signaler_servant=1", "goety:mountaineer_servant=1", "goety:crusher_servant=2", "goety:evoker_servant=2", "goety:geomancer_servant=3", "goety:iceologer_servant=3", "goety:cryologer_servant=3", "goety:wind_caller_servant=3", "goety:storm_caster_servant=3", "goety:witch_servant=3", "goety:warlock_servant=3", "goety:heretic_servant=3", "goety:maverick_servant=3", "goety:reprobate_servant=3", "goety:mod_ravager=5", "goety:redstone_golem=5", "goety:redstone_monstrosity=20"), o -> o instanceof String && ((String)o).contains("="));
        MAGIC_SERVANTS = b.defineList("magicServantEntityIds", List.of("goety:witch_servant", "goety:warlock_servant", "goety:iceologer_servant", "goety:geomancer_servant", "goety:wind_caller_servant", "goety:storm_caster_servant"), o -> o instanceof String);
        MAGIC_VESSEL_BLOCKS = b.comment("Blocks counted as Stash Urns by Supreme Mastery rituals").defineList("magicVesselBlockIds", List.of("goety:stash_urn"), o -> o instanceof String);
        ENCHANTING_POWER_BLOCKS = b.comment("Fallback enchanting-power values for blocks that do not expose Forge's enchanting-power API").defineList("enchantingPowerBlocks", List.of("minecraft:bookshelf=1", "minecraft:chiseled_bookshelf=1"), o -> o instanceof String && ((String)o).contains("="));
        b.pop();
        SPEC = b.build();
    }
}

