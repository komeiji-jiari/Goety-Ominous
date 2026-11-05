package com.qiuyue.goetyominous.common.events;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.util.FireWallEntity;
import com.qiuyue.goetyominous.common.entities.util.FireWallPart;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID)
public class FireWallEvents {

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        if (!(event.getRayTraceResult() instanceof EntityHitResult result)) {
            return;
        }
        if (!(result.getEntity() instanceof FireWallPart part)) {
            return;
        }
        FireWallEntity wall = part.getParent();
        if (wall != null && wall.isOwnerSide(event.getProjectile().getOwner())) {
            event.setCanceled(true);
        }
    }
}
