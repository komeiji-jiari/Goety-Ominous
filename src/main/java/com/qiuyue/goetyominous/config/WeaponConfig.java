package com.qiuyue.goetyominous.config;

import net.minecraftforge.common.ForgeConfigSpec;
import java.io.File;

public class WeaponConfig {

    public static void loadConfig(ForgeConfigSpec spec, String path) {
        final com.electronwill.nightconfig.core.file.CommentedFileConfig configData =
                com.electronwill.nightconfig.core.file.CommentedFileConfig.builder(new File(path))
                        .sync()
                        .autoreload()
                        .writingMode(com.electronwill.nightconfig.core.io.WritingMode.REPLACE)
                        .build();
        configData.load();
        spec.setConfig(configData);
    }

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> WolfArmorDurability;
    public static final ForgeConfigSpec.ConfigValue<Double> WolfArmorIngotRepair;
    public static final ForgeConfigSpec.ConfigValue<Integer> WolfArmorSoulRepairInterval;

    public static final ForgeConfigSpec.ConfigValue<Integer> WargArmorDurability;
    public static final ForgeConfigSpec.ConfigValue<Double> WargArmorIngotRepair;
    public static final ForgeConfigSpec.ConfigValue<Integer> WargArmorSoulRepairInterval;

    public static final ForgeConfigSpec.ConfigValue<Integer> BlackBeastArmorDurability;
    public static final ForgeConfigSpec.ConfigValue<Double> BlackBeastArmorIngotRepair;
    public static final ForgeConfigSpec.ConfigValue<Integer> BlackBeastArmorSoulRepairInterval;

    public static final ForgeConfigSpec.ConfigValue<Double> BoneCudgelDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> BoneCudgelAttackSpeed;

    public static final ForgeConfigSpec.ConfigValue<Double> WitherScytheDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> WitherScytheAttackSpeed;

    public static final ForgeConfigSpec.ConfigValue<Double> FirebrandDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> FirebrandFireBonus;

    static {
        BUILDER.push("Bone Cudgel");
        BoneCudgelDamage = BUILDER
                .comment("Bone Cudgel base damage, Default: 13.0")
                .defineInRange("boneCudgelDamage", 13.0, 0.0, Double.MAX_VALUE);
        BoneCudgelAttackSpeed = BUILDER
                .comment("Bone Cudgel attack speed modifier, Default: -3.75")
                .defineInRange("boneCudgelAttackSpeed", -3.75, -4.0, 0.0);
        BUILDER.pop();

        BUILDER.push("Wither Scythe");
        WitherScytheDamage = BUILDER
                .comment("Wither Scythe total attack damage, Default: 9.5")
                .defineInRange("witherScytheDamage", 9.5, 0.0, Double.MAX_VALUE);
        WitherScytheAttackSpeed = BUILDER
                .comment("Wither Scythe attack speed modifier, Default: -3.3")
                .defineInRange("witherScytheAttackSpeed", -3.3, -4.0, 10.0);
        BUILDER.pop();

        BUILDER.push("Firebrand");
        FirebrandDamage = BUILDER
                .comment("Firebrand total attack damage, Default: 9.0")
                .defineInRange("firebrandDamage", 9.0, 0.0, Double.MAX_VALUE);
        FirebrandFireBonus = BUILDER
                .comment("Firebrand bonus fire damage dealt to burning targets, Default: 3.0")
                .defineInRange("firebrandFireBonus", 3.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();

        BUILDER.push("Wolf Armor");
        WolfArmorDurability = BUILDER
                .comment("Cursed Metal Wolf Armor durability, Default: 78")
                .defineInRange("wolfArmorDurability", 78, 1, Integer.MAX_VALUE);
        WolfArmorIngotRepair = BUILDER
                .comment("Fraction of max durability repaired per Cursed Metal Ingot, Default: 0.125")
                .defineInRange("wolfArmorIngotRepair", 0.125, 0.01, 1.0);
        WolfArmorSoulRepairInterval = BUILDER
                .comment("Dark Wolf Armor soul repair interval in ticks (1 durability per N ticks), Default: 40")
                .defineInRange("wolfArmorSoulRepairInterval", 40, 1, 10000);
        BUILDER.pop();

        BUILDER.push("Warg Armor");
        WargArmorDurability = BUILDER
                .comment("Cursed Warg Armor durability, Default: 78")
                .defineInRange("wargArmorDurability", 78, 1, Integer.MAX_VALUE);
        WargArmorIngotRepair = BUILDER
                .comment("Fraction of max durability repaired per Cursed Metal Ingot, Default: 0.125")
                .defineInRange("wargArmorIngotRepair", 0.125, 0.01, 1.0);
        WargArmorSoulRepairInterval = BUILDER
                .comment("Dark Warg Armor soul repair interval in ticks (1 durability per N ticks), Default: 40")
                .defineInRange("wargArmorSoulRepairInterval", 40, 1, 10000);
        BUILDER.pop();

        BUILDER.push("Black Beast Armor");
        BlackBeastArmorDurability = BUILDER
                .comment("Cursed Black Beast Armor durability, Default: 78")
                .defineInRange("blackBeastArmorDurability", 78, 1, Integer.MAX_VALUE);
        BlackBeastArmorIngotRepair = BUILDER
                .comment("Fraction of max durability repaired per Cursed Metal Ingot, Default: 0.125")
                .defineInRange("blackBeastArmorIngotRepair", 0.125, 0.01, 1.0);
        BlackBeastArmorSoulRepairInterval = BUILDER
                .comment("Dark Black Beast Armor soul repair interval in ticks (1 durability per N ticks), Default: 40")
                .defineInRange("blackBeastArmorSoulRepairInterval", 40, 1, 10000);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
