package com.qiuyue.goetyominous.compat.mod;

import net.minecraftforge.fml.ModList;

public class PatchouliCompat {

    private static Boolean modLoaded = null;

    public static boolean isLoaded() {
        if (modLoaded == null) {
            modLoaded = ModList.get().isLoaded("patchouli");
        }
        return modLoaded;
    }
}
