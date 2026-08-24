/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.common.capabilities.lichdom.LichProvider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.TickEvent$PlayerTickEvent
 *  net.minecraftforge.event.entity.EntityJoinLevelEvent
 *  net.minecraftforge.event.entity.EntityTravelToDimensionEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerChangedDimensionEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerLoggedInEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerRespawnEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package com.vivideru.masteryofmagic.events;

import com.Polarice3.Goety.common.capabilities.lichdom.LichProvider;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModMobEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class LichFlightHandler {
    private static final String MOD_DATA_KEY = "goetymasteryofmagic";
    private static final String OUR_MAYFLY_FLAG = "gom_goblet_flight_mayfly_set";
    private static final String GOBLET_FLIGHT_UNLOCKED = "goblet_flight_unlocked";
    private static final String FLIGHT_REFRESH_TICKS = "gom_goblet_flight_refresh_ticks";

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Player player = event.player;
        if (player.m_9236_().f_46443_) {
            return;
        }
        CompoundTag persistentData = player.getPersistentData();
        CompoundTag modData = persistentData.m_128469_(MOD_DATA_KEY);
        if (modData.m_128441_(FLIGHT_REFRESH_TICKS)) {
            int ticks = modData.m_128451_(FLIGHT_REFRESH_TICKS);
            if (ticks > 0) {
                modData.m_128405_(FLIGHT_REFRESH_TICKS, ticks - 1);
                persistentData.m_128365_(MOD_DATA_KEY, (Tag)modData);
                LichFlightHandler.updateLichFlight(player, true);
                return;
            }
            modData.m_128473_(FLIGHT_REFRESH_TICKS);
            persistentData.m_128365_(MOD_DATA_KEY, (Tag)modData);
        }
        LichFlightHandler.updateLichFlight(player, false);
    }

    @SubscribeEvent
    public static void onDimensionTravel(EntityTravelToDimensionEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }
        Player player = (Player)entity;
        if (player.m_9236_().f_46443_) {
            return;
        }
        LichFlightHandler.scheduleFlightRefresh(player);
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        if (player.m_9236_().f_46443_) {
            return;
        }
        LichFlightHandler.scheduleFlightRefresh(player);
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player.m_9236_().f_46443_) {
            return;
        }
        LichFlightHandler.scheduleFlightRefresh(player);
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        if (player.m_9236_().f_46443_) {
            return;
        }
        LichFlightHandler.scheduleFlightRefresh(player);
    }

    private static void scheduleFlightRefresh(Player player) {
        CompoundTag persistentData = player.getPersistentData();
        CompoundTag modData = persistentData.m_128469_(MOD_DATA_KEY);
        if (modData.m_128471_(GOBLET_FLIGHT_UNLOCKED)) {
            modData.m_128379_(OUR_MAYFLY_FLAG, false);
            modData.m_128405_(FLIGHT_REFRESH_TICKS, 20);
            persistentData.m_128365_(MOD_DATA_KEY, (Tag)modData);
        }
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().m_5776_()) {
            return;
        }
        Entity entity = event.getEntity();
        if (entity instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer)entity;
            LichFlightHandler.updateLichFlight((Player)player, true);
        }
    }

    private static void updateLichFlight(Player player, boolean forceUpdate) {
        player.getCapability(LichProvider.CAPABILITY).ifPresent(lichdom -> {
            boolean shouldHaveOurFlight;
            boolean isLich = lichdom.getLichdom();
            CompoundTag persistentData = player.getPersistentData();
            CompoundTag modData = persistentData.m_128469_(MOD_DATA_KEY);
            boolean hasFlight = modData.m_128471_(GOBLET_FLIGHT_UNLOCKED);
            boolean isVulnerable = player.m_21023_((MobEffect)GoetyMasteryOfMagicModMobEffects.VULNERABLE.get());
            boolean bl = shouldHaveOurFlight = isLich && hasFlight && !isVulnerable;
            if (shouldHaveOurFlight) {
                player.m_150110_().f_35936_ = true;
                modData.m_128379_(OUR_MAYFLY_FLAG, true);
                persistentData.m_128365_(MOD_DATA_KEY, (Tag)modData);
                if (forceUpdate || !player.m_150110_().f_35936_) {
                    player.m_6885_();
                } else {
                    player.m_6885_();
                }
                return;
            }
            if (!player.m_150110_().f_35937_ && modData.m_128471_(OUR_MAYFLY_FLAG)) {
                modData.m_128379_(OUR_MAYFLY_FLAG, false);
                persistentData.m_128365_(MOD_DATA_KEY, (Tag)modData);
                player.m_150110_().f_35936_ = false;
                player.m_150110_().f_35935_ = false;
                player.m_6885_();
            }
        });
    }
}

