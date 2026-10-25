package com.qiuyue.goetyominous.common.explore;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;

public class StructureLootCache extends SimplePreparableReloadListener<StructureLootCache.Prepared> {
    private static final String DIR_PREFIX = "structures";
    private static final String NBT_SUFFIX = ".nbt";
    private static final String STRUCTURE_JSON_PREFIX = "worldgen/structure";
    private static final String POOL_JSON_PREFIX = "worldgen/template_pool";
    private static final String JSON_SUFFIX = ".json";
    private static final int MAX_STEPS = 20000;
    private static final int MAX_TEMPLATES = 8192;
    private static final int MAX_NBT_DEPTH = 32;
    private static final String JIGSAW_BLOCK = "minecraft:jigsaw";
    private static final String JIGSAW_POOL_FIELD = "pool";
    private static final Map<String, List<String>> CODE_GENERATED_LOOT = Map.ofEntries(
            Map.entry("minecraft:end_city", List.of("minecraft:chests/end_city_treasure")),
            Map.entry("minecraft:fortress", List.of("minecraft:chests/nether_bridge")),
            Map.entry("minecraft:ruined_portal", List.of("minecraft:chests/ruined_portal")),
            Map.entry("minecraft:ruined_portal_desert", List.of("minecraft:chests/ruined_portal")),
            Map.entry("minecraft:ruined_portal_jungle", List.of("minecraft:chests/ruined_portal")),
            Map.entry("minecraft:ruined_portal_mountain", List.of("minecraft:chests/ruined_portal")),
            Map.entry("minecraft:ruined_portal_nether", List.of("minecraft:chests/ruined_portal")),
            Map.entry("minecraft:ruined_portal_ocean", List.of("minecraft:chests/ruined_portal")),
            Map.entry("minecraft:ruined_portal_swamp", List.of("minecraft:chests/ruined_portal")),
            Map.entry("minecraft:desert_pyramid", List.of("minecraft:chests/desert_pyramid")),
            Map.entry("minecraft:jungle_pyramid", List.of("minecraft:chests/jungle_temple", "minecraft:chests/jungle_temple_dispenser")),
            Map.entry("minecraft:igloo", List.of("minecraft:chests/igloo_chest")),
            Map.entry("minecraft:mansion", List.of("minecraft:chests/woodland_mansion")),
            Map.entry("minecraft:monument", List.of("minecraft:chests/monument_treasure")),
            Map.entry("minecraft:stronghold", List.of("minecraft:chests/stronghold_library", "minecraft:chests/stronghold_corridor", "minecraft:chests/stronghold_crossing")),
            Map.entry("minecraft:mineshaft", List.of("minecraft:chests/abandoned_mineshaft")),
            Map.entry("minecraft:mineshaft_mesa", List.of("minecraft:chests/abandoned_mineshaft")),
            Map.entry("minecraft:buried_treasure", List.of("minecraft:chests/buried_treasure")),
            Map.entry("minecraft:shipwreck", List.of("minecraft:chests/shipwreck_treasure", "minecraft:chests/shipwreck_supply", "minecraft:chests/shipwreck_map")),
            Map.entry("minecraft:shipwreck_beached", List.of("minecraft:chests/shipwreck_treasure", "minecraft:chests/shipwreck_supply", "minecraft:chests/shipwreck_map")),
            Map.entry("minecraft:ocean_ruin_cold", List.of("minecraft:chests/underwater_ruin_big", "minecraft:chests/underwater_ruin_small")),
            Map.entry("minecraft:ocean_ruin_warm", List.of("minecraft:chests/underwater_ruin_big", "minecraft:chests/underwater_ruin_small")),
            Map.entry("minecraft:nether_fossil", List.of("minecraft:chests/nether_bridge"))
    );

    private static volatile Map<ResourceLocation, Set<ResourceLocation>> byStructure = Map.of();
    private static volatile Map<ResourceLocation, Set<ResourceLocation>> byTemplate = Map.of();

