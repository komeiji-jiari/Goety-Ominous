package com.qiuyue.goetyominus.compat.mod;

import net.minecraftforge.fml.ModList;

public class IllageAndSpillageCompat {

    private static Boolean illageAndSpillageLoaded = null;

    public static boolean isIllageAndSpillageLoaded() {
        if (illageAndSpillageLoaded == null) {
            illageAndSpillageLoaded = ModList.get().isLoaded("illageandspillage");
        }
        return illageAndSpillageLoaded;
    }
}
