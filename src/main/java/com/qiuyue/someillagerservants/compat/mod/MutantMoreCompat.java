package com.qiuyue.someillagerservants.compat.mod;

import net.minecraftforge.fml.ModList;

public class MutantMoreCompat {

    private static Boolean mutantMoreLoaded = null;

    public static boolean isMutantMoreLoaded() {
        if (mutantMoreLoaded == null) {
            mutantMoreLoaded = ModList.get().isLoaded("mutantmore");
        }
        return mutantMoreLoaded;
    }
}
