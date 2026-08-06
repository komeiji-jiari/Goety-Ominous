package com.qiuyue.goetyominous.utils;

import com.Polarice3.Goety.common.entities.ally.*;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.SkeletonWolf;
import com.qiuyue.goetyominous.common.items.ModItems;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class GoetyOminousWolfArmorUtil {

    public static final String SUMMONED_ARMOR_TAG = "GoetyOminousSummonedArmor";

    public static boolean isSupportedWolf(Summoned summoned) {
        return summoned instanceof BlackWolf
                || summoned instanceof SkeletonWolf
                || summoned instanceof WinterWolf
                || summoned instanceof Hellhound
                || summoned instanceof Stormhound;
    }

    public static void equipRingGrantedArmor(Summoned summoned) {
        if (!summoned.level().isClientSide
                && isSupportedWolf(summoned) && summoned.canSpawnArmor()
                && summoned.getItemBySlot(EquipmentSlot.CHEST).isEmpty()
                && summoned.level().getRandom().nextFloat() < 0.35F) {
            ItemStack armor = new ItemStack(ModItems.CURSED_METAL_WOLF_ARMOR.get());
            armor.getOrCreateTag().putBoolean(SUMMONED_ARMOR_TAG, true);
            summoned.setItemSlot(EquipmentSlot.CHEST, armor);
            summoned.setDropChance(EquipmentSlot.CHEST, 0.0F);
        }
    }
}
