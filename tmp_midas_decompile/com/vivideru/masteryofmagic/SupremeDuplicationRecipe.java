/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.common.items.magic.MagicFocus
 *  net.minecraft.core.NonNullList
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.inventory.CraftingContainer
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.CraftingBookCategory
 *  net.minecraft.world.item.crafting.CustomRecipe
 *  net.minecraft.world.item.crafting.RecipeSerializer
 *  net.minecraft.world.level.Level
 */
package com.vivideru.masteryofmagic;

import com.Polarice3.Goety.common.items.magic.MagicFocus;
import com.vivideru.masteryofmagic.GoetyMasteryOfMagicModRecipes;
import java.util.Set;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class SupremeDuplicationRecipe
extends CustomRecipe {
    private static final ResourceLocation EMPTY_FOCUS = new ResourceLocation("goety", "empty_focus");
    private static final ResourceLocation CONCENTRATED_EMERALD = new ResourceLocation("goety", "soul_emerald");
    private static final ResourceLocation AWAKENED_EMERALD = new ResourceLocation("goety", "magic_emerald");
    private static final Set<ResourceLocation> BASE_MAGIC_CORES = Set.of(new ResourceLocation("goety", "mystic_core"), new ResourceLocation("goety", "wind_core"), new ResourceLocation("goety", "hunger_core"), new ResourceLocation("goety", "animation_core"));

    public SupremeDuplicationRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    public static int requiredLevel(CraftingContainer container) {
        ItemStack target = SupremeDuplicationRecipe.findTarget(container);
        if (target.m_41619_()) {
            return 0;
        }
        return SupremeDuplicationRecipe.isBaseMagicCore(target) ? 2 : 3;
    }

    public boolean matches(CraftingContainer container, Level level) {
        return SupremeDuplicationRecipe.requiredLevel(container) > 0;
    }

    public ItemStack assemble(CraftingContainer container, RegistryAccess access) {
        ItemStack target = SupremeDuplicationRecipe.findTarget(container);
        if (target.m_41619_()) {
            return ItemStack.f_41583_;
        }
        ItemStack result = target.m_41777_();
        result.m_41764_(1);
        return result;
    }

    public NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
        NonNullList remaining = NonNullList.m_122780_((int)container.m_6643_(), (Object)ItemStack.f_41583_);
        for (int i = 0; i < container.m_6643_(); ++i) {
            ItemStack stack = container.m_8020_(i);
            if (stack.m_41619_() || !SupremeDuplicationRecipe.isBaseMagicCore(stack) && (!(stack.m_41720_() instanceof MagicFocus) || BuiltInRegistries.f_257033_.m_7981_((Object)stack.m_41720_()).equals((Object)EMPTY_FOCUS))) continue;
            ItemStack original = stack.m_41777_();
            original.m_41764_(1);
            remaining.set(i, (Object)original);
        }
        return remaining;
    }

    private static ItemStack findTarget(CraftingContainer container) {
        Item emptyFocus = (Item)BuiltInRegistries.f_257033_.m_7745_(EMPTY_FOCUS);
        Item concentratedEmerald = (Item)BuiltInRegistries.f_257033_.m_7745_(CONCENTRATED_EMERALD);
        Item awakenedEmerald = (Item)BuiltInRegistries.f_257033_.m_7745_(AWAKENED_EMERALD);
        int emptyCount = 0;
        int concentratedEmeraldCount = 0;
        int awakenedEmeraldCount = 0;
        ItemStack target = ItemStack.f_41583_;
        for (int i = 0; i < container.m_6643_(); ++i) {
            ItemStack stack = container.m_8020_(i);
            if (stack.m_41619_()) continue;
            if (stack.m_150930_(emptyFocus)) {
                ++emptyCount;
                continue;
            }
            if (stack.m_150930_(concentratedEmerald)) {
                ++concentratedEmeraldCount;
                continue;
            }
            if (stack.m_150930_(awakenedEmerald)) {
                ++awakenedEmeraldCount;
                continue;
            }
            if (target.m_41619_() && (SupremeDuplicationRecipe.isBaseMagicCore(stack) || stack.m_41720_() instanceof MagicFocus)) {
                target = stack;
                continue;
            }
            return ItemStack.f_41583_;
        }
        if (emptyCount != 1 || target.m_41619_()) {
            return ItemStack.f_41583_;
        }
        if (SupremeDuplicationRecipe.isBaseMagicCore(target)) {
            return awakenedEmeraldCount == 1 && concentratedEmeraldCount == 0 ? target : ItemStack.f_41583_;
        }
        return concentratedEmeraldCount == 1 && awakenedEmeraldCount == 0 ? target : ItemStack.f_41583_;
    }

    private static boolean isBaseMagicCore(ItemStack stack) {
        return !stack.m_41619_() && BASE_MAGIC_CORES.contains(BuiltInRegistries.f_257033_.m_7981_((Object)stack.m_41720_()));
    }

    public boolean m_8004_(int width, int height) {
        return width * height >= 3;
    }

    public RecipeSerializer<?> m_7707_() {
        return (RecipeSerializer)GoetyMasteryOfMagicModRecipes.SUPREME_DUPLICATION.get();
    }
}

