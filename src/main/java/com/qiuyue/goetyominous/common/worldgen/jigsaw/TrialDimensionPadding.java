package com.qiuyue.goetyominous.common.worldgen.jigsaw;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public record TrialDimensionPadding(int bottom, int top) {

    public static final TrialDimensionPadding ZERO = new TrialDimensionPadding(0, 0);

    private static final Codec<TrialDimensionPadding> RECORD_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            StrictFields.optional("bottom", ExtraCodecs.NON_NEGATIVE_INT, 0).forGetter(TrialDimensionPadding::bottom),
            StrictFields.optional("top", ExtraCodecs.NON_NEGATIVE_INT, 0).forGetter(TrialDimensionPadding::top)
    ).apply(instance, TrialDimensionPadding::new));

    public static final Codec<TrialDimensionPadding> CODEC = Codec.either(ExtraCodecs.NON_NEGATIVE_INT, RECORD_CODEC)
            .xmap(either -> either.map(value -> new TrialDimensionPadding(value, value), Function.identity()),
                    padding -> padding.bottom == padding.top
                            ? Either.left(padding.bottom)
                            : Either.right(padding));

    public boolean isZero() {
        return this.bottom == 0 && this.top == 0;
    }

    public int minY(LevelHeightAccessor level) {
        return level.getMinBuildHeight() + this.bottom;
    }

    public int maxYExclusive(LevelHeightAccessor level) {
        return level.getMaxBuildHeight() - this.top;
    }

    public boolean startTooClose(LevelHeightAccessor level, BoundingBox start) {
        return !this.isZero() && (start.minY() < this.minY(level) || start.maxY() >= this.maxYExclusive(level));
    }
}
