/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.NonNullList
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
 *  net.minecraftforge.registries.ForgeRegistries
 */
package com.vivideru.masteryofmagic;

import com.vivideru.masteryofmagic.GoetyMasteryOfMagicModRecipes;
import java.util.UUID;
import net.minecraft.core.NonNullList;
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
import net.minecraftforge.registries.ForgeRegistries;

public class BindFocusBagRecipe
extends CustomRecipe {
    public static final TagKey<Item> GOETY_WANDS = TagKey.m_203882_((ResourceKey)Registries.f_256913_, (ResourceLocation)new ResourceLocation("goety", "wands"));
    private static final Item FOCUS_BAG = (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation("goety", "focus_bag"));
    private static final Item FOCUS_PACK = (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation("goety", "focus_pack"));

    public BindFocusBagRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    public boolean matches(CraftingContainer container, Level level) {
        int wandCount = 0;
        int bagCount = 0;
        for (int i = 0; i < container.m_6643_(); ++i) {
            ItemStack stack = container.m_8020_(i);
            if (stack.m_41619_()) continue;
            if (stack.m_204117_(GOETY_WANDS)) {
                ++wandCount;
                continue;
            }
            if (stack.m_41720_() == FOCUS_BAG || stack.m_41720_() == FOCUS_PACK) {
                ++bagCount;
                continue;
            }
            return false;
        }
        return wandCount == 1 && bagCount == 1;
    }

    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        UUID bagID;
        ItemStack wandIn = ItemStack.f_41583_;
        ItemStack bagIn = ItemStack.f_41583_;
        for (int i = 0; i < container.m_6643_(); ++i) {
            ItemStack stack = container.m_8020_(i);
            if (stack.m_204117_(GOETY_WANDS)) {
                wandIn = stack;
                continue;
            }
            if (stack.m_41720_() != FOCUS_BAG && stack.m_41720_() != FOCUS_PACK) continue;
            bagIn = stack;
        }
        if (wandIn.m_41619_() || bagIn.m_41619_()) {
            return ItemStack.f_41583_;
        }
        UUID wandID = wandIn.m_41782_() && wandIn.m_41783_().m_128403_("BoundFocusBag") ? wandIn.m_41783_().m_128342_("BoundFocusBag") : null;
        UUID uUID = bagID = bagIn.m_41782_() && bagIn.m_41783_().m_128403_("FocusBagID") ? bagIn.m_41783_().m_128342_("FocusBagID") : null;
        UUID finalID = wandID == null && bagID == null ? UUID.randomUUID() : (wandID == null && bagID != null ? bagID : (wandID != null && bagID == null ? UUID.randomUUID() : (!wandID.equals(bagID) ? bagID : wandID)));
        bagIn.m_41784_().m_128362_("FocusBagID", finalID);
        ItemStack resultWand = wandIn.m_41777_();
        resultWand.m_41784_().m_128362_("BoundFocusBag", finalID);
        return resultWand;
    }

    public NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
        NonNullList remains = NonNullList.m_122780_((int)container.m_6643_(), (Object)ItemStack.f_41583_);
        for (int i = 0; i < container.m_6643_(); ++i) {
            ItemStack original = container.m_8020_(i);
            if (original.m_41619_()) continue;
            if (original.m_204117_(GOETY_WANDS)) {
                remains.set(i, (Object)ItemStack.f_41583_);
                continue;
            }
            if (original.m_41720_() == FOCUS_BAG || original.m_41720_() == FOCUS_PACK) {
                remains.set(i, (Object)original.m_41777_());
                continue;
            }
            ItemStack remaining = original.m_41720_().getCraftingRemainingItem(original);
            remains.set(i, (Object)remaining);
        }
        return remains;
    }

    public boolean m_8004_(int width, int height) {
        return width * height >= 2;
    }

    public RecipeSerializer<?> m_7707_() {
        return (RecipeSerializer)GoetyMasteryOfMagicModRecipes.BIND_FOCUS_BAG.get();
    }
}

