/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.data.tags.IntrinsicHolderTagsProvider$IntrinsicTagAppender
 *  net.minecraft.data.tags.ItemTagsProvider
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.ItemTags
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.item.ArmorItem
 *  net.minecraft.world.item.Item
 *  net.minecraftforge.common.data.ExistingFileHelper
 *  net.minecraftforge.registries.ForgeRegistries
 */
package com.vivideru.masteryofmagic.datagen;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class GoetyMasteryDynamicItemTagProvider
extends ItemTagsProvider {
    public GoetyMasteryDynamicItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, CompletableFuture.completedFuture(null), "goety_mastery_of_magic", existingFileHelper);
    }

    protected void m_6577_(HolderLookup.Provider provider) {
        this.addArmorTag("helmets", EquipmentSlot.HEAD);
        this.addArmorTag("chestplates", EquipmentSlot.CHEST);
        this.addArmorTag("leggings", EquipmentSlot.LEGS);
        this.addArmorTag("boots", EquipmentSlot.FEET);
    }

    private void addArmorTag(String tagName, EquipmentSlot slot) {
        IntrinsicHolderTagsProvider.IntrinsicTagAppender appender = this.m_206424_(ItemTags.create((ResourceLocation)new ResourceLocation("goety_mastery_of_magic", tagName)));
        for (Item item : ForgeRegistries.ITEMS) {
            ResourceLocation id;
            ArmorItem armorItem;
            if (!(item instanceof ArmorItem) || (armorItem = (ArmorItem)item).m_40402_() != slot || (id = ForgeRegistries.ITEMS.getKey((Object)item)) == null) continue;
            appender.m_176839_(id);
        }
    }
}

