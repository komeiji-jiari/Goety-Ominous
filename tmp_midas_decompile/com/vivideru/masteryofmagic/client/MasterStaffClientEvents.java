/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.InputEvent$MouseButton$Pre
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package com.vivideru.masteryofmagic.client;

import com.vivideru.masteryofmagic.MasterStaffHelper;
import com.vivideru.masteryofmagic.client.ModKeyMappings;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicNetwork;
import com.vivideru.masteryofmagic.network.CycleMasterStaffPacket;
import com.vivideru.masteryofmagic.network.OpenMasterStaffPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="goety_mastery_of_magic", value={Dist.CLIENT}, bus=Mod.EventBusSubscriber.Bus.FORGE)
public final class MasterStaffClientEvents {
    private MasterStaffClientEvents() {
    }

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        InteractionHand hand;
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft minecraft = Minecraft.m_91087_();
        if (minecraft.f_91074_ == null || minecraft.f_91080_ != null) {
            return;
        }
        while (ModKeyMappings.OPEN_MASTER_STAFF.m_90859_()) {
            hand = MasterStaffHelper.findHeldHand((Player)minecraft.f_91074_);
            if (hand == null) continue;
            GoetyMasteryOfMagicNetwork.sendToServer(new OpenMasterStaffPacket(hand));
        }
        while (ModKeyMappings.CYCLE_MASTER_STAFF.m_90859_()) {
            hand = MasterStaffHelper.findHeldHand((Player)minecraft.f_91074_);
            if (hand == null) continue;
            GoetyMasteryOfMagicNetwork.sendToServer(new CycleMasterStaffPacket(hand));
        }
    }

    @SubscribeEvent
    public static void mouseInput(InputEvent.MouseButton.Pre event) {
        Minecraft minecraft = Minecraft.m_91087_();
        if (minecraft.f_91074_ == null || minecraft.f_91080_ != null || event.getAction() != 1) {
            return;
        }
        if (!ModKeyMappings.CYCLE_MASTER_STAFF.m_90830_(event.getButton())) {
            return;
        }
        InteractionHand hand = MasterStaffHelper.findHeldHand((Player)minecraft.f_91074_);
        if (hand == null) {
            return;
        }
        GoetyMasteryOfMagicNetwork.sendToServer(new CycleMasterStaffPacket(hand));
        event.setCanceled(true);
    }
}

