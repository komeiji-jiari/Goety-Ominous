package com.Polarice3.Goety.common.items.block;

import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;

public class TallSkullItem extends StandingAndWallBlockItem {
    public TallSkullItem(Block pStandingBlock, Block pWallBlock, Item.Properties pProperties) {
        super(pStandingBlock, pWallBlock, pProperties, Direction.DOWN);
    }

    public boolean canEquip(ItemStack stack, EquipmentSlot armorType, Entity entity) {
        return armorType == EquipmentSlot.HEAD;
    }

    @Nullable
    public EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EquipmentSlot.HEAD;
    }
}
