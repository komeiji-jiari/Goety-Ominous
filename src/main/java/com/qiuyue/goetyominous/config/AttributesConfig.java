package com.qiuyue.goetyominous.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.File;

public class AttributesConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Double> BreezeHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> BreezeArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> BreezeRangeDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> HurricaneHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> HurricaneArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> HurricanePunchDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> HurricaneSmashDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> HurricaneCycloneDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> MiredHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> MiredDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> MiredArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> BoggedHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> BoggedArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> BoggedDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> BoggedRangeDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> WargHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> WargArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> WargDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> CerberusHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> CerberusArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> CerberusDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> DredenHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> DredenDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> SunkenNecromancerHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> SunkenNecromancerArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> SunkenNecromancerDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> StormNecromancerHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> StormNecromancerArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> StormNecromancerDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> ReturnedHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> ReturnedArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> AgonyHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> AgonyDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> BeldamHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> ZealotHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> FanaticHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> FanaticDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> ThugHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> ThugDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> MartyrHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> ChannellerHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> ScorchHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> ScorchDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> UrbhadhachHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> UrbhadhachDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> ArchGeomancerHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> ArchGeomancerArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> ArchGeomancerArmorToughness;
    public static final ForgeConfigSpec.ConfigValue<Double> ArchGeomancerAttackDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> PiglinServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> PiglinServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> PiglinServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> PiglinBruteServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> PiglinBruteServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> PiglinBruteServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> PiglinBruteServantEvolvedHealthBonus;
    public static final ForgeConfigSpec.ConfigValue<Double> PiglinBruteServantEvolvedDamageBonus;
    public static final ForgeConfigSpec.ConfigValue<Double> PiglinBruteServantEvolved2HealthBonus;
    public static final ForgeConfigSpec.ConfigValue<Double> PiglinBruteServantEvolved2DamageBonus;
    public static final ForgeConfigSpec.ConfigValue<Double> PiglinHunterServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> PiglinHunterServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> PiglinHunterServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> PiglinHunterServantEvolvedHealthBonus;
    public static final ForgeConfigSpec.ConfigValue<Double> PiglinHunterServantEvolvedDamageBonus;
    public static final ForgeConfigSpec.ConfigValue<Double> PiglinHunterServantEvolved2HealthBonus;
    public static final ForgeConfigSpec.ConfigValue<Double> PiglinHunterServantEvolved2DamageBonus;
    public static final ForgeConfigSpec.ConfigValue<Double> FungusThrowerServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> FungusThrowerServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> FungusThrowerServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> CrimsonSpiderServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> CrimsonSpiderServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> AxolotlServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> AxolotlServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> DiscipleHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> DiscipleArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> DiscipleDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> HeresiarchServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> HeresiarchServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> HeresiarchServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> CrimsonGuardHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> CrimsonGuardArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> CrimsonGuardToughness;
    public static final ForgeConfigSpec.ConfigValue<Double> CrimsonGuardDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> CrimsonGuardSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> CrimsonGuardFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> CrimsonGuardKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> CrimsonGuardAttackKnockback;
    public static final ForgeConfigSpec.ConfigValue<Double> ImperialChasseurHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> ImperialChasseurArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> ImperialChasseurToughness;
    public static final ForgeConfigSpec.ConfigValue<Double> ImperialChasseurDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> ImperialChasseurRangeDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> ImperialChasseurSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> ImperialChasseurFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> ImperialChasseurKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> ImperialChasseurAttackKnockback;

    static {
        BUILDER.push("Servants Attributes");
        BUILDER.push("Breeze Servant");
        BreezeHealth = BUILDER.comment("How much Max Health Breezes have, Default: 30.0")
                .defineInRange("breezeHealth", 30.0, 1.0, Double.MAX_VALUE);
        BreezeArmor = BUILDER.comment("How much natural Armor Breezes have, Default: 0.0")
                .defineInRange("breezeArmor", 0.0, 0.0, Double.MAX_VALUE);
        BreezeRangeDamage = BUILDER.comment("How much damage a direct hit from a Breeze Servant's wind charge deals, Default: 3.0. Hostile Breezes keep vanilla's flat 1.0 direct hit; this value only feeds their ATTACK_DAMAGE attribute.")
                .defineInRange("breezeRangeDamage", 3.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Hurricane Servant");
        HurricaneHealth = BUILDER.comment("How much Max Health Hurricanes have, Default: 110.0")
                .defineInRange("hurricaneHealth", 110.0, 1.0, Double.MAX_VALUE);
        HurricaneArmor = BUILDER.comment("How much natural Armor Hurricanes have, Default: 0.0")
                .defineInRange("hurricaneArmor", 0.0, 0.0, Double.MAX_VALUE);
        HurricanePunchDamage = BUILDER.comment("How much damage the Hurricane's Zoom-Punch deals, Default: 15.0")
                .defineInRange("hurricanePunchDamage", 15.0, 1.0, Double.MAX_VALUE);
        HurricaneSmashDamage = BUILDER.comment("How much damage the Hurricane's landing smash deals, Default: 8.0")
                .defineInRange("hurricaneSmashDamage", 8.0, 1.0, Double.MAX_VALUE);
        HurricaneCycloneDamage = BUILDER.comment("How much damage the Hurricane's cyclone deals each time it strikes, Default: 2.0")
                .defineInRange("hurricaneCycloneDamage", 2.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Mired Servant");
        MiredHealth = BUILDER.comment("How much Max Health Mired and Mired Servants have, Default: 24.0")
                .defineInRange("miredHealth", 24.0, 1.0, Double.MAX_VALUE);
        MiredDamage = BUILDER.comment("How much damage Mired and Mired Servants deal, Default: 3.0")
                .defineInRange("miredDamage", 3.0, 1.0, Double.MAX_VALUE);
        MiredArmor = BUILDER.comment("How much natural Armor Mired and Mired Servants have, Default: 6.0")
                .defineInRange("miredArmor", 6.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Bogged Servant");
        BoggedHealth = BUILDER.comment("How much Max Health Bogged and Bogged Servants have, Default: 16.0")
                .defineInRange("boggedHealth", 16.0, 1.0, Double.MAX_VALUE);
        BoggedArmor = BUILDER.comment("How much natural Armor Bogged and Bogged Servants have, Default: 0.0")
                .defineInRange("boggedArmor", 0.0, 0.0, Double.MAX_VALUE);
        BoggedDamage = BUILDER.comment("How much melee damage Bogged and Bogged Servants deal, Default: 3.0")
                .defineInRange("boggedDamage", 3.0, 0.0, Double.MAX_VALUE);
        BoggedRangeDamage = BUILDER.comment("How much damage Bogged and Bogged Servant arrows deal, Default: 2.0")
                .defineInRange("boggedRangeDamage", 2.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Warg Servant");
        WargHealth = BUILDER.comment("How much Max Health Wargs have, Default: 50.0")
                .defineInRange("wargHealth", 50.0, 1.0, Double.MAX_VALUE);
        WargArmor = BUILDER.comment("How much armor Wargs have, Default: 0.0")
                .defineInRange("wargArmor", 0.0, 0.0, Double.MAX_VALUE);
        WargDamage = BUILDER.comment("How much damage Wargs deal, Default: 8.0")
                .defineInRange("wargDamage", 8.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Cerberus Servant");
        CerberusHealth = BUILDER.comment("How much Max Health Cerberuses have, Default: 100.0")
                .defineInRange("cerberusHealth", 100.0, 1.0, Double.MAX_VALUE);
        CerberusArmor = BUILDER.comment("How much natural Armor Cerberuses have, Default: 0.0")
                .defineInRange("cerberusArmor", 0.0, 0.0, Double.MAX_VALUE);
        CerberusDamage = BUILDER.comment("How much damage Cerberus melee attacks deal, Default: 8.0")
                .defineInRange("cerberusDamage", 8.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Dreden Servant");
        DredenHealth = BUILDER.comment("How much Max Health Dredens have, Default: 30.0")
                .defineInRange("dredenHealth", 30.0, 1.0, Double.MAX_VALUE);
        DredenDamage = BUILDER.comment("How much damage Dreden deals, Default: 2.0")
                .defineInRange("dredenDamage", 2.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Sunken Necromancer Servant");
        SunkenNecromancerHealth = BUILDER.comment("How much Max Health Sunken Necromancers have, Default: 50.0")
                .defineInRange("sunkenNecromancerHealth", 50.0, 1.0, Double.MAX_VALUE);
        SunkenNecromancerArmor = BUILDER.comment("How much natural Armor Sunken Necromancers have, Default: 5.0")
                .defineInRange("sunkenNecromancerArmor", 5.0, 0.0, Double.MAX_VALUE);
        SunkenNecromancerDamage = BUILDER.comment("How much damage Sunken Necromancers deals, Default: 5.0")
                .defineInRange("sunkenNecromancerDamage", 5.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Storm Necromancer Servant");
        StormNecromancerHealth = BUILDER.comment("How much Max Health Storm Necromancers have, Default: 220.0")
                .defineInRange("stormNecromancerHealth", 220.0, 1.0, Double.MAX_VALUE);
        StormNecromancerArmor = BUILDER.comment("How much natural Armor Storm Necromancers have, Default: 4.0")
                .defineInRange("stormNecromancerArmor", 4.0, 0.0, Double.MAX_VALUE);
        StormNecromancerDamage = BUILDER.comment("How much damage Storm Necromancers deals, Default: 3.0")
                .defineInRange("stormNecromancerDamage", 3.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Returned Servant");
        ReturnedHealth = BUILDER.comment("How much Max Health Returned have, Default: 10.0")
                .defineInRange("returnedHealth", 40.0, 1.0, Double.MAX_VALUE);
        ReturnedArmor = BUILDER.comment("How much natural Armor Returned have, Default: 2.0")
                .defineInRange("returnedArmor", 2.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Agony Servant");
        AgonyHealth = BUILDER.comment("How much Max Health Agony have, Default: 50.0")
                .defineInRange("agonyHealth", 50.0, 1.0, Double.MAX_VALUE);
        AgonyDamage = BUILDER.comment("How much damage Agony deals, Default: 9.0")
                .defineInRange("agonyDamage", 9.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Beldam Servant");
        BeldamHealth = BUILDER
                .comment("How much Max Health Beldam have, Default: 26.0")
                .defineInRange("beldamHealth", 26.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Zealot Servant");
        ZealotHealth = BUILDER
                .comment("How much Max Health Zealot have, Default: 24.0")
                .defineInRange("zealotHealth", 24.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Fanatic Servant");
        FanaticHealth = BUILDER
                .comment("How much Max Health Fanatic have, Default: 24.0")
                .defineInRange("fanaticHealth", 24.0, 1.0, Double.MAX_VALUE);
        FanaticDamage = BUILDER
                .comment("How much damage Fanatic deals, Default: 2.0")
                .defineInRange("fanaticDamage", 2.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Thug Servant");
        ThugHealth = BUILDER.comment("How much Max Health Thug have, Default: 50.0")
                .defineInRange("thugHealth", 50.0, 1.0, Double.MAX_VALUE);
        ThugDamage = BUILDER.comment("How much damage Thug deals, Default: 7.0")
                .defineInRange("thugDamage", 7.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Martyr Servant");
        MartyrHealth = BUILDER
                .comment("How much Max Health Martyr have, Default: 32.0")
                .defineInRange("martyrHealth", 32.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Channeller Servant");
        ChannellerHealth = BUILDER.comment("How much Max Health Channeller have, Default: 32.0")
                .defineInRange("channellerHealth", 32.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Scorch Servant");
        ScorchHealth = BUILDER.comment("How much Max Health Scorch have, Default: 14.0")
                .defineInRange("scorchHealth", 14.0, 1.0, Double.MAX_VALUE);
        ScorchDamage = BUILDER.comment("How much damage Scorch deals, Default: 6.0")
                .defineInRange("scorchDamage", 6.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Urbhadhach Servant");
        UrbhadhachHealth = BUILDER.comment("How much Max Health Urbhadhach have, Default: 50.0")
                .defineInRange("urbhadhachHealth", 50.0, 1.0, Double.MAX_VALUE);
        UrbhadhachDamage = BUILDER.comment("How much damage Urbhadhach deals, Default: 12.0")
                .defineInRange("urbhadhachDamage", 12.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Arch Geomancer Servant");
        ArchGeomancerHealth = BUILDER.comment("How much Max Health Arch Geomancers have, Default: 160.0")
                .defineInRange("archGeomancerHealth", 160.0, 1.0, Double.MAX_VALUE);
        ArchGeomancerArmor = BUILDER.comment("How much natural Armor Arch Geomancers have, Default: 4.0")
                .defineInRange("archGeomancerArmor", 4.0, 0.0, Double.MAX_VALUE);
        ArchGeomancerArmorToughness = BUILDER.comment("How much natural Armor Toughness Arch Geomancers have, Default: 0.0")
                .defineInRange("archGeomancerArmorToughness", 0.0, 0.0, Double.MAX_VALUE);
        ArchGeomancerAttackDamage = BUILDER.comment("How much damage Arch Geomancers deal, Default: 7.0")
                .defineInRange("archGeomancerAttackDamage", 7.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Piglin Servant");
        PiglinServantHealth = BUILDER.comment("How much Max Health Piglin Servants have, Default: 16.0")
                .defineInRange("piglinServantHealth", 16.0, 1.0, Double.MAX_VALUE);
        PiglinServantArmor = BUILDER.comment("How much natural Armor Piglin Servants have, Default: 0.0")
                .defineInRange("piglinServantArmor", 0.0, 0.0, Double.MAX_VALUE);
        PiglinServantDamage = BUILDER.comment("How much damage Piglin Servants deals, Default: 5.0")
                .defineInRange("piglinServantDamage", 5.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Piglin Brute Servant");
        PiglinBruteServantHealth = BUILDER.comment("How much Max Health Piglin Brute Servants have, Default: 50.0")
                .defineInRange("piglinBruteServantHealth", 50.0, 1.0, Double.MAX_VALUE);
        PiglinBruteServantArmor = BUILDER.comment("How much natural Armor Piglin Brute Servants have, Default: 0.0")
                .defineInRange("piglinBruteServantArmor", 0.0, 0.0, Double.MAX_VALUE);
        PiglinBruteServantDamage = BUILDER.comment("How much damage Piglin Brute Servants deals, Default: 7.0")
                .defineInRange("piglinBruteServantDamage", 7.0, 1.0, Double.MAX_VALUE);
        PiglinBruteServantEvolvedHealthBonus = BUILDER
                .comment("How much bonus Max Health Evolved Piglin Brute Servants gain, Default: 20.0")
                .defineInRange("piglinBruteServantEvolvedHealthBonus", 20.0, 0.0, Double.MAX_VALUE);
        PiglinBruteServantEvolvedDamageBonus = BUILDER
                .comment("How much bonus damage Evolved Piglin Brute Servants deal, Default: 3.0")
                .defineInRange("piglinBruteServantEvolvedDamageBonus", 3.0, 0.0, Double.MAX_VALUE);
        PiglinBruteServantEvolved2HealthBonus = BUILDER
                .comment("How much bonus Max Health Stage 2 Evolved Piglin Brute Servants gain, Default: 20.0")
                .defineInRange("piglinBruteServantEvolved2HealthBonus", 20.0, 0.0, Double.MAX_VALUE);
        PiglinBruteServantEvolved2DamageBonus = BUILDER
                .comment("How much bonus damage Stage 2 Evolved Piglin Brute Servants deal, Default: 3.0")
                .defineInRange("piglinBruteServantEvolved2DamageBonus", 3.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Piglin Hunter Servant");
        PiglinHunterServantHealth = BUILDER.comment("How much Max Health Piglin Hunter Servants have, Default: 50.0")
                .defineInRange("piglinHunterServantHealth", 50.0, 1.0, Double.MAX_VALUE);
        PiglinHunterServantArmor = BUILDER.comment("How much natural Armor Piglin Hunter Servants have, Default: 0.0")
                .defineInRange("piglinHunterServantArmor", 0.0, 0.0, Double.MAX_VALUE);
        PiglinHunterServantDamage = BUILDER.comment("How much damage Piglin Hunter Servants deal, Default: 7.0")
                .defineInRange("piglinHunterServantDamage", 7.0, 1.0, Double.MAX_VALUE);
        PiglinHunterServantEvolvedHealthBonus = BUILDER
                .comment("How much bonus Max Health Evolved Piglin Hunter Servants gain, Default: 20.0")
                .defineInRange("piglinHunterServantEvolvedHealthBonus", 20.0, 0.0, Double.MAX_VALUE);
        PiglinHunterServantEvolvedDamageBonus = BUILDER
                .comment("How much bonus damage Evolved Piglin Hunter Servants deal, Default: 3.0")
                .defineInRange("piglinHunterServantEvolvedDamageBonus", 3.0, 0.0, Double.MAX_VALUE);
        PiglinHunterServantEvolved2HealthBonus = BUILDER
                .comment("How much bonus Max Health Stage 2 Evolved Piglin Hunter Servants gain, Default: 20.0")
                .defineInRange("piglinHunterServantEvolved2HealthBonus", 20.0, 0.0, Double.MAX_VALUE);
        PiglinHunterServantEvolved2DamageBonus = BUILDER
                .comment("How much bonus damage Stage 2 Evolved Piglin Hunter Servants deal, Default: 3.0")
                .defineInRange("piglinHunterServantEvolved2DamageBonus", 3.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Fungus Thrower Servant");
        FungusThrowerServantHealth = BUILDER.comment("How much Max Health Fungus Thrower have, Default: 20.0")
                .defineInRange("fungusThrowerHealth", 16.0, 1.0, Double.MAX_VALUE);
        FungusThrowerServantArmor = BUILDER.comment("How much natural Armor Fungus Thrower have, Default: 0.0")
                .defineInRange("fungusThrowerArmor", 0.0, 0.0, Double.MAX_VALUE);
        FungusThrowerServantDamage = BUILDER.comment("How much damage Fungus Thrower deal, Default: 3.0")
                .defineInRange("fungusThrowerDamage", 2.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Crimson Spider Servant");
        CrimsonSpiderServantHealth = BUILDER
                .comment("How much Max Health Crimson Spider Servants have, Default: 24.0")
                .defineInRange("crimsonSpiderServantHealth", 24.0, 1.0, Double.MAX_VALUE);
        CrimsonSpiderServantDamage = BUILDER
                .comment("How much damage Crimson Spider Servants deal, Default: 5.0")
                .defineInRange("crimsonSpiderServantDamage", 5.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Axolotl Servant");
        AxolotlServantHealth = BUILDER.comment("How much Max Health Axolotl Servants have, Default: 14.0")
                .defineInRange("axolotlServantHealth", 14.0, 1.0, Double.MAX_VALUE);
        AxolotlServantDamage = BUILDER.comment("How much damage Axolotl Servants deals, Default: 2.0")
                .defineInRange("axolotlServantDamage", 2.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Disciple Servant");
        DiscipleHealth = BUILDER.comment("How much Max Health Disciples have, Default: 50.0")
                .defineInRange("discipleHealth", 50.0, 1.0, Double.MAX_VALUE);
        DiscipleArmor = BUILDER.comment("How much natural Armor Disciples have, Default: 2.0")
                .defineInRange("discipleArmor", 2.0, 0.0, Double.MAX_VALUE);
        DiscipleDamage = BUILDER.comment("How much damage Disciples deal, Default: 3.0")
                .defineInRange("discipleDamage", 3.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Heresiarch Servant");
        HeresiarchServantHealth = BUILDER.comment("How much Max Health Heresiarch Servants have, Default: 200.0")
                .defineInRange("heresiarchServantHealth", 130.0, 1.0, Double.MAX_VALUE);
        HeresiarchServantArmor = BUILDER.comment("How much natural Armor Heresiarch Servants have, Default: 10.0")
                .defineInRange("heresiarchServantArmor", 4.0, 0.0, Double.MAX_VALUE);
        HeresiarchServantDamage = BUILDER.comment("How much damage Heresiarch Servants deals, Default: 8.0")
                .defineInRange("heresiarchServantDamage", 2.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Crimson Guard");
        CrimsonGuardHealth = BUILDER.comment("How much Max Health Crimson Guards have, Default: 48.0")
                .defineInRange("crimsonGuardHealth", 48.0, 1.0, Double.MAX_VALUE);
        CrimsonGuardArmor = BUILDER.comment("How much natural Armor Crimson Guards have, Default: 18.0")
                .defineInRange("crimsonGuardArmor", 18.0, 0.0, Double.MAX_VALUE);
        CrimsonGuardToughness = BUILDER.comment("How much natural Armor Toughness Crimson Guards have, Default: 6.0")
                .defineInRange("crimsonGuardToughness", 6.0, 0.0, Double.MAX_VALUE);
        CrimsonGuardDamage = BUILDER.comment("How much damage Crimson Guards deal, Default: 8.0")
                .defineInRange("crimsonGuardDamage", 8.0, 1.0, Double.MAX_VALUE);
        CrimsonGuardSpeed = BUILDER.comment("How fast Crimson Guards move, Default: 0.28")
                .defineInRange("crimsonGuardSpeed", 0.28, 0.0, Double.MAX_VALUE);
        CrimsonGuardFollowRange = BUILDER.comment("How far Crimson Guards follow targets, Default: 16.0")
                .defineInRange("crimsonGuardFollowRange", 16.0, 0.0, Double.MAX_VALUE);
        CrimsonGuardKnockbackResistance = BUILDER.comment("How much knockback resistance Crimson Guards have, Default: 1.0")
                .defineInRange("crimsonGuardKnockbackResistance", 1.0, 0.0, 1.0);
        CrimsonGuardAttackKnockback = BUILDER.comment("How much knockback Crimson Guards deal, Default: 1.5")
                .defineInRange("crimsonGuardAttackKnockback", 1.5, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Imperial Chasseur");
        ImperialChasseurHealth = BUILDER.comment("How much Max Health Imperial Chasseurs have, Default: 48.0")
                .defineInRange("imperialChasseurHealth", 48.0D, 1.0D, Double.MAX_VALUE);
        ImperialChasseurArmor = BUILDER.comment("How much natural Armor Imperial Chasseurs have, Default: 12.0")
                .defineInRange("imperialChasseurArmor", 12.0D, 0.0D, Double.MAX_VALUE);
        ImperialChasseurToughness = BUILDER.comment("How much natural Armor Toughness Imperial Chasseurs have, Default: 2.0")
                .defineInRange("imperialChasseurToughness", 2.0D, 0.0D, Double.MAX_VALUE);
        ImperialChasseurDamage = BUILDER.comment("How much melee damage Imperial Chasseurs deal, Default: 10.0")
                .defineInRange("imperialChasseurDamage", 10.0D, 1.0D, Double.MAX_VALUE);
        ImperialChasseurRangeDamage = BUILDER.comment("Base damage of an Imperial Chasseur's heavy arrow, Default: 10.0")
                .defineInRange("imperialChasseurRangeDamage", 10.0D, 0.0D, Double.MAX_VALUE);
        ImperialChasseurSpeed = BUILDER.comment("How fast Imperial Chasseurs move, Default: 0.3")
                .defineInRange("imperialChasseurSpeed", 0.3D, 0.0D, Double.MAX_VALUE);
        ImperialChasseurFollowRange = BUILDER.comment("How far Imperial Chasseurs target enemies, Default: 48.0")
                .defineInRange("imperialChasseurFollowRange", 48.0D, 0.0D, Double.MAX_VALUE);
        ImperialChasseurKnockbackResistance = BUILDER.comment("How much knockback resistance Imperial Chasseurs have, Default: 1.0")
                .defineInRange("imperialChasseurKnockbackResistance", 1.0D, 0.0D, 1.0D);
        ImperialChasseurAttackKnockback = BUILDER.comment("How much knockback Imperial Chasseurs deal, Default: 2.0")
                .defineInRange("imperialChasseurAttackKnockback", 2.0D, 0.0D, Double.MAX_VALUE);
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
