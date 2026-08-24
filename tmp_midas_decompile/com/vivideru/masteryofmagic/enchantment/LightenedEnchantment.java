/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.item.ArmorItem
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.enchantment.Enchantment
 *  net.minecraft.world.item.enchantment.Enchantment$Rarity
 *  net.minecraft.world.item.enchantment.EnchantmentCategory
 *  net.minecraft.world.item.enchantment.EnchantmentHelper
 */
package com.vivideru.masteryofmagic.enchantment;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class LightenedEnchantment
extends Enchantment {
    private static final EnchantmentCategory ENCHANTMENT_CATEGORY = EnchantmentCategory.ARMOR;
    private static final ResourceLocation LIGHTENED_ID = new ResourceLocation("goety_mastery_of_magic", "lightened");

    public LightenedEnchantment() {
        super(Enchantment.Rarity.VERY_RARE, ENCHANTMENT_CATEGORY, EquipmentSlot.values());
    }

    public int m_6183_(int level) {
        return 1 + level * 10;
    }

    public int m_6175_(int level) {
        return 6 + level * 10;
    }

    public boolean m_6591_() {
        return true;
    }

    public boolean m_6081_(ItemStack stack) {
        if (!(stack.m_41720_() instanceof ArmorItem)) {
            return false;
        }
        if (EnchantmentHelper.m_44843_((Enchantment)this, (ItemStack)stack) > 0) {
            return false;
        }
        ResourceLocation id = BuiltInRegistries.f_257033_.m_7981_((Object)stack.m_41720_());
        if (id == null) {
            return false;
        }
        return !"goety".equals(id.m_135827_());
    }

    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return this.m_6081_(stack);
    }

    public boolean m_6592_() {
        return false;
    }

    public boolean m_6594_() {
        return false;
    }

    public static boolean has(ItemStack stack) {
        Enchantment ench = (Enchantment)BuiltInRegistries.f_256876_.m_7745_(LIGHTENED_ID);
        if (ench == null) {
            return false;
        }
        return EnchantmentHelper.m_44843_((Enchantment)ench, (ItemStack)stack) > 0;
    }
}

