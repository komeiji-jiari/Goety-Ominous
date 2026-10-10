package com.qiuyue.goetyominous.common.worldgen.alias;

import com.mojang.serialization.MapCodec;
import java.util.function.BiConsumer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public interface PoolAliasBinding {

    void forEachResolved(RandomSource random,
                         BiConsumer<ResourceKey<StructureTemplatePool>, ResourceKey<StructureTemplatePool>> consumer);

    MapCodec<? extends PoolAliasBinding> codec();
}
