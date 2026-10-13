package com.qiuyue.goetyominous.config;

import net.minecraftforge.common.ForgeConfigSpec;
import java.io.File;

public class WeaponConfig {

    public static void loadConfig(ForgeConfigSpec spec, String path) {
        final com.electronwill.nightconfig.core.file.CommentedFileConfig configData =
                com.electronwill.nightconfig.core.file.CommentedFileConfig.builder(new File(path))
                        .sync()
                        .autoreload()
                        .writingMode(com.electronwill.nightconfig.core.io.WritingMode.REPLACE)
                        .build();
        configData.load();
        spec.setConfig(configData);
    }

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Double> FelStaffDamage;

    public static final ForgeConfigSpec.ConfigValue<Integer> WolfArmorDurability;
    public static final ForgeConfigSpec.ConfigValue<Double> WolfArmorIngotRepair;
    public static final ForgeConfigSpec.ConfigValue<Integer> WolfArmorSoulRepairInterval;

    public static final ForgeConfigSpec.ConfigValue<Integer> WargArmorDurability;
    public static final ForgeConfigSpec.ConfigValue<Double> WargArmorIngotRepair;
    public static final ForgeConfigSpec.ConfigValue<Integer> WargArmorSoulRepairInterval;

    public static final ForgeConfigSpec.ConfigValue<Integer> BlackBeastArmorDurability;
    public static final ForgeConfigSpec.ConfigValue<Double> BlackBeastArmorIngotRepair;
    public static final ForgeConfigSpec.ConfigValue<Integer> BlackBeastArmorSoulRepairInterval;

    public static final ForgeConfigSpec.ConfigValue<Double> BoneCudgelDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> BoneCudgelAttackSpeed;

    public static final ForgeConfigSpec.ConfigValue<Double> WitherScytheDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> WitherScytheAttackSpeed;

    public static final ForgeConfigSpec.ConfigValue<Double> FirebrandDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> FirebrandFireBonus;

    public static final ForgeConfigSpec.ConfigValue<Double> BarbaricAxeDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> BarbaricAxeAttackSpeed;
    public static final ForgeConfigSpec.ConfigValue<Double> BarbaricAxeSweepDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> BarbaricAxeSweepRange;
    public static final ForgeConfigSpec.ConfigValue<Double> BarbaricAxeSlamDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> BarbaricAxeSlamRadius;
    public static final ForgeConfigSpec.ConfigValue<Integer> BarbaricAxeIgniteSeconds;
    public static final ForgeConfigSpec.ConfigValue<Integer> BarbaricAxeChainLimit;

