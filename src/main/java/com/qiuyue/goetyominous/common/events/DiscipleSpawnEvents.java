package com.qiuyue.goetyominous.common.events;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.neutral.AbstractObsidianMonolith;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.qiuyue.goetyominous.common.entities.hostile.cultists.Disciple;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(modid = "goetyominous")
public class DiscipleSpawnEvents {

    private static final Map<String, Integer> pendingDiscipleSpawns = new LinkedHashMap<>();

    @SubscribeEvent
    public static void onMonolithDeath(LivingDeathEvent event) {
        Entity entity = event.getEntity();

        if (!(entity instanceof AbstractObsidianMonolith monolith)) {
            return;
        }

        if (monolith.getTrueOwner() != null) {
            return;
        }

        if (!(monolith.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        if (serverLevel.random.nextInt(100) < 20) {
            BlockPos pos = monolith.blockPosition();
            String key = serverLevel.dimension().location().toString() + ";" + pos.getX() + "," + pos.getY() + "," + pos.getZ();
            pendingDiscipleSpawns.put(key, 60);
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        MinecraftServer server = event.getServer();
        if (server == null) {
            return;
        }

        Iterator<Map.Entry<String, Integer>> iterator = pendingDiscipleSpawns.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();
            String key = entry.getKey();
            int remainingTicks = entry.getValue();

            String[] parts = key.split(";");
            if (parts.length != 2) {
                iterator.remove();
                continue;
            }

            String dimensionId = parts[0];
            String[] coords = parts[1].split(",");
            if (coords.length != 3) {
                iterator.remove();
                continue;
            }

            try {
                BlockPos pos = new BlockPos(
                        Integer.parseInt(coords[0]),
                        Integer.parseInt(coords[1]),
                        Integer.parseInt(coords[2])
                );

                ServerLevel serverLevel = getLevelByDimensionId(server, dimensionId);
                if (serverLevel == null) {
                    iterator.remove();
                    continue;
                }

                if (remainingTicks <= 0) {
                    spawnDiscipleAtPosition(serverLevel, pos);
                    iterator.remove();
                } else {
                    entry.setValue(remainingTicks - 1);
                }

            } catch (Exception e) {
                iterator.remove();
            }
        }
    }

    private static void spawnDiscipleAtPosition(ServerLevel serverLevel, BlockPos obeliskPos) {
        Disciple disciple = ModEntityTypes.DISCIPLE.get().create(serverLevel);
        if (disciple == null) {
            return;
        }

        BlockPos spawnPos = BlockFinder.SummonPosition(disciple, obeliskPos);

        if (spawnPos == null) {
            return;
        }

        disciple.moveTo(
                (double) spawnPos.getX() + 0.5,
                spawnPos.getY(),
                (double) spawnPos.getZ() + 0.5,
                serverLevel.random.nextFloat() * 360.0F,
                0.0F
        );

        disciple.finalizeSpawn(
                serverLevel,
                serverLevel.getCurrentDifficultyAt(spawnPos),
                MobSpawnType.MOB_SUMMONED,
                null,
                null
        );

        disciple.setPersistenceRequired();

        ServerParticleUtil.circularParticles(
                serverLevel,
                ModParticleTypes.RISING_ENCHANT.get(),
                spawnPos.getX() + 0.5,
                spawnPos.getY(),
                spawnPos.getZ() + 0.5,
                1.0,
                0.0,
                0.0,
                2.0F
        );
        ServerParticleUtil.summonUndeadParticles(serverLevel, disciple,
                new com.Polarice3.Goety.utils.ColorUtil(0xffa300), 0xffa300, 0xffff6e);
        serverLevel.addFreshEntity(disciple);
    }

    private static ServerLevel getLevelByDimensionId(MinecraftServer server, String dimensionId) {
        for (ServerLevel level : server.getAllLevels()) {
            if (level.dimension().location().toString().equals(dimensionId)) {
                return level;
            }
        }
        return null;
    }
}