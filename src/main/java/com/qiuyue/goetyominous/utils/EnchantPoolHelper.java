package com.qiuyue.goetyominous.utils;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

public final class EnchantPoolHelper {
    private EnchantPoolHelper() {
    }

    public static List<EnchantmentInstance> selectEnchantment(RandomSource random, ItemStack stack, int level,
                                                              HolderSet<Enchantment> possibleEnchantments) {
        List<EnchantmentInstance> result = Lists.newArrayList();
        int enchantmentValue = stack.getEnchantmentValue();
        if (enchantmentValue <= 0) {
            return result;
        }
        level += 1 + random.nextInt(enchantmentValue / 4 + 1) + random.nextInt(enchantmentValue / 4 + 1);
        float f = (random.nextFloat() + random.nextFloat() - 1.0F) * 0.15F;
        level = Mth.clamp(Math.round((float) level + (float) level * f), 1, Integer.MAX_VALUE);
        List<EnchantmentInstance> available = getAvailableEnchantmentResults(level, stack, possibleEnchantments);
        if (!available.isEmpty()) {
            WeightedRandom.getRandomItem(random, available).ifPresent(result::add);
            while (random.nextInt(50) <= level) {
                if (!result.isEmpty()) {
                    filterCompatibleEnchantments(available, Util.lastOf(result));
                }
                if (available.isEmpty()) {
                    break;
                }
                WeightedRandom.getRandomItem(random, available).ifPresent(result::add);
                level /= 2;
            }
        }
        return result;
    }

    public static void filterCompatibleEnchantments(List<EnchantmentInstance> candidates, EnchantmentInstance instance) {
        candidates.removeIf(other -> !instance.enchantment.isCompatibleWith(other.enchantment));
    }

    public static List<EnchantmentInstance> getAvailableEnchantmentResults(int level, ItemStack stack,
                                                                           HolderSet<Enchantment> possibleEnchantments) {
        List<EnchantmentInstance> list = Lists.newArrayList();
        boolean isBook = stack.is(Items.BOOK);
        for (Holder<Enchantment> holder : possibleEnchantments) {
            Enchantment enchantment = holder.value();
            // 1.21: canEnchant(stack) || isBook；1.20.1 对应方法为 canApplyAtEnchantingTable
            if (!enchantment.canApplyAtEnchantingTable(stack) && !isBook) {
                continue;
            }
            for (int i = enchantment.getMaxLevel(); i >= enchantment.getMinLevel(); --i) {
                if (level >= enchantment.getMinCost(i) && level <= enchantment.getMaxCost(i)) {
                    list.add(new EnchantmentInstance(enchantment, i));
                    break;
                }
            }
        }
        return list;
    }
}
