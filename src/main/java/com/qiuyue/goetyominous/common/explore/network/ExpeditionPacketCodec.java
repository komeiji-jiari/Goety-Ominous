package com.qiuyue.goetyominous.common.explore.network;

import com.qiuyue.goetyominous.common.explore.ExpeditionKind;
import com.qiuyue.goetyominous.common.explore.ExpeditionSnapshot;
import com.qiuyue.goetyominous.common.explore.ExpeditionTuning;
import com.qiuyue.goetyominous.common.explore.TravelerStatus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

final class ExpeditionPacketCodec {
    private static final int MAX_LIST = 4096;
    private static final int MAX_TRAVELERS = 64;
    private static final int MAX_EXPEDITIONS = 1024;

    private ExpeditionPacketCodec() {
    }

    static void writeTuning(FriendlyByteBuf buffer, ExpeditionTuning tuning) {
        buffer.writeInt(tuning.bindLimit());
        buffer.writeInt(tuning.minTravelers());
        buffer.writeInt(tuning.maxTravelers());
        buffer.writeInt(tuning.baseDraws());
        buffer.writeDouble(tuning.extraDrawChance());
        buffer.writeInt(tuning.biomeTicks());
        buffer.writeInt(tuning.ruinTicks());
        buffer.writeDouble(tuning.deathChance());
        buffer.writeInt(tuning.deathDecreaseStart());
        buffer.writeDouble(tuning.deathDecreasePerTraveler());
        buffer.writeDouble(tuning.deathChanceFloor());
        buffer.writeInt(tuning.baseOres());
        buffer.writeInt(tuning.oresPerPickaxe());
        buffer.writeInt(tuning.baseMobDraws());
        buffer.writeInt(tuning.mobDrawsPerCrossbow());
        buffer.writeInt(tuning.dispatchRadius());
        buffer.writeBoolean(tuning.recallLosesTravelers());
    }

    static ExpeditionTuning readTuning(FriendlyByteBuf buffer) {
        return new ExpeditionTuning(
                buffer.readInt(),
                buffer.readInt(),
                buffer.readInt(),
                buffer.readInt(),
                buffer.readDouble(),
                buffer.readInt(),
                buffer.readInt(),
                buffer.readDouble(),
                buffer.readInt(),
                buffer.readDouble(),
                buffer.readDouble(),
                buffer.readInt(),
                buffer.readInt(),
                buffer.readInt(),
                buffer.readInt(),
                buffer.readInt(),
                buffer.readBoolean()
        );
    }

    static void writeDimensions(FriendlyByteBuf buffer, List<ExpeditionSnapshot.DimensionEntry> entries) {
        buffer.writeInt(entries.size());
        for (ExpeditionSnapshot.DimensionEntry entry : entries) {
            buffer.writeResourceLocation(entry.id());
            buffer.writeBoolean(entry.unlocked());
        }
    }

