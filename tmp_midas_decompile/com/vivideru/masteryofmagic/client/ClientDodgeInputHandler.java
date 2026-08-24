/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.ClientPlayerNetworkEvent$LoggingOut
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package com.vivideru.masteryofmagic.client;

import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicNetwork;
import com.vivideru.masteryofmagic.network.DodgeInputPacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="goety_mastery_of_magic", value={Dist.CLIENT})
public class ClientDodgeInputHandler {
    private static boolean lastForward;
    private static boolean lastBackward;
    private static boolean lastLeft;
    private static boolean lastRight;
    private static boolean lastShift;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft minecraft = Minecraft.m_91087_();
        if (minecraft.f_91074_ == null || minecraft.f_91073_ == null) {
            return;
        }
        boolean forward = minecraft.f_91066_.f_92085_.m_90857_();
        boolean backward = minecraft.f_91066_.f_92087_.m_90857_();
        boolean left = minecraft.f_91066_.f_92086_.m_90857_();
        boolean right = minecraft.f_91066_.f_92088_.m_90857_();
        boolean shift = minecraft.f_91066_.f_92090_.m_90857_();
        if (forward == lastForward && backward == lastBackward && left == lastLeft && right == lastRight && shift == lastShift) {
            return;
        }
        lastForward = forward;
        lastBackward = backward;
        lastLeft = left;
        lastRight = right;
        lastShift = shift;
        GoetyMasteryOfMagicNetwork.sendToServer(new DodgeInputPacket(forward, backward, left, right, shift));
    }

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        lastForward = false;
        lastBackward = false;
        lastLeft = false;
        lastRight = false;
        lastShift = false;
    }
}

