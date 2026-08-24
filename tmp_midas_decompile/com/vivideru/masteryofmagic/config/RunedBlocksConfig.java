/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.common.ForgeConfigSpec
 *  net.minecraftforge.common.ForgeConfigSpec$Builder
 *  net.minecraftforge.common.ForgeConfigSpec$ConfigValue
 *  net.minecraftforge.fml.ModLoadingContext
 *  net.minecraftforge.fml.config.IConfigSpec
 *  net.minecraftforge.fml.config.ModConfig$Type
 */
package com.vivideru.masteryofmagic.config;

import java.util.List;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public class RunedBlocksConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> FOCUS_BLACKLIST;

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, (IConfigSpec)SPEC, "Goety_MOfM_Config/RunedBlocksConfig.toml");
    }

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("runed_lazethyst");
        FOCUS_BLACKLIST = builder.comment(new String[]{"Blacklist of focuses that CANNOT be inserted into the Runed Lazethyst Block.", "Format: <modid>:<item_name>", "Example: goety:corruption_focus", "The list can be extended by modpacks or other mods."}).defineListAllowEmpty("focus_blacklist", List.of("goety:corruption_focus", "goety:sword_focus", "goety:thunderstorm_focus", "goety:rupture_focus", "goety:skull_focus", "goety:stellar_focus", "goety:feast_focus", "goety:quaking_focus", "goety:trident_storm_focus"), o -> o instanceof String);
        builder.pop();
        SPEC = builder.build();
    }
}

