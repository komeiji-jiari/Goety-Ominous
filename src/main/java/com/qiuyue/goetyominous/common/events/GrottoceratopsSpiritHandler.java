package com.qiuyue.goetyominous.common.events;

import com.qiuyue.goetyominous.common.entities.ally.ac.GrottoceratopsSpiritEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class GrottoceratopsSpiritHandler {

    private static final double SPIRIT_RANGE = 30.0D;

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        LivingEntity victim = event.getEntity();
        if (victim.level().isClientSide) {
            return;
        }
        if (consumeClosestSpirit(victim)) {
            event.setCanceled(true);
            victim.playSound(SoundEvents.SHIELD_BLOCK, 1.0F, 1.0F);
        }
    }

    public static boolean consumeClosestSpirit(LivingEntity owner) {
        GrottoceratopsSpiritEntity closest = null;
        for (GrottoceratopsSpiritEntity spirit : owner.level().getEntitiesOfClass(
                GrottoceratopsSpiritEntity.class, owner.getBoundingBox().inflate(SPIRIT_RANGE))) {
            if (!spirit.isFading() && spirit.isOwnedBy(owner)
                    && (closest == null || closest.distanceTo(owner) > spirit.distanceTo(owner))) {
                closest = spirit;
            }
        }
        if (closest != null) {
            closest.setFading(true);
            return true;
        }
        return false;
    }

    public static boolean hasSpirits(LivingEntity owner) {
        for (GrottoceratopsSpiritEntity spirit : owner.level().getEntitiesOfClass(
                GrottoceratopsSpiritEntity.class, owner.getBoundingBox().inflate(SPIRIT_RANGE))) {
            if (!spirit.isFading() && spirit.isOwnedBy(owner)) {
                return true;
            }
        }
        return false;
    }
}
