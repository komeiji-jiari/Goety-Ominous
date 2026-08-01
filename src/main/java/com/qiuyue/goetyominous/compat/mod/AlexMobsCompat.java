package com.qiuyue.goetyominous.compat.mod;

import net.minecraftforge.fml.ModList;

public class AlexMobsCompat {

    private static Boolean amLoaded = null;

    public static boolean isAlexMobsLoaded() {
        if (amLoaded == null) {
            amLoaded = ModList.get().isLoaded("alexsmobs");
        }
        return amLoaded;
    }
}
