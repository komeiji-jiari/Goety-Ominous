/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.compat.ICompatable
 *  com.google.common.collect.ImmutableMap
 *  net.minecraftforge.fml.ModList
 *  net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
 */
package com.vivideru.masteryofmagic;

import com.Polarice3.Goety.compat.ICompatable;
import com.google.common.collect.ImmutableMap;
import com.vivideru.masteryofmagic.GMPatchouliIntegration;
import com.vivideru.masteryofmagic.GoetyMasteryOfMagicMod;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public final class ModCompat {
    private static final Map<String, Supplier<ICompatable>> MODULE_TYPES = ImmutableMap.builder().put((Object)"patchouli", GMPatchouliIntegration::new).build();
    private static final Map<String, ICompatable> MODULES = new HashMap<String, ICompatable>();

    public static void setup(FMLCommonSetupEvent event) {
        ModCompat.populateModules(arg_0 -> ((ModList)ModList.get()).isLoaded(arg_0));
        MODULES.values().forEach(c -> c.setup(event));
    }

    private static void populateModules(Predicate<String> isLoaded) {
        for (Map.Entry<String, Supplier<ICompatable>> entry : MODULE_TYPES.entrySet()) {
            String id = entry.getKey();
            if (!isLoaded.test(id)) continue;
            MODULES.put(id, entry.getValue().get());
            GoetyMasteryOfMagicMod.LOGGER.info("Loading compat module for mod " + id);
        }
    }
}

