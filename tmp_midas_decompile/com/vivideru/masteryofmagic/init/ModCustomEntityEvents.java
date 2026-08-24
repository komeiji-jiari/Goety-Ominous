/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.EntityType
 *  net.minecraftforge.event.entity.EntityAttributeCreationEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package com.vivideru.masteryofmagic.init;

import com.vivideru.masteryofmagic.FocusWildfireEntity;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="goety_mastery_of_magic", bus=Mod.EventBusSubscriber.Bus.MOD)
public class ModCustomEntityEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put((EntityType)GoetyMasteryOfMagicModEntities.FOCUS_WILDFIRE.get(), FocusWildfireEntity.setCustomAttributes().m_22265_());
    }
}

