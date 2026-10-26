package com.qiuyue.goetyominous.config.ac;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.File;

public class AcConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Double> NuclearGuardianServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> NuclearGuardianServantBeamDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> NuclearGuardianServantSelfDestructHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> GrottoceratopsServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> GrottoceratopsServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> GrottoceratopsServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> RelicheirusServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> RelicheirusServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> RelicheirusServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> TremorsaurusServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> TremorsaurusServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> TremorsaurusServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> TremorzillaServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> TremorzillaServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> TremorzillaServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> TremorzillaServantBeamDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> AtlatitanServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> AtlatitanServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> LuxtructosaurusServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> LuxtructosaurusServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> LuxtructosaurusServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> LuxtructosaurusServantTargetRange;
    public static final ForgeConfigSpec.ConfigValue<Double> GumWormServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> GumWormServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> GumWormServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> VallumraptorServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> VallumraptorServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> VallumraptorServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> VallumraptorServantElderHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> VallumraptorServantElderArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> NucleeperServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> NucleeperServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> NucleeperServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> NucleeperServantTauntRange;
    public static final ForgeConfigSpec.ConfigValue<Double> HullbreakerServantGlowTargetRange;
    public static final ForgeConfigSpec.ConfigValue<Double> HullbreakerServantGlowChaseSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> HullbreakerServantProximityTargetRange;
    public static final ForgeConfigSpec.ConfigValue<Double> BrainiacServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> BrainiacServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> BrainiacServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> CaniacServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> CaniacServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> CaniacServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> GummyBearServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> GummyBearServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> GummyBearServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> CaramelCubeServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> CaramelCubeServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> CaramelCubeServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> GumbeeperServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> GumbeeperServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> GumbeeperServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> VesperServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> VesperServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> VesperServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> TeletorServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> TeletorServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> TeletorServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> ForsakenServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> ForsakenServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> CandicornServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> CandicornServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> GammaroachServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> GammaroachServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> GammaroachServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> CorrodentServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> CorrodentServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> CorrodentServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> DeepOneServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> DeepOneServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> DeepOneServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> DeepOneKnightServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> DeepOneKnightServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> DeepOneKnightServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> DeepOneKnightServantOrtholanceDashDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> DeepOneKnightServantOrtholanceDashSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> DeepOneKnightServantOrtholanceChance;
    public static final ForgeConfigSpec.ConfigValue<Integer> DeepOneKnightServantOrtholanceWaveCooldown;
    public static final ForgeConfigSpec.ConfigValue<Double> DeepOneKnightServantOrtholanceWaveRange;
    public static final ForgeConfigSpec.ConfigValue<Integer> DeepOneKnightServantOrtholanceWaveCount;
    public static final ForgeConfigSpec.ConfigValue<Double> DeepOneKnightServantOrtholanceWaveScale;
    public static final ForgeConfigSpec.ConfigValue<Double> DeepOneMageServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> DeepOneMageServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> DeepOneMageServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> LicowitchServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> LicowitchServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> LicowitchServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> HullbreakerServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> HullbreakerServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Integer> HullbreakerServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Boolean> HullbreakerServantReturnEmbryo;
    public static final ForgeConfigSpec.ConfigValue<Boolean> HullbreakerServantPickUpDrops;
    public static final ForgeConfigSpec.ConfigValue<Boolean> HullbreakerServantBlockBreakGriefing;
    public static final ForgeConfigSpec.ConfigValue<Integer> GrottoceratopsServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> RelicheirusServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> TremorsaurusServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> TremorzillaServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Boolean> TremorzillaServantBreakBlocks;
    public static final ForgeConfigSpec.ConfigValue<Boolean> TremorzillaServantBreakTrees;
    public static final ForgeConfigSpec.ConfigValue<Integer> AtlatitanServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Boolean> AtlatitanServantBreakBlocks;
    public static final ForgeConfigSpec.ConfigValue<Integer> LuxtructosaurusServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Boolean> LuxtructosaurusServantBreakTrees;
    public static final ForgeConfigSpec.ConfigValue<Boolean> LuxtructosaurusServantFire;
    public static final ForgeConfigSpec.ConfigValue<Boolean> LuxtructosaurusServantWaterToStone;
    public static final ForgeConfigSpec.ConfigValue<Boolean> LuxtructosaurusServantTephra;
    public static final ForgeConfigSpec.ConfigValue<Boolean> LuxtructosaurusServantJumpExplosion;
    public static final ForgeConfigSpec.ConfigValue<Boolean> LuxtructosaurusServantPrimalMagma;
    public static final ForgeConfigSpec.ConfigValue<Boolean> LuxtructosaurusServantExtinctionCatalyst;
    public static final ForgeConfigSpec.ConfigValue<Integer> LuxtructosaurusServantRoarInterval;
    public static final ForgeConfigSpec.ConfigValue<Integer> GumWormServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> VallumraptorServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> VallumraptorElderChance;
    public static final ForgeConfigSpec.ConfigValue<Integer> NucleeperServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> BrainiacServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> CaniacServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> GummyBearServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> CaramelCubeServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> GumbeeperServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> VesperServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> TeletorServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> ForsakenServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> CandicornServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> GammaroachServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> CorrodentServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> DeepOneServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> DeepOneKnightServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> DeepOneMageServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Boolean> LicowitchSummonsLife;
    public static final ForgeConfigSpec.ConfigValue<Integer> LicowitchSummonLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> MineGuardianServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Boolean> MineGuardianServantExplosionGriefing;
    public static final ForgeConfigSpec.ConfigValue<Integer> NuclearGuardianServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Double> ExtinctionBreathDamage;
    public static final ForgeConfigSpec.ConfigValue<Integer> ExtinctionBreathSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> ExtinctionBreathChargeUp;
    public static final ForgeConfigSpec.ConfigValue<Integer> ExtinctionBreathDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> ExtinctionBreathCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> ExtinctionMeteorCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> ExtinctionMeteorChargeUp;
    public static final ForgeConfigSpec.ConfigValue<Integer> ExtinctionMeteorInterval;
    public static final ForgeConfigSpec.ConfigValue<Integer> ExtinctionMeteorShots;
    public static final ForgeConfigSpec.ConfigValue<Integer> ExtinctionMeteorCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> PrimalPackCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> PrimalPackDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> PrimalPackSummonDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> PrimalPackCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> TremorSpiritCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> TremorSpiritTime;
    public static final ForgeConfigSpec.ConfigValue<Integer> TremorSpiritCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> TremorSpiritBuffSeconds;
    public static final ForgeConfigSpec.ConfigValue<Integer> TremorSpiritSpiritCooldown;
    public static final ForgeConfigSpec.ConfigValue<Double> TremorSpiritDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> TremorSpiritDamagePerPotency;
    public static final ForgeConfigSpec.ConfigValue<Double> TremorSpiritStaffBonus;
    public static final ForgeConfigSpec.ConfigValue<Integer> GrottoSpiritCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> GrottoSpiritCastTime;
    public static final ForgeConfigSpec.ConfigValue<Integer> GrottoSpiritCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> GrottoSpiritTime;
    public static final ForgeConfigSpec.ConfigValue<Integer> DeepOneSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> DeepOneCastDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> DeepOneSummonDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> DeepOneCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> DeepKnightSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> DeepKnightCastDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> DeepKnightSummonDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> DeepKnightCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> DeepMageSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> DeepMageCastDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> DeepMageSummonDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> DeepMageCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> WaterBoltCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> WaterBoltCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> DeepWaveCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> DeepWaveCastDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> DeepWaveCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> TideBashCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> TideBashCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> AtomicBombCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> AtomicBombCastDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> AtomicBombCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> AtomicBombRange;
    public static final ForgeConfigSpec.ConfigValue<Integer> BrainiacCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> BrainiacDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> BrainiacSummonDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> BrainiacCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> TremorzillaBreathCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> TremorzillaBreathCastDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> TremorzillaBreathCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Double> TremorzillaBreathDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> TremorzillaBreathRange;
    public static final ForgeConfigSpec.ConfigValue<Integer> TremorzillaBreathDuration;
    public static final ForgeConfigSpec.ConfigValue<Boolean> TremorzillaBreathBreakBlocks;
    public static final ForgeConfigSpec.ConfigValue<Integer> XRaySoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> XRayCastDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> XRayRange;
    public static final ForgeConfigSpec.ConfigValue<Integer> XRayCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Double> BarbaricAxeDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> BarbaricAxeAttackSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> BarbaricAxeSweepDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> BarbaricAxeSweepRange;
    public static final ForgeConfigSpec.ConfigValue<Double> BarbaricAxeSlamDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> BarbaricAxeSlamRadius;
    public static final ForgeConfigSpec.ConfigValue<Integer> BarbaricAxeIgniteSeconds;
    public static final ForgeConfigSpec.ConfigValue<Integer> BarbaricAxeChainLimit;

    static {
        BUILDER.push("Servants Attributes");
        BUILDER.push("Nuclear Guardian Servant");
        NuclearGuardianServantHealth = BUILDER.comment("How much Max Health Nuclear Guardian Servants have, Default: 100.0")
                .defineInRange("nuclearGuardianServantHealth", 100.0, 1.0, Double.MAX_VALUE);
        NuclearGuardianServantBeamDamage = BUILDER.comment("How much damage the Nuclear Guardian Servant beam deals per hit, Default: 1.5")
                .defineInRange("nuclearGuardianServantBeamDamage", 1.5, 0.0, Double.MAX_VALUE);
        NuclearGuardianServantSelfDestructHealth = BUILDER.comment("Health ratio at or below which Nuclear Guardian Servants stop beaming and charge to self destruct, Default: 0.2")
                .defineInRange("nuclearGuardianServantSelfDestructHealth", 0.2, 0.0, 1.0);
        BUILDER.pop();
        BUILDER.push("Grottoceratops Servant");
        GrottoceratopsServantHealth = BUILDER.comment("How much Max Health Grottoceratops Servants have, Default: 50.0 (matches Alex's Caves Grottoceratops)")
                .defineInRange("grottoceratopsServantHealth", 50.0, 1.0, Double.MAX_VALUE);
        GrottoceratopsServantDamage = BUILDER.comment("How much damage Grottoceratops Servants deal, Default: 10.0 (matches Alex's Caves Grottoceratops)")
                .defineInRange("grottoceratopsServantDamage", 10.0, 1.0, Double.MAX_VALUE);
        GrottoceratopsServantArmor = BUILDER.comment("How much natural Armor Grottoceratops Servants have, Default: 8.0 (matches Alex's Caves Grottoceratops)")
                .defineInRange("grottoceratopsServantArmor", 8.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Relicheirus Servant");
        RelicheirusServantHealth = BUILDER.comment("How much Max Health Relicheirus Servants have, Default: 120.0 (matches Alex's Caves Relicheirus)")
                .defineInRange("relicheirusServantHealth", 120.0, 1.0, Double.MAX_VALUE);
        RelicheirusServantDamage = BUILDER.comment("How much damage Relicheirus Servants deal, Default: 12.0 (matches Alex's Caves Relicheirus)")
                .defineInRange("relicheirusServantDamage", 12.0, 1.0, Double.MAX_VALUE);
        RelicheirusServantArmor = BUILDER.comment("How much natural Armor Relicheirus Servants have, Default: 0.0 (matches Alex's Caves Relicheirus)")
                .defineInRange("relicheirusServantArmor", 0.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Tremorsaurus Servant");
        TremorsaurusServantHealth = BUILDER.comment("How much Max Health Tremorsaurus Servants have, Default: 150.0 (matches Alex's Caves Tremorsaurus)")
                .defineInRange("tremorsaurusServantHealth", 150.0, 1.0, Double.MAX_VALUE);
        TremorsaurusServantDamage = BUILDER.comment("How much damage Tremorsaurus Servants deal, Default: 14.0 (matches Alex's Caves Tremorsaurus)")
                .defineInRange("tremorsaurusServantDamage", 14.0, 1.0, Double.MAX_VALUE);
        TremorsaurusServantArmor = BUILDER.comment("How much natural Armor Tremorsaurus Servants have, Default: 8.0 (matches Alex's Caves Tremorsaurus)")
                .defineInRange("tremorsaurusServantArmor", 8.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Tremorzilla Servant");
        TremorzillaServantHealth = BUILDER.comment("How much Max Health Tremorzilla Servants have, Default: 500.0 (matches Alex's Caves Tremorzilla)")
                .defineInRange("tremorzillaServantHealth", 500.0, 1.0, Double.MAX_VALUE);
        TremorzillaServantDamage = BUILDER.comment("How much damage Tremorzilla Servants' scratch, bite, stomp and tail whip deal, Default: 10.0 (matches Alex's Caves Tremorzilla)")
                .defineInRange("tremorzillaServantDamage", 10.0, 1.0, Double.MAX_VALUE);
        TremorzillaServantArmor = BUILDER.comment("How much natural Armor Tremorzilla Servants have, Default: 10.0 (matches Alex's Caves Tremorzilla)")
                .defineInRange("tremorzillaServantArmor", 10.0, 0.0, Double.MAX_VALUE);
        TremorzillaServantBeamDamage = BUILDER.comment("How much damage each hit of a Tremorzilla Servant's nuclear beam deals, Default: 20.0 (matches Alex's Caves Tremorzilla)")
                .defineInRange("tremorzillaServantBeamDamage", 20.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Atlatitan Servant");
        AtlatitanServantHealth = BUILDER.comment("How much Max Health Atlatitan Servants have, Default: 400.0 (matches Alex's Caves Atlatitan)")
                .defineInRange("atlatitanServantHealth", 400.0, 1.0, Double.MAX_VALUE);
        AtlatitanServantDamage = BUILDER.comment("How much damage Atlatitan Servants' kick and tail whip deal, Default: 8.0 (matches Alex's Caves Atlatitan)")
                .defineInRange("atlatitanServantDamage", 8.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Luxtructosaurus Servant");
        LuxtructosaurusServantHealth = BUILDER.comment("How much Max Health Luxtructosaurus Servants have, Default: 600.0 (matches Alex's Caves Luxtructosaurus)")
                .defineInRange("luxtructosaurusServantHealth", 600.0, 1.0, Double.MAX_VALUE);
        LuxtructosaurusServantDamage = BUILDER.comment("How much damage Luxtructosaurus Servants' stomp, kick, tail whip and flames deal, Default: 12.0 (matches Alex's Caves Luxtructosaurus)")
                .defineInRange("luxtructosaurusServantDamage", 12.0, 1.0, Double.MAX_VALUE);
        LuxtructosaurusServantArmor = BUILDER.comment("How much natural Armor Luxtructosaurus Servants have, Default: 20.0 (matches Alex's Caves Luxtructosaurus)")
                .defineInRange("luxtructosaurusServantArmor", 20.0, 0.0, Double.MAX_VALUE);
        LuxtructosaurusServantTargetRange = BUILDER.comment("How far (in blocks) Luxtructosaurus Servants look for their own targets, Default: 32.0 (their follow range of 256 is only used for following)")
                .defineInRange("luxtructosaurusServantTargetRange", 32.0, 0.0, Double.MAX_VALUE);
        LuxtructosaurusServantRoarInterval = BUILDER
                .comment("How many seconds an enraged Luxtructosaurus Servant may go at most without roaring and calling down another tephra bombardment. The timer only runs while enraged, and never while a player is riding it. Set to 0 to disable (Default: 30)")
                .defineInRange("luxtructosaurusServantRoarInterval", 30, 0, 3600);
        BUILDER.pop();
        BUILDER.push("Gum Worm Servant");
        GumWormServantHealth = BUILDER.comment("How much Max Health Gum Worm Servants have, Default: 150.0 (matches Alex's Caves Gum Worm)")
                .defineInRange("gumWormServantHealth", 150.0, 1.0, Double.MAX_VALUE);
        GumWormServantDamage = BUILDER.comment("How much damage Gum Worm Servants' bite deals, Default: 9.0 (matches Alex's Caves Gum Worm)")
                .defineInRange("gumWormServantDamage", 9.0, 1.0, Double.MAX_VALUE);
        GumWormServantArmor = BUILDER.comment("How much natural Armor Gum Worm Servants have, Default: 10.0 (matches Alex's Caves Gum Worm)")
                .defineInRange("gumWormServantArmor", 10.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Vallumraptor Servant");
        VallumraptorServantHealth = BUILDER.comment("How much Max Health Vallumraptor Servants have, Default: 28.0 (matches Alex's Caves Vallumraptor)")
                .defineInRange("vallumraptorServantHealth", 28.0, 1.0, Double.MAX_VALUE);
        VallumraptorServantDamage = BUILDER.comment("How much damage Vallumraptor Servants deal, Default: 3.0 (matches Alex's Caves Vallumraptor)")
                .defineInRange("vallumraptorServantDamage", 3.0, 1.0, Double.MAX_VALUE);
        VallumraptorServantArmor = BUILDER.comment("How much natural Armor Vallumraptor Servants have, Default: 0.0 (matches Alex's Caves Vallumraptor)")
                .defineInRange("vallumraptorServantArmor", 0.0, 0.0, Double.MAX_VALUE);
        VallumraptorServantElderHealth = BUILDER.comment("How much Max Health Vallumraptor Servant Elders have, Default: 32.0 (matches Alex's Caves Vallumraptor elder)")
                .defineInRange("vallumraptorServantElderHealth", 32.0, 1.0, Double.MAX_VALUE);
        VallumraptorServantElderArmor = BUILDER.comment("How much natural Armor Vallumraptor Servant Elders have, Default: 5.0 (matches Alex's Caves Vallumraptor elder)")
                .defineInRange("vallumraptorServantElderArmor", 5.0, 0.0, Double.MAX_VALUE);
        VallumraptorElderChance = BUILDER
                .comment("Chance (percent) for a Vallumraptor Servant to spawn as an Elder/leader variant with boosted health and armor (Default: 15)")
                .defineInRange("vallumraptorServantElderChance", 15, 0, 100);
        BUILDER.pop();
        BUILDER.push("Nucleeper Servant");
        NucleeperServantHealth = BUILDER.comment("How much Max Health Nucleeper Servants have, Default: 40.0 (matches Alex's Caves Nucleeper)")
                .defineInRange("nucleeperServantHealth", 40.0, 1.0, Double.MAX_VALUE);
        NucleeperServantDamage = BUILDER.comment("How much damage Nucleeper Servants deal, Default: 2.0 (vanilla monster default; Nucleeper's real threat is its explosion)")
                .defineInRange("nucleeperServantDamage", 2.0, 1.0, Double.MAX_VALUE);
        NucleeperServantArmor = BUILDER.comment("How much natural Armor Nucleeper Servants have, Default: 4.0 (matches Alex's Caves Nucleeper)")
                .defineInRange("nucleeperServantArmor", 4.0, 0.0, Double.MAX_VALUE);
        NucleeperServantTauntRange = BUILDER.comment("How far (in blocks) Nucleeper Servants taunt mobs that are attacking their owner, pulling the mob's aggro onto the servant. Default: 12.0 (set to 0.0 to disable taunting)")
                .defineInRange("nucleeperServantTauntRange", 12.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Brainiac Servant");
        BrainiacServantHealth = BUILDER.comment("How much Max Health Brainiac Servants have, Default: 40.0 (matches Alex's Caves Brainiac)")
                .defineInRange("brainiacServantHealth", 40.0, 1.0, Double.MAX_VALUE);
        BrainiacServantDamage = BUILDER.comment("How much damage Brainiac Servants deal, Default: 5.0 (matches Alex's Caves Brainiac)")
                .defineInRange("brainiacServantDamage", 5.0, 1.0, Double.MAX_VALUE);
        BrainiacServantArmor = BUILDER.comment("How much natural Armor Brainiac Servants have, Default: 8.0 (matches Alex's Caves Brainiac)")
                .defineInRange("brainiacServantArmor", 8.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Caniac Servant");
        CaniacServantHealth = BUILDER.comment("How much Max Health Caniac Servants have, Default: 38.0 (matches Alex's Caves Caniac)")
                .defineInRange("caniacServantHealth", 38.0, 1.0, Double.MAX_VALUE);
        CaniacServantDamage = BUILDER.comment("How much damage Caniac Servants deal, Default: 2.0 (matches Alex's Caves Caniac)")
                .defineInRange("caniacServantDamage", 2.0, 1.0, Double.MAX_VALUE);
        CaniacServantArmor = BUILDER.comment("How much natural Armor Caniac Servants have, Default: 0.0 (matches Alex's Caves Caniac)")
                .defineInRange("caniacServantArmor", 0.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Gummy Bear Servant");
        GummyBearServantHealth = BUILDER.comment("How much Max Health Gummy Bear Servants have, Default: 36.0 (matches Alex's Caves Gummy Bear)")
                .defineInRange("gummyBearServantHealth", 36.0, 1.0, Double.MAX_VALUE);
        GummyBearServantDamage = BUILDER.comment("How much damage Gummy Bear Servants deal, Default: 4.0 (matches Alex's Caves Gummy Bear)")
                .defineInRange("gummyBearServantDamage", 4.0, 1.0, Double.MAX_VALUE);
        GummyBearServantArmor = BUILDER.comment("How much natural Armor Gummy Bear Servants have, Default: 0.0 (matches Alex's Caves Gummy Bear)")
                .defineInRange("gummyBearServantArmor", 0.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Caramel Cube Servant");
        CaramelCubeServantHealth = BUILDER.comment("Base Max Health of Caramel Cube Servants (size 0), Default: 4.0 (matches Alex's Caves Caramel Cube); larger sizes add +6 per size")
                .defineInRange("caramelCubeServantHealth", 4.0, 1.0, Double.MAX_VALUE);
        CaramelCubeServantDamage = BUILDER.comment("Base attack damage of Caramel Cube Servants (size 0), Default: 2.0 (matches Alex's Caves Caramel Cube); larger sizes add +2 per size")
                .defineInRange("caramelCubeServantDamage", 2.0, 1.0, Double.MAX_VALUE);
        CaramelCubeServantArmor = BUILDER.comment("How much natural Armor Caramel Cube Servants have, Default: 0.0 (matches Alex's Caves Caramel Cube)")
                .defineInRange("caramelCubeServantArmor", 0.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Gumbeeper Servant");
        GumbeeperServantHealth = BUILDER.comment("How much Max Health Gumbeeper Servants have, Default: 14.0 (matches Alex's Caves Gumbeeper)")
                .defineInRange("gumbeeperServantHealth", 14.0, 1.0, Double.MAX_VALUE);
        GumbeeperServantDamage = BUILDER.comment("How much damage Gumbeeper Servants' gumballs deal, Default: 4.0 (matches Alex's Caves Gumbeeper)")
                .defineInRange("gumbeeperServantDamage", 4.0, 1.0, Double.MAX_VALUE);
        GumbeeperServantArmor = BUILDER.comment("How much natural Armor Gumbeeper Servants have, Default: 4.0 (matches Alex's Caves Gumbeeper)")
                .defineInRange("gumbeeperServantArmor", 4.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Vesper Servant");
        VesperServantHealth = BUILDER.comment("How much Max Health Vesper Servants have, Default: 16.0 (matches Alex's Caves Vesper)")
                .defineInRange("vesperServantHealth", 16.0, 1.0, Double.MAX_VALUE);
        VesperServantDamage = BUILDER.comment("How much damage Vesper Servants deal, Default: 3.0 (matches Alex's Caves Vesper)")
                .defineInRange("vesperServantDamage", 3.0, 1.0, Double.MAX_VALUE);
        VesperServantArmor = BUILDER.comment("How much natural Armor Vesper Servants have, Default: 0.0 (matches Alex's Caves Vesper)")
                .defineInRange("vesperServantArmor", 0.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Teletor Servant");
        TeletorServantHealth = BUILDER.comment("How much Max Health Teletor Servants have, Default: 18.0 (matches Alex's Caves Teletor)")
                .defineInRange("teletorServantHealth", 18.0, 1.0, Double.MAX_VALUE);
        TeletorServantDamage = BUILDER.comment("How much damage Teletor Servants deal, Default: 2.0 (matches Alex's Caves Teletor)")
                .defineInRange("teletorServantDamage", 2.0, 1.0, Double.MAX_VALUE);
        TeletorServantArmor = BUILDER.comment("How much natural Armor Teletor Servants have, Default: 0.0 (matches Alex's Caves Teletor)")
                .defineInRange("teletorServantArmor", 0.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Forsaken Servant");
        ForsakenServantHealth = BUILDER.comment("How much Max Health Forsaken Servants have, Default: 250.0 (matches Alex's Caves Forsaken)")
                .defineInRange("forsakenServantHealth", 250.0, 1.0, Double.MAX_VALUE);
        ForsakenServantDamage = BUILDER.comment("How much damage Forsaken Servants deal, Default: 10.0 (matches Alex's Caves Forsaken)")
                .defineInRange("forsakenServantDamage", 10.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Candicorn Servant");
        CandicornServantHealth = BUILDER.comment("How much Max Health Candicorn Servants have, Default: 30.0 (matches Alex's Caves Candicorn)")
                .defineInRange("candicornServantHealth", 30.0, 1.0, Double.MAX_VALUE);
        CandicornServantDamage = BUILDER.comment("How much damage Candicorn Servants deal, Default: 6.0 (matches Alex's Caves Candicorn)")
                .defineInRange("candicornServantDamage", 6.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Gammaroach Servant");
        GammaroachServantHealth = BUILDER.comment("How much Max Health Gammaroach Servants have, Default: 14.0 (matches Alex's Caves Gammaroach)")
                .defineInRange("gammaroachServantHealth", 14.0, 1.0, Double.MAX_VALUE);
        GammaroachServantDamage = BUILDER.comment("How much damage Gammaroach Servants deal, Default: 2.0 (matches Alex's Caves Gammaroach)")
                .defineInRange("gammaroachServantDamage", 2.0, 1.0, Double.MAX_VALUE);
        GammaroachServantArmor = BUILDER.comment("How much natural Armor Gammaroach Servants have, Default: 0.0 (matches Alex's Caves Gammaroach)")
                .defineInRange("gammaroachServantArmor", 0.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Corrodent Servant");
        CorrodentServantHealth = BUILDER.comment("How much Max Health Corrodent Servants have, Default: 16.0 (matches Alex's Caves Corrodent)")
                .defineInRange("corrodentServantHealth", 16.0, 1.0, Double.MAX_VALUE);
        CorrodentServantDamage = BUILDER.comment("How much damage Corrodent Servants deal, Default: 3.0 (matches Alex's Caves Corrodent)")
                .defineInRange("corrodentServantDamage", 3.0, 1.0, Double.MAX_VALUE);
        CorrodentServantArmor = BUILDER.comment("How much natural Armor Corrodent Servants have, Default: 2.0 (matches Alex's Caves Corrodent)")
                .defineInRange("corrodentServantArmor", 2.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Deep One Servant");
        DeepOneServantHealth = BUILDER.comment("How much Max Health Deep One Servants have, Default: 30.0 (matches Alex's Caves Deep One)")
                .defineInRange("deepOneServantHealth", 30.0, 1.0, Double.MAX_VALUE);
        DeepOneServantDamage = BUILDER.comment("How much damage Deep One Servants deal, Default: 3.0 (matches Alex's Caves Deep One)")
                .defineInRange("deepOneServantDamage", 3.0, 1.0, Double.MAX_VALUE);
        DeepOneServantArmor = BUILDER.comment("How much natural Armor Deep One Servants have, Default: 0.0 (Alex's Caves Deep One has none)")
                .defineInRange("deepOneServantArmor", 0.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Deep One Knight Servant");
        DeepOneKnightServantHealth = BUILDER.comment("How much Max Health Deep One Knight Servants have, Default: 60.0 (matches Alex's Caves Deep One Knight)")
                .defineInRange("deepOneKnightServantHealth", 60.0, 1.0, Double.MAX_VALUE);
        DeepOneKnightServantDamage = BUILDER.comment("How much damage Deep One Knight Servants deal, Default: 5.0 (matches Alex's Caves Deep One Knight)")
                .defineInRange("deepOneKnightServantDamage", 5.0, 1.0, Double.MAX_VALUE);
        DeepOneKnightServantArmor = BUILDER.comment("How much natural Armor Deep One Knight Servants have, Default: 0.0 (Alex's Caves Deep One Knight has none)")
                .defineInRange("deepOneKnightServantArmor", 0.0, 0.0, Double.MAX_VALUE);
        DeepOneKnightServantOrtholanceDashDamage = BUILDER.comment("How much damage Deep One Knight Servants deal on Ortholance dash path hits, Default: 5.0")
                .defineInRange("deepOneKnightServantOrtholanceDashDamage", 5.0, 0.0, Double.MAX_VALUE);
        DeepOneKnightServantOrtholanceDashSpeed = BUILDER.comment("How fast (blocks/tick) Deep One Knight Servants dash toward the target, Default: 0.8")
                .defineInRange("deepOneKnightServantOrtholanceDashSpeed", 0.8, 0.0, Double.MAX_VALUE);
        DeepOneKnightServantOrtholanceChance = BUILDER.comment("Chance (0.0-1.0) for Deep One Knight Servants to spawn as the Ortholance variant (holding an Ortholance), Default: 0.7 (70%)")
                .defineInRange("deepOneKnightServantOrtholanceChance", 0.7, 0.0, 1.0);
        DeepOneKnightServantOrtholanceWaveCooldown = BUILDER.comment("Shared cooldown in ticks for the Ortholance wave+dash combo, Default: 60 (3 seconds; 0 = fire every tick)")
                .defineInRange("deepOneKnightServantOrtholanceWaveCooldown", 60, 0, Integer.MAX_VALUE);
        DeepOneKnightServantOrtholanceWaveRange = BUILDER.comment("Max distance (blocks) from the target at which Ortholance knights fire waves; beyond this they close in and fight with melee, Default: 8.0 (waves travel ~0.9 blocks/tick for 3-6 ticks, so ~5-7 blocks of reach)")
                .defineInRange("deepOneKnightServantOrtholanceWaveRange", 8.0, 1.0, 64.0);
        DeepOneKnightServantOrtholanceWaveCount = BUILDER.comment("How many fan wave indexes Deep One Knight Servants fire per Ortholance attack (each index spawns a left + right wave), Default: 4 (=8 waves), Ortholance maxes at 12 (=24 waves)")
                .defineInRange("deepOneKnightServantOrtholanceWaveCount", 4, 1, 12);
        DeepOneKnightServantOrtholanceWaveScale = BUILDER.comment("Wave scale of Ortholance waves, Default: 1.0 (matches Ortholance normal waves; contact damage ramps from waveScale+2 while travelling to waveScale+4 on impact, so 3.0 to 5.0 at default)")
                .defineInRange("deepOneKnightServantOrtholanceWaveScale", 1.0, 0.5, 10.0);
        BUILDER.pop();
        BUILDER.push("Deep One Mage Servant");
        DeepOneMageServantHealth = BUILDER.comment("How much Max Health Deep One Mage Servants have, Default: 80.0 (matches Alex's Caves Deep One Mage)")
                .defineInRange("deepOneMageServantHealth", 80.0, 1.0, Double.MAX_VALUE);
        DeepOneMageServantDamage = BUILDER.comment("How much damage Deep One Mage Servants deal, Default: 4.0 (matches Alex's Caves Deep One Mage)")
                .defineInRange("deepOneMageServantDamage", 4.0, 1.0, Double.MAX_VALUE);
        DeepOneMageServantArmor = BUILDER.comment("How much natural Armor Deep One Mage Servants have, Default: 0.0 (Alex's Caves Deep One Mage has none)")
                .defineInRange("deepOneMageServantArmor", 0.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Licowitch Servant");
        LicowitchServantHealth = BUILDER.comment("How much Max Health Licowitch Servants have, Default: 40.0 (matches Alex's Caves Licowitch)")
                .defineInRange("licowitchServantHealth", 40.0, 1.0, Double.MAX_VALUE);
        LicowitchServantDamage = BUILDER.comment("How much damage Licowitch Servants deal, Default: 3.0 (matches Alex's Caves Licowitch)")
                .defineInRange("licowitchServantDamage", 3.0, 1.0, Double.MAX_VALUE);
        LicowitchServantArmor = BUILDER.comment("How much natural Armor Licowitch Servants have, Default: 0.0 (Alex's Caves Licowitch has none)")
                .defineInRange("licowitchServantArmor", 0.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Hullbreaker Servant");
        HullbreakerServantGlowTargetRange = BUILDER.comment("How far (in blocks) Hullbreaker Servants search for glowing targets, Default: 48.0 (normal follow range is 16)")
                .defineInRange("hullbreakerServantGlowTargetRange", 48.0, 8.0, Double.MAX_VALUE);
        HullbreakerServantGlowChaseSpeed = BUILDER.comment("Swim speed Hullbreaker Servants use when chasing a glowing target, Default: 2.0 (normal approach speed is 1.6)")
                .defineInRange("hullbreakerServantGlowChaseSpeed", 2.0, 0.1, Double.MAX_VALUE);
        HullbreakerServantProximityTargetRange = BUILDER.comment("How close (in blocks) a non-glowing enemy must be for Hullbreaker Servants to sense and attack it, Default: 6.0")
                .defineInRange("hullbreakerServantProximityTargetRange", 6.0, 2.0, 64.0);
        HullbreakerServantHealth = BUILDER.comment("How much Max Health Hullbreaker Servants have, Default: 400.0 (matches Alex's Caves Hullbreaker)")
                .defineInRange("hullbreakerServantHealth", 400.0, 1.0, Double.MAX_VALUE);
        HullbreakerServantDamage = BUILDER.comment("How much damage Hullbreaker Servants deal, Default: 16.0 (matches Alex's Caves Hullbreaker)")
                .defineInRange("hullbreakerServantDamage", 16.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.pop();
        BUILDER.push("Servants Behavior");
        BUILDER.push("Tremorzilla Servant");
        TremorzillaServantBreakBlocks = BUILDER
                .comment("Whether Tremorzilla Servants can break blocks (melee attacks, beam, tail sweep and stomping through terrain while walking) (Default: false)")
                .define("tremorzillaServantBreakBlocks", false);
        TremorzillaServantBreakTrees = BUILDER
                .comment("Whether Tremorzilla Servants smash the leaves, logs and wool they walk through, like a Redstone Monstrosity (Default: true; also requires the mobGriefing game rule)")
                .define("tremorzillaServantBreakTrees", true);
        BUILDER.pop();
        BUILDER.push("Atlatitan Servant");
        AtlatitanServantBreakBlocks = BUILDER
                .comment("Whether Atlatitan Servants can crush blocks with their stomp (Default: true; also requires the mobGriefing game rule)")
                .define("atlatitanServantBreakBlocks", true);
        BUILDER.pop();
        BUILDER.push("Luxtructosaurus Servant");
        LuxtructosaurusServantBreakTrees = BUILDER
                .comment("Whether Luxtructosaurus Servants smash the leaves, logs and wool they walk through, like a Redstone Monstrosity (Default: true; also requires the mobGriefing game rule)")
                .define("luxtructosaurusServantBreakTrees", true);
        LuxtructosaurusServantFire = BUILDER
                .comment("Whether Luxtructosaurus Servants set fire to the ground with their flame breath (Default: true; also requires the mobGriefing game rule)")
                .define("luxtructosaurusServantFire", true);
        LuxtructosaurusServantWaterToStone = BUILDER
                .comment("Whether Luxtructosaurus Servants turn the water they wade through into stone (Default: true; also requires the mobGriefing game rule)")
                .define("luxtructosaurusServantWaterToStone", true);
        LuxtructosaurusServantTephra = BUILDER
                .comment("Whether Luxtructosaurus Servants call down a tephra bombardment while roaring. The meteors never break blocks (Default: true)")
                .define("luxtructosaurusServantTephra", true);
        LuxtructosaurusServantJumpExplosion = BUILDER
                .comment("Whether Luxtructosaurus Servants blast the ground alight where they land after a leap, like one of their falling tephra bombs. The blast damages and knocks back everything nearby and lights fires, but never breaks blocks (Default: true)")
                .define("luxtructosaurusServantJumpExplosion", true);
        LuxtructosaurusServantPrimalMagma = BUILDER
                .comment("Whether a Luxtructosaurus Servant keeps the primal magma around it molten the way the primordial boss does, and walks over it instead of sinking in (Default: true)")
                .define("luxtructosaurusServantPrimalMagma", true);
        LuxtructosaurusServantExtinctionCatalyst = BUILDER
                .comment("Whether owned Luxtructosaurus Servants return an Extinction Catalyst when they die (Default: true)")
                .define("luxtructosaurusServantExtinctionCatalyst", true);
        BUILDER.pop();
        BUILDER.push("Licowitch Servant");
        LicowitchSummonsLife = BUILDER
                .comment("Whether candy servants a Licowitch Servant summons have a limited lifespan (45~120s); when false they persist until killed, like Goety Necromancer summons with NecromancerSummonsLife off (Default: true)")
                .define("licowitchServantSummonsLife", true);
        BUILDER.pop();
        BUILDER.push("Hullbreaker Servant");
        HullbreakerServantReturnEmbryo = BUILDER
                .comment("Whether Hullbreaker Servants drop an immortal embryo when they die while owned (Default: true)")
                .define("hullbreakerServantReturnEmbryo", true);
        HullbreakerServantPickUpDrops = BUILDER
                .comment("Whether Hullbreaker Servants collect the drops of the mobs they kill (Default: true)")
                .define("hullbreakerServantPickUpDrops", true);
        HullbreakerServantBlockBreakGriefing = BUILDER.comment("Whether Hullbreaker Servants break blocks when bashing, Default: false (set to true to allow block destruction, which also respects the mobGriefing game rule)")
                .define("hullbreakerServantBlockBreakGriefing", false);
        BUILDER.pop();
        BUILDER.push("Mine Guardian Servant");
        MineGuardianServantExplosionGriefing = BUILDER.comment("Whether Mine Guardian Servant explosions destroy blocks, Default: false (set to true to allow block destruction, which also respects the mobGriefing game rule)")
                .define("mineGuardianServantExplosionGriefing", false);
        BUILDER.pop();
        BUILDER.pop();
        BUILDER.push("Servant Limits");
        HullbreakerServantLimit = BUILDER
                .comment("Maximum number of Hullbreaker Servants that can be summoned (Default: 1)")
                .defineInRange("hullbreakerServantLimit", 1, 1, 100);
        GrottoceratopsServantLimit = BUILDER
                .comment("Maximum number of Grottoceratops Servants that can be summoned (Default: 16)")
                .defineInRange("grottoceratopsServantLimit", 16, 1, 100);
        RelicheirusServantLimit = BUILDER
                .comment("Maximum number of Relicheirus Servants that can be summoned (Default: 4)")
                .defineInRange("relicheirusServantLimit", 4, 1, 100);
        TremorsaurusServantLimit = BUILDER
                .comment("Maximum number of Tremorsaurus Servants that can be summoned (Default: 4)")
                .defineInRange("tremorsaurusServantLimit", 4, 1, 100);
        TremorzillaServantLimit = BUILDER
                .comment("Maximum number of Tremorzilla Servants that can be summoned (Default: 1)")
                .defineInRange("tremorzillaServantLimit", 1, 1, 100);
        AtlatitanServantLimit = BUILDER
                .comment("Maximum number of Atlatitan Servants that can be summoned (Default: 3)")
                .defineInRange("atlatitanServantLimit", 3, 1, 100);
        LuxtructosaurusServantLimit = BUILDER
                .comment("Maximum number of Luxtructosaurus Servants that can be summoned (Default: 1)")
                .defineInRange("luxtructosaurusServantLimit", 1, 1, 100);
        GumWormServantLimit = BUILDER
                .comment("Maximum number of Gum Worm Servants that can be summoned (Default: 1; each one is a head plus 15-19 body segment entities)")
                .defineInRange("gumWormServantLimit", 1, 1, 100);
        VallumraptorServantLimit = BUILDER
                .comment("Maximum number of Vallumraptor Servants that can be summoned (Default: 32)")
                .defineInRange("vallumraptorServantLimit", 32, 1, 100);
        NucleeperServantLimit = BUILDER
                .comment("Maximum number of Nucleeper Servants that can be summoned (Default: 8)")
                .defineInRange("nucleeperServantLimit", 8, 1, 100);
        BrainiacServantLimit = BUILDER
                .comment("Maximum number of Brainiac Servants that can be summoned (Default: 16)")
                .defineInRange("brainiacServantLimit", 16, 1, 100);
        CaniacServantLimit = BUILDER
                .comment("Maximum number of Caniac Servants that can be summoned (Default: 16)")
                .defineInRange("caniacServantLimit", 16, 1, 100);
        GummyBearServantLimit = BUILDER
                .comment("Maximum number of Gummy Bear Servants that can be summoned (Default: 16)")
                .defineInRange("gummyBearServantLimit", 16, 1, 100);
        CaramelCubeServantLimit = BUILDER
                .comment("Maximum number of Caramel Cube Servants that can be summoned (Default: 16)")
                .defineInRange("caramelCubeServantLimit", 16, 1, 100);
        GumbeeperServantLimit = BUILDER
                .comment("Maximum number of Gumbeeper Servants that can be summoned (Default: 16)")
                .defineInRange("gumbeeperServantLimit", 16, 1, 100);
        VesperServantLimit = BUILDER
                .comment("Maximum number of Vesper Servants that can be summoned (Default: 16)")
                .defineInRange("vesperServantLimit", 16, 1, 100);
        TeletorServantLimit = BUILDER
                .comment("Maximum number of Teletor Servants that can be summoned (Default: 16)")
                .defineInRange("teletorServantLimit", 16, 1, 100);
        ForsakenServantLimit = BUILDER
                .comment("Maximum number of Forsaken Servants that can be summoned (Default: 2)")
                .defineInRange("forsakenServantLimit", 2, 1, 100);
        CandicornServantLimit = BUILDER
                .comment("Maximum number of Candicorn Servants that can be summoned (Default: 4)")
                .defineInRange("candicornServantLimit", 4, 1, 100);
        GammaroachServantLimit = BUILDER
                .comment("Maximum number of Gammaroach Servants that can be summoned (Default: 64)")
                .defineInRange("gammaroachServantLimit", 64, 1, 100);
        CorrodentServantLimit = BUILDER
                .comment("Maximum number of Corrodent Servants that can be summoned (Default: 16)")
                .defineInRange("corrodentServantLimit", 16, 1, 100);
        DeepOneServantLimit = BUILDER
                .comment("Maximum number of Deep One Servants that can be summoned (Default: 16)")
                .defineInRange("deepOneServantLimit", 16, 1, 100);
        DeepOneKnightServantLimit = BUILDER
                .comment("Maximum number of Deep One Knight Servants that can be summoned (Default: 8, same as Goety Cataclysm's Deepling Brute Servant)")
                .defineInRange("deepOneKnightServantLimit", 8, 1, 100);
        DeepOneMageServantLimit = BUILDER
                .comment("Maximum number of Deep One Mage Servants that can be summoned (Default: 6)")
                .defineInRange("deepOneMageServantLimit", 6, 1, 100);
        LicowitchSummonLimit = BUILDER
                .comment("Maximum number of candy servants one Licowitch Servant can keep summoned at once, Default: 6)")
                .defineInRange("licowitchServantLimit", 6, 1, 100);
        MineGuardianServantLimit = BUILDER
                .defineInRange("mineGuardianServantLimit", 16, 1, 100);
        NuclearGuardianServantLimit = BUILDER
                .comment("Maximum number of Nuclear Guardian Servants that can be summoned (Default: 2)")
                .defineInRange("nuclearGuardianServantLimit", 2, 1, 100);
        BUILDER.pop();
        BUILDER.push("Spells");
        BUILDER.push("Extinction Breath Spell");
        ExtinctionBreathDamage = BUILDER.comment("Damage dealt per tick by Extinction Breath, Default: 4.0")
                .defineInRange("extinctionBreathDamage", 4.0, 0.0, Double.MAX_VALUE);
        ExtinctionBreathSoulCost = BUILDER.comment("Soul cost of Extinction Breath, Default: 12")
                .defineInRange("extinctionBreathSoulCost", 12, 1, 100);
        ExtinctionBreathChargeUp = BUILDER.comment("Charge up time of Extinction Breath in ticks, Default: 0")
                .defineInRange("extinctionBreathChargeUp", 0, 0, 500);
        ExtinctionBreathDuration = BUILDER.comment("Breath duration of Extinction Breath in ticks, Default: 200")
                .defineInRange("extinctionBreathDuration", 200, 0, 1000);
        ExtinctionBreathCoolDown = BUILDER.comment("Cooldown of Extinction Breath in ticks, Default: 500")
                .defineInRange("extinctionBreathCoolDown", 500, 0, 5000);
        BUILDER.pop();
        BUILDER.push("Extinction Meteor Spell");
        ExtinctionMeteorCost = BUILDER.comment("Soul cost of Extinction Meteor, Default: 100")
                .defineInRange("extinctionMeteorCost", 100, 1, 500);
        ExtinctionMeteorChargeUp = BUILDER.comment("Ticks to charge up before the shower starts, Default: 100")
                .defineInRange("extinctionMeteorChargeUp", 100, 0, 72000);
        ExtinctionMeteorInterval = BUILDER.comment("Ticks between each meteor volley, Default: 8")
                .defineInRange("extinctionMeteorInterval", 8, 1, 200);
        ExtinctionMeteorShots = BUILDER.comment("Base number of volleys per cast, set 0 to allow the spell to be cast indefinitely, Default: 5")
                .defineInRange("extinctionMeteorShots", 5, 0, 72000);
        ExtinctionMeteorCoolDown = BUILDER.comment("Cooldown of Extinction Meteor in ticks, Default: 600")
                .defineInRange("extinctionMeteorCoolDown", 600, 0, 5000);
        BUILDER.pop();
        BUILDER.push("Primal Pack Spell");
        PrimalPackCost = BUILDER.comment("Soul cost of Primal Pack spell (Default: 16)")
                .defineInRange("primalPackCost", 16, 0, Integer.MAX_VALUE);
        PrimalPackDuration = BUILDER.comment("Cast duration of Primal Pack spell in ticks (Default: 120)")
                .defineInRange("primalPackDuration", 120, 0, 72000);
        PrimalPackSummonDown = BUILDER.comment("Summon down duration of Primal Pack spell in ticks (Default: 100)")
                .defineInRange("primalPackSummonDown", 100, 0, 72000);
        PrimalPackCoolDown = BUILDER.comment("Cooldown of Primal Pack spell in ticks (Default: 120)")
                .defineInRange("primalPackCoolDown", 120, 0, 72000);
        BUILDER.pop();
        BUILDER.push("Tremor Spirit Spell");
        TremorSpiritCost = BUILDER.comment("Soul cost of Tremor Spirit spell (Default: 24)")
                .defineInRange("tremorSpiritCost", 24, 0, Integer.MAX_VALUE);
        TremorSpiritTime = BUILDER.comment("Cast duration of Tremor Spirit spell in ticks, Default: 60")
                .defineInRange("tremorSpiritTime", 60, 0, 72000);
        TremorSpiritCoolDown = BUILDER.comment("Cooldown of Tremor Spirit spell in ticks, Default: 600")
                .defineInRange("tremorSpiritCoolDown", 600, 0, 72000);
        TremorSpiritBuffSeconds = BUILDER.comment("Base Tremor Spirit buff duration in seconds, multiplied by spell duration, Default: 30")
                .defineInRange("tremorSpiritBuffSeconds", 30, 1, 72000);
        TremorSpiritSpiritCooldown = BUILDER.comment("Ticks a player must wait between two spirit attacks, Default: 30")
                .defineInRange("tremorSpiritSpiritCooldown", 30, 0, 7200);
        TremorSpiritDamage = BUILDER.comment("Base damage of the Tremorsaurus spirit, Default: 3.0")
                .defineInRange("tremorSpiritDamage", 3.0D, 0.0D, Double.MAX_VALUE);
        TremorSpiritDamagePerPotency = BUILDER.comment("Extra damage per potency level, Default: 2.0")
                .defineInRange("tremorSpiritDamagePerPotency", 2.0D, 0.0D, Double.MAX_VALUE);
        TremorSpiritStaffBonus = BUILDER.comment("Extra damage when the buff was cast with a matching staff, Default: 2.0")
                .defineInRange("tremorSpiritStaffBonus", 2.0D, 0.0D, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Grotto Spirit Spell");
        GrottoSpiritCost = BUILDER.comment("Soul cost of Grotto Spirit spell, Default: 100")
                .defineInRange("grottoSpiritCost", 100, 0, Integer.MAX_VALUE);
        GrottoSpiritCastTime = BUILDER.comment("Cast duration of Grotto Spirit spell in ticks, Default: 100")
                .defineInRange("grottoSpiritCastTime", 100, 0, 72000);
        GrottoSpiritCoolDown = BUILDER.comment("Cooldown of Grotto Spirit spell in ticks, Default: 6000")
                .defineInRange("grottoSpiritCoolDown", 6000, 0, 72000);
        GrottoSpiritTime = BUILDER.comment("Lifetime of the Grotto Spirits in ticks, Default: 1500 (75s)")
                .defineInRange("grottoSpiritTime", 1500, 1, 72000);
        BUILDER.pop();
        BUILDER.push("Abyssal Dweller Spell");
        DeepOneSoulCost = BUILDER.comment("Soul cost of DeepOne Servant spell (Default: 24)")
                .defineInRange("deepOneSoulCost", 24, 1, 128);
        DeepOneCastDuration = BUILDER.comment("Cast duration of DeepOne Servant spell in ticks (Default: 60)")
                .defineInRange("deepOneCastDuration", 60, 0, 500);
        DeepOneSummonDown = BUILDER.comment("Summon down duration of DeepOne Servant spell in ticks (Default: 300)")
                .defineInRange("deepOneSummonDown", 300, 0, 5000);
        DeepOneCoolDown = BUILDER.comment("Cooldown of DeepOne Servant spell in ticks (Default: 100)")
                .defineInRange("deepOneCoolDown", 100, 0, 5000);
        BUILDER.pop();
        BUILDER.push("Abyssal Knight Spell");
        DeepKnightSoulCost = BUILDER.comment("Soul cost of DeepKnight Servant spell (Default: 32)")
                .defineInRange("deepKnightSoulCost", 32, 1, 128);
        DeepKnightCastDuration = BUILDER.comment("Cast duration of DeepKnight Servant spell in ticks (Default: 100)")
                .defineInRange("deepKnightCastDuration", 100, 0, 500);
        DeepKnightSummonDown = BUILDER.comment("Summon down duration of DeepKnight Servant spell in ticks (Default: 300)")
                .defineInRange("deepKnightSummonDown", 300, 0, 5000);
        DeepKnightCoolDown = BUILDER.comment("Cooldown of DeepKnight Servant spell in ticks (Default: 400)")
                .defineInRange("deepKnightCoolDown", 400, 0, 5000);
        BUILDER.pop();
        BUILDER.push("Abyssal Mage Spell");
        DeepMageSoulCost = BUILDER.comment("Soul cost of DeepMage Servant spell (Default: 32)")
                .defineInRange("deepMageSoulCost", 32, 1, 128);
        DeepMageCastDuration = BUILDER.comment("Cast duration of DeepMage Servant spell in ticks (Default: 100)")
                .defineInRange("deepMageCastDuration", 100, 0, 500);
        DeepMageSummonDown = BUILDER.comment("Summon down duration of DeepMage Servant spell in ticks (Default: 300)")
                .defineInRange("deepMageSummonDown", 300, 0, 5000);
        DeepMageCoolDown = BUILDER.comment("Cooldown of DeepMage Servant spell in ticks (Default: 600)")
                .defineInRange("deepMageCoolDown", 600, 0, 5000);
        BUILDER.pop();
        BUILDER.push("Water Bolt Spell");
        WaterBoltCost = BUILDER.comment("Soul cost of Water Bolt spell (Default: 8)")
                .defineInRange("waterBoltCost", 8, 1, 100);
        WaterBoltCoolDown = BUILDER.comment("Cooldown of Water Bolt spell in ticks (Default: 20)")
                .defineInRange("waterBoltCoolDown", 20, 0, 2000);
        BUILDER.pop();
        BUILDER.push("Deep Wave Spell");
        DeepWaveCost = BUILDER.comment("Soul cost of Deep Wave spell (Default: 12)")
                .defineInRange("deepWaveCost", 12, 1, 100);
        DeepWaveCastDuration = BUILDER.comment("Cast duration of Deep Wave spell in ticks (Default: 40)")
                .defineInRange("deepWaveCastDuration", 40, 0, 2000);
        DeepWaveCoolDown = BUILDER.comment("Cooldown of Deep Wave spell in ticks (Default: 50)")
                .defineInRange("deepWaveCoolDown", 50, 0, 2000);
        BUILDER.pop();
        BUILDER.push("Tide Bash Spell");
        TideBashCost = BUILDER.comment("Soul cost of Tide Bash spell (Default: 8)")
                .defineInRange("tideBashCost", 8, 1, 100);
        TideBashCoolDown = BUILDER.comment("Cooldown of Tide Bash spell in ticks (Default: 40)")
                .defineInRange("tideBashCoolDown", 40, 0, 2000);
        BUILDER.pop();
        BUILDER.push("Nuclear Bomb Spell");
        AtomicBombCost = BUILDER.comment("Soul cost of Atomic Bomb spell, Default: 10000")
                .defineInRange("atomicBombCost", 10000, 1, 10000);
        AtomicBombCastDuration = BUILDER.comment("Cast duration of Atomic Bomb spell in ticks, Default: 600")
                .defineInRange("atomicBombCastDuration", 600, 0, 2000);
        AtomicBombCoolDown = BUILDER.comment("Cooldown of Atomic Bomb spell in ticks, Default: 12000")
                .defineInRange("atomicBombCoolDown", 12000, 0, 100000);
        AtomicBombRange = BUILDER.comment("Base targeting range of Atomic Bomb spell in blocks, Default: 25")
                .defineInRange("atomicBombRange", 25, 1, 512);
        BUILDER.pop();
        BUILDER.push("Brainiac Spell");
        BrainiacCost = BUILDER.comment("Soul cost of Brainiac Servant spell (Default: 16)")
                .defineInRange("brainiacSoulCost", 16, 1, 100);
        BrainiacDuration = BUILDER.comment("Cast duration of Brainiac Servant spell in ticks (Default: 60)")
                .defineInRange("brainiacCastDuration", 60, 0, 500);
        BrainiacSummonDown = BUILDER.comment("Summon down duration of Brainiac Servant spell in ticks (Default: 160)")
                .defineInRange("brainiacSummonDown", 160, 0, 5000);
        BrainiacCoolDown = BUILDER.comment("Cooldown of Brainiac Servant spell in ticks (Default: 200)")
                .defineInRange("brainiacCoolDown", 200, 0, 5000);
        BUILDER.pop();
        BUILDER.push("Atomic Breath Spell");
        TremorzillaBreathCost = BUILDER.comment("Soul cost of Tremorzilla Breath spell, Default: 500")
                .defineInRange("tremorzillaBreathCost", 500, 1, 10000);
        TremorzillaBreathCastDuration = BUILDER.comment("Cast duration of Tremorzilla Breath spell in ticks, Default: 600")
                .defineInRange("tremorzillaBreathCastDuration", 600, 0, 2000);
        TremorzillaBreathCoolDown = BUILDER.comment("Cooldown of Tremorzilla Breath spell in ticks, Default: 1200")
                .defineInRange("tremorzillaBreathCoolDown", 1200, 0, 100000);
        TremorzillaBreathDamage = BUILDER.comment("Damage per hit of the Tremorzilla Breath beam, Default: 20.0")
                .defineInRange("tremorzillaBreathDamage", 20.0, 1.0, Double.MAX_VALUE);
        TremorzillaBreathRange = BUILDER.comment("Beam length in blocks, Default: 100.0")
                .defineInRange("tremorzillaBreathRange", 100.0, 1.0, 512.0);
        TremorzillaBreathDuration = BUILDER.comment("Base beam duration in ticks, Default: 100")
                .defineInRange("tremorzillaBreathDuration", 100, 1, 100000);
        TremorzillaBreathBreakBlocks = BUILDER.comment("Whether sneaking while casting lets the beam break blocks, Default: true")
                .define("tremorzillaBreathBreakBlocks", true);
        BUILDER.pop();
        BUILDER.push("X-Ray Spell");
        XRaySoulCost = BUILDER.comment("Soul cost of XRay Servant spell (Default: 12)")
                .defineInRange("xRaySoulCost", 12, 1, 100);
        XRayCastDuration = BUILDER.comment("Cast duration of XRay Servant spell in ticks (Default: 200)")
                .defineInRange("xRayCastDuration", 200, 0, 500);
        XRayRange = BUILDER.comment("Base range of X-Ray spell in blocks, Default: 25")
                .defineInRange("xrayRange", 25, 1, 512);
        XRayCoolDown = BUILDER.comment("Cooldown of XRay Servant spell in ticks (Default: 600)")
                .defineInRange("xRayCoolDown", 600, 0, 5000);
        BUILDER.pop();
        BUILDER.pop();
        BUILDER.push("Tools & Weapons");
        BUILDER.push("Barbaric Axe");
        BarbaricAxeDamage = BUILDER
                .comment("Barbaric Axe total attack damage, as shown in the tooltip, Default: 12.0")
                .defineInRange("barbaricAxeDamage", 12.0, 1.0, Double.MAX_VALUE);
        BarbaricAxeAttackSpeed = BUILDER
                .comment("Barbaric Axe total attack speed, as shown in the tooltip, Default: 1.0")
                .defineInRange("barbaricAxeAttackSpeed", 1.0, 0.5, 4.0);
        BarbaricAxeSweepDamage = BUILDER
                .comment("Damage dealt by the left click sweep to extra targets, Default: 6.0")
                .defineInRange("barbaricAxeSweepDamage", 6.0, 0.0, Double.MAX_VALUE);
        BarbaricAxeSweepRange = BUILDER
                .comment("Radius in blocks of the left click sweep, Default: 3.5")
                .defineInRange("barbaricAxeSweepRange", 3.5, 0.0, 16.0);
        BarbaricAxeSlamDamage = BUILDER
                .comment("Damage dealt by the ground slam, Default: 10.0")
                .defineInRange("barbaricAxeSlamDamage", 10.0, 0.0, Double.MAX_VALUE);
        BarbaricAxeSlamRadius = BUILDER
                .comment("Radius in blocks of the ground slam, Default: 5.0")
                .defineInRange("barbaricAxeSlamRadius", 5.0, 0.0, 16.0);
        BarbaricAxeIgniteSeconds = BUILDER
                .comment("Seconds affected entities stay on fire after a ground slam, Default: 5")
                .defineInRange("barbaricAxeIgniteSeconds", 5, 0, 60);
        BarbaricAxeChainLimit = BUILDER
                .comment("Maximum logs removed by one chain mining action, including the one that was broken, Default: 64")
                .defineInRange("barbaricAxeChainLimit", 64, 1, 1024);
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