    public static Map<ResourceLocation, Set<ResourceLocation>> all() {
        return byStructure;
    }

    public static Set<ResourceLocation> lootTablesOf(ResourceLocation structure) {
        return byStructure.getOrDefault(structure, Set.of());
    }

    public static boolean hasLoot(ResourceLocation structure) {
        return !lootTablesOf(structure).isEmpty();
    }

    public static int structureCount() {
        return byStructure.size();
    }

    public static Map<ResourceLocation, Set<ResourceLocation>> templates() {
        return byTemplate;
    }

    @Override
    protected Prepared prepare(ResourceManager manager, ProfilerFiller profiler) {
        Map<ResourceLocation, Set<ResourceLocation>> templateLoot = new HashMap<>();
        Map<ResourceLocation, Set<ResourceLocation>> templatePools = new HashMap<>();
        int templateFiles = 0;
        for (Map.Entry<ResourceLocation, Resource> entry : sorted(manager, DIR_PREFIX, path -> path.getPath().endsWith(NBT_SUFFIX)).entrySet()) {
            templateFiles++;
            try (InputStream stream = entry.getValue().open()) {
                CompoundTag root = NbtIo.readCompressed(stream);
                Set<ResourceLocation> tables = new LinkedHashSet<>();
                collectAllLootTables(root, tables, 0);
                ResourceLocation templateId = templateIdOf(entry.getKey());
                Set<ResourceLocation> outPools = collectJigsawPools(root);
                if (!tables.isEmpty()) {
                    templateLoot.put(templateId, Collections.unmodifiableSet(tables));
                }
                if (!outPools.isEmpty()) {
                    templatePools.put(templateId, Collections.unmodifiableSet(outPools));
                }
            } catch (IOException | RuntimeException broken) {
                GoetyOminous.LOGGER.warn("[goetyominous] Failed to read structure template {}, skipped: {}", entry.getKey(), broken.toString());
            }
        }

        Map<String, List<ResourceLocation>> poolTemplates = new HashMap<>();
        int poolFiles = 0;
        for (Map.Entry<ResourceLocation, Resource> entry : sorted(manager, POOL_JSON_PREFIX, path -> path.getPath().endsWith(JSON_SUFFIX)).entrySet()) {
            poolFiles++;
            try {
                JsonObject json = readJson(entry.getValue());
                if (json == null) {
                    continue;
                }
                Set<ResourceLocation> ids = new LinkedHashSet<>();
                String direct = optString(json, "location");
                if (direct != null) {
                    addId(direct, ids);
                }
                if (json.has("elements") && json.get("elements").isJsonArray()) {
                    JsonArray elements = json.getAsJsonArray("elements");
                    for (JsonElement element : elements) {
                        if (!element.isJsonObject()) {
                            continue;
                        }
                        JsonObject elementObj = element.getAsJsonObject().getAsJsonObject("element");
                        if (elementObj == null) {
                            continue;
                        }
                        String location = optString(elementObj, "location");
                        if (location != null) {
                            addId(location, ids);
                        }
                        String nested = optString(elementObj, JIGSAW_POOL_FIELD);
                        if (nested != null) {
                            addId(nested, ids);
                        }
                    }
                }
                if (!ids.isEmpty()) {
                    poolTemplates.put(poolIdOf(entry.getKey()), List.copyOf(ids));
                }
            } catch (IOException | RuntimeException broken) {
                GoetyOminous.LOGGER.warn("[goetyominous] Failed to read template pool {}, skipped: {}", entry.getKey(), broken.toString());
            }
        }

        Map<ResourceLocation, Set<ResourceLocation>> structureLoot = new HashMap<>();
        int structureFiles = 0;
        for (Map.Entry<ResourceLocation, Resource> entry : sorted(manager, STRUCTURE_JSON_PREFIX, path -> path.getPath().endsWith(JSON_SUFFIX)).entrySet()) {
            structureFiles++;
            ResourceLocation structureId = new ResourceLocation(entry.getKey().getNamespace(), idOf(entry.getKey(), STRUCTURE_JSON_PREFIX, JSON_SUFFIX));
            try {
                JsonObject json = readJson(entry.getValue());
                Set<ResourceLocation> tables = new LinkedHashSet<>();
                String startPool = json == null ? null : optString(json, "start_pool");
                if (startPool != null) {
                    for (ResourceLocation template : expandFromPool(startPool, templatePools, poolTemplates)) {
                        tables.addAll(templateLoot.getOrDefault(template, Set.of()));
                    }
                } else {
                    for (String table : CODE_GENERATED_LOOT.getOrDefault(structureId.toString(), List.of())) {
                        ResourceLocation id = ResourceLocation.tryParse(table);
                        if (id != null) {
                            tables.add(id);
                        }
                    }
                }
                if (!tables.isEmpty()) {
                    structureLoot.put(structureId, Collections.unmodifiableSet(tables));
                }
            } catch (IOException | RuntimeException broken) {
                GoetyOminous.LOGGER.warn("[goetyominous] Failed to parse structure {}, skipped: {}", structureId, broken.toString());
            }
        }

        return new Prepared(Map.copyOf(templateLoot), Map.copyOf(templatePools), Map.copyOf(poolTemplates),
                Map.copyOf(structureLoot), templateFiles, structureFiles, poolFiles);
    }

