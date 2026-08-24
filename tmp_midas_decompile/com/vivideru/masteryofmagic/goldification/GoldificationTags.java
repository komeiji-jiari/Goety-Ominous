/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.block.Block
 */
package com.vivideru.masteryofmagic.goldification;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;

public final class GoldificationTags {
    public static final TagKey<Block> IMMUNE_BLOCKS = BlockTags.create((ResourceLocation)new ResourceLocation("goety_mastery_of_magic", "goldification_immune_blocks"));
    public static final TagKey<EntityType<?>> IMMUNE_ENTITY_TYPES = TagKey.m_203882_((ResourceKey)Registries.f_256939_, (ResourceLocation)new ResourceLocation("goety_mastery_of_magic", "goldification_immune_entity_types"));

    private GoldificationTags() {
    }
}

