package com.qiuyue.goetyominous.config.am;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.File;

public class AmConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Double> MurmurServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> MurmurServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> FarseerServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> FarseerServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> FarseerServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> CrimsonMosquitoServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> CrimsonMosquitoServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> WarpedMoscoServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> WarpedMoscoServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> WarpedMoscoServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> WarpedMoscoServantArmorToughness;
    public static final ForgeConfigSpec.ConfigValue<Double> TusklinServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> TusklinServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> TigerServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> TigerServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> FroststalkerServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> FroststalkerServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> FroststalkerServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> DropBearServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> DropBearServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> DropBearServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> GusterServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> GusterServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> EmuServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> EmuServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> SoulVultureServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> SoulVultureServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> RockyRollerServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> RockyRollerServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> RockyRollerServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> ZombieCrocodileServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> ZombieCrocodileServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> ZombieCrocodileServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> SkelewagServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> SkelewagServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> BunfungusServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> BunfungusServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> BunfungusServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> BunfungusServantContactDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> IllagerElephantServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> IllagerElephantServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> CentipedeServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> CentipedeServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> CentipedeServantAttackDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> CentipedeServantBodyHealth;
    public static final ForgeConfigSpec.ConfigValue<Integer> CentipedeLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> SkelewagLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> RockyRollerLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> BunfungusLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> MurmurServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> FrostStalkerLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> FarseerServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> WarpedMoscoServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> IllagerElephantServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> DropBearServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> GusterServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> EmuServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> SoulVultureServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> TusklinServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> TigerServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> WarpedMoscoUnholyBloodHealthBonus;
    public static final ForgeConfigSpec.ConfigValue<Integer> WarpedMoscoUnholyBloodDamageBonus;
    public static final ForgeConfigSpec.ConfigValue<Boolean> WarpedMoscoUnholyBloodTexture;
    public static final ForgeConfigSpec.ConfigValue<Integer> SandSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> SandCastDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> SandCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Double> SandExtraDamage;
    public static final ForgeConfigSpec.ConfigValue<Integer> MurmurSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> MurmurCastDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> MurmurSummonDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> MurmurCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> BloodSpraySoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> BloodSprayCastDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> BloodSprayCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> CrimsonMosquitoSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> CrimsonMosquitoCastDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> CrimsonMosquitoSummonDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> CrimsonMosquitoCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> FrostStalkerSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> FrostStalkerCastDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> FrostStalkerSummonDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> FrostStalkerCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> RollerCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> RollerDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> RollerSummonDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> RollerCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> SkelewagCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> SkelewagDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> SkelewagSummonDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> SkelewagCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> GusterCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> GusterDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> GusterSummonDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> GusterCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> CentipedeCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> CentipedeDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> CentipedeSummonDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> CentipedeCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> DropBearSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> DropBearCastDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> DropBearSummonDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> DropBearCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> FartSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> FartCastDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> FartCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> VultureSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> VultureCastDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> VultureCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> VultureSummonDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> FarseerSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> FarseerCastDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> FarseerSummonDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> FarseerCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Integer> VoidShotSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> VoidShotCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Double> VoidShotDamage;
    public static final ForgeConfigSpec.ConfigValue<Integer> VoidShotCastUp;
    public static final ForgeConfigSpec.ConfigValue<Integer> VoidShotDuration;

    static {
        BUILDER.push("Servants Attributes");
        BUILDER.push("Murmur Servant");
        MurmurServantHealth = BUILDER.comment("How much Max Health Murmur Servants have, Default: 30.0")
                .defineInRange("murmurServantHealth", 30.0, 1.0, Double.MAX_VALUE);
        MurmurServantDamage = BUILDER.comment("How much damage Murmur Servants deal, Default: 3.0")
                .defineInRange("murmurServantDamage", 3.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Farseer Servant");
        FarseerServantHealth = BUILDER.comment("How much Max Health Farseer Servants have, Default: 70.0")
                .defineInRange("farseerServantHealth", 70.0, 1.0, Double.MAX_VALUE);
        FarseerServantArmor = BUILDER.comment("How much natural Armor Farseer Servants have, Default: 6.0")
                .defineInRange("farseerServantArmor", 6.0, 0.0, Double.MAX_VALUE);
        FarseerServantDamage = BUILDER.comment("How much damage Farseer Servants deal, Default: 4.5")
                .defineInRange("farseerServantDamage", 4.5, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Crimson Mosquito Servant");
        CrimsonMosquitoServantHealth = BUILDER.comment("How much Max Health Crimson Mosquito Servants have, Default: 10.0")
                .defineInRange("crimsonMosquitoServantHealth", 10.0, 1.0, Double.MAX_VALUE);
        CrimsonMosquitoServantDamage = BUILDER.comment("How much damage Crimson Mosquito Servants deal, Default: 5.0")
                .defineInRange("crimsonMosquitoServantDamage", 5.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Warped Mosco Servant");
        WarpedMoscoServantHealth = BUILDER.comment("How much Max Health Warped Mosco Servants have, Default: 100.0")
                .defineInRange("warpedMoscoServantHealth", 100.0, 1.0, Double.MAX_VALUE);
        WarpedMoscoServantDamage = BUILDER.comment("How much damage Warped Mosco Servants deal, Default: 10.0")
                .defineInRange("warpedMoscoServantDamage", 10.0, 1.0, Double.MAX_VALUE);
        WarpedMoscoServantArmor = BUILDER.comment("How much natural Armor Warped Mosco Servants have, Default: 10.0")
                .defineInRange("warpedMoscoServantArmor", 10.0, 0.0, Double.MAX_VALUE);
        WarpedMoscoServantArmorToughness = BUILDER.comment("How much natural Armor Toughness Warped Mosco Servants have, Default: 2.0")
                .defineInRange("warpedMoscoServantArmorToughness", 2.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Tusklin Servant");
        TusklinServantHealth = BUILDER.comment("How much Max Health Tusklin Servants have, Default: 40.0")
                .defineInRange("tusklinServantHealth", 40.0, 1.0, Double.MAX_VALUE);
        TusklinServantDamage = BUILDER.comment("How much damage Tusklin Servants deal, Default: 9.0")
                .defineInRange("tusklinServantDamage", 9.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Tiger Servant");
        TigerServantHealth = BUILDER.comment("How much Max Health Tiger Servants have, Default: 40.0")
                .defineInRange("tigerServantHealth", 40.0, 1.0, Double.MAX_VALUE);
        TigerServantDamage = BUILDER.comment("How much damage Tiger Servants deal, Default: 9.0")
                .defineInRange("tigerServantDamage", 9.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Froststalker Servant");
        FroststalkerServantHealth = BUILDER.comment("How much Max Health Froststalker Servants have, Default: 24.0 (matches Alex's Mobs Froststalker)")
                .defineInRange("froststalkerServantHealth", 24.0, 1.0, Double.MAX_VALUE);
        FroststalkerServantDamage = BUILDER.comment("How much damage Froststalker Servants deal, Default: 4.5 (matches Alex's Mobs Froststalker)")
                .defineInRange("froststalkerServantDamage", 4.5, 1.0, Double.MAX_VALUE);
        FroststalkerServantArmor = BUILDER.comment("How much natural Armor Froststalker Servants have, Default: 2.0 (matches Alex's Mobs Froststalker)")
                .defineInRange("froststalkerServantArmor", 2.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Drop Bear Servant");
        DropBearServantHealth = BUILDER.comment("How much Max Health Drop Bear Servants have, Default: 22.0 (matches Alex's Mobs drop bear)")
                .defineInRange("dropBearServantHealth", 22.0, 1.0, Double.MAX_VALUE);
        DropBearServantDamage = BUILDER.comment("How much damage Drop Bear Servants deal, Default: 2.0 (matches Alex's Mobs drop bear)")
                .defineInRange("dropBearServantDamage", 2.0, 1.0, Double.MAX_VALUE);
        DropBearServantArmor = BUILDER.comment("How much Armor Drop Bear Servants have, Default: 0 (matches Alex's Mobs drop bear)")
                .defineInRange("dropBearServantArmor", 0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Guster Servant");
        GusterServantHealth = BUILDER.comment("How much Max Health Guster Servants have, Default: 16.0 (matches Alex's Mobs guster)")
                .defineInRange("gusterServantHealth", 16.0, 1.0, Double.MAX_VALUE);
        GusterServantDamage = BUILDER.comment("How much damage Guster Servants deal, Default: 1.0 (matches Alex's Mobs guster)")
                .defineInRange("gusterServantDamage", 1.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Emu Servant");
        EmuServantHealth = BUILDER.comment("How much Max Health Emu Servants have, Default: 20.0 (matches Alex's Mobs emu)")
                .defineInRange("emuServantHealth", 20.0, 1.0, Double.MAX_VALUE);
        EmuServantDamage = BUILDER.comment("How much damage Emu Servants deal, Default: 3.0 (matches Alex's Mobs emu)")
                .defineInRange("emuServantDamage", 3.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Soul Vulture Servant");
        SoulVultureServantHealth = BUILDER.comment("How much Max Health Soul Vulture Servants have, Default: 12.0 (matches Alex's Mobs soul vulture)")
                .defineInRange("soulVultureServantHealth", 12.0, 1.0, Double.MAX_VALUE);
        SoulVultureServantDamage = BUILDER.comment("How much damage Soul Vulture Servants deal, Default: 4.0 (matches Alex's Mobs soul vulture)")
                .defineInRange("soulVultureServantDamage", 4.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Rocky Roller Servant");
        RockyRollerServantHealth = BUILDER.comment("How much Max Health Rocky Roller Servants have, Default: 10.0 (matches Alex's Mobs rocky roller)")
                .defineInRange("rockyRollerServantHealth", 10.0, 1.0, Double.MAX_VALUE);
        RockyRollerServantDamage = BUILDER.comment("How much damage Rocky Roller Servants deal, Default: 2.0 (matches Alex's Mobs rocky roller)")
                .defineInRange("rockyRollerServantDamage", 2.0, 1.0, Double.MAX_VALUE);
        RockyRollerServantArmor = BUILDER.comment("How much natural Armor Rocky Roller Servants have, Default: 20.0 (matches Alex's Mobs rocky roller)")
                .defineInRange("rockyRollerServantArmor", 20.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Zombie Crocodile Servant");
        ZombieCrocodileServantHealth = BUILDER.comment("How much Max Health Zombie Crocodile Servants have, Default: 30.0")
                .defineInRange("zombieCrocodileServantHealth", 30.0, 1.0, Double.MAX_VALUE);
        ZombieCrocodileServantDamage = BUILDER.comment("How much damage Zombie Crocodile Servants deal, Default: 10.0")
                .defineInRange("zombieCrocodileServantDamage", 10.0, 1.0, Double.MAX_VALUE);
        ZombieCrocodileServantArmor = BUILDER.comment("How much natural Armor Zombie Crocodile Servants have, Default: 8.0")
                .defineInRange("zombieCrocodileServantArmor", 8.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Skelewag Servant");
        SkelewagServantHealth = BUILDER.comment("How much Max Health Skelewag Servants have, Default: 20.0 (matches Alex's Mobs Skelewag)")
                .defineInRange("skelewagServantHealth", 20.0, 1.0, Double.MAX_VALUE);
        SkelewagServantDamage = BUILDER.comment("How much damage Skelewag Servants deal, Default: 3.0 (matches Alex's Mobs Skelewag)")
                .defineInRange("skelewagServantDamage", 3.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Bunfungus Servant");
        BunfungusServantHealth = BUILDER.comment("How much Max Health Bunfungus Servants have, Default: 80.0 (matches Alex's Mobs bunfungus)")
                .defineInRange("bunfungusServantHealth", 80.0, 1.0, Double.MAX_VALUE);
        BunfungusServantDamage = BUILDER.comment("How much damage Bunfungus Servants deal, Default: 8.0 (matches Alex's Mobs bunfungus)")
                .defineInRange("bunfungusServantDamage", 8.0, 1.0, Double.MAX_VALUE);
        BunfungusServantArmor = BUILDER.comment("How much Armor Bunfungus Servants have, Default: 0.21 (matches Alex's Mobs bunfungus)")
                .defineInRange("bunfungusServantArmor", 0.21, 0.0, Double.MAX_VALUE);
        BunfungusServantContactDamage = BUILDER.comment("Mid-air contact damage Bunfungus Servants deal when landing on a target, Default: 10.0 (matches Alex's Mobs hardcoded value)")
                .defineInRange("bunfungusServantContactDamage", 10.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Illager Elephant Servant");
        IllagerElephantServantHealth = BUILDER.comment("How much Max Health Illager Elephant Servants have, Default: 150.0 (matches Alex's Mobs tusked elephant spawn health)")
                .defineInRange("illagerElephantServantHealth", 150.0, 1.0, Double.MAX_VALUE);
        IllagerElephantServantDamage = BUILDER.comment("How much damage Illager Elephant Servants deal, Default: 15.0 (matches Alex's Mobs tusked elephant)")
                .defineInRange("illagerElephantServantDamage", 15.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Centipede Servant");
        CentipedeServantHealth = BUILDER.comment("Max health of Servant Centipede Head (Default: 35)")
                .defineInRange("centipedeServantHealth", 35.0, 1.0, 1000.0);
        CentipedeServantArmor = BUILDER.comment("Armor of Servant Centipede (Default: 6)")
                .defineInRange("centipedeServantArmor", 6.0, 0.0, 100.0);
        CentipedeServantAttackDamage = BUILDER.comment("Attack damage of Servant Centipede (Default: 8)")
                .defineInRange("centipedeServantAttackDamage", 8.0, 1.0, 1000.0);
        CentipedeServantBodyHealth = BUILDER.comment("Max health of Servant Centipede Body segments (Default: 10)")
                .defineInRange("centipedeServantBodyHealth", 10.0, 1.0, 1000.0);
        BUILDER.pop();
        BUILDER.pop();
        BUILDER.push("Servants Enhancements");
        BUILDER.push("Warped Mosco Servant");
        WarpedMoscoUnholyBloodHealthBonus = BUILDER.comment("Extra health bonus when Warped Mosco has Unholy Blood, Default: 50")
                .defineInRange("warpedMoscoUnholyBloodHealthBonus", 50, 0, Integer.MAX_VALUE);
        WarpedMoscoUnholyBloodDamageBonus = BUILDER.comment("Extra attack damage bonus when Warped Mosco has Unholy Blood, Default: 3")
                .defineInRange("warpedMoscoUnholyBloodDamageBonus", 3, 0, Integer.MAX_VALUE);
        WarpedMoscoUnholyBloodTexture = BUILDER.comment("Use the alternate texture when Warped Mosco has Unholy Blood (Default: true)")
                .define("warpedMoscoUnholyBloodTexture", true);
        BUILDER.pop();
        BUILDER.pop();
        BUILDER.push("Servant Limits");
        CentipedeLimit = BUILDER.comment("Maximum number of Centipede Servants that can exist at once (Default: 12)")
                .defineInRange("centipedeServantLimit", 12, 1, 100);
        SkelewagLimit = BUILDER
                .comment("Maximum number of Skelewag Servants that can be summoned (Default: 32)")
                .defineInRange("skelewagServantLimit", 32, 1, 100);
        RockyRollerLimit = BUILDER
                .comment("Maximum number of Rocky Roller Servants that can be summoned (Default: 16)")
                .defineInRange("rockyRollerServantLimit", 16, 1, 100);
        BunfungusLimit = BUILDER
                .comment("Maximum number of Bunfungus Servants that can be summoned (Default: 6)")
                .defineInRange("bunfungusServantLimit", 6, 1, 100);
        MurmurServantLimit = BUILDER
                .comment("Maximum number of Murmur Servants that can be summoned (Default: 16)")
                .defineInRange("murmurServantLimit", 16, 1, 100);
        FrostStalkerLimit = BUILDER
                .comment("Maximum number of FrostStalker Servants that can be summoned (Default: 32)")
                .defineInRange("froststalkerServantLimit", 32, 1, 100);
        FarseerServantLimit = BUILDER
                .comment("Maximum number of Farseer Servants that can be summoned (Default: 2)")
                .defineInRange("farseerServantLimit", 2, 1, 100);
        WarpedMoscoServantLimit = BUILDER
                .comment("Maximum number of Warped Mosco Servants that can be summoned (Default: 2)")
                .defineInRange("warpedMoscoServantLimit", 2, 1, 100);
        IllagerElephantServantLimit = BUILDER
                .comment("Maximum number of Illager Elephant Servants that can be summoned (Default: 2)")
                .defineInRange("illagerElephantServantLimit", 2, 1, 100);
        DropBearServantLimit = BUILDER
                .comment("Maximum number of Drop Bear Servants that can be summoned (Default: 16)")
                .defineInRange("dropBearServantLimit", 16, 1, 100);
        GusterServantLimit = BUILDER
                .comment("Maximum number of Guster Servants that can be summoned (Default: 32)")
                .defineInRange("gusterServantLimit", 32, 1, 100);
        EmuServantLimit = BUILDER
                .comment("Maximum number of Emu Servants that can be summoned (Default: 16)")
                .defineInRange("emuServantLimit", 16, 1, 100);
        SoulVultureServantLimit = BUILDER
                .comment("Maximum number of Soul Vulture Servants that can be summoned (Default: 64)")
                .defineInRange("soulVultureServantLimit", 64, 1, 100);
        TusklinServantLimit = BUILDER
                .comment("Maximum number of Tusklin Servants that can be summoned (Default: 16)")
                .defineInRange("tusklinServantLimit", 16, 1, 100);
        TigerServantLimit = BUILDER
                .comment("Maximum number of Tiger Servants that can be summoned (Default: 8)")
                .defineInRange("tigerServantLimit", 8, 1, 100);
        BUILDER.pop();
        BUILDER.push("Spells");
        BUILDER.push("Sandy Spell");
        SandSoulCost = BUILDER.comment("Sand Spell Cost, Default: 6")
                .defineInRange("sandCost", 6, 0, Integer.MAX_VALUE);
        SandCastDuration = BUILDER.comment("Time to cast Sand Spell, Default: 0")
                .defineInRange("sandTime", 0, 0, 72000);
        SandCoolDown = BUILDER.comment("Sand Spell Cooldown, Default: 10")
                .defineInRange("sandCoolDown", 10, 0, Integer.MAX_VALUE);
        SandExtraDamage = BUILDER.comment("How much base damage Sand deals, Default: 2.5")
                .defineInRange("sandDamage", 2.5, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Murmur Spell");
        MurmurSoulCost = BUILDER.comment("Soul cost of Murmur Servant spell (Default: 16)")
                .defineInRange("murmurSoulCost", 16, 1, 100);
        MurmurCastDuration = BUILDER.comment("Cast duration of Murmur Servant spell in ticks (Default: 80)")
                .defineInRange("murmurCastDuration", 80, 0, 500);
        MurmurSummonDown = BUILDER.comment("Summon down duration of Murmur Servant spell in ticks (Default: 100)")
                .defineInRange("murmurSummonDown", 100, 0, 5000);
        MurmurCoolDown = BUILDER.comment("Cooldown of Murmur Servant spell in ticks (Default: 80)")
                .defineInRange("murmurCoolDown", 80, 0, 5000);
        BUILDER.pop();
        BUILDER.push("Bloodspray Spell");
        BloodSpraySoulCost = BUILDER.comment("Soul cost of BloodSpray spell (Default: 8)")
                .defineInRange("bloodSpraySoulCost", 8, 1, 100);
        BloodSprayCastDuration = BUILDER.comment("Cast duration of BloodSpray spell in ticks (Default: 200)")
                .defineInRange("bloodSprayCastDuration", 200, 0, 500);
        BloodSprayCoolDown = BUILDER.comment("Cooldown of BloodSpray in ticks (Default: 100)")
                .defineInRange("bloodSprayCoolDown", 100, 0, 5000);
        BUILDER.pop();
        BUILDER.push("Crimson Spell");
        CrimsonMosquitoSoulCost = BUILDER.comment("Soul cost of Crimson Mosquito Servant spell (Default: 20)")
                .defineInRange("crimsonMosquitoSoulCost", 20, 1, 100);
        CrimsonMosquitoCastDuration = BUILDER.comment("Cast duration of Crimson Mosquito Servant spell in ticks (Default: 100)")
                .defineInRange("crimsonMosquitoCastDuration", 100, 0, 500);
        CrimsonMosquitoSummonDown = BUILDER.comment("Summon down duration of Crimson Mosquito Servant spell in ticks (Default: 100)")
                .defineInRange("crimsonMosquitoSummonDown", 100, 0, 5000);
        CrimsonMosquitoCoolDown = BUILDER.comment("Cooldown of Crimson Mosquito Servant spell in ticks (Default: 160)")
                .defineInRange("crimsonMosquitoCoolDown", 160, 0, 5000);
        BUILDER.pop();
        BUILDER.push("FrostStalker Spell");
        FrostStalkerSoulCost = BUILDER.comment("Soul cost of FrostStalker Servant spell (Default: 24)")
                .defineInRange("frostStalkerSoulCost", 24, 1, 100);
        FrostStalkerCastDuration = BUILDER.comment("Cast duration of FrostStalker Servant spell in ticks (Default: 60)")
                .defineInRange("frostStalkerCastDuration", 60, 0, 500);
        FrostStalkerSummonDown = BUILDER.comment("Summon down duration of FrostStalker Servant spell in ticks (Default: 80)")
                .defineInRange("frostStalkerSummonDown", 80, 0, 5000);
        FrostStalkerCoolDown = BUILDER.comment("Cooldown of FrostStalker Servant spell in ticks (Default: 100)")
                .defineInRange("frostStalkerCoolDown", 100, 0, 5000);
        BUILDER.pop();
        BUILDER.push("Roller Spell");
        RollerCost = BUILDER.comment("Soul cost of Rocky Roller Servant spell (Default: 24)")
                .defineInRange("rockyRollerSoulCost", 24, 1, 100);
        RollerDuration = BUILDER.comment("Cast duration of Rocky Roller Servant spell in ticks (Default: 60)")
                .defineInRange("rockyRollerCastDuration", 60, 0, 500);
        RollerSummonDown = BUILDER.comment("Summon down duration of Rocky Roller Servant spell in ticks (Default: 120)")
                .defineInRange("rockyRollerSummonDown", 120, 0, 5000);
        RollerCoolDown = BUILDER.comment("Cooldown of Rocky Roller Servant spell in ticks (Default: 100)")
                .defineInRange("rockyRollerCoolDown", 100, 0, 5000);
        BUILDER.pop();
        BUILDER.push("Skelewag Spell");
        SkelewagCost = BUILDER.comment("Soul cost of Skelewag Servant spell (Default: 16)")
                .defineInRange("skelewagSoulCost", 16, 1, 100);
        SkelewagDuration = BUILDER.comment("Cast duration of Skelewag Servant spell in ticks (Default: 40)")
                .defineInRange("skelewagCastDuration", 40, 0, 500);
        SkelewagSummonDown = BUILDER.comment("Summon down duration of Skelewag Servant spell in ticks (Default: 80)")
                .defineInRange("skelewagSummonDown", 80, 0, 5000);
        SkelewagCoolDown = BUILDER.comment("Cooldown of Skelewag Servant spell in ticks (Default: 60)")
                .defineInRange("skelewagCoolDown", 60, 0, 5000);
        BUILDER.pop();
        BUILDER.push("Guster Spell");
        GusterCost = BUILDER.comment("Soul cost of Guster Servant spell (Default: 8)")
                .defineInRange("gusterSoulCost", 8, 1, 100);
        GusterDuration = BUILDER.comment("Cast duration of Guster Servant spell in ticks (Default: 40)")
                .defineInRange("gusterCastDuration", 40, 0, 500);
        GusterSummonDown = BUILDER.comment("Summon down duration of Guster Servant spell in ticks (Default: 60)")
                .defineInRange("gusterSummonDown", 60, 0, 5000);
        GusterCoolDown = BUILDER.comment("Cooldown of Guster Servant spell in ticks (Default: 40)")
                .defineInRange("gusterCoolDown", 40, 0, 5000);
        BUILDER.pop();
        BUILDER.push("Centipede Spell");
        CentipedeCost = BUILDER.comment("Soul cost of Centipede Servant spell (Default: 16)")
                .defineInRange("centipedeSoulCost", 16, 1, 100);
        CentipedeDuration = BUILDER.comment("Cast duration of Centipede Servant spell in ticks (Default: 60)")
                .defineInRange("centipedeCastDuration", 60, 0, 500);
        CentipedeSummonDown = BUILDER.comment("Summon down duration of Centipede Servant spell in ticks (Default: 80)")
                .defineInRange("centipedeSummonDown", 80, 0, 5000);
        CentipedeCoolDown = BUILDER.comment("Cooldown of Centipede Servant spell in ticks (Default: 80)")
                .defineInRange("centipedeCoolDown", 80, 0, 5000);
        BUILDER.pop();
        BUILDER.push("Plunging Spell");
        DropBearSoulCost = BUILDER.comment("Soul cost of DropBear Servant spell (Default: 20)")
                .defineInRange("dropBearSoulCost", 20, 1, 100);
        DropBearCastDuration = BUILDER.comment("Cast duration of DropBear Servant spell in ticks (Default: 20)")
                .defineInRange("dropBearCastDuration", 20, 0, 500);
        DropBearSummonDown = BUILDER.comment("Summon down duration of DropBear Servant spell in ticks (Default: 100)")
                .defineInRange("dropBearSummonDown", 100, 0, 5000);
        DropBearCoolDown = BUILDER.comment("Cooldown of DropBear Servant spell in ticks (Default: 160)")
                .defineInRange("dropBearCoolDown", 160, 0, 5000);
        BUILDER.pop();
        BUILDER.push("Fart Spell");
        FartSoulCost = BUILDER.comment("Soul cost of Fart spell (Default: 10)")
                .defineInRange("fartSoulCost", 10, 1, 100);
        FartCastDuration = BUILDER.comment("Cast duration of Fart spell in ticks (Default: 60)")
                .defineInRange("fartCastDuration", 60, 0, 500);
        FartCoolDown = BUILDER.comment("Cooldown of Fart spell in ticks (Default: 100)")
                .defineInRange("fartCoolDown", 100, 0, 5000);
        BUILDER.pop();
        BUILDER.push("Vulture Spell");
        VultureSoulCost = BUILDER.comment("Soul cost of Soul Vulture Servant spell (Default: 24)")
                .defineInRange("vultureSoulCost", 24, 1, 100);
        VultureCastDuration = BUILDER.comment("Cast duration of Soul Vulture Servant spell in ticks (Default: 100)")
                .defineInRange("vultureCastDuration", 100, 0, 500);
        VultureCoolDown = BUILDER.comment("Cooldown of Soul Vulture Servant spell in ticks (Default: 100)")
                .defineInRange("vultureCoolDown", 100, 0, 5000);
        VultureSummonDown = BUILDER.comment("Summon down duration of Soul Vulture Servant spell in ticks (Default: 300)")
                .defineInRange("vultureSummonDown", 300, 0, 5000);
        BUILDER.pop();
        BUILDER.push("Farseer Spell");
        FarseerSoulCost = BUILDER.comment("Soul cost of Farseer Servant spell (Default: 128)")
                .defineInRange("farseerSoulCost", 128, 1, 128);
        FarseerCastDuration = BUILDER.comment("Cast duration of Farseer Servant spell in ticks (Default: 100)")
                .defineInRange("farseerCastDuration", 100, 0, 500);
        FarseerSummonDown = BUILDER.comment("Summon down duration of Farseer Servant spell in ticks (Default: 200)")
                .defineInRange("farseerSummonDown", 200, 0, 5000);
        FarseerCoolDown = BUILDER.comment("Cooldown of Farseer Servant spell in ticks (Default: 1200)")
                .defineInRange("farseerCoolDown", 1200, 0, 5000);
        BUILDER.pop();
        BUILDER.push("Void Crystal Spell");
        VoidShotSoulCost = BUILDER.comment("Soul cost of Void Shot spell (Default: 48)")
                .defineInRange("voidShotSoulCost", 48, 1, 100);
        VoidShotCoolDown = BUILDER.comment("Cooldown of Void Shot in ticks (Default: 160)")
                .defineInRange("voidShotCoolDown", 160, 0, 5000);
        VoidShotDamage = BUILDER.comment("Base damage of Void Shot crystal (Default: 8)")
                .defineInRange("voidShotDamage", 8.0D, 1.0D, 1000.0D);
        VoidShotCastUp = BUILDER.comment("Charge-up (wind-up) time of Void Shot in ticks, no crystals fire during this (Default: 40)")
                .defineInRange("voidShotCastUp", 40, 0, 500);
        VoidShotDuration = BUILDER.comment("Maximum channel duration of Void Shot in ticks (Default: 200)")
                .defineInRange("voidShotDuration", 200, 0, 5000);
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
