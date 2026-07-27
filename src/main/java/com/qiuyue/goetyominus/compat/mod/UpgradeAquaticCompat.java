package com.qiuyue.goetyominus.compat.mod;

import net.minecraftforge.fml.ModList;

public class UpgradeAquaticCompat {

    private static Boolean uaLoaded = null;

    public static boolean isUpgradeAquaticLoaded() {
        if (uaLoaded == null) {
            uaLoaded = ModList.get().isLoaded("upgrade_aquatic");
        }
        return uaLoaded;
    }
}
