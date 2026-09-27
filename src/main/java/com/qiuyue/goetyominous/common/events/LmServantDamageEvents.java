package com.qiuyue.goetyominous.common.events;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.ally.lm.IAnimatedMonsterServant;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID)
public class LmServantDamageEvents {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity().level().isClientSide) {
            return;
        }
        if (!(event.getSource().getEntity() instanceof IAnimatedMonsterServant servant)) {
            return;
        }
        double multiplier = servant.damageMultiplier();
        if (multiplier == 1.0D) {
            return;
        }
        event.setAmount((float) (event.getAmount() * multiplier));
    }
}
