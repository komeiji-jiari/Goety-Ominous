package com.qiuyue.goetyominous.config.mm;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.File;

public class MmConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

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
    public static final ForgeConfigSpec.ConfigValue<Double> MutantShulkerServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantShulkerServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantShulkerServantArmorToughness;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantShulkerServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantShulkerServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantShulkerServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantBlazeServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantBlazeServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantBlazeServantArmorToughness;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantBlazeServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantBlazeServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantBlazeServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> RodlingServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> RodlingServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> RodlingServantArmorToughness;
    public static final ForgeConfigSpec.ConfigValue<Double> RodlingServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> RodlingServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> RodlingServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantJungleZombieServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantJungleZombieServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantJungleZombieServantArmorToughness;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantJungleZombieServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantJungleZombieServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantJungleZombieServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantJungleZombieServantAttackDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantHuskServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantHuskServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantHuskServantArmorToughness;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantHuskServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantHuskServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantHuskServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantFrozenZombieServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantFrozenZombieServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantFrozenZombieServantArmorToughness;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantFrozenZombieServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantFrozenZombieServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantFrozenZombieServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Integer> MutantWitherSkeletonServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> MutantHoglinServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> MutantShulkerServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> MutantShulkerServantMaxTraps;
    public static final ForgeConfigSpec.ConfigValue<Integer> MutantBlazeServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> RodlingServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> MutantJungleZombieServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> MutantHuskServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> MutantFrozenZombieServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> MWSSoulShieldHealthBouns;
    public static final ForgeConfigSpec.ConfigValue<Integer> MWSSoulShieldDamageBouns;
    public static final ForgeConfigSpec.ConfigValue<Integer> MWSHowlingSoulHealthBouns;
    public static final ForgeConfigSpec.ConfigValue<Integer> MWSHowlingSoulDamageBouns;
    public static final ForgeConfigSpec.ConfigValue<Integer> MWSUnholyBloodHealthBouns;
    public static final ForgeConfigSpec.ConfigValue<Integer> MWSUnholyBloodDamageBouns;
    public static final ForgeConfigSpec.ConfigValue<Integer> MHSoulJarHealthBouns;
    public static final ForgeConfigSpec.ConfigValue<Integer> MHSoulJarDamageBouns;
    public static final ForgeConfigSpec.ConfigValue<Integer> MHBlazingHelmHealthBouns;
    public static final ForgeConfigSpec.ConfigValue<Integer> MHBlazingHelmDamageBouns;
    public static final ForgeConfigSpec.ConfigValue<Integer> MHUnholyBloodHealthBouns;
    public static final ForgeConfigSpec.ConfigValue<Integer> MHUnholyBloodDamageBouns;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantShulkerVoidEchoHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantShulkerVoidEyeHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantShulkerVoidShardHealth;
    public static final ForgeConfigSpec.ConfigValue<Integer> MBBulwarkFocusHealthBouns;
    public static final ForgeConfigSpec.ConfigValue<Integer> MBBlazingHelmHealthBouns;
    public static final ForgeConfigSpec.ConfigValue<Integer> MBUnholyBloodHealthBouns;
    public static final ForgeConfigSpec.ConfigValue<Boolean> MBUnholyBloodTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> MBUnholyBloodLowHealthTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> MutantMoreServantScreenShake;
    public static final ForgeConfigSpec.ConfigValue<Boolean> MutantJungleZombieServantOriginalTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> MutantFrozenZombieServantOriginalTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> MutantHuskServantOriginalTexture;
    public static final ForgeConfigSpec.ConfigValue<Integer> HogChargeSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> HogChargeCooldown;
    public static final ForgeConfigSpec.ConfigValue<Double> HogChargeDamage;
    public static final ForgeConfigSpec.ConfigValue<Integer> WitherBreathSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> WitherBreathCooldown;
    public static final ForgeConfigSpec.ConfigValue<Integer> WitherSlashSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> WitherSlashCooldown;
    public static final ForgeConfigSpec.ConfigValue<Double> WitherSlashDamage;
    public static final ForgeConfigSpec.ConfigValue<Integer> GiantSnowballSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> GiantSnowballCooldown;
    public static final ForgeConfigSpec.ConfigValue<Double> GiantSnowballDamage;
    public static final ForgeConfigSpec.ConfigValue<Integer> GiantSnowballCastDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> IcicleSlamSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> IcicleSlamCooldown;
    public static final ForgeConfigSpec.ConfigValue<Integer> IcicleSlamCastDuration;
    public static final ForgeConfigSpec.ConfigValue<Double> IcicleSlamDamage;
    public static final ForgeConfigSpec.ConfigValue<Integer> IcicleSlamFreezingLength;
    public static final ForgeConfigSpec.ConfigValue<Integer> IcicleSlamFreezingLevel;
    public static final ForgeConfigSpec.ConfigValue<Boolean> IcicleSlamIgnoresInvulnerability;
    public static final ForgeConfigSpec.ConfigValue<Integer> IcicleSpikeSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> IcicleSpikeCooldown;
    public static final ForgeConfigSpec.ConfigValue<Integer> IcicleSpikeCastDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> ShulkerScatterSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> ShulkerScatterCooldown;
    public static final ForgeConfigSpec.ConfigValue<Integer> ShulkerScatterCastDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> ShulkerBulletSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> ShulkerBulletCooldown;
    public static final ForgeConfigSpec.ConfigValue<Integer> ShulkerBulletCastDuration;
    public static final ForgeConfigSpec.ConfigValue<Double> ShulkerBulletDamage;
    public static final ForgeConfigSpec.ConfigValue<Integer> RodStrikeSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> RodStrikeCooldown;
    public static final ForgeConfigSpec.ConfigValue<Integer> RodStrikeCastDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> RodStrikeSummonDown;
    public static final ForgeConfigSpec.ConfigValue<Double> WitherScytheDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> WitherScytheAttackSpeed;

    static {
        BUILDER.push("Servants Attributes");
        BUILDER.push("Mutant Wither Skeleton Servant");
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
        BUILDER.push("Mutant Shulker Servant");
        MutantShulkerServantHealth = BUILDER.comment("How much Max Health Mutant Shulker Servants have, Default: 300.0")
                .defineInRange("mutantShulkerServantHealth", 300.0, 1.0, Double.MAX_VALUE);
        MutantShulkerServantArmor = BUILDER.comment("How much natural Armor Mutant Shulker Servants have, Default: 10.0")
                .defineInRange("mutantShulkerServantArmor", 10.0, 0.0, Double.MAX_VALUE);
        MutantShulkerServantArmorToughness = BUILDER.comment("How much natural Armor Toughness Mutant Shulker Servants have, Default: 0.0")
                .defineInRange("mutantShulkerServantArmorToughness", 0.0, 0.0, Double.MAX_VALUE);
        MutantShulkerServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Mutant Shulker Servants have, Default: 1.0")
                .defineInRange("mutantShulkerServantKnockbackResistance", 1.0, 0.0, Double.MAX_VALUE);
        MutantShulkerServantFollowRange = BUILDER.comment("How much following/detection range Mutant Shulker Servants have, Default: 64.0")
                .defineInRange("mutantShulkerServantFollowRange", 64.0, 1.0, Double.MAX_VALUE);
        MutantShulkerServantMovementSpeed = BUILDER.comment("How fast Mutant Shulker Servants move, Default: 0.275")
                .defineInRange("mutantShulkerServantMovementSpeed", 0.275, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Mutant Blaze Servant");
        MutantBlazeServantHealth = BUILDER.comment("How much Max Health Mutant Blaze Servants have, Default: 250.0")
                .defineInRange("mutantBlazeServantHealth", 250.0, 1.0, Double.MAX_VALUE);
        MutantBlazeServantArmor = BUILDER.comment("How much natural Armor Mutant Blaze Servants have, Default: 5.0")
                .defineInRange("mutantBlazeServantArmor", 5.0, 0.0, Double.MAX_VALUE);
        MutantBlazeServantArmorToughness = BUILDER.comment("How much natural Armor Toughness Mutant Blaze Servants have, Default: 0.0")
                .defineInRange("mutantBlazeServantArmorToughness", 0.0, 0.0, Double.MAX_VALUE);
        MutantBlazeServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Mutant Blaze Servants have, Default: 1.0")
                .defineInRange("mutantBlazeServantKnockbackResistance", 1.0, 0.0, Double.MAX_VALUE);
        MutantBlazeServantFollowRange = BUILDER.comment("How much following/detection range Mutant Blaze Servants have, Default: 64.0")
                .defineInRange("mutantBlazeServantFollowRange", 64.0, 1.0, Double.MAX_VALUE);
        MutantBlazeServantMovementSpeed = BUILDER.comment("How fast Mutant Blaze Servants move, Default: 0.25")
                .defineInRange("mutantBlazeServantMovementSpeed", 0.25, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Rodling Servant");
        RodlingServantHealth = BUILDER.comment("How much Max Health Rodling Servants have, Default: 10.0")
                .defineInRange("rodlingServantHealth", 10.0, 1.0, Double.MAX_VALUE);
        RodlingServantArmor = BUILDER.comment("How much natural Armor Rodling Servants have, Default: 0.0")
                .defineInRange("rodlingServantArmor", 0.0, 0.0, Double.MAX_VALUE);
        RodlingServantArmorToughness = BUILDER.comment("How much natural Armor Toughness Rodling Servants have, Default: 0.0")
                .defineInRange("rodlingServantArmorToughness", 0.0, 0.0, Double.MAX_VALUE);
        RodlingServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Rodling Servants have, Default: 0.0")
                .defineInRange("rodlingServantKnockbackResistance", 0.0, 0.0, Double.MAX_VALUE);
        RodlingServantFollowRange = BUILDER.comment("How much following/detection range Rodling Servants have, Default: 20.0")
                .defineInRange("rodlingServantFollowRange", 20.0, 1.0, Double.MAX_VALUE);
        RodlingServantMovementSpeed = BUILDER.comment("How fast Rodling Servants move, Default: 0.125")
                .defineInRange("rodlingServantMovementSpeed", 0.125, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Mutant Jungle Zombie Servant");
        MutantJungleZombieServantHealth = BUILDER.comment("How much Max Health Mutant Jungle Zombie Servants have, Default: 60.0")
                .defineInRange("mutantJungleZombieServantHealth", 60.0, 1.0, Double.MAX_VALUE);
        MutantJungleZombieServantArmor = BUILDER.comment("How much natural Armor Mutant Jungle Zombie Servants have, Default: 2.0")
                .defineInRange("mutantJungleZombieServantArmor", 2.0, 0.0, Double.MAX_VALUE);
        MutantJungleZombieServantArmorToughness = BUILDER.comment("How much natural Armor Toughness Mutant Jungle Zombie Servants have, Default: 0.0")
                .defineInRange("mutantJungleZombieServantArmorToughness", 0.0, 0.0, Double.MAX_VALUE);
        MutantJungleZombieServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Mutant Jungle Zombie Servants have, Default: 1.0")
                .defineInRange("mutantJungleZombieServantKnockbackResistance", 1.0, 0.0, Double.MAX_VALUE);
        MutantJungleZombieServantFollowRange = BUILDER.comment("How much following/detection range Mutant Jungle Zombie Servants have, Default: 64.0")
                .defineInRange("mutantJungleZombieServantFollowRange", 64.0, 1.0, Double.MAX_VALUE);
        MutantJungleZombieServantMovementSpeed = BUILDER.comment("How fast Mutant Jungle Zombie Servants move, Default: 0.24")
                .defineInRange("mutantJungleZombieServantMovementSpeed", 0.24, 0.0, Double.MAX_VALUE);
        MutantJungleZombieServantAttackDamage = BUILDER.comment("How much damage Mutant Jungle Zombie Servants deal, Default: 10.0")
                .defineInRange("mutantJungleZombieServantAttackDamage", 10.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Mutant Husk Servant");
        MutantHuskServantHealth = BUILDER.comment("How much Max Health Mutant Husk Servants have, Default: 180.0")
                .defineInRange("mutantHuskServantHealth", 180.0, 1.0, Double.MAX_VALUE);
        MutantHuskServantArmor = BUILDER.comment("How much natural Armor Mutant Husk Servants have, Default: 4.0")
                .defineInRange("mutantHuskServantArmor", 4.0, 0.0, Double.MAX_VALUE);
        MutantHuskServantArmorToughness = BUILDER.comment("How much natural Armor Toughness Mutant Husk Servants have, Default: 0.0")
                .defineInRange("mutantHuskServantArmorToughness", 0.0, 0.0, Double.MAX_VALUE);
        MutantHuskServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Mutant Husk Servants have, Default: 1.0")
                .defineInRange("mutantHuskServantKnockbackResistance", 1.0, 0.0, Double.MAX_VALUE);
        MutantHuskServantFollowRange = BUILDER.comment("How much following/detection range Mutant Husk Servants have, Default: 64.0")
                .defineInRange("mutantHuskServantFollowRange", 64.0, 1.0, Double.MAX_VALUE);
        MutantHuskServantMovementSpeed = BUILDER.comment("How fast Mutant Husk Servants move, Default: 0.24")
                .defineInRange("mutantHuskServantMovementSpeed", 0.24, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Mutant Frozen Zombie Servant");
        MutantFrozenZombieServantHealth = BUILDER.comment("How much Max Health Mutant Frozen Zombie Servants have, Default: 100.0")
                .defineInRange("mutantFrozenZombieServantHealth", 100.0, 1.0, Double.MAX_VALUE);
        MutantFrozenZombieServantArmor = BUILDER.comment("How much natural Armor Mutant Frozen Zombie Servants have, Default: 2.0")
                .defineInRange("mutantFrozenZombieServantArmor", 2.0, 0.0, Double.MAX_VALUE);
        MutantFrozenZombieServantArmorToughness = BUILDER.comment("How much natural Armor Toughness Mutant Frozen Zombie Servants have, Default: 0.0")
                .defineInRange("mutantFrozenZombieServantArmorToughness", 0.0, 0.0, Double.MAX_VALUE);
        MutantFrozenZombieServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Mutant Frozen Zombie Servants have, Default: 1.0")
                .defineInRange("mutantFrozenZombieServantKnockbackResistance", 1.0, 0.0, Double.MAX_VALUE);
        MutantFrozenZombieServantFollowRange = BUILDER.comment("How much following/detection range Mutant Frozen Zombie Servants have, Default: 64.0")
                .defineInRange("mutantFrozenZombieServantFollowRange", 64.0, 1.0, Double.MAX_VALUE);
        MutantFrozenZombieServantMovementSpeed = BUILDER.comment("How fast Mutant Frozen Zombie Servants move, Default: 0.22")
                .defineInRange("mutantFrozenZombieServantMovementSpeed", 0.22, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.pop();
        BUILDER.push("Servant Limits");
        MutantWitherSkeletonServantLimit = BUILDER
                .comment("Maximum number of Mutant Wither Skeleton Servants that can be summoned (Default: 2)")
                .defineInRange("mutantWitherSkeletonServantLimit", 2, 1, 100);
        MutantHoglinServantLimit = BUILDER
                .comment("Maximum number of Mutant Hoglin Servants that can be summoned (Default: 2)")
                .defineInRange("mutantHoglinServantLimit", 2, 1, 100);
        MutantShulkerServantLimit = BUILDER
                .comment("Maximum number of Mutant Shulker Servants that can be summoned (Default: 2)")
                .defineInRange("mutantShulkerServantLimit", 2, 1, 100);
        MutantShulkerServantMaxTraps = BUILDER
                .comment("Maximum number of active Mutant Shulker Traps within 32 blocks before the servant stops summoning more; 0 disables trap summoning (Default: 24)")
                .defineInRange("mutantShulkerServantMaxTraps", 24, 0, 1000);
        MutantBlazeServantLimit = BUILDER
                .comment("Maximum number of Mutant Blaze Servants that can be summoned (Default: 2)")
                .defineInRange("mutantBlazeServantLimit", 2, 1, 100);
        RodlingServantLimit = BUILDER
                .comment("Maximum number of Rodling Servants that can be summoned (Default: 16)")
                .defineInRange("rodlingServantLimit", 16, 1, 100);
        MutantJungleZombieServantLimit = BUILDER
                .comment("Maximum number of Mutant Jungle Zombie Servants that can be summoned (Default: 2)")
                .defineInRange("mutantJungleZombieServantLimit", 2, 1, 100);
        MutantHuskServantLimit = BUILDER
                .comment("Maximum number of Mutant Husk Servants that can be summoned (Default: 2)")
                .defineInRange("mutantHuskServantLimit", 2, 1, 100);
        MutantFrozenZombieServantLimit = BUILDER
                .comment("Maximum number of Mutant Frozen Zombie Servants that can be summoned (Default: 2)")
                .defineInRange("mutantFrozenZombieServantLimit", 2, 1, 100);
        BUILDER.pop();
        BUILDER.push("Mutant Wither Skeleton Enhancements");
        MWSSoulShieldHealthBouns = BUILDER.comment("Extra health bonus when Mutant Wither Skeleton has Soul Shield, Default: 30")
                .defineInRange("mwsSoulShieldHealthBonus", 30, 0, Integer.MAX_VALUE);
        MWSSoulShieldDamageBouns = BUILDER.comment("Extra attack damage bonus when Mutant Wither Skeleton has Soul Shield, Default: 2")
                .defineInRange("mwsSoulShieldDamageBonus", 2, 0, Integer.MAX_VALUE);
        MWSHowlingSoulHealthBouns = BUILDER.comment("Extra health bonus when Mutant Wither Skeleton has Howling Soul, Default: 20")
                .defineInRange("mwsHowlingSoulHealthBonus", 20, 0, Integer.MAX_VALUE);
        MWSHowlingSoulDamageBouns = BUILDER.comment("Extra attack damage bonus when Mutant Wither Skeleton has Howling Soul, Default: 1")
                .defineInRange("mwsHowlingSoulDamageBonus", 1, 0, Integer.MAX_VALUE);
        MWSUnholyBloodHealthBouns = BUILDER.comment("Extra health bonus when Mutant Wither Skeleton has Unholy Blood, Default: 50")
                .defineInRange("mwsUnholyBloodHealthBonus", 50, 0, Integer.MAX_VALUE);
        MWSUnholyBloodDamageBouns = BUILDER.comment("Extra attack damage bonus when Mutant Wither Skeleton has Unholy Blood, Default: 3")
                .defineInRange("mwsUnholyBloodDamageBonus", 3, 0, Integer.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Mutant Hoglin Enhancements");
        MHSoulJarHealthBouns = BUILDER.comment("Extra health bonus when Mutant Hoglin has Soul Jar, Default: 30")
                .defineInRange("mhSoulJarHealthBonus", 30, 0, Integer.MAX_VALUE);
        MHSoulJarDamageBouns = BUILDER.comment("Extra attack damage bonus when Mutant Hoglin has Soul Jar, Default: 2")
                .defineInRange("mhSoulJarDamageBonus", 2, 0, Integer.MAX_VALUE);
        MHBlazingHelmHealthBouns = BUILDER.comment("Extra health bonus when Mutant Hoglin has Blazing Helm, Default: 20")
                .defineInRange("mhBlazingHelmHealthBonus", 20, 0, Integer.MAX_VALUE);
        MHBlazingHelmDamageBouns = BUILDER.comment("Extra attack damage bonus when Mutant Hoglin has Blazing Helm, Default: 2")
                .defineInRange("mhBlazingHelmDamageBonus", 2, 0, Integer.MAX_VALUE);
        MHUnholyBloodHealthBouns = BUILDER.comment("Extra health bonus when Mutant Hoglin has Unholy Blood, Default: 50")
                .defineInRange("mhUnholyBloodHealthBonus", 50, 0, Integer.MAX_VALUE);
        MHUnholyBloodDamageBouns = BUILDER.comment("Extra attack damage bonus when Mutant Hoglin has Unholy Blood, Default: 3")
                .defineInRange("mhUnholyBloodDamageBonus", 3, 0, Integer.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Mutant Shulker Enhancements");
        MutantShulkerVoidEchoHealth = BUILDER.comment("Bonus max health of Void Echo Mutant Shulker (Default: 25)")
                .defineInRange("mutantShulkerVoidEchoHealth", 25.0, 1.0, 1000.0);
        MutantShulkerVoidEyeHealth = BUILDER.comment("Bonus max health of Void Eye Mutant Shulker (Default: 10)")
                .defineInRange("mutantShulkerVoidEyeHealth", 10.0, 1.0, 1000.0);
        MutantShulkerVoidShardHealth = BUILDER.comment("Bonus max health of Void Shard Mutant Shulker (Default: 15)")
                .defineInRange("mutantShulkerVoidShardHealth", 15.0, 1.0, 1000.0);
        BUILDER.pop();
        BUILDER.push("Mutant Blaze Enhancements");
        MBBulwarkFocusHealthBouns = BUILDER.comment("Extra health bonus when Mutant Blaze has Bulwark Focus, Default: 15")
                .defineInRange("mbBulwarkFocusHealthBouns", 15, 0, Integer.MAX_VALUE);
        MBBlazingHelmHealthBouns = BUILDER.comment("Extra health bonus when Mutant Blaze has Blazing Helm, Default: 15")
                .defineInRange("mbBlazingHelmHealthBouns", 15, 0, Integer.MAX_VALUE);
        MBUnholyBloodHealthBouns = BUILDER.comment("Extra health bonus when Mutant Blaze has Unholy Blood, Default: 20")
                .defineInRange("mbUnholyBloodHealthBouns", 20, 0, Integer.MAX_VALUE);
        MBUnholyBloodTexture = BUILDER.comment("Use the alternate texture when Mutant Blaze Servant has Unholy Blood (Default: true)")
                .define("mbUnholyBloodTexture", true);
        MBUnholyBloodLowHealthTexture = BUILDER.comment("Switch the Unholy Blood Mutant Blaze Servant to the enraged texture while at or below half health, and back when healed above it (Default: true)")
                .define("mbUnholyBloodLowHealthTexture", true);
        BUILDER.pop();
        BUILDER.push("Mutant More Servants");
        MutantMoreServantScreenShake = BUILDER.comment("Whether Mutant More Servants' attacks and abilities shake nearby players' cameras (Default: false)")
                .define("mutantMoreServantScreenShake", false);
        MutantJungleZombieServantOriginalTexture = BUILDER.comment("Whether Mutant Jungle Zombie Servants use the original Mutant More texture instead of the GoetyOminous servant texture (Default: false)")
                .define("mutantJungleZombieServantOriginalTexture", false);
        MutantFrozenZombieServantOriginalTexture = BUILDER.comment("Whether Mutant Frozen Zombie Servants (and their snowballs, icicles and spikes) use the original Mutant More texture instead of the GoetyOminous servant texture (Default: false)")
                .define("mutantFrozenZombieServantOriginalTexture", false);
        MutantHuskServantOriginalTexture = BUILDER.comment("Whether Mutant Husk Servants (and their sand boulders) use the original Mutant More texture instead of the GoetyOminous servant texture (Default: false)")
                .define("mutantHuskServantOriginalTexture", false);
        BUILDER.pop();
        BUILDER.push("Spells");
        BUILDER.push("Hog Charge");
        HogChargeSoulCost = BUILDER.comment("Soul cost of Hog Charge spell (Default: 4)")
                .defineInRange("hogChargeSoulCost", 4, 1, 100);
        HogChargeCooldown = BUILDER.comment("Cooldown of Hog Charge spell in ticks (Default: 60)")
                .defineInRange("hogChargeCooldown", 60, 0, 2000);
        HogChargeDamage = BUILDER.comment("Base damage of Hog Charge (Default: 4.0)")
                .defineInRange("hogChargeDamage", 4.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Wither Breath");
        WitherBreathSoulCost = BUILDER.comment("Soul cost per second during Wither Breath channel, Default: 24")
                .defineInRange("witherBreathSoulCost", 24, 1, 1000);
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
        BUILDER.push("GiantSnowball");
        GiantSnowballSoulCost = BUILDER.comment("Soul cost of Giant Snowball spell (Default: 12)")
                .defineInRange("giantSnowballSoulCost", 12, 1, 100);
        GiantSnowballCooldown = BUILDER.comment("Cooldown of Giant Snowball spell in ticks (Default: 120)")
                .defineInRange("giantSnowballCooldown", 120, 0, 2000);
        GiantSnowballDamage = BUILDER.comment("Base damage of Giant Snowball (Default: 12.0)")
                .defineInRange("giantSnowballDamage", 12.0, 0.0, Double.MAX_VALUE);
        GiantSnowballCastDuration = BUILDER.comment("Cast duration of Giant Snowball spell in ticks (Default: 40)")
                .defineInRange("giantSnowballCastDuration", 40, 0, 500);
        BUILDER.pop();
        BUILDER.push("IcicleSlam");
        IcicleSlamSoulCost = BUILDER.comment("Soul cost of Icicle Slam spell (Default: 24)")
                .defineInRange("icicleSlamSoulCost", 24, 1, 100);
        IcicleSlamCooldown = BUILDER.comment("Cooldown of Icicle Slam spell in ticks (Default: 160)")
                .defineInRange("icicleSlamCooldown", 160, 0, 2000);
        IcicleSlamCastDuration = BUILDER.comment("Cast duration of Icicle Slam spell in ticks (Default: 100)")
                .defineInRange("icicleSlamCastDuration", 100, 0, 500);
        IcicleSlamDamage = BUILDER.comment("Base damage of each icicle (Default: 16.0)")
                .defineInRange("icicleSlamDamage", 16.0, 0.0, Double.MAX_VALUE);
        IcicleSlamFreezingLength = BUILDER.comment("Freezing effect duration of each icicle (Default: 100)")
                .defineInRange("icicleSlamFreezingLength", 100, 0, 2000);
        IcicleSlamFreezingLevel = BUILDER.comment("Freezing effect level of each icicle (Default: 1)")
                .defineInRange("icicleSlamFreezingLevel", 1, 0, 10);
        IcicleSlamIgnoresInvulnerability = BUILDER.comment("Whether icicles ignore hurt invulnerability (Default: true)")
                .define("icicleSlamIgnoresInvulnerability", true);
        BUILDER.pop();
        BUILDER.push("IcicleSpike");
        IcicleSpikeSoulCost = BUILDER.comment("Soul cost of Icicle Spike spell (Default: 20)")
                .defineInRange("icicleSpikeSoulCost", 20, 1, 100);
        IcicleSpikeCooldown = BUILDER.comment("Cooldown of Icicle Spike spell in ticks (Default: 120)")
                .defineInRange("icicleSpikeCooldown", 120, 0, 2000);
        IcicleSpikeCastDuration = BUILDER.comment("Cast duration of Icicle Spike spell in ticks (Default: 60)")
                .defineInRange("icicleSpikeCastDuration", 60, 0, 500);
        BUILDER.pop();
        BUILDER.push("Shulker Scatter");
        ShulkerScatterSoulCost = BUILDER.comment("Soul cost of Shulker Scatter spell (Default: 32)")
                .defineInRange("shulkerScatterSoulCost", 32, 1, 100);
        ShulkerScatterCooldown = BUILDER.comment("Cooldown of Shulker Scatter spell in ticks (Default: 200)")
                .defineInRange("shulkerScatterCooldown", 200, 0, 2000);
        ShulkerScatterCastDuration = BUILDER.comment("Cast duration of Shulker Scatter spell in ticks (Default: 100)")
                .defineInRange("shulkerScatterCastDuration", 100, 0, 500);
        BUILDER.pop();
        BUILDER.push("Shulker Bullet");
        ShulkerBulletSoulCost = BUILDER.comment("Soul cost of Shulker Bullet spell (Default: 24)")
                .defineInRange("shulkerBulletSoulCost", 24, 1, 100);
        ShulkerBulletCooldown = BUILDER.comment("Cooldown of Shulker Bullet spell in ticks (Default: 100)")
                .defineInRange("shulkerBulletCooldown", 100, 0, 2000);
        ShulkerBulletCastDuration = BUILDER.comment("Cast duration of Shulker Bullet spell in ticks (Default: 40)")
                .defineInRange("shulkerBulletCastDuration", 40, 0, 500);
        ShulkerBulletDamage = BUILDER.comment("Base damage of Shulker Bullet (Default: 8.0)")
                .defineInRange("shulkerBulletDamage", 8.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Rod Strike");
        RodStrikeSoulCost = BUILDER.comment("Soul cost of Rod Strike spell (Default: 6)")
                .defineInRange("rodStrikeSoulCost", 6, 1, 100);
        RodStrikeCooldown = BUILDER.comment("Cooldown of Rod Strike spell in ticks (Default: 100)")
                .defineInRange("rodStrikeCooldown", 100, 0, 2000);
        RodStrikeCastDuration = BUILDER.comment("Cast duration of Rod Strike spell in ticks (Default: 60)")
                .defineInRange("rodStrikeCastDuration", 60, 0, 500);
        RodStrikeSummonDown = BUILDER.comment("Summon down duration of Rod Strike spell in ticks (Default: 80)")
                .defineInRange("rodStrikeSummonDown", 80, 0, 5000);
        BUILDER.pop();
        BUILDER.pop();
        BUILDER.push("Wither Scythe");
        WitherScytheDamage = BUILDER
                .comment("Wither Scythe total attack damage, Default: 9.5")
                .defineInRange("witherScytheDamage", 9.5, 0.0, Double.MAX_VALUE);
        WitherScytheAttackSpeed = BUILDER
                .comment("Wither Scythe attack speed modifier, Default: -3.3")
                .defineInRange("witherScytheAttackSpeed", -3.3, -4.0, 10.0);
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
