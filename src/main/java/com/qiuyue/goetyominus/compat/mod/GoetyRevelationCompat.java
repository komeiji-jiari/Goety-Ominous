package com.qiuyue.goetyominus.compat.mod;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

public class GoetyRevelationCompat {

    private static Boolean modLoaded = null;

    public static boolean isLoaded() {
        if (modLoaded == null) {
            modLoaded = ModList.get().isLoaded("goety_revelation");
        }
        return modLoaded;
    }

    public static Item getSoulOfObsidian() {
        if (!isLoaded()) {
            return null;
        }

        try {
            return ForgeRegistries.ITEMS.getValue(
                    new net.minecraft.resources.ResourceLocation("goety_revelation", "soul_of_obsidian")
            );
        } catch (Exception e) {
            System.err.println("[GoetyOminous] Failed to get Soul of Obsidian: " + e.getMessage());
            return null;
        }
    }

    public static boolean isHeresiarchServant(LivingEntity entity) {
        try {
            Class<?> heresiarchClass = Class.forName("com.qiuyue.goetyominus.common.entities.ally.mobs.HeresiarchServant");
            return heresiarchClass.isInstance(entity);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
