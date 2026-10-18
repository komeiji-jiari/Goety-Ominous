package com.qiuyue.goetyominous.compat.mod;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

public class IronsSpellbooksCompat {

    private static Boolean loaded = null;
    private static MobEffect trialOmen = null;

    public static boolean isLoaded() {
        if (loaded == null) {
            loaded = ModList.get().isLoaded("irons_spellbooks");
        }
        return loaded;
    }

    public static MobEffect trialOmen() {
        if (trialOmen == null && isLoaded()) {
            trialOmen = ForgeRegistries.MOB_EFFECTS.getValue(
                    new ResourceLocation("irons_spellbooks", "trial_omen"));
        }
        return trialOmen;
    }
}
