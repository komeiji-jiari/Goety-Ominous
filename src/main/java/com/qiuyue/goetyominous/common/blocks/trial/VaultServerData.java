package com.qiuyue.goetyominous.common.blocks.trial;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class VaultServerData {
    static final String TAG_NAME = "server_data";
    public static final Codec<Set<UUID>> UUID_SET =
            Codec.list(UUIDUtil.CODEC).xmap(Sets::newLinkedHashSet, Lists::newArrayList);
    static final Codec<VaultServerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            lenientOptionalFieldOf(UUID_SET, "rewarded_players", Set.of())
                    .forGetter(data -> data.rewardedPlayers),
            lenientOptionalFieldOf(Codec.LONG, "state_updating_resumes_at", 0L)
                    .forGetter(data -> data.stateUpdatingResumesAt),
            lenientOptionalFieldOf(ItemStack.CODEC.listOf(), "items_to_eject", List.of())
                    .forGetter(data -> data.itemsToEject),
            lenientOptionalFieldOf(Codec.INT, "total_ejections_needed", 0)
                    .forGetter(data -> data.totalEjectionsNeeded)
    ).apply(instance, VaultServerData::new));

    public static <T> MapCodec<T> lenientOptionalFieldOf(Codec<T> codec, String name, T defaultValue) {
        MapCodec<T> delegate = codec.optionalFieldOf(name, defaultValue);
        return delegate.mapResult(new MapCodec.ResultFunction<T>() {
            @Override
            public <V> DataResult<T> apply(DynamicOps<V> ops, MapLike<V> input, DataResult<T> result) {
                return result.result().map(DataResult::success)
                        .orElseGet(() -> DataResult.success(defaultValue));
            }

            @Override
            public <V> RecordBuilder<V> coApply(DynamicOps<V> ops, T input, RecordBuilder<V> prefix) {
                return prefix;
            }
        });
    }

    public static <T> MapCodec<Optional<T>> lenientOptionalFieldOf(Codec<T> codec, String name) {
        MapCodec<Optional<T>> delegate = codec.optionalFieldOf(name);
        return delegate.mapResult(new MapCodec.ResultFunction<Optional<T>>() {
            @Override
            public <V> DataResult<Optional<T>> apply(DynamicOps<V> ops, MapLike<V> input, DataResult<Optional<T>> result) {
                return result.result().map(DataResult::success)
                        .orElseGet(() -> DataResult.success(Optional.empty()));
            }

            @Override
            public <V> RecordBuilder<V> coApply(DynamicOps<V> ops, Optional<T> input, RecordBuilder<V> prefix) {
                return prefix;
            }
        });
    }

    private static final int MAX_REWARD_PLAYERS = 128;
    private final Set<UUID> rewardedPlayers = new ObjectLinkedOpenHashSet<>();
    private long stateUpdatingResumesAt;
    private final List<ItemStack> itemsToEject = new ObjectArrayList<>();
    private long lastInsertFailTimestamp;
    private int totalEjectionsNeeded;
    boolean isDirty;

    VaultServerData(Set<UUID> rewardedPlayers, long stateUpdatingResumesAt,
                    List<ItemStack> itemsToEject, int totalEjectionsNeeded) {
        this.rewardedPlayers.addAll(rewardedPlayers);
        this.stateUpdatingResumesAt = stateUpdatingResumesAt;
        this.itemsToEject.addAll(itemsToEject);
        this.totalEjectionsNeeded = totalEjectionsNeeded;
    }

    VaultServerData() {
    }

    void setLastInsertFailTimestamp(long lastInsertFailTimestamp) {
        this.lastInsertFailTimestamp = lastInsertFailTimestamp;
    }

    long getLastInsertFailTimestamp() {
        return this.lastInsertFailTimestamp;
    }

    Set<UUID> getRewardedPlayers() {
        return this.rewardedPlayers;
    }

    boolean hasRewardedPlayer(Player player) {
        return this.rewardedPlayers.contains(player.getUUID());
    }

    public void addToRewardedPlayers(Player player) {
        this.rewardedPlayers.add(player.getUUID());
        if (this.rewardedPlayers.size() > MAX_REWARD_PLAYERS) {
            var iterator = this.rewardedPlayers.iterator();
            if (iterator.hasNext()) {
                iterator.next();
                iterator.remove();
            }
        }
        this.markChanged();
    }

    long stateUpdatingResumesAt() {
        return this.stateUpdatingResumesAt;
    }

    void pauseStateUpdatingUntil(long time) {
        this.stateUpdatingResumesAt = time;
        this.markChanged();
    }

    List<ItemStack> getItemsToEject() {
        return this.itemsToEject;
    }

    void markEjectionFinished() {
        this.totalEjectionsNeeded = 0;
        this.markChanged();
    }

    void setItemsToEject(List<ItemStack> itemsToEject) {
        this.itemsToEject.clear();
        this.itemsToEject.addAll(itemsToEject);
        this.totalEjectionsNeeded = this.itemsToEject.size();
        this.markChanged();
    }

    ItemStack getNextItemToEject() {
        return this.itemsToEject.isEmpty()
                ? ItemStack.EMPTY
                : Objects.requireNonNullElse(this.itemsToEject.get(this.itemsToEject.size() - 1), ItemStack.EMPTY);
    }

    ItemStack popNextItemToEject() {
        if (this.itemsToEject.isEmpty()) {
            return ItemStack.EMPTY;
        }
        this.markChanged();
        return Objects.requireNonNullElse(this.itemsToEject.remove(this.itemsToEject.size() - 1), ItemStack.EMPTY);
    }

    void set(VaultServerData other) {
        this.stateUpdatingResumesAt = other.stateUpdatingResumesAt();
        this.itemsToEject.clear();
        this.itemsToEject.addAll(other.itemsToEject);
        this.rewardedPlayers.clear();
        this.rewardedPlayers.addAll(other.rewardedPlayers);
    }

    private void markChanged() {
        this.isDirty = true;
    }

    public float ejectionProgress() {
        return this.totalEjectionsNeeded == 1
                ? 1.0F
                : 1.0F - Mth.inverseLerp(this.getItemsToEject().size(), 1.0F, this.totalEjectionsNeeded);
    }
}
