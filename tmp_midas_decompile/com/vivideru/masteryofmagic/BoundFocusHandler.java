/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraftforge.registries.ForgeRegistries
 */
package com.vivideru.masteryofmagic;

import java.util.UUID;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class BoundFocusHandler {
    public static final TagKey<Item> GOETY_WANDS = TagKey.m_203882_((ResourceKey)Registries.f_256913_, (ResourceLocation)new ResourceLocation("goety", "wands"));
    private static final Item FOCUS_BAG = (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation("goety", "focus_bag"));
    private static final Item FOCUS_PACK = (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation("goety", "focus_pack"));

    public static ItemStack getBoundBag(Player player, ItemStack wand) {
        if (!wand.m_41782_() || !wand.m_41783_().m_128403_("BoundFocusBag")) {
            return ItemStack.f_41583_;
        }
        UUID targetID = wand.m_41783_().m_128342_("BoundFocusBag");
        for (ItemStack stack : player.m_150109_().f_35974_) {
            UUID id;
            Item item = stack.m_41720_();
            if (item != FOCUS_BAG && item != FOCUS_PACK || !stack.m_41782_() || !stack.m_41783_().m_128403_("FocusBagID") || !(id = stack.m_41783_().m_128342_("FocusBagID")).equals(targetID)) continue;
            return stack;
        }
        return ItemStack.f_41583_;
    }
}

