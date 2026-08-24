/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerLoggedInEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package com.vivideru.masteryofmagic.events;

import com.vivideru.masteryofmagic.MasteryData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="goety_mastery_of_magic", bus=Mod.EventBusSubscriber.Bus.FORGE)
public class LegacyMasteryMigrationHandler {
    private static final String MOD_DATA_KEY = "goetymasteryofmagic";
    private static final String MASTERIES_KEY = "masteries";
    private static final String[] LEGACY_GOBLET_KEYS = new String[]{"goblet_flight_unlocked", "gom_goblet_flight_mayfly_set", "goblet_bats_unlocked", "goblet_bats_cooldown", "goblet_nofreeze_unlocked"};

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity().m_9236_().m_5776_()) {
            return;
        }
        CompoundTag persistentData = event.getEntity().getPersistentData();
        CompoundTag modData = persistentData.m_128469_(MOD_DATA_KEY);
        CompoundTag masteriesData = modData.m_128469_(MASTERIES_KEY);
        boolean migrated = false;
        for (MasteryData.MasteryId masteryId : MasteryData.MasteryId.values()) {
            String key = masteryId.key();
            if (!persistentData.m_128441_(key)) continue;
            if (!masteriesData.m_128441_(key)) {
                masteriesData.m_128405_(key, persistentData.m_128451_(key));
            }
            persistentData.m_128473_(key);
            migrated = true;
        }
        for (String string : LEGACY_GOBLET_KEYS) {
            if (!persistentData.m_128441_(string)) continue;
            if (!modData.m_128441_(string)) {
                if ("goblet_bats_cooldown".equals(string)) {
                    modData.m_128356_(string, persistentData.m_128454_(string));
                } else {
                    modData.m_128379_(string, persistentData.m_128471_(string));
                }
            }
            persistentData.m_128473_(string);
            migrated = true;
        }
        if (migrated) {
            modData.m_128365_(MASTERIES_KEY, (Tag)masteriesData);
            persistentData.m_128365_(MOD_DATA_KEY, (Tag)modData);
        }
    }
}

