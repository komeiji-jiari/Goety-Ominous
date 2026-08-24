/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.world.entity.Entity
 *  net.minecraftforge.event.entity.EntityJoinLevelEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 */
package com.vivideru.masteryofmagic.client.sound;

import com.vivideru.masteryofmagic.client.sound.PhilosopherWindSlashSoundInstance;
import com.vivideru.masteryofmagic.entity.PhilosopherWindSlashEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class MidasProjectileSoundEvents {
    private MidasProjectileSoundEvents() {
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        Entity entity;
        if (event.getLevel().m_5776_() && (entity = event.getEntity()) instanceof PhilosopherWindSlashEntity) {
            PhilosopherWindSlashEntity slash = (PhilosopherWindSlashEntity)entity;
            Minecraft.m_91087_().m_91106_().m_120367_((SoundInstance)new PhilosopherWindSlashSoundInstance(slash));
        }
    }
}

