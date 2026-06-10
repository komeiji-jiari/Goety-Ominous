package com.qiuyue.someillagerservants.common.events;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.utils.MiscCapHelper;
import com.qiuyue.someillagerservants.SomeIllagerServants;
import com.qiuyue.someillagerservants.common.items.curios.DarkAnkh;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SomeIllagerServants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DarkAnkhTickHandler {

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity == null) return;

        if (livingEntity.level().isClientSide) return;

        if (!(livingEntity instanceof IOwned owned)) return;

        int noHealTime = MiscCapHelper.getNoHealTime(livingEntity);
        if (noHealTime > 0) {
            MiscCapHelper.setNoHealTime(livingEntity, noHealTime - 1);
        }

        LivingEntity owner = owned.getTrueOwner();
        if (owner == null) return;

        if (!(owner instanceof Player player)) return;

        DarkAnkh.tryHealServant(livingEntity, player);
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity == null) return;

        if (livingEntity.level().isClientSide) return;

        if (!(livingEntity instanceof IOwned)) return;

        MiscCapHelper.setNoHealTime(livingEntity, 100);
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity == null) return;

        if (livingEntity.level().isClientSide) return;

        if (!(livingEntity instanceof IOwned owned)) return;

        LivingEntity owner = owned.getTrueOwner();
        if (owner == null) return;

        if (!(owner instanceof Player player)) return;

        if (DarkAnkh.trySaveFromDeath(livingEntity, player)) {
            event.setCanceled(true);
        }
    }
}
