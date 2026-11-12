package com.qiuyue.goetyominous.common.events;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.hostile.grimmonarch.GrimMonarchEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID)
public final class GrimMonarchDamageEvents {
    private GrimMonarchDamageEvents() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof GrimMonarchEntity monarch) {
            float bonus = monarch.pendingSmitingBonus();
            if (bonus > 0.0F) {
                event.setAmount(Math.max(0.0F, event.getAmount() - bonus));
            }
        }
    }
}
