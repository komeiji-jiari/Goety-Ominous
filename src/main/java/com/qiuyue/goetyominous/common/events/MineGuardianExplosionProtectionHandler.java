package com.qiuyue.goetyominous.common.events;

import com.qiuyue.goetyominous.common.entities.ally.ac.MineGuardianServant;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MineGuardianExplosionProtectionHandler {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getSource().getDirectEntity() instanceof MineGuardianServant servant)) {
            return;
        }
        if (isProtected(servant, event.getEntity())) {
            event.setCanceled(true);
        }
    }

    
    
    private static boolean isProtected(MineGuardianServant servant, LivingEntity victim) {
        if (victim == servant) {
            return false;
        }
        return servant.isAlliedTo(victim) || victim.isAlliedTo(servant);
    }

    
    public static List<Map.Entry<Entity, Vec3>> snapshotProtectedVelocities(Level level, MineGuardianServant servant,
                                                                            double x, double y, double z, float radius) {
        
        double range = radius * 2.0F + 1.0F;
        AABB aabb = new AABB(x - range, y - range, z - range, x + range, y + range, z + range);
        List<Map.Entry<Entity, Vec3>> snapshots = new ArrayList<>();
        for (Entity entity : level.getEntities(servant, aabb, e -> e instanceof LivingEntity living && isProtected(servant, living))) {
            snapshots.add(new AbstractMap.SimpleImmutableEntry<>(entity, entity.getDeltaMovement()));
        }
        return snapshots;
    }

    
    public static void restoreProtectedVelocities(List<Map.Entry<Entity, Vec3>> snapshots) {
        for (Map.Entry<Entity, Vec3> snapshot : snapshots) {
            Entity entity = snapshot.getKey();
            if (entity.isAlive()) {
                entity.setDeltaMovement(snapshot.getValue());
            }
        }
    }
}