    static List<ExpeditionSnapshot.DimensionEntry> readDimensions(FriendlyByteBuf buffer) {
        int size = clamp(buffer.readInt(), MAX_LIST);
        List<ExpeditionSnapshot.DimensionEntry> out = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            out.add(new ExpeditionSnapshot.DimensionEntry(buffer.readResourceLocation(), buffer.readBoolean()));
        }
        return out;
    }

    static void writeBiomes(FriendlyByteBuf buffer, List<ExpeditionSnapshot.BiomeEntry> entries) {
        buffer.writeInt(entries.size());
        for (ExpeditionSnapshot.BiomeEntry entry : entries) {
            buffer.writeResourceLocation(entry.id());
            buffer.writeBoolean(entry.hasLoot());
        }
    }

    static List<ExpeditionSnapshot.BiomeEntry> readBiomes(FriendlyByteBuf buffer) {
        int size = clamp(buffer.readInt(), MAX_LIST);
        List<ExpeditionSnapshot.BiomeEntry> out = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            out.add(new ExpeditionSnapshot.BiomeEntry(buffer.readResourceLocation(), buffer.readBoolean()));
        }
        return out;
    }

    static void writeRuins(FriendlyByteBuf buffer, List<ExpeditionSnapshot.RuinEntry> entries) {
        buffer.writeInt(entries.size());
        for (ExpeditionSnapshot.RuinEntry entry : entries) {
            buffer.writeResourceLocation(entry.id());
            buffer.writeBoolean(entry.visited());
        }
    }

    static List<ExpeditionSnapshot.RuinEntry> readRuins(FriendlyByteBuf buffer) {
        int size = clamp(buffer.readInt(), MAX_LIST);
        List<ExpeditionSnapshot.RuinEntry> out = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            out.add(new ExpeditionSnapshot.RuinEntry(buffer.readResourceLocation(), buffer.readBoolean()));
        }
        return out;
    }

    static void writeTravelers(FriendlyByteBuf buffer, List<ExpeditionSnapshot.TravelerEntry> entries) {
        buffer.writeInt(entries.size());
        for (ExpeditionSnapshot.TravelerEntry entry : entries) {
            buffer.writeUUID(entry.id());
            String name = entry.name() == null ? "" : entry.name();
            buffer.writeUtf(name.substring(0, Math.min(name.length(), 64)), 64);
            buffer.writeBoolean(entry.typeId() != null);
            if (entry.typeId() != null) {
                buffer.writeResourceLocation(entry.typeId());
            }
            buffer.writeEnum(entry.status());
            buffer.writeFloat(entry.health());
            buffer.writeFloat(entry.maxHealth());
            buffer.writeBoolean(entry.pickaxe());
            buffer.writeBoolean(entry.crossbow());
        }
    }

    static List<ExpeditionSnapshot.TravelerEntry> readTravelers(FriendlyByteBuf buffer) {
        int size = clamp(buffer.readInt(), MAX_TRAVELERS);
        List<ExpeditionSnapshot.TravelerEntry> out = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            UUID id = buffer.readUUID();
            String name = buffer.readUtf(64);
            ResourceLocation typeId = buffer.readBoolean() ? buffer.readResourceLocation() : null;
            TravelerStatus status = buffer.readEnum(TravelerStatus.class);
            float health = buffer.readFloat();
            float maxHealth = buffer.readFloat();
            boolean pickaxe = buffer.readBoolean();
            boolean crossbow = buffer.readBoolean();
            out.add(new ExpeditionSnapshot.TravelerEntry(id, name, typeId, status, health, maxHealth, pickaxe, crossbow));
        }
        return out;
    }

    static void writeExpeditions(FriendlyByteBuf buffer, List<ExpeditionSnapshot.ExpeditionInfo> entries) {
        buffer.writeInt(entries.size());
        for (ExpeditionSnapshot.ExpeditionInfo entry : entries) {
            buffer.writeUUID(entry.expeditionId());
            buffer.writeEnum(entry.kind());
            buffer.writeResourceLocation(entry.dimension());
            writeOptionalId(buffer, entry.biome());
            writeOptionalId(buffer, entry.structure());
            buffer.writeInt(entry.totalCount());
            buffer.writeLong(entry.remainingTicks());
            buffer.writeLong(entry.durationTicks());
            buffer.writeBoolean(entry.settled());
            buffer.writeBoolean(entry.delivering());
        }
    }

    static List<ExpeditionSnapshot.ExpeditionInfo> readExpeditions(FriendlyByteBuf buffer) {
        int size = clamp(buffer.readInt(), MAX_EXPEDITIONS);
        List<ExpeditionSnapshot.ExpeditionInfo> out = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            out.add(new ExpeditionSnapshot.ExpeditionInfo(
                    buffer.readUUID(),
                    buffer.readEnum(ExpeditionKind.class),
                    buffer.readResourceLocation(),
                    readOptionalId(buffer),
                    readOptionalId(buffer),
                    buffer.readInt(),
                    buffer.readLong(),
                    buffer.readLong(),
                    buffer.readBoolean(),
                    buffer.readBoolean()
            ));
        }
        return out;
    }

    static void writeOptionalId(FriendlyByteBuf buffer, ResourceLocation id) {
        buffer.writeBoolean(id != null);
        if (id != null) {
            buffer.writeResourceLocation(id);
        }
    }

    static ResourceLocation readOptionalId(FriendlyByteBuf buffer) {
        return buffer.readBoolean() ? buffer.readResourceLocation() : null;
    }

    static int clamp(int value, int max) {
        if (value < 0 || value > max) {
            return 0;
        }
        return value;
    }
}
