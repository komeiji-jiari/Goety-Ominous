package com.qiuyue.goetyominous.common.events;

import com.qiuyue.goetyominous.common.magic.spells.ac.AtomicBombSpell;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class AtomicBombCleanupHandler {

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        AtomicBombSpell.clearAll();
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        AtomicBombSpell.clearCaster(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.isCanceled()) {
            return;
        }
        AtomicBombSpell.clearCaster(event.getEntity().getUUID());
    }
}
