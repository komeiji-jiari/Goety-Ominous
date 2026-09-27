package com.qiuyue.goetyominous.common.events;

import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.qiuyue.goetyominous.compat.mod.AlexCavesCompat;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RadiationAllyEvents {

    @SubscribeEvent
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        if (!AlexCavesCompat.isAlexCavesLoaded()) {
            return;
        }
        MobEffectInstance instance = event.getEffectInstance();
        if (instance == null || instance.getEffect() != ACEffectRegistry.IRRADIATED.get()) {
            return;
        }
        LivingEntity entity = event.getEntity();
        if (NucleeperNukeProtectionHandler.isRadiationProtected(entity) || isInFriendlyRadiationCloud(entity)) {
            event.setResult(Event.Result.DENY);
        }
    }

    private static boolean isInFriendlyRadiationCloud(LivingEntity entity) {
        AABB area = entity.getBoundingBox().inflate(4.0D, 8.0D, 4.0D);
        for (AreaEffectCloud cloud : entity.level().getEntitiesOfClass(AreaEffectCloud.class, area)) {
            LivingEntity owner = cloud.getOwner();
            if (owner == null || !owner.isAlliedTo(entity)) {
                continue;
            }
            double dx = entity.getX() - cloud.getX();
            double dz = entity.getZ() - cloud.getZ();
            double radius = cloud.getRadius();
            if (dx * dx + dz * dz <= radius * radius) {
                return true;
            }
        }
        return false;
    }
}
