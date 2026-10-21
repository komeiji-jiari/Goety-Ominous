package com.qiuyue.goetyominous.utils;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public final class BossUtil {
    public static final TagKey<EntityType<?>> FORGE_BOSSES =
            TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("forge", "bosses"));

    private BossUtil() {
    }

    public static boolean isBoss(Entity entity) {
        return entity.getType().is(FORGE_BOSSES);
    }
}
