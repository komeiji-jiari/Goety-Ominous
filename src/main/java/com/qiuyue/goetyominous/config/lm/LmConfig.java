package com.qiuyue.goetyominous.config.lm;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.File;

public class LmConfig {
    private static final String[] LM_DAMAGE_MULTIPLIER_COMMENT = {
            "Damage multiplier for every single attack of this servant, including the ones fired by its projectiles (Default: 1.0)",
            "Every move keeps its own ratio from Legendary Monsters; this single value scales all of them at once",
            "Unlike Legendary Monsters' own *DamageMutliplier options, this one does not touch the original boss"
    };

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Double> OvergrownColossusServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> OvergrownColossusServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> OvergrownColossusServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> FrostbittenGolemServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> FrostbittenGolemServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> FrostbittenGolemServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> AncientGuardianServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> AncientGuardianServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> AncientGuardianServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> PossessedPaladinServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> PossessedPaladinServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> PossessedPaladinServantArmorToughness;
    public static final ForgeConfigSpec.ConfigValue<Double> PossessedPaladinServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> PossessedPaladinServantDamageCap;
    public static final ForgeConfigSpec.ConfigValue<Double> HoveringHurricaneServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> HoveringHurricaneServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> HoveringHurricaneServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> CloudGolemServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> CloudGolemServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> CloudGolemServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> CloudGolemServantDamageCap;
    public static final ForgeConfigSpec.ConfigValue<Double> WanderingEyeServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> WanderingEyeServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> WanderingEyeServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> ShulkerMimicServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> ShulkerMimicServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> ShulkerMimicServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> AnnihilationPursuerServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> AnnihilationPursuerServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> AnnihilationPursuerServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> TheObliteratorServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> TheObliteratorServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> TheObliteratorServantArmorToughness;
    public static final ForgeConfigSpec.ConfigValue<Double> TheObliteratorServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> TheObliteratorServantDamageCap;
    public static final ForgeConfigSpec.ConfigValue<Integer> TheObliteratorServantAdaptationFactorMultiplier;
    public static final ForgeConfigSpec.ConfigValue<Double> FlameDrifterServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> FlameDrifterServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> FlameDrifterServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> FlamebornWarriorServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> FlamebornWarriorServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> FlamebornWarriorServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> FlamebornGuardServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> FlamebornGuardServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> FlamebornGuardServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> BeheadedKnightServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> BeheadedKnightServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> BeheadedKnightServantArmorToughness;
    public static final ForgeConfigSpec.ConfigValue<Double> BeheadedKnightServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> ResurrectedKnightServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> ResurrectedKnightServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> ResurrectedKnightServantArmorToughness;
    public static final ForgeConfigSpec.ConfigValue<Double> ResurrectedKnightServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> EndersentServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> EndersentServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> EndersentServantArmorToughness;
    public static final ForgeConfigSpec.ConfigValue<Double> EndersentServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> EndersentServantTeleportSmashDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> ChoruslingServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> ChoruslingServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> ChoruslingServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Integer> OvergrownColossusServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Boolean> OvergrownColossusServantProjectileImmunity;
    public static final ForgeConfigSpec.ConfigValue<Integer> FrostbittenGolemServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Boolean> FrostbittenGolemServantProjectileImmunity;
    public static final ForgeConfigSpec.ConfigValue<Double> FrostbittenGolemServantDamageMultiplier;
    public static final ForgeConfigSpec.ConfigValue<Integer> AncientGuardianServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Boolean> AncientGuardianServantProjectileImmunity;
    public static final ForgeConfigSpec.ConfigValue<Double> AncientGuardianServantDamageMultiplier;
    public static final ForgeConfigSpec.ConfigValue<Integer> PossessedPaladinServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Double> PossessedPaladinServantDamageMultiplier;
    public static final ForgeConfigSpec.ConfigValue<Boolean> PossessedPaladinServantInvulnerability;
    public static final ForgeConfigSpec.ConfigValue<Integer> HoveringHurricaneServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Double> HoveringHurricaneServantDamageMultiplier;
    public static final ForgeConfigSpec.ConfigValue<Integer> CloudGolemServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Double> CloudGolemServantDamageMultiplier;
    public static final ForgeConfigSpec.ConfigValue<Boolean> CloudGolemServantLosesConsciousness;
    public static final ForgeConfigSpec.ConfigValue<Boolean> CloudGolemServantCanCharge2Times;
    public static final ForgeConfigSpec.ConfigValue<Boolean> CloudGolemServantInvulnerability;
    public static final ForgeConfigSpec.ConfigValue<Boolean> CloudGolemServantProjectileImmunity;
    public static final ForgeConfigSpec.ConfigValue<Boolean> CloudGolemServantFallingCloudParticles;
    public static final ForgeConfigSpec.ConfigValue<Integer> ShulkerMimicServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Double> ShulkerMimicServantDamageMultiplier;
    public static final ForgeConfigSpec.ConfigValue<Integer> AnnihilationPursuerServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Double> AnnihilationPursuerServantDamageMultiplier;
    public static final ForgeConfigSpec.ConfigValue<Integer> BeheadedKnightServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Double> BeheadedKnightServantDamageMultiplier;
    public static final ForgeConfigSpec.ConfigValue<Integer> ResurrectedKnightServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Double> ResurrectedKnightServantDamageMultiplier;
    public static final ForgeConfigSpec.ConfigValue<Integer> EndersentServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Double> EndersentServantDamageMultiplier;
    public static final ForgeConfigSpec.ConfigValue<Integer> ChoruslingServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Double> ChoruslingServantDamageMultiplier;
    public static final ForgeConfigSpec.ConfigValue<Integer> TheObliteratorServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Double> TheObliteratorServantDamageMultiplier;
    public static final ForgeConfigSpec.ConfigValue<Double> FlamebornGuardServantDamageMultiplier;
    public static final ForgeConfigSpec.ConfigValue<Double> FlamebornWarriorServantDamageMultiplier;
    public static final ForgeConfigSpec.ConfigValue<Double> FlameDrifterServantDamageMultiplier;
    public static final ForgeConfigSpec.ConfigValue<Double> WanderingEyeServantDamageMultiplier;
    public static final ForgeConfigSpec.ConfigValue<Integer> TheObliteratorCloneBurstHpDamage;
    public static final ForgeConfigSpec.ConfigValue<Boolean> TheObliteratorServantFirstPhaseProjectileImmunity;
    public static final ForgeConfigSpec.ConfigValue<Boolean> LmServantScreenShake;
    public static final ForgeConfigSpec.ConfigValue<Double> FracturedServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> FracturedServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> FracturedServantArmorToughness;
    public static final ForgeConfigSpec.ConfigValue<Double> FracturedServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> FracturedApostleServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> FracturedApostleServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> FracturedApostleServantArmorToughness;
    public static final ForgeConfigSpec.ConfigValue<Double> FracturedApostleServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> HauntedKnightServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> HauntedKnightServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> HauntedKnightServantArmorToughness;
    public static final ForgeConfigSpec.ConfigValue<Double> HauntedKnightServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> HauntedGuardServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> HauntedGuardServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> HauntedGuardServantArmorToughness;
    public static final ForgeConfigSpec.ConfigValue<Double> HauntedGuardServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Integer> FracturedServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Double> FracturedServantDamageMultiplier;
    public static final ForgeConfigSpec.ConfigValue<Integer> FracturedApostleServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Double> FracturedApostleServantDamageMultiplier;
    public static final ForgeConfigSpec.ConfigValue<Integer> HauntedKnightServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Double> HauntedKnightServantDamageMultiplier;
    public static final ForgeConfigSpec.ConfigValue<Integer> HauntedGuardServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Double> HauntedGuardServantDamageMultiplier;
    public static final ForgeConfigSpec.ConfigValue<Boolean> HauntedGuardServantProjectileImmunity;

