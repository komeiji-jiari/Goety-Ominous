/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.common.capabilities.lichdom.LichProvider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.TickEvent$PlayerTickEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package com.vivideru.masteryofmagic.events;

import com.Polarice3.Goety.common.capabilities.lichdom.LichProvider;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModMobEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class LichNoFreezeHandler {
    private static final String MOD_DATA_KEY = "goetymasteryofmagic";
    private static final String GOBLET_NOFREEZE_UNLOCKED = "goblet_nofreeze_unlocked";

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Player player = event.player;
        if (player.m_9236_().f_46443_) {
            return;
        }
        player.getCapability(LichProvider.CAPABILITY).ifPresent(lichdom -> {
            if (!lichdom.getLichdom()) {
                return;
            }
            CompoundTag persistentData = player.getPersistentData();
            CompoundTag modData = persistentData.m_128469_(MOD_DATA_KEY);
            boolean hasNoFreeze = modData.m_128471_(GOBLET_NOFREEZE_UNLOCKED);
            boolean isVulnerable = player.m_21023_((MobEffect)GoetyMasteryOfMagicModMobEffects.VULNERABLE.get());
            if (hasNoFreeze && !isVulnerable) {
                player.m_146917_(0);
                player.m_146924_(false);
            }
        });
    }
}

