package com.qiuyue.goetyominous.compat.mod;

import com.Polarice3.Goety.api.magic.SpellType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Method;

public class GoetyRevelationCompat {

    private static Boolean modLoaded = null;

    public static boolean isLoaded() {
        if (modLoaded == null) {
            modLoaded = ModList.get().isLoaded("goety_revelation");
        }
        return modLoaded;
    }

    public static void registerFelSpellPower(SpellType felType) {
        if (!isLoaded()) {
            return;
        }

        try {
            Class<?> clazz = Class.forName("com.mega.revelationfix.common.init.ModAttributes");
            Method method = clazz.getDeclaredMethod("registerSpellPowerAttribute", SpellType.class, String.class);
            method.setAccessible(true);
            method.invoke(null, felType, "goety_revelation");
        } catch (Throwable t) {
            System.err.println("[GoetyOminous] Failed to register fel spell power attribute: " + t);
        }
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
            Class<?> heresiarchClass = Class.forName("com.qiuyue.goetyominous.common.entities.ally.mobs.HeresiarchServant");
            return heresiarchClass.isInstance(entity);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
