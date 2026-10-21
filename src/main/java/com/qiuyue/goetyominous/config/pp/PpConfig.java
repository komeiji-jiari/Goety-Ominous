package com.qiuyue.goetyominous.config.pp;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.File;

public class PpConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Double> PiglinAlchemistServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> PiglinAlchemistServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> PiglinAlchemistServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> PiglinAlchemistServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> PiglinAlchemistServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> PiglinAlchemistServantBeltDropChance;
    public static final ForgeConfigSpec.ConfigValue<Double> PiglinAlchemistServantHealingArrowChance;
    public static final ForgeConfigSpec.ConfigValue<Double> PiglinTravelerServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> PiglinTravelerServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> PiglinTravelerServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> PiglinTravelerServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> PiglinTravelerServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> PiglinTravelerServantCrossbowChance;

    static {
        BUILDER.push("Servants Attributes");
        BUILDER.push("Piglin Proliferation Servants");
        PiglinAlchemistServantHealth = BUILDER.comment("How much Max Health Piglin Alchemist Servants have, Default: 20.0")
                .defineInRange("piglinAlchemistServantHealth", 20.0, 1.0, Double.MAX_VALUE);
        PiglinAlchemistServantArmor = BUILDER.comment("How much armor Piglin Alchemist Servants have, Default: 0.0")
                .defineInRange("piglinAlchemistServantArmor", 0.0, 0.0, Double.MAX_VALUE);
        PiglinAlchemistServantFollowRange = BUILDER.comment("How much following/detection range Piglin Alchemist Servants have, Default: 24.0")
                .defineInRange("piglinAlchemistServantFollowRange", 24.0, 1.0, Double.MAX_VALUE);
        PiglinAlchemistServantMovementSpeed = BUILDER.comment("How fast Piglin Alchemist Servants move, Default: 0.35")
                .defineInRange("piglinAlchemistServantMovementSpeed", 0.35, 0.0, Double.MAX_VALUE);
        PiglinAlchemistServantDamage = BUILDER.comment("How much damage Piglin Alchemist Servants deal, Default: 5.0")
                .defineInRange("piglinAlchemistServantDamage", 5.0, 0.0, Double.MAX_VALUE);
        PiglinAlchemistServantBeltDropChance = BUILDER.comment("Chance for each belt item to drop on death, Default: 0.5")
                .defineInRange("piglinAlchemistServantBeltDropChance", 0.5, 0.0, 1.0);
        PiglinAlchemistServantHealingArrowChance = BUILDER.comment("Chance for a Piglin Alchemist Servant to spawn with a stack of healing arrows, Default: 0.3")
                .defineInRange("piglinAlchemistServantHealingArrowChance", 0.3, 0.0, 1.0);
        PiglinTravelerServantHealth = BUILDER.comment("How much Max Health Piglin Traveler Servants have, Default: 16.0")
                .defineInRange("piglinTravelerServantHealth", 16.0, 1.0, Double.MAX_VALUE);
        PiglinTravelerServantArmor = BUILDER.comment("How much armor Piglin Traveler Servants have, Default: 0.0")
                .defineInRange("piglinTravelerServantArmor", 0.0, 0.0, Double.MAX_VALUE);
        PiglinTravelerServantFollowRange = BUILDER.comment("How much following/detection range Piglin Traveler Servants have, Default: 16.0")
                .defineInRange("piglinTravelerServantFollowRange", 16.0, 1.0, Double.MAX_VALUE);
        PiglinTravelerServantMovementSpeed = BUILDER.comment("How fast Piglin Traveler Servants move, Default: 0.35")
                .defineInRange("piglinTravelerServantMovementSpeed", 0.35, 0.0, Double.MAX_VALUE);
        PiglinTravelerServantDamage = BUILDER.comment("How much damage Piglin Traveler Servants deal, Default: 5.0")
                .defineInRange("piglinTravelerServantDamage", 5.0, 0.0, Double.MAX_VALUE);
        PiglinTravelerServantCrossbowChance = BUILDER.comment("Chance for a Piglin Traveler Servant to spawn with a crossbow instead of a golden sword, Default: 0.2")
                .defineInRange("piglinTravelerServantCrossbowChance", 0.2, 0.0, 1.0);
        BUILDER.pop();
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
