package com.qiuyue.goetyominous.common.explore;

import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class RuinVisitTracker {
    private static final int SCAN_INTERVAL = 40;
    private static final double MIN_MOVE_SQ = 64.0D;
    private static final double BOX_INFLATE = 4.0D;
    private static final Map<UUID, BlockPos> LAST_SCAN = new HashMap<>();

    private RuinVisitTracker() {
    }

    public static void onPlayerChangeDimension(ServerPlayer player, ResourceKey<Level> to) {
        if (player == null || to == null) {
            return;
        }
        ExpeditionData data = ExpeditionData.get(player.getServer());
        if (data.markDimensionVisited(player.getUUID(), to)) {
            GoetyOminous.LOGGER.debug("[goetyominous] Player {} first reached dimension {}.",
                    player.getGameProfile().getName(), to.location());
        }
        LAST_SCAN.remove(player.getUUID());
    }

    public static void onPlayerLogin(ServerPlayer player) {
        if (player == null) {
            return;
        }
        ExpeditionData data = ExpeditionData.get(player.getServer());
        data.markDimensionVisited(player.getUUID(), player.level().dimension());
        LAST_SCAN.remove(player.getUUID());
    }

    public static void onPlayerLogout(ServerPlayer player) {
        if (player != null) {
            LAST_SCAN.remove(player.getUUID());
        }
    }

    public static void tick(MinecraftServer server) {
        if (server.getTickCount() % SCAN_INTERVAL != 0) {
            return;
        }
        for (ServerLevel level : server.getAllLevels()) {
            if (level.players().isEmpty()) {
                continue;
            }
            for (ServerPlayer player : level.players()) {
                scan(level, player);
            }
        }
    }

    private static void scan(ServerLevel level, ServerPlayer player) {
        BlockPos pos = player.blockPosition();
        BlockPos last = LAST_SCAN.get(player.getUUID());
        if (last != null && last.distSqr(pos) < MIN_MOVE_SQ) {
            return;
        }
        LAST_SCAN.put(player.getUUID(), pos);
        Map<Structure, ?> references;
        try {
            references = level.structureManager().getAllStructuresAt(pos);
        } catch (RuntimeException boundaryCase) {
            return;
        }
        if (references == null || references.isEmpty()) {
            return;
        }
        ExpeditionData data = ExpeditionData.get(level.getServer());
        for (Structure structure : references.keySet()) {
            StructureStart start = level.structureManager().getStructureAt(pos, structure);
            if (start == null || !start.isValid()) {
                continue;
            }
            if (!start.getBoundingBox().inflatedBy((int) BOX_INFLATE).isInside(pos)) {
                continue;
            }
            ResourceLocation id = level.registryAccess().registryOrThrow(Registries.STRUCTURE).getKey(structure);
            if (id == null) {
                continue;
            }
            if (data.markRuinVisited(player.getUUID(), level.dimension(), id)) {
                GoetyOminous.LOGGER.debug("[goetyominous] Player {} entered ruin {}.",
                        player.getGameProfile().getName(), id);
            }
        }
    }
}