    public static final ForgeConfigSpec.ConfigValue<Double> AnimSweepDuration;
    public static final ForgeConfigSpec.ConfigValue<Double> AnimSlamDuration;
    public static final ForgeConfigSpec.ConfigValue<Double> AnimSweepWindupShare;
    public static final ForgeConfigSpec.ConfigValue<Double> AnimSweepStrikeShare;
    public static final ForgeConfigSpec.ConfigValue<Double> AnimSweepWindupRatio;
    public static final ForgeConfigSpec.ConfigValue<Double> AnimSlamWindupShare;
    public static final ForgeConfigSpec.ConfigValue<Double> AnimSlamStrikeShare;
    public static final ForgeConfigSpec.ConfigValue<Double> AnimSlamWindupRatio;
    public static final ForgeConfigSpec.ConfigValue<Double> AnimFirstSweepAmplitude;
    public static final ForgeConfigSpec.ConfigValue<Double> AnimFirstSweepTilt;
    public static final ForgeConfigSpec.ConfigValue<Double> AnimFirstSweepPushOut;
    public static final ForgeConfigSpec.ConfigValue<Double> AnimFirstSweepLift;
    public static final ForgeConfigSpec.ConfigValue<Double> AnimFirstSweepSide;
    public static final ForgeConfigSpec.ConfigValue<Double> AnimFirstSlamAmplitude;
    public static final ForgeConfigSpec.ConfigValue<Double> AnimFirstSlamTilt;
    public static final ForgeConfigSpec.ConfigValue<Double> AnimFirstSlamPushOut;
    public static final ForgeConfigSpec.ConfigValue<Double> AnimFirstSlamLift;
    public static final ForgeConfigSpec.ConfigValue<Double> AnimFirstSlamSide;
    public static final ForgeConfigSpec.ConfigValue<Double> AnimThirdSweepBodyPitchWindup;
    public static final ForgeConfigSpec.ConfigValue<Double> AnimThirdSweepBodyPitchStrike;
    public static final ForgeConfigSpec.ConfigValue<Double> AnimThirdSweepBodyYawWindup;
    public static final ForgeConfigSpec.ConfigValue<Double> AnimThirdSweepBodyYawStrike;
    public static final ForgeConfigSpec.ConfigValue<Double> AnimThirdSweepArmPitchWindup;
    public static final ForgeConfigSpec.ConfigValue<Double> AnimThirdSweepArmPitchStrike;
    public static final ForgeConfigSpec.ConfigValue<Double> AnimThirdSweepArmYawWindup;
    public static final ForgeConfigSpec.ConfigValue<Double> AnimThirdSweepArmYawStrike;
    public static final ForgeConfigSpec.ConfigValue<Double> AnimThirdSweepArmRollWindup;
    public static final ForgeConfigSpec.ConfigValue<Double> AnimThirdSweepArmRollStrike;
    public static final ForgeConfigSpec.ConfigValue<Double> AnimThirdSlamBodyPitchWindup;
    public static final ForgeConfigSpec.ConfigValue<Double> AnimThirdSlamBodyPitchStrike;
    public static final ForgeConfigSpec.ConfigValue<Double> AnimThirdSlamBodyYawWindup;
    public static final ForgeConfigSpec.ConfigValue<Double> AnimThirdSlamBodyYawStrike;
    public static final ForgeConfigSpec.ConfigValue<Double> AnimThirdSlamArmPitchWindup;
    public static final ForgeConfigSpec.ConfigValue<Double> AnimThirdSlamArmPitchStrike;
    public static final ForgeConfigSpec.ConfigValue<Double> AnimThirdSlamArmYawWindup;
    public static final ForgeConfigSpec.ConfigValue<Double> AnimThirdSlamArmYawStrike;
    public static final ForgeConfigSpec.ConfigValue<Double> AnimThirdSlamArmRollWindup;
    public static final ForgeConfigSpec.ConfigValue<Double> AnimThirdSlamArmRollStrike;
    public static final ForgeConfigSpec.ConfigValue<Double> AnimThirdLeftArmFollow;
    public static final ForgeConfigSpec.ConfigValue<Boolean> AnimSwingOnEveryClick;

