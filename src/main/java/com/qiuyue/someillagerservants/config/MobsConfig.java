package com.qiuyue.someillagerservants.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.File;

public class MobsConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> ThrasherServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> GreatThrasherServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> MutantWitherSkeletonServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> MutantHoglinServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> OvergrownColossusServantLimit;

    public static final ForgeConfigSpec.ConfigValue<Boolean> ApostleSpawnAcolyte;
    public static final ForgeConfigSpec.ConfigValue<Boolean> HeresiarchHysteriaEnabled;

    static {
        BUILDER.push("Servant Limits");

        ThrasherServantLimit = BUILDER
                .comment("Maximum number of Thrasher Servants that can be summoned at once (Default: 10)")
                .defineInRange("thrasherServantLimit", 10, 1, 100);

        GreatThrasherServantLimit = BUILDER
                .comment("Maximum number of Great Thrasher Servants that can be summoned at once (Default: 7)")
                .defineInRange("greatThrasherServantLimit", 7, 1, 100);

        MutantWitherSkeletonServantLimit = BUILDER
                .comment("Maximum number of Mutant Wither Skeleton Servants that can be summoned at once (Default: 2)")
                .defineInRange("mutantWitherSkeletonServantLimit", 2, 1, 100);

        MutantHoglinServantLimit = BUILDER
                .comment("Maximum number of Mutant Hoglin Servants that can be summoned at once (Default: 2)")
                .defineInRange("mutantHoglinServantLimit", 2, 1, 100);

        OvergrownColossusServantLimit = BUILDER
                .comment("Maximum number of Overgrown Colossus Servants that can be summoned at once (Default: 2)")
                .defineInRange("overgrownColossusServantLimit", 2, 1, 100);

        BUILDER.pop();

        BUILDER.push("Mechanics");
        ApostleSpawnAcolyte = BUILDER
                .comment("Whether Apostles spawn with Acolyte servants in the Nether Sabbath Ritual (Default: false)")
                .define("apostleSpawnAcolyte", false);

        HeresiarchHysteriaEnabled = BUILDER
                .comment("Whether Heresiarch Servants can apply Hysteria effect to nearby cultist servants (Default: true)")
                .define("heresiarchHysteriaEnabled", true);
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