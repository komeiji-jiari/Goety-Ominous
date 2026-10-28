package com.qiuyue.goetyominous.common.events;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.ally.mobs.AxolotlServant;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AxolotlRehydrateEvents {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        if (event.isCanceled()) {
            return;
        }
        if (!(event.getProjectile() instanceof ThrownPotion potion)) {
            return;
        }
        if (!(potion.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        if (PotionUtils.getPotion(potion.getItem()) != Potions.WATER
                || !PotionUtils.getMobEffects(potion.getItem()).isEmpty()) {
            return;
        }
        AABB area = potion.getBoundingBox().inflate(4.0D, 2.0D, 4.0D);
        for (AxolotlServant servant : serverLevel.getEntitiesOfClass(AxolotlServant.class, area)) {
            servant.rehydrate();
        }
    }
}
