package com.qiuyue.goetyominous.common.events.mm;

import com.qiuyue.goetyominous.common.magic.spells.mm.HogChargeSpell;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class HogChargeChannelCleanup {

    private HogChargeChannelCleanup() {
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        HogChargeSpell.forgetChannel(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            HogChargeSpell.forgetChannel(player.getUUID());
        }
    }
}
