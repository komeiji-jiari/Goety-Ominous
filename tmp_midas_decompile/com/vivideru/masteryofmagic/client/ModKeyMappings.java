/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants
 *  com.mojang.blaze3d.platform.InputConstants$Type
 *  net.minecraft.client.KeyMapping
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.RegisterKeyMappingsEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package com.vivideru.masteryofmagic.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="goety_mastery_of_magic", value={Dist.CLIENT}, bus=Mod.EventBusSubscriber.Bus.MOD)
public final class ModKeyMappings {
    private static final String CATEGORY = "key.goety_mastery_of_magic.category";
    public static final KeyMapping SPELL_RING_SLOT_1 = new KeyMapping("key.goety_mastery_of_magic.spell_ring_slot_1", 82, "key.goety_mastery_of_magic.category");
    public static final KeyMapping SPELL_RING_SLOT_2 = new KeyMapping("key.goety_mastery_of_magic.spell_ring_slot_2", 84, "key.goety_mastery_of_magic.category");
    public static final KeyMapping SPELL_RING_SLOT_3 = new KeyMapping("key.goety_mastery_of_magic.spell_ring_slot_3", 89, "key.goety_mastery_of_magic.category");
    public static final KeyMapping OPEN_MASTER_STAFF = new KeyMapping("key.goety_mastery_of_magic.open_master_staff", 71, "key.goety_mastery_of_magic.category");
    public static final KeyMapping CYCLE_MASTER_STAFF = new KeyMapping("key.goety_mastery_of_magic.cycle_master_staff", InputConstants.Type.MOUSE, 2, "key.goety_mastery_of_magic.category");
    public static final KeyMapping PLANET_SHAPESHIFT = new KeyMapping("key.goety_mastery_of_magic.planet_shapeshift", InputConstants.f_84822_.m_84873_(), "key.goety_mastery_of_magic.category");
    public static final KeyMapping PLANET_COPY_SHAPE = new KeyMapping("key.goety_mastery_of_magic.planet_copy_shape", InputConstants.f_84822_.m_84873_(), "key.goety_mastery_of_magic.category");
    private static boolean registered;

    private ModKeyMappings() {
    }

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        if (registered) {
            return;
        }
        event.register(SPELL_RING_SLOT_1);
        event.register(SPELL_RING_SLOT_2);
        event.register(SPELL_RING_SLOT_3);
        event.register(OPEN_MASTER_STAFF);
        event.register(CYCLE_MASTER_STAFF);
        event.register(PLANET_SHAPESHIFT);
        event.register(PLANET_COPY_SHAPE);
        registered = true;
    }
}

