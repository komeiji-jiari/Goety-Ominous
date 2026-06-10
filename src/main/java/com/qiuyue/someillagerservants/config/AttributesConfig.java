package com.qiuyue.someillagerservants.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.File;

public class AttributesConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Double> MagispellerServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> MagispellerServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> MagispellerServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> MagispellerServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> MagispellerServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> MagispellerServantArmorToughness;

    public static final ForgeConfigSpec.ConfigValue<Double> AbsorberServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> AbsorberServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> AbsorberServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> AbsorberServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> AbsorberServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> AbsorberServantAttackKnockback;

    public static final ForgeConfigSpec.ConfigValue<Double> TwittollagerServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> TwittollagerServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> TwittollagerServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> TwittollagerServantFollowRange;

    public static final ForgeConfigSpec.ConfigValue<Double> CrashagerServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> CrashagerServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> CrashagerServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> CrashagerServantFollowRange;

    public static final ForgeConfigSpec.ConfigValue<Double> KaboomerServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> KaboomerServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> KaboomerServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> KaboomerServantFollowRange;

    public static final ForgeConfigSpec.ConfigValue<Double> IllashooterServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> IllashooterServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> IllashooterServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> IllashooterServantFollowRange;

    public static final ForgeConfigSpec.ConfigValue<Double> DispenserServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> DispenserServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> DispenserServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> DispenserServantFollowRange;

    public static final ForgeConfigSpec.ConfigValue<Double> SunkenNecromancerHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> SunkenNecromancerDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> SunkenNecromancerArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> SunkenNecromancerFollowRange;

    public static final ForgeConfigSpec.ConfigValue<Double> AxolotlServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> AxolotlServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> AxolotlServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> AxolotlServantFollowRange;

    public static final ForgeConfigSpec.ConfigValue<Double> CreepieServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> CreepieServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> CreepieServantMovementSpeed;

    public static final ForgeConfigSpec.ConfigValue<Double> GrieferServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> GrieferServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> GrieferServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> GrieferServantFollowRange;

    public static final ForgeConfigSpec.ConfigValue<Double> ExecutionerServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> ExecutionerServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> ExecutionerServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> ExecutionerServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> ExecutionerServantArmor;

    public static final ForgeConfigSpec.ConfigValue<Double> SkeletonVillagerServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> SkeletonVillagerServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> SkeletonVillagerServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> SkeletonVillagerServantFollowRange;

    public static final ForgeConfigSpec.ConfigValue<Double> TricksterServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> TricksterServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> TricksterServantFollowRange;

    public static final ForgeConfigSpec.ConfigValue<Double> ThrasherServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> ThrasherServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> ThrasherServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> ThrasherServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> ThrasherServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> ThrasherServantArmor;

    public static final ForgeConfigSpec.ConfigValue<Double> GreatThrasherServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> GreatThrasherServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> GreatThrasherServantArmor;

    public static final ForgeConfigSpec.ConfigValue<Double> FlareServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> FlareServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> FlareServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> FlareServantFlyingSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> FlareServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> FlareServantFollowRange;

    public static final ForgeConfigSpec.ConfigValue<Double> HeresiarchServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> HeresiarchServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> HeresiarchServantDamage;

    public static final ForgeConfigSpec.ConfigValue<Double> AcolyteHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> AcolyteArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> AcolyteDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> AcolyteMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> AcolyteFollowRange;


    public static final ForgeConfigSpec.ConfigValue<Double> MutantWitherSkeletonServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantWitherSkeletonServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantWitherSkeletonServantArmorToughness;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantWitherSkeletonServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantWitherSkeletonServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantWitherSkeletonServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantWitherSkeletonServantAttackDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantWitherSkeletonServantAttackKnockback;

    public static final ForgeConfigSpec.ConfigValue<Double> MutantHoglinServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantHoglinServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantHoglinServantArmorToughness;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantHoglinServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantHoglinServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantHoglinServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantHoglinServantAttackDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantHoglinServantAttackKnockback;

    public static final ForgeConfigSpec.ConfigValue<Double> OvergrownColossusServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> OvergrownColossusServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> OvergrownColossusServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> OvergrownColossusServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> OvergrownColossusServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> OvergrownColossusServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> OvergrownColossusServantAttackKnockback;


    static {
        BUILDER.push("Servants Attributes");
        BUILDER.push("Necromancer");
        SunkenNecromancerHealth = BUILDER.comment("How much Max Health Sunken Necromancers have, Default: 50.0")
                .defineInRange("SunkenNecromancerHealth", 50.0, 1.0, Double.MAX_VALUE);
        SunkenNecromancerArmor = BUILDER.comment("How much natural Armor Sunken Necromancers have, Default: 5.0")
                .defineInRange("SunkenNecromancerArmor", 5.0, 0.0, Double.MAX_VALUE);
        SunkenNecromancerFollowRange = BUILDER.comment("How much following/detection range Sunken Necromancers have, Default: 16.0")
                .defineInRange("SunkenNecromancerFollowRange", 16.0, 1.0, 2048.0);
        SunkenNecromancerDamage = BUILDER.comment("How much damage Sunken Necromancers deals, Default: 5.0")
                .defineInRange("SunkenNecromancerDamage", 5.0, 1.0, Double.MAX_VALUE);

        BUILDER.pop();
        BUILDER.push("Magispeller Servant");
        MagispellerServantHealth = BUILDER.comment("How much Max Health Magispeller Servants have, Default: 250.0")
                .defineInRange("magispellerServantHealth", 250.0, 1.0, Double.MAX_VALUE);
        MagispellerServantDamage = BUILDER.comment("How much damage Magispeller Servants deals, Default: 5.0")
                .defineInRange("magispellerServantDamage", 5.0, 1.0, Double.MAX_VALUE);
        MagispellerServantMovementSpeed = BUILDER.comment("How fast Magispeller Servants move, Default: 0.35")
                .defineInRange("magispellerServantMovementSpeed", 0.35, 0.0, Double.MAX_VALUE);
        MagispellerServantFollowRange = BUILDER
                .comment("How much following/detection range Magispeller Servants have, Default: 96.0")
                .defineInRange("magispellerServantFollowRange", 96.0, 1.0, Double.MAX_VALUE);
        MagispellerServantArmor = BUILDER.comment("How much natural Armor Magispeller Servants have, Default: 0.0")
                .defineInRange("magispellerServantArmor", 0.0, 0.0, Double.MAX_VALUE);
        MagispellerServantArmorToughness = BUILDER
                .comment("How much natural Armor Toughness Magispeller Servants have, Default: 0.0")
                .defineInRange("magispellerServantArmorToughness", 0.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Absorber Servant");
        AbsorberServantHealth = BUILDER.comment("How much Max Health Absorber Servants have, Default: 100.0")
                .defineInRange("absorberServantHealth", 100.0, 1.0, Double.MAX_VALUE);
        AbsorberServantDamage = BUILDER.comment("How much damage Absorber Servants deals, Default: 15.0")
                .defineInRange("absorberServantDamage", 15.0, 1.0, Double.MAX_VALUE);
        AbsorberServantMovementSpeed = BUILDER.comment("How fast Absorber Servants move, Default: 0.15")
                .defineInRange("absorberServantMovementSpeed", 0.15, 0.0, Double.MAX_VALUE);
        AbsorberServantFollowRange = BUILDER
                .comment("How much following/detection range Absorber Servants have, Default: 32.0")
                .defineInRange("absorberServantFollowRange", 32.0, 1.0, Double.MAX_VALUE);
        AbsorberServantKnockbackResistance = BUILDER
                .comment("How much Knockback Resistance Absorber Servants have, Default: 1.0")
                .defineInRange("absorberServantKnockbackResistance", 1.0, 0.0, Double.MAX_VALUE);
        AbsorberServantAttackKnockback = BUILDER
                .comment("How much Attack Knockback Absorber Servants have, Default: 2.0")
                .defineInRange("absorberServantAttackKnockback", 2.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Twittollager Servant");
        TwittollagerServantHealth = BUILDER.comment("How much Max Health Twittollager Servants have, Default: 24.0")
                .defineInRange("twittollagerServantHealth", 24.0, 1.0, Double.MAX_VALUE);
        TwittollagerServantDamage = BUILDER.comment("How much damage Twittollager Servants deals, Default: 5.0")
                .defineInRange("twittollagerServantDamage", 5.0, 1.0, Double.MAX_VALUE);
        TwittollagerServantMovementSpeed = BUILDER.comment("How fast Twittollager Servants move, Default: 0.2")
                .defineInRange("twittollagerServantMovementSpeed", 0.2, 0.0, Double.MAX_VALUE);
        TwittollagerServantFollowRange = BUILDER
                .comment("How much following/detection range Twittollager Servants have, Default: 32.0")
                .defineInRange("twittollagerServantFollowRange", 32.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Crashager Servant");
        CrashagerServantHealth = BUILDER.comment("How much Max Health Crashager Servants have, Default: 500.0")
                .defineInRange("crashagerServantHealth", 500.0, 1.0, Double.MAX_VALUE);
        CrashagerServantDamage = BUILDER.comment("How much damage Crashager Servants deals, Default: 15.0")
                .defineInRange("crashagerServantDamage", 15.0, 1.0, Double.MAX_VALUE);
        CrashagerServantMovementSpeed = BUILDER.comment("How fast Crashager Servants move, Default: 0.8")
                .defineInRange("crashagerServantMovementSpeed", 0.8, 0.0, Double.MAX_VALUE);
        CrashagerServantFollowRange = BUILDER
                .comment("How much following/detection range Crashager Servants have, Default: 32.0")
                .defineInRange("crashagerServantFollowRange", 32.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Kaboomer Servant");
        KaboomerServantHealth = BUILDER.comment("How much Max Health Kaboomer Servants have, Default: 500.0")
                .defineInRange("kaboomerServantHealth", 500.0, 1.0, Double.MAX_VALUE);
        KaboomerServantDamage = BUILDER.comment("How much damage Kaboomer Servants deals, Default: 15.0")
                .defineInRange("kaboomerServantDamage", 15.0, 1.0, Double.MAX_VALUE);
        KaboomerServantMovementSpeed = BUILDER.comment("How fast Kaboomer Servants move, Default: 0.8")
                .defineInRange("kaboomerServantMovementSpeed", 0.8, 0.0, Double.MAX_VALUE);
        KaboomerServantFollowRange = BUILDER
                .comment("How much following/detection range Kaboomer Servants have, Default: 32.0")
                .defineInRange("kaboomerServantFollowRange", 32.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Illashooter Servant");
        IllashooterServantHealth = BUILDER.comment("How much Max Health Illashooter Servants have, Default: 2.0")
                .defineInRange("illashooterServantHealth", 2.0, 1.0, Double.MAX_VALUE);
        IllashooterServantDamage = BUILDER.comment("How much damage Illashooter Servants deals, Default: 0.0")
                .defineInRange("illashooterServantDamage", 0.0, 0.0, Double.MAX_VALUE);
        IllashooterServantMovementSpeed = BUILDER.comment("How fast Illashooter Servants move, Default: 0.4")
                .defineInRange("illashooterServantMovementSpeed", 0.4, 0.0, Double.MAX_VALUE);
        IllashooterServantFollowRange = BUILDER
                .comment("How much following/detection range Illashooter Servants have, Default: 32.0")
                .defineInRange("illashooterServantFollowRange", 32.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Dispenser Servant");
        DispenserServantHealth = BUILDER.comment("How much Max Health Dispenser Servants have, Default: 15.0")
                .defineInRange("dispenserServantHealth", 15.0, 1.0, Double.MAX_VALUE);
        DispenserServantDamage = BUILDER.comment("How much damage Dispenser Servants deals, Default: 0.0")
                .defineInRange("dispenserServantDamage", 0.0, 0.0, Double.MAX_VALUE);
        DispenserServantMovementSpeed = BUILDER.comment("How fast Dispenser Servants move, Default: 0.0")
                .defineInRange("dispenserServantMovementSpeed", 0.0, 0.0, Double.MAX_VALUE);
        DispenserServantFollowRange = BUILDER
                .comment("How much following/detection range Dispenser Servants have, Default: 32.0")
                .defineInRange("dispenserServantFollowRange", 32.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Axolotl Servant");
        AxolotlServantHealth = BUILDER.comment("How much Max Health Axolotl Servants have, Default: 15.0")
                .defineInRange("AxolotlServantHealth", 14.0, 1.0, Double.MAX_VALUE);
        AxolotlServantDamage = BUILDER.comment("How much damage Axolotl Servants deals, Default: 0.0")
                .defineInRange("AxolotlServantDamage", 3.0, 0.0, Double.MAX_VALUE);
        AxolotlServantMovementSpeed = BUILDER.comment("How fast Axolotl Servants move, Default: 0.0")
                .defineInRange("AxolotlServantMovementSpeed", 1.0, 0.0, Double.MAX_VALUE);
        AxolotlServantFollowRange = BUILDER
                .comment("How much following/detection range Axolotl Servants have, Default: 32.0")
                .defineInRange("AxolotlServantFollowRange", 16.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Acolyte");
        AcolyteHealth = BUILDER.comment("How much Max Health Acolytes have, Default: 50.0")
                .defineInRange("acolyteHealth", 50.0, 1.0, Double.MAX_VALUE);
        AcolyteArmor = BUILDER.comment("How much natural Armor Acolytes have, Default: 2.0")
                .defineInRange("acolyteArmor", 2.0, 0.0, Double.MAX_VALUE);
        AcolyteDamage = BUILDER.comment("How much damage Acolytes deal, Default: 3.0")
                .defineInRange("acolyteDamage", 3.0, 1.0, Double.MAX_VALUE);
        AcolyteMovementSpeed = BUILDER.comment("How fast Acolytes move, Default: 0.25")
                .defineInRange("acolyteMovementSpeed", 0.35, 0.0, Double.MAX_VALUE);
        AcolyteFollowRange = BUILDER.comment("How much following/detection range Acolytes have, Default: 32.0")
                .defineInRange("acolyteFollowRange", 32.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Heresiarch Servant");
        HeresiarchServantHealth = BUILDER.comment("How much Max Health Heresiarch Servants have, Default: 200.0")
                .defineInRange("heresiarchServantHealth", 130.0, 1.0, Double.MAX_VALUE);
        HeresiarchServantArmor = BUILDER.comment("How much natural Armor Heresiarch Servants have, Default: 10.0")
                .defineInRange("heresiarchServantArmor", 4.0, 0.0, Double.MAX_VALUE);
        HeresiarchServantDamage = BUILDER.comment("How much damage Heresiarch Servants deals, Default: 8.0")
                .defineInRange("heresiarchServantDamage", 2.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();

        BUILDER.push("SAR Servants (Optional)");
        CreepieServantHealth = BUILDER.comment("How much Max Health Creepie Servants have, Default: 5.0")
                .defineInRange("creepieServantHealth", 5.0, 1.0, Double.MAX_VALUE);
        CreepieServantDamage = BUILDER.comment("How much damage Creepie Servants deals, Default: 2.0")
                .defineInRange("creepieServantDamage", 2.0, 0.0, Double.MAX_VALUE);
        CreepieServantMovementSpeed = BUILDER.comment("How fast Creepie Servants move, Default: 0.35")
                .defineInRange("creepieServantMovementSpeed", 0.35, 0.0, Double.MAX_VALUE);
        GrieferServantHealth = BUILDER.comment("How much Max Health Griefer Servants have, Default: 24.0")
                .defineInRange("grieferServantHealth", 24.0, 1.0, Double.MAX_VALUE);
        GrieferServantDamage = BUILDER.comment("How much damage Griefer Servants deals, Default: 5.0")
                .defineInRange("grieferServantDamage", 5.0, 0.0, Double.MAX_VALUE);
        GrieferServantMovementSpeed = BUILDER.comment("How fast Griefer Servants move, Default: 0.35")
                .defineInRange("grieferServantMovementSpeed", 0.35, 0.0, Double.MAX_VALUE);
        GrieferServantFollowRange = BUILDER.comment("How much following/detection range Griefer Servants have, Default: 32.0")
                .defineInRange("grieferServantFollowRange", 32.0, 1.0, Double.MAX_VALUE);
        ExecutionerServantHealth = BUILDER.comment("How much Max Health Executioner Servants have, Default: 35.0")
                .defineInRange("executionerServantHealth", 35.0, 1.0, Double.MAX_VALUE);
        ExecutionerServantDamage = BUILDER.comment("How much damage Executioner Servants deals, Default: 8.0")
                .defineInRange("executionerServantDamage", 8.0, 0.0, Double.MAX_VALUE);
        ExecutionerServantMovementSpeed = BUILDER.comment("How fast Executioner Servants move, Default: 0.30")
                .defineInRange("executionerServantMovementSpeed", 0.30, 0.0, Double.MAX_VALUE);
        ExecutionerServantFollowRange = BUILDER.comment("How much following/detection range Executioner Servants have, Default: 14.0")
                .defineInRange("executionerServantFollowRange", 14.0, 1.0, Double.MAX_VALUE);
        ExecutionerServantArmor = BUILDER.comment("How much natural Armor Executioner Servants have, Default: 3.0")
                .defineInRange("executionerServantArmor", 3.0, 0.0, Double.MAX_VALUE);
        SkeletonVillagerServantHealth = BUILDER.comment("How much Max Health Skeleton Villager Servants have, Default: 20.0")
                .defineInRange("skeletonVillagerServantHealth", 20.0, 1.0, Double.MAX_VALUE);
        SkeletonVillagerServantDamage = BUILDER.comment("How much damage Skeleton Villager Servants deals, Default: 3.0")
                .defineInRange("skeletonVillagerServantDamage", 3.0, 0.0, Double.MAX_VALUE);
        SkeletonVillagerServantMovementSpeed = BUILDER.comment("How fast Skeleton Villager Servants move, Default: 0.25")
                .defineInRange("skeletonVillagerServantMovementSpeed", 0.25, 0.0, Double.MAX_VALUE);
        SkeletonVillagerServantFollowRange = BUILDER.comment("How much following/detection range Skeleton Villager Servants have, Default: 16.0")
                .defineInRange("skeletonVillagerServantFollowRange", 16.0, 1.0, Double.MAX_VALUE);
        TricksterServantHealth = BUILDER.comment("How much Max Health Trickster Servants have, Default: 24.0")
                .defineInRange("tricksterServantHealth", 24.0, 1.0, Double.MAX_VALUE);
        TricksterServantMovementSpeed = BUILDER.comment("How fast Trickster Servants move, Default: 0.5")
                .defineInRange("tricksterServantMovementSpeed", 0.5, 0.0, Double.MAX_VALUE);
        TricksterServantFollowRange = BUILDER.comment("How much following/detection range Trickster Servants have, Default: 16.0")
                .defineInRange("tricksterServantFollowRange", 16.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();

        BUILDER.push("UA Servants (Optional)");
        ThrasherServantHealth = BUILDER.comment("How much Max Health Thrasher Servants have, Default: 50.0")
                .defineInRange("thrasherServantHealth", 50.0, 1.0, Double.MAX_VALUE);
        ThrasherServantDamage = BUILDER.comment("How much damage Thrasher Servants deals, Default: 5.0")
                .defineInRange("thrasherServantDamage", 5.0, 0.0, Double.MAX_VALUE);
        ThrasherServantMovementSpeed = BUILDER.comment("How fast Thrasher Servants move, Default: 0.55")
                .defineInRange("thrasherServantMovementSpeed", 0.55, 0.0, Double.MAX_VALUE);
        ThrasherServantFollowRange = BUILDER.comment("How much following/detection range Thrasher Servants have, Default: 32.0")
                .defineInRange("thrasherServantFollowRange", 32.0, 1.0, Double.MAX_VALUE);
        ThrasherServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Thrasher Servants have, Default: 1.25")
                .defineInRange("thrasherServantKnockbackResistance", 1.25, 0.0, Double.MAX_VALUE);
        ThrasherServantArmor = BUILDER.comment("How much natural Armor Thrasher Servants have, Default: 8.0")
                .defineInRange("thrasherServantArmor", 8.0, 0.0, Double.MAX_VALUE);

        GreatThrasherServantHealth = BUILDER.comment("How much Max Health Great Thrasher Servants have, Default: 125.0")
                .defineInRange("greatThrasherServantHealth", 125.0, 1.0, Double.MAX_VALUE);
        GreatThrasherServantDamage = BUILDER.comment("How much damage Great Thrasher Servants deals, Default: 8.0")
                .defineInRange("greatThrasherServantDamage", 8.0, 0.0, Double.MAX_VALUE);
        GreatThrasherServantArmor = BUILDER.comment("How much natural Armor Great Thrasher Servants have, Default: 16.0")
                .defineInRange("greatThrasherServantArmor", 16.0, 0.0, Double.MAX_VALUE);

        FlareServantHealth = BUILDER.comment("How much Max Health Flare Servants have, Default: 20.0")
                .defineInRange("flareServantHealth", 20.0, 1.0, Double.MAX_VALUE);
        FlareServantDamage = BUILDER.comment("How much damage Flare Servants deals, Default: 8.0")
                .defineInRange("flareServantDamage", 8.0, 0.0, Double.MAX_VALUE);
        FlareServantArmor = BUILDER.comment("How much natural Armor Flare Servants have, Default: 2.0")
                .defineInRange("flareServantArmor", 2.0, 0.0, Double.MAX_VALUE);
        FlareServantFlyingSpeed = BUILDER.comment("How fast Flare Servants fly, Default: 0.6")
                .defineInRange("flareServantFlyingSpeed", 0.6, 0.0, Double.MAX_VALUE);
        FlareServantMovementSpeed = BUILDER.comment("How fast Flare Servants move, Default: 0.3")
                .defineInRange("flareServantMovementSpeed", 0.3, 0.0, Double.MAX_VALUE);
        FlareServantFollowRange = BUILDER.comment("How much following/detection range Flare Servants have, Default: 64.0")
                .defineInRange("flareServantFollowRange", 64.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();

        BUILDER.push("MutantMore Servants (Optional)");
        MutantWitherSkeletonServantHealth = BUILDER.comment("How much Max Health Mutant Wither Skeleton Servants have, Default: 200.0")
                .defineInRange("mutantWitherSkeletonServantHealth", 200.0, 1.0, Double.MAX_VALUE);
        MutantWitherSkeletonServantArmor = BUILDER.comment("How much natural Armor Mutant Wither Skeleton Servants have, Default: 6.0")
                .defineInRange("mutantWitherSkeletonServantArmor", 6.0, 0.0, Double.MAX_VALUE);
        MutantWitherSkeletonServantArmorToughness = BUILDER.comment("How much natural Armor Toughness Mutant Wither Skeleton Servants have, Default: 0")
                .defineInRange("mutantWitherSkeletonServantArmorToughness", 0.0, 0.0, Double.MAX_VALUE);
        MutantWitherSkeletonServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Mutant Wither Skeleton Servants have, Default: 1.0")
                .defineInRange("mutantWitherSkeletonServantKnockbackResistance", 1.0, 0.0, Double.MAX_VALUE);
        MutantWitherSkeletonServantFollowRange = BUILDER.comment("How much following/detection range Mutant Wither Skeleton Servants have, Default: 64.0")
                .defineInRange("mutantWitherSkeletonServantFollowRange", 64.0, 1.0, Double.MAX_VALUE);
        MutantWitherSkeletonServantMovementSpeed = BUILDER.comment("How fast Mutant Wither Skeleton Servants move, Default: 0.27")
                .defineInRange("mutantWitherSkeletonServantMovementSpeed", 0.27, 0.0, Double.MAX_VALUE);
        MutantWitherSkeletonServantAttackDamage = BUILDER.comment("How much damage Mutant Wither Skeleton Servants deal, Default: 8.0")
                .defineInRange("mutantWitherSkeletonServantAttackDamage", 8.0, 1.0, Double.MAX_VALUE);
        MutantWitherSkeletonServantAttackKnockback = BUILDER.comment("How much Attack Knockback Mutant Wither Skeleton Servants have, Default: 0.2")
                .defineInRange("mutantWitherSkeletonServantAttackKnockback", 0.2, 0.0, Double.MAX_VALUE);
        BUILDER.pop();

        BUILDER.push("Mutant Hoglin Servant");
        MutantHoglinServantHealth = BUILDER.comment("How much Max Health Mutant Hoglin Servants have, Default: 250.0")
                .defineInRange("mutantHoglinServantHealth", 250.0, 1.0, Double.MAX_VALUE);
        MutantHoglinServantArmor = BUILDER.comment("How much natural Armor Mutant Hoglin Servants have, Default: 12.0")
                .defineInRange("mutantHoglinServantArmor", 12.0, 0.0, Double.MAX_VALUE);
        MutantHoglinServantArmorToughness = BUILDER.comment("How much natural Armor Toughness Mutant Hoglin Servants have, Default: 0.0")
                .defineInRange("mutantHoglinServantArmorToughness", 0.0, 0.0, Double.MAX_VALUE);
        MutantHoglinServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Mutant Hoglin Servants have, Default: 1.0")
                .defineInRange("mutantHoglinServantKnockbackResistance", 1.0, 0.0, Double.MAX_VALUE);
        MutantHoglinServantFollowRange = BUILDER.comment("How much following/detection range Mutant Hoglin Servants have, Default: 64.0")
                .defineInRange("mutantHoglinServantFollowRange", 64.0, 1.0, Double.MAX_VALUE);
        MutantHoglinServantMovementSpeed = BUILDER.comment("How fast Mutant Hoglin Servants move, Default: 0.28")
                .defineInRange("mutantHoglinServantMovementSpeed", 0.28, 0.0, Double.MAX_VALUE);
        MutantHoglinServantAttackDamage = BUILDER.comment("How much damage Mutant Hoglin Servants deal, Default: 8.0")
                .defineInRange("mutantHoglinServantAttackDamage", 8.0, 1.0, Double.MAX_VALUE);
        MutantHoglinServantAttackKnockback = BUILDER.comment("How much Attack Knockback Mutant Hoglin Servants have, Default: 1.75")
                .defineInRange("mutantHoglinServantAttackKnockback", 1.75, 0.0, Double.MAX_VALUE);
        BUILDER.pop();

        BUILDER.push("Overgrown Colossus Servant (Optional - LM)");
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
