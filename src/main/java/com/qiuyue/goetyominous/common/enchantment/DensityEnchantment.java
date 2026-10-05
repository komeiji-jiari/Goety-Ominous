package com.qiuyue.goetyominous.common.enchantment;

import com.qiuyue.goetyominous.common.init.ModEnchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

public class DensityEnchantment extends Enchantment {

    public DensityEnchantment() {
        super(Rarity.UNCOMMON, ModEnchantments.MACE, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinCost(int level) {
        return 5 + 8 * (level - 1);
    }

    @Override
    public int getMaxCost(int level) {
        return 25 + 8 * (level - 1);
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    protected boolean checkCompatibility(Enchantment other) {
        return !(other instanceof DamageEnchantment)
                && other != Enchantments.IMPALING
                && other != ModEnchantments.DENSITY.get()
                && other != ModEnchantments.BREACH.get()
                && super.checkCompatibility(other);
    }
}