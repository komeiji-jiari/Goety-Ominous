package com.qiuyue.goetyominous.common.worldgen.alias;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public record Random(ResourceKey<StructureTemplatePool> alias,
                     WeightedRandomList<WeightedEntry.Wrapper<ResourceKey<StructureTemplatePool>>> targets)
        implements PoolAliasBinding {

    public static final MapCodec<Random> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceKey.codec(Registries.TEMPLATE_POOL).fieldOf("alias").forGetter(Random::alias),
            WeightedRandomList.codec(WeightedEntry.Wrapper.codec(ResourceKey.codec(Registries.TEMPLATE_POOL)))
                    .fieldOf("targets").forGetter(Random::targets)
    ).apply(instance, Random::new));

    @Override
    public void forEachResolved(RandomSource random,
                                BiConsumer<ResourceKey<StructureTemplatePool>, ResourceKey<StructureTemplatePool>> consumer) {
        this.targets.getRandom(random).ifPresent(entry -> consumer.accept(this.alias, entry.getData()));
    }

    @Override
    public Stream<ResourceKey<StructureTemplatePool>> allTargets() {
        return this.targets.unwrap().stream().map(WeightedEntry.Wrapper::getData);
    }

    @Override
    public MapCodec<Random> codec() {
        return CODEC;
    }
}
