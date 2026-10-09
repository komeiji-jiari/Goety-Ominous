package com.qiuyue.goetyominous.common.worldgen.alias;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;

public final class PoolAliasBindings {

    public static final ResourceLocation DIRECT = new ResourceLocation("minecraft", "direct");
    public static final ResourceLocation RANDOM = new ResourceLocation("minecraft", "random");
    public static final ResourceLocation RANDOM_GROUP = new ResourceLocation("minecraft", "random_group");

    private static final Codec<PoolAliasBinding> UNKNOWN = new Codec<>() {
        @Override
        public <T> DataResult<Pair<PoolAliasBinding, T>> decode(DynamicOps<T> ops, T input) {
            return DataResult.error(() -> "Unknown pool alias binding type");
        }

        @Override
        public <T> DataResult<T> encode(PoolAliasBinding input, DynamicOps<T> ops, T prefix) {
            return DataResult.error(() -> "Unknown pool alias binding type");
        }
    };

    private static Codec<PoolAliasBinding> codec;

    private PoolAliasBindings() {
    }

    public static Codec<PoolAliasBinding> bindings() {
        if (codec == null) {
            Map<ResourceLocation, Supplier<MapCodec<? extends PoolAliasBinding>>> types = Map.of(
                    DIRECT, () -> Direct.CODEC,
                    RANDOM, () -> Random.CODEC,
                    RANDOM_GROUP, () -> RandomGroup.CODEC);
            codec = ResourceLocation.CODEC.dispatch(PoolAliasBindings::idOf, id -> {
                Supplier<MapCodec<? extends PoolAliasBinding>> supplier = types.get(id);
                return supplier != null ? supplier.get().codec() : UNKNOWN;
            });
        }
        return codec;
    }

    private static ResourceLocation idOf(PoolAliasBinding binding) {
        if (binding instanceof Direct) {
            return DIRECT;
        }
        if (binding instanceof Random) {
            return RANDOM;
        }
        if (binding instanceof RandomGroup) {
            return RANDOM_GROUP;
        }
        throw new IllegalStateException("Unknown pool alias binding: " + binding);
    }
}
