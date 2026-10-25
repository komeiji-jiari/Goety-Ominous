package com.qiuyue.goetyominous.common.explore;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.explore.network.ExpeditionNetwork;
import com.qiuyue.goetyominous.common.items.explore.ExpeditionCompassItem;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class ExpeditionViewSessions {
    private static final int LEASE_TICKS = 120;
    private static final Map<UUID, View> VIEWS = new HashMap<>();

    private ExpeditionViewSessions() {
    }

    public static void open(ServerPlayer player, InteractionHand hand) {
        if (!(player.getItemInHand(hand).getItem() instanceof ExpeditionCompassItem)) {
            return;
        }
        VIEWS.put(player.getUUID(), new View(player.level().dimension(), hand,
                player.getServer().getTickCount() + LEASE_TICKS));
        try {
            ExpeditionNetwork.sendOpen(player, ExpeditionManager.snapshot(player, player.level().dimension(),
                    hand, true, Component.empty()));
        } catch (RuntimeException broken) {
            GoetyOminous.LOGGER.warn("[goetyominous] Failed to build initial expedition snapshot: {}", broken.toString());
        }
    }

    public static void select(ServerPlayer player, ResourceKey<Level> dimension) {
        View view = VIEWS.get(player.getUUID());
        if (view == null || player.getServer() == null || player.getServer().getLevel(dimension) == null) {
            return;
        }
        if (!ExpeditionData.get(player.getServer()).isDimensionVisited(player.getUUID(), dimension)) {
            return;
        }
        VIEWS.put(player.getUUID(), new View(dimension, view.hand(),
                player.getServer().getTickCount() + LEASE_TICKS));
    }

    public static void heartbeat(ServerPlayer player, boolean active) {
        if (!active) {
            VIEWS.remove(player.getUUID());
            return;
        }
        View view = VIEWS.get(player.getUUID());
        if (view != null && player.getServer() != null) {
            VIEWS.put(player.getUUID(), new View(view.dimension(), view.hand(),
                    player.getServer().getTickCount() + LEASE_TICKS));
        }
    }

    public static void refresh(ServerPlayer player, Component feedback) {
        View view = VIEWS.get(player.getUUID());
        if (view == null) {
            return;
        }
        try {
            ExpeditionNetwork.sendOpen(player, ExpeditionManager.snapshot(player, view.dimension(), view.hand(),
                    false, feedback == null ? Component.empty() : feedback));
        } catch (RuntimeException broken) {
            GoetyOminous.LOGGER.warn("[goetyominous] Failed to refresh expedition snapshot: {}", broken.toString());
        }
    }

    public static void tick(MinecraftServer server) {
        if (server.getTickCount() % ExpeditionManager.CHECK_INTERVAL != 0) {
            return;
        }
        int now = server.getTickCount();
        Iterator<Map.Entry<UUID, View>> iterator = VIEWS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, View> entry = iterator.next();
            ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());
            if (player == null || entry.getValue().expiresAt() < now) {
                iterator.remove();
            }
        }
        for (Map.Entry<UUID, View> entry : VIEWS.entrySet()) {
            ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());
            if (player == null) {
                continue;
            }
            View view = entry.getValue();
            try {
                ExpeditionNetwork.sendProgress(player,
                        ExpeditionManager.travelerEntries(player, view.hand(), view.dimension()),
                        ExpeditionManager.expeditionInfos(player));
            } catch (RuntimeException broken) {
                GoetyOminous.LOGGER.warn("[goetyominous] Failed to push expedition progress: {}", broken.toString());
            }
        }
    }

    public static Component gather(ServerPlayer player) {
        View view = VIEWS.get(player.getUUID());
        if (view == null) {
            return Component.translatable("screen.goetyominous.expedition.fail.noView");
        }
        ItemStack held = player.getItemInHand(view.hand());
        if (!(held.getItem() instanceof ExpeditionCompassItem compass)) {
            return Component.translatable("screen.goetyominous.expedition.fail.compass");
        }
        compass.reconcile(held, player);
        MinecraftServer server = player.getServer();
        if (server == null || !(player.level() instanceof ServerLevel level)) {
            return Component.translatable("screen.goetyominous.expedition.fail.level");
        }
        ExpeditionData data = ExpeditionData.get(server);
        int count = 0;
        int index = 0;
        for (UUID id : ExpeditionCompassItem.getBound(held)) {
            Entity entity = ExpeditionManager.findLoaded(server, data.resolveRemap(id));
            if (entity == null) {
                continue;
            }
            TravelerStatus status = compass.evaluate(player, entity);
            if (status != TravelerStatus.READY && status != TravelerStatus.TOO_FAR
                    && status != TravelerStatus.LIMITED_LIFE && status != TravelerStatus.PENDING_LOOT) {
                continue;
            }
            double angle = (double) index * 2.399963229728653D;
            double targetX = player.getX() + Math.cos(angle) * 1.5D;
            double targetZ = player.getZ() + Math.sin(angle) * 1.5D;
            entity.teleportTo(level, targetX, player.getY(), targetZ, Set.of(), entity.getYRot(), entity.getXRot());
            if (entity instanceof Mob mob) {
                mob.getNavigation().stop();
                mob.setTarget(null);
            }
            entity.setDeltaMovement(Vec3.ZERO);
            entity.fallDistance = 0.0F;
            index++;
            count++;
        }
        return Component.translatable("message.goetyominous.expedition.gathered", count);
    }

    public static void close(UUID playerId) {
        VIEWS.remove(playerId);
    }

    public static void clear() {
        VIEWS.clear();
    }

    private record View(ResourceKey<Level> dimension, InteractionHand hand, int expiresAt) {
    }
}
