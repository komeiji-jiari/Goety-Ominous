package com.qiuyue.goetyominous.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.File;

public class SpellConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> AcidPoolSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> AcidPoolCastDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> AcidPoolCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Double> AcidPoolBaseDamage;
    public static final ForgeConfigSpec.ConfigValue<Integer> AcidPoolBaseDuration;
    public static final ForgeConfigSpec.ConfigValue<Double> AcidPoolRadius;
    public static final ForgeConfigSpec.ConfigValue<Integer> BrainEaterDrainPerSecond;
    public static final ForgeConfigSpec.ConfigValue<Integer> BrainEaterSoulsPerDrain;
    public static final ForgeConfigSpec.ConfigValue<Integer> PoisonBallCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> PoisonBallCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Double> PoisonBallDamage;
    public static final ForgeConfigSpec.ConfigValue<Integer> UrbhadhachSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> UrbhadhachCastDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> UrbhadhachSummonDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> UrbhadhachCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> DredenSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> DredenCastDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> DredenSummonDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> DredenCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> BreezeCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> BreezeDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> BreezeSummonDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> BreezeCoolDown;
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
    public static final ForgeConfigSpec.ConfigValue<Integer> HauntSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> HauntCastDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> HauntCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> HauntSummonDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> HauntLimit;

    static {
        BUILDER.push("Spells");
        BUILDER.push("Acid Pool");
        AcidPoolSoulCost = BUILDER.comment("Soul cost of Acid Pool spell (Default: 24)")
                .defineInRange("acidPoolSoulCost", 24, 1, 100);
        AcidPoolCastDuration = BUILDER.comment("Cast duration of Acid Pool spell in ticks (Default: 60)")
                .defineInRange("acidPoolCastDuration", 60, 0, 500);
        AcidPoolCoolDown = BUILDER.comment("Cooldown of Acid Pool spell in ticks (Default: 200)")
                .defineInRange("acidPoolCoolDown", 200, 0, 5000);
        AcidPoolBaseDamage = BUILDER.comment("Base damage of Acid Pool per hit (Default: 5.0)")
                .defineInRange("acidPoolBaseDamage", 5.0D, 0.0D, 1000.0D);
        AcidPoolBaseDuration = BUILDER.comment("Base lifespan of Acid Pool in ticks (Default: 80)")
                .defineInRange("acidPoolBaseDuration", 80, 1, 20000);
        AcidPoolRadius = BUILDER.comment("Radius of Acid Pool (Default: 1.0)")
                .defineInRange("acidPoolRadius", 1.0D, 0.5D, 16.0D);
        BUILDER.pop();
        BUILDER.push("Brain Eater");
        BrainEaterDrainPerSecond = BUILDER.comment("Experience drained per second while channeling (Default: 10)")
                .defineInRange("brainEaterDrainPerSecond", 10, 1, 1000);
        BrainEaterSoulsPerDrain = BUILDER.comment("Souls gained per drain (Default: 100)")
                .defineInRange("brainEaterSoulsPerDrain", 100, 1, 100000);
        BUILDER.pop();
        BUILDER.push("Poison Ball");
        PoisonBallCost = BUILDER.comment("Soul cost of Poison Ball spell (Default: 8)")
                .defineInRange("poisonBallCost", 8, 1, 100);
        PoisonBallCoolDown = BUILDER.comment("Cooldown of Poison Ball (Default: 20)")
                .defineInRange("poisonBallCoolDown", 20, 0, 5000);
        PoisonBallDamage = BUILDER.comment("Base damage of Poison Ball (Default: 5.0)")
                .defineInRange("poisonBallDamage", 5.0, 1.0, 1000.0);
        BUILDER.pop();
        BUILDER.push("Urbhadhach");
        UrbhadhachSoulCost = BUILDER.comment("Soul cost of Urbhadhach Servant spell (Default: 48)")
                .defineInRange("urbhadhachSoulCost", 48, 1, 100);
        UrbhadhachCastDuration = BUILDER.comment("Cast duration of Urbhadhach Servant spell in ticks (Default: 60)")
                .defineInRange("urbhadhachCastDuration", 60, 0, 500);
        UrbhadhachSummonDown = BUILDER.comment("Summon down duration of Urbhadhach Servant spell in ticks (Default: 200)")
                .defineInRange("urbhadhachSummonDown", 200, 0, 5000);
        UrbhadhachCoolDown = BUILDER.comment("Cooldown of Urbhadhach Servant spell in ticks (Default: 300)")
                .defineInRange("urbhadhachCoolDown", 300, 0, 5000);
        BUILDER.pop();
        BUILDER.push("Dreden");
        DredenSoulCost = BUILDER.comment("Soul cost of Dreden Servant spell (Default: 24)")
                .defineInRange("dredenSoulCost", 24, 1, 100);
        DredenCastDuration = BUILDER.comment("Cast duration of Dreden Servant spell in ticks (Default: 80)")
                .defineInRange("dredenCastDuration", 80, 0, 500);
        DredenSummonDown = BUILDER.comment("Summon down duration of Dreden Servant spell in ticks (Default: 300)")
                .defineInRange("dredenSummonDown", 300, 0, 5000);
        DredenCoolDown = BUILDER.comment("Cooldown of Dreden Servant spell in ticks (Default: 120)")
                .defineInRange("dredenCoolDown", 120, 0, 5000);
        BUILDER.pop();
        BUILDER.push("Breeze");
        BreezeCost = BUILDER.comment("Soul cost of Breeze Servant spell (Default: 16)")
                .defineInRange("breezeSoulCost", 16, 1, 100);
        BreezeDuration = BUILDER.comment("Cast duration of Breeze Servant spell in ticks (Default: 100)")
                .defineInRange("breezeCastDuration", 100, 0, 500);
        BreezeSummonDown = BUILDER.comment("Summon down duration of Breeze Servant spell in ticks (Default: 400)")
                .defineInRange("breezeSummonDown", 400, 0, 5000);
        BreezeCoolDown = BUILDER.comment("Cooldown of Breeze Servant spell in ticks (Default: 200)")
                .defineInRange("breezeCoolDown", 200, 0, 5000);
        BUILDER.pop();
        BUILDER.push("Scorch");
        ScorchSoulCost = BUILDER.comment("Soul cost of Scorch spell (Default: 18)")
                .defineInRange("scorchSoulCost", 18, 1, 100);
        ScorchCastDuration = BUILDER.comment("Cast duration of Scorch spell in ticks (Default: 40)")
                .defineInRange("scorchCastDuration", 40, 0, 500);
        ScorchSummonDown = BUILDER.comment("Summon down duration of Scorch spell in ticks (Default: 80)")
                .defineInRange("scorchSummonDown", 80, 0, 5000);
        ScorchCoolDown = BUILDER.comment("Cooldown of Scorch spell in ticks (Default: 100)")
                .defineInRange("scorchCoolDown", 100, 0, 5000);
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
        SpiderSummonDown = BUILDER.comment("Summon down duration of Spider spell in ticks (Default: 80)")
                .defineInRange("spiderSummonDown", 80, 0, 5000);
        BUILDER.pop();
        BUILDER.push("Redstone Cube");
        RedstoneCost = BUILDER.comment("Soul cost of Redstone Cube spell (Default: 10)")
                .defineInRange("redstoneCost", 10, 1, 100);
        RedstoneDuration = BUILDER.comment("Cast duration of Redstone Cube spell in ticks (Default: 20)")
                .defineInRange("redstoneDuration", 20, 0, 500);
        RedstoneCoolDown = BUILDER.comment("Cooldown of Redstone Cube spell in ticks (Default: 100)")
                .defineInRange("redstoneCoolDown", 100, 0, 5000);
        RedstoneSummonDown = BUILDER.comment("Summon down duration of Redstone Cube spell in ticks (Default: 80)")
                .defineInRange("redstoneSummonDown", 80, 0, 5000);
        RedstoneCubeLimit = BUILDER.comment("Max number of Redstone Cubes summoned (Default: 16)")
                .defineInRange("redstoneCubeLimit", 16, 1, 100);
        BUILDER.pop();
        BUILDER.push("Haunt");
        HauntSoulCost = BUILDER.comment("Soul cost of Haunt spell (Default: 6)")
                .defineInRange("hauntCost", 6, 1, 100);
        HauntCastDuration = BUILDER.comment("Cast duration of Haunt spell in ticks (Default: 20)")
                .defineInRange("hauntDuration", 20, 0, 500);
        HauntCoolDown = BUILDER.comment("Cooldown of Haunt spell in ticks (Default: 60)")
                .defineInRange("hauntCoolDown", 60, 0, 5000);
        HauntSummonDown = BUILDER.comment("Summon down duration of Haunt spell in ticks (Default: 60)")
                .defineInRange("hauntSummonDown", 60, 0, 5000);
        HauntLimit = BUILDER.comment("Max number of Haunt summoned (Default: 32)")
                .defineInRange("hauntLimit", 32, 1, 100);
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
