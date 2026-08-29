package com.qiuyue.goetyominous.common.init;

import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

public class ModTags {
    public static final TagKey<Item> FUNGUS_PACKS = TagKey.create(
            Registries.ITEM,
            new ResourceLocation(GoetyOminous.MOD_ID, "fungus_packs"));

    public static class EntityTypes {
        public static final TagKey<EntityType<?>> FEL_HEAL = TagKey.create(
                net.minecraft.core.registries.Registries.ENTITY_TYPE,
                new ResourceLocation(com.qiuyue.goetyominous.GoetyOminous.MOD_ID, "fel_heal"));
    }
}
