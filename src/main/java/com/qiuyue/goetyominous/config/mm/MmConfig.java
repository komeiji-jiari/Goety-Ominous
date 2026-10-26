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
    public static final ForgeConfigSpec.ConfigValue<Double> MutantWitherSkeletonServantAttackDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantHoglinServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantHoglinServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantHoglinServantArmorToughness;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantHoglinServantAttackDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantShulkerServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantShulkerServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantShulkerServantArmorToughness;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantBlazeServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantBlazeServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantBlazeServantArmorToughness;
    public static final ForgeConfigSpec.ConfigValue<Double> RodlingServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> RodlingServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> RodlingServantArmorToughness;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantJungleZombieServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantJungleZombieServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantJungleZombieServantArmorToughness;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantJungleZombieServantAttackDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantHuskServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantHuskServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantHuskServantArmorToughness;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantFrozenZombieServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantFrozenZombieServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantFrozenZombieServantArmorToughness;
    public static final ForgeConfigSpec.ConfigValue<Integer> MutantWitherSkeletonServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> MutantHoglinServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> MutantShulkerServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> MutantShulkerServantMaxTraps;
    public static final ForgeConfigSpec.ConfigValue<Integer> MutantBlazeServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> RodlingServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> MutantJungleZombieServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> MutantHuskServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> MutantFrozenZombieServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> MutantWitherSkeletonSoulShieldHealthBonus;
    public static final ForgeConfigSpec.ConfigValue<Integer> MutantWitherSkeletonSoulShieldDamageBonus;
    public static final ForgeConfigSpec.ConfigValue<Integer> MutantWitherSkeletonHowlingSoulHealthBonus;
    public static final ForgeConfigSpec.ConfigValue<Integer> MutantWitherSkeletonHowlingSoulDamageBonus;
    public static final ForgeConfigSpec.ConfigValue<Integer> MutantWitherSkeletonUnholyBloodHealthBonus;
    public static final ForgeConfigSpec.ConfigValue<Integer> MutantWitherSkeletonUnholyBloodDamageBonus;
    public static final ForgeConfigSpec.ConfigValue<Integer> MutantHoglinSoulJarHealthBonus;
    public static final ForgeConfigSpec.ConfigValue<Integer> MutantHoglinSoulJarDamageBonus;
    public static final ForgeConfigSpec.ConfigValue<Integer> MutantHoglinBlazingHelmHealthBonus;
    public static final ForgeConfigSpec.ConfigValue<Integer> MutantHoglinBlazingHelmDamageBonus;
    public static final ForgeConfigSpec.ConfigValue<Integer> MutantHoglinUnholyBloodHealthBonus;
    public static final ForgeConfigSpec.ConfigValue<Integer> MutantHoglinUnholyBloodDamageBonus;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantShulkerVoidEchoHealthBonus;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantShulkerVoidEyeHealthBonus;
    public static final ForgeConfigSpec.ConfigValue<Double> MutantShulkerVoidShardHealthBonus;
    public static final ForgeConfigSpec.ConfigValue<Integer> MutantBlazeBulwarkFocusHealthBonus;
    public static final ForgeConfigSpec.ConfigValue<Integer> MutantBlazeBlazingHelmHealthBonus;
    public static final ForgeConfigSpec.ConfigValue<Integer> MutantBlazeUnholyBloodHealthBonus;
    public static final ForgeConfigSpec.ConfigValue<Boolean> MutantBlazeUnholyBloodTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> MutantBlazeUnholyBloodLowHealthTexture;
    public static final ForgeConfigSpec.ConfigValue<Boolean> MutantMoreServantScreenShake;
    public static final ForgeConfigSpec.ConfigValue<Boolean> MutantMoreMobAnimationFix;
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
    public static final ForgeConfigSpec.ConfigValue<Integer> SandBallSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> SandBallCooldown;
    public static final ForgeConfigSpec.ConfigValue<Integer> SandBallCastDuration;
    public static final ForgeConfigSpec.ConfigValue<Double> SandBallDamage;
    public static final ForgeConfigSpec.ConfigValue<Integer> SandGeyserSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> SandGeyserCooldown;
    public static final ForgeConfigSpec.ConfigValue<Integer> SandGeyserCastDuration;
    public static final ForgeConfigSpec.ConfigValue<Double> SandGeyserDamage;
    public static final ForgeConfigSpec.ConfigValue<Integer> PoisonBreathSoulCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> PoisonBreathCooldown;
    public static final ForgeConfigSpec.ConfigValue<Double> PoisonBreathDamage;
    public static final ForgeConfigSpec.ConfigValue<Integer> PoisonBreathDuration;
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
        MutantWitherSkeletonServantAttackDamage = BUILDER.comment("How much damage Mutant Wither Skeleton Servants deal, Default: 8.0")
                .defineInRange("mutantWitherSkeletonServantAttackDamage", 8.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Mutant Hoglin Servant");
        MutantHoglinServantHealth = BUILDER.comment("How much Max Health Mutant Hoglin Servants have, Default: 250.0")
                .defineInRange("mutantHoglinServantHealth", 250.0, 1.0, Double.MAX_VALUE);
        MutantHoglinServantArmor = BUILDER.comment("How much natural Armor Mutant Hoglin Servants have, Default: 12.0")
                .defineInRange("mutantHoglinServantArmor", 12.0, 0.0, Double.MAX_VALUE);
        MutantHoglinServantArmorToughness = BUILDER.comment("How much natural Armor Toughness Mutant Hoglin Servants have, Default: 0.0")
                .defineInRange("mutantHoglinServantArmorToughness", 0.0, 0.0, Double.MAX_VALUE);
        MutantHoglinServantAttackDamage = BUILDER.comment("How much damage Mutant Hoglin Servants deal, Default: 8.0")
                .defineInRange("mutantHoglinServantAttackDamage", 8.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Mutant Shulker Servant");
        MutantShulkerServantHealth = BUILDER.comment("How much Max Health Mutant Shulker Servants have, Default: 300.0")
                .defineInRange("mutantShulkerServantHealth", 300.0, 1.0, Double.MAX_VALUE);
        MutantShulkerServantArmor = BUILDER.comment("How much natural Armor Mutant Shulker Servants have, Default: 10.0")
                .defineInRange("mutantShulkerServantArmor", 10.0, 0.0, Double.MAX_VALUE);
        MutantShulkerServantArmorToughness = BUILDER.comment("How much natural Armor Toughness Mutant Shulker Servants have, Default: 0.0")
                .defineInRange("mutantShulkerServantArmorToughness", 0.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Mutant Blaze Servant");
        MutantBlazeServantHealth = BUILDER.comment("How much Max Health Mutant Blaze Servants have, Default: 250.0")
                .defineInRange("mutantBlazeServantHealth", 250.0, 1.0, Double.MAX_VALUE);
        MutantBlazeServantArmor = BUILDER.comment("How much natural Armor Mutant Blaze Servants have, Default: 5.0")
                .defineInRange("mutantBlazeServantArmor", 5.0, 0.0, Double.MAX_VALUE);
        MutantBlazeServantArmorToughness = BUILDER.comment("How much natural Armor Toughness Mutant Blaze Servants have, Default: 0.0")
                .defineInRange("mutantBlazeServantArmorToughness", 0.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Rodling Servant");
        RodlingServantHealth = BUILDER.comment("How much Max Health Rodling Servants have, Default: 10.0")
                .defineInRange("rodlingServantHealth", 10.0, 1.0, Double.MAX_VALUE);
        RodlingServantArmor = BUILDER.comment("How much natural Armor Rodling Servants have, Default: 0.0")
                .defineInRange("rodlingServantArmor", 0.0, 0.0, Double.MAX_VALUE);
        RodlingServantArmorToughness = BUILDER.comment("How much natural Armor Toughness Rodling Servants have, Default: 0.0")
                .defineInRange("rodlingServantArmorToughness", 0.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Mutant Jungle Zombie Servant");
        MutantJungleZombieServantHealth = BUILDER.comment("How much Max Health Mutant Jungle Zombie Servants have, Default: 60.0")
                .defineInRange("mutantJungleZombieServantHealth", 60.0, 1.0, Double.MAX_VALUE);
        MutantJungleZombieServantArmor = BUILDER.comment("How much natural Armor Mutant Jungle Zombie Servants have, Default: 2.0")
                .defineInRange("mutantJungleZombieServantArmor", 2.0, 0.0, Double.MAX_VALUE);
        MutantJungleZombieServantArmorToughness = BUILDER.comment("How much natural Armor Toughness Mutant Jungle Zombie Servants have, Default: 0.0")
                .defineInRange("mutantJungleZombieServantArmorToughness", 0.0, 0.0, Double.MAX_VALUE);
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
        BUILDER.pop();
        BUILDER.push("Mutant Frozen Zombie Servant");
        MutantFrozenZombieServantHealth = BUILDER.comment("How much Max Health Mutant Frozen Zombie Servants have, Default: 100.0")
                .defineInRange("mutantFrozenZombieServantHealth", 100.0, 1.0, Double.MAX_VALUE);
        MutantFrozenZombieServantArmor = BUILDER.comment("How much natural Armor Mutant Frozen Zombie Servants have, Default: 2.0")
                .defineInRange("mutantFrozenZombieServantArmor", 2.0, 0.0, Double.MAX_VALUE);
        MutantFrozenZombieServantArmorToughness = BUILDER.comment("How much natural Armor Toughness Mutant Frozen Zombie Servants have, Default: 0.0")
                .defineInRange("mutantFrozenZombieServantArmorToughness", 0.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.pop();
        BUILDER.push("Servants Enhancements");
        BUILDER.push("Mutant Wither Skeleton Servant");
        MutantWitherSkeletonSoulShieldHealthBonus = BUILDER.comment("Extra health bonus when Mutant Wither Skeleton has Soul Shield, Default: 30")
                .defineInRange("mutantWitherSkeletonSoulShieldHealthBonus", 30, 0, Integer.MAX_VALUE);
        MutantWitherSkeletonSoulShieldDamageBonus = BUILDER.comment("Extra attack damage bonus when Mutant Wither Skeleton has Soul Shield, Default: 2")
                .defineInRange("mutantWitherSkeletonSoulShieldDamageBonus", 2, 0, Integer.MAX_VALUE);
        MutantWitherSkeletonHowlingSoulHealthBonus = BUILDER.comment("Extra health bonus when Mutant Wither Skeleton has Howling Soul, Default: 20")
                .defineInRange("mutantWitherSkeletonHowlingSoulHealthBonus", 20, 0, Integer.MAX_VALUE);
        MutantWitherSkeletonHowlingSoulDamageBonus = BUILDER.comment("Extra attack damage bonus when Mutant Wither Skeleton has Howling Soul, Default: 1")
                .defineInRange("mutantWitherSkeletonHowlingSoulDamageBonus", 1, 0, Integer.MAX_VALUE);
        MutantWitherSkeletonUnholyBloodHealthBonus = BUILDER.comment("Extra health bonus when Mutant Wither Skeleton has Unholy Blood, Default: 50")
                .defineInRange("mutantWitherSkeletonUnholyBloodHealthBonus", 50, 0, Integer.MAX_VALUE);
        MutantWitherSkeletonUnholyBloodDamageBonus = BUILDER.comment("Extra attack damage bonus when Mutant Wither Skeleton has Unholy Blood, Default: 3")
                .defineInRange("mutantWitherSkeletonUnholyBloodDamageBonus", 3, 0, Integer.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Mutant Hoglin Servant");
        MutantHoglinSoulJarHealthBonus = BUILDER.comment("Extra health bonus when Mutant Hoglin has Soul Jar, Default: 30")
                .defineInRange("mutantHoglinSoulJarHealthBonus", 30, 0, Integer.MAX_VALUE);
        MutantHoglinSoulJarDamageBonus = BUILDER.comment("Extra attack damage bonus when Mutant Hoglin has Soul Jar, Default: 2")
                .defineInRange("mutantHoglinSoulJarDamageBonus", 2, 0, Integer.MAX_VALUE);
        MutantHoglinBlazingHelmHealthBonus = BUILDER.comment("Extra health bonus when Mutant Hoglin has Blazing Helm, Default: 20")
                .defineInRange("mutantHoglinBlazingHelmHealthBonus", 20, 0, Integer.MAX_VALUE);
        MutantHoglinBlazingHelmDamageBonus = BUILDER.comment("Extra attack damage bonus when Mutant Hoglin has Blazing Helm, Default: 2")
                .defineInRange("mutantHoglinBlazingHelmDamageBonus", 2, 0, Integer.MAX_VALUE);
        MutantHoglinUnholyBloodHealthBonus = BUILDER.comment("Extra health bonus when Mutant Hoglin has Unholy Blood, Default: 50")
                .defineInRange("mutantHoglinUnholyBloodHealthBonus", 50, 0, Integer.MAX_VALUE);
        MutantHoglinUnholyBloodDamageBonus = BUILDER.comment("Extra attack damage bonus when Mutant Hoglin has Unholy Blood, Default: 3")
                .defineInRange("mutantHoglinUnholyBloodDamageBonus", 3, 0, Integer.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Mutant Shulker Servant");
        MutantShulkerVoidEchoHealthBonus = BUILDER.comment("Bonus max health of Void Echo Mutant Shulker (Default: 25)")
                .defineInRange("mutantShulkerVoidEchoHealthBonus", 25.0, 1.0, 1000.0);
        MutantShulkerVoidEyeHealthBonus = BUILDER.comment("Bonus max health of Void Eye Mutant Shulker (Default: 10)")
                .defineInRange("mutantShulkerVoidEyeHealthBonus", 10.0, 1.0, 1000.0);
        MutantShulkerVoidShardHealthBonus = BUILDER.comment("Bonus max health of Void Shard Mutant Shulker (Default: 15)")
                .defineInRange("mutantShulkerVoidShardHealthBonus", 15.0, 1.0, 1000.0);
        BUILDER.pop();
        BUILDER.push("Mutant Blaze Servant");
        MutantBlazeBulwarkFocusHealthBonus = BUILDER.comment("Extra health bonus when Mutant Blaze has Bulwark Focus, Default: 15")
                .defineInRange("mutantBlazeBulwarkFocusHealthBonus", 15, 0, Integer.MAX_VALUE);
        MutantBlazeBlazingHelmHealthBonus = BUILDER.comment("Extra health bonus when Mutant Blaze has Blazing Helm, Default: 15")
                .defineInRange("mutantBlazeBlazingHelmHealthBonus", 15, 0, Integer.MAX_VALUE);
        MutantBlazeUnholyBloodHealthBonus = BUILDER.comment("Extra health bonus when Mutant Blaze has Unholy Blood, Default: 20")
                .defineInRange("mutantBlazeUnholyBloodHealthBonus", 20, 0, Integer.MAX_VALUE);
        MutantBlazeUnholyBloodTexture = BUILDER.comment("Use the alternate texture when Mutant Blaze Servant has Unholy Blood (Default: true)")
                .define("mutantBlazeUnholyBloodTexture", true);
        MutantBlazeUnholyBloodLowHealthTexture = BUILDER.comment("Switch the Unholy Blood Mutant Blaze Servant to the enraged texture while at or below half health, and back when healed above it (Default: true)")
                .define("mutantBlazeUnholyBloodLowHealthTexture", true);
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
        BUILDER.push("Spells");
        BUILDER.push("Hog Charge Spell");
        HogChargeSoulCost = BUILDER.comment("Soul cost of Hog Charge spell (Default: 4)")
                .defineInRange("hogChargeSoulCost", 4, 1, 100);
        HogChargeCooldown = BUILDER.comment("Cooldown of Hog Charge spell in ticks (Default: 60)")
                .defineInRange("hogChargeCooldown", 60, 0, 2000);
        HogChargeDamage = BUILDER.comment("Base damage of Hog Charge (Default: 4.0)")
                .defineInRange("hogChargeDamage", 4.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Wither Breath Spell");
        WitherBreathSoulCost = BUILDER.comment("Soul cost per second during Wither Breath channel, Default: 24")
                .defineInRange("witherBreathSoulCost", 24, 1, 1000);
        WitherBreathCooldown = BUILDER.comment("Cooldown of Wither Breath spell in ticks (Default: 400)")
                .defineInRange("witherBreathCooldown", 400, 0, 2000);
        BUILDER.pop();
        BUILDER.push("Wither Slash Spell");
        WitherSlashSoulCost = BUILDER.comment("Soul cost of Wither Slash spell (Default: 24)")
                .defineInRange("witherSlashSoulCost", 24, 1, 100);
        WitherSlashCooldown = BUILDER.comment("Cooldown of Wither Slash spell in ticks (Default: 60)")
                .defineInRange("witherSlashCooldown", 60, 0, 2000);
        WitherSlashDamage = BUILDER.comment("Base damage of Wither Slash (Default: 6.0)")
                .defineInRange("witherSlashDamage", 6.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Giant Snowball Spell");
        GiantSnowballSoulCost = BUILDER.comment("Soul cost of Giant Snowball spell (Default: 12)")
                .defineInRange("giantSnowballSoulCost", 12, 1, 100);
        GiantSnowballCooldown = BUILDER.comment("Cooldown of Giant Snowball spell in ticks (Default: 120)")
                .defineInRange("giantSnowballCooldown", 120, 0, 2000);
        GiantSnowballDamage = BUILDER.comment("Base damage of Giant Snowball (Default: 12.0)")
                .defineInRange("giantSnowballDamage", 12.0, 0.0, Double.MAX_VALUE);
        GiantSnowballCastDuration = BUILDER.comment("Cast duration of Giant Snowball spell in ticks (Default: 40)")
                .defineInRange("giantSnowballCastDuration", 40, 0, 500);
        BUILDER.pop();
        BUILDER.push("Icicle Slam Spell");
        IcicleSlamSoulCost = BUILDER.comment("Soul cost of Icicle Slam spell (Default: 24)")
                .defineInRange("icicleSlamSoulCost", 24, 1, 100);
        IcicleSlamCooldown = BUILDER.comment("Cooldown of Icicle Slam spell in ticks (Default: 160)")
                .defineInRange("icicleSlamCooldown", 160, 0, 2000);
        IcicleSlamCastDuration = BUILDER.comment("Cast duration of Icicle Slam spell in ticks (Default: 60)")
                .defineInRange("icicleSlamCastDuration", 60, 0, 500);
        IcicleSlamDamage = BUILDER.comment("Base damage of each icicle (Default: 3.0)")
                .defineInRange("icicleSlamDamage", 3.0, 0.0, Double.MAX_VALUE);
        IcicleSlamFreezingLength = BUILDER.comment("Freezing effect duration of each icicle (Default: 100)")
                .defineInRange("icicleSlamFreezingLength", 100, 0, 2000);
        IcicleSlamFreezingLevel = BUILDER.comment("Freezing effect level of each icicle (Default: 1)")
                .defineInRange("icicleSlamFreezingLevel", 1, 0, 10);
        IcicleSlamIgnoresInvulnerability = BUILDER.comment("Whether icicles ignore hurt invulnerability (Default: true)")
                .define("icicleSlamIgnoresInvulnerability", true);
        BUILDER.pop();
        BUILDER.push("Icicle Spike Spell");
        IcicleSpikeSoulCost = BUILDER.comment("Soul cost of Icicle Spike spell (Default: 12)")
                .defineInRange("icicleSpikeSoulCost", 12, 1, 100);
        IcicleSpikeCooldown = BUILDER.comment("Cooldown of Icicle Spike spell in ticks (Default: 60)")
                .defineInRange("icicleSpikeCooldown", 60, 0, 2000);
        IcicleSpikeCastDuration = BUILDER.comment("Cast duration of Icicle Spike spell in ticks (Default: 0)")
                .defineInRange("icicleSpikeCastDuration", 0, 0, 500);
        BUILDER.pop();
        BUILDER.push("Sand Boulder Spell");
        SandBallSoulCost = BUILDER.comment("Soul cost of Sand Ball spell (Default: 16)")
                .defineInRange("sandBallSoulCost", 16, 1, 100);
        SandBallCooldown = BUILDER.comment("Cooldown of Sand Ball spell in ticks (Default: 100)")
                .defineInRange("sandBallCooldown", 100, 0, 2000);
        SandBallDamage = BUILDER.comment("Base damage of Sand Ball (Default: 10.0, matches Mutant Husk sand boulder)")
                .defineInRange("sandBallDamage", 10.0, 0.0, Double.MAX_VALUE);
        SandBallCastDuration = BUILDER.comment("Cast duration of Sand Ball spell in ticks (Default: 40)")
                .defineInRange("sandBallCastDuration", 40, 0, 500);
        BUILDER.pop();
        BUILDER.push("Sand Geyser Spell");
        SandGeyserSoulCost = BUILDER.comment("Soul cost of Sand Geyser spell (Default: 20)")
                .defineInRange("sandGeyserSoulCost", 20, 1, 100);
        SandGeyserCooldown = BUILDER.comment("Cooldown of Sand Geyser spell in ticks (Default: 400)")
                .defineInRange("sandGeyserCooldown", 400, 0, 2000);
        SandGeyserDamage = BUILDER.comment("Base damage of Sand Geyser (Default: 8.0, matches Mutant Husk geyser)")
                .defineInRange("sandGeyserDamage", 8.0, 0.0, Double.MAX_VALUE);
        SandGeyserCastDuration = BUILDER.comment("Cast duration of Sand Geyser spell in ticks (Default: 60)")
                .defineInRange("sandGeyserCastDuration", 60, 0, 500);
        BUILDER.pop();
        BUILDER.push("Poison Breath Spell");
        PoisonBreathSoulCost = BUILDER.comment("Soul cost of Poison Breath (Default: 2)")
                .defineInRange("poisonBreathSoulCost", 2, 1, 100);
        PoisonBreathCooldown = BUILDER.comment("Cooldown of Poison Breath in ticks (Default: 100)")
                .defineInRange("poisonBreathCooldown", 100, 0, 5000);
        PoisonBreathDamage = BUILDER.comment("Damage per Poison Breath hit (Default: 1.0)")
                .defineInRange("poisonBreathDamage", 1.0, 0.0, Double.MAX_VALUE);
        PoisonBreathDuration = BUILDER.comment("Breath duration of Poison Breath (Default: 100)")
                .defineInRange("poisonBreathDuration", 100, 0, 1000);
        BUILDER.pop();
        BUILDER.push("Shulker Scatter Spell");
        ShulkerScatterSoulCost = BUILDER.comment("Soul cost of Shulker Scatter spell (Default: 32)")
                .defineInRange("shulkerScatterSoulCost", 32, 1, 100);
        ShulkerScatterCooldown = BUILDER.comment("Cooldown of Shulker Scatter spell in ticks (Default: 200)")
                .defineInRange("shulkerScatterCooldown", 200, 0, 2000);
        ShulkerScatterCastDuration = BUILDER.comment("Cast duration of Shulker Scatter spell in ticks (Default: 100)")
                .defineInRange("shulkerScatterCastDuration", 100, 0, 500);
        BUILDER.pop();
        BUILDER.push("Shulker Bullet Spell");
        ShulkerBulletSoulCost = BUILDER.comment("Soul cost of Shulker Bullet spell (Default: 24)")
                .defineInRange("shulkerBulletSoulCost", 24, 1, 100);
        ShulkerBulletCooldown = BUILDER.comment("Cooldown of Shulker Bullet spell in ticks (Default: 100)")
                .defineInRange("shulkerBulletCooldown", 100, 0, 2000);
        ShulkerBulletCastDuration = BUILDER.comment("Cast duration of Shulker Bullet spell in ticks (Default: 40)")
                .defineInRange("shulkerBulletCastDuration", 40, 0, 500);
        ShulkerBulletDamage = BUILDER.comment("Base damage of Shulker Bullet (Default: 8.0)")
                .defineInRange("shulkerBulletDamage", 8.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Rod Strike Spell");
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
        BUILDER.push("Tools & Weapons");
        BUILDER.push("Wither Scythe");
        WitherScytheDamage = BUILDER
                .comment("Wither Scythe total attack damage, Default: 9.5")
                .defineInRange("witherScytheDamage", 9.5, 0.0, Double.MAX_VALUE);
        WitherScytheAttackSpeed = BUILDER
                .comment("Wither Scythe attack speed modifier, Default: -3.3")
                .defineInRange("witherScytheAttackSpeed", -3.3, -4.0, 10.0);
        BUILDER.pop();
        BUILDER.pop();
        BUILDER.push("Mechanics");
        MutantMoreServantScreenShake = BUILDER.comment("Whether Mutant More Servants' attacks and abilities shake nearby players' cameras (Default: false)")
                .define("mutantMoreServantScreenShake", false);
        MutantMoreMobAnimationFix = BUILDER
                .comment("Apply GoetyOminous' fixed animations to Mutant More's own mutant mobs (client-side, requires restart) (Default: true)")
                .define("mutantMoreMobAnimationFix", true);
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
