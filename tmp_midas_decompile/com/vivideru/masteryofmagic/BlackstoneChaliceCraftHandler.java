/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.Container
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.CraftingContainer
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraftforge.event.entity.player.PlayerEvent$ItemCraftedEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package com.vivideru.masteryofmagic;

import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModItems;
import com.vivideru.masteryofmagic.item.UndeadBloodVialItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="goety_mastery_of_magic", bus=Mod.EventBusSubscriber.Bus.FORGE)
public class BlackstoneChaliceCraftHandler {
    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        Player player = event.getEntity();
        ItemStack result = event.getCrafting();
        Container container = event.getInventory();
        if (!result.m_150930_((Item)GoetyMasteryOfMagicModItems.BLACKSTONE_CHALICE.get())) {
            return;
        }
        if (!(container instanceof CraftingContainer)) {
            return;
        }
        CraftingContainer crafting = (CraftingContainer)container;
        ItemStack bloodVial = ItemStack.f_41583_;
        for (int i = 0; i < crafting.m_6643_(); ++i) {
            ItemStack stack = crafting.m_8020_(i);
            if (!stack.m_150930_((Item)GoetyMasteryOfMagicModItems.UNDEAD_BLOOD_VIAL.get())) continue;
            bloodVial = stack;
            break;
        }
        if (bloodVial.m_41619_()) {
            return;
        }
        if (!UndeadBloodVialItem.hasSource(bloodVial)) {
            return;
        }
        CompoundTag vialTag = bloodVial.m_41783_();
        if (vialTag == null) {
            return;
        }
        CompoundTag resultTag = result.m_41784_();
        resultTag.m_128359_("OwnerUUID", vialTag.m_128342_("SourceUUID").toString());
        resultTag.m_128359_("OwnerName", vialTag.m_128461_("SourceName"));
    }
}

