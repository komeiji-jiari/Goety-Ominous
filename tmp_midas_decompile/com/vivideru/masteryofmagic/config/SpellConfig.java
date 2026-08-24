/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.electronwill.nightconfig.core.CommentedConfig
 *  com.electronwill.nightconfig.core.file.CommentedFileConfig
 *  com.electronwill.nightconfig.core.io.WritingMode
 *  net.minecraftforge.common.ForgeConfigSpec
 *  net.minecraftforge.common.ForgeConfigSpec$Builder
 *  net.minecraftforge.common.ForgeConfigSpec$DoubleValue
 *  net.minecraftforge.common.ForgeConfigSpec$IntValue
 *  net.minecraftforge.fml.ModLoadingContext
 *  net.minecraftforge.fml.config.IConfigSpec
 *  net.minecraftforge.fml.config.ModConfig$Type
 *  net.minecraftforge.fml.loading.FMLPaths
 */
package com.vivideru.masteryofmagic.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;

public class SpellConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.IntValue MINING_CURSE_SOUL_COST;
    public static final ForgeConfigSpec.IntValue MINING_CURSE_CAST_TIME;
    public static final ForgeConfigSpec.IntValue MINING_CURSE_COOLDOWN;
    public static final ForgeConfigSpec.IntValue MINING_CURSE_BASE_DURATION;
    public static final ForgeConfigSpec.IntValue FIRESHOT_MIN_DAMAGE;
    public static final ForgeConfigSpec.IntValue FIRESHOT_MAX_DAMAGE;
    public static final ForgeConfigSpec.IntValue FIRESHOT_SOUL_COST;
    public static final ForgeConfigSpec.IntValue FIRESHOT_CAST_TIME;
    public static final ForgeConfigSpec.IntValue FIRESHOT_COOLDOWN;
    public static final ForgeConfigSpec.IntValue FOCUS_WILDFIRE_SOUL_COST;
    public static final ForgeConfigSpec.IntValue FOCUS_WILDFIRE_CAST_TIME;
    public static final ForgeConfigSpec.IntValue FOCUS_WILDFIRE_COOLDOWN;
    public static final ForgeConfigSpec.IntValue FOCUS_WILDFIRE_SUMMON_DOWN;
    public static final ForgeConfigSpec.IntValue FOCUS_WILDFIRE_LIMIT;
    public static final ForgeConfigSpec.IntValue NECROMANCER_FOCUS_SOUL_COST;
    public static final ForgeConfigSpec.IntValue NECROMANCER_FOCUS_CAST_TIME;
    public static final ForgeConfigSpec.IntValue NECROMANCER_FOCUS_COOLDOWN;
    public static final ForgeConfigSpec.IntValue NECROMANCER_FOCUS_SUMMON_DOWN;
    public static final ForgeConfigSpec.IntValue NECROMANCER_FOCUS_LIMIT;
    public static final ForgeConfigSpec.DoubleValue NAMELESS_NECROMANCER_BASE_HEALTH;
    public static final ForgeConfigSpec.DoubleValue NAMELESS_NECROMANCER_COMBAT_DISTANCE;
    public static final ForgeConfigSpec.DoubleValue NAMELESS_NECROMANCER_FOLLOW_RANGE;
    public static final ForgeConfigSpec.IntValue TIME_STOP_SOUL_COST;
    public static final ForgeConfigSpec.IntValue TIME_STOP_CAST_TIME;
    public static final ForgeConfigSpec.IntValue TIME_STOP_COOLDOWN;
    public static final ForgeConfigSpec.IntValue TIME_STOP_BASE_RADIUS;
    public static final ForgeConfigSpec.IntValue TIME_STOP_RADIUS_PER_LEVEL;
    public static final ForgeConfigSpec.IntValue TIME_STOP_BASE_DURATION;
    public static final ForgeConfigSpec.IntValue TIME_STOP_DURATION_PER_LEVEL;
    public static final ForgeConfigSpec.IntValue ICE_MONARCH_SOUL_COST;
    public static final ForgeConfigSpec.IntValue ICE_MONARCH_CAST_TIME;
    public static final ForgeConfigSpec.IntValue ICE_MONARCH_COOLDOWN;
    public static final ForgeConfigSpec.IntValue ICE_MONARCH_SUMMON_DOWN;
    public static final ForgeConfigSpec.IntValue ICE_MONARCH_LIMIT;
    public static final ForgeConfigSpec.IntValue SOUL_BARRIER_SOUL_COST_PER_SECOND;
    public static final ForgeConfigSpec.IntValue DODGING_SOUL_COST;
    public static final ForgeConfigSpec.IntValue DODGING_COOLDOWN;
    public static final ForgeConfigSpec.IntValue FADING_SOUL_COST;
    public static final ForgeConfigSpec.IntValue FADING_COOLDOWN;

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, (IConfigSpec)SPEC, "Goety_MOfM_Config/SpellConfig.toml");
        SpellConfig.loadConfig(SPEC, FMLPaths.CONFIGDIR.get().resolve("Goety_MOfM_Config").resolve("SpellConfig.toml").toString());
    }

    public static void loadConfig(ForgeConfigSpec config, String path) {
        File configFile = new File(path);
        File parent = configFile.getParentFile();
        if (parent != null && !parent.exists()) {
            try {
                Files.createDirectories(parent.toPath(), new FileAttribute[0]);
            }
            catch (IOException e) {
                throw new RuntimeException("Failed to create config directory: " + parent.getAbsolutePath(), e);
            }
        }
        CommentedFileConfig file = (CommentedFileConfig)CommentedFileConfig.builder((File)configFile).sync().autosave().writingMode(WritingMode.REPLACE).build();
        file.load();
        config.setConfig((CommentedConfig)file);
    }

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("spells");
        builder.push("mining_curse");
        MINING_CURSE_SOUL_COST = builder.comment("Soul cost of the Mining Curse spell").defineInRange("soul_cost", 4, 0, Integer.MAX_VALUE);
        MINING_CURSE_CAST_TIME = builder.comment("Cast time of the Mining Curse spell (ticks)").defineInRange("cast_time", 0, 0, Integer.MAX_VALUE);
        MINING_CURSE_COOLDOWN = builder.comment("Cooldown of the Mining Curse spell (ticks)").defineInRange("cooldown", 100, 0, Integer.MAX_VALUE);
        MINING_CURSE_BASE_DURATION = builder.comment("Base duration of the Mining Curse spell (seconds)").defineInRange("base_duration", 12, 0, Integer.MAX_VALUE);
        builder.pop();
        builder.push("fireshot");
        FIRESHOT_MIN_DAMAGE = builder.comment("Minimum Fireshot damage at max range").defineInRange("min_damage", 8, 0, Integer.MAX_VALUE);
        FIRESHOT_MAX_DAMAGE = builder.comment("Maximum Fireshot damage at point blank").defineInRange("max_damage", 20, 0, Integer.MAX_VALUE);
        FIRESHOT_SOUL_COST = builder.comment("Soul cost of the Fireshot spell").defineInRange("soul_cost", 50, 0, Integer.MAX_VALUE);
        FIRESHOT_CAST_TIME = builder.comment("Cast time of the Fireshot spell (ticks)").defineInRange("cast_time", 30, 0, Integer.MAX_VALUE);
        FIRESHOT_COOLDOWN = builder.comment("Cooldown of the Fireshot spell (ticks)").defineInRange("cooldown", 40, 0, Integer.MAX_VALUE);
        builder.pop();
        builder.push("focus_wildfire");
        FOCUS_WILDFIRE_SOUL_COST = builder.comment("Soul cost of the Focus Wildfire spell").defineInRange("soul_cost", 120, 0, Integer.MAX_VALUE);
        FOCUS_WILDFIRE_CAST_TIME = builder.comment("Cast time of the Focus Wildfire spell (ticks)").defineInRange("cast_time", 60, 0, Integer.MAX_VALUE);
        FOCUS_WILDFIRE_COOLDOWN = builder.comment("Cooldown of the Focus Wildfire spell (ticks)").defineInRange("cooldown", 600, 0, Integer.MAX_VALUE);
        FOCUS_WILDFIRE_SUMMON_DOWN = builder.comment("Summon down duration of the Focus Wildfire spell (ticks)").defineInRange("summon_down", 200, 0, Integer.MAX_VALUE);
        FOCUS_WILDFIRE_LIMIT = builder.comment("Summon limit of the Focus Wildfire spell").defineInRange("limit", 1, 0, Integer.MAX_VALUE);
        builder.pop();
        builder.push("necromancer_focus");
        NECROMANCER_FOCUS_SOUL_COST = builder.comment("Soul Energy required to cast the Necromancer Focus").defineInRange("soul_cost", 2000, 0, Integer.MAX_VALUE);
        NECROMANCER_FOCUS_CAST_TIME = builder.comment("Necromancer Focus cast time in ticks; 60 matches Goety's Osseous Focus").defineInRange("cast_time", 60, 0, Integer.MAX_VALUE);
        NECROMANCER_FOCUS_COOLDOWN = builder.comment("Necromancer Focus cooldown in ticks").defineInRange("cooldown", 1200, 0, Integer.MAX_VALUE);
        NECROMANCER_FOCUS_SUMMON_DOWN = builder.comment("Necromancer Focus summon-down duration in ticks").defineInRange("summon_down", 200, 0, Integer.MAX_VALUE);
        NECROMANCER_FOCUS_LIMIT = builder.comment("Maximum number of servants summoned through the Necromancer Focus").defineInRange("summon_limit", 4, 0, Integer.MAX_VALUE);
        NAMELESS_NECROMANCER_BASE_HEALTH = builder.comment("Base maximum health of the Nameless Necromancer before level scaling").defineInRange("nameless_base_health", 350.0, 1.0, Double.MAX_VALUE);
        NAMELESS_NECROMANCER_COMBAT_DISTANCE = builder.comment("Preferred horizontal distance of the Nameless Necromancer from its target").defineInRange("nameless_combat_distance", 20.0, 2.0, 128.0);
        NAMELESS_NECROMANCER_FOLLOW_RANGE = builder.comment("Target acquisition and follow range of the Nameless Necromancer").defineInRange("nameless_follow_range", 96.0, 8.0, 256.0);
        builder.pop();
        builder.push("time_stop");
        TIME_STOP_SOUL_COST = builder.comment("Soul cost of the Time Stop spell").defineInRange("soul_cost", 3000, 0, Integer.MAX_VALUE);
        TIME_STOP_CAST_TIME = builder.comment("Cast time of the Time Stop spell (ticks)").defineInRange("cast_time", 20, 0, Integer.MAX_VALUE);
        TIME_STOP_COOLDOWN = builder.comment("Cooldown of the Time Stop spell (ticks)").defineInRange("cooldown", 600, 0, Integer.MAX_VALUE);
        TIME_STOP_BASE_RADIUS = builder.comment("Base radius of the Time Stop spell (blocks)").defineInRange("base_radius", 10, 0, Integer.MAX_VALUE);
        TIME_STOP_RADIUS_PER_LEVEL = builder.comment("Extra Time Stop radius per Radius level").defineInRange("radius_per_level", 2, 0, Integer.MAX_VALUE);
        TIME_STOP_BASE_DURATION = builder.comment("Base duration of the Time Stop spell (seconds)").defineInRange("base_duration", 5, 0, Integer.MAX_VALUE);
        TIME_STOP_DURATION_PER_LEVEL = builder.comment("Extra Time Stop duration per Duration level (seconds)").defineInRange("duration_per_level", 1, 0, Integer.MAX_VALUE);
        builder.pop();
        builder.push("ice_monarch");
        ICE_MONARCH_SOUL_COST = builder.comment("Soul cost of the Ice Monarch spell").defineInRange("soul_cost", 500, 0, Integer.MAX_VALUE);
        ICE_MONARCH_CAST_TIME = builder.comment("Cast time of the Ice Monarch spell (ticks)").defineInRange("cast_time", 80, 0, Integer.MAX_VALUE);
        ICE_MONARCH_COOLDOWN = builder.comment("Cooldown of the Ice Monarch spell (ticks)").defineInRange("cooldown", 800, 0, Integer.MAX_VALUE);
        ICE_MONARCH_SUMMON_DOWN = builder.comment("Summon down duration of the Ice Monarch spell (ticks)").defineInRange("summon_down", 200, 0, Integer.MAX_VALUE);
        ICE_MONARCH_LIMIT = builder.comment("Summon limit of the Ice Monarch spell").defineInRange("limit", 1, 0, Integer.MAX_VALUE);
        builder.pop();
        builder.push("soul_barrier");
        SOUL_BARRIER_SOUL_COST_PER_SECOND = builder.comment("Soul cost per second of the Soul Barrier spell").defineInRange("soul_cost_per_second", 200, 0, Integer.MAX_VALUE);
        builder.pop();
        builder.push("dodging");
        DODGING_SOUL_COST = builder.comment("Soul cost of the Dodging Focus spell").defineInRange("soul_cost", 20, 0, Integer.MAX_VALUE);
        DODGING_COOLDOWN = builder.comment("Cooldown of the Dodging Focus spell (ticks)").defineInRange("cooldown", 20, 0, Integer.MAX_VALUE);
        builder.pop();
        builder.push("fading");
        FADING_SOUL_COST = builder.comment("Soul cost of the Fading Focus spell").defineInRange("soul_cost", 30, 0, Integer.MAX_VALUE);
        FADING_COOLDOWN = builder.comment("Cooldown of the Fading Focus spell (ticks)").defineInRange("cooldown", 50, 0, Integer.MAX_VALUE);
        builder.pop();
        builder.pop();
        SPEC = builder.build();
    }
}

