/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.server.ServerStartedEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.loading.FMLPaths
 */
package com.vivideru.masteryofmagic.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.loading.FMLPaths;

public class MobWeaknessConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path BASE_DIR = FMLPaths.CONFIGDIR.get().resolve("Goety_MOfM_Config");
    private static final Path CONFIG_PATH = BASE_DIR.resolve("mob_weaknesses.json");
    private static final String DEFAULT_RESOURCE_PATH = "data/goety_mastery_of_magic/defaults/mob_weaknesses_defaults.json";
    private static boolean registered = false;
    private static final List<WeaknessEntry> ENTRIES = new ArrayList<WeaknessEntry>();
    private static final Map<String, List<String>> DAMAGE_GROUPS = new HashMap<String, List<String>>();

    public static void register() {
        if (!registered) {
            MinecraftForge.EVENT_BUS.register(MobWeaknessConfig.class);
            registered = true;
        }
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        MobWeaknessConfig.ensureDirectory();
        MobWeaknessConfig.load();
    }

    private static void ensureDirectory() {
        try {
            if (!Files.exists(BASE_DIR, new LinkOption[0])) {
                Files.createDirectories(BASE_DIR, new FileAttribute[0]);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void load() {
        try {
            if (!Files.exists(CONFIG_PATH, new LinkOption[0])) {
                MobWeaknessConfig.loadDefaultsFromResource();
                MobWeaknessConfig.save();
                return;
            }
            try (BufferedReader reader = Files.newBufferedReader(CONFIG_PATH);){
                JsonObject root = JsonParser.parseReader((Reader)reader).getAsJsonObject();
                MobWeaknessConfig.parseGroups(root.getAsJsonObject("damage_groups"));
                MobWeaknessConfig.parseWeaknesses(root.getAsJsonArray("weaknesses"));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadDefaultsFromResource() {
        try (InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(DEFAULT_RESOURCE_PATH);){
            if (stream == null) {
                System.out.println("DEFAULT FILE NOT FOUND: data/goety_mastery_of_magic/defaults/mob_weaknesses_defaults.json");
                return;
            }
            try (InputStreamReader reader = new InputStreamReader(stream);){
                JsonObject root = JsonParser.parseReader((Reader)reader).getAsJsonObject();
                MobWeaknessConfig.parseGroups(root.getAsJsonObject("damage_groups"));
                MobWeaknessConfig.parseWeaknesses(root.getAsJsonArray("weaknesses"));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void parseGroups(JsonObject obj) {
        DAMAGE_GROUPS.clear();
        if (obj == null) {
            return;
        }
        for (String key : obj.keySet()) {
            JsonArray arr = obj.getAsJsonArray(key);
            ArrayList<String> list = new ArrayList<String>();
            for (JsonElement el : arr) {
                list.add(el.getAsString());
            }
            DAMAGE_GROUPS.put(key, list);
        }
    }

    private static void parseWeaknesses(JsonArray array) {
        ENTRIES.clear();
        if (array == null) {
            return;
        }
        for (JsonElement element : array) {
            JsonArray entry = element.getAsJsonArray();
            ENTRIES.add(new WeaknessEntry(entry.get(0).getAsString(), entry.get(1).getAsString(), entry.get(2).getAsFloat()));
        }
    }

    private static void createDefault() {
        DAMAGE_GROUPS.clear();
        ENTRIES.clear();
        DAMAGE_GROUPS.put("fire", List.of("goety:hellfire", "goety:fire_breath"));
        DAMAGE_GROUPS.put("lightning", List.of("goety:lightning", "goety:shock"));
        DAMAGE_GROUPS.put("water", List.of("goety:drench"));
        DAMAGE_GROUPS.put("ice", List.of("goety:ice_spike"));
        DAMAGE_GROUPS.put("acid", List.of("goety:acid"));
        ENTRIES.add(new WeaknessEntry("minecraft:blaze", "water", 2.0f));
        ENTRIES.add(new WeaknessEntry("#forge:undead", "fire", 2.0f));
    }

    public static void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(CONFIG_PATH, new OpenOption[0]);){
            JsonObject root = new JsonObject();
            JsonObject groups = new JsonObject();
            for (Map.Entry<String, List<String>> entry : DAMAGE_GROUPS.entrySet()) {
                JsonArray arr = new JsonArray();
                for (String id : entry.getValue()) {
                    arr.add(id);
                }
                groups.add(entry.getKey(), (JsonElement)arr);
            }
            JsonArray weaknessArr = new JsonArray();
            for (WeaknessEntry w : ENTRIES) {
                JsonArray arr = new JsonArray();
                arr.add(w.mobId);
                arr.add(w.damageKey);
                arr.add((Number)Float.valueOf(w.multiplier));
                weaknessArr.add((JsonElement)arr);
            }
            root.add("damage_groups", (JsonElement)groups);
            root.add("weaknesses", (JsonElement)weaknessArr);
            GSON.toJson((JsonElement)root, (Appendable)writer);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<WeaknessEntry> getEntries() {
        return ENTRIES;
    }

    public static Map<String, List<String>> getDamageGroups() {
        return DAMAGE_GROUPS;
    }

    public static class WeaknessEntry {
        public String mobId;
        public String damageKey;
        public float multiplier;

        public WeaknessEntry(String mobId, String damageKey, float multiplier) {
            this.mobId = mobId;
            this.damageKey = damageKey;
            this.multiplier = multiplier;
        }
    }
}

