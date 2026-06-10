package com.qiuyue.someillagerservants.compat.mod;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.ModList;

public class GoetyAwakenCompat {

    private static Boolean modLoaded = null;

    public static boolean isLoaded() {
        if (modLoaded == null) {
            modLoaded = ModList.get().isLoaded("goetyawaken");
        }
        return modLoaded;
    }

    public static boolean isObsidianMonolithServant(LivingEntity entity) {
        if (!isLoaded()) {
            return false;
        }

        try {
            Class<?> monolithServantClass = Class.forName("com.k1sak1.goetyawaken.common.entities.ally.ObsidianMonolithServant");
            return monolithServantClass.isInstance(entity);
        } catch (ClassNotFoundException e) {
            System.err.println("[SomeIllagerServants] GoetyAwaken ObsidianMonolithServant class not found: " + e.getMessage());
            return false;
        }
    }

    public static boolean isGoetyAwakenHeresiarchServant(LivingEntity entity) {
        if (!isLoaded()) {
            return false;
        }

        try {
            String entityTypeRegistryName = net.minecraftforge.registries.ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()).toString();
            return "goetyawaken:heresiarch_servant".equals(entityTypeRegistryName);
        } catch (Exception e) {
            System.err.println("[SomeIllagerServants] Failed to check GoetyAwaken HeresiarchServant: " + e.getMessage());
            return false;
        }
    }
}
