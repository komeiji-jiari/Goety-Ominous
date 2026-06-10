package com.qiuyue.someillagerservants.common.magic.utils;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class SmeltHelper {

    public static ItemStack getSmeltResult(Level level, ItemStack input) {
        if (input.isEmpty()) return ItemStack.EMPTY;

        Optional<net.minecraft.world.item.crafting.SmeltingRecipe> recipe =
                level.getRecipeManager().getAllRecipesFor(RecipeType.SMELTING).stream()
                        .filter(r -> r.getIngredients().get(0).test(input))
                        .findFirst();

        return recipe.map(r -> r.getResultItem(level.registryAccess()).copy())
                .orElse(ItemStack.EMPTY);
    }
}
