/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraftforge.event.entity.EntityJoinLevelEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.ModList
 *  net.minecraftforge.registries.ForgeRegistries
 */
package com.vivideru.masteryofmagic.events;

import java.lang.reflect.Method;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

public final class MidasOptionalCompatEvents {
    private static final ResourceLocation MIDAS = new ResourceLocation("goety_mastery_of_magic", "philosopher_king_midas");
    private static final ResourceLocation ICE_AND_FIRE_STATUE = new ResourceLocation("iceandfire", "stone_statue");

    private MidasOptionalCompatEvents() {
    }

    @SubscribeEvent
    public static void preventMidasStatue(EntityJoinLevelEvent event) {
        if (!ModList.get().isLoaded("iceandfire") || !ICE_AND_FIRE_STATUE.equals((Object)ForgeRegistries.ENTITY_TYPES.getKey((Object)event.getEntity().m_6095_()))) {
            return;
        }
        try {
            Method getter = event.getEntity().getClass().getMethod("getTrappedEntityTypeString", new Class[0]);
            Object trappedType = getter.invoke(event.getEntity(), new Object[0]);
            if (MIDAS.toString().equals(trappedType)) {
                event.setCanceled(true);
            }
        }
        catch (ReflectiveOperationException reflectiveOperationException) {
            // empty catch block
        }
    }
}

