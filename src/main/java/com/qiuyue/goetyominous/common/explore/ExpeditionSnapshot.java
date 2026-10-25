package com.qiuyue.goetyominous.common.explore;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;

import java.util.List;
import java.util.UUID;

public record ExpeditionSnapshot(
        ResourceLocation dimension,
        ResourceLocation playerBiome,
        InteractionHand hand,
        ExpeditionTuning tuning,
        boolean authoritative,
        boolean oresAvailable,
        boolean mobsAvailable,
        List<DimensionEntry> dimensions,
        List<BiomeEntry> biomes,
        List<RuinEntry> ruins,
        List<TravelerEntry> travelers,
        List<ExpeditionInfo> expeditions,
        Component feedback
) {
    public ExpeditionSnapshot {
        dimensions = List.copyOf(dimensions);
        biomes = List.copyOf(biomes);
        ruins = List.copyOf(ruins);
        travelers = List.copyOf(travelers);
        expeditions = List.copyOf(expeditions);
    }

    public record DimensionEntry(ResourceLocation id, boolean unlocked) {
    }

    public record BiomeEntry(ResourceLocation id, boolean hasLoot) {
    }

    public record RuinEntry(ResourceLocation id, boolean visited) {
    }

    public record TravelerEntry(
            UUID id,
            String name,
            ResourceLocation typeId,
            TravelerStatus status,
            float health,
            float maxHealth,
            boolean pickaxe,
            boolean crossbow
    ) {
    }

    public record ExpeditionInfo(
            UUID expeditionId,
            ExpeditionKind kind,
            ResourceLocation dimension,
            ResourceLocation biome,
            ResourceLocation structure,
            int totalCount,
            long remainingTicks,
            long durationTicks,
            boolean settled,
            boolean delivering
    ) {
    }
}
