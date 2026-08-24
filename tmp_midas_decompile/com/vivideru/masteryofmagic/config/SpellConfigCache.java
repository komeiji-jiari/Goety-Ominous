/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  net.minecraftforge.fml.event.config.ModConfigEvent$Loading
 *  net.minecraftforge.fml.event.config.ModConfigEvent$Reloading
 */
package com.vivideru.masteryofmagic.config;

import com.vivideru.masteryofmagic.config.SpellConfig;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid="goety_mastery_of_magic", bus=Mod.EventBusSubscriber.Bus.MOD)
public class SpellConfigCache {
    public static int MINING_CURSE_SOUL_COST;
    public static int MINING_CURSE_BASE_DURATION;
    public static int MINING_CURSE_CAST_TIME;
    public static int MINING_CURSE_COOLDOWN;
    public static int FIRESHOT_MIN_DAMAGE;
    public static int FIRESHOT_MAX_DAMAGE;
    public static int FIRESHOT_SOUL_COST;
    public static int FIRESHOT_CAST_TIME;
    public static int FIRESHOT_COOLDOWN;
    public static int FOCUS_WILDFIRE_SOUL_COST;
    public static int FOCUS_WILDFIRE_CAST_TIME;
    public static int FOCUS_WILDFIRE_COOLDOWN;
    public static int FOCUS_WILDFIRE_SUMMON_DOWN;
    public static int FOCUS_WILDFIRE_LIMIT;
    public static int TIME_STOP_SOUL_COST;
    public static int TIME_STOP_CAST_TIME;
    public static int TIME_STOP_COOLDOWN;
    public static int TIME_STOP_BASE_RADIUS;
    public static int TIME_STOP_RADIUS_PER_LEVEL;
    public static int TIME_STOP_BASE_DURATION;
    public static int TIME_STOP_DURATION_PER_LEVEL;

    @SubscribeEvent
    public static void onConfigLoad(ModConfigEvent.Loading event) {
        if (event.getConfig().getModId().equals("goety_mastery_of_magic")) {
            SpellConfigCache.bake();
        }
    }

    @SubscribeEvent
    public static void onConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getModId().equals("goety_mastery_of_magic")) {
            SpellConfigCache.bake();
        }
    }

    public static void bake() {
        try {
            MINING_CURSE_SOUL_COST = (Integer)SpellConfig.MINING_CURSE_SOUL_COST.get();
            MINING_CURSE_BASE_DURATION = (Integer)SpellConfig.MINING_CURSE_BASE_DURATION.get();
            MINING_CURSE_CAST_TIME = (Integer)SpellConfig.MINING_CURSE_CAST_TIME.get();
            MINING_CURSE_COOLDOWN = (Integer)SpellConfig.MINING_CURSE_COOLDOWN.get();
            FIRESHOT_MIN_DAMAGE = (Integer)SpellConfig.FIRESHOT_MIN_DAMAGE.get();
            FIRESHOT_MAX_DAMAGE = (Integer)SpellConfig.FIRESHOT_MAX_DAMAGE.get();
            FIRESHOT_SOUL_COST = (Integer)SpellConfig.FIRESHOT_SOUL_COST.get();
            FIRESHOT_CAST_TIME = (Integer)SpellConfig.FIRESHOT_CAST_TIME.get();
            FIRESHOT_COOLDOWN = (Integer)SpellConfig.FIRESHOT_COOLDOWN.get();
            FOCUS_WILDFIRE_SOUL_COST = (Integer)SpellConfig.FOCUS_WILDFIRE_SOUL_COST.get();
            FOCUS_WILDFIRE_CAST_TIME = (Integer)SpellConfig.FOCUS_WILDFIRE_CAST_TIME.get();
            FOCUS_WILDFIRE_COOLDOWN = (Integer)SpellConfig.FOCUS_WILDFIRE_COOLDOWN.get();
            FOCUS_WILDFIRE_SUMMON_DOWN = (Integer)SpellConfig.FOCUS_WILDFIRE_SUMMON_DOWN.get();
            FOCUS_WILDFIRE_LIMIT = (Integer)SpellConfig.FOCUS_WILDFIRE_LIMIT.get();
            TIME_STOP_SOUL_COST = (Integer)SpellConfig.TIME_STOP_SOUL_COST.get();
            TIME_STOP_CAST_TIME = (Integer)SpellConfig.TIME_STOP_CAST_TIME.get();
            TIME_STOP_COOLDOWN = (Integer)SpellConfig.TIME_STOP_COOLDOWN.get();
            TIME_STOP_BASE_RADIUS = (Integer)SpellConfig.TIME_STOP_BASE_RADIUS.get();
            TIME_STOP_RADIUS_PER_LEVEL = (Integer)SpellConfig.TIME_STOP_RADIUS_PER_LEVEL.get();
            TIME_STOP_BASE_DURATION = (Integer)SpellConfig.TIME_STOP_BASE_DURATION.get();
            TIME_STOP_DURATION_PER_LEVEL = (Integer)SpellConfig.TIME_STOP_DURATION_PER_LEVEL.get();
            System.out.println("TIME_STOP_SOUL_COST = " + TIME_STOP_SOUL_COST);
        }
        catch (IllegalStateException illegalStateException) {
            // empty catch block
        }
    }
}

