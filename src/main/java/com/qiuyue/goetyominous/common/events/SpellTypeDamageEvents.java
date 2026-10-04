package com.qiuyue.goetyominous.common.events;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.utils.SpellTypeDamage;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID)
public class SpellTypeDamageEvents {

    @SubscribeEvent
    public static void onGeomancyHitsWind(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (!target.level().isClientSide) {
            event.setAmount(SpellTypeDamage.adjust(target, event.getSource(), event.getAmount()));
        }
    }
}
