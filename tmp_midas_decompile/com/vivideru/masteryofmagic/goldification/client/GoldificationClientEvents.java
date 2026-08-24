/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.ClientPlayerNetworkEvent$LoggingOut
 *  net.minecraftforge.event.PlayLevelSoundEvent
 *  net.minecraftforge.event.PlayLevelSoundEvent$AtEntity
 *  net.minecraftforge.event.PlayLevelSoundEvent$AtPosition
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.entity.player.PlayerEvent$BreakSpeed
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package com.vivideru.masteryofmagic.goldification.client;

import com.vivideru.masteryofmagic.goldification.GoldificationSoundUtil;
import com.vivideru.masteryofmagic.goldification.client.GoldificationClientState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.PlayLevelSoundEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="goety_mastery_of_magic", value={Dist.CLIENT}, bus=Mod.EventBusSubscriber.Bus.FORGE)
public final class GoldificationClientEvents {
    private GoldificationClientEvents() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            GoldificationClientState.resetForCurrentLevel();
        }
    }

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        GoldificationClientState.clear();
    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (event.getEntity().m_9236_().m_5776_()) {
            event.getPosition().ifPresent(position -> {
                if (GoldificationClientState.isBlockGoldified(position)) {
                    event.setNewSpeed(1000000.0f);
                }
            });
        }
    }

    @SubscribeEvent
    public static void onBlockSound(PlayLevelSoundEvent.AtPosition event) {
        BlockPos position = BlockPos.m_274446_((Position)event.getPosition());
        if (event.getLevel().m_5776_() && GoldificationClientState.isBlockGoldifiedForSound(position)) {
            GoldificationSoundUtil.remapBlockSound((PlayLevelSoundEvent)event, position, true);
        }
    }

    @SubscribeEvent
    public static void onEntitySound(PlayLevelSoundEvent.AtEntity event) {
        if (event.getLevel().m_5776_() && GoldificationClientState.isEntityGoldified(event.getEntity().m_19879_())) {
            event.setCanceled(true);
            return;
        }
        BlockPos position = event.getEntity().m_20097_();
        if (event.getLevel().m_5776_() && GoldificationClientState.isBlockGoldifiedForSound(position)) {
            GoldificationSoundUtil.remapBlockSound((PlayLevelSoundEvent)event, position, false);
        }
    }
}

