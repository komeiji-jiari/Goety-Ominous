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

    public static final TagKey<EntityType<?>> DEFLECTS_PROJECTILES = TagKey.create(
            Registries.ENTITY_TYPE,
            new ResourceLocation(GoetyOminous.MOD_ID, "deflects_projectiles"));

    public static final TagKey<Item> BREEZE_RODS = TagKey.create(
            Registries.ITEM,
            new ResourceLocation("forge", "rods/breeze"));

    public static class Blocks {
        public static final TagKey<net.minecraft.world.level.block.Block> BLOCKS_WIND_CHARGE_EXPLOSIONS = TagKey.create(
                Registries.BLOCK,
                new ResourceLocation(GoetyOminous.MOD_ID, "blocks_wind_charge_explosions"));
    }
}
