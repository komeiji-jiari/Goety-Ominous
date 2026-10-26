package com.qiuyue.goetyominous.config.of;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.File;

public class OfConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Double> RamblerServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> RamblerServantAttackDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> VoltServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> DicerServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> DicerServantAttackDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> TremblerServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> TremblerServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> TremblerServantAttackDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> FireSlimeServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> FireSlimeServantAttackDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> GuzzlerServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> GuzzlerServantAttackDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> SkyvernServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> SkyvernServantHealthPerSegment;
    public static final ForgeConfigSpec.ConfigValue<Double> SkyvernServantAttackDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> TerrorServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> TerrorServantAttackDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> FrowzyServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> FrowzyServantAttackDamage;
    public static final ForgeConfigSpec.ConfigValue<Integer> RamblerServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> DicerServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> TremblerServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> TerrorServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> VoltServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> FireSlimeServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> FrowzyServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> GuzzlerServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> UmberSpiderServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> SkyvernServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Boolean> SkyvernServantGhost;
    public static final ForgeConfigSpec.ConfigValue<Integer> FrowzySoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> FrowzyCastDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> FrowzySummonDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> FrowzyCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> VoltSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> VoltCastDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> VoltSummonDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> VoltCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> VoltBoltCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> VoltBoltCastDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> VoltBoltCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> UmberSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> UmberCastDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> UmberSummonDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> UmberCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> InfernoSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> InfernoCastDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> InfernoCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> DicerLaserCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> DicerLaserCastDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> DicerLaserCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> DicerSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> DicerCastDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> DicerSummonDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> DicerCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> TerrorSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> TerrorCastDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> TerrorSummonDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> TerrorCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> SkyvernSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> SkyvernCastDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> SkyvernSummonDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> SkyvernCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> TremblerSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> TremblerCastDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> TremblerSummonDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> TremblerCoolDown;

    static {
        BUILDER.push("Servants Attributes");
        BUILDER.push("Rambler Servant");
        RamblerServantHealth = BUILDER.comment("How much Max Health Rambler Servants have, Default: 60.0")
                .defineInRange("ramblerServantHealth", 60.0, 1.0, Double.MAX_VALUE);
        RamblerServantAttackDamage = BUILDER.comment("How much Attack Damage Rambler Servants have, Default: 5.0")
                .defineInRange("ramblerServantAttackDamage", 5.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Volt Servant");
        VoltServantHealth = BUILDER.comment("How much Max Health Volt Servants have, Default: 16.0 (matches Opposing Force Volt)")
                .defineInRange("voltServantHealth", 16.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Dicer Servant");
        DicerServantHealth = BUILDER.comment("How much Max Health Dicer Servants have, Default: 36.0")
                .defineInRange("dicerServantHealth", 36.0, 1.0, Double.MAX_VALUE);
        DicerServantAttackDamage = BUILDER.comment("How much Attack Damage Dicer Servants have, Default: 8.0")
                .defineInRange("dicerServantAttackDamage", 8.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Trembler Servant");
        TremblerServantHealth = BUILDER.comment("How much Max Health Trembler Servants have, Default: 16.0")
                .defineInRange("tremblerServantHealth", 16.0, 1.0, Double.MAX_VALUE);
        TremblerServantArmor = BUILDER.comment("How much Armor Trembler Servants have, Default: 20.0")
                .defineInRange("tremblerServantArmor", 20.0, 0.0, Double.MAX_VALUE);
        TremblerServantAttackDamage = BUILDER.comment("How much Attack Damage Trembler Servants have, Default: 5.0")
                .defineInRange("tremblerServantAttackDamage", 5.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Fire Slime Servant");
        FireSlimeServantHealth = BUILDER.comment("How much Max Health Fire Slime Servants have, Default: 8.0")
                .defineInRange("fireSlimeServantHealth", 8.0, 1.0, Double.MAX_VALUE);
        FireSlimeServantAttackDamage = BUILDER.comment("How much Attack Damage Fire Slime Servants have, Default: 3.0")
                .defineInRange("fireSlimeServantAttackDamage", 3.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Guzzler Servant");
        GuzzlerServantHealth = BUILDER.comment("How much Max Health Guzzler Servants have, Default: 100.0")
                .defineInRange("guzzlerServantHealth", 100.0, 1.0, Double.MAX_VALUE);
        GuzzlerServantAttackDamage = BUILDER.comment("How much Attack Damage Guzzler Servants have, Default: 5.0")
                .defineInRange("guzzlerServantAttackDamage", 5.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Skyvern Servant");
        SkyvernServantHealth = BUILDER.comment("Base Max Health of Skyvern Servants before the segment bonus, Default: 10.0")
                .defineInRange("skyvernServantHealth", 10.0, 1.0, Double.MAX_VALUE);
        SkyvernServantHealthPerSegment = BUILDER.comment("Extra Max Health per body segment. Total = base + this x segment count, Default: 6.0")
                .defineInRange("skyvernServantHealthPerSegment", 6.0, 0.0, Double.MAX_VALUE);
        SkyvernServantAttackDamage = BUILDER.comment("How much Attack Damage Skyvern Servants have, Default: 9.0")
                .defineInRange("skyvernServantAttackDamage", 9.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Terror Servant");
        TerrorServantHealth = BUILDER.comment("How much Max Health Terror Servants have, Default: 36.0")
                .defineInRange("terrorServantHealth", 36.0, 1.0, Double.MAX_VALUE);
        TerrorServantAttackDamage = BUILDER.comment("How much Attack Damage Terror Servants have, Default: 4.0")
                .defineInRange("terrorServantAttackDamage", 4.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Frowzy Servant");
        FrowzyServantHealth = BUILDER.comment("How much Max Health Frowzy Servants have, Default: 16.0")
                .defineInRange("frowzyServantHealth", 16.0, 1.0, Double.MAX_VALUE);
        FrowzyServantAttackDamage = BUILDER.comment("How much Attack Damage Frowzy Servants have, Default: 2.0")
                .defineInRange("frowzyServantAttackDamage", 2.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.pop();
        BUILDER.push("Servants Behavior");
        BUILDER.push("Skyvern Servant");
        SkyvernServantGhost = BUILDER
                .comment("Whether Skyvern Servants summoned while the owner wears a Wind Robe become translucent and lose collision (Default: true)")
                .define("skyvernServantGhost", true);
        BUILDER.pop();
        BUILDER.pop();
        BUILDER.push("Servant Limits");
        RamblerServantLimit = BUILDER
                .comment("Maximum number of Rambler Servants that can be summoned (Default: 8)")
                .defineInRange("ramblerServantLimit", 8, 1, 100);
        DicerServantLimit = BUILDER
                .comment("Maximum number of Dicer Servants that can be summoned (Default: 16)")
                .defineInRange("dicerServantLimit", 16, 1, 100);
        TremblerServantLimit = BUILDER
                .comment("Maximum number of Trembler Servants that can be summoned (Default: 16)")
                .defineInRange("tremblerServantLimit", 16, 1, 100);
        TerrorServantLimit = BUILDER
                .comment("Maximum number of Terror Servants that can be summoned (Default: 16)")
                .defineInRange("terrorServantLimit", 16, 1, 100);
        VoltServantLimit = BUILDER
                .comment("Maximum number of Volt Servants that can be summoned (Default: 16)")
                .defineInRange("voltServantLimit", 16, 1, 100);
        FireSlimeServantLimit = BUILDER
                .comment("Maximum number of Fire Slime Servants that can be summoned (Default: 2147483647 = no limit)")
                .defineInRange("fireSlimeServantLimit", Integer.MAX_VALUE, 1, Integer.MAX_VALUE);
        FrowzyServantLimit = BUILDER
                .comment("Maximum number of Frowzy Servants that can be summoned (Default: 16)")
                .defineInRange("frowzyServantLimit", 16, 1, 100);
        GuzzlerServantLimit = BUILDER
                .comment("Maximum number of Guzzler Servants that can be summoned (Default: 2)")
                .defineInRange("guzzlerServantLimit", 2, 1, 100);
        UmberSpiderServantLimit = BUILDER
                .comment("Maximum number of Umber Spider Servants that can be summoned (Default: 32)")
                .defineInRange("umberSpiderServantLimit", 32, 1, 100);
        SkyvernServantLimit = BUILDER
                .comment("Maximum number of Skyvern Servants that can be summoned (Default: 3)")
                .defineInRange("skyvernServantLimit", 3, 1, 100);
        BUILDER.pop();
        BUILDER.push("Spells");
        BUILDER.push("Volt Spell");
        VoltSoulCost = BUILDER.comment("Soul cost of Volt Servant spell (Default: 32)")
                .defineInRange("voltSoulCost", 32, 1, 128);
        VoltCastDuration = BUILDER.comment("Cast duration of Volt Servant spell in ticks (Default: 100)")
                .defineInRange("voltCastDuration", 100, 0, 500);
        VoltSummonDown = BUILDER.comment("Summon down duration of Volt Servant spell in ticks (Default: 340)")
                .defineInRange("voltSummonDown", 340, 0, 5000);
        VoltCoolDown = BUILDER.comment("Cooldown of Volt Servant spell in ticks (Default: 900)")
                .defineInRange("voltCoolDown", 900, 0, 5000);
        BUILDER.pop();
        BUILDER.push("Volt Bolt Spell");
        VoltBoltCost = BUILDER.comment("Soul cost of Volt Bolt spell (Default: 12)")
                .defineInRange("voltBoltCost", 12, 1, 100);
        VoltBoltCastDuration = BUILDER.comment("Cast duration of Volt Bolt spell in ticks (Default: 40)")
                .defineInRange("voltBoltCastDuration", 40, 0, 2000);
        VoltBoltCoolDown = BUILDER.comment("Cooldown of Volt Bolt spell in ticks (Default: 60)")
                .defineInRange("voltBoltCoolDown", 60, 0, 2000);
        BUILDER.pop();
        BUILDER.push("Stalker Spell");
        UmberSoulCost = BUILDER.comment("Soul cost of Umber Servant spell (Default: 16)")
                .defineInRange("umberSoulCost", 16, 1, 128);
        UmberCastDuration = BUILDER.comment("Cast duration of Umber Servant spell in ticks (Default: 60)")
                .defineInRange("umberCastDuration", 60, 0, 500);
        UmberSummonDown = BUILDER.comment("Summon down duration of Umber Servant spell in ticks (Default: 120)")
                .defineInRange("umberSummonDown", 120, 0, 5000);
        UmberCoolDown = BUILDER.comment("Cooldown of Umber Servant spell in ticks (Default: 200)")
                .defineInRange("umberCoolDown", 200, 0, 5000);
        BUILDER.pop();
        BUILDER.push("Fiery Spell");
        InfernoSoulCost = BUILDER.comment("Soul cost of Inferno spell (Default: 8)")
                .defineInRange("infernoSoulCost", 8, 1, 128);
        InfernoCastDuration = BUILDER.comment("Cast duration of Inferno spell in ticks (Default: 20)")
                .defineInRange("infernoCastDuration", 20, 0, 500);
        InfernoCoolDown = BUILDER.comment("Cooldown of Inferno spell in ticks (Default: 40)")
                .defineInRange("infernoCoolDown", 40, 0, 5000);
        BUILDER.pop();
        BUILDER.push("Dicer Laser Spell");
        DicerLaserCost = BUILDER.comment("Soul cost of Dicer Laser spell (Default: 64)")
                .defineInRange("dicerLaserCost", 64, 1, 100);
        DicerLaserCastDuration = BUILDER.comment("Cast duration of Dicer Laser spell in ticks (Default: 40)")
                .defineInRange("dicerLaserCastDuration", 40, 0, 2000);
        DicerLaserCoolDown = BUILDER.comment("Cooldown of Dicer Laser spell in ticks (Default: 200)")
                .defineInRange("dicerLaserCoolDown", 200, 0, 2000);
        BUILDER.pop();
        BUILDER.push("Dicer Spell");
        DicerSoulCost = BUILDER.comment("Soul cost of Dicer Servant spell (Default: 32)")
                .defineInRange("dicerSoulCost", 32, 1, 128);
        DicerCastDuration = BUILDER.comment("Cast duration of Dicer Servant spell in ticks (Default: 100)")
                .defineInRange("dicerCastDuration", 100, 0, 500);
        DicerSummonDown = BUILDER.comment("Summon down duration of Dicer Servant spell in ticks (Default: 240)")
                .defineInRange("dicerSummonDown", 240, 0, 5000);
        DicerCoolDown = BUILDER.comment("Cooldown of Dicer Servant spell in ticks (Default: 600)")
                .defineInRange("dicerCoolDown", 600, 0, 5000);
        BUILDER.pop();
        BUILDER.push("Terror Spell");
        TerrorSoulCost = BUILDER.comment("Soul cost of Terror Servant spell (Default: 32)")
                .defineInRange("terrorSoulCost", 32, 1, 128);
        TerrorCastDuration = BUILDER.comment("Cast duration of Terror Servant spell in ticks (Default: 100)")
                .defineInRange("terrorCastDuration", 100, 0, 500);
        TerrorSummonDown = BUILDER.comment("Summon down duration of Terror Servant spell in ticks (Default: 240)")
                .defineInRange("terrorSummonDown", 240, 0, 5000);
        TerrorCoolDown = BUILDER.comment("Cooldown of Terror Servant spell in ticks (Default: 600)")
                .defineInRange("terrorCoolDown", 600, 0, 5000);
        BUILDER.pop();
        BUILDER.push("Skyvern Spell");
        SkyvernSoulCost = BUILDER.comment("Soul cost of Skyvern Servant spell (Default: 100)")
                .defineInRange("skyvernSoulCost", 100, 1, 128);
        SkyvernCastDuration = BUILDER.comment("Cast duration of Skyvern Servant spell in ticks (Default: 120)")
                .defineInRange("skyvernCastDuration", 120, 0, 500);
        SkyvernSummonDown = BUILDER.comment("Summon down duration of Skyvern Servant spell in ticks (Default: 400)")
                .defineInRange("skyvernSummonDown", 400, 0, 5000);
        SkyvernCoolDown = BUILDER.comment("Cooldown of Skyvern Servant spell in ticks (Default: 1200)")
                .defineInRange("skyvernCoolDown", 1200, 0, 5000);
        BUILDER.pop();
        BUILDER.push("Trembler Spell");
        TremblerSoulCost = BUILDER.comment("Soul cost of Trembler Servant spell (Default: 32)")
                .defineInRange("tremblerSoulCost", 32, 1, 128);
        TremblerCastDuration = BUILDER.comment("Cast duration of Trembler Servant spell in ticks (Default: 120)")
                .defineInRange("tremblerCastDuration", 120, 0, 500);
        TremblerSummonDown = BUILDER.comment("Summon down duration of Trembler Servant spell in ticks (Default: 300)")
                .defineInRange("tremblerSummonDown", 300, 0, 5000);
        TremblerCoolDown = BUILDER.comment("Cooldown of Trembler Servant spell in ticks (Default: 600)")
                .defineInRange("tremblerCoolDown", 600, 0, 5000);
        BUILDER.pop();
        BUILDER.push("Frowzy Spell");
        FrowzySoulCost = BUILDER.comment("Soul cost of Frowzy Servant spell (Default: 5)")
                .defineInRange("frowzySoulCost", 5, 1, 128);
        FrowzyCastDuration = BUILDER.comment("Cast duration of Frowzy Servant spell in ticks (Default: 20)")
                .defineInRange("frowzyCastDuration", 20, 0, 500);
        FrowzySummonDown = BUILDER.comment("Summon down duration of Frowzy Servant spell in ticks (Default: 120)")
                .defineInRange("frowzySummonDown", 120, 0, 5000);
        FrowzyCoolDown = BUILDER.comment("Cooldown of Frowzy Servant spell in ticks (Default: 120)")
                .defineInRange("frowzyCoolDown", 120, 0, 5000);
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
