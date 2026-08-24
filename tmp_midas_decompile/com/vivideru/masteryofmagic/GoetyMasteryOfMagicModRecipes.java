/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.crafting.RecipeSerializer
 *  net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.RegistryObject
 */
package com.vivideru.masteryofmagic;

import com.vivideru.masteryofmagic.BindFocusBagRecipe;
import com.vivideru.masteryofmagic.SupremeDuplicationRecipe;
import com.vivideru.masteryofmagic.UnbindFocusBagRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

public class GoetyMasteryOfMagicModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create((IForgeRegistry)ForgeRegistries.RECIPE_SERIALIZERS, (String)"goety_mastery_of_magic");
    public static final RegistryObject<RecipeSerializer<BindFocusBagRecipe>> BIND_FOCUS_BAG = RECIPE_SERIALIZERS.register("bind_focus_bag", () -> new SimpleCraftingRecipeSerializer((id, category) -> new BindFocusBagRecipe(id, category)));
    public static final RegistryObject<RecipeSerializer<UnbindFocusBagRecipe>> UNBIND_FOCUS_BAG = RECIPE_SERIALIZERS.register("unbind_focus_bag", () -> new SimpleCraftingRecipeSerializer((id, category) -> new UnbindFocusBagRecipe(id, category)));
    public static final RegistryObject<RecipeSerializer<SupremeDuplicationRecipe>> SUPREME_DUPLICATION = RECIPE_SERIALIZERS.register("supreme_duplication", () -> new SimpleCraftingRecipeSerializer(SupremeDuplicationRecipe::new));
}

