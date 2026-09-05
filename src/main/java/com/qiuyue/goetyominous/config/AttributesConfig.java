package com.qiuyue.goetyominous.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.File;

public class AttributesConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Double> WargHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> WargArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> WargDamage;

    public static final ForgeConfigSpec.ConfigValue<Double> MurmurServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> MurmurServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> MurmurServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> MurmurServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> MurmurServantKnockbackResistance;

    public static final ForgeConfigSpec.ConfigValue<Double> FarseerServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> FarseerServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> FarseerServantFlyingSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> FarseerServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> FarseerServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> FarseerServantFollowRange;

    public static final ForgeConfigSpec.ConfigValue<Double> CrimsonMosquitoServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> CrimsonMosquitoServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> CrimsonMosquitoServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> CrimsonMosquitoServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> CrimsonMosquitoServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> CrimsonMosquitoFleeHealthThreshold;
    public static final ForgeConfigSpec.ConfigValue<Double> CrimsonMosquitoFleeHealthThresholdUnholy;

    public static final ForgeConfigSpec.ConfigValue<Double> WarpedMoscoServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> WarpedMoscoServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> WarpedMoscoServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> WarpedMoscoServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> WarpedMoscoServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> WarpedMoscoServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> WarpedMoscoServantArmorToughness;

    public static final ForgeConfigSpec.ConfigValue<Double> TusklinServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> TusklinServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> TusklinServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> TusklinServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> TusklinServantKnockbackResistance;

    public static final ForgeConfigSpec.ConfigValue<Double> GrottoceratopsServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> GrottoceratopsServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> GrottoceratopsServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> GrottoceratopsServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> GrottoceratopsServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> GrottoceratopsServantArmor;

    public static final ForgeConfigSpec.ConfigValue<Double> TremorsaurusServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> TremorsaurusServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> TremorsaurusServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> TremorsaurusServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> TremorsaurusServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> TremorsaurusServantArmor;

    public static final ForgeConfigSpec.ConfigValue<Double> TremorzillaServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> TremorzillaServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> TremorzillaServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> TremorzillaServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> TremorzillaServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> TremorzillaServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> TremorzillaServantTailHpPercentDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> TremorzillaServantDamageCap;

    public static final ForgeConfigSpec.ConfigValue<Double> VallumraptorServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> VallumraptorServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> VallumraptorServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> VallumraptorServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> VallumraptorServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> VallumraptorServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> VallumraptorServantElderHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> VallumraptorServantElderArmor;

    public static final ForgeConfigSpec.ConfigValue<Double> NucleeperServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> NucleeperServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> NucleeperServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> NucleeperServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> NucleeperServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> NucleeperServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> NucleeperServantTauntRange;

    public static final ForgeConfigSpec.ConfigValue<Boolean> MineGuardianServantExplosionGriefing;
    public static final ForgeConfigSpec.ConfigValue<Boolean> HullbreakerServantBlockBreakGriefing;
    public static final ForgeConfigSpec.ConfigValue<Double> HullbreakerServantGlowTargetRange;
    public static final ForgeConfigSpec.ConfigValue<Double> HullbreakerServantGlowChaseSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> HullbreakerServantProximityTargetRange;
    public static final ForgeConfigSpec.ConfigValue<Double> BrainiacServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> BrainiacServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> BrainiacServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> BrainiacServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> BrainiacServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> BrainiacServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> CaniacServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> CaniacServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> CaniacServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> CaniacServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> CaniacServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> CaniacServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> GummyBearServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> GummyBearServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> GummyBearServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> GummyBearServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> GummyBearServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> GummyBearServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> CaramelCubeServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> CaramelCubeServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> CaramelCubeServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> CaramelCubeServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> CaramelCubeServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> CaramelCubeServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> GumbeeperServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> GumbeeperServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> GumbeeperServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> GumbeeperServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> GumbeeperServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> GumbeeperServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> VesperServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> VesperServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> VesperServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> VesperServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> VesperServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> VesperServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> TeletorServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> TeletorServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> TeletorServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> TeletorServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> TeletorServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> TeletorServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> ForsakenServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> ForsakenServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> ForsakenServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> ForsakenServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> ForsakenServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> GammaroachServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> GammaroachServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> GammaroachServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> GammaroachServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> GammaroachServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> GammaroachServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> CorrodentServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> CorrodentServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> CorrodentServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> CorrodentServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> CorrodentServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> CorrodentServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> DeepOneServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> DeepOneServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> DeepOneServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> DeepOneServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> DeepOneServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> DeepOneServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> DeepOneKnightServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> DeepOneKnightServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> DeepOneKnightServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> DeepOneKnightServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> DeepOneKnightServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> DeepOneKnightServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Integer> DeepOneKnightServantOrtholanceWaveCooldown;
    public static final ForgeConfigSpec.ConfigValue<Double> DeepOneKnightServantOrtholanceWaveRange;
    public static final ForgeConfigSpec.ConfigValue<Integer> DeepOneKnightServantOrtholanceWaveCount;
    public static final ForgeConfigSpec.ConfigValue<Double> DeepOneKnightServantOrtholanceWaveScale;
    public static final ForgeConfigSpec.ConfigValue<Double> DeepOneKnightServantOrtholanceDashDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> DeepOneKnightServantOrtholanceDashSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> DeepOneKnightServantOrtholanceChance;
    public static final ForgeConfigSpec.ConfigValue<Double> DeepOneMageServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> DeepOneMageServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> DeepOneMageServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> DeepOneMageServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> DeepOneMageServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> DeepOneMageServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> FroststalkerServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> FroststalkerServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> FroststalkerServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> FroststalkerServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> FroststalkerServantArmor;

    public static final ForgeConfigSpec.ConfigValue<Double> DropBearServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> DropBearServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> DropBearServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> DropBearServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> DropBearServantKnockbackResistance;

    public static final ForgeConfigSpec.ConfigValue<Double> GusterServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> GusterServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> GusterServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> GusterServantKnockbackResistance;

    public static final ForgeConfigSpec.ConfigValue<Double> RockyRollerServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> RockyRollerServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> RockyRollerServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> RockyRollerServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> RockyRollerServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> RockyRollerServantArmor;

    public static final ForgeConfigSpec.ConfigValue<Double> ZombieCrocodileServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> ZombieCrocodileServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> ZombieCrocodileServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> ZombieCrocodileServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> ZombieCrocodileServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> ZombieCrocodileServantArmor;

    public static final ForgeConfigSpec.ConfigValue<Double> SkelewagServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> SkelewagServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> SkelewagServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> SkelewagServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> SkelewagServantKnockbackResistance;

    public static final ForgeConfigSpec.ConfigValue<Double> BunfungusServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> BunfungusServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> BunfungusServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> BunfungusServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> BunfungusServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> BunfungusServantContactDamage;

    public static final ForgeConfigSpec.ConfigValue<Double> IllagerElephantServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> IllagerElephantServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> IllagerElephantServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> IllagerElephantServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> IllagerElephantServantKnockbackResistance;

    public static final ForgeConfigSpec.ConfigValue<Double> ServantCentipedeHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> ServantCentipedeFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> ServantCentipedeArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> ServantCentipedeAttackDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> ServantCentipedeKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> ServantCentipedeMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> ServantCentipedeBodyHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> ServantCentipedeBodyMovementSpeed;

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

    public static final ForgeConfigSpec.ConfigValue<Double> DiscipleHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> DiscipleArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> DiscipleDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> DiscipleMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> DiscipleFollowRange;

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

    public static final ForgeConfigSpec.ConfigValue<Double> OvergrownColossusServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> OvergrownColossusServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> OvergrownColossusServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> OvergrownColossusServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> OvergrownColossusServantFollowRange;
    public static final ForgeConfigSpec.ConfigValue<Double> OvergrownColossusServantKnockbackResistance;
    public static final ForgeConfigSpec.ConfigValue<Double> OvergrownColossusServantAttackKnockback;

    public static final ForgeConfigSpec.ConfigValue<Double> StormNecromancerHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> StormNecromancerArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> StormNecromancerDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> StormNecromancerFollowRange;

    public static final ForgeConfigSpec.ConfigValue<Double> PiglinServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> PiglinServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> PiglinServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> PiglinServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> PiglinServantFollowRange;

    public static final ForgeConfigSpec.ConfigValue<Double> PiglinBruteServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> PiglinBruteServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> PiglinBruteServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> PiglinBruteServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> PiglinBruteServantFollowRange;

    public static final ForgeConfigSpec.ConfigValue<Double> PiglinBruteServantEvolvedHealthBonus;
    public static final ForgeConfigSpec.ConfigValue<Double> PiglinBruteServantEvolvedDamageBonus;
    public static final ForgeConfigSpec.ConfigValue<Double> PiglinBruteServantEvolved2HealthBonus;
    public static final ForgeConfigSpec.ConfigValue<Double> PiglinBruteServantEvolved2DamageBonus;

    public static final ForgeConfigSpec.ConfigValue<Double> PiglinHunterServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> PiglinHunterServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> PiglinHunterServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> PiglinHunterServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> PiglinHunterServantFollowRange;

    public static final ForgeConfigSpec.ConfigValue<Double> PiglinHunterServantEvolvedHealthBonus;
    public static final ForgeConfigSpec.ConfigValue<Double> PiglinHunterServantEvolvedDamageBonus;
    public static final ForgeConfigSpec.ConfigValue<Double> PiglinHunterServantEvolved2HealthBonus;
    public static final ForgeConfigSpec.ConfigValue<Double> PiglinHunterServantEvolved2DamageBonus;

    public static final ForgeConfigSpec.ConfigValue<Double> FungusThrowerServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> FungusThrowerServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> FungusThrowerServantDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> FungusThrowerServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> FungusThrowerServantFollowRange;

    public static final ForgeConfigSpec.ConfigValue<Double> CrimsonSpiderServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> CrimsonSpiderServantDamage;

    public static final ForgeConfigSpec.ConfigValue<Double> ScorchHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> ScorchDamage;

    public static final ForgeConfigSpec.ConfigValue<Double> ReturnedHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> ReturnedArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> ReturnedFollowRange;

    public static final ForgeConfigSpec.ConfigValue<Double> AgonyHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> AgonyDamage;

    public static final ForgeConfigSpec.ConfigValue<Double> BeldamHealth;

    public static final ForgeConfigSpec.ConfigValue<Double> ZealotHealth;

    public static final ForgeConfigSpec.ConfigValue<Double> FanaticHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> FanaticDamage;

    public static final ForgeConfigSpec.ConfigValue<Double> ChannellerHealth;

    public static final ForgeConfigSpec.ConfigValue<Double> MartyrHealth;

    public static final ForgeConfigSpec.ConfigValue<Double> ThugHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> ThugDamage;

    public static final ForgeConfigSpec.ConfigValue<Double> UrbhadhachHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> UrbhadhachDamage;

    public static final ForgeConfigSpec.ConfigValue<Double> ArchGeomancerHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> ArchGeomancerArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> ArchGeomancerArmorToughness;
    public static final ForgeConfigSpec.ConfigValue<Double> ArchGeomancerMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> ArchGeomancerAttackDamage;

    public static final ForgeConfigSpec.ConfigValue<Double> RamblerServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> RamblerServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> RamblerServantAttackDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> RamblerServantAttackKnockback;

    public static final ForgeConfigSpec.ConfigValue<Double> DicerServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> DicerServantMovementSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> DicerServantAttackDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> DicerServantAttackKnockback;

    static {
        BUILDER.push("Servants Attributes");

        BUILDER.push("Warg");
        WargHealth = BUILDER.comment("How much Max Health Wargs have, Default: 50.0")
                .defineInRange("wargHealth", 50.0, 1.0, Double.MAX_VALUE);
        WargArmor = BUILDER.comment("How much armor Wargs have, Default: 0.0")
                .defineInRange("wargArmor", 0.0, 0.0, Double.MAX_VALUE);
        WargDamage = BUILDER.comment("How much damage Wargs deal, Default: 8.0")
                .defineInRange("wargDamage", 8.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();

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

        BUILDER.push("Storm Necromancer");
        StormNecromancerHealth = BUILDER.comment("How much Max Health Storm Necromancers have, Default: 220.0")
                .defineInRange("stormNecromancerHealth", 220.0, 1.0, Double.MAX_VALUE);
        StormNecromancerArmor = BUILDER.comment("How much natural Armor Storm Necromancers have, Default: 4.0")
                .defineInRange("stormNecromancerArmor", 4.0, 0.0, Double.MAX_VALUE);
        StormNecromancerFollowRange = BUILDER.comment("How much following/detection range Storm Necromancers have, Default: 32.0")
                .defineInRange("stormNecromancerFollowRange", 32.0, 1.0, 2048.0);
        StormNecromancerDamage = BUILDER.comment("How much damage Storm Necromancers deals, Default: 3.0")
                .defineInRange("stormNecromancerDamage", 3.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();

        BUILDER.push("Returned");
        ReturnedHealth = BUILDER.comment("How much Max Health Returned have, Default: 10.0")
                .defineInRange("returnedHealth", 40.0, 1.0, Double.MAX_VALUE);
        ReturnedArmor = BUILDER.comment("How much natural Armor Returned have, Default: 2.0")
                .defineInRange("returnedArmor", 2.0, 0.0, Double.MAX_VALUE);
        ReturnedFollowRange = BUILDER.comment("How much following/detection range Returned have, Default: 32.0")
                .defineInRange("returnedFollowRange", 32.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();

        BUILDER.push("Agony");
        AgonyHealth = BUILDER.comment("How much Max Health Agony have, Default: 50.0")
                .defineInRange("agonyHealth", 50.0, 1.0, Double.MAX_VALUE);
        AgonyDamage = BUILDER.comment("How much damage Agony deals, Default: 9.0")
                .defineInRange("agonyDamage", 9.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();

        BUILDER.push("Beldam");
        BeldamHealth = BUILDER
                .comment("How much Max Health Beldam have, Default: 26.0")
                .defineInRange("beldamHealth", 26.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();

        BUILDER.push("Zealot");
        ZealotHealth = BUILDER
                .comment("How much Max Health Zealot have, Default: 24.0")
                .defineInRange("zealotHealth", 24.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();

        BUILDER.push("Fanatic");
        FanaticHealth = BUILDER
                .comment("How much Max Health Fanatic have, Default: 24.0")
                .defineInRange("fanaticHealth", 24.0, 1.0, Double.MAX_VALUE);
        FanaticDamage = BUILDER
                .comment("How much damage Fanatic deals, Default: 2.0")
                .defineInRange("fanaticDamage", 2.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();

        BUILDER.push("Thug");
        ThugHealth = BUILDER.comment("How much Max Health Thug have, Default: 50.0")
                .defineInRange("thugHealth", 50.0, 1.0, Double.MAX_VALUE);
        ThugDamage = BUILDER.comment("How much damage Thug deals, Default: 7.0")
                .defineInRange("thugDamage", 7.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();

        BUILDER.push("Martyr");
        MartyrHealth = BUILDER
                .comment("How much Max Health Martyr have, Default: 32.0")
                .defineInRange("martyrHealth", 32.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();

        BUILDER.push("Channeller");
        ChannellerHealth = BUILDER.comment("How much Max Health Channeller have, Default: 32.0")
                .defineInRange("channellerHealth", 32.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();

        BUILDER.push("Scorch");
        ScorchHealth = BUILDER.comment("How much Max Health Scorch have, Default: 14.0")
                .defineInRange("scorchHealth", 14.0, 1.0, Double.MAX_VALUE);
        ScorchDamage = BUILDER.comment("How much damage Scorch deals, Default: 6.0")
                .defineInRange("scorchDamage", 6.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();

        BUILDER.push("Urbhadhach");
        UrbhadhachHealth = BUILDER.comment("How much Max Health Urbhadhach have, Default: 50.0")
                .defineInRange("urbhadhachHealth", 50.0, 1.0, Double.MAX_VALUE);
        UrbhadhachDamage = BUILDER.comment("How much damage Urbhadhach deals, Default: 12.0")
                .defineInRange("urbhadhachDamage", 12.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();

        BUILDER.push("Arch Geomancer");
        ArchGeomancerHealth = BUILDER.comment("How much Max Health Arch Geomancers have, Default: 160.0")
                .defineInRange("archGeomancerHealth", 160.0, 1.0, Double.MAX_VALUE);
        ArchGeomancerArmor = BUILDER.comment("How much natural Armor Arch Geomancers have, Default: 4.0")
                .defineInRange("archGeomancerArmor", 4.0, 0.0, Double.MAX_VALUE);
        ArchGeomancerArmorToughness = BUILDER.comment("How much natural Armor Toughness Arch Geomancers have, Default: 0.0")
                .defineInRange("archGeomancerArmorToughness", 0.0, 0.0, Double.MAX_VALUE);
        ArchGeomancerMovementSpeed = BUILDER.comment("How fast Arch Geomancers move, Default: 0.25")
                .defineInRange("archGeomancerMovementSpeed", 0.25, 0.0, Double.MAX_VALUE);
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
        PiglinServantMovementSpeed = BUILDER.comment("How fast Piglin Servants move, Default: 0.35")
                .defineInRange("piglinServantMovementSpeed", 0.35, 0.0, Double.MAX_VALUE);
        PiglinServantFollowRange = BUILDER.comment("How much following/detection range Piglin Servants have, Default: 16.0")
                .defineInRange("piglinServantFollowRange", 16.0, 1.0, 2048.0);
        BUILDER.pop();

        BUILDER.push("Piglin Brute Servant");
        PiglinBruteServantHealth = BUILDER.comment("How much Max Health Piglin Brute Servants have, Default: 50.0")
                .defineInRange("piglinBruteServantHealth", 50.0, 1.0, Double.MAX_VALUE);
        PiglinBruteServantArmor = BUILDER.comment("How much natural Armor Piglin Brute Servants have, Default: 0.0")
                .defineInRange("piglinBruteServantArmor", 0.0, 0.0, Double.MAX_VALUE);
        PiglinBruteServantDamage = BUILDER.comment("How much damage Piglin Brute Servants deals, Default: 7.0")
                .defineInRange("piglinBruteServantDamage", 7.0, 1.0, Double.MAX_VALUE);
        PiglinBruteServantMovementSpeed = BUILDER.comment("How fast Piglin Brute Servants move, Default: 0.35")
                .defineInRange("piglinBruteServantMovementSpeed", 0.35, 0.0, Double.MAX_VALUE);
        PiglinBruteServantFollowRange = BUILDER.comment("How much following/detection range Piglin Brute Servants have, Default: 16.0")
                .defineInRange("piglinBruteServantFollowRange", 16.0, 1.0, 2048.0);
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
        PiglinHunterServantMovementSpeed = BUILDER.comment("How fast Piglin Hunter Servants move, Default: 0.35")
                .defineInRange("piglinHunterServantMovementSpeed", 0.35, 0.0, Double.MAX_VALUE);
        PiglinHunterServantFollowRange = BUILDER.comment("How much following/detection range Piglin Hunter Servants have, Default: 16.0")
                .defineInRange("piglinHunterServantFollowRange", 16.0, 1.0, 2048.0);
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

        BUILDER.push("Fungus Thrower");
        FungusThrowerServantHealth = BUILDER.comment("How much Max Health Fungus Thrower have, Default: 20.0")
                .defineInRange("fungusThrowerHealth", 16.0, 1.0, Double.MAX_VALUE);
        FungusThrowerServantArmor = BUILDER.comment("How much natural Armor Fungus Thrower have, Default: 0.0")
                .defineInRange("fungusThrowerArmor", 0.0, 0.0, Double.MAX_VALUE);
        FungusThrowerServantDamage = BUILDER.comment("How much damage Fungus Thrower deal, Default: 3.0")
                .defineInRange("fungusThrowerDamage", 2.0, 1.0, Double.MAX_VALUE);
        FungusThrowerServantMovementSpeed = BUILDER.comment("How fast Fungus Thrower move, Default: 0.35")
                .defineInRange("fungusThrowerMovementSpeed", 0.35, 0.0, Double.MAX_VALUE);
        FungusThrowerServantFollowRange = BUILDER.comment("How much following/detection range Fungus Thrower have, Default: 32.0")
                .defineInRange("fungusThrowerFollowRange", 32.0, 1.0, Double.MAX_VALUE);
        BUILDER.pop();

        BUILDER.push("Crimson Spider Servant");
        CrimsonSpiderServantHealth = BUILDER
                .comment("How much Max Health Crimson Spider Servants have, Default: 24.0")
                .defineInRange("crimsonSpiderServantHealth", 24.0, 1.0, Double.MAX_VALUE);
        CrimsonSpiderServantDamage = BUILDER
                .comment("How much damage Crimson Spider Servants deal, Default: 5.0")
                .defineInRange("crimsonSpiderServantDamage", 5.0, 1.0, Double.MAX_VALUE);
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
        TwittollagerServantFollowRange = BUILDER.comment("How much following/detection range Twittollager Servants have, Default: 32.0")
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
        BUILDER.push("Disciple");
        DiscipleHealth = BUILDER.comment("How much Max Health Disciples have, Default: 50.0")
                .defineInRange("discipleHealth", 50.0, 1.0, Double.MAX_VALUE);
        DiscipleArmor = BUILDER.comment("How much natural Armor Disciples have, Default: 2.0")
                .defineInRange("discipleArmor", 2.0, 0.0, Double.MAX_VALUE);
        DiscipleDamage = BUILDER.comment("How much damage Disciples deal, Default: 3.0")
                .defineInRange("discipleDamage", 3.0, 1.0, Double.MAX_VALUE);
        DiscipleMovementSpeed = BUILDER.comment("How fast Disciples move, Default: 0.25")
                .defineInRange("discipleMovementSpeed", 0.35, 0.0, Double.MAX_VALUE);
        DiscipleFollowRange = BUILDER.comment("How much following/detection range Disciples have, Default: 32.0")
                .defineInRange("discipleFollowRange", 32.0, 1.0, Double.MAX_VALUE);
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

        BUILDER.push("AM Servants (Optional)");
        MurmurServantHealth = BUILDER.comment("How much Max Health Murmur Servants have, Default: 30.0")
                .defineInRange("murmurServantHealth", 30.0, 1.0, Double.MAX_VALUE);
        MurmurServantDamage = BUILDER.comment("How much damage Murmur Servants deal, Default: 3.0")
                .defineInRange("murmurServantDamage", 3.0, 1.0, Double.MAX_VALUE);
        MurmurServantMovementSpeed = BUILDER.comment("How fast Murmur Servants move, Default: 0.2")
                .defineInRange("murmurServantMovementSpeed", 0.2, 0.0, Double.MAX_VALUE);
        MurmurServantFollowRange = BUILDER.comment("How much following/detection range Murmur Servants have, Default: 48.0")
                .defineInRange("murmurServantFollowRange", 48.0, 0.0, Double.MAX_VALUE);
        MurmurServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Murmur Servants have, Default: 0.3")
                .defineInRange("murmurServantKnockbackResistance", 0.3, 0.0, Double.MAX_VALUE);
        FarseerServantHealth = BUILDER.comment("How much Max Health Farseer Servants have, Default: 70.0")
                .defineInRange("farseerServantHealth", 70.0, 1.0, Double.MAX_VALUE);
        FarseerServantArmor = BUILDER.comment("How much natural Armor Farseer Servants have, Default: 6.0")
                .defineInRange("farseerServantArmor", 6.0, 0.0, Double.MAX_VALUE);
        FarseerServantFlyingSpeed = BUILDER.comment("How fast Farseer Servants fly, Default: 0.5")
                .defineInRange("farseerServantFlyingSpeed", 0.5, 0.0, Double.MAX_VALUE);
        FarseerServantDamage = BUILDER.comment("How much damage Farseer Servants deal, Default: 4.5")
                .defineInRange("farseerServantDamage", 4.5, 1.0, Double.MAX_VALUE);
        FarseerServantMovementSpeed = BUILDER.comment("How fast Farseer Servants move, Default: 0.35")
                .defineInRange("farseerServantMovementSpeed", 0.35, 0.0, Double.MAX_VALUE);
        FarseerServantFollowRange = BUILDER.comment("How much following/detection range Farseer Servants have, Default: 16.0")
                .defineInRange("farseerServantFollowRange", 16.0, 0.0, Double.MAX_VALUE);
        CrimsonMosquitoServantHealth = BUILDER.comment("How much Max Health Crimson Mosquito Servants have, Default: 10.0")
                .defineInRange("crimsonMosquitoServantHealth", 10.0, 1.0, Double.MAX_VALUE);
        CrimsonMosquitoServantDamage = BUILDER.comment("How much damage Crimson Mosquito Servants deal, Default: 5.0")
                .defineInRange("crimsonMosquitoServantDamage", 5.0, 1.0, Double.MAX_VALUE);
        CrimsonMosquitoServantMovementSpeed = BUILDER.comment("How fast Crimson Mosquito Servants move, Default: 0.25")
                .defineInRange("crimsonMosquitoServantMovementSpeed", 0.25, 0.0, Double.MAX_VALUE);
        CrimsonMosquitoServantFollowRange = BUILDER.comment("How much following/detection range Crimson Mosquito Servants have, Default: 32.0")
                .defineInRange("crimsonMosquitoServantFollowRange", 32.0, 0.0, Double.MAX_VALUE);
        CrimsonMosquitoServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Crimson Mosquito Servants have, Default: 0.0")
                .defineInRange("crimsonMosquitoServantKnockbackResistance", 0.0, 0.0, Double.MAX_VALUE);
        CrimsonMosquitoFleeHealthThreshold = BUILDER.comment("Max Health threshold for Crimson Mosquito Servants to flee, Default: 100.0")
                .defineInRange("crimsonMosquitoFleeHealthThreshold", 100.0, 1.0, Double.MAX_VALUE);
        CrimsonMosquitoFleeHealthThresholdUnholy = BUILDER.comment("Max Health threshold for Crimson Mosquito Servants to flee when the owner wears Unholy equipment, Default: 200.0")
                .defineInRange("crimsonMosquitoFleeHealthThresholdUnholy", 200.0, 1.0, Double.MAX_VALUE);
        WarpedMoscoServantHealth = BUILDER.comment("How much Max Health Warped Mosco Servants have, Default: 100.0")
                .defineInRange("warpedMoscoServantHealth", 100.0, 1.0, Double.MAX_VALUE);
        WarpedMoscoServantDamage = BUILDER.comment("How much damage Warped Mosco Servants deal, Default: 10.0")
                .defineInRange("warpedMoscoServantDamage", 10.0, 1.0, Double.MAX_VALUE);
        WarpedMoscoServantMovementSpeed = BUILDER.comment("How fast Warped Mosco Servants move, Default: 0.3")
                .defineInRange("warpedMoscoServantMovementSpeed", 0.3, 0.0, Double.MAX_VALUE);
        WarpedMoscoServantFollowRange = BUILDER.comment("How much following/detection range Warped Mosco Servants have, Default: 64.0")
                .defineInRange("warpedMoscoServantFollowRange", 64.0, 0.0, Double.MAX_VALUE);
        WarpedMoscoServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Warped Mosco Servants have, Default: 1.0")
                .defineInRange("warpedMoscoServantKnockbackResistance", 1.0, 0.0, Double.MAX_VALUE);
        WarpedMoscoServantArmor = BUILDER.comment("How much natural Armor Warped Mosco Servants have, Default: 10.0")
                .defineInRange("warpedMoscoServantArmor", 10.0, 0.0, Double.MAX_VALUE);
        WarpedMoscoServantArmorToughness = BUILDER.comment("How much natural Armor Toughness Warped Mosco Servants have, Default: 2.0")
                .defineInRange("warpedMoscoServantArmorToughness", 2.0, 0.0, Double.MAX_VALUE);
        TusklinServantHealth = BUILDER.comment("How much Max Health Tusklin Servants have, Default: 40.0")
                .defineInRange("tusklinServantHealth", 40.0, 1.0, Double.MAX_VALUE);
        TusklinServantDamage = BUILDER.comment("How much damage Tusklin Servants deal, Default: 9.0")
                .defineInRange("tusklinServantDamage", 9.0, 1.0, Double.MAX_VALUE);
        TusklinServantMovementSpeed = BUILDER.comment("How fast Tusklin Servants move, Default: 0.3")
                .defineInRange("tusklinServantMovementSpeed", 0.3, 0.0, Double.MAX_VALUE);
        TusklinServantFollowRange = BUILDER.comment("How much following/detection range Tusklin Servants have, Default: 32.0")
                .defineInRange("tusklinServantFollowRange", 32.0, 0.0, Double.MAX_VALUE);
        TusklinServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Tusklin Servants have, Default: 0.9")
                .defineInRange("tusklinServantKnockbackResistance", 0.9, 0.0, Double.MAX_VALUE);
        GrottoceratopsServantHealth = BUILDER.comment("How much Max Health Grottoceratops Servants have, Default: 50.0 (matches Alex's Caves Grottoceratops)")
                .defineInRange("grottoceratopsServantHealth", 50.0, 1.0, Double.MAX_VALUE);
        GrottoceratopsServantDamage = BUILDER.comment("How much damage Grottoceratops Servants deal, Default: 10.0 (matches Alex's Caves Grottoceratops)")
                .defineInRange("grottoceratopsServantDamage", 10.0, 1.0, Double.MAX_VALUE);
        GrottoceratopsServantMovementSpeed = BUILDER.comment("How fast Grottoceratops Servants move, Default: 0.2")
                .defineInRange("grottoceratopsServantMovementSpeed", 0.2, 0.0, Double.MAX_VALUE);
        GrottoceratopsServantFollowRange = BUILDER.comment("How much following/detection range Grottoceratops Servants have, Default: 32.0")
                .defineInRange("grottoceratopsServantFollowRange", 32.0, 0.0, Double.MAX_VALUE);
        GrottoceratopsServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Grottoceratops Servants have, Default: 0.9 (matches Alex's Caves Grottoceratops)")
                .defineInRange("grottoceratopsServantKnockbackResistance", 0.9, 0.0, Double.MAX_VALUE);
        GrottoceratopsServantArmor = BUILDER.comment("How much natural Armor Grottoceratops Servants have, Default: 8.0 (matches Alex's Caves Grottoceratops)")
                .defineInRange("grottoceratopsServantArmor", 8.0, 0.0, Double.MAX_VALUE);
        TremorsaurusServantHealth = BUILDER.comment("How much Max Health Tremorsaurus Servants have, Default: 150.0 (matches Alex's Caves Tremorsaurus)")
                .defineInRange("tremorsaurusServantHealth", 150.0, 1.0, Double.MAX_VALUE);
        TremorsaurusServantDamage = BUILDER.comment("How much damage Tremorsaurus Servants deal, Default: 14.0 (matches Alex's Caves Tremorsaurus)")
                .defineInRange("tremorsaurusServantDamage", 14.0, 1.0, Double.MAX_VALUE);
        TremorsaurusServantMovementSpeed = BUILDER.comment("How fast Tremorsaurus Servants move, Default: 0.2")
                .defineInRange("tremorsaurusServantMovementSpeed", 0.2, 0.0, Double.MAX_VALUE);
        TremorsaurusServantFollowRange = BUILDER.comment("How much following/detection range Tremorsaurus Servants have, Default: 32.0")
                .defineInRange("tremorsaurusServantFollowRange", 32.0, 0.0, Double.MAX_VALUE);
        TremorsaurusServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Tremorsaurus Servants have, Default: 0.9 (matches Alex's Caves Tremorsaurus)")
                .defineInRange("tremorsaurusServantKnockbackResistance", 0.9, 0.0, Double.MAX_VALUE);
        TremorsaurusServantArmor = BUILDER.comment("How much natural Armor Tremorsaurus Servants have, Default: 8.0 (matches Alex's Caves Tremorsaurus)")
                .defineInRange("tremorsaurusServantArmor", 8.0, 0.0, Double.MAX_VALUE);
        TremorzillaServantHealth = BUILDER.comment("How much Max Health Tremorzilla Servants have, Default: 500.0 (matches Alex's Caves Tremorzilla)")
                .defineInRange("tremorzillaServantHealth", 500.0, 1.0, Double.MAX_VALUE);
        TremorzillaServantDamage = BUILDER.comment("How much damage Tremorzilla Servants deal, Default: 30.0 (matches Alex's Caves Tremorzilla)")
                .defineInRange("tremorzillaServantDamage", 30.0, 1.0, Double.MAX_VALUE);
        TremorzillaServantMovementSpeed = BUILDER.comment("How fast Tremorzilla Servants move, Default: 0.3")
                .defineInRange("tremorzillaServantMovementSpeed", 0.3, 0.0, Double.MAX_VALUE);
        TremorzillaServantFollowRange = BUILDER.comment("How much following/detection range Tremorzilla Servants have, Default: 32.0 (32 block radius)")
                .defineInRange("tremorzillaServantFollowRange", 32.0, 0.0, Double.MAX_VALUE);
        TremorzillaServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Tremorzilla Servants have, Default: 1.0 (matches Alex's Caves Tremorzilla)")
                .defineInRange("tremorzillaServantKnockbackResistance", 1.0, 0.0, Double.MAX_VALUE);
        TremorzillaServantArmor = BUILDER.comment("How much natural Armor Tremorzilla Servants have, Default: 10.0 (matches Alex's Caves Tremorzilla)")
                .defineInRange("tremorzillaServantArmor", 10.0, 0.0, Double.MAX_VALUE);
        TremorzillaServantTailHpPercentDamage = BUILDER.comment("How much HP percent damage Tremorzilla Servants' tail whip deals on top of the 30 base damage, Default: 0.05 (5% of target Max Health, like Goety's RedstoneMonstrosity)")
                .defineInRange("tremorzillaServantTailHpPercentDamage", 0.05, 0.0, 1.0);
        TremorzillaServantDamageCap = BUILDER.comment("Maximum damage a single hit can deal to Tremorzilla Servants, Default: 30.0 (like Goety's Vizier's VizierDamageCap; bypassed by damage that ignores invulnerability)")
                .defineInRange("tremorzillaServantDamageCap", 30.0, 1.0, Double.MAX_VALUE);
        VallumraptorServantHealth = BUILDER.comment("How much Max Health Vallumraptor Servants have, Default: 28.0 (matches Alex's Caves Vallumraptor)")
                .defineInRange("vallumraptorServantHealth", 28.0, 1.0, Double.MAX_VALUE);
        VallumraptorServantDamage = BUILDER.comment("How much damage Vallumraptor Servants deal, Default: 3.0 (matches Alex's Caves Vallumraptor)")
                .defineInRange("vallumraptorServantDamage", 3.0, 1.0, Double.MAX_VALUE);
        VallumraptorServantMovementSpeed = BUILDER.comment("How fast Vallumraptor Servants move, Default: 0.2")
                .defineInRange("vallumraptorServantMovementSpeed", 0.2, 0.0, Double.MAX_VALUE);
        VallumraptorServantFollowRange = BUILDER.comment("How much following/detection range Vallumraptor Servants have, Default: 32.0")
                .defineInRange("vallumraptorServantFollowRange", 32.0, 0.0, Double.MAX_VALUE);
        VallumraptorServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Vallumraptor Servants have, Default: 0.0 (matches Alex's Caves Vallumraptor)")
                .defineInRange("vallumraptorServantKnockbackResistance", 0.0, 0.0, Double.MAX_VALUE);
        VallumraptorServantArmor = BUILDER.comment("How much natural Armor Vallumraptor Servants have, Default: 0.0 (matches Alex's Caves Vallumraptor)")
                .defineInRange("vallumraptorServantArmor", 0.0, 0.0, Double.MAX_VALUE);
        VallumraptorServantElderHealth = BUILDER.comment("How much Max Health Vallumraptor Servant Elders have, Default: 32.0 (matches Alex's Caves Vallumraptor elder)")
                .defineInRange("vallumraptorServantElderHealth", 32.0, 1.0, Double.MAX_VALUE);
        VallumraptorServantElderArmor = BUILDER.comment("How much natural Armor Vallumraptor Servant Elders have, Default: 5.0 (matches Alex's Caves Vallumraptor elder)")
                .defineInRange("vallumraptorServantElderArmor", 5.0, 0.0, Double.MAX_VALUE);
        NucleeperServantHealth = BUILDER.comment("How much Max Health Nucleeper Servants have, Default: 40.0 (matches Alex's Caves Nucleeper)")
                .defineInRange("nucleeperServantHealth", 40.0, 1.0, Double.MAX_VALUE);
        NucleeperServantDamage = BUILDER.comment("How much damage Nucleeper Servants deal, Default: 2.0 (vanilla monster default; Nucleeper's real threat is its explosion)")
                .defineInRange("nucleeperServantDamage", 2.0, 1.0, Double.MAX_VALUE);
        NucleeperServantMovementSpeed = BUILDER.comment("How fast Nucleeper Servants move, Default: 0.2 (matches Alex's Caves Nucleeper)")
                .defineInRange("nucleeperServantMovementSpeed", 0.2, 0.0, Double.MAX_VALUE);
        NucleeperServantFollowRange = BUILDER.comment("How much following/detection range Nucleeper Servants have, Default: 32.0")
                .defineInRange("nucleeperServantFollowRange", 32.0, 0.0, Double.MAX_VALUE);
        NucleeperServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Nucleeper Servants have, Default: 0.0 (Alex's Caves Nucleeper has none)")
                .defineInRange("nucleeperServantKnockbackResistance", 0.0, 0.0, Double.MAX_VALUE);
        NucleeperServantArmor = BUILDER.comment("How much natural Armor Nucleeper Servants have, Default: 4.0 (matches Alex's Caves Nucleeper)")
                .defineInRange("nucleeperServantArmor", 4.0, 0.0, Double.MAX_VALUE);
        NucleeperServantTauntRange = BUILDER.comment("How far (in blocks) Nucleeper Servants taunt mobs that are attacking their owner, pulling the mob's aggro onto the servant. Default: 12.0 (set to 0.0 to disable taunting)")
                .defineInRange("nucleeperServantTauntRange", 12.0, 0.0, Double.MAX_VALUE);
        MineGuardianServantExplosionGriefing = BUILDER.comment("Whether Mine Guardian Servant explosions destroy blocks, Default: false (set to true to allow block destruction, which also respects the mobGriefing game rule)")
                .define("mineGuardianServantExplosionGriefing", false);
        HullbreakerServantBlockBreakGriefing = BUILDER.comment("Whether Hullbreaker Servants break blocks when bashing, Default: false (set to true to allow block destruction, which also respects the mobGriefing game rule)")
                .define("hullbreakerServantBlockBreakGriefing", false);
        HullbreakerServantGlowTargetRange = BUILDER.comment("How far (in blocks) Hullbreaker Servants search for glowing targets, Default: 48.0 (normal follow range is 16)")
                .defineInRange("hullbreakerServantGlowTargetRange", 48.0, 8.0, Double.MAX_VALUE);
        HullbreakerServantGlowChaseSpeed = BUILDER.comment("Swim speed Hullbreaker Servants use when chasing a glowing target, Default: 2.4 (normal approach speed is 1.6)")
                .defineInRange("hullbreakerServantGlowChaseSpeed", 2.4, 0.1, Double.MAX_VALUE);
        HullbreakerServantProximityTargetRange = BUILDER.comment("How close (in blocks) a non-glowing enemy must be for Hullbreaker Servants to sense and attack it, Default: 6.0")
                .defineInRange("hullbreakerServantProximityTargetRange", 6.0, 2.0, 64.0);
        BrainiacServantHealth = BUILDER.comment("How much Max Health Brainiac Servants have, Default: 40.0 (matches Alex's Caves Brainiac)")
                .defineInRange("brainiacServantHealth", 40.0, 1.0, Double.MAX_VALUE);
        BrainiacServantDamage = BUILDER.comment("How much damage Brainiac Servants deal, Default: 5.0 (matches Alex's Caves Brainiac)")
                .defineInRange("brainiacServantDamage", 5.0, 1.0, Double.MAX_VALUE);
        BrainiacServantMovementSpeed = BUILDER.comment("How fast Brainiac Servants move, Default: 0.25 (matches Alex's Caves Brainiac)")
                .defineInRange("brainiacServantMovementSpeed", 0.25, 0.0, Double.MAX_VALUE);
        BrainiacServantFollowRange = BUILDER.comment("How much following/detection range Brainiac Servants have, Default: 32.0")
                .defineInRange("brainiacServantFollowRange", 32.0, 0.0, Double.MAX_VALUE);
        BrainiacServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Brainiac Servants have, Default: 0.0 (Alex's Caves Brainiac has none)")
                .defineInRange("brainiacServantKnockbackResistance", 0.0, 0.0, Double.MAX_VALUE);
        BrainiacServantArmor = BUILDER.comment("How much natural Armor Brainiac Servants have, Default: 8.0 (matches Alex's Caves Brainiac)")
                .defineInRange("brainiacServantArmor", 8.0, 0.0, Double.MAX_VALUE);
        CaniacServantHealth = BUILDER.comment("How much Max Health Caniac Servants have, Default: 38.0 (matches Alex's Caves Caniac)")
                .defineInRange("caniacServantHealth", 38.0, 1.0, Double.MAX_VALUE);
        CaniacServantDamage = BUILDER.comment("How much damage Caniac Servants deal, Default: 2.0 (matches Alex's Caves Caniac)")
                .defineInRange("caniacServantDamage", 2.0, 1.0, Double.MAX_VALUE);
        CaniacServantMovementSpeed = BUILDER.comment("How fast Caniac Servants move, Default: 0.25 (matches Alex's Caves Caniac; chased running speed boosts to 0.4)")
                .defineInRange("caniacServantMovementSpeed", 0.25, 0.0, Double.MAX_VALUE);
        CaniacServantFollowRange = BUILDER.comment("How much following/detection range Caniac Servants have, Default: 32.0")
                .defineInRange("caniacServantFollowRange", 32.0, 0.0, Double.MAX_VALUE);
        CaniacServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Caniac Servants have, Default: 0.0 (Alex's Caves Caniac has none)")
                .defineInRange("caniacServantKnockbackResistance", 0.0, 0.0, Double.MAX_VALUE);
        CaniacServantArmor = BUILDER.comment("How much natural Armor Caniac Servants have, Default: 0.0 (matches Alex's Caves Caniac)")
                .defineInRange("caniacServantArmor", 0.0, 0.0, Double.MAX_VALUE);
        GummyBearServantHealth = BUILDER.comment("How much Max Health Gummy Bear Servants have, Default: 36.0 (matches Alex's Caves Gummy Bear)")
                .defineInRange("gummyBearServantHealth", 36.0, 1.0, Double.MAX_VALUE);
        GummyBearServantDamage = BUILDER.comment("How much damage Gummy Bear Servants deal, Default: 4.0 (matches Alex's Caves Gummy Bear)")
                .defineInRange("gummyBearServantDamage", 4.0, 1.0, Double.MAX_VALUE);
        GummyBearServantMovementSpeed = BUILDER.comment("How fast Gummy Bear Servants move, Default: 0.25 (matches Alex's Caves Gummy Bear)")
                .defineInRange("gummyBearServantMovementSpeed", 0.25, 0.0, Double.MAX_VALUE);
        GummyBearServantFollowRange = BUILDER.comment("How much following/detection range Gummy Bear Servants have, Default: 32.0")
                .defineInRange("gummyBearServantFollowRange", 32.0, 0.0, Double.MAX_VALUE);
        GummyBearServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Gummy Bear Servants have, Default: 0.0 (Alex's Caves Gummy Bear has none)")
                .defineInRange("gummyBearServantKnockbackResistance", 0.0, 0.0, Double.MAX_VALUE);
        GummyBearServantArmor = BUILDER.comment("How much natural Armor Gummy Bear Servants have, Default: 0.0 (matches Alex's Caves Gummy Bear)")
                .defineInRange("gummyBearServantArmor", 0.0, 0.0, Double.MAX_VALUE);
        CaramelCubeServantHealth = BUILDER.comment("Base Max Health of Caramel Cube Servants (size 0), Default: 4.0 (matches Alex's Caves Caramel Cube); larger sizes add +6 per size")
                .defineInRange("caramelCubeServantHealth", 4.0, 1.0, Double.MAX_VALUE);
        CaramelCubeServantDamage = BUILDER.comment("Base attack damage of Caramel Cube Servants (size 0), Default: 2.0 (matches Alex's Caves Caramel Cube); larger sizes add +2 per size")
                .defineInRange("caramelCubeServantDamage", 2.0, 1.0, Double.MAX_VALUE);
        CaramelCubeServantMovementSpeed = BUILDER.comment("Base movement speed of Caramel Cube Servants (size 0), Default: 0.25 (matches Alex's Caves Caramel Cube); larger sizes add +0.1 per size")
                .defineInRange("caramelCubeServantMovementSpeed", 0.25, 0.0, Double.MAX_VALUE);
        CaramelCubeServantFollowRange = BUILDER.comment("How much following/detection range Caramel Cube Servants have, Default: 32.0")
                .defineInRange("caramelCubeServantFollowRange", 32.0, 0.0, Double.MAX_VALUE);
        CaramelCubeServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Caramel Cube Servants have, Default: 0.0 (Alex's Caves Caramel Cube has none)")
                .defineInRange("caramelCubeServantKnockbackResistance", 0.0, 0.0, Double.MAX_VALUE);
        CaramelCubeServantArmor = BUILDER.comment("How much natural Armor Caramel Cube Servants have, Default: 0.0 (matches Alex's Caves Caramel Cube)")
                .defineInRange("caramelCubeServantArmor", 0.0, 0.0, Double.MAX_VALUE);
        GumbeeperServantHealth = BUILDER.comment("How much Max Health Gumbeeper Servants have, Default: 14.0 (matches Alex's Caves Gumbeeper)")
                .defineInRange("gumbeeperServantHealth", 14.0, 1.0, Double.MAX_VALUE);
        GumbeeperServantDamage = BUILDER.comment("How much damage Gumbeeper Servants' gumballs deal, Default: 4.0 (matches Alex's Caves Gumbeeper)")
                .defineInRange("gumbeeperServantDamage", 4.0, 1.0, Double.MAX_VALUE);
        GumbeeperServantMovementSpeed = BUILDER.comment("How fast Gumbeeper Servants move, Default: 0.2 (matches Alex's Caves Gumbeeper)")
                .defineInRange("gumbeeperServantMovementSpeed", 0.2, 0.0, Double.MAX_VALUE);
        GumbeeperServantFollowRange = BUILDER.comment("How much following/detection range Gumbeeper Servants have, Default: 32.0")
                .defineInRange("gumbeeperServantFollowRange", 32.0, 0.0, Double.MAX_VALUE);
        GumbeeperServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Gumbeeper Servants have, Default: 0.0 (Alex's Caves Gumbeeper has none)")
                .defineInRange("gumbeeperServantKnockbackResistance", 0.0, 0.0, Double.MAX_VALUE);
        GumbeeperServantArmor = BUILDER.comment("How much natural Armor Gumbeeper Servants have, Default: 4.0 (matches Alex's Caves Gumbeeper)")
                .defineInRange("gumbeeperServantArmor", 4.0, 0.0, Double.MAX_VALUE);
        VesperServantHealth = BUILDER.comment("How much Max Health Vesper Servants have, Default: 16.0 (matches Alex's Caves Vesper)")
                .defineInRange("vesperServantHealth", 16.0, 1.0, Double.MAX_VALUE);
        VesperServantDamage = BUILDER.comment("How much damage Vesper Servants deal, Default: 3.0 (matches Alex's Caves Vesper)")
                .defineInRange("vesperServantDamage", 3.0, 1.0, Double.MAX_VALUE);
        VesperServantMovementSpeed = BUILDER.comment("How fast Vesper Servants fly, Default: 0.25 (matches Alex's Caves Vesper; flight is driven by the custom flight move controller)")
                .defineInRange("vesperServantMovementSpeed", 0.25, 0.0, Double.MAX_VALUE);
        VesperServantFollowRange = BUILDER.comment("How much following/detection range Vesper Servants have, Default: 32.0 (Alex's Caves Vesper natural mob uses 52; a summon uses a tighter 32 so it does not dart after far-away mobs)")
                .defineInRange("vesperServantFollowRange", 32.0, 0.0, Double.MAX_VALUE);
        VesperServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Vesper Servants have, Default: 0.0 (Alex's Caves Vesper has none)")
                .defineInRange("vesperServantKnockbackResistance", 0.0, 0.0, Double.MAX_VALUE);
        VesperServantArmor = BUILDER.comment("How much natural Armor Vesper Servants have, Default: 0.0 (matches Alex's Caves Vesper)")
                .defineInRange("vesperServantArmor", 0.0, 0.0, Double.MAX_VALUE);
        TeletorServantHealth = BUILDER.comment("How much Max Health Teletor Servants have, Default: 18.0 (matches Alex's Caves Teletor)")
                .defineInRange("teletorServantHealth", 18.0, 1.0, Double.MAX_VALUE);
        TeletorServantDamage = BUILDER.comment("How much damage Teletor Servants deal, Default: 2.0 (matches Alex's Caves Teletor)")
                .defineInRange("teletorServantDamage", 2.0, 1.0, Double.MAX_VALUE);
        TeletorServantMovementSpeed = BUILDER.comment("How fast Teletor Servants move, Default: 0.2 (matches Alex's Caves Teletor; flight is driven by the custom hover move controller)")
                .defineInRange("teletorServantMovementSpeed", 0.2, 0.0, Double.MAX_VALUE);
        TeletorServantFollowRange = BUILDER.comment("How much following/detection range Teletor Servants have, Default: 32.0 (matches Alex's Caves Teletor)")
                .defineInRange("teletorServantFollowRange", 32.0, 0.0, Double.MAX_VALUE);
        TeletorServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Teletor Servants have, Default: 0.0 (Alex's Caves Teletor has none)")
                .defineInRange("teletorServantKnockbackResistance", 0.0, 0.0, Double.MAX_VALUE);
        TeletorServantArmor = BUILDER.comment("How much natural Armor Teletor Servants have, Default: 0.0 (matches Alex's Caves Teletor)")
                .defineInRange("teletorServantArmor", 0.0, 0.0, Double.MAX_VALUE);
        ForsakenServantHealth = BUILDER.comment("How much Max Health Forsaken Servants have, Default: 250.0 (matches Alex's Caves Forsaken)")
                .defineInRange("forsakenServantHealth", 250.0, 1.0, Double.MAX_VALUE);
        ForsakenServantDamage = BUILDER.comment("How much damage Forsaken Servants deal, Default: 10.0 (matches Alex's Caves Forsaken)")
                .defineInRange("forsakenServantDamage", 10.0, 1.0, Double.MAX_VALUE);
        ForsakenServantMovementSpeed = BUILDER.comment("How fast Forsaken Servants move when walking, Default: 0.25 (matches Alex's Caves Forsaken; sprint/chase momentarily boosts this ~1.8x)")
                .defineInRange("forsakenServantMovementSpeed", 0.25, 0.0, Double.MAX_VALUE);
        ForsakenServantFollowRange = BUILDER.comment("How much following/detection range Forsaken Servants have, Default: 64.0 (matches Alex's Caves Forsaken)")
                .defineInRange("forsakenServantFollowRange", 64.0, 0.0, Double.MAX_VALUE);
        ForsakenServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Forsaken Servants have, Default: 0.6 (matches Alex's Caves Forsaken)")
                .defineInRange("forsakenServantKnockbackResistance", 0.6, 0.0, Double.MAX_VALUE);
        GammaroachServantHealth = BUILDER.comment("How much Max Health Gammaroach Servants have, Default: 14.0 (matches Alex's Caves Gammaroach)")
                .defineInRange("gammaroachServantHealth", 14.0, 1.0, Double.MAX_VALUE);
        GammaroachServantDamage = BUILDER.comment("How much damage Gammaroach Servants deal, Default: 2.0 (matches Alex's Caves Gammaroach)")
                .defineInRange("gammaroachServantDamage", 2.0, 1.0, Double.MAX_VALUE);
        GammaroachServantMovementSpeed = BUILDER.comment("How fast Gammaroach Servants move, Default: 0.4 (matches Alex's Caves Gammaroach)")
                .defineInRange("gammaroachServantMovementSpeed", 0.4, 0.0, Double.MAX_VALUE);
        GammaroachServantFollowRange = BUILDER.comment("How much following/detection range Gammaroach Servants have, Default: 32.0")
                .defineInRange("gammaroachServantFollowRange", 32.0, 0.0, Double.MAX_VALUE);
        GammaroachServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Gammaroach Servants have, Default: 0.0 (Alex's Caves Gammaroach has none)")
                .defineInRange("gammaroachServantKnockbackResistance", 0.0, 0.0, Double.MAX_VALUE);
        GammaroachServantArmor = BUILDER.comment("How much natural Armor Gammaroach Servants have, Default: 0.0 (matches Alex's Caves Gammaroach)")
                .defineInRange("gammaroachServantArmor", 0.0, 0.0, Double.MAX_VALUE);
        CorrodentServantHealth = BUILDER.comment("How much Max Health Corrodent Servants have, Default: 16.0 (matches Alex's Caves Corrodent)")
                .defineInRange("corrodentServantHealth", 16.0, 1.0, Double.MAX_VALUE);
        CorrodentServantDamage = BUILDER.comment("How much damage Corrodent Servants deal, Default: 3.0 (matches Alex's Caves Corrodent)")
                .defineInRange("corrodentServantDamage", 3.0, 1.0, Double.MAX_VALUE);
        CorrodentServantMovementSpeed = BUILDER.comment("How fast Corrodent Servants move, Default: 0.25 (matches Alex's Caves Corrodent)")
                .defineInRange("corrodentServantMovementSpeed", 0.25, 0.0, Double.MAX_VALUE);
        CorrodentServantFollowRange = BUILDER.comment("How much following/detection range Corrodent Servants have, Default: 32.0")
                .defineInRange("corrodentServantFollowRange", 32.0, 0.0, Double.MAX_VALUE);
        CorrodentServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Corrodent Servants have, Default: 0.0 (Alex's Caves Corrodent has none)")
                .defineInRange("corrodentServantKnockbackResistance", 0.0, 0.0, Double.MAX_VALUE);
        CorrodentServantArmor = BUILDER.comment("How much natural Armor Corrodent Servants have, Default: 2.0 (matches Alex's Caves Corrodent)")
                .defineInRange("corrodentServantArmor", 2.0, 0.0, Double.MAX_VALUE);
        DeepOneServantHealth = BUILDER.comment("How much Max Health Deep One Servants have, Default: 30.0 (matches Alex's Caves Deep One)")
                .defineInRange("deepOneServantHealth", 30.0, 1.0, Double.MAX_VALUE);
        DeepOneServantDamage = BUILDER.comment("How much damage Deep One Servants deal, Default: 3.0 (matches Alex's Caves Deep One)")
                .defineInRange("deepOneServantDamage", 3.0, 1.0, Double.MAX_VALUE);
        DeepOneServantMovementSpeed = BUILDER.comment("How fast Deep One Servants move, Default: 0.25 (matches Alex's Caves Deep One)")
                .defineInRange("deepOneServantMovementSpeed", 0.25, 0.0, Double.MAX_VALUE);
        DeepOneServantFollowRange = BUILDER.comment("How much following/detection range Deep One Servants have, Default: 32.0")
                .defineInRange("deepOneServantFollowRange", 32.0, 0.0, Double.MAX_VALUE);
        DeepOneServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Deep One Servants have, Default: 0.0 (Alex's Caves Deep One has none)")
                .defineInRange("deepOneServantKnockbackResistance", 0.0, 0.0, Double.MAX_VALUE);
        DeepOneServantArmor = BUILDER.comment("How much natural Armor Deep One Servants have, Default: 0.0 (Alex's Caves Deep One has none)")
                .defineInRange("deepOneServantArmor", 0.0, 0.0, Double.MAX_VALUE);
        DeepOneKnightServantHealth = BUILDER.comment("How much Max Health Deep One Knight Servants have, Default: 60.0 (matches Alex's Caves Deep One Knight)")
                .defineInRange("deepOneKnightServantHealth", 60.0, 1.0, Double.MAX_VALUE);
        DeepOneKnightServantDamage = BUILDER.comment("How much damage Deep One Knight Servants deal, Default: 5.0 (matches Alex's Caves Deep One Knight)")
                .defineInRange("deepOneKnightServantDamage", 5.0, 1.0, Double.MAX_VALUE);
        DeepOneKnightServantMovementSpeed = BUILDER.comment("How fast Deep One Knight Servants move, Default: 0.25 (matches Alex's Caves Deep One Knight)")
                .defineInRange("deepOneKnightServantMovementSpeed", 0.25, 0.0, Double.MAX_VALUE);
        DeepOneKnightServantFollowRange = BUILDER.comment("How much following/detection range Deep One Knight Servants have, Default: 32.0")
                .defineInRange("deepOneKnightServantFollowRange", 32.0, 0.0, Double.MAX_VALUE);
        DeepOneKnightServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Deep One Knight Servants have, Default: 0.0 (Alex's Caves Deep One Knight has none)")
                .defineInRange("deepOneKnightServantKnockbackResistance", 0.0, 0.0, Double.MAX_VALUE);
        DeepOneKnightServantArmor = BUILDER.comment("How much natural Armor Deep One Knight Servants have, Default: 0.0 (Alex's Caves Deep One Knight has none)")
                .defineInRange("deepOneKnightServantArmor", 0.0, 0.0, Double.MAX_VALUE);
        // 奥托兰长矛(Ortholance)专属:先近距离扇形水浪(带击退、固定伤害5),再冲刺追击,整套共享一个冷却
        // (fan 图案 ±(60-15*wave)°;冲刺速度0.8格/tick、距离=速度×tick数)
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
        DeepOneKnightServantOrtholanceWaveScale = BUILDER.comment("Wave scale of Ortholance waves, Default: 1.0 (matches Ortholance normal waves; scale+4 damage = 5.0)")
                .defineInRange("deepOneKnightServantOrtholanceWaveScale", 1.0, 0.5, 10.0);
        DeepOneMageServantHealth = BUILDER.comment("How much Max Health Deep One Mage Servants have, Default: 80.0 (matches Alex's Caves Deep One Mage)")
                .defineInRange("deepOneMageServantHealth", 80.0, 1.0, Double.MAX_VALUE);
        DeepOneMageServantDamage = BUILDER.comment("How much damage Deep One Mage Servants deal, Default: 4.0 (matches Alex's Caves Deep One Mage)")
                .defineInRange("deepOneMageServantDamage", 4.0, 1.0, Double.MAX_VALUE);
        DeepOneMageServantMovementSpeed = BUILDER.comment("How fast Deep One Mage Servants move, Default: 0.25 (matches Alex's Caves Deep One Mage)")
                .defineInRange("deepOneMageServantMovementSpeed", 0.25, 0.0, Double.MAX_VALUE);
        DeepOneMageServantFollowRange = BUILDER.comment("How much following/detection range Deep One Mage Servants have, Default: 32.0")
                .defineInRange("deepOneMageServantFollowRange", 32.0, 0.0, Double.MAX_VALUE);
        DeepOneMageServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Deep One Mage Servants have, Default: 0.0 (Alex's Caves Deep One Mage has none)")
                .defineInRange("deepOneMageServantKnockbackResistance", 0.0, 0.0, Double.MAX_VALUE);
        DeepOneMageServantArmor = BUILDER.comment("How much natural Armor Deep One Mage Servants have, Default: 0.0 (Alex's Caves Deep One Mage has none)")
                .defineInRange("deepOneMageServantArmor", 0.0, 0.0, Double.MAX_VALUE);
        FroststalkerServantHealth = BUILDER.comment("How much Max Health Froststalker Servants have, Default: 24.0 (matches Alex's Mobs Froststalker)")
                .defineInRange("froststalkerServantHealth", 24.0, 1.0, Double.MAX_VALUE);
        FroststalkerServantDamage = BUILDER.comment("How much damage Froststalker Servants deal, Default: 4.5 (matches Alex's Mobs Froststalker)")
                .defineInRange("froststalkerServantDamage", 4.5, 1.0, Double.MAX_VALUE);
        FroststalkerServantFollowRange = BUILDER.comment("How much following/detection range Froststalker Servants have, Default: 16.0 (Alex's Mobs vanilla monster default)")
                .defineInRange("froststalkerServantFollowRange", 16.0, 0.0, Double.MAX_VALUE);
        FroststalkerServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Froststalker Servants have, Default: 0.0 (Alex's Mobs Froststalker has none)")
                .defineInRange("froststalkerServantKnockbackResistance", 0.0, 0.0, Double.MAX_VALUE);
        FroststalkerServantArmor = BUILDER.comment("How much natural Armor Froststalker Servants have, Default: 2.0 (matches Alex's Mobs Froststalker)")
                .defineInRange("froststalkerServantArmor", 2.0, 0.0, Double.MAX_VALUE);
        DropBearServantHealth = BUILDER.comment("How much Max Health Drop Bear Servants have, Default: 22.0 (matches Alex's Mobs drop bear)")
                .defineInRange("dropBearServantHealth", 22.0, 1.0, Double.MAX_VALUE);
        DropBearServantDamage = BUILDER.comment("How much damage Drop Bear Servants deal, Default: 2.0 (matches Alex's Mobs drop bear)")
                .defineInRange("dropBearServantDamage", 2.0, 1.0, Double.MAX_VALUE);
        DropBearServantFollowRange = BUILDER.comment("How much following/detection range Drop Bear Servants have, Default: 20.0 (matches Alex's Mobs drop bear)")
                .defineInRange("dropBearServantFollowRange", 20.0, 0.0, Double.MAX_VALUE);
        DropBearServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Drop Bear Servants have, Default: 0.7 (matches Alex's Mobs drop bear)")
                .defineInRange("dropBearServantKnockbackResistance", 0.7, 0.0, Double.MAX_VALUE);
        DropBearServantArmor = BUILDER.comment("How much Armor Drop Bear Servants have, Default: 0 (matches Alex's Mobs drop bear)")
                .defineInRange("dropBearServantKnockbackResistance", 0, 0.0, Double.MAX_VALUE);
        GusterServantHealth = BUILDER.comment("How much Max Health Guster Servants have, Default: 16.0 (matches Alex's Mobs guster)")
                .defineInRange("gusterServantHealth", 16.0, 1.0, Double.MAX_VALUE);
        GusterServantDamage = BUILDER.comment("How much damage Guster Servants deal, Default: 1.0 (matches Alex's Mobs guster)")
                .defineInRange("gusterServantDamage", 1.0, 1.0, Double.MAX_VALUE);
        GusterServantFollowRange = BUILDER.comment("How much following/detection range Guster Servants have, Default: 32.0 (matches Alex's Mobs guster)")
                .defineInRange("gusterServantFollowRange", 32.0, 0.0, Double.MAX_VALUE);
        GusterServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Guster Servants have, Default: 0.0 (matches Alex's Mobs guster)")
                .defineInRange("gusterServantKnockbackResistance", 0.0, 0.0, Double.MAX_VALUE);
        RockyRollerServantHealth = BUILDER.comment("How much Max Health Rocky Roller Servants have, Default: 10.0 (matches Alex's Mobs rocky roller)")
                .defineInRange("rockyRollerServantHealth", 10.0, 1.0, Double.MAX_VALUE);
        RockyRollerServantDamage = BUILDER.comment("How much damage Rocky Roller Servants deal, Default: 2.0 (matches Alex's Mobs rocky roller)")
                .defineInRange("rockyRollerServantDamage", 2.0, 1.0, Double.MAX_VALUE);
        RockyRollerServantMovementSpeed = BUILDER.comment("How fast Rocky Roller Servants move, Default: 0.25 (matches Alex's Mobs rocky roller)")
                .defineInRange("rockyRollerServantMovementSpeed", 0.25, 0.0, Double.MAX_VALUE);
        RockyRollerServantFollowRange = BUILDER.comment("How much following/detection range Rocky Roller Servants have, Default: 20.0 (Alex's Mobs rocky roller)")
                .defineInRange("rockyRollerServantFollowRange", 20.0, 0.0, Double.MAX_VALUE);
        RockyRollerServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Rocky Roller Servants have, Default: 0.7 (matches Alex's Mobs rocky roller)")
                .defineInRange("rockyRollerServantKnockbackResistance", 0.7, 0.0, Double.MAX_VALUE);
        RockyRollerServantArmor = BUILDER.comment("How much natural Armor Rocky Roller Servants have, Default: 20.0 (matches Alex's Mobs rocky roller)")
                .defineInRange("rockyRollerServantArmor", 20.0, 0.0, Double.MAX_VALUE);
        ZombieCrocodileServantHealth = BUILDER.comment("How much Max Health Zombie Crocodile Servants have, Default: 30.0")
                .defineInRange("zombieCrocodileServantHealth", 30.0, 1.0, Double.MAX_VALUE);
        ZombieCrocodileServantDamage = BUILDER.comment("How much damage Zombie Crocodile Servants deal, Default: 10.0")
                .defineInRange("zombieCrocodileServantDamage", 10.0, 1.0, Double.MAX_VALUE);
        ZombieCrocodileServantMovementSpeed = BUILDER.comment("How fast Zombie Crocodile Servants move, Default: 0.25")
                .defineInRange("zombieCrocodileServantMovementSpeed", 0.25, 0.0, Double.MAX_VALUE);
        ZombieCrocodileServantFollowRange = BUILDER.comment("How much following/detection range Zombie Crocodile Servants have, Default: 15.0")
                .defineInRange("zombieCrocodileServantFollowRange", 15.0, 0.0, Double.MAX_VALUE);
        ZombieCrocodileServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Zombie Crocodile Servants have, Default: 0.4")
                .defineInRange("zombieCrocodileServantKnockbackResistance", 0.4, 0.0, Double.MAX_VALUE);
        ZombieCrocodileServantArmor = BUILDER.comment("How much natural Armor Zombie Crocodile Servants have, Default: 8.0")
                .defineInRange("zombieCrocodileServantArmor", 8.0, 0.0, Double.MAX_VALUE);
        SkelewagServantHealth = BUILDER.comment("How much Max Health Skelewag Servants have, Default: 20.0 (matches Alex's Mobs Skelewag)")
                .defineInRange("skelewagServantHealth", 20.0, 1.0, Double.MAX_VALUE);
        SkelewagServantDamage = BUILDER.comment("How much damage Skelewag Servants deal, Default: 3.0 (matches Alex's Mobs Skelewag)")
                .defineInRange("skelewagServantDamage", 3.0, 1.0, Double.MAX_VALUE);
        SkelewagServantMovementSpeed = BUILDER.comment("How fast Skelewag Servants move, Default: 0.45 (matches Alex's Mobs Skelewag)")
                .defineInRange("skelewagServantMovementSpeed", 0.45, 0.0, Double.MAX_VALUE);
        SkelewagServantFollowRange = BUILDER.comment("How much following/detection range Skelewag Servants have, Default: 16.0 (Alex's Mobs vanilla monster default)")
                .defineInRange("skelewagServantFollowRange", 16.0, 0.0, Double.MAX_VALUE);
        SkelewagServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Skelewag Servants have, Default: 0.0 (Alex's Mobs Skelewag has none)")
                .defineInRange("skelewagServantKnockbackResistance", 0.0, 0.0, Double.MAX_VALUE);
        BunfungusServantHealth = BUILDER.comment("How much Max Health Bunfungus Servants have, Default: 80.0 (matches Alex's Mobs bunfungus)")
                .defineInRange("bunfungusServantHealth", 80.0, 1.0, Double.MAX_VALUE);
        BunfungusServantDamage = BUILDER.comment("How much damage Bunfungus Servants deal, Default: 8.0 (matches Alex's Mobs bunfungus)")
                .defineInRange("bunfungusServantDamage", 8.0, 1.0, Double.MAX_VALUE);
        BunfungusServantMovementSpeed = BUILDER.comment("How fast Bunfungus Servants move, Default: 0.32 (matches Alex's Mobs bunfungus)")
                .defineInRange("bunfungusServantMovementSpeed", 0.32, 0.0, Double.MAX_VALUE);
        BunfungusServantArmor = BUILDER.comment("How much Armor Bunfungus Servants have, Default: 0.21 (matches Alex's Mobs bunfungus)")
                .defineInRange("bunfungusServantArmor", 0.21, 0.0, Double.MAX_VALUE);
        BunfungusServantFollowRange = BUILDER.comment("How much following/detection range Bunfungus Servants have, Default: 16.0 (Alex's Mobs bunfungus uses the vanilla Mob default of 16)")
                .defineInRange("bunfungusServantFollowRange", 16.0, 0.0, Double.MAX_VALUE);
        BunfungusServantContactDamage = BUILDER.comment("Mid-air contact damage Bunfungus Servants deal when landing on a target, Default: 10.0 (matches Alex's Mobs hardcoded value)")
                .defineInRange("bunfungusServantContactDamage", 10.0, 1.0, Double.MAX_VALUE);
        IllagerElephantServantHealth = BUILDER.comment("How much Max Health Illager Elephant Servants have, Default: 150.0 (matches Alex's Mobs tusked elephant spawn health)")
                .defineInRange("illagerElephantServantHealth", 150.0, 1.0, Double.MAX_VALUE);
        IllagerElephantServantDamage = BUILDER.comment("How much damage Illager Elephant Servants deal, Default: 15.0 (matches Alex's Mobs tusked elephant)")
                .defineInRange("illagerElephantServantDamage", 15.0, 1.0, Double.MAX_VALUE);
        IllagerElephantServantMovementSpeed = BUILDER.comment("How fast Illager Elephant Servants move, Default: 0.35 (matches Alex's Mobs tusked elephant)")
                .defineInRange("illagerElephantServantMovementSpeed", 0.35, 0.0, Double.MAX_VALUE);
        IllagerElephantServantFollowRange = BUILDER.comment("How much following/detection range Illager Elephant Servants have, Default: 32.0")
                .defineInRange("illagerElephantServantFollowRange", 32.0, 0.0, Double.MAX_VALUE);
        IllagerElephantServantKnockbackResistance = BUILDER.comment("How much Knockback Resistance Illager Elephant Servants have, Default: 0.9")
                .defineInRange("illagerElephantServantKnockbackResistance", 0.9, 0.0, Double.MAX_VALUE);
        ServantCentipedeHealth = BUILDER.comment("Max health of Servant Centipede Head (Default: 35)")
                .defineInRange("servantCentipedeHealth", 35.0, 1.0, 1000.0);
        ServantCentipedeFollowRange = BUILDER.comment("Follow range of Servant Centipede (Default: 32)")
                .defineInRange("servantCentipedeFollowRange", 32.0, 1.0, 1000.0);
        ServantCentipedeArmor = BUILDER.comment("Armor of Servant Centipede (Default: 6)")
                .defineInRange("servantCentipedeArmor", 6.0, 0.0, 100.0);
        ServantCentipedeAttackDamage = BUILDER.comment("Attack damage of Servant Centipede (Default: 8)")
                .defineInRange("servantCentipedeAttackDamage", 8.0, 1.0, 1000.0);
        ServantCentipedeKnockbackResistance = BUILDER.comment("Knockback resistance of Servant Centipede (Default: 0.5)")
                .defineInRange("servantCentipedeKnockbackResistance", 0.5, 0.0, 1.0);
        ServantCentipedeMovementSpeed = BUILDER.comment("Movement speed of Servant Centipede Head (Default: 0.22)")
                .defineInRange("servantCentipedeMovementSpeed", 0.22, 0.0, 1.0);
        ServantCentipedeBodyHealth = BUILDER.comment("Max health of Servant Centipede Body segments (Default: 10)")
                .defineInRange("servantCentipedeBodyHealth", 10.0, 1.0, 1000.0);
        ServantCentipedeBodyMovementSpeed = BUILDER.comment("Movement speed of Servant Centipede Body (Default: 0.25)")
                .defineInRange("servantCentipedeBodyMovementSpeed", 0.25, 0.0, 1.0);
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

        BUILDER.push("Rambler Servant");
        RamblerServantHealth = BUILDER.comment("How much Max Health Rambler Servants have, Default: 60.0 (matches Opposing Force Rambler)")
                .defineInRange("ramblerServantHealth", 60.0, 1.0, Double.MAX_VALUE);
        RamblerServantMovementSpeed = BUILDER.comment("How much Movement Speed Rambler Servants have, Default: 0.15 (matches Opposing Force Rambler)")
                .defineInRange("ramblerServantMovementSpeed", 0.15, 0.0, Double.MAX_VALUE);
        RamblerServantAttackDamage = BUILDER.comment("How much Attack Damage Rambler Servants have, Default: 5.0 (matches Opposing Force Rambler)")
                .defineInRange("ramblerServantAttackDamage", 5.0, 0.0, Double.MAX_VALUE);
        RamblerServantAttackKnockback = BUILDER.comment("How much Attack Knockback Rambler Servants have, Default: 0.3 (matches Opposing Force Rambler)")
                .defineInRange("ramblerServantAttackKnockback", 0.3, 0.0, Double.MAX_VALUE);
        BUILDER.pop();

        BUILDER.push("Dicer Servant");
        DicerServantHealth = BUILDER.comment("How much Max Health Dicer Servants have, Default: 36.0 (matches Opposing Force Dicer)")
                .defineInRange("dicerServantHealth", 36.0, 1.0, Double.MAX_VALUE);
        DicerServantMovementSpeed = BUILDER.comment("How much Movement Speed Dicer Servants have, Default: 0.2 (matches Opposing Force Dicer)")
                .defineInRange("dicerServantMovementSpeed", 0.2, 0.0, Double.MAX_VALUE);
        DicerServantAttackDamage = BUILDER.comment("How much Attack Damage Dicer Servants have, Default: 8.0 (matches Opposing Force Dicer)")
                .defineInRange("dicerServantAttackDamage", 8.0, 0.0, Double.MAX_VALUE);
        DicerServantAttackKnockback = BUILDER.comment("How much Attack Knockback Dicer Servants have, Default: 0.3")
                .defineInRange("dicerServantAttackKnockback", 0.3, 0.0, Double.MAX_VALUE);
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
