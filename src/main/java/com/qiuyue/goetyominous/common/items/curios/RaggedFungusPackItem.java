package com.qiuyue.goetyominous.common.items.curios;

import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class RaggedFungusPackItem extends FungusPackItem {

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return GoetyOminous.MOD_ID + ":textures/models/armor/ragged_fungus_pack.png";
    }
}
