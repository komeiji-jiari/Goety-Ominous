package com.qiuyue.goetyominous.config.ua;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.File;

public class UaConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Double> ThrasherServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> ThrasherServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> ThrasherServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> GreatThrasherServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> GreatThrasherServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> GreatThrasherServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> FlareServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> FlareServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> FlareServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Integer> ThrasherServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> GreatThrasherServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> ThrasherSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> ThrasherCooldown;
    public static final ForgeConfigSpec.ConfigValue<Integer> FlareSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> FlareCooldown;

    static {
        BUILDER.push("Servants Attributes");
        BUILDER.push("Thrasher Servant");
        ThrasherServantHealth = BUILDER.comment("How much Max Health Thrasher Servants have, Default: 50.0")
                .defineInRange("thrasherServantHealth", 50.0, 1.0, Double.MAX_VALUE);
        ThrasherServantDamage = BUILDER.comment("How much damage Thrasher Servants deals, Default: 5.0")
                .defineInRange("thrasherServantDamage", 5.0, 0.0, Double.MAX_VALUE);
        ThrasherServantArmor = BUILDER.comment("How much natural Armor Thrasher Servants have, Default: 8.0")
                .defineInRange("thrasherServantArmor", 8.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Great Thrasher Servant");
        GreatThrasherServantHealth = BUILDER.comment("How much Max Health Great Thrasher Servants have, Default: 125.0")
                .defineInRange("greatThrasherServantHealth", 125.0, 1.0, Double.MAX_VALUE);
        GreatThrasherServantDamage = BUILDER.comment("How much damage Great Thrasher Servants deals, Default: 8.0")
                .defineInRange("greatThrasherServantDamage", 8.0, 0.0, Double.MAX_VALUE);
        GreatThrasherServantArmor = BUILDER.comment("How much natural Armor Great Thrasher Servants have, Default: 16.0")
                .defineInRange("greatThrasherServantArmor", 16.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Flare Servant");
        FlareServantHealth = BUILDER.comment("How much Max Health Flare Servants have, Default: 20.0")
                .defineInRange("flareServantHealth", 20.0, 1.0, Double.MAX_VALUE);
        FlareServantDamage = BUILDER.comment("How much damage Flare Servants deals, Default: 8.0")
                .defineInRange("flareServantDamage", 8.0, 0.0, Double.MAX_VALUE);
        FlareServantArmor = BUILDER.comment("How much natural Armor Flare Servants have, Default: 2.0")
                .defineInRange("flareServantArmor", 2.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.pop();
        BUILDER.push("Servant Limits");
        ThrasherServantLimit = BUILDER
                .comment("Maximum number of Thrasher Servants that can be summoned (Default: 10)")
                .defineInRange("thrasherServantLimit", 10, 1, 100);
        GreatThrasherServantLimit = BUILDER
                .comment("Maximum number of Great Thrasher Servants that can be summoned (Default: 3)")
                .defineInRange("greatThrasherServantLimit", 3, 1, 100);
        BUILDER.pop();
        BUILDER.push("Spells");
        BUILDER.push("Thrasher Spell");
        ThrasherSoulCost = BUILDER.comment("Soul cost of Thrasher summon spell (Default: 64)")
                .defineInRange("thrasherSoulCost", 64, 1, 100);
        ThrasherCooldown = BUILDER.comment("Cooldown of Thrasher summon spell in ticks (Default: 200)")
                .defineInRange("thrasherCooldown", 200, 0, 2000);
        BUILDER.pop();
        BUILDER.push("Flare Spell");
        FlareSoulCost = BUILDER.comment("Soul cost of Flare summon spell (Default: 64)")
                .defineInRange("flareSoulCost", 64, 1, 100);
        FlareCooldown = BUILDER.comment("Cooldown of Flare summon spell in ticks (Default: 200)")
                .defineInRange("flareCooldown", 200, 0, 2000);
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
