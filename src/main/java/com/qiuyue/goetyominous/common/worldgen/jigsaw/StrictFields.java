package com.qiuyue.goetyominous.common.worldgen.jigsaw;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public final class StrictFields {

    public static <A> MapCodec<A> optional(String name, Codec<A> codec, A defaultValue) {
        return new MapCodec<>() {
            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return Stream.of(ops.createString(name));
            }

            @Override
            public <T> DataResult<A> decode(DynamicOps<T> ops, MapLike<T> input) {
                T value = input.get(name);
                return value == null ? DataResult.success(defaultValue) : codec.parse(ops, value).mapError(e -> name + ": " + e);
            }

            @Override
            public <T> RecordBuilder<T> encode(A input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
                return Objects.equals(input, defaultValue) ? prefix : prefix.add(name, codec.encodeStart(ops, input));
            }

            @Override
            public String toString() {
                return "StrictOptional[" + name + " -> " + codec + "]";
            }
        };
    }

    public static <A> MapCodec<Optional<A>> optional(String name, Codec<A> codec) {
        return new MapCodec<>() {
            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return Stream.of(ops.createString(name));
            }

            @Override
            public <T> DataResult<Optional<A>> decode(DynamicOps<T> ops, MapLike<T> input) {
                T value = input.get(name);
                return value == null ? DataResult.success(Optional.empty()) : codec.parse(ops, value).map(Optional::of).mapError(e -> name + ": " + e);
            }

            @Override
            public <T> RecordBuilder<T> encode(Optional<A> input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
                return input.isEmpty() ? prefix : prefix.add(name, codec.encodeStart(ops, input.get()));
            }

            @Override
            public String toString() {
                return "StrictOptional[" + name + " -> " + codec + "]";
            }
        };
    }

    private StrictFields() {
    }
}
