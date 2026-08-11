package com.qiuyue.goetyominous.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.File;

public class MobsConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> MurmurServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> FarseerServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> CrimsonMosquitoServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> WarpedMoscoServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> IllagerElephantServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> UrbhadhachServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> ThrasherServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> GreatThrasherServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> MutantWitherSkeletonServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> MutantHoglinServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> OvergrownColossusServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> HeresiarchServantLimit;

    public static final ForgeConfigSpec.ConfigValue<Boolean> CultistPatrol;
    public static final ForgeConfigSpec.ConfigValue<Integer> CultistPatrolInterval;
    public static final ForgeConfigSpec.ConfigValue<Integer> UrbhadhachSpawnWeight;
    public static final ForgeConfigSpec.ConfigValue<Integer> UrbhadhachSpawnMinCount;
    public static final ForgeConfigSpec.ConfigValue<Integer> UrbhadhachSpawnMaxCount;
    public static final ForgeConfigSpec.ConfigValue<Integer> CultistSpawnWeight;
    public static final ForgeConfigSpec.ConfigValue<Integer> CultistSpawnMinCount;
    public static final ForgeConfigSpec.ConfigValue<Integer> CultistSpawnMaxCount;
    public static final ForgeConfigSpec.ConfigValue<Integer> BeldamSpawnWeight;
    public static final ForgeConfigSpec.ConfigValue<Integer> BeldamSpawnMinCount;
    public static final ForgeConfigSpec.ConfigValue<Integer> BeldamSpawnMaxCount;
    public static final ForgeConfigSpec.ConfigValue<Integer> FanaticSpawnWeight;
    public static final ForgeConfigSpec.ConfigValue<Integer> FanaticSpawnMinCount;
    public static final ForgeConfigSpec.ConfigValue<Integer> FanaticSpawnMaxCount;
    public static final ForgeConfigSpec.ConfigValue<Integer> ZealotSpawnWeight;
    public static final ForgeConfigSpec.ConfigValue<Integer> ZealotSpawnMinCount;
    public static final ForgeConfigSpec.ConfigValue<Integer> ZealotSpawnMaxCount;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ApostleSpawnDisciple;
    public static final ForgeConfigSpec.ConfigValue<Integer> ChannellerMaxStealHealth;
    public static final ForgeConfigSpec.ConfigValue<Boolean> HeresiarchHysteriaEnabled;
    public static final ForgeConfigSpec.ConfigValue<Boolean> CultistSpread;
    public static final ForgeConfigSpec.ConfigValue<Boolean> MonolithConversionEnabled;
    public static final ForgeConfigSpec.ConfigValue<Integer> MonolithConversionTime;
    public static final ForgeConfigSpec.ConfigValue<Integer> PiglinServantBabyGrowthTime;
    public static final ForgeConfigSpec.ConfigValue<Integer> PiglinServantEvolutionDamage;
    public static final ForgeConfigSpec.ConfigValue<Integer> PiglinBruteServantEvolutionDamage;
    public static final ForgeConfigSpec.ConfigValue<Integer> StrongPiglinBruteServantEvolutionDamage;
    public static final ForgeConfigSpec.ConfigValue<Integer> PiglinServantRangedEvolutionDamage;
    public static final ForgeConfigSpec.ConfigValue<Integer> PiglinHunterServantEvolutionDamage;
    public static final ForgeConfigSpec.ConfigValue<Integer> StrongPiglinHunterServantEvolutionDamage;

    static {
        BUILDER.push("Servant Limits");

        MurmurServantLimit = BUILDER
                .comment("Maximum number of Murmur Servants that can be summoned (Default: 16)")
                .defineInRange("murmurServantLimit", 16, 1, 100);

        FarseerServantLimit = BUILDER
                .comment("Maximum number of Farseer Servants that can be summoned (Default: 2)")
                .defineInRange("farseerServantLimit", 2, 1, 100);

        CrimsonMosquitoServantLimit = BUILDER
                .comment("Maximum number of Crimson Mosquito Servants that can be summoned (Default: 8)")
                .defineInRange("crimsonMosquitoServantLimit", 8, 1, 100);

        WarpedMoscoServantLimit = BUILDER
                .comment("Maximum number of Warped Mosco Servants that can be summoned (Default: 2)")
                .defineInRange("warpedMoscoServantLimit", 2, 1, 100);

        IllagerElephantServantLimit = BUILDER
                .comment("Maximum number of Illager Elephant Servants that can be summoned (Default: 2)")
                .defineInRange("illagerElephantServantLimit", 2, 1, 100);

        UrbhadhachServantLimit = BUILDER
                .comment("Maximum number of Urbhadhach Servants that can be summoned (Default: 8)")
                .defineInRange("urbhadhachServantLimit", 8, 1, 100);

        HeresiarchServantLimit = BUILDER
                .comment("Maximum number of Heresiarch Servants that can be summoned (Default: 3)")
                .defineInRange("heresiarchServantLimit", 3, 1, 100);

        ThrasherServantLimit = BUILDER
                .comment("Maximum number of Thrasher Servants that can be summoned (Default: 10)")
                .defineInRange("thrasherServantLimit", 10, 1, 100);

        GreatThrasherServantLimit = BUILDER
                .comment("Maximum number of Great Thrasher Servants that can be summoned (Default: 7)")
                .defineInRange("greatThrasherServantLimit", 3, 1, 100);

        MutantWitherSkeletonServantLimit = BUILDER
                .comment("Maximum number of Mutant Wither Skeleton Servants that can be summoned (Default: 2)")
                .defineInRange("mutantWitherSkeletonServantLimit", 2, 1, 100);

        MutantHoglinServantLimit = BUILDER
                .comment("Maximum number of Mutant Hoglin Servants that can be summoned (Default: 2)")
                .defineInRange("mutantHoglinServantLimit", 2, 1, 100);

        OvergrownColossusServantLimit = BUILDER
                .comment("Maximum number of Overgrown Colossus Servants that can be summoned (Default: 2)")
                .defineInRange("overgrownColossusServantLimit", 2, 1, 100);

        BUILDER.pop();

        BUILDER.push("Mechanics");
        ApostleSpawnDisciple = BUILDER
                .comment("Whether Apostles spawn with Disciple servants in the Nether Sabbath Ritual (Default: false)")
                .define("apostleSpawnDisciple", false);

        HeresiarchHysteriaEnabled = BUILDER
                .comment("Whether Heresiarch Servants can apply Hysteria effect to nearby cultist servants (Default: true)")
                .define("heresiarchHysteriaEnabled", true);

        CultistSpread = BUILDER
                .comment("Whether Villagers are able to become secret Cultists (Default: true)")
                .define("cultistSpread", true);

        ChannellerMaxStealHealth = BUILDER
                .comment("Maximum health for mobs that Channeller can steal/control (Default: 40)")
                .defineInRange("channellerMaxStealHealth", 40, 1, 1000);

        CultistPatrol = BUILDER
                .comment("Whether AbstractGOCultists can spawn in night patrols near players (Default: true)")
                .define("cultistPatrol", true);

        CultistPatrolInterval = BUILDER
                .comment("Ticks between cultist patrol spawn attempts (Default: 18000)")
                .defineInRange("cultistPatrolInterval", 18000, 1200, 72000);

        MonolithConversionEnabled = BUILDER
                .comment("Whether Obsidian Monolith can convert nearby villagers/witches/wandering traders into cultists (Default: true)")
                .define("monolithConversionEnabled", true);

        MonolithConversionTime = BUILDER
                .comment("How many seconds it takes for a villager to be converted near an Obsidian Monolith (Default: 300)")
                .defineInRange("monolithConversionTime", 300, 10, 3600);

        CultistSpawnWeight = BUILDER
                .comment("Natural spawn weight for Beldam/Fanatic/Zealot (0 to disable, Default: 5)")
                .defineInRange("cultistSpawnWeight", 5, 0, 100);

        CultistSpawnMinCount = BUILDER
                .comment("Minimum group size for cultist natural spawn (Default: 1)")
                .defineInRange("cultistSpawnMinCount", 1, 1, 10);
        CultistSpawnMaxCount = BUILDER
                .comment("Maximum group size for cultist natural spawn (Default: 1)")
                .defineInRange("cultistSpawnMaxCount", 1, 1, 10);

        BeldamSpawnWeight = BUILDER
                .comment("Natural spawn weight for Beldam (0 to disable, Default: 5)")
                .defineInRange("beldamSpawnWeight", 5, 0, 100);
        BeldamSpawnMinCount = BUILDER
                .comment("Minimum group size for Beldam natural spawn (Default: 1)")
                .defineInRange("beldamSpawnMinCount", 1, 1, 10);
        BeldamSpawnMaxCount = BUILDER
                .comment("Maximum group size for Beldam natural spawn (Default: 1)")
                .defineInRange("beldamSpawnMaxCount", 1, 1, 10);

        FanaticSpawnWeight = BUILDER
                .comment("Natural spawn weight for Fanatic (0 to disable, Default: 10)")
                .defineInRange("fanaticSpawnWeight", 10, 0, 100);
        FanaticSpawnMinCount = BUILDER
                .comment("Minimum group size for Fanatic natural spawn (Default: 1)")
                .defineInRange("fanaticSpawnMinCount", 1, 1, 10);
        FanaticSpawnMaxCount = BUILDER
                .comment("Maximum group size for Fanatic natural spawn (Default: 4)")
                .defineInRange("fanaticSpawnMaxCount", 4, 1, 10);

        ZealotSpawnWeight = BUILDER
                .comment("Natural spawn weight for Zealot (0 to disable, Default: 7)")
                .defineInRange("zealotSpawnWeight", 7, 0, 100);
        ZealotSpawnMinCount = BUILDER
                .comment("Minimum group size for Zealot natural spawn (Default: 1)")
                .defineInRange("zealotSpawnMinCount", 1, 1, 10);
        ZealotSpawnMaxCount = BUILDER
                .comment("Maximum group size for Zealot natural spawn (Default: 2)")
                .defineInRange("zealotSpawnMaxCount", 2, 1, 10);

        UrbhadhachSpawnWeight = BUILDER
                .comment("Natural spawn weight for Urbhadhach (0 to disable, Default: 12)")
                .defineInRange("urbhadhachSpawnWeight", 12, 0, 100);
        UrbhadhachSpawnMinCount = BUILDER
                .comment("Minimum group size for Urbhadhach natural spawn (Default: 1)")
                .defineInRange("urbhadhachSpawnMinCount", 1, 1, 10);
        UrbhadhachSpawnMaxCount = BUILDER
                .comment("Maximum group size for Urbhadhach natural spawn (Default: 1)")
                .defineInRange("urbhadhachSpawnMaxCount", 1, 1, 10);

        PiglinServantBabyGrowthTime = BUILDER.comment("How many ticks it takes for a baby Piglin Servant to grow up, Default: 12000 (10 minutes)")
                .defineInRange("piglinServantBabyGrowthTime", 12000, 1, Integer.MAX_VALUE);

        PiglinServantEvolutionDamage = BUILDER
                .comment("How much melee damage a Piglin Servant needs to evolve into a Brute, Default: 100")
                .defineInRange("piglinServantEvolutionDamage", 100, 1, Integer.MAX_VALUE);

        PiglinBruteServantEvolutionDamage = BUILDER
                .comment("How much melee damage a Brute needs to evolve into Strong, Default: 180")
                .defineInRange("piglinBruteServantEvolutionDamage", 180, 1, Integer.MAX_VALUE);
        StrongPiglinBruteServantEvolutionDamage = BUILDER

                .comment("How much melee damage a Strong Brute needs to evolve into Elite, Default: 260")
                .defineInRange("strongPiglinBruteServantEvolutionDamage", 260, 1, Integer.MAX_VALUE);
        PiglinServantRangedEvolutionDamage = BUILDER

                .comment("How much ranged damage a Piglin Servant needs to evolve into a Hunter, Default: 60")
                .defineInRange("piglinServantRangedEvolutionDamage", 60, 1, Integer.MAX_VALUE);

        PiglinHunterServantEvolutionDamage = BUILDER
                .comment("How much ranged damage a Hunter needs to evolve into Strong, Default: 80")
                .defineInRange("piglinHunterServantEvolutionDamage", 80, 1, Integer.MAX_VALUE);

        StrongPiglinHunterServantEvolutionDamage = BUILDER
                .comment("How much ranged damage a Strong Hunter needs to evolve into Elite, Default: 160")
                .defineInRange("strongPiglinHunterServantEvolutionDamage", 160, 1, Integer.MAX_VALUE);
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