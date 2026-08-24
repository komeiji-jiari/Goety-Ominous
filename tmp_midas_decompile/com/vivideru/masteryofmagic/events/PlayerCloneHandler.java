/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraftforge.event.entity.player.PlayerEvent$Clone
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package com.vivideru.masteryofmagic.events;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class PlayerCloneHandler {
    private static final String MOD_DATA_KEY = "goetymasteryofmagic";

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        CompoundTag oldPersistentData = event.getOriginal().getPersistentData();
        CompoundTag newPersistentData = event.getEntity().getPersistentData();
        if (oldPersistentData.m_128425_(MOD_DATA_KEY, 10)) {
            newPersistentData.m_128365_(MOD_DATA_KEY, (Tag)oldPersistentData.m_128469_(MOD_DATA_KEY).m_6426_());
        }
    }
}

