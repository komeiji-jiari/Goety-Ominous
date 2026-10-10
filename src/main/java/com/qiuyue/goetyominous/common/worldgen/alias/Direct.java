package com.qiuyue.goetyominous.common.worldgen.alias;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.BiConsumer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public record Direct(ResourceKey<StructureTemplatePool> alias, ResourceKey<StructureTemplatePool> target)
        implements PoolAliasBinding {

    public static final MapCodec<Direct> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceKey.codec(Registries.TEMPLATE_POOL).fieldOf("alias").forGetter(Direct::alias),
            ResourceKey.codec(Registries.TEMPLATE_POOL).fieldOf("target").forGetter(Direct::target)
    ).apply(instance, Direct::new));

    @Override
    public void forEachResolved(RandomSource random,
                                BiConsumer<ResourceKey<StructureTemplatePool>, ResourceKey<StructureTemplatePool>> consumer) {
        consumer.accept(this.alias, this.target);
    }

    @Override
    public MapCodec<Direct> codec() {
        return CODEC;
    }
}
