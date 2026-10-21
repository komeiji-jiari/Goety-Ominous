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
    public static final ForgeConfigSpec.ConfigValue<Double> CreepieServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> GrieferServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> GrieferServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> GrieferServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> GrieferServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> ExecutionerServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> ExecutionerServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> ExecutionerServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> ExecutionerServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> ExecutionerServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> SkeletonVillagerServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> SkeletonVillagerServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> SkeletonVillagerServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> SkeletonVillagerServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> TricksterServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> TricksterServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> TricksterServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Integer> SporeCloudSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> SporeCloudCooldown;
    public static final ForgeConfigSpec.ConfigValue<Integer> ConfusionBoltSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> ConfusionBoltCooldown;
    public static final ForgeConfigSpec.ConfigValue<Integer> RunePrisonSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> RunePrisonCooldown;
    public static final ForgeConfigSpec.ConfigValue<Double> RunePrisonDamage;

    static {
        BUILDER.push("Servants Attributes");
        BUILDER.push("SAR Servants");
        CreepieServantHealth = BUILDER.comment("How much Max Health Creepie Servants have, Default: 5.0")
                .defineInRange("creepieServantHealth", 5.0, 1.0, Double.MAX_VALUE);
        CreepieServantDamage = BUILDER.comment("How much damage Creepie Servants deals, Default: 2.0")
                .defineInRange("creepieServantDamage", 2.0, 0.0, Double.MAX_VALUE);
        CreepieServantMovementSpeed = BUILDER.comment("How fast Creepie Servants move, Default: 0.35")
                .defineInRange("creepieServantMovementSpeed", 0.35, 0.0, Double.MAX_VALUE);
        GrieferServantHealth = BUILDER.comment("How much Max Health Griefer Servants have, Default: 24.0")
                .defineInRange("grieferServantHealth", 24.0, 1.0, Double.MAX_VALUE);
        GrieferServantDamage = BUILDER.comment("How much damage Griefer Servants deals, Default: 5.0")
                .defineInRange("grieferServantDamage", 5.0, 0.0, Double.MAX_VALUE);
        GrieferServantMovementSpeed = BUILDER.comment("How fast Griefer Servants move, Default: 0.35")
                .defineInRange("grieferServantMovementSpeed", 0.35, 0.0, Double.MAX_VALUE);
        GrieferServantFollowRange = BUILDER.comment("How much following/detection range Griefer Servants have, Default: 32.0")
                .defineInRange("grieferServantFollowRange", 32.0, 1.0, Double.MAX_VALUE);
        ExecutionerServantHealth = BUILDER.comment("How much Max Health Executioner Servants have, Default: 35.0")
                .defineInRange("executionerServantHealth", 35.0, 1.0, Double.MAX_VALUE);
        ExecutionerServantDamage = BUILDER.comment("How much damage Executioner Servants deals, Default: 8.0")
                .defineInRange("executionerServantDamage", 8.0, 0.0, Double.MAX_VALUE);
        ExecutionerServantMovementSpeed = BUILDER.comment("How fast Executioner Servants move, Default: 0.30")
                .defineInRange("executionerServantMovementSpeed", 0.30, 0.0, Double.MAX_VALUE);
        ExecutionerServantFollowRange = BUILDER.comment("How much following/detection range Executioner Servants have, Default: 14.0")
                .defineInRange("executionerServantFollowRange", 14.0, 1.0, Double.MAX_VALUE);
        ExecutionerServantArmor = BUILDER.comment("How much natural Armor Executioner Servants have, Default: 3.0")
                .defineInRange("executionerServantArmor", 3.0, 0.0, Double.MAX_VALUE);
        SkeletonVillagerServantHealth = BUILDER.comment("How much Max Health Skeleton Villager Servants have, Default: 20.0")
                .defineInRange("skeletonVillagerServantHealth", 20.0, 1.0, Double.MAX_VALUE);
        SkeletonVillagerServantDamage = BUILDER.comment("How much damage Skeleton Villager Servants deals, Default: 3.0")
                .defineInRange("skeletonVillagerServantDamage", 3.0, 0.0, Double.MAX_VALUE);
        SkeletonVillagerServantMovementSpeed = BUILDER.comment("How fast Skeleton Villager Servants move, Default: 0.25")
                .defineInRange("skeletonVillagerServantMovementSpeed", 0.25, 0.0, Double.MAX_VALUE);
        SkeletonVillagerServantFollowRange = BUILDER.comment("How much following/detection range Skeleton Villager Servants have, Default: 16.0")
                .defineInRange("skeletonVillagerServantFollowRange", 16.0, 1.0, Double.MAX_VALUE);
        TricksterServantHealth = BUILDER.comment("How much Max Health Trickster Servants have, Default: 24.0")
                .defineInRange("tricksterServantHealth", 24.0, 1.0, Double.MAX_VALUE);
        TricksterServantMovementSpeed = BUILDER.comment("How fast Trickster Servants move, Default: 0.5")
                .defineInRange("tricksterServantMovementSpeed", 0.5, 0.0, Double.MAX_VALUE);
        TricksterServantFollowRange = BUILDER.comment("How much following/detection range Trickster Servants have, Default: 16.0")
                .defineInRange("tricksterServantFollowRange", 16.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.pop();
        BUILDER.push("Spells");
        BUILDER.push("Spore Cloud");
        SporeCloudSoulCost = BUILDER.comment("Soul cost of Spore Cloud spell (Default: 16)")
                .defineInRange("sporeCloudSoulCost", 16, 1, 100);
        SporeCloudCooldown = BUILDER.comment("Cooldown of Spore Cloud spell in ticks (Default: 200)")
                .defineInRange("sporeCloudCooldown", 200, 0, 2000);
        BUILDER.pop();
        BUILDER.push("Confusion Bolt");
        ConfusionBoltSoulCost = BUILDER.comment("Soul cost of Confusion Bolt spell (Default: 8)")
                .defineInRange("confusionBoltSoulCost", 8, 1, 100);
        ConfusionBoltCooldown = BUILDER.comment("Cooldown of Confusion Bolt spell in ticks (Default: 80)")
                .defineInRange("confusionBoltCooldown", 80, 0, 2000);
        BUILDER.pop();
        BUILDER.push("Rune Prison");
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
