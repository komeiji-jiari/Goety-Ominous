/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.ItemLike
 *  net.minecraftforge.event.BuildCreativeModeTabContentsEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package com.vivideru.masteryofmagic.init;

import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModItems;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModTabs;
import com.vivideru.masteryofmagic.init.ModFocuses;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="goety_mastery_of_magic", bus=Mod.EventBusSubscriber.Bus.MOD)
public class ModCreativeTabs {
    @SubscribeEvent
    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == GoetyMasteryOfMagicModTabs.MASTERYOF_MAGIC.get()) {
            event.accept(ModFocuses.MINING_CURSE_FOCUS);
            event.accept(ModFocuses.FIRESHOT_FOCUS);
            event.accept(ModFocuses.FOCUS_WILDFIRE_FOCUS);
            event.m_246326_((ItemLike)ModFocuses.ICE_MONARCH_FOCUS.get());
            event.accept(ModFocuses.TIME_STOP_FOCUS);
            event.accept(ModFocuses.TERRAFORMING_FOCUS);
            event.accept(ModFocuses.SOUL_BARRIER_FOCUS);
            event.accept(ModFocuses.MAGIC_COUNTER_FOCUS);
            event.accept(ModFocuses.DODGING_FOCUS);
            event.accept(ModFocuses.FADING_FOCUS);
            event.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.SPELL_RING.get());
            event.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.MASTER_STAFF.get());
            event.m_246326_((ItemLike)GoetyMasteryOfMagicModItems.EMPOWERED_FORGE_RING.get());
        }
    }
}

