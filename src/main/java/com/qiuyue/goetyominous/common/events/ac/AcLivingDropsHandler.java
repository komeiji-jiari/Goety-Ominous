package com.qiuyue.goetyominous.common.events.ac;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.ally.ac.HullbreakerServant;
import com.qiuyue.goetyominous.compat.mod.AlexCavesCompat;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.qiuyue.goetyominous.config.ac.AcConfig;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID)
public class AcLivingDropsHandler {

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (event.getEntity().level().isClientSide) {
            return;
        }

        if (!AlexCavesCompat.isAlexCavesLoaded()) {
            return;
        }

        Entity sourceEntity = event.getSource().getEntity();
        HullbreakerServant hullbreaker = null;
        if (sourceEntity instanceof HullbreakerServant direct) {
            hullbreaker = direct;
        } else if (event.getEntity().getLastHurtByMob() instanceof HullbreakerServant lastHurt) {
            hullbreaker = lastHurt;
        }

        if (hullbreaker == null || hullbreaker.getTrueOwner() == null
                || !AcConfig.HullbreakerServantPickUpDrops.get()) {
            return;
        }

        hullbreaker.addDrops(event.getDrops());
        event.getDrops().clear();
    }
}