    @Override
    protected void apply(Prepared prepared, ResourceManager manager, ProfilerFiller profiler) {
        byTemplate = prepared.templateLoot();
        byStructure = prepared.structureLoot();
        StructureIndex.invalidate();
        DimensionLootPool.invalidate();
        LootResolver.invalidateOreIndex();
        GoetyOminous.LOGGER.info("[goetyominous] Structure loot cache rebuilt: structures {}, templates {}, pools {}, loot-bearing structures {}, templates {}.",
                prepared.structureFiles(), prepared.templateFiles(), prepared.poolFiles(), byStructure.size(), byTemplate.size());
    }

    static Set<ResourceLocation> expandFromPool(String startPool, Map<ResourceLocation, Set<ResourceLocation>> templatePools, Map<String, List<ResourceLocation>> poolTemplates) {
        LinkedHashSet<ResourceLocation> collected = new LinkedHashSet<>();
        Set<String> visitedPools = new HashSet<>();
        Set<ResourceLocation> visitedTemplates = new HashSet<>();
        ArrayDeque<ResourceLocation> queue = new ArrayDeque<>();
        ResourceLocation start = ResourceLocation.tryParse(startPool);
        if (start != null) {
            queue.add(start);
        }
        int steps = 0;
        while (!queue.isEmpty() && steps++ < MAX_STEPS && collected.size() < MAX_TEMPLATES) {
            ResourceLocation id = queue.poll();
            boolean matched = false;
            Set<ResourceLocation> outPools = templatePools.get(id);
            if (outPools != null && visitedTemplates.add(id)) {
                collected.add(id);
                matched = true;
                queue.addAll(outPools);
            }
            List<ResourceLocation> fromPool = poolTemplates.get(id.toString());
            if (fromPool != null && visitedPools.add(id.toString())) {
                matched = true;
                queue.addAll(fromPool);
            }
            if (!matched && visitedTemplates.add(id)) {
                collected.add(id);
            }
        }
        return collected;
    }

    private static Map<ResourceLocation, Resource> sorted(ResourceManager manager, String directory, Predicate<ResourceLocation> filter) {
        try {
            return new TreeMap<>(manager.listResources(directory, filter));
        } catch (RuntimeException broken) {
            GoetyOminous.LOGGER.error("[goetyominous] Failed to list resource directory '{}', structure loot cache will be empty: {}", directory, broken.toString());
            return Map.of();
        }
    }

