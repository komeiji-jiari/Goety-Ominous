package com.qiuyue.goetyominous.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.File;

public class ExpeditionConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.IntValue CompassBindLimit;
    public static final ForgeConfigSpec.IntValue MinTravelers;
    public static final ForgeConfigSpec.IntValue MaxTravelers;
    public static final ForgeConfigSpec.IntValue BaseDraws;
    public static final ForgeConfigSpec.ConfigValue<Double> ExtraDrawChancePerTraveler;
    public static final ForgeConfigSpec.IntValue BiomeTicks;
    public static final ForgeConfigSpec.IntValue RuinTicks;
    public static final ForgeConfigSpec.ConfigValue<Double> DeathChance;
    public static final ForgeConfigSpec.IntValue DeathDecreaseStart;
    public static final ForgeConfigSpec.ConfigValue<Double> DeathDecreasePerTraveler;
    public static final ForgeConfigSpec.ConfigValue<Double> DeathChanceFloor;
    public static final ForgeConfigSpec.IntValue BaseOres;
    public static final ForgeConfigSpec.IntValue OresPerPickaxe;
    public static final ForgeConfigSpec.IntValue BaseMobDraws;
    public static final ForgeConfigSpec.IntValue MobDrawsPerCrossbow;
    public static final ForgeConfigSpec.IntValue DispatchRadius;
    public static final ForgeConfigSpec.BooleanValue RecallLosesTravelers;

    static {
        BUILDER.push("Traveler Expeditions");
        CompassBindLimit = BUILDER.comment("Maximum travelers bound to one Traveler's Compass, Default: 10")
                .defineInRange("compassBindLimit", 10, 1, 64);
        MinTravelers = BUILDER.comment("Minimum travelers required to start an expedition, Default: 1")
                .defineInRange("minTravelers", 1, 1, 64);
        MaxTravelers = BUILDER.comment("Maximum travelers allowed in one expedition, Default: 10")
                .defineInRange("maxTravelers", 10, 1, 64);
        BaseDraws = BUILDER.comment("Base number of structure or biome loot table draws, Default: 3")
                .defineInRange("baseDraws", 3, 0, 64);
        ExtraDrawChancePerTraveler = BUILDER.comment("Chance per traveler above the minimum for one extra loot table draw, Default: 0.25")
                .defineInRange("extraDrawChancePerTraveler", 0.25, 0.0, 1.0);
        BiomeTicks = BUILDER.comment("Biome expedition duration in ticks, Default: 18000")
                .defineInRange("biomeTicks", 18000, 1, Integer.MAX_VALUE);
        RuinTicks = BUILDER.comment("Ruin expedition duration in ticks, Default: 36000")
                .defineInRange("ruinTicks", 36000, 1, Integer.MAX_VALUE);
        DeathChance = BUILDER.comment("Base chance for a traveler to die when departing, Default: 0.20")
                .defineInRange("deathChance", 0.20, 0.0, 1.0);
        DeathDecreaseStart = BUILDER.comment("Traveler count at which the death chance starts decreasing, Default: 7")
                .defineInRange("deathDecreaseStart", 7, 1, 64);
        DeathDecreasePerTraveler = BUILDER.comment("Death chance reduction per traveler above the start count, Default: 0.05")
                .defineInRange("deathDecreasePerTraveler", 0.05, 0.0, 1.0);
        DeathChanceFloor = BUILDER.comment("Minimum departure and recall death chance, Default: 0.0")
                .defineInRange("deathChanceFloor", 0.0, 0.0, 1.0);
        BaseOres = BUILDER.comment("Base number of ore rewards, Default: 0")
                .defineInRange("baseOres", 0, 0, 256);
        OresPerPickaxe = BUILDER.comment("Extra ore rewards per traveler holding a pickaxe, Default: 2")
                .defineInRange("oresPerPickaxe", 2, 0, 64);
        BaseMobDraws = BUILDER.comment("Base number of mob loot draws, Default: 0")
                .defineInRange("baseMobDraws", 0, 0, 256);
        MobDrawsPerCrossbow = BUILDER.comment("Extra mob loot draws per traveler holding a crossbow, Default: 2")
                .defineInRange("mobDrawsPerCrossbow", 2, 0, 64);
        DispatchRadius = BUILDER.comment("Maximum distance between the player and a dispatchable traveler, Default: 16")
                .defineInRange("dispatchRadius", 16, 1, 128);
        RecallLosesTravelers = BUILDER.comment("Whether early recall also rolls departure deaths, Default: false")
                .define("recallLosesTravelers", false);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    public static void loadConfig(ForgeConfigSpec config, String path) {
        final CommentedFileConfig file = CommentedFileConfig.builder(new File(path))
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();
        file.load();
        config.setConfig(file);
    }
}
