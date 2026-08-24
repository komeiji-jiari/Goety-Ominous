/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.ItemTags
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.item.ArmorItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 */
package com.vivideru.masteryofmagic;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vivideru.masteryofmagic.ImprovedForgingRingBlacklist;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class DynamicArmorIngredientHelper {
    public static boolean test(Ingredient ingredient, ItemStack stack) {
        if (DynamicArmorIngredientHelper.isImprovedForgeRingIngredient(ingredient) && ImprovedForgingRingBlacklist.matches(stack)) {
            return false;
        }
        if (ingredient.test(stack)) {
            return true;
        }
        JsonElement json = ingredient.m_43942_();
        if (!json.isJsonObject()) {
            return false;
        }
        JsonObject object = json.getAsJsonObject();
        if (!object.has("tag")) {
            return false;
        }
        String tag = object.get("tag").getAsString();
        if (tag.equals("goety_mastery_of_magic:helmets")) {
            return DynamicArmorIngredientHelper.isArmorSlot(stack, EquipmentSlot.HEAD) || stack.m_204117_(ItemTags.create((ResourceLocation)new ResourceLocation("forge", "armors/helmets")));
        }
        if (tag.equals("goety_mastery_of_magic:chestplates")) {
            return DynamicArmorIngredientHelper.isArmorSlot(stack, EquipmentSlot.CHEST) || stack.m_204117_(ItemTags.create((ResourceLocation)new ResourceLocation("forge", "armors/chestplates")));
        }
        if (tag.equals("goety_mastery_of_magic:leggings")) {
            return DynamicArmorIngredientHelper.isArmorSlot(stack, EquipmentSlot.LEGS) || stack.m_204117_(ItemTags.create((ResourceLocation)new ResourceLocation("forge", "armors/leggings")));
        }
        if (tag.equals("goety_mastery_of_magic:boots")) {
            return DynamicArmorIngredientHelper.isArmorSlot(stack, EquipmentSlot.FEET) || stack.m_204117_(ItemTags.create((ResourceLocation)new ResourceLocation("forge", "armors/boots")));
        }
        return false;
    }

    private static boolean isImprovedForgeRingIngredient(Ingredient ingredient) {
        JsonElement json = ingredient.m_43942_();
        if (!json.isJsonObject()) {
            return false;
        }
        JsonObject object = json.getAsJsonObject();
        if (!object.has("tag") || !object.get("tag").isJsonPrimitive()) {
            return false;
        }
        String tag = object.get("tag").getAsString();
        return tag.equals("goety_mastery_of_magic:helmets") || tag.equals("goety_mastery_of_magic:chestplates") || tag.equals("goety_mastery_of_magic:leggings") || tag.equals("goety_mastery_of_magic:boots") || tag.equals("goety_mastery_of_magic:weapons") || tag.equals("forge:tools/shields");
    }

    private static boolean isArmorSlot(ItemStack stack, EquipmentSlot slot) {
        if (stack.m_41619_()) {
            return false;
        }
        Item item = stack.m_41720_();
        if (item instanceof ArmorItem) {
            ArmorItem armorItem = (ArmorItem)item;
            return armorItem.m_40402_() == slot;
        }
        return stack.getEquipmentSlot() == slot;
    }
}

