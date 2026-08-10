package com.qiuyue.goetyominous.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.File;

public class SpellConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> BrainEaterDrainPerSecond;
    public static final ForgeConfigSpec.ConfigValue<Integer> BrainEaterSoulsPerDrain;

    public static final ForgeConfigSpec.ConfigValue<Integer> UrbhadhachSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> UrbhadhachCastDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> UrbhadhachSummonDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> UrbhadhachCoolDown;

    public static final ForgeConfigSpec.ConfigValue<Integer> ScorchSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> ScorchCastDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> ScorchSummonDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> ScorchCoolDown;

    public static final ForgeConfigSpec.ConfigValue<Integer> BroodSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> BroodSummonDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> BroodCoolDown;

    public static final ForgeConfigSpec.ConfigValue<Integer> SpiderCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> SpiderDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> SpiderCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> SpiderSummonDown;

    public static final ForgeConfigSpec.ConfigValue<Integer> RedstoneCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> RedstoneDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> RedstoneCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> RedstoneSummonDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> RedstoneCubeLimit;

    public static final ForgeConfigSpec.ConfigValue<Integer> HogChargeSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> HogChargeCooldown;
    public static final ForgeConfigSpec.ConfigValue<Double> HogChargeDamage;

    public static final ForgeConfigSpec.ConfigValue<Integer> WitherBreathSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> WitherBreathCooldown;

    public static final ForgeConfigSpec.ConfigValue<Integer> WitherSlashSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> WitherSlashCooldown;
    public static final ForgeConfigSpec.ConfigValue<Double> WitherSlashDamage;

    public static final ForgeConfigSpec.ConfigValue<Integer> SporeCloudSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> SporeCloudCooldown;

    public static final ForgeConfigSpec.ConfigValue<Integer> ConfusionBoltSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> ConfusionBoltCooldown;

    public static final ForgeConfigSpec.ConfigValue<Integer> RunePrisonSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> RunePrisonCooldown;
    public static final ForgeConfigSpec.ConfigValue<Double> RunePrisonDamage;

    public static final ForgeConfigSpec.ConfigValue<Integer> ThrasherSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> ThrasherCooldown;

    public static final ForgeConfigSpec.ConfigValue<Integer> FlareSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> FlareCooldown;

    public static final ForgeConfigSpec.ConfigValue<Integer> MurmurSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> MurmurCastDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> MurmurSummonDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> MurmurCoolDown;

    public static final ForgeConfigSpec.ConfigValue<Integer> CrimsonMosquitoSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> CrimsonMosquitoCastDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> CrimsonMosquitoSummonDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> CrimsonMosquitoCoolDown;

    public static final ForgeConfigSpec.ConfigValue<Integer> BloodSpraySoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> BloodSprayCastDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> BloodSprayCoolDown;

    public static final ForgeConfigSpec.ConfigValue<Integer> FarseerSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> FarseerCastDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> FarseerSummonDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> FarseerCoolDown;

    static {
        BUILDER.push("Spells");

        BUILDER.push("Murmur");
        MurmurSoulCost = BUILDER.comment("Soul cost of Murmur Servant spell (Default: 16)")
                .defineInRange("murmurSoulCost", 16, 1, 100);
        MurmurCastDuration = BUILDER.comment("Cast duration of Murmur Servant spell in ticks (Default: 120)")
                .defineInRange("murmurCastDuration", 120, 0, 500);
        MurmurSummonDown = BUILDER.comment("Summon down duration of Murmur Servant spell in ticks (Default: 160)")
                .defineInRange("murmurSummonDown", 160, 0, 5000);
        MurmurCoolDown = BUILDER.comment("Cooldown of Murmur Servant spell in ticks (Default: 100)")
                .defineInRange("murmurCoolDown", 100, 0, 5000);
        BUILDER.pop();

        BUILDER.push("BloodSpray");
        BloodSpraySoulCost = BUILDER.comment("Soul cost of BloodSpray spell (Default: 8)")
                .defineInRange("bloodSpraySoulCost", 8, 1, 100);
        BloodSprayCastDuration = BUILDER.comment("Cast duration of BloodSpray spell in ticks (Default: 200)")
                .defineInRange("bloodSprayCastDuration", 200, 0, 500);
        BloodSprayCoolDown = BUILDER.comment("Cooldown of BloodSpray in ticks (Default: 100)")
                .defineInRange("bloodSprayCoolDown", 100, 0, 5000);
        BUILDER.pop();

        BUILDER.push("Crimson Mosquito");
        CrimsonMosquitoSoulCost = BUILDER.comment("Soul cost of Crimson Mosquito Servant spell (Default: 20)")
                .defineInRange("crimsonMosquitoSoulCost", 20, 1, 100);
        CrimsonMosquitoCastDuration = BUILDER.comment("Cast duration of Crimson Mosquito Servant spell in ticks (Default: 120)")
                .defineInRange("crimsonMosquitoCastDuration", 120, 0, 500);
        CrimsonMosquitoSummonDown = BUILDER.comment("Summon down duration of Crimson Mosquito Servant spell in ticks (Default: 200)")
                .defineInRange("crimsonMosquitoSummonDown", 200, 0, 5000);
        CrimsonMosquitoCoolDown = BUILDER.comment("Cooldown of Crimson Mosquito Servant spell in ticks (Default: 160)")
                .defineInRange("crimsonMosquitoCoolDown", 160, 0, 5000);
        BUILDER.pop();

        BUILDER.push("Farseer");
        FarseerSoulCost = BUILDER.comment("Soul cost of Farseer Servant spell (Default: 128)")
                .defineInRange("farseerSoulCost", 128, 1, 128);
        FarseerCastDuration = BUILDER.comment("Cast duration of Farseer Servant spell in ticks (Default: 100)")
                .defineInRange("farseerCastDuration", 100, 0, 500);
        FarseerSummonDown = BUILDER.comment("Summon down duration of Farseer Servant spell in ticks (Default: 200)")
                .defineInRange("farseerSummonDown", 200, 0, 5000);
        FarseerCoolDown = BUILDER.comment("Cooldown of Farseer Servant spell in ticks (Default: 1200)")
                .defineInRange("farseerCoolDown", 1200, 0, 5000);
        BUILDER.pop();

        BUILDER.push("Brain Eater");
        BrainEaterDrainPerSecond = BUILDER.comment("Experience drained per second while channeling (Default: 10)")
                .defineInRange("brainEaterDrainPerSecond", 10, 1, 1000);
        BrainEaterSoulsPerDrain = BUILDER.comment("Souls gained per drain (Default: 100)")
                .defineInRange("brainEaterSoulsPerDrain", 100, 1, 100000);
        BUILDER.pop();

        BUILDER.push("Urbhadhach");
        UrbhadhachSoulCost = BUILDER.comment("Soul cost of Urbhadhach Servant spell (Default: 48)")
                .defineInRange("urbhadhachSoulCost", 48, 1, 100);
        UrbhadhachCastDuration = BUILDER.comment("Cast duration of Urbhadhach Servant spell in ticks (Default: 60)")
                .defineInRange("urbhadhachCastDuration", 60, 0, 500);
        UrbhadhachSummonDown = BUILDER.comment("Summon down duration of Urbhadhach Servant spell in ticks (Default: 200)")
                .defineInRange("urbhadhachSummonDown", 200, 0, 5000);
        UrbhadhachCoolDown = BUILDER.comment("Cooldown of Urbhadhach Servant spell in ticks (Default: 500)")
                .defineInRange("urbhadhachCoolDown", 500, 0, 5000);
        BUILDER.pop();

        BUILDER.push("Scorch");
        ScorchSoulCost = BUILDER.comment("Soul cost of Scorch spell (Default: 18)")
                .defineInRange("scorchSoulCost", 18, 1, 100);
        ScorchCastDuration = BUILDER.comment("Cast duration of Scorch spell in ticks (Default: 100)")
                .defineInRange("scorchCastDuration", 100, 0, 500);
        ScorchSummonDown = BUILDER.comment("Summon down duration of Scorch spell in ticks (Default: 340)")
                .defineInRange("scorchSummonDown", 340, 0, 5000);
        ScorchCoolDown = BUILDER.comment("Cooldown of Scorch spell in ticks (Default: 340)")
                .defineInRange("scorchCoolDown", 340, 0, 5000);
        BUILDER.pop();

        BUILDER.push("Brood");
        BroodSoulCost = BUILDER.comment("Soul cost of Brood spell (Default: 48)")
                .defineInRange("broodSoulCost", 48, 1, 100);
        BroodSummonDown = BUILDER.comment("Summon down duration of Brood spell in ticks (Default: 300)")
                .defineInRange("broodSummonDown", 300, 0, 5000);
        BroodCoolDown = BUILDER.comment("Cooldown of Brood spell in ticks (Default: 1000)")
                .defineInRange("broodCoolDown", 1000, 0, 5000);
        BUILDER.pop();

        BUILDER.push("Spider");
        SpiderCost = BUILDER.comment("Soul cost of Spider spell (Default: 8)")
                .defineInRange("spiderCost", 8, 1, 100);
        SpiderDuration = BUILDER.comment("Cast duration of Spider spell in ticks (Default: 20)")
                .defineInRange("spiderDuration", 20, 0, 500);
        SpiderCoolDown = BUILDER.comment("Cooldown of Spider spell in ticks (Default: 100)")
                .defineInRange("spiderCoolDown", 100, 0, 5000);
        SpiderSummonDown = BUILDER.comment("Summon down duration of Spider spell in ticks (Default: 120)")
                .defineInRange("spiderSummonDown", 120, 0, 5000);
        BUILDER.pop();

        BUILDER.push("Redstone Cube");
        RedstoneCost = BUILDER.comment("Soul cost of Redstone Cube spell (Default: 10)")
                .defineInRange("redstoneCost", 10, 1, 100);
        RedstoneDuration = BUILDER.comment("Cast duration of Redstone Cube spell in ticks (Default: 20)")
                .defineInRange("redstoneDuration", 20, 0, 500);
        RedstoneCoolDown = BUILDER.comment("Cooldown of Redstone Cube spell in ticks (Default: 200)")
                .defineInRange("redstoneCoolDown", 200, 0, 5000);
        RedstoneSummonDown = BUILDER.comment("Summon down duration of Redstone Cube spell in ticks (Default: 100)")
                .defineInRange("redstoneSummonDown", 100, 0, 5000);
        RedstoneCubeLimit = BUILDER.comment("Max number of Redstone Cubes summoned (Default: 16)")
                .defineInRange("redstoneCubeLimit", 16, 1, 100);
        BUILDER.pop();

        BUILDER.push("Hog Charge");
        HogChargeSoulCost = BUILDER.comment("Soul cost of Hog Charge spell (Default: 4)")
                .defineInRange("hogChargeSoulCost", 4, 1, 100);
        HogChargeCooldown = BUILDER.comment("Cooldown of Hog Charge spell in ticks (Default: 60)")
                .defineInRange("hogChargeCooldown", 60, 0, 2000);
        HogChargeDamage = BUILDER.comment("Base damage of Hog Charge (Default: 4.0)")
                .defineInRange("hogChargeDamage", 4.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();

        BUILDER.push("Wither Breath");
        WitherBreathSoulCost = BUILDER.comment("Soul cost of Wither Breath spell (Default: 24)")
                .defineInRange("witherBreathSoulCost", 24, 1, 100);
        WitherBreathCooldown = BUILDER.comment("Cooldown of Wither Breath spell in ticks (Default: 400)")
                .defineInRange("witherBreathCooldown", 400, 0, 2000);
        BUILDER.pop();

        BUILDER.push("Wither Slash");
        WitherSlashSoulCost = BUILDER.comment("Soul cost of Wither Slash spell (Default: 24)")
                .defineInRange("witherSlashSoulCost", 24, 1, 100);
        WitherSlashCooldown = BUILDER.comment("Cooldown of Wither Slash spell in ticks (Default: 60)")
                .defineInRange("witherSlashCooldown", 60, 0, 2000);
        WitherSlashDamage = BUILDER.comment("Base damage of Wither Slash (Default: 6.0)")
                .defineInRange("witherSlashDamage", 6.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();

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

        BUILDER.push("Thrasher");
        ThrasherSoulCost = BUILDER.comment("Soul cost of Thrasher summon spell (Default: 64)")
                .defineInRange("thrasherSoulCost", 64, 1, 100);
        ThrasherCooldown = BUILDER.comment("Cooldown of Thrasher summon spell in ticks (Default: 200)")
                .defineInRange("thrasherCooldown", 200, 0, 2000);
        BUILDER.pop();

        BUILDER.push("Flare");
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
