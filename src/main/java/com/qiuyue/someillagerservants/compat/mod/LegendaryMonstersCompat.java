package com.qiuyue.someillagerservants.compat.mod;

import net.minecraftforge.fml.ModList;

public class LegendaryMonstersCompat {

    private static Boolean legendaryMonstersLoaded = null;

    public static boolean isLegendaryMonstersLoaded() {
        if (legendaryMonstersLoaded == null) {
            legendaryMonstersLoaded = ModList.get().isLoaded("legendary_monsters");
        }
        return legendaryMonstersLoaded;
    }
}
