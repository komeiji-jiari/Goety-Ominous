package com.qiuyue.goetyominous.common.events;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.utils.ProjectileDeflection;
import com.qiuyue.goetyominous.utils.ProjectileDeflector;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID)
public class ProjectileDeflectionEvents {

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        Projectile projectile = event.getProjectile();
        if (!(event.getRayTraceResult() instanceof EntityHitResult result)) {
            return;
        }
        if (!(result.getEntity() instanceof ProjectileDeflector deflector)) {
            return;
        }
        ProjectileDeflection deflection = deflector.deflection(projectile);
        if (deflection == ProjectileDeflection.NONE) {
            return;
        }
        event.setCanceled(true);
        Entity entity = result.getEntity();
        if (entity != projectile.getOwner() && !projectile.level().isClientSide) {
            deflection.deflect(projectile, entity, projectile.level().random);
            projectile.setOwner(entity);
        }
    }
}
