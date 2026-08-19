package com.qiuyue.goetyominous.compat.mod;

import net.minecraftforge.fml.ModList;

public class AlexCavesCompat {

    private static Boolean acLoaded = null;

    public static boolean isAlexCavesLoaded() {
        if (acLoaded == null) {
            acLoaded = ModList.get().isLoaded("alexscaves");
        }
        return acLoaded;
    }
}
