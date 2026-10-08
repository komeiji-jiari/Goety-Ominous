package com.qiuyue.goetyominous.common.blocks.trial;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.qiuyue.goetyominous.common.init.ModEffects;
import com.qiuyue.goetyominous.config.TrialSpawnerConfig;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class TrialSpawnerData {
    public static final String TAG_SPAWN_DATA = "spawn_data";
    private static final String TAG_NEXT_MOB_SPAWNS_AT = "next_mob_spawns_at";
    private static final int DELAY_BETWEEN_PLAYER_SCANS = 20;
    private static final int TRIAL_OMEN_PER_BAD_OMEN_LEVEL = 18000;

    public static final MapCodec<TrialSpawnerData> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            VaultServerData.lenientOptionalFieldOf(VaultServerData.UUID_SET, "registered_players", Sets.newHashSet())
                    .forGetter(data -> data.detectedPlayers),
            VaultServerData.lenientOptionalFieldOf(VaultServerData.UUID_SET, "current_mobs", Sets.newHashSet())
                    .forGetter(data -> data.currentMobs),
            VaultServerData.lenientOptionalFieldOf(Codec.LONG, "cooldown_ends_at", 0L)
                    .forGetter(data -> data.cooldownEndsAt),
            VaultServerData.lenientOptionalFieldOf(Codec.LONG, TAG_NEXT_MOB_SPAWNS_AT, 0L)
                    .forGetter(data -> data.nextMobSpawnsAt),
            VaultServerData.lenientOptionalFieldOf(Codec.intRange(0, Integer.MAX_VALUE), "total_mobs_spawned", 0)
                    .forGetter(data -> data.totalMobsSpawned),
            VaultServerData.lenientOptionalFieldOf(SpawnDataWithEquipment.CODEC, TAG_SPAWN_DATA)
                    .forGetter(data -> data.nextSpawnData),
            VaultServerData.lenientOptionalFieldOf(ResourceLocation.CODEC, "ejecting_loot_table")
                    .forGetter(data -> data.ejectingLootTable)
    ).apply(instance, TrialSpawnerData::new));

    protected final Set<UUID> detectedPlayers = new HashSet<>();
    protected final Set<UUID> currentMobs = new HashSet<>();
    protected long cooldownEndsAt;
    protected long nextMobSpawnsAt;
    protected int totalMobsSpawned;
    protected Optional<SpawnDataWithEquipment> nextSpawnData = Optional.empty();
    protected Optional<ResourceLocation> ejectingLootTable = Optional.empty();
    @Nullable
    protected Entity displayEntity;
    @Nullable
    private SimpleWeightedRandomList<ItemStack> dispensing;
    protected double spin;
    protected double oSpin;

    public TrialSpawnerData() {
        this(Collections.emptySet(), Collections.emptySet(), 0L, 0L, 0, Optional.empty(), Optional.empty());
    }

    public TrialSpawnerData(Set<UUID> detectedPlayers, Set<UUID> currentMobs, long cooldownEndsAt, long nextMobSpawnsAt,
                            int totalMobsSpawned, Optional<SpawnDataWithEquipment> nextSpawnData,
                            Optional<ResourceLocation> ejectingLootTable) {
        this.detectedPlayers.addAll(detectedPlayers);
        this.currentMobs.addAll(currentMobs);
        this.cooldownEndsAt = cooldownEndsAt;
        this.nextMobSpawnsAt = nextMobSpawnsAt;
        this.totalMobsSpawned = totalMobsSpawned;
        this.nextSpawnData = nextSpawnData;
        this.ejectingLootTable = ejectingLootTable;
    }

    public void reset() {
        this.detectedPlayers.clear();
        this.totalMobsSpawned = 0;
        this.nextMobSpawnsAt = 0L;
        this.cooldownEndsAt = 0L;
        this.currentMobs.clear();
        this.nextSpawnData = Optional.empty();
    }

    public void resetKeepingSpawnData() {
        this.detectedPlayers.clear();
        this.totalMobsSpawned = 0;
        this.nextMobSpawnsAt = 0L;
        this.cooldownEndsAt = 0L;
        this.currentMobs.clear();
    }

    public boolean hasMobToSpawn(TrialSpawner spawner, RandomSource random) {
        boolean flag = this.getOrCreateNextSpawnData(spawner, random).getEntityToSpawn().contains("id", 8);
        return flag || !spawner.getConfig().spawnPotentialsDefinition().isEmpty();
    }

    public boolean hasFinishedSpawningAllMobs(TrialSpawnerConfig config, int players) {
        return this.totalMobsSpawned >= config.calculateTargetTotalMobs(players);
    }

    public boolean haveAllCurrentMobsDied() {
        return this.currentMobs.isEmpty();
    }

    public boolean isReadyToSpawnNextMob(ServerLevel level, TrialSpawnerConfig config, int players) {
        return level.getGameTime() >= this.nextMobSpawnsAt
                && this.currentMobs.size() < config.calculateTargetSimultaneousMobs(players);
    }

    public int countAdditionalPlayers(BlockPos pos) {
        if (this.detectedPlayers.isEmpty()) {
            Util.logAndPauseIfInIde("Trial Spawner at " + pos + " has no detected players");
        }
        return Math.max(0, this.detectedPlayers.size() - 1);
    }

    public void tryDetectPlayers(ServerLevel level, BlockPos pos, TrialSpawner spawner) {
        if ((pos.asLong() + level.getGameTime()) % 20L != 0L) {
            return;
        }
        if (spawner.getState() == TrialSpawnerState.COOLDOWN && spawner.isOminous()) {
            return;
        }
        List<UUID> players = spawner.getPlayerDetector()
                .detect(level, spawner.getEntitySelector(), pos, spawner.getRequiredPlayerRange(), true);
        boolean foundOminous;
        if (spawner.isOminous() || players.isEmpty()) {
            foundOminous = false;
        } else {
            Optional<Pair<Player, MobEffect>> optional = findPlayerWithOminousEffect(level, players);
            optional.ifPresent(pair -> {
                Player player = pair.getFirst();
                if (pair.getSecond() == MobEffects.BAD_OMEN) {
                    this.transformBadOmenIntoTrialOmen(player);
                }
                level.levelEvent(3020, BlockPos.containing(player.getEyePosition()), 0);
                spawner.applyOminous(level, pos);
            });
            foundOminous = optional.isPresent();
        }
        if (spawner.getState() == TrialSpawnerState.COOLDOWN && !foundOminous) {
            return;
        }
        boolean firstScan = spawner.getData().detectedPlayers.isEmpty();
        List<UUID> detected = firstScan
                ? players
                : spawner.getPlayerDetector().detect(level, spawner.getEntitySelector(), pos,
                spawner.getRequiredPlayerRange(), false);
        if (this.detectedPlayers.addAll(detected)) {
            this.nextMobSpawnsAt = Math.max(level.getGameTime() + 40L, this.nextMobSpawnsAt);
            if (!foundOminous) {
                int eventId = spawner.isOminous() ? 3019 : 3013;
                level.levelEvent(eventId, pos, this.detectedPlayers.size());
            }
        }
    }

    public Optional<Pair<Player, MobEffect>> findPlayerWithOminousEffect(ServerLevel level, List<UUID> players) {
        Player badOmenPlayer = null;
        for (UUID uuid : players) {
            Player player = level.getPlayerByUUID(uuid);
            if (player == null) {
                continue;
            }
            if (player.hasEffect(ModEffects.TRIAL_OMEN.get())) {
                return Optional.of(Pair.of(player, ModEffects.TRIAL_OMEN.get()));
            }
            if (!player.hasEffect(MobEffects.BAD_OMEN)) {
                continue;
            }
            badOmenPlayer = player;
        }
        return Optional.ofNullable(badOmenPlayer).map(player -> Pair.of(player, MobEffects.BAD_OMEN));
    }

    public void resetAfterBecomingOminous(TrialSpawner spawner, ServerLevel level) {
        Stream<UUID> stream = this.currentMobs.stream();
        this.currentMobs.clear();
        stream.map(level::getEntity).forEach(entity -> {
            if (entity == null) {
                return;
            }
            level.levelEvent(3012, entity.blockPosition(), TrialSpawner.FlameParticle.NORMAL.getId());
            entity.discard();
        });
        if (!spawner.getOminousConfig().spawnPotentialsDefinition().isEmpty()) {
            this.nextSpawnData = Optional.empty();
        }
        this.totalMobsSpawned = 0;
        this.currentMobs.clear();
        this.nextMobSpawnsAt = level.getGameTime() + (long) spawner.getOminousConfig().ticksBetweenSpawn();
        spawner.markUpdated();
        this.cooldownEndsAt = level.getGameTime() + spawner.getOminousConfig().ticksBetweenItemSpawners();
    }

    public void transformBadOmenIntoTrialOmen(Player player) {
        MobEffectInstance instance = player.getEffect(MobEffects.BAD_OMEN);
        if (instance == null) {
            return;
        }
        int amplifier = instance.getAmplifier() + 1;
        int duration = TRIAL_OMEN_PER_BAD_OMEN_LEVEL * amplifier;
        player.removeEffect(MobEffects.BAD_OMEN);
        player.addEffect(new MobEffectInstance(ModEffects.TRIAL_OMEN.get(), duration, 0));
    }

    public boolean isReadyToOpenShutter(ServerLevel level, float delay, int targetCooldownLength) {
        long time = this.cooldownEndsAt - (long) targetCooldownLength;
        return (float) level.getGameTime() >= (float) time + delay;
    }

    public boolean isReadyToEjectItems(ServerLevel level, float delay, int targetCooldownLength) {
        long time = this.cooldownEndsAt - (long) targetCooldownLength;
        return (float) (level.getGameTime() - time) % delay == 0.0F;
    }

    public boolean isCooldownFinished(ServerLevel level) {
        return level.getGameTime() >= this.cooldownEndsAt;
    }

    public void setEntityId(TrialSpawner spawner, RandomSource random, EntityType<?> type) {
        this.getOrCreateNextSpawnData(spawner, random).getEntityToSpawn()
                .putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(type).toString());
    }

    public SpawnData getOrCreateNextSpawnData(TrialSpawner spawner, RandomSource random) {
        if (this.nextSpawnData.isPresent()) {
            return this.nextSpawnData.get().spawnData();
        }
        SimpleWeightedRandomList<SpawnDataWithEquipment> potentials = spawner.getConfig().spawnPotentialsDefinition();
        Optional<SpawnDataWithEquipment> optional = potentials.isEmpty()
                ? this.nextSpawnData
                : potentials.getRandom(random).map(WeightedEntry.Wrapper::getData);
        this.nextSpawnData = Optional.of(optional.orElseGet(() -> SpawnDataWithEquipment.of(new SpawnData())));
        spawner.markUpdated();
        return this.nextSpawnData.get().spawnData();
    }

    public Optional<EquipmentTable> getNextSpawnEquipment() {
        return this.nextSpawnData.flatMap(SpawnDataWithEquipment::equipment);
    }

    @Nullable
    public Entity getOrCreateDisplayEntity(TrialSpawner spawner, Level level, TrialSpawnerState state) {
        if (!spawner.canSpawnInLevel(level) || !state.hasSpinningMob()) {
            return null;
        }
        if (this.displayEntity == null) {
            CompoundTag tag = this.getOrCreateNextSpawnData(spawner, level.getRandom()).getEntityToSpawn();
            if (tag.contains("id", 8)) {
                this.displayEntity = EntityType.loadEntityRecursive(tag, level, Function.identity());
            }
        }
        return this.displayEntity;
    }

    public CompoundTag getUpdateTag(TrialSpawnerState state) {
        CompoundTag tag = new CompoundTag();
        if (state == TrialSpawnerState.ACTIVE) {
            tag.putLong(TAG_NEXT_MOB_SPAWNS_AT, this.nextMobSpawnsAt);
        }
        this.nextSpawnData.ifPresent(data -> tag.put(TAG_SPAWN_DATA,
                SpawnDataWithEquipment.CODEC.encodeStart(NbtOps.INSTANCE, data)
                        .result().orElseThrow(() -> new IllegalStateException("Invalid SpawnData"))));
        return tag;
    }

    public double getSpin() {
        return this.spin;
    }

    public double getOSpin() {
        return this.oSpin;
    }

    SimpleWeightedRandomList<ItemStack> getDispensingItems(ServerLevel level, TrialSpawnerConfig config, BlockPos pos) {
        if (this.dispensing != null) {
            return this.dispensing;
        }
        LootTable table = level.getServer().getLootData().getLootTable(config.itemsToDropWhenOminous());
        ObjectArrayList<ItemStack> items = table.getRandomItems(
                new LootParams.Builder(level).create(LootContextParamSets.EMPTY),
                lowResolutionPosition(level, pos));
        if (items.isEmpty()) {
            return SimpleWeightedRandomList.empty();
        }
        SimpleWeightedRandomList.Builder<ItemStack> builder = SimpleWeightedRandomList.builder();
        for (ItemStack stack : items) {
            builder.add(stack.copyWithCount(1), stack.getCount());
        }
        this.dispensing = builder.build();
        return this.dispensing;
    }

    private static long lowResolutionPosition(ServerLevel level, BlockPos pos) {
        BlockPos lowPos = new BlockPos(
                Mth.floor((float) pos.getX() / 30.0F),
                Mth.floor((float) pos.getY() / 20.0F),
                Mth.floor((float) pos.getZ() / 30.0F));
        return level.getSeed() + lowPos.asLong();
    }
}
