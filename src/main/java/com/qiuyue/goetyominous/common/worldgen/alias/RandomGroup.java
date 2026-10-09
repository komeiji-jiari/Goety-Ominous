package com.qiuyue.goetyominous.common.worldgen.alias;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public record RandomGroup(WeightedRandomList<WeightedEntry.Wrapper<List<PoolAliasBinding>>> groups)
        implements PoolAliasBinding {

    public static final MapCodec<RandomGroup> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            WeightedRandomList.codec(WeightedEntry.Wrapper.codec(PoolAliasBindings.bindings().listOf()))
                    .fieldOf("groups").forGetter(RandomGroup::groups)
    ).apply(instance, RandomGroup::new));

    @Override
    public void forEachResolved(RandomSource random,
                                BiConsumer<ResourceKey<StructureTemplatePool>, ResourceKey<StructureTemplatePool>> consumer) {
        this.groups.getRandom(random)
                .ifPresent(entry -> entry.getData().forEach(binding -> binding.forEachResolved(random, consumer)));
    }

    @Override
    public Stream<ResourceKey<StructureTemplatePool>> allTargets() {
        return this.groups.unwrap().stream()
                .map(WeightedEntry.Wrapper::getData)
                .flatMap(List::stream)
                .flatMap(PoolAliasBinding::allTargets);
    }

    @Override
    public MapCodec<RandomGroup> codec() {
        return CODEC;
    }
}
