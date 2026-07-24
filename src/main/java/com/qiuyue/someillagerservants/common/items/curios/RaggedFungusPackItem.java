package com.qiuyue.someillagerservants.common.items.curios;

import com.qiuyue.someillagerservants.SomeIllagerServants;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class RaggedFungusPackItem extends FungusPackItem {

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return SomeIllagerServants.MOD_ID + ":textures/models/armor/ragged_fungus_pack.png";
    }
}
