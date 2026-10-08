package com.qiuyue.goetyominous.common.blocks.trial;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;

public record EquipmentTable(ResourceLocation lootTable, Map<EquipmentSlot, Float> slotDropChances) {
    public static final Codec<EquipmentSlot> SLOT_CODEC = Codec.STRING.comapFlatMap(name -> {
        EquipmentSlot slot = EquipmentSlot.byName(name);
        return slot == null
                ? DataResult.error(() -> "Unknown equipment slot: " + name)
                : DataResult.success(slot);
    }, EquipmentSlot::getName);

    public static final Codec<Map<EquipmentSlot, Float>> SLOT_DROP_CHANCES_CODEC =
            Codec.either(Codec.FLOAT, Codec.unboundedMap(SLOT_CODEC, Codec.FLOAT)).xmap(
                    either -> either.map(EquipmentTable::createForAllSlots, map -> map),
                    map -> {
                        boolean allSame = map.values().stream().distinct().count() == 1L;
                        boolean allSlots = map.keySet().containsAll(Arrays.asList(EquipmentSlot.values()));
                        if (allSame && allSlots) {
                            return Either.left(map.values().stream().findFirst().orElse(0.0F));
                        }
                        return Either.right(map);
                    });

    public static final Codec<EquipmentTable> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("loot_table").forGetter(EquipmentTable::lootTable),
            SLOT_DROP_CHANCES_CODEC.optionalFieldOf("slot_drop_chances", Map.of())
                    .forGetter(EquipmentTable::slotDropChances)
    ).apply(instance, EquipmentTable::new));

    public static Map<EquipmentSlot, Float> createForAllSlots(float chance) {
        return createForAllSlots(List.of(EquipmentSlot.values()), chance);
    }

    public static Map<EquipmentSlot, Float> createForAllSlots(List<EquipmentSlot> slots, float chance) {
        Map<EquipmentSlot, Float> map = new HashMap<>();
        for (EquipmentSlot slot : slots) {
            map.put(slot, chance);
        }
        return map;
    }
}