    static {

        BUILDER.push("Fel Staff");
        FelStaffDamage = BUILDER.comment("Melee damage of Fel Staff (Default: 4.0)")
                .defineInRange("felStaffDamage", 4.0, 1.0, 100.0);
        BUILDER.pop();

        BUILDER.push("Bone Cudgel");
        BoneCudgelDamage = BUILDER
                .comment("Bone Cudgel base damage, Default: 13.0")
                .defineInRange("boneCudgelDamage", 13.0, 0.0, Double.MAX_VALUE);
        BoneCudgelAttackSpeed = BUILDER
                .comment("Bone Cudgel attack speed modifier, Default: -3.75")
                .defineInRange("boneCudgelAttackSpeed", -3.75, -4.0, 0.0);
        BUILDER.pop();

        BUILDER.push("Wither Scythe");
        WitherScytheDamage = BUILDER
                .comment("Wither Scythe total attack damage, Default: 9.5")
                .defineInRange("witherScytheDamage", 9.5, 0.0, Double.MAX_VALUE);
        WitherScytheAttackSpeed = BUILDER
                .comment("Wither Scythe attack speed modifier, Default: -3.3")
                .defineInRange("witherScytheAttackSpeed", -3.3, -4.0, 10.0);
        BUILDER.pop();

        BUILDER.push("Firebrand");
        FirebrandDamage = BUILDER
                .comment("Firebrand total attack damage, Default: 9.0")
                .defineInRange("firebrandDamage", 9.0, 0.0, Double.MAX_VALUE);
        FirebrandFireBonus = BUILDER
                .comment("Firebrand bonus fire damage dealt to burning targets, Default: 3.0")
                .defineInRange("firebrandFireBonus", 3.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();

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

        BUILDER.comment("Barbaric Axe swing animation.",
                        "All entries here take effect on the client only. They are re-read every frame, so editing this file",
                        "while the game is running updates the animation without restarting Minecraft.",
                        "Two animations are tuned separately: SWEEP = left click chop, SLAM = right click ground smash.",
                        "Angles are in degrees, distances are in blocks, durations are in seconds.")
                .push("Barbaric Axe Animation");

        AnimSweepDuration = BUILDER
                .comment("SWEEP (left click) total animation length in seconds, Default: 0.40",
                        "Shorten it for a snappier hit, lengthen it for a heavier feel.")
                .defineInRange("animSweepDuration", 0.40, 0.15, 5.0);
        AnimSlamDuration = BUILDER
                .comment("SLAM (right click) total animation length in seconds, Default: 0.55")
                .defineInRange("animSlamDuration", 0.55, 0.15, 5.0);

        AnimSweepWindupShare = BUILDER
                .comment("SWEEP rhythm: share of the duration spent raising the axe (windup), Default: 0.02",
                        "Must satisfy windup + strike < 1.0. The remaining time is the recovery.")
                .defineInRange("animSweepWindupShare", 0.02, 0.02, 0.90);
        AnimSweepStrikeShare = BUILDER
                .comment("SWEEP rhythm: share of the duration spent travelling down to the impact, Default: 0.12",
                        "Smaller = the blade crosses the screen faster.")
                .defineInRange("animSweepStrikeShare", 0.12, 0.02, 0.90);
        AnimSweepWindupRatio = BUILDER
                .comment("SWEEP how far the windup goes, as a fraction of the total swing, Default: 0.00",
                        "0.0 = no windup at all (starts already extended), 0.38 = the axe first pulls back 38% of the arc.",
                        "This is the only value that controls the windup size; amplitude controls the whole arc.")
                .defineInRange("animSweepWindupRatio", 0.0, 0.0, 0.95);

        AnimSlamWindupShare = BUILDER
                .comment("SLAM rhythm: share of the duration spent raising the axe overhead, Default: 0.02")
                .defineInRange("animSlamWindupShare", 0.02, 0.02, 0.90);
        AnimSlamStrikeShare = BUILDER
                .comment("SLAM rhythm: share of the duration spent driving the axe into the ground, Default: 0.14")
                .defineInRange("animSlamStrikeShare", 0.14, 0.02, 0.90);
        AnimSlamWindupRatio = BUILDER
                .comment("SLAM how far the windup goes, as a fraction of the total swing, Default: 0.00")
                .defineInRange("animSlamWindupRatio", 0.0, 0.0, 0.95);

        AnimFirstSweepAmplitude = BUILDER
                .comment("FIRST PERSON SWEEP amplitude in degrees: how far the axe rotates from windup to impact, Default: 140.0",
                        "This is the single biggest lever on how big the swing looks. 90 = small wrist flick, 140 = full chop,",
                        "180 = the blade sweeps all the way around. Note the axe model is held vertically, so the visible",
                        "arc is what this value produces together with animFirstSweepTilt.")
                .defineInRange("animFirstSweepAmplitude", 140.0, 0.0, 360.0);
        AnimFirstSweepTilt = BUILDER
                .comment("FIRST PERSON SWEEP tilt in degrees: the plane the axe swings in, Default: 20.0",
                        "0   = the blade sweeps flat across the screen, like a clock hand (widest on screen).",
                        "45  = diagonal: down and across at the same time.",
                        "90  = straight over the head into the screen; the blade gets foreshortened and almost disappears",
                        "      at the middle of the swing, so avoid large values here.",
                        "20-35 reads as a proper diagonal axe chop.")
                .defineInRange("animFirstSweepTilt", 20.0, 0.0, 90.0);
        AnimFirstSweepPushOut = BUILDER
                .comment("FIRST PERSON SWEEP push-out distance in blocks, Default: 0.30",
                        "The axe blade is very large, so without this it sweeps straight through the camera and blacks out",
                        "the view. This moves the whole hand away from the camera: 100% of it at the impact, 70% of it",
                        "during the windup. Raise it if the blade still covers the screen, lower it for a closer viewmodel.")
                .defineInRange("animFirstSweepPushOut", 0.30, 0.0, 1.5);
        AnimFirstSweepLift = BUILDER
                .comment("FIRST PERSON SWEEP vertical travel in blocks, Default: 0.18",
                        "The hand rises by this amount during the windup and drops by it at the impact.")
                .defineInRange("animFirstSweepLift", 0.18, 0.0, 1.5);
        AnimFirstSweepSide = BUILDER
                .comment("FIRST PERSON SWEEP sideways travel in blocks, Default: 0.10",
                        "The hand moves right during the windup and left at the impact. Negative values mirror it.")
                .defineInRange("animFirstSweepSide", 0.10, -1.5, 1.5);

        AnimFirstSlamAmplitude = BUILDER
                .comment("FIRST PERSON SLAM amplitude in degrees, Default: 160.0")
                .defineInRange("animFirstSlamAmplitude", 160.0, 0.0, 360.0);
        AnimFirstSlamTilt = BUILDER
                .comment("FIRST PERSON SLAM tilt in degrees, Default: 45.0",
                        "Higher than the sweep so the smash reads as coming over the head rather than across.")
                .defineInRange("animFirstSlamTilt", 45.0, 0.0, 90.0);
        AnimFirstSlamPushOut = BUILDER
                .comment("FIRST PERSON SLAM push-out distance in blocks, Default: 0.35")
                .defineInRange("animFirstSlamPushOut", 0.35, 0.0, 1.5);
        AnimFirstSlamLift = BUILDER
                .comment("FIRST PERSON SLAM vertical travel in blocks, Default: 0.26")
                .defineInRange("animFirstSlamLift", 0.26, 0.0, 1.5);
        AnimFirstSlamSide = BUILDER
                .comment("FIRST PERSON SLAM sideways travel in blocks, Default: 0.05")
                .defineInRange("animFirstSlamSide", 0.05, -1.5, 1.5);

        AnimThirdSweepBodyPitchWindup = BUILDER
                .comment("THIRD PERSON SWEEP: torso lean during the windup in degrees. Negative = lean back, Default: -10.0")
                .defineInRange("animThirdSweepBodyPitchWindup", -10.0, -60.0, 60.0);
        AnimThirdSweepBodyPitchStrike = BUILDER
                .comment("THIRD PERSON SWEEP: torso lean at the impact in degrees. Positive = lean forward, Default: 17.0")
                .defineInRange("animThirdSweepBodyPitchStrike", 17.0, -60.0, 60.0);
        AnimThirdSweepBodyYawWindup = BUILDER
                .comment("THIRD PERSON SWEEP: torso twist during the windup in degrees. Negative = twist to the model's right, Default: -24.0")
                .defineInRange("animThirdSweepBodyYawWindup", -24.0, -90.0, 90.0);
        AnimThirdSweepBodyYawStrike = BUILDER
                .comment("THIRD PERSON SWEEP: torso twist at the impact in degrees. Positive = twist to the model's left, Default: 19.0")
                .defineInRange("animThirdSweepBodyYawStrike", 19.0, -90.0, 90.0);
        AnimThirdSweepArmPitchWindup = BUILDER
                .comment("THIRD PERSON SWEEP: swinging arm angle during the windup in degrees, Default: -158.0",
                        "0 = arm hanging down, -90 = arm pointing straight forward, -180 = arm pointing straight up.",
                        "Values below -180 raise the arm up and past vertical, behind the head.")
                .defineInRange("animThirdSweepArmPitchWindup", -158.0, -270.0, 0.0);
        AnimThirdSweepArmPitchStrike = BUILDER
                .comment("THIRD PERSON SWEEP: swinging arm angle at the impact in degrees, Default: -54.0")
                .defineInRange("animThirdSweepArmPitchStrike", -54.0, -270.0, 60.0);
        AnimThirdSweepArmYawWindup = BUILDER
                .comment("THIRD PERSON SWEEP: swinging arm sideways angle during the windup in degrees. Negative = to the model's right, Default: -36.0")
                .defineInRange("animThirdSweepArmYawWindup", -36.0, -120.0, 120.0);
        AnimThirdSweepArmYawStrike = BUILDER
                .comment("THIRD PERSON SWEEP: swinging arm sideways angle at the impact in degrees. Positive = across to the model's left, Default: 26.0")
                .defineInRange("animThirdSweepArmYawStrike", 26.0, -120.0, 120.0);
        AnimThirdSweepArmRollWindup = BUILDER
                .comment("THIRD PERSON SWEEP: swinging arm roll during the windup in degrees, Default: -14.0")
                .defineInRange("animThirdSweepArmRollWindup", -14.0, -120.0, 120.0);
        AnimThirdSweepArmRollStrike = BUILDER
                .comment("THIRD PERSON SWEEP: swinging arm roll at the impact in degrees, Default: 22.0")
                .defineInRange("animThirdSweepArmRollStrike", 22.0, -120.0, 120.0);

        AnimThirdSlamBodyPitchWindup = BUILDER
                .comment("THIRD PERSON SLAM: torso lean during the windup in degrees, Default: -14.0")
                .defineInRange("animThirdSlamBodyPitchWindup", -14.0, -60.0, 60.0);
        AnimThirdSlamBodyPitchStrike = BUILDER
                .comment("THIRD PERSON SLAM: torso lean at the impact in degrees, Default: 24.0")
                .defineInRange("animThirdSlamBodyPitchStrike", 24.0, -60.0, 60.0);
        AnimThirdSlamBodyYawWindup = BUILDER
                .comment("THIRD PERSON SLAM: torso twist during the windup in degrees, Default: -9.0")
                .defineInRange("animThirdSlamBodyYawWindup", -9.0, -90.0, 90.0);
        AnimThirdSlamBodyYawStrike = BUILDER
                .comment("THIRD PERSON SLAM: torso twist at the impact in degrees, Default: 7.0")
                .defineInRange("animThirdSlamBodyYawStrike", 7.0, -90.0, 90.0);
        AnimThirdSlamArmPitchWindup = BUILDER
                .comment("THIRD PERSON SLAM: swinging arm angle during the windup in degrees, Default: -192.0",
                        "Beyond -180 means the arm is raised past vertical, i.e. cocked behind the head.")
                .defineInRange("animThirdSlamArmPitchWindup", -192.0, -270.0, 0.0);
        AnimThirdSlamArmPitchStrike = BUILDER
                .comment("THIRD PERSON SLAM: swinging arm angle at the impact in degrees, Default: -26.0",
                        "Close to 0 means the arm ends up almost straight down in front of the body.")
                .defineInRange("animThirdSlamArmPitchStrike", -26.0, -270.0, 60.0);
        AnimThirdSlamArmYawWindup = BUILDER
                .comment("THIRD PERSON SLAM: swinging arm sideways angle during the windup in degrees, Default: -12.0")
                .defineInRange("animThirdSlamArmYawWindup", -12.0, -120.0, 120.0);
        AnimThirdSlamArmYawStrike = BUILDER
                .comment("THIRD PERSON SLAM: swinging arm sideways angle at the impact in degrees, Default: 6.0")
                .defineInRange("animThirdSlamArmYawStrike", 6.0, -120.0, 120.0);
        AnimThirdSlamArmRollWindup = BUILDER
                .comment("THIRD PERSON SLAM: swinging arm roll during the windup in degrees, Default: -6.0")
                .defineInRange("animThirdSlamArmRollWindup", -6.0, -120.0, 120.0);
        AnimThirdSlamArmRollStrike = BUILDER
                .comment("THIRD PERSON SLAM: swinging arm roll at the impact in degrees, Default: 5.0")
                .defineInRange("animThirdSlamArmRollStrike", 5.0, -120.0, 120.0);

        AnimThirdLeftArmFollow = BUILDER
                .comment("THIRD PERSON: how much the off hand copies the swinging arm, Default: 0.0",
                        "0.0 = the other arm keeps its normal walking animation (one handed grip).",
                        "0.6-0.9 = the other arm follows along, which reads as a two handed grip.",
                        "Negative values mirror it. Only applies while the axe is being swung.")
                .defineInRange("animThirdLeftArmFollow", 0.0, -1.0, 1.0);

        AnimSwingOnEveryClick = BUILDER
                .comment("SWEEP only. true = every left click plays the chop immediately on the client, even while the attack",
                        "cooldown is still recharging, so the weapon always answers the mouse button. false = the chop is only",
                        "played once the server confirms a landed hit, which makes early clicks look like nothing happened.",
                        "Default: true")
                .define("animSwingOnEveryClick", true);

        BUILDER.pop();

        BUILDER.push("Wolf Armor");
        WolfArmorDurability = BUILDER
                .comment("Cursed Metal Wolf Armor durability, Default: 78")
                .defineInRange("wolfArmorDurability", 78, 1, Integer.MAX_VALUE);
        WolfArmorIngotRepair = BUILDER
                .comment("Fraction of max durability repaired per Cursed Metal Ingot, Default: 0.125")
                .defineInRange("wolfArmorIngotRepair", 0.125, 0.01, 1.0);
        WolfArmorSoulRepairInterval = BUILDER
                .comment("Dark Wolf Armor soul repair interval in ticks (1 durability per N ticks), Default: 20")
                .defineInRange("wolfArmorSoulRepairInterval", 20, 1, 10000);
        BUILDER.pop();

        BUILDER.push("Warg Armor");
        WargArmorDurability = BUILDER
                .comment("Cursed Warg Armor durability, Default: 78")
                .defineInRange("wargArmorDurability", 78, 1, Integer.MAX_VALUE);
        WargArmorIngotRepair = BUILDER
                .comment("Fraction of max durability repaired per Cursed Metal Ingot, Default: 0.125")
                .defineInRange("wargArmorIngotRepair", 0.125, 0.01, 1.0);
        WargArmorSoulRepairInterval = BUILDER
                .comment("Dark Warg Armor soul repair interval in ticks (1 durability per N ticks), Default: 20")
                .defineInRange("wargArmorSoulRepairInterval", 20, 1, 10000);
        BUILDER.pop();

        BUILDER.push("Black Beast Armor");
        BlackBeastArmorDurability = BUILDER
                .comment("Cursed Black Beast Armor durability, Default: 78")
                .defineInRange("blackBeastArmorDurability", 78, 1, Integer.MAX_VALUE);
        BlackBeastArmorIngotRepair = BUILDER
                .comment("Fraction of max durability repaired per Cursed Metal Ingot, Default: 0.125")
                .defineInRange("blackBeastArmorIngotRepair", 0.125, 0.01, 1.0);
        BlackBeastArmorSoulRepairInterval = BUILDER
                .comment("Dark Black Beast Armor soul repair interval in ticks (1 durability per N ticks), Default: 20")
                .defineInRange("blackBeastArmorSoulRepairInterval", 20, 1, 10000);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
