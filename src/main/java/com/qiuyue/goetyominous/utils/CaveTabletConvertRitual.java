package com.qiuyue.goetyominous.utils;

import com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity;
import com.Polarice3.Goety.common.crafting.RitualRecipe;
import com.Polarice3.Goety.common.ritual.CraftItemRitual;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;


public class CaveTabletConvertRitual extends CraftItemRitual {

    private static final String CAVE_BIOME_TAG = "CaveBiome";

    public CaveTabletConvertRitual(RitualRecipe recipe) {
        super(recipe);
    }

    @Override
    public void finish(Level world, BlockPos blockPos, DarkAltarBlockEntity tileEntity,
                       Player castingPlayer, ItemStack activationItem) {
        String caveBiome = readCaveBiome(activationItem);
        super.finish(world, blockPos, tileEntity, castingPlayer, activationItem);
        if (caveBiome == null) {
            return;
        }
        ItemStack expected = this.recipe.getResultItem(world.registryAccess());
        tileEntity.itemStackHandler.ifPresent(handler -> {
            ItemStack result = handler.getStackInSlot(0);
            if (!result.isEmpty() && ItemStack.isSameItem(result, expected)) {
                result.getOrCreateTag().putString(CAVE_BIOME_TAG, caveBiome);
            }
        });
    }

    private static String readCaveBiome(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(CAVE_BIOME_TAG, Tag.TAG_STRING)) {
            return null;
        }
        String value = tag.getString(CAVE_BIOME_TAG);
        return value.isEmpty() ? null : value;
    }
}
