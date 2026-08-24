/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants
 *  com.mojang.blaze3d.platform.InputConstants$Key
 *  com.mojang.blaze3d.platform.InputConstants$Type
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.Minecraft
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.InputEvent$Key
 *  net.minecraftforge.client.event.InputEvent$MouseButton$Pre
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  org.lwjgl.glfw.GLFW
 */
package com.vivideru.masteryofmagic.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.vivideru.masteryofmagic.client.ModKeyMappings;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicNetwork;
import com.vivideru.masteryofmagic.network.CSpellRingCastPacket;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

public class SpellRingClientEvents {

    @Mod.EventBusSubscriber(modid="goety_mastery_of_magic", value={Dist.CLIENT})
    public static class ForgeBusEvents {
        private static final boolean[] held = new boolean[3];
        private static final boolean[] casting = new boolean[3];

        @SubscribeEvent
        public static void keyInput(InputEvent.Key event) {
            Minecraft minecraft = Minecraft.m_91087_();
            if (!ForgeBusEvents.canUseInput(minecraft)) {
                return;
            }
            ForgeBusEvents.handleKeyEvent(0, ModKeyMappings.SPELL_RING_SLOT_1, event);
            ForgeBusEvents.handleKeyEvent(1, ModKeyMappings.SPELL_RING_SLOT_2, event);
            ForgeBusEvents.handleKeyEvent(2, ModKeyMappings.SPELL_RING_SLOT_3, event);
        }

        @SubscribeEvent
        public static void mouseInput(InputEvent.MouseButton.Pre event) {
            Minecraft minecraft = Minecraft.m_91087_();
            if (!ForgeBusEvents.canUseInput(minecraft)) {
                return;
            }
            ForgeBusEvents.handleMouseEvent(0, ModKeyMappings.SPELL_RING_SLOT_1, event);
            ForgeBusEvents.handleMouseEvent(1, ModKeyMappings.SPELL_RING_SLOT_2, event);
            ForgeBusEvents.handleMouseEvent(2, ModKeyMappings.SPELL_RING_SLOT_3, event);
        }

        @SubscribeEvent
        public static void clientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) {
                return;
            }
            Minecraft minecraft = Minecraft.m_91087_();
            if (!ForgeBusEvents.canUseInput(minecraft)) {
                ForgeBusEvents.releaseAll();
                return;
            }
            ForgeBusEvents.handlePhysicalState(0, ModKeyMappings.SPELL_RING_SLOT_1);
            ForgeBusEvents.handlePhysicalState(1, ModKeyMappings.SPELL_RING_SLOT_2);
            ForgeBusEvents.handlePhysicalState(2, ModKeyMappings.SPELL_RING_SLOT_3);
        }

        private static boolean canUseInput(Minecraft minecraft) {
            return minecraft.f_91074_ != null && minecraft.m_91302_() && minecraft.f_91080_ == null;
        }

        private static void handleKeyEvent(int slot, KeyMapping keyMapping, InputEvent.Key event) {
            if (!keyMapping.m_90832_(event.getKey(), event.getScanCode())) {
                return;
            }
            if (event.getAction() == 1) {
                ForgeBusEvents.press(slot);
            } else if (event.getAction() == 0) {
                ForgeBusEvents.release(slot);
            }
        }

        private static void handleMouseEvent(int slot, KeyMapping keyMapping, InputEvent.MouseButton.Pre event) {
            if (!keyMapping.m_90830_(event.getButton())) {
                return;
            }
            if (event.getAction() == 1) {
                ForgeBusEvents.press(slot);
            } else if (event.getAction() == 0) {
                ForgeBusEvents.release(slot);
            }
        }

        private static void handlePhysicalState(int slot, KeyMapping keyMapping) {
            boolean down = ForgeBusEvents.isPhysicallyDown(keyMapping);
            if (down) {
                if (!held[slot]) {
                    ForgeBusEvents.press(slot);
                } else if (ForgeBusEvents.isCasting(slot)) {
                    GoetyMasteryOfMagicNetwork.sendToServer(new CSpellRingCastPacket(slot, true));
                }
            } else if (held[slot]) {
                ForgeBusEvents.release(slot);
            }
        }

        private static boolean isPhysicallyDown(KeyMapping keyMapping) {
            if (keyMapping == null || keyMapping.m_90862_()) {
                return false;
            }
            Minecraft minecraft = Minecraft.m_91087_();
            long window = minecraft.m_91268_().m_85439_();
            InputConstants.Key key = keyMapping.getKey();
            if (key.m_84868_() == InputConstants.Type.KEYSYM) {
                return InputConstants.m_84830_((long)window, (int)key.m_84873_());
            }
            if (key.m_84868_() == InputConstants.Type.MOUSE) {
                return GLFW.glfwGetMouseButton((long)window, (int)key.m_84873_()) == 1;
            }
            return false;
        }

        private static void press(int slot) {
            ForgeBusEvents.held[slot] = true;
            ForgeBusEvents.casting[slot] = true;
            GoetyMasteryOfMagicNetwork.sendToServer(new CSpellRingCastPacket(slot, true));
        }

        private static void release(int slot) {
            ForgeBusEvents.held[slot] = false;
            ForgeBusEvents.casting[slot] = false;
            GoetyMasteryOfMagicNetwork.sendToServer(new CSpellRingCastPacket(slot, false));
        }

        private static boolean isCasting(int slot) {
            return casting[slot];
        }

        private static void releaseAll() {
            for (int i = 0; i < 3; ++i) {
                if (!held[i]) continue;
                ForgeBusEvents.release(i);
            }
        }
    }
}

