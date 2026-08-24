/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.item.ArmorItem
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.enchantment.Enchantment
 *  net.minecraft.world.item.enchantment.Enchantment$Rarity
 *  net.minecraft.world.item.enchantment.EnchantmentCategory
 *  net.minecraft.world.item.enchantment.EnchantmentHelper
 *  net.minecraftforge.registries.ForgeRegistries
 */
package com.vivideru.masteryofmagic.enchantment;

import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModEnchantments;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class DarkenedEnchantment
extends Enchantment {
    private static final EnchantmentCategory ENCHANTMENT_CATEGORY = EnchantmentCategory.ARMOR;

    public DarkenedEnchantment() {
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

    public boolean m_6592_() {
        return false;
    }

    public boolean m_6594_() {
        return false;
    }

    public int m_6586_() {
        return 1;
    }

    private boolean isValidTarget(ItemStack stack) {
        if (stack == null || stack.m_41619_()) {
            return false;
        }
        if (!(stack.m_41720_() instanceof ArmorItem)) {
            return false;
        }
        if (EnchantmentHelper.m_44843_((Enchantment)this, (ItemStack)stack) > 0) {
            return false;
        }
        ResourceLocation id = ForgeRegistries.ITEMS.getKey((Object)stack.m_41720_());
        return id != null && !"goety".equals(id.m_135827_());
    }

    public boolean m_6081_(ItemStack stack) {
        return this.isValidTarget(stack);
    }

    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return this.isValidTarget(stack);
    }

    public boolean canApply(ItemStack stack) {
        return this.isValidTarget(stack);
    }

    public boolean isAllowedOnBooks() {
        return false;
    }

    protected boolean m_5975_(Enchantment other) {
        return other != this && super.m_5975_(other);
    }

    public static boolean has(ItemStack stack) {
        if (stack == null || stack.m_41619_() || !GoetyMasteryOfMagicModEnchantments.DARKENED.isPresent()) {
            return false;
        }
        return EnchantmentHelper.m_44843_((Enchantment)((Enchantment)GoetyMasteryOfMagicModEnchantments.DARKENED.get()), (ItemStack)stack) > 0;
    }
}

