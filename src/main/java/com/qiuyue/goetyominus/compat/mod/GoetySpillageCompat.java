package com.qiuyue.goetyominus.compat.mod;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;

public class GoetySpillageCompat {

    private static Boolean modLoaded = null;

    public static boolean isLoaded() {
        if (modLoaded == null) {
            modLoaded = ModList.get().isLoaded("goety_spillage");
        }
        return modLoaded;
    }

    public static boolean shouldConvertToUndead(Player player) {
        if (!isLoaded()) {
            return false;
        }

        try {
            Class<?> curiosFinderClass = Class.forName("com.Polarice3.Goety.utils.CuriosFinder");
            java.lang.reflect.Method hasNamelessSetMethod =
                    curiosFinderClass.getMethod("hasNamelessSet", net.minecraft.world.entity.LivingEntity.class);

            return (Boolean) hasNamelessSetMethod.invoke(null, player);
        } catch (Exception e) {
            System.err.println("[GoetyOminous] Failed to check Nameless Set: " + e.getMessage());
            return false;
        }
    }
}
