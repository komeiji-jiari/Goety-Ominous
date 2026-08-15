package com.qiuyue.goetyominous.compat.mod;

import net.minecraftforge.fml.ModList;

public class OpposingForceCompat {

    private static Boolean ofLoaded = null;

    public static boolean isOpposingForceLoaded() {
        if (ofLoaded == null) {
            ofLoaded = ModList.get().isLoaded("opposing_force");
        }
        return ofLoaded;
    }
}
