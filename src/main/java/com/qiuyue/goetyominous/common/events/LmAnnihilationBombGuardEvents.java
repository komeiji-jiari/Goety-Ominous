package com.qiuyue.goetyominous.common.events;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.compat.mod.LegendaryMonstersCompat;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Projectile.SmallAnnihilationBombEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID)
public class LmAnnihilationBombGuardEvents {

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        if (!LegendaryMonstersCompat.isLegendaryMonstersLoaded()) {
            return;
        }
        if (!(event.getProjectile() instanceof SmallAnnihilationBombEntity)) {
            return;
        }
        if (!(event.getRayTraceResult() instanceof EntityHitResult result)) {
            return;
        }
        if (result.getEntity() instanceof LivingEntity) {
            return;
        }
        event.setCanceled(true);
    }
}
