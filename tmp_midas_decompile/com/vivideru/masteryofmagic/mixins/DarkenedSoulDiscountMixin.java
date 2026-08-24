/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.api.items.armor.ISoulDiscount
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  org.spongepowered.asm.mixin.Mixin
 */
package com.vivideru.masteryofmagic.mixins;

import com.Polarice3.Goety.api.items.armor.ISoulDiscount;
import com.vivideru.masteryofmagic.enchantment.DarkenedEnchantment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={Item.class})
public abstract class DarkenedSoulDiscountMixin
implements ISoulDiscount {
    public int getSoulDiscount(EquipmentSlot slot, ItemStack stack) {
        if (DarkenedEnchantment.has(stack)) {
            return 5;
        }
        return 0;
    }
}

