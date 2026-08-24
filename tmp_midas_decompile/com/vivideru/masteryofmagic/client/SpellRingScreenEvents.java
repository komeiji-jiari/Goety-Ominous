/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.screens.MenuScreens
 *  net.minecraft.world.inventory.MenuType
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
 */
package com.vivideru.masteryofmagic.client;

import com.vivideru.masteryofmagic.client.gui.screen.inventory.MasterStaffScreen;
import com.vivideru.masteryofmagic.client.gui.screen.inventory.SpellRingScreen;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid="goety_mastery_of_magic", value={Dist.CLIENT}, bus=Mod.EventBusSubscriber.Bus.MOD)
public class SpellRingScreenEvents {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> MenuScreens.m_96206_((MenuType)((MenuType)GoetyMasteryOfMagicModMenus.SPELL_RING.get()), SpellRingScreen::new));
        event.enqueueWork(() -> MenuScreens.m_96206_((MenuType)((MenuType)GoetyMasteryOfMagicModMenus.MASTER_STAFF.get()), MasterStaffScreen::new));
    }
}

