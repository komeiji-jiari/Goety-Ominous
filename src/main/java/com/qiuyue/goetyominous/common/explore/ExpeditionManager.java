package com.qiuyue.goetyominous.common.explore;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.items.explore.ExpeditionCompassItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class ExpeditionManager {
    public static final int CHECK_INTERVAL = 20;

    private ExpeditionManager() {
    }

    public static Component startExploration(ServerPlayer player, ExpeditionKind kind, ResourceKey<Level> dimension,
                                             ResourceLocation biome, ResourceLocation structure, List<UUID> selected,
                                             InteractionHand hand) {
        if (!(player.level() instanceof ServerLevel currentLevel)) {
            return fail("level");
        }
        ItemStack held = player.getItemInHand(hand);
        if (!(held.getItem() instanceof ExpeditionCompassItem compass)) {
            return fail("compass");
        }
        compass.reconcile(held, player);
        MinecraftServer server = player.getServer();
        if (server == null) {
            return fail("level");
        }
        ExpeditionData data = ExpeditionData.get(server);
        ExpeditionTuning tuning = ExpeditionTuning.fromConfig();
        LinkedHashSet<UUID> uniqueSet = new LinkedHashSet<>();
        for (UUID id : selected) {
            uniqueSet.add(data.resolveRemap(id));
        }
        List<UUID> unique = new ArrayList<>(uniqueSet);
        int min = tuning.minTravelers();
        int max = tuning.effectiveMaxTravelers();
        if (unique.size() < min || unique.size() > max) {
            return fail("count", min, max);
        }
        ServerLevel targetLevel = server.getLevel(dimension);
        if (targetLevel == null || !data.isDimensionVisited(player.getUUID(), dimension)) {
            return fail("dimension");
        }
        StructureIndex index = StructureIndex.get(server);
        if (kind == ExpeditionKind.BIOME) {
            if (biome == null || !biomeExists(targetLevel, biome)) {
                return fail("biome");
            }
        } else {
            if (structure == null || !index.hasLoot(structure)) {
                return fail("ruin");
            }
            if (!index.inDimension(structure, biomeIds(targetLevel))) {
                return fail("ruin");
            }
            if (!data.isRuinVisited(player.getUUID(), dimension, structure)) {
                return fail("ruinUnvisited");
            }
        }

        Set<UUID> bound = new LinkedHashSet<>();
        for (UUID uuid : ExpeditionCompassItem.getBound(held)) {
            bound.add(data.resolveRemap(uuid));
        }
        List<LivingEntity> travelers = new ArrayList<>();
        for (UUID id : unique) {
            if (!bound.contains(id)) {
                return fail("notBound");
            }
            Entity entity = findLoaded(server, id);
            if (!(entity instanceof LivingEntity living)) {
                return fail("gone");
            }
            TravelerStatus status = compass.evaluate(player, living);
            if (status != TravelerStatus.READY) {
                return fail(statusKey(status));
            }
            travelers.add(living);
        }

        RandomSource random = currentLevel.random;
        double chance = tuning.deathChanceFor(travelers.size());
        List<UUID> members = new ArrayList<>();
        List<CompoundTag> snapshots = new ArrayList<>();
        List<UUID> dead = new ArrayList<>();
        int pickaxes = 0;
        int crossbows = 0;
        for (LivingEntity traveler : travelers) {
            if (random.nextFloat() < chance) {
                dead.add(traveler.getUUID());
                data.markDeadBinding(traveler.getUUID());
                continue;
            }
            members.add(traveler.getUUID());
            snapshots.add(makeSnapshot(traveler));
            ItemStack mainHand = traveler.getMainHandItem();
            if (mainHand.getItem() instanceof PickaxeItem) {
                pickaxes++;
            }
            if (mainHand.getItem() instanceof CrossbowItem) {
                crossbows++;
            }
        }
        int extraDraws = 0;
        for (int i = 0; i < tuning.extraDrawRolls(travelers.size()); i++) {
            if (random.nextFloat() < tuning.extraDrawChance()) {
                extraDraws++;
            }
        }
        int draws = tuning.baseDraws() + extraDraws;
        int ores = tuning.baseOres() + pickaxes * tuning.oresPerPickaxe();
        int mobDraws = tuning.baseMobDraws() + crossbows * tuning.mobDrawsPerCrossbow();
        int duration = tuning.ticks(kind);
        long now = server.overworld().getGameTime();
        BlockPos postedAt = player.blockPosition();
        ExpeditionData.ActiveExpedition active = new ExpeditionData.ActiveExpedition(
                UUID.randomUUID(),
                player.getUUID(),
                postedAt,
                dimension,
                kind,
                now,
                now + duration,
                kind == ExpeditionKind.BIOME ? biome : null,
                kind == ExpeditionKind.RUIN ? structure : null,
                members,
                snapshots,
                dead,
                draws,
                ores,
                mobDraws,
                chance,
                false
        );
        data.putExpedition(active);
        data.markDimensionVisited(player.getUUID(), dimension);
        flush(server);
        for (LivingEntity traveler : travelers) {
            traveler.discard();
        }
        currentLevel.playSound(null, postedAt, SoundEvents.ENDER_EYE_LAUNCH, SoundSource.PLAYERS, 0.8F, 1.4F);
        player.displayClientMessage(Component.translatable("message.goetyominous.expedition.started",
                travelers.size(), formatDuration(duration)), false);
        GoetyOminous.LOGGER.info("[goetyominous] Expedition {} started by {}: {} travelers, {} dead, {} snapshots, {} draws, {} ores, {} mob draws.",
                active.id(), player.getGameProfile().getName(), travelers.size(), dead.size(), snapshots.size(),
                draws, ores, mobDraws);
        return null;
    }

    public static Component claimExploration(ServerPlayer player, UUID expeditionId) {
        return ExpeditionDeliveryService.deliver(player, expeditionId, false);
    }

    public static Component recallExploration(ServerPlayer player, UUID expeditionId) {
        return ExpeditionDeliveryService.deliver(player, expeditionId, true);
    }

    public static void tick(MinecraftServer server) {
        if (server.getTickCount() % CHECK_INTERVAL != 0) {
            return;
        }
        ExpeditionData data = ExpeditionData.get(server);
        long now = server.overworld().getGameTime();
        Map<ExpeditionData.ExpeditionRef, ExpeditionData.ActiveExpedition> due = data.dueExpeditions(now);
        if (!due.isEmpty()) {
            for (Map.Entry<ExpeditionData.ExpeditionRef, ExpeditionData.ActiveExpedition> entry : due.entrySet()) {
                ExpeditionData.ActiveExpedition active = entry.getValue();
                data.putExpedition(active.withSettled());
                GoetyOminous.LOGGER.info("[goetyominous] Expedition {} of {} settled, waiting for delivery.",
                        active.id(), active.owner());
            }
            flush(server);
        }
        ExpeditionViewSessions.tick(server);
    }

    public static ExpeditionSnapshot snapshot(ServerPlayer player, ResourceKey<Level> selectedDimension,
                                              InteractionHand hand, boolean authoritative, Component feedback) {
        MinecraftServer server = player.getServer();
        if (server == null) {
            return new ExpeditionSnapshot(player.level().dimension().location(), null, hand,
                    ExpeditionTuning.fromConfig(), authoritative, false, false,
                    List.of(), List.of(), List.of(), List.of(), List.of(),
                    feedback == null ? Component.empty() : feedback);
        }
        ExpeditionData data = ExpeditionData.get(server);
        StructureIndex index = StructureIndex.get(server);
        ServerLevel targetLevel = selectedDimension == null ? null : server.getLevel(selectedDimension);
        ResourceKey<Level> effective = targetLevel == null ? player.level().dimension() : selectedDimension;
        if (targetLevel == null) {
            targetLevel = server.getLevel(effective);
        }

        List<ExpeditionSnapshot.DimensionEntry> dimensions = new ArrayList<>();
        for (ServerLevel level : server.getAllLevels()) {
            ResourceKey<Level> key = level.dimension();
            dimensions.add(new ExpeditionSnapshot.DimensionEntry(key.location(), data.isDimensionVisited(player.getUUID(), key)));
        }
        dimensions.sort(Comparator.comparing(entry -> entry.id().toString()));

        List<ExpeditionSnapshot.BiomeEntry> biomes = new ArrayList<>();
        List<ExpeditionSnapshot.RuinEntry> ruins = new ArrayList<>();
        if (targetLevel != null) {
            for (ResourceLocation biome : index.sortedBiomes(targetLevel)) {
                biomes.add(new ExpeditionSnapshot.BiomeEntry(biome, index.hasStructureLoot(biome)));
            }
            Set<ResourceLocation> biomeIds = biomeIds(targetLevel);
            List<ResourceLocation> structures = new ArrayList<>(index.structuresWithLoot());
            structures.sort(Comparator.comparing(ResourceLocation::toString));
            for (ResourceLocation structure : structures) {
                if (index.inDimension(structure, biomeIds)) {
                    ruins.add(new ExpeditionSnapshot.RuinEntry(structure,
                            data.isRuinVisited(player.getUUID(), effective, structure)));
                }
            }
        }

        List<ExpeditionSnapshot.TravelerEntry> travelers = travelerEntries(player, hand);
        List<ExpeditionSnapshot.ExpeditionInfo> expeditions = expeditionInfos(player);

        ResourceLocation playerBiome = player.level().getBiome(player.blockPosition()).unwrapKey()
                .map(ResourceKey::location).orElse(null);
        boolean oresAvailable = targetLevel != null && !DimensionLootPool.oreTables(targetLevel).isEmpty();
        boolean mobsAvailable = targetLevel != null && !DimensionLootPool.mobTables(targetLevel).isEmpty();
        return new ExpeditionSnapshot(effective.location(), playerBiome, hand, ExpeditionTuning.fromConfig(),
                authoritative, oresAvailable, mobsAvailable, dimensions, biomes, ruins, travelers, expeditions,
                feedback == null ? Component.empty() : feedback);
    }

    public static List<ExpeditionSnapshot.TravelerEntry> travelerEntries(ServerPlayer player, InteractionHand hand) {
        MinecraftServer server = player.getServer();
        if (server == null) {
            return List.of();
        }
        ItemStack held = player.getItemInHand(hand);
        if (!(held.getItem() instanceof ExpeditionCompassItem compass)) {
            return List.of();
        }
        ExpeditionData data = ExpeditionData.get(server);
        compass.reconcile(held, player);
        Map<UUID, ExpeditionSnapshot.TravelerEntry> entries = new LinkedHashMap<>();
        for (UUID id : ExpeditionCompassItem.getBound(held)) {
            UUID current = data.resolveRemap(id);
            if (entries.containsKey(current)) {
                continue;
            }
            if (data.isDeadBinding(current)) {
                continue;
            }
            if (data.isInActiveSnapshot(current)) {
                entries.put(current, new ExpeditionSnapshot.TravelerEntry(current,
                        awayName(data, current), null, TravelerStatus.IN_EXPEDITION,
                        0.0F, 0.0F, false, false));
                continue;
            }
            Entity entity = findLoaded(server, current);
            if (entity == null) {
                entries.put(current, new ExpeditionSnapshot.TravelerEntry(current,
                        current.toString().substring(0, 8), null, TravelerStatus.UNLOADED,
                        0.0F, 0.0F, false, false));
                continue;
            }
            TravelerStatus status = compass.evaluate(player, entity);
            if (status == TravelerStatus.DEAD) {
                continue;
            }
            ResourceLocation typeId = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
            float health = 0.0F;
            float maxHealth = 0.0F;
            boolean pickaxe = false;
            boolean crossbow = false;
            if (entity instanceof LivingEntity living) {
                health = living.getHealth();
                maxHealth = living.getMaxHealth();
                pickaxe = living.getMainHandItem().getItem() instanceof PickaxeItem;
                crossbow = living.getMainHandItem().getItem() instanceof CrossbowItem;
            }
            String name = entity.getDisplayName().getString();
            if (name.length() > 64) {
                name = name.substring(0, 64);
            }
            entries.put(current, new ExpeditionSnapshot.TravelerEntry(current, name, typeId, status,
                    health, maxHealth, pickaxe, crossbow));
        }
        return new ArrayList<>(entries.values());
    }

    public static List<ExpeditionSnapshot.ExpeditionInfo> expeditionInfos(ServerPlayer player) {
        MinecraftServer server = player.getServer();
        if (server == null) {
            return List.of();
        }
        ExpeditionData data = ExpeditionData.get(server);
        long now = server.overworld().getGameTime();
        List<ExpeditionSnapshot.ExpeditionInfo> expeditions = new ArrayList<>();
        for (ExpeditionData.ActiveExpedition active : data.activeExpeditions(player.getUUID())) {
            expeditions.add(new ExpeditionSnapshot.ExpeditionInfo(
                    active.id(),
                    active.kind(),
                    active.dimension().location(),
                    active.biome(),
                    active.structure(),
                    active.totalCount(),
                    Math.max(0L, active.endTick() - now),
                    Math.max(1L, active.endTick() - active.startTick()),
                    active.settled(),
                    data.delivery(player.getUUID(), active.id()) != null
            ));
        }
        expeditions.sort(Comparator
                .comparingInt((ExpeditionSnapshot.ExpeditionInfo info) -> info.delivering() ? 0 : (info.settled() ? 1 : 2))
                .thenComparingLong(ExpeditionSnapshot.ExpeditionInfo::remainingTicks)
                .thenComparing(info -> info.expeditionId().toString()));
        return expeditions;
    }

    static ExpeditionRewards rollRewards(ServerLevel targetLevel, ServerLevel playerLevel,
                                         ExpeditionData.ActiveExpedition active, ServerPlayer owner) {
        MinecraftServer server = targetLevel.getServer();
        RandomSource random = targetLevel.random;
        StructureIndex index = StructureIndex.get(server);
        List<ItemStack> structure = new ArrayList<>();
        List<ItemStack> ores = new ArrayList<>();
        List<ItemStack> mobs = new ArrayList<>();
        LinkedHashSet<ResourceLocation> tableIds = new LinkedHashSet<>();
        if (active.kind() == ExpeditionKind.BIOME) {
            if (active.biome() != null) {
                tableIds.addAll(index.lootTablesForBiome(active.biome()));
            }
        } else if (active.structure() != null) {
            tableIds.addAll(index.lootTablesOf(active.structure()));
        }
        List<ResourceLocation> tables = new ArrayList<>(tableIds);
        int draws = tables.isEmpty() ? 0 : Math.max(0, active.structureDraws());
        for (int i = 0; i < draws; i++) {
            ResourceLocation tableId = tables.get(random.nextInt(tables.size()));
            try {
                structure.addAll(LootResolver.sampleTable(targetLevel, server.getLootData().getLootTable(tableId),
                        tableId, active.postedAt(), owner, random.nextLong()));
            } catch (RuntimeException broken) {
                GoetyOminous.LOGGER.error("[goetyominous] Failed drawing loot table {}: {}", tableId, broken.toString());
            }
        }
        if (active.ores() > 0) {
            try {
                ores.addAll(LootResolver.sampleOrePool(targetLevel, DimensionLootPool.oreTables(targetLevel),
                        owner, active.ores(), random));
            } catch (RuntimeException broken) {
                GoetyOminous.LOGGER.error("[goetyominous] Failed drawing ore pool: {}", broken.toString());
            }
        }
        if (active.mobDraws() > 0) {
            try {
                mobs.addAll(LootResolver.sampleMobPool(targetLevel, DimensionLootPool.mobTables(targetLevel),
                        owner, active.mobDraws(), random));
            } catch (RuntimeException broken) {
                GoetyOminous.LOGGER.error("[goetyominous] Failed drawing mob pool: {}", broken.toString());
            }
        }
        return new ExpeditionRewards(structure, ores, mobs);
    }

    public static boolean snapshotHasPickaxe(CompoundTag snapshot) {
        return snapshotMainHand(snapshot).getItem() instanceof PickaxeItem;
    }

    public static boolean snapshotHasCrossbow(CompoundTag snapshot) {
        return snapshotMainHand(snapshot).getItem() instanceof CrossbowItem;
    }

    private static ItemStack snapshotMainHand(CompoundTag snapshot) {
        ListTag handItems = snapshot.getList("HandItems", Tag.TAG_COMPOUND);
        return handItems.isEmpty() ? ItemStack.EMPTY : ItemStack.of(handItems.getCompound(0));
    }

    public static Entity findLoaded(MinecraftServer server, UUID uuid) {
        for (ServerLevel level : server.getAllLevels()) {
            Entity entity = level.getEntity(uuid);
            if (entity != null) {
                return entity;
            }
        }
        return null;
    }

    public static void flush(MinecraftServer server) {
        try {
            server.overworld().getDataStorage().save();
        } catch (RuntimeException broken) {
            GoetyOminous.LOGGER.warn("[goetyominous] Failed to save expedition data: {}", broken.toString());
        }
    }

    public static Component formatDuration(long ticks) {
        long seconds = Math.max(0L, (ticks + 19L) / 20L);
        return Component.translatable("screen.goetyominous.expedition.duration",
                seconds / 60L, String.format(java.util.Locale.ROOT, "%02d", seconds % 60L));
    }

    public record ExpeditionRewards(List<ItemStack> structure, List<ItemStack> ores, List<ItemStack> mobs) {
        public ExpeditionRewards {
            structure = List.copyOf(structure);
            ores = List.copyOf(ores);
            mobs = List.copyOf(mobs);
        }

        public int totalCount() {
            return this.structure.size() + this.ores.size() + this.mobs.size();
        }
    }

    static Set<ResourceLocation> biomeIds(ServerLevel level) {
        Set<ResourceLocation> ids = new LinkedHashSet<>();
        for (Holder<Biome> holder : level.getChunkSource().getGenerator().getBiomeSource().possibleBiomes()) {
            holder.unwrapKey().map(ResourceKey::location).ifPresent(ids::add);
        }
        return ids;
    }

    static boolean biomeExists(ServerLevel level, ResourceLocation biome) {
        for (Holder<Biome> holder : level.getChunkSource().getGenerator().getBiomeSource().possibleBiomes()) {
            if (holder.unwrapKey().map(ResourceKey::location).map(biome::equals).orElse(false)) {
                return true;
            }
        }
        return false;
    }

    private static CompoundTag makeSnapshot(LivingEntity traveler) {
        CompoundTag tag = new CompoundTag();
        traveler.saveWithoutId(tag);
        ResourceLocation typeId = BuiltInRegistries.ENTITY_TYPE.getKey(traveler.getType());
        if (typeId != null) {
            tag.putString("id", typeId.toString());
        }
        tag.remove("UUID");
        tag.remove("Dimension");
        tag.remove("Motion");
        tag.remove("FallDistance");
        return tag;
    }

    private static String awayName(ExpeditionData data, UUID uuid) {
        for (ExpeditionData.ActiveExpedition active : data.allExpeditions()) {
            int index = active.memberUuids().indexOf(uuid);
            if (index < 0 || index >= active.snapshots().size()) {
                continue;
            }
            ResourceLocation typeId = ResourceLocation.tryParse(active.snapshots().get(index).getString("id"));
            if (typeId != null) {
                EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(typeId);
                if (type != null) {
                    return type.getDescription().getString();
                }
            }
        }
        return uuid.toString().substring(0, 8);
    }

    private static String statusKey(TravelerStatus status) {
        return switch (status) {
            case UNSUPPORTED -> "notTraveler";
            case NOT_OWNER -> "notYours";
            case LIMITED_LIFE -> "limited";
            case TOO_FAR -> "tooFar";
            case BUSY -> "busy";
            case IN_EXPEDITION -> "expedition";
            case DEAD -> "gone";
            default -> "gone";
        };
    }

    private static Component fail(String key, Object... args) {
        return Component.translatable("screen.goetyominous.expedition.fail." + key, args);
    }
}
