package com.qiuyue.goetyominous.common.explore;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ExpeditionData extends SavedData {
    private static final String DATA_NAME = "goetyominous_expeditions";
    private static final String PLAYERS = "Players";
    private static final String EXPEDITIONS = "Expeditions";
    private static final String REMAPS = "Remaps";
    private static final String DEAD_BINDINGS = "DeadBindings";

    private final Map<UUID, PlayerData> players = new HashMap<>();
    private final Map<UUID, UUID> remaps = new HashMap<>();
    private final Set<UUID> deadBindings = new LinkedHashSet<>();

    public static ExpeditionData load(CompoundTag tag) {
        ExpeditionData data = new ExpeditionData();
        ListTag playerList = tag.getList(PLAYERS, Tag.TAG_COMPOUND);
        for (int i = 0; i < playerList.size(); i++) {
            CompoundTag entry = playerList.getCompound(i);
            UUID uuid = readUuid(entry, "Uuid");
            if (uuid == null) {
                continue;
            }
            PlayerData playerData = new PlayerData();
            ListTag dimensions = entry.getList("Dimensions", Tag.TAG_STRING);
            for (int d = 0; d < dimensions.size(); d++) {
                ResourceKey<Level> dimension = readDimension(dimensions.getString(d));
                if (dimension != null) {
                    playerData.dimensions.add(dimension);
                }
            }
            ListTag ruins = entry.getList("Ruins", Tag.TAG_COMPOUND);
            for (int r = 0; r < ruins.size(); r++) {
                CompoundTag ruin = ruins.getCompound(r);
                ResourceKey<Level> dimension = readDimension(ruin.getString("Dim"));
                ResourceLocation structure = readLocation(ruin.getString("Id"));
                if (dimension == null || structure == null) {
                    continue;
                }
                playerData.ruins.computeIfAbsent(dimension, key -> new LinkedHashSet<>()).add(structure);
            }
            data.players.put(uuid, playerData);
        }

        ListTag expeditionList = tag.getList(EXPEDITIONS, Tag.TAG_COMPOUND);
        for (int i = 0; i < expeditionList.size(); i++) {
            CompoundTag entry = expeditionList.getCompound(i);
            UUID id = readUuid(entry, "Id");
            UUID owner = readUuid(entry, "Owner");
            ResourceKey<Level> dimension = readDimension(entry.getString("Dimension"));
            if (id == null || owner == null || dimension == null) {
                continue;
            }
            List<UUID> members = readUuidList(entry.getList("Members", Tag.TAG_STRING));
            List<CompoundTag> snapshots = new ArrayList<>();
            ListTag snapshotList = entry.getList("Snapshots", Tag.TAG_COMPOUND);
            for (int s = 0; s < snapshotList.size(); s++) {
                snapshots.add(snapshotList.getCompound(s).copy());
            }
            ActiveExpedition active = new ActiveExpedition(
                    id,
                    owner,
                    readPos(entry),
                    dimension,
                    readKind(entry.getInt("Kind")),
                    entry.getLong("Start"),
                    entry.getLong("End"),
                    readLocation(entry.getString("Biome")),
                    readLocation(entry.getString("Structure")),
                    members,
                    snapshots,
                    readUuidList(entry.getList("Dead", Tag.TAG_STRING)),
                    entry.getInt("Draws"),
                    entry.getInt("Ores"),
                    entry.getInt("MobDraws"),
                    entry.getDouble("DeathChance"),
                    entry.getBoolean("Settled")
            );
            PlayerData playerData = data.players.computeIfAbsent(owner, key -> new PlayerData());
            playerData.explorations.put(id, active);
            if (entry.contains("Delivery", Tag.TAG_COMPOUND)) {
                playerData.deliveries.put(id, ExpeditionDelivery.load(entry.getCompound("Delivery")));
            }
        }

        CompoundTag remapTag = tag.getCompound(REMAPS);
        for (String key : remapTag.getAllKeys()) {
            UUID from = parseUuid(key);
            UUID to = parseUuid(remapTag.getString(key));
            if (from != null && to != null && !from.equals(to)) {
                data.remaps.put(from, to);
            }
        }
        for (UUID uuid : readUuidList(tag.getList(DEAD_BINDINGS, Tag.TAG_STRING))) {
            data.deadBindings.add(uuid);
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag playerList = new ListTag();
        for (Map.Entry<UUID, PlayerData> mapEntry : players.entrySet()) {
            PlayerData playerData = mapEntry.getValue();
            if (playerData.dimensions.isEmpty() && playerData.ruins.isEmpty()) {
                continue;
            }
            CompoundTag entry = new CompoundTag();
            entry.putUUID("Uuid", mapEntry.getKey());
            ListTag dimensions = new ListTag();
            for (ResourceKey<Level> dimension : playerData.dimensions) {
                dimensions.add(StringTag.valueOf(dimension.location().toString()));
            }
            entry.put("Dimensions", dimensions);
            ListTag ruins = new ListTag();
            for (Map.Entry<ResourceKey<Level>, Set<ResourceLocation>> ruinEntry : playerData.ruins.entrySet()) {
                for (ResourceLocation structure : ruinEntry.getValue()) {
                    CompoundTag ruin = new CompoundTag();
                    ruin.putString("Dim", ruinEntry.getKey().location().toString());
                    ruin.putString("Id", structure.toString());
                    ruins.add(ruin);
                }
            }
            entry.put("Ruins", ruins);
            playerList.add(entry);
        }
        tag.put(PLAYERS, playerList);

        ListTag expeditionList = new ListTag();
        for (Map.Entry<UUID, PlayerData> mapEntry : players.entrySet()) {
            for (ActiveExpedition active : mapEntry.getValue().explorations.values()) {
                CompoundTag entry = new CompoundTag();
                entry.putUUID("Id", active.id());
                entry.putUUID("Owner", active.owner());
                entry.putString("Dimension", active.dimension().location().toString());
                entry.putInt("PostedX", active.postedAt().getX());
                entry.putInt("PostedY", active.postedAt().getY());
                entry.putInt("PostedZ", active.postedAt().getZ());
                entry.putInt("Kind", active.kind().ordinal());
                entry.putLong("Start", active.startTick());
                entry.putLong("End", active.endTick());
                if (active.biome() != null) {
                    entry.putString("Biome", active.biome().toString());
                }
                if (active.structure() != null) {
                    entry.putString("Structure", active.structure().toString());
                }
                entry.put("Members", writeUuidList(active.memberUuids()));
                ListTag snapshots = new ListTag();
                for (CompoundTag snapshot : active.snapshots()) {
                    snapshots.add(snapshot.copy());
                }
                entry.put("Snapshots", snapshots);
                entry.put("Dead", writeUuidList(active.dead()));
                entry.putInt("Draws", active.structureDraws());
                entry.putInt("Ores", active.ores());
                entry.putInt("MobDraws", active.mobDraws());
                entry.putDouble("DeathChance", active.deathChance());
                entry.putBoolean("Settled", active.settled());
                ExpeditionDelivery delivery = mapEntry.getValue().deliveries.get(active.id());
                if (delivery != null) {
                    entry.put("Delivery", delivery.save());
                }
                expeditionList.add(entry);
            }
        }
        tag.put(EXPEDITIONS, expeditionList);

        CompoundTag remapTag = new CompoundTag();
        for (Map.Entry<UUID, UUID> remap : remaps.entrySet()) {
            remapTag.putString(remap.getKey().toString(), remap.getValue().toString());
        }
        tag.put(REMAPS, remapTag);
        tag.put(DEAD_BINDINGS, writeUuidList(new ArrayList<>(deadBindings)));
        return tag;
    }

    public static ExpeditionData get(MinecraftServer server) {
        return server.overworld().getDataStorage()
                .computeIfAbsent(ExpeditionData::load, ExpeditionData::new, DATA_NAME);
    }

    public boolean isDimensionVisited(UUID player, ResourceKey<Level> dimension) {
        PlayerData playerData = players.get(player);
        return playerData != null && playerData.dimensions.contains(dimension);
    }

    public Set<ResourceKey<Level>> visitedDimensions(UUID player) {
        PlayerData playerData = players.get(player);
        return playerData == null ? Set.of() : Set.copyOf(playerData.dimensions);
    }

    public boolean markDimensionVisited(UUID player, ResourceKey<Level> dimension) {
        PlayerData playerData = players.computeIfAbsent(player, key -> new PlayerData());
        if (playerData.dimensions.add(dimension)) {
            setDirty();
            return true;
        }
        return false;
    }

    public boolean isRuinVisited(UUID player, ResourceKey<Level> dimension, ResourceLocation structure) {
        PlayerData playerData = players.get(player);
        return playerData != null && playerData.ruins.getOrDefault(dimension, Set.of()).contains(structure);
    }

    public Set<ResourceLocation> visitedRuins(UUID player, ResourceKey<Level> dimension) {
        PlayerData playerData = players.get(player);
        return playerData == null ? Set.of() : Set.copyOf(playerData.ruins.getOrDefault(dimension, Set.of()));
    }

    public boolean markRuinVisited(UUID player, ResourceKey<Level> dimension, ResourceLocation structure) {
        PlayerData playerData = players.computeIfAbsent(player, key -> new PlayerData());
        if (playerData.ruins.computeIfAbsent(dimension, key -> new LinkedHashSet<>()).add(structure)) {
            setDirty();
            return true;
        }
        return false;
    }

    public List<ActiveExpedition> activeExpeditions(UUID owner) {
        PlayerData playerData = players.get(owner);
        return playerData == null ? List.of() : List.copyOf(playerData.explorations.values());
    }

    public List<ActiveExpedition> allExpeditions() {
        List<ActiveExpedition> out = new ArrayList<>();
        for (PlayerData playerData : players.values()) {
            out.addAll(playerData.explorations.values());
        }
        return out;
    }

    public ActiveExpedition expedition(UUID owner, UUID expeditionId) {
        PlayerData playerData = players.get(owner);
        return playerData == null ? null : playerData.explorations.get(expeditionId);
    }

    public void putExpedition(ActiveExpedition active) {
        players.computeIfAbsent(active.owner(), key -> new PlayerData()).explorations.put(active.id(), active);
        setDirty();
    }

    public void removeExpedition(UUID owner, UUID expeditionId) {
        PlayerData playerData = players.get(owner);
        if (playerData != null && playerData.explorations.remove(expeditionId) != null) {
            playerData.deliveries.remove(expeditionId);
            setDirty();
        }
    }

    public ExpeditionDelivery delivery(UUID owner, UUID expeditionId) {
        PlayerData playerData = players.get(owner);
        return playerData == null ? null : playerData.deliveries.get(expeditionId);
    }

    public void putDelivery(UUID owner, UUID expeditionId, ExpeditionDelivery delivery) {
        PlayerData playerData = players.get(owner);
        if (playerData == null || !playerData.explorations.containsKey(expeditionId)) {
            return;
        }
        playerData.deliveries.put(expeditionId, delivery);
        setDirty();
    }

    public Map<ExpeditionRef, ActiveExpedition> dueExpeditions(long gameTime) {
        LinkedHashMap<ExpeditionRef, ActiveExpedition> due = new LinkedHashMap<>();
        for (Map.Entry<UUID, PlayerData> mapEntry : players.entrySet()) {
            for (ActiveExpedition active : mapEntry.getValue().explorations.values()) {
                if (!active.settled() && active.endTick() <= gameTime) {
                    due.put(new ExpeditionRef(mapEntry.getKey(), active.id()), active);
                }
            }
        }
        return due;
    }

    public boolean isInActiveSnapshot(UUID uuid) {
        for (PlayerData playerData : players.values()) {
            for (ActiveExpedition active : playerData.explorations.values()) {
                if (active.memberUuids().contains(uuid)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isInAnyExpedition(UUID uuid) {
        for (PlayerData playerData : players.values()) {
            for (ActiveExpedition active : playerData.explorations.values()) {
                if (active.memberUuids().contains(uuid) || active.dead().contains(uuid)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isDeadInExpedition(UUID uuid) {
        for (PlayerData playerData : players.values()) {
            for (ActiveExpedition active : playerData.explorations.values()) {
                if (active.dead().contains(uuid)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isDeadBinding(UUID uuid) {
        return deadBindings.contains(uuid) || isDeadInExpedition(uuid);
    }

    public void markDeadBinding(UUID uuid) {
        if (deadBindings.add(uuid)) {
            setDirty();
        }
    }

    public UUID resolveRemap(UUID uuid) {
        UUID current = uuid;
        for (int i = 0; i < 32; i++) {
            UUID next = remaps.get(current);
            if (next == null || next.equals(current)) {
                return current;
            }
            current = next;
        }
        return current;
    }

    public void addRemap(UUID oldUuid, UUID newUuid) {
        if (oldUuid == null || newUuid == null || oldUuid.equals(newUuid)) {
            return;
        }
        UUID resolved = resolveRemap(newUuid);
        if (!resolved.equals(oldUuid)) {
            remaps.put(oldUuid, resolved);
            setDirty();
        }
    }

    private static UUID readUuid(CompoundTag tag, String key) {
        if (!tag.contains(key)) {
            return null;
        }
        try {
            return tag.getUUID(key);
        } catch (RuntimeException broken) {
            return null;
        }
    }

    private static UUID parseUuid(String value) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException broken) {
            return null;
        }
    }

    private static ResourceKey<Level> readDimension(String value) {
        ResourceLocation id = readLocation(value);
        return id == null ? null : ResourceKey.create(Registries.DIMENSION, id);
    }

    private static ResourceLocation readLocation(String value) {
        return value == null || value.isEmpty() ? null : ResourceLocation.tryParse(value);
    }

    private static ExpeditionKind readKind(int ordinal) {
        ExpeditionKind[] values = ExpeditionKind.values();
        return ordinal >= 0 && ordinal < values.length ? values[ordinal] : ExpeditionKind.BIOME;
    }

    private static BlockPos readPos(CompoundTag tag) {
        if (!tag.contains("PostedX") || !tag.contains("PostedY") || !tag.contains("PostedZ")) {
            return BlockPos.ZERO;
        }
        return new BlockPos(tag.getInt("PostedX"), tag.getInt("PostedY"), tag.getInt("PostedZ"));
    }

    private static List<UUID> readUuidList(ListTag list) {
        List<UUID> out = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            UUID uuid = parseUuid(list.getString(i));
            if (uuid != null) {
                out.add(uuid);
            }
        }
        return out;
    }

    private static ListTag writeUuidList(List<UUID> uuids) {
        ListTag list = new ListTag();
        for (UUID uuid : uuids) {
            list.add(StringTag.valueOf(uuid.toString()));
        }
        return list;
    }

    private static class PlayerData {
        final Set<ResourceKey<Level>> dimensions = new LinkedHashSet<>();
        final Map<ResourceKey<Level>, Set<ResourceLocation>> ruins = new HashMap<>();
        final Map<UUID, ActiveExpedition> explorations = new LinkedHashMap<>();
        final Map<UUID, ExpeditionDelivery> deliveries = new LinkedHashMap<>();
    }

    public record ActiveExpedition(
            UUID id,
            UUID owner,
            BlockPos postedAt,
            ResourceKey<Level> dimension,
            ExpeditionKind kind,
            long startTick,
            long endTick,
            ResourceLocation biome,
            ResourceLocation structure,
            List<UUID> memberUuids,
            List<CompoundTag> snapshots,
            List<UUID> dead,
            int structureDraws,
            int ores,
            int mobDraws,
            double deathChance,
            boolean settled
    ) {
        public ActiveExpedition {
            memberUuids = List.copyOf(memberUuids);
            snapshots = List.copyOf(snapshots);
            dead = List.copyOf(dead);
        }

        public int aliveCount() {
            return snapshots.size();
        }

        public int totalCount() {
            return snapshots.size() + dead.size();
        }

        public ActiveExpedition withSettled() {
            return new ActiveExpedition(id, owner, postedAt, dimension, kind, startTick, endTick,
                    biome, structure, memberUuids, snapshots, dead, structureDraws, ores, mobDraws,
                    deathChance, true);
        }
    }

    public record ExpeditionRef(UUID owner, UUID expeditionId) {
    }
}
