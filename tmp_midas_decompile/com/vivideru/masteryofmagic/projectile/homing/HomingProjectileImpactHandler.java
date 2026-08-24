/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.projectile.Projectile
 *  net.minecraftforge.event.entity.living.LivingHurtEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package com.vivideru.masteryofmagic.projectile.homing;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class HomingProjectileImpactHandler {
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        Entity directEntity = source.m_7640_();
        if (!(directEntity instanceof Projectile)) {
            return;
        }
        Projectile projectile = (Projectile)directEntity;
        if (!projectile.getPersistentData().m_128441_("GoetyMasteryHomingLevel")) {
            return;
        }
        projectile.getPersistentData().m_128473_("GoetyMasteryHomingLevel");
        projectile.getPersistentData().m_128473_("GoetyMasteryHomingTarget");
        projectile.getPersistentData().m_128379_("GoetyMasteryHomingHitTarget", true);
    }
}

