package com.qiuyue.someillagerservants.common.init;

import com.qiuyue.someillagerservants.SomeIllagerServants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTags {
    public static final TagKey<Item> FUNGUS_PACKS = TagKey.create(
            Registries.ITEM,
            new ResourceLocation(SomeIllagerServants.MOD_ID, "fungus_packs"));
}
