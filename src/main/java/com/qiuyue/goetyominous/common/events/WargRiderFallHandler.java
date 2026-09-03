package com.qiuyue.goetyominous.common.events;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.ally.mobs.Warg;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID)
public class WargRiderFallHandler {

    @SubscribeEvent
    public static void onRiderFall(LivingFallEvent event) {
        if (event.getEntity() instanceof Player player
                && player.getVehicle() instanceof Warg) {
            event.setCanceled(true);
        }
    }
}
