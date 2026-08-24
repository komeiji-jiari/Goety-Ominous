/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.WorldGenRegion
 *  net.minecraft.world.entity.MobSpawnType
 *  net.minecraft.world.level.ServerLevelAccessor
 *  net.minecraftforge.event.entity.living.MobSpawnEvent$FinalizeSpawn
 *  net.minecraftforge.eventbus.api.EventPriority
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 */
package com.vivideru.masteryofmagic.events;

import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class MidasWorldSpawnSuppression {
    private static final Map<ServerLevel, Long> ACTIVE_LEVEL_TICKS = new WeakHashMap<ServerLevel, Long>();

    private MidasWorldSpawnSuppression() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void markActive(ServerLevel level) {
        Map<ServerLevel, Long> map = ACTIVE_LEVEL_TICKS;
        synchronized (map) {
            ACTIVE_LEVEL_TICKS.put(level, level.m_46467_());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static boolean isActive(ServerLevel level) {
        Map<ServerLevel, Long> map = ACTIVE_LEVEL_TICKS;
        synchronized (map) {
            Long lastActiveTick = ACTIVE_LEVEL_TICKS.get(level);
            return lastActiveTick != null && level.m_46467_() - lastActiveTick <= 2L;
        }
    }

    private static boolean isNaturalSpawn(MobSpawnType spawnType) {
        return spawnType == MobSpawnType.NATURAL || spawnType == MobSpawnType.CHUNK_GENERATION || spawnType == MobSpawnType.PATROL;
    }

    private static ServerLevel resolveLevel(ServerLevelAccessor accessor) {
        if (accessor instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)accessor;
            return serverLevel;
        }
        if (accessor instanceof WorldGenRegion) {
            WorldGenRegion worldGenRegion = (WorldGenRegion)accessor;
            return worldGenRegion.m_6018_();
        }
        return null;
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void onFinalizeSpawn(MobSpawnEvent.FinalizeSpawn event) {
        ServerLevel serverLevel = MidasWorldSpawnSuppression.resolveLevel(event.getLevel());
        if (serverLevel != null && MidasWorldSpawnSuppression.isNaturalSpawn(event.getSpawnType()) && MidasWorldSpawnSuppression.isActive(serverLevel)) {
            event.setSpawnCancelled(true);
        }
    }
}

