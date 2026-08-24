/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.world.phys.EntityHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package com.vivideru.masteryofmagic.client;

import com.vivideru.masteryofmagic.client.ModKeyMappings;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicNetwork;
import com.vivideru.masteryofmagic.network.PlanetShapeTogglePacket;
import com.vivideru.masteryofmagic.network.PlanetShapeshiftPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="goety_mastery_of_magic", value={Dist.CLIENT}, bus=Mod.EventBusSubscriber.Bus.FORGE)
public final class PlanetShapeshiftClientEvents {
    private PlanetShapeshiftClientEvents() {
    }

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft minecraft = Minecraft.m_91087_();
        while (ModKeyMappings.PLANET_COPY_SHAPE.m_90859_()) {
            int n;
            if (minecraft.f_91074_ == null || minecraft.f_91080_ != null) continue;
            HitResult hitResult = minecraft.f_91077_;
            if (hitResult instanceof EntityHitResult) {
                EntityHitResult hit = (EntityHitResult)hitResult;
                n = hit.m_82443_().m_19879_();
            } else {
                n = -1;
            }
            int target = n;
            GoetyMasteryOfMagicNetwork.sendToServer(new PlanetShapeshiftPacket(target));
        }
        while (ModKeyMappings.PLANET_SHAPESHIFT.m_90859_()) {
            if (minecraft.f_91074_ == null || minecraft.f_91080_ != null) continue;
            GoetyMasteryOfMagicNetwork.sendToServer(new PlanetShapeTogglePacket());
        }
    }
}

