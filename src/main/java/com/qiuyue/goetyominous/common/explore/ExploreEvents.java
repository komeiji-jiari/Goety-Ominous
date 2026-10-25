package com.qiuyue.goetyominous.common.explore;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class ExploreEvents {
    private ExploreEvents() {
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.register(ExploreEvents.class);
    }

    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(new StructureLootCache());
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        ExpeditionViewSessions.clear();
        StructureIndex.invalidate();
        DimensionLootPool.invalidate();
        LootResolver.invalidateOreIndex();
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.getServer() == null) {
            return;
        }
        ExpeditionManager.tick(event.getServer());
        RuinVisitTracker.tick(event.getServer());
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            RuinVisitTracker.onPlayerLogin(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            ExpeditionViewSessions.close(serverPlayer.getUUID());
            RuinVisitTracker.onPlayerLogout(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            ResourceKey<Level> to = event.getTo();
            RuinVisitTracker.onPlayerChangeDimension(serverPlayer, to);
        }
    }
}
