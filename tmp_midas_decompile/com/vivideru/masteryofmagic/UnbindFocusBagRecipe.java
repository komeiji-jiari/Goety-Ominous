/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.inventory.CraftingContainer
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.CraftingBookCategory
 *  net.minecraft.world.item.crafting.CustomRecipe
 *  net.minecraft.world.item.crafting.RecipeSerializer
 *  net.minecraft.world.level.Level
 */
package com.vivideru.masteryofmagic;

import com.vivideru.masteryofmagic.GoetyMasteryOfMagicModRecipes;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class UnbindFocusBagRecipe
extends CustomRecipe {
    public static final TagKey<Item> GOETY_WANDS = TagKey.m_203882_((ResourceKey)Registries.f_256913_, (ResourceLocation)new ResourceLocation("goety", "wands"));

    public UnbindFocusBagRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    public boolean matches(CraftingContainer container, Level level) {
        boolean wandFound = false;
        for (int i = 0; i < container.m_6643_(); ++i) {
            ItemStack stack = container.m_8020_(i);
            if (stack.m_41619_()) continue;
            if (stack.m_204117_(GOETY_WANDS) && stack.m_41782_() && stack.m_41783_().m_128403_("BoundFocusBag")) {
                wandFound = true;
                continue;
            }
            return false;
        }
        return wandFound;
    }

    public ItemStack assemble(CraftingContainer container, RegistryAccess access) {
        ItemStack wand = ItemStack.f_41583_;
        for (int i = 0; i < container.m_6643_(); ++i) {
            ItemStack stack = container.m_8020_(i);
            if (!stack.m_204117_(GOETY_WANDS)) continue;
            wand = stack.m_41777_();
        }
        if (!wand.m_41619_() && wand.m_41782_()) {
            wand.m_41783_().m_128473_("BoundFocusBag");
        }
        return wand;
    }

    public boolean m_8004_(int width, int height) {
        return width * height >= 1;
    }

    public RecipeSerializer<?> m_7707_() {
        return (RecipeSerializer)GoetyMasteryOfMagicModRecipes.UNBIND_FOCUS_BAG.get();
    }
}

