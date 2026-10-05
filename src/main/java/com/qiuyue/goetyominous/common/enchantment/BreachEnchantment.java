package com.qiuyue.goetyominous.common.enchantment;

import com.qiuyue.goetyominous.common.init.ModEnchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

public class BreachEnchantment extends Enchantment {

    public BreachEnchantment() {
        super(Rarity.RARE, ModEnchantments.MACE, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinCost(int level) {
        return 15 + 9 * (level - 1);
    }

    @Override
    public int getMaxCost(int level) {
        return 65 + 9 * (level - 1);
    }

    @Override
    public int getMaxLevel() {
        return 4;
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
