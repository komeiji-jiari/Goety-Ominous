package com.qiuyue.goetyominous.config.sar;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.File;

public class SarConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Double> CreepieServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> CreepieServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> GrieferServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> GrieferServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> ExecutionerServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> ExecutionerServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> ExecutionerServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> SkeletonVillagerServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> SkeletonVillagerServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> TricksterServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Integer> SporeCloudSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> SporeCloudCooldown;
    public static final ForgeConfigSpec.ConfigValue<Integer> ConfusionBoltSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> ConfusionBoltCooldown;
    public static final ForgeConfigSpec.ConfigValue<Integer> RunePrisonSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> RunePrisonCooldown;
    public static final ForgeConfigSpec.ConfigValue<Double> RunePrisonDamage;

    static {
        BUILDER.push("Servants Attributes");
        BUILDER.push("Creepie Servant");
        CreepieServantHealth = BUILDER.comment("How much Max Health Creepie Servants have, Default: 5.0")
                .defineInRange("creepieServantHealth", 5.0, 1.0, Double.MAX_VALUE);
        CreepieServantDamage = BUILDER.comment("How much damage Creepie Servants deals, Default: 2.0")
                .defineInRange("creepieServantDamage", 2.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Griefer Servant");
        GrieferServantHealth = BUILDER.comment("How much Max Health Griefer Servants have, Default: 24.0")
                .defineInRange("grieferServantHealth", 24.0, 1.0, Double.MAX_VALUE);
        GrieferServantDamage = BUILDER.comment("How much damage Griefer Servants deals, Default: 5.0")
                .defineInRange("grieferServantDamage", 5.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Executioner Servant");
        ExecutionerServantHealth = BUILDER.comment("How much Max Health Executioner Servants have, Default: 35.0")
                .defineInRange("executionerServantHealth", 35.0, 1.0, Double.MAX_VALUE);
        ExecutionerServantDamage = BUILDER.comment("How much damage Executioner Servants deals, Default: 8.0")
                .defineInRange("executionerServantDamage", 8.0, 0.0, Double.MAX_VALUE);
        ExecutionerServantArmor = BUILDER.comment("How much natural Armor Executioner Servants have, Default: 3.0")
                .defineInRange("executionerServantArmor", 3.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Skeleton Villager Servant");
        SkeletonVillagerServantHealth = BUILDER.comment("How much Max Health Skeleton Villager Servants have, Default: 20.0")
                .defineInRange("skeletonVillagerServantHealth", 20.0, 1.0, Double.MAX_VALUE);
        SkeletonVillagerServantDamage = BUILDER.comment("How much damage Skeleton Villager Servants deals, Default: 3.0")
                .defineInRange("skeletonVillagerServantDamage", 3.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Trickster Servant");
        TricksterServantHealth = BUILDER.comment("How much Max Health Trickster Servants have, Default: 24.0")
                .defineInRange("tricksterServantHealth", 24.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.pop();
        BUILDER.push("Spells");
        BUILDER.push("Spore Cloud Spell");
        SporeCloudSoulCost = BUILDER.comment("Soul cost of Spore Cloud spell (Default: 16)")
                .defineInRange("sporeCloudSoulCost", 16, 1, 100);
        SporeCloudCooldown = BUILDER.comment("Cooldown of Spore Cloud spell in ticks (Default: 200)")
                .defineInRange("sporeCloudCooldown", 200, 0, 2000);
        BUILDER.pop();
        BUILDER.push("Confusion Bolt Spell");
        ConfusionBoltSoulCost = BUILDER.comment("Soul cost of Confusion Bolt spell (Default: 8)")
                .defineInRange("confusionBoltSoulCost", 8, 1, 100);
        ConfusionBoltCooldown = BUILDER.comment("Cooldown of Confusion Bolt spell in ticks (Default: 80)")
                .defineInRange("confusionBoltCooldown", 80, 0, 2000);
        BUILDER.pop();
        BUILDER.push("Rune Prison Spell");
        RunePrisonSoulCost = BUILDER.comment("Soul cost of Rune Prison spell (Default: 2)")
                .defineInRange("runePrisonSoulCost", 2, 1, 100);
        RunePrisonCooldown = BUILDER.comment("Cooldown of Rune Prison spell in ticks (Default: 80)")
                .defineInRange("runePrisonCooldown", 80, 0, 2000);
        RunePrisonDamage = BUILDER.comment("Base damage of Rune Prison with Ominous Staff (Default: 5.0)")
                .defineInRange("runePrisonDamage", 5.0, 0.0, Double.MAX_VALUE);
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
