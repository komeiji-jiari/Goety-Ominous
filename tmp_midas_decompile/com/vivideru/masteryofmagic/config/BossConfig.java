/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.electronwill.nightconfig.core.CommentedConfig
 *  com.electronwill.nightconfig.core.file.CommentedFileConfig
 *  com.electronwill.nightconfig.core.io.WritingMode
 *  net.minecraftforge.common.ForgeConfigSpec
 *  net.minecraftforge.common.ForgeConfigSpec$BooleanValue
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

public class BossConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.IntValue GHIACCIO_HEALTH;
    public static final ForgeConfigSpec.IntValue GHIACCIO_FROST_BARRIER_MAX;
    public static final ForgeConfigSpec.DoubleValue GHIACCIO_FROST_BARRIER_REGEN;
    public static final ForgeConfigSpec.IntValue GHIACCIO_FROST_BARRIER_REGEN_DELAY_AFTER_FIRE_DAMAGE;
    public static final ForgeConfigSpec.DoubleValue GHIACCIO_MAX_DAMAGE_REDUCTION;
    public static final ForgeConfigSpec.DoubleValue GHIACCIO_MELEE_BASE_DAMAGE;
    public static final ForgeConfigSpec.DoubleValue GHIACCIO_MELEE_TARGET_MAX_HEALTH_PERCENT;
    public static final ForgeConfigSpec.DoubleValue GHIACCIO_MOVEMENT_SPEED;
    public static final ForgeConfigSpec.DoubleValue GHIACCIO_ARMOR;
    public static final ForgeConfigSpec.DoubleValue GHIACCIO_ARMOR_TOUGHNESS;
    public static final ForgeConfigSpec.DoubleValue GHIACCIO_KNOCKBACK_RESISTANCE;
    public static final ForgeConfigSpec.DoubleValue GHIACCIO_FOLLOW_RANGE;
    public static final ForgeConfigSpec.BooleanValue MIDAS_ANNOUNCEMENTS_ENABLED;

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, (IConfigSpec)SPEC, "Goety_MOfM_Config/BossConfig.toml");
        BossConfig.loadConfig(SPEC, FMLPaths.CONFIGDIR.get().resolve("Goety_MOfM_Config").resolve("BossConfig.toml").toString());
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
        builder.push("bosses");
        builder.push("ghiaccio");
        GHIACCIO_HEALTH = builder.comment("Maximum health of Ghiaccio").defineInRange("health", 666, 1, Integer.MAX_VALUE);
        GHIACCIO_FROST_BARRIER_MAX = builder.comment("Maximum frost barrier health of Ghiaccio").defineInRange("frost_barrier_max", 70, 0, Integer.MAX_VALUE);
        GHIACCIO_FROST_BARRIER_REGEN = builder.comment("Frost barrier regeneration per second").defineInRange("frost_barrier_regen", 1.0, 0.0, Double.MAX_VALUE);
        GHIACCIO_FROST_BARRIER_REGEN_DELAY_AFTER_FIRE_DAMAGE = builder.comment("Frost barrier regeneration delay after taking fire damage (ticks)").defineInRange("frost_barrier_regen_delay_after_fire_damage", 100, 0, Integer.MAX_VALUE);
        GHIACCIO_MAX_DAMAGE_REDUCTION = builder.comment("Maximum damage reduction granted by the frost barrier").defineInRange("max_damage_reduction", 0.8, 0.0, 0.95);
        GHIACCIO_MELEE_BASE_DAMAGE = builder.comment("Base melee damage of Ghiaccio").defineInRange("melee_base_damage", 20.0, 0.0, Double.MAX_VALUE);
        GHIACCIO_MELEE_TARGET_MAX_HEALTH_PERCENT = builder.comment("Additional melee damage based on target maximum health").defineInRange("melee_target_max_health_percent", 0.02, 0.0, 1.0);
        GHIACCIO_MOVEMENT_SPEED = builder.comment("Base movement speed of Ghiaccio").defineInRange("movement_speed", 0.25, 0.0, Double.MAX_VALUE);
        GHIACCIO_ARMOR = builder.comment("Armor value of Ghiaccio").defineInRange("armor", 12.0, 0.0, Double.MAX_VALUE);
        GHIACCIO_ARMOR_TOUGHNESS = builder.comment("Armor toughness value of Ghiaccio").defineInRange("armor_toughness", 10.0, 0.0, Double.MAX_VALUE);
        GHIACCIO_KNOCKBACK_RESISTANCE = builder.comment("Knockback resistance of Ghiaccio").defineInRange("knockback_resistance", 0.75, 0.0, 1.0);
        GHIACCIO_FOLLOW_RANGE = builder.comment("Follow range of Ghiaccio").defineInRange("follow_range", 70.0, 1.0, Double.MAX_VALUE);
        builder.pop();
        builder.push("midas");
        MIDAS_ANNOUNCEMENTS_ENABLED = builder.comment("Whether Midas spell announcements are shown to players involved in the boss fight").define("announcements_enabled", true);
        builder.pop();
        builder.pop();
        SPEC = builder.build();
    }
}

