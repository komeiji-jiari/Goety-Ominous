package com.qiuyue.goetyominous.common.blocks.trial;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.SpawnData;

public record SpawnDataWithEquipment(SpawnData spawnData, Optional<EquipmentTable> equipment) {
    public static final Codec<SpawnDataWithEquipment> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CompoundTag.CODEC.fieldOf(SpawnData.ENTITY_TAG)
                    .forGetter(data -> data.spawnData().getEntityToSpawn()),
            SpawnData.CustomSpawnRules.CODEC.optionalFieldOf("custom_spawn_rules")
                    .forGetter(data -> data.spawnData().getCustomSpawnRules()),
            EquipmentTable.CODEC.optionalFieldOf("equipment")
                    .forGetter(SpawnDataWithEquipment::equipment)
    ).apply(instance, (entityToSpawn, customSpawnRules, equipment) ->
            new SpawnDataWithEquipment(new SpawnData(entityToSpawn, customSpawnRules), equipment)));

    public static SpawnDataWithEquipment of(SpawnData spawnData) {
        return new SpawnDataWithEquipment(spawnData, Optional.empty());
    }
}
