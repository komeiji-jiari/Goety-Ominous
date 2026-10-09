package com.qiuyue.goetyominous.common.worldgen.alias;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

@FunctionalInterface
public interface PoolAliasLookup {

    PoolAliasLookup EMPTY = key -> key;

    ResourceKey<StructureTemplatePool> lookup(ResourceKey<StructureTemplatePool> key);

    static PoolAliasLookup create(List<PoolAliasBinding> bindings, BlockPos pos, long seed) {
        if (bindings.isEmpty()) {
            return EMPTY;
        }
        RandomSource random = RandomSource.create(seed).forkPositional().at(pos);
        ImmutableMap.Builder<ResourceKey<StructureTemplatePool>, ResourceKey<StructureTemplatePool>> builder =
                ImmutableMap.builder();
        bindings.forEach(binding -> binding.forEachResolved(random, builder::put));
        ImmutableMap<ResourceKey<StructureTemplatePool>, ResourceKey<StructureTemplatePool>> resolved = builder.build();
        return key -> Objects.requireNonNull(resolved.getOrDefault(key, key),
                () -> "alias " + key + " was mapped to null value");
    }
}
