package com.qiuyue.goetyominous.compat.mod;

import net.minecraftforge.fml.ModList;

public class PiglinProliferationCompat {

    private static Boolean ppLoaded = null;

    public static boolean isLoaded() {
        if (ppLoaded == null) {
            ppLoaded = ModList.get().isLoaded("piglinproliferation");
        }
        return ppLoaded;
    }
}
