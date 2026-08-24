/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Multimap
 *  net.minecraft.world.item.ItemStack
 *  net.minecraftforge.event.ItemAttributeModifierEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.ModList
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package com.vivideru.masteryofmagic.compat.bettercombat;

import com.google.common.collect.Multimap;
import com.vivideru.masteryofmagic.MasterStaffHelper;
import com.vivideru.masteryofmagic.item.MasterStaffItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="goety_mastery_of_magic")
public final class MasterStaffBetterCombatCompat {
    private static final String BETTER_COMBAT_MOD_ID = "bettercombat";

    private MasterStaffBetterCombatCompat() {
    }

    @SubscribeEvent
    public static void inheritSelectedWandAttributes(ItemAttributeModifierEvent event) {
        if (!ModList.get().isLoaded(BETTER_COMBAT_MOD_ID) || !(event.getItemStack().m_41720_() instanceof MasterStaffItem)) {
            return;
        }
        ItemStack selectedWand = MasterStaffHelper.getSelectedWand(event.getItemStack());
        if (selectedWand.m_41619_()) {
            return;
        }
        Multimap selectedModifiers = selectedWand.m_41638_(event.getSlotType());
        event.clearModifiers();
        selectedModifiers.forEach((arg_0, arg_1) -> ((ItemAttributeModifierEvent)event).addModifier(arg_0, arg_1));
    }
}

