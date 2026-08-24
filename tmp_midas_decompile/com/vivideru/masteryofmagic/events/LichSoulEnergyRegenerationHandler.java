/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.common.capabilities.lichdom.LichProvider
 *  com.Polarice3.Goety.utils.SEHelper
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.TickEvent$PlayerTickEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package com.vivideru.masteryofmagic.events;

import com.Polarice3.Goety.common.capabilities.lichdom.LichProvider;
import com.Polarice3.Goety.utils.SEHelper;
import com.vivideru.masteryofmagic.config.GameplayConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="goety_mastery_of_magic", bus=Mod.EventBusSubscriber.Bus.FORGE)
public final class LichSoulEnergyRegenerationHandler {
    private LichSoulEnergyRegenerationHandler() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player;
        if (event.phase != TickEvent.Phase.END || !((player = event.player) instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer player2 = (ServerPlayer)player;
        if (!((Boolean)GameplayConfig.SOUL_ENERGY_CHALICE_REGENERATION_ENABLED.get()).booleanValue()) {
            return;
        }
        int amount = (Integer)GameplayConfig.SOUL_ENERGY_CHALICE_REGENERATION_AMOUNT.get();
        int interval = (Integer)GameplayConfig.SOUL_ENERGY_CHALICE_REGENERATION_INTERVAL.get();
        if (amount <= 0 || player2.f_19797_ % interval != 0) {
            return;
        }
        CompoundTag modData = player2.getPersistentData().m_128469_("goetymasteryofmagic");
        if (!modData.m_128471_("goblet_soul_energy_unlocked")) {
            return;
        }
        player2.getCapability(LichProvider.CAPABILITY).ifPresent(lichdom -> {
            if (lichdom.getLichdom() && SEHelper.increaseSESouls((Player)player2, (int)amount)) {
                SEHelper.sendSEUpdatePacket((Player)player2);
            }
        });
    }
}

