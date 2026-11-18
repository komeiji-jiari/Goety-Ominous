package com.qiuyue.goetyominous.common.events;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.blocks.PolarCrystalBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class PolarCrystalEvents {

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        if (event.getRayTraceResult() instanceof BlockHitResult hit && isGoetySpell(event.getProjectile())) {
            PolarCrystalBlock.hit(event.getProjectile().level(), hit.getBlockPos());
        }
    }

    private static boolean isGoetySpell(Projectile projectile) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(projectile.getType()).getNamespace()
                .equals(com.Polarice3.Goety.Goety.MOD_ID);
    }

    private PolarCrystalEvents() {
    }
}