    private static void addId(String value, Set<ResourceLocation> out) {
        ResourceLocation id = ResourceLocation.tryParse(value);
        if (id != null) {
            out.add(id);
        }
    }

    private static String poolIdOf(ResourceLocation file) {
        return file.getNamespace() + ":" + idOf(file, POOL_JSON_PREFIX, JSON_SUFFIX);
    }

    private static ResourceLocation templateIdOf(ResourceLocation file) {
        return new ResourceLocation(file.getNamespace(), idOf(file, DIR_PREFIX, NBT_SUFFIX));
    }

    private static String idOf(ResourceLocation file, String pathPrefix, String suffix) {
        String path = file.getPath();
        if (path.startsWith(pathPrefix)) {
            path = path.substring(pathPrefix.length());
        }
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        if (path.endsWith(suffix)) {
            path = path.substring(0, path.length() - suffix.length());
        }
        return path;
    }

    private static void collectAllLootTables(Tag tag, Set<ResourceLocation> lootOut, int depth) {
        if (tag == null || depth > MAX_NBT_DEPTH) {
            return;
        }
        if (tag instanceof CompoundTag compound) {
            for (String key : compound.getAllKeys()) {
                Tag child = compound.get(key);
                if (child == null) {
                    continue;
                }
                if ("LootTable".equals(key) && child.getId() == Tag.TAG_STRING) {
                    ResourceLocation id = ResourceLocation.tryParse(compound.getString(key));
                    if (id != null) {
                        lootOut.add(id);
                    }
                    continue;
                }
                collectAllLootTables(child, lootOut, depth + 1);
            }
        } else if (tag instanceof ListTag list) {
            for (int i = 0; i < list.size(); i++) {
                collectAllLootTables(list.get(i), lootOut, depth + 1);
            }
        }
    }

    private static Set<ResourceLocation> collectJigsawPools(CompoundTag root) {
        LinkedHashSet<ResourceLocation> out = new LinkedHashSet<>();
        ListTag palette = root.getList("palette", Tag.TAG_COMPOUND);
        ListTag blocks = root.getList("blocks", Tag.TAG_COMPOUND);
        if (palette.isEmpty() || blocks.isEmpty()) {
            return out;
        }
        for (int i = 0; i < blocks.size(); i++) {
            CompoundTag block = blocks.getCompound(i);
            int index = block.getInt("state");
            if (index < 0 || index >= palette.size()) {
                continue;
            }
            if (!JIGSAW_BLOCK.equals(palette.getCompound(index).getString("Name"))) {
                continue;
            }
            Tag nbtTag = block.get("nbt");
            if (!(nbtTag instanceof CompoundTag nbt)) {
                continue;
            }
            String pool = nbt.getString(JIGSAW_POOL_FIELD);
            ResourceLocation id = ResourceLocation.tryParse(pool);
            if (id != null) {
                out.add(id);
            }
        }
        return out;
    }

    private static JsonObject readJson(Resource resource) throws IOException {
        try (InputStream stream = resource.open(); BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            JsonElement parsed = JsonParser.parseReader(reader);
            return parsed != null && parsed.isJsonObject() ? parsed.getAsJsonObject() : null;
        }
    }

    private static String optString(JsonObject json, String key) {
        if (json == null) {
            return null;
        }
        JsonElement element = json.get(key);
        return element != null && element.isJsonPrimitive() ? element.getAsString() : null;
    }

    public record Prepared(
            Map<ResourceLocation, Set<ResourceLocation>> templateLoot,
            Map<ResourceLocation, Set<ResourceLocation>> templatePools,
            Map<String, List<ResourceLocation>> poolTemplates,
            Map<ResourceLocation, Set<ResourceLocation>> structureLoot,
            int templateFiles,
            int structureFiles,
            int poolFiles
    ) {
    }
}
