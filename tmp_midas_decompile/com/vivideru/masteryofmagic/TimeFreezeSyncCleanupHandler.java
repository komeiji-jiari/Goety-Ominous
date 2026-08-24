/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.TickEvent$ServerTickEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package com.vivideru.masteryofmagic;

import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModMobEffects;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicNetwork;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class TimeFreezeSyncCleanupHandler {
    private static final Set<Integer> SERVER_FROZEN_ENTITIES = new HashSet<Integer>();

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        HashSet<Integer> stillFrozen = new HashSet<Integer>();
        for (ServerPlayer player : event.getServer().m_6846_().m_11314_()) {
            for (LivingEntity entity : player.m_284548_().m_45976_(LivingEntity.class, player.m_20191_().m_82400_(128.0))) {
                if (!entity.m_21023_((MobEffect)GoetyMasteryOfMagicModMobEffects.TIME_FREEZE_EFFECT.get())) continue;
                stillFrozen.add(entity.m_19879_());
                if (SERVER_FROZEN_ENTITIES.contains(entity.m_19879_())) continue;
                GoetyMasteryOfMagicNetwork.sendTimeFreezeSync(player, entity.m_19879_(), true);
            }
        }
        for (Integer entityId : SERVER_FROZEN_ENTITIES) {
            if (stillFrozen.contains(entityId)) continue;
            for (ServerPlayer player : event.getServer().m_6846_().m_11314_()) {
                GoetyMasteryOfMagicNetwork.sendTimeFreezeSync(player, entityId, false);
            }
        }
        SERVER_FROZEN_ENTITIES.clear();
        SERVER_FROZEN_ENTITIES.addAll(stillFrozen);
    }
}

