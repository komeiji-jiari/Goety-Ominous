package com.qiuyue.goetyominous.common.blocks.trial;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

public class VaultSharedData {
    static final String TAG_NAME = "shared_data";
    static final Codec<VaultSharedData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            VaultServerData.lenientOptionalFieldOf(ItemStack.CODEC, "display_item", ItemStack.EMPTY)
                    .forGetter(data -> data.displayItem),
            VaultServerData.lenientOptionalFieldOf(VaultServerData.UUID_SET, "connected_players", Set.of())
                    .forGetter(data -> data.connectedPlayers),
            VaultServerData.lenientOptionalFieldOf(Codec.DOUBLE, "connected_particles_range",
                            VaultConfig.defaultFor(false).deactivationRange())
                    .forGetter(data -> data.connectedParticlesRange)
    ).apply(instance, VaultSharedData::new));

    private ItemStack displayItem = ItemStack.EMPTY;
    private Set<UUID> connectedPlayers = new ObjectLinkedOpenHashSet<>();
    private double connectedParticlesRange = VaultConfig.defaultFor(false).deactivationRange();
    boolean isDirty;

    VaultSharedData(ItemStack displayItem, Set<UUID> connectedPlayers, double connectedParticlesRange) {
        this.displayItem = displayItem;
        this.connectedPlayers.addAll(connectedPlayers);
        this.connectedParticlesRange = connectedParticlesRange;
    }

    VaultSharedData() {
    }

    public ItemStack getDisplayItem() {
        return this.displayItem;
    }

    public boolean hasDisplayItem() {
        return !this.displayItem.isEmpty();
    }

    public void setDisplayItem(ItemStack displayItem) {
        if (!ItemStack.matches(this.displayItem, displayItem)) {
            this.displayItem = displayItem.copy();
            this.markDirty();
        }
    }

    boolean hasConnectedPlayers() {
        return !this.connectedPlayers.isEmpty();
    }

    Set<UUID> getConnectedPlayers() {
        return this.connectedPlayers;
    }

    double connectedParticlesRange() {
        return this.connectedParticlesRange;
    }

    void updateConnectedPlayersWithinRange(ServerLevel level, BlockPos pos, VaultServerData serverData,
                                           VaultConfig config, double deactivationRange) {
        Set<UUID> found = config.playerDetector()
                .detect(level, config.entitySelector(), pos, deactivationRange, false)
                .stream()
                .filter(uuid -> !serverData.getRewardedPlayers().contains(uuid))
                .collect(Collectors.toSet());
        if (!this.connectedPlayers.equals(found)) {
            this.connectedPlayers = found;
            this.markDirty();
        }
    }

    private void markDirty() {
        this.isDirty = true;
    }

    void set(VaultSharedData other) {
        this.displayItem = other.displayItem;
        this.connectedPlayers = other.connectedPlayers;
        this.connectedParticlesRange = other.connectedParticlesRange;
    }
}
