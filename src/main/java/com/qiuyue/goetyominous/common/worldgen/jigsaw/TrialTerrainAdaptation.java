package com.qiuyue.goetyominous.common.worldgen.jigsaw;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;

public enum TrialTerrainAdaptation implements StringRepresentable {
    NONE("none", TerrainAdjustment.NONE),
    BURY("bury", TerrainAdjustment.BURY),
    BEARD_THIN("beard_thin", TerrainAdjustment.BEARD_THIN),
    BEARD_BOX("beard_box", TerrainAdjustment.BEARD_BOX),
    ENCAPSULATE("encapsulate", TerrainAdjustment.NONE);

    public static final Codec<TrialTerrainAdaptation> CODEC = StringRepresentable.fromEnum(TrialTerrainAdaptation::values);

    private final String id;
    private final TerrainAdjustment vanilla;

    TrialTerrainAdaptation(String id, TerrainAdjustment vanilla) {
        this.id = id;
        this.vanilla = vanilla;
    }

    public TerrainAdjustment vanilla() {
        return this.vanilla;
    }

    public boolean hasMargin() {
        return this != NONE;
    }

    @Override
    public String getSerializedName() {
        return this.id;
    }
}
