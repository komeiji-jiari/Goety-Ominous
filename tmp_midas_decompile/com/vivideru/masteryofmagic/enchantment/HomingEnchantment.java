/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.api.items.magic.IFocus
 *  com.Polarice3.Goety.common.items.ModItems
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.enchantment.Enchantment
 *  net.minecraft.world.item.enchantment.Enchantment$Rarity
 *  net.minecraft.world.item.enchantment.EnchantmentCategory
 */
package com.vivideru.masteryofmagic.enchantment;

import com.Polarice3.Goety.api.items.magic.IFocus;
import com.Polarice3.Goety.common.items.ModItems;
import com.vivideru.masteryofmagic.init.ModFocuses;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class HomingEnchantment
extends Enchantment {
    public HomingEnchantment() {
        super(Enchantment.Rarity.RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    public boolean isAllowedOnBooks() {
        return true;
    }

    public boolean m_6594_() {
        return true;
    }

    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return stack.m_41720_() instanceof IFocus && HomingEnchantment.isValidFocusItem(stack.m_41720_());
    }

    public boolean m_6081_(ItemStack stack) {
        return stack.m_41720_() instanceof IFocus && HomingEnchantment.isValidFocusItem(stack.m_41720_());
    }

    private static boolean isValidFocusItem(Item item) {
        return item == ModFocuses.FIRESHOT_FOCUS.get() || item == ModItems.SOUL_BOLT_FOCUS.get() || item == ModItems.MAGIC_BOLT_FOCUS.get() || item == ModItems.ARROW_RAIN_FOCUS.get() || item == ModItems.SONIC_BOOM_FOCUS.get() || item == ModItems.SKULL_FOCUS.get() || item == ModItems.ICE_SPIKE_FOCUS.get() || item == ModItems.ICE_STORM_FOCUS.get() || item == ModItems.POISON_DART_FOCUS.get() || item == ModItems.RAZOR_WIND_FOCUS.get() || item == ModItems.ELECTROCUTE_FOCUS.get() || item == ModItems.WATER_JET_FOCUS.get() || item == ModItems.BOUNCY_BUBBLE_FOCUS.get() || item == ModItems.FIREBALL_FOCUS.get() || item == ModItems.LAVABALL_FOCUS.get() || item == ModItems.BOMBARDMENT_FOCUS.get() || item == ModItems.METEOR_SHOWER_FOCUS.get() || item == ModItems.MAGMA_BOMB_FOCUS.get() || item == ModItems.WITHER_SKULL_FOCUS.get();
    }

    public int m_6183_(int level) {
        return 1 + level * 10;
    }

    public int m_6175_(int level) {
        return 6 + level * 10;
    }

    public int m_6586_() {
        return 3;
    }
}

