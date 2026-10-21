package com.qiuyue.goetyominous.compat.mm;

import com.alexander.mutantmore.entities.IceCube;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class MmBossIceCubeController {
    private static final int BOSS_CONTROL_TICKS = 30;
    private static final List<TrackedCube> TRACKED = new ArrayList<>();

    private MmBossIceCubeController() {
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.register(MmBossIceCubeController.class);
    }

    public static void track(ServerLevel level, IceCube cube) {
        TRACKED.add(new TrackedCube(level, cube, level.getGameTime() + BOSS_CONTROL_TICKS));
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.level instanceof ServerLevel level)) {
            return;
        }
        Iterator<TrackedCube> iterator = TRACKED.iterator();
        while (iterator.hasNext()) {
            TrackedCube tracked = iterator.next();
            if (tracked.level != level) {
                continue;
            }
            if (tracked.cube.isRemoved()) {
                iterator.remove();
                continue;
            }
            if (level.getGameTime() < tracked.shatterAt) {
                continue;
            }
            iterator.remove();
            for (int i = 0; i < 4 && !tracked.cube.isRemoved(); i++) {
                tracked.cube.crack();
            }
        }
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level) {
            TRACKED.removeIf(tracked -> tracked.level == level);
        }
    }

    private static final class TrackedCube {
        private final ServerLevel level;
        private final IceCube cube;
        private final long shatterAt;

        private TrackedCube(ServerLevel level, IceCube cube, long shatterAt) {
            this.level = level;
            this.cube = cube;
            this.shatterAt = shatterAt;
        }
    }
}
