package com.qiuyue.goetyominous.compat.spear;

import com.qiuyue.goetyominous.common.items.spear.SpearItems;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;

public class SpearBackportCompat {
    private static Boolean loaded = null;

    public static boolean isSpearBackportLoaded() {
        if (loaded == null) {
            loaded = ModList.get().isLoaded("spears");
        }
        return loaded;
    }

    public static void init(IEventBus modEventBus) {
        SpearItems.register(modEventBus);
    }
}
