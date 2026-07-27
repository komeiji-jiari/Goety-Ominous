package com.qiuyue.goetyominus.compat.mod;

import net.minecraftforge.fml.ModList;

public class SavageRavageCompat {

    private static Boolean sarLoaded = null;
    private static Boolean blueprintLoaded = null;

    public static boolean isSavageRavageLoaded() {
        if (sarLoaded == null) {
            sarLoaded = ModList.get().isLoaded("savage_and_ravage");
        }
        return sarLoaded;
    }

    public static boolean isBlueprintLoaded() {
        if (blueprintLoaded == null) {
            blueprintLoaded = ModList.get().isLoaded("blueprint");
        }
        return blueprintLoaded;
    }
}
