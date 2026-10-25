package com.qiuyue.goetyominous.common.explore;

import com.mojang.datafixers.util.Either;
import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class StructureIndex {
    private final Map<ResourceLocation, Set<ResourceLocation>> biomeToStructures;
    private final Set<ResourceLocation> structuresWithLoot;
    private final Map<ResourceLocation, Set<ResourceLocation>> validatedLoot;
    private static volatile StructureIndex cache;

    private StructureIndex(Map<ResourceLocation, Set<ResourceLocation>> biomeToStructures,
                           Set<ResourceLocation> structuresWithLoot,
                           Map<ResourceLocation, Set<ResourceLocation>> validatedLoot) {
        this.biomeToStructures = biomeToStructures;
        this.structuresWithLoot = structuresWithLoot;
        this.validatedLoot = validatedLoot;
    }

    public static StructureIndex get(MinecraftServer server) {
        StructureIndex local = cache;
        if (local == null) {
            cache = local = build(server);
            GoetyOminous.LOGGER.info("[goetyominous] Structure index built: {} structures with loot, {} biomes covered.",
                    local.structuresWithLoot.size(), local.biomeToStructures.size());
        }
        return local;
    }

    public static void invalidate() {
        cache = null;
    }

    public Set<ResourceLocation> structuresWithLoot() {
        return structuresWithLoot;
    }

    public boolean hasLoot(ResourceLocation structure) {
        return structuresWithLoot.contains(structure);
    }

    public Set<ResourceLocation> lootTablesForBiome(ResourceLocation biome) {
        LinkedHashSet<ResourceLocation> out = new LinkedHashSet<>();
        for (ResourceLocation structure : biomeToStructures.getOrDefault(biome, Set.of())) {
            out.addAll(lootTablesOf(structure));
        }
        return out;
    }

    public Set<ResourceLocation> lootTablesOf(ResourceLocation structure) {
        return validatedLoot.getOrDefault(structure, Set.of());
    }

    public Set<ResourceLocation> structuresForBiome(ResourceLocation biome) {
        return biomeToStructures.getOrDefault(biome, Set.of());
    }

    public boolean inDimension(ResourceLocation structure, Set<ResourceLocation> dimensionBiomes) {
        for (ResourceLocation biome : dimensionBiomes) {
            if (biomeToStructures.getOrDefault(biome, Set.of()).contains(structure)) {
                return true;
            }
        }
        return false;
    }

    public List<ResourceLocation> sortedBiomes(ServerLevel level) {
        List<ResourceLocation> out = new ArrayList<>();
        for (Holder<Biome> holder : level.getChunkSource().getGenerator().getBiomeSource().possibleBiomes()) {
            holder.unwrapKey().map(ResourceKey::location).ifPresent(out::add);
        }
        out.sort(Comparator.comparing(ResourceLocation::toString));
        return out;
    }

    public boolean hasStructureLoot(ResourceLocation biome) {
        return biomeToStructures.containsKey(biome);
    }

    private static StructureIndex build(MinecraftServer server) {
        Map<ResourceLocation, Set<ResourceLocation>> biomeMap = new HashMap<>();
        Map<ResourceLocation, Set<ResourceLocation>> validated = new HashMap<>();
        LinkedHashSet<ResourceLocation> withLoot = new LinkedHashSet<>();
        Registry<Structure> structureRegistry = server.registryAccess().registryOrThrow(Registries.STRUCTURE);
        Registry<Biome> biomeRegistry = server.registryAccess().registryOrThrow(Registries.BIOME);
        int dropped = 0;
        for (Holder.Reference<Structure> holder : structureRegistry.holders().toList()) {
            ResourceLocation id = holder.unwrapKey().map(ResourceKey::location).orElse(null);
            if (id == null) {
                continue;
            }
            LinkedHashSet<ResourceLocation> real = new LinkedHashSet<>();
            for (ResourceLocation table : StructureLootCache.lootTablesOf(id)) {
                if (isRealTable(server, table)) {
                    real.add(table);
                } else {
                    dropped++;
                }
            }
            if (real.isEmpty()) {
                continue;
            }
            validated.put(id, Collections.unmodifiableSet(real));
            withLoot.add(id);
            for (ResourceLocation biome : resolveBiomes(holder.value(), biomeRegistry)) {
                biomeMap.computeIfAbsent(biome, key -> new LinkedHashSet<>()).add(id);
            }
        }
        if (dropped > 0) {
            GoetyOminous.LOGGER.info("[goetyominous] Filtered {} references to missing or empty loot tables.", dropped);
        }
        Map<ResourceLocation, Set<ResourceLocation>> frozen = new HashMap<>(biomeMap.size());
        biomeMap.forEach((biome, structures) -> frozen.put(biome, Collections.unmodifiableSet(structures)));
        return new StructureIndex(Collections.unmodifiableMap(frozen), Collections.unmodifiableSet(withLoot), Collections.unmodifiableMap(validated));
    }

    private static boolean isRealTable(MinecraftServer server, ResourceLocation table) {
        try {
            LootTable loot = server.getLootData().getLootTable(table);
            return loot != null && loot != LootTable.EMPTY;
        } catch (RuntimeException broken) {
            return false;
        }
    }

    private static Set<ResourceLocation> resolveBiomes(Structure structure, Registry<Biome> biomeRegistry) {
        Set<ResourceLocation> out = new LinkedHashSet<>();
        HolderSet<Biome> biomes = structure.biomes();
        Either<TagKey<Biome>, List<Holder<Biome>>> either = biomes.unwrap();
        either.left().ifPresent(tagKey -> addTaggedBiomes(biomeRegistry, tagKey, out));
        either.right().ifPresent(holders -> {
            for (Holder<Biome> holder : holders) {
                holder.unwrapKey().map(ResourceKey::location).ifPresent(out::add);
            }
        });
        return out;
    }

    private static void addTaggedBiomes(Registry<Biome> biomeRegistry, TagKey<Biome> tagKey, Set<ResourceLocation> out) {
        try {
            biomeRegistry.getTag(tagKey).ifPresent(holders -> {
                for (Holder<Biome> holder : holders) {
                    holder.unwrapKey().map(ResourceKey::location).ifPresent(out::add);
                }
            });
        } catch (RuntimeException broken) {
            GoetyOminous.LOGGER.warn("[goetyominous] Failed to expand biome tag {}: {}", tagKey.location(), broken.toString());
        }
    }
}
