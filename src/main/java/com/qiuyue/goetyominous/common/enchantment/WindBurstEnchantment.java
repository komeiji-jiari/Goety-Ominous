package com.qiuyue.goetyominous.common.enchantment;

import com.qiuyue.goetyominous.common.init.ModEnchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class WindBurstEnchantment extends Enchantment {

    public WindBurstEnchantment() {
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
        return 3;
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return false;
    }
}