    static {
        BUILDER.push("Servants Attributes");
        BUILDER.push("Overgrown Colossus Servant");
        OvergrownColossusServantHealth = BUILDER.comment("How much Max Health Overgrown Colossus Servants have, Default: 170.0")
                .defineInRange("overgrownColossusServantHealth", 170.0, 1.0, Double.MAX_VALUE);
        OvergrownColossusServantArmor = BUILDER.comment("How much natural Armor Overgrown Colossus Servants have, Default: 10.0")
                .defineInRange("overgrownColossusServantArmor", 10.0, 0.0, Double.MAX_VALUE);
        OvergrownColossusServantDamage = BUILDER.comment("How much damage Overgrown Colossus Servants deal, Default: 14.0")
                .defineInRange("overgrownColossusServantDamage", 14.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Frostbitten Golem Servant");
        FrostbittenGolemServantHealth = BUILDER.comment("How much Max Health Frostbitten Golem Servants have, Default: 220.0 (Legendary Monsters' value)")
                .defineInRange("frostbittenGolemServantHealth", 220.0, 1.0, Double.MAX_VALUE);
        FrostbittenGolemServantArmor = BUILDER.comment("How much natural Armor Frostbitten Golem Servants have, Default: 10.0 (Legendary Monsters' value)")
                .defineInRange("frostbittenGolemServantArmor", 10.0, 0.0, Double.MAX_VALUE);
        FrostbittenGolemServantDamage = BUILDER.comment("How much damage Frostbitten Golem Servants deal, Default: 16.0 (Legendary Monsters' value)")
                .defineInRange("frostbittenGolemServantDamage", 16.0, 1.0, Double.MAX_VALUE);
        FrostbittenGolemServantDamageMultiplier = BUILDER
                .comment(LM_DAMAGE_MULTIPLIER_COMMENT)
                .defineInRange("frostbittenGolemServantDamageMultiplier", 1.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Ancient Guardian Servant");
        AncientGuardianServantHealth = BUILDER.comment("How much Max Health Ancient Guardian Servants have, Default: 170.0 (Legendary Monsters' value)")
                .defineInRange("ancientGuardianServantHealth", 170.0, 1.0, Double.MAX_VALUE);
        AncientGuardianServantArmor = BUILDER.comment("How much natural Armor Ancient Guardian Servants have, Default: 13.0 (Legendary Monsters' value, raised to 20 when cracked)")
                .defineInRange("ancientGuardianServantArmor", 13.0, 0.0, Double.MAX_VALUE);
        AncientGuardianServantDamage = BUILDER.comment("How much damage Ancient Guardian Servants deal, Default: 20.0 (Legendary Monsters' value)")
                .defineInRange("ancientGuardianServantDamage", 20.0, 1.0, Double.MAX_VALUE);
        AncientGuardianServantDamageMultiplier = BUILDER
                .comment(LM_DAMAGE_MULTIPLIER_COMMENT)
                .defineInRange("ancientGuardianServantDamageMultiplier", 1.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Possessed Paladin Servant");
        PossessedPaladinServantHealth = BUILDER.comment("How much Max Health Possessed Paladin Servants have, Default: 400.0 (Legendary Monsters' value)")
                .defineInRange("possessedPaladinServantHealth", 400.0, 1.0, Double.MAX_VALUE);
        PossessedPaladinServantArmor = BUILDER.comment("How much natural Armor Possessed Paladin Servants have, Default: 12.0 (Legendary Monsters' value)")
                .defineInRange("possessedPaladinServantArmor", 12.0, 0.0, Double.MAX_VALUE);
        PossessedPaladinServantArmorToughness = BUILDER.comment("How much Armor Toughness Possessed Paladin Servants have, Default: 3.0 (Legendary Monsters' value)")
                .defineInRange("possessedPaladinServantArmorToughness", 3.0, 0.0, Double.MAX_VALUE);
        PossessedPaladinServantDamage = BUILDER.comment("How much damage Possessed Paladin Servants deal, Default: 15.0 (Legendary Monsters' value)")
                .defineInRange("possessedPaladinServantDamage", 15.0, 1.0, Double.MAX_VALUE);
        PossessedPaladinServantDamageCap = BUILDER.comment("Maximum damage a single hit can deal to Possessed Paladin Servants, Default: 21.0 (Legendary Monsters' own Possessed Paladin Damage Cap; bypassed by damage that ignores invulnerability)")
                .defineInRange("possessedPaladinServantDamageCap", 21.0, 1.0, Double.MAX_VALUE);
        PossessedPaladinServantDamageMultiplier = BUILDER
                .comment(LM_DAMAGE_MULTIPLIER_COMMENT)
                .defineInRange("possessedPaladinServantDamageMultiplier", 1.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Hovering Hurricane Servant");
        HoveringHurricaneServantHealth = BUILDER.comment("How much Max Health Hovering Hurricane Servants have, Default: 30.0")
                .defineInRange("hoveringHurricaneServantHealth", 30.0, 1.0, Double.MAX_VALUE);
        HoveringHurricaneServantArmor = BUILDER.comment("How much natural Armor Hovering Hurricane Servants have, Default: 5.0")
                .defineInRange("hoveringHurricaneServantArmor", 5.0, 0.0, Double.MAX_VALUE);
        HoveringHurricaneServantDamage = BUILDER.comment("How much damage Hovering Hurricane Servants deal, Default: 6.0")
                .defineInRange("hoveringHurricaneServantDamage", 6.0, 1.0, Double.MAX_VALUE);
        HoveringHurricaneServantDamageMultiplier = BUILDER
                .comment(LM_DAMAGE_MULTIPLIER_COMMENT)
                .defineInRange("hoveringHurricaneServantDamageMultiplier", 1.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Cloud Golem Servant");
        CloudGolemServantHealth = BUILDER.comment("How much Max Health Cloud Golem Servants have, Default: 350.0")
                .defineInRange("cloudGolemServantHealth", 350.0, 1.0, Double.MAX_VALUE);
        CloudGolemServantArmor = BUILDER.comment("How much natural Armor Cloud Golem Servants have, Default: 10.0")
                .defineInRange("cloudGolemServantArmor", 10.0, 0.0, Double.MAX_VALUE);
        CloudGolemServantDamage = BUILDER.comment("How much damage Cloud Golem Servants deal, Default: 0.0")
                .defineInRange("cloudGolemServantDamage", 0.0, 0.0, Double.MAX_VALUE);
        CloudGolemServantDamageCap = BUILDER.comment("Maximum damage a single hit can deal to Cloud Golem Servants, Default: 18.0 (Legendary Monsters' own Cloud Golem Damage Cap; bypassed by damage that ignores invulnerability)")
                .defineInRange("cloudGolemServantDamageCap", 18.0, 0.0, 1000.0);
        CloudGolemServantDamageMultiplier = BUILDER
                .comment(LM_DAMAGE_MULTIPLIER_COMMENT)
                .defineInRange("cloudGolemServantDamageMultiplier", 1.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Wandering Eye Servant");
        WanderingEyeServantHealth = BUILDER.comment("How much Max Health Wandering Eye Servants have, Default: 30.0")
                .defineInRange("wanderingEyeServantHealth", 30.0, 1.0, Double.MAX_VALUE);
        WanderingEyeServantArmor = BUILDER.comment("How much natural Armor Wandering Eye Servants have, Default: 7.0")
                .defineInRange("wanderingEyeServantArmor", 7.0, 0.0, Double.MAX_VALUE);
        WanderingEyeServantDamage = BUILDER.comment("How much damage Wandering Eye Servants deal, Default: 10.0")
                .defineInRange("wanderingEyeServantDamage", 10.0, 1.0, Double.MAX_VALUE);
        WanderingEyeServantDamageMultiplier = BUILDER
                .comment(LM_DAMAGE_MULTIPLIER_COMMENT)
                .defineInRange("wanderingEyeServantDamageMultiplier", 1.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Shulker Mimic Servant");
        ShulkerMimicServantHealth = BUILDER.comment("How much Max Health Shulker Mimic Servants have, Default: 200.0")
                .defineInRange("shulkerMimicServantHealth", 200.0, 1.0, Double.MAX_VALUE);
        ShulkerMimicServantArmor = BUILDER.comment("How much natural Armor Shulker Mimic Servants have, Default: 15.0")
                .defineInRange("shulkerMimicServantArmor", 15.0, 0.0, Double.MAX_VALUE);
        ShulkerMimicServantDamage = BUILDER.comment("How much damage Shulker Mimic Servants deal, Default: 12.0")
                .defineInRange("shulkerMimicServantDamage", 12.0, 0.0, Double.MAX_VALUE);
        ShulkerMimicServantDamageMultiplier = BUILDER
                .comment(LM_DAMAGE_MULTIPLIER_COMMENT)
                .defineInRange("shulkerMimicServantDamageMultiplier", 1.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Annihilation Pursuer Servant");
        AnnihilationPursuerServantHealth = BUILDER.comment("How much Max Health Annihilation Pursuer Servants have, Default: 210.0")
                .defineInRange("annihilationPursuerServantHealth", 210.0, 1.0, Double.MAX_VALUE);
        AnnihilationPursuerServantArmor = BUILDER.comment("How much natural Armor Annihilation Pursuer Servants have, Default: 13.0")
                .defineInRange("annihilationPursuerServantArmor", 13.0, 0.0, Double.MAX_VALUE);
        AnnihilationPursuerServantDamage = BUILDER.comment("How much damage Annihilation Pursuer Servants deal, Default: 10.0")
                .defineInRange("annihilationPursuerServantDamage", 10.0, 0.0, Double.MAX_VALUE);
        AnnihilationPursuerServantDamageMultiplier = BUILDER
                .comment(LM_DAMAGE_MULTIPLIER_COMMENT)
                .defineInRange("annihilationPursuerServantDamageMultiplier", 1.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("The Obliterator Servant");
        TheObliteratorServantHealth = BUILDER.comment("How much Max Health The Obliterator Servants have, Default: 550.0 (Legendary Monsters' value)")
                .defineInRange("theObliteratorServantHealth", 550.0, 1.0, Double.MAX_VALUE);
        TheObliteratorServantArmor = BUILDER.comment("How much natural Armor The Obliterator Servants have, Default: 14.0 (Legendary Monsters' value)")
                .defineInRange("theObliteratorServantArmor", 14.0, 0.0, Double.MAX_VALUE);
        TheObliteratorServantArmorToughness = BUILDER.comment("How much Armor Toughness The Obliterator Servants have, Default: 4.0 (Legendary Monsters' value). Halved from phase 2 on, same as the boss")
                .defineInRange("theObliteratorServantArmorToughness", 4.0, 0.0, Double.MAX_VALUE);
        TheObliteratorServantDamage = BUILDER.comment("How much damage The Obliterator Servants deal, Default: 6.0 (Legendary Monsters' value). Most of its moves carry their own hardcoded damage")
                .defineInRange("theObliteratorServantDamage", 6.0, 0.0, Double.MAX_VALUE);
        TheObliteratorServantDamageCap = BUILDER.comment("Maximum damage a single hit can deal to The Obliterator Servants, Default: 20.0 (Legendary Monsters' own The Obliterator Damage Cap; bypassed by damage that ignores invulnerability)")
                .defineInRange("theObliteratorServantDamageCap", 20.0, 1.0, Double.MAX_VALUE);
        TheObliteratorServantAdaptationFactorMultiplier = BUILDER.comment("The bigger it is the stronger The Obliterator Servants' damage adaptation becomes, Default: 1 (Legendary Monsters' own The Obliterator Damage Adaptation Multiplier)")
                .defineInRange("theObliteratorServantAdaptationFactorMultiplier", 1, 0, 1000);
        TheObliteratorServantDamageMultiplier = BUILDER
                .comment(LM_DAMAGE_MULTIPLIER_COMMENT)
                .defineInRange("theObliteratorServantDamageMultiplier", 1.0, 0.0, Double.MAX_VALUE);
        TheObliteratorCloneBurstHpDamage = BUILDER
                .comment("Percentage of the victim's max health added on top of an armed clone's burst (Default: 7)",
                "Matches Legendary Monsters' own theObliteratorCloneBurstHPDamage, but only affects the servant's clones")
                .defineInRange("theObliteratorCloneBurstHpDamage", 7, 0, 1000);
        BUILDER.pop();
        BUILDER.push("Flame Drifter Servant");
        FlameDrifterServantHealth = BUILDER.comment("How much Max Health Flame Drifter Servants have, Default: 60.0")
                .defineInRange("flameDrifterServantHealth", 60.0, 1.0, Double.MAX_VALUE);
        FlameDrifterServantArmor = BUILDER.comment("How much natural Armor Flame Drifter Servants have, Default: 8.0")
                .defineInRange("flameDrifterServantArmor", 8.0, 0.0, Double.MAX_VALUE);
        FlameDrifterServantDamage = BUILDER.comment("How much damage Flame Drifter Servants deal, Default: 10.0")
                .defineInRange("flameDrifterServantDamage", 10.0, 0.0, Double.MAX_VALUE);
        FlameDrifterServantDamageMultiplier = BUILDER
                .comment(LM_DAMAGE_MULTIPLIER_COMMENT)
                .defineInRange("flameDrifterServantDamageMultiplier", 1.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Flameborn Warrior Servant");
        FlamebornWarriorServantHealth = BUILDER.comment("How much Max Health Flameborn Warrior Servants have, Default: 50.0")
                .defineInRange("flamebornWarriorServantHealth", 50.0, 1.0, Double.MAX_VALUE);
        FlamebornWarriorServantArmor = BUILDER.comment("How much natural Armor Flameborn Warrior Servants have, Default: 4.0")
                .defineInRange("flamebornWarriorServantArmor", 4.0, 0.0, Double.MAX_VALUE);
        FlamebornWarriorServantDamage = BUILDER.comment("How much damage Flameborn Warrior Servants deal, Default: 12.0")
                .defineInRange("flamebornWarriorServantDamage", 12.0, 0.0, Double.MAX_VALUE);
        FlamebornWarriorServantDamageMultiplier = BUILDER
                .comment(LM_DAMAGE_MULTIPLIER_COMMENT)
                .defineInRange("flamebornWarriorServantDamageMultiplier", 1.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Flameborn Guard Servant");
        FlamebornGuardServantHealth = BUILDER.comment("How much Max Health Flameborn Guard Servants have, Default: 60.0")
                .defineInRange("flamebornGuardServantHealth", 60.0, 1.0, Double.MAX_VALUE);
        FlamebornGuardServantArmor = BUILDER.comment("How much natural Armor Flameborn Guard Servants have, Default: 8.0")
                .defineInRange("flamebornGuardServantArmor", 8.0, 0.0, Double.MAX_VALUE);
        FlamebornGuardServantDamage = BUILDER.comment("How much damage Flameborn Guard Servants deal, Default: 12.0")
                .defineInRange("flamebornGuardServantDamage", 12.0, 0.0, Double.MAX_VALUE);
        FlamebornGuardServantDamageMultiplier = BUILDER
                .comment(LM_DAMAGE_MULTIPLIER_COMMENT)
                .defineInRange("flamebornGuardServantDamageMultiplier", 1.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Beheaded Knight Servant");
        BeheadedKnightServantHealth = BUILDER.comment("How much Max Health Beheaded Knight Servants have, Default: 195.0 (Legendary Monsters' value)")
                .defineInRange("beheadedKnightServantHealth", 195.0, 1.0, Double.MAX_VALUE);
        BeheadedKnightServantArmor = BUILDER.comment("How much natural Armor Beheaded Knight Servants have, Default: 14.0 (Legendary Monsters' value)")
                .defineInRange("beheadedKnightServantArmor", 14.0, 0.0, Double.MAX_VALUE);
        BeheadedKnightServantArmorToughness = BUILDER.comment("How much Armor Toughness Beheaded Knight Servants have, Default: 4.0 (Legendary Monsters' value)")
                .defineInRange("beheadedKnightServantArmorToughness", 4.0, 0.0, Double.MAX_VALUE);
        BeheadedKnightServantDamage = BUILDER.comment("How much damage Beheaded Knight Servants deal, Default: 10.0 (Legendary Monsters' value)")
                .defineInRange("beheadedKnightServantDamage", 10.0, 0.0, Double.MAX_VALUE);
        BeheadedKnightServantDamageMultiplier = BUILDER
                .comment(LM_DAMAGE_MULTIPLIER_COMMENT)
                .defineInRange("beheadedKnightServantDamageMultiplier", 1.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Resurrected Knight Servant");
        ResurrectedKnightServantHealth = BUILDER.comment("How much Max Health Resurrected Knight Servants have, Default: 190.0 (Legendary Monsters' value)")
                .defineInRange("resurrectedKnightServantHealth", 190.0, 1.0, Double.MAX_VALUE);
        ResurrectedKnightServantArmor = BUILDER.comment("How much natural Armor Resurrected Knight Servants have, Default: 12.0 (Legendary Monsters' value)")
                .defineInRange("resurrectedKnightServantArmor", 12.0, 0.0, Double.MAX_VALUE);
        ResurrectedKnightServantArmorToughness = BUILDER.comment("How much Armor Toughness Resurrected Knight Servants have, Default: 2.0 (Legendary Monsters' value)")
                .defineInRange("resurrectedKnightServantArmorToughness", 2.0, 0.0, Double.MAX_VALUE);
        ResurrectedKnightServantDamage = BUILDER.comment("How much damage Resurrected Knight Servants deal, Default: 10.0 (Legendary Monsters' value)")
                .defineInRange("resurrectedKnightServantDamage", 10.0, 0.0, Double.MAX_VALUE);
        ResurrectedKnightServantDamageMultiplier = BUILDER
                .comment(LM_DAMAGE_MULTIPLIER_COMMENT)
                .defineInRange("resurrectedKnightServantDamageMultiplier", 1.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Endersent Servant");
        EndersentServantHealth = BUILDER.comment("How much Max Health Endersent Servants have, Default: 200.0 (Goety's value)")
                .defineInRange("endersentServantHealth", 200.0, 1.0, Double.MAX_VALUE);
        EndersentServantArmor = BUILDER.comment("How much natural Armor Endersent Servants have, Default: 0.0 (Goety's value)")
                .defineInRange("endersentServantArmor", 0.0, 0.0, Double.MAX_VALUE);
        EndersentServantArmorToughness = BUILDER.comment("How much Armor Toughness Endersent Servants have, Default: 0.0")
                .defineInRange("endersentServantArmorToughness", 0.0, 0.0, Double.MAX_VALUE);
        EndersentServantDamage = BUILDER.comment("How much damage Endersent Servants deal, Default: 14.0 (Goety's value)")
                .defineInRange("endersentServantDamage", 14.0, 0.0, Double.MAX_VALUE);
        EndersentServantTeleportSmashDamage = BUILDER.comment("How much damage the Endersent Servant's teleport smash deals, Default: 39.9")
                .defineInRange("endersentServantTeleportSmashDamage", 39.9, 0.0, Double.MAX_VALUE);
        EndersentServantDamageMultiplier = BUILDER
                .comment(LM_DAMAGE_MULTIPLIER_COMMENT)
                .defineInRange("endersentServantDamageMultiplier", 1.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Chorusling Servant");
        ChoruslingServantHealth = BUILDER.comment("How much Max Health Chorusling Servants have, Default: 70.0 (Legendary Monsters' value)")
                .defineInRange("choruslingServantHealth", 70.0, 1.0, Double.MAX_VALUE);
        ChoruslingServantArmor = BUILDER.comment("How much natural Armor Chorusling Servants have, Default: 6.0 (Legendary Monsters' value)")
                .defineInRange("choruslingServantArmor", 6.0, 0.0, Double.MAX_VALUE);
        ChoruslingServantDamage = BUILDER.comment("How much damage Chorusling Servants deal, Default: 5.0 (Legendary Monsters' value)")
                .defineInRange("choruslingServantDamage", 5.0, 0.0, Double.MAX_VALUE);
        ChoruslingServantDamageMultiplier = BUILDER
                .comment(LM_DAMAGE_MULTIPLIER_COMMENT)
                .defineInRange("choruslingServantDamageMultiplier", 1.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Fractured Servant");
        FracturedServantHealth = BUILDER.comment("How much Max Health Fractured Servants have, Default: 40.0 (Legendary Monsters' value)")
                .defineInRange("fracturedServantHealth", 40.0, 1.0, Double.MAX_VALUE);
        FracturedServantArmor = BUILDER.comment("How much natural Armor Fractured Servants have, Default: 18.0 (Legendary Monsters' value)")
                .defineInRange("fracturedServantArmor", 18.0, 0.0, Double.MAX_VALUE);
        FracturedServantArmorToughness = BUILDER.comment("How much Armor Toughness Fractured Servants have, Default: 10.0 (Legendary Monsters' value)")
                .defineInRange("fracturedServantArmorToughness", 10.0, 0.0, Double.MAX_VALUE);
        FracturedServantDamage = BUILDER.comment("How much damage Fractured Servants deal, Default: 11.0 (Legendary Monsters' value)")
                .defineInRange("fracturedServantDamage", 11.0, 0.0, Double.MAX_VALUE);
        FracturedServantDamageMultiplier = BUILDER
                .comment(LM_DAMAGE_MULTIPLIER_COMMENT)
                .defineInRange("fracturedServantDamageMultiplier", 1.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Fractured Apostle Servant");
        FracturedApostleServantHealth = BUILDER.comment("How much Max Health Fractured Apostle Servants have, Default: 50.0 (Legendary Monsters' value)")
                .defineInRange("fracturedApostleServantHealth", 50.0, 1.0, Double.MAX_VALUE);
        FracturedApostleServantArmor = BUILDER.comment("How much natural Armor Fractured Apostle Servants have, Default: 18.0 (Legendary Monsters' value)")
                .defineInRange("fracturedApostleServantArmor", 18.0, 0.0, Double.MAX_VALUE);
        FracturedApostleServantArmorToughness = BUILDER.comment("How much Armor Toughness Fractured Apostle Servants have, Default: 10.0 (Legendary Monsters' value)")
                .defineInRange("fracturedApostleServantArmorToughness", 10.0, 0.0, Double.MAX_VALUE);
        FracturedApostleServantDamage = BUILDER.comment("How much damage Fractured Apostle Servants deal, Default: 11.0 (Legendary Monsters' value)")
                .defineInRange("fracturedApostleServantDamage", 11.0, 0.0, Double.MAX_VALUE);
        FracturedApostleServantDamageMultiplier = BUILDER
                .comment(LM_DAMAGE_MULTIPLIER_COMMENT)
                .defineInRange("fracturedApostleServantDamageMultiplier", 1.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Haunted Knight Servant");
        HauntedKnightServantHealth = BUILDER.comment("How much Max Health Haunted Knight Servants have, Default: 50.0 (Legendary Monsters' value)")
                .defineInRange("hauntedKnightServantHealth", 50.0, 1.0, Double.MAX_VALUE);
        HauntedKnightServantArmor = BUILDER.comment("How much natural Armor Haunted Knight Servants have, Default: 18.0 (Legendary Monsters' value)")
                .defineInRange("hauntedKnightServantArmor", 18.0, 0.0, Double.MAX_VALUE);
        HauntedKnightServantArmorToughness = BUILDER.comment("How much Armor Toughness Haunted Knight Servants have, Default: 5.0 (Legendary Monsters' value)")
                .defineInRange("hauntedKnightServantArmorToughness", 5.0, 0.0, Double.MAX_VALUE);
        HauntedKnightServantDamage = BUILDER.comment("How much damage Haunted Knight Servants deal, Default: 11.0 (Legendary Monsters' value)")
                .defineInRange("hauntedKnightServantDamage", 11.0, 0.0, Double.MAX_VALUE);
        HauntedKnightServantDamageMultiplier = BUILDER
                .comment(LM_DAMAGE_MULTIPLIER_COMMENT)
                .defineInRange("hauntedKnightServantDamageMultiplier", 1.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Haunted Guard Servant");
        HauntedGuardServantHealth = BUILDER.comment("How much Max Health Haunted Guard Servants have, Default: 50.0 (Legendary Monsters' value)")
                .defineInRange("hauntedGuardServantHealth", 50.0, 1.0, Double.MAX_VALUE);
        HauntedGuardServantArmor = BUILDER.comment("How much natural Armor Haunted Guard Servants have, Default: 18.0 (Legendary Monsters' value)")
                .defineInRange("hauntedGuardServantArmor", 18.0, 0.0, Double.MAX_VALUE);
        HauntedGuardServantArmorToughness = BUILDER.comment("How much Armor Toughness Haunted Guard Servants have, Default: 10.0 (Legendary Monsters' value)")
                .defineInRange("hauntedGuardServantArmorToughness", 10.0, 0.0, Double.MAX_VALUE);
        HauntedGuardServantDamage = BUILDER.comment("How much damage Haunted Guard Servants deal, Default: 11.0 (Legendary Monsters' value)")
                .defineInRange("hauntedGuardServantDamage", 11.0, 0.0, Double.MAX_VALUE);
        HauntedGuardServantDamageMultiplier = BUILDER
                .comment(LM_DAMAGE_MULTIPLIER_COMMENT)
                .defineInRange("hauntedGuardServantDamageMultiplier", 1.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.pop();
        BUILDER.push("Servants Behavior");
        BUILDER.push("Overgrown Colossus Servant");
        OvergrownColossusServantProjectileImmunity = BUILDER
                .comment("Whether Overgrown Colossus Servants are immune to arrows (Default: true)")
                .define("overgrownColossusServantProjectileImmunity", true);
        BUILDER.pop();
        BUILDER.push("Frostbitten Golem Servant");
        FrostbittenGolemServantProjectileImmunity = BUILDER
                .comment("Whether Frostbitten Golem Servants are immune to arrows (Default: true)")
                .define("frostbittenGolemServantProjectileImmunity", true);
        BUILDER.pop();
        BUILDER.push("Ancient Guardian Servant");
        AncientGuardianServantProjectileImmunity = BUILDER
                .comment("Whether Ancient Guardian Servants are immune to arrows (Default: true)")
                .define("ancientGuardianServantProjectileImmunity", true);
        BUILDER.pop();
        BUILDER.push("Possessed Paladin Servant");
        PossessedPaladinServantInvulnerability = BUILDER
                .comment("Whether Possessed Paladin Servants get 10 ticks of invulnerability after being hit (Default: true)")
                .define("possessedPaladinServantInvulnerability", true);
        BUILDER.pop();
        BUILDER.push("Cloud Golem Servant");
        CloudGolemServantLosesConsciousness = BUILDER
                .comment("Whether Cloud Golem Servants go dormant after their laser attack (Default: true)")
                .define("cloudGolemServantLosesConsciousness", true);
        CloudGolemServantCanCharge2Times = BUILDER
                .comment("Whether Cloud Golem Servants charge one more time after missing while low on health (Default: true)")
                .define("cloudGolemServantCanCharge2Times", true);
        CloudGolemServantInvulnerability = BUILDER
                .comment("Whether Cloud Golem Servants get a short window of invulnerability after every hit they take (Default: true)")
                .define("cloudGolemServantInvulnerability", true);
        CloudGolemServantProjectileImmunity = BUILDER
                .comment("Whether the arrows a Cloud Golem Servant shrugs off share a cooldown, so it cannot ignore an entire volley (Default: true)")
                .define("cloudGolemServantProjectileImmunity", true);
        CloudGolemServantFallingCloudParticles = BUILDER
                .comment("Whether the clouds a Cloud Golem Servant drops keep their falling particles (Default: true)")
                .define("cloudGolemServantFallingCloudParticles", true);
        BUILDER.pop();
        BUILDER.push("The Obliterator Servant");
        TheObliteratorServantFirstPhaseProjectileImmunity = BUILDER
                .comment("Whether The Obliterator Servants are immune to arrows and other projectiles while in their first phase (Default: true; Legendary Monsters' ON/OFF The Obliterator Projectile immunity in first Phase)")
                .define("theObliteratorServantFirstPhaseProjectileImmunity", true);
        BUILDER.pop();
        BUILDER.push("Haunted Guard Servant");
        HauntedGuardServantProjectileImmunity = BUILDER
                .comment("Whether Haunted Guard Servants are immune to arrows (Default: true; Legendary Monsters' Guardprojectile)")
                .define("hauntedGuardServantProjectileImmunity", true);
        BUILDER.pop();
        BUILDER.pop();
        BUILDER.push("Servant Limits");
        OvergrownColossusServantLimit = BUILDER
                .comment("Maximum number of Overgrown Colossus Servants that can be summoned (Default: 2)")
                .defineInRange("overgrownColossusServantLimit", 2, 1, 100);
        FrostbittenGolemServantLimit = BUILDER
                .comment("Maximum number of Frostbitten Golem Servants that can be summoned (Default: 2)")
                .defineInRange("frostbittenGolemServantLimit", 2, 1, 100);
        AncientGuardianServantLimit = BUILDER
                .comment("Maximum number of Ancient Guardian Servants that can be summoned (Default: 2)")
                .defineInRange("ancientGuardianServantLimit", 2, 1, 100);
        PossessedPaladinServantLimit = BUILDER
                .comment("Maximum number of Possessed Paladin Servants that can be summoned (Default: 1)")
                .defineInRange("possessedPaladinServantLimit", 1, 1, 100);
        HoveringHurricaneServantLimit = BUILDER
                .comment("Maximum number of Hovering Hurricane Servants that can be summoned (Default: 8)")
                .defineInRange("hoveringHurricaneServantLimit", 8, 1, 100);
        CloudGolemServantLimit = BUILDER
                .comment("Maximum number of Cloud Golem Servants that can be summoned (Default: 1)")
                .defineInRange("cloudGolemServantLimit", 1, 1, 100);
        ShulkerMimicServantLimit = BUILDER
                .comment("Maximum number of Shulker Mimic Servants that can be summoned (Default: 1)")
                .defineInRange("shulkerMimicServantLimit", 1, 1, 100);
        AnnihilationPursuerServantLimit = BUILDER
                .comment("Maximum number of Annihilation Pursuer Servants that can be summoned (Default: 1)")
                .defineInRange("annihilationPursuerServantLimit", 1, 1, 100);
        BeheadedKnightServantLimit = BUILDER
                .comment("Maximum number of Beheaded Knight Servants that can be summoned (Default: 2)")
                .defineInRange("beheadedKnightServantLimit", 2, 1, 100);
        ResurrectedKnightServantLimit = BUILDER
                .comment("Maximum number of Resurrected Knight Servants that can be summoned (Default: 2)")
                .defineInRange("resurrectedKnightServantLimit", 2, 1, 100);
        EndersentServantLimit = BUILDER
                .comment("Maximum number of Endersent Servants that can be summoned (Default: 2)")
                .defineInRange("endersentServantLimit", 2, 1, 100);
        ChoruslingServantLimit = BUILDER
                .comment("Maximum number of Chorusling Servants that can be summoned (Default: 4)")
                .defineInRange("choruslingServantLimit", 4, 1, 100);
        TheObliteratorServantLimit = BUILDER
                .comment("Maximum number of The Obliterator Servants that can be summoned (Default: 1)")
                .defineInRange("theObliteratorServantLimit", 1, 1, 100);
        FracturedServantLimit = BUILDER
                .comment("Maximum number of Fractured Servants that can be summoned (Default: 4)")
                .defineInRange("fracturedServantLimit", 4, 1, 100);
        FracturedApostleServantLimit = BUILDER
                .comment("Maximum number of Fractured Apostle Servants that can be summoned (Default: 2)")
                .defineInRange("fracturedApostleServantLimit", 2, 1, 100);
        HauntedKnightServantLimit = BUILDER
                .comment("Maximum number of Haunted Knight Servants that can be summoned (Default: 2)")
                .defineInRange("hauntedKnightServantLimit", 2, 1, 100);
        HauntedGuardServantLimit = BUILDER
                .comment("Maximum number of Haunted Guard Servants that can be summoned (Default: 2)")
                .defineInRange("hauntedGuardServantLimit", 2, 1, 100);
        BUILDER.pop();
        BUILDER.push("Mechanics");
        LmServantScreenShake = BUILDER.comment("Whether Legendary Monsters Servants' attacks and abilities shake nearby players' cameras (Default: false)")
                .define("lmServantScreenShake", false);
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
