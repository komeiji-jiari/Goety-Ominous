package com.qiuyue.goetyominous.common.events;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.message.UpdateEffectVisualityEntityMessage;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.qiuyue.goetyominous.common.entities.ally.ac.DeepOneMageServant;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BubbledVisualCleanupHandler {

    @SubscribeEvent
    public static void onBubbledExpired(MobEffectEvent.Expired event) {
        stripBubbledVisual(event.getEntity(), event.getEffectInstance());
    }

    @SubscribeEvent
    public static void onBubbledRemoved(MobEffectEvent.Remove event) {
        stripBubbledVisual(event.getEntity(), event.getEffectInstance());
    }

    private static void stripBubbledVisual(LivingEntity entity, MobEffectInstance instance) {
        if (instance == null || instance.getEffect() != ACEffectRegistry.BUBBLED.get()) {
            return;
        }
        if (entity instanceof DeepOneMageServant) {
            return;
        }
        if (entity.level().isClientSide) {
            return;
        }
        AlexsCaves.sendMSGToAll(new UpdateEffectVisualityEntityMessage(entity.getId(), entity.getId(), 1, 0, true));
    }
}
