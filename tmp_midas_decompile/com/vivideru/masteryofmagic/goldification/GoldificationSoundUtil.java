/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.SoundType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraftforge.event.PlayLevelSoundEvent
 */
package com.vivideru.masteryofmagic.goldification;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.PlayLevelSoundEvent;

public final class GoldificationSoundUtil {
    private GoldificationSoundUtil() {
    }

    public static void remapBlockSound(PlayLevelSoundEvent event, BlockPos position, boolean allowGenericBlockSound) {
        if (event.getSound() == null) {
            return;
        }
        Level level = event.getLevel();
        BlockState state = level.m_8055_(position);
        SoundType soundType = state.getSoundType((LevelReader)level, position, null);
        SoundEvent current = (SoundEvent)event.getSound().m_203334_();
        SoundEvent replacement = null;
        if (current == soundType.m_56776_()) {
            replacement = SoundEvents.f_12068_;
        } else if (current == soundType.m_56778_()) {
            replacement = SoundEvents.f_12064_;
        } else if (current == soundType.m_56777_()) {
            replacement = SoundEvents.f_12065_;
        } else if (current == soundType.m_56775_()) {
            replacement = SoundEvents.f_12062_;
        } else if (current == soundType.m_56779_()) {
            replacement = SoundEvents.f_12063_;
        } else if (allowGenericBlockSound && !GoldificationSoundUtil.isMetalSound(current)) {
            replacement = SoundEvents.f_12064_;
        }
        if (replacement != null) {
            event.setSound(BuiltInRegistries.f_256894_.m_263177_((Object)replacement));
            event.setNewPitch(Math.max(0.72f, Math.min(1.25f, event.getNewPitch())));
        }
    }

    private static boolean isMetalSound(SoundEvent sound) {
        return sound == SoundEvents.f_12068_ || sound == SoundEvents.f_12064_ || sound == SoundEvents.f_12065_ || sound == SoundEvents.f_12062_ || sound == SoundEvents.f_12063_;
    }
}

