package com.qiuyue.goetyominous.common.items;

import com.Polarice3.Goety.api.items.ISoulRepair;
import com.Polarice3.Goety.common.items.ModItems;
import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class DarkWolfArmorItem extends CursedMetalWolfArmorItem implements ISoulRepair {
    public static final ResourceLocation VANILLA_WOLF_TEXTURE = new ResourceLocation(
            GoetyOminous.MOD_ID, "textures/entity/dark_wolf_armor.png");
    public static final ResourceLocation BLACK_WOLF_TEXTURE = new ResourceLocation(
            GoetyOminous.MOD_ID, "textures/entity/dark_black_wolf_armor.png");

    public DarkWolfArmorItem(Item.Properties properties) {
        super(properties, VANILLA_WOLF_TEXTURE, BLACK_WOLF_TEXTURE, 78);
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
        return repairCandidate.is(ModItems.DARK_ALLOY_INGOT.get())
                || repairCandidate.is(ModItems.CURSED_METAL_INGOT.get());
    }
}
