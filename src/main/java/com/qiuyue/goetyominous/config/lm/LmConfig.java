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
    public static final ForgeConfigSpec.ConfigValue<Double> OvergrownColossusServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> OvergrownColossusServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> OvergrownColossusServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> OvergrownColossusServantAttackKnockback;
    public static final ForgeConfigSpec.ConfigValue<Double> FrostbittenGolemServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> FrostbittenGolemServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> FrostbittenGolemServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> FrostbittenGolemServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> FrostbittenGolemServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> FrostbittenGolemServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> FrostbittenGolemServantAttackKnockback;
    public static final ForgeConfigSpec.ConfigValue<Double> AncientGuardianServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> AncientGuardianServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> AncientGuardianServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> AncientGuardianServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> AncientGuardianServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> AncientGuardianServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> AncientGuardianServantAttackKnockback;
    public static final ForgeConfigSpec.ConfigValue<Double> PossessedPaladinServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> PossessedPaladinServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> PossessedPaladinServantArmorToughness;
    public static final ForgeConfigSpec.ConfigValue<Double> PossessedPaladinServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> PossessedPaladinServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> PossessedPaladinServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> PossessedPaladinServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> PossessedPaladinServantAttackKnockback;
    public static final ForgeConfigSpec.ConfigValue<Double> PossessedPaladinServantDamageCap;
    public static final ForgeConfigSpec.ConfigValue<Double> HoveringHurricaneServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> HoveringHurricaneServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> HoveringHurricaneServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> HoveringHurricaneServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> HoveringHurricaneServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> HoveringHurricaneServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> HoveringHurricaneServantAttackKnockback;
    public static final ForgeConfigSpec.ConfigValue<Double> CloudGolemServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> CloudGolemServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> CloudGolemServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> CloudGolemServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> CloudGolemServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> CloudGolemServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> CloudGolemServantAttackKnockback;
    public static final ForgeConfigSpec.ConfigValue<Double> CloudGolemServantDamageCap;
    public static final ForgeConfigSpec.ConfigValue<Double> WanderingEyeServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> WanderingEyeServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> WanderingEyeServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> WanderingEyeServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> WanderingEyeServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> WanderingEyeServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> WanderingEyeServantAttackKnockback;
    public static final ForgeConfigSpec.ConfigValue<Double> ShulkerMimicServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> ShulkerMimicServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> ShulkerMimicServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> ShulkerMimicServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> ShulkerMimicServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> ShulkerMimicServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> ShulkerMimicServantAttackKnockback;
    public static final ForgeConfigSpec.ConfigValue<Double> AnnihilationPursuerServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> AnnihilationPursuerServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> AnnihilationPursuerServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> AnnihilationPursuerServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> AnnihilationPursuerServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> AnnihilationPursuerServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> AnnihilationPursuerServantAttackKnockback;
    public static final ForgeConfigSpec.ConfigValue<Double> TheObliteratorServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> TheObliteratorServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> TheObliteratorServantArmorToughness;
    public static final ForgeConfigSpec.ConfigValue<Double> TheObliteratorServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> TheObliteratorServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> TheObliteratorServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> TheObliteratorServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> TheObliteratorServantAttackKnockback;
    public static final ForgeConfigSpec.ConfigValue<Double> TheObliteratorServantDamageCap;
    public static final ForgeConfigSpec.ConfigValue<Integer> TheObliteratorServantAdaptationFactorMultiplier;
    public static final ForgeConfigSpec.ConfigValue<Double> FlameDrifterServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> FlameDrifterServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> FlameDrifterServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> FlameDrifterServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> FlameDrifterServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> FlameDrifterServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> FlameDrifterServantAttackKnockback;
    public static final ForgeConfigSpec.ConfigValue<Double> FlamebornWarriorServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> FlamebornWarriorServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> FlamebornWarriorServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> FlamebornWarriorServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> FlamebornWarriorServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> FlamebornWarriorServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> FlamebornWarriorServantAttackKnockback;
    public static final ForgeConfigSpec.ConfigValue<Double> FlamebornGuardServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> FlamebornGuardServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> FlamebornGuardServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> FlamebornGuardServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> FlamebornGuardServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> FlamebornGuardServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> FlamebornGuardServantAttackKnockback;
    public static final ForgeConfigSpec.ConfigValue<Double> BeheadedKnightServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> BeheadedKnightServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> BeheadedKnightServantArmorToughness;
    public static final ForgeConfigSpec.ConfigValue<Double> BeheadedKnightServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> BeheadedKnightServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> BeheadedKnightServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> BeheadedKnightServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> BeheadedKnightServantAttackKnockback;
    public static final ForgeConfigSpec.ConfigValue<Double> ResurrectedKnightServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> ResurrectedKnightServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> ResurrectedKnightServantArmorToughness;
    public static final ForgeConfigSpec.ConfigValue<Double> ResurrectedKnightServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> ResurrectedKnightServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> ResurrectedKnightServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> ResurrectedKnightServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> ResurrectedKnightServantAttackKnockback;
    public static final ForgeConfigSpec.ConfigValue<Double> EndersentServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> EndersentServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> EndersentServantArmorToughness;
    public static final ForgeConfigSpec.ConfigValue<Double> EndersentServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> EndersentServantTeleportSmashDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> EndersentServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> EndersentServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> EndersentServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> EndersentServantAttackKnockback;
    public static final ForgeConfigSpec.ConfigValue<Double> ChoruslingServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> ChoruslingServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> ChoruslingServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> ChoruslingServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> ChoruslingServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> ChoruslingServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> ChoruslingServantAttackKnockback;
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
    public static final ForgeConfigSpec.ConfigValue<Boolean> PossessedPaladinServantInvulnerabilityTime;
    public static final ForgeConfigSpec.ConfigValue<Integer> HoveringHurricaneServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Double> HoveringHurricaneServantDamageMultiplier;
    public static final ForgeConfigSpec.ConfigValue<Integer> CloudGolemServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Double> CloudGolemServantDamageMultiplier;
    public static final ForgeConfigSpec.ConfigValue<Boolean> CloudGolemServantLosesConsciousness;
    public static final ForgeConfigSpec.ConfigValue<Boolean> CloudGolemServantCanCharge2Times;
    public static final ForgeConfigSpec.ConfigValue<Boolean> CloudGolemServantInvulnerabilityTime;
    public static final ForgeConfigSpec.ConfigValue<Boolean> CloudGolemServantProjectileImmunityTimer;
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
    public static final ForgeConfigSpec.ConfigValue<Boolean> SoulFractureOnServantHit;
    public static final ForgeConfigSpec.ConfigValue<Boolean> LmServantScreenShake;

    static {
        BUILDER.push("Servants Attributes");
        BUILDER.push("Overgrown Colossus Servant");
        OvergrownColossusServantHealth = BUILDER.comment("How much Max Health Overgrown Colossus Servants have, Default: 170.0")
                .defineInRange("overgrownColossusServantHealth", 170.0, 1.0, Double.MAX_VALUE);
        OvergrownColossusServantArmor = BUILDER.comment("How much natural Armor Overgrown Colossus Servants have, Default: 10.0")
                .defineInRange("overgrownColossusServantArmor", 10.0, 0.0, Double.MAX_VALUE);
        OvergrownColossusServantDamage = BUILDER.comment("How much damage Overgrown Colossus Servants deal, Default: 14.0")
                .defineInRange("overgrownColossusServantDamage", 14.0, 1.0, Double.MAX_VALUE);
        OvergrownColossusServantMovementSpeed = BUILDER.comment("How fast Overgrown Colossus Servants move, Default: 0.3")
                .defineInRange("overgrownColossusServantMovementSpeed", 0.3, 0.0, Double.MAX_VALUE);
        OvergrownColossusServantFollowRange = BUILDER.comment("How much following/detection range Overgrown Colossus Servants have, Default: 30.0")
                .defineInRange("overgrownColossusServantFollowRange", 30.0, 1.0, Double.MAX_VALUE);
        OvergrownColossusServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Overgrown Colossus Servants have, Default: 1.0")
                .defineInRange("overgrownColossusServantKnockbackResistance", 1.0, 0.0, Double.MAX_VALUE);
        OvergrownColossusServantAttackKnockback = BUILDER.comment("How much Attack Knockback Overgrown Colossus Servants have, Default: 1.5")
                .defineInRange("overgrownColossusServantAttackKnockback", 1.5, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Frostbitten Golem Servant");
        FrostbittenGolemServantHealth = BUILDER.comment("How much Max Health Frostbitten Golem Servants have, Default: 220.0 (Legendary Monsters' value)")
                .defineInRange("frostbittenGolemServantHealth", 220.0, 1.0, Double.MAX_VALUE);
        FrostbittenGolemServantArmor = BUILDER.comment("How much natural Armor Frostbitten Golem Servants have, Default: 10.0 (Legendary Monsters' value)")
                .defineInRange("frostbittenGolemServantArmor", 10.0, 0.0, Double.MAX_VALUE);
        FrostbittenGolemServantDamage = BUILDER.comment("How much damage Frostbitten Golem Servants deal, Default: 16.0 (Legendary Monsters' value)")
                .defineInRange("frostbittenGolemServantDamage", 16.0, 1.0, Double.MAX_VALUE);
        FrostbittenGolemServantMovementSpeed = BUILDER.comment("How fast Frostbitten Golem Servants move, Default: 0.3 (Legendary Monsters' boss uses 0.1 with a x3.0 navigation modifier, giving the same 0.30 chase speed)")
                .defineInRange("frostbittenGolemServantMovementSpeed", 0.3, 0.0, Double.MAX_VALUE);
        FrostbittenGolemServantFollowRange = BUILDER.comment("How much following/detection range Frostbitten Golem Servants have, Default: 30.0 (Legendary Monsters' value)")
                .defineInRange("frostbittenGolemServantFollowRange", 30.0, 1.0, Double.MAX_VALUE);
        FrostbittenGolemServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Frostbitten Golem Servants have, Default: 1.0 (Legendary Monsters' value)")
                .defineInRange("frostbittenGolemServantKnockbackResistance", 1.0, 0.0, Double.MAX_VALUE);
        FrostbittenGolemServantAttackKnockback = BUILDER.comment("How much Attack Knockback Frostbitten Golem Servants have, Default: 1.0 (Legendary Monsters' value)")
                .defineInRange("frostbittenGolemServantAttackKnockback", 1.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Ancient Guardian Servant");
        AncientGuardianServantHealth = BUILDER.comment("How much Max Health Ancient Guardian Servants have, Default: 170.0 (Legendary Monsters' value)")
                .defineInRange("ancientGuardianServantHealth", 170.0, 1.0, Double.MAX_VALUE);
        AncientGuardianServantArmor = BUILDER.comment("How much natural Armor Ancient Guardian Servants have, Default: 13.0 (Legendary Monsters' value, raised to 20 when cracked)")
                .defineInRange("ancientGuardianServantArmor", 13.0, 0.0, Double.MAX_VALUE);
        AncientGuardianServantDamage = BUILDER.comment("How much damage Ancient Guardian Servants deal, Default: 20.0 (Legendary Monsters' value)")
                .defineInRange("ancientGuardianServantDamage", 20.0, 1.0, Double.MAX_VALUE);
        AncientGuardianServantMovementSpeed = BUILDER.comment("How fast Ancient Guardian Servants move, Default: 0.3 (Legendary Monsters' boss uses 0.1 with a x3.0 navigation modifier, giving the same 0.30 chase speed)")
                .defineInRange("ancientGuardianServantMovementSpeed", 0.3, 0.0, Double.MAX_VALUE);
        AncientGuardianServantFollowRange = BUILDER.comment("How much following/detection range Ancient Guardian Servants have, Default: 30.0 (Legendary Monsters' value)")
                .defineInRange("ancientGuardianServantFollowRange", 30.0, 1.0, Double.MAX_VALUE);
        AncientGuardianServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Ancient Guardian Servants have, Default: 1.0 (Legendary Monsters' value)")
                .defineInRange("ancientGuardianServantKnockbackResistance", 1.0, 0.0, Double.MAX_VALUE);
        AncientGuardianServantAttackKnockback = BUILDER.comment("How much Attack Knockback Ancient Guardian Servants have, Default: 1.0 (Legendary Monsters' value)")
                .defineInRange("ancientGuardianServantAttackKnockback", 1.0, 0.0, Double.MAX_VALUE);
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
        PossessedPaladinServantMovementSpeed = BUILDER.comment("How fast Possessed Paladin Servants move, Default: 0.3 (Legendary Monsters' boss uses 0.1 with a x3.0 navigation modifier, giving the same 0.30 chase speed; chase/follow/wander all use this value, same as Overgrown Colossus Servant)")
                .defineInRange("possessedPaladinServantMovementSpeed", 0.3, 0.0, Double.MAX_VALUE);
        PossessedPaladinServantFollowRange = BUILDER.comment("How much following/detection range Possessed Paladin Servants have, Default: 30.0 (same as Overgrown Colossus Servant; Legendary Monsters' boss value is 80.0)")
                .defineInRange("possessedPaladinServantFollowRange", 30.0, 1.0, Double.MAX_VALUE);
        PossessedPaladinServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Possessed Paladin Servants have, Default: 1.0 (Legendary Monsters' value)")
                .defineInRange("possessedPaladinServantKnockbackResistance", 1.0, 0.0, Double.MAX_VALUE);
        PossessedPaladinServantAttackKnockback = BUILDER.comment("How much Attack Knockback Possessed Paladin Servants have, Default: 1.0 (Legendary Monsters' value)")
                .defineInRange("possessedPaladinServantAttackKnockback", 1.0, 0.0, Double.MAX_VALUE);
        PossessedPaladinServantDamageCap = BUILDER.comment("Maximum damage a single hit can deal to Possessed Paladin Servants, Default: 21.0 (Legendary Monsters' own Possessed Paladin Damage Cap; bypassed by damage that ignores invulnerability)")
                .defineInRange("possessedPaladinServantDamageCap", 21.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Hovering Hurricane Servant");
        HoveringHurricaneServantHealth = BUILDER.comment("How much Max Health Hovering Hurricane Servants have, Default: 30.0")
                .defineInRange("hoveringHurricaneServantHealth", 30.0, 1.0, Double.MAX_VALUE);
        HoveringHurricaneServantArmor = BUILDER.comment("How much natural Armor Hovering Hurricane Servants have, Default: 5.0")
                .defineInRange("hoveringHurricaneServantArmor", 5.0, 0.0, Double.MAX_VALUE);
        HoveringHurricaneServantDamage = BUILDER.comment("How much damage Hovering Hurricane Servants deal, Default: 6.0")
                .defineInRange("hoveringHurricaneServantDamage", 6.0, 1.0, Double.MAX_VALUE);
        HoveringHurricaneServantMovementSpeed = BUILDER.comment("How fast Hovering Hurricane Servants move, Default: 0.1")
                .defineInRange("hoveringHurricaneServantMovementSpeed", 0.1, 0.0, Double.MAX_VALUE);
        HoveringHurricaneServantFollowRange = BUILDER.comment("How much following/detection range Hovering Hurricane Servants have, Default: 20.0")
                .defineInRange("hoveringHurricaneServantFollowRange", 20.0, 1.0, Double.MAX_VALUE);
        HoveringHurricaneServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Hovering Hurricane Servants have, Default: 1.0")
                .defineInRange("hoveringHurricaneServantKnockbackResistance", 1.0, 0.0, Double.MAX_VALUE);
        HoveringHurricaneServantAttackKnockback = BUILDER.comment("How much Attack Knockback Hovering Hurricane Servants have, Default: 0.5")
                .defineInRange("hoveringHurricaneServantAttackKnockback", 0.5, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Cloud Golem Servant");
        CloudGolemServantHealth = BUILDER.comment("How much Max Health Cloud Golem Servants have, Default: 350.0")
                .defineInRange("cloudGolemServantHealth", 350.0, 1.0, Double.MAX_VALUE);
        CloudGolemServantArmor = BUILDER.comment("How much natural Armor Cloud Golem Servants have, Default: 10.0")
                .defineInRange("cloudGolemServantArmor", 10.0, 0.0, Double.MAX_VALUE);
        CloudGolemServantDamage = BUILDER.comment("How much damage Cloud Golem Servants deal, Default: 0.0")
                .defineInRange("cloudGolemServantDamage", 0.0, 0.0, Double.MAX_VALUE);
        CloudGolemServantMovementSpeed = BUILDER.comment("How fast Cloud Golem Servants move, Default: 0.1")
                .defineInRange("cloudGolemServantMovementSpeed", 0.1, 0.0, Double.MAX_VALUE);
        CloudGolemServantFollowRange = BUILDER.comment("How much following/detection range Cloud Golem Servants have, Default: 90.0")
                .defineInRange("cloudGolemServantFollowRange", 90.0, 1.0, Double.MAX_VALUE);
        CloudGolemServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Cloud Golem Servants have, Default: 1.0")
                .defineInRange("cloudGolemServantKnockbackResistance", 1.0, 0.0, Double.MAX_VALUE);
        CloudGolemServantAttackKnockback = BUILDER.comment("How much Attack Knockback Cloud Golem Servants have, Default: 1.0")
                .defineInRange("cloudGolemServantAttackKnockback", 1.0, 0.0, Double.MAX_VALUE);
        CloudGolemServantDamageCap = BUILDER.comment("Maximum damage a single hit can deal to Cloud Golem Servants, Default: 18.0 (Legendary Monsters' own Cloud Golem Damage Cap; bypassed by damage that ignores invulnerability)")
                .defineInRange("cloudGolemServantDamageCap", 18.0, 0.0, 1000.0);
        BUILDER.pop();
        BUILDER.push("Wandering Eye Servant");
        WanderingEyeServantHealth = BUILDER.comment("How much Max Health Wandering Eye Servants have, Default: 30.0")
                .defineInRange("wanderingEyeServantHealth", 30.0, 1.0, Double.MAX_VALUE);
        WanderingEyeServantArmor = BUILDER.comment("How much natural Armor Wandering Eye Servants have, Default: 7.0")
                .defineInRange("wanderingEyeServantArmor", 7.0, 0.0, Double.MAX_VALUE);
        WanderingEyeServantDamage = BUILDER.comment("How much damage Wandering Eye Servants deal, Default: 10.0")
                .defineInRange("wanderingEyeServantDamage", 10.0, 1.0, Double.MAX_VALUE);
        WanderingEyeServantMovementSpeed = BUILDER.comment("How fast Wandering Eye Servants move, Default: 0.1")
                .defineInRange("wanderingEyeServantMovementSpeed", 0.1, 0.0, Double.MAX_VALUE);
        WanderingEyeServantFollowRange = BUILDER.comment("How much following/detection range Wandering Eye Servants have, Default: 20.0")
                .defineInRange("wanderingEyeServantFollowRange", 20.0, 1.0, Double.MAX_VALUE);
        WanderingEyeServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Wandering Eye Servants have, Default: 0.34754")
                .defineInRange("wanderingEyeServantKnockbackResistance", 0.34754D, 0.0, Double.MAX_VALUE);
        WanderingEyeServantAttackKnockback = BUILDER.comment("How much Attack Knockback Wandering Eye Servants have, Default: 0.5")
                .defineInRange("wanderingEyeServantAttackKnockback", 0.5, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Shulker Mimic Servant");
        ShulkerMimicServantHealth = BUILDER.comment("How much Max Health Shulker Mimic Servants have, Default: 200.0")
                .defineInRange("shulkerMimicServantHealth", 200.0, 1.0, Double.MAX_VALUE);
        ShulkerMimicServantArmor = BUILDER.comment("How much natural Armor Shulker Mimic Servants have, Default: 15.0")
                .defineInRange("shulkerMimicServantArmor", 15.0, 0.0, Double.MAX_VALUE);
        ShulkerMimicServantDamage = BUILDER.comment("How much damage Shulker Mimic Servants deal, Default: 12.0")
                .defineInRange("shulkerMimicServantDamage", 12.0, 0.0, Double.MAX_VALUE);
        ShulkerMimicServantMovementSpeed = BUILDER.comment("How fast Shulker Mimic Servants move, Default: 0.1")
                .defineInRange("shulkerMimicServantMovementSpeed", 0.1, 0.0, Double.MAX_VALUE);
        ShulkerMimicServantFollowRange = BUILDER.comment("How much following/detection range Shulker Mimic Servants have, Default: 30.0")
                .defineInRange("shulkerMimicServantFollowRange", 30.0, 1.0, Double.MAX_VALUE);
        ShulkerMimicServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Shulker Mimic Servants have, Default: 1.0")
                .defineInRange("shulkerMimicServantKnockbackResistance", 1.0, 0.0, Double.MAX_VALUE);
        ShulkerMimicServantAttackKnockback = BUILDER.comment("How much Attack Knockback Shulker Mimic Servants have, Default: 0.5")
                .defineInRange("shulkerMimicServantAttackKnockback", 0.5, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Annihilation Pursuer Servant");
        AnnihilationPursuerServantHealth = BUILDER.comment("How much Max Health Annihilation Pursuer Servants have, Default: 210.0")
                .defineInRange("annihilationPursuerServantHealth", 210.0, 1.0, Double.MAX_VALUE);
        AnnihilationPursuerServantArmor = BUILDER.comment("How much natural Armor Annihilation Pursuer Servants have, Default: 13.0")
                .defineInRange("annihilationPursuerServantArmor", 13.0, 0.0, Double.MAX_VALUE);
        AnnihilationPursuerServantDamage = BUILDER.comment("How much damage Annihilation Pursuer Servants deal, Default: 10.0")
                .defineInRange("annihilationPursuerServantDamage", 10.0, 0.0, Double.MAX_VALUE);
        AnnihilationPursuerServantMovementSpeed = BUILDER.comment("How fast Annihilation Pursuer Servants move, Default: 0.1")
                .defineInRange("annihilationPursuerServantMovementSpeed", 0.1, 0.0, Double.MAX_VALUE);
        AnnihilationPursuerServantFollowRange = BUILDER.comment("How much following/detection range Annihilation Pursuer Servants have, Default: 30.0")
                .defineInRange("annihilationPursuerServantFollowRange", 30.0, 1.0, Double.MAX_VALUE);
        AnnihilationPursuerServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Annihilation Pursuer Servants have, Default: 1.0")
                .defineInRange("annihilationPursuerServantKnockbackResistance", 1.0, 0.0, Double.MAX_VALUE);
        AnnihilationPursuerServantAttackKnockback = BUILDER.comment("How much Attack Knockback Annihilation Pursuer Servants have, Default: 0.5")
                .defineInRange("annihilationPursuerServantAttackKnockback", 0.5, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("The Obliterator Servant");
        TheObliteratorServantHealth = BUILDER.comment("How much Max Health The Obliterator Servants have, Default: 450.0 (Legendary Monsters' value)")
                .defineInRange("theObliteratorServantHealth", 450.0, 1.0, Double.MAX_VALUE);
        TheObliteratorServantArmor = BUILDER.comment("How much natural Armor The Obliterator Servants have, Default: 14.0 (Legendary Monsters' value)")
                .defineInRange("theObliteratorServantArmor", 14.0, 0.0, Double.MAX_VALUE);
        TheObliteratorServantArmorToughness = BUILDER.comment("How much Armor Toughness The Obliterator Servants have, Default: 4.0 (Legendary Monsters' value). Halved from phase 2 on, same as the boss")
                .defineInRange("theObliteratorServantArmorToughness", 4.0, 0.0, Double.MAX_VALUE);
        TheObliteratorServantDamage = BUILDER.comment("How much damage The Obliterator Servants deal, Default: 6.0 (Legendary Monsters' value). Most of its moves carry their own hardcoded damage")
                .defineInRange("theObliteratorServantDamage", 6.0, 0.0, Double.MAX_VALUE);
        TheObliteratorServantMovementSpeed = BUILDER.comment("How fast The Obliterator Servants move, Default: 0.36 (Legendary Monsters' boss uses 0.12 with a x3.0 navigation modifier, giving the same 0.36 chase speed)")
                .defineInRange("theObliteratorServantMovementSpeed", 0.36, 0.0, Double.MAX_VALUE);
        TheObliteratorServantFollowRange = BUILDER.comment("How much following/detection range The Obliterator Servants have, Default: 30.0 (Legendary Monsters' boss value is 60.0)")
                .defineInRange("theObliteratorServantFollowRange", 30.0, 1.0, Double.MAX_VALUE);
        TheObliteratorServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance The Obliterator Servants have, Default: 1.0 (Legendary Monsters' value)")
                .defineInRange("theObliteratorServantKnockbackResistance", 1.0, 0.0, Double.MAX_VALUE);
        TheObliteratorServantAttackKnockback = BUILDER.comment("How much Attack Knockback The Obliterator Servants have, Default: 0.5 (Legendary Monsters' value)")
                .defineInRange("theObliteratorServantAttackKnockback", 0.5, 0.0, Double.MAX_VALUE);
        TheObliteratorServantDamageCap = BUILDER.comment("Maximum damage a single hit can deal to The Obliterator Servants, Default: 20.0 (Legendary Monsters' own The Obliterator Damage Cap; bypassed by damage that ignores invulnerability)")
                .defineInRange("theObliteratorServantDamageCap", 20.0, 1.0, Double.MAX_VALUE);
        TheObliteratorServantAdaptationFactorMultiplier = BUILDER.comment("The bigger it is the stronger The Obliterator Servants' damage adaptation becomes, Default: 1 (Legendary Monsters' own The Obliterator Damage Adaptation Multiplier)")
                .defineInRange("theObliteratorServantAdaptationFactorMultiplier", 1, 0, 1000);
        BUILDER.pop();
        BUILDER.push("Flame Drifter Servant");
        FlameDrifterServantHealth = BUILDER.comment("How much Max Health Flame Drifter Servants have, Default: 60.0")
                .defineInRange("flameDrifterServantHealth", 60.0, 1.0, Double.MAX_VALUE);
        FlameDrifterServantArmor = BUILDER.comment("How much natural Armor Flame Drifter Servants have, Default: 8.0")
                .defineInRange("flameDrifterServantArmor", 8.0, 0.0, Double.MAX_VALUE);
        FlameDrifterServantDamage = BUILDER.comment("How much damage Flame Drifter Servants deal, Default: 10.0")
                .defineInRange("flameDrifterServantDamage", 10.0, 0.0, Double.MAX_VALUE);
        FlameDrifterServantMovementSpeed = BUILDER.comment("How fast Flame Drifter Servants move, Default: 0.1")
                .defineInRange("flameDrifterServantMovementSpeed", 0.1, 0.0, Double.MAX_VALUE);
        FlameDrifterServantFollowRange = BUILDER.comment("How much following/detection range Flame Drifter Servants have, Default: 20.0")
                .defineInRange("flameDrifterServantFollowRange", 20.0, 1.0, Double.MAX_VALUE);
        FlameDrifterServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Flame Drifter Servants have, Default: 1.0")
                .defineInRange("flameDrifterServantKnockbackResistance", 1.0, 0.0, Double.MAX_VALUE);
        FlameDrifterServantAttackKnockback = BUILDER.comment("How much Attack Knockback Flame Drifter Servants have, Default: 0.5")
                .defineInRange("flameDrifterServantAttackKnockback", 0.5, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Flameborn Warrior Servant");
        FlamebornWarriorServantHealth = BUILDER.comment("How much Max Health Flameborn Warrior Servants have, Default: 50.0")
                .defineInRange("flamebornWarriorServantHealth", 50.0, 1.0, Double.MAX_VALUE);
        FlamebornWarriorServantArmor = BUILDER.comment("How much natural Armor Flameborn Warrior Servants have, Default: 4.0")
                .defineInRange("flamebornWarriorServantArmor", 4.0, 0.0, Double.MAX_VALUE);
        FlamebornWarriorServantDamage = BUILDER.comment("How much damage Flameborn Warrior Servants deal, Default: 12.0")
                .defineInRange("flamebornWarriorServantDamage", 12.0, 0.0, Double.MAX_VALUE);
        FlamebornWarriorServantMovementSpeed = BUILDER.comment("How fast Flameborn Warrior Servants move, Default: 0.1")
                .defineInRange("flamebornWarriorServantMovementSpeed", 0.1, 0.0, Double.MAX_VALUE);
        FlamebornWarriorServantFollowRange = BUILDER.comment("How much following/detection range Flameborn Warrior Servants have, Default: 20.0")
                .defineInRange("flamebornWarriorServantFollowRange", 20.0, 1.0, Double.MAX_VALUE);
        FlamebornWarriorServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Flameborn Warrior Servants have, Default: 0.3")
                .defineInRange("flamebornWarriorServantKnockbackResistance", 0.3, 0.0, Double.MAX_VALUE);
        FlamebornWarriorServantAttackKnockback = BUILDER.comment("How much Attack Knockback Flameborn Warrior Servants have, Default: 0.5")
                .defineInRange("flamebornWarriorServantAttackKnockback", 0.5, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Flameborn Guard Servant");
        FlamebornGuardServantHealth = BUILDER.comment("How much Max Health Flameborn Guard Servants have, Default: 60.0")
                .defineInRange("flamebornGuardServantHealth", 60.0, 1.0, Double.MAX_VALUE);
        FlamebornGuardServantArmor = BUILDER.comment("How much natural Armor Flameborn Guard Servants have, Default: 8.0")
                .defineInRange("flamebornGuardServantArmor", 8.0, 0.0, Double.MAX_VALUE);
        FlamebornGuardServantDamage = BUILDER.comment("How much damage Flameborn Guard Servants deal, Default: 12.0")
                .defineInRange("flamebornGuardServantDamage", 12.0, 0.0, Double.MAX_VALUE);
        FlamebornGuardServantMovementSpeed = BUILDER.comment("How fast Flameborn Guard Servants move, Default: 0.1")
                .defineInRange("flamebornGuardServantMovementSpeed", 0.1, 0.0, Double.MAX_VALUE);
        FlamebornGuardServantFollowRange = BUILDER.comment("How much following/detection range Flameborn Guard Servants have, Default: 20.0")
                .defineInRange("flamebornGuardServantFollowRange", 20.0, 1.0, Double.MAX_VALUE);
        FlamebornGuardServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Flameborn Guard Servants have, Default: 1.0")
                .defineInRange("flamebornGuardServantKnockbackResistance", 1.0, 0.0, Double.MAX_VALUE);
        FlamebornGuardServantAttackKnockback = BUILDER.comment("How much Attack Knockback Flameborn Guard Servants have, Default: 0.5")
                .defineInRange("flamebornGuardServantAttackKnockback", 0.5, 0.0, Double.MAX_VALUE);
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
        BeheadedKnightServantMovementSpeed = BUILDER.comment("How fast Beheaded Knight Servants move, Default: 0.1 (Legendary Monsters' value)")
                .defineInRange("beheadedKnightServantMovementSpeed", 0.1, 0.0, Double.MAX_VALUE);
        BeheadedKnightServantFollowRange = BUILDER.comment("How much following/detection range Beheaded Knight Servants have, Default: 30.0 (Legendary Monsters' value)")
                .defineInRange("beheadedKnightServantFollowRange", 30.0, 1.0, Double.MAX_VALUE);
        BeheadedKnightServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Beheaded Knight Servants have, Default: 1.0 (Legendary Monsters' value)")
                .defineInRange("beheadedKnightServantKnockbackResistance", 1.0, 0.0, Double.MAX_VALUE);
        BeheadedKnightServantAttackKnockback = BUILDER.comment("How much Attack Knockback Beheaded Knight Servants have, Default: 0.5 (Legendary Monsters' value)")
                .defineInRange("beheadedKnightServantAttackKnockback", 0.5, 0.0, Double.MAX_VALUE);
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
        ResurrectedKnightServantMovementSpeed = BUILDER.comment("How fast Resurrected Knight Servants move, Default: 0.1 (Legendary Monsters' value)")
                .defineInRange("resurrectedKnightServantMovementSpeed", 0.1, 0.0, Double.MAX_VALUE);
        ResurrectedKnightServantFollowRange = BUILDER.comment("How much following/detection range Resurrected Knight Servants have, Default: 30.0 (Legendary Monsters' value)")
                .defineInRange("resurrectedKnightServantFollowRange", 30.0, 1.0, Double.MAX_VALUE);
        ResurrectedKnightServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Resurrected Knight Servants have, Default: 1.0 (Legendary Monsters' value)")
                .defineInRange("resurrectedKnightServantKnockbackResistance", 1.0, 0.0, Double.MAX_VALUE);
        ResurrectedKnightServantAttackKnockback = BUILDER.comment("How much Attack Knockback Resurrected Knight Servants have, Default: 0.5 (Legendary Monsters' value)")
                .defineInRange("resurrectedKnightServantAttackKnockback", 0.5, 0.0, Double.MAX_VALUE);
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
        EndersentServantMovementSpeed = BUILDER.comment("How fast Endersent Servants move, Default: 0.25 (Goety's value)")
                .defineInRange("endersentServantMovementSpeed", 0.25, 0.0, Double.MAX_VALUE);
        EndersentServantFollowRange = BUILDER.comment("How much following/detection range Endersent Servants have, Default: 32.0 (Goety's value)")
                .defineInRange("endersentServantFollowRange", 32.0, 1.0, Double.MAX_VALUE);
        EndersentServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Endersent Servants have, Default: 1.0 (Goety's value)")
                .defineInRange("endersentServantKnockbackResistance", 1.0, 0.0, Double.MAX_VALUE);
        EndersentServantAttackKnockback = BUILDER.comment("How much Attack Knockback Endersent Servants have, Default: 2.0 (Goety's value)")
                .defineInRange("endersentServantAttackKnockback", 2.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Chorusling Servant");
        ChoruslingServantHealth = BUILDER.comment("How much Max Health Chorusling Servants have, Default: 70.0 (Legendary Monsters' value)")
                .defineInRange("choruslingServantHealth", 70.0, 1.0, Double.MAX_VALUE);
        ChoruslingServantArmor = BUILDER.comment("How much natural Armor Chorusling Servants have, Default: 6.0 (Legendary Monsters' value)")
                .defineInRange("choruslingServantArmor", 6.0, 0.0, Double.MAX_VALUE);
        ChoruslingServantDamage = BUILDER.comment("How much damage Chorusling Servants deal, Default: 5.0 (Legendary Monsters' value)")
                .defineInRange("choruslingServantDamage", 5.0, 0.0, Double.MAX_VALUE);
        ChoruslingServantMovementSpeed = BUILDER.comment("How fast Chorusling Servants move, Default: 0.1 (Legendary Monsters' value)")
                .defineInRange("choruslingServantMovementSpeed", 0.1, 0.0, Double.MAX_VALUE);
        ChoruslingServantFollowRange = BUILDER.comment("How much following/detection range Chorusling Servants have, Default: 14.0 (Legendary Monsters' value)")
                .defineInRange("choruslingServantFollowRange", 14.0, 1.0, Double.MAX_VALUE);
        ChoruslingServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Chorusling Servants have, Default: 0.5 (Legendary Monsters' value)")
                .defineInRange("choruslingServantKnockbackResistance", 0.5, 0.0, Double.MAX_VALUE);
        ChoruslingServantAttackKnockback = BUILDER.comment("How much Attack Knockback Chorusling Servants have, Default: 0.5 (Legendary Monsters' value)")
                .defineInRange("choruslingServantAttackKnockback", 0.5, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.pop();
        BUILDER.push("Servant Limits");
        OvergrownColossusServantLimit = BUILDER
                .comment("Maximum number of Overgrown Colossus Servants that can be summoned (Default: 2)")
                .defineInRange("overgrownColossusServantLimit", 2, 1, 100);
        OvergrownColossusServantProjectileImmunity = BUILDER
                .comment("Whether Overgrown Colossus Servants are immune to arrows (Default: true")
                .define("overgrownColossusServantProjectileImmunity", true);
        FrostbittenGolemServantLimit = BUILDER
                .comment("Maximum number of Frostbitten Golem Servants that can be summoned (Default: 2)")
                .defineInRange("frostbittenGolemServantLimit", 2, 1, 100);
        FrostbittenGolemServantProjectileImmunity = BUILDER
                .comment("Whether Frostbitten Golem Servants are immune to arrows (Default: true)")
                .define("frostbittenGolemServantProjectileImmunity", true);
        FrostbittenGolemServantDamageMultiplier = BUILDER
                .comment(LM_DAMAGE_MULTIPLIER_COMMENT)
                .defineInRange("frostbittenGolemServantDamageMultiplier", 1.0, 0.0, Double.MAX_VALUE);
        AncientGuardianServantLimit = BUILDER
                .comment("Maximum number of Ancient Guardian Servants that can be summoned (Default: 2)")
                .defineInRange("ancientGuardianServantLimit", 2, 1, 100);
        AncientGuardianServantProjectileImmunity = BUILDER
                .comment("Whether Ancient Guardian Servants are immune to arrows (Default: true)")
                .define("ancientGuardianServantProjectileImmunity", true);
        AncientGuardianServantDamageMultiplier = BUILDER
                .comment(LM_DAMAGE_MULTIPLIER_COMMENT)
                .defineInRange("ancientGuardianServantDamageMultiplier", 1.0, 0.0, Double.MAX_VALUE);
        PossessedPaladinServantLimit = BUILDER
                .comment("Maximum number of Possessed Paladin Servants that can be summoned (Default: 1)")
                .defineInRange("possessedPaladinServantLimit", 1, 1, 100);
        PossessedPaladinServantDamageMultiplier = BUILDER
                .comment(LM_DAMAGE_MULTIPLIER_COMMENT)
                .defineInRange("possessedPaladinServantDamageMultiplier", 1.0, 0.0, Double.MAX_VALUE);
        PossessedPaladinServantInvulnerabilityTime = BUILDER
                .comment("Whether Possessed Paladin Servants get 10 ticks of invulnerability after being hit (Default: true)")
                .define("possessedPaladinServantInvulnerabilityTime", true);
        HoveringHurricaneServantLimit = BUILDER
                .comment("Maximum number of Hovering Hurricane Servants that can be summoned (Default: 8)")
                .defineInRange("hoveringHurricaneServantLimit", 8, 1, 100);
        HoveringHurricaneServantDamageMultiplier = BUILDER
                .comment(LM_DAMAGE_MULTIPLIER_COMMENT)
                .defineInRange("hoveringHurricaneServantDamageMultiplier", 1.0, 0.0, Double.MAX_VALUE);
        CloudGolemServantLimit = BUILDER
                .comment("Maximum number of Cloud Golem Servants that can be summoned (Default: 1)")
                .defineInRange("cloudGolemServantLimit", 1, 1, 100);
        CloudGolemServantDamageMultiplier = BUILDER
                .comment(LM_DAMAGE_MULTIPLIER_COMMENT)
                .defineInRange("cloudGolemServantDamageMultiplier", 1.0, 0.0, Double.MAX_VALUE);
        CloudGolemServantLosesConsciousness = BUILDER
                .comment("Whether Cloud Golem Servants go dormant after their laser attack (Default: true)")
                .define("cloudGolemServantLosesConsciousness", true);
        CloudGolemServantCanCharge2Times = BUILDER
                .comment("Whether Cloud Golem Servants charge one more time after missing while low on health (Default: true)")
                .define("cloudGolemServantCanCharge2Times", true);
        CloudGolemServantInvulnerabilityTime = BUILDER
                .comment("Whether Cloud Golem Servants get a short window of invulnerability after every hit they take (Default: true)")
                .define("cloudGolemServantInvulnerabilityTime", true);
        CloudGolemServantProjectileImmunityTimer = BUILDER
                .comment("Whether the arrows a Cloud Golem Servant shrugs off share a cooldown, so it cannot ignore an entire volley (Default: true)")
                .define("cloudGolemServantProjectileImmunityTimer", true);
        CloudGolemServantFallingCloudParticles = BUILDER
                .comment("Whether the clouds a Cloud Golem Servant drops keep their falling particles (Default: true)")
                .define("cloudGolemServantFallingCloudParticles", true);
        ShulkerMimicServantLimit = BUILDER
                .comment("Maximum number of Shulker Mimic Servants that can be summoned (Default: 1)")
                .defineInRange("shulkerMimicServantLimit", 1, 1, 100);
        ShulkerMimicServantDamageMultiplier = BUILDER
                .comment(LM_DAMAGE_MULTIPLIER_COMMENT)
                .defineInRange("shulkerMimicServantDamageMultiplier", 1.0, 0.0, Double.MAX_VALUE);
        AnnihilationPursuerServantLimit = BUILDER
                .comment("Maximum number of Annihilation Pursuer Servants that can be summoned (Default: 1)")
                .defineInRange("annihilationPursuerServantLimit", 1, 1, 100);
        AnnihilationPursuerServantDamageMultiplier = BUILDER
                .comment(LM_DAMAGE_MULTIPLIER_COMMENT)
                .defineInRange("annihilationPursuerServantDamageMultiplier", 1.0, 0.0, Double.MAX_VALUE);
        BeheadedKnightServantLimit = BUILDER
                .comment("Maximum number of Beheaded Knight Servants that can be summoned (Default: 2)")
                .defineInRange("beheadedKnightServantLimit", 2, 1, 100);
        BeheadedKnightServantDamageMultiplier = BUILDER
                .comment(LM_DAMAGE_MULTIPLIER_COMMENT)
                .defineInRange("beheadedKnightServantDamageMultiplier", 1.0, 0.0, Double.MAX_VALUE);
        ResurrectedKnightServantLimit = BUILDER
                .comment("Maximum number of Resurrected Knight Servants that can be summoned (Default: 2)")
                .defineInRange("resurrectedKnightServantLimit", 2, 1, 100);
        ResurrectedKnightServantDamageMultiplier = BUILDER
                .comment(LM_DAMAGE_MULTIPLIER_COMMENT)
                .defineInRange("resurrectedKnightServantDamageMultiplier", 1.0, 0.0, Double.MAX_VALUE);
        EndersentServantLimit = BUILDER
                .comment("Maximum number of Endersent Servants that can be summoned (Default: 2)")
                .defineInRange("endersentServantLimit", 2, 1, 100);
        EndersentServantDamageMultiplier = BUILDER
                .comment(LM_DAMAGE_MULTIPLIER_COMMENT)
                .defineInRange("endersentServantDamageMultiplier", 1.0, 0.0, Double.MAX_VALUE);
        ChoruslingServantLimit = BUILDER
                .comment("Maximum number of Chorusling Servants that can be summoned (Default: 4)")
                .defineInRange("choruslingServantLimit", 4, 1, 100);
        ChoruslingServantDamageMultiplier = BUILDER
                .comment(LM_DAMAGE_MULTIPLIER_COMMENT)
                .defineInRange("choruslingServantDamageMultiplier", 1.0, 0.0, Double.MAX_VALUE);
        TheObliteratorServantLimit = BUILDER
                .comment("Maximum number of The Obliterator Servants that can be summoned (Default: 1)")
                .defineInRange("theObliteratorServantLimit", 1, 1, 100);
        TheObliteratorServantDamageMultiplier = BUILDER
                .comment(LM_DAMAGE_MULTIPLIER_COMMENT)
                .defineInRange("theObliteratorServantDamageMultiplier", 1.0, 0.0, Double.MAX_VALUE);
        FlamebornGuardServantDamageMultiplier = BUILDER
                .comment(LM_DAMAGE_MULTIPLIER_COMMENT)
                .defineInRange("flamebornGuardServantDamageMultiplier", 1.0, 0.0, Double.MAX_VALUE);
        FlamebornWarriorServantDamageMultiplier = BUILDER
                .comment(LM_DAMAGE_MULTIPLIER_COMMENT)
                .defineInRange("flamebornWarriorServantDamageMultiplier", 1.0, 0.0, Double.MAX_VALUE);
        FlameDrifterServantDamageMultiplier = BUILDER
                .comment(LM_DAMAGE_MULTIPLIER_COMMENT)
                .defineInRange("flameDrifterServantDamageMultiplier", 1.0, 0.0, Double.MAX_VALUE);
        WanderingEyeServantDamageMultiplier = BUILDER
                .comment(LM_DAMAGE_MULTIPLIER_COMMENT)
                .defineInRange("wanderingEyeServantDamageMultiplier", 1.0, 0.0, Double.MAX_VALUE);
        TheObliteratorCloneBurstHpDamage = BUILDER
                .comment("Percentage of the victim's max health added on top of an armed clone's burst (Default: 7)",
                "Matches Legendary Monsters' own theObliteratorCloneBurstHPDamage, but only affects the servant's clones")
                .defineInRange("theObliteratorCloneBurstHpDamage", 7, 0, 1000);
        TheObliteratorServantFirstPhaseProjectileImmunity = BUILDER
                .comment("Whether The Obliterator Servants are immune to arrows and other projectiles while in their first phase (Default: true; Legendary Monsters' ON/OFF The Obliterator Projectile immunity in first Phase)")
                .define("theObliteratorServantFirstPhaseProjectileImmunity", true);
        SoulFractureOnServantHit = BUILDER
                .comment("Whether Beheaded Knight, Resurrected Knight and Possessed Paladin Servants apply Soul Fracture to the enemies they hit (Default: false)",
                "Soul Fracture is Legendary Monsters' stacking -20% max health debuff; only the attacks of these three servants ever apply it",
                "This never touches the original Legendary Monsters bosses")
                .define("soulFractureOnServantHit", false);
        BUILDER.pop();
        BUILDER.push("Legendary Monsters Servants");
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
