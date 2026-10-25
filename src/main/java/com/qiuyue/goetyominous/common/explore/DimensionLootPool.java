package com.qiuyue.goetyominous.common.explore;

import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public final class DimensionLootPool {
    private static final Map<ResourceKey<Level>, List<ResourceLocation>> ORE_CACHE = new HashMap<>();
    private static final Map<ResourceKey<Level>, List<ResourceLocation>> MOB_CACHE = new HashMap<>();

    private DimensionLootPool() {
    }

    public static synchronized void invalidate() {
        ORE_CACHE.clear();
        MOB_CACHE.clear();
    }

    public static synchronized List<ResourceLocation> oreTables(ServerLevel level) {
        ResourceKey<Level> key = level.dimension();
        List<ResourceLocation> cached = ORE_CACHE.get(key);
        if (cached != null) {
            return cached;
        }
        List<ResourceLocation> usable;
        try {
            usable = computeOreTables(level);
        } catch (RuntimeException | StackOverflowError broken) {
            GoetyOminous.LOGGER.warn("[goetyominous] Failed to compute ore pool for {}: {}", key.location(), broken.toString());
            usable = List.of();
        }
        ORE_CACHE.put(key, List.copyOf(usable));
        return ORE_CACHE.get(key);
    }

    private static List<ResourceLocation> computeOreTables(ServerLevel level) {
        List<ResourceLocation> fromWorldgen = new ArrayList<>();
        for (Holder<Biome> holder : level.getChunkSource().getGenerator().getBiomeSource().possibleBiomes()) {
            Biome biome = holder.value();
            for (HolderSet<PlacedFeature> stepFeatures : biome.getGenerationSettings().features()) {
                for (Holder<PlacedFeature> placedHolder : stepFeatures) {
                    for (ConfiguredFeature<?, ?> configured : placedHolder.value().getFeatures().toList()) {
                        LootResolver.collectOreTables(configured, fromWorldgen);
                    }
                }
            }
        }
        LinkedHashSet<ResourceLocation> candidates = new LinkedHashSet<>(fromWorldgen);
        List<ResourceLocation> usable = new ArrayList<>();
        RandomSource random = RandomSource.create(20240101L);
        for (ResourceLocation tableId : candidates) {
            if (LootResolver.producesOreBlock(level, tableId, random)) {
                usable.add(tableId);
            }
        }
        GoetyOminous.LOGGER.info("[goetyominous] Ore source pool for {}: {} loot tables.", level.dimension().location(), usable.size());
        return usable;
    }

    public static synchronized List<ResourceLocation> mobTables(ServerLevel level) {
        ResourceKey<Level> key = level.dimension();
        List<ResourceLocation> cached = MOB_CACHE.get(key);
        if (cached != null) {
            return cached;
        }
        LinkedHashSet<ResourceLocation> tables = new LinkedHashSet<>();
        for (Holder<Biome> holder : level.getChunkSource().getGenerator().getBiomeSource().possibleBiomes()) {
            Biome biome = holder.value();
            MobSpawnSettings spawnSettings = biome.getMobSettings();
            for (MobCategory category : MobCategory.values()) {
                for (MobSpawnSettings.SpawnerData spawnerData : spawnSettings.getMobs(category).unwrap()) {
                    ResourceLocation table = spawnerData.type.getDefaultLootTable();
                    if (table == null || table.equals(BuiltInLootTables.EMPTY)) {
                        continue;
                    }
                    tables.add(table);
                }
            }
        }
        GoetyOminous.LOGGER.info("[goetyominous] Mob source pool for {}: {} loot tables.", level.dimension().location(), tables.size());
        MOB_CACHE.put(key, List.copyOf(tables));
        return MOB_CACHE.get(key);
    }
}
